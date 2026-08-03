package com.nishu.elms.service;

import com.nishu.elms.dto.request.LeaveRequest;
import com.nishu.elms.dto.response.LeaveResponse;
import com.nishu.elms.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveService {

    LeaveResponse applyLeave(LeaveRequest request);
    // List<LeaveResponse> getMyLeaves();
    Page<LeaveResponse> getMyLeaves(
            LeaveStatus status,
            Pageable pageable
    );
    List<LeaveResponse> getPendingLeaves();
    LeaveResponse approveLeave(Long leaveId);
    LeaveResponse rejectLeave(Long leaveId);
}
