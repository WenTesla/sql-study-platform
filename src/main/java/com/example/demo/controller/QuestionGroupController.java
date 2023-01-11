package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.QuestionBean;
import com.example.demo.bean.QuestionGroupBean;
import com.example.demo.entity.LinkQuestionGroupEntity;
import com.example.demo.entity.QuestionGroupEntity;
import com.example.demo.entity.UserResultEntity;
import com.example.demo.mapper.LinkQuestionGroupMapper;
import com.example.demo.mapper.QuestionGroupMapper;
import com.example.demo.mapper.UserResultMapper;
import com.example.enc.base.annotation.CurrentUser;
import com.example.enc.base.bean.LoginInfoBean;
import com.example.enc.base.bean.PageResultBean;
import com.example.enc.base.exception.BizException;
import com.example.enc.base.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("question/group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionGroupController {

    private final QuestionGroupMapper questionGroupMapper;

    private final LinkQuestionGroupMapper linkQuestionGroupMapper;

    private final QuestionController questionController;

    private final UserResultMapper userResultMapper;


    @GetMapping("/{id}")
    public QuestionGroupBean get(@PathVariable long id) {

        var entity = questionGroupMapper.selectById(id);
        var bean = BeanUtil.to(entity, new QuestionGroupBean());

        var list = linkQuestionGroupMapper.selectList(
                Wrappers.<LinkQuestionGroupEntity>lambdaQuery()
                        .eq(LinkQuestionGroupEntity::getQuestionGroupId, entity.getId())
        );

        bean.setQuestionList(
                list.stream()
                        .map(LinkQuestionGroupEntity::getQuestionId)
                        .map(questionController::get)
                        .collect(Collectors.toList())
        );
        return bean;
    }

    @GetMapping("/list")
    public List<QuestionGroupBean> getList(QuestionGroupBean query) {

        var list = questionGroupMapper.selectList(
                Wrappers.<QuestionGroupEntity>lambdaQuery()
        );

        List<QuestionGroupBean> beanList = new ArrayList<>();
        for (QuestionGroupEntity entity : list) {
            beanList.add(
                    BeanUtil.to(entity, new QuestionGroupBean())
            );
        }

        return beanList;
    }

    @GetMapping("/page")
    public PageResultBean<QuestionGroupBean> getPage(@CurrentUser LoginInfoBean loginInfo, QuestionGroupBean query) {

        IPage<QuestionGroupEntity> page = questionGroupMapper.selectPage(
                query.toPage(),
                Wrappers.<QuestionGroupEntity>lambdaQuery()
                        .like(
                                StrUtil.isNotBlank(query.getQuestionGroupTitle()),
                                QuestionGroupEntity::getQuestionGroupTitle,
                                query.getQuestionGroupTitle()
                        )
        );

        List<QuestionGroupBean> beanList = new ArrayList<>();
        for (QuestionGroupEntity entity : page.getRecords()) {

            var linkList = linkQuestionGroupMapper.selectList(
                    Wrappers.<LinkQuestionGroupEntity>lambdaQuery()
                            .eq(LinkQuestionGroupEntity::getQuestionGroupId, entity.getId())
            );

            var sumScore = linkList.stream()
                    .map(LinkQuestionGroupEntity::getQuestionId)
                    .map(questionController::get)
                    .mapToInt(QuestionBean::getScore)
                    .sum();

            var resultList = userResultMapper.selectList(
                    Wrappers.<UserResultEntity>lambdaQuery()
                            .eq(UserResultEntity::getUserId, loginInfo.getId())
                            .eq(UserResultEntity::getQuestionGroupId, entity.getId())
            );

            var lastScore = resultList.stream()
                    .mapToInt(UserResultEntity::getScore)
                    .sum();

            beanList.add(
                    BeanUtil.to(entity, new QuestionGroupBean())
                            .setCount(linkList.size())
                            .setSumScore(sumScore)
                            .setLastScore(resultList.isEmpty() ? null : lastScore)
            );
        }

        return PageResultBean.from(page, beanList);
    }

    @PostMapping
    @Transactional
    public Long post(@RequestBody @Valid QuestionGroupBean param) {

        var entity = BeanUtil.to(param, new QuestionGroupEntity());
        questionGroupMapper.insert(entity);

        this.modifyQuestionList(
                entity.getId(),
                param.getQuestionList()
                        .stream()
                        .map(QuestionBean::getId)
                        .collect(Collectors.toList())
        );
        return entity.getId();
    }

    @PutMapping("/{id}")
    @Transactional
    public Long put(@PathVariable Long id, @RequestBody @Valid QuestionGroupBean param) {

        var entity = questionGroupMapper.selectById(id);

        if (entity == null) {
            throw new BizException("题目组 不存在");
        }

        entity = BeanUtil.to(param, new QuestionGroupEntity())
                .setId(id);
        questionGroupMapper.updateById(entity);

        this.modifyQuestionList(
                entity.getId(),
                param.getQuestionList()
                        .stream()
                        .map(QuestionBean::getId)
                        .collect(Collectors.toList())
        );

        return entity.getId();
    }

    private void modifyQuestionList(Long questionGroupId, List<Long> questionIdList) {

        linkQuestionGroupMapper.delete(
                Wrappers.<LinkQuestionGroupEntity>lambdaQuery()
                        .eq(LinkQuestionGroupEntity::getQuestionGroupId, questionGroupId)
        );

        for (Long questionId : questionIdList) {

            var link = new LinkQuestionGroupEntity();
            link.setQuestionGroupId(questionGroupId);
            link.setQuestionId(questionId);
            linkQuestionGroupMapper.insert(link);
        }

    }

    @DeleteMapping("/{id}")
    public QuestionGroupBean delete(@PathVariable Long id) {

        var entity = questionGroupMapper.selectById(id);

        if (entity == null) {
            throw new BizException("题目组 不存在");
        }

        questionGroupMapper.deleteById(id);

        return BeanUtil.to(entity, new QuestionGroupBean());
    }

}