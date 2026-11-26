package ch.no1hardy.service.mapper;

import ch.no1hardy.service.front.user.UserReq;
import ch.no1hardy.service.front.user.UserRes;
import ch.no1hardy.service.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {NotificationMapper.class}
)
public interface UserMapper extends EntityMapper<User, UserReq, UserRes> {
    /**
     * The email is left untouched: changing it requires re-verification, which is not implemented yet.
     */
    @Override
    @Mapping(target = "email", ignore = true)
    void update(UserReq dto, @MappingTarget User entity);
}
