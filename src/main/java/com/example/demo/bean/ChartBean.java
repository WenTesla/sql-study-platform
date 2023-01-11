package com.example.demo.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

@Data
@Accessors(chain = true)
public class ChartBean {

    private List<String> labels;

    private List<Dataset> datasets;

    @Data
    @Accessors(chain = true)
    public static class Dataset {

        private String label;

        private List<Number> data;

        private List<String> backgroundColor = Arrays.asList(
                "rgba(255, 99, 132, 0.2)",
                "rgba(54, 162, 235, 0.2)",
                "rgba(255, 206, 86, 0.2)",
                "rgba(75, 192, 192, 0.2)",
                "rgba(153, 102, 255, 0.2)",
                "rgba(255, 159, 64, 0.2)"
        );

        private List<String> borderColor = Arrays.asList(
                "rgba(255, 99, 132, 1)",
                "rgba(54, 162, 235, 1)",
                "rgba(255, 206, 86, 1)",
                "rgba(75, 192, 192, 1)",
                "rgba(153, 102, 255, 1)",
                "rgba(255, 159, 64, 1)"
        );

        private Integer borderWidth = 1;

    }

}
