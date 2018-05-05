package com.zookeeper;

import java.io.IOException;  
import java.util.ArrayList;  
import java.util.Random;  
  
import org.apache.zookeeper.CreateMode;  
import org.apache.zookeeper.KeeperException;  
import org.apache.zookeeper.WatchedEvent;  
import org.apache.zookeeper.Watcher;  
import org.apache.zookeeper.ZooDefs.Ids;  
import org.apache.zookeeper.ZooKeeper;  
  
public class TestZooKeeperDistributeLock {  
  
    // 是否为可重入锁  
    private boolean reentrant = false;  
  
    public boolean isReentrant() {  
        return reentrant;  
    }  
  
    private ZooKeeper zk = null;  
  
    public ZooKeeper getZk() {  
        return zk;  
    }  
  
    public TestZooKeeperDistributeLock(boolean reentrant) {  
  
        this.reentrant = reentrant;  
  
        // 初始化环境：连接Zookeeper并创建根目录  
        init();  
    }  
  
    private void init() {  
        try {  
            System.out.println("...");  
            System.out.println("...");  
            System.out.println("...");  
            System.out.println("...");  
  
            System.out.println("开始连接ZooKeeper...");  
  
            // 创建与ZooKeeper服务器的连接zk  
            String address = "192.168.171.131:2181";  
            int sessionTimeout = 3000;  
            zk = new ZooKeeper(address, sessionTimeout, new Watcher() {  
                // 监控所有被触发的事件  
                public void process(WatchedEvent event) {  
                    if (event.getType() == null || "".equals(event.getType())) {  
                        return;  
                    }  
                    System.out.println("已经触发了" + event.getType() + "事件！");  
                }  
            });  
  
            System.out.println("ZooKeeper连接创建成功！");  
  
            Thread.currentThread().sleep(1000l);  
  
            System.out.println("...");  
            System.out.println("...");  
            System.out.println("...");  
            System.out.println("...");  
  
            // 创建根目录节点  
            // 路径为/tmp_root_path  
            // 节点内容为字符串"我是根目录/tmp_root_path"  
            // 创建模式为CreateMode.PERSISTENT  
            System.out.println("开始创建根目录节点/tmp_root_path...");  
            zk.create("/tmp_root_path", "我是根目录/tmp_root_path".getBytes(),  
                    Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);  
            System.out.println("根目录节点/tmp_root_path创建成功！");  
  
            Thread.currentThread().sleep(1000l);  
  
            System.out.println("...");  
            System.out.println("...");  
            System.out.println("...");  
            System.out.println("...");  
        } catch (Exception e) {  
            zk = null;  
        }  
    }  
  
    public void destroy() {  
  
        // 删除根目录节点  
        try {  
            System.out.println("开始删除根目录节点/tmp_root_path...");  
            zk.delete("/tmp_root_path", -1);  
            System.out.println("根目录节点/tmp_root_path删除成功！");  
        } catch (InterruptedException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        } catch (KeeperException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        }  
  
        // 关闭连接  
        if (zk != null) {  
            try {  
                zk.close();  
                System.out.println("释放ZooKeeper连接成功！");  
  
            } catch (InterruptedException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }  
  
    public static void main(String[] args) {  
  
        final TestZooKeeperDistributeLock testZooKeeperDistributeLock = new TestZooKeeperDistributeLock(  
                true);  
        final Random radom = new Random();  
  
        try {  
            Thread.currentThread().sleep(1000l);  
        } catch (InterruptedException e2) {  
            // TODO Auto-generated catch block  
            e2.printStackTrace();  
        }  
          
        ArrayList<Thread> threadList = new ArrayList<Thread>();  
          
        for (int i = 0; i < 4; i++) {  
              
            Thread thread = new Thread() {  
                @Override  
                public void run() {  
  
                    boolean locked = false;  
                    while (true) {  
                        try {  
                            // 创建需要获取锁的目录节点，创建成功则说明能够获取到锁，创建不成功，则说明锁已被其他线程（哪怕是不同进程的）获取  
                            // 路径为/tmp_root_path/lock  
                            // 节点内容为当前线程名  
                            // 创建模式为CreateMode.PERSISTENT  
                            System.out.println("线程"  
                                    + Thread.currentThread().getName()  
                                    + "尝试获取锁...");  
                            testZooKeeperDistributeLock.getZk()  
                                    .create("/tmp_root_path/lock",  
                                            Thread.currentThread().getName()  
                                                    .getBytes(),  
                                            Ids.OPEN_ACL_UNSAFE,  
                                            CreateMode.PERSISTENT);  
                            System.out.println("线程"  
                                    + Thread.currentThread().getName()  
                                    + "成功获取到锁！");  
  
                            locked = true;  
  
                            System.out.println("线程"  
                                    + Thread.currentThread().getName()  
                                    + "开始处理业务逻辑...");  
                            Thread.currentThread().sleep(  
                                    3000 + radom.nextInt(3000));  
                            System.out.println("线程"  
                                    + Thread.currentThread().getName()  
                                    + "业务逻辑处理完毕！");  
  
                        } catch (Exception e) {  
  
                            if (testZooKeeperDistributeLock.isReentrant()) {  
                                try {  
                                    String lockThread = new String(  
                                            testZooKeeperDistributeLock  
                                                    .getZk()  
                                                    .getData(  
                                                            "/tmp_root_path/lock",  
                                                            false, null));  
                                    if (lockThread != null) {  
  
                                        // 当前线程与获取到的锁线程名一致，重入锁  
                                        if (lockThread.equals(Thread  
                                                .currentThread().getName())) {  
                                            System.out.println("线程"  
                                                    + Thread.currentThread()  
                                                            .getName()  
                                                    + "成功重入锁！");  
  
                                            locked = true;  
  
                                            System.out.println("线程"  
                                                    + Thread.currentThread()  
                                                            .getName()  
                                                    + "开始处理业务逻辑...");  
                                            Thread.currentThread().sleep(  
                                                    3000 + radom.nextInt(3000));  
                                            System.out.println("线程"  
                                                    + Thread.currentThread()  
                                                            .getName()  
                                                    + "业务逻辑处理完毕！");  
                                        } else {  
                                            System.out.println("线程"  
                                                    + Thread.currentThread()  
                                                            .getName()  
                                                    + "尝试获取锁失败，锁被线程"  
                                                    + lockThread + "占用！");  
                                        }  
  
                                    }  
                                } catch (KeeperException e1) {  
                                    // TODO Auto-generated catch block  
                                    e1.printStackTrace();  
                                } catch (InterruptedException e1) {  
                                    // TODO Auto-generated catch block  
                                    e1.printStackTrace();  
                                }  
  
                            } else {  
                                System.out.println("线程"  
                                        + Thread.currentThread().getName()  
                                        + "尝试获取锁失败！");  
                            }  
  
                            try {  
                                Thread.currentThread().sleep(  
                                        3000 + radom.nextInt(3000));  
                            } catch (InterruptedException e1) {  
                                // TODO Auto-generated catch block  
                                e1.printStackTrace();  
                            }  
                        } finally {  
                            try {  
  
                                if (locked) {  
                                    System.out.println("线程"  
                                            + Thread.currentThread().getName()  
                                            + "开始释放锁...");  
                                    testZooKeeperDistributeLock.getZk().delete(  
                                            "/tmp_root_path/lock", -1);  
                                    System.out.println("线程"  
                                            + Thread.currentThread().getName()  
                                            + "成功释放锁！");  
  
                                    Thread.currentThread().sleep(  
                                            3000 + radom.nextInt(3000));  
                                }  
  
                            } catch (InterruptedException e) {  
                                // TODO Auto-generated catch block  
                                e.printStackTrace();  
                            } catch (KeeperException e) {  
                                // TODO Auto-generated catch block  
                                e.printStackTrace();  
                            } finally {  
                                locked = false;  
                            }  
                        }  
                    }  
  
                }  
            };  
              
            threadList.add(thread);  
              
            thread.start();  
        }  
  
        try {  
            Thread.currentThread().sleep(1000 * 20);  
        } catch (InterruptedException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
          
        for(int i = 0; i < threadList.size(); i++){  
            Thread thread = threadList.get(i);  
            thread.stop();  
        }  
  
        // 释放资源  
        testZooKeeperDistributeLock.destroy();  
  
    }  
}