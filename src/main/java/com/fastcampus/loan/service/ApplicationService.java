package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.ApplicationDTO.*;
import com.fastcampus.loan.dto.CounselDTO;

public interface ApplicationService {
    Response create(Request request);
    Response get(Long applicationId);

    Response update(Long applicationId, Request request);

    void delete(Long applicationId);
}
