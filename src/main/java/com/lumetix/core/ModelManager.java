package com.lumetix.core;

import com.lumetix.entity.model.ModelEntity;
import com.lumetix.entity.model.ModelEnum;

import java.util.Objects;

import static com.lumetix.core.DbManager.getJdbi;

public class ModelManager {

    public static ModelEntity queryModel(ModelEnum modelEnum) {
        return getJdbi().withHandle(handle ->
                Objects.requireNonNull(handle.createQuery("SELECT * FROM model WHERE name = :name AND version = :version AND deleted_at IS NULL")
                        .bind("name", modelEnum.getModel())
                        .bind("version", modelEnum.getVersion())
                        .mapToBean(ModelEntity.class)
                        .findOne()
                        .orElse(null)));
    }
}
