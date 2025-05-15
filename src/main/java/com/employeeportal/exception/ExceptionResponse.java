
package com.employeeportal.exception;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    private Integer code;
    private String message;
    private Object source;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
