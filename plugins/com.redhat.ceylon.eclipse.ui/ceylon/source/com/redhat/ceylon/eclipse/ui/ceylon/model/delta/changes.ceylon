"Change in the declaration that may impact code in other compilation units"
shared abstract class ImpactingChange()
        of StructuralChange | MemberAdded | Removed | MadeVisibleOutsideScope | MadeInvisibleOutsideScope {}

"The visible own structure of the [[element|AbstractDelta.changedElement]] has changed, for example :
 - parameters of a method,
 - extended type of a class,
 - the name or version of a module in module descriptor,
 - etc ..."
shared abstract class StructuralChange() of structuralChange extends ImpactingChange() {}
shared object structuralChange extends StructuralChange() {
    string => "structuralChange";
}

"The [[changed element|AbstractDelta.changedElement]] is top-level and it has been made shared.
 
 More precisely :
 - a top level declaration that has been made shared. This means that 
 it might now be seen by units in other packages, which could solve some
 unresolved reference problems in other packages. However this doesn't
 change anything for the current package compilation units.
 - a module import that has been made shared. This means it could solve some 
 unresolved problems in other modules, but also that the graph of 
 module dependencies may have greatly changed. However this doesn't change anything
 for the current module compilation units
 "
shared abstract class MadeVisibleOutsideScope() of madeVisibleOutsideScope extends ImpactingChange() {}
shared object madeVisibleOutsideScope extends MadeVisibleOutsideScope() {
    string => "madeVisibleOutsideScope";
}

"The [[changed element|AbstractDelta.changedElement]] is top-level and it has been made unshared
 
 More precisely :
 - a top level declaration that has been made unshared. This means that 
 it might not anymore be seen by units in other packages,
 which could produce some unresolved reference problems in other packages.
 However this doesn't change anything for the current package compilation units. 
 - a module import that has been made unshared. This means it could produce some 
 unresolved problems in other modules, but also that the graph of 
 module dependencies may have greatly changed. However this doesn't change anything
 for the current module compilation units"
shared abstract class MadeInvisibleOutsideScope() of madeInvisibleOutsideScope extends ImpactingChange() {}
shared object madeInvisibleOutsideScope extends MadeInvisibleOutsideScope() {
    string => "madeInvisibleOutsideScope";
}

"The [[changed element|AbstractDelta.changedElement]] is not visible anymore from any other 
 compilation unit.
 
 More precisely :
 - a top-level declaration that has been removed,
 - a nested declaration that has been removed or made unshared,
 - a module import that has been removed"
shared abstract class Removed() of removed extends ImpactingChange() {}
shared object removed extends Removed() {
    string => "removed";
}

"A member has been added to [[changed element|AbstractDelta.changedElement]], and is visible
 from some other compilation units.
 More precisely :
 - a shared member that has been added to a declaration
 - a top-level declaration (shared or not) that has been added
 to the compilation unit,
 - a module import (shared or unshared) that has been
 added to a module descriptor"
see(`class ModuleImportAdded`, `class TopLevelDeclarationAdded`, `class DeclarationMemberAdded`)
shared abstract class MemberAdded(name) of ScopedMemberAdded | DeclarationMemberAdded extends ImpactingChange() {
    "The name of the member added to the [[changed element|AbstractDelta.changedElement]]:
     - the local name of a declaration (without the package name)
     - the name of the imported module of a module import"
    shared String name;
}

"A shared member has been added to either a top-level declaration 
 or a nested shared declaration. So it's visible in every scope where 
 the parent declaration is visible."
shared class DeclarationMemberAdded(String name) extends MemberAdded(name) {
    string => "DeclarationMemberAdded (\"`` name ``\")";
    shared actual Boolean equals(Object that) {
        if (is DeclarationMemberAdded that) {
            return name==that.name;
        }
        else {
            return false;
        }
    }
}

shared abstract class ScopeVisibility() of visibleOutside | invisibleOutside {}
shared object invisibleOutside extends ScopeVisibility() {
    string => "invisibleOutside";
}
shared object visibleOutside extends ScopeVisibility()  {
    string => "visibleOutside";
}

"A member has been added to the [[changed element|AbstractDelta.changedElement]], and is visible
 from some other compilation units.
 More precisely :
 - a top-level declaration (shared or not) that has been added
 to the compilation unit,
 - a module import (shared or not) that has been
 added to a module descriptor"
see(`class ModuleImportAdded`, `class TopLevelDeclarationAdded`)
shared abstract class ScopedMemberAdded(String name, visibility) of ModuleImportAdded | TopLevelDeclarationAdded extends MemberAdded(name) {
    "The visibility of the added member outside the [[changed element|AbstractDelta.changedElement]]
     scope, which means : 
     - in referencing modules for a module import,
     - in outide the current package for a top-level declaration"
    shared ScopeVisibility visibility; 
}

"A top-level declaration (shared or not) has been added
 to the compilation unit"
shared class TopLevelDeclarationAdded(String name, ScopeVisibility visibility)  extends ScopedMemberAdded(name, visibility) {
    string => "TopLevelDeclarationAdded (\"`` name ``\", `` visibility ``)";
    shared actual Boolean equals(Object that) {
        if (is TopLevelDeclarationAdded that) {
            return name==that.name && 
                visibility===that.visibility;
        }
        else {
            return false;
        }
    }
}

"A module import (shared or unshared) that has been
 added to a module descriptor"
shared class ModuleImportAdded(String name, version, ScopeVisibility visibility) extends ScopedMemberAdded(name, visibility) {
    "The version of the added module import"
    shared String version;
    string => "moduleImportAdded (\"`` name ``\", \"`` version ``\", `` visibility ``)";
    shared actual Boolean equals(Object that) {
        if (is ModuleImportAdded that) {
            return name==that.name && 
                visibility === that.visibility && 
                version==that.version;
        }
        else {
            return false;
        }
    }
}