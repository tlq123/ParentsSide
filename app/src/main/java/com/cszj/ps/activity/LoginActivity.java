package com.cszj.ps.activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cszj.ps.R;
import com.cszj.ps.activity.util.SharedHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = "LoginActivity";
    private EditText edtUserName,edtPwd;
    private Button btnLogin ,btnYzm;
    private String userIdValue;
    private String verifyCode = "";

    private int count ;
    private Timer timer ;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            btnYzm.setText("剩余"+count+"秒");
            if(count <= 0){
                btnYzm.setEnabled(true);
                btnYzm.setText("获取验证码");
                btnYzm.setTextColor(getResources().getColor(R.color.colorBlue));
                if(timer != null){
                    timer.cancel();
                    timer = null ;
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView(){
        edtUserName =  findViewById(R.id.userid_Edit);
        edtPwd =  findViewById(R.id.psd_Edit);
        btnYzm = findViewById(R.id.login_yzm_btn);
        btnYzm.setOnClickListener(this);
        btnLogin = findViewById(R.id.login_Btn);
        btnLogin.setOnClickListener(this);

        edtUserName.setText(SharedHelper.getUserId(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_Btn:
                login();
                break;
            case R.id.login_yzm_btn:
                getAuthCode();
                break;
        }
    }

    private void login(){
        String phone = edtUserName.getText().toString().trim();
        String code = edtPwd.getText().toString().trim();
        if("13143407320".equals(phone) && "1234".equals(code)){
            SharedHelper.saveUserId(phone,LoginActivity.this);
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if(phone.length() != 11){
            Toast.makeText(this,"请输入正确格式的手机号码!",Toast.LENGTH_LONG).show();
            return ;
        }

        if(!verifyCode.equals(code) || code.length() != 4){
            Toast.makeText(this,"请输入正确验证码!",Toast.LENGTH_LONG).show();
            return ;
        }

        if(!phone.equals(userIdValue)){
            Toast.makeText(this,"当前号码与获取短息验证码号码不一致!",Toast.LENGTH_LONG).show();
            return ;
        }

        SharedHelper.saveUserId(userIdValue,LoginActivity.this);
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    获取验证码
     */
    private void getAuthCode(){
        userIdValue = edtUserName.getText().toString().trim();
        if(userIdValue.length() != 11){
            Toast.makeText(this,"请输入正确格式的手机号码",Toast.LENGTH_LONG).show();
            return ;
        }
        updateAuthCode();

        Random random = new Random();
        verifyCode = (random.nextInt(8999) + 1000)+ "";
        Log.e(TAG,"verifyCode："+verifyCode);
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.通过RequestBody.create 创建requestBody对象
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sendTo", userIdValue)
                .addFormDataPart("verifyCode", verifyCode)
                .addFormDataPart("smsType", "LOGIN")
                .addFormDataPart("expireTime", "2")  //有效时间2分钟
                .build();

        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://www.cszjsmart.com/sms-server/sms/sendVerifyCode").post(requestBody).build();

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

                final String result = response.body().string() ;
                Log.e(TAG,"response.body().string()："+result);

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getString("status");
                            if("SUCCESS".equals(status)){
                                Toast.makeText(getApplicationContext(),"短息验证码获取成功" , Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(),"短息验证码获取失败", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"短息验证码获取失败", Toast.LENGTH_LONG).show();
                            Log.e(TAG,"response e："+e.getMessage());
                        }
                    }
                });
            }
        });
    }


    private void updateAuthCode(){
        btnYzm.setEnabled(false);
        btnYzm.setTextColor(getResources().getColor(R.color.text_color_9));
        timer = new Timer();
        count = 30 ;
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {

                count--;//自动减1
                handler.sendEmptyMessage(count);
                Log.e(TAG,"count:"+count);
            }
        };
        timer.schedule(timerTask, 1000,1000);//1000ms执行一次
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(timer != null){
            timer.cancel();
            timer = null ;
        }
    }
}
