package io.github.qeesung.brace;

import com.intellij.embedding.IndentEatingLexer;
import com.intellij.lang.Language;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;

public class BraceTokenType {
    private final static Map<IElementType, String> ElementType2Text = new HashMap<>();
    public final static IElementType DOUBLE_QUOTE = new IElementType("DOUBLE_QUOTE", Language.ANY);
    static {
        ElementType2Text.put(JavaTokenType.LBRACE, "{");
        ElementType2Text.put(JavaTokenType.RBRACE, "}");
        ElementType2Text.put(JavaTokenType.LPARENTH, "[");
        ElementType2Text.put(JavaTokenType.RPARENTH, "]");
        ElementType2Text.put(JavaTokenType.LBRACKET, "(");
        ElementType2Text.put(JavaTokenType.RBRACKET, ")");
        ElementType2Text.put(DOUBLE_QUOTE, "\"");
    }

    public static String getElementTypeText(IElementType type) {
        return ElementType2Text.get(type);
    }
}
