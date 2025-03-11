import React from 'react';
import { ProForm, ProFormText, ProFormSelect, ProFormUploadButton } from '@ant-design/pro-components';
import { message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { createStyles } from 'antd-style';
import { genChartByAiUsingPost } from '@/services/shuo-bi/chartController';


// 使用 createStyles 定义样式
const useStyles = createStyles(({ token }) => ({
  formContainer: {
    width: '50%', // 限制表单宽度为屏幕的一半
    float: 'left', // 靠左对齐
    padding: '24px', // 添加内边距
    backgroundColor: token.colorBgContainer, // 使用主题背景色
    borderRadius: token.borderRadiusLG, // 添加圆角
    boxShadow: token.boxShadow, // 添加阴影
  },
}));

const ChartForm: React.FC = () => {
  const { styles } = useStyles();

  // 表单提交处理函数
  const handleSubmit = async (values: any) => {
    console.log('表单数据:', values);

    // 构造请求参数
    const params: API.genChartByAiUsingPOSTParams = {
      goal: values.analysisTarget, // 分析目标
      chartType: values.chartType, // 图表类型
      chartName: values.chartName, // 如果需要，可以从表单中获取
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
      } else {
        message.error(`分析失败：${response.message}`);
      }
    } catch (error) {
      console.error('请求出错:', error);
      message.error('分析失败，请重试！');
    }
  };

  return (
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
  );
};

export default ChartForm;