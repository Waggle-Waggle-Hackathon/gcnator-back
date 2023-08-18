package com.example.demo.src.dropout;

import com.example.demo.config.BaseException;
import com.example.demo.src.chatGPT.ChatGPTService;
import com.example.demo.src.question.CategoryRepository;
import com.example.demo.src.question.UserWeightRepository;
import com.example.demo.src.question.entity.Category;
import com.example.demo.src.question.entity.UserWeight;
import com.example.demo.src.user.UserRepository;
import com.example.demo.src.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DropoutService {
    private final DropoutRepository dropoutRepository;
    private final UserRepository userRepository;
    private final UserWeightRepository userWeightRepository;
    private final CategoryRepository categoryRepository;
    private final ChatGPTService chatGPTService;

    public String sendDropout(Long userId) throws BaseException {
        List<UserWeight> userWeight = userWeightRepository.findByUserId(userId);
        int[][] weight = new int[userWeight.size()][2];

        for(int i = 0; i < userWeight.size(); i++) {
            weight[i][0] = Math.toIntExact(userWeight.get(i).getCategory().getId());
            weight[i][1] = Math.abs(userWeight.get(i).getWeight());
        }

        Arrays.sort(weight, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[1] - o1[1];
            }
        });

//        for(int i = 0; i < userWeight.size(); i++) {
//            System.out.println(weight[i][0] + " " + weight[i][1]);
//        }

        Category firt = categoryRepository.findById((long) weight[0][1]).get();
        Category second = categoryRepository.findById((long) weight[1][1]).get();

        String firstCategory = firt.getCategory();
        String secondCategory = second.getCategory();

        User user = userRepository.findById(userId).get();

        String request =
                "학교: " + user.getUniversity() +
                "학번: " + user.getStudent_num() +
                "학과: " + user.getDepartment() +
                "이름: " + user.getUserName() +
                "\n 정보를 가지고 " + firstCategory + "와 " + secondCategory + "를 이유로 한 대학교 자퇴서 조금 짧" +
                        "" +
                        "" +
                        "" +
                        "ㅐㅕ ㅕㅑㅐㅏㅣ;;" +
                        "" +
                        "" +
                        "" +
                        "" +
                        " 써줘.";

        return chatGPTService.getDropout(request);
    }
}
