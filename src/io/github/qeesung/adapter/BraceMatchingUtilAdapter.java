package io.github.qeesung.adapter;

import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.tree.IElementType;

import java.util.Stack;

import static com.intellij.codeInsight.highlighting.BraceMatchingUtil.*;

public class BraceMatchingUtilAdapter {
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
                                     FileType fileType) {
        int lastLbraceOffset = -1;
        int initOffset = iterator.atEnd() ? -1 : iterator.getStart();
        Stack<IElementType> braceStack = new Stack<>();
        for (; !iterator.atEnd(); iterator.retreat()) {
            final IElementType tokenType = iterator.getTokenType();

            if (isLBraceToken(iterator, fileText, fileType)) {
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
            } else if (isRBraceToken(iterator, fileText, fileType) &&
                    initOffset != iterator.getStart()) {
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
                                      FileType fileType) {
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
            } else if (isLBraceToken(iterator, fileText, fileType)
                    && initOffset != iterator.getStart()) {
                braceStack.push(iterator.getTokenType());
            }
        }

        return lastRbraceOffset;
    }
}
