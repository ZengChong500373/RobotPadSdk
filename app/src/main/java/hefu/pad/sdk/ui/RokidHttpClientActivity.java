package hefu.pad.sdk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import hefu.pad.sdk.R;
import hefu.pad.sdk.http.RokidCmdSocket;
import hefu.pad.sdk.listener.RokidInfoCallBack;
import hefu.pad.sdk.utils.NetUtil;

/**
 * Created by zc on 2018/4/20.
 *
 *
 */

public class RokidHttpClientActivity extends AppCompatActivity implements RokidInfoCallBack {
    EditText ed;
    TextView tv_receive;
    public RokidCmdSocket socket;
    private String TAG = "RokidHttpClientActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rokid_http_client);
        ed = findViewById(R.id.ed);
        tv_receive = findViewById(R.id.tv_receive);

    }

    public void openSocket(View view) {
        NetUtil netUtil = new NetUtil();
        List<NetUtil.IP_MAC> list = netUtil.getLocalDevices();
        socket = RokidCmdSocket.getInstance();
        socket.setRobotInfoCallBack(this);

        socket.setIpList(list);
        socket.setPort(9007);
        socket.start();
    }

    public void sendTts(View view) {
        String str = getText();
        if (TextUtils.isEmpty(str)) {
            return;
        }
        socket.robotCmd("tts" + str);
    }

    public void sendAsr(View view) {
        String str = getText();
        if (TextUtils.isEmpty(str)) {
            return;
        }
        socket.robotCmd(str);
    }

    public String getText() {
        return ed.getText().toString().trim();
    }

    @Override
    public void RokidInfos(String str) {
        tv_receive.setText(str);
    }

    @Override
    public void RokidInfoFail() {
        tv_receive.setText("服务器消息接收失败");
    }
}
