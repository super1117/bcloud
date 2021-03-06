package com.zero.library.mvp.presenter;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.zero.library.mvp.view.IDelegate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Presenter base class for Activity
 * Presenter层的实现基类
 *
 * @param <T> View delegate class type
 */
public abstract class ActivityPresenter<T extends IDelegate> extends AppCompatActivity {

    protected T viewDelegate;

    public ActivityPresenter(){
        try{
            this.viewDelegate = getT(this, 0);
        }catch (Exception e) {
            throw new RuntimeException("create IDelegate error");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.viewDelegate.create(getLayoutInflater(), null, savedInstanceState);
        this.setContentView(this.viewDelegate.getRootView());
        this.initToolbar();
        this.viewDelegate.initWidget();
        this.bindEventListener();
    }

    protected void initToolbar(){
        Toolbar toolbar = this.viewDelegate.getToolbar();
        if(toolbar != null){
            this.setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(this.viewDelegate == null){
            try{
                this.viewDelegate = getT(this, 0);
            }catch (Exception e) {
                throw new RuntimeException("create IDelegate error");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(this.viewDelegate.getOptionMenuId() != 0){
            this.getMenuInflater().inflate(this.viewDelegate.getOptionMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        this.viewDelegate = null;
        super.onDestroy();
    }

    protected void bindEventListener(){}

//    protected abstract Class<T> getDelegateClass();

    public <T> T getT(Object object, int i){
        Type type = object.getClass().getGenericSuperclass();
        if(type instanceof ParameterizedType){
            try{
                return ((Class<T>)((ParameterizedType)type).getActualTypeArguments()[i]).newInstance();
            } catch (java.lang.InstantiationException e){
                e.printStackTrace();
            } catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
