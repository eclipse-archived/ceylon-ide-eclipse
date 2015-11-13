import org.eclipse.jdt.core {
    JavaModelException
}
shared Return? nullIfJavaModelException<Return>(Return do() , void onException(JavaModelException e) => e.printStackTrace()  ) {
    try {
        return do();
    } catch (JavaModelException e) {
        onException(e);
        return null;
    }
}