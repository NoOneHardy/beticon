package ch.no1hardy.service.front.admin;


/**
 * A user whose total Points changed during "re-score everything".
 */
public record PointsChangeRes(
        String userId,
        String username,
        Integer oldPoints,
        Integer newPoints
) {
}
