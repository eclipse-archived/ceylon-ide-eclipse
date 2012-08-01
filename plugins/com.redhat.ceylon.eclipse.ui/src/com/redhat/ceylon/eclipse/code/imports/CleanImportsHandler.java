package com.redhat.ceylon.eclipse.code.imports;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportList;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CleanImportsHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        Tree.CompilationUnit cu = editor.getParseController().getRootNode();
        if (cu!=null) {
            String imports = imports(cu);
            if (imports!=null) {
                TextFileChange tfc = new TextFileChange("Clean Imports", 
                        ((IFileEditorInput) editor.getEditorInput()).getFile());
                tfc.setEdit(new MultiTextEdit());
                ImportList til = cu.getImportList();
                int start;
                int length;
                String extra;
                if (til==null || til.getImports().isEmpty()) {
                	start=0;
                	length=0;
                	extra="\n";
                }
                else {
                	start = til.getStartIndex();
                	length = til.getStopIndex()-til.getStartIndex()+1;
                	extra="";
                }
                tfc.addEdit(new ReplaceEdit(start, length, imports+extra));
                tfc.initializeValidationData(null);
                try {
                    getWorkspace().run(new PerformChangeOperation(tfc), 
                            new NullProgressMonitor());
                }
                catch (CoreException ce) {
                    throw new ExecutionException("Error cleaning imports", ce);
                }
            }
        }
        return null;
    }

    public static String imports(Node node, ImportList til) {
        DetectUnusedImportsVisitor duiv = new DetectUnusedImportsVisitor();
        til.visit(duiv);
        node.visit(duiv);
        return imports(til, duiv.getResult(), Collections.<Declaration>emptyList());
    }
    
    private static String imports(final Tree.CompilationUnit cu) {
		final List<Declaration> proposals = new ArrayList<Declaration>();
    	new Visitor() {
    		public void visit(Tree.BaseMemberOrTypeExpression that) {
    			super.visit(that);
    			if (that.getDeclaration()==null) {
    				String name = that.getIdentifier().getText();
    				addProposal(cu, proposals, name);
    			}
    		}
    		public void visit(Tree.BaseType that) {
    			super.visit(that);
    			if (that.getDeclarationModel()==null) {
    				String name = that.getIdentifier().getText();
    				addProposal(cu, proposals, name);
    			}
    		}
			private void addProposal(final Tree.CompilationUnit cu,
					final List<Declaration> proposals, String name) {
				Declaration prop = null;
				for (Package p: cu.getUnit().getPackage().getModule()
				        .getAllPackages()) {
					Declaration d = p.getMember(name, null); //TODO: pass sig
					if (d!=null && d.isToplevel() && 
							d.isShared() && !d.isAnonymous()) {
						if (prop==null) {
							prop=d;
						}
						else {
							//ambiguous
							//TODO: pop up a window!
							prop=null;
							break;
						}
					}
					if (prop!=null && !proposals.contains(prop)) {
						proposals.add(prop);
					}
				}
			}
    	}.visit(cu);
        DetectUnusedImportsVisitor duiv = new DetectUnusedImportsVisitor();
        cu.visit(duiv);
        return imports(cu.getImportList(), duiv.getResult(), proposals);
    }
    
    private static String imports(ImportList til, List<Declaration> unused, List<Declaration> proposed) {
        List<Tree.Import> importList = new ArrayList<Tree.Import>();
        if (til!=null) {
            importList.addAll(til.getImports());
        }
        Collections.sort(importList, new Comparator<Tree.Import>() {
        	@Override
        	public int compare(Tree.Import i1, Tree.Import i2) {
        		return packageName(i1).compareTo(packageName(i2));
        	}
        });
        String lastToplevel=null;
        StringBuilder builder = new StringBuilder();
        String last = null;
        boolean lastHasWildcard = false;
        Map<String, Tree.Import> packages = new LinkedHashMap<String, Tree.Import>();
        for (Tree.Import ti: importList) {
        	packages.put(packageName(ti), ti);
        }
        for (Declaration d: proposed) {
        	String pn = d.getUnit().getPackage().getNameAsString();
        	if (!packages.containsKey(pn)) {
        		packages.put(pn, null);
        	}
        }
        for (Map.Entry<String, Tree.Import> pack: packages.entrySet()) {
        	String pn = pack.getKey();
        	Tree.Import ti = pack.getValue();
        	boolean appendingToLast = last!=null && last.equals(pn);
        	boolean hasWildcard = ti!=null && ti.getImportMemberOrTypeList().getImportWildcard()!=null || 
        			appendingToLast&&lastHasWildcard;
        	List<Tree.ImportMemberOrType> list = new ArrayList<Tree.ImportMemberOrType>();
        	if (ti!=null) {
        		for (Tree.ImportMemberOrType i: ti.getImportMemberOrTypeList()
        				.getImportMemberOrTypes()) {
        			if (i.getDeclarationModel()!=null && 
        					i.getIdentifier().getErrors().isEmpty() && 
        					i.getErrors().isEmpty()) {
        				if (!unused.contains(i.getDeclarationModel())) {
        					if (!hasWildcard || i.getAlias()!=null || 
        							i.getImportMemberOrTypeList()!=null) {
        						list.add(i);
        					}
        				}
        				else {
        					if (i.getImportMemberOrTypeList()!=null) {
        						for (Tree.ImportMemberOrType j: i.getImportMemberOrTypeList()
        								.getImportMemberOrTypes()) {
        							if (j.getDeclarationModel()!=null && 
        									j.getIdentifier().getErrors().isEmpty() && 
        									j.getErrors().isEmpty()) {
        								if (!unused.contains(j.getDeclarationModel())) {
        									list.add(i);
        									break;
        								}
        							}
        						}
        					}
        				}
        			}
        		}
        	}
        	int di = pn.indexOf('.');
        	String topLevel = di<0 ? pn:pn.substring(0,di);
        	if (lastToplevel!=null && !topLevel.equals(lastToplevel)) {
        		builder.append("\n");
        	}
        	lastToplevel=topLevel;
        	if (hasWildcard || !list.isEmpty() || ti==null) {
        		if (appendingToLast) {
        			builder.setLength(builder.length()-3);
        			if (lastHasWildcard) {
        				builder.setLength(builder.length()-3);                        
        			}
        			else {
        				builder.append(", ");
        			}
        		}
        		else  {
        			builder.append("import ")
        			.append(pn)
        			.append(" { ");
        		}
        		for (Tree.ImportMemberOrType i: list) {
        			if (i.getDeclarationModel()!=null && 
        					i.getIdentifier().getErrors().isEmpty() &&
        					i.getErrors().isEmpty()) {
        				if ( !i.getImportModel().getAlias().equals(i.getDeclarationModel().getName()) ) {
        					builder.append(i.getImportModel().getAlias()).append("=");
        				}
        				builder.append(i.getDeclarationModel().getName());
        				if (i.getImportMemberOrTypeList()!=null) {
        					builder.append(" { ");
        					boolean found=false;
        					for (Tree.ImportMemberOrType j: i.getImportMemberOrTypeList()
        							.getImportMemberOrTypes()) {
        						if (j.getDeclarationModel()!=null && 
        								j.getIdentifier().getErrors().isEmpty() &&
        								j.getErrors().isEmpty()) {
        							if (!unused.contains(j.getDeclarationModel())) {
        								found=true;
        								if (!j.getImportModel().getAlias().equals(j.getDeclarationModel().getName())) {
        									builder.append(j.getImportModel().getAlias()).append("=");
        								}
        								builder.append(j.getDeclarationModel().getName()).append(", ");
        							}
        						}
        					}
        					if (found) builder.setLength(builder.length()-2);
        					builder.append(" }");
        					if (!found) builder.setLength(builder.length()-5);
        				}
        				builder.append(", ");
        			}
        		}
        		for (Declaration d: proposed) {
        			if (d.getUnit().getPackage().getNameAsString().equals(pn)) {
        				builder.append(d.getName()).append(", ");
        			}
        		}
        		if (hasWildcard) {
        			builder.append("...");
        		}
        		else {
        			builder.setLength(builder.length()-2);
        		}
        		builder.append(" }\n");
        		last = pn;
        		lastHasWildcard= hasWildcard;
        	}
        	else {
        		last=null;
        	}
        }
        if (builder.length()!=0) {
        	builder.setLength(builder.length()-1);
        }
        return builder.toString();
    }
    
    private static String packageName(Tree.Import i) {
        return i.getImportMemberOrTypeList()
                .getImportList().getImportedScope()
                .getQualifiedNameString();
    }
    
    @Override
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && 
                editor instanceof CeylonEditor &&
                editor.getEditorInput() instanceof IFileEditorInput) {
            CeylonParseController cpc = ((CeylonEditor) editor).getParseController();
            return cpc==null || cpc.getRootNode()==null ? false : true;
                //!cpc.getRootNode().getImportList().getImports().isEmpty();
        }
        return false;
    }
}
