package io.bytestreme.sshbot.handler.scenario;

import io.bytestreme.sshbot.handler.AbstractHandler;
import io.bytestreme.sshbot.handler.annotation.BotHandler;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@BotHandler(command = "/start")
public class StartHandler extends AbstractHandler {

    private final static String MSG_START = "example to connect: /connect username:password@host:2222";

    @Override
    public List<BotApiMethod<Message>> handle(Update update, String chatId) {
        return List.of(new SendMessage(chatId, MSG_START));
    }

}
