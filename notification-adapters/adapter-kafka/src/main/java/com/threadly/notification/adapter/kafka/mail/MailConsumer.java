//package com.threadly.notification.adapter.kafka.mail;
//
//import com.threadly.notification.core.port.mail.in.MailIngestionUseCase;
//import java.util.function.Consumer;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.protocol.Message;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
///**
// * Kafka 메일 이벤트 수신 consumer
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class MailConsumer {
//
//  private final MailIngestionUseCase mailIngestionUseCase;
//
//  @Bean("mail")
//  public Consumer<Message<MailEvent>> mailEventConsumer() {
//
//  }
//
//
//}
