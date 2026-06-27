package com.lumetix.ui.content.chatview;

import com.lumetix.entity.chat.ChatDetail;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

import static com.lumetix.entity.BasicConstants.ChatUi.chatList;
import static com.lumetix.entity.BasicConstants.ChatUi.chatViewRefresh;
import static com.lumetix.entity.BasicConstants.InPutUi.TEXTAREA_WIDTH;
import static com.lumetix.ui.content.basicInputview.InputView.newInputViewWithBorder;

public class ChatView {

    private static final double CHAT_VIEW_WIDTH = TEXTAREA_WIDTH;


    public static BorderPane newChatViewWrapper() {
        StackPane input = newInputViewWithBorder();
        BorderPane chatView = new BorderPane();
        chatView.setBottom(input);
        chatView.setCenter(newChatView());
        chatView.setMaxWidth(CHAT_VIEW_WIDTH);

        chatView.setPadding(new Insets(10, 0, 10, 0));
        return chatView;
    }

    public static ListView<ChatDetail> newChatView() {
        ListView<ChatDetail> listView = new ListView<>();
        listView.itemsProperty().bindBidirectional(chatList);
        listView.setCellFactory(param -> new ChatCell());

        listView.setBorder(Border.EMPTY);
        listView.getStylesheets().add("data:text/css," +
                ".scroll-bar:vertical { -fx-opacity: 0; -fx-pref-width: 0; }"
        );
        chatList.addListener((_, _, newValue) ->
        {
            if (newValue.isEmpty()) {
                return;
            }
            ChatDetail last = newValue.getLast();
            if (Objects.isNull(last)) {
                return;
            }
            listView.scrollTo(last);
        });
        chatViewRefresh.addListener((observable, oldValue, newValue) -> listView.refresh());
        return listView;
    }
}
