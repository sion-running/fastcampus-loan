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

    @Override
    public Response create(Long applicationId, Request request) {
        // 밸런스 정보는 로우 하나로 업데이트한다. 따라서, applicationId로 조회되는 데이터가 있다면 새로 생성하면 안 됨
        if (balanceRepository.findByApplicationId(applicationId).isPresent()) {
            throw new BaseException(ResultType.SYSTEM_ERROR);
        }

        Balance balance = modelMapper.map(request, Balance.class);
        BigDecimal entryAmount = request.getEntryAmount();
        balance.setApplicationId(applicationId);
        balance.setBalance(entryAmount);

        Balance saved = balanceRepository.save(balance);
        return modelMapper.map(saved, Response.class);
    }
}
