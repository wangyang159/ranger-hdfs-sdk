package com.wy.ranger.policy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Base64;

import static com.wy.ranger.util.RangerUtil.*;

public class RangerPolicyUpdate {
    /**
     * 修改一个权限策略
     * ranger修改权限策略是通过查询同API的PUT请求，明确id后推送最新的全部参数
     * 所以说，业内用ranger完成hdfs的权限时，会尽量控制单挑数据的控制范围
     * 不会说搞几个总的就解决了
     * 一般都是考虑一个规范号维护的组合，比如辅助表权限时，用表+用户做策略名，单挑策略只控制一个用户的访问
     * @throws Exception
     */
    public static String updateHDFSPolicy() throws Exception {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            //先获取到现在完整的策略Json，这个内容正式使用时通常是保存在一个数据库中，而不是现调ranger API去查
            HttpGet httpGet = new HttpGet(RANGER_URL + "/service/public/v2/api/policy/guid/c5488213-54ab-4b90-ba8e-e2209ec84954?serviceName=hadoopdev");
            //访问头
            httpGet.setHeader("Content-Type", "*/*; charset=UTF-8");
            httpGet.setHeader("Accept", "application/json; charset=UTF-8");

            String auth = RANGER_USER + ":" + RANGER_PWD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpGet.setHeader("Authorization", "Basic " + encodedAuth);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(entity));

            System.out.println("拿到的Json");
            System.out.println(jsonObject.toJSONString());

            //修改需要变动的内容，比如这里修改说明
            jsonObject.put("description", "新增变修改");

            //用put请求写回去
            HttpPut httpPut = new HttpPut(RANGER_URL + "/service/public/v2/api/policy/"+jsonObject.get("id"));
            //访问头
            httpPut.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPut.setHeader("Accept", "application/json; charset=UTF-8");
            httpPut.setEntity(new StringEntity(jsonObject.toJSONString()));
            response = httpClient.execute(httpPut);
            entity = response.getEntity();
            return EntityUtils.toString(entity);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void main(String[] args) throws Exception {
        System.out.println("最终的响应");
        System.out.println(updateHDFSPolicy());
    }
}
