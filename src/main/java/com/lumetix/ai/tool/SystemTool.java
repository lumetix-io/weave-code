package com.lumetix.ai.tool;

import dev.langchain4j.agent.tool.Tool;

public class SystemTool {

    private static final String OS_TYPE;
    private static final String OS_NAME;
    private static final String OS_VERSION;
    private static final String OS_ARCH;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        OS_NAME = System.getProperty("os.name");
        OS_VERSION = System.getProperty("os.version");
        OS_ARCH = System.getProperty("os.arch");
        if (os.contains("mac")) {
            OS_TYPE = "mac";
        } else if (os.contains("win")) {
            OS_TYPE = "windows";
        } else {
            OS_TYPE = "linux";
        }
    }

    @Tool("获取当前操作系统信息，包含系统类型、版本、架构")
    public String getSystemInfo() {
        return String.format("类型: %s | 系统: %s | 版本: %s | 架构: %s",
                OS_TYPE, OS_NAME, OS_VERSION, OS_ARCH);
    }
}