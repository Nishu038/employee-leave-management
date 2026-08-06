package com.nishu.elms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardResponse {
    private Long totalUsers;
    private Long totalDepartments;
    private Long pendingLeaves;
    private Long approvedLeaves;
    private Long rejectedLeaves;
}
