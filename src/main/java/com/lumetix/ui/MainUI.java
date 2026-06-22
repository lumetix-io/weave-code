package com.lumetix.ui;

import com.lumetix.ui.leftmenu.LeftMenuUI;
import javafx.geometry.Insets;
import javafx.scene.layout.*;

import java.util.Objects;

import static com.lumetix.entity.BasicConstants.ChatUi.chatModel;
import static com.lumetix.ui.content.chatview.ChatView.newChatViewWrapper;
import static com.lumetix.ui.content.createquestview.CreateQuestView.newCreateInputViewWrapper;
import static com.lumetix.ui.leftmenu.LeftMenuUI.newCreateQuestButton;
import static com.lumetix.ui.leftmenu.LeftMenuUI.newSelectModelButton;

public class MainUI {
    public static BorderPane newMainUI() {
        BorderPane root = new BorderPane();
        root.setLeft(newLeftMenu());
        root.setCenter(newRightContent());
        return root;
    }

    public static VBox newLeftMenu() {
        VBox leftMenu = new VBox();
        leftMenu.getChildren().add(newCreateQuestButton());
        leftMenu.getChildren().add(LeftMenuUI.newQuestTreeView());

        Region region = new BorderPane();
        VBox.setVgrow(region, Priority.ALWAYS);
        leftMenu.getChildren().add(region);

        leftMenu.getChildren().add(newSelectModelButton());
        leftMenu.setStyle("-fx-background-color: #F5F5F5;");
        leftMenu.setPadding(new Insets(10, 10, 10, 0));
        leftMenu.setSpacing(10);
        return leftMenu;
    }

    public static StackPane newRightContent() {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(newCreateInputViewWrapper());
        chatModel.addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(oldValue, newValue)) {
                return;
            }
            stackPane.getChildren().clear();
            if (Boolean.TRUE.equals(newValue)) {
                stackPane.getChildren().add(newChatViewWrapper());
            } else {
                stackPane.getChildren().add(newCreateInputViewWrapper());
            }
        });
        return stackPane;
    }


}
