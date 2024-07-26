package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Entry;
import com.fastcampus.loan.domain.Repayment;
import com.fastcampus.loan.dto.BalanceDTO.*;
import com.fastcampus.loan.dto.RepaymentDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.EntryRepository;
import com.fastcampus.loan.repository.RepaymentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.fastcampus.loan.dto.BalanceDTO;
import com.fastcampus.loan.dto.BalanceDTO.RepaymentRequest.RepaymentType;
import com.fastcampus.loan.dto.RepaymentDTO.Request;
import com.fastcampus.loan.dto.RepaymentDTO.Response;

import java.util.Optional;

import static com.fastcampus.loan.dto.RepaymentDTO.*;

@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {
    private final RepaymentRepository repaymentRepository;
    private final ApplicationRepository applicationRepository;
    private final EntryRepository entryRepository;
    private final BalanceService balanceService;
    private final ModelMapper modelMapper;

    @Override
    public RepaymentDTO.Response create(Long applicationId, RepaymentDTO.Request request) {
        // validation
        // 1. 계약이 완료된 신청정보 존재여부
        // 2. 집행이 되어있어야 함
        if (!isRepayableApplication(applicationId)) {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        Repayment repayment = modelMapper.map(request, Repayment.class);
        repayment.setApplicationId(applicationId);

        repaymentRepository.save(repayment);

        // 잔고 업데이트
        BalanceDTO.Response updatedBalance = balanceService.repaymentUpdate(applicationId, RepaymentRequest.builder()
                .repaymentAmount(request.getRepaymentAmount())
                .type(RepaymentType.REMOVE)
                .build());

        Response response = modelMapper.map(repayment, Response.class);
        response.setBalance(updatedBalance.getBalance());

        return response;
    }

    private boolean isRepayableApplication(Long applicationId) {
        Optional<Application> existedApplication = applicationRepository.findById(applicationId);
        if (existedApplication.isEmpty()) {
            return false;
        }

        if (existedApplication.get().getContractedAt() == null) {
            return false;
        }

        Optional<Entry> existedEntry = entryRepository.findByApplicationId(applicationId);
        return existedEntry.isPresent();
    }
}
