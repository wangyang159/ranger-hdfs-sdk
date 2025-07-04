package com.wy.ranger.policy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Base64;

import static com.wy.ranger.util.RangerUtil.*;

/**
 * 作者: wangyang <br/>
 * 创建时间: 2025/7/4 <br/>
 * 描述: <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;RangerPolicyDelete
 *
 * 删除策略，其实和查询或者跟新用的接口是一个，只不过请求方式用的是DELETE，总的来实ranger的API和ES的操作模式其实是一样的
 */
public class RangerPolicyDelete {

    public static String deleteHDFSPolicy() throws Exception {
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // DELETE /service/public/v2/api/policy/{id}
            HttpDelete httpDelete = new HttpDelete(RANGER_URL + "/service/public/v2/api/policy/5");
            //访问头
            httpDelete.setHeader("Content-Type", "*/*; charset=UTF-8");

            String auth = RANGER_USER + ":" + RANGER_PWD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpDelete.setHeader("Authorization", "Basic " + encodedAuth);

            CloseableHttpResponse response = httpClient.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);

        }catch (Exception e){
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(deleteHDFSPolicy());
    }

}
