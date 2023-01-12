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

/**
 * 用于返回表格的数据
 *
 */
@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChartController {

    private final QuestionGroupMapper questionGroupMapper;

    private final LinkQuestionGroupMapper linkQuestionGroupMapper;

    /**
     * 统计学生总分
     * 下面的例子
     * {
     * 	"success": true,
     * 	"code": 200,
     * 	"data": {
     * 		"labels": ["单元测试1", "单元测试2", "期末考试"],
     * 		"datasets": [{
     * 			"label": "试卷",
     * 			"data": [5, 2, 1],
     * 			"backgroundColor": ["rgba(255, 99, 132, 0.2)", "rgba(54, 162, 235, 0.2)", "rgba(255, 206, 86, 0.2)", "rgba(75, 192, 192, 0.2)", "rgba(153, 102, 255, 0.2)", "rgba(255, 159, 64, 0.2)"],
     * 			"borderColor": ["rgba(255, 99, 132, 1)", "rgba(54, 162, 235, 1)", "rgba(255, 206, 86, 1)", "rgba(75, 192, 192, 1)", "rgba(153, 102, 255, 1)", "rgba(255, 159, 64, 1)"],
     * 			"borderWidth": 1
     *                }]* 	}
     * }
     * @return
     */
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

    /**
     * 统计试卷题目数
     * 下列的例子
     {
     "success": true,
     "code": 200,
     "data": {
     "labels": ["1", "松涵梅", "蒋念雁", "睦辰", "test", "666"],
     "datasets": [{
     "label": "总分",
     "data": [20, 41, 64, 39, 32, 15],
     "backgroundColor": ["rgba(255, 99, 132, 0.2)", "rgba(54, 162, 235, 0.2)", "rgba(255, 206, 86, 0.2)", "rgba(75, 192, 192, 0.2)", "rgba(153, 102, 255, 0.2)", "rgba(255, 159, 64, 0.2)"],
     "borderColor": ["rgba(255, 99, 132, 1)", "rgba(54, 162, 235, 1)", "rgba(255, 206, 86, 1)", "rgba(75, 192, 192, 1)", "rgba(153, 102, 255, 1)", "rgba(255, 159, 64, 1)"],
     "borderWidth": 1
     }]
     }
     }
     *
     * @return
     */
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
