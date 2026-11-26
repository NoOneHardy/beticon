package ch.no1hardy.service.front.game;

public record BetReq(
        String game,
        Integer scoreTeamHome,
        Integer scoreTeamGuest,
        Integer joker
) implements ScoreReq {
    @Override
    public boolean isValid() {
        return ScoreReq.super.isValid() && joker() != null;
    }

    /**
     * @return the Joker, 1 when it is outside 1 to 3; null when it is missing
     */
    public Integer normalizedJoker() {
        if (joker() == null) return null;
        return joker() > 3 || joker() < 1 ? Integer.valueOf(1) : joker();
    }
}
