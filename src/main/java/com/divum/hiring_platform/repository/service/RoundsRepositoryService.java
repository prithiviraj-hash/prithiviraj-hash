package com.divum.hiring_platform.repository.service;


import com.divum.hiring_platform.entity.Rounds;

import java.util.Optional;

public interface RoundsRepositoryService {
    Optional<Rounds> findById(String roundId);

    int passPercentage(String roundId);
    void save(Rounds previousRound);
}
