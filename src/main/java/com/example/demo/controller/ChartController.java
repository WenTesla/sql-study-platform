package com.example.demo.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.bean.ChartBean;
import com.example.demo.entity.LinkQuestionGroupEntity;
import com.example.demo.entity.QuestionGroupEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UserResultEntity;
import com.example.demo.mapper.LinkQuestionGroupMapper;
import com.example.demo.mapper.QuestionGroupMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChartController {

    private final QuestionGroupMapper questionGroupMapper;

    private final LinkQuestionGroupMapper linkQuestionGroupMapper;

    @GetMapping("/question")
    public ChartBean getQuestion() {

        var groupList = questionGroupMapper.selectList(
                Wrappers.<QuestionGroupEntity>lambdaQuery()
        );

        var dataset = new ChartBean.Dataset();
        dataset.setLabel("试卷");
        dataset.setData(
                groupList.stream()
                        .map(QuestionGroupEntity::getId)
                        .map(groupId -> linkQuestionGroupMapper.selectCount(
                                Wrappers.<LinkQuestionGroupEntity>lambdaQuery()
                                        .eq(LinkQuestionGroupEntity::getQuestionGroupId, groupId)
                        ))
                        .collect(Collectors.toList())
        );

        return new ChartBean()
                .setLabels(
                        groupList.stream()
                                .map(QuestionGroupEntity::getQuestionGroupTitle)
                                .collect(Collectors.toList())
                )
                .setDatasets(Collections.singletonList(dataset));
    }

    private final UserResultMapper userResultMapper;

    private final UserMapper userMapper;

    @GetMapping("/sumScore")
    public ChartBean getSumScore() {

        var list = userResultMapper.selectList(
                Wrappers.<UserResultEntity>lambdaQuery()
                        .select(UserResultEntity::getUserId)
                        .groupBy(UserResultEntity::getUserId)
        );

        var dataset = new ChartBean.Dataset();
        dataset.setLabel("总分");
        dataset.setData(
                list.stream()
                        .map(UserResultEntity::getUserId)
                        .map(userId -> {

                            var scoreList = userResultMapper.selectList(
                                    Wrappers.<UserResultEntity>lambdaQuery()
                                            .eq(UserResultEntity::getUserId, userId)
                            );

                            return scoreList.stream()
                                    .mapToInt(UserResultEntity::getScore)
                                    .sum();
                        })
                        .collect(Collectors.toList())
        );


        return new ChartBean()
                .setLabels(
                        list.stream()
                                .map(UserResultEntity::getUserId)
                                .map(userMapper::selectById)
                                .map(UserEntity::getUsername)
                                .collect(Collectors.toList())
                )
                .setDatasets(Collections.singletonList(dataset));
    }


}
