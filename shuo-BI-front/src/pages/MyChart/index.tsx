import { listChartByPageUsingPost } from '@/services/shuo-bi/chartController'; // 引入接口
import { message, Pagination } from 'antd'; // 引入分页组件
import { createStyles } from 'antd-style';
import ReactECharts from 'echarts-for-react';
import React, { useState, useEffect } from 'react';

// 使用 createStyles 定义样式
const useStyles = createStyles(({ token }) => ({
  container: {
    display: 'flex',
    flexDirection: 'column',
    minHeight: '100vh', // 容器最小高度为视口高度
  },
  resultContainer: {
    flex: 1, // 内容区域占满剩余空间
    padding: '24px',
    display: 'grid', // 使用 Grid 布局
    gridTemplateColumns: 'repeat(2, 1fr)', // 每行显示两个图表
    gap: '16px', // 图表之间的间距
  },
  chartResult: {
    backgroundColor: token.colorBgContainer,
    borderRadius: token.borderRadiusLG,
    boxShadow: token.boxShadow,
    padding: '16px',
  },
  paginationContainer: {
    position: 'sticky', // 分页组件固定在底部
    bottom: 0, // 固定在底部
    left: 0,
    right: 0,
    textAlign: 'center', // 分页组件居中
    padding: '16px',
    backgroundColor: token.colorBgContainer, // 分页组件背景色
    borderTop: `1px solid ${token.colorBorder}`, // 顶部边框
    zIndex: 1, // 确保分页组件在内容上方
  },
}));

const ChartForm: React.FC = () => {
  const { styles } = useStyles();
  const [chartData, setChartData] = useState<API.BaseResponsePageChart_ | undefined>(undefined); // 存储分页数据
  const [currentPage, setCurrentPage] = useState(1); // 当前页码
  const [pageSize, setPageSize] = useState(10); // 每页条数

  // 加载分页数据
  const loadData = async (current: number, pageSize: number) => {
    try {
      // 调用分页查询接口
      const response = await listChartByPageUsingPost({
        current,
        pageSize,
      });

      // 处理响应
      if (response.code === 0) {
        setChartData(response); // 存储分页数据
      } else {
        message.error(`查询失败：${response.message}`);
      }
    } catch (error) {
      console.error('请求出错:', error);
      message.error('查询失败，请重试！');
    }
  };

  // 初始化加载数据
  useEffect(() => {
    loadData(currentPage, pageSize);
  }, [currentPage, pageSize]);

  // 分页改变时的回调
  const handlePageChange = (page: number, pageSize?: number) => {
    setCurrentPage(page);
    if (pageSize) {
      setPageSize(pageSize);
    }
  };

  // 渲染图表数据
  const renderChart = () => {
    if (!chartData?.data?.records) {
      return <p>暂无图表数据</p>;
    }

    return chartData.data.records.map((chart, index) => {
      let chartOption = null;
      try {
        if (chart.genChart) {
          const formattedString = chart.genChart
            .replace(/(\w+)\s*:/g, '"$1":') // 给 key 添加双引号
            .replace(/'([^']+)'/g, '"$1"'); // 替换单引号为双引号

          chartOption = JSON.parse(formattedString);
        }
      } catch (error) {
        console.error('解析 genChart 失败:', error);
        message.error('图表数据解析失败，请检查返回格式！');
      }

      return (
        <div key={index} className={styles.chartResult}>
          <h3>图表 {index + 1}</h3>
          {chartOption ? (
            <ReactECharts
              option={chartOption}
              style={{ height: '300px', width: '100%' }} // 调整图表大小
            />
          ) : (
            <p>暂无图表数据</p>
          )}
        </div>
      );
    });
  };

  return (
    <div className={styles.container}>
      {/* 结果部分 */}
      <div className={styles.resultContainer}>
        {renderChart()}
      </div>

      {/* 分页组件 */}
      <div className={styles.paginationContainer}>
        <Pagination
          current={currentPage}
          pageSize={pageSize}
          total={chartData?.data?.total || 0} // 总条数
          onChange={handlePageChange} // 分页改变回调
          showSizeChanger // 显示每页条数选择器
          onShowSizeChange={(current, size) => setPageSize(size)} // 每页条数改变回调
        />
      </div>
    </div>
  );
};

export default ChartForm;