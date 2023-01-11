package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.QuestionGroupBean;
import com.example.demo.bean.UserResultBean;
import com.example.demo.entity.UserResultEntity;
import com.example.demo.mapper.UserResultMapper;
import com.example.enc.base.annotation.CurrentUser;
import com.example.enc.base.bean.LoginInfoBean;
import com.example.enc.base.bean.PageResultBean;
import com.example.enc.base.exception.BizException;
import com.example.enc.base.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("user/result")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserResultController {

    private final UserResultMapper userResultMapper;

    private final QuestionGroupController questionGroupController;


    @GetMapping("/{id}")
    public UserResultBean get(@PathVariable long id) {

        var entity = userResultMapper.selectById(id);
        return BeanUtil.to(entity, new UserResultBean());
    }

    @GetMapping("/my/group")
    public List<QuestionGroupBean> getMyGroupList(@CurrentUser LoginInfoBean loginInfo) {

        var list = userResultMapper.selectList(
                Wrappers.<UserResultEntity>lambdaQuery()
                        .select(UserResultEntity::getQuestionGroupId)
                        .eq(UserResultEntity::getUserId, loginInfo.getId())
                        .groupBy(UserResultEntity::getQuestionGroupId)
        );

        var beanList = new ArrayList<QuestionGroupBean>();
        for (UserResultEntity entity : list) {
            beanList.add(questionGroupController.get(entity.getQuestionGroupId()));
        }

        return beanList;
    }

    @GetMapping("/list")
    public List<UserResultBean> getList(UserResultBean query) {

        var list = userResultMapper.selectList(
                Wrappers.<UserResultEntity>lambdaQuery()
        );

        List<UserResultBean> beanList = new ArrayList<>();
        for (UserResultEntity entity : list) {
            beanList.add(
                    BeanUtil.to(entity, new UserResultBean())
            );
        }

        return beanList;
    }

    @GetMapping("/page")
    public PageResultBean<UserResultBean> getPage(UserResultBean query) {

        IPage<UserResultEntity> page = userResultMapper.selectPage(
                query.toPage(),
                Wrappers.<UserResultEntity>lambdaQuery()
                        .eq(
                                query.getUserId() != null,
                                UserResultEntity::getUserId,
                                query.getUserId()
                        )
                        .eq(
                                query.getQuestionGroupId() != null,
                                UserResultEntity::getQuestionGroupId,
                                query.getQuestionGroupId()
                        )
                        .eq(
                                query.getQuestionId() != null,
                                UserResultEntity::getQuestionId,
                                query.getQuestionId()
                        ));

        List<UserResultBean> beanList = new ArrayList<>();
        for (UserResultEntity entity : page.getRecords()) {
            beanList.add(
                    BeanUtil.to(entity, new UserResultBean())
            );
        }

        return PageResultBean.from(page, beanList);
    }

    @PostMapping
    public Long post(@RequestBody @Valid UserResultBean param) {

        var entity = BeanUtil.to(param, new UserResultEntity());
        userResultMapper.insert(entity);
        return entity.getId();
    }

    @PutMapping("/{id}")
    public Long put(@PathVariable Long id, @RequestBody @Valid UserResultBean param) {

        var entity = userResultMapper.selectById(id);

        if (entity == null) {
            throw new BizException("用户结果 不存在");
        }

        entity = BeanUtil.to(param, new UserResultEntity())
                .setId(id);
        userResultMapper.updateById(entity);
        return entity.getId();
    }

    @DeleteMapping("/{id}")
    public UserResultBean delete(@PathVariable Long id) {

        var entity = userResultMapper.selectById(id);

        if (entity == null) {
            throw new BizException("用户结果 不存在");
        }

        userResultMapper.deleteById(id);

        return BeanUtil.to(entity, new UserResultBean());
    }

}