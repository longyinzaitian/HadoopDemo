package org.conan.myhadoop.hdfs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapred.JobConf;

/**
 * 利用HDFS java API操作文件
 * 我的博客：http://blog.csdn.net/u010156024/article/details/50113273
 */
public class HdfsDAO {
	//修改成自己的HDFS主机地址
	private static final String HDFS = "hdfs://centos:9000/";
	/**
	 * 两个构造器
	 * @param conf
	 */
	public HdfsDAO(Configuration conf) {
		this(HDFS, conf);
	}

	public HdfsDAO(String hdfs, Configuration conf) {
		this.hdfsPath = hdfs;
		this.conf = conf;
	}

	private String hdfsPath;
	private Configuration conf;
	/**
	 * 测试方法入口
	 */
	public static void main(String[] args) throws IOException {
		JobConf conf = config();
		HdfsDAO hdfs = new HdfsDAO(conf);
		/**
		 * 测试拷贝文件
		 */
//		hdfs.copyFile("datafile/item.csv", "/tmp/new");
		/**
		 * 测试遍历文件目录
		 */
//		hdfs.ls("/out");
		/**
		 * 测试重命名文件
		 */
//		hdfs.rename("/out.txt", "/out");
		/**
		 * 测试获取给定文件的主机名，偏移量，大小
		 */
		hdfs.location();
	}

	public static JobConf config() {
		JobConf conf = new JobConf();
		conf.setJobName("HdfsDAO");
//		conf.addResource("classpath:/hadoop/core-site.xml");
//		conf.addResource("classpath:/hadoop/hdfs-site.xml");
//		conf.addResource("classpath:/hadoop/mapred-site.xml");
		return conf;
	}
	/**
	 * 创建目录
	 * @param folder
	 * @throws IOException
	 */
	public void mkdirs(String folder) throws IOException {
		Path path = new Path(folder);
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		if (!fs.exists(path)) {
			fs.mkdirs(path);
			System.out.println("Create: " + folder);
		}
		fs.close();
	}
	/**
	 * 删除文件或目录
	 * @param folder
	 * @throws IOException
	 */
	public void rmr(String folder) throws IOException {
		Path path = new Path(folder);
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		fs.deleteOnExit(path);
		System.out.println("Delete: " + folder);
		fs.close();
	}
	/**
	 * 重命名文件
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public void rename(String src, String dst) throws IOException {
		Path name1 = new Path(src);
		Path name2 = new Path(dst);
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		fs.rename(name1, name2);
		System.out.println("Rename: from " + src + " to " + dst);
		fs.close();
	}
	/**
	 * 遍历文件
	 * @param folder
	 * @throws IOException
	 */
	public void ls(String folder) throws IOException {
		Path path = new Path(folder);
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		FileStatus[] list = fs.listStatus(path);
		System.out.println("ls: " + folder);
		System.out
				.println("==========================================================");
		for (FileStatus f : list) {
			System.out.printf("name: %s, folder: %s, size: %d\n", f.getPath(),
					f.isDir(), f.getLen());
		}
		System.out
				.println("==========================================================");
		fs.close();
	}
	/**
	 * 创建文件
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public void createFile(String file, String content) throws IOException {
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		byte[] buff = content.getBytes();
		FSDataOutputStream os = null;
		try {
			os = fs.create(new Path(file));
			os.write(buff, 0, buff.length);
			System.out.println("Create: " + file);
		} finally {
			if (os != null)
				os.close();
		}
		fs.close();
	}
	/**
	 * 拷贝文件到HDFS
	 * @param local
	 * @param remote
	 * @throws IOException
	 */
	public void copyFile(String local, String remote) throws IOException {
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		fs.copyFromLocalFile(new Path(local), new Path(remote));
		System.out.println("copy from: " + local + " to " + remote);
		fs.close();
	}
	/**
	 * 从HDFS中下载文件到本地中
	 * @param remote
	 * @param local
	 * @throws IOException
	 */
	public void download(String remote, String local) throws IOException {
		Path path = new Path(remote);
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		fs.copyToLocalFile(path, new Path(local));
		System.out.println("download: from" + remote + " to " + local);
		fs.close();
	}
	/**
	 * 查看文件中的内容
	 * @param remoteFile
	 * @return
	 * @throws IOException
	 */
	public String cat(String remoteFile) throws IOException {
		Path path = new Path(remoteFile);
		FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
		FSDataInputStream fsdis = null;
		System.out.println("cat: " + remoteFile);

		OutputStream baos = new ByteArrayOutputStream();
		String str = null;
		try {
			fsdis = fs.open(path);
			IOUtils.copyBytes(fsdis, baos, 4096, false);
			str = baos.toString();
		} finally {
			IOUtils.closeStream(fsdis);
			fs.close();
		}
		System.out.println(str);
		return str;
	}
	//返回给定文件的位置
	/**
	 * Return an array containing hostnames, offset and size of 
	 * portions of the given file.
	 */
	public void location() throws IOException {
		String folder = hdfsPath + "/";
		String file = "sample.txt";
		FileSystem fs = FileSystem.get(URI.create(hdfsPath),
				new Configuration());
		FileStatus f = fs.getFileStatus(new Path(folder + file));
		BlockLocation[] list = fs.getFileBlockLocations(f, 0, f.getLen());

		System.out.println("File Location: " + folder + file);
		for (BlockLocation bl : list) {
			String[] hosts = bl.getHosts();
			for (String host : hosts) {
				System.out.println("host:" + host);
			}
		}
		fs.close();
	}
}
