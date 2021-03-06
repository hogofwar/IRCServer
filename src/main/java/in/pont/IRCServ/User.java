package in.pont.IRCServ;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class User {
    final Logger logger = LoggerFactory.getLogger(User.class);
    private String nickname = null;
    private String username = null;
    private String realname = null;
    private String hostname = null;
    private ChannelHandlerContext conn = null;
    private boolean registered = false;

    public User(ChannelHandlerContext connection) {
        conn = connection;
        hostname = ((InetSocketAddress) connection.channel().remoteAddress()).getHostName();
    }

    public void rcvMsg(Message m) {
        if (IRCDaemon.CmdMap.containsKey(m.type)) {
            Command cmd;
            cmd = IRCDaemon.CmdMap.get(m.type);
            String[] params = m.params.toArray(new String[m.params.size()]);
            HashMap cmdParams = cmd.params(this, params);
            if (cmdParams == null) {
                return;
            }
            cmd.use(this, cmdParams);
        } else if (isRegistered()) {
            sendMsg(Reply.ERR_UNKNOWNCOMMAND, m.type);
        }

    }

    public void sendWelcome() {
        logger.debug("Sending welcome messages to {}", getNickname());
        sendMsg(Reply.RPL_WELCOME, nickname, username, hostname);
    }

    public void sendMsg(String strm) {

    }

    public void sendMsg(Message m) {
        logger.debug("Sending message: {}", m);
        conn.channel().writeAndFlush(m);
    }

    public void sendMsg(Reply rep) {
        sendMsg(new Message(rep.ID, rep.getMessage()));
    }

    public void sendMsg(Reply rep, Object... input) {
        sendMsg(new Message(rep.ID, rep.getMessage(input)));

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nick) {
        logger.info("Setting nickname from {} to {}", nickname, nick);
        if (nick.length() <= 9) {
            this.nickname = nick;
        }
    }

    public void setUsername(String user) {
        logger.debug("Setting username from {} to {}", username, user);
        this.username = user;
    }

    public void setRealname(String name) {
        logger.debug("Setting realname from {} to {}", realname, name);
        this.realname = name;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean set) {
        registered = set;
    }

    public void quit(){
        logger.debug("Closing channel for {}",getNickname());
        conn.channel().close();
    }
    public String getUniqueID(){
        return String.format("%s!%s@%s", nickname, username, hostname);
    }
}
