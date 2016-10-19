package com.txw.controller.workflow;

import com.txw.controller.util.SessionHelpUtils;
import com.txw.controller.util.UserUtil;
import com.txw.service.impl.UserServiceImpl;
import com.txw.vo.user.User;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * Created by tangxiewen on 2016/10/18.
 */
public class LeaveTaskListener implements TaskListener{
    @Autowired
    UserServiceImpl userService;


    public void notify(DelegateTask delegateTask) {
        HttpSession session = SessionHelpUtils.getSession();
        Object attribute =session.getAttribute("user");
        User user = attribute == null ? null : (User) attribute;
        User leaderInfo =userService.getLeaderInfo(user);
        delegateTask.setAssignee(leaderInfo.getUserName());
        System.out.println(delegateTask.getProcessInstanceId()+" "+delegateTask.getTaskDefinitionKey());
    }

}
