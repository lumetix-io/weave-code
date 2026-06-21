package com.lumetix.ui.leftmenu;

import atlantafx.base.theme.Styles;
import com.lumetix.entity.tree.QuestEntity;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.lumetix.core.ProjectManager.getQuestTreeData;
import static com.lumetix.entity.BasicConstants.ChatUi.chatList;
import static com.lumetix.entity.BasicConstants.ChatUi.chatModel;
import static com.lumetix.entity.BasicConstants.InPutUi.treeListFresh;

public class LeftMenuUI {

    public static Button newCreateQuestButton() {

        Button createQuestButton = new Button("创建Quest", new FontIcon(Feather.PLUS));
        createQuestButton.getStyleClass().addAll(
                Styles.FLAT,
                Styles.ACCENT
        );
        createQuestButton.setMaxWidth(Double.MAX_VALUE);
        createQuestButton.setAlignment(Pos.CENTER_LEFT);
        createQuestButton.setCursor(Cursor.HAND);
        createQuestButton.setOnAction(_ -> {
            chatModel.setValue(false);
            chatList.clear();
        });
        return createQuestButton;
    }


    public static TreeView<QuestEntity> newQuestTreeView() {
        TreeView<QuestEntity> treeView = new TreeView<>();
        treeView.setShowRoot(false);
        treeView.getStyleClass().addAll(Styles.SMALL, Styles.ACCENT);
        treeView.setCellFactory(_ -> new QuestCell());
        treeView.setStyle(
                "-fx-border-color: transparent; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent; " +
                        "-fx-background-insets: 0; " +
                        "-fx-padding: 0; " +
                        "-fx-background-color: transparent;"
        );
        refreshTreeView(treeView);
        treeListFresh.addListener((_, _, _) -> refreshTreeView(treeView));
        return treeView;
    }

    private static void refreshTreeView(TreeView<QuestEntity> treeView) {
        autoFitTreeWidth(treeView);
        List<QuestEntity> dataList = getQuestTreeData();
        if (dataList.isEmpty()) {
            return;
        }

        // 查找根节点 (parentId为0或null)
        Optional<QuestEntity> rootEntity = dataList.stream()
                .filter(entity -> entity.getParentId() == null || entity.getParentId() == 0)
                .findAny();

        if (rootEntity.isPresent()) {
            TreeItem<QuestEntity> root = new TreeItem<>(rootEntity.get());
            // 递归构建子节点
            buildChildren(root, dataList);
            treeView.setRoot(root);
        }
    }

    private static <T> void autoFitTreeWidth(TreeView<T> treeView) {
        Runnable fit = () -> Platform.runLater(() -> {
            treeView.applyCss();
            treeView.layout();
            double maxW = treeView.lookupAll(".tree-cell").stream()
                    .mapToDouble(n -> n.prefWidth(-1))
                    .max().orElse(0);
            if (maxW > 0) treeView.setPrefWidth(maxW + 30);
        });

        // 用事件过滤器统一拦截所有节点的展开/折叠，无需递归绑定
//        treeView.addEventFilter(TreeItem.branchExpandedEvent(), e -> fit.run());
//        treeView.addEventFilter(TreeItem.branchCollapsedEvent(), e -> fit.run());
        fit.run(); // 初始计算
    }


    private static void buildChildren(TreeItem<QuestEntity> parent, List<QuestEntity> allEntities) {
        if (Objects.isNull(parent)) {
            return;
        }
        QuestEntity parentEntity = parent.getValue();
        for (QuestEntity entity : allEntities) {
            if (parentEntity.getId() != null && parentEntity.getId().equals(entity.getParentId())) {
                TreeItem<QuestEntity> childNode = new TreeItem<>(entity);
                parent.getChildren().add(childNode);
//                if (Boolean.TRUE.equals(entity.getIsExpand())) {
//                    childNode.setExpanded(true);
//                }
                buildChildren(childNode, allEntities);
            }
        }
    }

}
