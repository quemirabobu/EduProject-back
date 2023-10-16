package com.bit.eduventure.quiz;

import com.bit.eduventure.objectStorage.service.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class QuizBoardServiceImpl implements QuizBoardService {


    private RepositoryQuizBoard repositoryQuizBoard;

    private RepositoryQuizBoardFile repositoryQuizBoardFile;
    private ObjectStorageService objectStorageService;

    //생성자 주입
    @Autowired
    public QuizBoardServiceImpl(
            RepositoryQuizBoard repositoryQuizBoard,
            RepositoryQuizBoardFile repositoryQuizBoardFile,
            ObjectStorageService objectStorageService) {
        this.repositoryQuizBoard = repositoryQuizBoard;
        this.repositoryQuizBoardFile = repositoryQuizBoardFile;
        this.objectStorageService = objectStorageService;
    }

    @Override
    public QuizBoard getBoard(int boardNo) {
        if (repositoryQuizBoard.findById(boardNo).isEmpty())
            return null;

        return repositoryQuizBoard.findById(boardNo).get();
    }

    @Override
    public List<QuizBoard> getBoardList() {
        return repositoryQuizBoard.findAll();
    }

    @Override
    public void insertBoard(QuizBoard quizBoard, List<QuizBoardFile> uploadFileList) {
        repositoryQuizBoard.save(quizBoard);
        //변경사항 커밋 후 저징
        repositoryQuizBoard.flush();

        for (QuizBoardFile quizBoardFile : uploadFileList) {
            quizBoardFile.setQuizBoard(quizBoard);
            int boardFileNo = repositoryQuizBoardFile.findMaxFileNo(quizBoard.getBoardNo());
            quizBoardFile.setBoardFileNo(boardFileNo);

            repositoryQuizBoardFile.save(quizBoardFile);
        }
    }

    @Override
    public void updateBoard(QuizBoard quizBoard, List<QuizBoardFile> uFileList) {
        repositoryQuizBoard.save(quizBoard);

        if (uFileList.size() > 0) {
            for (int i = 0; i < uFileList.size(); i++) {
                if (uFileList.get(i).getBoardFileStatus().equals("U")) {
                    repositoryQuizBoardFile.save(uFileList.get(i));
                } else if (uFileList.get(i).getBoardFileStatus().equals("D")) {
                    repositoryQuizBoardFile.delete(uFileList.get(i));
                } else if (uFileList.get(i).getBoardFileStatus().equals("I")) {
                    //추가한 파일들은 boardNo은 가지고 있지만 boardFileNo가 없는 상태라
                    //boardFileNo를 추가
                    int boardFileNo = repositoryQuizBoardFile.findMaxFileNo(
                            uFileList.get(i).getQuizBoard().getBoardNo());

                    uFileList.get(i).setBoardFileNo(boardFileNo);

                    repositoryQuizBoardFile.save(uFileList.get(i));
                }
            }
        }
    }

    @Override
    public void deleteBoard(int boardNo) {
        repositoryQuizBoard.deleteById(boardNo);
        repositoryQuizBoard.flush();

    }

    @Override
    public List<QuizBoardFile> getBoardFileList(int boardNo) {

        return repositoryQuizBoardFile.findByQuizBoardBoardNo(boardNo);
    }

    @Override
    public Page<QuizBoard> getBoardList(Pageable pageable, String searchCondition, String searchKeyword) {
        if (searchCondition.equals("all")) {
            if (searchKeyword.equals("")) {
                return repositoryQuizBoard.findAll(pageable);
            } else {
                return repositoryQuizBoard.findByBoardTitleContainingOrBoardContentContainingOrBoardWriterContaining(searchKeyword, searchKeyword, searchKeyword, pageable);
            }
        } else {
            if (searchKeyword.equals("")) {
                return repositoryQuizBoard.findAll(pageable);
            } else {
                if (searchCondition.equals("title")) {
                    return repositoryQuizBoard.findByBoardTitleContaining(searchKeyword, pageable);
                } else if (searchCondition.equals("content")) {
                    return repositoryQuizBoard.findByBoardContentContaining(searchKeyword, pageable);
                } else if (searchCondition.equals("writer")) {
                    return repositoryQuizBoard.findByBoardWriterContaining(searchKeyword, pageable);
                } else {
                    return repositoryQuizBoard.findAll(pageable);
                }
            }
        }
    }

    @Override
    public void plussGrossSample(int boardNo) {
        repositoryQuizBoard.plusGrossSample(boardNo);
    }

    @Override
    public void plussGrossRightAnswer(int boardNo) {
        repositoryQuizBoard.plusGrossSampleAndRightAnswer(boardNo);

    }

    @Override
    public QuizBoardFile saveQuizFile(MultipartFile file) {
        QuizBoardFile quizBoardFile = new QuizBoardFile();
        String origin = file.getOriginalFilename(); //원본 파일명
        String saveName = objectStorageService.uploadFile(file);    //저장된 파일명
        String saveSrc = objectStorageService.getObjectSrc(saveName);   //파일주소

        quizBoardFile.setBoardFileOrigin(origin);
        quizBoardFile.setBoardFileName(saveName);
        quizBoardFile.setBoardFilePath(saveSrc);
        return quizBoardFile;
    }

    @Override
    public void deleteQuizFileList(int boardNo) {
        List<QuizBoardFile> boardFileList = getBoardFileList(boardNo);

        boardFileList.stream()
                .forEach(quizBoardFile -> {
                    objectStorageService.deleteObject(quizBoardFile.getBoardFileName());
                });
        repositoryQuizBoard.flush();

    }


}
