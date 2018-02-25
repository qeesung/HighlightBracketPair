package io.github.qeesung.highlighter;

import com.intellij.json.JsonElementTypes;
import com.intellij.json.JsonTokenType;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import io.github.qeesung.brace.BraceTokenType;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonBraceHighlighter extends BraceHighlighter {
    private static final List<Map.Entry<IElementType, IElementType>> BRACE_TOKENS = new LinkedList<>();

    /**
     * supported brace types
     */
    static {
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(
                JsonElementTypes.L_CURLY, JsonElementTypes.R_CURLY
        ));
        BRACE_TOKENS.add(new AbstractMap.SimpleEntry<>(
                JsonElementTypes.L_BRACKET, JsonElementTypes.R_BRACKET
        ));
    }
    @Override
    public List<Map.Entry<IElementType, IElementType>> getSupportedBraceToken() {
        return BRACE_TOKENS;
    }

    public JsonBraceHighlighter(Editor editor) {
        super(editor);
    }
}
