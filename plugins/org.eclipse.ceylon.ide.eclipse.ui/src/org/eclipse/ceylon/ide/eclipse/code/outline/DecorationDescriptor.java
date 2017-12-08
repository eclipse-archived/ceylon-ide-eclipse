/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package org.eclipse.ceylon.ide.eclipse.code.outline;


public class DecorationDescriptor {

    /**
     * the integer attribute value that selects this decoration
     */
    private final int mask;

    /**
     * the quadrant of the base icon image in which the given decoration should be displayed
     */
    private final int quadrant;

    private String imageKey;

    public DecorationDescriptor(int mask, String imageKey, 
            int quadrant) {
        this.mask= mask;
        this.imageKey = imageKey;
        this.quadrant= quadrant;
    }

    public String getImageKey() {
        return imageKey;
    }
    
    public boolean hasDecoration(int flags) {
        return (mask&flags)!=0;
    }
    
    public int getQuadrant() {
        return quadrant;
    }
    
}