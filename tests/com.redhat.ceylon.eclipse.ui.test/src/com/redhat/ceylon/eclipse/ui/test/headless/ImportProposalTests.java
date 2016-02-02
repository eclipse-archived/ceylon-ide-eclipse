package com.redhat.ceylon.eclipse.ui.test.headless;

import static org.junit.Assert.assertEquals;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importProposals;

import ceylon.collection.HashSet;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.junit.Test;

import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrTypeList;
import com.redhat.ceylon.model.typechecker.model.Declaration;

public class ImportProposalTests {
    @Test
    public void testDelimiter1() {
    	ImportMemberOrTypeList imtl = prepareImportMemberOrTypeList();
		HashSet<Declaration> ignoredDeclarations = new HashSet<Declaration>(TypeDescriptor.klass(Declaration.class));
		
		String result = importProposals().formatImportMembers("\n", "  ", ignoredDeclarations, imtl);
		assertEquals(result, "{\n  Bar,\n  Foo\n}");
		
	}
    
    @Test
    public void testDelimiter2() {
    	ImportMemberOrTypeList imtl = prepareImportMemberOrTypeList();
		HashSet<Declaration> ignoredDeclarations = new HashSet<Declaration>(TypeDescriptor.klass(Declaration.class));
		
		String result = importProposals().formatImportMembers("\r\n", "  ", ignoredDeclarations, imtl);
		assertEquals(result, "{\r\n  Bar,\r\n  Foo\r\n}");
	}

    @Test
    public void testDelimiter3() {
    	ImportMemberOrTypeList imtl = prepareImportMemberOrTypeList();
		HashSet<Declaration> ignoredDeclarations = new HashSet<Declaration>(TypeDescriptor.klass(Declaration.class));
		
		String result = importProposals().formatImportMembers("|||", "  ", ignoredDeclarations, imtl);
		assertEquals(result, "{|||  Bar,|||  Foo|||}");
	}
    
    @Test
    public void testSingleImport1() {
    	ImportMemberOrTypeList imtl = prepareSingleImportMemberOrTypeList();
		HashSet<Declaration> ignoredDeclarations = new HashSet<Declaration>(TypeDescriptor.klass(Declaration.class));
		
		String result = importProposals().formatImportMembers("\r\n", "  ", ignoredDeclarations, imtl);
		assertEquals(result, "{\r\n  Bar\r\n}");
	}
    
    @Test
    public void testSingleImport2() {
    	ImportMemberOrTypeList imtl = prepareSingleImportMemberOrTypeList();
		HashSet<Declaration> ignoredDeclarations = new HashSet<Declaration>(TypeDescriptor.klass(Declaration.class));
		
		String result = importProposals().formatImportMembers("\n", "  ", ignoredDeclarations, imtl);
		assertEquals(result, "{\n  Bar\n}");
	}


    @Test
    public void testSingleImport3() {
    	ImportMemberOrTypeList imtl = prepareSingleImportMemberOrTypeList();
		HashSet<Declaration> ignoredDeclarations = new HashSet<Declaration>(TypeDescriptor.klass(Declaration.class));
		
		String result = importProposals().formatImportMembers("|||", "  ", ignoredDeclarations, imtl);
		assertEquals(result, "{|||  Bar|||}");
	}
    
	private ImportMemberOrTypeList prepareSingleImportMemberOrTypeList() {
		CommonToken noToken = new CommonToken(0);
		ImportMemberOrTypeList imtl = new ImportMemberOrTypeList(noToken);
    	List<ImportMemberOrType> importMembers = imtl.getImportMemberOrTypes();
    	
    	ImportMemberOrType importMember = new ImportMemberOrType(noToken);
    	importMember.setAlias(null);
    	importMember.setText("Bar");
    	Identifier identifier = new Identifier(noToken);
    	identifier.setText("Bar");
		importMember.setIdentifier(identifier);
    	importMembers.add(importMember);
    	
		return imtl;
	}

	private ImportMemberOrTypeList prepareImportMemberOrTypeList() {
		CommonToken noToken = new CommonToken(0);
		ImportMemberOrTypeList imtl = new ImportMemberOrTypeList(noToken);
    	List<ImportMemberOrType> importMembers = imtl.getImportMemberOrTypes();
    	
    	ImportMemberOrType importMember = new ImportMemberOrType(noToken);
    	importMember.setAlias(null);
    	importMember.setText("Bar");
    	Identifier identifier = new Identifier(noToken);
    	identifier.setText("Bar");
		importMember.setIdentifier(identifier);
    	importMembers.add(importMember);
    	
    	importMember = new ImportMemberOrType(noToken);
    	importMember.setAlias(null);
    	importMember.setText("Foo");
    	identifier = new Identifier(noToken);
    	identifier.setText("Foo");
		importMember.setIdentifier(identifier);
    	importMembers.add(importMember);
		return imtl;
	}

}
