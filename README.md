# Bus

#### 项目介绍

支持淄博、莱芜长运、荣成、泰安、招远、潍坊城市单票制

#### 软件功能

查询记录  <br />
汇总 <br />
日志导出 <br />
数据库导出 <br />
校准时间  <br />
更新参数  <br />
检查网络状态  <br />
记录按照日期导出  <br />
参数查询  <br />

#### 安装教程

1. adb install 路径
2. adb shell am start -n com.szxb.buspay/com.szxb.buspay.module.init.InitActivity
3. adb uninstall com.szxb.buspay

#### 使用说明

1. 
2. 
3. 

#### 如何集成

1. 依赖 compile 'com.github.wuxinxi:unionpay:1.1.0'  <br />
2.初始化
 ` UnionPayManager unionPayManager = new UnionPayManager();
   BusllPosManage.init(unionPayManager);
`

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
