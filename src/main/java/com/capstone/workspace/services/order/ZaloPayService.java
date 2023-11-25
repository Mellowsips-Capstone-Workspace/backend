package com.capstone.workspace.services.order;

import com.capstone.workspace.entities.order.Order;
import com.capstone.workspace.entities.order.Transaction;
import com.capstone.workspace.exceptions.InternalServerErrorException;
import com.capstone.workspace.helpers.shared.AppHelper;
import com.capstone.workspace.models.auth.UserIdentity;
import com.capstone.workspace.models.order.ZaloPayCallbackResult;
import com.capstone.workspace.services.auth.IdentityService;
import com.capstone.workspace.utils.zalopay.crypto.HMACUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ZaloPayService {
    private static Logger logger = LoggerFactory.getLogger(ZaloPayService.class);

    @Value("${zalopay.appId}")
    private int appId;

    @Value("${zalopay.key1}")
    private String key1;

    @Value("${zalopay.key2}")
    private String key2;

    @Value("${zalopay.endpoint}")
    private String endpoint;

    @Value("${zalopay.transaction.callbackUrl}")
    private String callbackUrl;

    @NonNull
    private final IdentityService identityService;

    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private final RestTemplate restTemplate;

    public synchronized Map createOrder(Order orderEntity) {
        try {
            UserIdentity userIdentity = identityService.getUserIdentity();

            Map embedData = Collections.emptyMap();
            long currentTimeMillis = System.currentTimeMillis();
            String appTransId = AppHelper.getCurrentVietnamTimeString("yyMMdd") + "_" + currentTimeMillis;

            Map<String, Object> orderRequest = new HashMap<>() {{
                put("app_id", appId);
                put("app_trans_id", appTransId);
                put("app_time", currentTimeMillis);
                put("app_user", userIdentity.getUsername());
                put("amount", orderEntity.getFinalPrice());
                put("description", "MellowSips - Thanh toán cho đơn hàng #" + orderEntity.getId());
                put("bank_code", "zalopayapp");
                put("callback_url", callbackUrl);
                put("item", objectMapper.writeValueAsString(Collections.emptyList()));
                put("embed_data", objectMapper.writeValueAsString(embedData));
            }};

            String data = orderRequest.get("app_id") +"|"+ orderRequest.get("app_trans_id") +"|"+ orderRequest.get("app_user") +"|"+ orderRequest.get("amount")
                    +"|"+ orderRequest.get("app_time") +"|"+ orderRequest.get("embed_data") +"|"+ orderRequest.get("item");
            String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);
            orderRequest.put("mac", mac);

            HttpEntity request = new HttpEntity(orderRequest);
            ResponseEntity<HashMap> responseEntity = restTemplate.exchange(endpoint + "/create", HttpMethod.POST, request, HashMap.class);

            Map<String, Object> response = responseEntity.getBody();
            if ((int) response.get("return_code") != 1) {
                logger.error(String.valueOf(response.get("return_message")) + " " + String.valueOf(response.get("sub_return_message")));
                throw new InternalServerErrorException("ZaloPay transaction failed");
            }

            response.put("app_id", appId);
            response.put("app_trans_id", appTransId);

            return response;
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public Map<String, Object> receiveCallback(String jsonStr) {
        Map<String, Object> result = new HashMap<>();

        try {
            ZaloPayCallbackResult cbData = objectMapper.readValue(jsonStr, ZaloPayCallbackResult.class);
            String dataStr = cbData.getData();
            String reqMac = cbData.getMac();

            String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key2, dataStr);
            if (!reqMac.equals(mac)) {
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
            } else {
                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0);
            result.put("return_message", ex.getMessage());
        }

        return result;
    }

    public int checkTransactionStatusCode(Transaction transaction) {
        Map<String, Object> externalPaymentInfo = transaction.getExternalPaymentInfo();
        String appTransId = (String) externalPaymentInfo.get("appTransId");

        String data = appId +"|"+ appTransId  +"|"+ key1;
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        Map<String, Object> params = new HashMap<>() {{
            put("app_id", appId);
            put("app_trans_id", appTransId);
            put("mac", mac);
        }};

        HttpEntity request = new HttpEntity(params);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(endpoint + "/query", HttpMethod.POST, request, HashMap.class);

        Map<String, Object> response = responseEntity.getBody();
        int statusCode = (int) response.get("return_code");
        if (statusCode == 3) {
            return ((String) response.get("return_message")).equals("Giao dịch chưa được thực hiện") ? 3 : 4;
        }

        return statusCode;
    }

    public Map<String, Object> refund(Transaction transaction) {
        long currentTimeMillis = System.currentTimeMillis();
        String mRefundId = AppHelper.getCurrentVietnamTimeString("yyMMdd") + "_" + appId + "_" + currentTimeMillis;
        String zpTransId = (String) transaction.getExternalPaymentInfo().get("zpTransToken");

        Map<String, Object> refundRequest = new HashMap<String, Object>(){{
            put("app_id", appId);
            put("zp_trans_id", zpTransId);
            put("m_refund_id", mRefundId);
            put("timestamp", currentTimeMillis);
            put("amount", Math.abs(transaction.getAmount()));
            put("description", "MellowSips - Thanh toán cho đơn hàng #" + transaction.getOrder().getId());
        }};

        String data = refundRequest.get("app_id") +"|"+ refundRequest.get("zp_trans_id") +"|"+ refundRequest.get("amount")
                +"|"+ refundRequest.get("description") +"|"+ refundRequest.get("timestamp");
        refundRequest.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data));

        HttpEntity request = new HttpEntity(refundRequest);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(endpoint + "/refund", HttpMethod.POST, request, HashMap.class);

        Map<String, Object> response = responseEntity.getBody();

        response.put("m_refund_id", mRefundId);
        return response;
    }

    public int checkRefundTransactionStatusCode(String mRefundId) {
        long currentTimeMillis = System.currentTimeMillis();
        String data = appId +"|"+ mRefundId  +"|"+ currentTimeMillis;
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        Map<String, Object> params = new HashMap<>() {{
            put("app_id", appId);
            put("m_refund_id", mRefundId);
            put("timestamp", currentTimeMillis);
            put("mac", mac);
        }};

        HttpEntity request = new HttpEntity(params);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(endpoint + "/query_refund", HttpMethod.POST, request, HashMap.class);

        Map<String, Object> response = responseEntity.getBody();
        int statusCode = (int) response.get("return_code");
        if (statusCode != 1) {
            logger.error(statusCode + " " + String.valueOf(response.get("return_message")) + " " + String.valueOf(response.get("sub_return_message")));
        }

        return statusCode;
    }
}
