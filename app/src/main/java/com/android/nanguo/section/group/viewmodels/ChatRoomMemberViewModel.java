package com.android.nanguo.section.group.viewmodels;

import android.app.Application;

import com.hyphenate.chat.EMChatRoom;
import com.android.nanguo.common.livedatas.LiveDataBus;
import com.android.nanguo.common.livedatas.SingleSourceLiveData;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.repositories.EMChatRoomManagerRepository;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class ChatRoomMemberViewModel extends AndroidViewModel {
    private final EMChatRoomManagerRepository repository;
    private final SingleSourceLiveData<Resource<EMChatRoom>> chatRoomObservable;
    private final SingleSourceLiveData<Resource<Boolean>> destroyGroupObservable;
    private final SingleSourceLiveData<Resource<List<String>>> blackObservable;
    private final SingleSourceLiveData<Resource<Map<String, Long>>> muteMapObservable;
    private final SingleSourceLiveData<Resource<List<String>>> membersObservable;
    private final LiveDataBus messageChangeObservable;

    public ChatRoomMemberViewModel(@NonNull Application application) {
        super(application);
        repository = new EMChatRoomManagerRepository();
        chatRoomObservable = new SingleSourceLiveData<>();
        destroyGroupObservable = new SingleSourceLiveData<>();
        blackObservable = new SingleSourceLiveData<>();
        muteMapObservable = new SingleSourceLiveData<>();
        membersObservable = new SingleSourceLiveData<>();
        messageChangeObservable = LiveDataBus.get();
    }

    public LiveData<Resource<EMChatRoom>> chatRoomObservable() {
        return chatRoomObservable;
    }

    public LiveData<Resource<Boolean>> destroyGroupObservable() {
        return destroyGroupObservable;
    }

    public LiveData<Resource<List<String>>> blackObservable() {
        return blackObservable;
    }

    public LiveData<Resource<Map<String, Long>>> muteMapObservable() {
        return muteMapObservable;
    }

    public LiveData<Resource<List<String>>> membersObservable() {
        return membersObservable;
    }

    public LiveDataBus getMessageChangeObservable() {
        return messageChangeObservable;
    }

    public void getGroupMuteMap(String roomId) {
        muteMapObservable.setSource(repository.getChatRoomMuteMap(roomId));
    }

    public void getGroupBlackList(String roomId) {
        blackObservable.setSource(repository.getChatRoomBlackList(roomId));
    }

    public void getMembersList(String roomId) {
        membersObservable.setSource(repository.loadMembers(roomId));
    }

    public void getChatRoom(String roomId) {
        chatRoomObservable.setSource(repository.getChatRoomById(roomId));
    }

    public void addGroupAdmin(String roomId, String username) {
        chatRoomObservable.setSource(repository.addChatRoomAdmin(roomId, username));
    }

    public void removeGroupAdmin(String roomId, String username) {
        chatRoomObservable.setSource(repository.removeChatRoomAdmin(roomId, username));
    }

    public void changeOwner(String roomId, String username) {
        chatRoomObservable.setSource(repository.changeOwner(roomId, username));
    }

    public void removeUserFromGroup(String roomId, List<String> usernames) {
        chatRoomObservable.setSource(repository.removeUserFromChatRoom(roomId, usernames));
    }

    public void blockUser(String roomId, List<String> username) {
        LiveData<Resource<EMChatRoom>> block = repository.blockUser(roomId, username);
        chatRoomObservable.setSource(Transformations.switchMap(block, response -> repository.removeUserFromChatRoom(roomId, username)));
    }

    public void unblockUser(String roomId, List<String> username) {
        chatRoomObservable.setSource(repository.unblockUser(roomId, username));
    }

    public void muteGroupMembers(String roomId, List<String> usernames, long duration) {
        chatRoomObservable.setSource(repository.muteChatRoomMembers(roomId, usernames, duration));
    }

    public void unMuteGroupMembers(String roomId, List<String> usernames) {
        chatRoomObservable.setSource(repository.unMuteChatRoomMembers(roomId, usernames));
    }

    public void destroyGroup(String roomId) {
        destroyGroupObservable.setSource(repository.destroyChatRoom(roomId));
    }
}
