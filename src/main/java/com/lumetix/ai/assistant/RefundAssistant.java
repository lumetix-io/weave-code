package com.lumetix.ai.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDate;

public interface RefundAssistant extends AbstractAiAssistant {

    @SystemMessage("你是南京航空公司的票务助手，，你负责以亲切，友好的用语，帮助引导用户实现退票业务，今天的日期是{{curDate}}")
    TokenStream chat(@UserMessage String userMessage, @V("curDate") LocalDate curDate);
}
