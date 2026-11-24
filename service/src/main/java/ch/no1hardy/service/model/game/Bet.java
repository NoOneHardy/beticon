package ch.no1hardy.service.model.game;

import ch.no1hardy.service.front.game.BetReq;
import ch.no1hardy.service.model.BaseEntity;
import ch.no1hardy.service.model.user.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;

@Data
@Entity
public class Bet extends BaseEntity {
    @NotNull
    private Integer joker;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @ManyToOne
    @ToString.Exclude
    private Game game;

    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "home", column = @Column(name = "score_team_home")),
            @AttributeOverride(name = "guest", column = @Column(name = "score_team_guest"))
    })
    private Scoreline scoreline;

    public Integer getScoreTeamHome() {
        return scoreline == null ? null : scoreline.getHome();
    }

    public Integer getScoreTeamGuest() {
        return scoreline == null ? null : scoreline.getGuest();
    }

    public void setScoreTeamHome(Integer home) {
        if (scoreline == null) scoreline = new Scoreline();
        scoreline.setHome(home);
    }

    public void setScoreTeamGuest(Integer guest) {
        if (scoreline == null) scoreline = new Scoreline();
        scoreline.setGuest(guest);
    }

    public boolean equals(BetReq bet) {
        return Objects.equals(this.getScoreTeamGuest(), bet.getScoreTeamGuest())
                && Objects.equals(this.getScoreTeamHome(), bet.getScoreTeamHome())
                && Objects.equals(this.getJoker(), bet.getJoker());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Score this Bet against its Game's Result.
     *
     * @return the Points breakdown, or empty if the Game has no Result yet.
     */
    public Optional<PointsBreakdown> pointsBreakdown() {
        return Optional.ofNullable(game.getResult())
                .map(result -> Scoring.score(scoreline, result.getScoreline(), joker));
    }

    public boolean isCorrect() {
        Score result = game.getResult();
        if (result == null) return false;

        return Objects.equals(result.getScoreTeamHome(), getScoreTeamHome())
                && Objects.equals(result.getScoreTeamGuest(), getScoreTeamGuest());

    }
}
