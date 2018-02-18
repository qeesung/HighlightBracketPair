package io.github.qeesung.highlighter;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import io.github.qeesung.brace.BracePair;

import java.util.List;
import java.util.Map;

public class DefaultBraceHighlighter extends BraceHighlighter {
    public DefaultBraceHighlighter(Editor editor) {
        super(editor);
    }

    @Override
    public BracePair findClosetBracePair(int offset) {
        return new BracePair.BracePairBuilder().
                leftOffset(NON_OFFSET).
                rightOffset(NON_OFFSET).build();
    }

    @Override
    public Map.Entry<RangeHighlighter, RangeHighlighter> highlightPair(BracePair bracePair) {
        return null;
    }

    @Override
    public void eraseHighlight(List<RangeHighlighter> list) {
        return;
    }
}
