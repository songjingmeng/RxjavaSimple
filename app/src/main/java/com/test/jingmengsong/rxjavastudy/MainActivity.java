package com.test.jingmengsong.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 *   rxjava2 创建被观察者对象  对应的操作符 讲解
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = "TAG";
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);

        //------------------------  创建操作符 -------------------
        //create()
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });

        //快速创建被观察者 just(), 快速创建1个被观察这对象， 并直接发送传入的事件
        Observable.just("1", "2", "3")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: just.......");
                    }

                    @Override
                    public void onNext(String string) {
                        Log.d(TAG, "onNext: just ....." + string);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: just.....");

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: just......");

                    }
                });

        //快速创建被观察者 fromArray() 并直接发送传入的数组数据
        Integer[] item1 = {1, 2, 3, 4, 5};
        Integer[] item2 = {6, 7, 8};
        Observable.fromArray(item1).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

                Log.d(TAG, "onSubscribe: fromArray......");

            }

            @Override
            public void onNext(Integer integer) {

                Log.d(TAG, "onNext: fromArray......" + integer);
            }

            @Override
            public void onError(Throwable e) {

                Log.d(TAG, "onError: fromArray......");
            }

            @Override
            public void onComplete() {

                Log.d(TAG, "onComplete: fromArray...... 数组遍历结束");
            }
        });

        Observable.fromArray(item1, item2).subscribe(new Observer<Integer[]>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer[] integers) {

                Log.d(TAG, "onNext: fromArray....." + integers.length);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //快速创建 被观察者对象（Observable） & 发送10个以上事件（集合形式）
        //集合元素遍历
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Observable.fromIterable(list).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

                Log.d(TAG, "onSubscribe: 集合遍历");

            }

            @Override
            public void onNext(Integer integer) {

                Log.d(TAG, "onNext: 集合中的元素 =" + integer);
            }

            @Override
            public void onError(Throwable e) {

                Log.d(TAG, "onError: ");
            }

            @Override
            public void onComplete() {

                Log.d(TAG, "onComplete: 集合遍历结束");
            }
        });

        //// 下列方法一般用于测试使用
        //
        //<-- empty()  -->
        //// 该方法创建的被观察者对象发送事件的特点：仅发送Complete事件，直接通知完成
        //Observable observable1=Observable.empty();
        //// 即观察者接收后会直接调用onCompleted（）
        //
        //<-- error()  -->
        //// 该方法创建的被观察者对象发送事件的特点：仅发送Error事件，直接通知异常
        //// 可自定义异常
        //Observable observable2=Observable.error(new RuntimeException())
        //// 即观察者接收后会直接调用onError（）
        //
        //<-- never()  -->
        //// 该方法创建的被观察者对象发送事件的特点：不发送任何事件
        //Observable observable3=Observable.never();
        //// 即观察者接收后什么都不调用


        // 延迟创建 defer（）      观察者开始订阅时才会开始创建被观察者
        //定时操作：在经过了x秒后，需要自动执行y操作
        //周期性操作：每隔x秒后，需要自动执行y操作
        final Integer i = 10;
        Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(i);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                tv.setText(integer + "");
                Log.d(TAG, "onNext: defer.....1......." + integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


//        defer.subscribe(new Observer<Integer>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//
//                Log.d(TAG, "onNext: defer....." + integer);
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

        //延迟创建 timer（）
        //快速创建1个被观察者对象（Observable）
        //发送事件的特点：延迟指定时间后，发送1个数值0（Long类型）, 多用于检测
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {

                tv.setText(aLong + "");
                Log.d(TAG, "onNext: timer ...." + aLong);

                // 注：timer操作符默认运行在一个新线程上
                // 也可自定义线程调度器（第3个参数）：timer(long,TimeUnit,Scheduler)
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //延迟创建 interval（）
        //快速创建1个被观察者对象（Observable）
        //发送事件的特点：每隔指定时间 就发送 事件
//        Observable.interval(3, 1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Long aLong) {
//
//                // 该例子发送的事件序列特点：延迟3s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）
//                Log.d(TAG, "onNext: interval...." + aLong);
//
//                tv.setText(aLong + "");
//
//                // 注：interval默认在computation调度器上执行
//                // 也可自定义指定线程调度器（第3个参数）：interval(long,TimeUnit,Scheduler)
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

        //延迟创建 intervalRange()
        //快速创建1个被观察者对象（Observable）
        //发送事件的特点：每隔指定时间 就发送 事件，可指定发送的数据的数量
        Observable.intervalRange(2, 10, 0, 1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {

                Log.i(TAG, "onNext: intervalRange......" + aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

                Log.i(TAG, "onComplete: intervalRange......");

            }
        });

        //延迟创建 range（）
        //快速创建1个被观察者对象（Observable）
        //发送事件的特点：连续发送 1个事件序列，可指定范围 (无延迟发送事件)
        Disposable subscribe = Observable.range(1, 10)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "accept: range........." + integer);

                    }
                });

    }
}
