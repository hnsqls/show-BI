package com.ls.bi.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 工具类
 */
@Slf4j
public class ExcelUtils {

    /**
     * excel 转csv
     * @param multiPartFile
     * @return
     */

    public  static String excelToCsv(MultipartFile multiPartFile){

//        File file = null;
//        try {
//             file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        //读 excel
        List<Map<Integer,String>> list = null;
        try {
            list = EasyExcel.read(multiPartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("excel 转csv 失败",e);
            throw new RuntimeException(e);
        }


        //数据为 空
        if (list == null || list.size() == 0){
            return "";
        }

        //数据转csv
        StringBuilder stringBuilder = new StringBuilder();
        //表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap)list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
//        System.out.println(StringUtils.join(headerList,","));
        stringBuilder.append(StringUtils.join(headerList,",")).append("\n");

        //读取每一行
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap)list.get(i);
            List<String> dateList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
//            System.out.println(StringUtils.join(dateList,","));
            stringBuilder.append(StringUtils.join(dateList,",")).append("\n");

        }

        return stringBuilder.toString();

    }

    public static void main(String[] args) {
        excelToCsv(null);
    }
}
