package com.example.demo.bean;

import com.example.enc.base.bean.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.*;
import java.time.LocalDateTime;

/**
 * 基本配置
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SettingBean extends PageParam {
    
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
     * KEY
     */
    private String key;
    
    /**
     * VALUE
     */
    private String value;
    
}