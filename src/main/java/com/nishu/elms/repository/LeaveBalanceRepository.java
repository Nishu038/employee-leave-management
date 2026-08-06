package com.nishu.elms.repository;

import com.nishu.elms.entity.LeaveBalance;
import com.nishu.elms.entity.User;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistentAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance,Long> {
    Optional<LeaveBalance> findByUser(User user);
}
