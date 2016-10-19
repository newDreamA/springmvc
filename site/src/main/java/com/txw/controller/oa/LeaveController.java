package com.txw.controller.oa;

import com.txw.service.vo.LeaveEntity;
import com.txw.util.Page;
import com.txw.controller.util.PageUtil;
import com.txw.controller.util.UserUtil;
import com.txw.service.impl.oa.LeaveWorkflowService;
import com.txw.vo.leave.Leave;
import com.txw.vo.user.User;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangxiewen on 2016/10/16.
 */
@Controller
@RequestMapping(value = "/oa/leave")
public class LeaveController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private LeaveWorkflowService workflowService;


    @RequestMapping(value = {"apply", ""})
    public String createForm(Model model) {
        model.addAttribute("leave",null);
        return "/oa/leave/leaveApply";
    }


    /**
     * 启动请假流程
     *
     * @param leave
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String startWorkflow(@ModelAttribute Leave leave, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            User user = UserUtil.getUserFromSession(session);
            // 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
            if (user == null || StringUtils.isBlank(user.getUserName())) {
                return "redirect:/login?timeout=true";
            }
            leave.setUserId(user.getUserId());
            leave.setUserName(user.getUserName());
            Map<String, Object> variables = new HashMap<String, Object>();
            ProcessInstance processInstance = workflowService.startWorkflow(leave, variables);
            redirectAttributes.addFlashAttribute("message", "流程已启动，流程ID：" + processInstance.getId());
        } catch (ActivitiException e) {
            if (e.getMessage().indexOf("no processes deployed with key") != -1) {
                logger.warn("没有部署流程!", e);
                redirectAttributes.addFlashAttribute("error", "没有部署流程，请在[工作流]->[流程管理]页面点击<重新部署流程>");
            } else {
                logger.error("启动请假流程失败：", e);
                redirectAttributes.addFlashAttribute("error", "系统内部错误！");
            }
        } catch (Exception e) {
            logger.error("启动请假流程失败：", e);
            redirectAttributes.addFlashAttribute("error", "系统内部错误！");
        }
        return "redirect:/oa/leave/apply";
    }


//    /**
//     * 任务列表
//     *
//     * @param leave
//     */
//    @RequestMapping(value = "list/task")
//    public ModelAndView taskList(HttpSession session, HttpServletRequest request) {
//        ModelAndView mav = new ModelAndView("/oa/leave/taskList");
//        Page<Leave> page = new Page<Leave>(PageUtil.PAGE_SIZE);
//        int[] pageParams = PageUtil.init(page, request);
//
//        String userId = UserUtil.getUserFromSession(session).getId();
//        workflowService.findTodoTasks(userId, page, pageParams);
//        mav.addObject("page", page);
//        return mav;
//    }


    /**
     * 读取运行中的流程实例
     *
     * @return
     */
    @RequestMapping(value = "list/running")
    public ModelAndView runningList(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/oa/leave/running");
        Page<LeaveEntity> page = new Page<LeaveEntity>(PageUtil.PAGE_SIZE);

        int[] pageParams = PageUtil.init(page,request);
        workflowService.findRunningProcessInstaces(page, pageParams);
        mav.addObject("page", page);
        return mav;
    }



}

