# TestForDagger
Dagger+mvp例子
#Dagger
**本文例子基于mvp**  
[https://github.com/dunwen/TestForDagger](https://github.com/dunwen/TestForDagger)

什么是dagger什么是依赖注入这些概念网上一大堆，我就懒得写了，用一句话总结就是：**用了dagger以后，不需要通过构造函数就可以使用相应的实例**。  
这样做有什么好处？我有很多个activity引用了某个类的相同或不同实例，现在我因为设计问题，修改了这个类的构造函数，那么引用了这个类的其他地方都要修改，使用dagger可以避免这种修改。可以降低耦合什么的好处就不说了，毕竟依赖注入本来就是希望降低耦合的。再如，可以降低测试复杂性，想象一下，我要测试presenter里面的函数，如果是标准的mvp，你可以新建一个TestPresenter，然后在引用的地方修改这个引用为TestPresenter，如果你有多个地方引用，改起来会十分复杂，并且还可能会出错。如果要大规模测试view层里面的调用，需要模拟presenter返回的各种场景，比如，view提出登录请求，一般做法就是在presenter里面模拟返回各种状态，以此来测试view层是否正确，这时候呢，就会有多个TestPresenter的出现了，对于view层的测试，修改起来也是十分复杂的。一扯测试就扯了这么多，什么鬼
#怎么用
其实网上说怎么用已经有很多很多很多例子了。。。可是为毛我还要再写一篇呢，因为宝宝看了其他牛写的宝宝看不懂。。。所以我决定带上自己的理解了，oh，当然不一定对。
##@Inject
`@Inject`inject的注解就是声明这个玩意需要注入到某个类里面来，关于inject，也可是使用懒加载的方式 `@inject Lazy<LoginPresenter> presenter;` 接下来在需要的地方 `presenter.get();`即可  
我下github上面的例子代码就是  

```   
    @Inject LoginPresenter presenter;

```

##@Module
在`@Inject`，我们刚刚声明了，某个字段需要被注入到类里面来，那么问题来了，你只说你要被注入，dagger去哪里找这个类的实例？？？？？这时候我们就需要新建一个Module类了，这个类，需要被`@Module`来注释着，来标示这个类是dagger从里面找你想要的注入实例的类，这个类的形式也很好理解，基本上就是里面一大片`provideXXX()`方法，这个提供xxx的方法需要用`@provides`注解注释着，顾名思义，就是提供注入字段XXX的方法，我写的例子里面就有一个提供上面声明的presenter的方法。

```
    @Provides
    LoginPresenter provideLoginPresenter(LoginView view){
        //用于测试的presenter，用于单元测试啊，什么的
        return new TestLoginPresenterImpl(view);
        //真正集成在app里面的presenter，这里也体现了dagger的好处了，在测试版本的时候使用测试版本的presenter，在正式版本的时候
        //使用正式版本的presenter。
//        return new LoginPresenterImpl(view);
    }
```

在上面这个例子里面，首先，这个方法是我们程序员去调用的吗？答案肯定是否定的，那么下一个问题来了，Dagger去哪弄上面这个函数的参数`(LoginView view)`?,这个时候呢，我们必须弄下一个函数名字叫`provideLoginView`去给Dagger提供这个参数。同样，这个函数也要用`@Provides`去注释

```
    /*
    * 给provideLoginPresenter提供view，这个我们在构造函数传进来了。
    * */
    @Provides
    LoginView provideLoginView(){
        return this.view;
    }
```

那么接下来还是有问题，这个函数应该写在哪？为了复用，我可以写在别的Module里面去吗？我可以就写在这个类里面去吗？答案都是可以的。对于写在别的Module，你得告诉Dagger，这个参数（就是provideLoginPresenter里面的参数）你需要去别的Module里面找，所以呢，你就可以这么标注这个Module：

```
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
```
其他被`Module`包含的常用注解，都在上面代码的注释里面说清楚了，我就懒得再打一遍了

##被@Provides标注的函数
`@Provides`注解就是用来标注Module里面的各种provideXXXX函数的那个注解了，这个providexxxxx的函数，也可以有很多形式，比如可以被`@SingleTon`注释，可以保证全局获取的这个函数的返回值唯一，使用自定义限定符`@Qualifier`或者`@Named`，配合声明的`@Inject @Named("LoginPresenter") LoginPresenter provideTestLoginPresenter`表示Dagger应该区分这几类调用的provideXXX函数是不一样的，需要区分。关于这些，官方文档说的比我清楚
##ObjectGraph
现在呢，宝宝已经声明了某个字段需要依赖注入了，这个字段的提供的实例的方法也写好了，接下来，我要怎么告诉Dagger这个依赖需要到指定的Module里面去找呢？？？？？

```
 ObjectGraph graph = ObjectGraph.create(new DaggerModule(this,this));
 graph.inject(this);
```
在需要被注入的类的初始化时调用如上方法，在此之后，就可以直接使用注入的实例了。
