package org.conan.myhadoop.mr;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

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
 * 单词统计MapReduce
 * 我的博客地址：http://blog.csdn.net/u010156024/article/details/50117659
 */
public class WordCount {
	/**
	 * Mapper类
	 */
    public static class WordCountMapper extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        /**
         * map方法完成工作就是读取文件
         * 将文件中每个单词作为key键，值设置为1，
         * 然后将此键值对设置为map的输出，即reduce的输入
         */
        @Override
        public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            /**
             * StringTokenizer：字符串分隔解析类型
             * 之前没有发现竟然有这么好用的工具类
             * java.util.StringTokenizer
             * 1. StringTokenizer(String str) ：
             * 	构造一个用来解析str的StringTokenizer对象。
             * 	java默认的分隔符是“空格”、“制表符(‘\t’)”、“换行符(‘\n’)”、“回车符(‘\r’)”。
             * 2. StringTokenizer(String str, String delim) ：
             * 	构造一个用来解析str的StringTokenizer对象，并提供一个指定的分隔符。
             * 3. StringTokenizer(String str, String delim, boolean returnDelims) ：
             * 	构造一个用来解析str的StringTokenizer对象，并提供一个指定的分隔符，同时，指定是否返回分隔符。
             * 
             * 默认情况下，java默认的分隔符是“空格”、“制表符(‘\t’)”、“换行符(‘\n’)”、“回车符(‘\r’)”。
             */
        	StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                output.collect(word, one);
            }
        }
    }
    /**
     * reduce的输入即是map的输出，将相同键的单词的值进行统计累加
     * 即可得出单词的统计个数，最后把单词作为键，单词的个数作为值，
     * 输出到设置的输出文件中保存
     */
    public static class WordCountReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
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
        //数据输入路径     这里的路径需要换成自己的hadoop所在地址
    	String input = "hdfs://centos:9000/words.txt";
        /**
         * 输出路径设置为HDFS的根目录下的out文件夹下
         * 注意：该文件夹不应该存在，否则出错
         */
        String output = "hdfs://centos:9000/out";

        JobConf conf = new JobConf(WordCount.class);
        conf.setJobName("WordCount");
//        conf.addResource("classpath:/hadoop/core-site.xml");
//        conf.addResource("classpath:/hadoop/hdfs-site.xml");
//        conf.addResource("classpath:/hadoop/mapred-site.xml");
        //对应单词字符串
        conf.setOutputKeyClass(Text.class);
        //对应单词的统计个数 int类型
        conf.setOutputValueClass(IntWritable.class);
        //设置mapper类
        conf.setMapperClass(WordCountMapper.class);
        /**
         * 设置合并函数，合并函数的输出作为Reducer的输入，
         * 提高性能，能有效的降低map和reduce之间数据传输量。
         * 但是合并函数不能滥用。需要结合具体的业务。
         * 由于本次应用是统计单词个数，所以使用合并函数不会对结果或者说
         * 业务逻辑结果产生影响。
         * 当对于结果产生影响的时候，是不能使用合并函数的。
         * 例如：我们统计单词出现的平均值的业务逻辑时，就不能使用合并
         * 函数。此时如果使用，会影响最终的结果。
         */
        conf.setCombinerClass(WordCountReducer.class);
        //设置reduce类
        conf.setReducerClass(WordCountReducer.class);
        /**
         * 设置输入格式，TextInputFormat是默认的输入格式
         * 这里可以不写这句代码。
         * 它产生的键类型是LongWritable类型（代表文件中每行中开始的偏移量值）
         * 它的值类型是Text类型（文本类型）
         */
        conf.setInputFormat(TextInputFormat.class);
        /**
         * 设置输出格式，TextOutpuTFormat是默认的输出格式
         * 每条记录写为文本行，它的键和值可以是任意类型，输出回调用toString()
         * 输出字符串写入文本中。默认键和值使用制表符进行分割。
         */
        conf.setOutputFormat(TextOutputFormat.class);
        //设置输入数据文件路径
        FileInputFormat.setInputPaths(conf, new Path(input));
        //设置输出数据文件路径（该路径不能存在，否则异常）
        FileOutputFormat.setOutputPath(conf, new Path(output));
        //启动mapreduce
        JobClient.runJob(conf);
        System.exit(0);
    }

}
