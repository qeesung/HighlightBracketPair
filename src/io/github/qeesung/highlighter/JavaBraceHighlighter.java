package io.github.qeesung.highlighter;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import io.github.qeesung.brace.BracePair;

import java.util.*;
import java.util.List;

import static io.github.qeesung.brace.BraceTokenType.DOUBLE_QUOTE;

public class JavaBraceHighlighter extends BraceHighlighter {
    private static final List<Map.Entry<IElementType, IElementType>> BRACE_TOKENS = new LinkedList<>();

    /**
     * supported brace types
     */
    static {
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(JavaTokenType.LBRACE, JavaTokenType.RBRACE));
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(JavaTokenType.LBRACKET, JavaTokenType.RBRACKET));
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(JavaTokenType.LPARENTH, JavaTokenType.RPARENTH));
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(JavaTokenType.LT, JavaTokenType.GT));
    }


    public JavaBraceHighlighter(Editor editor) {
        super(editor);
    }

    @Override
    public List<Map.Entry<IElementType, IElementType>> getSupportedBraceToken() {
        return BRACE_TOKENS;
    }

    @Override
    public BracePair findClosetBracePairInStringSymbols(int offset) {
        EditorHighlighter editorHighlighter = ((EditorEx) editor).getHighlighter();
        HighlighterIterator leftIterator = editorHighlighter.createIterator(offset);
        HighlighterIterator rightIterator = editorHighlighter.createIterator(offset);
        IElementType type = leftIterator.getTokenType();
        if(type != JavaTokenType.STRING_LITERAL)
            return super.findClosetBracePairInStringSymbols(offset);

        int leftOffset = leftIterator.getStart();
//        for (; !leftIterator.atEnd(); leftIterator.retreat()) {
//            int index = leftIterator.getStart();
//            String text = this.document.getText(new TextRange(index, index+1));
//            if("\"".equals(text)) {
//                leftOffset = index;
//                break;
//            }
//        }

        int rightOffset = leftIterator.getEnd()-1;
//        for (; !rightIterator.atEnd(); rightIterator.advance()) {
//            int index = rightIterator.getEnd();
//            String text = this.document.getText(new TextRange(index, index+1));
//            if("\"".equals(text)) {
//                rightOffset = index;
//                break;
//            }
//        }
        return new BracePair.BracePairBuilder().
                leftType(DOUBLE_QUOTE).
                rightType(DOUBLE_QUOTE).
                leftOffset(leftOffset).
                rightOffset(rightOffset).build();
    }
}
