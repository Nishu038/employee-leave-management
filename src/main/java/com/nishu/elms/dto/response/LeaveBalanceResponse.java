package com.nishu.elms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalanceResponse {
    private Integer casualLeave;
    private Integer sickLeave;
    private Integer earnedLeave;
}
