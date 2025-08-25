package dev.mark.telegram.handlers;

import dev.mark.converters.gif2video;
import dev.mark.converters.video2gif;
import dev.mark.interfaces.IMediaConverter;
import dev.mark.model.ConversionType;
import dev.mark.service.FileUploadService;
import dev.mark.util.TempFileManager;
import dev.mark.util.telegram.MessageUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Map;

public class CallbackQueryHandler {
    private final FileUploadService uploadService;
    private final Map<Long, String> userFiles;

    public CallbackQueryHandler(FileUploadService uploadService, Map<Long, String> userFiles) {
        this.uploadService = uploadService;
        this.userFiles = userFiles;
    }

    public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        try {
            if (data.startsWith("convert_")) {
                handleConversion(chatId, data, bot);
            }
        } catch (Exception e) {
            MessageUtils.sendErrorMessage(chatId, "An error occurred, please contact the administrator: " + e.getMessage(), bot);
            System.err.println("Error in callback handler: " + e.getMessage());
        }
    }

    private void handleConversion(Long chatId, String data, TelegramLongPollingBot bot) {
        String conversionTypeStr = data.substring(8);
        ConversionType conversionType = ConversionType.fromString(conversionTypeStr);

        String filePath = userFiles.get(chatId);
        if (filePath == null) {
            MessageUtils.sendErrorMessage(chatId, "File not found. Please upload file again.", bot);
            return;
        }

        performConversion(chatId, filePath, conversionType, bot);
    }

    private void performConversion(Long chatId, String inputPath, ConversionType conversionType, TelegramLongPollingBot bot) {
        try {
            MessageUtils.sendMessage(chatId, "🔄 Start Conversion...", bot);

            IMediaConverter converter = getConverter(conversionType);
            String outputPath = TempFileManager.generateOutputPath(inputPath, conversionType.getOutputExtension());

            long startTime = System.currentTimeMillis();
            converter.convert(inputPath, outputPath);
            long duration = (System.currentTimeMillis() - startTime) / 1000;

            uploadService.uploadFile(chatId, outputPath);
            MessageUtils.sendMessage(chatId, String.format("✅ Conversion completed successfully!\n⏱️ Processing time: %d sec", duration), bot);
        } catch (Exception e) {
            MessageUtils.sendErrorMessage(chatId, "An error occurred, please contact the administrator: " + e.getMessage(), bot);
            System.err.println("Conversion error: " + e.getMessage());
        } finally {
            userFiles.remove(chatId);
            TempFileManager.cleanup();
        }
    }

    private IMediaConverter getConverter(ConversionType conversionType) throws IllegalArgumentException {
        return switch (conversionType) {
            case MP4_TO_GIF, AVI_TO_GIF, MOV_TO_GIF, MKV_TO_GIF -> new video2gif();
            case GIF_TO_MP4 -> new gif2video();
        };
    }
}