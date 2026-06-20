package com.lumetix.core;

import com.lumetix.entity.ChatDetail;
import com.lumetix.entity.ChatEnum;
import com.lumetix.entity.QuestEntity;

import java.util.List;
import java.util.Objects;

import static com.lumetix.core.DbManager.getJdbi;
import static com.lumetix.entity.BasicConstants.ChatUi.chatList;
import static com.lumetix.entity.BasicConstants.ChatUi.chatModel;
import static com.lumetix.entity.BasicConstants.InPutUi.*;

public class Chat {


    private final static Long ZERO_ID = 0L;

    public static void send() {
        long projectId = curProject.get();
        isExecuteTask.set(true);

        //获取最新的聊天记录chatId
        Long taskId = curTaskId.get();

        //如果用户没有选择项目
        ChatDetail robotChat = null;
        ChatDetail userSendChat = getUserSendChat(taskId);
        if (projectId == 0) {
            robotChat = robotAnswer(taskId);
        } else {
            if (ZERO_ID.equals(taskId)) {
                QuestEntity task = new QuestEntity();
                task.setIsExpand(false);
                task.setIsProject(false);
                task.setTitle(sendMsg.getValue());
                task.setAbsoluteFullPath("");
                task.setParentId(curProject.get());
                task.setAbsoluteFullPath("");
                taskId = getJdbi().withHandle(handle ->
                        handle.createUpdate("INSERT INTO quest_list (parent_id, chat_id, title, absolute_full_path, is_expand, is_project) VALUES (:parentId, :chatId, :title, :absoluteFullPath, :isExpand, :isProject)").
                                bind("parentId", task.getParentId()).
                                bind("title", task.getTitle()).
                                bind("absoluteFullPath", task.getAbsoluteFullPath()).
                                bind("isExpand", task.getIsExpand() ? 1 : 0).
                                bind("isProject", task.getIsProject() ? 1 : 0).
                                executeAndReturnGeneratedKeys("id")
                                .mapTo(Long.class)
                                .one());
                userSendChat.setChatId(taskId);
            }
            getJdbi().useHandle(handle ->
                    handle.createUpdate("INSERT INTO chat_detail (chat_id, type, content) VALUES (:chatId, :type, :content)").
                            bind("chatId", userSendChat.getChatId()).
                            bind("type", userSendChat.getType()).
                            bind("content", userSendChat.getContent()).
                            execute());
        }
        chatList.getValue().add(userSendChat);
        if (Objects.nonNull(robotChat)) {
            chatList.getValue().add(robotChat);
        }
        isExecuteTask.set(false);
        sendMsg.set("");
        curTaskId.set(taskId);
        chatModel.setValue(true);
        treeListFresh.setValue(treeListFresh.getValue() + 1);
    }

    private static ChatDetail robotAnswer(Long chatId) {
        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setChatId(chatId);
        chatDetail.setType(ChatEnum.ROBOT.name());
        chatDetail.setContent("请先选择您的项目");
        return chatDetail;
    }

    private static ChatDetail getUserSendChat(Long chatId) {
        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setChatId(chatId);
        chatDetail.setType(ChatEnum.USER.name());
        chatDetail.setContent(sendMsg.get());
        return chatDetail;
    }

    private static Long getNextChatId() {
        Long maxId = getJdbi().withHandle(handle -> handle.createQuery("SELECT MAX(id) FROM chat").mapTo(Long.class).findOne().orElse(0L));
        return maxId + 1;
    }

    public static void refreshChatDataByChatId(Long chatId) {
        List<ChatDetail> detailList = getJdbi().withHandle(handle ->
                handle.createQuery("SELECT * FROM chat_detail WHERE chat_id = :chatId AND deleted_at IS NULL ORDER BY create_at ASC").
                        bind("chatId", chatId).
                        mapToBean(ChatDetail.class).list());
        chatList.clear();
        chatList.setAll(detailList);
    }
}
