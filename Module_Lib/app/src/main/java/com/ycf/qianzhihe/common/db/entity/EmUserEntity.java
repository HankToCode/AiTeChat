package com.ycf.qianzhihe.common.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;


import com.ycf.qianzhihe.app.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "em_users", primaryKeys = {"username"},
        indices = {@Index(value = {"username"}, unique = true)})
public class EmUserEntity extends EaseUser {
    public EmUserEntity() {
        super();
    }

    @Ignore
    public EmUserEntity(@NonNull String username) {
        super(username);
    }

    public static List<EaseUser> parses(List<String> ids) {
        List<EaseUser> users = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return users;
        }
        EaseUser user;
        for (String id : ids) {
            user = new EaseUser(id);
            users.add(user);
        }
        return users;
    }

    @Ignore
    public static List<EmUserEntity> parseList(List<EaseUser> users) {
        List<EmUserEntity> entities = new ArrayList<>();
        if (users == null || users.isEmpty()) {
            return entities;
        }
        EmUserEntity entity;
        for (EaseUser user : users) {
            entity = parseParent(user);
            entities.add(entity);
        }
        return entities;
    }

    @Ignore
    public static EmUserEntity parseParent(EaseUser user) {
        EmUserEntity entity = new EmUserEntity();
        entity.setUsername(user.getUsername());
        entity.setNickname(user.getNickname());
        entity.setAvatar(user.getAvatar());
        entity.setInitialLetter(user.getInitialLetter());
        entity.setContact(user.getContact());
        return entity;
    }
}
