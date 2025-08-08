package com.example.common.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * Banner实体类
 */
public class Banner {
    private BigInteger id;
    private String image;  // 图片URL
    private String link;   // 跳转链接
    private String title;  // 标题
    private Integer sort;  // 排序
    private Integer status; // 状态：1-启用，0-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Banner() {}

    public Banner(BigInteger id, String image, String link, String title, Integer sort, Integer status) {
        this.id = id;
        this.image = image;
        this.link = link;
        this.title = title;
        this.sort = sort;
        this.status = status;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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