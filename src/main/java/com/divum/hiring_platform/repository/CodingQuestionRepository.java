package com.divum.hiring_platform.repository;


import com.divum.hiring_platform.entity.CodingQuestion;
import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.util.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CodingQuestionRepository extends JpaRepository<CodingQuestion, Long> {

    @Query("SELECT q FROM CodingQuestion q WHERE q.category.categoryId =?1 AND q.difficulty =?2 AND q NOT IN (SELECT rq.contestAndCoding.codingQuestion FROM RoundAndCodingQuestion rq WHERE rq.contestAndCoding.contest =?3) ORDER BY RANDOM() LIMIT ?4")
    CodingQuestion getRandomQuestion(int categoryId, Difficulty difficulty, Contest contest, int easy);

}
