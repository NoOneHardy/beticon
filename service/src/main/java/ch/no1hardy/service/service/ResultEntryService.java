package ch.no1hardy.service.service;

import ch.no1hardy.service.exception.KnockoutTieException;
import ch.no1hardy.service.exception.NotFoundException;
import ch.no1hardy.service.front.game.BetGameRes;
import ch.no1hardy.service.front.game.ResultReq;
import ch.no1hardy.service.mapper.GameMapper;
import ch.no1hardy.service.model.game.Game;
import ch.no1hardy.service.model.game.GameRepository;
import ch.no1hardy.service.model.game.Score;
import ch.no1hardy.service.model.game.ScoreRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Enters, corrects or removes the Result of a Game as one transaction: the Result, the Points of the Game's Bets
 * and the affected user totals are saved together or not at all (see ADR-0001).
 * Notifications are not part of it; {@link GameService#uploadResult} sends them once this has committed.
 */
@Service
@RequiredArgsConstructor
public class ResultEntryService {
    private final GameRepository repository;
    private final ScoreRepository scoreRepository;
    private final PointsService pointsService;
    private final EntityManager entityManager;
    private final GameMapper mapper;

    /**
     * What a Result entry did.
     *
     * @param game          the Game after the entry
     * @param response      the Game as returned to the admin
     * @param resultEntered whether a Result was entered or corrected, i.e. whether the users are to be notified
     */
    public record ResultEntry(Game game, BetGameRes response, boolean resultEntered) {
    }

    /**
     * Enters the given Result for a Game, replacing any existing one; a blank Result removes it.
     *
     * @param id  the ID of the Game
     * @param dto the Result
     * @return what the entry did
     * @throws NotFoundException    if the Game does not exist
     * @throws KnockoutTieException if the Game is in a knockout group and the Result is a tie
     */
    @Transactional
    public ResultEntry enter(@NotNull String id, @NotNull ResultReq dto) {
        Game game = repository.findById(id).orElse(null);
        if (game == null) throw new NotFoundException("Game " + id + " not found", "Spiel '" + id + "' nicht gefunden");

        if (!dto.isValid()) return new ResultEntry(game, mapper.toDto(game), false);

        if (!dto.isNull() && game.getGroup().getIsKnockout() && dto.isTie()) {
            throw new KnockoutTieException("Game " + id + " is in a knockout group and cannot end in a tie", "Spiel '" + id + "' ist in einer K.O.-Gruppe und kann nicht unentschieden enden");
        }

        removeExistingResult(game);
        if (dto.isNull()) return new ResultEntry(game, mapper.toDto(repository.save(game)), false);

        pointsService.snapshotLastReviewedPoints();
        Score result = mapper.toEntity(dto);
        result.setGame(game);
        result = scoreRepository.save(result);
        game.setResult(result);
        pointsService.rescoreBetsAndTotals(game.getBets());

        Game saved = repository.save(game);
        return new ResultEntry(saved, mapper.toDto(saved), true);
    }

    private void removeExistingResult(@NotNull Game game) {
        Score existingResult = game.getResult();
        if (existingResult == null) return;

        pointsService.snapshotLastReviewedPoints();
        game.setResult(null);
        scoreRepository.delete(existingResult);
        // the old Result must be gone before the corrected one is inserted (one Result per Game)
        entityManager.flush();
        pointsService.rescoreBetsAndTotals(game.getBets());
    }
}
