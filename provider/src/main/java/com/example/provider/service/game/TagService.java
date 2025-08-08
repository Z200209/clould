package com.example.provider.service.game;


import com.example.common.annotations.DataSource;
import com.example.common.config.mysql.DataSourceType;
import com.example.common.entity.Tag;

import com.example.provider.mapper.game.TagMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class TagService {

    @Resource
    private TagMapper mapper;

    @Autowired
    private GameTagRelationService gameTagRelationService;

    /**
     * 根据ID获取实体
     */
    @DataSource(DataSourceType.SLAVE)
    public Tag getById(BigInteger id) {
        return mapper.getById(id);
    }

    /**
     * 提取特定的实体信息
     */
    @DataSource(DataSourceType.SLAVE)
    public Tag extractById(BigInteger id) {
        return mapper.extractById(id);
    }

    /**
     * 插入新的实体记录
     */
    @DataSource(DataSourceType.MASTER)
    public int insert(Tag tag) {
        return mapper.insert(tag);
    }

    /**
     * 更新实体记录
     */
    @DataSource(DataSourceType.MASTER)
    public int update(Tag tag) {
        return mapper.update(tag);
    }

    /**
     * 删除实体记录（逻辑删除）
     */
    @DataSource(DataSourceType.MASTER)
    public int delete(BigInteger id) {
        if (id == null) {
            throw new RuntimeException("ID 不能为空");
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        return mapper.delete(id, time);
    }

    /**
     * 根据标签名称获取标签
     */
    @DataSource(DataSourceType.SLAVE)
    public Tag getByName(String name) {
        return mapper.getByName(name);
    }

    /**
     * 根据标签名称获取标签，不存在则创建
     */
    @DataSource(DataSourceType.MASTER)
    public Tag getOrCreateByName(String name) {
        Tag tag = getByName(name);
        if (tag == null) {
            // 创建新标签
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            tag = new Tag()
                .setName(name)
                .setCreateTime(currentTime)
                .setUpdateTime(currentTime)
                .setIsDeleted(0);
            insert(tag);
        }
        return tag;
    }

    /**
     * 根据游戏ID获取标签列表
     */
    @DataSource(DataSourceType.SLAVE)
    public List<Tag> getTagsByGameId(BigInteger gameId) {
        if (gameId == null) {
            throw new RuntimeException("游戏ID不能为空");
        }
        List<BigInteger> tagIds = gameTagRelationService.getTagIdsByGameId(gameId);
        return mapper.getByIds(tagIds);
    }

    /**
     * 获取所有标签
     */
    @DataSource(DataSourceType.SLAVE)
    public List<Tag> getAllTag(String keyword) {
        return mapper.getAllTag(keyword);
    }

    /**
     * 更新游戏标签
     * @param gameId 游戏ID
     * @param tags 标签字符串，多个标签用逗号分隔
     */
    @Transactional
    @DataSource(DataSourceType.MASTER)
    public void updateGameTags(BigInteger gameId, String tags) {
        if (gameId == null) {
            throw new RuntimeException("游戏ID不能为空");
        }
        List<BigInteger> tagIdList = new ArrayList<>();
        if (tags != null && !tags.isEmpty()) {
            // 解析标签字符串
            String[] tagArray = tags.split(",");
            for (String tagName : tagArray) {
                tagName = tagName.trim();
                if (!tagName.isEmpty()) {
                    // 获取或创建标签
                    Tag tag = getOrCreateByName(tagName);
                    if (tag != null && tag.getId() != null) {
                        tagIdList.add(tag.getId());

                        // 创建关联关系
                        gameTagRelationService.create(gameId, tag.getId());
                    }
                }
            }
        }

        // 删除不再使用的标签关联
        if (!tagIdList.isEmpty()) {
            gameTagRelationService.deleteNotInTagIds(gameId, tagIdList);
        }
    }

}