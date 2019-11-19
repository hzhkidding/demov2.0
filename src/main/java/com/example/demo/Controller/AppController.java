package com.example.demo.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.App;
import com.example.demo.Entity.Device;
import com.example.demo.Service.AppService;
import com.example.demo.Util.HttpInvoke;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.Util.Constans.APP_LIST_URL;
import static com.example.demo.Util.Constans.APP_STATUS_URL;

@Slf4j
@Controller
public class AppController {

   private int num = 0;
    @Autowired
    HttpInvoke httpInvoke;

    private String status;
    @Autowired
    AppService appService;
    public JSONArray deviceListArray;

    public String appInstanceId;
    public Double X;
    public Double Y;

    @RequestMapping(path = {"/getAppList"}, method = RequestMethod.GET)
    public String getAllAppList(Model model) {

        List<App> appList = appService.getAppList();
        model.addAttribute("AppList", appList);
        return "app";

    }

    @RequestMapping(path = {"/appInstance"}, method = RequestMethod.POST)
    public String appInstance(@RequestParam("appId") String appId, @RequestParam("userId") String userId) {
        if (appId.equals("5dcfe456f6169f7f8ded2cb7"))
            return "run";
        appService.appInstance(appId, userId, this.X, this.Y);
        return "about";
    }

    @RequestMapping(path = {"/appInvoke"}, method = RequestMethod.GET)
    public String appInvoke(Model model) {
        //  String appInstanceId = this.appInstanceId;

        new Thread(){
            public void run(){
                appService.appInvoke();
            }
        }.start();
        return "appRunning";
    }

    @RequestMapping(path = {"/getStatus"}, method = RequestMethod.GET)
    @ResponseBody
    public String getStatus() {
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("app_instance_id",appService.appInstanceId);
        JSONArray jsonArray = JSONArray.parseArray(httpInvoke.postInvoke(map,APP_STATUS_URL));
        if(num != 3) {
            JSONObject jsonObject = jsonArray.getJSONObject(num);
            if (jsonObject.getString("state").equals("2")) {
                log.info(jsonObject.getString("state"));
                num++;
                return "2";
            }
        }
        return "0";
    }
}