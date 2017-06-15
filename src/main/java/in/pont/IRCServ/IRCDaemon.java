package in.pont.IRCServ;
import in.pont.IRCServ.cmd.CmdNick;
import in.pont.IRCServ.cmd.CmdUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class IRCDaemon {
    public static String serverName = "TestServer";
    public static Command[] CmdList = {new CmdNick(), new CmdUser()};
    public static HashMap<String, Command> CmdMap = new HashMap<>();
    public static ArrayList<User> userList = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        final Logger logger = LoggerFactory.getLogger(IRCDaemon.class);
        loadCommands();
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
            logger.debug("Read Port as: {}", port);
        } else {
            port = 6667;
            logger.debug("Using default port {}", port);
        }
        logger.info("Starting server at port {}", port);
        new IRCServer(port).run();
    }
    public static void loadCommands(){
        for(Command c: CmdList){
            for(String s : c.hook()){
                CmdMap.put(s, c);
            }
        }
    }
}
