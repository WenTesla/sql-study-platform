package com.example.demo.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExamStartBean {

    private Long groupId;

    private Boolean must;

}
