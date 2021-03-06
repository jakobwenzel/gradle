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

package org.gradle.tooling.internal.provider.events;

import org.gradle.tooling.internal.protocol.events.InternalBuildFinishedProgressEvent;

public class DefaultBuildFinishedProgressEvent extends AbstractBuildProgressEvent implements InternalBuildFinishedProgressEvent {
    private final AbstractBuildResult result;

    public DefaultBuildFinishedProgressEvent(long eventTime, DefaultBuildDescriptor descriptor, AbstractBuildResult result) {
        super(eventTime, descriptor);
        this.result = result;
    }

    @Override
    public AbstractBuildResult getResult() {
        return result;
    }

    @Override
    public String getDisplayName() {
        return String.format("%s %s", getDescriptor().getDisplayName(), result.getOutcomeDescription());
    }
}
