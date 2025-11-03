package com.threadly.notification.core.port.notification.in.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.user.ActorProfile;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * NotificationDetails 테스트
 */
class NotificationDetailsTest {

  private NotificationDetails sampleDetails() {
    return new NotificationDetails(
        "event-1",
        "sort-1",
        "receiver-1",
        NotificationType.POST_LIKE,
        LocalDateTime.of(2024, 1, 1, 12, 0),
        new ActorProfile("actor-1", "행위자", "/profile.png"),
        false
    );
  }

  @Nested
  @DisplayName("CursorSupport 구현 테스트")
  class CursorSupportTest {

    /*[Case #1] cursorTimeStamp 는 occurredAt 값을 반환해야 한다*/
    @DisplayName("1. cursorTimeStamp 가 occurredAt 값을 반환하는지 검증")
    @Test
    void cursorTimeStamp_shouldReturnOccurredAt() throws Exception {
      //given
      NotificationDetails details = sampleDetails();

      //when
      LocalDateTime cursorTimestamp = details.cursorTimeStamp();

      //then
      assertThat(cursorTimestamp).isEqualTo(details.occurredAt());
    }

    /*[Case #2] cursorId 는 sortId 값을 반환해야 한다*/
    @DisplayName("2. cursorId 가 sortId 값을 반환하는지 검증")
    @Test
    void cursorId_shouldReturnSortId() throws Exception {
      //given
      NotificationDetails details = sampleDetails();

      //when
      String cursorId = details.cursorId();

      //then
      assertThat(cursorId).isEqualTo(details.sortId());
    }
  }
}
