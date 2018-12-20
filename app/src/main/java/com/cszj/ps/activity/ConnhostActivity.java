package com.cszj.ps.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.cszj.ps.R;
import com.cszj.ps.activity.util.SharedHelper;
import com.turing123.libs.android.connectivity.ConnectionManager;

/*
配网界面
 */
public class ConnhostActivity extends BaseActivity {
    private String TAG = "ConnhostActivity";
    private static final int SHOW_CONNECT_RESULT = 1;
    TextView tvConnResult;
    EditText etSSID;
    EditText etPassword;
    EditText etType;
    EditText etName;
    EditText etImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connhost);

        tvConnResult = (TextView) findViewById(R.id.tv_conn_result);
        etSSID = (EditText) findViewById(R.id.ssid);
        etPassword = (EditText) findViewById(R.id.password);
        etType = (EditText) findViewById(R.id.type);
        etName = (EditText) findViewById(R.id.name);
        etImageUrl = (EditText) findViewById(R.id.imageUrl);
        etSSID.setText(SharedHelper.getWifiName(this));
        etPassword.setText(SharedHelper.getWifiPwd(this));

        /**
         * 联网示例： AP
         */
        findViewById(R.id.btn_conn_ap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 替换为需要连接的端口号，wifi ssid, pwd 和 type,注意网络操作不要再主线程中进行

                final String wifiName = etSSID.getText().toString() ;
                final String wifiPwd = etPassword.getText().toString() ;
                final String type = etType.getText().toString() ;
                SharedHelper.saveWifiName(wifiName,ConnhostActivity.this);
                SharedHelper.saveWifiPwd(wifiPwd,ConnhostActivity.this);

                String name = etName.getText().toString() ;
                String imageUrl = etImageUrl.getText().toString() ;
                final String uid = SharedHelper.getUserId(ConnhostActivity.this);
                final String customData = "{\"name\":\""+name+"\""+","+"\"uid\":\""+uid+"\""+","+"\"imageUrl\":\""+imageUrl+"\""+"}";
                Log.i(TAG,"customData:"+customData);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean ret = ConnectionManager.connectAsAp(ConnhostActivity.this, 22334,
                                wifiName, wifiPwd, type, customData);
                        Message message = Message.obtain();
                        message.what = SHOW_CONNECT_RESULT;
                        message.obj = "Send wifi info State: " + ret;
                        mainHandler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CONNECT_RESULT:
                    tvConnResult.setText(msg.obj.toString());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
