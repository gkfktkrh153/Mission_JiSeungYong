package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeablePersonRepository extends JpaRepository<LikeablePerson, Long> {

    List<LikeablePerson> findByFromInstaMemberIdAndToInstaMemberId(Long fromInstaMemberId, Long toInstaMemberId);

    List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId);
}
