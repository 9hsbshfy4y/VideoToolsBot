package dev.mark.util.telegram;

import dev.mark.model.ConversionType;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {
    public static void sendMessage(Long chatId, String text, TelegramLongPollingBot bot) throws TelegramApiException {
        SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();
        bot.execute(message);
    }

    public static void sendErrorMessage(Long chatId, String error, TelegramLongPollingBot bot) {
        try {
            sendMessage(chatId, error, bot);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send error message: " + e.getMessage());
        }
    }

    public static void sendStartMessage(Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        String text = """
        🤖 Welcome to the Video Converter Bot!
        
        I can convert video files to GIF and vice versa:
        • 🎹 MP4, AVI, MOV, MKV ➡️ GIF
        • 🎬 GIF ➡️ MP4
        
        📊 Conversion settings:
        • FPS: 15 frames/sec
        • Duration: up to 10 seconds
        • Width: 480px (maintaining aspect ratio)
        
        Just send me a video or GIF file!
        
        Use /help for more information.
        """;
        sendMessage(chatId, text, bot);
    }

    public static void sendHelpMessage(Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        String text = """
        📋 How to use:
        
        1️⃣ Send a video or GIF file
        2️⃣ Choose the conversion type from the available options
        3️⃣ Receive the converted file
        
        📊 Conversion settings:
        • Frame rate: 15 FPS
        • Maximum duration: 10 seconds
        • GIF width: 480px (maintaining aspect ratio)
        • High quality with optimized color palette
        
        📁 Supported formats:
        • Video: MP4, AVI, MOV, MKV → GIF
        • GIF → MP4
        
        ⚡ Limitations:
        • Maximum file size: 50 MB
        • Processing time: from 5 seconds to 2 minutes
        
        💡 Tips:
        • For best quality, use short videos
        • GIF files are automatically trimmed to 10 seconds
        • All files are processed in high quality
        
        For questions, contact the developer: @your_username
        """;
        sendMessage(chatId, text, bot);
    }

    public static void sendConversionOptions(Long chatId, List<ConversionType> conversions, TelegramLongPollingBot bot) throws TelegramApiException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (ConversionType conversion : conversions) {
            InlineKeyboardButton button = InlineKeyboardButton.builder().text(conversion.getDisplayName()).callbackData("convert_" + conversion.name()).build();
            rows.add(List.of(button));
        }

        markup.setKeyboard(rows);

        SendMessage message = SendMessage.builder().chatId(chatId).text("🔄 Choose type of conversation:").replyMarkup(markup).build();
        bot.execute(message);
    }
}