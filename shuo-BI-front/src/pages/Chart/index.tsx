import { genChartByAiUsingPost } from '@/services/shuo-bi/chartController';
import { UploadOutlined } from '@ant-design/icons';
import {
  ProForm,
  ProFormSelect,
  ProFormText,
  ProFormUploadButton,
} from '@ant-design/pro-components';
import { message } from 'antd';
import { createStyles } from 'antd-style';
import ReactECharts from 'echarts-for-react';
import React, { useState } from 'react';

// 使用 createStyles 定义样式
const useStyles = createStyles(({ token }) => ({
  container: {
    display: 'flex',
    height: '100vh',
  },
  formContainer: {
    width: '50%', // 表单占左边一半
    padding: '24px',
    backgroundColor: token.colorBgContainer,
    borderRadius: token.borderRadiusLG,
    boxShadow: token.boxShadow,
  },
  resultContainer: {
    width: '50%', // 结果占右边一半
    display: 'flex',
    flexDirection: 'column',
    padding: '24px',
  },
  chartResult: {
    flex: 1, // 图表结果占一半高度
    backgroundColor: token.colorBgContainer,
    borderRadius: token.borderRadiusLG,
    boxShadow: token.boxShadow,
    padding: '16px',
    marginBottom: '16px', // 与下方分析结果间隔
  },
  analysisResult: {
    flex: 1, // 分析结果占一半高度
    backgroundColor: token.colorBgContainer,
    borderRadius: token.borderRadiusLG,
    boxShadow: token.boxShadow,
    padding: '16px',
  },
}));

const ChartForm: React.FC = () => {
  const { styles } = useStyles();
  const [chartData, setChartData] = useState<API.BaseResponseChart_ | undefined>(undefined); // 用于存储返回的 data

  // 表单提交处理函数
  const handleSubmit = async (values: any) => {
    console.log('表单数据:', values);

    // 构造请求参数
    const params: API.genChartByAiUsingPOSTParams = {
      goal: values.analysisTarget, // 分析目标
      chartType: values.chartType, // 图表类型
      chartName: values.chartName, // 图表名称
    };

    // 获取文件
    const file = values.file && values.file.length > 0 ? values.file[0].originFileObj : undefined;
    try {
      // 调用接口
      const response = await genChartByAiUsingPost(params, {}, file);

      // 处理响应
      if (response.code === 0) {
        message.success('分析成功！');
        console.log('服务器响应:', response.data);

        // 提取 data 部分并存储到状态中
        setChartData(response);

        // 重点：打印 genChart 的内容
  console.log('genChart 数据:', response.data?.genChart);

        // 在这里可以使用 response.data 进行进一步处理
        if (response.data) {
          console.log('提取的 data:', response.data);
        }
      } else {
        message.error(`分析失败：${response.message}`);
      }
    } catch (error) {
      console.error('请求出错:', error);
      message.error('分析失败，请重试！');
    }
  };

  // 将 genChart 字符串解析为 JSON 对象
  let chartOption = null;
  try {
    if (chartData?.data?.genChart) {
      const formattedString = chartData.data.genChart
        .replace(/(\w+)\s*:/g, '"$1":') // 给 key 添加双引号
        .replace(/'([^']+)'/g, '"$1"'); // 替换单引号为双引号
  
      chartOption = JSON.parse(formattedString);
    }
  } catch (error) {
    console.error("解析 genChart 失败:", error);
    message.error("图表数据解析失败，请检查返回格式！");
  }
  return (
    <div className={styles.container}>
      {/* 表单部分 */}
      <div className={styles.formContainer}>
        <ProForm
          onFinish={handleSubmit}
          initialValues={{
            chartType: 'pie', // 默认图表类型
          }}
        >
          {/* 分析目标输入框 */}
          <ProFormText
            name="analysisTarget"
            label="分析目标"
            placeholder="请输入分析目标"
            rules={[{ required: true, message: '分析目标不能为空！' }]}
          />

          {/* 图表名称 */}
          <ProFormText
            name="chartName"
            label="图表名称"
            placeholder="请输入生成图表的名称"
            rules={[{ required: true, message: '图表名称不能为空！' }]}
          />

          {/* 图表类型下拉框 */}
          <ProFormSelect
            name="chartType"
            label="图表类型"
            placeholder="请选择图表类型"
            options={[
              { label: '饼图', value: 'pie' },
              { label: '柱状图', value: 'bar' },
              { label: '折线图', value: 'line' },
              { label: '散点图', value: 'scatter' },
            ]}
            rules={[{ required: true, message: '请选择图表类型！' }]}
          />

          {/* 文件上传 */}
          <ProFormUploadButton
            name="file"
            label="上传文件"
            max={1} // 最多上传 1 个文件
            icon={<UploadOutlined />}
            rules={[{ required: true, message: '请上传文件！' }]}
            fieldProps={{
              beforeUpload: () => false, // 阻止默认上传行为
            }}
          />
        </ProForm>
      </div>
      {/* 结果部分 */}
      <div className={styles.resultContainer}>
        {/* AI 生成图表 */}
        <div className={styles.chartResult}>
          <h3>AI 生成图表</h3>
          {chartOption ? (
            <ReactECharts option={chartOption} />
          ) : (
            <p>暂无图表数据</p>
          )}
        </div>

        {/* AI 分析结果 */}
        <div className={styles.analysisResult}>
          <h3>AI 分析结果</h3>
          <pre>{chartData?.data?.genResult}</pre>
        </div>
      </div>
    </div>
  );
};

export default ChartForm;