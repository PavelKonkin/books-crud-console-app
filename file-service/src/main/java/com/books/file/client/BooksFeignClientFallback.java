package com.books.file.client;

import com.books.dto.BookDto;
import com.books.exception.NotFoundException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BooksFeignClientFallback implements FallbackFactory<BooksFeignClient> {
    private final MessageSource messageSource;

    public BooksFeignClientFallback(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public BooksFeignClient create(Throwable cause) {
        return new BooksFeignClient() {
            @Override
            public void updateBook(String token, BookDto bookDto) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, messageSource
                        .getMessage("bookServiceUnavailable",null, LocaleContextHolder.getLocale()));
            }

            @Override
            public BookDto getBook(String token, int id) {
                if (cause instanceof FeignException.NotFound) {
                    throw new NotFoundException(messageSource
                            .getMessage("bookNotFound",null, LocaleContextHolder.getLocale()));
                }
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, messageSource
                        .getMessage("bookServiceUnavailable",null, LocaleContextHolder.getLocale()));
            }
        };
    }
}
