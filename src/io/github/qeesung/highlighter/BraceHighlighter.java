package io.github.qeesung.highlighter;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import io.github.qeesung.adapter.BraceMatchingUtilAdapter;
import io.github.qeesung.brace.Brace;
import io.github.qeesung.brace.BracePair;
import io.github.qeesung.setting.HighlightBracketPairSettingsPage;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract public class BraceHighlighter {
    public final static int NON_OFFSET = -1;
    public final static int HIGHLIGHT_LAYER_WEIGHT = 100;

    protected Editor editor;
    protected Project project;
    protected Document document;
    protected FileType fileType;
    protected CharSequence fileText;
    protected PsiFile psiFile;
    protected MarkupModelEx markupModelEx;

    public BraceHighlighter(Editor editor) {
        this.editor = editor;
        this.project = this.editor.getProject();
        this.document = this.editor.getDocument();
        this.psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        this.fileType = psiFile.getFileType();
        this.fileText = this.editor.getDocument().getImmutableCharSequence();
        this.markupModelEx = (MarkupModelEx) this.editor.getMarkupModel();
    }

    public List<Map.Entry<IElementType, IElementType>> getSupportedBraceToken() {
        return new LinkedList<>();
    }

    public BracePair findClosetBracePairInBraceTokens(int offset) {
        EditorHighlighter editorHighlighter = ((EditorEx) editor).getHighlighter();
        HighlighterIterator leftTraverseIterator = editorHighlighter.createIterator(offset);
        HighlighterIterator rightTraverseIterator = editorHighlighter.createIterator(offset);
        List<Map.Entry<IElementType, IElementType>> braceTokens = this.getSupportedBraceToken();
        for (Map.Entry<IElementType, IElementType> braceTokenPair :
                braceTokens) {
            int leftBraceOffset = BraceMatchingUtilAdapter.findLeftLParen(
                    leftTraverseIterator, braceTokenPair.getKey(), this.fileText, this.fileType);
            int rightBraceOffset = BraceMatchingUtilAdapter.findRightRParen(
                    rightTraverseIterator, braceTokenPair.getValue(), this.fileText, this.fileType);
            if (leftBraceOffset != NON_OFFSET && rightBraceOffset != NON_OFFSET) {
                return new BracePair.BracePairBuilder().
                        leftType(braceTokenPair.getKey()).
                        rightType(braceTokenPair.getValue()).
                        leftIterator(leftTraverseIterator).
                        rightIterator(rightTraverseIterator).build();


            }
        }
        return new BracePair.BracePairBuilder().
                leftOffset(NON_OFFSET).
                rightOffset(NON_OFFSET).build();
    }

    public BracePair findClosetBracePairInStringSymbols(int offset) {
        return new BracePair.BracePairBuilder().
                leftOffset(NON_OFFSET).
                rightOffset(NON_OFFSET).build();
    }

    public BracePair findClosetBracePair(int offset) {
        BracePair braceTokenBracePair = this.findClosetBracePairInBraceTokens(offset);
        BracePair stringSymbolBracePair = this.findClosetBracePairInStringSymbols(offset);
        if (
                (offset - braceTokenBracePair.getLeftBrace().getOffset() >
                        offset - stringSymbolBracePair.getLeftBrace().getOffset())
                        && (offset - braceTokenBracePair.getRightBrace().getOffset() <
                        offset - stringSymbolBracePair.getRightBrace().getOffset()
                )) {
            return stringSymbolBracePair;
        } else {
            return braceTokenBracePair;
        }
    }

    public Map.Entry<RangeHighlighter, RangeHighlighter> highlightPair(BracePair bracePair) {
        final Brace leftBrace = bracePair.getLeftBrace();
        final Brace rightBrace = bracePair.getRightBrace();
        final int leftBraceOffset = leftBrace.getOffset();
        final int rightBraceOffset = rightBrace.getOffset();
        final String leftBraceText = leftBrace.getText();
        final String rightBraceText = rightBrace.getText();

        if (leftBraceOffset == NON_OFFSET ||
                rightBraceOffset == NON_OFFSET)
            return null;
        TextAttributesKey textAttributesKey =
                HighlightBracketPairSettingsPage.getTextAttributesKeyByToken(leftBrace.getElementType());
        final TextAttributes textAttributes = editor.getColorsScheme().getAttributes(textAttributesKey);

        RangeHighlighter leftHighlighter = markupModelEx.addRangeHighlighter(
                leftBraceOffset,
                leftBraceOffset + leftBraceText.length(),
                HighlighterLayer.SELECTION + HIGHLIGHT_LAYER_WEIGHT,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE);
        RangeHighlighter rightHighlighter = markupModelEx.addRangeHighlighter(
                rightBraceOffset,
                rightBraceOffset + rightBraceText.length(),
                HighlighterLayer.SELECTION + HIGHLIGHT_LAYER_WEIGHT,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE);
        return new AbstractMap.SimpleEntry<>(leftHighlighter, rightHighlighter);
    }

    public void eraseHighlight(List<RangeHighlighter> list) {
        for (RangeHighlighter l :
                list) {
            this.markupModelEx.removeHighlighter(l);
        }
    }
}
