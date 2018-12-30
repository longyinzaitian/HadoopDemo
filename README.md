# HadoopDemo
Hadoop简单应用案例，包括MapReduce、单词统计、HDFS基本操作、web日志分析、Zookeeper基本使用、Hive简单操作等    
  
------------------------------
### 运行环境：  
java 1.8  
hadoop1.1.2  
zookeeper3.4.5  
采用伪分布模式下eclipse工具进行开发的java project。    
  
  
------------------------------
>org.conan.myhadoop.hdfs    

该包下是HDFS类的基本操作。包括文件的创建，复制，删除， 查看数据，文件重命名，从hdfs下载文件到本地系统等  
请参考我的博客：[Hadoop-利用java API操作HDFS文件](http://blog.csdn.net/u010156024/article/details/50113273)  
  
  
------------------------------
>org.conan.myhadoop.mr  

该包下是一个单词统计的MapReduce任务类。  
完成对文件中单词的统计。    
请参看我的博客：[Hadoop-MapReduce初步应用-统计单词个数](http://blog.csdn.net/u010156024/article/details/50117659)  
  
  
------------------------------
>org.conan.myhadoop.mr.kpi  

该包下是一个web日志的分析的四个MapReduce任务类。  
完成对一天中[粉丝日志](http://blog.fens.me/)网站的web日志的分析。包括独立IP统计、资源访问次数统计、每小时访问量统计、客户端类型统计。  
请参看我的博客：[Hadoop-web日志信息挖掘MapReduce简单应用](http://blog.csdn.net/u010156024/article/details/50147697)  
  
  
------------------------------
>org.conan.myzk  

该包下是对zookeeper工具的简单使用。   
需要安装zookeeper。代码中使用的是zookeeper3.4.5版本。  
请查看我的博客：[Zookeeper命令行以及java API简单使用](http://blog.csdn.net/u010156024/article/details/50151029)  
  
  
------------------------------  
>org.longyin.myhadoo.hive  

该包下是Hive的简单示例。  
代码中使用的Hive版本是0.9.0  
请参看我的博客：[Hive-命令行基本操作和java API简单操作](http://blog.csdn.net/u010156024/article/details/50165385)  
  

------------------------------  
>source目录    
  
source目录内是项目中使用到的资源文件      
  
------------------------------  
最后感谢代码原创作者@bsspirit。    
原项目地址：请点击[这里](https://github.com/bsspirit/maven_hadoop_template)    
原项目采用eclipse+Maven构建的。我在原有基础上没有使用Maven。使用eclipse构建。    
  
###### 提示：
各位在学习代码时，需要把代码中相关的配置信息改成自己对应的配置信息。例如主机地址、端口号等。  
