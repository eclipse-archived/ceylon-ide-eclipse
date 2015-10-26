shared class CeylonTopLevelClass_Main_Ceylon_Project() {
	shared void method(String argument) {}
	shared String attribute => string;
	shared class InnerClass(Object o) {}
	shared object obj {}
}

shared interface CeylonTopLevelInterface_Main_Ceylon_Project {
    shared void interfaceMethod(String argument) {}
    shared String interfaceAttribute => string;
    shared class InterfaceClass(Object o) {}
}

shared class CeylonTopLevelInterfaceImplementation_Main_Ceylon_Project() 
        satisfies CeylonTopLevelInterface_Main_Ceylon_Project {
}

shared void ceylonTopLevelMethod_Main_Ceylon_Project() {
}

shared object ceylonTopLevelObject_Main_Ceylon_Project {
}