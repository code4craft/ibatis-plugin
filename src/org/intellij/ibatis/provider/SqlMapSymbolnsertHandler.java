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

package org.intellij.ibatis.provider;

import com.intellij.codeInsight.completion.BasicInsertHandler;
import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.LookupData;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;

/**
 * insert handler for xmlt tag text.
 *
 * @author: jacky
 */
public class SqlMapSymbolnsertHandler extends BasicInsertHandler {
    
    private void insertCodeCloseTag(final Editor editor) {
        EditorModificationUtil.insertStringAtCaret(editor, SqlMapSymbolCompletionData.CLOSE_TAG);
        PsiDocumentManager.getInstance(editor.getProject()).commitDocument(editor.getDocument());
        editor.getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
    }

    public void handleInsert(final CompletionContext context, final int startOffset, final LookupData data, final LookupItem item, final boolean signatureSelected, final char completionChar) {
        super.handleInsert(context, startOffset, data, item, signatureSelected, completionChar);
//        insertCodeCloseTag(context.editor);
    }
}