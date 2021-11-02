package com.ycf.qianzhihe.common.rx;


import androidx.annotation.NonNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by LJJ on 2017/11/16.
 * use to:剩下onNext()没有实现的订阅者
 */

public abstract class NextObserver<T> implements Observer<T> {


    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }


    @Override
    public void onError(@NonNull Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}