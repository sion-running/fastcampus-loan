package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.EntryDTO;
import com.fastcampus.loan.dto.RepaymentDTO;
import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.EntryService;
import com.fastcampus.loan.service.RepaymentService;
import com.fastcampus.loan.service.RepaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fastcampus.loan.dto.EntryDTO.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/applications")
public class InternalController extends AbstractController {
    private final EntryService entryService;
    private final RepaymentService repaymentService;

    @PostMapping("/{applicationId}/entries")
    public ResponseDTO<Response> create(@PathVariable Long applicationId, @RequestBody Request request) {
        return ok(entryService.create(applicationId, request));
    }

    @GetMapping("/{applicationId}/entries")
    public ResponseDTO<Response> get(@PathVariable Long applicationId) {
        return ok(entryService.get(applicationId));
    }

    @PutMapping("/entries/{entryId}")
    public ResponseDTO<UpdateResponse> update(@PathVariable Long entryId, @RequestBody Request request) {
        return ok(entryService.update(entryId, request));
    }

    @DeleteMapping("/entries/{entryId}")
    public ResponseDTO<Void> delete(@PathVariable Long entryId) {
        entryService.delete(entryId);
        return ok();
    }

    @PostMapping("/{applicationId}/repayments")
    public ResponseDTO<RepaymentDTO.Response> create(@PathVariable Long applicationId, @RequestBody RepaymentDTO.Request request) {
        return ok(repaymentService.create(applicationId, request));
    }

    @GetMapping("/{applicationId}/repayments")
    public ResponseDTO<List<RepaymentDTO.ListResponse>> getPayments(@PathVariable Long applicationId) {
        return ok(repaymentService.get(applicationId));
    }

    @PutMapping("/repayments/{repaymentId}")
    public ResponseDTO<RepaymentDTO.UpdateResponse> update(@PathVariable Long repaymentId,
                                                           @RequestBody RepaymentDTO.Request request) {
        return ok(repaymentService.update(repaymentId, request));
    }

    @DeleteMapping("/repayments/{repaymentId}")
    public ResponseDTO<Void> deleteRepayment(@PathVariable Long repaymentId) {
        repaymentService.delete(repaymentId);
        return ok();
    }
}
