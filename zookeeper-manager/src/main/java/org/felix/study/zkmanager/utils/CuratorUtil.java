package org.felix.study.zkmanager.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.felix.study.zkmanager.model.ZkNode;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fw13
 * @date 2019/2/15 13:31
 */
public class CuratorUtil implements Closeable {

    CuratorFramework client;

    public CuratorUtil() {
    }

    public void initClient(String node){
        client = CuratorFrameworkFactory.builder().connectString(node).retryPolicy(new ExponentialBackoffRetry(1000,3)).connectionTimeoutMs(5000).sessionTimeoutMs(5000).build();
        client.start();
    }

    public List<String> listChildrenNode(String path) throws Exception {
        PathUtils.validatePath(path);
        return client.getChildren().forPath(path);
    }

    public boolean checkExists(String path) throws Exception {
        PathUtils.validatePath(path);
        return client.checkExists().forPath(path)==null;
    }

    public boolean createNode(String path, byte[] value) throws Exception {
        PathUtils.validatePath(path);
        return client.create().creatingParentsIfNeeded().forPath(path,value)!=null;
    }

    public boolean saveNode(String path, byte[] value) throws Exception {
        PathUtils.validatePath(path);
        return client.setData().forPath(path,value)!=null;
    }

    public Closeable createEphemeralNode(String path, byte[] value) throws Exception {
        PathUtils.validatePath(path);
        PersistentNode persistentNode = new PersistentNode(client, CreateMode.EPHEMERAL,false,path,value);
        persistentNode.start();
        persistentNode.waitForInitialCreate(3000, TimeUnit.MILLISECONDS);
        return persistentNode;
    }

    public Stat getStat(String path) throws Exception {
        PathUtils.validatePath(path);
        return client.checkExists().forPath(path);
    }

    public String getData(String path) throws Exception {
        PathUtils.validatePath(path);
        return new String(client.getData().forPath(path));
    }

    public void deleteNode(String path) throws Exception {
        PathUtils.validatePath(path);
        client.delete().forPath(path);
    }

    public boolean isEphemeralNode(String path) throws Exception {
        boolean isEphemeral = false;

        PathUtils.validatePath(path);
        Stat stat = client.checkExists().forPath(path);
        long ephemeralOwner = stat.getEphemeralOwner();
        if (ephemeralOwner > 0){
            isEphemeral = true;
        }
        return isEphemeral;
    }

    public ZkNode getAllZkNode(String path) throws Exception{
        ZkNode zkNode = new ZkNode();
        String[] split = path.split("/");
        if (split.length==0){
            zkNode.setLabel("/");
        } else {
            zkNode.setLabel(split[split.length-1]);
        }

        zkNode.setPath(path);

        List<String> childrenPath = listChildrenNode(path);
        if (childrenPath.size()>0){
            zkNode.setIcon("el-icon-check");
            List<ZkNode> childNodes = new ArrayList<>();
            for (String child:childrenPath) {
                StringBuilder absolutePath = new StringBuilder();
                if (path.equals("/")){
                    absolutePath.append("/").append(child);
                }else {
                    absolutePath.append(path).append("/").append(child);
                }

                childNodes.add(getAllZkNode(absolutePath.toString()));
                zkNode.setChildren(childNodes);
            }
        }else {
            boolean isEphemeralNode = isEphemeralNode(path);
            if (isEphemeralNode){
                zkNode.setIcon("el-icon-lightning");
            } else {
                zkNode.setIcon("el-icon-sunny");
            }
        }

        return zkNode;
    }

    @Override
    public void close() {
        if (client != null){
            client.close();
        }

    }
}
