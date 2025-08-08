package com.example.common.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Event活动实体类
 */
public class Event {
    private BigInteger id;
    private String title;  // 活动标题
    private String image;  // 活动图片URL
    private String content; // 活动内容
    private String link;   // 跳转链接
    private Integer sort;  // 排序
    private Integer status; // 状态：1-启用，0-禁用
    private LocalDateTime startTime; // 活动开始时间
    private LocalDateTime endTime;   // 活动结束时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Event() {}

    public Event(BigInteger id, String title, String image, String content, String link,
                Integer sort, Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.content = content;
        this.link = link;
        this.sort = sort;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}