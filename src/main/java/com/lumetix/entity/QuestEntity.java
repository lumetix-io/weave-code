package com.lumetix.entity;

import java.io.Serializable;

public class QuestEntity implements Serializable {
    private Long Id;
    /**
     * parentId是0的时候，是根节点
     */
    private Long parentId;

    private String title;

    private String absoluteFullPath;

    private Boolean isExpand;

    private Boolean isProject;

    private Long chatId;

    private String createAt;

    private String updateAt;

    private String deletedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAbsoluteFullPath() {
        return absoluteFullPath;
    }

    public void setAbsoluteFullPath(String absoluteFullPath) {
        this.absoluteFullPath = absoluteFullPath;
    }

    public Boolean getIsExpand() {
        return isExpand;
    }

    public void setIsExpand(Boolean expand) {
        isExpand = expand;
    }


    public Boolean getIsProject() {
        return isProject;
    }

    public void setIsProject(Boolean project) {
        isProject = project;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
