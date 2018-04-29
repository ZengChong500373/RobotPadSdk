package hefu.pad.sdk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import hefu.pad.sdk.R;


/**
 * Created by zc on 2018/4/20.
 *
 *
 */

public class RokidHttpServerActivity extends AppCompatActivity {
    EditText ed;
    TextView tv_receive;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rokid_http_server);
        ed = findViewById(R.id.ed);
        tv_receive = findViewById(R.id.tv_receive);
    }

    public void openServer(View view) {
        Intent intent=new Intent(this, RokidServerService.class);
        startService(intent);

    }




}
