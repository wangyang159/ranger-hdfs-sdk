package com.wy.ranger.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * 连接ranger的基础类
 */
public class RangerUtil {
    /**
     * ranger 的地址、管理用户、目的组件
     */
    public static final String RANGER_URL = "http://node1:6080";
    public static final String RANGER_USER = "admin";
    public static final String RANGER_PWD = "admin123A";
    public static final String RANGER_SERVICE_NAME = "hadoopdev";

    /**
     * k8s集群需要的携带额外配置方法
     */
    public static void setupKerberos() throws Exception {
        // 如果是Kerberos环境,需要配置环境文件
        //System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
        Configuration conf = new Configuration();
        //conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        //权限文件
        //UserGroupInformation.loginUserFromKeytab("hdfs@EXAMPLE.COM", "/etc/security/keytabs/hdfs.headless.keytab");
    }
}
