package com.redhat.ceylon.eclipse.core.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.SourceTypeElementInfo;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;

public final class ModelLoaderTypeRequestor implements
        ITypeRequestor {
    private Parser basicParser;
    private LookupEnvironment lookupEnvironment;
    private CompilerOptions compilerOptions;
    
    public ModelLoaderTypeRequestor(CompilerOptions compilerOptions) {
        this.compilerOptions = compilerOptions;
    }
    
    public void initialize(LookupEnvironment lookupEnvironment) {
        this.lookupEnvironment = lookupEnvironment;
    }
    
    @Override
    public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding,
            AccessRestriction accessRestriction) {
        // case of SearchableEnvironment of an IJavaProject is used
        ISourceType sourceType = sourceTypes[0];
        while (sourceType.getEnclosingType() != null)
            sourceType = sourceType.getEnclosingType();
        if (sourceType instanceof SourceTypeElementInfo) {
            // get source
            SourceTypeElementInfo elementInfo = (SourceTypeElementInfo) sourceType;
            IType type = elementInfo.getHandle();
            ICompilationUnit sourceUnit = (ICompilationUnit) type.getCompilationUnit();
            accept(sourceUnit, accessRestriction);
        } else {
            CompilationResult result = new CompilationResult(sourceType.getFileName(), 1, 1, 0);
            CompilationUnitDeclaration unit =
                SourceTypeConverter.buildCompilationUnit(
                    sourceTypes,
                    SourceTypeConverter.FIELD_AND_METHOD // need field and methods
                    | SourceTypeConverter.MEMBER_TYPE, // need member types
                    // no need for field initialization
                    lookupEnvironment.problemReporter,
                    result);
            lookupEnvironment.buildTypeBindings(unit, accessRestriction);
            lookupEnvironment.completeTypeBindings(unit, true);
        }
    }

    private static IBinaryAnnotation[] noAnnots = new IBinaryAnnotation[0];
    private static char[] dummyClassFileName = "unknown".toCharArray();
    private static Class<?> dummyClassFileNameClass = dummyClassFileName.getClass();
    private static boolean hasClassFileNameParameter = false;

    private static Method getParameterAnnotationsMethod = null;
    private static void loadGetParameterAnnotationsMethod() throws NoSuchMethodException, SecurityException {
        if (getParameterAnnotationsMethod == null) {
            Method m = null;
            try {
                m = IBinaryMethod.class.getMethod("getParameterAnnotations", Integer.TYPE);
            } catch (NoSuchMethodException e) {
                m = IBinaryMethod.class.getMethod("getParameterAnnotations", Integer.TYPE, dummyClassFileNameClass);
                hasClassFileNameParameter = true;
            }
            m.setAccessible(true);
            getParameterAnnotationsMethod = m;
        }
    }
    
    private static IBinaryAnnotation[] getParameterAnnotations(IBinaryMethod methodInfo, int index) {
        try {
            loadGetParameterAnnotationsMethod();
            if (hasClassFileNameParameter) {
                return (IBinaryAnnotation[]) getParameterAnnotationsMethod.invoke(methodInfo, index, dummyClassFileName);
            } else {
                return (IBinaryAnnotation[]) getParameterAnnotationsMethod.invoke(methodInfo, index);
            }
        } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return noAnnots;
        }
    }
    
    @Override
    public void accept(IBinaryType binaryType, PackageBinding packageBinding,
            AccessRestriction accessRestriction) {
        BinaryTypeBinding btb = lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);

        if (btb.isNestedType() && !btb.isStatic()) {
            for (MethodBinding method : btb.methods()) {
                if (method.isConstructor() && method.parameters.length > 0) {
                    char[] signature = method.signature();
                    for (IBinaryMethod methodInfo : binaryType.getMethods()) {
                        if (methodInfo.isConstructor()) {
                            char[] methodInfoSignature = methodInfo.getMethodDescriptor();
                            if (new String(signature).equals(new String(methodInfoSignature))) {
                                IBinaryAnnotation[] binaryAnnotation = getParameterAnnotations(methodInfo, 0);
                                if (binaryAnnotation == null) {
                                    if (methodInfo.getAnnotatedParametersCount() == method.parameters.length + 1) {
                                        AnnotationBinding[][] newParameterAnnotations = new AnnotationBinding[method.parameters.length][];
                                        for (int i=0; i<method.parameters.length; i++) {
                                            IBinaryAnnotation[] goodAnnotations = null;
                                            try {
                                                 goodAnnotations = getParameterAnnotations(methodInfo, i + 1);
                                            }
                                            catch(IndexOutOfBoundsException e) {
                                                break;
                                            }
                                            if (goodAnnotations != null) {
                                                AnnotationBinding[] parameterAnnotations = BinaryTypeBinding.createAnnotations(goodAnnotations, lookupEnvironment, new char[][][] {});
                                                newParameterAnnotations[i] = parameterAnnotations;
                                            }
                                        }
                                        method.setParameterAnnotations(newParameterAnnotations);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void accept(ICompilationUnit sourceUnit,
            AccessRestriction accessRestriction) {
        // Switch the current policy and compilation result for this unit to the requested one.
        CompilationResult unitResult = new CompilationResult(sourceUnit, 1, 1, compilerOptions.maxProblemsPerUnit);
        try {
            CompilationUnitDeclaration parsedUnit = basicParser().dietParse(sourceUnit, unitResult);
            lookupEnvironment.buildTypeBindings(parsedUnit, accessRestriction);
            lookupEnvironment.completeTypeBindings(parsedUnit, true);
        } catch (AbortCompilationUnit e) {
            // at this point, currentCompilationUnitResult may not be sourceUnit, but some other
            // one requested further along to resolve sourceUnit.
            if (unitResult.compilationUnit == sourceUnit) { // only report once
                //requestor.acceptResult(unitResult.tagAsAccepted());
            } else {
                throw e; // want to abort enclosing request to compile
            }
        }
        // Display unit error in debug mode
        if (BasicSearchEngine.VERBOSE) {
            if (unitResult.problemCount > 0) {
                System.out.println(unitResult);
            }
        }
    }

    private Parser basicParser() {
        if (this.basicParser == null) {
            ProblemReporter problemReporter =
                new ProblemReporter(
                    DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                    compilerOptions,
                    new DefaultProblemFactory());
            this.basicParser = new Parser(problemReporter, false);
            this.basicParser.reportOnlyOneSyntaxError = true;
        }
        return this.basicParser;
    }
}