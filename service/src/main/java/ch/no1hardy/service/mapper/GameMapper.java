package ch.no1hardy.service.mapper;

import ch.no1hardy.service.front.game.*;
import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.Game;
import ch.no1hardy.service.model.game.GameRepository;
import ch.no1hardy.service.model.game.PointsBreakdown;
import ch.no1hardy.service.model.game.Score;
import ch.no1hardy.service.model.group.GroupRepository;
import ch.no1hardy.service.model.team.TeamRepository;
import ch.no1hardy.service.model.user.UserRepository;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {TeamRepository.class, GroupRepository.class, TeamMapper.class, UserHelper.class, GameRepository.class, UserRepository.class}
)
public interface GameMapper extends EntityMapper<Game, GameReq, BetGameRes> {
    @Mapping(target = "gameId", source = "game", qualifiedByName = "getGameId")
    @Mapping(target = "points", expression = "java(toPointsBreakdownRes(entity.pointsBreakdown().orElse(null)))")
    BetRes toDto(Bet entity);

    @Mapping(target = "bet", source = "bets", qualifiedByName = "getUserBet")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    BetGameRes toDto(Game game);

    @Mapping(target = "gameId", source = "game", qualifiedByName = "getGameId")
    ScoreRes toDto(Score entity);

    Score toEntity(ResultReq dto);

    Bet toEntity(BetReq dto);

    @Mapping(target = "correctWinner", source = "correctWinner", qualifiedByName = "nullIfZero")
    @Mapping(target = "correctGoalsHome", source = "correctGoalsHome", qualifiedByName = "nullIfZero")
    @Mapping(target = "correctGoalsGuest", source = "correctGoalsGuest", qualifiedByName = "nullIfZero")
    @Mapping(target = "correctGoalsTotal", source = "correctGoalsTotal", qualifiedByName = "nullIfZero")
    @Mapping(target = "total", expression = "java(breakdown.total())")
    PointsBreakdownRes toPointsBreakdownRes(PointsBreakdown breakdown);

    @Named("nullIfZero")
    default Integer nullIfZero(int value) {
        return value > 0 ? value : null;
    }

    @Named("getGameId")
    default String getId(Game game) {
        return game.getId();
    }
}