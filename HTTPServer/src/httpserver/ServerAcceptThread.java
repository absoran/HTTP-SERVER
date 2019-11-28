package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerAcceptThread implements Runnable {

    ServerSocket serverSocket;
    FrameServer serverFrame;
    static boolean continueThread=true;
    public ServerAcceptThread(ServerSocket ss, FrameServer serverFrame) {
        this.serverSocket = ss;
        this.serverFrame = serverFrame;
    }

    @Override
    public void run() {
        while (continueThread) {
            try {
                ServerResponseThread myServer = new ServerResponseThread(serverSocket.accept(),serverFrame);
                Thread thread = new Thread(myServer);
                thread.start();
            } catch (IOException ex) {
                serverFrame.addTextFrame(ex.getMessage());
            }
        }
    }

}
