package com.threadly.notification.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

public class TestLogUtils {

  public static void log(MvcResult result) throws UnsupportedEncodingException {
    MockHttpServletRequest request = result.getRequest();
    MockHttpServletResponse response = result.getResponse();

    System.out.println("\n================ REQUEST ================");
    System.out.printf("%-10s %s\n", request.getMethod(), request.getRequestURI());
    System.out.println("Headers:");
    Collections.list(request.getHeaderNames())
        .forEach(header -> System.out.printf("  %s: %s\n", header, request.getHeader(header)));
    System.out.println("Body:");
    System.out.println(Optional.ofNullable(request.getContentAsString()).orElse("(empty)"));

    System.out.println("\n================ RESPONSE ================");
    System.out.printf("Status: %d\n", response.getStatus());
    System.out.println("Headers:");
    response.getHeaderNames()
        .forEach(header -> System.out.printf("  %s: %s\n", header, response.getHeader(header)));
    System.out.println("Body:");
    System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));

    System.out.println("==========================================\n");
  }

  public static void log(String desc) {
    System.out.println("\n\n================" + desc + "================");
  }
}
