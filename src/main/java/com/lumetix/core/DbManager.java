package com.lumetix.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.io.File;

public class DbManager {
    private static volatile Jdbi jdbi;


    public static synchronized void init() {
        if (jdbi != null) {
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + ensureDbFileExists());
        config.setMaximumPoolSize(1);           // SQLite 单写限制
        config.setConnectionTestQuery("SELECT 1");

        jdbi = Jdbi.create(new HikariDataSource(config));

        jdbi.useHandle(handle -> {
            handle.execute("""
                    CREATE TABLE IF NOT EXISTS quest_list (
                        id                 INTEGER PRIMARY KEY AUTOINCREMENT,
                        parent_id          INTEGER NOT NULL DEFAULT 0,
                        chat_id            INTEGER NOT NULL DEFAULT 0,
                        title              TEXT,
                        absolute_full_path TEXT,
                        is_expand          INTEGER NOT NULL DEFAULT 0,
                        is_project         INTEGER NOT NULL DEFAULT 0,
                        create_at          TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                        update_at          TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                        deleted_at         TEXT
                    );
                    """);

            handle.execute("""
                    CREATE TABLE IF NOT EXISTS chat (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id     INTEGER,
                        model       TEXT,
                        version     TEXT,
                        create_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                        update_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                        delete_time TEXT
                    );
                    """);

            handle.execute("""
                    CREATE TABLE IF NOT EXISTS chat_detail (
                        id         INTEGER PRIMARY KEY AUTOINCREMENT,
                        chat_id    INTEGER,
                        type       TEXT,
                        content    TEXT,
                        create_at  TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                        update_at  TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                        deleted_at TEXT
                    );
                    """);
        });
    }

    public static Jdbi getJdbi() {
        if (jdbi == null) {
            init();
        }
        return jdbi;
    }


    public static String ensureDbFileExists() {
        File dbFile = new File(System.getProperty("user.home"), "lumetix/lumetix.db");
        File parentDir = dbFile.getParentFile();

        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new RuntimeException("无法创建数据库父目录: " + parentDir.getAbsolutePath());
        }
        if (!dbFile.exists()) {
            try {
                boolean newFile = dbFile.createNewFile();
                if (!newFile) {
                    throw new RuntimeException("创建数据库文件失败: " + dbFile.getAbsolutePath());
                }
            } catch (Exception e) {
                throw new RuntimeException("创建数据库文件错误: " + dbFile.getAbsolutePath(), e);
            }
        }
        return dbFile.getAbsolutePath();
    }
}
