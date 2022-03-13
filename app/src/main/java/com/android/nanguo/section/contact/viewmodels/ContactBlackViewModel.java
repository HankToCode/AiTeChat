package com.android.nanguo.section.contact.viewmodels;

import android.app.Application;

import com.android.nanguo.common.livedatas.SingleSourceLiveData;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMContactManagerRepository;
import com.android.nanguo.app.domain.EaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ContactBlackViewModel extends AndroidViewModel {
    private final EMContactManagerRepository repository;
    private final SingleSourceLiveData<Resource<List<EaseUser>>> blackObservable;
    private final SingleSourceLiveData<Resource<Boolean>> resultObservable;

    public ContactBlackViewModel(@NonNull Application application) {
        super(application);
        repository = new EMContactManagerRepository();
        blackObservable = new SingleSourceLiveData<>();
        resultObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EaseUser>>> blackObservable() {
        return blackObservable;
    }

    public void getBlackList() {
        blackObservable.setSource(repository.getBlackContactList());
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return resultObservable;
    }

    public void removeUserFromBlackList(String username) {
        resultObservable.setSource(repository.removeUserFromBlackList(username));
    }

}
