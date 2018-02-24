package io.github.qeesung.highlighter;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

public class BraceHighlighterFactory {
    // only provide factory
    private BraceHighlighterFactory() {
    }

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

        FileType fileType = psiFile.getFileType();
        if (fileType instanceof JavaFileType) {
            return new JavaBraceHighlighter(editor);
        } else if(fileType instanceof XmlFileType) {
            return new XmlBraceHighlighter(editor);
        } else if(fileType instanceof JsonFileType) {
            return new JsonBraceHighlighter(editor);
        }
        return new DefaultBraceHighlighter(editor);
    }
}
