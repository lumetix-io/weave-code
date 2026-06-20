package com.lumetix.entity;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

public interface BasicConstants {


    class MainUi {
        public static final String TITLE = "Code Assistant";
        public static final String VERSION = "V 1.0.0";
        public static final double WIDTH = 1200;
        public static final double HEIGHT = 800;
    }

    class InPutUi {
        //UI数据
        public static final double CONTENT_WIDTH = 600;
        public static final double TEXTAREA_WIDTH = CONTENT_WIDTH;
        public static final double TEXTAREA_HEIGHT = 120;
        //数据绑定
        public static SimpleBooleanProperty isExecuteTask = new SimpleBooleanProperty(false);
        public static SimpleStringProperty curProjectTitle = new SimpleStringProperty("从选择项目开始吧,do it");
        public static SimpleLongProperty curProject = new SimpleLongProperty(0);
        public static SimpleIntegerProperty treeListFresh = new SimpleIntegerProperty(0);
        public static SimpleStringProperty sendMsg = new SimpleStringProperty("");
        public static SimpleLongProperty curTaskId = new SimpleLongProperty(0);
    }

    class ChatUi {
        public static final SimpleListProperty<ChatDetail> chatList = new SimpleListProperty<>(FXCollections.observableArrayList());
        // public static final SimpleLongProperty curChatId = new SimpleLongProperty(0L);
        public static final SimpleBooleanProperty chatModel = new SimpleBooleanProperty(false);
    }

}
