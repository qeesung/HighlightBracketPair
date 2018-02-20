package io.github.qeesung.brace;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;

public class Brace {
    private final IElementType elementType;
    private final int offset;
    private final String text;

    public Brace(IElementType elementType, HighlighterIterator iterator) {
        this.elementType = elementType;
        this.offset = iterator.getStart();

        Document document = iterator.getDocument();
        this.text = document.getText(new TextRange(iterator.getStart(),
                iterator.getEnd()));
    }

    public Brace(IElementType elementType, String text, int offset) {
        this.elementType = elementType;
        this.offset = offset;
        this.text = text;
    }

    public IElementType getElementType() {
        return elementType;
    }

    public String getText() {
        return text;
    }

    public int getOffset() {
        return offset;
    }
}
