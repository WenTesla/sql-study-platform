package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 题目
 */
@Data
@Accessors(chain = true)
@TableName(value = "question", autoResultMap = true)
public class QuestionEntity {
    
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 题目标题
     */
    private String questionTitle;
    
        /**
     * 题目描述
     */
    private String content;
    
    /**
     * 初始化表结构
     */
    private String initTableSql;
    
    /**
     * 初始化数据
     */
    private String initDataSql;
    
    /**
     * 答案
     */
    private String answerSql;
    
    /**
     * 得分
     */
    private Integer score;
    
    /**
     * 其他信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> info;
    
}