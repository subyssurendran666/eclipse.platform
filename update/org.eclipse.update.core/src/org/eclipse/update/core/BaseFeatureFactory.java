package org.eclipse.update.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.update.core.model.*;

/**
 * Base implementation of a feature factory.
 * The factory is responsible for constructing the correct
 * concrete implementation of the model objects for each particular
 * feature type. This class creates model objects that correspond
 * to the concrete implementation classes provided in this package.
 * The actual feature creation method is subclass responsibility.
 * <p>
 * This class must be subclassed by clients.
 * </p>
 * @see org.eclipse.update.core.IFeatureFactory
 * @see org.eclipse.update.core.model.FeatureModelFactory
 * @since 2.0
 */
public abstract class BaseFeatureFactory
	extends FeatureModelFactory
	implements IFeatureFactory {

	/**
	 * Create feature. Implementation of this method must be provided by 
	 * subclass
	 * 
	 * @see IFeatureFactory#createFeature(URL,ISite)
	 * @since 2.0
	 */
	public abstract IFeature createFeature(URL url, ISite site)
		throws CoreException;

	/**
	 * Create a concrete implementation of feature model.
	 * 
	 * @see Feature
	 * @return feature model
	 * @since 2.0
	 */
	public FeatureModel createFeatureModel() {
		return new Feature();
	}

	/**
	 * Create a concrete implementation of included feature reference model.
	 * 
	 * @see IncludedFeatureReference
	 * @return feature model
	 * @since 2.1
	 */
	public IncludedFeatureReferenceModel createIncludedFeatureReferenceModel() {
		return new IncludedFeatureReference();
	}

	/**
	 * Create a concrete implementation of install handler model.
	 * 
	 * @see InstallHandlerEntry
	 * @return install handler entry model
	 * @since 2.0
	 */
	public InstallHandlerEntryModel createInstallHandlerEntryModel() {
		return new InstallHandlerEntry();
	}

	/**
	 * Create a concrete implementation of import dependency model.
	 * 
	 * @see Import
	 * @return import dependency model
	 * @since 2.0
	 */
	public ImportModel createImportModel() {
		return new Import();
	}

	/**
	 * Create a concrete implementation of plug-in entry model.
	 * 
	 * @see PluginEntry
	 * @return plug-in entry model
	 * @since 2.0
	 */
	public PluginEntryModel createPluginEntryModel() {
		return new PluginEntry();
	}

	/**
	 * Create a concrete implementation of non-plug-in entry model.
	 * 
	 * @see NonPluginEntry
	 * @return non-plug-in entry model
	 * @since 2.0
	 */
	public NonPluginEntryModel createNonPluginEntryModel() {
		return new NonPluginEntry();
	}

	/**
	 * Create a concrete implementation of annotated URL model.
	 * 
	 * @see URLEntry
	 * @return annotated URL model
	 * @since 2.0
	 */
	public URLEntryModel createURLEntryModel() {
		return new URLEntry();
	}
}