package com.lumetix.core;

import com.lumetix.entity.QuestEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.lumetix.core.Chat.refreshChatDataByChatId;
import static com.lumetix.core.Common.getSelectFolderPath;
import static com.lumetix.core.DbManager.getJdbi;
import static com.lumetix.entity.BasicConstants.ChatUi.chatList;
import static com.lumetix.entity.BasicConstants.ChatUi.chatModel;
import static com.lumetix.entity.BasicConstants.InPutUi.*;

public class ProjectManager {

    public static void itemClick(QuestEntity item) {
        if (item == null) {
            return;
        }
        QuestEntity queryCurProject = null;
        Boolean isProject = item.getIsProject();
        if (isProject) {
            chatModel.setValue(false);
            queryCurProject = item;
            curTaskId.set(0);
        } else {
            curTaskId.set(item.getId());
            chatModel.setValue(true);
            refreshChatDataByChatId(item.getChatId());
            queryCurProject = getJdbi().withHandle(handle ->
                    Objects.requireNonNull(handle.createQuery("SELECT * FROM quest_list WHERE id = :id AND deleted_at IS NULL")
                            .bind("id", item.getParentId())
                            .mapToBean(QuestEntity.class)
                            .findOne()
                            .orElse(null)));
        }
        Integer isExpand = getItemIsExpand(item);
        List<Long> ids = Arrays.asList(item.getId(), queryCurProject.getId());
        getJdbi().useHandle(handle ->
                handle.createUpdate(
                                "UPDATE quest_list SET update_at = datetime('now', 'localtime'),is_expand=:isExpand WHERE id IN (<ids>) AND deleted_at IS NULL"
                        )
                        .bindList("ids", ids)
                        .bind("isExpand", isExpand)
                        .execute()
        );
        curProjectTitle.setValue(queryCurProject.getTitle());
        curProject.set(queryCurProject.getId());

    }

    private static Integer getItemIsExpand(QuestEntity item) {
        Boolean isProject = item.getIsProject();
        if (!Boolean.TRUE.equals(isProject)) {
            return 0;
        }
        Boolean isExpand = item.getIsExpand();
        if (Boolean.TRUE.equals(isExpand)) {
            return 0;
        }
        return 1;
    }

    public static void createProject() {

        File selectFolder = getSelectFolderPath();
        if (Objects.isNull(selectFolder)) {
            return;
        }
        QuestEntity project = new QuestEntity();
        project.setParentId(-1L);
        project.setTitle(selectFolder.getName());
        project.setAbsoluteFullPath(selectFolder.getAbsolutePath());
        project.setIsProject(true);
        project.setIsExpand(true);


        long projectId = getJdbi().withHandle(handle ->
                handle.createUpdate(
                                "INSERT INTO quest_list (parent_id, title, absolute_full_path, is_expand, is_project) " +
                                        "VALUES (:parentId, :title, :absoluteFullPath, :isExpand, :isProject)")
                        .bind("parentId", project.getParentId())
                        .bind("title", project.getTitle())
                        .bind("absoluteFullPath", project.getAbsoluteFullPath())
                        .bind("isExpand", project.getIsExpand())
                        .bind("isProject", project.getIsProject())
                        .executeAndReturnGeneratedKeys("id")
                        .mapTo(Long.class)
                        .one());
        curProjectTitle.set(project.getTitle());
        curProject.set(projectId);
        treeListFresh.setValue(treeListFresh.getValue() + 1);
        chatModel.setValue(false);
        chatList.clear();
    }


    public static List<QuestEntity> getQuestTreeData() {
        List<QuestEntity> questEntities = getJdbi().withHandle(handle ->
                handle.createQuery("SELECT * FROM quest_list WHERE deleted_at IS NULL ORDER BY update_at DESC")
                        .mapToBean(QuestEntity.class)
                        .list()
        );
        if (Objects.isNull(questEntities) || questEntities.isEmpty()) {
            return new ArrayList<>();
        }

        //设置当前project
        QuestEntity firstProject = findFirstProject(questEntities);
        if (Objects.nonNull(firstProject)) {
            curProject.setValue(firstProject.getId());
            curProjectTitle.setValue(firstProject.getTitle());
        }
        QuestEntity root = new QuestEntity();
        root.setId(-1L);
        root.setParentId(0L);
        root.setTitle("Root");
        questEntities.addFirst(root);
        return questEntities;
    }

    private static QuestEntity findFirstProject(List<QuestEntity> questEntities) {
        for (QuestEntity v : questEntities) {
            if (Boolean.TRUE.equals(v.getIsProject())) {
                return v;
            }
        }
        return null;
    }

    private static QuestEntity findFirstTask(List<QuestEntity> questEntities) {
        for (QuestEntity v : questEntities) {
            if (Boolean.TRUE.equals(v.getIsProject())) {
                continue;
            }
            return v;
        }
        return null;
    }
}
