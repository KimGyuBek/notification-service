package com.threadly.notification.commons.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 커서 기반 조회 API 응답 객체
 */
public record CursorPageApiResponse<T>(
    List<T> content,
    NextCursor nextCursor
) {

  public static <T extends CursorSupport> CursorPageApiResponse<T> from(List<T> items, int limit) {
    /*다음 페이지가 존재하는지 검증*/
    boolean hasNext = items.size() > limit;

    /*페이지 분할*/
    List<T> pagedList = hasNext ? items.subList(0, limit) : items;

    /*커서 지정*/
    NextCursor nextCursor;
    if (hasNext) {
      T last = pagedList.getLast();
      nextCursor =
          new NextCursor(
              last.cursorTimeStamp(),
              last.cursorId()
          );
    } else {
      nextCursor = new NextCursor(null, null);
    }

    return new CursorPageApiResponse<>(pagedList, nextCursor);
  }

  public record NextCursor(LocalDateTime cursorTimestamp, String cursorId) {

  }
}
