package com.codessquad.qna.repository;

import com.codessquad.qna.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface QuestionRepository extends CrudRepository<Question, Long> {

    Page<Question> findAllByDeletedFalse(Pageable pageable);

    Optional<Question> findByIdAndDeletedFalse(Long id);

}
