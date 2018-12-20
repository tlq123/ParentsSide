package com.cszj.ps.frame;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.cszj.ps.R;
import com.cszj.ps.activity.BindActivity;
import com.cszj.ps.activity.ConnhostActivity;
import com.cszj.ps.activity.LoginActivity;
import com.cszj.ps.activity.util.SharedHelper;
import com.cszj.ps.bean.ConstantsUtil;

/*
我de界面
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    private TextView tvUserName ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        tvUserName = view.findViewById(R.id.me_user_name);
        tvUserName.setText(SharedHelper.getUserId(getActivity()));
        view.findViewById(R.id.me_bandle).setOnClickListener(this);
        view.findViewById(R.id.me_unbandle).setOnClickListener(this);
        view.findViewById(R.id.me_peiwang).setOnClickListener(this);
        view.findViewById(R.id.me_zhanghao).setOnClickListener(this);
        view.findViewById(R.id.me_check_app).setOnClickListener(this);
    }

    public void updateName(){
        if(tvUserName != null){
            tvUserName.setText(SharedHelper.getUserId(getActivity()));
        }
    }

    private void checkAppCode(){
        BDAutoUpdateSDK.uiUpdateAction(getActivity(), new UICheckUpdateCallback() {
            @Override
            public void onNoUpdateFound() {
                Toast.makeText(getContext(),"当前已是最新版本",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCheckComplete() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.me_bandle:  //绑定
                intent.setClass(getActivity(),BindActivity.class);
                intent.putExtra("bindType","bind");
            break;
            case R.id.me_unbandle://解绑
                intent.setClass(getActivity(),BindActivity.class);
                intent.putExtra("bindType","unbind");
                break;
            case R.id.me_peiwang: //配网
                intent.setClass(getActivity(),ConnhostActivity.class);
                intent.putExtra("bindType","unbind");
                break;
            case R.id.me_zhanghao: //切换账号
                ConstantsUtil.isResetWeb = true ;
                intent.setClass(getActivity(),LoginActivity.class);
                break;
            case R.id.me_check_app:  //版本更新
                checkAppCode();
                break;
        }
        if(view.getId() != R.id.me_check_app){
            startActivity(intent);
        }
    }
}
