package org.eclipselabs.qlipse.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipselabs.qlipse.EclipseQuickLook;

public class QuickLookExecutionJob extends Job {
    private final List<String> paths;
    private final IContextService contextService;
    private Process process;

    public QuickLookExecutionJob(List<String> paths, IContextService contextService) {
        super("QuickLookExecutionJob");

        this.paths = paths;
        this.contextService = contextService;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        // Start process
        final List<String> arguments = new ArrayList<String>();
        arguments.add("qlmanage");
        arguments.add("-x");
        arguments.add("-p");
        arguments.addAll(paths);

        try {
            process = (new ProcessBuilder(arguments)).start();
        } catch (IOException e) {
            return new Status(IStatus.ERROR, EclipseQuickLook.PLUGIN_ID, e.getMessage(), e);
        }

        // Activate context
        final IContextActivation[] activation = new IContextActivation[1];

        final Display display = Display.getDefault();

        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                activation[0] = contextService.activateContext(EclipseQuickLook.CONTEXT_ID);
            }
        });

        // Wait for process termination
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        // Deactivate context
        if (!display.isDisposed()) {
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    contextService.deactivateContext(activation[0]);
                }
            });
        }

        return Status.OK_STATUS;
    }

    public void destroy() {
        if (process != null) {
            process.destroy();
        }
    }
}
