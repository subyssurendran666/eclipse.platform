/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.model;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.*;
import org.eclipse.team.internal.ccvs.core.resources.RemoteFolder;
import org.eclipse.team.internal.ccvs.ui.*;
import org.eclipse.team.internal.ccvs.ui.Policy;
import org.eclipse.team.internal.ccvs.ui.operations.FetchMembersOperation;
import org.eclipse.team.internal.ccvs.ui.operations.FetchMembersOperation.RemoteFolderFilter;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

public class CVSTagElement extends CVSModelElement implements IDeferredWorkbenchAdapter {
	CVSTag tag;
	ICVSRepositoryLocation root;

	private static final String REPO_VIEW_LONG_FORAMT = "dd MMM yyyy HH:mm:ss"; //$NON-NLS-1$
	private static final String REPO_VIEW_SHORT_FORMAT = "dd MMM yyyy"; //$NON-NLS-1$
	private static final String TIME_ONLY_COLUMN_FORMAT = "HH:mm:ss"; //$NON-NLS-1$
	private static SimpleDateFormat localLongFormat = new SimpleDateFormat(REPO_VIEW_LONG_FORAMT,Locale.getDefault());
	private static SimpleDateFormat localShortFormat = new SimpleDateFormat(REPO_VIEW_SHORT_FORMAT,Locale.getDefault());
	private static SimpleDateFormat timeColumnFormat = new SimpleDateFormat(TIME_ONLY_COLUMN_FORMAT, Locale.getDefault());

	static synchronized public String toDisplayString(Date date){
		String localTime = timeColumnFormat.format(date);
		timeColumnFormat.setTimeZone(TimeZone.getDefault());
		if(localTime.equals("00:00:00")){ //$NON-NLS-1$
			return localShortFormat.format(date);
		}
		return localLongFormat.format(date);
	}
	
	public CVSTagElement(CVSTag tag, ICVSRepositoryLocation root) {
		this.tag = tag;
		this.root = root;
	}

	public ICVSRepositoryLocation getRoot() {
		return root;
	}

	public CVSTag getTag() {
		return tag;
	}

	public boolean equals(Object o) {
		if (!(o instanceof CVSTagElement))
			return false;
		CVSTagElement t = (CVSTagElement) o;
		if (!tag.equals(t.tag))
			return false;
		return root.equals(t.root);
	}

	public int hashCode() {
		return root.hashCode() ^ tag.hashCode();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		if (!(object instanceof CVSTagElement))
			return null;
		if (tag.getType() == CVSTag.BRANCH || tag.getType() == CVSTag.HEAD) {
			return CVSUIPlugin.getPlugin().getImageDescriptor(
				ICVSUIConstants.IMG_TAG);
		} else if (tag.getType() == CVSTag.VERSION) {
			return CVSUIPlugin.getPlugin().getImageDescriptor(
				ICVSUIConstants.IMG_PROJECT_VERSION);
		} else {
			// This could be a Date tag
			return CVSUIPlugin.getPlugin().getImageDescriptor(
					ICVSUIConstants.IMG_DATE);
		}
	}
	public String getLabel(Object o) {
		if (!(o instanceof CVSTagElement))
			return null;
		CVSTag aTag = ((CVSTagElement) o).tag;
		if(aTag.getType() == CVSTag.DATE){
			Date date = tag.asDate();
			if (date != null){
				return toDisplayString(date);
			}
		}
		return aTag.getName();
	}
	
	public String toString() {
		return tag.getName();
	}
	
	public Object getParent(Object o) {
		if (!(o instanceof CVSTagElement))
			return null;
		return ((CVSTagElement) o).root;
	}

	protected Object[] fetchChildren(Object o, IProgressMonitor monitor) throws TeamException {
		ICVSRemoteResource[] children = CVSUIPlugin.getPlugin().getRepositoryManager().getFoldersForTag(root, tag, monitor);
		if (getWorkingSet() != null)
			children = CVSUIPlugin.getPlugin().getRepositoryManager().filterResources(getWorkingSet(), children);
		return children;
	}
	
	public void fetchDeferredChildren(Object o, IElementCollector collector, IProgressMonitor monitor) {
		if (tag.getType() == CVSTag.HEAD || tag.getType() == CVSTag.DATE) {
			try {
				monitor = Policy.monitorFor(monitor);
				RemoteFolder folder = new RemoteFolder(null, root, ICVSRemoteFolder.REPOSITORY_ROOT_FOLDER_NAME, tag);
				monitor.beginTask(Policy.bind("RemoteFolderElement.fetchingRemoteChildren", root.toString()), 100); //$NON-NLS-1$
				FetchMembersOperation operation = new FetchMembersOperation(null, folder, collector);
				operation.setFilter(new RemoteFolderFilter() {
					public ICVSRemoteResource[] filter(ICVSRemoteResource[] folders) {
						return CVSUIPlugin.getPlugin().getRepositoryManager().filterResources(getWorkingSet(), folders);
					}
				});
				operation.run(Policy.subMonitorFor(monitor, 100));
			} catch (final InvocationTargetException e) {
				Display d = CVSUIPlugin.getStandardDisplay();
				d.asyncExec(new Runnable() {
					public void run() {
						CVSUIPlugin.openError(Utils.getShell(null), Policy.bind("CVSTagElement.0"), Policy.bind("CVSTagElement.1"), e); //$NON-NLS-1$ //$NON-NLS-2$
					}
				});
			} catch (InterruptedException e) {
				// Cancelled by the user;
			} finally {
				monitor.done();
			}
		} else {
			try {
				collector.add(fetchChildren(o, monitor), monitor);
			} catch (TeamException e) {
				CVSUIPlugin.log(e);
			}
		}
	}

	public ISchedulingRule getRule(Object element) {
		return new RepositoryLocationSchedulingRule(root); //$NON-NLS-1$
	}
	
	public boolean isContainer() {
		return true;
	}
}
