package com.android.nanguo.section.contact.viewmodels;

import android.app.Application;

import com.android.nanguo.common.livedatas.SingleSourceLiveData;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMContactManagerRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ContactDetailViewModel extends AndroidViewModel {
    private final EMContactManagerRepository repository;
    private final SingleSourceLiveData<Resource<Boolean>> deleteObservable;
    private final SingleSourceLiveData<Resource<Boolean>> blackObservable;

    public ContactDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMContactManagerRepository();
        deleteObservable = new SingleSourceLiveData<>();
        blackObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> deleteObservable() {
        return deleteObservable;
    }

    public LiveData<Resource<Boolean>> blackObservable() {
        return blackObservable;
    }

    public void deleteContact(String username) {
        deleteObservable.setSource(repository.deleteContact(username));
    }

    public void addUserToBlackList(String username, boolean both) {
        blackObservable.setSource(repository.addUserToBlackList(username, both));
    }

}
