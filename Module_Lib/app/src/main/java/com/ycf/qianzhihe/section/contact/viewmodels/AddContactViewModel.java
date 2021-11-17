package com.ycf.qianzhihe.section.contact.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ycf.qianzhihe.common.livedatas.SingleSourceLiveData;
import com.ycf.qianzhihe.common.net.Resource;
import com.ycf.qianzhihe.common.repositories.EMContactManagerRepository;

public class AddContactViewModel extends AndroidViewModel {
    private EMContactManagerRepository mRepository;
    private SingleSourceLiveData<Resource<Boolean>> addContactObservable;

    public AddContactViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        addContactObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getAddContact() {
        return addContactObservable;
    }

    public void addContact(String username, String reason) {
        addContactObservable.setSource(mRepository.addContact(username, reason));
    }

}
