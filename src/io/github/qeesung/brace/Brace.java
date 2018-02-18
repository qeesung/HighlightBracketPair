package io.github.qeesung.brace;

import com.intellij.psi.tree.IElementType;

public class Brace {
    private final IElementType elementType;
    private final String text;
    private final int offset;

    public Brace(IElementType elementType, String text, int offset) {
        this.elementType = elementType;
        this.text = text;
        this.offset = offset;
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
