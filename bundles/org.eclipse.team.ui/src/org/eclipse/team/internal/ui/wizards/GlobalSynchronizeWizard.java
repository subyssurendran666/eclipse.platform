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
package org.eclipse.team.internal.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.team.internal.ui.ITeamUIImages;
import org.eclipse.team.internal.ui.Policy;
import org.eclipse.team.internal.ui.registry.SynchronizeWizardDescription;
import org.eclipse.team.internal.ui.synchronize.SynchronizeManager;
import org.eclipse.team.ui.TeamImages;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipantReference;
import org.eclipse.ui.IWorkbench;

/**
 * The wizard for synchronizing a synchronize participant.
 * 
 * @since 3.0
 */
public class GlobalSynchronizeWizard extends Wizard {

	protected IWorkbench workbench;
	protected GlobalRefreshWizardSelectionPage mainPage;
	protected ISynchronizeParticipantReference participant;

	public GlobalSynchronizeWizard() {
		setWindowTitle(Policy.bind("GlobalSynchronizeWizard.11")); //$NON-NLS-1$
		setDefaultPageImageDescriptor(TeamImages.getImageDescriptor(ITeamUIImages.IMG_WIZBAN_SHARE));
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(false);
	}
	
	/*
	 * @see Wizard#addPages
	 */
	public void addPages() {
		SynchronizeWizardDescription[] wizards = getWizards();
		mainPage = new GlobalRefreshWizardSelectionPage();
		addPage(mainPage);
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#canFinish()
	 */
	public boolean canFinish() {
		// If we are on the first page, never allow finish unless the selected
		// wizard has no pages.
		if (getContainer().getCurrentPage() == mainPage) {
			if (mainPage.getSelectedWizard() != null && mainPage.getNextPage() == null) {
				return true;
			}
			return false;
		}
		return super.canFinish();
	}

	/*
	 * @see Wizard#performFinish
	 */
	public boolean performFinish() {
		// If we are on the first page and the selected wizard has no pages then allow it to finish.
		if (getContainer().getCurrentPage() == mainPage) {
			IWizard noPageWizard = mainPage.getSelectedWizard();
			if (noPageWizard != null) {
				if (noPageWizard.canFinish()) {
					return noPageWizard.performFinish();
				}
			}
		}
		return true;
	}

	protected SynchronizeWizardDescription[] getWizards() {
		SynchronizeManager manager = (SynchronizeManager) TeamUI.getSynchronizeManager();
		return manager.getWizardDescriptors();
	}	
}
