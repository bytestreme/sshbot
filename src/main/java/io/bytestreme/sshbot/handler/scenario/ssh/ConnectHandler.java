package io.bytestreme.sshbot.handler.scenario.ssh;

import com.jcraft.jsch.JSchException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@BotHandler(command = "/connect")
@RequiredArgsConstructor
public class ConnectHandler extends AbstractHandler {

    private final SshConnectionRegistry connectionRegistry;

    private final static Pattern CONNECT_STRING_PATTERN = Pattern.compile("(/connect) ([a-zA-Z0-9]*):(.*)@(.*):([0-9]*)");
    private final static String MSG_CONNECTED_ALREADY = "You have already established connection.\nClose it before creating new one";
    private final static String MSG_INVALID_CONNECT = "Invalid connect string input";
    private final static String MSG_CONNECTED = "Connection established";
    private final static String MSG_CONNECTION_FAILED = "Failed to connect";
    private final static String MSG_PORT_INVALID = "Invalid port number";

    @Override
    public List<BotApiMethod<Message>> handle(Update update, String chatId) {
        if (connectionRegistry.isConnected(chatId)) {
            return List.of(new SendMessage(chatId, MSG_CONNECTED_ALREADY));
        }
        String message = update.getMessage().getText();
        Matcher m = CONNECT_STRING_PATTERN.matcher(message);
        if (!m.find()) {
            return List.of(new SendMessage(chatId, MSG_INVALID_CONNECT));
        }
        try {
            int port = Integer.parseInt(m.group(5));
            String host = m.group(4);
            String password = m.group(3);
            String username = m.group(2);
            SshConnection stream = new SshConnection(username, password, host, port);
            if (stream.connect()) {
                connectionRegistry.addConnection(chatId, stream);
                return singleTextMessage(chatId, MSG_CONNECTED);
            } else {
                return singleTextMessage(chatId, MSG_CONNECTION_FAILED);
            }
        } catch (NumberFormatException e) {
            return singleTextMessage(chatId, MSG_PORT_INVALID);
        } catch (JSchException e) {
            return singleTextMessage(chatId, e.getMessage());
        }
    }

    private List<BotApiMethod<Message>> singleTextMessage(String chatId, String text) {
        return List.of(new SendMessage(chatId, text));
    }

}
