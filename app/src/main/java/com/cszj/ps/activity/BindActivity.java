package com.cszj.ps.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cszj.ps.bean.ConstantsUtil;
import com.cszj.ps.R;
import com.cszj.ps.bean.ResponseInfo;
import com.cszj.ps.activity.util.SharedHelper;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/*
绑定 和 解绑 设备
 */
public class BindActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = "BindActivity";
    private Context mContext;
    private Activity mActivity;

    private LinearLayout username_Linear,imageurl_Linear;
    private TextView bind_textView;
    private EditText userid_Edit, username_Edit, imageurl_Edit ,deciceId_Edit;
    private Button bind_Btn;

    private String[] bindTypeArray = new String[] {"bind","unbind"};
    private String bindType;

    String deviceId = "ai2232100000006";
//    String deviceId = "ai12345678901033";
    String uid = "";
    String name = "czh";
    String imageUrl = "http://shp.qpic.cn/hd_priv_pic/0/153213686957a32fecc65728ab14b6d48ead607138/0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        mContext = this;
        mActivity = this;

        bindType = getIntent().getStringExtra("bindType");

        initView();

    }

    public void initView(){
        username_Linear =  findViewById(R.id.username_Linear);
        imageurl_Linear =  findViewById(R.id.imageurl_Linear);

        bind_textView = findViewById(R.id.bind_textView);
        userid_Edit =  findViewById(R.id.userid_Edit);
        username_Edit =  findViewById(R.id.username_Edit);
        imageurl_Edit =  findViewById(R.id.imageurl_Edit);
        bind_Btn =  findViewById(R.id.bind_Btn);
        deciceId_Edit = findViewById(R.id.devices_edt);

        bind_Btn.setOnClickListener(this);

        if(bindTypeArray[0].equals(bindType)){
            bind_textView.setText(getString(R.string.zj_bind_device));
            bind_Btn.setText(getString(R.string.zj_bind));
        }else{
            bind_textView.setText(getString(R.string.zj_unbind_device));
            username_Linear.setVisibility(View.GONE);
            imageurl_Linear.setVisibility(View.GONE);
            bind_Btn.setText(getString(R.string.zj_unbind));
        }

        uid = SharedHelper.getUserId(this);
        userid_Edit.setText(uid);
        username_Edit.setText(name);
        imageurl_Edit.setText(imageUrl);
        deciceId_Edit.setText(deviceId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bind_Btn:
                uid = userid_Edit.getText().toString().trim();
                SharedHelper.saveUserId(uid,BindActivity.this);
                name = username_Edit.getText().toString().trim();
                imageUrl = imageurl_Edit.getText().toString().trim();
                deviceId = deciceId_Edit.getText().toString().trim();
                if(bindTypeArray[0].equals(bindType)){
                    bindDevice(uid,name,imageUrl);
                }else{
                    unbindDevice(uid);
                }
                break;
        }
    }


    // 绑定设备
    public void bindDevice(String uid, String name, String imageUrl){

        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.通过RequestBody.create 创建requestBody对象
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("apiKey", ConstantsUtil.MQTT_APP_KEY)
                .addFormDataPart("uid", uid)
                .addFormDataPart("deviceId",deviceId)
                .addFormDataPart("name",name)
                .addFormDataPart("imageUrl",imageUrl)
                .build();

        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://iot-ai.tuling123.com/app-author/bind").post(requestBody).build();

        //4.创建一个call对象,参数就是Request请求对象
        okhttp3.Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.i(TAG,"onFailure:");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                Gson gson=new Gson();
                final ResponseInfo mResponseInfo = gson.fromJson(response.body().string(), ResponseInfo.class);//把JSON字符串转为对象\
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mResponseInfo.getCode() == 0){
                            Toast.makeText(mContext, "绑定成功", Toast.LENGTH_LONG).show();
                            ConstantsUtil.isResetWeb = true ;
                        }else{
                            Toast.makeText(mContext, mResponseInfo.getDesc(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

        });
    }

    // 绑定设备
    public void unbindDevice(String uid){

        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.通过RequestBody.create 创建requestBody对象
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("apiKey", ConstantsUtil.MQTT_APP_KEY)
                .addFormDataPart("uid", uid)
                .addFormDataPart("deviceId",deviceId)
                .build();

        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://iot-ai.tuling123.com/app-author/unbind").post(requestBody).build();

        //4.创建一个call对象,参数就是Request请求对象
        okhttp3.Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.i(TAG,"onFailure:");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Gson gson=new Gson();
                final ResponseInfo mResponseInfo = gson.fromJson(response.body().string(), ResponseInfo.class);//把JSON字符串转为对象
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mResponseInfo.getCode() == 0){
                            Toast.makeText(mContext, "解绑成功", Toast.LENGTH_LONG).show();
                            ConstantsUtil.isResetWeb = true ;
                        }else{
                            Toast.makeText(mContext, mResponseInfo.getDesc(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
             }

        });
    }

}
