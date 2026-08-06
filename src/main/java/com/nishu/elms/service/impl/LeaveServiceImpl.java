package com.nishu.elms.service.impl;

import com.nishu.elms.dto.request.LeaveRequest;
import com.nishu.elms.dto.response.LeaveBalanceResponse;
import com.nishu.elms.dto.response.LeaveResponse;
import com.nishu.elms.entity.Leave;
import com.nishu.elms.entity.LeaveBalance;
import com.nishu.elms.entity.User;
import com.nishu.elms.enums.LeaveStatus;
import com.nishu.elms.enums.LeaveType;
import com.nishu.elms.exception.ResourceNotFoundException;
import com.nishu.elms.repository.LeaveBalanceRepository;
import com.nishu.elms.repository.LeaveRepository;
import com.nishu.elms.repository.UserRepository;
import com.nishu.elms.service.CurrentUserService;
import com.nishu.elms.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public LeaveResponse applyLeave(LeaveRequest request) {
        User user = currentUserService.getCurrentUser();
        Leave leave = Leave.builder()
                .reason(request.getReason())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(LeaveStatus.PENDING)
                .user(user)
                .leaveType(request.getLeaveType())
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return mapToResponse(savedLeave);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveResponse> getMyLeaves(LeaveStatus status, Pageable pageable) {
        User user = currentUserService.getCurrentUser();
        Page<Leave> leavePage;
        if(status == null){
            leavePage = leaveRepository.findByUser(user,pageable);
        }else{
            leavePage = leaveRepository.findByUserAndStatus(user,status,pageable);
        }
        return leavePage.map(this::mapToResponse);
    }

    private LeaveResponse mapToResponse(Leave leave){
        return new LeaveResponse(
                leave.getId(),
                leave.getReason(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getStatus(),
                leave.getUser().getId()
        );
    }
//    @Override
//    public List<LeaveResponse> getMyLeaves() {
//        User user = currentUserService.getCurrentUser();
//        return leaveRepository.findByUser(user)
//                .stream().map(this::mapToResponse)
//                .toList();
//    }

    @Override
    public List<LeaveResponse> getPendingLeaves() {
        User manager = currentUserService.getCurrentUser();
        return leaveRepository.findPendingLeavesByDepartment(LeaveStatus.PENDING,manager.getDepartment().getId())
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public LeaveResponse approveLeave(Long leaveId) {
        User manager = currentUserService.getCurrentUser();
        Leave leave = leaveRepository.findById(leaveId).orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
//        validate leave status
        if(leave.getStatus() != LeaveStatus.PENDING){
            throw new IllegalArgumentException("Only pending leaves can be approved");
        }
        //department restriction
        if(!manager.getDepartment().getId()
                .equals(leave.getUser().getDepartment().getId())){
            throw new IllegalArgumentException("You can only approve leaves within your department");
        }
        LeaveBalance balance = leaveBalanceRepository.findByUser(leave.getUser())
                        .orElseThrow(()-> new ResourceNotFoundException("Leave balance not found"));
        int leaveDays = calculateLeaveDays(leave);
        deductLeaveBalance(balance,leave.getLeaveType(),leaveDays);

        leave.setStatus(LeaveStatus.APPROVED);
        leaveBalanceRepository.save(balance);
        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    @Override
    public LeaveResponse rejectLeave(Long leaveId) {
        User manager = currentUserService.getCurrentUser();
        Leave leave = leaveRepository.findById(leaveId).orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
        if(leave.getStatus() != LeaveStatus.PENDING){
            throw new IllegalArgumentException("only pending leaves can be rejected");
        }

        if(!manager.getDepartment().getId()
                .equals(leave.getUser().getDepartment().getId())){
            throw new IllegalArgumentException("you can only reject leave within your department");
        }
        leave.setStatus(LeaveStatus.REJECTED);
        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    @Override
    public LeaveBalanceResponse getMyLeaveBalance() {
        User user = currentUserService.getCurrentUser();
        LeaveBalance balance = leaveBalanceRepository.findByUser(user)
                .orElseThrow(()-> new ResourceNotFoundException("leave balance not found"));
        return LeaveBalanceResponse.builder()
                .casualLeave(balance.getCasualLeave())
                .sickLeave(balance.getSickLeave())
                .earnedLeave(balance.getEarnedLeaves())
                .build();
    }

    private int calculateLeaveDays(Leave leave){
        return (int) ChronoUnit.DAYS.between(
                leave.getStartDate(),
                leave.getEndDate()
        )+1;
    }
    private void deductLeaveBalance(LeaveBalance balance,
                                    LeaveType leaveType,
                                    int leaveDays){
        switch (leaveType){
            case CASUAL -> {
                if(balance.getCasualLeave()<leaveDays){
                    throw new IllegalStateException("Insufficient casual leave balance");
                }
                balance.setCasualLeave(
                        balance.getCasualLeave() - leaveDays
                );
            }
            case SICK -> {
                if(balance.getSickLeave()<leaveDays){
                    throw new IllegalStateException("Insufficient sick leave balance");
                }
                balance.setSickLeave(
                        balance.getCasualLeave() - leaveDays
                );
            }
            case EARNED -> {
                if(balance.getEarnedLeaves()<leaveDays){
                    throw new IllegalStateException("Insufficient earned leave balance");
                }
                balance.setEarnedLeaves(
                        balance.getCasualLeave() - leaveDays
                );
            }
        }
    }
}
