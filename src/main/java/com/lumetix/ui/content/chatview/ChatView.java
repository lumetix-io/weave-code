package com.lumetix.ui.content.chatview;

import com.lumetix.entity.chat.ChatDetail;
import com.lumetix.entity.chat.ChatEnum;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

//    public static BorderPane newChatViewWrapper() {
//        StackPane input = newInputViewWithBorder();
//        BorderPane chatView = new BorderPane();
//        chatView.setBottom(input);
//        chatView.setCenter(newChatView());
//        chatView.setMaxWidth(CHAT_VIEW_WIDTH);
//
//        chatView.setPadding(new Insets(10, 0, 10, 0));
//        return chatView;
//    }


    public static StackPane newChatViewWrapper() {

        StackPane rootContent = new StackPane();

        StackPane chatList = new StackPane();
        chatList.setAlignment(Pos.TOP_CENTER);
        chatList.getChildren().add(newChatView());
        rootContent.getChildren().add(chatList);


        StackPane inputView = new StackPane();
        inputView.setAlignment(Pos.BOTTOM_CENTER);
        inputView.setBackground(Background.fill(Color.TRANSPARENT));
        inputView.setMouseTransparent(true);
        StackPane input = newInputViewWithBorder();
        input.setMaxWidth(TEXTAREA_WIDTH);
        input.setMaxHeight(TEXTAREA_HEIGHT);
        input.setPickOnBounds(false);
        inputView.getChildren().add(input);

        rootContent.getChildren().add(inputView);

        return rootContent;
    }


    public static ScrollPane newChatView() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.TOP_CENTER);
//        stackPane.setBackground(Background.fill(Color.GREEN));
//        scrollPane.setBackground(Background.fill(Color.BLUE));
        // scrollPane.setMaxWidth(CHAT_VIEW_WIDTH);
        VBox vBox = new VBox();
        vBox.setMaxWidth(CHAT_VIEW_WIDTH);
        stackPane.getChildren().add(vBox);
        vBox.setBackground(Background.fill(Color.TRANSPARENT));
        chatList.addListener((_, _, newValue) -> {
            if (newValue.isEmpty()) {
                vBox.getChildren().clear();
            }
        });
        chatList.addListener((ListChangeListener<ChatDetail>) changelist -> {
            for (ChatDetail detail : changelist.getList()) {
                String type = detail.getType();
                if (type.equals(ChatEnum.USER.name())) {
                    TextArea textArea = new TextArea();
                    textArea.setText(detail.getContent());
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    textArea.setBorder(Border.EMPTY);
                    textArea.setStyle(
                            "-fx-background-color: #3498db;" +
                                    "-fx-background-radius: 16;" +
                                    "-fx-padding: 10 14 10 14;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 14px;"
                    );
                    double textW = computeTextWidth(detail.getContent());
                    double bubbleW = Math.min(textW + 32, CHAT_VIEW_WIDTH * 0.75);
                    double textH = computeTextHeight(detail.getContent(), bubbleW - 28);
                    textArea.setPrefWidth(bubbleW);
                    textArea.setMaxWidth(bubbleW);
                    textArea.setPrefHeight(textH + 20);
                    textArea.setMaxHeight(textH + 20);
                    HBox bubbleBox = new HBox(textArea);
                    bubbleBox.setAlignment(Pos.CENTER_RIGHT);
                    vBox.getChildren().add(bubbleBox);
                } else {
                    Node document = PARSER.parse(detail.getContent());

                    // 5. 将 AST 渲染为 HTML 字符串
                    String html = RENDERER.render(document);

                    WebView webView = new WebView();
                    webView.setPrefWidth(CHAT_VIEW_WIDTH);
                    webView.setMaxWidth(CHAT_VIEW_WIDTH);
                    webView.setMinWidth(CHAT_VIEW_WIDTH);
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
                    vBox.getChildren().add(webView);
                }
            }
        });
        scrollPane.setContent(stackPane);
        return scrollPane;
    }

    private static double computeTextWidth(String text) {
        Text helper = new Text();
        helper.setFont(Font.font(14));
        double maxW = 0;
        for (String line : text.split("\n")) {
            helper.setText(line);
            maxW = Math.max(maxW, helper.getLayoutBounds().getWidth());
        }
        return maxW;
    }

    private static double computeTextHeight(String text, double wrapWidth) {
        Text helper = new Text();
        helper.setFont(Font.font(14));
        helper.setWrappingWidth(wrapWidth);
        helper.setText(text);
        return helper.getLayoutBounds().getHeight();
    }
}