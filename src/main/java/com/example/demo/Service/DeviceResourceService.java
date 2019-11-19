package com.example.demo.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Config.RestTemplateConfig;
import com.example.demo.Controller.AppController;
import com.example.demo.PhoneInfoController.PhoneInfoController;
import com.example.demo.Entity.Device;
import com.example.demo.Util.HttpInvoke;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.demo.Util.Constans.*;

@Slf4j
@Service
public class DeviceResourceService {
    public String testJson = "{\n" +
            " \"labelSelector\": [\n" +
            "  { \"key\": \"io.fusionapp/kind\", \"op\": \"Eq\", \"value\": \"smarthome\" } \n" +
            " ]\n" +
            "}";


    @Autowired
    AppController appController;

    @Autowired
    PhoneInfoController phoneInfoController;

    @Autowired
    HttpInvoke httpInvoke;

    public List<Device> getDeviceResource() {
        //从phoneController获取手机相关信息//json格式数据
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String phoneInfo = phoneInfoController.phoneInfo;
        /**
         * 传递wifi信号强度
         */
      //  log.info(phoneInfo);
        JSONObject labelXY = (JSONObject) JSONObject.parse(httpInvoke.postInvoke(phoneInfo,DEVICE_LABEL_URL));
     //   System.out.println("hahahh"+labelXY.toJSONString());
        appController.X =  labelXY.getString("x");
        appController.Y = labelXY.getString("y");
        log.info("获取"+labelXY);
        String id = labelXY.getString("id");
        String room = labelXY.getString("room");

        JSONObject selectorJson = new JSONObject();
        JSONArray labelSelector = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("key", "io.fusionapp/kind");
        json.put("op", "Eq");
        json.put("value", "smarthome");
        labelSelector.add(json);
        selectorJson.put("labelSelector", labelSelector);
        String deviceResourceInfo = httpInvoke.exchange(selectorJson.toJSONString(), DEVICE_RESOURCE_URL);
        JSONArray deviceResourceJsonArray = JSONObject.parseArray(deviceResourceInfo);
        List<Device> devicesList = new ArrayList<>();
        /**
         * 设置设备List的属性，返回给controller
         */
        for (int i = 0; i < deviceResourceJsonArray.size(); i++) {
            JSONObject deviceObject = deviceResourceJsonArray.getJSONObject(i);
            Device device = new Device();
            String deviceId = getDeviceId(deviceObject);
            device.setId(deviceId);
            JSONObject labelJson = deviceObject.getJSONObject("labels");
            device.setX(labelJson.getDouble("x"));
            device.setY(labelJson.getDouble("y"));
            device.setStatus(deviceObject.getString("phase"));
            devicesList.add(device);
        }
        return devicesList;
    }

    /**
     * 获取设备的ID, 设备的Id由四个字段组成 是个jsson字符串
     *
     * @param jsonObject
     * @return
     */
    public String getDeviceId(JSONObject jsonObject) {
        JSONObject deviceId = new JSONObject();
        deviceId.put("uid", jsonObject.getString("uid"));
        deviceId.put("namespace", jsonObject.getString("namespace"));
        deviceId.put("kind", jsonObject.getString("kind"));
        deviceId.put("name", jsonObject.getString("name"));
        return deviceId.toJSONString();


    }

}
