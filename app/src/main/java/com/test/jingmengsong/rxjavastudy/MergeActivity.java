package com.test.jingmengsong.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * rxjava2 组合/合并操作符
 */
public class MergeActivity extends AppCompatActivity {
    private String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);

        //组合多个被观察者 concat()组合多个被观察者（<=4）一起发送数据串行执行  ；concatArray() 组合多个被观察者一起发送数据（可>4）串行执行
        Observable.concat(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                        Log.d(TAG, "onNext: 接收到的值为 concat：" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        Observable.concatArray(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10, 11, 12),
                Observable.just(13, 14, 15))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: 接收到的值为concatArray:" + integer);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //组合多个被观察者一起发送数据，合并后按时间线并行执行 merge() / mergeArray(), 区别于concat（）/concatArray（） 合并后是按发送顺序串行执行
        Observable.merge(Observable.intervalRange(1,6,0,1, TimeUnit.SECONDS),
                Observable.intervalRange(10,3,1,1, TimeUnit.SECONDS))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                        Log.d(TAG, "onNext: merge....接收到的事件"+aLong);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //concatDelayError（）  /   mergeDelayError()   使用该操作符可以将error 事件推迟到其他被观察者发送事件结束后才触发


    }
}
