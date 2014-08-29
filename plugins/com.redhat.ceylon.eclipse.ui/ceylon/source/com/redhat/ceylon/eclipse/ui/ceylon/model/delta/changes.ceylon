"Change in the declaration that may impact code in other compilation units"
shared abstract class ImpactingChange()
        of StructuralChange | MemberAdded | Removed | MadeVisibleOutsideScope | MadeInvisibleOutsideScope {}

"The visible own structure of the [[element|AbstractDelta.changedElement]] has changed, for example :
 - parameters of a method,
 - extended type of a class,
 - the name or version of a module in module descriptor,
 - etc ..."
shared class StructuralChange() extends ImpactingChange() {}

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
shared class MadeVisibleOutsideScope() extends ImpactingChange() {}

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
shared class MadeInvisibleOutsideScope() extends ImpactingChange() {}


"The [[changed element|AbstractDelta.changedElement]] is not visible anymore from any other 
 compilation unit.
 
 More precisely :
 - a top-level declaration that has been removed,
 - a nested declaration that has been removed or made unshared,
 - a module import that has been removed"
shared class Removed() extends ImpactingChange() {}

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
}

"A member has been added to the [[changed element|AbstractDelta.changedElement]], and is visible
 from some other compilation units.
 More precisely :
 - a top-level declaration (shared or not) that has been added
 to the compilation unit,
 - a module import (shared or not) that has been
 added to a module descriptor"
see(`class ModuleImportAdded`, `class TopLevelDeclarationAdded`)
shared abstract class ScopedMemberAdded(String name, visibleOutsideScope) of ModuleImportAdded | TopLevelDeclarationAdded extends MemberAdded(name) {
    "The visibility of the added member outside the [[changed element|AbstractDelta.changedElement]]
     scope, which means : 
     - in referencing modules for a module import,
     - in outide the current package for a top-level declaration"
    shared Boolean visibleOutsideScope; 
}

"A top-level declaration (shared or not) has been added
 to the compilation unit"
shared abstract class TopLevelDeclarationAdded(String name, Boolean visibleOutsideScope)  extends ScopedMemberAdded(name, visibleOutsideScope) {
}

"A module import (shared or unshared) that has been
 added to a module descriptor"
shared class ModuleImportAdded(String name, Boolean visibleOutsideScope, version) extends ScopedMemberAdded(name, visibleOutsideScope) {
    "The version of the added module import"
    shared String version;
}