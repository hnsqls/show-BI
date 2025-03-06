package com.ls.bi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.bi.common.BaseResponse;
import com.ls.bi.common.DeleteRequest;
import com.ls.bi.common.ErrorCode;
import com.ls.bi.common.ResultUtils;
import com.ls.bi.annotation.AuthCheck;
import com.ls.bi.constant.UserConstant;
import com.ls.bi.exception.BusinessException;
import com.ls.bi.exception.ThrowUtils;
import com.ls.bi.model.dto.chart.ChartAddRequest;
import com.ls.bi.model.dto.chart.ChartQueryRequest;
import com.ls.bi.model.dto.chart.ChartUpdateRequest;
import com.ls.bi.model.entity.Chart;
import com.ls.bi.service.ChartService;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图表接口
 */
@RestController
@RequestMapping("/chart")
public class ChartController {

    @Resource
    private ChartService chartService;

    // region 增删改查

    /**
     * 创建图表
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 校验参数
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        chartService.save(chart);
        return ResultUtils.success(chart.getId());
    }

    /**
     * 删除图表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = chartService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新图表
     *
     * @param chartUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest,
                                             HttpServletRequest request) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        boolean result = chartService.updateById(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取图表
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        ThrowUtils.throwIf(chart == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(chart);
    }


    /**
     * 分页获取图表列表（仅管理员）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
//    @PostMapping("/list/page")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
//                                                     HttpServletRequest request) {
//        long current = chartQueryRequest.getCurrent();
//        long size = chartQueryRequest.getPageSize();
//        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
//                chartService.getQueryWrapper(chartQueryRequest));
//        return ResultUtils.success(chartPage);
//    }



    // endregion
}