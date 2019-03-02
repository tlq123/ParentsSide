package com.cszj.ps.frame;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cszj.ps.R;
import com.cszj.ps.activity.util.SharedHelper;
import com.cszj.ps.bean.ConstantsUtil;
import com.cszj.ps.view.MyWebView;


/*
点播界面
 */
public class MusicFragment extends Fragment {

    private MyWebView webView  ;
    private SwipeRefreshLayout swipeLayout ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        webView = view.findViewById(R.id.fragmen_music_web);
        swipeLayout =  view.findViewById(R.id.swipe_container);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                // super.onReceivedSslError(view, handler, error);
                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler.proceed();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //重新刷新页面
                ConstantsUtil.isResetWeb = true ;
                webReload();
            }
        });

//        swipeLayout.setColorScheme(R.color.holo_blue_bright,
//                R.color.holo_green_light, R.color.holo_orange_light,
//                R.color.holo_red_light);
        //设置进度条
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //隐藏进度条
                    swipeLayout.setRefreshing(false);
                } else {
                    if (!swipeLayout.isRefreshing())
                        swipeLayout.setRefreshing(true);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        webView.setOnScrollListener(new MyWebView.IScrollListener() {
            @Override
            public void onScrollChanged(int scrollY) {
                Log.e("MusicFragment","scrollY=========="+scrollY);
                if (scrollY == 0) {
                    //开启下拉刷新
                    swipeLayout.setEnabled(true);
                } else {
                    //关闭下拉刷新
                    swipeLayout.setEnabled(false);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        String userId = SharedHelper.getUserId(getActivity());
        String apiKey = SharedHelper.getMQTT_APP_KEY(getActivity()) ;
        if(TextUtils.isEmpty(apiKey)){
            apiKey = ConstantsUtil.MQTT_APP_KEY ;
        }
        String url = "http://iot-ai.tuling123.com/jump/app/source?apiKey="+ apiKey +"&uid="+userId+"&client=android";
        Log.d("MusicFragment","url="+url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl(url);
        swipeLayout.setEnabled(false);
    }



    /*
        判断网页是否可以返回
     */
    public boolean isCanBack(){
        return webView.canGoBack();
    }

    public void goBack(){
        webView.goBack();
    }
    //刷新
    public void webReload(){
        if(ConstantsUtil.isResetWeb){
            ConstantsUtil.isResetWeb = false ;
            webView.clearHistory();
            String userId = SharedHelper.getUserId(getActivity());
            String apiKey = SharedHelper.getMQTT_APP_KEY(getActivity()) ;
            if(TextUtils.isEmpty(apiKey)){
                apiKey = ConstantsUtil.MQTT_APP_KEY ;
            }
            String url = "http://iot-ai.tuling123.com/jump/app/source?apiKey="+ apiKey +"&uid="+userId+"&client=android";
            Log.d("MusicFragment","url 2="+url);
            webView.loadUrl(url);
        }

    }
}
