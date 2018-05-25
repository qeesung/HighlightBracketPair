package io.github.qeesung.highlighter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * Factory to get the {@link BraceHighlighter} instance according to the editor.
 */
public class BraceHighlighterFactory {
    /**
     * Factory instance should not be instantiated.
     */
    private BraceHighlighterFactory() {
    }

    /**
     * Get the {@link BraceHighlighter} according to the editor and file type.
     * @param editor editor
     * @return brace highlighter
     */
    public static BraceHighlighter getBraceHighlighterInstance(Editor editor) {
        if (editor == null)
            return null;
        Project project = editor.getProject();
        Document document = editor.getDocument();

        if (project == null || document == null)
            return null;

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null)
            return null;

        return new DefaultBraceHighlighter(editor);
    }
}
