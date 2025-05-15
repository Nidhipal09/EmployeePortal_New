package com.employeeportal.util;

import com.employeeportal.model.login.ResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ResponseUtil {

    public ResponseDTO prepareResponseDto(Object obj, String message, Integer code, Boolean status) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(obj);
        responseDTO.setMessage(message);
        responseDTO.setCode(code);
        responseDTO.setStatus(status);
        return responseDTO;

    }

    public static String toJson(Object object) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw e;
        }

    }
}
