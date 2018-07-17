package com.test.jingmengsong.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * rxJava2 条件/ 布尔 操作符
 */
public class ConditionActivity extends AppCompatActivity {

    private String TAG = "TAG";
    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

        mDisposable = new CompositeDisposable();

        // all() 判断发送的每项数据是否 都 满足设置的函数条件, 若都满足则返回true, 否则返回false
        Disposable subscribe = Observable.just(1, 2, 3, 4, 5, 20).all(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {

                Log.d(TAG, "test: ");
                return integer < 10;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

                Log.d(TAG, "accept: " + (aBoolean ? "发送的数据都满足小于10 的条件" : "有不满足小于10 的条件的数据"));
            }
        });
        mDisposable.add(subscribe);

        //takeWhile()  判断发送的每项数据是否满足 设置函数条件； 若发送的数据满足该条件，则发送该项数据；否则不发送
        Disposable subscribe1 = Observable.just(1, 2, 3, 4, 5).takeWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer < 3;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.d(TAG, "accept: takeWhile....." + integer);
            }
        });
        mDisposable.add(subscribe1);

        //skipWhile()  判断发送的每项数据是否满足 设置函数条件； 直到该判断条件 = false 时， 才开始发送observable 的数据
        Disposable subscribe2 = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).skipWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer < 5;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.d(TAG, "accept: skipWhile......." + integer);
            }
        });
        mDisposable.add(subscribe2);

        //takeUntil() 执行到某个条件时 ， 停止发送事件
        // 该判断条件也可以是Observable，即 等到 takeUntil（） 传入的Observable开始发送数据，（原始）第1个Observable的数据停止发送数据
        Disposable subscribe3 = Observable.just(1, 1, 3, 4, 5, 6, 7, 8, 6).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer > 4;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.d(TAG, "accept: takeUntil..." + integer);

            }
        });
        mDisposable.add(subscribe3);

        // TODO: 2018/7/17  理解不是很深刻
        //skipuntil() 等到传入的observable 开始发送数据，原始第一个observable的数据才开始发送数据
        Disposable subscribe4 = Observable.just(1, 2, 3, 4, 5, 6, 7).skipUntil(Observable.timer(5, TimeUnit.SECONDS)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "accept: skipUntil....." + integer);
            }
        });
        mDisposable.add(subscribe4);


        // SequenceEqual（） 判断两个 observable 需要发送的数据是否相同 若相同则返回true 否则返回false
        Disposable subscribe5 = Observable.sequenceEqual(Observable.just(1), Observable.just(1, 3)).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

                Log.d(TAG, "accept: " + (aBoolean ? "发送的数据是相同的" : "发送的数据是不同的"));

            }
        });

        mDisposable.add(subscribe5);

        //contains（） 判断发送的数据中是否包含指定数据
        Disposable subscribe6 = Observable.just(1, 2, 3, 4, 5).contains(4).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d(TAG, "accept: " + (aBoolean ? "包含这个数据" : "不包含这个数据"));
            }
        });
        mDisposable.add(subscribe6);

        //isEmpty()  判断发送的数据是否为空
        Disposable subscribe7 = Observable.just(1, 2, 3, 4).isEmpty().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.i(TAG, "accept: " + aBoolean);
            }
        });
        mDisposable.add(subscribe7);

        //amb（） 当需要发送多个observable时， 只发送先发送数据的observable的数据， 而其余observable则被丢弃
        // 设置2个需要发送的Observable & 放入到集合中
        List<ObservableSource<Integer>> list = new ArrayList<>();
        // 第1个Observable延迟1秒发射数据
        list.add(Observable.just(1, 2, 3).delay(1, TimeUnit.SECONDS));
        // 第2个Observable正常发送数据
        list.add(Observable.just(4, 5, 6));

        // 一共需要发送2个Observable的数据
        // 但由于使用了amba（）,所以仅发送先发送数据的Observable
        // 即第二个（因为第1个延时了）
        Disposable subscribe8 = Observable.amb(list).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "接收到了事件 " + integer);
            }
        });

        mDisposable.add(subscribe8);

        // defaultIfEmpty（）作用
        //作用 在不发送任何有效事件（ Next事件）、仅发送了 Complete 事件的前提下，发送一个默认值
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // 不发送任何有效事件
                //  e.onNext(1);
                //  e.onNext(2);

                // 仅发送Complete事件
                e.onComplete();
            }
        }).defaultIfEmpty(10) // 若仅发送了Complete事件，默认发送 值 = 10
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件"+ value  );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });

    }
}
