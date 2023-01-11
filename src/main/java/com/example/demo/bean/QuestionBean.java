package com.example.demo.bean;

import com.example.enc.base.bean.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.*;
import java.time.LocalDateTime;

/**
 * 题目
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class QuestionBean extends PageParam {

    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
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
    private Map<String, Object> info;

}