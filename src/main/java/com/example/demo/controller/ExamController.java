package com.example.demo.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.ExamStartBean;
import com.example.demo.bean.QuestionGroupBean;
import com.example.demo.bean.TableDataBean;
import com.example.demo.bean.UserResultBean;
import com.example.demo.entity.QuestionEntity;
import com.example.demo.entity.UserResultEntity;
import com.example.demo.mapper.LinkQuestionGroupMapper;
import com.example.demo.mapper.QuestionGroupMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.UserResultMapper;
import com.example.demo.util.SqlUtil;
import com.example.enc.base.annotation.CurrentUser;
import com.example.enc.base.bean.LoginInfoBean;
import com.example.enc.base.exception.BizException;
import com.example.enc.base.mapper.AnySqlMapper;
import lombok.RequiredArgsConstructor;
import lombok.var;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author yunong.byn
 * @since 2021/2/9 7:47 下午
 */
@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExamController {

    private final QuestionGroupMapper questionGroupMapper;

    private final QuestionGroupController questionGroupController;

    private final QuestionMapper questionMapper;

    private final UserResultMapper userResultMapper;

    private final LinkQuestionGroupMapper linkQuestionGroupMapper;

    private final HttpSession session;

    private final AnySqlMapper anySqlMapper;


    @PostMapping("/start")
    public QuestionGroupBean start(@CurrentUser LoginInfoBean loginInfo, @RequestBody ExamStartBean param) {

        if (Boolean.FALSE.equals(param.getMust())) {
            UserResultEntity exam = (UserResultEntity) session.getAttribute("EXAM");
            if (exam != null) {
                return questionGroupController.get(exam.getQuestionGroupId());
            }
        }

        var exam = new UserResultEntity();
        exam.setUserId(loginInfo.getId());
        exam.setQuestionGroupId(param.getGroupId());
        exam.setStartTime(LocalDateTime.now());

        session.setAttribute("EXAM", exam);
        return null;
    }

    @GetMapping("/data")
    public List<TableDataBean> data(Long questionId) {

        var question = questionMapper.selectById(questionId);

        try {
            var tableNameList = this.createTable(question);

            List<TableDataBean> beanList = new ArrayList<>();
            for (String tableName : tableNameList) {

                var list = anySqlMapper.exec(
                        StrUtil.format("SELECT * FROM {}", tableName)
                );

                beanList.add(
                        new TableDataBean()
                                .setTableName(tableName)
                                .setColumnNameList(new ArrayList<>(list.get(0).keySet()))
                                .setDataList(list)
                );
            }

            return beanList;
        } finally {
            this.dropTable(question);
        }
    }

    @PostMapping("/exec")
    public TableDataBean exec(@RequestBody UserResultBean param) {

        var selectSqlList = SqlUtil.getSqlList(param.getAnswer(), "SELECT");
        for (String selectSql : selectSqlList) {
            SqlUtil.getSelect(selectSql);
        }

        var question = questionMapper.selectById(param.getQuestionId());

        try {
            this.createTable(question);

            List<LinkedHashMap<String, Object>> list;
            try {
                list = anySqlMapper.exec(param.getAnswer());
            } catch (Exception e) {
                throw new BizException(e.getMessage());
            }
            if (list == null || list.isEmpty()) {
                return new TableDataBean();
            }
            return new TableDataBean()
                    .setColumnNameList(new ArrayList<>(list.get(0).keySet()))
                    .setDataList(list);
        } finally {
            this.dropTable(question);
        }
    }

    @PostMapping("/finish")
    public int finish(@CurrentUser LoginInfoBean loginInfo, @RequestBody List<UserResultBean> paramList) {

        UserResultEntity exam = (UserResultEntity) session.getAttribute("EXAM");

        var now = LocalDateTime.now();
        int sum = 0;
        List<UserResultEntity> resultList = new ArrayList<>();
        for (UserResultBean finish : paramList) {
            var result = new UserResultEntity();
            result.setUserId(loginInfo.getId());
            result.setQuestionGroupId(exam.getQuestionGroupId());
            result.setQuestionId(finish.getQuestionId());

            int score = this.getScore(finish.getQuestionId(), finish.getAnswer());
            sum += score;
            result.setScore(score);
            result.setAnswer(finish.getAnswer());
            result.setStartTime(exam.getStartTime());
            result.setEndTime(now);

            resultList.add(result);
        }

        userResultMapper.delete(
                Wrappers.<UserResultEntity>lambdaQuery()
                        .eq(UserResultEntity::getQuestionGroupId, exam.getQuestionGroupId())
        );

        for (UserResultEntity result : resultList) {
            userResultMapper.insert(result);
        }
        session.removeAttribute("EXAM");

        return sum;
    }

    private List<String> createTable(QuestionEntity question) {

        List<String> tableSqlList = SqlUtil.getSqlList(
                Base64.decodeStr(question.getInitTableSql()),
                "CREATE TABLE"
        );
        List<String> tableNameList = new ArrayList<>();
        for (String tableSql : tableSqlList) {

            CreateTable createTable = SqlUtil.getCreateTable(tableSql);
            String tableName = SqlUtil.getTableName(createTable.getTable());
            tableNameList.add(tableName);
            anySqlMapper.exec(StrUtil.format("DROP TABLE IF EXISTS `{}`", tableName));
            anySqlMapper.exec(tableSql);
        }

        var insertSqlList = SqlUtil.getSqlList(
                Base64.decodeStr(question.getInitDataSql()),
                "INSERT INTO"
        );
        for (String insertSql : insertSqlList) {
            anySqlMapper.exec(insertSql);
        }
        return tableNameList;
    }

    private void dropTable(QuestionEntity question) {

        List<String> tableSqlList = SqlUtil.getSqlList(
                Base64.decodeStr(question.getInitTableSql()),
                "CREATE TABLE"
        );
        for (String tableSql : tableSqlList) {

            CreateTable createTable = SqlUtil.getCreateTable(tableSql);
            String tableName = SqlUtil.getTableName(createTable.getTable());
            anySqlMapper.exec(StrUtil.format("DROP TABLE IF EXISTS `{}`", tableName));
        }
    }

    private int getScore(Long questionId, String answer) {

        var question = questionMapper.selectById(questionId);

        this.createTable(question);

        try {
            var selectSqlList = SqlUtil.getSqlList(answer, "SELECT");
            for (String selectSql : selectSqlList) {
                SqlUtil.getSelect(selectSql);
            }

            var teacher = anySqlMapper.exec(
                    Base64.decodeStr(question.getAnswerSql())
            );
            var student = anySqlMapper.exec(answer);

            return JSON.toJSONString(teacher).equals(JSON.toJSONString(student)) ? question.getScore() : 0;
        } catch (Exception e) {
            return 0;
        } finally {
            this.dropTable(question);
        }
    }

}
