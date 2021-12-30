<p align="center">
	<img alt="logo" src="https://opensca-test.xmirror.cn/static/media/OpenSCAlogo.e980a0f9.svg">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">OpenSCA Xcheck</h1>
<h4 align="center">IntelliJ平台的OpenSCA Xcheck插件，让代码更安全</h4>
<p align="center">
	<a href="https://github.com/XmirrorSecurity/OpenSCA-intellij-plugin/blob/master/LICENSE"><img src="https://img.shields.io/github/license/XmirrorSecurity/OpenSCA-intellij-plugin?style=flat-square"></a>
	<a href="https://github.com/XmirrorSecurity/OpenSCA-intellij-plugin/releases"><img src="https://img.shields.io/github/v/release/XmirrorSecurity/OpenSCA-intellij-plugin?style=flat-square"></a>
</p>



---

## 项目介绍

[Xcheck](https://plugins.jetbrains.com/plugin/18246-opensca-xcheck )是基于IntelliJ平台的OpenSCA插件。Xcheck能对当前项目进行代码质量评估，并在可视化界面中展示评估结果。评估结果包括漏洞和有漏洞的组件的统计数、具体组件信息和相关漏洞信息。

## 安装插件

**安装方法一**：在[适配的IDE](https://plugins.jetbrains.com/plugin/18246-opensca-xcheck#:~:text=Code%20tools%2C%20Security-,Product%20Compatibility,-Determined%20by%20plugin )中通过插件市场安装（推荐）

以IntelliJ IDEA为例：在IDE中依次点击“File|Settings|Plugins|Marketplace”，在搜索框中输入“OpenSCA Xcheck”，点击“Install”

<img src="docs/media/xcheck_marketplace.jpg" alt="xcheck_market" />

**安装方法二**：在[OpenSCA平台](https://opensca.xmirror.cn/pages/plug-in )下载插件安装

以IntelliJ IDEA为例：将下载下来的插件安装包拖入适配的IDE中即可

**安装方法三**：[下载源码](https://github.com/Xmirror-DevSecOps/OpenSCA-intellij-plugin )自行编译安装

使用IntelliJ IDEA打开下载到本地的源码，需要配置运行环境：`jDK11`，待Gradle导入依赖和插件，在Gradle中执行`intellij`插件的`buildPlugin`任务，构建的安装包存放于当前项目下*build/distributions*目录下，将此目录下的安装包拖入当前IDE中即可

## 使用插件

### 插件功能

- 配置：点击File|Settings|Other Settings|OpenSCA Setting或点击OpenSCA窗口中的`Setting`按钮，在配置界面中配置连接服务器Url和Token
- 测试连接：在OpenSCA配置界面中，配置服务器Url和Token之后点击`测试连接`按钮可验证Url和Token是否有效
- 运行：点击OpenSCA窗口中的`Run`按钮，可对当前项目进行代码评估
- 停止：如果正在对当前项目代码评估，那么`Stop`按钮是可用的，点击Stop按钮可结束当前评估任务
- 清除：如果OpenSCA窗口中的Xcheck子窗口已有评估结果，点击`Clean`按钮可清除Xcheck子窗口中所有结果

<img src="docs/media/xcheck_function.jpg" alt="xcheck_function" />

### 插件执行流程

<img src="docs/media/xcheck_process.jpg" alt="xcheck流程图"  />

### 使用插件

点击View|Tool Windows|OpenSCA可打开OpenSCA窗口。首先在OpenSCA配置界面中配置服务器参数（参考：插件功能-配置），然后在OpenSCA窗口中点击“运行”（参考：插件功能-运行）

## 友情链接

[悬镜官网](https://www.xmirror.cn/), [OpenSCA官网](https://opensca.xmirror.cn)

