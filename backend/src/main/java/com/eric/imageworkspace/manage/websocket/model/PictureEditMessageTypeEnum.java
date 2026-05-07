package com.eric.imageworkspace.manage.websocket.model;

import lombok.Getter;

/**
 * 图片编辑消息类型枚举
 */
@Getter
public enum PictureEditMessageTypeEnum {

    INFO("INFO", "INFO"),
    ERROR("ERROR", "ERROR"),
    ENTER_EDIT("ENTER_EDIT", "ENTER_EDIT"),
    EXIT_EDIT("EXIT_EDIT", "EXIT_EDIT"),
    EDIT_ACTION("EDIT_ACTION", "EDIT_ACTION");

    private final String text;
    private final String value;

    PictureEditMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static PictureEditMessageTypeEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditMessageTypeEnum typeEnum : PictureEditMessageTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}