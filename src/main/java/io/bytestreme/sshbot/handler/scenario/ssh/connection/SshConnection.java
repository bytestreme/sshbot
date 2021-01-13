package io.bytestreme.sshbot.handler.scenario.ssh.connection;

import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;


@Slf4j
@RequiredArgsConstructor
public class SshConnection {

    private Session session;
    private BufferedReader reader;
    private ChannelExec channel;
    private boolean ready;
    private final String username;
    private final String password;
    private final String host;
    private final int port;

    public boolean connect() throws JSchException {

        try {
            JSch ssh = new JSch();
            session = ssh.getSession(this.username, this.host, this.port);
            session.setPassword(this.password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
            this.ready = true;
            return true;
        } catch (Exception e) {
            this.ready = false;
        }
        return false;
    }

    public String write(String command) {

        try {
            channel = (ChannelExec) session.openChannel("shell");
            channel.setCommand(command);
            setCommandOutput(channel.getInputStream());
            channel.connect(30000);

            StringBuilder sBuilder = new StringBuilder();
            String read = reader.readLine();

            while (read != null) {
                sBuilder.append(read);
                sBuilder.append("\n");
                read = reader.readLine();
            }
            return sBuilder.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public boolean upload(String origin, String destinationDirectory) {

        try {
            File origin_ = new File(origin);
            destinationDirectory = destinationDirectory.replace(" ", "_");
            String destination = destinationDirectory.concat("/").concat(origin_.getName());
            return upload(origin, destination, destinationDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean upload(String origin, String destination, String destinationDirectory) {
        try {
            ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            destinationDirectory = destinationDirectory.replace(" ", "_");
            sftp.cd(destinationDirectory);
            sftp.put(origin, destination);
            sftp.disconnect();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        this.ready = false;
    }

    public void setCommandOutput(InputStream in) {
        reader = new BufferedReader(new InputStreamReader(in));
    }

    public boolean prepareUpload(String fileName) {
        File file = new File(fileName);
        return file.exists() && file.isFile();
    }

    public boolean isReady() {
        return ready;
    }

    public boolean download(String remoteArchive, String localArchive) {

        if (prepareUpload(localArchive)) {
            try {
                ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                sftp.connect();
                sftp.get(remoteArchive, localArchive);
                sftp.disconnect();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
