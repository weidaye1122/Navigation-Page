package com.tuituidan.openhub.config;

import com.tuituidan.openhub.consts.Consts;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.Arrays;
import javax.servlet.MultipartConfigElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * AppConfig.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2021/3/5
 */
@Configuration
public class AppConfig {

    /**
     * 连接超时时间.
     */
    private static final int CONNECT_TIMEOUT = 30;

    /**
     * 读取数据时间.
     */
    private static final int READ_TIMEOUT = 120;

    @Value("${team-nav.security.cors.allowed-origin-patterns:}")
    private String allowedOriginPatterns;

    /**
     * 初始RestTemplate 设置超时时间.
     *
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(NoRedirectSimpleClientHttpRequestFactory::new)
                .setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
                .setReadTimeout(Duration.ofSeconds(READ_TIMEOUT))
                .build();
    }

    /**
     * 明确指定上传文件的临时目录，避免Tomcat使用默认的tmp而被操作系统清理掉
     *
     * @return MultipartConfigElement
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = Consts.ROOT_DIR + "/temp";
        File tmpFile = new File(location);
        if (!tmpFile.exists()) {
            Assert.isTrue(tmpFile.mkdirs(), "临时上传路径创建失败");
        }
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        if (StringUtils.isNotBlank(allowedOriginPatterns)) {
            config.setAllowCredentials(true);
            Arrays.stream(StringUtils.split(allowedOriginPatterns, ","))
                    .map(StringUtils::trimToEmpty)
                    .filter(StringUtils::isNotBlank)
                    .forEach(config::addAllowedOriginPattern);
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            config.setMaxAge(1800L);
        }
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }

    private static class NoRedirectSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);
            connection.setInstanceFollowRedirects(false);
        }
    }

}
