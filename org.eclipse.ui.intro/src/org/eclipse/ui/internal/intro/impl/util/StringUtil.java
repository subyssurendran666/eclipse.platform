/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.intro.impl.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;



public class StringUtil {

    public static StringBuffer concat(String string1, String string2,
            String string3) {
        StringBuffer buffer = new StringBuffer(string1);
        buffer.append(string2);
        buffer.append(string3);
        return buffer;
    }

    public static StringBuffer concat(String string1, String string2,
            String string3, String string4) {
        StringBuffer buffer = concat(string1, string2, string3);
        buffer.append(string4);
        return buffer;
    }

    public static StringBuffer concat(String string1, String string2,
            String string3, String string4, String string5) {
        StringBuffer buffer = concat(string1, string2, string3, string4);
        buffer.append(string5);
        return buffer;
    }

    public static StringBuffer concat(String string1, String string2,
            String string3, String string4, String string5, String string6) {
        StringBuffer buffer = concat(string1, string2, string3, string4,
            string5);
        buffer.append(string6);
        return buffer;
    }

    /*
     * Helper method for String#split to handle the case where we
     * might be running on Foundation class libraries instead of 1.4.
     */
    public static String[] split(String string, String delimiters) {
    	try {
    		return string.split(delimiters);
    	} catch (NoSuchMethodError e) {
    		// not running 1.4 so try a string tokenizer
    		List result = new ArrayList();
    		for (StringTokenizer tokenizer = new StringTokenizer(string, delimiters); tokenizer.hasMoreTokens(); )
    			result.add(tokenizer.nextToken());
    		return (String[]) result.toArray(new String[result.size()]);
    	}
    }

    /*
     * Helper method for URLDecoder#decode to handle the case where we
     * might be running on Foundation class libraries instead of 1.4.
     */
	public static String decode(String s, String enc) throws UnsupportedEncodingException {
		try {
			return URLDecoder.decode(s, enc);
		} catch (NoSuchMethodError e) {
			// not running 1.4 so try the old method without an encoding
			return URLDecoder.decode(s);
		}
	}

}
