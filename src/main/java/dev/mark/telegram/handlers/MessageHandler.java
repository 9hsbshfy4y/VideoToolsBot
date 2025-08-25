package dev.mark.telegram.handlers;

import dev.mark.model.ConversionType;
import dev.mark.service.FileDownloadService;
import dev.mark.util.telegram.MessageUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessageHandler {
    private final FileDownloadService downloadService;
    private final Map<Long, String> userFiles;

    public MessageHandler(FileDownloadService downloadService, Map<Long, String> userFiles) {
        this.downloadService = downloadService;
        this.userFiles = userFiles;
    }

    public void handle(Message message, TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        Long chatId = message.getChatId();

        if (message.hasText()) {
            handleTextMessage(message.getText(), chatId, bot);
        } else if (message.hasDocument()) {
            handleDocument(chatId, message.getDocument(), bot);
        } else if (message.hasVideo()) {
            handleVideo(chatId, message.getVideo(), bot);
        } else {
            MessageUtils.sendMessage(chatId, "Only video files and GIF images are supported.", bot);
        }
    }

    private void handleTextMessage(String text, Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        switch (text) {
            case "/start" -> MessageUtils.sendStartMessage(chatId, bot);
            case "/help" -> MessageUtils.sendHelpMessage(chatId, bot);
        }
    }

    private void handleDocument(Long chatId, Document document, TelegramLongPollingBot bot) throws IOException, TelegramApiException {
        String fileName = document.getFileName();
        if (fileName == null) {
            MessageUtils.sendMessage(chatId, "Failed to determine the file type.", bot);
            return;
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        List<ConversionType> supportedConversions = ConversionType.getSupportedConversions(fileExtension);

        if (supportedConversions.isEmpty()) {
            MessageUtils.sendMessage(chatId, "Unsupported file type: " + fileExtension + "\nOnly the following are supported: MP4, AVI, MOV, MKV, GIF", bot);
            return;
        }

        MessageUtils.sendMessage(chatId, "Uploading file...", bot);
        String filePath = downloadService.downloadFile(document.getFileId(), fileName);
        userFiles.put(chatId, filePath);

        MessageUtils.sendConversionOptions(chatId, supportedConversions, bot);
    }

    private void handleVideo(Long chatId, Video video, TelegramLongPollingBot bot) throws IOException, TelegramApiException {
        String fileName = video.getFileName() != null ? video.getFileName() : "video.mp4";

        MessageUtils.sendMessage(chatId, "Uploading video...", bot);
        String filePath = downloadService.downloadFile(video.getFileId(), fileName);
        userFiles.put(chatId, filePath);

        String fileExtension = getFileExtension(fileName).toLowerCase();
        List<ConversionType> supportedConversions = ConversionType.getSupportedConversions(fileExtension);
        MessageUtils.sendConversionOptions(chatId, supportedConversions, bot);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }
}