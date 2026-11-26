package ch.no1hardy.service.front.group;

import ch.no1hardy.service.front.game.BetGameRes;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GroupRes(
        String id,
        String name,
        Double percentage,
        Double percentageResult,
        LocalDateTime lastSavedAt,
        LocalDateTime lastSavedAtResult,
        JokerQuota jokerQuota,
        List<BetGameRes> games,
        Boolean isKnockout
) {
    public GroupRes {
        if (games == null) games = List.of();
        if (isKnockout == null) isKnockout = false;
    }
}
