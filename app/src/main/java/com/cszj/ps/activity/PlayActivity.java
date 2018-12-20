package com.cszj.ps.activity;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.cszj.ps.R;
import com.cszj.ps.activity.util.SharedHelper;
import com.cszj.ps.bean.ConstantsUtil;

public class PlayActivity extends BaseActivity implements View.OnClickListener{
    private String TAG = "PlayActivity";
    private Context mContext;
    private WebView webView;
    private ImageView leftImage,rightImage;
    private String url = "";

    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
        setContentView(R.layout.activity_play);
        mContext = this;


        leftImage = (ImageView)findViewById(R.id.leftImage);
        rightImage = (ImageView)findViewById(R.id.rightImage);
        leftImage.setOnClickListener(this);
        rightImage.setOnClickListener(this);

        webView = (WebView)findViewById(R.id.wv_webview);

        webView.setWebViewClient(new WebViewClient() {
             @Override
             public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                 // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                 // super.onReceivedSslError(view, handler, error);
                 // 接受所有网站的证书，忽略SSL错误，执行访问网页
                 handler.proceed();
             }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        userId = SharedHelper.getUserId(PlayActivity.this);
        url = "http://iot-ai.tuling123.com/jump/app/source?apiKey="+ ConstantsUtil.MQTT_APP_KEY+"&uid="+userId+"&client=android";
        Log.i(TAG," url:"+url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);

    }


    /**
     * 拿到上一页的路径
     */
    private  void myLastUrl(){
        WebBackForwardList backForwardList = webView.copyBackForwardList();
        Log.i(TAG," myLastUrl backForwardList.getSize():"+backForwardList.getSize());
        if (backForwardList != null && backForwardList.getSize() != 0) {
            //当前页面在历史队列中的位置
            int currentIndex = backForwardList.getCurrentIndex();
            Log.i(TAG," myLastUrl currentIndex:"+currentIndex);
            WebHistoryItem historyItem =
                    backForwardList.getItemAtIndex(currentIndex - 1);
            if (historyItem != null) {
                String backPageUrl = historyItem.getUrl();
                Log.i(TAG,"拿到返回上一页的url:"+backPageUrl);
                webView.goBack();

            }
        }
    }


    /**
     * 拿到下一页的路径
     */
    private  void myNextUrl(){
        WebBackForwardList backForwardList = webView.copyBackForwardList();

        Log.i(TAG," myNextUrl backForwardList.getSize():"+backForwardList.getSize());
        if (backForwardList != null && backForwardList.getSize() != 0) {
            //当前页面在历史队列中的位置
            int currentIndex = backForwardList.getCurrentIndex();
            Log.i(TAG," myNextUrl currentIndex:"+currentIndex);
            WebHistoryItem historyItem =
                    backForwardList.getItemAtIndex(currentIndex + 1);
            if (historyItem != null) {
                String backPageUrl = historyItem.getUrl();
                Log.i(TAG,"拿到返回下一页的url:"+backPageUrl);
                webView.goForward();
            }
        }
    }


    String type = "left";
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.leftImage:
                type = "left";
                myLastUrl();
                break;
            case R.id.rightImage:
                type = "right";
                myNextUrl();
                break;
        }
    }
}
