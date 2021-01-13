package io.bytestreme.sshbot.handler.scenario.ssh;

import io.bytestreme.sshbot.handler.AbstractHandler;
import io.bytestreme.sshbot.handler.annotation.BotHandler;
import io.bytestreme.sshbot.handler.scenario.ssh.connection.SshConnection;
import io.bytestreme.sshbot.handler.scenario.ssh.connection.SshConnectionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@BotHandler(command = "/close")
@RequiredArgsConstructor
public class CloseHandler extends AbstractHandler {

    private final SshConnectionRegistry connectionRegistry;
    private final static String CONNECTION_CLOSED = "Connection closed.";
    private final static String NO_CONNECTION = "No active connections found to close!";

    @Override
    public List<BotApiMethod<Message>> handle(Update update, String chatId) {
        String message;

        if (connectionRegistry.isConnected(chatId)) {
            SshConnection connection = connectionRegistry.getConnection(chatId);
            connection.close();
            connectionRegistry.removeConnection(chatId);
            message = CONNECTION_CLOSED;
        } else {
            message = NO_CONNECTION;
        }
        return List.of(new SendMessage(chatId, message));
    }

}
