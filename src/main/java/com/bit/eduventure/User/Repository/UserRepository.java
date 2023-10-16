package com.bit.eduventure.User.Repository;

import com.bit.eduventure.User.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

//update나 Delete가 발생했을 때 곧장 커밋 롤백 처리



@Transactional
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByUserId(String userId);
//    Optional<User> findById(int id);

    boolean existsByUserId(String userId);

    Page<User> findByUserNameContaining(String searchKeyword, Pageable pageable);

    Page<User> findByUserIdContaining(String searchKeyword, Pageable pageable);

    Page<User> findById(Integer usernoKeyword, Pageable pageable);

    Page<User> findByUserTypeContaining(String searchKeyword, Pageable pageable);

    Page<User> findByUserBus(Integer busKeyword, Pageable pageable);

    Page<User> findByUserTelContaining(String searchKeyword, Pageable pageable);

    Page<User> findByUserNameContainingOrUserIdContainingOrUserTypeContainingOrUserTelContaining(String searchKeyword, String searchKeyword1, String searchKeyword2, String searchKeyword3, Pageable pageable);

    //권한에 맞는 유저 리스트 가져오기
    List<User> findAllByUserType(String userType);

    Long countByUserTypeAndCourseCouNo(String userType, int courseId);

    List<User> findAllByCourseCouNo(int couNo);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.userScore = u.userScore + 1 WHERE u.id = ?1")
    void increaseuserscore(Integer id);

    Page<User> findAll(Specification<User> spec, Pageable pageable);

}
