package ch.no1hardy.service.front.game;

public record ResultReq(
        String game,
        Integer scoreTeamHome,
        Integer scoreTeamGuest
) implements ScoreReq {
}
