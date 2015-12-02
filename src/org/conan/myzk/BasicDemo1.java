package org.conan.myzk;

import java.io.IOException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class BasicDemo1 {

    public static void main(String[] args) 
    			throws IOException, KeeperException, InterruptedException {
        /**
         *  创建一个与服务器的连接
         *  参数一：服务器地址和端口号（该端口号值服务器允许客户端连接的端口号，配置文件的默认端口号2181）
         *  参数二：连接会话超时时间
         *  参数三：观察者，连接成功会触发该观察者。不过只会触发一次。
         *  	该Watcher会获取各种事件的通知
         */
        ZooKeeper zk = new ZooKeeper("centos:2181", 60000, new Watcher() {
            // 监控所有被触发的事件
            public void process(WatchedEvent event) {
                System.out.println("监控所有被触发的事件:EVENT:" + event.getType());
            }
        });
        System.out.println("*******************************************************");
        // 查看根节点的子节点
        System.out.println("查看根节点的子节点:ls / => " + zk.getChildren("/", true));
        System.out.println("*******************************************************");
        // 创建一个目录节点
        if (zk.exists("/node", true) == null) {
        	/**
        	 * 参数一：路径地址
        	 * 参数二：想要保存的数据，需要转换成字节数组
        	 * 参数三：ACL访问控制列表（Access control list）,
        	 * 		参数类型为ArrayList<ACL>，Ids接口提供了一些默认的值可以调用。
        	 * 		OPEN_ACL_UNSAFE		This is a completely open ACL 
        	 * 							这是一个完全开放的ACL，不安全
        	 * 		CREATOR_ALL_ACL		This ACL gives the
        	 * 							 creators authentication id's all permissions.
        	 * 							这个ACL赋予那些授权了的用户具备权限
        	 * 		READ_ACL_UNSAFE		This ACL gives the world the ability to read.
        	 * 							这个ACL赋予用户读的权限，也就是获取数据之类的权限。
        	 * 参数四：创建的节点类型。枚举值CreateMode
        	 * 		PERSISTENT (0, false, false)
        	 * 		PERSISTENT_SEQUENTIAL (2, false, true)
        	 * 			这两个类型创建的都是持久型类型节点，回话结束之后不会自动删除。
        	 * 			区别在于，第二个类型所创建的节点名后会有一个单调递增的数值
        	 * 		EPHEMERAL (1, true, false)
        	 * 		EPHEMERAL_SEQUENTIAL (3, true, true)
        	 * 			这两个类型所创建的是临时型类型节点，在回话结束之后，自动删除。
        	 * 			区别在于，第二个类型所创建的临时型节点名后面会有一个单调递增的数值。
        	 * 最后create()方法的返回值是创建的节点的实际路径
        	 */
            zk.create("/node", "conan".getBytes(),
            		Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("创建一个目录节点:create /node conan");
            /**
             *  查看/node节点数据，这里应该输出"conan"
             *  参数一：获取节点的路径
             *  参数二：说明是否需要观察该节点，设置为true，则设定共享默认的观察器
             *  参数三：stat类，保存节点的信息。例如数据版本信息，创建时间，修改时间等信息
             */
            System.out.println("查看/node节点数据:get /node => "
            		+ new String(zk.getData("/node", false, null)));
            /**
             * 查看根节点
             * 在此查看根节点的值，这里应该输出上面所创建的/node节点
             */
            System.out.println("查看根节点:ls / => " + zk.getChildren("/", true));
        }
        System.out.println("*******************************************************");
        // 创建一个子目录节点
        if (zk.exists("/node/sub1", true) == null) {
            zk.create("/node/sub1", "sub1".getBytes(),
            		Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("创建一个子目录节点:create /node/sub1 sub1");
            // 查看node节点
            System.out.println("查看node节点:ls /node => "
            		+ zk.getChildren("/node", true));
        }
        System.out.println("*******************************************************");
        /**
         *  修改节点数据
         *  修改的数据会覆盖上次所设置的数据
         *  setData()方法参数一、参数二不多说，与上面类似。
         *  参数三：数值型。需要传入该界面的数值类型版本号！！！
         *  	该信息可以通过Stat类获取，也可以通过命令行获取。
         *  	如果该值设置为-1，就是忽视版本匹配，直接设置节点保存的值。
         */
        if (zk.exists("/node", true) != null) {
            zk.setData("/node", "changed".getBytes(), -1);
            // 查看/node节点数据
            System.out.println("修改节点数据:get /node => "
            		+ new String(zk.getData("/node", false, null)));
        }
        System.out.println("*******************************************************");
        // 删除节点
        if (zk.exists("/node/sub1", true) != null) {
            zk.delete("/node/sub1", -1);
            zk.delete("/node", -1);
            // 查看根节点
            System.out.println("删除节点:ls / => " + zk.getChildren("/", true));
        }
        // 关闭连接
        zk.close();
    }
}
/**
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:zookeeper.version=3.4.5-1392090, built on 09/30/2012 17:52 GMT
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:host.name=husy
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:java.version=1.8.0_66
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:java.vendor=Oracle Corporation
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:java.home=D:\JRE8
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:java.class.path=D:\Program Files (x86)\ADT\workspace\webhadoop\bin;D:\BaiduYunDownload\hadoop-1.1.2\hadoop-ant-1.1.2.jar;D:\BaiduYunDownload\hadoop-1.1.2\hadoop-client-1.1.2.jar;D:\BaiduYunDownload\hadoop-1.1.2\hadoop-core-1.1.2.jar;D:\BaiduYunDownload\hadoop-1.1.2\hadoop-examples-1.1.2.jar;D:\BaiduYunDownload\hadoop-1.1.2\hadoop-minicluster-1.1.2.jar;D:\BaiduYunDownload\hadoop-1.1.2\hadoop-test-1.1.2.jar;D:\BaiduYunDownload\hadoop-1.1.2\hadoop-tools-1.1.2.jar;D:\BaiduYunDownload\zookeeper-3.4.5\zookeeper-3.4.5.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\commons-logging-1.1.1.jar;D:\BaiduYunDownload\ant.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\commons-configuration-1.6.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\commons-lang-2.4.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\jackson-mapper-asl-1.8.8.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\jackson-core-asl-1.8.8.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\jasper-compiler-5.5.12.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\jasper-runtime-5.5.12.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\commons-io-2.1.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\commons-httpclient-3.0.1.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\slf4j-log4j12-1.4.3.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\slf4j-api-1.4.3.jar;D:\BaiduYunDownload\hadoop-1.1.2\lib\log4j-1.2.15.jar
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:java.library.path=D:\JRE8\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;D:/JRE8/bin/server;D:/JRE8/bin;D:/JRE8/lib/amd64;D:\Python27\;D:\Python27\Scripts;C:\ProgramData\Oracle\Java\javapath;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;D:\JDK8\bin;D:\apache-ant-1.9.6\bin;D:\gradle-2.4\bin;D:\MySQL\mysql-5.6.19-winx64\bin;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;D:\GnuWin32\bin;D:\Program Files (x86)\apache-maven-3.3.9\bin;D:\Program Files (x86)\ADT\adt-bundle-windows-x86_64-20140702\eclipse;;.
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:java.io.tmpdir=C:\Users\15361\AppData\Local\Temp\
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:java.compiler=<NA>
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:os.name=Windows 10
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:os.arch=amd64
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:os.version=10.0
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:user.name=15361
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:user.home=C:\Users\15361
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Client environment:user.dir=D:\Program Files (x86)\ADT\workspace\webhadoop
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Initiating client connection, connectString=centos:2181 sessionTimeout=60000 watcher=org.conan.myzk.BasicDemo1$1@5c29bfd
*******************************************************
15/12/02 19:14:04 INFO zookeeper.ClientCnxn: Opening socket connection to server centos/192.168.56.100:2181. Will not attempt to authenticate using SASL (unknown error)
15/12/02 19:14:04 INFO zookeeper.ClientCnxn: Socket connection established to centos/192.168.56.100:2181, initiating session
15/12/02 19:14:04 INFO zookeeper.ClientCnxn: Session establishment complete on server centos/192.168.56.100:2181, sessionid = 0x51619ccb840001, negotiated timeout = 40000
监控所有被触发的事件:EVENT:None
查看根节点的子节点:ls / => [zookeeper]
*******************************************************
监控所有被触发的事件:EVENT:NodeCreated
监控所有被触发的事件:EVENT:NodeChildrenChanged
创建一个目录节点:create /node conan
查看/node节点数据:get /node => conan
查看根节点:ls / => [node, zookeeper]
*******************************************************
监控所有被触发的事件:EVENT:NodeCreated
创建一个子目录节点:create /node/sub1 sub1
查看node节点:ls /node => [sub1]
*******************************************************
监控所有被触发的事件:EVENT:NodeDataChanged
修改节点数据:get /node => changed
*******************************************************
监控所有被触发的事件:EVENT:NodeDeleted
监控所有被触发的事件:EVENT:NodeChildrenChanged
监控所有被触发的事件:EVENT:NodeChildrenChanged
删除节点:ls / => [zookeeper]
15/12/02 19:14:04 INFO zookeeper.ZooKeeper: Session: 0x51619ccb840001 closed
15/12/02 19:14:04 INFO zookeeper.ClientCnxn: EventThread shut down
 */