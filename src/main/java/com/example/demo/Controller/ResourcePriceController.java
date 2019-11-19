package com.example.demo.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.DevicePrice;
import com.example.demo.Service.AppService;
import com.example.demo.Service.ResourcePriceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ResourcePriceController {

    @Autowired
    AppService appService;
    private String deviceId;
    private Double BasePrice;
    @Autowired
    ResourcePriceService resourcePriceService;

    @RequestMapping("/registerDevices/{deviceNo}")
    public String registerDevices(@PathVariable(name = "deviceNo") String deviceId, @CookieValue("userId") String userId, Model model) {
        //System.out.println(deviceNo);
        //  this.deviceId = deviceId;
        return "devicesRegister";
    }


    @RequestMapping("/sendCost")
    @ResponseBody
    public Double sendCost(@RequestBody String jsonObject) {
        JSONObject userDevicePrice = JSONObject.parseObject(jsonObject);
        this.deviceId = userDevicePrice.getString("deviceId");
        System.out.println("设备ID sendCost" + deviceId);
        // userDevicePrice.put("deviceId",this.deviceId);
        //userDevicePrice.put("userId",userId);
        System.out.println(userDevicePrice.get("cost"));
        Double basePrice = resourcePriceService.getBaseprice(userDevicePrice);
        BasePrice = basePrice;
        return basePrice;
    }

    @RequestMapping("/sendExPrice")
    @ResponseBody
    public void sendExPrice(@RequestBody String jsonObject) throws JsonProcessingException {
        JSONObject exPriceJsonObj = JSONObject.parseObject(jsonObject);

        String expricejson = exPriceJsonObj.toJSONString();
        System.out.println("sendExprice" + expricejson);
        JSONObject deviceId = exPriceJsonObj.getJSONObject("deviceId");
        System.out.println(deviceId);
        JSONObject finalJson = new JSONObject();
        JSONObject refResource = new JSONObject();
        refResource.put("kind","Edge");
        refResource.put("name",deviceId.getString("name"));
        JSONObject resourceSpec = new JSONObject();
        resourceSpec.put("probeEnabled",true);
        JSONObject labels = new JSONObject();
        labels.put("io.fusionapp.smarthome.coffeepot/price",exPriceJsonObj.getString("exPrice"));
        resourceSpec.put("labels",labels);
        finalJson.put("refResource",refResource);
        finalJson.put("resourceSpec",resourceSpec);
        System.out.println("最终json"+finalJson);


        resourcePriceService.sendExPrice(finalJson);
    }


    @PostMapping("/sendNeed")
    @ResponseBody
    public Double sendNeed(@RequestBody String jsonObject, Model model) {

        JSONObject sendNeed = JSONObject.parseObject(jsonObject);
        JSONObject getFinalPrice = new JSONObject();
        getFinalPrice.put("demand", sendNeed.getDouble("demand"));
        getFinalPrice.put("userId", 11);
        getFinalPrice.put("devices", appService.deviceListArray);
        return resourcePriceService.getFinalPrice(getFinalPrice);

      /*  List<DevicePrice> devicePriceList = JSONObject.parseArray(finalPriceArray.toJSONString(), DevicePrice.class);
        model.addAttribute("devicePriceList",devicePriceList);*/
        //System.out.println(need);
//        Double returnNeed=appService.getNeed(need);
//        Double basePrice = appService.getBaseprice(need);
//        BasePrice=basePrice;
//        Need =returnNeed;
        //return "collect";
    }


}
