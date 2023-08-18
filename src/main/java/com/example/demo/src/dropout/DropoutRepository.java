package com.example.demo.src.dropout;


import com.example.demo.src.dropout.entity.Dropout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface DropoutRepository extends JpaRepository<Dropout, Long> {

}
