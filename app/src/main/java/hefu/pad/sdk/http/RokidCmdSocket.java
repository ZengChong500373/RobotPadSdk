package hefu.pad.sdk.http;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import hefu.pad.sdk.listener.RokidInfoCallBack;
import hefu.pad.sdk.utils.Constant;
import hefu.pad.sdk.utils.CrashHandler;
import hefu.pad.sdk.utils.NetUtil;
import hefu.pad.sdk.utils.ThreadManager;

/**
 * 平板端发送数据给Rokid设备
 */
public class RokidCmdSocket {
    public static String TAG = "RokidCmdSocket";
    private static RokidCmdSocket instance = null;
    //socket相关配置
    private Socket socket;
    private static Writer writer;
    private static InputStreamReader reader;
    private Boolean isReady = false;
    List<NetUtil.IP_MAC> list;

    public void setIpList(List<NetUtil.IP_MAC> list) {
        this.list = list;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRobotInfoCallBack(RokidInfoCallBack callBack) {
        this.callBack = callBack;
    }

    RokidInfoCallBack callBack;
    private String hostName;
    private int port = 9007;
    private Thread mThread = null;

    private RokidCmdSocket() {
    }

    public static RokidCmdSocket getInstance() {
        if (instance == null) {
            instance = new RokidCmdSocket();
        }
        return instance;
    }

    private int position = 0;

    public void start() {
        //TODO 这里存在线程安全问题
        if (mThread != null) {
            return;
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (getReady() == false) {
                        Thread.sleep(500);
                        ConnectServer();
//                        keepAlive();
                        keepGetRebotInfo();

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isReady = false;
                    mThread = null;
                }
            }
        });
        mThread.start();
    }


    /**
     * 链接服务器
     */

    String str = "192.168.10.153";

    public void ConnectServer() {
        try {
            if (position >= list.size()) {
                position = 0;
            }
            str = list.get(position).getIp();
            socket = new Socket();
            socket.connect(new InetSocketAddress(str, port), 1000);
            socket.setSoTimeout(0);
            socket.setKeepAlive(true);
            socket.setOOBInline(true);
            writer = new OutputStreamWriter(socket.getOutputStream());
            reader = new InputStreamReader(socket.getInputStream());
            isReady = true;
            Log.e("jyh_ConnectServer_ip2", str);
            String sendStr = "tts平板链接成功";
            sendMsg(sendStr);
        } catch (Exception e) {
            Log.e("jyh_error", e.toString() + "");
            Log.e("jyh_error_info", "ip=" + str + " position=" + position);
            isReady = false;
            position++;
        }

    }

    public Boolean getReady() {
        return isReady;
    }


    public void robotCmd(final String speak) {
        ThreadManager.getInstance().addRun(new Runnable() {
            @Override
            public void run() {
                sendMsg(speak);
            }
        });
    }

    protected void sendMsg(String cmd) {
        cmd += Constant.BUF_INSTRUCTION_SPLIT_SYMBOL;
        try {
            if (writer != null && !TextUtils.isEmpty(cmd)) {
                Log.e("jyh_sendMsg", cmd);
                writer.write(cmd, 0, cmd.length());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            CrashHandler.getInstance().saveCrashInfo2File(e);
            Log.e("jyh_error", e.toString());
        }
    }

    /**
     * 服务器心跳连接
     */
    public void keepAlive() {
        if (getReady()) {
            KeepHeartBeatThread keepHeartBeatThread = new KeepHeartBeatThread();
            keepHeartBeatThread.start();
        }
    }

    public void keepGetRebotInfo() {
        if (getReady()) {
            KeepGetRokidInfoThread keepGetRobotInfoThread = new KeepGetRokidInfoThread();
            keepGetRobotInfoThread.start();
        }
    }


    private int REPLY_MAX_LENGTH = 1024 * 10;       //socket数据接收最大长度
    private char buf[] = new char[REPLY_MAX_LENGTH];

    public void getInfo() {
        int len = read(buf, 0, REPLY_MAX_LENGTH - 1);
        if (len != -1) {
            String clientCmd = new String(buf, 0, len);
            Log.e(TAG, clientCmd);
            if (callBack != null) {
                callBack.RokidInfos(clientCmd);
            }
        }
    }

    public int read(char buf[], int offset, int maxLength) {
        int length = 0;
        if (reader != null) {
            try {
                length = reader.read(buf, offset, maxLength);
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return length;
    }
}
