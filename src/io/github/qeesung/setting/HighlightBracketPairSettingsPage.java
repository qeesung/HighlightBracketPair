package io.github.qeesung.setting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static io.github.qeesung.brace.BraceTokenType.DOUBLE_QUOTE;

public class HighlightBracketPairSettingsPage implements ColorSettingsPage {
    public static final TextAttributesKey BRACE_ATTR =
            TextAttributesKey.createTextAttributesKey("BRACE_ATTR");
    public static final TextAttributesKey BRACKET_ATTR =
            TextAttributesKey.createTextAttributesKey("BRACKET_ATTR");
    public static final TextAttributesKey PARENTHESIS_ATTR =
            TextAttributesKey.createTextAttributesKey("PARENTHESIS_ATTR");
    public static final TextAttributesKey DOUBLE_QUOTE_ATTR =
            TextAttributesKey.createTextAttributesKey("DOUBLE_QUOTE_ATTR");

    private static final Map<String, TextAttributesKey> TAGS = new HashMap<>();
    private static final AttributesDescriptor[] ATTRIBUTESDESC = {
            new AttributesDescriptor("Brace", BRACE_ATTR),
            new AttributesDescriptor("Bracket", BRACKET_ATTR),
            new AttributesDescriptor("Parenthesis", PARENTHESIS_ATTR),
            new AttributesDescriptor("DoubleQuote", DOUBLE_QUOTE_ATTR),
    };
    private static final Map<IElementType, TextAttributesKey> ELETYPE2ATTR = new HashMap<>();

    static {
        ELETYPE2ATTR.put(JavaTokenType.LBRACE, BRACE_ATTR);
        ELETYPE2ATTR.put(JavaTokenType.LBRACKET, BRACKET_ATTR);
        ELETYPE2ATTR.put(JavaTokenType.LPARENTH, PARENTHESIS_ATTR);
        ELETYPE2ATTR.put(DOUBLE_QUOTE, DOUBLE_QUOTE_ATTR);
    }

    static {
        TAGS.put("Brace", BRACE_ATTR);
        TAGS.put("Bracket", BRACKET_ATTR);
        TAGS.put("Parenthesis", PARENTHESIS_ATTR);
        TAGS.put("DoubleQuote", DOUBLE_QUOTE_ATTR);
    }

    public static TextAttributesKey getTextAttributesKeyByToken(IElementType type) {
        return ELETYPE2ATTR.get(type);
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PlainSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "<Brace>{</Brace>...<Brace>}</Brace>" +
                " <Parenthesis>(</Parenthesis>...<Parenthesis>)</Parenthesis>" +
                " <Bracket>[</Bracket>...<Bracket>]</Bracket>" +
                " <DoubleQuote>\"</DoubleQuote>...<DoubleQuote>\"</DoubleQuote>";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAGS;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBUTESDESC;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return new ColorDescriptor[0];
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "HighlightBracketPair";
    }
}
