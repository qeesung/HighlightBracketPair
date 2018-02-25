package io.github.qeesung.highlighter;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import io.github.qeesung.brace.BracePair;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        HighlighterIterator iterator = editorHighlighter.createIterator(offset);
        IElementType type = iterator.getTokenType();
        boolean isBlockCaret = this.isBlockCaret();
        if (type != JavaTokenType.STRING_LITERAL)
            return super.findClosetBracePairInStringSymbols(offset);

        int leftOffset = iterator.getStart();
        int rightOffset = iterator.getEnd() - 1;
        if(!isBlockCaret && leftOffset == offset)
            return super.findClosetBracePairInStringSymbols(offset);
        return new BracePair.BracePairBuilder().
                leftType(DOUBLE_QUOTE).
                rightType(DOUBLE_QUOTE).
                leftOffset(leftOffset).
                rightOffset(rightOffset).build();
    }
}
