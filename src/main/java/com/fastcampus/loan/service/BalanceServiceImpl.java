package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Balance;
import com.fastcampus.loan.dto.BalanceDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.fastcampus.loan.dto.BalanceDTO.*;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final ModelMapper modelMapper;

    // 엔트리를 삭제하면 balance를 0으로 업데이트하는데, 엔트리를 다시 생성할 경우
    // 기존 로직에서는 balance가 존재하면 에러를 뱉도록 되어있어서 수정
    @Override
    public Response create(Long applicationId, Request request) {
        Balance balance = modelMapper.map(request, Balance.class);

        BigDecimal entryAmount = request.getEntryAmount();
        balance.setApplicationId(applicationId);
        balance.setBalance(entryAmount);

        // balance 존재 시 기존 값을 엎어친다
        balanceRepository.findByApplicationId(applicationId).ifPresent(b -> {
            balance.setBalanceId(b.getBalanceId());
            balance.setIsDeleted(b.getIsDeleted());
            balance.setCreatedAt(b.getCreatedAt());
            balance.setUpdatedAt(b.getUpdatedAt());
        });

        Balance saved = balanceRepository.save(balance);
        return modelMapper.map(saved, Response.class);
    }

//    @Override
//    public Response create(Long applicationId, Request request) {
//        // 밸런스 정보는 로우 하나로 업데이트한다. 따라서, applicationId로 조회되는 데이터가 있다면 새로 생성하면 안 됨
//        if (balanceRepository.findByApplicationId(applicationId).isPresent()) {
//            throw new BaseException(ResultType.SYSTEM_ERROR);
//        }
//
//        Balance balance = modelMapper.map(request, Balance.class);
//        BigDecimal entryAmount = request.getEntryAmount();
//        balance.setApplicationId(applicationId);
//        balance.setBalance(entryAmount);
//
//        Balance saved = balanceRepository.save(balance);
//        return modelMapper.map(saved, Response.class);
//    }

    @Override
    public Response update(Long applicationId, UpdateRequest request) {
        // balance가 있는지
        Balance balance = balanceRepository.findByApplicationId(applicationId).orElseThrow(() -> {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        BigDecimal beforeEntryAmount = request.getBeforeEntryAmount();
        BigDecimal afterEntryAmount = request.getAfterEntryAmount();
        BigDecimal updatedBalance = balance.getBalance();

        // before -> after 업데이트
        updatedBalance = updatedBalance.subtract(beforeEntryAmount).add(afterEntryAmount);
        balance.setBalance(updatedBalance);

        Balance updated = balanceRepository.save(balance);
        return modelMapper.map(balance, Response.class);
    }

    @Override
    public Response repaymentUpdate(Long applicationId, RepaymentRequest request) {
        Balance balance = balanceRepository.findByApplicationId(applicationId).orElseThrow(() -> {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        });

        BigDecimal updatedBalance = balance.getBalance();
        BigDecimal repaymentAmount = request.getRepaymentAmount();

        // 상환 타입에 따라 잔고 업데이트
        if (request.getType().equals(RepaymentRequest.RepaymentType.ADD)) {
            updatedBalance = updatedBalance.add(repaymentAmount);
        } else {
            updatedBalance = updatedBalance.subtract(repaymentAmount);
        }

        balance.setBalance(updatedBalance);

        Balance updated = balanceRepository.save(balance);
        return modelMapper.map(updated, Response.class);
    }
}
