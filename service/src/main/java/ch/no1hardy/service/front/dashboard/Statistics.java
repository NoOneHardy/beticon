package ch.no1hardy.service.front.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Statistics {
    private Integer totalGoalsBet;
    private Integer exactBets;
    private Integer wastedJokers;
}
