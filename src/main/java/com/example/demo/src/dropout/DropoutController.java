package com.example.demo.src.dropout;

import com.example.demo.config.BaseException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dropouts")
@Api(tags = "자퇴서")
public class DropoutController {
    private final DropoutService dropoutService;


    /**
     * 자퇴서 반환 API
     */
    @GetMapping("/{userId}")
    public String sendDropout(@PathVariable Long userId) throws BaseException {
        String dropout = dropoutService.sendDropout(userId);
        return dropout;
    }




}
