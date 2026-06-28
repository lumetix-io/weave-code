package com.lumetix.ai.assistant.factory;

import com.lumetix.ai.tool.SystemTool;
import com.lumetix.entity.model.ModelEnum;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;

public class Qianwen37MaxRouterAssistant implements IChatAssistant {


    @Override
    public ModelEnum getModelType() {
        return ModelEnum.QIANWEN37MAX;
    }

    @Override
    public UserFaceChatAssistant getChatAssistant(String appiKey) {
        StreamingChatModel streamingChatModel = QwenStreamingChatModel.builder()
                .apiKey(appiKey)
                .modelName("qwen3.7-max")
                .build();
        return AiServices.builder(UserFaceChatAssistant.class)
                .streamingChatModel(streamingChatModel)
                .tools(new SystemTool())
                // .tools(new TicketToolManager())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder().id(memoryId).maxMessages(1000).build())
                .build();
    }
}
