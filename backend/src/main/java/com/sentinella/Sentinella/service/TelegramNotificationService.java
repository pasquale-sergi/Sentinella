package com.sentinella.Sentinella.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Service
public class TelegramNotificationService  extends DefaultAbsSender {
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    // Constructor required by DefaultAbsSender
    protected TelegramNotificationService(@Value("${telegram.bot.token}") String botToken) {
        super(new DefaultBotOptions()); // Use default options
        this.botToken = botToken; // Initialize botToken here explicitly
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendAnomalyAlert(String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId); // Set the chat ID
        message.setText(messageText); // Set the text of the message
        message.setParseMode("HTML"); // Allows basic HTML formatting (bold, italics)

        try {
            execute(message);
            System.out.println("Telegram: Anomaly alert sent successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Telegram: Failed to send anomaly alert: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
