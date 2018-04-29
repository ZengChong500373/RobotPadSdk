package hefu.pad.sdk.ui;

import android.os.Bundle;
import android.serialport.SerialPortFinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import hefu.pad.sdk.R;
import hefu.pad.sdk.serial.Device;
import hefu.pad.sdk.serial.SerialPortManager;
import hefu.pad.sdk.utils.ToastUtils;

/**
 * Created by zc on 2018/4/18.
 */

public class SerialActivity extends AppCompatActivity {
    private String TAG = "SerialActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);
    }

    public void open(View view) {
        Device mDevice = new Device();
        mDevice.setBaudrate("115200");
        mDevice.setPath("/dev/ttyS3");
        Boolean mOpened = SerialPortManager.instance().open(mDevice) != null;
        if (mOpened) {
            ToastUtils.getInstance().show("成功打开串口");
        } else {
            ToastUtils.getInstance().show("打开串口失败");
        }
    }

    public void getList(View view) {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        // 设备
        String[] mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices == null) {
            ToastUtils.getInstance().show("没有串口设备");
            return;
        }
        for (int i = 0; i < mDevices.length; i++) {
            Log.e("TAG", mDevices[i]);
        }
    }

    public void retrunOrigin(View view) {

    }

    public void up(View view) {
        String text="4EB6967DAE79 14a51a024bdf 450 60";

        SerialPortManager.instance().sendCmdDirection(text);
    }

    public void back(View view) {
        String text="4EB6967DAE79 14a51a024bdf 451 60";

        SerialPortManager.instance().sendCmdDirection(text);
    }

    public void left(View view) {
        String text="4EB6967DAE79 14a51a024bdf 452 60";
        SerialPortManager.instance().sendCmdDirection(text);
    }

    public void right(View view) {
        String text="4EB6967DAE79 14a51a024bdf 453 60";
        SerialPortManager.instance().sendCmdDirection(text);
    }


}
