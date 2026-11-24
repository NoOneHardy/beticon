package ch.no1hardy.service.model.game;

import ch.no1hardy.service.model.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class Score extends BaseEntity {
    @NotNull
    @OneToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Game game;

    @NotNull
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
}
