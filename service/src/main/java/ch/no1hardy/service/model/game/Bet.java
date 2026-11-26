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

    /**
     * The stored Points of this Bet. Null while the Game has no Result.
     */
    private Integer points;

    public Integer getScoreTeamHome() {
        return scoreline == null ? null : scoreline.home();
    }

    public Integer getScoreTeamGuest() {
        return scoreline == null ? null : scoreline.guest();
    }


    public boolean equals(BetReq bet) {
        return Objects.equals(this.getScoreTeamGuest(), bet.scoreTeamGuest())
                && Objects.equals(this.getScoreTeamHome(), bet.scoreTeamHome())
                && Objects.equals(this.getJoker(), bet.joker());
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
}
