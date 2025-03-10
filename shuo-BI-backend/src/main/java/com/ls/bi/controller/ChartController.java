package com.ls.bi.controller;

import com.ls.bi.common.BaseResponse;
import com.ls.bi.common.DeleteRequest;
import com.ls.bi.common.ErrorCode;
import com.ls.bi.common.ResultUtils;
import com.ls.bi.exception.BusinessException;
import com.ls.bi.exception.ThrowUtils;
import com.ls.bi.model.dto.chart.ChartAddRequest;
import com.ls.bi.model.dto.chart.ChartGenRequest;
import com.ls.bi.model.dto.chart.ChartUpdateRequest;
import com.ls.bi.model.entity.Chart;
import com.ls.bi.model.entity.User;
import com.ls.bi.service.ChartService;
import com.ls.bi.service.UserService;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ls.bi.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



/**
 * 图表接口
 */
@RestController
@RequestMapping("/chart")
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

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

        // 补充字段 userId
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        chart.setUserId(userService.getLoginUser(request).getId());
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


    /**
     * 智能分析
     * @param multipartFile
     * @param chartGenRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                             ChartGenRequest chartGenRequest, HttpServletRequest request) {

        String chartName = chartGenRequest.getChartName();
        String goal = chartGenRequest.getGoal();
        String chartType = chartGenRequest.getChartType();

        //校验
        ThrowUtils.throwIf(StringUtils.isAnyBlank(chartName, goal, chartType), ErrorCode.PARAMS_ERROR);

        Long userId = userService.getLoginUser(request).getId();
        //读取文件 excel ->csv
        String csv = ExcelUtils.excelToCsv(multipartFile);

        //系统prompt
        String systemPrompt = "你是一个数据分析师，我会给你一些数据，请根据我的要求生成的特定的图表，并且给出总结，不要回复其他任何无关的信息";
        // 用户prompt
        String userPrompt = "根据以下数据，生成一个%s类型的图表,目标为%s";
        String userPromptFormat = String.format(userPrompt, chartType, goal);

        //调用接口


        return ResultUtils.success(systemPrompt + userPromptFormat + csv);
//        // 文件目录：根据业务、用户来划分
//        String uuid = RandomStringUtils.randomAlphanumeric(8);
//        String filename = uuid + "-" + multipartFile.getOriginalFilename();
//
//        File file = null;
//        try {
//            return ResultUtils.success("http://localhost:8080/chart/" + filename);
//
//        } catch (Exception e) {
//
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
//        } finally {
//            if (file != null) {
//                // 删除临时文件
//                boolean delete = file.delete();
//                if (!delete) {
////                    log.error("file delete error, filepath = {}", filepath);
//                }
//            }
//        }
    }



    // endregion
}