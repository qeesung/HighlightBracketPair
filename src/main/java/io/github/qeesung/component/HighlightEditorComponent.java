package io.github.qeesung.component;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import io.github.qeesung.brace.BracePair;
import io.github.qeesung.highlighter.BraceHighlighter;
import io.github.qeesung.highlighter.BraceHighlighterFactory;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HighlightEditorComponent implements CaretListener {
    private final Editor editor;
    private List<RangeHighlighter> highlighterList = new ArrayList<>();
    private ExtraHighlightTrigger extraHighlightTrigger;

    public void dispose() {
        editor.getCaretModel().removeCaretListener(this);
        editor.getContentComponent().removeKeyListener(this.extraHighlightTrigger);
    }

    private static class ExtraHighlightTrigger extends KeyAdapter {
        private static char VIM_INSERT_KEY = 'i';

        private final Editor editor;
        private HighlightEditorComponent highlightEditorComponent;

        public ExtraHighlightTrigger(HighlightEditorComponent component) {
            this.editor = component.getEditor();
            this.highlightEditorComponent = component;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // compatible the vim insert mode
            // will trigger the highlight from normal mode to insert at caret mode
            if (e.getKeyChar() != VIM_INSERT_KEY ||
                    this.editor.getSettings().isBlockCursor())
                return;
            this.highlightEditorComponent.highlightEditorCurrentPair(this.editor);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_ESCAPE)
                return;
            this.highlightEditorComponent.highlightEditorCurrentPair(this.editor);
        }
    }

    public HighlightEditorComponent(Editor editor) {
        this.editor = editor;
        this.extraHighlightTrigger = new ExtraHighlightTrigger(this);
        this.editor.getContentComponent().addKeyListener(this.extraHighlightTrigger);
        editor.getCaretModel().addCaretListener(this);
    }

    @Override
    public void caretPositionChanged(CaretEvent e) {
        Editor editor = e.getEditor();
        highlightEditorCurrentPair(editor);
    }

    @Override
    public void caretAdded(CaretEvent e) {
        // ignore the event
    }

    @Override
    public void caretRemoved(CaretEvent e) {
        // ignore the event
    }

    public void highlightEditorCurrentPair(Editor editor) {
        int offset = editor.getCaretModel().getOffset();
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

    public Editor getEditor() {
        return editor;
    }
}
