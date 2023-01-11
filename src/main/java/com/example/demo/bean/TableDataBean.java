package com.example.demo.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@Accessors(chain = true)
public class TableDataBean {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列
     */
    private List<String> columnNameList;

    /**
     * 数据
     */
    private List<LinkedHashMap<String, Object>> dataList;

}
