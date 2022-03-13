package com.android.nanguo.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.android.nanguo.common.livedatas.LiveDataBus;
import com.android.nanguo.common.livedatas.SingleSourceLiveData;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMContactManagerRepository;
import com.android.nanguo.app.domain.EaseUser;

import java.util.List;

public class ContactsViewModel extends AndroidViewModel {
    private final EMContactManagerRepository mRepository;
    private final SingleSourceLiveData<Resource<List<EaseUser>>> contactObservable;
    private final MediatorLiveData<Resource<List<EaseUser>>> blackObservable;
    private final SingleSourceLiveData<Resource<Boolean>> blackResultObservable;
    private final SingleSourceLiveData<Resource<Boolean>> deleteObservable;

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        contactObservable = new SingleSourceLiveData<>();
        blackObservable = new MediatorLiveData<>();
        blackResultObservable = new SingleSourceLiveData<>();
        deleteObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EaseUser>>> blackObservable() {
        return blackObservable;
    }

    public LiveDataBus messageChangeObservable() {
        return LiveDataBus.get();
    }

    public void getBlackList() {
        blackObservable.addSource(mRepository.getBlackContactList(), result -> blackObservable.postValue(result));
    }

    public void loadContactList() {
        contactObservable.setSource(mRepository.getContactList());
    }

    public LiveData<Resource<List<EaseUser>>> getContactObservable() {
        return contactObservable;
    }

    public LiveData<Resource<Boolean>> resultObservable() {
        return blackResultObservable;
    }

    public LiveData<Resource<Boolean>> deleteObservable() {
        return deleteObservable;
    }

    public void deleteContact(String username) {
        deleteObservable.setSource(mRepository.deleteContact(username));
    }

    public void addUserToBlackList(String username, boolean both) {
        blackResultObservable.setSource(mRepository.addUserToBlackList(username, both));
    }

}
