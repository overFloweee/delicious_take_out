package com.hjw.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.hjw.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@Slf4j
// 这俩是生成接口文档
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig extends WebMvcConfigurationSupport
{
    // 设置静态资源映射
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // log.info("开始静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        // 访问接口文档
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    // 扩展mvc框架的消息转换器
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        // log.info("扩展消息转换器...");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用jackson将java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的 消息转换器 对象，追加到mvc框架的转换器容器
        converters.add(0, messageConverter);
    }


    @Bean
    public Docket createRestApi()
    {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.hjw.controller")).paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder().title("美味在路上").version("v1.0").description("美味在路上接口文档").build();
    }
}
