package top.shy.campusassistant.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 容联云短信发送工具类
 *
 * @author mqxu
 */
@Slf4j
@Component
public class RongLianSmsUtils {

    @Value("${ronglian.account-sid}")
    private String accountSid;

    @Value("${ronglian.auth-token}")
    private String authToken;

    @Value("${ronglian.app-id}")
    private String appId;

    @Value("${ronglian.template-id}")
    private String templateId;

    @Value("${ronglian.dev-mode:false}")
    private boolean devMode;

    private static final String BASE_URL = "https://app.cloopen.com:8883/2013-12-26";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否发送成功
     */
    public boolean sendSmsCode(String phone, String code) {
        // 开发模式：直接返回成功，不调用真实API
        if (devMode) {
            log.info("【开发模式】模拟短信发送成功，手机号：{}，验证码：{}", phone, code);
            return true;
        }

        try {
            // 生成时间戳
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            // 生成签名
            String sig = md5(accountSid + authToken + timestamp).toUpperCase();

            // 构建请求 URL
            String url = BASE_URL + "/Accounts/" + accountSid + "/SMS/TemplateSMS?sig=" + sig;

            // 构建请求头
            String authorization = Base64.getEncoder().encodeToString(
                    (accountSid + ":" + timestamp).getBytes(StandardCharsets.UTF_8)
            );

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", phone);
            requestBody.put("appId", appId);
            requestBody.put("templateId", templateId);
            requestBody.put("datas", new String[]{code, "5"}); // 验证码和有效期（5分钟）


            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // 发送 HTTP 请求
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                httpPost.setHeader("Authorization", authorization);
                httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    String responseBody = new String(
                            response.getEntity().getContent().readAllBytes(),
                            StandardCharsets.UTF_8
                    );

                    // 解析响应
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    String statusCode = jsonNode.path("statusCode").asText();

                    if ("000000".equals(statusCode)) {
                        log.info("短信发送成功，手机号：{}", phone);
                        return true;
                    } else {
                        log.error("短信发送失败，手机号：{}，错误码：{}，错误信息：{}",
                                phone, statusCode, jsonNode.path("statusMsg").asText());
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("短信发送异常，手机号：{}", phone, e);
            return false;
        }
    }

    /**
     * MD5 加密
     */
    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
