package com.lumetix.ai.assistant.factory;

import com.lumetix.entity.model.ModelEnum;

public interface IChatAssistant {


    ModelEnum getModelType();

    RouterChatAssistant getChatAssistant(String appiKey);
}
