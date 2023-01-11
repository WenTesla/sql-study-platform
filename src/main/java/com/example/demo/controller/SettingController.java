package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.SettingBean;
import com.example.demo.entity.SettingEntity;
import com.example.demo.mapper.SettingMapper;
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
@RequestMapping("/setting")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SettingController {

    private final SettingMapper settingMapper;


    @GetMapping("/{id}")
    public SettingBean get(@PathVariable long id) {

        var entity = settingMapper.selectById(id);
        return BeanUtil.to(entity, new SettingBean());
    }

    @GetMapping("/list")
    public List<SettingBean> getList(SettingBean query) {

        var list = settingMapper.selectList(
                Wrappers.<SettingEntity>lambdaQuery()
                        .eq(SettingEntity::getKey, query.getKey())
        );

        List<SettingBean> beanList = new ArrayList<>();
        for (SettingEntity entity : list) {
            beanList.add(
                    BeanUtil.to(entity, new SettingBean())
            );
        }

        return beanList;
    }

    @GetMapping("/page")
    public PageResultBean<SettingBean> getPage(SettingBean query) {

        IPage<SettingEntity> page = settingMapper.selectPage(
                query.toPage(),
                Wrappers.<SettingEntity>lambdaQuery()
        );

        List<SettingBean> beanList = new ArrayList<>();
        for (SettingEntity entity : page.getRecords()) {
            beanList.add(
                    BeanUtil.to(entity, new SettingBean())
            );
        }

        return PageResultBean.from(page, beanList);
    }

    @PostMapping
    public Long post(@RequestBody @Valid SettingBean param) {

        var entity = BeanUtil.to(param, new SettingEntity());
        settingMapper.insert(entity);
        return entity.getId();
    }

    @PutMapping("/{id}")
    public Long put(@PathVariable Long id, @RequestBody @Valid SettingBean param) {

        var entity = settingMapper.selectById(id);

        if (entity == null) {
            throw new BizException("基本配置 不存在");
        }

        entity = BeanUtil.to(param, new SettingEntity())
                .setId(id);
        settingMapper.updateById(entity);
        return entity.getId();
    }

    @DeleteMapping("/{id}")
    public SettingBean delete(@PathVariable Long id) {

        var entity = settingMapper.selectById(id);

        if (entity == null) {
            throw new BizException("基本配置 不存在");
        }

        settingMapper.deleteById(id);

        return BeanUtil.to(entity, new SettingBean());
    }

}