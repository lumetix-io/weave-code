package com.lumetix.ai.assistant;


import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDate;

public interface AbstractAiAssistant {

    TokenStream chat(@UserMessage String userMessage, @V("curDate") LocalDate curDate);
}
