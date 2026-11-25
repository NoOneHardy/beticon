package ch.no1hardy.service.mapper;

import ch.no1hardy.service.front.admin.PointsChangeRes;
import ch.no1hardy.service.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PointsChangeMapper {
    /**
     * Maps a user whose total changed to its {@link PointsChangeRes}.
     *
     * @param user      the user, carrying the new total
     * @param oldPoints the total before the change
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "newPoints", source = "user.points")
    @Mapping(target = "oldPoints", source = "oldPoints")
    PointsChangeRes toPointsChange(User user, Integer oldPoints);
}
