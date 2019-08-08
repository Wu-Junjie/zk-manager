package org.felix.study.zkmanager.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.data.Stat;
import org.felix.study.zkmanager.utils.CuratorUtil;
import org.felix.study.zkmanager.model.ZkNode;
import org.felix.study.zkmanager.utils.XmlUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ZkController implements ApplicationListener<ContextRefreshedEvent> {

    private static XmlUtil xmlUtil = null;

/*    @RequestMapping("/login_error")
    public JSONObject loginError() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status","error");
        jsonObject.put("msg","登录失败!");
        return jsonObject;
    }

    @RequestMapping("/login_success")
    public JSONObject loginSuccess() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status","success");
        jsonObject.put("msg","登录成功!");
        return jsonObject;
    }*/

    /**
     * 如果自动跳转到这个页面，说明用户未登录，返回相应的提示即可
     * <p>
     * 如果要支持表单登录，可以在这个方法中判断请求的类型，进而决定返回JSON还是HTML页面
     *
     * @return
     */
    @RequestMapping("/login_page")
    public JSONObject loginPage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status","error");
        jsonObject.put("msg","尚未登录!");
        return jsonObject;
    }

    /*@RequestMapping(value = "/login", method = RequestMethod.POST)
    public boolean login(@RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        if ("admin".equals(username) && "admin".equals(password)) {
            return true;
        }
        return false;
    }*/

    @RequestMapping(value = "/getNodeList", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getNodeList() {

        List<Map<String, String>> response = new ArrayList<>();

        Map<String, String> allMap = xmlUtil.getAllMap();

        allMap.forEach((k, v) -> {
            Map<String, String> oneNode = new HashMap<>();
            oneNode.put("host", k);
            oneNode.put("label", v);
            response.add(oneNode);
        });

        return response;
    }

    @RequestMapping(value = "/addNode", method = RequestMethod.POST)
    public boolean addNode(@RequestBody JSONObject nodeObj) {
        if (StringUtils.isEmpty(nodeObj.get("node"))) {
            return false;
        }
        String node = (String) nodeObj.get("node");
        xmlUtil.add(node, node);
        return true;
    }

    @RequestMapping(value = "/getAllZkNodes", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getAllZkNodes(@RequestParam String node) {
        JSONObject jsonObject = new JSONObject();
        CuratorUtil curatorUtil = new CuratorUtil();;
        try {
            curatorUtil.initClient(node);
            ZkNode allZkNode = curatorUtil.getAllZkNode("/");
            jsonObject.put("status","success");
            jsonObject.put("allZkNode",allZkNode);
        } catch (Exception e) {
            jsonObject.put("status","fail");
            jsonObject.put("msg",e.toString());
        } finally {
            curatorUtil.close();
        }
        return jsonObject;
    }

    @RequestMapping(value = "/getNodeDetail", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getNodeDetail(@RequestBody JSONObject request) {
        JSONObject nodeDetail = new JSONObject();
        CuratorUtil curatorUtil = new CuratorUtil();
        try {
            curatorUtil.initClient(request.getString("node"));
            Stat stat = curatorUtil.getStat(request.getString("path"));
            String data = curatorUtil.getData(request.getString("path"));
            //考虑到有些value会是 json 类型，需要转换
            if (!StringUtils.isEmpty(data)){
                try{
                    nodeDetail.put("data",JSONObject.parse(data));
                }catch (Exception e){
                    nodeDetail.put("data", data);
                }
            }else {
                nodeDetail.put("data", "");
            }

            nodeDetail.put("stat", stat);
            nodeDetail.put("status","success");
        } catch (Exception e) {
            nodeDetail.put("status","fail");
            nodeDetail.put("msg",e.toString());
        } finally {
            curatorUtil.close();
        }
        return nodeDetail;
    }

    @RequestMapping(value = "/deleteNode", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject deleteNode(@RequestBody JSONObject request) {
        JSONObject nodeDetail = new JSONObject();
        CuratorUtil curatorUtil = new CuratorUtil();
        try {
            curatorUtil.initClient(request.getString("node"));
            curatorUtil.deleteNode(request.getString("path"));

            nodeDetail.put("status","success");
        } catch (Exception e) {
            nodeDetail.put("status","fail");
            nodeDetail.put("msg",e.toString());
        } finally {
            curatorUtil.close();
        }
        return nodeDetail;
    }

    @RequestMapping(value = "/saveNode", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject saveNode(@RequestBody JSONObject request) {
        JSONObject nodeDetail = new JSONObject();
        CuratorUtil curatorUtil = new CuratorUtil();
        try {
            curatorUtil.initClient(request.getString("node"));
            String path = request.getString("path");
            String data = request.getString("data");
            byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
            if (curatorUtil.checkExists(path)){
                curatorUtil.createNode(path,bytes);
            }else {
                curatorUtil.saveNode(path,bytes);
            }

            nodeDetail.put("status","success");
        } catch (Exception e) {
            nodeDetail.put("status","fail");
            nodeDetail.put("msg",e.toString());
        } finally {
            curatorUtil.close();
        }
        return nodeDetail;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        xmlUtil = new XmlUtil("nodes.xml");
    }

}
