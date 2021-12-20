package com.android.nanguo.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.android.nanguo.common.db.converter.DateConverter;
import com.android.nanguo.common.db.dao.AppKeyDao;
import com.android.nanguo.common.db.dao.EmUserDao;
import com.android.nanguo.common.db.dao.InviteMessageDao;
import com.android.nanguo.common.db.dao.MsgTypeManageDao;
import com.android.nanguo.common.db.entity.AppKeyEntity;
import com.android.nanguo.common.db.entity.EmUserEntity;
import com.android.nanguo.common.db.entity.InviteMessage;
import com.android.nanguo.common.db.entity.MsgTypeManageEntity;

@Database(entities = {EmUserEntity.class,
        InviteMessage.class,
        MsgTypeManageEntity.class,
        AppKeyEntity.class},
        version = 16)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EmUserDao userDao();

    public abstract InviteMessageDao inviteMessageDao();

    public abstract MsgTypeManageDao msgTypeManageDao();

    public abstract AppKeyDao appKeyDao();
}
