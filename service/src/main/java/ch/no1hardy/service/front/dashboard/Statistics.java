package ch.no1hardy.service.front.dashboard;

import lombok.Builder;

@Builder
public record Statistics(
        Integer totalGoalsBet,
        Integer exactBets,
        Integer wastedJokers
) {
}
