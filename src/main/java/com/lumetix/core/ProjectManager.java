package com.lumetix.core;

import com.lumetix.entity.tree.ProjectNode;
import com.lumetix.entity.tree.QuestEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.lumetix.core.ChatManager.refreshChatDataByChatId;
import static com.lumetix.core.Common.getSelectFolderPath;
import static com.lumetix.core.DbManager.getJdbi;
import static com.lumetix.core.SerializationUtil.deserializeFromString;
import static com.lumetix.entity.BasicConstants.ChatUi.chatList;
import static com.lumetix.entity.BasicConstants.ChatUi.chatModel;
import static com.lumetix.entity.BasicConstants.InPutUi.*;
import static com.lumetix.entity.tree.TreeNodeType.PROJECT;

public class ProjectManager {

    public static void itemClick(QuestEntity item) {
        if (item == null) {
            return;
        }
        QuestEntity queryCurProject = null;
        String nodeType = item.getType();
        if (PROJECT.name().equals(nodeType)) {
            queryCurProject = item;
            curTaskId.set(0);
        } else {
            queryCurProject = getJdbi().withHandle(handle ->
                    Objects.requireNonNull(handle.createQuery("SELECT * FROM quest_list WHERE parent_id = :parentId AND deleted_at IS NULL").
                            bind("parentId",
                                    item.getParentId()).
                            mapToBean(QuestEntity.class).
                            findOne().orElse(null)));
            refreshChatDataByChatId(item.getId());
            curTaskId.set(item.getId());
        }
        List<Long> ids = Arrays.asList(item.getId(), queryCurProject.getId());
        getJdbi().useHandle(handle ->
                handle.createUpdate("UPDATE quest_list SET update_at = datetime('now', 'localtime') WHERE id IN (<ids>) AND deleted_at IS NULL").
                        bindList("ids", ids).
                        execute());
        curProjectTitle.setValue(queryCurProject.getTitle());
        curProject.set(queryCurProject.getId());
    }

    private static Integer getItemIsExpand(QuestEntity item) {

        if (!PROJECT.name().equals(item.getType())) {
            return 0;
        }
        String expand = item.getExpand();
        if (Objects.isNull(expand) || expand.isEmpty()) {
            return 0;
        }
        ProjectNode projectNode = deserializeFromString(expand);
        if (Objects.isNull(projectNode)) {
            return 0;
        }
        Boolean isExpand = projectNode.getIsExpand();
        return Boolean.TRUE.equals(isExpand) ? 1 : 0;
    }

    public static void createProject() {

        File selectFolder = getSelectFolderPath();
        if (Objects.isNull(selectFolder)) {
            return;
        }
        QuestEntity project = new QuestEntity();
        project.setParentId(-1L);
        project.setTitle(selectFolder.getName());


        ProjectNode projectNode = new ProjectNode();
        projectNode.setProjectId(project.getId());
        projectNode.setAbsoluteFullPath(selectFolder.getAbsolutePath());
        projectNode.setIsExpand(true);
        project.setExpand(SerializationUtil.serializeToString(projectNode));


        long projectId = getJdbi().withHandle(handle ->
                handle.createUpdate(
                                "INSERT INTO quest_list (parent_id, title, expand) " +
                                        "VALUES (:parentId, :title, :expand)").
                        bind("parentId", project.getParentId()).
                        bind("title", project.getTitle()).
                        bind("type", project.getType()).
                        bind("expand", project.getExpand()).
                        executeAndReturnGeneratedKeys("id").mapTo(Long.class).one());
        curProjectTitle.set(project.getTitle());
        curProject.set(projectId);
        treeListFresh.setValue(treeListFresh.getValue() + 1);
        chatModel.setValue(false);
        chatList.clear();
    }


    public static List<QuestEntity> getQuestTreeData() {
        List<QuestEntity> questEntities = getJdbi().withHandle(handle -> handle.createQuery("SELECT * FROM quest_list WHERE deleted_at IS NULL ORDER BY update_at DESC").mapToBean(QuestEntity.class).list());
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
            if (PROJECT.name().equals(v.getType())) {
                return v;
            }
        }
        return null;
    }
}
