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

public class ContactListViewModel extends AndroidViewModel {
    private final EMContactManagerRepository mRepository;
    private final SingleSourceLiveData<Resource<List<EaseUser>>> contactListObservable;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EMContactManagerRepository();
        contactListObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<List<EaseUser>>> getContactListObservable() {
        return contactListObservable;
    }

    public void getContactList() {
        contactListObservable.setSource(mRepository.getContactList());
    }
}
