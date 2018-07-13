package com.test.jingmengsong.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 过滤操作符
 */
public class FilterActivity extends AppCompatActivity {

    private String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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

        //skip() skipLast() 跳过某个事件
        //1、根据顺序跳过
        Disposable subscribe1 = Observable.just(1, 2, 3, 45).skip(1).skipLast(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                Log.d(TAG, "accept: skip .......根据顺序进行调过" + integer);
            }
        });
        //2、根据时间跳过
        Disposable subscribe2 = Observable.intervalRange(5, 5, 0, 1, TimeUnit.SECONDS).skip(2).skipLast(1).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

                Log.d(TAG, "accept: skip,,,,,根据时间进行跳过" + aLong);

            }
        });

        //distinct（） /  distinctUntilChange() 过滤重复事件
        Observable.just(1,2,3,2,3,1).distinct(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) throws Exception {
                return null;
            }
        });

    }
}
