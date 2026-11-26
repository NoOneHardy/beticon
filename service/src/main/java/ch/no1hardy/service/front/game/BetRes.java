package ch.no1hardy.service.front.game;

import lombok.Builder;

@Builder
public record BetRes(
        String id,
        String gameId,
        Integer scoreTeamHome,
        Integer scoreTeamGuest,
        Integer joker,
        PointsBreakdownRes points
) {
}
