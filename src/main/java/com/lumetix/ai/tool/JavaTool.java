package com.lumetix.ai.tool;

import dev.langchain4j.agent.tool.Tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JavaTool {

    @Tool("获取当前系统上安装的java运行环境的版本号")
    public String getJavaVersion() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"java", "-version"});
            String version = new BufferedReader(new InputStreamReader(p.getErrorStream()))
                    .lines().findFirst().orElse("");
            p.waitFor();
            return version.isEmpty() ? "未检测到Java运行环境" : version;
        } catch (Exception e) {
            return "获取Java版本失败: " + e.getMessage();
        }
    }
}