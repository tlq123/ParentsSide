package com.cszj.ps.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cszj.ps.R;
import com.cszj.ps.frame.MeFragment;
import com.cszj.ps.frame.MusicFragment;

public class  MainActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout musicLayout ,meLayout ;
    private MusicFragment musicFragment ;
    private MeFragment meFragment ;
    private FragmentManager fragmentManager ;
    private int fragmentIndex ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();

    }

    @Override
    public void onResume() {
        super.onResume();
        //解绑或者绑定设备时 进行刷新
        musicFragment.webReload();
        meFragment.updateName();
    }

    private void init(){
        musicLayout = findViewById(R.id.main_music);
        meLayout = findViewById(R.id.main_me);
        musicLayout.setOnClickListener(this);
        meLayout.setOnClickListener(this);
        musicLayout.setSelected(true);
        meLayout.setSelected(false);

        musicFragment = new MusicFragment();
        meFragment = new MeFragment();
        fragmentManager = getSupportFragmentManager() ;
        FragmentTransaction transaction = fragmentManager.beginTransaction() ;
        transaction.add(R.id.main_frame_content,musicFragment);
        transaction.add(R.id.main_frame_content,meFragment);
        transaction.hide(meFragment).show(musicFragment).commit();
        fragmentIndex = 1 ;


    }

    /*
    主界面的切换
     */
    private void selectTab(int index){
        if(index == fragmentIndex ){
            return;
        }else {
            fragmentIndex = index ;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction() ;
        if(index == 1){
            musicLayout.setSelected(true);
            meLayout.setSelected(false);
            transaction.hide(meFragment).show(musicFragment).commit();
        }else {
            musicLayout.setSelected(false);
            meLayout.setSelected(true);
            transaction.hide(musicFragment).show(meFragment).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.main_music:
                selectTab(1);
                break;
            case R.id.main_me:
                selectTab(2);
                break;
        }
    }

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    //使用Webview的时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if( keyCode == KeyEvent.KEYCODE_BACK ){
            //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
            if((fragmentIndex == 1) && musicFragment.isCanBack()){
                musicFragment.goBack();
                return true;
            }else {
                //双击进行退出应用
                if (System.currentTimeMillis()-firstTime > 2000){
                    Toast.makeText(getApplicationContext(),"再按一次退出程序" ,Toast.LENGTH_SHORT).show();
                    firstTime = System.currentTimeMillis();
                }else{
                    finish();
                }
                return  true ;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

