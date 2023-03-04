package com.pickx3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pickx3.domain.dto.*;
import com.pickx3.domain.entity.post_package.dto.*;
import com.pickx3.domain.entity.user_package.User;
import com.pickx3.service.post_package.PostService;
import com.pickx3.service.post_package.PostImgService;
import com.pickx3.service.UserService;
import com.pickx3.util.rsMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final PostImgService postImgService;
    private final UserService userService;

    @Operation(summary = "작가별 문의게시판에 게시글 등록" , description = "작가 유저 번호 = postBoardNum <br><br>test example:<br>postBoardNum - 2, <br> files - jpg/jpeg/png 파일,<br> postContent - 일러스트 가격 문의 드립니다. ,<br> postPwd - test,<br> postTitle - 인물도 첨부사진처럼 작업 가능한가요?,<br>userNum - 3")
    @PostMapping(path = "/board/{postBoardNum}/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@PathVariable Long postBoardNum, @ModelAttribute PostFileVO postFileVO) throws Exception {
        // Member id로 조회하는 메소드 존재한다고 가정하에 진행
        User user = userService.searchUserById(
                Long.parseLong(postFileVO.getUserNum()));

        PostCreateRequestDto requestDto =
                PostCreateRequestDto.builder()
                        .user(user)
                        .postBoardNum(postBoardNum)
                        .postTitle(postFileVO.getPostTitle())
                        .postContent(postFileVO.getPostContent())
                        .postPwd(postFileVO.getPostPwd())
                        .build();
        rsMessage result;

        try{
            HashMap data = new HashMap<>();
            data.put("postNum", postService.create(requestDto, postFileVO.getFiles()));
            result = new rsMessage(true, "Success" ,"200", "", data );
//            Long postNum = postService.create(requestDto, postFileVO.getFiles());
//            PostResponseDto postResponseDto = postService.searchByPostNum(postNum);
//            result = new rsMessage(true, "Success" ,"200", "", postResponseDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "게시글 수정", description = "게시글 수정 시 기존 게시글의 이미지는 삭제되고 수정 api로 보낸 이미지가 등록 됩니다. <br><br>test example:<br>postNum - 1,<br>files - jpg/jpeg/png 파일<br>postContent - 지금 작업 가능하신가요?,<br> postTitle - 안녕하세요 ")
    @PostMapping(path = "/post/{postNum}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@PathVariable Long postNum, @ModelAttribute PostUpdateRequestDto requestDto) throws Exception {
        rsMessage result;
        try{
            HashMap data = new HashMap<>();
            data.put("postNum", postService.update(postNum,requestDto));
            result = new rsMessage(true, "Success" ,"200", "", data);
//            Long newPostNum = postService.update(postNum,postPwd,requestDto);
//            PostResponseDto postResponseDto = postService.searchByPostNum(newPostNum);
//            result = new rsMessage(true, "Success" ,"200", "",postResponseDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    //개별 조회
    @Operation(summary = "게시글 상세 정보 조회", description = "test example:<br>postNum - 1")
    @GetMapping("/post/{postNum}")
    public ResponseEntity<?> searchById(@PathVariable Long postNum) {
        rsMessage result;
        try{
            PostResponseDto postResponseDto =postService.searchByPostNum(postNum);
            result = new rsMessage(true, "Success" ,"200", "", postResponseDto );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    //전체 조회(목록)
    @Operation(summary = "게시글 전체 조회")
    @GetMapping("/post")
    public ResponseEntity<?> searchAllDesc() {
        rsMessage result;
        try{
            List<PostListResponseDto> postListResponseDtos =  postService.searchAllDesc();
            result = new rsMessage(true, "Success" ,"200", "", postListResponseDtos );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    // 작가 번호(작가 userNum = postBoardNum)별 문의게시판 글 조회
    @Operation(summary = "작가별 문의게시판의 게시글 목록 조회", description = "작가 유저 번호 = postBoardNum <br><br>test example:<br>postBoardNum - 2")
    @GetMapping("/board/{postBoardNum}/post")
    public ResponseEntity<?> searchByPostBoardNum( @PathVariable Long postBoardNum) {
        rsMessage result;
        try{
            List<PostListResponseDto> postListResponseDtos =  postService.searchByPostBoardNum(postBoardNum);
            result = new rsMessage(true, "Success" ,"200", "", postListResponseDtos );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "게시글 삭제", description = "delete api 이용할 경우 post api로 등록하여 결과 값으로 나온 고유번호로 이용 부탁드립니다.<br>" +
            "고유번호 ex) postNum , portfolioNum, WorkNum")
    @DeleteMapping("/post/{postNum}")
    public ResponseEntity<?> delete(@PathVariable Long postNum) {
        rsMessage result;
        try{
            postService.delete(postNum);
            result = new rsMessage(true, "Success" ,"200", "", null );
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            result = new rsMessage(false, "", "400", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}