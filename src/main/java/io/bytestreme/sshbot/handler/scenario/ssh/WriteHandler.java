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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@BotHandler(command = "/write")
@RequiredArgsConstructor
public class WriteHandler extends AbstractHandler {

    private final SshConnectionRegistry connectionRegistry;

    private final static Pattern WRITE_PATTERN = Pattern.compile("(/write) (.*)");
    private final static String MSG_NO_CONNECTIONS = "No active connections were found.";
    private final static String MSG_INVALID_COMMAND = "Invalid command";
    private final static String MSG_EMPTY_PROMPT = "$";

    @Override
    public List<BotApiMethod<Message>> handle(Update update, String chatId) {
        if (!connectionRegistry.isConnected(chatId)) {
            return List.of(new SendMessage(chatId, MSG_NO_CONNECTIONS));
        }
        SshConnection connection = connectionRegistry.getConnection(chatId);
        String message = update.getMessage().getText();
        Matcher m = WRITE_PATTERN.matcher(message);
        if (!m.find()) {
            return List.of(new SendMessage(chatId, MSG_INVALID_COMMAND));
        }
        Optional<String> response = Optional.ofNullable(connection.write(m.group(2)));
        if (response.isPresent() && !response.get().isEmpty()) {
            return List.of(new SendMessage(chatId, response.get()));
        } else {
            return List.of(new SendMessage(chatId, MSG_EMPTY_PROMPT));
        }
    }
}
