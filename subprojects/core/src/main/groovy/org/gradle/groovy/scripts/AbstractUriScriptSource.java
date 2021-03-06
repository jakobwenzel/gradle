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

package org.gradle.groovy.scripts;

import org.apache.commons.lang.StringUtils;
import org.gradle.internal.hash.HashUtil;

import java.net.URI;

public abstract class AbstractUriScriptSource implements ScriptSource {

    private String className;

    /**
     * Returns the class name for use for this script source.  The name is intended to be unique to support mapping
     * class names to source files even if many sources have the same file name (e.g. build.gradle).
     */
    public String getClassName() {
        if (className == null) {
            URI sourceUri = getResource().getURI();
            String name = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(sourceUri.toString(), "/"), ".");
            StringBuilder className = new StringBuilder(name.length());
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (Character.isJavaIdentifierPart(ch)) {
                    className.append(ch);
                } else {
                    className.append('_');
                }
            }
            if (!Character.isJavaIdentifierStart(className.charAt(0))) {
                className.insert(0, '_');
            }
            className.setLength(Math.min(className.length(), 30));
            className.append('_');
            String path = sourceUri.toString();
            className.append(HashUtil.createCompactMD5(path));

            this.className = className.toString();
        }

        return className;
    }
}
