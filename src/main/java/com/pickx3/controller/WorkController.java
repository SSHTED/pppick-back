package com.pickx3.controller;

import com.pickx3.domain.entity.work_package.Work;
import com.pickx3.domain.entity.work_package.WorkImg;
import com.pickx3.domain.entity.work_package.dto.work.WorkForm;
import com.pickx3.domain.entity.work_package.dto.work.WorkResponseDTO;
import com.pickx3.domain.entity.work_package.dto.work.WorkUpdateForm;
import com.pickx3.service.WorkImgService;
import com.pickx3.service.WorkService;
import com.pickx3.util.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

//@Tag(name = "상품 CRUD", description = "상품 관련 API")
@Slf4j
@RequestMapping("/works")
@RestController
public class WorkController {
    @Autowired
    private WorkService workService;

    @Autowired
    private WorkImgService workImgService;

    /*
    * 상품 등록
    * */
    @Operation(summary = "상품 정보 저장", description = "샘플 데이터 : <br>{ <br>&nbsp; &nbsp; files: 확장자가 jpg,png인 파일,<br>" +
            "&nbsp; &nbsp; workDesc : 만화 설명입니다,<br>" +
            "&nbsp; &nbsp; workName : 만화, <br>" +
            "&nbsp; &nbsp; workPrice: 50000, <br>" +
            "&nbsp; &nbsp; workerNum :2 <br>}")
    @PostMapping(consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createWork(@ModelAttribute WorkForm workForm){
        ApiResponseMessage result;
        HashMap data = new HashMap<>();
        try{
            log.info("===========상품명=============" +  workForm.toString().toString());
            Work work = workService.createWork(workForm);
            List<MultipartFile> files = workForm.getFiles();
            List<WorkImg> workImages = workImgService.uploadWorkImg(files, work);

            data.put("workNum",work.getWorkNum());
            data.put("workerNum",work.getUserInfo().getId());
            data.put("workName",work.getWorkName());
            data.put("workPrice",work.getWorkPrice());
            data.put("workDesc",work.getWorkDesc());
            data.put("workImages", workImages);
//            data.put("workImg", workForm.getFiles());

            result = new ApiResponseMessage(true, "Success", "200", "",data);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 회원별 상품 목록 조회
     * @param workerNum
     * @return
     */
    @Operation(summary = "회원별 상품 목록 조회", description = "샘플 데이터 =  workerNum : 2")
    @GetMapping("/users/{workerNum}")
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    public List<WorkResponseDTO> getWorks(@Parameter(description = "회원 고유 아이디", required = true, example = "1") @PathVariable(value = "workerNum") Long workerNum){
        ApiResponseMessage result;
        HashMap data = new HashMap<>();
        try{
            List<WorkResponseDTO> works = workService.getWorks(workerNum);
            data.put("works",works);

            result = new ApiResponseMessage(true, "Success", "200", "",data);
            return works;
//            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
//            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            return null;
        }
    }


    /**
     * 상품 상세정보 조회
     * @param workNum
     * @return
     */
    @Operation(summary = "상품 상세 정보 조회", description = "샘플데이터 = workNum : 1")
    @GetMapping("/{workNum}")
    public ResponseEntity<?> getWorkInfo(@PathVariable(value = "workNum") Long workNum){
        ApiResponseMessage result;
        try{
            HashMap data = new HashMap();
            data.put("workInfo", workService.getWorkInfo(workNum));
            result = new ApiResponseMessage(true, "Success" ,"200", "", data );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
     }


    /**
     * 상품 정보 수정
     * @param workUpdateForm
     * @return
     */
    @Operation(summary = "상품 정보 수정",  description = "샘플 데이터 : <br>{ <br> &nbsp; &nbsp; files: 확장자가 jpg,png인 파일 ,<br>" +
            "&nbsp; &nbsp; workDesc : 만화 그려드려요 ,<br>" +
            "&nbsp; &nbsp; workName : 만화 그려드립니다, <br>" +
            "&nbsp; &nbsp; workPrice: 70000, <br>" +
            "&nbsp; &nbsp; workerNum :2 <br>} <br>" +
            "@PutMapping은 파일 전송을 지원하지않아, @Postmapping으로 변경했습니다!")
    @PostMapping(value = "/edit", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateWork(@ModelAttribute WorkUpdateForm workUpdateForm){
        ApiResponseMessage result;
        try{
            HashMap data = new HashMap();
            Work work = workService.updateWork(workUpdateForm);

            workImgService.removeWorkImages(workUpdateForm.getWorkNum());

            List<MultipartFile> files = workUpdateForm.getFiles();
            List<WorkImg> workImages = workImgService.uploadWorkImg(files, work);

            data.put("workNum",work.getWorkNum());
            data.put("workerNum",work.getUserInfo().getId());
            data.put("workName",work.getWorkName());
            data.put("workPrice",work.getWorkPrice());
            data.put("workDesc",work.getWorkDesc());
            data.put("workImages", workImages);

            result = new ApiResponseMessage(true, "Success" ,"200", "", data );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new ApiResponseMessage(false, "Error", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 상품 정보 삭제
     * @param workNum
     * @return
     */
    @Operation(summary = "상품 정보 삭제", description = "delete api 이용할 경우 상품 신규로 등록 시 결과값으로 나온 고유번호로 이용 부탁드립니다.<br>" +
            "고유번호 ex)WorkNum")
    @DeleteMapping("/{workNum}")
    public ResponseEntity<?> removeWork(@PathVariable(value = "workNum") Long workNum){
        ApiResponseMessage result;
        try{
            workImgService.removeWorkImages(workNum);
            workService.removeWork(workNum);
            result = new ApiResponseMessage(true, "Success" ,"200", "", null );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new ApiResponseMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
