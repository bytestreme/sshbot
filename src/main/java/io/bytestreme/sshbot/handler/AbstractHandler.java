package io.bytestreme.sshbot.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public abstract class AbstractHandler {

    public abstract List<BotApiMethod<Message>> handle(Update update, String chatId);

}
