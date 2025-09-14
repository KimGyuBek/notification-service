package com.threadly.notification.commons.response;

import java.time.LocalDateTime;

public interface CursorSupport {

  public LocalDateTime cursorTimeStamp();

  public String cursorId();


}
