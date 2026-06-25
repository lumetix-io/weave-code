package com.lumetix.ai.assistant.factory;

import com.lumetix.entity.model.ModelEntity;
import com.lumetix.entity.model.ModelEnum;

import java.util.Map;

import static com.lumetix.core.ModelManager.queryModel;

public class AiAssistantFactory {


    public static ModelEntity getModel(ModelEnum modelEnum) {
        return queryModel(modelEnum);
    }


    public static RouterChatAssistant newChatAssistant(ModelEnum modelEnum) {
        Map<ModelEnum, IChatAssistant> assistantMap = Map.of(
                ModelEnum.QIANWEN37MAX, new Qianwen37MaxRouterAssistant()
        );
        ModelEntity modelEntity = getModel(modelEnum);
        IChatAssistant iChatAssistant = assistantMap.get(modelEnum);


        return iChatAssistant.getChatAssistant(modelEntity.getApiKey());
    }
}
