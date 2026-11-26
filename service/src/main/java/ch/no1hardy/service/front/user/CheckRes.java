package ch.no1hardy.service.front.user;

import lombok.Builder;

@Builder
public record CheckRes(
        Boolean isUsernameAvailable,
        Boolean isEmailAvailable
) {
}
