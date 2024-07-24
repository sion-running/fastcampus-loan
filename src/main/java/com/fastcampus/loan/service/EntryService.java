package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.EntryDTO;

import static com.fastcampus.loan.dto.EntryDTO.*;

public interface EntryService {
    Response create(Long applicationId, Request request);
}
