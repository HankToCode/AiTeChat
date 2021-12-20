package com.android.nanguo.section.account.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMClientRepository;

public class SplashViewModel extends AndroidViewModel {
    private EMClientRepository mRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMClientRepository();
    }

    public LiveData<Resource<Boolean>> getLoginData() {
        return mRepository.loadAllInfoFromHX();
    }
}
