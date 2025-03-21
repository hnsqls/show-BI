package com.ls.bi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ls.bi.common.BaseResponse;
import com.ls.bi.common.DeleteRequest;
import com.ls.bi.common.ErrorCode;
import com.ls.bi.common.ResultUtils;
import com.ls.bi.exception.BusinessException;
import com.ls.bi.exception.ThrowUtils;
import com.ls.bi.manager.AiManager;
import com.ls.bi.model.dto.chart.ChartAddRequest;
import com.ls.bi.model.dto.chart.ChartGenRequest;
import com.ls.bi.model.dto.chart.ChartQueryRequest;
import com.ls.bi.model.dto.chart.ChartUpdateRequest;
import com.ls.bi.model.entity.Chart;
import com.ls.bi.model.entity.User;
import com.ls.bi.mq.bus.AIProducer;
import com.ls.bi.service.ChartService;
import com.ls.bi.service.UserService;
import com.ls.bi.utils.ExcelUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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


    @Resource
    private AiManager aiManager;

    @Resource
    private AIProducer aiProducer;

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
     * 分页获取图表列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                     HttpServletRequest request) {


        //用户id
        Long userId = userService.getLoginUser(request).getId();

        Page<Chart> chartPage = chartService.listChartByPage(chartQueryRequest,userId);
        return ResultUtils.success(chartPage);
    }


    /**
     * 智能分析
     * @param multipartFile
     * @param chartGenRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<Chart> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                             ChartGenRequest chartGenRequest, HttpServletRequest request) {

        String chartName = chartGenRequest.getChartName();
        String goal = chartGenRequest.getGoal();
        String chartType = chartGenRequest.getChartType();

        //校验
        ThrowUtils.throwIf(StringUtils.isAnyBlank(chartName, goal, chartType), ErrorCode.PARAMS_ERROR);

        // todo :文件校验 大小,后缀
        // 校验文件大小
        long maxSize = 10 * 1024 * 1024; // 1MB
        if (multipartFile.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 10MB");
        }

        // 校验文件后缀名
//        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
        String[] allowedExtensions = {"xlsx"};
        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        boolean isValidExtension = false;
        for (String ext : allowedExtensions) {
            if (ext.equals(fileExtension)) {
                isValidExtension = true;
                break;
            }
        }
        if (!isValidExtension) {

            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "不支持的文件类型，仅支持: " + String.join(", ", allowedExtensions));
        }


        Long userId = userService.getLoginUser(request).getId();
        //读取文件 excel ->csv
        String csv = ExcelUtils.excelToCsv(multipartFile);

        //系统prompt
//        String systemPrompt = "你是一个数据分析师，我会给你一些数据，请根据我的要求生成的特定的图表，并且给出总结，不要回复其他任何无关的信息";
        String systemPrompt = "你是数据分析师，我会给你{csv数据}，以及{分析的要求}，请你生成{图形类型}ECharts的配置对象的json代码\n" +
                "例如生成的图表数据\n" +"{\n" +
                "```\n" +
                "  \"xAxis\": {\n" +
                "    \"type\": \"category\",\n" +
                "    \"data\": [\"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\", \"Sat\", \"Sun\"]\n" +
                "  },\n" +
                "  \"yAxis\": {\n" +
                "    \"type\": \"value\"\n" +
                "  },\n" +
                "  \"series\": [\n" +
                "    {\n" +
                "      \"data\": [150, 230, 224, 218, 135, 147, 260],\n" +
                "      \"type\": \"line\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "```\n" +
                "1. 生成符合Echarts 能直接使用的图片代码\n" +
                "2. 生成分析结论\n" +
                "3. 在json串前以及都添加```\n" +
                "4. 不要回复给我其他任何无关的信息包括你的结束语。";
        // 用户prompt
        String userPrompt = "根据csv数据:%s，分析要求:%s,生成一个%s类型的图表,";
        String userPromptFormat = String.format(userPrompt, csv, chartType, goal);

        String string = aiManager.doChat(systemPrompt, userPromptFormat);

        // 拆分代码， 和结果
        String[] split = string.split("```");
        // js 代码
        String genChart = split[1].trim();
        // 分析结果
        String genResult = split[2].trim();

        // 保存到数据库
        Chart chart = new Chart();
        chart.setChartName(chartName);
        chart.setChartType(chartType);
        chart.setGoal(goal);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(userId);

        chart.setChartData(csv);
        chartService.save(chart);


        //调用接口


        return ResultUtils.success(chart);
    }


    /**
     * 模糊查询 图表名称，图表类型，分析目标
     * @param searchText
     * @param request
     * @return
     */
    @GetMapping("/searchTextPage")
    public BaseResponse<Page<Chart>> searchTextPage(String searchText, HttpServletRequest request) {


        User loginUser = userService.getLoginUser(request);
        // 参数校验
        ThrowUtils.throwIf(StringUtils.isBlank(searchText), ErrorCode.PARAMS_ERROR);

        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = loginUser.getId();



        // 调用词条模糊查询
        Page<Chart> result =  chartService.searchText(searchText,userId);
        return ResultUtils.success(result);
    }


    /**
     * 智能分析
     * @param multipartFile
     * @param chartGenRequest
     * @param request
     * @return
     */
    @PostMapping("/genmq")
    public BaseResponse<String> genChartMQByAi(@RequestPart("file") MultipartFile multipartFile,
                                            ChartGenRequest chartGenRequest, HttpServletRequest request) {

        String chartName = chartGenRequest.getChartName();
        String goal = chartGenRequest.getGoal();
        String chartType = chartGenRequest.getChartType();

        //校验
        ThrowUtils.throwIf(StringUtils.isAnyBlank(chartName, goal, chartType), ErrorCode.PARAMS_ERROR);

        // todo :文件校验 大小,后缀
        // 校验文件大小
        long maxSize = 10 * 1024 * 1024; // 1MB
        if (multipartFile.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 10MB");
        }

        // 校验文件后缀名
//        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
        String[] allowedExtensions = {"xlsx"};
        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        boolean isValidExtension = false;
        for (String ext : allowedExtensions) {
            if (ext.equals(fileExtension)) {
                isValidExtension = true;
                break;
            }
        }
        if (!isValidExtension) {

            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "不支持的文件类型，仅支持: " + String.join(", ", allowedExtensions));
        }


        Long userId = userService.getLoginUser(request).getId();
        //读取文件 excel ->csv
        String csv = ExcelUtils.excelToCsv(multipartFile);

        Chart chart = new Chart();


        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartData(csv);
        chart.setChartType(chartType);
        chart.setGenChart("");
        chart.setGenResult("");
        chart.setUserId(userId);
        chartService.save(chart);

        Long id = chart.getId();

        try {
            aiProducer.send(String.valueOf(id));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"AI 服务繁忙");
        }
//        String string = aiManager.doChat(systemPrompt, userPromptFormat);



        //调用接口

        return ResultUtils.success("AI 分析已提交,正在处理中");
    }
}