package com.lonelyprogrammer.forum.auth.utils;

import com.lonelyprogrammer.forum.auth.models.ErrorResponse;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Created by Nikita on 12.03.2017.
 */
public class ResponseUtils {
    public static ResponseEntity buildErrorResponse(List<ErrorResponse> errors) {
        final StringBuilder errorString = new StringBuilder();
        for(ErrorResponse e : errors){
            errorString.append(e.getErrorText());
            errorString.append(',');
        }
        final JSONObject result = new JSONObject();
        final String error = errorString.toString();
        result.put("Errors", error);
        System.out.println(error);
        return ResponseEntity.status(errors.get(0).getErrorStatus().getCode()).body(result);
    }

    public static ResponseEntity buildErrorResponse(ErrorResponse error) {
        return ResponseEntity.status(error.getErrorStatus().getCode()).body(error);
    }
}
