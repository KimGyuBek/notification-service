//package con.threadly.notification.commons.properties;
//
//import java.time.Duration;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
///**
// * Ttl
// */
//@Component
//@ConfigurationProperties(prefix = "properties.ttl")
//@Setter
//public class TtlProperties {
//
//  private long accessToken;
//  private long refreshToken;
//  private long blacklistToken;
//  private long emailVerification;
//  private long passwordVerification;
//  private long loginAttempt;
//  private long postView;
//
//  public Duration getAccessToken() {
//    return Duration.ofSeconds(accessToken);
//  }
//
//  public Duration getRefreshToken() {
//    return Duration.ofSeconds(refreshToken);
//  }
//
//  public Duration getBlacklistToken() {
//    return Duration.ofSeconds(blacklistToken);
//  }
//
//  public Duration getEmailVerification() {
//    return Duration.ofSeconds(emailVerification);
//  }
//
//  public Duration getPasswordVerification() {
//    return Duration.ofSeconds(passwordVerification);
//  }
//  public Duration getLoginAttempt() {
//    return Duration.ofSeconds(loginAttempt);
//  }
//  public Duration getPostViewSeconds() {
//    return Duration.ofSeconds(postView);
//  }
//}
