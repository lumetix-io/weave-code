package com.lumetix.ui.content.chatview;

import com.lumetix.entity.chat.ChatDetail;
import com.lumetix.entity.chat.ChatEnum;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.web.WebView;

import java.util.Objects;

public class ChatCell extends ListCell<ChatDetail> {

    @Override
    protected void updateItem(ChatDetail item, boolean empty) {
        super.updateItem(item, empty);
        if (Objects.isNull(item) || empty) {
            setText(null);
            setGraphic(null);
            return;
        }
        String type = item.getType();
        if (type.equals(ChatEnum.USER.name())) {
            Label chatLabel = new Label(item.getContent());
            chatLabel.setAlignment(Pos.CENTER);
            setGraphic(chatLabel);
        } else {
            WebView webView = new WebView();
            webView.getEngine().loadContent(item.getContent());
            setGraphic(webView);
            setAlignment(Pos.CENTER_LEFT);
        }

    }
}
