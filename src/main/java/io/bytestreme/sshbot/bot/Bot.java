package io.bytestreme.sshbot.bot;

import io.bytestreme.sshbot.handler.AbstractHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class Bot extends AbstractBot {

    @Resource(name = "handlerMap")
    private Map<String, AbstractHandler> handlerMap;

    private final static String MSG_UNSUPPORTED_COMMAND = "Unsupported command";

    @Override
    public void onUpdateReceived(Update update) {
        Optional<String> text = Optional.ofNullable(update.getMessage().getText());
        String chatId = String.valueOf(update.getMessage().getChatId());

        if (text.isPresent()) {
            Optional<String> command = extractCommand(text.get());
            if (command.isPresent() && handlerMap.containsKey(command.get())) {
                AbstractHandler handler = handlerMap.get(command.get());
                handler.handle(update, chatId).forEach(this::executeMethod);
            } else {
                executeMethod(new SendMessage(chatId, MSG_UNSUPPORTED_COMMAND));
            }
        }
    }


    public void executeMethod(BotApiMethod<Message> method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            log.error("Exception while sending message {} to user: {}", method, e.getMessage());
        }
    }

    private Optional<String> extractCommand(String text) {
        String word = text.split(" ")[0];
        if (word.startsWith("/")) {
            return Optional.of(word);
        } else {
            return Optional.empty();
        }
    }

}
