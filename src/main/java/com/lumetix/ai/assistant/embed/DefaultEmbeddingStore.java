package com.lumetix.ai.assistant.embed;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.util.List;

public class DefaultEmbeddingStore implements EmbeddingStore {
    @Override
    public String add(Embedding embedding) {
        return "";
    }

    @Override
    public void add(String id, Embedding embedding) {

    }

    @Override
    public String add(Embedding embedding, Object o) {
        return "";
    }

    @Override
    public EmbeddingSearchResult search(EmbeddingSearchRequest request) {
        return null;
    }

    @Override
    public List<String> addAll(List list) {
        return List.of();
    }
}
