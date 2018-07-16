package com.test.jingmengsong.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 过滤操作符
 * <p>
 * 实际开发需求案例：
 * 1、功能防抖 即用户同规定时间内多次触发该功能， 仅会响应第一次触发操作
 * 原理： 使用throttleFirst（）根据指定时间 过滤事件 即 在指定的时间内，只执行第一次事件；
 * 2、 联想搜索优化  即当用户每输入一个字符，就要对应显示字符相关的搜索结果
 * 原理： 使用的debounce() 根据指定时间 过滤事件
 */
public class FilterActivity extends AppCompatActivity {

    private String TAG = "TAG";
    private CompositeDisposable mDisposable;
    private AppCompatEditText mEdit;
    private AppCompatTextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mDisposable = new CompositeDisposable();
        // 注册控件
        AppCompatButton button;
        button = findViewById(R.id.btn);
        mEdit = findViewById(R.id.edit);
        mTv = findViewById(R.id.tv);
        //------------------------ 根据指定条件进行过滤操作符 -------------------------
        //filter（）  过滤特定条件的事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer > 3;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: ");

            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "onNext: " + integer);

            }

            @Override
            public void onError(Throwable e) {

                Log.i(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");

            }
        });


        //ofType（） 过滤特定数据类型的数据
        Disposable subscribe = Observable.just(1, 3, "一不留神", 4, "完美的倩倩深深").ofType(String.class).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

                Log.d(TAG, "accept: oftype" + s);

            }
        });
        mDisposable.add(subscribe);

        //skip() skipLast() 跳过某个事件
        //1、根据顺序跳过
        Disposable subscribe1 = Observable.just(1, 2, 3, 45).skip(1).skipLast(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.d(TAG, "accept: skip .......根据顺序进行调过" + integer);
            }
        });
        mDisposable.add(subscribe1);
        //2、根据时间跳过
        Disposable subscribe2 = Observable.intervalRange(5, 5, 0, 1, TimeUnit.SECONDS).skip(2).skipLast(1).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

                Log.d(TAG, "accept: skip,,,,,根据时间进行跳过" + aLong);

            }
        });
        mDisposable.add(subscribe2);

        //distinct（） /  distinctUntilChange() 过滤重复事件
        Disposable subscribe3 = Observable.just(1, 2, 3, 2, 3, 1)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                        Log.d(TAG, "accept: 不重复的整形数据是。。。。。" + integer);

                    }
                });
        mDisposable.add(subscribe3);

        Disposable subscribe4 = Observable.just(1, 1, 2, 3, 4, 3).distinctUntilChanged().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.d(TAG, "accept: 不连续重复的整形数据是。。。。。" + integer);
            }
        });
        mDisposable.add(subscribe4);

        // take() takeLast()  通过设置指定的事件数量 ， 仅发送特定数量的事件
        //take() 作用：指定观察者最多能接受到的事件数量
        Disposable subscribe5 = Observable.just(1, 2, 3, 4, 5, 6).take(3).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.d(TAG, "accept: 观察者最多接收的事件个数。。。。。" + integer);

            }
        });
        mDisposable.add(subscribe5);

        //takeLast() 指定观察者只能接收到被观察者最后几个事件
        Disposable subscribe6 = Observable.just("北", "京", "欢", "迎", "你", "呀").takeLast(3).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "accept: 观察者最多只能接收到被观察者的最后几个事件。。。。。" + s);
            }
        });
        mDisposable.add(subscribe6);

        // ------------------------- 根据指定 时间 过滤事件 -----------------------
        // throttleFirst（）在某段时间内只发送该段时间内第一次事件  throtleLast() simple（）在某段时间内只发送该段时间内最后一次事件
        //注：如一段时间内连续点击按钮，但只执行第一次的点击事件

        Disposable subscribe7 = Observable.just(1, 2, 3, 4, 5).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: throttleFirst...值是" + integer);
            }
        });
        mDisposable.add(subscribe7);
        //<<- 在某段时间内，只发送该段时间内最后1次事件 ->>
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // 隔段事件发送时间
                e.onNext(1);
                Thread.sleep(500);

                e.onNext(2);
                Thread.sleep(400);

                e.onNext(3);
                Thread.sleep(300);

                e.onNext(4);
                Thread.sleep(300);

                e.onNext(5);
                Thread.sleep(300);

                e.onNext(6);
                Thread.sleep(400);

                e.onNext(7);
                Thread.sleep(300);
                e.onNext(8);

                Thread.sleep(300);
                e.onNext(9);

                Thread.sleep(300);
                e.onComplete();
            }
        }).throttleLast(1, TimeUnit.SECONDS)//每1秒中采用数据
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        // throttleWithTimeOut() debounce()  作用
        // 作用：发送数据事件时，若2次发送事件的间隔＜指定时间，就会丢弃前一次的数据，直到指定时间内都没有新数据发射时才会发送后一次的数据
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // 隔段事件发送时间
                e.onNext(1);
                Thread.sleep(500);
                e.onNext(2); // 1和2之间的间隔小于指定时间1s，所以前1次数据（1）会被抛弃，2会被保留
                Thread.sleep(1500);  // 因为2和3之间的间隔大于指定时间1s，所以之前被保留的2事件将发出
                e.onNext(3);
                Thread.sleep(1500);  // 因为3和4之间的间隔大于指定时间1s，所以3事件将发出
                e.onNext(4);
                Thread.sleep(500); // 因为4和5之间的间隔小于指定时间1s，所以前1次数据（4）会被抛弃，5会被保留
                e.onNext(5);
                Thread.sleep(500); // 因为5和6之间的间隔小于指定时间1s，所以前1次数据（5）会被抛弃，6会被保留
                e.onNext(6);
                Thread.sleep(1500); // 因为6和Complete实践之间的间隔大于指定时间1s，所以之前被保留的6事件将发出

                e.onComplete();
            }
        }).throttleWithTimeout(1, TimeUnit.SECONDS)//每1秒中采用数据
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        // -----------------------  根据指定事件位置过滤事件 ------------------
        //通过设置指定的位置，过滤在该位置的事件
        //firstElement（） lastElement（） 仅选取第一个元素 / 最后一个元素
        Disposable subscribe8 = Observable.just(1, 2, 3, 45, 6).firstElement().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "accept: firstElement....." + integer);
            }
        });

        mDisposable.add(subscribe8);

        Disposable subscribe9 = Observable.just(1, 2, 3, 45, 6).lastElement().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "accept: lastElement....." + integer);
            }
        });

        mDisposable.add(subscribe9);

        //elementAt（）
        //作用
        //指定接收某个元素（通过 索引值 确定）

        Disposable subscribe10 = Observable.just(23, 46, 88, 999).elementAt(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                // 使用1：获取位置索引 = 2的 元素
                // 位置索引从0开始
                Log.i(TAG, "accept:elementAt...... " + integer);

            }
        });
        mDisposable.add(subscribe10);

        Disposable subscribe11 = Observable.just(23, 46, 88, 999).elementAt(6, 10).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                // 使用2：获取的位置索引 ＞ 发送事件序列长度时，设置默认参数
                Log.i(TAG, "accept:elementAt...... " + integer);

            }
        });
        mDisposable.add(subscribe11);

        //elementAtOrError（）
        //作用
        //在elementAt（）的基础上，当出现越界情况（即获取的位置索引 ＞ 发送事件序列长度）时，即抛出异常
//        Observable.just(1, 2, 3, 4, 5)
////                .elementAtOrError(6)
////                .subscribe(new Consumer<Integer>() {
////                    @Override
////                    public void accept( Integer integer) throws Exception {
////                        Log.d(TAG,"获取到的事件元素是： "+ integer);
////                    }
////                });





        /*  功能防抖
         * 1. 此处采用了RxBinding：RxView.clicks(button) = 对控件点击进行监听，需要引入依赖：compile 'com.jakewharton.rxbinding2:rxbinding:2.0.0'
         * 2. 传入Button控件，点击时，都会发送数据事件（但由于使用了throttleFirst（）操作符，所以只会发送该段时间内的第1次点击事件）
         **/
        RxView.clicks(button)
                .throttleFirst(2, TimeUnit.SECONDS)  // 才发送 2s内第1次点击按钮的事件
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        Log.d(TAG, "发送了网络请求");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应" + e.toString());
                        // 获取异常错误信息
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });

        /*
         * *联想搜索优化
         */
        Disposable subscribe12 = RxTextView.textChanges(mEdit).debounce(1, TimeUnit.SECONDS).skip(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {

                        mTv.setText(charSequence.toString().trim());
                    }
                });

        mDisposable.add(subscribe12);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }
}
