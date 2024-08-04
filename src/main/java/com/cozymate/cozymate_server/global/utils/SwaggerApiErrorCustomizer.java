package com.cozymate.cozymate_server.global.utils;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class SwaggerApiErrorCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        SwaggerApiError swaggerApiError = handlerMethod.getMethodAnnotation(SwaggerApiError.class);
        if (swaggerApiError != null) {
            Map<String, List<Map<String, Object>>> errorDetailsMap = new HashMap<>();

            for (ErrorStatus errorStatus : swaggerApiError.value()) {
                String statusCode = String.valueOf(errorStatus.getHttpStatus().value());
                List<Map<String, Object>> errorDetails = errorDetailsMap.getOrDefault(statusCode,
                    new ArrayList<>());

                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("isSuccess", false);
                errorInfo.put("code", errorStatus.getCode());
                errorInfo.put("message", errorStatus.getMessage());
                errorDetails.add(errorInfo);

                errorDetailsMap.put(statusCode, errorDetails);
            }

            errorDetailsMap.forEach((statusCode, errorList) -> {
                Schema<?> errorSchema = new Schema<>().type("object").example(errorList);
                Content content = new Content()
                    .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(errorSchema));
                ApiResponse apiResponse = new ApiResponse().content(content);
                operation.getResponses().addApiResponse(statusCode, apiResponse);
            });
        }
        return operation;
    }
}