package com.example.demo.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.QuestionBean;
import com.example.demo.entity.QuestionEntity;
import com.example.demo.mapper.LinkQuestionGroupMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.util.SqlUtil;
import com.example.enc.base.bean.PageResultBean;
import com.example.enc.base.exception.BizException;
import com.example.enc.base.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("question")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionController {

    private final QuestionMapper questionMapper;

    private final LinkQuestionGroupMapper linkQuestionGroupMapper;


    @GetMapping("/{id}")
    public QuestionBean get(@PathVariable long id) {

        var entity = questionMapper.selectById(id);
        return BeanUtil.to(entity, new QuestionBean())
                .setInitTableSql(Base64.decodeStr(entity.getInitTableSql()))
                .setInitDataSql(Base64.decodeStr(entity.getInitDataSql()))
                .setAnswerSql(Base64.decodeStr(entity.getAnswerSql()));
    }

    @GetMapping("/random")
    public List<QuestionBean> getRandom(Integer size) {

        var list = questionMapper.selectList(
                Wrappers.emptyWrapper()
        );

        Collections.shuffle(list);
        list = list.subList(0, list.size() < size ? list.size() : size);

        List<QuestionBean> beanList = new ArrayList<>();
        for (QuestionEntity entity : list) {
            beanList.add(
                    BeanUtil.to(entity, new QuestionBean())
                            .setInitTableSql(Base64.decodeStr(entity.getInitTableSql()))
                            .setInitDataSql(Base64.decodeStr(entity.getInitDataSql()))
                            .setAnswerSql(Base64.decodeStr(entity.getAnswerSql()))
            );
        }

        return beanList;
    }

    @GetMapping("/list")
    public List<QuestionBean> getList(QuestionBean query) {

        var list = questionMapper.selectList(
                Wrappers.<QuestionEntity>lambdaQuery()
                        .like(
                                StrUtil.isNotBlank(query.getQuestionTitle()),
                                QuestionEntity::getQuestionTitle,
                                query.getQuestionTitle()
                        )
        );

        List<QuestionBean> beanList = new ArrayList<>();
        for (QuestionEntity entity : list) {
            beanList.add(
                    BeanUtil.to(entity, new QuestionBean())
                            .setInitTableSql(Base64.decodeStr(entity.getInitTableSql()))
                            .setInitDataSql(Base64.decodeStr(entity.getInitDataSql()))
                            .setAnswerSql(Base64.decodeStr(entity.getAnswerSql()))
            );
        }

        return beanList;
    }

    @GetMapping("/page")
    public PageResultBean<QuestionBean> getPage(QuestionBean query) {

        IPage<QuestionEntity> page = questionMapper.selectPage(
                query.toPage(),
                Wrappers.<QuestionEntity>lambdaQuery()
                        .like(
                                StrUtil.isNotBlank(query.getQuestionTitle()),
                                QuestionEntity::getQuestionTitle,
                                query.getQuestionTitle()
                        )
        );

        List<QuestionBean> beanList = new ArrayList<>();
        for (QuestionEntity entity : page.getRecords()) {
            beanList.add(
                    BeanUtil.to(entity, new QuestionBean())
                            .setInitTableSql(Base64.decodeStr(entity.getInitTableSql()))
                            .setInitDataSql(Base64.decodeStr(entity.getInitDataSql()))
                            .setAnswerSql(Base64.decodeStr(entity.getAnswerSql()))
            );
        }

        return PageResultBean.from(page, beanList);
    }

    @PostMapping
    public Long post(@RequestBody @Valid QuestionBean param) {

        var entity = BeanUtil.to(param, new QuestionEntity())
                .setInitTableSql(Base64.encode(param.getInitTableSql()))
                .setInitDataSql(Base64.encode(param.getInitDataSql()))
                .setAnswerSql(Base64.encode(param.getAnswerSql()));

        this.checkInsertSql(entity, this.checkInitTableSql(entity));

        questionMapper.insert(entity);
        return entity.getId();
    }

    @PutMapping("/{id}")
    public Long put(@PathVariable Long id, @RequestBody @Valid QuestionBean param) {

        var entity = questionMapper.selectById(id);

        if (entity == null) {
            throw new BizException("题目 不存在");
        }

        entity = BeanUtil.to(param, new QuestionEntity())
                .setId(id)
                .setInitTableSql(Base64.encode(param.getInitTableSql()))
                .setInitDataSql(Base64.encode(param.getInitDataSql()))
                .setAnswerSql(Base64.encode(param.getAnswerSql()));
        this.checkInsertSql(entity, this.checkInitTableSql(entity));

        questionMapper.updateById(entity);
        return entity.getId();
    }

    @DeleteMapping("/{id}")
    public QuestionBean delete(@PathVariable Long id) {

        var entity = questionMapper.selectById(id);

        if (entity == null) {
            throw new BizException("题目 不存在");
        }

        questionMapper.deleteById(id);

        return BeanUtil.to(entity, new QuestionBean());
    }

    private Set<String> checkInitTableSql(QuestionEntity question) {

        var createSqlList = SqlUtil.getSqlList(
                Base64.decodeStr(question.getInitTableSql()),
                "CREATE TABLE"
        );

        List<String> tableNameList = new ArrayList<>();
        for (String sql : createSqlList) {
            CreateTable createTable = SqlUtil.getCreateTable(sql);
            String tableName = SqlUtil.getTableName(createTable.getTable());
            if (!tableName.startsWith("temp_")) {
                throw new BizException("表名以 temp_ 开头");
            }
            tableNameList.add(SqlUtil.getTableName(createTable.getTable()));
        }

        Set<String> tableNameSet = new HashSet<>(tableNameList);
        if (tableNameSet.size() != tableNameList.size()) {
            throw new BizException("表重复");
        }

        return tableNameSet;
    }

    private void checkInsertSql(QuestionEntity question, Set<String> tableNameSet) {

        var insertSqlList = SqlUtil.getSqlList(
                Base64.decodeStr(question.getInitDataSql()),
                "INSERT INTO"
        );

        for (String sql : insertSqlList) {
            Insert insert = SqlUtil.getInsert(sql);
            String tableName = SqlUtil.getTableName(insert.getTable());
            if (!tableNameSet.contains(tableName)) {
                throw new BizException(StrUtil.format("表 {} 不存在", tableName));
            }
        }
    }

}