/*
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;

import io.netty.util.internal.UnstableApi;

/**
 * 创建新的EventExecutorChooserFactory.EventExecutorChoosers的工厂。
 *
 * Factory that creates new {@link EventExecutorChooser}s.
 */
@UnstableApi
public interface EventExecutorChooserFactory {

    /**
     * 返回一个新的EventExecutorChooserFactory.EventExecutorChooser。
     *
     * Returns a new {@link EventExecutorChooser}.
     */
    EventExecutorChooser newChooser(EventExecutor[] executors);

    /**
     * 选择下一个EventExecutor来使用。
     *
     * Chooses the next {@link EventExecutor} to use.
     */
    @UnstableApi
    interface EventExecutorChooser {

        /**
         * 返回要使用的新的EventExecutor。
         *
         * Returns the new {@link EventExecutor} to use.
         */
        EventExecutor next();
    }
}
