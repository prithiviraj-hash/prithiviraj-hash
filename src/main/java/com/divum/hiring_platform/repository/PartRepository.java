package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, String>{
    List<Part> findAllByRounds_Id(String roundsId);
}
