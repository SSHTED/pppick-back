package com.pickx3.domain.entity.work_package;

import com.pickx3.domain.entity.work_package.dto.orders.OrderStatus;
import com.pickx3.domain.entity.work_package.dto.pay.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentNum;
    private String merchantUid;
    private String pg;
    private int paymentCount;
    private int paymentPrice;
    private String payMethod;
    private LocalDateTime paymentDate;
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    public void updateStatus(PaymentStatus status){
        this.paymentStatus = status;
    }

//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name="payerNum")
//    private User user;
//
//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name="workNum")
//    private Work work;
}
