package com.txw.dao.leave;

import com.txw.vo.leave.Leave;

public interface LeaveMapper {
    int deleteByPrimaryKey(Long leaveId);

    int insert(Leave record);

    int insertSelective(Leave record);

    Leave selectByPrimaryKey(Long leaveId);

    int updateByPrimaryKeySelective(Leave record);

    int updateByPrimaryKey(Leave record);

}