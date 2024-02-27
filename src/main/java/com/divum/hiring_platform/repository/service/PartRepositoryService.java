package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.Part;

import java.util.List;

public interface PartRepositoryService {
    List<Part> findAllByRounds_Id(String roundId);
}
