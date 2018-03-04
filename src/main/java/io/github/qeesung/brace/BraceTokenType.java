package io.github.qeesung.brace;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;

import java.util.HashMap;
import java.util.Map;

public class BraceTokenType {
    private final static Map<IElementType, String> ElementType2Text = new HashMap<>();
    public final static IElementType DOUBLE_QUOTE = new IElementType("DOUBLE_QUOTE", Language.ANY);
    public final static String GROOVY_STRING_TOKEN = "Gstring";
    public final static String GROOVY_SINGLE_QUOTE_TOKEN = "string";
    public final static String KOTLIN_STRING_TOKEN = "REGULAR_STRING_PART";
    public final static String KOTLIN_CHAR_TOKEN = "CHARACTER_LITERAL";
    public final static String JS_STRING_TOKEN = "STRING";
    public final static String JAVA_STRING_TOKEN = "STRING_LITERAL";

    static {
        ElementType2Text.put(DOUBLE_QUOTE, "\"");
    }

    public static String getElementTypeText(IElementType type) {
        return ElementType2Text.get(type);
    }
}
