/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.ibatis;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GoTo sql map id support.
 */
public class GoToSqlMapReferenceIdSymbolProvider extends GoToSymbolProvider {

    protected void getNames(@NotNull final Module module, final Set<String> result) {
        IbatisManager manager = IbatisManager.getInstance();
        Map<String, DomElement> allReference = manager.getAllSqlMapReference(module);
        GoToSymbolProvider.addNames(allReference.values(), result);

    }

    protected void getItems(@NotNull final Module module, final String name, final List<NavigationItem> result) {
        IbatisManager manager = IbatisManager.getInstance();
        Map<String, DomElement> allReference = manager.getAllSqlMapReference(module);
        if (allReference.containsKey(name)) {
            final NavigationItem item = GoToSymbolProvider.createNavigationItem(allReference.get(name));
            if (item != null) {
                result.add(item);
            }
        }
    }

}