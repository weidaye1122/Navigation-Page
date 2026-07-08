package com.tuituidan.openhub.consts;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UploadTypeEnum.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/3/1
 */
@Getter
@AllArgsConstructor
public enum UploadTypeEnum {
    /**
     * 默认
     */
    DEFAULT("default"),

    /**
     * 还原文件
     */
    REVERT("revert"),

    /**
     * 浏览器书签
     */
    BOOKMARK("bookmark"),

    /**
     * 卡片附件
     */
    ATTACHMENTS("attachments"),

    /**
     * 卡片图标
     */
    IMAGES("images"),

    /**
     * 静态网站压缩包
     */
    MODULES("modules");

    private String type;

    public static boolean contains(String type) {
        return Arrays.stream(values()).anyMatch(item -> item.getType().equals(type));
    }
}
