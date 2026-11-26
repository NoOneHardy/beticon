package ch.no1hardy.service.front.dashboard;

public record GlobalStatistics(
        Integer totalPoints,
        Integer exactBets,
        Integer wastedJokers
) {
}
