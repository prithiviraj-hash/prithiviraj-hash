package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.User;

import java.util.List;
import java.util.Set;

public interface UserRepositoryService {

    Long countUsersByContest(Contest contest);
    void saveAll(List<User> users);
    void save(User user);

    User findUserByUserId(String userId);

    boolean existsUserByEmail(String email);

    User findUserByEmail(String email);

    Set<Contest> getParticipatedContest(String userId);
}
