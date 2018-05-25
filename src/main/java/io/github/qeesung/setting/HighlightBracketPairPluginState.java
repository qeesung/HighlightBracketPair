package io.github.qeesung.setting;

/**
 * Highlight bracket pair plugin state to be hold {@link HighlightBracketPairSettings}.
 */
public class HighlightBracketPairPluginState {
    /**
     * Plugin version.
     */
    private String pluginVersion = "";

    /**
     * Get the Plugin version.
     *
     * @return plugin version
     */
    public String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * Set the plugin version.
     *
     * @param pluginVersion plugin version
     */
    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }
}
