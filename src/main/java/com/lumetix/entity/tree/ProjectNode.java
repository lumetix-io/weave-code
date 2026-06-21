package com.lumetix.entity.tree;

import java.io.Serializable;

public class ProjectNode implements Serializable {
    // private Long projectId;

    private Boolean isExpand;

    private String absoluteFullPath;


    public Boolean getIsExpand() {
        return isExpand;
    }

    public void setIsExpand(Boolean expand) {
        isExpand = expand;
    }

    public String getAbsoluteFullPath() {
        return absoluteFullPath;
    }

    public void setAbsoluteFullPath(String absoluteFullPath) {
        this.absoluteFullPath = absoluteFullPath;
    }
}
