/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.intro.config;

import org.eclipse.ui.internal.intro.impl.model.*;

/**
 * Factory class used to create instances of an Intro URL. Instances of intro
 * URLs need to be created if you need to programatically construct and execute
 * a valid Intro URL.
 * 
 * @see org.eclipse.ui.intro.config.IIntroURL
 *  
 */
public final class IntroURLFactory {

    /**
     * Non-instantiable.
     */
    private IntroURLFactory() {
        // do nothing
    }


    /**
     * Parses the given string, and returns an IntroURL if the string is a valid
     * Intro URL. Returns null in all other cases. Example usage:
     * 
     * <pre> 
     * <p>
     * StringBuffer url = new StringBuffer();
     * url.append(&quot;http://org.eclipse.ui.intro/showStandby?&quot;);
     * url.append(&quot;pluginId=org.eclipse.pde.ui&quot;);
     * url.append(&quot;&amp;&quot;);
     * url.append(&quot;partId=org.eclipse.pde.ui.sampleStandbyPart&quot;);
     * url.append(&quot;&amp;&quot;);
     * url.append(&quot;input=&quot;);
     * url.append(sampleId);
     * IIntroURL introURL = IntroURLFactory.createIntroURL(url.toString());
     * if (introURL != null) {
     *     introURL.execute();
     * }
     * </pre>
     * 
     * @param url
     * @return
     */
    public static IIntroURL createIntroURL(String url) {
        IntroURLParser parser = new IntroURLParser(url);
        if (parser.hasIntroUrl()) {
            IntroURL introURL = parser.getIntroURL();
            return introURL;
        }
        return null;
    }

}