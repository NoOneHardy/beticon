package ch.no1hardy.service.front.group;

public record JokerQuota(
        Integer remainingDoubleJokers,
        Integer remainingTripleJokers,
        Integer doubleJokerLimit,
        Integer tripleJokerLimit
) {
}
