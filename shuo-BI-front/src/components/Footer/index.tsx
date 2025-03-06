import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'Show AI + BI',
          title: 'Show AI + BI',
          href: 'https://github.com/hnsqls/show-BI',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/hnsqls/show-BI',
          blankTarget: true,
        },
        {
          key: 'Show AI + BI',
          title: 'Show AI + BI',
          href: 'https://github.com/hnsqls/show-BI',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
