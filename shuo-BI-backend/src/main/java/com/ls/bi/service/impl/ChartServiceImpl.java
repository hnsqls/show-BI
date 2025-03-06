package com.ls.bi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.bi.model.entity.Chart;
import com.ls.bi.service.ChartService;
import com.ls.bi.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author 26611
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2025-03-06 14:56:00
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




