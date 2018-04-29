package hefu.pad.sdk.http;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import hefu.pad.sdk.utils.ThreadManager;

/**
 * Created by zc on 2018/4/20.
 *
 * 这个类运行在rokid 硬件上的
 *
 * 一个socket的管理类
 */

public class RokidServer {
    private static RokidServer instance = null;
    public static String TAG = "jyhRobotServer";
    private int port = 9007;
    ServerSocket server;
    private static List<RokidServerTcpClient> list;

    public static RokidServer getInstance() {
        if (instance == null) {
            instance = new RokidServer();
            list = new ArrayList<>();
        }
        return instance;
    }


    private RokidServer() {
    }


    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server = new ServerSocket(port);
                    while (true) {
                        Socket socket = server.accept();
                        RokidServerTcpClient client = new RokidServerTcpClient(socket);
                        ThreadManager.getInstance().addRun(client);
                        list.add(client);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "IOException");
                }
            }
        }).start();
    }

    /**
     * 向平板端发送文字方法
     */
    public void send(String str) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).sendMsg(str);
        }
    }
}
