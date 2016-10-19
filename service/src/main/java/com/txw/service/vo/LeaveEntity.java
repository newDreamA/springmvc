package com.txw.service.vo;

import com.txw.vo.leave.Leave;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

/**
 * Created by tangxiewen on 2016/10/18.
 */
public class LeaveEntity extends Leave{
    // 运行中的流程实例
    private ProcessInstance processInstance;

    // 流程任务
    private Task task;

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
