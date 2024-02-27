package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.MultipleChoiceQuestion;
import com.divum.hiring_platform.entity.RoundAndMcqQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundAndMcqQuestionRepository extends JpaRepository<RoundAndMcqQuestion, Long> {

    @Query(value = "SELECT q.contestAndMcq.multipleChoiceQuestion FROM RoundAndMcqQuestion q WHERE q.rounds.id =?1")
    List<MultipleChoiceQuestion> findByRoundId(String roundsId);

    @Query("SELECT COUNT(r) > 0 FROM RoundAndMcqQuestion r WHERE r.contestAndMcq.contest =?1 AND r.contestAndMcq.multipleChoiceQuestion =?2")
    boolean checkIfTheQuestionIsAssigned(Contest contest, MultipleChoiceQuestion question);

    @Query("SELECT COUNT(r) FROM RoundAndMcqQuestion r WHERE r.contestAndMcq.contest =?1")
    int getQuestionCount(Contest contest);
}
