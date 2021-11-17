package com.ycf.qianzhihe.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ycf.qianzhihe.common.db.converter.DateConverter;
import com.ycf.qianzhihe.common.db.dao.AppKeyDao;
import com.ycf.qianzhihe.common.db.dao.EmUserDao;
import com.ycf.qianzhihe.common.db.dao.InviteMessageDao;
import com.ycf.qianzhihe.common.db.dao.MsgTypeManageDao;
import com.ycf.qianzhihe.common.db.entity.AppKeyEntity;
import com.ycf.qianzhihe.common.db.entity.EmUserEntity;
import com.ycf.qianzhihe.common.db.entity.InviteMessage;
import com.ycf.qianzhihe.common.db.entity.MsgTypeManageEntity;

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
