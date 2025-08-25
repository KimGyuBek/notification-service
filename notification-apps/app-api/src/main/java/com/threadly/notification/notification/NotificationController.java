package com.threadly.notification.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notification Controller
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {


  /**
   * test
   *
   * @return
   */
  @GetMapping("/test")
  public ResponseEntity<Void> test() {
    return ResponseEntity.ok().build();
  }


}
