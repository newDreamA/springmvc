package com.txw.controller;

import com.txw.controller.util.UserUtil;
import com.txw.service.impl.UserServiceImpl;
import com.txw.vo.user.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by tangxiewen on 2016/10/18.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    /**
     * 登录系统
     *
     * @param userName
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/logon")
    public String logon(@RequestParam("username") String userName, @RequestParam("password") String password, HttpSession session) {
        logger.debug("logon request: {username={}, password={}}", userName, password);
        User userInfo = userService.getUserInfo(userName);
        boolean checkPassword =false;
        if(userInfo!=null) {
            if (StringUtils.isNotBlank(password) && password.equals(userInfo.getUserPassword())) {
                checkPassword = true;
            }
        }
        if (checkPassword) {
            UserUtil.saveUserToSession(session, userInfo);
            return "redirect:/index";
        } else {
            return "redirect:/user/login?error=true";
        }
    }
}
