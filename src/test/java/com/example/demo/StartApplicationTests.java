package com.example.demo;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.entity.QuestionEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.UserResultEntity;
import com.example.demo.mapper.QuestionGroupMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StartApplicationTests {

    private final QuestionMapper questionMapper;

    private final QuestionGroupMapper questionGroupMapper;

    private final UserMapper userMapper;

    private final UserResultMapper userResultMapper;

    @Test
    void contextLoads() {

        var userList = userMapper.selectList(Wrappers.<UserEntity>query()
                .ge("id", 100)
        );
        Collections.shuffle(userList);

        userList = userList.subList(0, 5);

        for (UserEntity user : userList) {

            int k = RandomUtil.randomInt(1, 6);
            for (int i = 0; i < k; i++) {
                var result = new UserResultEntity();
                result.setUserId(user.getId());
                result.setQuestionGroupId(RandomUtil.randomLong(3, 6));
                result.setQuestionId(RandomUtil.randomLong(1, 7));
                result.setScore(RandomUtil.randomInt(0, 20));
                result.setAnswer("mock");
                result.setStartTime(LocalDateTime.now());
                result.setEndTime(LocalDateTime.now());

                userResultMapper.insert(result);
            }
        }

    }

}
