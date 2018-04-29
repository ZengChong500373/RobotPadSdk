package hefu.pad.sdk.http;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import hefu.pad.sdk.utils.Constant;
import hefu.pad.sdk.utils.ThreadManager;

/**
 * Created by zc on 2018/4/20.
 * <p>
 * 这个类运行在rokid 硬件上的
 * <p>
 * 接收平板端发送过来的消息
 * 然后让rokid 设备做出相应处理
 */

public class RokidServerTcpClient implements Runnable {
    private String TAG = "RokidServerTcpClient";
    private Socket socket;
    private InputStreamReader in = null;
    private OutputStreamWriter out = null;
    private int REPLY_MAX_LENGTH = 1024 * 10;       //socket数据接收最大长度
    private char buf[] = new char[REPLY_MAX_LENGTH]; //socket缓存buf
    private String clientCmd = "";

    public RokidServerTcpClient(Socket socket) {
        this.socket = socket;
        try {
            in = new InputStreamReader(socket.getInputStream());
            out = new OutputStreamWriter(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            int len = read(buf, 0, REPLY_MAX_LENGTH - 1);
            if (len != -1) {
                clientCmd = new String(buf, 0, len);
                Log.e(TAG, clientCmd);
            } else {
                return;
            }
            if (!TextUtils.isEmpty(clientCmd)) {
                int aCommonEnd = clientCmd.indexOf(Constant.BUF_INSTRUCTION_SPLIT_SYMBOL);
                if (aCommonEnd > 0) {
                    String value = clientCmd.substring(0, aCommonEnd);
                    if (!TextUtils.isEmpty(value)) {
                        //让rokid 设备读文字 ，或者语义理解 一段话
//                        RKSpeechAgent.executeWithAsr(value);
                        sendMsg(value);
                        Log.e(TAG, value);
                    }
                }
            }
        }
    }

    public int read(char buf[], int offset, int maxLength) {
        int length = 0;
        if (in != null) {
            try {
                length = in.read(buf, offset, maxLength);
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return length;
    }

    /**
     * 把数据发送给平板端
     */
    public void sendMsg(String value) {
        try {
            out.write(value, 0, value.length());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
