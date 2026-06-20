package com.lumetix.ui.content.basicInputview;

import com.lumetix.entity.ModelEnum;
import javafx.scene.Cursor;
import javafx.scene.control.ListCell;

import java.util.Objects;

public class InPutModelView extends ListCell<ModelEnum> {


    @Override
    protected void updateItem(ModelEnum item, boolean empty) {
        super.updateItem(item, empty);
        setCursor(Cursor.HAND);
        if (Objects.isNull(item) || empty) {
            setText(null);
            setGraphic(null);
            setCursor(Cursor.DEFAULT);
            return;
        }
        setText(item.getDesc());
        setCursor(Cursor.HAND);
    }
}
