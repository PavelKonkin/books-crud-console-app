package com.books.book.service;

import com.books.dto.BookDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FileUpdateListenerImpl implements FileUpdateListener {
    private final BookService bookService;

    public FileUpdateListenerImpl(BookService bookService) {
        this.bookService = bookService;
    }

    @KafkaListener(topics = {"new-file", "delete-file"}, containerFactory = "bookKafkaListenerContainerFactory")
    public void handleUpdatedFile(ConsumerRecord<String, BookDto> bookRecord) {
        BookDto bookDto = bookRecord.value();
        bookService.update(bookDto);
    }
}
