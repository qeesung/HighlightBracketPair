package io.github.qeesung.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

/**
 * Highlight bracket pair settings to save the some configuration
 * that store int the {@link HighlightBracketPairPluginState}.
 */
@State(
        name = "HighlightBracketPairSettings",
        storages = {
                @Storage(id = "highlight-bracket-pair", file = "$APP_CONFIG$/highlight-bracket-pair-plugin.xml")
        }
)
public class HighlightBracketPairSettings
        implements PersistentStateComponent<HighlightBracketPairPluginState> {
    private HighlightBracketPairPluginState myState = new HighlightBracketPairPluginState();

    /**
     * Get the settings singleton instance.
     *
     * @return settings instance
     */
    public static HighlightBracketPairSettings getInstance() {
        return ServiceManager.getService(HighlightBracketPairSettings.class);
    }

    /**
     * Get the state.
     *
     * @return state
     */
    @Nullable
    @Override
    public HighlightBracketPairPluginState getState() {
        return myState;
    }

    /**
     * Load the state from configuration file.
     *
     * @param state loaded state
     */
    @Override
    public void loadState(HighlightBracketPairPluginState state) {
        this.myState = state;
    }

    /**
     * Get the plugin version.
     *
     * @return plugin version
     */
    public String getVersion() {
        return myState.getPluginVersion();
    }

    /**
     * Set the plugin version.
     *
     * @param version new plugin version
     */
    public void setVersion(String version) {
        myState.setPluginVersion(version);
    }
}
