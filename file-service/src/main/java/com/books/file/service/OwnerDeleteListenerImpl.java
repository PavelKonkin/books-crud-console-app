package com.books.file.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OwnerDeleteListenerImpl implements OwnerDeleteListener {
    public final FileService fileService;

    public OwnerDeleteListenerImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @KafkaListener(topics = {"delete-book", "delete-old-file"})
    public void handleUpdatedFile(String fileId) {
        fileService.delete(fileId);
    }
}
