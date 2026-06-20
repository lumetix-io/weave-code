package com.lumetix.ui.leftmenu;

import com.lumetix.entity.QuestEntity;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static com.lumetix.core.ProjectManager.itemClick;


public class QuestCell extends TreeCell<QuestEntity> {

    public QuestCell() {
        this.setStyle("-fx-background-color: transparent; -fx-indent: 0;");
    }

    @Override
    protected void updateItem(QuestEntity item, boolean empty) {
        super.updateItem(item, empty);
        setCursor(Cursor.HAND);
        Long PARENT_ID = 0L;
        if (item == null || empty || PARENT_ID.equals(item.getParentId())) {
            setText(null);
            setGraphic(null);
            return;
        }

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(5);
        if (Boolean.TRUE.equals(item.getIsProject())) {
            hBox.getChildren().add(new FontIcon(Feather.FOLDER));
        } else {
            hBox.getChildren().add(new Circle(2.5, Color.GRAY));
        }
        Label title = new Label(item.getTitle());
        title.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(title);
        this.setGraphic(hBox);
        this.setOnMouseClicked(event -> {
            int clickCount = event.getClickCount();
            if (clickCount == 1) {
                itemClick(item);
            }
        });
    }
}
