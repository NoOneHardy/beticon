package ch.no1hardy.service.front;

import ch.no1hardy.service.front.notification.NotificationRes;

import java.util.List;

public record GlobalData(
        String id,
        List<NotificationRes> notifications
) {
}
