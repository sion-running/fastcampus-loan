package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Application;
import com.fastcampus.loan.domain.Terms;
import com.fastcampus.loan.dto.ApplicationDTO;
import com.fastcampus.loan.dto.ApplicationDTO.*;
import com.fastcampus.loan.dto.CounselDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.repository.AcceptTermsRepository;
import com.fastcampus.loan.repository.ApplicationRepository;
import com.fastcampus.loan.repository.TermsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {
    @InjectMocks
    ApplicationServiceImpl applicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private TermsRepository termsRepository;

    @Mock
    private AcceptTermsRepository acceptTermsRepository;

    @Spy
    private ModelMapper modelMapper;

    @Test
    void Should_ReturnResponseOfNewApplicationEntity_When_RequestCreateApplication() {
        Application entity = Application.builder()
                .name("member A")
                .cellPhone("010-1111-2222")
                .email("abc@naver.com")
                .hopeAmount(BigDecimal.valueOf(50000000))
                .build();

        Request request = Request.builder()
                .name("member A")
                .cellPhone("010-1111-2222")
                .email("abc@naver.com")
                .hopeAmount(BigDecimal.valueOf(50000000))
                .build();

        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(entity);
        Response actual = applicationService.create(request);
        assertThat(actual.getHopeAmount()).isSameAs(entity.getHopeAmount());
        assertThat(actual.getName()).isSameAs(entity.getName());
    }

    @Test
    void Should_ReturnResponseOfExistApplicationEntity_When_RequestExistApplicationId() {
        Long findId = 1L;
        Application entity = Application.builder()
                .applicationId(1L)
                .build();

        when(applicationRepository.findById(findId)).thenReturn(Optional.of(entity));

        Response actual = applicationService.get(findId);
        assertThat(actual.getApplicationId()).isSameAs(findId);
    }

    @Test
    void Should_Return_UpdatedResponseOfExistEntity_When_RequestUpdateExistApplicationInfo() {
        Long findId = 1L;
        Application entity = Application.builder()
                .applicationId(1L)
                .hopeAmount(BigDecimal.valueOf(50000000))
                .build();

        Request request = Request.builder()
                        .hopeAmount(BigDecimal.valueOf(5000))
                                .build();

        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(entity);
        when(applicationRepository.findById(findId)).thenReturn(Optional.of(entity));

        Response actual = applicationService.update(findId, request);

        assertThat(actual.getApplicationId()).isSameAs(findId);
        assertThat(actual.getHopeAmount()).isSameAs(request.getHopeAmount());
    }

    @Test
    void Should_DeletedApplicationEntity_When_RequestDeleteExistApplicationInfo() {
        Long targetId = 1L;

        Application entity = Application.builder()
                                .applicationId(1L)
                                .build();

        when(applicationRepository.save(ArgumentMatchers.any(Application.class))).thenReturn(entity);
        when(applicationRepository.findById(targetId)).thenReturn(Optional.of(entity));

        applicationService.delete(targetId);
        assertThat(entity.getIsDeleted()).isSameAs(true);
    }

    @Test
    void Should_AddAcceptTerms_When_RequestAcceptTermsOfApplication() {
        Terms entityA = Terms.builder()
                .termsId(1L)
                .name("약관 1")
                .termsDetailUrl("https://aaa.bbb/1")
                .build();

        Terms entityB = Terms.builder()
                .termsId(2L)
                .name("약관 2")
                .termsDetailUrl("https://ccc.ddd/2")
                .build();

        List<Long> acceptTerms = Arrays.asList(1L, 2L);

        AcceptTerms request = AcceptTerms.builder()
                .acceptTermsIds(acceptTerms)
                .build();

        Long findId = 1L;

        when(applicationRepository.findById(findId)).thenReturn(Optional.ofNullable(Application.builder().build()));
        when(termsRepository.findAll(Sort.by(Sort.Direction.ASC, "termsId"))).thenReturn(Arrays.asList(entityA, entityB));
        when(acceptTermsRepository.save(ArgumentMatchers.any(com.fastcampus.loan.domain.AcceptTerms.class))).thenReturn(com.fastcampus.loan.domain.AcceptTerms.builder().build());

        Boolean actual = applicationService.acceptTerms(findId, request);
        assertThat(actual).isTrue();
    }

    @Test
    void Should_ThrowException_When_RequestNotAllAcceptTermsOfApplication() { // 모든 약관에 동의하지 않은경우 실패
        Terms entityA = Terms.builder()
                .termsId(1L)
                .name("약관 1")
                .termsDetailUrl("https://aaa.bbb/1")
                .build();

        Terms entityB = Terms.builder()
                .termsId(2L)
                .name("약관 2")
                .termsDetailUrl("https://ccc.ddd/2")
                .build();

        List<Long> acceptTerms = Arrays.asList(1L);

        AcceptTerms request = AcceptTerms.builder()
                .acceptTermsIds(acceptTerms)
                .build();

        Long findId = 1L;

        when(applicationRepository.findById(findId)).thenReturn(Optional.ofNullable(Application.builder().build()));
        when(termsRepository.findAll(Sort.by(Sort.Direction.ASC, "termsId"))).thenReturn(Arrays.asList(entityA, entityB));

        Assertions.assertThrows(BaseException.class, () -> applicationService.acceptTerms(findId, request));
    }

    @Test
    void Should_ThrowException_When_RequestNotExistAcceptTermsOfApplication() { // 존재하지 않는 약관에 대한 동의일 경우
        Terms entityA = Terms.builder()
                .termsId(1L)
                .name("약관 1")
                .termsDetailUrl("https://aaa.bbb/1")
                .build();

        Terms entityB = Terms.builder()
                .termsId(2L)
                .name("약관 2")
                .termsDetailUrl("https://ccc.ddd/2")
                .build();

        List<Long> acceptTerms = Arrays.asList(1L, 3L);

        AcceptTerms request = AcceptTerms.builder()
                .acceptTermsIds(acceptTerms)
                .build();

        Long findId = 1L;

        when(applicationRepository.findById(findId)).thenReturn(Optional.ofNullable(Application.builder().build()));
        when(termsRepository.findAll(Sort.by(Sort.Direction.ASC, "termsId"))).thenReturn(Arrays.asList(entityA, entityB));

        Assertions.assertThrows(BaseException.class, () -> applicationService.acceptTerms(findId, request));
    }
}


