package dev.mark.service;

import dev.mark.util.TempFileManager;
import org.apache.commons.io.IOUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class FileDownloadService {
    private final TelegramLongPollingBot bot;
    private final String botToken;

    public FileDownloadService(TelegramLongPollingBot bot, String botToken) {
        this.bot = bot;
        this.botToken = botToken;
    }

    public String downloadFile(String fileId, String fileName) throws IOException {
        try {
            GetFile getFile = new GetFile(fileId);
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);

            String tempFilePath = TempFileManager.createTempFile(fileName);

            URI uri = URI.create("https://api.telegram.org/file/bot" + botToken + "/" + file.getFilePath());
            URL fileUrl = uri.toURL();

            try (InputStream inputStream = fileUrl.openStream();
                 FileOutputStream outputStream = new FileOutputStream(tempFilePath)) {
                IOUtils.copy(inputStream, outputStream);
            }

            return tempFilePath;
        } catch (TelegramApiException e) {
            throw new IOException("TelegramApiException error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IOException("Error uploading file: " + e.getMessage(), e);
        }
    }
}
