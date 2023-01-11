package com.example.demo.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExamFinishBean {

    private Long questionId;

    private String answer;

}
