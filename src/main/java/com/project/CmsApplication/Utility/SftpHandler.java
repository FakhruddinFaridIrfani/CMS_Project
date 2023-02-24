package com.project.CmsApplication.Utility;

import com.jcraft.jsch.*;
import com.project.CmsApplication.controller.BranchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SftpHandler {
    static Logger logger = LoggerFactory.getLogger(SftpHandler.class);


    public static ChannelSftp getSftpConnnection(Session session) {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            logger.info("Can't connect to sftp");
        }
        return channel;
    }

    public static Session getSftpSession(String sftpUser,String sftpUrl, String sftpPassword) {
        Session session = null;
        try {
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return session;
    }

    public static void closeSession(Session session) {
        if (session != null) {
            session.disconnect();
        }
    }

    public static void closeChannel(Channel channel) {
        if (channel != null) {
            channel.disconnect();
        }
    }
}
