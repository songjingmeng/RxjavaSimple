package com.test.jingmengsong.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * rxJava2 条件/ 布尔 操作符
 */
public class ConditionActivity extends AppCompatActivity {

    private String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

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


    }
}
