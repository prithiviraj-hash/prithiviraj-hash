package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.Part;
import com.divum.hiring_platform.repository.PartRepository;
import com.divum.hiring_platform.repository.service.PartRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PartRepositoryServiceImpl implements PartRepositoryService {

    private final PartRepository partRepository;
    @Override
    public List<Part> findAllByRounds_Id(String roundId) {
        return partRepository.findAllByRounds_Id(roundId);
    }
}
