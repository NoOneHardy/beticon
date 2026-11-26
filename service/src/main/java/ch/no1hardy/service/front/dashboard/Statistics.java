package ch.no1hardy.service.front.dashboard;

public record Statistics(
        Integer totalGoalsBet,
        Integer exactBets,
        Integer wastedJokers
) {
}
