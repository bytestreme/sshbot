package io.bytestreme.sshbot.handler.scenario.ssh.connection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SshConnectionRegistry {

    private final Map<String, SshConnection> activeConnections = new HashMap<>();

    public SshConnection getConnection(String chatId) {
        return this.activeConnections.get(chatId);
    }

    public void addConnection(String chatId, SshConnection connection) {
        this.activeConnections.put(chatId, connection);
    }

    public boolean isConnected(String chatId) {
        return this.activeConnections.containsKey(chatId);
    }

    public void removeConnection(String chatId) {
        this.activeConnections.remove(chatId);
    }

}
