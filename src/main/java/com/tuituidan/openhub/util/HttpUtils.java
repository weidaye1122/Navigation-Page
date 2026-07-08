package com.tuituidan.openhub.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.tuituidan.openhub.bean.dto.CardDynamicBuilder;
import com.tuituidan.openhub.bean.dto.KeyValueDto;
import com.tuituidan.openhub.consts.AuthTypeEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * HttpUtils.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/2/17
 */
@UtilityClass
public class HttpUtils {

    private static final int HTTP_TIMEOUT = 5000;

    private static final String HTTPS = "https";

    private static final String HTTP = "http";

    /**
     * isHttp
     *
     * @param url url
     * @return boolean
     */
    public boolean isHttp(String url) {
        try {
            String protocol = new URL(url).getProtocol();
            return HTTP.equals(protocol) || HTTPS.equals(protocol);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * isHttps
     *
     * @param url url
     * @return boolean
     */
    public boolean isHttps(String url) {
        try {
            return HTTPS.equals(new URL(url).getProtocol());
        } catch (Exception ex) {
            throw new IllegalArgumentException("url解析错误", ex);
        }
    }

    /**
     * isHttps
     *
     * @param request request
     * @return boolean
     */
    public boolean isHttps(HttpServletRequest request) {
        return HTTPS.equalsIgnoreCase(request.getScheme());
    }

    /**
     * 获取url的根路径，如http://www.test.com/aa/xx.html -> http://www.test.com
     *
     * @param orgUrl 原始url
     * @return url
     */
    public static String getDomainUrl(String orgUrl) {
        assertPublicHttpUrl(orgUrl);
        try {
            URL url = new URL(orgUrl);
            StringBuilder sb = new StringBuilder(url.getProtocol())
                    .append("://").append(url.getHost());
            if (url.getPort() != -1) {
                sb.append(":").append(url.getPort());
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new IllegalArgumentException("url解析错误", ex);
        }
    }

    /**
     * 获取网站favicon
     *
     * @param domainUrl domainUrl
     * @return String
     */
    public static String getFromDocument(String domainUrl) {
        Document doc = HttpUtils.getJsoupDoc(domainUrl);
        if (Objects.isNull(doc)) {
            return StringUtils.EMPTY;
        }
        Elements links = doc.head().children().select("link[rel~=icon]");
        if (links.isEmpty()) {
            return "";
        }
        String href = links.get(0).attr("href");
        return requestFavicon(formatLinkIcon(domainUrl, href));
    }

    /**
     * 从link拿到的格式很多种，这里统一格式化一下
     *
     * @param domainUrl domainUrl
     * @param href href
     * @return String
     */
    private static String formatLinkIcon(String domainUrl, String href) {
        if (StringUtils.startsWith(href, HTTP)) {
            return href;
        }
        if (StringUtils.startsWith(href, "//")) {
            return StringUtils.substringBefore(domainUrl, "//") + href;
        }
        if (StringUtils.startsWith(href, "/")) {
            return domainUrl + href;
        }
        return domainUrl + "/" + href;
    }

    /**
     * 获取url的favicon
     *
     * @param url url
     * @return String
     */
    public static String requestFavicon(String url) {
        byte[] body = HttpUtils.toByteArray(url);
        // 要能实际获取到favicon的数据，如果返回是一个html文件，往往是鉴权导致重定向了
        if (ArrayUtils.isNotEmpty(body) && !FileExtUtils.isHtml(body)) {
            if (HttpUtils.isHttps(RequestUtils.getRequest())) {
                return url;
            }
            if (!HttpUtils.isHttps(url)) {
                return url;
            }
            return IconUtils.saveIcon(url);
        }
        return "";
    }

    /**
     * getJsoupDoc
     *
     * @param url url
     * @return Document
     */
    public Document getJsoupDoc(String url) {
        assertPublicHttpUrl(url);
        try {
            return Jsoup.connect(url)
                    .followRedirects(false)
                    .timeout(HTTP_TIMEOUT)
                    .get();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * toByteArray
     *
     * @param url url
     * @return byte[]
     */
    public byte[] toByteArray(String url) {
        assertPublicHttpUrl(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(HTTP_TIMEOUT);
            conn.setReadTimeout(HTTP_TIMEOUT);
            try (InputStream inputStream = conn.getInputStream()) {
                return IOUtils.toByteArray(inputStream);
            }
        } catch (Exception ex) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void assertPublicHttpUrl(String url) {
        try {
            URL target = new URL(url);
            String protocol = target.getProtocol();
            Assert.isTrue(HTTP.equals(protocol) || HTTPS.equals(protocol), "仅支持http或https地址");
            String host = target.getHost();
            Assert.hasText(host, "url host不能为空");
            Assert.isTrue(!StringUtils.equalsIgnoreCase(host, "localhost")
                    && !StringUtils.endsWithIgnoreCase(host, ".local"), "不允许访问内网地址");
            for (InetAddress address : InetAddress.getAllByName(host)) {
                Assert.isTrue(isPublicAddress(address), "不允许访问内网地址");
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("url解析错误", ex);
        }
    }

    private boolean isPublicAddress(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress() || address.isMulticastAddress()) {
            return false;
        }
        byte[] bytes = address.getAddress();
        if (bytes.length == 4) {
            int first = bytes[0] & 0xff;
            int second = bytes[1] & 0xff;
            if (first == 0 || first >= 224) {
                return false;
            }
            if (first == 100 && second >= 64 && second <= 127) {
                return false;
            }
            if (first == 198 && (second == 18 || second == 19)) {
                return false;
            }
        }
        if (bytes.length == 16) {
            int first = bytes[0] & 0xff;
            return (first & 0xfe) != 0xfc;
        }
        return true;
    }

    /**
     * buildQueryParams
     *
     * @param url url
     * @param queryParams queryParams
     * @return String
     */
    public static String buildQueryParams(String url, List<KeyValueDto> queryParams) {
        assertPublicHttpUrl(url);
        if (CollectionUtils.isEmpty(queryParams)) {
            return url;
        }
        List<KeyValueDto> params = queryParams.stream().filter(it -> StringUtils.isNoneBlank(it.getKey(),
                it.getValue())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(params)) {
            return url;
        }
        UriComponents build = UriComponentsBuilder.fromHttpUrl(url).build();
        if (StringUtils.isBlank(build.getQuery())) {
            return url + "?" + queryParams.stream().map(it -> it.getKey() + "=" + it.getValue())
                    .collect(Collectors.joining("&"));
        }
        StringBuilder sb = new StringBuilder(url);
        for (KeyValueDto item : queryParams) {
            if (!build.getQueryParams().containsKey(item.getKey())) {
                sb.append("&").append(item.getKey()).append("=").append(item.getValue());
            }
        }
        return sb.toString();
    }

    /**
     * buildHttpEntity
     *
     * @param dynamicBuilder dynamicBuilder
     * @return HttpEntity
     */
    public static HttpEntity<Object> buildHttpEntity(CardDynamicBuilder dynamicBuilder) {
        HttpHeaders httpHeaders = buildHttpHeaders(dynamicBuilder);
        if (JsonFactory.FORMAT_NAME_JSON.equalsIgnoreCase(dynamicBuilder.getBodyType())) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new HttpEntity<>(dynamicBuilder.getBodyJsonData(), httpHeaders);
        }
        if (FileUploadBase.FORM_DATA.equalsIgnoreCase(dynamicBuilder.getBodyType())) {
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
            return new HttpEntity<>(paramMap, httpHeaders);
        }
        return new HttpEntity<>(httpHeaders);
    }

    private static HttpHeaders buildHttpHeaders(CardDynamicBuilder dynamicBuilder) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (AuthTypeEnum.KEY_VALUE.getType().equals(dynamicBuilder.getAuthType())) {
            for (KeyValueDto item : dynamicBuilder.getAuthKeyValues()) {
                if (StringUtils.isNoneBlank(item.getKey(), item.getValue())) {
                    httpHeaders.add(item.getKey(), item.getValue());
                }
            }
            return httpHeaders;
        }
        if (AuthTypeEnum.BASIC.getType().equals(dynamicBuilder.getAuthType())) {
            httpHeaders.setBasicAuth(dynamicBuilder.getBasicUsername(), dynamicBuilder.getBasicPassword(),
                    StandardCharsets.UTF_8);
            return httpHeaders;
        }
        if (AuthTypeEnum.BEARER.getType().equals(dynamicBuilder.getAuthType())) {
            httpHeaders.setBearerAuth(dynamicBuilder.getBearerToken());
            return httpHeaders;
        }
        if (AuthTypeEnum.JWT.getType().equals(dynamicBuilder.getAuthType())) {
            List<KeyValueDto> list = dynamicBuilder.getJwtPayload().stream()
                    .filter(it -> StringUtils.isNoneBlank(it.getKey(), it.getValue())).collect(Collectors.toList());
            Map<String, Object> map = list.stream().collect(Collectors.toMap(KeyValueDto::getKey,
                    KeyValueDto::getValue));
            SignatureAlgorithm algorithm = SignatureAlgorithm.forName(dynamicBuilder.getJwtAlgorithm());
            httpHeaders.setBearerAuth(Jwts.builder().setClaims(map).signWith(algorithm,
                    dynamicBuilder.getJwtSecret()).compact());
            return httpHeaders;
        }
        return httpHeaders;
    }

}
