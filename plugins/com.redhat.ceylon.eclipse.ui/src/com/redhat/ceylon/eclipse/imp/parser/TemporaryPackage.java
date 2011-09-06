package com.redhat.ceylon.eclipse.imp.parser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;

public class TemporaryPackage extends Package {

	Package sourcePackage;
    public TemporaryPackage(Package sourcePackage)
    {
        super();
        setName(sourcePackage.getName());
        setModule(sourcePackage.getModule());
    }

	@Override
	public List<Declaration> getMembers() {
		final List<Declaration> superMembers = sourcePackage.getMembers();
		return new List<Declaration>() {
			@Override
			public int size() {
				return superMembers.size();
			}
			@Override
			public boolean isEmpty() {
				return superMembers.isEmpty();
			}
			@Override
			public boolean contains(Object o) {
				return superMembers.contains(o);
			}
			@Override
			public Iterator<Declaration> iterator() {
				return superMembers.iterator();
			}
			@Override
			public Object[] toArray() {
				return superMembers.toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return superMembers.toArray(a);
			}

			@Override
			public boolean add(Declaration e) {
				if (! superMembers.contains(e))
					return superMembers.add(e);
				else
					return true;
			}

			@Override
			public boolean remove(Object o) {
				return superMembers.remove(o);
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				return superMembers.containsAll(c);
			}

			@Override
			public boolean addAll(Collection<? extends Declaration> c) {
				return superMembers.addAll(c);
			}

			@Override
			public boolean addAll(int index, Collection<? extends Declaration> c) {
				return superMembers.addAll(index, c);
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				return superMembers.removeAll(c);
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				return superMembers.retainAll(c);
			}

			@Override
			public void clear() {
				superMembers.clear();
			}

			@Override
			public Declaration get(int index) {
				return superMembers.get(index);
			}

			@Override
			public Declaration set(int index, Declaration element) {
				return superMembers.set(index, element);
			}

			@Override
			public void add(int index, Declaration element) {
				if (!superMembers.contains(element))
					superMembers.add(index, element);
			}

			@Override
			public Declaration remove(int index) {				
				return superMembers.remove(index);
			}

			@Override
			public int indexOf(Object o) {
				return superMembers.indexOf(o);
			}

			@Override
			public int lastIndexOf(Object o) {
				return superMembers.lastIndexOf(o);
			}

			@Override
			public ListIterator<Declaration> listIterator() {
				return superMembers.listIterator();
			}

			@Override
			public ListIterator<Declaration> listIterator(int index) {
				return superMembers.listIterator(index);
			}

			@Override
			public List<Declaration> subList(int fromIndex, int toIndex) {
				return superMembers.subList(fromIndex, toIndex);
			}
		};	
	}   
}
