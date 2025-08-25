package dev.mark.telegram;

import dev.mark.telegram.bot.MediaConverterBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotApplication {
    public static void startTelegramBot() {
        try {
            String botToken = ""; /// https://t.me/BotFather
            String botUsername = ""; /// your bot username without @

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MediaConverterBot bot = new MediaConverterBot(botToken, botUsername);

            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            System.err.println("Failed to start bot: " + e.getMessage());
        }
    }
}