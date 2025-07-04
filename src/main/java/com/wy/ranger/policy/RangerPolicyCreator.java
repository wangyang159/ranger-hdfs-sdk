package com.wy.ranger.policy;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Base64;

import static com.wy.ranger.util.RangerUtil.*;

/**
 * 所有的接口文档见官网：https://ranger.apache.org/apidocs/index.html
 *
 * 不过官网文档很咋，相似的接口比较多，具体使用的时候要自己试一下
 */
public class RangerPolicyCreator {

    /**
     * 新增一个权限策略
     * @param path 目的路径，可是是多个运行是放在json数组中，这里起演示作用，只写了一个
     * @param user 授权用户
     * @param permission 权限标识
     * @return
     * @throws Exception
     */
    public static String createHDFSPolicy(String path, String user, String permission) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //ranger的api访问地址
            HttpPost httpPost = new HttpPost(RANGER_URL + "/service/public/v2/api/policy");

            //访问头
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.setHeader("Accept", "application/json; charset=UTF-8");

            // 管理账号使用基本认证，有其他的认证方式可以查询其他资料，一般都是内外环境用账号就行，外网在企业内一般是安全部门出方案通过跳板机代理隔离，可以做到最小访问联通限制，不会涉及到ranger给别人访问
            String auth = RANGER_USER + ":" + RANGER_PWD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpPost.setHeader("Authorization", "Basic " + encodedAuth);

            // 构建策略JSON
            JSONObject policy = new JSONObject();//最外层的json对象
            policy.put("service", RANGER_SERVICE_NAME);//ranger上的hdfs服务名称
            policy.put("name", "policy_" + path.replace("/", "_"));//策略名称
            policy.put("isEnabled", true);//是否有效
            policy.put("isAuditEnabled", true);//是否审计
            policy.put("description", "新增");//说明

            // 这个策略生效在的路径
            JSONObject pathResource = new JSONObject();
            pathResource.put("values", new String[]{path});//路径数组
            pathResource.put("isExcludes", false);//当前策略对于这组路径是排除还是作用其上
            pathResource.put("isRecursive", true);//是否递归生效子路径

            //所有的路径策略，视为单个的path，包含在resources下
            JSONObject resources = new JSONObject();
            resources.put("path", pathResource);
            policy.put("resources", resources);

            // 构建policyItems部分，包含生效的用户，和权限类型标识
            JSONArray policyItems = new JSONArray();

            //<-----第一组用户权限给只读
            JSONObject policyItem = new JSONObject();
            // 策略作用于那些用户,多个用户放在一起就行
            JSONArray users = new JSONArray();
            users.add(user);
            policyItem.put("users", users);

            // 添加权限类型
            JSONObject access = new JSONObject();
            access.put("type", permission); // 权限标识 "read", "write", "execute"
            access.put("isAllowed", true);//是否是Allow Conditions 一般设置true就行

            JSONArray accesses = new JSONArray();
            accesses.add(access);// 注意！！！这里这里虽然是JSONArray 但给的权限不能重复，也就是最多三个元素
            policyItem.put("accesses", accesses);

            policyItems.add(policyItem);
            //---->第一组权限结束

            //<------第二组权限用来默认给超级用户root可读可写可执行权限
            JSONObject policyItem2 = new JSONObject();
            // 策略作用于那些用户
            JSONArray users2 = new JSONArray();
            users2.add("root");
            policyItem2.put("users", users2);

            // 添加权限类型
            JSONObject access2_1 = new JSONObject();
            access2_1.put("type", permission);
            access2_1.put("isAllowed", true);

            JSONObject access2_2 = new JSONObject();
            access2_2.put("type", "write");
            access2_2.put("isAllowed", true);

            JSONObject access2_3 = new JSONObject();
            access2_3.put("type", "execute");
            access2_3.put("isAllowed", true);

            JSONArray accesses2 = new JSONArray();
            accesses2.add(access2_1);
            accesses2.add(access2_2);
            accesses2.add(access2_3);
            policyItem2.put("accesses", accesses2);

            policyItems.add(policyItem2);
            //----->第二组权限结束

            policy.put("policyItems", policyItems);

            /*
            添加权限有效时间
            关键注释：在网上其他文献中，你会看到ranger操作hdfs权限的时候支持颗粒度到某一个周内的某几天或者某几个小时
                    但是在官网的API文档中并没有提到支持这样的配置，从API调用到自带管理页面都是直接指定某一段时间范围
                    当然如果头铁依靠时间时间范围是个JSON数组，从而强行便利出一个范围，那我只能说算你牛博一
             */
            JSONArray validitySchedules = new JSONArray();
            JSONObject time = new JSONObject();
            time.put("startTime", "2025/06/19 00:00:00");
            time.put("endTime", "2025/07/16 00:00:00");
            time.put("timeZone", "Asia/Shanghai");
            validitySchedules.add(time);
            policy.put("validitySchedules", validitySchedules);

            //这个数据的样例格式可以看createpol.json
            System.out.println(policy.toJSONString());

            // 使用Fastjson2的toJSONString方法生成JSON字符串
            httpPost.setEntity(new StringEntity(policy.toJSONString()));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String result = createHDFSPolicy("/test3", "hive", "read");
        System.out.println("Policy created: " + result);
    }

}