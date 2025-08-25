package com.threadly.notification;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse<T>{

  private boolean success;
  private String code;
  private String message;
  private T data;
  private LocalDateTime timestamp;

}
