package com.pickx3.controller;

//import com.pickx3.domain.dto.UserUpdateRequestDto;

import com.pickx3.domain.entity.portfolio_package.Portfolio;
import com.pickx3.domain.entity.user_package.User;
import com.pickx3.domain.entity.work_package.Orders;
import com.pickx3.domain.entity.work_package.Payment;
import com.pickx3.domain.entity.work_package.Work;
import com.pickx3.domain.entity.work_package.dto.pay.PaymentResponseDTO;
import com.pickx3.domain.repository.*;
import com.pickx3.exception.ResourceNotFoundException;
import com.pickx3.security.CurrentUser;
import com.pickx3.security.UserPrincipal;
import com.pickx3.security.token.TokenProvider;
import com.pickx3.service.UserService;
import com.pickx3.util.rsMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
public class AdminController {

    private final UserRepository userRepository;;

    private final PortfolioRepository portfolioRepository;

    private final WorkRepository workRepository;
    private final PaymentRepository paymentRepository;

    @Operation(summary = "회원 관리 - 회원 정보 전체 조회" , description = "")
    @GetMapping("/admin/manage/user")
    public ResponseEntity<?> getUserList() {
        rsMessage result;
        try{
            List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            result = new rsMessage(true, "Success" ,"200", "", users);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "상품 관리 - 상품 정보 전체 조회" , description = "")
    @GetMapping("/admin/manage/work")
    public ResponseEntity<?> getWorkList() {
        rsMessage result;
        try{
            List<Work> works = workRepository.findAll(Sort.by(Sort.Direction.DESC, "workNum"));
            result = new rsMessage(true, "Success" ,"200", "", works);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "포트폴리오 관리 - 프트폴리오 정보 전체 조회" , description = "")
    @GetMapping("/admin/manage/portfolio")
    public ResponseEntity<?> getPList() {
        rsMessage result;
        try{
            List<Portfolio> portfolioss = portfolioRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            result = new rsMessage(true, "Success" ,"200", "", portfolioss);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "결제 관리 - 결제 정보 전체 조회" , description = "")
    @GetMapping("/admin/manage/payment")
    public ResponseEntity<?> getPayList() {
        rsMessage result;
        try{
            List<Object[]> resulto = paymentRepository.findSameMerchant();
            List<PaymentResponseDTO> paymentResponseDTOS = new ArrayList<>();
            for(Object[] o : resulto){
                Payment payment =  (Payment) o[0];
                Orders orders = (Orders) o[1];
                paymentResponseDTOS.add(new PaymentResponseDTO(payment,orders));
            }
            paymentResponseDTOS= paymentResponseDTOS.stream().sorted(Comparator.comparing(PaymentResponseDTO::getPaymentId).reversed()).collect(Collectors.toList());
            result = new rsMessage(true, "Success" ,"200", "", paymentResponseDTOS);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

}
