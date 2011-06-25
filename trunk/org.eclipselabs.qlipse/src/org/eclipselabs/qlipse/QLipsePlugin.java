package org.eclipselabs.qlipse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.qlipse.internal.QuickLookExecutionJob;
import org.osgi.framework.BundleContext;

public class QLipsePlugin extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "org.eclipselabs.qlipse"; //$NON-NLS-1$

    public static final String CONTEXT_ID = "org.eclipselabs.qlipse.context"; //$NON-NLS-1$

    private static QLipsePlugin plugin;

    public QLipsePlugin() {
    }

    private final Map<IWorkbenchWindow, QuickLookExecutionJob> map = new HashMap<IWorkbenchWindow, QuickLookExecutionJob>();

    public void openQuickLook(IWorkbenchWindow window, List<String> paths) {
        closeQuickLook(window);

        final IContextService contextService = (IContextService) window.getService(IContextService.class);

        final QuickLookExecutionJob job = new QuickLookExecutionJob(paths, contextService);
        job.setSystem(true);
        job.setUser(false);
        map.put(window, job);
        job.schedule();
    }

    public void closeQuickLook(IWorkbenchWindow window) {
        final QuickLookExecutionJob job = map.remove(window);
        if (job != null) {
            job.destroy();
        }
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;

        for (QuickLookExecutionJob job : map.values()) {
            job.destroy();
        }

        super.stop(context);
    }

    public static QLipsePlugin getDefault() {
        return plugin;
    }
}
