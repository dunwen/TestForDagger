package com.dundunwen.testfordagger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dundunwen.testfordagger.dagger.DaggerModule;
import com.dundunwen.testfordagger.interfaces.LoginPresenter;
import com.dundunwen.testfordagger.interfaces.LoginView;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.ObjectGraph;

public class MainActivity extends AppCompatActivity implements LoginView{

    private final String TAG = MainActivity.this.getClass().getName();

    /*
    * inject的注解就是声明这个玩意需要注入到mainactivity这个类里面来
    * 那么问题来了，我只是告诉dagger我需要注入，那它怎么知道这个玩意的实例去哪里找呢？
    * --这个问题接下来看oncreate的注释
    *
    * 关于inject，也可是使用懒加载的方式 @inject Lazy<LoginPresenter> presenter; 接下来在需要的地方 presenter.get();即可
    * */
    @Inject LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        * dagger需要用户创建一个module，并用@module注释去注释他
        * 接着，在需要创建的时候，用下面的代码注入
        *
        * 如果是dagger2，这里应该是Component了，是建立起@inject和@module的桥梁
        *
        * --看daggerModule这个类
        * */
        ObjectGraph graph = ObjectGraph.create(new DaggerModule(this,this));
        graph.inject(this);

        //经过上面的步骤，就可以直接调用presenter的方法了
        presenter.doLogin("123","123");
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void showProgressDialog(boolean isShow) {

    }

    @Override
    public void loginSuccess() {
        Log.i(TAG, "loginSuccess: ");
    }
}
