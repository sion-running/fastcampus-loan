package com.fastcampus.loan.service;

import java.util.List;

import static com.fastcampus.loan.dto.TermsDTO.*;

public interface TermsService {
    Response create(Request request);

    List<Response> getAll();
}
