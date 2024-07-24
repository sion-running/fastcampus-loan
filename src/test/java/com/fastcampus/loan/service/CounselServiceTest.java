package com.fastcampus.loan.service;

import com.fastcampus.loan.domain.Counsel;
import com.fastcampus.loan.dto.CounselDTO;
import com.fastcampus.loan.exception.BaseException;
import com.fastcampus.loan.exception.ResultType;
import com.fastcampus.loan.repository.CounselRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.fastcampus.loan.dto.CounselDTO.Request;
import com.fastcampus.loan.dto.CounselDTO.Response;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CounselServiceTest {
    @InjectMocks
    CounselServiceImpl counselService;

    @Mock
    private CounselRepository counselRepository;
    @Spy // model mapper는 각각 다른 오브젝트를 매핑해주는 유틸성 클래스이므로, 모킹처리를 하기 보다는 역할 자체를 순수하게 하기 위해서 spy 사용
    private ModelMapper modelMapper;

    @Test
    void Should_ReturnResponseOfNewCounselEntity_When_RequestCounsel() {
        Counsel entity = Counsel.builder()
                .name("member1")
                .cellPhone("010-1111-2222")
                .email("adb@naver.com")
                .memo("대출 해주세요.")
                .address("서울특별시 어딘가 어딘동")
                .addressDetail("701호")
                .zipCode("12345")
                .build();

        Request request = Request.builder()
                .name("member1")
                .cellPhone("010-1111-2222")
                .email("adb@naver.com")
                .memo("대출 해주세요.")
                .address("서울특별시 어딘가 어딘동")
                .addressDetail("701호")
                .zipCode("12345")
                .build();

        when(counselRepository.save(ArgumentMatchers.any(Counsel.class))).thenReturn(entity);

        Response actual = counselService.create(request);
        assertThat(actual.getName()).isSameAs(entity.getName());
    }

    @Test
    void Should_ReturnResponseOfExistingCounselEntity_When_RequestExistCounselId() {
        Long findId = 1L;

        Counsel entity = Counsel.builder()
                .counselId(1L)
                .build();

        when(counselRepository.findById(findId)).thenReturn(Optional.ofNullable(entity));

        Response actual = counselService.get(findId);
        assertThat(actual.getCounselId()).isSameAs(findId);
    }

    @Test
    void Should_ThrowException_When_RequestNonExistCounselId() {
        Long findId = 2L;

        when(counselRepository.findById(findId)).thenThrow(new BaseException(ResultType.SYSTEM_ERROR));
        Assertions.assertThrows(BaseException.class, () -> counselService.get(findId));
    }

    @Test
    void Should_ReturnUpdatedResponseOfExistCounselEntity_When_RequestUpdateExistCounselInfo() { // 있는 엔티티만 수정 가능하다
        Long findId = 1L;

        Counsel entity = Counsel.builder()
                .counselId(1L)
                .name("member1")
                .build();

        Request request = Request.builder()
                .name("member2")
                .build();

        when(counselRepository.save(ArgumentMatchers.any(Counsel.class))).thenReturn(entity);
        when(counselRepository.findById(findId)).thenReturn(Optional.ofNullable(entity));

        Response actual = counselService.update(findId, request);
        assertThat(actual.getCounselId()).isSameAs(findId);
        assertThat(actual.getName()).isSameAs(request.getName());
    }

    @Test
    void Should_DeletedCounselEntity_When_RequestDeleteExistCounselInfo() {
        Long targetId = 1L;

        Counsel entity = Counsel.builder()
                .counselId(1L)
                .build();

        when(counselRepository.save(ArgumentMatchers.any(Counsel.class))).thenReturn(entity);
        when(counselRepository.findById(targetId)).thenReturn(Optional.ofNullable(entity));

        counselService.delete(targetId);
        assertThat(entity.getIsDeleted()).isSameAs(true);
    }

}
