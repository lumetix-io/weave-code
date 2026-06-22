package com.lumetix.ui.content.basicInputview;

import atlantafx.base.theme.Styles;
import com.lumetix.core.ChatManager;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static com.lumetix.core.ProjectManager.createProject;
import static com.lumetix.entity.BasicConstants.InPutUi.*;
import static com.lumetix.ui.basicui.BasicButton.newNoBorderButton;


public class InputView {

    public static StackPane newInputViewWithBorder() {
        StackPane input = new StackPane();
        input.setStyle(
                "-fx-border-color: #d0d0d0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-background-color: white;"
        );
        input.setPadding(new Insets(5, 5, 5, 5));
        input.getChildren().add(newInputView());
        return input;
    }


    public static BorderPane newInputView() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(newInputTextArea());
        borderPane.setBottom(newInputModelHBox());
        borderPane.setMaxWidth(TEXTAREA_WIDTH);
        borderPane.setMaxHeight(TEXTAREA_HEIGHT);

        return borderPane;
    }


    private static TextArea newInputTextArea() {
        TextArea textArea = new TextArea();
        textArea.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
        textArea.textProperty().bindBidirectional(sendMsg);
        textArea.setPromptText("开始描述你的计划");
        return textArea;
    }

    private static HBox newInputModelHBox() {
        HBox hBox = new HBox();
        hBox.getChildren().add(newCreateProjectButton());

//        Separator separator = new Separator(Orientation.VERTICAL);
//        separator.setMaxHeight(5);
//        separator.getStyleClass().add(Styles.SMALL);
//        hBox.getChildren().add(separator);

//        hBox.getChildren().add(newSelectModelButton());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hBox.getChildren().add(spacer);

        hBox.getChildren().add(newInPutButton());
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }


    public static Button newCreateProjectButton() {
        Button modelSelect = newNoBorderButton();
        modelSelect.textProperty().bindBidirectional(curProjectTitle);
        modelSelect.getStyleClass().add(Styles.SMALL);
        modelSelect.setGraphic(new FontIcon(Feather.FOLDER_PLUS));
        modelSelect.setOnAction(event -> createProject());
        return modelSelect;
    }

    private static Button newInPutButton() {
        Button send = newNoBorderButton();
        send.graphicProperty().bind(Bindings.createObjectBinding(() -> {
            Feather icon = isExecuteTask.get() ? Feather.STOP_CIRCLE : Feather.SEND;
            return FontIcon.of(icon);
        }, isExecuteTask));
        send.setOnAction(_ -> ChatManager.send());
        return send; // ← 原代码还缺少 return 语句
    }
}

