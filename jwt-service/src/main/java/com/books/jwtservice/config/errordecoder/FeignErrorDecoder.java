package com.books.jwtservice.config.errordecoder;

import com.books.exception.ApiError;
import com.books.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    public FeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            // Попытка десериализовать тело ответа в ApiError
            ApiError apiError = objectMapper.readValue(response.body().asInputStream(), ApiError.class);

            if (response.status() == 404) {
                return new NotFoundException(apiError.getMessage());
            }
            // Добавить обработку других статусов, если необходимо
        } catch (IOException e) {
            // Обработка ошибки декодирования
            return new Exception("Ошибка при декодировании ответа Feign Client");
        }

        return FeignException.errorStatus(methodKey, response);
    }
}
