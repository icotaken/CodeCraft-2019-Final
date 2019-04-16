# CodeCraft-2019-Final
2019年华为软件精英挑战赛源代码，采用Floyd、模拟退火等算法，可调式单时间片发车数目。<br>
## 使用指南
1. TrainningMap,ExamMap分别对应训练赛地图以及正赛地图，地图主要包含car,cross,road三类文件，文件中数据格式的定义在CodeCraft-2019-TaskBook文档中有提及，两类地图的主要区别体现在车辆数目、路口标号（正赛中路口标号是无序的）；<br>
2. 解压CodeCraft-2019.rar文件，并在IDEA环境下打开（源码中需要改变地图文件的存储路径）；<br>
3. History.txt中记录了各个版本的主要改动内容，大概涵盖了我们小分队在半个月内的软挑思路变化（如果需要其它的历史版本可以私聊我qq：541374715）；<br>
4. 我们学校在江山赛区，单张地图的初赛成绩大概在2200左右，这个分数勉强能搭上32强的末班车。<br>
## 2019年华为软挑经验汇总
在去年的“华为杯”数学建模比赛失利后，我开始关注华为软件精英挑战赛。期间在GitHub, CSDN等一些网站上了解到前几届华为软挑的赛题形式、题目内容和大佬们的比赛心得，本着“从别人的经历中学习成功经验”的态度，花了些时间去学习像模拟退火、遗传算法等一些高频算法，事实证明这在后期给我们提供了很多的思路。<br>
### 团队建设
华为软挑是团队竞赛，要求至多三人一组，编程语言限定在C/C++, Java, Python。我主学的Java，只是当时实验室的很多小伙伴去出差了，最后和两位分别搞C++和Python的同门组了一队，“劈里啪啦”，寓意代码敲的飞起，哈哈。俗话说“三人同心，其利断金”，单打独斗总是很孤独的，如果能和志同道合的队友集思广益，这段旅程会更加amazing。同时建议大家早些关注大赛，敲定队友，当然大家最好是主攻同一门语言，这确实能很大程度上提升后期的代码优化速度，降低了很多比赛风险。
### 2019年软挑的赛程安排
初赛的题目是在3月8号放出来的，3月15号软挑官网放出了两张训练地图，3月23号左右官网又放出了第三、第四张地图，3月30号晚10点关闭训练赛的跑分通道（到3月30号晚10点，应该说基本结束了训练赛的角逐，开始为3月31日的初赛正赛做铺垫），初赛正赛的比赛时间为3月31号09：00~17：00，总计8个小时。<br>
3月8号~3月15号，这段时间华为已经公布了任务书和地图模板，其实这给了我们很长的组建队伍和分析题目的时间。事后想想，我们没能很好的利用这段时间crying。<br>
### 心路——代码从无都有
