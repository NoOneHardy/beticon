package ch.no1hardy.service.front.admin;

import lombok.Builder;

/**
 * A user whose total Points changed during "re-score everything".
 */
@Builder
public record PointsChangeRes(
        String userId,
        String username,
        Integer oldPoints,
        Integer newPoints
) {
}
