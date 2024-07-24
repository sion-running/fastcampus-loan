package com.fastcampus.loan.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Where(clause = "is_deleted = false")
public class Application extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long applicationId;

    @Column(columnDefinition = "varchar(12) DEFAULT NULL COMMENT '신청자'")
    private String name;

    @Column(columnDefinition = "varchar(13) DEFAULT NULL COMMENT '전화번호'")
    private String cellPhone;

    @Column(columnDefinition = "varchar(50) DEFAULT NULL COMMENT '신청자의 이메일'")
    private String email;

    @Column(columnDefinition = "decimal(5,4) DEFAULT NULL COMMENT '금리'")
    private BigDecimal interestRate;

    @Column(columnDefinition = "decimal(5,4) DEFAULT NULL COMMENT '취급 수수료'")
    private BigDecimal fee;

    @Column(columnDefinition = "datetime DEFAULT NULL COMMENT '만기 일자'")
    private LocalDateTime maturity;

    // 소숫점 두자리까지, 자연수는 열세자리
    @Column(columnDefinition = "decimal(15, 2) DEFAULT NULL COMMENT '대출 신청 금액'")
    private BigDecimal hopeAmount;

    @Column(columnDefinition = "datetime DEFAULT NULL COMMENT '신청 일자'")
    private LocalDateTime appliedAt;

    @Column(columnDefinition = "decimal(15,2) DEFAULT NULL COMMENT '승인 금액'") //심사가 완료되기 전까지는 금액이 없으므로 null 허용
    private BigDecimal approvalAmount;

    @Column(columnDefinition = "datetime DEFAULT NULL COMMENT '계약 일자'")
    private LocalDateTime contractedAt;
}
