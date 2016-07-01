package com.dundunwen.testfordagger.dagger;

import android.content.Context;

import com.dundunwen.testfordagger.TestLoginPresenterImpl;
import com.dundunwen.testfordagger.MainActivity;
import com.dundunwen.testfordagger.interfaces.LoginPresenter;
import com.dundunwen.testfordagger.interfaces.LoginView;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dun on 16/7/1.
 */

@Module(
        /*这个注解，就是需要注入的类了*/
        injects = {
                MainActivity.class
        },
        /*二这个library的注解是什么鬼呢，上面不是有个injects的注解标注的需要注入的类的列表嘛，
        * 但是有一种情况就是，宝宝的这个module，也可以给其它的类provide一些东西，那么这种情况下，
        * 就需要加上 library = true 避免报错了
        * */
        library = true,

        /*
        * 在这个例子里面，我们providePresenter也是需要两个参数的，在下面的注释也提到，额外的参数需要用额外的providexxxx方法里面获取
        * 这时候，我们一般有两种解决办法，1.把这个providexxxx的方法，写进这个类（这里指代的是DaggerModule这个类），2.就是在Module
        * 里面写上complete = false，表示这个module是不完整的，需要dagger去检索其他的module以获取其他的东西
        * */
        complete = false
)
public class DaggerModule {
    Context mContext;
    LoginView view;
    public DaggerModule(Context mContext,LoginView view) {
        this.mContext = mContext;
        this.view = view;
    }


    /*
    * 我们这里用到了LoginPresenter这个注入了，那么问题来了，木有参数的调用还好，
    * 那么有参数的调用要怎么办？？？？这个函数的调用时dagger给我们完成的，那么dagger
    * 去哪里找我们的mContext和view对象呢？？？？
    *
    * 这时候就需要我们额外提供这两个玩意的provider了，我写在了这里，但是也可以写在其它module，但是必须在module注解里面标注 complete = false。
    *
    * 另外，需要注意的是，你也可以用@Singleton来表示这个provide在全局app范围类获取到的实例都是同一个（就是单例）
    * 除了@Singleton，我们还可以使用这样的注释在注入类
    *  @inject @Name("TestLoginPresenter") LoginPresenter testPresenter;
    *  @inject @Name("LoginPresenter") LoginPresenter Presenter;
    * 在module类使用如下
    * @Provides
    * @Named("TestLoginPresenter")
    * LoginPresenter provideTestLoginPresenter(Context mContext, LoginView view){}
    *
    * @Provides
    * @Named("LoginPresenter")
    * LoginPresenter provideTestLoginPresenter(Context mContext, LoginView view){}
    * 这样，就可以标示两个不同的实例，如果这样仍然不能满足你的需要，你可以使用自定义限定符 @Qualifier，在官方的demo里面也有
    *
    *
    *
    *  @inject Provider<LoginPresenter> providerPresenter;
    *  Object o1 = providerPresenter.get();
    *  Object o2 = providerPresenter.get();
    *  除非标示了@Singleton 否则这两个是不同的对象
    * */

    @Provides
    LoginPresenter provideLoginPresenter(LoginView view){
        //用于测试的presenter，用于单元测试啊，什么的
        return new TestLoginPresenterImpl(view);
        //真正集成在app里面的presenter，这里也体现了dagger的好处了，在测试版本的时候使用测试版本的presenter，在正式版本的时候
        //使用正式版本的presenter。
//        return new LoginPresenterImpl(view);
    }

    /*
    * 给provideLoginPresenter提供view，这个我们在构造函数传进来了，
    *
    * */
    @Provides
    LoginView provideLoginView(){
        return this.view;
    }
    /*
    * 给provideLoginPresenter提供context
    * */
    @Provides
    Context provideContext(){
        return this.mContext;
    }


}
