package com.hjw.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qifei
 * @since 2024-03-26
 */
@Configuration
public class OssUtil
{
    @Value("${oss.aliyun.endpoint}")
    public String ENDPOINT;


    @Value("${oss.aliyun.accessKeyId}")
    public String ACCESSKEY_ID;

    @Value("${oss.aliyun.accessKeySecret}")
    public String ACCESSKEY_SECRET;

    // 初始化OSS
    @Bean
    public OSS oss()
    {
        return new OSSClientBuilder().build(ENDPOINT, ACCESSKEY_ID, ACCESSKEY_SECRET);
    }
}
