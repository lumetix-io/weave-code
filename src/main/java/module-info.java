module com.lumetix.weave_code {
    requires langchain4j;
    requires langchain4j.community.dashscope;
    requires javafx.graphics;
    requires javafx.controls;
    requires atlantafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires com.zaxxer.hikari;
    requires org.jdbi.v3.core;
    requires java.desktop;
    requires langchain4j.core;
    requires javafx.web;

    // 导出包给 JavaFX 使用
    exports com.lumetix to javafx.graphics;
    exports com.lumetix.ai to javafx.graphics;
    opens com.lumetix.entity to org.jdbi.v3.core;
    opens com.lumetix.entity.tree to org.jdbi.v3.core;
    opens com.lumetix.entity.chat to org.jdbi.v3.core;
    opens com.lumetix.entity.model to org.jdbi.v3.core;
    exports com.lumetix.ai.assistant.factory to javafx.graphics;
    exports com.lumetix.ai.assistant.embed to javafx.graphics;
}