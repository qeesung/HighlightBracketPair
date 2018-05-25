package io.github.qeesung.component;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.extensions.PluginId;
import io.github.qeesung.setting.HighlightBracketPairSettings;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Highlight bracket pair application component, responsible for some initialization operations,
 * such as register the editor event listener , check the plugin update and so on.
 */
public class HighlightBracketPairApplicationComponent implements ApplicationComponent, EditorFactoryListener {
    private Map<Editor, HighlightEditorComponent> editorHighlightEditorComponentMap;
    /**
     * Plugin has been updated with the current run.
     */
    private boolean updated;

    /**
     * Plugin update notification has been shown.
     */
    private boolean updateNotificationShown;

    /**
     * Checks if plugin was updated in the current run.
     *
     * @return plugin was updated
     */
    public boolean isUpdated() {
        return updated;
    }

    /**
     * Check if the plugin notification is shown.
     *
     * @return is shown.
     */
    public boolean isUpdateNotificationShown() {
        return updateNotificationShown;
    }

    /**
     * Update the state that is notification is shown.
     *
     * @param shown new shown state
     */
    public void setUpdateNotificationShown(boolean shown) {
        this.updateNotificationShown = shown;
    }

    /**
     * Invoked when the editor is created, and establish the relationship
     * between the {@link Editor} editor and {@link HighlightEditorComponent} component.
     *
     * @param editorFactoryEvent editor factory event.
     */
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent editorFactoryEvent) {
        Editor editor = editorFactoryEvent.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        HighlightEditorComponent highlightEditorComponent =
                new HighlightEditorComponent(editor);
        editorHighlightEditorComponentMap.put(editor, highlightEditorComponent);
    }

    /**
     * Invoked when the editor is released, and dissolve the relationship
     * between the {@link Editor} editor and {@link HighlightEditorComponent} component,
     * and dispose the component.
     *
     * @param editorFactoryEvent
     */
    @Override
    public void editorReleased(@NotNull EditorFactoryEvent editorFactoryEvent) {
        HighlightEditorComponent editorComponent =
                editorHighlightEditorComponentMap.remove(editorFactoryEvent.getEditor());
        if (editorComponent == null) {
            return;
        }
        editorComponent.dispose();
    }

    /**
     * Invoked when the application is started, then register the {@link HighlightBracketPairApplicationComponent}
     * component to the editor events,  and check if the plugin is updated.
     */
    @Override
    public void initComponent() {
        editorHighlightEditorComponentMap = new HashMap<>();
        EditorFactory.getInstance().
                addEditorFactoryListener(this, ApplicationManager.getApplication());

        final HighlightBracketPairSettings settings = HighlightBracketPairSettings.getInstance();
        updated = !getPlugin().getVersion().equals(settings.getVersion());
        if (updated) {
            settings.setVersion(getPlugin().getVersion());
        }
    }

    /**
     * Invoked when the application is shutdown, then dissolve all the relationship between the
     * {@link Editor } editors and {@link HighlightEditorComponent} components, finally dispose
     * all the components.
     */
    @Override
    public void disposeComponent() {
        for (HighlightEditorComponent editorComponent : editorHighlightEditorComponentMap.values()) {
            editorComponent.dispose();
        }
        editorHighlightEditorComponentMap.clear();
    }

    /**
     * Get the component name.
     *
     * @return component name
     */
    @NotNull
    @Override
    public String getComponentName() {
        return "HighlightBracketPair";
    }

    /**
     * Get the {@link HighlightBracketPairApplicationComponent} component single instance.
     *
     * @return the {@link HighlightBracketPairApplicationComponent} single instance.
     */
    public static HighlightBracketPairApplicationComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(HighlightBracketPairApplicationComponent.class);
    }

    /**
     * Get the plugin description by plugin id.
     *
     * @return plugin description
     */
    private IdeaPluginDescriptor getPlugin() {
        return PluginManager.getPlugin(
                PluginId.getId("io.github.qeesung.component.HighlightBracketPair"));
    }
}
