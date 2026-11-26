package ch.no1hardy.service.front.game;

import lombok.Builder;

@Builder
public record ScoreRes(
        String id,
        String gameId,
        Integer scoreTeamHome,
        Integer scoreTeamGuest
) {
}
