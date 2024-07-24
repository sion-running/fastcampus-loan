package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Judgement;
import com.fastcampus.loan.dto.JudgementDTO;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.JudgementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import static com.fastcampus.loan.dto.JudgementDTO.*;

@ExtendWith(MockitoExtension.class)
public class JudgementServiceTest {
    @InjectMocks
    private JudgementServiceImpl judgementService;

    @Mock
    private JudgementRepository judgementRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Spy
    private ModelMapper modelMapper;


    @Test
    void Should_ReturnResponseOfNewJudgementEntity_When_RequestNewJudgement() {
        Judgement judgmentEntity = Judgement.builder()
                .name("Member Kim")
                .applicationId(1L)
                .approvalAmount(BigDecimal.valueOf(50000000))
                .build();

        Application applicationEntity = Application.builder()
                .applicationId(1L)
                .build();

        Request request = Request.builder()
                .name("Member Kim")
                .applicationId(1L)
                .approvalAmount(BigDecimal.valueOf(50000000))
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.ofNullable(applicationEntity));
        when(judgementRepository.save(ArgumentMatchers.any(Judgement.class))).thenReturn(judgmentEntity);

        Response actual = judgementService.create(request);
        assertThat(actual.getName()).isSameAs(judgmentEntity.getName());
        assertThat(actual.getApplicationId()).isSameAs(judgmentEntity.getJudgementId());
        assertThat(actual.getApprovalAmount()).isSameAs(judgmentEntity.getApprovalAmount());
    }

    @Test
    void Should_Return_ResponseOfExistJudgementEntity_When_RequestExistJudgementId() {
        Judgement judgmentEntity = Judgement.builder()
                .judgementId(1L)
                .build();

        when(judgementRepository.findById(1L)).thenReturn(Optional.ofNullable(judgmentEntity));

        Response actual = judgementService.get(1L);

        assertThat(actual.getJudgementId()).isSameAs(1L);
    }

    @Test
    void Should_Return_ResponseOfExistJudgementEntity_When_RequestExistApplicationId() {
        Judgement judgmentEntity = Judgement.builder()
                .judgementId(1L)
                .build();

        Application applicationEntity = Application.builder()
                .applicationId(1L)
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.ofNullable(applicationEntity));
        when(judgementRepository.findByApplicationId(1L)).thenReturn(Optional.ofNullable(judgmentEntity));

        Response actual = judgementService.getJudgementOfApplication(1L);
        assertThat(actual.getJudgementId()).isEqualTo(1L);
    }

    @Test
    void Should_Return_UpdatedResponseOfExistJudgementEntity_When_RequestUpdateExistJudgementInfo() {
        Judgement judgmentEntity = Judgement.builder()
                .judgementId(1L)
                .name("member A")
                .approvalAmount(BigDecimal.valueOf(5000000))
                .build();

        Request request = Request.builder()
                .name("member new")
                .approvalAmount(BigDecimal.valueOf(10000000))
                .build();


        when(judgementRepository.findById(1L)).thenReturn(Optional.ofNullable(judgmentEntity));
        when(judgementRepository.save(ArgumentMatchers.any(Judgement.class))).thenReturn(judgmentEntity);

        Response actual = judgementService.update(1L, request);

        assertThat(actual.getJudgementId()).isEqualTo(1L);
        assertThat(actual.getName()).isEqualTo(request.getName());
        assertThat(actual.getApprovalAmount()).isEqualTo(request.getApprovalAmount());
    }

    @Test
    void Should_DeletedJudgementEntity_When_RequestDeleteExistJudgementInfo() {
        Judgement judgmentEntity = Judgement.builder()
                .judgementId(1L)
                .build();

        when(judgementRepository.findById(1L)).thenReturn(Optional.ofNullable(judgmentEntity));
        when(judgementRepository.save(ArgumentMatchers.any(Judgement.class))).thenReturn(judgmentEntity);

        judgementService.delete(1L);
        assertThat(judgmentEntity.getIsDeleted()).isTrue();

    }
}
