package com.lumetix.ai.tool;

import dev.langchain4j.agent.tool.Tool;

public class SystemTool {

    private static final String OS_NAME = initOsName();

    private static String initOsName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) return "mac";
        if (os.contains("win")) return "windows";
        return "linux";
    }

    @Tool(name = "获取当前操作系统类型，返回 mac、windows 或 linux")
    public String getOsName() {
        return OS_NAME;
    }
}
