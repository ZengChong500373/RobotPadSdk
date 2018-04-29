package hefu.pad.sdk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.aiui.uartkit.UARTAgent;
import com.iflytek.aiui.uartkit.constant.UARTConstant;
import com.iflytek.aiui.uartkit.entity.AIUIPacket;
import com.iflytek.aiui.uartkit.entity.CustomPacket;
import com.iflytek.aiui.uartkit.entity.MsgPacket;
import com.iflytek.aiui.uartkit.listener.EventListener;
import com.iflytek.aiui.uartkit.listener.UARTEvent;
import com.iflytek.aiui.uartkit.util.PacketBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import hefu.pad.sdk.R;

/**
 * Created by zc on 2018/4/29.
 */

public class AiuiActivity extends AppCompatActivity {
    private static final String TAG = "UART_Controller";

    public UARTAgent mAgent;
    private EditText ed;
    private TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aiui);
        tv = (TextView) findViewById(R.id.tv);
        ed= (EditText) findViewById(R.id.ed);
//		ttyUSB0 ttyS2
        mAgent = UARTAgent.createAgent("/dev/ttyUSB0", 115200, new EventListener() {

            @Override
            public void onEvent(UARTEvent event) {
                switch (event.eventType) {
                    case UARTConstant.EVENT_INIT_SUCCESS:

                        showMessage("Init UART Success");
                        Log.d(TAG, "Init UART Success");
                        break;

                    case UARTConstant.EVENT_INIT_FAILED:
                        showMessage("Init UART Failed");
                        Log.d(TAG, "Init UART Failed");
                        break;

                    case UARTConstant.EVENT_MSG:
                        MsgPacket recvPacket = (MsgPacket) event.data;

                        processPacket(recvPacket);
                        break;

                    case UARTConstant.EVENT_SEND_FAILED:
                        MsgPacket sendPacket = (MsgPacket) event.data;
                        mAgent.sendMessage(sendPacket);
                    default:
                        break;
                }
            }
        });
    }
    private void processPacket(MsgPacket packet) {
        Log.d(TAG, "type " + packet.getMsgType());
        switch (packet.getMsgType()) {
            case MsgPacket.AIUI_PACKET_TYPE:
                String content = new String(((AIUIPacket) packet).content);
                Log.d(TAG, "recv aiui result" + content);

                showMessage("recv aiui result" + content);
                proecssAIUIPacket(content);
                break;

            case MsgPacket.CUSTOM_PACKET_TYPE:
                Log.d(TAG, "recv aiui custom data " + Arrays.toString(((CustomPacket) packet).customData));
                showMessage("recv aiui custom data " + Arrays.toString(((CustomPacket) packet).customData));
                break;

            default:
                break;
        }
    }

    private void proecssAIUIPacket(String content) {
        try {
            JSONObject data = new JSONObject(content);
            String type = data.optString("type", "");
            //OTA message
            if (type.equals("ota")) {
//                OTAProcessor.process(data);
                showMessage(data.toString().trim());
            } else if (type.equals("smartConfig")) {
//                SmartConfigProcessor.process(data);
                Log.d(TAG, "data " + data);
            } else {
                Log.d(TAG, "type: " + type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showMessage(final String str) {
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(str);
            }
        });
    }

    public void send(View view) {
        String str=ed.getText().toString().trim();
        MsgPacket msgPacket = PacketBuilder.obtainTTSStartPacket(str);
        if (null != mAgent) {
            mAgent.sendMessage(msgPacket);
        }
    }
}
