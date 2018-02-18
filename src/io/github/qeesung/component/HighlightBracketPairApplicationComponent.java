package io.github.qeesung.component;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HighlightBracketPairApplicationComponent implements ApplicationComponent, EditorFactoryListener {
    private Map<Editor, HighlightEditorComponent> editorHighlightEditorComponentMap;

    /**
     * create editor component, and create the relationship between editor and component
     *
     * @param editorFactoryEvent
     */
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        HighlightEditorComponent highlightEditorComponent =
                new HighlightEditorComponent(editor, true);
        editorHighlightEditorComponentMap.put(editor, highlightEditorComponent);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent editorFactoryEvent) {
        HighlightEditorComponent editorComponent =
                editorHighlightEditorComponentMap.remove(editorFactoryEvent.getEditor());
        if (editorComponent == null) {
            return;
        }
        editorComponent.dispose();
    }

    @Override
    public void initComponent() {
        editorHighlightEditorComponentMap = new HashMap<>();
        EditorFactory.getInstance().
                addEditorFactoryListener(this, ApplicationManager.getApplication());
    }

    @Override
    public void disposeComponent() {
        for (HighlightEditorComponent editorComponent : editorHighlightEditorComponentMap.values()) {
            editorComponent.dispose();
        }
        editorHighlightEditorComponentMap.clear();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "HighlightBracketPair";
    }
}
