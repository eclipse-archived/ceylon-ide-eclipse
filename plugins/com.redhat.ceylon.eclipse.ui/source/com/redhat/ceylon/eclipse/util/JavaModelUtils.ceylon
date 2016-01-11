import org.eclipse.jdt.core {
    JavaModelException
}

"Returns the result of the [[do]] action or [[null]] if a JavaModelException occured."
shared Return? withJavaModel<Return>(Return do() , void onException(JavaModelException e) => e.printStackTrace()  ) {
    try {
        return do();
    } catch (JavaModelException e) {
        onException(e);
        return null;
    }
}