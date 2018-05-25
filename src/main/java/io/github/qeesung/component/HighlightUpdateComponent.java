package io.github.qeesung.component;

import com.intellij.notification.*;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import io.github.qeesung.setting.HighlightBracketPairSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Highlight bracket pair project component for update.
 */
public class HighlightUpdateComponent extends AbstractProjectComponent {
    private HighlightBracketPairApplicationComponent applicationComponent;

    protected HighlightUpdateComponent(Project project) {
        super(project);
    }

    /**
     * Invoked when project opened, and check if the plugin is
     * updated and if should * show the notification window.
     */
    @Override
    public void projectOpened() {
        if (applicationComponent.isUpdated() && !applicationComponent.isUpdateNotificationShown()) {
            applicationComponent.setUpdateNotificationShown(true);
            NotificationGroup balloonNotifications = new NotificationGroup(
                    "Notification group", NotificationDisplayType.BALLOON, true);
            Notification success = balloonNotifications.createNotification(
                    "HighlightBracketPair is updated to "
                            +HighlightBracketPairSettings.getInstance().getVersion(),
                    "<br/>If this plugin helps you, please give me a star on " +
                            "<b><a href=\"https://github.com/qeesung/HighlightBracketPair\">Github</a>, ^_^.</b>",
                    NotificationType.INFORMATION,
                    new NotificationListener.UrlOpeningListener(true));
            Notifications.Bus.notify(success, this.myProject);
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return super.getComponentName();
    }

    @Override
    public void initComponent() {
        applicationComponent = HighlightBracketPairApplicationComponent.getInstance();
    }

    @Override
    public void disposeComponent() {
        super.disposeComponent();
    }

    @Override
    public void projectClosed() {
        super.projectClosed();
    }
}
