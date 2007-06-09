package org.intellij.ibatis.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.intellij.ibatis.dom.abator.AbatorConfiguration;

/**
 * abator generation action
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class AbatorGeneratorAction extends AnAction {
    public void actionPerformed(AnActionEvent event) {

    }

    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        PsiFile psiFile = event.getData(DataKeys.PSI_FILE);
        if (psiFile != null && psiFile instanceof XmlFile) {
            final DomFileElement fileElement = DomManager.getDomManager(psiFile.getProject()).getFileElement((XmlFile) psiFile, DomElement.class);
            if (fileElement != null && fileElement.getRootElement() instanceof AbatorConfiguration) {
                presentation.setEnabled(true);
                presentation.setVisible(true);
                return;
            }
        }
        presentation.setEnabled(false);
        presentation.setVisible(false);
    }
}
