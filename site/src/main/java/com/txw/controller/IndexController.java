package com.txw.controller;

import com.txw.service.impl.UserServiceImpl;
import com.txw.vo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by tangxiewen on 2016/10/11.
 */
@Controller
public class IndexController {
    @Resource
    private UserServiceImpl userService;
    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/index")
    @ResponseBody
    public String action(){
        return "index";
    }

    @RequestMapping("/insertUser")
    @ResponseBody
    public String action(@ModelAttribute User user){
        userService.insertUser(user);
        return "ok";
    }
}
