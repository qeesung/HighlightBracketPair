package io.github.qeesung.adapter;

import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.tree.IElementType;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static com.intellij.codeInsight.highlighting.BraceMatchingUtil.*;
import static io.github.qeesung.brace.BraceTokenTypes.*;

public class BraceMatchingUtilAdapter {

    public static final Set<String> STRING_TOKEN_SET = new HashSet<>();
    static {
        STRING_TOKEN_SET.add(GROOVY_STRING_TOKEN);
        STRING_TOKEN_SET.add(GROOVY_SINGLE_QUOTE_TOKEN);
        STRING_TOKEN_SET.add(KOTLIN_STRING_TOKEN);
        STRING_TOKEN_SET.add(KOTLIN_CHAR_TOKEN);
        STRING_TOKEN_SET.add(JS_STRING_TOKEN);
        STRING_TOKEN_SET.add(JAVA_STRING_TOKEN);
    }

    public static boolean isStringToken(IElementType tokenType) {
        String elementName = tokenType.toString();
        return STRING_TOKEN_SET.contains(elementName);
    }
    /**
     * find the left closest brace offset position
     *
     * @param iterator
     * @param lparenTokenType
     * @param fileText
     * @param fileType
     * @return offset
     */
    public static int findLeftLParen(HighlighterIterator iterator,
                                     IElementType lparenTokenType,
                                     CharSequence fileText,
                                     FileType fileType, boolean isBlockCaret) {
        int lastLbraceOffset = -1;
        int initOffset = iterator.atEnd() ? -1 : iterator.getStart();
        Stack<IElementType> braceStack = new Stack<>();
        for (; !iterator.atEnd(); iterator.retreat()) {
            final IElementType tokenType = iterator.getTokenType();

            if (isLBraceToken(iterator, fileText, fileType)) {
                if (!isBlockCaret && initOffset == iterator.getStart())
                    continue;
                if (!braceStack.isEmpty()) {
                    IElementType topToken = braceStack.pop();
                    if (!isPairBraces(tokenType, topToken, fileType)) {
                        break; // unmatched braces
                    }
                } else {
                    if (tokenType == lparenTokenType) {
                        return iterator.getStart();
                    } else {
                        break;
                    }
                }
            } else if (isRBraceToken(iterator, fileText, fileType)) {
                if (initOffset == iterator.getStart())
                    continue;
                braceStack.push(iterator.getTokenType());
            }
        }

        return lastLbraceOffset;
    }

    /**
     * find the right closest brace offset position
     *
     * @param iterator
     * @param rparenTokenType
     * @param fileText
     * @param fileType
     * @return offset
     */
    public static int findRightRParen(HighlighterIterator iterator,
                                      IElementType rparenTokenType,
                                      CharSequence fileText,
                                      FileType fileType, boolean isBlockCaret) {
        int lastRbraceOffset = -1;
        int initOffset = iterator.atEnd() ? -1 : iterator.getStart();
        Stack<IElementType> braceStack = new Stack<>();
        for (; !iterator.atEnd(); iterator.advance()) {
            final IElementType tokenType = iterator.getTokenType();

            if (isRBraceToken(iterator, fileText, fileType)) {
                if (!braceStack.isEmpty()) {
                    IElementType topToken = braceStack.pop();
                    if (!isPairBraces(tokenType, topToken, fileType)) {
                        break; // unmatched braces
                    }
                } else {
                    if (tokenType == rparenTokenType) {
                        return iterator.getStart();
                    } else {
                        break;
                    }
                }
            } else if (isLBraceToken(iterator, fileText, fileType)) {
                if (isBlockCaret && initOffset == iterator.getStart())
                    continue;
                else
                    braceStack.push(iterator.getTokenType());
            }
        }

        return lastRbraceOffset;
    }
}
