package io.github.qeesung.component;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import io.github.qeesung.brace.BracePair;
import io.github.qeesung.highlighter.BraceHighlighter;
import io.github.qeesung.highlighter.BraceHighlighterFactory;
import io.github.qeesung.util.Pair;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Highlight editor component to highlight the most left brace
 * and most right brace when the current caret is change.
 */
public class HighlightEditorComponent implements CaretListener {
    private final Editor editor;
    private List<RangeHighlighter> highlighterList = new ArrayList<>();
    private List<RangeHighlighter> gutterHighlighterList = new ArrayList<>();
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

    /**
     * Highlight the current pair.
     * @param editor editor
     */
    public void highlightEditorCurrentPair(Editor editor) {
        int offset = editor.getCaretModel().getOffset();
        BraceHighlighter highlighter =
                BraceHighlighterFactory.getBraceHighlighterInstance(editor);
        if (highlighter == null)
            return;
        // clear the high lighter
        highlighter.eraseHighlight(highlighterList);

        // clear braces in gutter
        highlighter.eraseHighlight(gutterHighlighterList);

        // find the brace positions
        BracePair bracePair = highlighter.findClosetBracePair(offset);

        // high light the brace
        Pair<RangeHighlighter, RangeHighlighter> highlighterEntry =
                highlighter.highlightPair(bracePair);

        // show braces in gutter
        List<RangeHighlighter> showBracesInGutter =
                highlighter.showBracesInGutter(bracePair);

        if (showBracesInGutter!= null) {
            gutterHighlighterList.addAll(showBracesInGutter);
        }

        // record the high lighter
        if (highlighterEntry != null) {
            highlighterList.add(highlighterEntry.getLeft());
            highlighterList.add(highlighterEntry.getRight());
        }
    }

    public Editor getEditor() {
        return editor;
    }
}
