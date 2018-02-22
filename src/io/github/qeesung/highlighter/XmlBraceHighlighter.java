package io.github.qeesung.highlighter;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import io.github.qeesung.brace.Brace;
import io.github.qeesung.brace.BracePair;

import java.awt.event.HierarchyListener;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XmlBraceHighlighter extends BraceHighlighter{
    private static final List<Map.Entry<IElementType, IElementType>> BRACE_TOKENS = new LinkedList<>();

    /**
     * supported brace types
     */
    static {
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(XmlTokenType.XML_START_TAG_START,
                XmlTokenType.XML_TAG_END));
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER,
                XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER));
    }

    public XmlBraceHighlighter(Editor editor) {
        super(editor);
    }

    @Override
    public List<Map.Entry<IElementType, IElementType>> getSupportedBraceToken() {
        return BRACE_TOKENS;
    }

    @Override
    public BracePair findClosetBracePairInBraceTokens(int offset) {
        BracePair pair =  super.findClosetBracePairInBraceTokens(offset);
        EditorHighlighter editorHighlighter = ((EditorEx) editor).getHighlighter();
        Brace leftBrace = pair.getLeftBrace();
        Brace rightBrace = pair.getRightBrace();
        if(leftBrace.getOffset() == NON_OFFSET ||
                rightBrace.getOffset() == NON_OFFSET ||
                leftBrace.getElementType() != XmlTokenType.XML_START_TAG_START ||
                rightBrace.getElementType() != XmlTokenType.XML_TAG_END
                ) {
            return  pair;
        }
        HighlighterIterator leftIterator = editorHighlighter.createIterator(leftBrace.getOffset());
        HighlighterIterator rightIterator = editorHighlighter.createIterator(rightBrace.getOffset());
        int leftStartOffset = leftIterator.getStart();
        int rightEndOffset = rightIterator.getEnd();
        int leftEndOffset = getLeftTagEndOffset(leftIterator);
        int rightStartOffset = getRightTagOffset(rightIterator);
        String leftText = this.document.getText(new TextRange(leftStartOffset, leftEndOffset));
        String rightText = this.document.getText(new TextRange(rightStartOffset, rightEndOffset));
        return new BracePair.BracePairBuilder().
                leftType(leftBrace.getElementType()).
                rightType(rightBrace.getElementType()).
                leftOffset(leftStartOffset).
                rightOffset(rightStartOffset).
                leftText(leftText).
                rightText(rightText).build();
    }

    private int getLeftTagEndOffset(HighlighterIterator iterator){
        int initOffset = iterator.getEnd();
        for (; !iterator.atEnd(); iterator.advance()) {
            final IElementType tokenType = iterator.getTokenType();
            if(tokenType == XmlTokenType.XML_TAG_END)
                return iterator.getEnd();
        }
        return initOffset;
    }

    private int getRightTagOffset(HighlighterIterator iterator) {
        int initOffset = iterator.getStart();
        for (; !iterator.atEnd(); iterator.retreat()) {
            final IElementType tokenType = iterator.getTokenType();
            if(tokenType == XmlTokenType.XML_END_TAG_START)
                return iterator.getStart();
        }
        return initOffset;
    }
}
