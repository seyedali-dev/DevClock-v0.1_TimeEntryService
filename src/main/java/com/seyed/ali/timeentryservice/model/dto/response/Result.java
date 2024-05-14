package com.seyed.ali.timeentryservice.model.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private boolean flag; // Two values: true means success, false means not successful
    private HttpStatus httpStatus; // Http Status. e.g., OK, FORBIDDEN, etc..
    private String message; // Response message
    private Object data; // The response payload

    public Result(boolean flag, HttpStatus httpStatus, String message) {
        this.flag = flag;
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
