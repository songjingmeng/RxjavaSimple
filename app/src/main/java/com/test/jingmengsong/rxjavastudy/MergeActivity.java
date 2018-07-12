package com.test.jingmengsong.rxjavastudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * rxjava2 组合/合并操作符
 * <p>
 * 实战场景
 * 1、获取缓存数据  concat() firstElement()
 * 2、合并数据源  zip() merge
 * 3、联合判断  combineLatest
 */
public class MergeActivity extends AppCompatActivity {
    String content = "";
    // 该2变量用于模拟内存缓存 & 磁盘缓存中的数据
    String memoryCache = null;
    String diskCache = "从磁盘缓存中获取数据";
    private String TAG = "TAG";
    private AppCompatTextView mMergeTv;
    private CompositeDisposable mDisposable;
    private EditText mNameEt;
    private EditText mAgeEt;
    private EditText mJobEt;
    private Button mListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        mMergeTv = findViewById(R.id.tv_merge);
        mNameEt = findViewById(R.id.name);
        mAgeEt = findViewById(R.id.age);
        mJobEt = findViewById(R.id.job);
        mListBtn = findViewById(R.id.list);

        mDisposable = new CompositeDisposable();
        //-------------------------- 合并多个被观察者 -----------------------
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
        Observable.merge(Observable.intervalRange(1, 6, 0, 1, TimeUnit.SECONDS),
                Observable.intervalRange(10, 3, 1, 1, TimeUnit.SECONDS))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                        Log.d(TAG, "onNext: merge....接收到的事件" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //concatDelayError（）  /   mergeDelayError()   使用该操作符可以将error 事件推迟到其他被观察者发送事件结束后才触发


        //--------------- 合并多个事件 -----------------

        // zip() 合并多个被观察者发送的多个事件， 生成一个新的事件序列，并最终发送
        //特别注意：事件组合方式 = 严格按照原先事件序列 进行对位合并
        //        最终合并的事件数量 = 多个被观察者（Observable）中数量最少的数量

        Observable<String> observable1 = Observable.just("被观察者1发送了事件1",
                "被观察者1发送了事件2",
                "被观察者1发送了事件3",
                "被观察者1发送了事件4")
                .subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.just("被观察者2发送了事件a",
                "被观察者2发送了事件b",
                "被观察者2发送了事件c",
                "被观察者2发送了事件d",
                "被观察者2发送了事件e")
                .subscribeOn(Schedulers.newThread());

        Observable.zip(observable1, observable2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String s1, String s2) throws Exception {
                return s1 + s2;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {


                content = content + s + "\n";
                mMergeTv.setText(content);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //combineLatest()
        //作用：当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
        //与 zip() 区别：zip（）是按个数进行合并，及一对一合并； combinelatest（）是按时间合并，即在同一个时间点上合并
        Observable.combineLatest(Observable.just("第一个被观察者发送的事件a", "第一个被观察者发送的事件b", "第一个被观察者发送的事件c"), Observable.intervalRange(1, 3, 0, 1, TimeUnit.SECONDS), new BiFunction<String, Long, String>() {
            @Override
            public String apply(String s, Long aLong) throws Exception {
                // 合并的逻辑 = 相加
                // 即第1个Observable发送的最后1个数据 与 第2个Observable发送的每1个数据进行相加
                return s + aLong;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

                Log.i(TAG, "onNext: combineLatest 合并后的结果是" + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        //combineLatestDelayError() 即错误处理

        //reduce（） 作用 把被观察者需要发送的事件聚合成1个事件并且发送  重点是聚合
        Disposable disposable = Observable.just(1, 2, 3).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer i1, Integer i2) throws Exception {
                Log.i(TAG, "apply: 此次计算的数据是" + i1 + "乘" + i2);
                // 本次聚合的逻辑是：全部数据相乘起来
                // 原理：第1次取前2个数据相乘，之后每次获取到的数据 = 返回的数据x原始下1个数据每

                return i1 * i2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "accept: 最后聚合的数据是" + integer);
            }
        });
        mDisposable.add(disposable);

        //collect（） 作用 将被观察者发送的数据事件 收集到一个数据结构里
        Disposable disposable1 = Observable.just("北", "京", "欢", "迎", "你")
                // 1. 创建数据结构（容器），用于收集被观察者发送的数据
                .collect(new Callable<ArrayList<String>>() {
                    @Override
                    public ArrayList<String> call() throws Exception {
                        return new ArrayList<String>();
                    }
                    //2、对发送的数据进行收集
                }, new BiConsumer<ArrayList<String>, String>() {
                    @Override
                    public void accept(ArrayList<String> list, String s) throws Exception {

                        list.add(s);

                    }
                }).subscribe(new Consumer<ArrayList<String>>() {
                    @Override
                    public void accept(ArrayList<String> strings) throws Exception {

                        Log.i(TAG, "accept: collect 收集的数据是" + strings);
                    }
                });
        mDisposable.add(disposable1);

        //--------------  发送事件前追加发送事件  --------------

        // startWith() / startWithArray() 在一个被观察者发送事件前， 追加发送一些数据/ 一个新的被观察者
        // 注：追加数据顺序 = 后调用先追加
        Disposable subscribe = Observable.just(4, 5, 6)
                .startWith(1)
                .startWithArray(2, 3).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "accept: startWith()追加后数据" + integer);
                    }
                });

        // --------------- 统计发送事件数量 ------------
        Disposable disposable2 = Observable.just(1, 2, 3, 4)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        Log.i(TAG, "accept: 发送的事件数量 = " + aLong);
                    }
                });

        mDisposable.add(disposable2);


        //---------------------------实战 从磁盘/内存缓存中获取缓存数据 ------------------------------
        /*
         * 设置第1个Observable：检查内存缓存是否有该数据的缓存
         **/
        Observable<String> memory = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                // 先判断内存缓存有无数据
                if (memoryCache != null) {
                    // 若有该数据，则发送
                    emitter.onNext(memoryCache);
                } else {
                    // 若无该数据，则直接发送结束事件
                    emitter.onComplete();
                }

            }
        });

        /*
         * 设置第2个Observable：检查磁盘缓存是否有该数据的缓存
         **/
        Observable<String> disk = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                // 先判断磁盘缓存有无数据
                if (diskCache != null) {
                    // 若有该数据，则发送
                    emitter.onNext(diskCache);
                } else {
                    // 若无该数据，则直接发送结束事件
                    emitter.onComplete();
                }

            }
        });

        /*
         * 设置第3个Observable：通过网络获取数据
         **/
        Observable<String> network = Observable.just("从网络中获取数据");
        // 此处仅作网络请求的模拟


        /*
         * 通过concat（） 和 firstElement（）操作符实现缓存功能
         **/

        // 1. 通过concat（）合并memory、disk、network 3个被观察者的事件（即检查内存缓存、磁盘缓存 & 发送网络请求）
        //    并将它们按顺序串联成队列
        Disposable memberDisposable = Observable.concat(memory, disk, network)
                // 2. 通过firstElement()，从串联队列中取出并发送第1个有效事件（Next事件），即依次判断检查memory、disk、network
                .firstElement()
                // 即本例的逻辑为：
                // a. firstElement()取出第1个事件 = memory，即先判断内存缓存中有无数据缓存；由于memoryCache = null，即内存缓存中无数据，所以发送结束事件（视为无效事件）
                // b. firstElement()继续取出第2个事件 = disk，即判断磁盘缓存中有无数据缓存：由于diskCache ≠ null，即磁盘缓存中有数据，所以发送Next事件（有效事件）
                // c. 即firstElement()已发出第1个有效事件（disk事件），所以停止判断。

                // 3. 观察者订阅
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "最终获取的数据来源 =  " + s);
                    }
                });

        mDisposable.add(memberDisposable);

        // ---------------------- 合并数据源并同时展示数据 ----------------
        //存在两种方式  merge（）和 zip（）；merge（）实现较为简单的从（网络 + 本地）获取数据并同时展示；  zip() 实现较为复杂的合并2个网络请求向两个服务器获取数据并统一展示


        //------------------------ 联合判断 ------------------------
        //应用场景 填写表单时，需要表单里所有信息（姓名、年龄、职业等）都被填写后，才允许点击 “提交” 按钮

        Observable<CharSequence> nameObservable = RxTextView.textChanges(mNameEt).skip(1);
        Observable<CharSequence> ageObservable = RxTextView.textChanges(mAgeEt).skip(1);
        Observable<CharSequence> jobObservable = RxTextView.textChanges(mJobEt).skip(1);

        Disposable disposable3 = Observable.combineLatest(nameObservable, ageObservable, jobObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence name, CharSequence age, CharSequence job) throws Exception {

                boolean isEmptyName = !TextUtils.isEmpty(name);
                boolean isEmptyAge = !TextUtils.isEmpty(age);
                boolean isEmptyJob = !TextUtils.isEmpty(job);
                return isEmptyName && isEmptyAge && isEmptyJob;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {


                mListBtn.setEnabled(aBoolean);


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDisposable != null) {
            mDisposable.clear();
        }

    }
}
