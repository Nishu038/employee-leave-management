package com.nishu.elms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EmployeeLeaveManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeLeaveManagementApplication.class, args);
	}

}
