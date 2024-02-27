package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, String> {


    User findUserByUserId(String userId);

    boolean existsUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findUserByEmail(String email);

    @Query("SELECT u.contest FROM User u WHERE u.userId =?1")
    Set<Contest> getParticipatedContest(String userId);


    @Query("SELECT COUNT(u) FROM User u WHERE :contest MEMBER OF u.contest")
    Long countUsersByContest(@Param("contest") Contest contest);



}
