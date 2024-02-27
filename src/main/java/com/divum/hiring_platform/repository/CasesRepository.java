package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Cases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CasesRepository extends JpaRepository<Cases, Long> {

}
