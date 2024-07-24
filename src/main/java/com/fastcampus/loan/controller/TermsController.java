package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fastcampus.loan.dto.TermsDTO.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/terms")
public class TermsController extends AbstractController {
    private final TermsService termsService;

    @PostMapping
    public ResponseDTO<Response> create(@RequestBody Request request) {
        return ok(termsService.create(request));
    }

    @GetMapping
    public ResponseDTO<List<Response>> getAll() {
        return ok(termsService.getAll());
    }
}
