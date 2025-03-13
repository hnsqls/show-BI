package com.ls.bi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.bi.model.dto.chart.ChartQueryRequest;
import com.ls.bi.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 26611
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2025-03-06 14:56:00
*/
public interface ChartService extends IService<Chart> {

    /**
     *  分页查询图表信息
     * @param chartQueryRequest
     * @param userId
     * @return
     */
    Page<Chart> listChartByPage(ChartQueryRequest chartQueryRequest, Long userId);


    /**
     * 模糊查询 图表名称，图表类型，分析目标
     * @param searchText
     * @param userId
     * @return
     */
    Page<Chart> searchText(String searchText,Long userId);
}
