package io.github.qeesung.component;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import io.github.qeesung.brace.BracePair;
import io.github.qeesung.highlighter.BraceHighlighter;
import io.github.qeesung.highlighter.BraceHighlighterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HighlightEditorComponent implements CaretListener {
    private final Editor editor;
    private boolean enableColorful;
    private List<RangeHighlighter> highlighterList = new ArrayList<>();

    public void dispose() {
        editor.getCaretModel().removeCaretListener(this);
    }

    public HighlightEditorComponent(Editor editor, boolean enableColorful) {
        this.editor = editor;
        this.enableColorful = enableColorful;
        editor.getCaretModel().addCaretListener(this);
    }

    @Override
    public void caretPositionChanged(CaretEvent e) {
        Editor editor = e.getEditor();
        int offset = e.getCaret().getOffset();
        BraceHighlighter highlighter =
                BraceHighlighterFactory.getBraceHighlighterInstance(editor);
        if (highlighter == null)
            return;
        // clear the high lighter
        highlighter.eraseHighlight(highlighterList);

        // find the brace positions
        BracePair bracePair = highlighter.findClosetBracePair(offset);

        // high light the brace
        Map.Entry<RangeHighlighter, RangeHighlighter> highlighterEntry =
                highlighter.highlightPair(bracePair);

        // record the high lighter
        if (highlighterEntry != null) {
            highlighterList.add(highlighterEntry.getKey());
            highlighterList.add(highlighterEntry.getValue());
        }
    }
}
