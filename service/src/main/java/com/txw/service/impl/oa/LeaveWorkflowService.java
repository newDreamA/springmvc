package com.txw.service.impl.oa;

import com.txw.dao.leave.LeaveMapper;
import com.txw.service.vo.LeaveEntity;
import com.txw.util.FatherToChildUtils;
import com.txw.util.Page;
import com.txw.vo.leave.Leave;
import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tangxiewen on 2016/10/16.
 */
@Component
@Transactional
public class LeaveWorkflowService {
    private static Logger logger = LoggerFactory.getLogger(LeaveWorkflowService.class);

    @Autowired
    private LeaveMapper leaveManager;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    /**
     * 启动流程
     *
     * @param entity
     */
    @Transactional
    public ProcessInstance startWorkflow(Leave entity, Map<String, Object> variables) {
        leaveManager.insertSelective(entity);
        logger.debug("save entity: {}", entity);
        String businessKey = entity.getLeaveId().toString();

        ProcessInstance processInstance = null;
        try {
            // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
     //      identityService.setAuthenticatedUserId(entity.getUserName());
            variables.put("applyUserId",entity.getUserName());
            processInstance = runtimeService.startProcessInstanceByKey("starLeave", businessKey, variables);
            String processInstanceId = processInstance.getId();

            logger.debug("start process of {key={}, bkey={}, pid={}, variables={}}", new Object[]{"leave", businessKey, processInstanceId, variables});
        } finally {
          identityService.setAuthenticatedUserId(null);
        }
        return processInstance;
    }

    /**
     * 读取运行中的流程
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<LeaveEntity> findRunningProcessInstaces(Page<LeaveEntity> page, int[] pageParams) {
        List<LeaveEntity> results = new ArrayList<LeaveEntity>();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processDefinitionKey("starLeave").active().orderByProcessInstanceId().desc();
        List<ProcessInstance> list = query.listPage(pageParams[0], pageParams[1]);

        // 关联业务实体
        for (ProcessInstance processInstance : list) {
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            Leave leave = leaveManager.selectByPrimaryKey(new Long(businessKey));
            LeaveEntity leaveEntity = new LeaveEntity();

            leaveEntity.setLeaveId(leave.getLeaveId());
            leaveEntity.setLeaveType(leave.getLeaveType());
            leaveEntity.setUserId(leave.getUserId());
            leaveEntity.setUserName(leave.getUserName());
            leaveEntity.setApplyTime(leave.getApplyTime());
            leaveEntity.setStartTime(leave.getStartTime());
            leaveEntity.setEndTime(leave.getEndTime());

            leaveEntity.setProcessInstance(processInstance);
            // 设置当前任务信息
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().orderByTaskCreateTime().desc().listPage(0, 1);
            leaveEntity.setTask(tasks.get(0));

            results.add(leaveEntity);

        }

        page.setTotalCount(query.count());
        page.setResult(results);
        return results;
    }


    /**
     * 查询流程定义对象
     *
     * @param processDefinitionId 流程定义ID
     * @return
     */
    protected ProcessDefinition getProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        return processDefinition;
    }
}
