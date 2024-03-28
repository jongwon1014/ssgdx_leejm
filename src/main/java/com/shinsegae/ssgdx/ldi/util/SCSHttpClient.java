/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.util
 * @fileName : SCSSocketClient.java
 * @author : q93m9k
 * @date : 2024.01.08
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.08       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SCSHttpClient {

    private final CloseableHttpClient scsCloseableHttpClient;

    public int sendPostData(String url, String sendData, Map<String, String> responseData, String httpRecvCharset,
            String httpSendCharset, int connectTimeout, int serviceTimeout){
        int ret = -1;
        HttpPost request = null;
        try{
            StringEntity params = new StringEntity(sendData, httpSendCharset);
            request = new HttpPost(url);
            request.addHeader("content-type", "application/json;charset=" + httpSendCharset);
            request.setEntity(params);

            CloseableHttpResponse httpResponse = scsCloseableHttpClient.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line;
            StringBuffer sb = new StringBuffer();

            while((line = rd.readLine()) != null){
                sb.append(line);
            }
            String response = sb.toString();

            if(response == null || response.isEmpty()){
                log.error("sendPostData -- response ERROR (null or empty)");
            }else{
                ret = 0;
                responseData.put("response", response);
                log.info("sendPostData -- response:[{}]", response);
            }
        }catch(Exception ex){
            log.error("sendPostData -- ERROR (Exception) : [{}]", ex.toString());
        }finally{
            if(request != null)
                request.releaseConnection();
        }
        return ret;
    }
}
