package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.Rounds;
import com.divum.hiring_platform.repository.RoundsRepository;
import com.divum.hiring_platform.repository.service.RoundsRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoundsRepositoryServiceImpl implements RoundsRepositoryService {


    private final RoundsRepository roundsRepository;
    @Override
    public Optional<Rounds> findById(String roundId) {
        return roundsRepository.findById(roundId);
    }

    @Override
    public int passPercentage(String roundId) {
        return roundsRepository.passPercentage(roundId);
    }

    @Override
    public void save(Rounds rounds) {
        roundsRepository.save(rounds);
    }
}
