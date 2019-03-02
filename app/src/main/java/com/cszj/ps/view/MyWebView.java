package com.cszj.ps.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    //嵌套在SwipeRefreshLayout 此方法不被调用
//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);
//        Log.e("MusicFragment","l="+l+" t="+t+" oldl="+oldl+" oldt="+oldt);
//        Log.e("MusicFragment","getScrollY="+this.getScrollY());
//        if (mScrollListener != null) {
//            mScrollListener.onScrollChanged(t);
//        }
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                很简单的重写，每次按下的时候，如果在0,0坐标，让它滚动到0,1，这样就会告诉SwipeRefreshLayout他还在滑动，就不会触发刷新事件了。
                if(this.getScrollY() <= 0)
                    this.scrollTo(0,1);
                break;
            case MotionEvent.ACTION_UP:
//                if(this.getScrollY() == 0)
//                this.scrollTo(0,-1);
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface IScrollListener {
        void onScrollChanged(int scrollY);
    }

    private IScrollListener mScrollListener;

    public void setOnScrollListener(IScrollListener listener) {
        mScrollListener = listener;
    }
}
