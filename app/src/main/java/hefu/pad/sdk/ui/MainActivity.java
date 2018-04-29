package hefu.pad.sdk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import hefu.pad.sdk.R;

public class MainActivity extends AppCompatActivity {
Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void serial(View view) {
        intent=new Intent(this,SerialActivity.class);
        startActivity(intent);
    }

    public void httpServer(View view) {
        intent=new Intent(this,RokidHttpServerActivity.class);
        startActivity(intent);
    }

    public void httpSocket(View view) {
        intent=new Intent(this,RokidHttpClientActivity.class);
        startActivity(intent);
    }
}
