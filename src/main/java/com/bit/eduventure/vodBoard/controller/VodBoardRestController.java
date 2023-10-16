package com.bit.eduventure.vodBoard.controller;

import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.objectStorage.service.ObjectStorageService;
import com.bit.eduventure.validate.ValidateService;
import com.bit.eduventure.vodBoard.dto.*;
import com.bit.eduventure.vodBoard.entity.VodBoard;
import com.bit.eduventure.vodBoard.entity.VodBoardComment;
import com.bit.eduventure.vodBoard.entity.VodBoardFile;
import com.bit.eduventure.vodBoard.service.VodBoardCommentService;
import com.bit.eduventure.vodBoard.service.VodBoardLikeService;
import com.bit.eduventure.vodBoard.service.VodBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vod")
@RequiredArgsConstructor
@Slf4j
public class VodBoardRestController {
    private final VodBoardService vodBoardService;
    private final ObjectStorageService objectStorageService;
    private final VodBoardCommentService vodBoardCommentService;
    private final VodBoardLikeService vodBoardLikeService;
    private final UserService userService;
    private final ValidateService validateService;

    @PostMapping("/board")
    public ResponseEntity<?> insertBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestPart(value = "boardDTO", required = false) VodBoardDTO boardDTO,
                                         @RequestPart(value = "videoFile", required = false) MultipartFile videoFile,
                                         @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                         @RequestPart(value = "fileList", required = false) MultipartFile[] fileList) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        List<VodBoardFile> uploadFileList = new ArrayList<>();
        String saveName;

        int userNo = customUserDetails.getUser().getId();
        User user = userService.findById(userNo);
        validateService.validateTeacherAndAdmin(user);

        boardDTO.setUserDTO(user.EntityToDTO());

        if (fileList != null) {
            for (MultipartFile file : fileList) {
                VodBoardFile boardFile = new VodBoardFile();
                saveName = objectStorageService.uploadFile(file);
                boardFile.setVodOriginName(file.getOriginalFilename());
                boardFile.setVodSaveName(saveName);
                uploadFileList.add(boardFile);
            }
        }

        //메인 비디오 저장
        if (videoFile != null) {
            saveName = objectStorageService.uploadFile(videoFile);
            boardDTO.setSavePath(objectStorageService.getObjectSrc(saveName));
            boardDTO.setOriginPath(videoFile.getOriginalFilename());
            boardDTO.setObjectPath(saveName);
        }

        //섬네일 저장
        if (thumbnail != null) {
            saveName = objectStorageService.uploadFile(thumbnail);
            boardDTO.setSaveThumb(objectStorageService.getObjectSrc(saveName));
            boardDTO.setOriginThumb(thumbnail.getOriginalFilename());
            boardDTO.setObjectThumb(saveName);
        } else {
            saveName = "edu-venture.png";
            boardDTO.setSaveThumb(objectStorageService.getObjectSrc(saveName));
            boardDTO.setOriginThumb(saveName);
        }

        VodBoard board = boardDTO.DTOTOEntity();
        board.setUser(user);

        vodBoardService.insertBoard(board, uploadFileList);

        responseDTO.setItem("등록되었습니다.");
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    //강의 목록(제목, 강사, 영상 다 포함됨)
    @GetMapping("/board-list")
    public ResponseEntity<?> getList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDTO<VodBoardDTO> responseDTO = new ResponseDTO<>();

        List<VodBoard> vodBoardList = vodBoardService.getVodBoardList();

        List<VodBoardDTO> vodBoardDTOList = vodBoardList.stream()
                .map(VodBoard::EntityToDTO)
                .collect(Collectors.toList());

        //데이터, 통신 오류나 상태 코드 등을 담기 위해서 responseDTO를 선언하고 사용한다.
        responseDTO.setItems(vodBoardDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO); // 파일 목록을 보여줄 뷰 페이지로 이동
    }

    //상세 페이지 보여주기
    @GetMapping("/board/{boardNo}")
    public ResponseEntity<?> getBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @PathVariable int boardNo) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        int userNo = customUserDetails.getUser().getId();
        VodBoard board = vodBoardService.getBoard(boardNo);

        VodBoardDTO returnBoardDTO = board.EntityToDTO();
        returnBoardDTO.setLikeCount(vodBoardLikeService.getLikeCount(boardNo));
        returnBoardDTO.setLikeStatus(vodBoardLikeService.getLikeStatue(boardNo, userNo));

        List<VodBoardFile> boardFileList = vodBoardService.getBoardFileList(boardNo); //첨부파일 첨가

        List<VodBoardFileDTO> boardFileDTOList = new ArrayList<>();
        if (!boardFileList.isEmpty()) {
            //DTO형태의 list 선언해주기 하나씩 꺼내서 엔티티형태를 DTO형태의 list에 넣어주기
            boardFileDTOList = boardFileList.stream()
                    .map(VodBoardFile::EntityToDTO)
                    .collect(Collectors.toList());
        }

        List<VodBoardCommentDTO> commentList = vodBoardCommentService.getAllCommentList(boardNo);

        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("board", returnBoardDTO); //키 밸류값 게시글dto
        returnMap.put("boardFileList", boardFileDTOList); //첨부파일dto
        returnMap.put("commentList", commentList); //댓글

        responseDTO.setItem(returnMap);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/board/{boardNo}") // 수정 기능
    public ResponseEntity<?> updateVodBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable int boardNo,
                                            @RequestPart(value = "boardDTO", required = false) VodBoardDTO updatedBoardDTO,
                                            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile,
                                            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                            @RequestPart(value = "fileList", required = false) MultipartFile[] fileList) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        String saveName;

        //게시글 작성자와 수정자 비교
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        // 기존에 등록된 첨부파일 정보 가져오기
        List<VodBoardFile> existingFileList = vodBoardService.getBoardFileList(boardNo);
        // 기존에 등록된 파일 삭제
        vodBoardService.deleteAllFile(boardNo);
        for (VodBoardFile existingFile : existingFileList) {
            objectStorageService.deleteObject(existingFile.getVodSaveName());
        }

        // 새로 업로드한 파일 저장
        List<VodBoardFile> uploadFileList = new ArrayList<>();
        if (fileList != null) {
            for (MultipartFile file : fileList) {
                if (!file.isEmpty()) {
                    VodBoardFile boardFile = new VodBoardFile();
                    saveName = objectStorageService.uploadFile(file);
                    boardFile.setVodOriginName(file.getOriginalFilename());
                    boardFile.setVodSaveName(saveName);
                    uploadFileList.add(boardFile);
                }
            }
        }

        //메인 비디오 저장
        if (videoFile != null) {
            saveName = objectStorageService.uploadFile(videoFile);
            updatedBoardDTO.setSavePath(objectStorageService.getObjectSrc(saveName));
            updatedBoardDTO.setOriginPath(videoFile.getOriginalFilename());
            updatedBoardDTO.setObjectThumb(saveName);
        }

        //섬네일 저장
        if (thumbnail != null) {
            saveName = objectStorageService.uploadFile(thumbnail);
            updatedBoardDTO.setSaveThumb(objectStorageService.getObjectSrc(saveName));
            updatedBoardDTO.setOriginThumb(thumbnail.getOriginalFilename());
            updatedBoardDTO.setObjectThumb(saveName);
        } else {
            saveName = "edu-venture.png";
            updatedBoardDTO.setSaveThumb(objectStorageService.getObjectSrc(saveName));
            updatedBoardDTO.setOriginThumb(saveName);
        }

        // 새로 업로드한 파일 등록
        vodBoardService.updateVodBoard(boardNo, updatedBoardDTO.DTOTOEntity());

        responseDTO.setItem("수정되었습니다.");
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/board/{boardNo}") //삭제 기능
    public ResponseEntity<?> deleteVodBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable int boardNo) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));
        VodBoard vodBoard = vodBoardService.getBoard(boardNo);


        List<VodBoardFile> boardFileList = vodBoardService.getBoardFileList(boardNo);

        if (boardFileList != null) {
            boardFileList.stream().forEach(vodBoardFile -> {
                objectStorageService.deleteObject(vodBoardFile.getVodSaveName());
            });
        }

        //DB에 있는 첨부파일 리스트 삭제
        vodBoardService.deleteAllFile(boardNo);
        //게시물에 달려있는 댓글 삭제
        vodBoardCommentService.deleteCommentVodNo(boardNo);
        //게시물 좋아요 삭제
        vodBoardLikeService.deleteVodBoard(boardNo);
        //오브젝트 스토리지 파일 삭제
        if (StringUtils.hasText(vodBoard.getObjectPath())) {
            objectStorageService.deleteObject(vodBoard.getObjectPath());
        }
        if (StringUtils.hasText(vodBoard.getOriginThumb())) {
            objectStorageService.deleteObject(vodBoard.getObjectThumb());
        }


        //게시물 삭제
        vodBoardService.deleteVodBoard(boardNo);


        responseDTO.setItem("삭제되었습니다.");
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }
    // ---------------------------------------- 댓글 ----------------------------------------

    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody VodBoardCommentDTO vodBoardCommentDTO) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        int userNo = customUserDetails.getUser().getId();
        User user = userService.findById(userNo);
        vodBoardCommentDTO.setUserDTO(user.EntityToDTO());
        vodBoardCommentService.addComment(vodBoardCommentDTO);

        List<VodBoardCommentDTO> commentList =
                vodBoardCommentService.getAllCommentList(vodBoardCommentDTO.getVodNo());

        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("commentList", commentList);
        responseDTO.setItem(returnMap);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/comment")
    public ResponseEntity<?> modifyComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody VodBoardCommentDTO updateComment) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        int userNo = customUserDetails.getUser().getId();
        VodBoardComment originComment = vodBoardCommentService.getComment(updateComment.getId());

        if (userNo != originComment.getUser().getId()) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        vodBoardCommentService.modifyComment(originComment, updateComment);

        List<VodBoardCommentDTO> commentList =
                vodBoardCommentService.getAllCommentList(updateComment.getVodNo());

        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("commentList", commentList);
        responseDTO.setItem(returnMap);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/comment/{commentNo}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable int commentNo) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        int userNo = customUserDetails.getUser().getId();
        VodBoardComment originComment = vodBoardCommentService.getComment(commentNo);

        if (userNo != originComment.getUser().getId()) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        vodBoardCommentService.deleteComment(commentNo);

        List<VodBoardCommentDTO> commentList =
                vodBoardCommentService.getAllCommentList(originComment.getVodNo());

        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("commentList", commentList);
        responseDTO.setItem(returnMap);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    // ---------------------------------------- 좋아요 ----------------------------------------

    // 좋아요 등록
    @GetMapping("/like/{vodNo}")
    public ResponseEntity<?> likeVodBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @PathVariable int vodNo) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();

        int userNo = customUserDetails.getUser().getId();

        vodBoardLikeService.likeVodBoard(vodNo, userNo);

        responseDTO.setItem("좋아요 등록");
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    // 좋아요 취소
    @GetMapping("/unlike/{vodNo}")
    public ResponseEntity<?> unlikeVodBoard(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable int vodNo) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        int userNo = customUserDetails.getUser().getId();

        vodBoardLikeService.unlikeVodBoard(vodNo, userNo);

        responseDTO.setItem("좋아요 취소");
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

//    ---------------------------------------- 좋아요 ----------------------------------------

    @GetMapping("/page/board-list")
    public ResponseEntity<?> getBoardList(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "category", required = false, defaultValue = "all") String category,
                                          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDTO<VodBoardDTO> responseDTO = new ResponseDTO<>();

        Page<VodBoard> pageBoard = vodBoardService.getBoardPage(page, category, keyword);

        Page<VodBoardDTO> pageList = new PageImpl<>(
                pageBoard.get().map(VodBoard::EntityToDTO).collect(Collectors.toList()),
                pageBoard.getPageable(),
                pageBoard.getTotalElements()
        );


        responseDTO.setPageItems(pageList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);

    }
}
