package com.cszj.ps.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cszj.ps.R;
import com.cszj.ps.activity.util.SharedHelper;
import com.turing123.libs.android.connectivity.ConnectionManager;
import com.turing123.libs.android.utils.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
配网界面
 */
public class ConnhostActivity extends BaseActivity {
    private String TAG = "ConnhostActivity";
    private static final int SHOW_CONNECT_RESULT = 1;
    private static final int CONNECT_TIME_OUT = 2 ;
    TextView tvConnResult , tvWifi;
    EditText etSSID;
    EditText etPassword;
    EditText etType;
    EditText etName;
    EditText etImageUrl;

    private ServerSocket server;
    private boolean threadFlag ;
    private int port ;

    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CONNECT_RESULT:
                    tvConnResult.setText(msg.obj.toString());
                    break;
                case CONNECT_TIME_OUT:
                    tvConnResult.setText(msg.obj.toString());
                    break;
                case 3:
                    finish();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connhost);
        tvConnResult = (TextView) findViewById(R.id.tv_conn_result);
        tvWifi = findViewById(R.id.tv_wifi);
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

//                if(!wifiId.contains("CSZJ_AP_666")){
//                    Toast.makeText(ConnhostActivity.this,"请先连接WiFi：CSZJ_AP_666",Toast.LENGTH_LONG).show();
//                    return;
//                }
                final String [] ips = ip.split("\\.");
                if(ips.length != 4){
                    Toast.makeText(ConnhostActivity.this,"IP获取失败",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String serverIP = ips[0]+"."+ips[1]+"."+ips[2]+".1";
                        Message message = new Message();
                        message.what = CONNECT_TIME_OUT ;
                        message.obj = "向IP:"+serverIP+"发送消息失败，检查WiFi是否连接到CSZJ_AP_666。 端口:"+port;
                        mainHandler.sendMessageDelayed(message,5000); //延时操作为7秒
                        SendString("ip:" + ip.trim(), serverIP ,9999);

                    }
                }).start();

            }
        });

        regBroadcast();
        wifiName();
        initSocket();
    }

    private void initSocket(){
        threadFlag = true ;
        port = 9999;
        while(port > 9000){
            try {
                server = new ServerSocket(port);
                break;
            } catch (Exception e) {
                port--;
            }
        }
        Logger.e(TAG,"port========="+port);
        Thread receiveFileThread = new Thread(new Runnable(){
            @Override
            public void run() {
                while(threadFlag){
                    ReceiveString();
                }
            }
        });
        receiveFileThread.start();
    }

    void ReceiveString(){
        try{
            Socket data = server.accept();
            InputStream dataStream = data.getInputStream();
            int count = 0 ;
            int len = 0 ;
            while (len == 0){
                len = dataStream.available();
                count ++ ;
                if(count > 100){
                    len = 1024 ;
                    break;
                }
            }
            Logger.e(TAG,"dataStream.available()==="+len+" count:"+count);
            byte[] buffer = new byte[len];
            String content = "";
            while (dataStream.read(buffer) != -1){
                content= content + (new String(buffer,"UTF-8"));
            }
            dataStream.close();
            data.close();
            content = content.trim() ;
            Logger.e(TAG,"content:"+content);
            if(!TextUtils.isEmpty(content)){
                doMessages(content);
            }
        }catch(Exception e){
            Logger.e(TAG,"接收错误::"+e.getMessage());
        }
    }

    /*
    处理接收的消息
     */
    private void doMessages(String content){
        if(content.startsWith("message:")){
            mainHandler.removeMessages(CONNECT_TIME_OUT);
            SharedHelper.saveMQTT_APP_KEY(content.split(":")[1],this);
            SharedHelper.saveDeviceId(content.split(":")[2],this);

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
            boolean ret = ConnectionManager.connectAsAp(ConnhostActivity.this, 22334,
                    wifiName, wifiPwd, type, customData);
            Message message = Message.obtain();
            message.what = SHOW_CONNECT_RESULT;
            message.obj = "KEY:"+content.split(":")[1]+"\nDeviceId:"+content.split(":")[2]+"\nSend wifi info State: " + ret;
            mainHandler.sendMessage(message);

            if(ret){
                mainHandler.sendEmptyMessageDelayed(3,3000);  //3秒后关闭页面
            }
        }
    }
//991207
    public void SendString(String content, String ipAddress, int port){
        try {
            Logger.e(TAG,"ipAddress==="+ipAddress+" port:"+port+" content"+content);
            Socket data = new Socket(ipAddress, port);
            OutputStream outputData = data.getOutputStream();
            InputStream Input = new ByteArrayInputStream(content.getBytes());
            int size = -1;
            byte[] buffer = new byte[1024];
            while((size = Input.read(buffer, 0, 1024)) != -1){
                outputData.write(buffer, 0, size);
            }
            outputData.close();
            Input.close();
            data.close();
//            SendMessage(0, "发送完成");
            Logger.e(TAG,"发送完成:");
        } catch (Exception e) {
//            SendMessage(0, "发送错误:\n" + e.getMessage());
            Logger.e(TAG,"接收错误::"+e.getMessage());
        }
    }

    /**
     注册广播接收器  电量和wifi监听
     */
    private void regBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mtouchReceiver, filter);
    }


    /**
     * 广播接收器  电量和wifi监听
     */
    private BroadcastReceiver mtouchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.i(TAG,"CONNECTIVITY_ACTION action:"+action);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {//网络变化
                wifiName();
            }
        }
    };

    private String wifiId = "";
    private String ip = "";
    private void wifiName(){
        WifiManager wifiMgr = (WifiManager)(getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        WifiInfo info = wifiMgr.getConnectionInfo();
        wifiId = info != null ? info.getSSID() : null;
        ip = GetIpAddress(info);
        Logger.i(TAG,"wifiId:"+wifiId + "  IP:"+ip);
        tvWifi.setText("当前连接wifi:"+wifiId+"  IP:"+ip);
    }

    private String GetIpAddress(WifiInfo wifiInfo) {
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadFlag = false ;
        if(server != null ){
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server = null;
        }
        unregisterReceiver(mtouchReceiver);
    }
}
