package com.ls.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.bi.common.ErrorCode;
import com.ls.bi.exception.ThrowUtils;
import com.ls.bi.model.dto.chart.ChartQueryRequest;
import com.ls.bi.model.entity.Chart;
import com.ls.bi.service.ChartService;
import com.ls.bi.mapper.ChartMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 26611
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2025-03-06 14:56:00
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    /**
     * 分页查询图表信息
     * @param chartQueryRequest
     * @param userId
     * @return
     */
    @Override
    public Page<Chart> listChartByPage(ChartQueryRequest chartQueryRequest, Long userId) {

        //参数逻辑校验
        ThrowUtils.throwIf(chartQueryRequest == null, ErrorCode.PARAMS_ERROR);

        int current = chartQueryRequest.getCurrent();
        int pageSize = chartQueryRequest.getPageSize();
        
        // 构造查询条件
        LambdaQueryWrapper<Chart> chartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chartLambdaQueryWrapper.eq(Chart::getUserId, userId);
        
        // 构造分页
        Page<Chart> page = new Page<>(current, pageSize);

        // 分页排序
        page.addOrder(OrderItem.desc("updateTime"));

        //分页查询
         page = this.page(page, chartLambdaQueryWrapper);

         // 获取分页结果
//        List<Chart> records = page.getRecords();

        return page;
    }
}




