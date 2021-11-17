package com.ycf.qianzhihe.section.account.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.app.utils.my.MyModel;
import com.ycf.qianzhihe.common.net.Resource;
import com.ycf.qianzhihe.common.repositories.EMClientRepository;

public class LoginFragmentViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;
    private MediatorLiveData<Resource<EaseUser>> loginObservable;

    private MyModel myModel;

    public LoginFragmentViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
        loginObservable = new MediatorLiveData<>();
        myModel = new MyModel(application);
    }

    /**
     * 登录环信
     */
    public void HXlogin() {

        loginObservable.addSource(mRepository.loginToServer(), response -> {
            loginObservable.setValue(response);
        });
    }

    public LiveData<Resource<EaseUser>> getLoginObservable() {
        return loginObservable;
    }

    public MyModel getMyModel(){
        return myModel;
    }
}
