/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.intro.impl;

public interface IIntroConstants {

    // all attributes here are by default public static final.

    // General consts.
    // ---------------
    String PLUGIN_ID = "org.eclipse.ui.intro"; //$NON-NLS-1$
    String PREFIX = PLUGIN_ID + "."; //$NON-NLS-1$

    //  Empty Standby Content part. This is registered in this plugin,
    // through markup.
    String EMPTY_STANDBY_CONTENT_PART = "org.eclipse.ui.intro.config.emptyStandby"; //$NON-NLS-1$

    // Memento constants
    // ------------------
    String MEMENTO_PRESENTATION_TAG = "presentation"; //$NON-NLS-1$
    String MEMENTO_CURRENT_PAGE_ATT = "currentPage"; //$NON-NLS-1$
    String MEMENTO_STANDBY_PART_TAG = "standbyPart"; //$NON-NLS-1$
    String MEMENTO_STANDBY_CONTENT_PART_ID_ATT = "contentPartID"; //$NON-NLS-1$
    String MEMENTO_STANDBY_CONTENT_PART_TAG = "standbyContentPart"; //$NON-NLS-1$
    String MEMENTO_RESTORE_ATT = "restore";

    // CustomizableIntroPart consts:
    // -----------------------------
    // key to retrieve if a standby part is needed.
    String SHOW_STANDBY_PART = "showStandbyPart";

    // Form implementation consts:
    // ---------------------------
    // key to retrieve the into link model object from imageHyperlink widget.
    // convention: actual string value is class name.
    String INTRO_LINK = "IntroLink"; //$NON-NLS-1$

    // key to retrive page sub-title from PageContentForm
    String PAGE_SUBTITLE = "PageSubtitle"; //$NON-NLS-1$

}