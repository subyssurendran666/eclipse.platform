/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.viewers.model.provisional;


/**
 * Context sensitive update request for whether an element has children.
 * 
 * @since 3.3
 */
public interface IHasChildrenUpdate extends IViewerUpdate {

	/**
	 * Sets whether there are children for this update.
	 * 
	 * @param hasChildren whether there are children
	 */
	public void setHasChilren(boolean hasChildren);
}
