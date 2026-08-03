package com.nishu.elms.service;

import com.nishu.elms.dto.request.DepartmentRequest;
import com.nishu.elms.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentRequest request);
    List<DepartmentResponse> getAllDepartments();
}
