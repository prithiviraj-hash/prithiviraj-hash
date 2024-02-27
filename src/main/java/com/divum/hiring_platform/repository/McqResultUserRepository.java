package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.MCQResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface McqResultUserRepository extends MongoRepository<MCQResult,String> {

    MCQResult findByid(String resultId);
    public List<MCQResult> findAll();
//    String save(MCQResult entity);

//    <S extends MCQResult> S save(S entity);

}