package com.cszj.ps.activity;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cszj.ps.R;
import com.turing123.libs.android.connectivity.ConnectionManager;
import com.turing123.libs.android.connectivity.ConnectionStatusCallback;

public class NetActivity extends BaseActivity {
    private static final int SHOW_CONNECT_RESULT = 1;
    private String strname;
    private String strpasswd;
    TextView tvConnResult;
    EditText etSSID;
    EditText etPassword;
    EditText etType;
    EditText etCustomData;

    Button btn_password_change;
    boolean flag = false;
    String str;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        getConnectWifiSsid();
        Log.d("wifi名称",getConnectWifiSsid());
        mContext = getApplicationContext();


        /**
         * 联网示例： WIFI-DIRECT
         * 需要权限：
         *     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
         *     <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
         *     <uses-permission android:name="android.permission.INTERNET"/>
         */
        tvConnResult = (TextView) findViewById(R.id.tv_conn_result);
        etSSID = (EditText) findViewById(R.id.ssid);
        etPassword = (EditText) findViewById(R.id.password);
        etType = (EditText) findViewById(R.id.type);
        etCustomData = (EditText) findViewById(R.id.customData);

        btn_password_change = (Button)findViewById(R.id.btn_password_change);
//        取消wifi名称的""
        str = getConnectWifiSsid().toString().replace("\"","");

        etSSID.setText(str);
        //设置初始密码隐藏
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());



        btn_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == false) {
//                    设置密码可见
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = true;
                    btn_password_change.setBackgroundResource(R.drawable.btn_blue_shape_ovl);
                } else {
//                    设置密码不可见
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = false;
                    btn_password_change.setBackgroundResource(R.drawable.btn_blue_shape);
                }
            }
        });
//        btn_ssid_change.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (flag == true) {
//                    etSSID.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    flag = false;
//                    btn_ssid_change.setText("名称可见");
//                } else {
//                    etSSID.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    flag = true;
//                    btn_ssid_change.setText("名称不可见");
//                }
//
//            }
//        });

        findViewById(R.id.btn_conn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 创建wifi configuration 对象。
                WifiConfiguration configuration = new WifiConfiguration();
                //2. 替换需要让机器人连接上的wifi的ssid.
                configuration.SSID = "\"" + etSSID + "\"";
                //3. 配置密码管理方案。
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                //4. 设置wifi密码。
                configuration.preSharedKey = "\"" + etPassword + "\"";
                //5. 向机器人发送联网信息。
                ConnectionManager.connectAsP2P(getApplicationContext(), configuration,
                        new ConnectionStatusCallback() {
                            @Override
                            public void onConnectionCompleted(int status) {
                                //6. 联网结果状态回调
                                Message message = Message.obtain();
                                message.what = SHOW_CONNECT_RESULT;
                                message.obj = "Send wifi info State: " + status;
                                mainHandler.sendMessage(message);
                            }
                        });
            }
        });

        /**
         * 联网示例： AP
         */
        findViewById(R.id.btn_conn_ap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //记住密码
                strpasswd = etPassword.getText().toString();

                //1. 替换为需要连接的端口号，wifi ssid, pwd 和 type,注意网络操作不要再主线程中进行
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean ret = ConnectionManager.connectAsAp(NetActivity.this, 22334,
                                etSSID.getText().toString(), etPassword.getText().toString(),
                                etType.getText().toString(), etCustomData.getText().toString());
                        Message message = Message.obtain();
                        message.what = SHOW_CONNECT_RESULT;
                        message.obj = "Send wifi info state State: " + ret;
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

    //    获取设备当前连接wifi名称
    private String getConnectWifiSsid(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID",wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }



    @Override
    protected void onStart() {
        super.onStart();
    }
}
