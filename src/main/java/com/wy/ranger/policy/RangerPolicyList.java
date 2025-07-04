package com.wy.ranger.policy;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Base64;

import static com.wy.ranger.util.RangerUtil.*;


public class RangerPolicyList {

    /**
     * 查询已有权限列表
     */
    public static String listHDFSPolicy() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //ranger的api访问地址 + GET /service/public/v2/api/service/{servicename}/policy/{policyname}  不加{policyname}是查询所有
            //如果你记录了id也可以用 GET /service/public/v2/api/policy/{id}
            //如果你记录了guid也可以用 GET /service/public/v2/api/policy/guid/{guid}?serviceName=...
            HttpGet httpGet = new HttpGet(RANGER_URL + "/service/public/v2/api/policy/guid/c5488213-54ab-4b90-ba8e-e2209ec84954?serviceName=hadoopdev");
//            HttpGet httpGet = new HttpGet(RANGER_URL + "/service/public/v2/api/policy/5");

            //访问头
            httpGet.setHeader("Content-Type", "*/*; charset=UTF-8");
            httpGet.setHeader("Accept", "application/json; charset=UTF-8");


            // 管理账号使用基本认证，有其他的认证方式可以查询其他资料，一般都是内外环境用账号就行，外网在企业内一般是安全部门出方案通过跳板机代理隔离，可以做到最小访问联通限制，不会涉及到ranger给别人访问
            String auth = RANGER_USER + ":" + RANGER_PWD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpGet.setHeader("Authorization", "Basic " + encodedAuth);


            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(listHDFSPolicy());
    }
}
