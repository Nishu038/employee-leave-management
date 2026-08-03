package com.nishu.elms.repository;

import com.nishu.elms.entity.Leave;
import com.nishu.elms.entity.User;
import com.nishu.elms.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave,Long> {

    List<Leave> findByUser(User user);

    List<Leave> findByUserId(Long userId);

    List<Leave> findByStatus(LeaveStatus status);

    @Query("""
        SELECT l
        FROM Leave l
        JOIN FETCH l.user u
        JOIN FETCH u.department d
        WHERE l.status = :status
          AND d.id = :departmentId
        """)
    List<Leave> findPendingLeavesByDepartment(
            @Param("status") LeaveStatus status,
            @Param("departmentId") Long departmentId
    );

    Page<Leave> findByUser(User user, Pageable pageable);

    Page<Leave> findByUserAndStatus(
            User user,
            LeaveStatus status,
            Pageable pageable
    );
}
