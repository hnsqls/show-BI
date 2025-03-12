package com.ls.bi.model.dto.chart;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 查询图表请求
 */
@Data
public class ChartQueryRequest implements Serializable {


    /**
     * page
     */
    private int current;

    /**
     * size
     */
    private int pageSize;



    private static final long serialVersionUID = 1L;
}
