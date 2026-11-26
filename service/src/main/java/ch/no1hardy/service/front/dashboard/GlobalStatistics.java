package ch.no1hardy.service.front.dashboard;

import lombok.Builder;

@Builder
public record GlobalStatistics(
        Integer totalPoints,
        Integer exactBets,
        Integer wastedJokers
) {
}
