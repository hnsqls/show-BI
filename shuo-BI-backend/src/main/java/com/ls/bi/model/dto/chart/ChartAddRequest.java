package com.ls.bi.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增图表
 */
@Data
public class ChartAddRequest implements Serializable {


    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;


    private static final long serialVersionUID = 1L;

}
