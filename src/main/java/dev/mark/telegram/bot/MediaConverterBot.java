package dev.mark.telegram.bot;

import dev.mark.service.FileDownloadService;
import dev.mark.service.FileUploadService;
import dev.mark.telegram.handlers.MessageHandler;
import dev.mark.telegram.handlers.CallbackQueryHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MediaConverterBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackHandler;

    public MediaConverterBot(String botToken, String botUsername) {
        super(botToken);
        this.botUsername = botUsername;

        FileDownloadService downloadService = new FileDownloadService(this, botToken);
        FileUploadService uploadService = new FileUploadService(this);

        Map<Long, String> userFiles = new ConcurrentHashMap<>();

        this.messageHandler = new MessageHandler(downloadService, userFiles);
        this.callbackHandler = new CallbackQueryHandler(uploadService, userFiles);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                messageHandler.handle(update.getMessage(), this);
            } else if (update.hasCallbackQuery()) {
                callbackHandler.handle(update.getCallbackQuery(), this);
            }
        } catch (Exception e) {
            System.err.println("Error processing update: " + e.getMessage());
        }
    }
}