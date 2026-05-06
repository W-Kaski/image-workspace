package com.eric.ekcloudgallerybackend.manage.websocket.model;

import lombok.Getter;

/**
 * 图片编辑动作枚举
 */
@Getter
public enum PictureEditActionEnum {

    ZOOM_IN("zoom in", "ZOOM_IN"),
    ZOOM_OUT("zoom out", "ZOOM_OUT"),
    ROTATE_LEFT("rotate left", "ROTATE_LEFT"),
    ROTATE_RIGHT("rotate right", "ROTATE_RIGHT");

    private final String text;
    private final String value;

    PictureEditActionEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static PictureEditActionEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditActionEnum actionEnum : PictureEditActionEnum.values()) {
            if (actionEnum.value.equals(value)) {
                return actionEnum;
            }
        }
        return null;
    }
}