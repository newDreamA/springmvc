package com.txw.controller.oa;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangxiewen on 2016/10/16.
 */
@Controller
@RequestMapping(value = "/oa/leave")
public class LeaveController {
    private Logger logger = LoggerFactory.getLogger(getClass());
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
    @RequestMapping(value = "start", method = RequestMethod.POST)
    public String startWorkflow(Leave leave, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            User user = UserUtil.getUserFromSession(session);
            // 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
            if (user == null || StringUtils.isBlank(user.getId())) {
                return "redirect:/login?timeout=true";
            }
            leave.setUserId(user.getId());
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

}

