package com.nishu.elms.service;

import com.nishu.elms.dto.request.LeaveRequest;
import com.nishu.elms.dto.response.LeaveResponse;

import java.util.List;

public interface LeaveService {

    LeaveResponse applyLeave(LeaveRequest request);
    List<LeaveResponse> getMyLeaves();
    List<LeaveResponse> getPendingLeaves();
    LeaveResponse approveLeave(Long leaveId);
    LeaveResponse rejectLeave(Long leaveId);
}
