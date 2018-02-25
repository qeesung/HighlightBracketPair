package io.github.qeesung.brace;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;

import java.util.HashMap;
import java.util.Map;

public class BraceTokenType {
    private final static Map<IElementType, String> ElementType2Text = new HashMap<>();
    public final static IElementType DOUBLE_QUOTE = new IElementType("DOUBLE_QUOTE", Language.ANY);

    static {
        ElementType2Text.put(DOUBLE_QUOTE, "\"");
    }

    public static String getElementTypeText(IElementType type) {
        return ElementType2Text.get(type);
    }
}
