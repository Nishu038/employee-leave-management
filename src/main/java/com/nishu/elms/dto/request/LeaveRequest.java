package com.nishu.elms.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter

public class LeaveRequest {
    @NotBlank(message = "Reason is required")
    String reason;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    LocalDate startDate;

    @NotNull(message = "End date is required")
    LocalDate endDate;

}
