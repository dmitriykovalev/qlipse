package org.eclipselabs.qlipse.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipselabs.qlipse.QLipsePlugin;

public class OpenQuickLookHandler extends AbstractHandler {
    private String getResourcePath(IResource resource) {
        return resource.getLocation().toOSString();
    }

    private String getPath(Object obj) {
        if (obj instanceof IResource) {
            return getResourcePath((IResource) obj);
        }

        if (obj instanceof IAdaptable) {
            final IAdaptable adaptable = (IAdaptable) obj;

            final Object resource = adaptable.getAdapter(IResource.class);
            if (resource != null) {
                return getResourcePath((IResource) resource);
            }

            final ResourceMapping mapping = (ResourceMapping) adaptable.getAdapter(ResourceMapping.class);
            if (mapping != null) {
                System.out.println(mapping);
            }
        }

        final IResource resource = (IResource) Platform.getAdapterManager().getAdapter(obj, IResource.class);
        if (resource != null) {
            return getResourcePath(resource);
        }

        return null;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            final List<String> paths = new ArrayList<String>();
            for (Object obj : ((IStructuredSelection) selection).toList()) {
                final String path = getPath(obj);
                if (path != null) {
                    paths.add(path);
                }
            }
            QLipsePlugin.getDefault().openQuickLook(HandlerUtil.getActiveWorkbenchWindowChecked(event), paths);
        }
        return null;
    }
}
