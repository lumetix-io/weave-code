package com.lumetix.core;

import com.lumetix.entity.ChatDetail;
import com.lumetix.entity.ChatEnum;
import com.lumetix.entity.QuestEntity;

import java.util.List;

import static com.lumetix.core.DbManager.getJdbi;
import static com.lumetix.entity.BasicConstants.ChatUi.*;
import static com.lumetix.entity.BasicConstants.InPutUi.*;

public class Chat {


    private final static Integer ZERO_CHAT_ID = 0;

    public static void send() {
        long projectId = curProject.get();
        isExecuteTask.set(true);

        //获取最新的聊天记录chatId
        int chatId = curChatId.get();
        if (chatId == ZERO_CHAT_ID) {
            chatId = getNextChatId();
        }
        //获取用户发送记录，并且添加到聊天记录
        ChatDetail userSendChat = getUserSendChat(chatId);
        chatList.getValue().add(userSendChat);
        //如果用户没有选择项目
        if (projectId == 0) {
            robotAnswer(chatId);
        } else {
            QuestEntity task = new QuestEntity();
            task.setIsExpand(false);
            task.setChatId(chatId);
            task.setIsProject(true);
            task.setTitle(sendMsg.getValue());
            task.setAbsoluteFullPath("");
            task.setParentId(curProject.get());
            task.setAbsoluteFullPath("");
            getJdbi().useHandle(handle ->
                    handle.createUpdate("INSERT INTO quest_list (parent_id, chat_id, title, absolute_full_path, is_expand, is_project) VALUES (:parentId, :chatId, :title, :absoluteFullPath, :isExpand, :isProject)").
                            bind("parentId", task.getParentId()).
                            bind("chatId", task.getChatId()).
                            bind("title", task.getTitle()).
                            bind("absoluteFullPath", task.getAbsoluteFullPath()).
                            bind("isExpand", task.getIsExpand() ? 1 : 0).
                            bind("isProject", task.getIsProject() ? 1 : 0).
                            execute());

            getJdbi().useHandle(handle ->
                    handle.createUpdate("INSERT INTO chat_detail (chat_id, type, content) VALUES (:chatId, :type, :content)").
                            bind("chatId", userSendChat.getChatId()).
                            bind("type", userSendChat.getType()).
                            bind("content", userSendChat.getContent()).
                            execute());
        }
        isExecuteTask.set(false);
        sendMsg.set("");
        chatModel.setValue(true);
    }

    private static void robotAnswer(int chatId) {
        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setChatId(0);
        chatDetail.setType(ChatEnum.ROBOT.name());
        chatDetail.setContent("请先选择您的项目");
        chatList.getValue().add(chatDetail);
    }

    private static ChatDetail getUserSendChat(int chatId) {
        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setChatId(chatId);
        chatDetail.setType(ChatEnum.USER.name());
        chatDetail.setContent(sendMsg.get());
        return chatDetail;
    }

    private static int getNextChatId() {
        Integer maxId = getJdbi().withHandle(handle -> handle.createQuery("SELECT MAX(id) FROM chat").mapTo(Integer.class).findOne().orElse(0));
        return maxId + 1;
    }

    public static void refreshChatDataByChatId(Integer chatId) {
        curChatId.setValue(chatId);
        List<ChatDetail> detailList = getJdbi().withHandle(handle -> handle.createQuery("SELECT * FROM chat_detail WHERE chat_id = :chatId AND deleted_at IS NULL ORDER BY create_at ASC").bind("chat_id", chatId).mapToBean(ChatDetail.class).list());
        chatList.setAll(detailList);
    }
}
