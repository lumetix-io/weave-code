package com.lumetix.entity;

public enum ModelEnum {
    DEEP_SEEK("DeepSeek", "DeepSeek"),
    KIMI("Kimi", "Kimi"),
    QIANWEN("Qianwen", "Qianwen"),
    GLM("GLM", "GLM");

    private final String code;
    private final String desc;

    ModelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
