/*
 * Copyright Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the authors tag. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License version 2.
 * 
 * This particular file is subject to the "Classpath" exception as provided in the 
 * LICENSE file that accompanied this code.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.redhat.ceylon.eclipse.core.model.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

import com.redhat.ceylon.compiler.loader.mirror.TypeMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeParameterMirror;

public class JDTTypeParameter implements TypeParameterMirror {

    private TypeVariableBinding type;
    private String name;
    private List<TypeMirror> bounds;
    private LookupEnvironment lookupEnvironment;

    public JDTTypeParameter(TypeVariableBinding parameter, LookupEnvironment lookupEnvironment) {
        this.type = parameter;
        this.lookupEnvironment = lookupEnvironment;
    }

    @Override
    public String getName() {
        if (name == null) {
            name = new String(type.readableName());
        }
        return name;
    }

    @Override
    public List<TypeMirror> getBounds() {
        if (bounds == null) {
            List<TypeBinding> javaBounds = new ArrayList<TypeBinding>();
            javaBounds.add(type.upperBound());
            javaBounds.addAll(Arrays.asList(type.otherUpperBounds()));
            bounds = new ArrayList<TypeMirror>(javaBounds.size());
            for(TypeBinding bound : javaBounds)
                bounds.add(new JDTType(bound, lookupEnvironment));
        }
        return bounds;
    }
}
