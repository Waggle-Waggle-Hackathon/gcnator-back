package com.example.demo.src.question;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.question.dto.QuestionRes;
import com.example.demo.src.question.entity.Answer;
import com.example.demo.src.question.entity.Category;
import com.example.demo.src.question.entity.Question;
import com.example.demo.src.question.entity.UserWeight;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import com.example.demo.utils.entity.BaseEntity;
import com.example.demo.utils.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static sun.jvm.hotspot.code.CompressedStream.L;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserWeightRepository userWeightRepository;
    private final UserRepository userRepository;

    public QuestionRes sendQuestion(Long userId) {
        List<Answer> userQuestion = answerRepository.findByUserIdAndStatus(userId, Status.A);
        ArrayList<Long> array = new ArrayList<>();
        for (Answer a : userQuestion){
            array.add(a.getQuestion().getId());
            System.out.println("중복 ID" + a.getQuestion().getId());
        }

        Long cnt = answerRepository.count();
        if (cnt < 16) {
            // 범위 내에서 랜덤 아이디를 가지고 옴
            Long length = questionRepository.count();  // 전체 질문 길이
            Long questionId = null;
            while (true) {
                Random random = new Random();
                Long idx = Math.abs(random.nextLong() % length) + 1;

                if (!array.contains(idx)) {
                    questionId = idx;
                    break;
                }
            }

            Optional<Question> question = questionRepository.findById(questionId);
//        if(!question.isPresent()) throw new BaseException(BaseResponseStatus.SERVER_ERROR);
            Question q = question.get();

            return new QuestionRes(q.getId(), q.getQuestion(), false, userId);
        } else {
            //todo 질문 중복 처리하기
            System.out.println("------------------------------");
            QuestionRes questionRes = new QuestionRes();
            User user = userRepository.getReferenceById(userId);
            List<UserWeight> userWeights = userWeightRepository.findByUser(user);

            // 0인 카테고리를 찾음
            List<Category> zeroWeight = userWeights.stream()
                    .filter(userWeight -> userWeight.getWeight() == 0)
                    .map(UserWeight::getCategory)
                    .collect(Collectors.toList());

            List<Question> selectedQuestions = new ArrayList<>();  // 가중치가 0인 카테고리의 질문 모음

            // 0인 카테고리에 대한 질문 리스트
            for (Category category : zeroWeight) {
                List<Question> availableQuestions = questionRepository.findNonAnsweredQuestionsByCategory(category);
                selectedQuestions.addAll(availableQuestions);
            }

            ArrayList<Long> categoryArray = new ArrayList<>();
            for (Question q : selectedQuestions){
                categoryArray.add(q.getId());
                System.out.println(q.getId());
            }

            Long length = (long) selectedQuestions.size();  // 전체 질문 길이
            Long questionId = null;
            while (true) {
                Random random = new Random();
                Long idx = Math.abs(random.nextLong() % length) + 1;
                System.out.println(idx);

                if (!array.contains(idx) && categoryArray.contains(idx)) {
                    questionId = idx;

                    break;
                }else {
                    System.out.println("중복발생");
                }
            }

            if (!selectedQuestions.isEmpty()) {
                Question selectedQuestion = selectedQuestions.get(Math.toIntExact(questionId));
                questionRes.setQuestion(selectedQuestion.getQuestion());
                questionRes.setUserId(userId);
                questionRes.setLastQuestion(false);
                questionRes.setQuestionId(selectedQuestion.getId());
            }else {
                //todo 현재는 질문 null로 들어가는데 마지막 질문 어떻게 할지?
                questionRes.setLastQuestion(true);
            }

            return questionRes;
        }

    }
}
