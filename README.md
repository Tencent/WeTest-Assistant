1.WeTest助手是什么？

   WeTest助手是由WeTest（质量开放平台http://wetest.qq.com）自主研发的基于手机端的辅助测试工具，目前包括性能测试和远程调试两大功能，
   能够为手游等项目发现CPU、内存、FPS等性能问题，并提供云端真机用于问题在线调试，共计为公司内外部项目服务5.4万次；

   手游客户端性能测试常用性能维度，CPU，内存，FPS，流量一次性全部收集，图表化展示，数据可按场景化分类，并且支持离线和在线两种模式，
   地铁上都可以做测试，Web上看报告。远程调试配合自研的云真机技术，支持多点触控、类手柄遥控，真实还原手游测试场景，极速流畅、极低延迟，
   本地只需要一台手机即可操控云端任何一台手机。

   支持ROOT和非ROOT安卓手机，支持越狱iOS系统。
   
2.功能清单

   Android（2.3 - 6.0）：
   a.手游性能数据采集记录（CPU，内存，流量，FPS）
   b.支持异步和实时数据同步
   c.支持场景标签和分场景统计
   d.自动输出测试报告

   iOS（iOS7以上，需要越狱）：
   a.手游性能数据采集记录（CPU，内存，流量）
   b.支持异步数据同步
   c.自动输出测试报告

   完整版体验地址：http://wetest.qq.com/cloud/index.php/phone/step1?test=effective

   WeTest平台另外还提供标准兼容测试、自动探索云测试、弱网络测试、耗电量测试、安全测试等一站式测试服务，自动输出测试报告；提供舆情监控、
   舆情分析、案例输出等全流程舆情服务，为产品口碑运营保驾护航。
   WeTest质量开放平台出品，欢迎体验：http://wetest.qq.com
   
3.如何使用wetest助手?
   
   WeTest助手分IOS 和 Android两个版本，使用方法相同：
  
   首先，你需要编译并安装wetest助手到你的手机上 ， 然后

   a.启动wetest助手
   b.选择目标应用
   c.点击开始测试按钮
   d.查看测试结果

4.如何编译?

	a.Android版本
	使用Gradle + Android Studio 

	目前只支持使用Gradle构建，直接项目目录运行./gradlew clean build即可，
	(Windows用户使用 gradlew.bat clean build)
	也可以直接使用Android Studio打开项目根目录的build.gradle  

	b.IOS版本

	目前只支持sdk 7  和 sdk 8 上编译。使用xcode打开工程文件编译后，由于
	该工具需要task_for_pid权限 ，因此需对项目进行签名。

	签名方式可参考该越狱开发文档http://kuangqi.me/ios/ios-development-without-idp/
	签名脚本gen_entitlements.py 已放到项目根目录下 ，编译时放到脚本的对应目录即可。

	deb打包
	编译好后，获取wetest助手的执行文件WeTest.app , 使用dpkg打包,打包命令:
	dpkg-deb -b deb目录 WetestForIOS.deb

	deb的签名文件已放到项目根目录下 ，需根据生成的WeTest.app文件自行修改。

5.未来规划： 

   未来WeTest助手计划获取日志，将日志和性能数据关联起来。 

   也会考虑接入针对unity引擎的性能数据分析。并提供针对unity引擎游戏的优化建议。 

   考虑在不影响数据采集的情况下为用户提供测试过程的截图，显示在性能数据曲线至上，方便用户定位性能异常发生在游戏的哪个场景