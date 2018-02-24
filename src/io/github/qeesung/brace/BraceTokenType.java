package io.github.qeesung.brace;

import com.intellij.json.JsonTokenType;
import com.intellij.lang.Language;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;

import java.util.HashMap;
import java.util.Map;

public class BraceTokenType {
    private final static Map<IElementType, String> ElementType2Text = new HashMap<>();
    public final static IElementType DOUBLE_QUOTE = new IElementType("DOUBLE_QUOTE", Language.ANY);

    public final static short JSON_LBRACE_INDEX = 1253;
    public final static short JSON_RBRACE_INDEX = 1257;
    public final static short JSON_LBRACKET_INDEX = 1252;
    public final static short JSON_RBRACKET_INDEX = 1256;
    public final static IElementType JSON_LBRACE = JsonTokenType.find(JSON_LBRACE_INDEX);
    public final static IElementType JSON_RBRACE = JsonTokenType.find(JSON_RBRACE_INDEX);
    public final static IElementType JSON_LBRACKET= JsonTokenType.find(JSON_LBRACKET_INDEX);
    public final static IElementType JSON_RBRACKET= JsonTokenType.find(JSON_RBRACKET_INDEX);

    static {
        ElementType2Text.put(DOUBLE_QUOTE, "\"");
    }

    public static String getElementTypeText(IElementType type) {
        return ElementType2Text.get(type);
    }
}
