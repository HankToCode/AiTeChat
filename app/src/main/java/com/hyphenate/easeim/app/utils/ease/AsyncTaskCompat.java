/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.easeim.app.utils.ease;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

/**
 * Helper for accessing features in {@link AsyncTask}.
 *
 * @deprecated Use {@link AsyncTask} directly.
 */
@Deprecated
public final class AsyncTaskCompat {

    /**
     * Executes the task with the specified parameters, allowing multiple tasks to run in parallel
     * on a pool of threads managed by {@link AsyncTask}.
     *
     * @param task   The {@link AsyncTask} to execute.
     * @param params The parameters of the task.
     * @return the instance of AsyncTask.
     * @deprecated Use {@link AsyncTask#executeOnExecutor(Executor, Object[])} directly.
     */
    @Deprecated
    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(
            AsyncTask<Params, Progress, Result> task,
            Params... params) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

        return task;
    }

    private AsyncTaskCompat() {
    }

}
