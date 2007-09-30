package org.intellij.ibatis;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * goto SQL map id support.
 *
 * @author Jacky
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