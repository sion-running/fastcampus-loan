package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.EntryDTO;

import static com.fastcampus.loan.dto.BalanceDTO.*;

public interface BalanceService {
    Response create(Long applicationId, Request request);
}
