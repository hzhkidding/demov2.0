package com.example.demo.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.App;
import com.example.demo.Entity.Device;
import com.example.demo.Service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class AppController {
    @Autowired
    AppService appService;
    public JSONArray deviceListArray;

    private static String appInstanceId;
    public String X;
    public String Y;

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

        new Thread() {
            public void run() {
                appService.appInvoke();
            }
        }.start();
        return "appRunning";
    }


}