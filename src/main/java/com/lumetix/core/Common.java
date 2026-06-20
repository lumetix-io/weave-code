package com.lumetix.core;

import com.lumetix.ApplicationLauncher;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class Common {

    public static File getSelectFolderPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件夹");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return directoryChooser.showDialog(ApplicationLauncher.primaryStage);
    }
}
