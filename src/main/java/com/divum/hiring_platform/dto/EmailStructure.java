    package com.divum.hiring_platform.dto;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class EmailStructure {

        private String sender;
        private String receiver;
        private String subject;
        private String text;
        private String recipientType;
    }
