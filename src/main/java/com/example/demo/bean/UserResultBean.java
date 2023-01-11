package com.example.demo.bean;

import com.example.enc.base.bean.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.*;
import java.time.LocalDateTime;

/**
 * 用户结果
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserResultBean extends PageParam {

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
     * 用户ID
     */
    private Long userId;

    /**
     * 试题组ID
     */
    private Long questionGroupId;

    /**
     * 试题组
     */
    private QuestionGroupBean questionGroup;

    /**
     * 试题组
     */
    private QuestionBean question;

    /**
     * 试题ID
     */
    private Long questionId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 答案
     */
    private String answer;

     /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 其他信息
     */
    private Map<String, Object> info;

}