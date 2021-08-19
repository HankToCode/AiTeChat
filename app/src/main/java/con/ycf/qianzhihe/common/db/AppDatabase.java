package con.ycf.qianzhihe.common.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import con.ycf.qianzhihe.common.db.converter.DateConverter;
import con.ycf.qianzhihe.common.db.dao.AppKeyDao;
import con.ycf.qianzhihe.common.db.dao.EmUserDao;
import con.ycf.qianzhihe.common.db.dao.InviteMessageDao;
import con.ycf.qianzhihe.common.db.dao.MsgTypeManageDao;
import con.ycf.qianzhihe.common.db.entity.AppKeyEntity;
import con.ycf.qianzhihe.common.db.entity.EmUserEntity;
import con.ycf.qianzhihe.common.db.entity.InviteMessage;
import con.ycf.qianzhihe.common.db.entity.MsgTypeManageEntity;

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
