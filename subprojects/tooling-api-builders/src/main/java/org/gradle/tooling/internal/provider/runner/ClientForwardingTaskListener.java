/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.tooling.internal.provider.runner;

import org.gradle.api.Task;
import org.gradle.api.execution.internal.InternalTaskExecutionListener;
import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.tasks.TaskStateInternal;
import org.gradle.initialization.BuildEventConsumer;
import org.gradle.tooling.internal.provider.events.*;

import java.util.Collections;

/**
 * Task listener that forwards all receiving events to the client via the provided {@code BuildEventConsumer} instance.
 *
 * @since 2.5
 */
class ClientForwardingTaskListener implements InternalTaskExecutionListener {

    private final BuildEventConsumer eventConsumer;

    ClientForwardingTaskListener(BuildEventConsumer eventConsumer) {
        this.eventConsumer = eventConsumer;
    }

    private static DefaultTaskDescriptor adapt(Task taskDescriptor) {
        return new DefaultTaskDescriptor(
            EventIdGenerator.generateId(taskDescriptor),
            taskDescriptor.getName(),
            taskDescriptor.getDescription(),
            taskDescriptor.getPath(),
            taskDescriptor.getProject().getPath()
        );
    }

    private static AbstractTaskResult adaptTaskResult(Task task) {
        TaskStateInternal state = (TaskStateInternal) task.getState();
        long startTime = state.getStartTime();
        long endTime = state.getEndTime();

        if (state.getUpToDate()) {
            return new DefaultTaskSuccessResult(startTime, endTime, true);
        }
        if (state.getSkipped()) {
            return new DefaultTaskSkippedResult(startTime, endTime, state.getSkipMessage());
        }
        Throwable failure = state.getFailure();
        if (failure == null) {
            return new DefaultTaskSuccessResult(startTime, endTime, false);
        }
        return new DefaultTaskFailureResult(startTime, endTime, Collections.singletonList(DefaultFailure.fromThrowable(failure)));
    }

    /**
     * This method is called immediately before a task is executed.
     *
     * @param task The task about to be executed. Never null.
     */
    @Override
    public void beforeExecute(TaskInternal task, TaskStateInternal state) {
        eventConsumer.dispatch(new DefaultTaskStartedProgressEvent(state.getStartTime(), adapt(task)));
    }

    /**
     * This method is call immediately after a task has been executed. It is always called, regardless of whether the task completed successfully, or failed with an exception.
     *
     * @param task The task which was executed. Never null.
     * @param state The task state. If the task failed with an exception, the exception is available in this
     */
    @Override
    public void afterExecute(TaskInternal task, TaskStateInternal state) {
        eventConsumer.dispatch(new DefaultTaskFinishedProgressEvent(state.getEndTime(), adapt(task), adaptTaskResult(task)));
    }
}
