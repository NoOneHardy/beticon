package ch.no1hardy.service.front.user;

import ch.no1hardy.service.model.user.Role;
import ch.no1hardy.service.model.user.UserApplicationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserRes(
        String id,
        String username,
        String email,
        String firstname,
        String lastname,
        Integer points,
        Integer lastReviewedPoints,
        String avatarUrl,
        Role role,
        LocalDateTime applicationReviewedAt,
        LocalDateTime emailVerifiedAt,
        UserApplicationStatus userApplicationStatus
) {
}
