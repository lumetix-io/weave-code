package com.lumetix.ui.content.chatview;

import com.lumetix.entity.chat.ChatDetail;
import com.lumetix.entity.chat.ChatEnum;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

import static com.lumetix.entity.BasicConstants.ChatUi.chatList;
import static com.lumetix.entity.BasicConstants.InPutUi.TEXTAREA_HEIGHT;
import static com.lumetix.entity.BasicConstants.InPutUi.TEXTAREA_WIDTH;
import static com.lumetix.ui.content.basicInputview.InputView.newInputViewWithBorder;

public class ChatView {


    private static final Parser PARSER;
    private static final HtmlRenderer RENDERER;
    private static final double CHAT_VIEW_WIDTH = TEXTAREA_WIDTH;

    static {
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create()
        );
        PARSER = Parser.builder().extensions(extensions).build();
        RENDERER = HtmlRenderer.builder().extensions(extensions).build();
    }

    public static StackPane newChatViewWrapper() {

        StackPane rootContent = new StackPane();

        StackPane chatList = new StackPane();
        chatList.setAlignment(Pos.TOP_CENTER);
        chatList.getChildren().add(newChatView());
        rootContent.getChildren().add(chatList);


        StackPane inputView = new StackPane();
        inputView.setAlignment(Pos.BOTTOM_CENTER);
        inputView.setPickOnBounds(false);
        StackPane input = newInputViewWithBorder();
        input.setMaxWidth(TEXTAREA_WIDTH);
        input.setMaxHeight(TEXTAREA_HEIGHT);
        inputView.getChildren().add(input);

        rootContent.getChildren().add(inputView);

        return rootContent;
    }


    public static ScrollPane newChatView() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.TOP_CENTER);
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 0, TEXTAREA_HEIGHT, 0));
        vBox.setMaxWidth(CHAT_VIEW_WIDTH);
        stackPane.getChildren().add(vBox);
        vBox.setBackground(Background.fill(Color.TRANSPARENT));
        chatList.addListener((_, _, newValue) -> {
            if (newValue.isEmpty()) {
                vBox.getChildren().clear();
            }
            scrollPane.setVvalue(1.0);
        });
        chatList.addListener((ListChangeListener<ChatDetail>) changelist -> {
            while (changelist.next()) {
                if (changelist.wasRemoved()) {
                    int size = vBox.getChildren().size();
                    if (size > 0) {
                        vBox.getChildren().removeLast();
                    }
                }
                if (changelist.wasAdded()) {
                    List<? extends ChatDetail> addedSubList = changelist.getAddedSubList();
                    if (addedSubList.isEmpty()) {
                        return;
                    }
                    for (ChatDetail detail : addedSubList) {

                        String type = detail.getType();
                        if (type.equals(ChatEnum.USER.name())) {

                            HBox bubbleBox = getHBox(detail);
                            vBox.getChildren().add(bubbleBox);
                        } else {
                            WebView webView = newRobotWebView(detail);
                            vBox.getChildren().add(webView);
                        }
                    }
                }
            }
        });
        scrollPane.setContent(stackPane);
        return scrollPane;
    }

    public static WebView newRobotWebView(ChatDetail detail) {
        Node document = PARSER.parse(detail.getContent());

        // 5. 将 AST 渲染为 HTML 字符串
        String html = RENDERER.render(document);

        WebView webView = new WebView();
        webView.setPrefWidth(CHAT_VIEW_WIDTH);
        webView.setMaxWidth(CHAT_VIEW_WIDTH);
        webView.setMinWidth(CHAT_VIEW_WIDTH);
        webView.setPrefHeight(0);
        webView.setMinHeight(0);
        webView.setMaxHeight(0);
        String fullHtml = "<html><head><style>"
                + "body { margin: 0; padding: 8px; font-size: 14px; overflow: hidden; }"
                + "img { max-width: 100%; height: auto; }"
                + "pre { overflow-x: auto; white-space: pre-wrap; word-wrap: break-word; }"
                + "table { max-width: 100%; word-break: break-all; }"
                + "</style></head><body><div id='content'>" + html + "</div></body></html>";
        webView.getEngine().loadContent(fullHtml);
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, o, n) -> {
            if (n == Worker.State.SUCCEEDED) {
                Platform.runLater(() -> Platform.runLater(() -> {
                    Object result = webView.getEngine()
                            .executeScript("document.getElementById('content').offsetHeight");
                    if (result instanceof Number) {
                        double h = ((Number) result).doubleValue() + 16;
                        webView.setPrefHeight(h);
                        webView.setMinHeight(h);
                        webView.setMaxHeight(h);
                    }
                }));
            }
        });
        return webView;
    }

    private static HBox getHBox(ChatDetail detail) {
        Label label = new Label(detail.getContent());
        label.setWrapText(true);
        label.setMaxWidth(TEXTAREA_WIDTH * 0.75);
        label.setStyle(
                "-fx-background-color: #F5F5F5;" +
                        "-fx-background-radius: 16;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-text-fill: #333333;" +
                        "-fx-font-size: 14px;"
        );
        HBox bubbleBox = new HBox(label);
        bubbleBox.setAlignment(Pos.CENTER_RIGHT);
        return bubbleBox;
    }
}