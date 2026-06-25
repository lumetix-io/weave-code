package com.lumetix.entity.model;

public enum ModelEnum {

    QIANWEN37MAX("qwen", "qwen3.7-max"),
    QIANWEN35FLASH("qwen", "qwen3.5-flash"),
    DEEPSEEKV4PRO("deepseek", "deepseek-v4-pro"),
    ;
    private final String model;

    private final String version;

    ModelEnum(String model, String version) {
        this.model = model;
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public String getVersion() {
        return version;
    }
}
