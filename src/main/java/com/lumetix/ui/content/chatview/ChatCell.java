package com.lumetix.ui.content.chatview;

import com.lumetix.entity.chat.ChatDetail;
import com.lumetix.entity.chat.ChatEnum;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.web.WebView;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChatCell extends ListCell<ChatDetail> {

    private static final Parser PARSER;
    private static final HtmlRenderer RENDERER;

    static {
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create()
        );
        PARSER = Parser.builder().extensions(extensions).build();
        RENDERER = HtmlRenderer.builder().extensions(extensions).build();
    }


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
            //  chatLabel.setBackground(Background.fill(Color.RED));
            //   chatLabel.setAlignment(Pos.CENTER_RIGHT);
            setAlignment(Pos.CENTER_RIGHT);
            setGraphic(chatLabel);
        } else {
            Node document = PARSER.parse(item.getContent());

            // 5. 将 AST 渲染为 HTML 字符串
            String html = RENDERER.render(document);

            WebView webView = new WebView();
            webView.setPrefWidth(this.getWidth());
            webView.getEngine().loadContent(html);
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, o, n) -> {
                if (n == Worker.State.SUCCEEDED)
                    webView.setPrefHeight(((Number) webView.getEngine()
                            .executeScript("document.body.scrollHeight")).doubleValue() + 10);
            });
            setGraphic(webView);
            setAlignment(Pos.CENTER_LEFT);
        }

    }
}
