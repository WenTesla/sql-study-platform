package com.example.demo.util;

import cn.hutool.core.util.StrUtil;
import com.example.enc.base.exception.BizException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;
import java.util.stream.Collectors;

public class SqlUtil {

    public static List<String> getSqlList(String sql, String keyword) {

        sql = sql.replace(keyword.toLowerCase(), keyword.toUpperCase());

        List<String> list = StrUtil.splitTrim(sql, keyword.toUpperCase());
        return list.stream()
                .map(s -> keyword.toUpperCase() + " " + s)
                .collect(Collectors.toList());
    }

    public static CreateTable getCreateTable(String sql) {

        try {
            return (CreateTable) CCJSqlParserUtil.parse(sql);
        } catch (Exception e) {
            throw new BizException("SQL ERROR: " + e.getMessage());
        }
    }

    public static Insert getInsert(String sql) {

        try {
            return (Insert) CCJSqlParserUtil.parse(sql);
        } catch (Exception e) {
            throw new BizException("SQL ERROR: " + e.getMessage());
        }
    }

    public static Select getSelect(String sql) {

        try {
            return (Select) CCJSqlParserUtil.parse(sql);
        } catch (Exception e) {
            throw new BizException("SQL ERROR: " + e.getMessage());
        }
    }

    public static String getTableName(Table table) {
        return table.getName().replace("`", "");
    }

}
