package com.nishu.elms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LeaveBalance extends BaseEntity{
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @Column(nullable = false)
    private Integer casualLeave;

    @Column(nullable = false)
    private Integer sickLeave;

    @Column(nullable = false)
    private Integer earnedLeaves;
}
