package dev.mark.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class FileUploadService {
    private final TelegramLongPollingBot bot;

    public FileUploadService(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void uploadFile(Long chatId, String filePath) throws TelegramApiException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        SendDocument sendDocument = SendDocument.builder().chatId(chatId).document(new InputFile(file)).caption("Your converted file is ready!").build();
        bot.execute(sendDocument);
    }
}