package org.eclipselabs.qlipse.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipselabs.qlipse.QLipsePlugin;

public class CloseQuickLookHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        QLipsePlugin.getDefault().closeQuickLook(HandlerUtil.getActiveWorkbenchWindow(event));
        return null;
    }
}
