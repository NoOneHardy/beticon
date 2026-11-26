package ch.no1hardy.service.front.dashboard;

import lombok.Builder;

@Builder
public record UserSummary(
        Integer points,
        Integer ranking,
        Double percentage,
        Boolean isConfirmed
) {
}
