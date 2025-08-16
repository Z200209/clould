package com.example.provider.service.game;

import lombok.Getter;


@Getter
public enum IntroductionDefine {
    INTRODUCTION_CONTENT_TYPE_TEXT(1, "text"),
    INTRODUCTION_CONTENT_TYPE_IMAGE(2, "image"),
    INTRODUCTION_CONTENT_TYPE_VIDEO(3, "video");

    private final int code;
    private final String name;

    IntroductionDefine(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static boolean isIntroductionType(String name) {
        if (INTRODUCTION_CONTENT_TYPE_TEXT.getName().equals(name)) {
            return true;
        }
        if (INTRODUCTION_CONTENT_TYPE_IMAGE.getName().equals(name)) {
            return true;
        }
        return INTRODUCTION_CONTENT_TYPE_VIDEO.getName().equals(name);
    }

}
