package com.test.jingmengsong.rxjavastudy;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Rxjava2 变换操作符
 * 实际开发场景  callback hell 嵌套回调 （注册登录链式调用）
 */
public class AlterActivity extends AppCompatActivity {

    private String TAG = "TAG";
    private CompositeDisposable compositeDisposable;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);
        compositeDisposable = new CompositeDisposable();

        // ---------------------------- 变换操作符 ----------------------------

        //map() 作用：对被观察者对象发送的每一个事件进行指定函数处理， 从而变换成另外一种事件
        // 应用场景：数据类型转换

        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "使用map 变换操作符 将事件" + integer + "从整型转换为字符类型" + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "accept: " + s);
            }
        });
        compositeDisposable.add(subscribe);

        //flatMap()  无序的，即与旧序列发送事件的顺序无关
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("事件拆分后" + integer + "i=" + i);
                }

                Log.i(TAG, "事件拆分" + integer);
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "accept: flatmap" + s);
            }
        });

        //contactmap（）  有序的，即与旧序列发送事件的顺序保持一致， 与flatmap其实用法一致，只是区别于有序无序
        //buffer（）作用：定期从 被观察者（Obervable）需要发送的事件中 获取一定数量的事件 & 放到缓存区中，最终发送
        //应用场景 ：缓存被观察者发送的事件
        Observable.just(1, 2, 3, 4, 5)
                .buffer(2, 2)// 设置缓存区大小 & 步长
                // 缓存区大小 = 每次从被观察者中获取的事件数量
                // 步长 = 每次获取新事件的数量
                .subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Integer> stringList) {
                Log.d(TAG, " 缓存区里的事件数量 = " +  stringList.size());
                for (Integer value : stringList) {
                    Log.d(TAG, " 事件 = " + value);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
