package com.android.nanguo.section.group.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMChatRoom;
import com.android.nanguo.common.livedatas.LiveDataBus;
import com.android.nanguo.common.livedatas.SingleSourceLiveData;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMChatRoomManagerRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ChatRoomDetailViewModel extends AndroidViewModel {
    private final EMChatRoomManagerRepository repository;
    private final SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable = new SingleSourceLiveData<>();
    private final SingleSourceLiveData<Resource<List<String>>> membersObservable;
    private final SingleSourceLiveData<Resource<String>> announcementObservable = new SingleSourceLiveData<>();
    private final LiveDataBus messageChangeObservable = LiveDataBus.get();
    private final SingleSourceLiveData<Resource<Boolean>> destroyGroupObservable = new SingleSourceLiveData<>();

    public ChatRoomDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new EMChatRoomManagerRepository();
        membersObservable = new SingleSourceLiveData<>();
    }

    public LiveDataBus getMessageChangeObservable() {
        return messageChangeObservable;
    }

    public LiveData<Resource<EMChatRoom>> chatRoomObservable() {
        return chatRoomObservable;
    }

    public LiveData<Resource<List<String>>> memberObservable() {
        return membersObservable;
    }

    public LiveData<Resource<String>> updateAnnouncementObservable() {
        return announcementObservable;
    }

    public LiveData<Resource<Boolean>> destroyGroupObservable() {
        return destroyGroupObservable;
    }

    public void getChatRoomFromServer(String roomId) {
        chatRoomObservable.setSource(repository.getChatRoomById(roomId));
    }

    public void changeChatRoomSubject(String roomId, String newSubject) {
        chatRoomObservable.setSource(repository.changeChatRoomSubject(roomId, newSubject));
    }

    public void changeChatroomDescription(String roomId, String newDescription) {
        chatRoomObservable.setSource(repository.changeChatroomDescription(roomId, newDescription));
    }

    public void getChatRoomMembers(String roomId) {
        membersObservable.setSource(repository.loadMembers(roomId));
    }

    public void updateAnnouncement(String roomId, String announcement) {
        announcementObservable.setSource(repository.updateAnnouncement(roomId, announcement));
    }

    public void fetchChatRoomAnnouncement(String roomId) {
        announcementObservable.setSource(repository.fetchChatRoomAnnouncement(roomId));
    }

    public void destroyGroup(String roomId) {
        destroyGroupObservable.setSource(repository.destroyChatRoom(roomId));
    }

}
