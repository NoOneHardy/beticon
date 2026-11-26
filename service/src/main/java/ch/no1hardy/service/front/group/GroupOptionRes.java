package ch.no1hardy.service.front.group;

public record GroupOptionRes(
        String id,
        String name,
        Boolean isKnockout,
        Integer order,
        String thumbnail
) {
}
