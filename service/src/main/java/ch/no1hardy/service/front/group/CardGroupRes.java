package ch.no1hardy.service.front.group;

import lombok.Builder;

import java.util.List;

@Builder
public record CardGroupRes(
        String id,
        String name,
        Double percentage,
        Double percentageResult,
        List<String> thumbnail,
        Boolean isKnockout,
        Integer order
) {
    public CardGroupRes {
        if (order == null) order = 0;
    }
}
