package com.bit.eduventure.quiz;

import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.objectStorage.service.ObjectStorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;
@RequiredArgsConstructor
@RestController
@RequestMapping("/quiz")
public class ControllerQuizBoard {

    private final ObjectStorageService objectStorageService;
    private final QuizBoardService quizBoardService;
    private final UserService userService;
    private final QuizUserHistoryService quizUserHistoryService;
//    @Autowired
//    private RepositoryQuizBoard repositoryQuizBoard;

    @GetMapping("/board-list")
    public ResponseEntity<?> getBoardList(@PageableDefault(page = 0, size = 10, sort = "boardNo", direction = Sort.Direction.DESC) Pageable pageable,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @RequestParam(value = "searchCondition", required = false) String searchCondition,
                                          @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
        ResponseDTO<QuizBoardDTO> responseDTO = new ResponseDTO<>();

        try {
            searchCondition = searchCondition == null ? "all" : searchCondition;
            searchKeyword = searchKeyword == null ? "" : searchKeyword;

            Page<QuizBoard> pageBoard = quizBoardService.getBoardList(pageable, searchCondition, searchKeyword);

            //김은석 바보

            Page<QuizBoardDTO> pageBoardDTO = pageBoard.map(board ->
                    QuizBoardDTO.builder()
                            .boardNo(board.getBoardNo())
                            .boardTitle(board.getBoardTitle())
                            .boardWriter(board.getBoardWriter()).claName(board.getClaName()).option1(board.getOption1()).option2(board.getOption2()).option3(board.getOption3()).option4(board.getOption4()).answer(board.getAnswer()).grossRightAnswer(board.getGrossRightAnswer()).grossSample(board.getGrossSample())
                            .boardContent(board.getBoardContent())
                            .boardRegdate(board.getBoardRegdate().toString())
                            .boardCnt(board.getBoardCnt())
                            .build()
            );

//            List<Board> boardList = boardService.getBoardList();
//
//            List<BoardDTO> boardDTOList = new ArrayList<>();
//
//            for(Board board : boardList) {
//                boardDTOList.add(board.EntityToDTO());
//            }

            responseDTO.setPageItems(pageBoardDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            System.out.println(pageBoardDTO);
            return ResponseEntity.ok().body(responseDTO);

        } catch(Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }





    //multipart form 데이터 형식을 받기 위해 consumes 속성 지정
    @PostMapping(value = "/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> insertBoard(QuizBoardDTO quizBoardDTO,
                                         MultipartHttpServletRequest mphsRequest) {
        ResponseDTO<Map<String, String>> responseDTO =
                new ResponseDTO<Map<String, String>>();
//        String attachPath =
//                request.getSession().getServletContext().getRealPath("/")
//                + "/upload/";



        List<QuizBoardFile> uploadFileList = new ArrayList<QuizBoardFile>();

        try {
            //BoardEntity에 지정한 boardRegdate의 기본값은
            //기본생성자 호출할 때만 기본값으로 지정되는데
            //builder()는 모든 매개변수를 갖는 생성자를 호출하기 때문에
            //boardRegdate의 값이 null값으로 들어간다.
            QuizBoard quizBoard = QuizBoard.builder()
                    .boardTitle(quizBoardDTO.getBoardTitle()).option1(quizBoardDTO.getOption1()).option2(quizBoardDTO.getOption2()).option3(quizBoardDTO.getOption3()).option4(quizBoardDTO.getOption4()).answer(quizBoardDTO.getAnswer())
                    .boardContent(quizBoardDTO.getBoardContent()).claName(quizBoardDTO.getClaName()).grossRightAnswer(quizBoardDTO.getGrossRightAnswer()).grossSample(quizBoardDTO.getGrossSample())
                    .boardWriter(quizBoardDTO.getBoardWriter())
                    .boardRegdate(LocalDateTime.now())
                    .build();
            System.out.println("========================"+quizBoard.getBoardRegdate());

            Iterator<String> iterator = mphsRequest.getFileNames();

            while(iterator.hasNext()) {
                List<MultipartFile> fileList = mphsRequest.getFiles(iterator.next());

                for(MultipartFile file : fileList) {
                    if(!file.isEmpty()) {
                        //                        quizBoardFile = fileUtilsForObjectStorage.parseFileInfo(multipartFile, "quiz/");
                        QuizBoardFile quizBoardFile = new QuizBoardFile();

                        quizBoardFile = quizBoardService.saveQuizFile(file);

                        quizBoardFile.setQuizBoard(quizBoard);

                        uploadFileList.add(quizBoardFile);
                    }
                }
            }

            quizBoardService.insertBoard(quizBoard, uploadFileList);

            Map<String, String> returnMap =
                    new HashMap<String, String>();

            returnMap.put("msg", "정상적으로 저장되었습니다.");

            responseDTO.setItem(returnMap);

            return ResponseEntity.ok().body(responseDTO);
        } catch(Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

//    수정할때 boardDTO를 quizBoardDTO로 바꿨다
    @PutMapping(value = "/board")
    public ResponseEntity<?> updateBoard(@RequestPart(value = "quizBoardDTO") QuizBoardDTO quizBoardDTO,
                                         @RequestPart(value = "uploadFiles", required = false) MultipartFile[] uploadFiles,
                                         @RequestPart(value = "changeFileList", required = false) MultipartFile[] changeFileList,
                                         @RequestPart(value = "originFileList", required = false) String originFileList)
            throws Exception {
        System.out.println(quizBoardDTO);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        List<QuizBoardFileDTO> originFiles = null;

        if(originFileList != null) {
            originFiles = new ObjectMapper().readValue(originFileList,
                    new TypeReference<List<QuizBoardFileDTO>>() {
                    });
        }
        System.out.println("여기까지 온건가 182");

        //DB에서 수정, 삭제, 추가 될 파일 정보를 담는 리스트
        List<QuizBoardFile> uFileList = new ArrayList<QuizBoardFile>();

        try {
            System.out.println("트라이도 못들어왔따고?");
            QuizBoard quizBoard = quizBoardDTO.DTOToEntity();
            System.out.println("여기까지왓나 189");
            if(originFiles != null) {
                //파일 처리
                for (int i = 0; i < originFiles.size(); i++) {
                    //수정되는 파일 처리
                    if (originFiles.get(i).getBoardFileStatus().equals("U")) {
                        for (int j = 0; j < changeFileList.length; j++) {
                            if (originFiles.get(i).getNewFileName().equals(
                                    changeFileList[j].getOriginalFilename())) {
                                QuizBoardFile quizBoardFile = new QuizBoardFile();

                                MultipartFile file = changeFileList[j];

//                                quizBoardFile = fileUtilsForObjectStorage.parseFileInfo(file, "quiz/");
                                quizBoardFile = quizBoardService.saveQuizFile(file);
                                System.out.println("여긴가");
                                quizBoardFile.setQuizBoard(quizBoard);
                                quizBoardFile.setBoardFileNo(originFiles.get(i).getBoardFileNo());
                                quizBoardFile.setBoardFileStatus("U");

                                uFileList.add(quizBoardFile);
                            }
                        }
                        //삭제되는 파일 처리
                    } else if (originFiles.get(i).getBoardFileStatus().equals("D")) {
                        QuizBoardFile boardFile = new QuizBoardFile();

                        boardFile.setQuizBoard(quizBoard);
                        boardFile.setBoardFileNo(originFiles.get(i).getBoardFileNo());
                        boardFile.setBoardFileStatus("D");

                        //실제 파일 삭제


                        uFileList.add(boardFile);
                    }
                }
            }
            System.out.println("여기까지 온건가 226");

            //추가된 파일 처리
            if(uploadFiles != null && uploadFiles.length > 0) {
                for(int i = 0; i < uploadFiles.length; i++) {
                    MultipartFile file = uploadFiles[i];

                    if(file.getOriginalFilename() != null &&
                            !file.getOriginalFilename().equals("")) {
                        QuizBoardFile quizBoardFile = new QuizBoardFile();

//                        quizBoardFile = fileUtilsForObjectStorage.parseFileInfo(file, "quiz/");
                        quizBoardFile = quizBoardService.saveQuizFile(file);

                        quizBoardFile.setQuizBoard(quizBoard);
                        quizBoardFile.setBoardFileStatus("I");

                        uFileList.add(quizBoardFile);
                    }
                }
            }
            System.out.println("업데이트하기 일보직전");
            quizBoardService.updateBoard(quizBoard, uFileList);

            Map<String, Object> returnMap = new HashMap<>();

            QuizBoard updateBoard = quizBoardService.getBoard(quizBoard.getBoardNo());
            List<QuizBoardFile> updateBoardFileList =
                    quizBoardService.getBoardFileList(quizBoard.getBoardNo());

            QuizBoardDTO returnBoardDTO = updateBoard.EntityToDTO();

            List<QuizBoardFileDTO> boardFileDTOList = new ArrayList<>();

            for(QuizBoardFile boardFile : updateBoardFileList) {
                QuizBoardFileDTO boardFileDTO = boardFile.EntityToDTO();
                boardFileDTOList.add(boardFileDTO);
            }

            returnMap.put("board", returnBoardDTO);
            returnMap.put("boardFileList", boardFileDTOList);

            responseDTO.setItem(returnMap);

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("애러가났다.");
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping("/board/{boardNo}")
    public ResponseEntity<?> deleteBoard(@PathVariable int boardNo) {
        ResponseDTO<Map<String, String>> responseDTO =
                new ResponseDTO<Map<String, String>>();
        try {
            quizBoardService.deleteBoard(boardNo);

            quizBoardService.deleteQuizFileList(boardNo);
            System.out.println("지우러 들어왔음.");
            System.out.println(boardNo);
            Map<String, String> returnMap = new HashMap<String, String>();
            returnMap.put("msg", "정상적으로 삭제되었습니다.");
            responseDTO.setItem(returnMap);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/board/{boardNo}")
    public ResponseEntity<?> getBoard(@PathVariable int boardNo) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        try {
            QuizBoard quizBoard = quizBoardService.getBoard(boardNo);

            QuizBoardDTO returnBoardDTO = quizBoard.EntityToDTO();

            List<QuizBoardFile> boardFileList = quizBoardService.getBoardFileList(boardNo);

            List<QuizBoardFileDTO> boardFileDTOList = new ArrayList<>();

            for (QuizBoardFile boardFile : boardFileList) {
                QuizBoardFileDTO boardFileDTO = boardFile.EntityToDTO();
                boardFileDTOList.add(boardFileDTO);
            }

            Map<String, Object> returnMap = new HashMap<>();

            returnMap.put("board", returnBoardDTO);
            returnMap.put("boardFileList", boardFileDTOList);

            responseDTO.setItem(returnMap);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/answerwrong")
    public ResponseEntity<?> gotWrongAnswer(@RequestBody Map<String, Integer> request) {
        int boardNo = request.get("boardNo"); // Map에서 boardNo 값을 가져옵니다.
        ResponseDTO<QuizBoardDTO> responseDTO = new ResponseDTO<>();
        System.out.println("answerwrong에 들어옴");
        System.out.println(request);
        try {
           quizBoardService.plussGrossSample(boardNo);
           QuizBoard quizBoard = quizBoardService.getBoard(boardNo);
            QuizBoardDTO returnBoardDTO = quizBoard.EntityToDTO();
            responseDTO.setItem(returnBoardDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @PostMapping("/answerright")
    public ResponseEntity<?> gotRightAnswer(@RequestBody Map<String, Integer> request) {
        int boardNo = request.get("boardNo"); // Map에서 boardNo 값을 가져옵니다.
        ResponseDTO<QuizBoardDTO> responseDTO = new ResponseDTO<>();
        System.out.println("answerwrong에 들어옴");
        System.out.println(request);
        try {
            quizBoardService.plussGrossRightAnswer(boardNo);
            QuizBoard quizBoard = quizBoardService.getBoard(boardNo);
            QuizBoardDTO returnBoardDTO = quizBoard.EntityToDTO();
            responseDTO.setItem(returnBoardDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/increaseuserscore")
    public ResponseEntity<?> increasescore(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println(customUserDetails);
        System.out.println("토큰으로  사람 불러오는거 들어옴");
        System.out.println(customUserDetails.getUser().getUserId());
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        User userme;

        userme = userService.findByUserId(customUserDetails.getUser().getUserId());

userService.increaseuserscore(customUserDetails.getUser().getId());

        UserDTO userDTO = userme.EntityToDTO();
        userDTO.setUserPw(userDTO.getUserPw());
        try {

            responseDTO.setItem(userDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


    @PostMapping("/registerquizhistory")
    public ResponseEntity<?> registerUserQuizHistory(@RequestBody QuizUserHistoryDTO quizUserHistoryDTO) {
        System.out.println(quizUserHistoryDTO);
        System.out.println(quizUserHistoryDTO);
        ResponseDTO<QuizUserHistoryDTO> responseDTO = new ResponseDTO<>();
        QuizUserHistory quizUserHistory = quizUserHistoryDTO.DTOToEntity();
        QuizUserHistory quizUserHistory1 =  quizUserHistoryService.register(quizUserHistory);
        try {

            responseDTO.setItem(quizUserHistory1.EntityToDTO());
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/getallquizhistory")
    public ResponseEntity<?> getallquizhistory(@RequestBody QuizUserHistoryDTO quizUserHistoryDTO) {
        System.out.println("겟올퀴즈 히스토리 들오옴");
        System.out.println(quizUserHistoryDTO);
        ResponseDTO<QuizUserHistoryDTO> responseDTO = new ResponseDTO<>();

        QuizUserHistory quizUserHistory = quizUserHistoryDTO.DTOToEntity();
        QuizUserHistory quizUserHistory1 =  quizUserHistoryService.findIfExist(quizUserHistory);
        System.out.println(quizUserHistory1);
        try {

            responseDTO.setItem(quizUserHistory1.EntityToDTO());
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }





}
