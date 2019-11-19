package com.example.demo.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Controller.ResourcePriceController;
import com.example.demo.Entity.App;
import com.example.demo.Util.HttpInvoke;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.Util.Constans.*;
@Slf4j
@Service
public class AppService {

    @Autowired
    HttpInvoke httpInvoke;
    @Autowired
    ResourcePriceController resourcePriceController;


    public String appInstanceId;
    public  JSONArray  deviceListArray;


    //获取全部应用概览
    public List<App> getAppList(){
        JSONObject appJson = JSONObject.parseObject(httpInvoke.getInvoke(APP_LIST_URL));
        JSONArray appJsonArray = (JSONArray) appJson.get("app_classes_introduction");
        List<App> appList = new ArrayList<>();
        for(int i=0;i<appJsonArray.size();i++){
            JSONObject appJsonObj = appJsonArray.getJSONObject(i);
            App app= new App();
            app.setId(appJsonObj .getString("_id"));
            JSONObject appProperties = (JSONObject) appJsonObj.get("properties");
            app.setName(appProperties.getString("name"));
            app.setProcess_author(appProperties.getString("v"));
            app.setProcess_id(appProperties.getString("process_id"));
            app.setImage(appProperties.getString("type"));
            app.setColor(appProperties.getString("color"));
            appList.add(app);
        }
        return appList;

    }
    //根据ID获取应用详情
/*    public JSONObject getAppDetailById(String appId){
        //指定参数请求
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("app_class_id",appId);
        ResponseEntity<String>  entity = postInvoke(map,APP_DETAIL_URL);
        JSONObject appDetailJsonObj = (JSONObject) JSONObject.parse(entity.getBody());
       // System.out.println(appDetailJsonObj.toJSONString());
        return appDetailJsonObj;
    }*/
    //应用实例化
    public String appInstance(String appId,String userId,String X,String Y){
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("app_class_id",appId);
        map.add("user_id",userId);
        map.add("x",X);
        map.add("y",Y);
      //  ResponseEntity<String> entity = postInvoke(map,APP_INSTANCE_URL);
        String appInstanceInfoString = httpInvoke.postInvoke(map,APP_INSTANCE_URL);
        JSONObject appInstanceInfo = JSONObject.parseObject(appInstanceInfoString);
      //  JSONObject appInstanceInfo  = JSONObject.parseObject(httpInvoke.postInvoke(map,APP_INSTANCE_URL));
        JSONObject jsonObject = appInstanceInfo.getJSONObject("app_instance_resource");
        this.appInstanceId = jsonObject.getString("_id");
        JSONArray deviceListArray = new JSONArray();
        JSONObject deviceIdList = jsonObject.getJSONObject("resource");
        for (Map.Entry<String, Object> entry : deviceIdList.entrySet()){
            deviceListArray.add(entry.getValue());
        }
        this.deviceListArray = deviceListArray;
        return appInstanceInfoString;
    }
    //应用调用
    public void appInvoke(){

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("app_instance_id",this.appInstanceId);
        httpInvoke.postInvoke(map,APP_INSTANCE_URL);
    }

}
