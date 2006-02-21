/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.mappings;

import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.diff.IThreeWayDiff;
import org.eclipse.team.core.mapping.*;
import org.eclipse.team.core.mapping.provider.MergeContext;
import org.eclipse.team.core.mapping.provider.SynchronizationContext;
import org.eclipse.team.core.subscribers.SubscriberScopeManager;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.ui.*;
import org.eclipse.team.internal.ccvs.ui.actions.*;
import org.eclipse.team.internal.ccvs.ui.subscriber.CVSActionDelegateWrapper;
import org.eclipse.team.internal.ui.TeamUIPlugin;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.internal.ui.mapping.MergeAllActionHandler;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.mapping.ITeamContentProviderManager;
import org.eclipse.team.ui.mapping.SynchronizationActionProvider;
import org.eclipse.team.ui.synchronize.*;

public class WorkspaceModelParticipant extends
		ModelSynchronizeParticipant {

	public static final String VIEWER_ID = "org.eclipse.team.cvs.ui.workspaceSynchronization"; //$NON-NLS-1$
	
	public static final String CONTEXT_MENU_UPDATE_GROUP_1 = "update"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_COMMIT_GROUP_1 = "commit"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_CONTRIBUTION_GROUP_2 = "overrideActions"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_CONTRIBUTION_GROUP_3 = "otherActions1"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_CONTRIBUTION_GROUP_4 = "otherActions2"; //$NON-NLS-1$

	public static final String ID = "org.eclipse.team.cvs.ui.workspace-participant"; //$NON-NLS-1$
	
	/**
	 * CVS workspace action contribution
	 */
	public class WorkspaceMergeActionGroup extends ModelSynchronizeParticipantActionGroup {
		private WorkspaceCommitAction commitToolbar;
		
		public void initialize(ISynchronizePageConfiguration configuration) {
			configuration.setProperty(MERGE_ALL_ACTION_ID, new MergeAllActionHandler(configuration) {
				protected String getJobName() {
					String name = getConfiguration().getParticipant().getName();
					return NLS.bind("Updating all changes in {0}", Utils.shortenText(30, name));
				}
				
				protected boolean promptToUpdate() {
					final IResourceDiffTree tree = getMergeContext().getDiffTree();
					if (tree.isEmpty()) {
						return false;
					}
					final long count = tree.countFor(IThreeWayDiff.INCOMING, IThreeWayDiff.DIRECTION_MASK) + tree.countFor(IThreeWayDiff.CONFLICTING, IThreeWayDiff.DIRECTION_MASK);
					if (count == 0)
						return false;
					final boolean[] result = new boolean[] {true};
					TeamUIPlugin.getStandardDisplay().syncExec(new Runnable() {
						public void run() {
							String sizeString = Long.toString(count);
							String message = tree.size() > 1 ? NLS.bind(CVSUIMessages.UpdateAction_promptForUpdateSeveral, new String[] { sizeString }) : NLS.bind(CVSUIMessages.UpdateAction_promptForUpdateOne, new String[] { sizeString }); // 
							result[0] = MessageDialog.openQuestion(getConfiguration().getSite().getShell(), NLS.bind(CVSUIMessages.UpdateAction_promptForUpdateTitle, new String[] { sizeString }), message); 					 
				 
						}
					});
					return result[0];
				}
				private IMergeContext getMergeContext() {
					return ((IMergeContext)getConfiguration().getProperty(ITeamContentProviderManager.P_SYNCHRONIZATION_CONTEXT));
				}
			});
			super.initialize(configuration);
			
			int modes = configuration.getSupportedModes();
			if ((modes & (ISynchronizePageConfiguration.OUTGOING_MODE | ISynchronizePageConfiguration.BOTH_MODE)) != 0) {	
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_COMMIT_GROUP_1,
						new CommitAction(configuration));
				
				commitToolbar = new WorkspaceCommitAction(configuration);
				Utils.initAction(commitToolbar, "WorkspaceToolbarCommitAction.", Policy.getActionBundle()); //$NON-NLS-1$
				appendToGroup(
						ISynchronizePageConfiguration.P_TOOLBAR_MENU,
						MERGE_ACTION_GROUP,
						commitToolbar);
				// TODO: let's leave off override and commit for now
//				appendToGroup(
//					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
//					CONTEXT_MENU_CONTRIBUTION_GROUP_2,
//					new OverrideAndCommitAction(configuration));
				
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new IgnoreAction(), configuration));
			}
			
			if (!configuration.getSite().isModal()) {
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CreatePatchAction(configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new BranchAction(), configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new ShowAnnotationAction(), configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new ShowResourceInHistoryAction(), configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new SetKeywordSubstitutionAction(), configuration));	
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.team.ui.operations.MergeActionGroup#configureMergeAction(java.lang.String, org.eclipse.jface.action.Action)
		 */
		protected void configureMergeAction(String mergeActionId, Action action) {
			if (mergeActionId == SynchronizationActionProvider.MERGE_ACTION_ID) {
				Utils.initAction(action, "WorkspaceUpdateAction.", Policy.getActionBundle()); //$NON-NLS-1$
			} else if (mergeActionId == SynchronizationActionProvider.OVERWRITE_ACTION_ID) {
				Utils.initAction(action, "OverrideAndUpdateAction.", Policy.getActionBundle()); //$NON-NLS-1$
			} else if (mergeActionId == SynchronizationActionProvider.MARK_AS_MERGE_ACTION_ID) {
				Utils.initAction(action, "ConfirmMergedAction.", Policy.getActionBundle()); //$NON-NLS-1$
			} else if (mergeActionId == MERGE_ALL_ACTION_ID) {
				Utils.initAction(action, "WorkspaceToolbarUpdateAction.", Policy.getActionBundle()); //$NON-NLS-1$
			} else {
				super.configureMergeAction(mergeActionId, action);
			}
		}
		
		protected void addToContextMenu(String mergeActionId, Action action, IMenuManager manager) {
			IContributionItem group = null;;
			if (mergeActionId == SynchronizationActionProvider.MERGE_ACTION_ID) {
				group = manager.find(CONTEXT_MENU_UPDATE_GROUP_1);
			} else if (mergeActionId == SynchronizationActionProvider.OVERWRITE_ACTION_ID) {
				group = manager.find(CONTEXT_MENU_CONTRIBUTION_GROUP_2);
			} else if (mergeActionId == SynchronizationActionProvider.MARK_AS_MERGE_ACTION_ID) {
				group = manager.find(CONTEXT_MENU_CONTRIBUTION_GROUP_2);
			} else {
				super.addToContextMenu(mergeActionId, action, manager);
				return;
			}
			if (group != null) {
				manager.appendToGroup(group.getId(), action);
			} else {
				manager.add(action);
			}
		}
	}
	
	public WorkspaceModelParticipant() {
	}
	
	public WorkspaceModelParticipant(SynchronizationContext context) {
		super(context);
		try {
			setInitializationData(TeamUI.getSynchronizeManager().getParticipantDescriptor("org.eclipse.team.cvs.ui.workspace-participant")); //$NON-NLS-1$
		} catch (CoreException e) {
			TeamUIPlugin.log(e);
		}
		setSecondaryId(Long.toString(System.currentTimeMillis()));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.operations.ModelSynchronizeParticipant#initializeConfiguration(org.eclipse.team.ui.synchronize.ISynchronizePageConfiguration)
	 */
	protected void initializeConfiguration(ISynchronizePageConfiguration configuration) {
		configuration.setProperty(ISynchronizePageConfiguration.P_VIEWER_ID, VIEWER_ID);
		super.initializeConfiguration(configuration);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.operations.ResourceMappingSynchronizeParticipant#createMergeActionGroup()
	 */
	protected ModelSynchronizeParticipantActionGroup createMergeActionGroup() {
		return new WorkspaceMergeActionGroup();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.operations.ModelSynchronizeParticipant#restoreContext(org.eclipse.team.core.mapping.IResourceMappingScope, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected MergeContext restoreContext(ISynchronizationScopeManager manager) {
		return WorkspaceSubscriberContext.createContext(manager, ISynchronizationContext.THREE_WAY);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.operations.ModelSynchronizeParticipant#createScopeManager(org.eclipse.core.resources.mapping.ResourceMapping[])
	 */
	protected ISynchronizationScopeManager createScopeManager(ResourceMapping[] mappings) {
		return new SubscriberScopeManager(CVSProviderPlugin.getPlugin().getCVSWorkspaceSubscriber().getName(), 
				mappings, CVSProviderPlugin.getPlugin().getCVSWorkspaceSubscriber(), true);
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.team.ui.synchronize.AbstractSynchronizeParticipant#getPreferencePages()
     */
    public PreferencePage[] getPreferencePages() {
        return addCVSPreferencePages(super.getPreferencePages());
    }

    public static PreferencePage[] addCVSPreferencePages(PreferencePage[] inheritedPages) {
        PreferencePage[] pages = new PreferencePage[inheritedPages.length + 1];
        for (int i = 0; i < inheritedPages.length; i++) {
            pages[i] = inheritedPages[i];
        }
        pages[pages.length - 1] = new ComparePreferencePage();
        pages[pages.length - 1].setTitle(CVSUIMessages.CVSParticipant_2); 
        return pages;
    }
	
}
