package ch.no1hardy.service.front.group;

import lombok.Data;

@Data
public class JokerQuota {
    private Integer remainingDoubleJokers;
    private Integer remainingTripleJokers;
    private Integer doubleJokerLimit;
    private Integer tripleJokerLimit;
}
