package com.nishu.elms.controller;

import com.nishu.elms.dto.request.LeaveRequest;
import com.nishu.elms.dto.response.LeaveResponse;
import com.nishu.elms.entity.Leave;
import com.nishu.elms.entity.User;
import com.nishu.elms.enums.LeaveStatus;
import com.nishu.elms.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor

public class LeaveController {
    private final LeaveService leaveService;

    @PostMapping
    public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody LeaveRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(leaveService.applyLeave(request));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<LeaveResponse>> getMyLeaves(
            @RequestParam(required = false)LeaveStatus status,
            @PageableDefault(
                    size = 5,sort="startDate",direction = Sort.Direction.DESC
            )Pageable pageable
            ){
           return ResponseEntity.ok(leaveService.getMyLeaves(status, pageable));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves(){
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }


    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("{id}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable Long id){
        return ResponseEntity.ok(leaveService.approveLeave(id));

    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("{id}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(@PathVariable Long id){
        return ResponseEntity.ok(leaveService.rejectLeave(id));

    }
}
