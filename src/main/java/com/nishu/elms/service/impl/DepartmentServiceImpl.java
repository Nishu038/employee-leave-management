package com.nishu.elms.service.impl;

import com.nishu.elms.dto.request.DepartmentRequest;
import com.nishu.elms.dto.response.DepartmentResponse;
import com.nishu.elms.entity.Department;
import com.nishu.elms.exception.ResourceNotFoundException;
import com.nishu.elms.repository.DepartmentRepository;
import com.nishu.elms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if(departmentRepository.findByName(request.getName()).isPresent()){
            throw new ResourceNotFoundException("Department already exists");
        }
        Department department = Department.builder()
                .name(request.getName()).build();
        Department savedDepartment = departmentRepository.save(department);
        return mapToResponse(savedDepartment);
    }

    private DepartmentResponse mapToResponse(Department department){
        return DepartmentResponse.builder().id(department.getId())
                .name(department.getName()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse).toList();
    }
}
