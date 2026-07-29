package com.nishu.elms.dto.response;

import com.nishu.elms.enums.LeaveStatus;

import java.time.LocalDate;

public record LeaveResponse(Long id,
                            String reason,
                            LocalDate startDate,
                            LocalDate endDate,
                            LeaveStatus status,
                            Long userId) {
}
