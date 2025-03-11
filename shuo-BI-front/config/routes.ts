export default [
  { name:'登录',path: '/user', layout: false, routes: [
    { path: '/user/login', component: './User/Login' },
    {
      path: '/user/register',
      component: './User/register', // 确保大小写与实际文件路径一致
    },
  ] },
  
  
  { name:'欢迎页面',path: '/welcome', icon: 'smile', component: './Welcome' },
  { name:'智能分析页面',path: '/addChart', icon: 'smile', component: './Chart' },
  {
    path: '/admin',
    icon: 'crown',
    access: 'canAdmin',
    name:"管理员页面",
    routes: [
      { path: '/admin', redirect: '/admin/sub-page' },
      { path: '/admin/sub-page', component: './Admin' },
    ],
  },
  { icon: 'table', path: '/list', component: './TableList',name:'表格页' },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
