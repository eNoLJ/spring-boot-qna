package com.codessquad.qna.service;

import com.codessquad.qna.exception.EntityNotFoundException;
import com.codessquad.qna.exception.ErrorMessage;
import com.codessquad.qna.exception.UserSessionException;
import com.codessquad.qna.model.Question;
import com.codessquad.qna.model.User;
import com.codessquad.qna.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void save(Question question, User sessionUser) {
        question.save(sessionUser);
        this.questionRepository.save(question);
    }

    public void update(Long id, Question question, User sessionUser) {
        Question targetQuestion = verifyQuestion(id, sessionUser);
        targetQuestion.update(question);
        this.questionRepository.save(targetQuestion);
    }

    public boolean delete(Long id, User sessionUser) {
        Question targetQuestion = verifyQuestion(id, sessionUser);
        boolean result = targetQuestion.delete();
        if (result) {
            this.questionRepository.save(targetQuestion);
            return true;
        }
        return false;
    }

    public Question verifyQuestion(Long id, User sessionUser) {
        Question question = findById(id);
        if (!question.matchWriter(sessionUser)) {
            throw new UserSessionException(ErrorMessage.ILLEGAL_USER);
        }
        return question;
    }

    public Page<Question> findAllQuestionByPage(Pageable pageable) {
        Page<Question> questionPage = this.questionRepository.findAllByDeletedFalse(pageable);
        int pageNumber = questionPage.getNumber();
        int totalPageNumber = questionPage.getTotalPages();
        if ((totalPageNumber == 0 && pageNumber != 0)
                || (totalPageNumber != 0 && pageNumber >= totalPageNumber)) {
            throw new EntityNotFoundException(ErrorMessage.PAGE_NOT_FOUND);
        }
        return this.questionRepository.findAllByDeletedFalse(pageable);
    }

    public List<Integer> getPageRange(Page<Question> questionPage) {
        int pageNumber = questionPage.getNumber();
        int totalPageNumber = questionPage.getTotalPages();
        int range = (pageNumber < totalPageNumber / 5 * 5) ? 5 : totalPageNumber % 5;
        return IntStream.rangeClosed(1, range)
                .map(num -> pageNumber / 5 * 5 + num)
                .boxed()
                .collect(Collectors.toList());

    }

    public Question findById(Long id) {
        return this.questionRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new EntityNotFoundException(ErrorMessage.QUESTION_NOT_FOUND));
    }

}
