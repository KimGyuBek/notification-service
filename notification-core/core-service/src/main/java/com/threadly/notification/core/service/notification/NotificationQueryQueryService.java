package com.threadly.notification.core.service.notification;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.notification.NotificationException;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.notification.in.NotificationQueryUseCase;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryQueryService implements NotificationQueryUseCase {

  private final NotificationQueryPort notificationQueryPort;

  @Override
  public GetNotificationDetailsApiResponse findNotificationDetail(String userId,
      String eventId) {

    if (eventId.equals("")) {
      throw new NotificationException(ErrorCode.INVALID_REQUEST);
    }

    /*Notification 조회*/
    Notification notification = notificationQueryPort.fetchByEventId(eventId).orElseThrow(
        () -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND)
    );

    /*요청 userId와 receiverId 검증*/
    if (!userId.equals(notification.getReceiverId())) {
      throw new NotificationException(ErrorCode.NOTIFICATION_ACCESS_FORBIDDEN);
    }

    return new GetNotificationDetailsApiResponse(
        notification.getEventId(),
        notification.getReceiverId(),
        notification.getNotificationType(),
        notification.getOccurredAt(),
        notification.getActorProfile(),
        notification.isRead(),
        notification.getMetadata()
    );
  }

  @Override
  public CursorPageApiResponse<NotificationDetails> findNotificationByCursor(GetNotificationsQuery query) {

    List<NotificationDetails> notifications = notificationQueryPort.fetchAllByCursor(query);

    return CursorPageApiResponse.from(notifications, query.limit());
  }
}
