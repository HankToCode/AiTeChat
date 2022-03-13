package com.android.nanguo.section.me.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.nanguo.common.livedatas.SingleSourceLiveData;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMClientRepository;

public class AppKeyManagerViewModel extends AndroidViewModel {
    private final EMClientRepository repository;
    private final SingleSourceLiveData<Resource<Boolean>> logoutObservable;

    public AppKeyManagerViewModel(@NonNull Application application) {
        super(application);
        repository = new EMClientRepository();
        logoutObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getLogoutObservable() {
        return logoutObservable;
    }

    public void logout(boolean unbindDeviceToken) {
        logoutObservable.setSource(repository.logout(unbindDeviceToken));
    }
}

