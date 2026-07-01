package com.lumetix.ai.assistant.factory;

import com.lumetix.ai.tool.JavaTool;
import com.lumetix.ai.tool.SystemTool;
import com.lumetix.entity.model.ModelEnum;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;

public interface IChatAssistant {


    ModelEnum getModelType();

    UserFaceChatAssistant getChatAssistant(String appiKey);

    default <T> T getAssistant(Class<T> toolClass, StreamingChatModel streamingChatModel) {
        return AiServices.builder(toolClass)
                .streamingChatModel(streamingChatModel)
                .tools(new SystemTool(), new JavaTool())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().id(memoryId).maxMessages(1000).build())
                .build();
    }
}
