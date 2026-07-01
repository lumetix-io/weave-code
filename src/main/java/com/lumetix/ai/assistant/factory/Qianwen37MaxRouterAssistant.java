package com.lumetix.ai.assistant.factory;

import com.lumetix.entity.model.ModelEnum;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;

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
        return getAssistant(UserFaceChatAssistant.class, streamingChatModel);
    }
}
