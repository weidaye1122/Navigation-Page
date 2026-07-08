package com.tuituidan.openhub.util;

import com.tuituidan.openhub.bean.dto.CardIconDto;
import com.tuituidan.openhub.consts.Consts;
import com.tuituidan.openhub.exception.ResourceWriteException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * IconUtils.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/3/10
 */
@UtilityClass
public class IconUtils {

    /**
     * 从base64字符串中获取文件扩展名.
     */
    private static final Pattern PATTERN = Pattern.compile("data:image/(.*?);base64");

    private static final int MAX_ICON_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList("png", "jpg", "jpeg", "gif", "webp", "ico"));

    /**
     * buildTextIcon
     *
     * @param text text
     * @return CardIconDto
     */
    public static CardIconDto buildTextIcon(String text) {
        return new CardIconDto().setText(StringUtils.truncate(text, 2))
                .setColor(RandomColorUtils.generate());
    }

    /**
     * saveBase64.
     *
     * @param source String
     * @return String
     */
    public static String saveBase64(String source) {
        String[] datas = StringUtils.split(source, ",");
        Assert.isTrue(datas != null && datas.length == 2, "base64图片数据解析失败");
        Matcher matcher = PATTERN.matcher(datas[0]);
        Assert.isTrue(matcher.find(), "base64图片数据解析失败");
        String ext = normalizeImageExt(matcher.group(1));
        byte[] body = Base64.getDecoder().decode(datas[1]);
        Assert.isTrue(body.length > 0 && body.length <= MAX_ICON_SIZE, "base64图片大小异常");
        String path = buildIconSavePath(ext);
        File saveFile = FileExtUtils.forceMkdirParent(Consts.ROOT_DIR + path);
        try {
            FileUtils.writeByteArrayToFile(saveFile, body);
            return path;
        } catch (IOException ex) {
            throw new ResourceWriteException("base64图标保存失败", ex);
        }
    }

    /**
     * saveIcon
     *
     * @param iconUrl iconUrl
     * @return String
     */
    public static String saveIcon(String iconUrl) {
        HttpUtils.assertPublicHttpUrl(iconUrl);
        try {
            String ext = normalizeImageExt(StringUtils.defaultIfBlank(
                    FilenameUtils.getExtension(new URL(iconUrl).getPath()), "ico"));
            String path = buildIconSavePath(ext);
            File saveFile = FileExtUtils.forceMkdirParent(Consts.ROOT_DIR + path);
            byte[] body = HttpUtils.toByteArray(iconUrl);
            Assert.isTrue(body.length > 0 && body.length <= MAX_ICON_SIZE, "网络图标大小异常");
            FileUtils.writeByteArrayToFile(saveFile, body);
            return path;
        } catch (Exception ex) {
            throw new ResourceWriteException("网络图标保存失败", ex);
        }
    }

    private String normalizeImageExt(String ext) {
        String normalized = StringUtils.lowerCase(StringUtils.substringBefore(ext, "+"));
        if (StringUtils.equals(normalized, "x-icon")) {
            normalized = "ico";
        }
        Assert.isTrue(ALLOWED_IMAGE_EXTENSIONS.contains(normalized), "不支持的图片格式");
        return normalized;
    }

    private static String buildIconSavePath(String ext) {
        return StringExtUtils.format("/ext-resources/images/{}/{}.{}",
                DateTimeFormatter.BASIC_ISO_DATE.format(LocalDate.now()),
                StringExtUtils.getUuid(), ext);
    }

}
