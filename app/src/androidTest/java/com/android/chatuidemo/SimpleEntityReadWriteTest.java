package com.android.chatuidemo;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertThat;

import com.android.nanguo.common.db.AppDatabase;
import com.android.nanguo.common.db.dao.EmUserDao;
import com.android.nanguo.common.db.entity.EmUserEntity;

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private EmUserDao userDao;
    private AppDatabase db;

    @Before
    public void createdDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userDao = db.userDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        EmUserEntity user = new EmUserEntity();
        user.setUsername("george");
        userDao.insert(user);
       /* List<EmUserEntity> byName = userDao.loadUserById("george");
        assertThat(byName.get(0), equalTo(user));*/
    }
}
