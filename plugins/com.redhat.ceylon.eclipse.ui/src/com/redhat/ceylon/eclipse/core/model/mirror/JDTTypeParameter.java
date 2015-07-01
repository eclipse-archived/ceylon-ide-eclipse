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

package com.redhat.ceylon.eclipse.core.model.mirror;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

import com.redhat.ceylon.model.loader.mirror.TypeMirror;
import com.redhat.ceylon.model.loader.mirror.TypeParameterMirror;

public class JDTTypeParameter implements TypeParameterMirror {

    private String name;
    private List<TypeMirror> bounds;

    public JDTTypeParameter(TypeVariableBinding parameter) {
        this(parameter, null, new IdentityHashMap<TypeBinding, JDTType>());
    }
    
    public JDTTypeParameter(TypeVariableBinding parameter, JDTType type, IdentityHashMap<TypeBinding, JDTType> originatingTypes) {
        name = new String(parameter.readableName());
        List<TypeBinding> javaBounds = new ArrayList<TypeBinding>();
        javaBounds.add(parameter.upperBound());
        javaBounds.addAll(Arrays.asList(parameter.otherUpperBounds()));
        bounds = new ArrayList<TypeMirror>(javaBounds.size());
        for(TypeBinding bound : javaBounds)
            bounds.add(JDTType.toTypeMirror(bound, parameter, type, originatingTypes));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TypeMirror> getBounds() {
        return bounds;
    }
    
    @Override
    public String toString() {
        return "[JDTTypeParameter: "+name+"]";
    }
}
