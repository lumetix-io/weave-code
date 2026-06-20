package com.lumetix.ui.content.createquestview;

import atlantafx.base.theme.Styles;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static com.lumetix.core.ProjectManager.createProject;
import static com.lumetix.entity.BasicConstants.InPutUi.*;
import static com.lumetix.ui.basicui.BasicButton.newNoBorderButton;
import static com.lumetix.ui.content.basicInputview.InputView.newInputViewWithBorder;


public class CreateQuestView {


    public static BorderPane newCreateInputViewWrapper() {
        StackPane input = newInputViewWithBorder();
        BorderPane wrapperBorder = new BorderPane();
        wrapperBorder.setTop(createProjectView());
        wrapperBorder.setCenter(input);
        wrapperBorder.setMaxWidth(TEXTAREA_WIDTH);
        wrapperBorder.setMaxHeight(TEXTAREA_HEIGHT);
        return wrapperBorder;
    }


    private static HBox createProjectView() {

        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.getChildren().add(selectProjectPath());
        hBox.getChildren().add(createQuestTitle());
        hBox.setCursor(Cursor.HAND);
        hBox.setOnMouseClicked(event -> {
            int clickCount = event.getClickCount();
            if (clickCount == 1) {
                createProject();
            }
        });
        return hBox;
    }


    private static Label createQuestTitle() {
        Label title = new Label("");
        title.textProperty().bindBidirectional(curProjectTitle);
        title.setAlignment(Pos.CENTER_LEFT);
        return title;
    }

    private static Button selectProjectPath() {
        Button selectProjectPath = newNoBorderButton();
        selectProjectPath.setGraphic(new FontIcon(Feather.FOLDER_PLUS));
        selectProjectPath.getStyleClass().add(Styles.SMALL);
        return selectProjectPath;
    }
}
