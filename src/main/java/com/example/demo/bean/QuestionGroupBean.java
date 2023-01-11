package com.example.demo.bean;

import com.example.enc.base.bean.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.*;
import java.time.LocalDateTime;

/**
 * 题目组
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class QuestionGroupBean extends PageParam {

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
     * 题目组标题
     */
    private String questionGroupTitle;

    /**
     * 题目数量
     */
    private Integer count;

    /**
     * 共计分数
     */
    private Integer sumScore;

    /**
     * 题目列表
     */
    private List<QuestionBean> questionList;

    /**
     * 上次得分
     */
    private Integer lastScore;

}