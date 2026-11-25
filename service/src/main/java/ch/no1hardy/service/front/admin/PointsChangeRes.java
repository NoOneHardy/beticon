package ch.no1hardy.service.front.admin;

import lombok.Builder;
import lombok.Data;

/**
 * A user whose total Points changed during "re-score everything".
 */
@Data
@Builder
public class PointsChangeRes {
    private String userId;
    private String username;
    private Integer oldPoints;
    private Integer newPoints;
}
