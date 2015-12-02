package org.conan.myhadoop.mr.kpi;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
/**
 * 页面访问量统计MapReduce
 * 统计每个页面的访问次数
 * 完成的功能类似于单词统计：
 * 以每个访问网页的资源路径为键，经过mapreduce任务，
 * 最终得到每一个资源路径的访问次数
 */
public class KPIPV { 

    public static class KPIPVMapper extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {
        private IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            KPI kpi = KPI.filterPVs(value.toString());
            if (kpi.isValid()) {
            	/**
            	 * 以页面的资源路径为键，每访问一次，值为1
            	 * 作为map任务的输出
            	 */
                word.set(kpi.getRequest());
                output.collect(word, one);
            }
        }
    }

    public static class KPIPVReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            result.set(sum);
            output.collect(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
    	/**
    	 * 对应于HDFS中文件所在的位置路径
    	 */
        String input = "hdfs://centos:9000/access.log.10";
        /**
         * 对应HDFS中的输出文件。文件不能事先存在。否则出错
         */
        String output = "hdfs://centos:9000/out_kpipv";

        JobConf conf = new JobConf(KPIPV.class);
        conf.setJobName("KPIPV");
//        conf.addResource("classpath:/hadoop/core-site.xml");
//        conf.addResource("classpath:/hadoop/hdfs-site.xml");
//        conf.addResource("classpath:/hadoop/mapred-site.xml");
        //设置map输出的键类型，对应于map的输出以资源路径字符串作为键
        conf.setMapOutputKeyClass(Text.class);
        //设置map输出的值类型，对应于map输出，值为1
        conf.setMapOutputValueClass(IntWritable.class);
        //设置reduce的输出键类型，和map相同
        conf.setOutputKeyClass(Text.class);
        //设置reduce的输出值类型，和map相同，不过是累加的值
        conf.setOutputValueClass(IntWritable.class);
        //设置map类
        conf.setMapperClass(KPIPVMapper.class);
        //设置合并函数，该合并函数和reduce完成相同的功能，提升性能，减少map和reduce之间数据传输量
        conf.setCombinerClass(KPIPVReducer.class);
        //设置reduce类
        conf.setReducerClass(KPIPVReducer.class);
        //设置输入文件类型，默认TextInputFormat，该行代码可省略
        conf.setInputFormat(TextInputFormat.class);
        //设置输出文件类型，默认TextOutputFormat,该行代码可省略
        conf.setOutputFormat(TextOutputFormat.class);
        //设置输入文件路径
        FileInputFormat.setInputPaths(conf, new Path(input));
        //设置输出文件路径。该路径不能存在，否则出错！！！
        FileOutputFormat.setOutputPath(conf, new Path(output));
        //运行启动任务
        JobClient.runJob(conf);
        System.exit(0);
    }

}
