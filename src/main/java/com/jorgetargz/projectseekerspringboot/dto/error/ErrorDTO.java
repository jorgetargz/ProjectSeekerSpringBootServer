package com.jorgetargz.projectseekerspringboot.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {
    private Timestamp timestamp;
    private Integer httpErrorCode;
    private String firebaseError;
    private String message;
    private String description;
}