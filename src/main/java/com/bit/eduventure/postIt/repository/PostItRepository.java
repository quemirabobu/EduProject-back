package com.bit.eduventure.postIt.repository;

import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.postIt.entity.PostItEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostItRepository extends JpaRepository<PostItEntity, Long> {
    List<PostItEntity> findByReceiverAndRepliedOrderBySentDateDesc(User receiver, boolean replied);
}
