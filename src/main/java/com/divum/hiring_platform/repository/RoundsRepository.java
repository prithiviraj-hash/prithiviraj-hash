package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Rounds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundsRepository extends JpaRepository<Rounds, String> {

    @Query(value = "SELECT pass FROM round WHERE id = ?1", nativeQuery = true)
    int passPercentage(String roundId);

}
