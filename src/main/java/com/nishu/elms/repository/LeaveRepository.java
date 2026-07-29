package com.nishu.elms.repository;

import com.nishu.elms.entity.Leave;
import com.nishu.elms.entity.User;
import com.nishu.elms.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave,Long> {

    List<Leave> findByUser(User user);

    List<Leave> findByUserId(Long userId);

    List<Leave> findByStatus(LeaveStatus status);
}
