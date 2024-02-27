    package com.divum.hiring_platform.dto;


    import com.divum.hiring_platform.util.enums.RoundType;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class InterviewRoundDTO {

        private int roundNumber;
        private RoundType interviewType;
    }
