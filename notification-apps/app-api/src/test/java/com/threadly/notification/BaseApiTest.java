package com.threadly.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.utils.TestLogUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Base Api Test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BaseApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * get 요청 전송
   *
   * @param accessToken
   * @param path
   * @param expectedStatus
   * @param typeRef
   * @param <T>
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendGetRequest(String accessToken, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef) throws Exception {

    TestLogUtils.log(path + " 요청 전송");

    String bearerToken = "Bearer " + accessToken;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", bearerToken);
    headers.set("Accept-Charset", "utf-8");

    MvcResult result = mockMvc.perform(
        get(path)
            .headers(headers)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return
        getResponse(result, typeRef);
  }

  /**
   * post 요청 전송
   *
   * @param requestJson
   * @param path
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendPostRequest(String requestJson, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef,
      Map<String, String> headers) throws Exception {
    TestLogUtils.log(path + " 요청 전송");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set("Accept-Charset", "utf-8");
    headers.forEach((key, value) -> httpHeaders.add(key, value));

    MvcResult result = mockMvc.perform(
        post(path)
            .headers(httpHeaders)
            .content(requestJson)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return getResponse(result, typeRef);
  }


  /**
   * patch 요청 전송
   *
   * @param requestJson
   * @param path
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendPatchRequest(String requestJson, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef,
      Map<String, String> headers) throws Exception {
    TestLogUtils.log(path + " 요청 전송");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set("Accept-Charset", "utf-8");
    headers.forEach((key, value) -> httpHeaders.add(key, value));

    MvcResult result = mockMvc.perform(
        patch(path)
            .headers(httpHeaders)
            .content(requestJson)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return getResponse(result, typeRef);
  }

  /**
   * delete 요청 전송
   *
   * @param requestJson
   * @param path
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendDeleteRequest(String requestJson, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef,
      Map<String, String> headers) throws Exception {
    TestLogUtils.log(path + " 요청 전송");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set("Accept-Charset", "utf-8");
    headers.forEach((key, value) -> httpHeaders.add(key, value));

    MvcResult result = mockMvc.perform(
        delete(path)
            .headers(httpHeaders)
            .content(requestJson)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return getResponse(result, typeRef);
  }

  /**
   * response -> CommonResponse<T>
   *
   * @param result
   * @return
   * @throws UnsupportedEncodingException
   * @throws JsonProcessingException
   */
  public <T> CommonResponse<T> getResponse(MvcResult result,
      TypeReference<CommonResponse<T>> typeRef)
      throws UnsupportedEncodingException, JsonProcessingException {
    String resultAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    CommonResponse response = objectMapper.readValue(resultAsString, typeRef);
    return response;
  }


  /**
   * test 요청 전송
   *
   * @param accessToken
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendTestRequest(String accessToken, ResultMatcher expectedStatus)
      throws Exception {
    return
        sendGetRequest(
            accessToken, "/api/test/authentication", expectedStatus, new TypeReference<>() {
            }
        );
  }


  /**
   * Kafka 수신 테스트를 위한 kafka 이벤트 발행 요청
   *
   * @param notificationEvent
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendKafkaTest(
      NotificationEvent notificationEvent, ResultMatcher expectedStatus
  ) throws Exception {
    String requestBody = generateRequestBody(notificationEvent);

    return sendPostRequest(
        requestBody,
        "/api/test/kafka",
        expectedStatus,
        new TypeReference<>() {
        },
        Map.of()
    );
  }

  /**
   * request body 생성
   *
   * @param data
   * @param <T>
   * @return
   * @throws JsonProcessingException
   */
  public <T> String generateRequestBody(T data) throws JsonProcessingException {
    return
        objectMapper.writeValueAsString(data);
  }

  /**
   * 실패 응답 검증
   *
   * @param failResponse
   * @param expectedErrorCode
   */
  public void validateFailResponse(CommonResponse failResponse, ErrorCode expectedErrorCode) {
    assertThat(failResponse.isSuccess()).isFalse();
    assertThat(failResponse.getCode()).isEqualTo(expectedErrorCode.getCode());
  }
}
