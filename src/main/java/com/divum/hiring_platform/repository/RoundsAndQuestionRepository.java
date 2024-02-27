package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.CodingQuestion;
import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.ContestAndCoding;
import com.divum.hiring_platform.entity.RoundAndCodingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundsAndQuestionRepository extends JpaRepository<RoundAndCodingQuestion, ContestAndCoding> {


    @Query(value = "SELECT q.contestAndCoding.codingQuestion FROM RoundAndCodingQuestion q WHERE q.rounds.id =?1")
    List<CodingQuestion> findByRoundId(String id);

    @Query("SELECT COUNT(r) FROM RoundAndCodingQuestion r WHERE r.contestAndCoding.contest =?1")
    int getQuestionCount(Contest contest);


    @Query("SELECT COUNT(r) > 0 FROM RoundAndCodingQuestion r WHERE r.contestAndCoding.contest =?1 AND r.contestAndCoding.codingQuestion =?2")
    boolean checkIfTheQuestionIsAssigned(Contest contest, CodingQuestion question);

}
