package com.lumetix.core;

import com.lumetix.ai.assistant.factory.UserFaceChatAssistant;
import com.lumetix.entity.chat.ChatDetail;
import com.lumetix.entity.chat.ChatEnum;
import com.lumetix.entity.model.ModelEnum;
import com.lumetix.entity.tree.QuestEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.lumetix.ai.assistant.factory.AiAssistantFactory.newChatAssistant;
import static com.lumetix.core.DbManager.getJdbi;
import static com.lumetix.entity.BasicConstants.ChatUi.*;
import static com.lumetix.entity.BasicConstants.InPutUi.*;

public class ChatManager {


    private final static Long ZERO_ID = 0L;

    public static void send() {
        String msg = sendMsg.get();
        if (Objects.isNull(msg) || msg.isEmpty() || "".equals(msg.trim())) {
            return;
        }
        if (isExecuteTask.get()) {
            isExecuteTask.set(false);
            return;
        }
        isExecuteTask.set(true);
        long projectId = curProject.get();
        //获取最新的聊天记录chatId
        Long taskId = curTaskId.get();

        //如果用户没有选择项目
        ChatDetail robotChat = null;
        ChatDetail userSendChat = getUserSendChat(taskId);
        if (projectId == 0) {
            robotChat = robotAnswer(taskId);
            isExecuteTask.set(false);
        } else {
            if (ZERO_ID.equals(taskId)) {
                QuestEntity task = new QuestEntity();

                task.setType("TASK");
                task.setTitle(sendMsg.getValue());
                task.setParentId(curProject.get());
                taskId = getJdbi().withHandle(handle ->
                        handle.createUpdate("INSERT INTO quest_list (parent_id, title, type) " +
                                        "VALUES (:parentId, :title, :type)").
                                bind("parentId", task.getParentId()).
                                bind("title", task.getTitle()).
                                bind("type", task.getType()).
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
        snedMsg2Model();
        sendMsg.set("");
        curTaskId.set(taskId);
        chatModel.setValue(true);
        treeListFresh.setValue(treeListFresh.getValue() + 1);
    }

    private static void snedMsg2Model() {
        String uuid = chatSendUUid.get();
        String msg = sendMsg.get();
        UserFaceChatAssistant userFaceChatAssistant = newChatAssistant(ModelEnum.QIANWEN37MAX);
        userFaceChatAssistant.chat(msg, LocalDate.now()).onPartialResponse(response -> {
            ChatDetail last = chatList.getLast();
            String contentUuid = last.getUuid();
            if (uuid.equals(contentUuid)) {
                last.setContent(last.getContent() + response);
                chatViewRefresh.set(chatViewRefresh.getValue() + 1);
            } else {
                ChatDetail robotChat = getRobotChat(response);
                chatList.addLast(robotChat);
            }
        }).onCompleteResponse(response -> {
            robotAnswer(response.aiMessage().text());
        }).onError(Throwable::printStackTrace).start();
    }

    private static ChatDetail getRobotChat(String content) {
        String uuid = chatSendUUid.get();
        Long chatId = curTaskId.get();
        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setChatId(chatId);
        chatDetail.setUuid(uuid);
        chatDetail.setType(ChatEnum.ROBOT.name());
        chatDetail.setContent(content);
        return chatDetail;
    }

    private static ChatDetail robotAnswer(String content) {
        ChatDetail chatDetail = getRobotChat(content);
        getJdbi().useHandle(handle ->
                handle.createUpdate("""
                                INSERT INTO chat_detail (chat_id, type, content, uuid)
                                VALUES (:chatId, :type, :content, :uuid)
                                ON CONFLICT(chat_id, type) WHERE type = 'ROBOT' AND deleted_at IS NULL
                                DO UPDATE SET content = :content, uuid = :uuid, update_at = datetime('now', 'localtime')
                                """)
                        .bind("chatId", chatDetail.getChatId())
                        .bind("type", ChatEnum.ROBOT.name())
                        .bind("content", chatDetail.getContent())
                        .bind("uuid", chatDetail.getUuid())
                        .execute());
        return chatDetail;
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


    public static void refreshChatDataByChatId(Long chatId) {
        List<ChatDetail> detailList = getJdbi().withHandle(handle ->
                handle.createQuery("SELECT * FROM chat_detail WHERE chat_id = :chatId AND deleted_at IS NULL ORDER BY create_at ASC").
                        bind("chatId", chatId).
                        mapToBean(ChatDetail.class).list());
        chatList.clear();
        chatList.setAll(detailList);
    }


}
