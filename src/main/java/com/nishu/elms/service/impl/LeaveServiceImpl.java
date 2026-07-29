package com.nishu.elms.service.impl;

import com.nishu.elms.dto.request.LeaveRequest;
import com.nishu.elms.dto.response.LeaveResponse;
import com.nishu.elms.entity.Leave;
import com.nishu.elms.entity.User;
import com.nishu.elms.enums.LeaveStatus;
import com.nishu.elms.exception.ResourceNotFoundException;
import com.nishu.elms.repository.LeaveRepository;
import com.nishu.elms.repository.UserRepository;
import com.nishu.elms.service.CurrentUserService;
import com.nishu.elms.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    public LeaveResponse applyLeave(LeaveRequest request) {
        User user = currentUserService.getCurrentUser();
        Leave leave = Leave.builder()
                .reason(request.getReason())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(LeaveStatus.PENDING)
                .user(user)
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return mapToResponse(savedLeave);
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
    @Override
    public List<LeaveResponse> getMyLeaves() {
        User user = currentUserService.getCurrentUser();
        return leaveRepository.findByUser(user)
                .stream().map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<LeaveResponse> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public LeaveResponse approveLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId).orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
        leave.setStatus(LeaveStatus.APPROVED);
        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }

    @Override
    public LeaveResponse rejectLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId).orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
        leave.setStatus(LeaveStatus.REJECTED);
        Leave updatedLeave = leaveRepository.save(leave);
        return mapToResponse(updatedLeave);
    }
}
