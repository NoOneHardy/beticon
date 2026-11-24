package ch.no1hardy.service.front.game;

import ch.no1hardy.service.model.game.Scoreline;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class ScoreReq {
    private String game;
    private Integer scoreTeamHome;
    private Integer scoreTeamGuest;

    public boolean isValid() {
        return (getScoreTeamHome() == null || getScoreTeamHome() >= 0) &&
                (getScoreTeamGuest() == null || getScoreTeamGuest() >= 0);
    }

    public boolean isTie() {
        return getScoreTeamHome() != null && getScoreTeamGuest() != null &&
                new Scoreline(getScoreTeamHome(), getScoreTeamGuest()).isTie();
    }

    public boolean isNull() {
        return getScoreTeamHome() == null && getScoreTeamGuest() == null;
    }

    public void clamp() {
        if (getScoreTeamHome() == null && getScoreTeamGuest() != null) {
            setScoreTeamHome(0);
        }
        if (getScoreTeamGuest() == null && getScoreTeamHome() != null) {
            setScoreTeamGuest(0);
        }
    }
}
