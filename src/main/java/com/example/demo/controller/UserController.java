package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.UserBean;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
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
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserMapper userMapper;


    @GetMapping("/{id}")
    public UserBean get(@PathVariable long id) {

        var entity = userMapper.selectById(id);
        return BeanUtil.to(entity, new UserBean());
    }

    @GetMapping("/list")
    public List<UserBean> getList(UserBean query) {

        var list = userMapper.selectList(
                Wrappers.<UserEntity>lambdaQuery()
        );

        List<UserBean> beanList = new ArrayList<>();
        for (UserEntity entity : list) {
            beanList.add(
                    BeanUtil.to(entity, new UserBean())
            );
        }

        return beanList;
    }

    @GetMapping("/page")
    public PageResultBean<UserBean> getPage(UserBean query) {

        IPage<UserEntity> page = userMapper.selectPage(
                query.toPage(),
                Wrappers.<UserEntity>lambdaQuery()
                        .like(
                                StrUtil.isNotBlank(query.getUsername()),
                                UserEntity::getUsername,
                                query.getUsername()
                        )
                        .eq(
                                StrUtil.isNotBlank(query.getRoleCode()),
                                UserEntity::getRoleCode,
                                query.getRoleCode()
                        )
        );

        List<UserBean> beanList = new ArrayList<>();
        for (UserEntity entity : page.getRecords()) {
            beanList.add(
                    BeanUtil.to(entity, new UserBean())
            );
        }

        return PageResultBean.from(page, beanList);
    }

    @PostMapping
    public Long post(@RequestBody @Valid UserBean param) {

        var entity = BeanUtil.to(param, new UserEntity());
        userMapper.insert(entity);
        return entity.getId();
    }

    @PutMapping("/{id}")
    public Long put(@PathVariable Long id, @RequestBody @Valid UserBean param) {

        var entity = userMapper.selectById(id);

        if (entity == null) {
            throw new BizException("用户 不存在");
        }

        entity = BeanUtil.to(param, new UserEntity())
                .setId(id);
        userMapper.updateById(entity);
        return entity.getId();
    }

    @DeleteMapping("/{id}")
    public UserBean delete(@PathVariable Long id) {

        var entity = userMapper.selectById(id);

        if (entity == null) {
            throw new BizException("用户 不存在");
        }

        userMapper.deleteById(id);

        return BeanUtil.to(entity, new UserBean());
    }

}