(function(define) {
define(function(require, exports, module) {
//the Ceylon language module
var $$METAMODEL$$={"$mod-name":"ceylon.language","$mod-version":"0.6","ceylon.language":{"Iterator":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Iterable"],"doc":["Produces elements of an `Iterable` object. Classes that \nimplement this interface should be immutable."],"by":["Gavin"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The next element, or `finished` if there are no \nmore elements to be iterated."]},"$nm":"next"}},"$nm":"Iterator"},"Callable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A reference to a function. The type arguments encode \nthe function return type and parameter types. The \nparameter types are typically represented as a tuple\ntype. For example, the type of the function reference \n`plus<Float>` is:\n\n    Callable<Float,[Float,Float]>\n\nwhich we usually abbreviate `Float(Float,Float)`. Any\ninstance of `Callable` may be _invoked_ by supplying a \npositional argument list:\n\n    Float(Float,Float) add = plus<Float>;\n    value four = add(2.0, 2.0);\n\nor by supplying a tuple containing the arguments:\n\n    Float(Float,Float) add = plus<Float>;\n    [Float,Float] twoAndTwo = [2.0, 2.0];\n    value four = add(*twoAndTwo);\n\nThis interface may not be implemented by user code."]},"$nm":"Callable"},"Array":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Cloneable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Ranged"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"abstract":[],"shared":[],"native":[],"doc":["A fixed-size array of elements. An array may have zero\nsize (an empty array). Arrays are mutable. Any element\nof an array may be set to a new value.\n\nThis class is provided primarily to support interoperation \nwith Java, and for some performance-critical low-level \nprogramming tasks."]},"$m":{"copyTo":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$an":{"doc":["The array into which to copy the elements."]},"$nm":"other"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["The index of the first element in this array to copy."]},"$nm":"sourcePosition"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["The index in the given array into which to \ncopy the first element."]},"$nm":"destinationPosition"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["The number of elements to copy."]},"$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Efficiently copy the elements in the segment \n`sourcePosition:length` of this array to the segment \n`destinationPosition:length` of the given array."]},"$nm":"copyTo"},"set":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$an":{"doc":["The index of the element to replace."]},"$nm":"index"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$an":{"doc":["The new element."]},"$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Replace the existing element at the specified index \nwith the given element. Does nothing if the specified \nindex is negative or larger than the index of the \nlast element in the array."]},"$nm":"set"}},"$at":{"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the array, without the first element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this array, returning a new array."],"actual":[]},"$nm":"reversed"}},"$nm":"Array"},"ArraySequence":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder","SequenceAppender"],"doc":["An immutable `Sequence` implemented using the platform's \nnative array type. Where possible copying of the underlying \narray is avoided."]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"iterator"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"count"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"spanTo"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"contains"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"ArraySequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"ArraySequence"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"first"},"elements":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"elements"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"size"}},"$nm":"ArraySequence"},"Singleton":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A sequence with exactly one element, which may be null."]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$c":{"SingletonIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"done":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"done"}},"$nm":"SingletonIterator"}},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns the contained element, if the specified \nindex is `0`."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `1` if this `Singleton`'s element\nsatisfies the predicate, or `0` otherwise."],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A `Singleton` can be equal to another `List` if \nthat `List` has only one element which is equal to \nthis `Singleton`'s element."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0` and the given `length` is greater than `0`.\nOtherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `true` if the specified element is this \n`Singleton`'s element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"initial"},{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"mthd","$nm":"selecting"}},"$nm":"find"},"filter":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0`. Otherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `0`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `Singleton` with the same element."],"actual":[]},"$nm":"clone"},"element":{"$t":{"$nm":"Element"},"$mt":"attr","$nm":"element"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the Singleton itself, or empty"],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Return this singleton."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `Empty`."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `1`."],"actual":[]},"$nm":"size"}},"$nm":"Singleton"},"byKey":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"}],"$an":{"shared":[],"see":["byItem"],"doc":["A comparator for `Entry`s which compares their keys \naccording to the given `comparing()` function."]},"$nm":"byKey"},"Comparable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Other"}],"$an":{"shared":[],"doc":["The general contract for values whose magnitude can be \ncompared. `Comparable` imposes a total ordering upon\ninstances of any type that satisfies the interface.\nIf a type `T` satisfies `Comparable<T>`, then instances\nof `T` may be compared using the comparison operators\n`<`, `>`, `<=`, >=`, and `<=>`.\n\nThe total order of a type must be consistent with the \ndefinition of equality for the type. That is, there\nare three mutually exclusive possibilities:\n\n- `x<y`,\n- `x>y`, or\n- `x==y`"],"by":["Gavin"]},"$m":{"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["equals"],"doc":["Compares this value with the given value. \nImplementations must respect the constraints that: \n\n- `x==y` if and only if `x<=>y == equal` \n   (consistency with `equals()`), \n- if `x>y` then `y<x` (symmetry), and \n- if `x>y` and `y>z` then `x>z` (transitivity)."]},"$nm":"compare"}},"$nm":"Comparable","$st":"Other"},"Comparison":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"larger"},{"$pk":"ceylon.language","$nm":"smaller"},{"$pk":"ceylon.language","$nm":"equal"}],"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[],"actual":[]},"$nm":"string"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Comparable"],"doc":["The result of a comparison between two `Comparable` \nobjects."],"by":["Gavin"]},"$m":{"largerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"largerThan"},"equal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"equal"},"asSmallAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asSmallAs"},"asLargeAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asLargeAs"},"smallerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"smallerThan"},"unequal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"unequal"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"Comparison"},"annotation":{"$t":{"$pk":"ceylon.language","$nm":"Annotation"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as an annotation class, or a \nmethod as an annotation method."]},"$nm":"annotation"},"Empty":{"of":[{"$pk":"ceylon.language","$nm":"empty"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$an":{"shared":[],"see":["Sequence"],"doc":["A sequence with no elements. The type `Empty` may be\nabbreviated `[]`, and an instance is produced by the \nexpression `[]`. That is, in the following expression,\n`e` has type `[]` and refers to the value `[]`:\n\n    [] none = [];\n\n(Whether the syntax `[]` refers to the type or the \nvalue depends upon how it occurs grammatically.)"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["Returns an iterator that is already exhausted."],"actual":[]},"$nm":"iterator"},"sort":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `null` for any given index."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns 0 for any given predicate."],"actual":[]},"$nm":"count"},"select":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"select"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given segment."],"actual":[]},"$nm":"segment"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `false` for any given element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"initial"},{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"following":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"head"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"following"},"withTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"defines"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanTo"},"chain":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"OtherAbsent"}],"$an":{"shared":[],"doc":["Returns `other`."],"actual":[]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"withLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"collect":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"collect"},"span":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"clone"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"indexed"},"sequence":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a string description of the empty sequence: \n`{}`."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `true`."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns 0."],"actual":[]},"$nm":"size"}},"$nm":"Empty"},"Enumerable":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["Abstraction of ordinal types whose instances can be \nmapped to the integers or to a range of integers."]},"$at":{"integerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The corresponding integer. The implementation must\nsatisfy these constraints:\n\n    (x.successor).integerValue = x.integerValue+1\n    (x.predecessor).integerValue = x.integerValue-1\n\nfor every instance `x` of the enumerable type."]},"$nm":"integerValue"}},"$nm":"Enumerable","$st":"Other"},"combine":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"},{"$t":{"$nm":"OtherElement"},"$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"prm","$pt":"f","$nm":"combination"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"otherElements"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"},{"$nm":"Element"},{"$nm":"OtherElement"}],"$an":{"shared":[],"doc":["Applies a function to each element of two `Iterable`s\nand returns an `Iterable` with the results."],"by":["Gavin","Enrique Zamudio","Tako"]},"$m":{"combination":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"},{"$t":{"$nm":"OtherElement"},"$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"mthd","$nm":"combination"}},"$nm":"combine","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Result"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"otherIter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$nm":"otherIter"},"iter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$nm":"iter"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"empty":{"super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Empty"}],"$mt":"obj","$an":{"shared":[],"doc":["A sequence with no elements, abbreviated `[]`. The \nunique instance of the type `[]`."]},"$nm":"empty"},"false":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing falsity in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"false"},"compose":{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"X"},{"$nm":"Y"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[]},"$nm":"compose"},"Formal":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Formal"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[formal]]."]},"$nm":"Formal"},"Sequential":{"of":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A possibly-empty, immutable sequence of values. The \ntype `Sequential<Element>` may be abbreviated \n`[Element*]` or `Element[]`. \n\n`Sequential` has two enumerated subtypes:\n\n- `Empty`, abbreviated `[]`, represents an empty \n   sequence, and\n- `Sequence<Element>`, abbreviated `[Element+]` \n   represents a non-empty sequence, and has the very\n   important subclass `Tuple`."]},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"[ x, y, z ]\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \nelement."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new sequence."],"actual":[]},"$nm":"reversed"}},"$nm":"Sequential"},"minIntegerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The minimum `Integer` value that can be represented\nby the backend"]},"$nm":"minIntegerValue"},"Finished":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"finished"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Iterator"],"doc":["The type of the value that indicates that \nan `Iterator` is exhausted and has no more \nvalues to return."]},"$nm":"Finished"},"Default":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Default"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[default]]."]},"$nm":"Default"},"plus":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["times","sum"],"doc":["Add the given `Summable` values."]},"$nm":"plus"},"final":{"$t":{"$pk":"ceylon.language","$nm":"Final"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as final. A `final` class may \nnot be extended. Marking a class as final affects disjoint\ntype analysis."]},"$nm":"final"},"coalesce":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$an":{"doc":["The values, some of which may be null."]},"$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Return a sequence containing the given values which are\nnot null. If there are no values which are not null,\nreturn an empty sequence."]},"$nm":"coalesce"},"Entry":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"Key"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The key used to access the entry."]},"$nm":"key"},{"$t":{"$nm":"Item"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The value associated with the key."]},"$nm":"item"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A pair containing a _key_ and an associated value called \nthe _item_. Used primarily to represent the elements of \na `Map`. The type `Entry<Key,Item>` may be abbreviated \n`Key->Item`. An instance of `Entry` may be constructed \nusing the `->` operator:\n\n    String->Person entry = person.name->person;\n"],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if this entry is equal to the given\nentry. Two entries are equal if they have the same\nkey and the same value."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a description of the entry in the form \n`key->item`."],"actual":[]},"$nm":"string"},"item":{"$t":{"$nm":"Item"},"$mt":"attr","$an":{"shared":[],"doc":["The value associated with the key."]},"$nm":"item"},"key":{"$t":{"$nm":"Key"},"$mt":"attr","$an":{"shared":[],"doc":["The key used to access the entry."]},"$nm":"key"}},"$nm":"Entry"},"aInt":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"aInt"},"Variable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Variable"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[variable]]."]},"$nm":"Variable"},"Invertable":{"of":[{"$nm":"Inverse"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Inverse"}],"$pk":"ceylon.language","$nm":"Invertable"}],"variance":"out","$nm":"Inverse"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of types which support a unary additive inversion\noperation. For a numeric type, this should return the \nnegative of the argument value. Note that the type \nparameter of this interface is not restricted to be a \nself type, in order to accommodate the possibility of \ntypes whose additive inverse can only be expressed in terms of \na wider type."],"by":["Gavin"]},"$at":{"positiveValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The value itself, expressed as an instance of the\nwider type."]},"$nm":"positiveValue"},"negativeValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The additive inverse of the value, which may be expressed\nas an instance of a wider type."]},"$nm":"negativeValue"}},"$nm":"Invertable","$st":"Inverse"},"ThrownException":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"type"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"when"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"ThrownException"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[]},"$at":{"when":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"when"},"type":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},"$mt":"attr","$an":{"shared":[]},"$nm":"type"}},"$nm":"ThrownException"},"Cloneable":{"of":[{"$nm":"Clone"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Clone"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"variance":"out","$nm":"Clone"}],"$an":{"shared":[],"doc":["Abstract supertype of objects whose value can be \ncloned."]},"$at":{"clone":{"$t":{"$nm":"Clone"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Obtain a clone of this object. For a mutable \nobject, this should return a copy of the object. \nFor an immutable object, it is acceptable to return\nthe object itself."]},"$nm":"clone"}},"$nm":"Cloneable","$st":"Clone"},"Ordinal":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"see":["Character","Integer","Integral","Range"],"doc":["Abstraction of ordinal types, that is, types with \nsuccessor and predecessor operations, including\n`Integer` and other `Integral` numeric types.\n`Character` is also considered an ordinal type. \n`Ordinal` types may be used to generate a `Range`."],"by":["Gavin"]},"$at":{"predecessor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The predecessor of this value."]},"$nm":"predecessor"},"successor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The successor of this value."]},"$nm":"successor"}},"$nm":"Ordinal","$st":"Other"},"maxIntegerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The maximum `Integer` value that can be represented\nby the backend"]},"$nm":"maxIntegerValue"},"largest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","smallest","max"],"doc":["Given two `Comparable` values, return largest of the\ntwo."]},"$nm":"largest"},"native":{"$t":{"$pk":"ceylon.language","$nm":"Native"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation is defined \nin platform-native code."]},"$nm":"native"},"unflatten":{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"flatFunction"}],[{"$t":{"$nm":"Args"},"$mt":"prm","$pt":"v","$nm":"args"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"unflatten"},"formatInteger":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"integer"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$nm":"radix"}]],"$mt":"mthd","$an":{"shared":[],"throws":["AssertionException","if `radix` is not between `minRadix` and `maxRadix`"],"doc":["The string representation of `integer` in the `radix` base.\n`radix` must be between `minRadix` and `maxRadix` included.\n\nIf `integer` is negative, returned string will start by character `-`"]},"$nm":"formatInteger"},"greaterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is greater than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"greaterThan"},"Identifiable":{"$mt":"ifc","$an":{"shared":[],"doc":["The abstract supertype of all types with a well-defined\nnotion of identity. Values of type `Identifiable` may \nbe compared using the `===` operator to determine if \nthey are references to the same object instance. For\nthe sake of convenience, this interface defines a\ndefault implementation of value equality equivalent\nto identity. Of course, subtypes are encouraged to\nrefine this implementation."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Identity equality comparing the identity of the two \nvalues. May be refined by subtypes for which value \nequality is more appropriate. Implementations must\nrespect the constraint that if `x===y` then `x==y` \n(equality is consistent with identity)."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["identityHash"],"doc":["The system-defined identity hash value of the \ninstance. Subtypes which refine `equals()` must \nalso refine `hash`, according to the general \ncontract defined by `Object`."],"actual":[]},"$nm":"hash"}},"$nm":"Identifiable"},"language":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Contains information about the language"]},"$at":{"majorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language major version."]},"$nm":"majorVersion"},"majorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The major version of the code generated for the underlying runtime."]},"$nm":"majorVersionBinary"},"minorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language minor version."]},"$nm":"minorVersion"},"versionName":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release name."]},"$nm":"versionName"},"releaseVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release version."]},"$nm":"releaseVersion"},"minorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The minor version of the code generated for the underlying runtime."]},"$nm":"minorVersionBinary"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language version."]},"$nm":"version"}},"$nm":"language"},"Native":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Native"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[native]]."]},"$nm":"Native"},"Null":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"of":[{"$pk":"ceylon.language","$nm":"null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["null"],"doc":["The type of the `null` value. Any union type of form \n`Null|T` is considered an optional type, whose values\ninclude `null`. Any type of this form may be written as\n`T?` for convenience.\n\nThe `if (exists ... )` construct, or, alternatively,\nthe `assert (exists ...)` construct, may be used to\nnarrow an optional type to a _definite_ type, that is,\na subtype of `Object`:\n\n    String? firstArg = process.arguments.first;\n    if (exists firstArg) {\n        print(\"hello \" + firstArg);\n    }\n\nThe `else` operator evaluates its second operand if \nand only if its first operand is `null`:\n\n    String name = process.arguments.first else \"world\";\n\nThe `then` operator evaluates its second operand when\nits first operand evaluates to `true`, and to `null` \notherwise:\n\n    Float? diff = x>=y then x-y;"],"by":["Gavin"]},"$nm":"Null"},"array":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array containing the given elements. If no\nelements are provided, create an empty array of the\ngiven element type."]},"$nm":"array"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable"],"doc":["Sort the given elements, returning a new sequence."]},"$nm":"sort"},"equalTo":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if they're equal.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"equalTo"},"AssertionException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"message"}],"$mt":"cls","$an":{"shared":[],"doc":["An exception that occurs when an assertion fails, that\nis, when a condition in an `assert` statement evaluates\nto false at runtime."]},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$nm":"message"}},"$nm":"AssertionException"},"suppressedExceptions":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Exception"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns the exceptions that were suppressed (if any) during the handling of the given exception."]},"$nm":"suppressedExceptions"},"Ranged":{"of":[{"$nm":"Span"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Index"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Index"},{"variance":"out","$nm":"Span"}],"$an":{"shared":[],"see":["List","Sequence","String"],"doc":["Abstract supertype of ranged objects which map a range\nof `Comparable` keys to ranges of values. The type\nparameter `Span` abstracts the type of the resulting\nrange.\n\nA span may be obtained from an instance of `Ranged`\nusing the span operator:\n\n    print(\"hello world\"[0..5])\n"]},"$m":{"spanTo":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe start of the receiver and the end index."]},"$nm":"spanTo"},"segment":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a segment containing the mapped values\nstarting from the given index, with the given \nlength."]},"$nm":"segment"},"spanFrom":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe starting index and the end of the receiver."]},"$nm":"spanFrom"},"span":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between \nthe two given indices."]},"$nm":"span"}},"$nm":"Ranged","$st":"Span"},"arrayOfSize":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$an":{"doc":["The size of the resulting array. If the size\nis non-positive, an empty array will be \ncreated."]},"$nm":"size"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$an":{"doc":["The element value with which to populate the\narray. All elements of the resulting array \nwill have the same value."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array of the specified size, populating every \nindex with the given element. If the specified size is\nsmaller than `1`, return an empty array of the given\nelement type."]},"$nm":"arrayOfSize"},"times":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["plus","product"],"doc":["Multiply the given `Numeric` values."]},"$nm":"times"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Produces a sequence of each index to element `Entry` \nfor the given sequence of values."]},"$nm":"entries"},"license":{"$t":{"$pk":"ceylon.language","$nm":"License"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify the URL of the license of a module or \npackage."]},"$nm":"license"},"Object":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Basic","Null"],"doc":["The abstract supertype of all types representing \ndefinite values. Any two `Object`s may be compared\nfor value equality using the `==` and `!=` operators:\n\n    true==false\n    1==\"hello world\"\n    \"hello\"+\" \"+\"world\"==\"hello world\"\n    Singleton(\"hello world\")=={ \"hello world\" }\n\nHowever, since `Null` is not a subtype of `Object`, the\nvalue `null` cannot be compared to any other value\nusing `==`. Thus, value equality is not defined for\noptional types. This neatly voids the problem of\ndeciding the value of the expression `null==null`,\nwhich is simply illegal."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determine if two values are equal. Implementations\nshould respect the constraints that:\n\n- if `x===y` then `x==y` (reflexivity), \n- if `x==y` then `y==x` (symmetry), \n- if `x==y` and `y==z` then `x==z` (transitivity).\n\nFurthermore it is recommended that implementations\nensure that if `x==y` then `x` and `y` have the\nsame concrete class."]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The hash value of the value, which allows the value\nto be an element of a hash-based set or key of a\nhash-based map. Implementations must respect the\nconstraint that if `x==y` then `x.hash==y.hash`."]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A developer-friendly string representing the \ninstance. Concatenates the name of the concrete \nclass of the instance with the `hash` of the \ninstance. Subclasses are encouraged to refine this \nimplementation to produce a more meaningful \nrepresentation."]},"$nm":"string"}},"$nm":"Object"},"null":{"super":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"obj","$an":{"shared":[],"doc":["The null value."],"by":["Gavin"]},"$nm":"null"},"min":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","max","smallest"],"doc":["Given a nonempty stream of `Comparable` values, \nreturn the smallest value in the stream."]},"$nm":"min"},"LazySet":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of `Set` that wraps an `Iterable` of\nelements. All operations on this Set are performed\non the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"complement"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"intersection"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"exclusiveUnion"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"union"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"LazySet"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"elems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"elems"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazySet"},"Float":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"prm","$pt":"v","$nm":"float"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"doc":["An IEEE 754 64-bit [floating point number][]. A `Float` \nis capable of approximately representing numeric values \nbetween 2<sup>-1022<\/sup> and \n(2-2<sup>-52<\/sup>)×2<sup>1023<\/sup>, along with \nthe special values `infinity` and `-infinity`, and \nundefined values (Not a Number). Zero is represented by \ndistinct instances `+0`, `-0`, but these instances are \nequal. An undefined value is not equal to any other\nvalue, not even to itself.\n\n[floating point number]: http:\/\/www.validlab.com\/goldberg\/paper.pdf"]},"$at":{"strictlyNegative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a negative number, `-0`, \nor `-infinity`. Produces `false` for a positive \nnumber, `+0`, or undefined."]},"$nm":"strictlyNegative"},"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The sign of this value. Produces `1` for a positive \nnumber or `infinity`. Produces `-1` for a negative\nnumber or `-infinity`. Produces `0` for `+0`, `-0`, \nor undefined."],"actual":[]},"$nm":"sign"},"infinite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinity","finite"],"doc":["Determines whether this value is infinite in \nmagnitude. Produces `true` for `infinity` and \n`-infinity`. Produces `false` for a finite number, \n`+0`, `-0`, or undefined."]},"$nm":"infinite"},"undefined":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines whether this value is undefined (that is, \nNot a Number or NaN). The undefined value has the \nproperty that it is not equal (`==`) to itself, as \na consequence the undefined value cannot sensibly \nbe used in most collections."]},"$nm":"undefined"},"strictlyPositive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a positive number, `+0`, \nor `infinity`. Produces `false` for a negative \nnumber, `-0`, or undefined."]},"$nm":"strictlyPositive"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a negative number or\n`-infinity`. Produces `false` for a positive number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a positive number or\n`infinity`. Produces `false` for a negative number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"positive"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$nm":"float"},"finite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinite","infinity"],"doc":["Determines whether this value is finite. Produces\n`false` for `infinity`, `-infinity`, and undefined."]},"$nm":"finite"}},"$nm":"Float"},"deprecated":{"$t":{"$pk":"ceylon.language","$nm":"Deprecation"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"reason"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark program elements which should not be \nused anymore."]},"$nm":"deprecated"},"Collection":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["List","Map","Set"],"doc":["Represents an iterable collection of elements of finite \nsize. `Collection` is the abstract supertype of `List`,\n`Map`, and `Set`.\n\nA `Collection` forms a `Category` of its elements.\n\nAll `Collection`s are `Cloneable`. If a collection is\nimmutable, it is acceptable that `clone` produce a\nreference to the collection itself. If a collection is\nmutable, `clone` should produce an immutable collection\ncontaining references to the same elements, with the\nsame structure as the original collection&mdash;that \nis, it should produce an immutable shallow copy of the\ncollection."]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if the given object is an element of\nthis collection. In this default implementation,\nand in most refining implementations, return `false`\notherwise. An acceptable refining implementation\nmay return `true` for objects which are not \nelements of the collection, but this is not \nrecommended. (For example, the `contains()` method \nof `String` returns `true` for any substring of the\nstring.)"],"actual":[]},"$nm":"contains"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determine if the collection is empty, that is, if \nit has no elements."],"actual":[]},"$nm":"empty"}},"$nm":"Collection"},"Range":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"Element"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The start of the range."],"actual":[]},"$nm":"first"},{"$t":{"$nm":"Element"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The end of the range."],"actual":[]},"$nm":"last"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Represents the range of totally ordered, ordinal values \ngenerated by two endpoints of type `Ordinal` and \n`Comparable`. If the first value is smaller than the\nlast value, the range is increasing. If the first value\nis larger than the last value, the range is decreasing.\nIf the two values are equal, the range contains exactly\none element. The range is always nonempty, containing \nat least one value.\n\nA range may be produced using the `..` operator:\n\n    for (i in min..max) { ... }\n    if (char in `A`..`Z`) { ... }\n"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["An iterator for the elements of the range."],"actual":[]},"$c":{"RangeIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"current":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"current"}},"$nm":"RangeIterator"}},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"n"}]],"$mt":"mthd","$an":{"shared":[],"doc":["The element of the range that occurs `n` values after\nthe start of the range. Note that this operation \nis inefficient for large ranges."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$nm":"selecting"}},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if two ranges are the same by comparing\ntheir endpoints."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"next":{"$t":{"$nm":"Element"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$nm":"next"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given object."],"actual":[]},"$nm":"contains"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given value."]},"$nm":"includes"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"span":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the end of the range."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since ranges are \nimmutable."],"actual":[]},"$nm":"clone"},"decreasing":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines if the range is decreasing."]},"$nm":"decreasing"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The end of the range."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this range."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since a Range cannot\ncontain nulls."],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Reverse this range, returning a new range."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"doc":["The rest of the range, without the start of the\nrange."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The start of the range."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The nonzero number of elements in the range."],"actual":[]},"$nm":"size"}},"$nm":"Range"},"max":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","min","largest"],"doc":["Given a nonempty stream of `Comparable` values, \nreturn the largest value in the stream."]},"$nm":"max"},"Integral":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Integral"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of integral numeric types. That is, types \nwith no fractional part, including `Integer`. The \ndivision operation for integral numeric types results \nin a remainder. Therefore, integral numeric types have \nan operation to determine the remainder of any division \noperation."],"by":["Gavin"]},"$m":{"remainder":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Numeric.divided"],"doc":["The remainder, after dividing this number by the \ngiven number."]},"$nm":"remainder"}},"$at":{"unit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is one."]},"$nm":"unit"},"zero":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is zero."]},"$nm":"zero"}},"$nm":"Integral","$st":"Other"},"SequenceAppender":{"super":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder"],"doc":["This class is used for constructing a new nonempty \nsequence by incrementally appending elements to an\nexisting nonempty sequence. The existing sequence is\nnot modified, since `Sequence`s are immutable. This \nclass is mutable but threadsafe."]},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting nonempty sequence. If no elements \nhave been appended, the original nonempty \nsequence."],"actual":[]},"$nm":"sequence"},"elements":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$nm":"elements"}},"$nm":"SequenceAppender"},"smallest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","largest","min"],"doc":["Given two `Comparable` values, return smallest of the\ntwo."]},"$nm":"smallest"},"byIncreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byDecreasing"],"doc":["A comparator which orders elements in increasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byIncreasing"},"larger":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is larger than the given value."],"by":["Gavin"]},"$nm":"larger"},"true":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing truth in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"true"},"join":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"doc":["The iterable objects to join."]},"$nm":"iterables"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["Given a list of iterable objects, return a new sequence \nof all elements of the all given objects. If there are\nno arguments, or if none of the arguments contains any\nelements, return the empty sequence."]},"$nm":"join"},"Exponentiable":{"of":[{"$nm":"This"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"},{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$nm":"This"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types that may be raised to a\npower. Note that the type of the exponent may be\ndifferent to the numeric type which can be \nexponentiated."]},"$m":{"power":{"$t":{"$nm":"This"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of raising this number to the given\npower."]},"$nm":"power"}},"$nm":"Exponentiable","$st":"This"},"Keys":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"prm","$pt":"v","$nm":"correspondence"}],"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"contains"}},"$at":{"correspondence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"attr","$nm":"correspondence"}},"$nm":"Keys"},"Character":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"see":["String"],"doc":["A 32-bit Unicode character."],"by":["Gavin"]},"$at":{"control":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is an ISO control \ncharacter."]},"$nm":"control"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The code point of the character."]},"$nm":"integer"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The lowercase representation of this character."]},"$nm":"lowercased"},"letter":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is a letter. That is,\nif its Unicode general category is *Lu*, *Ll*, \n*Lt*, *Lm*, or *Lo*."]},"$nm":"letter"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The uppercase representation of this character."]},"$nm":"uppercased"},"whitespace":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is a whitespace character. \nThe following characters are whitespace characters:\n\n* *LINE FEED*, `\\n` or `\\{#000A}`,\n* *FORM FEED*, `\\f` or `\\{#000C}`,\n* *CARRIAGE RETURN*, `\\r` or `\\{#000D}`,\n* *HORIZONTAL TABULATION*, `\\t` or `\\{#0009}`,\n* *VERTICAL TABULATION*, `\\{#000B}`,\n* *FILE SEPARATOR*, `\\{#001C}`,\n* *GROUP SEPARATOR*, `\\{#001D}`,\n* *RECORD SEPARATOR*, `\\{#001E}`,\n* *UNIT SEPARATOR*, `\\{#001F}`, and\n* any Unicode character in the general category \n  *Zs*, *Zl*, or *Zp* that is not a non-breaking \n  space."]},"$nm":"whitespace"},"titlecased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The title case representation of this character."]},"$nm":"titlecased"},"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$nm":"character"},"uppercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this is an uppercase representation of\nthe character. That is, if its Unicode general \ncategory is *Lu*."]},"$nm":"uppercase"},"digit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is a numeric digit.\nThat is, if its Unicode general category is *Nd*."]},"$nm":"digit"},"lowercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this is a lowercase representation of\nthe character. That is, if its Unicode general \ncategory is *Ll*."]},"$nm":"lowercase"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["A string containg just this character."],"actual":[]},"$nm":"string"},"titlecase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this is a title case representation of\nthe character. That is, if its Unicode general \ncategory is *Lt*."]},"$nm":"titlecase"}},"$nm":"Character"},"curry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}],[{"$t":{"$nm":"First"},"$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"curry"},"forKey":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forItem"],"doc":["A function that returns the result of the given `resulting()` function \non the key of a given `Entry`."]},"$nm":"forKey"},"product":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["sum"],"doc":["Given a nonempty stream of `Numeric` values, return \nthe product of the values."]},"$nm":"product"},"process":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Represents the current process (instance of the virtual\nmachine)."],"by":["Gavin","Tako"]},"$m":{"readLine":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Read a line of input text from the standard input \nof the virtual machine process."]},"$nm":"readLine"},"writeErrorLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print a line to the standard error of the \nvirtual machine process."]},"$nm":"writeErrorLine"},"writeError":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard error of the \nvirtual machine process."]},"$nm":"writeError"},"propertyValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the given system property of the virtual\nmachine, if any."]},"$nm":"propertyValue"},"namedArgumentValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the first argument of form `-name=value`, \n`--name=value`, or `-name value` specified among the \ncommand line arguments to the virtual machine, if\nany."]},"$nm":"namedArgumentValue"},"write":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard output of the \nvirtual machine process."]},"$nm":"write"},"namedArgumentPresent":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determine if an argument of form `-name` or `--name` \nwas specified among the command line arguments to \nthe virtual machine."]},"$nm":"namedArgumentPresent"},"writeLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["print"],"doc":["Print a line to the standard output of the \nvirtual machine process."]},"$nm":"writeLine"},"exit":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"code"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"exit"}},"$at":{"os":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the operating system this \nprocess is running on."]},"$nm":"os"},"vmVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the virtual machine this \nprocess is running on."]},"$nm":"vmVersion"},"vm":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the virtual machine this \nprocess is running on."]},"$nm":"vm"},"osVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the operating system this \nprocess is running on."]},"$nm":"osVersion"},"newline":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The line ending character sequence on this platform."]},"$nm":"newline"},"arguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The command line arguments to the virtual machine."]},"$nm":"arguments"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"locale":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the IETF language tag representing the\ndefault locale for this virtual machine."]},"$nm":"locale"},"nanoseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in nanoseconds since an arbitrary\nstarting point."]},"$nm":"nanoseconds"},"timezoneOffset":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the offset from UTC, in milliseconds, of\nthe default timezone for this virtual machine."]},"$nm":"timezoneOffset"},"milliseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in milliseconds since midnight, \n1 January 1970."]},"$nm":"milliseconds"}},"$nm":"process"},"forItem":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forKey"],"doc":["A function that returns the result of the given `resulting()` function \non the item of a given `Entry`."]},"$nm":"forItem"},"License":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"url"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"License"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Module"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[license]]."]},"$at":{"url":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"url"}},"$nm":"License"},"shuffle":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"FirstArgs"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"SecondArgs"}],"$an":{"shared":[]},"$nm":"shuffle"},"Annotation":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Annotation"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[annotation]]."]},"$nm":"Annotation"},"nothing":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"doc":["A value that is assignable to any type, but that \nresults in an exception when evaluated. This is most \nuseful for generating members in an IDE."]},"$nm":"nothing"},"doc":{"$t":{"$pk":"ceylon.language","$nm":"Doc"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API documentation of a program\nelement."]},"$nm":"doc"},"Scalar":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$pk":"ceylon.language","$nm":"Number"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Scalar"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types representing scalar\nvalues, including `Integer` and `Float`."],"by":["Gavin"]},"$at":{"magnitude":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of this number."],"actual":[]},"$nm":"magnitude"},"wholePart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part. For integral numeric types,\nthe integral value of a number is the number \nitself."],"actual":[]},"$nm":"wholePart"},"fractionalPart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part. For integral numeric types,\nthe fractional part is always zero."],"actual":[]},"$nm":"fractionalPart"}},"$nm":"Scalar","$st":"Other"},"SequenceBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceAppender","join","Singleton"],"doc":["Since sequences are immutable, this class is used for\nconstructing a new sequence by incrementally appending \nelements to the empty sequence. This class is mutable\nbut threadsafe."]},"$m":{"append":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append an element to the sequence and return this \n`SequenceBuilder`"]},"$nm":"append"},"appendAll":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Append multiple elements to the sequence and return \nthis `SequenceBuilder`"]},"$nm":"appendAll"}},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"native":[],"doc":["The resulting sequence. If no elements have been\nappended, the empty sequence."]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determine if the resulting sequence is empty."]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The size of the resulting sequence."]},"$nm":"size"}},"$nm":"SequenceBuilder"},"ifExists":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"prm","$pt":"f","$nm":"predicate"}],[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$m":{"predicate":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$nm":"predicate"}},"$nm":"ifExists"},"tagged":{"$t":{"$pk":"ceylon.language","$nm":"Tags"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to categorize the API by tag."]},"$nm":"tagged"},"variable":{"$t":{"$pk":"ceylon.language","$nm":"Variable"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark an value as variable. A `variable` value \nmust be assigned multiple times."]},"$nm":"variable"},"Abstract":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Abstract"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[abstract]]."]},"$nm":"Abstract"},"Correspondence":{"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Map","List","Category"],"doc":["Abstract supertype of objects which associate values \nwith keys. `Correspondence` does not satisfy `Category`,\nsince in some cases&mdash;`List`, for example&mdash;it is \nconvenient to consider the subtype a `Category` of its\nvalues, and in other cases&mdash;`Map`, for example&mdash;it \nis convenient to treat the subtype as a `Category` of its\nentries.\n\nThe item corresponding to a given key may be obtained \nfrom a `Correspondence` using the item operator:\n\n    value bg = settings[\"backgroundColor\"] else white;\n\nThe `get()` operation and item operator result in an\noptional type, to reflect the possibility that there is\nno item for the given key."],"by":["Gavin"]},"$m":{"definesAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.defines"],"doc":["Determines if this `Correspondence` defines a value\nfor any one of the given keys."]},"$nm":"definesAny"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.definesAny","Correspondence.definesEvery","Correspondence.keys"],"doc":["Determines if there is a value defined for the \ngiven key."]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Correspondence.items"],"doc":["Returns the value defined for the given key, or \n`null` if there is no value defined for the given \nkey."]},"$nm":"get"},"items":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.get"],"doc":["Returns the items defined for the given keys, in\nthe same order as the corresponding keys."]},"$nm":"items"},"definesEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.defines"],"doc":["Determines if this `Correspondence` defines a value\nfor every one of the given keys."]},"$nm":"definesEvery"}},"$at":{"keys":{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["Correspondence.defines"],"doc":["The `Category` of all keys for which a value is \ndefined by this `Correspondence`."]},"$nm":"keys"}},"$nm":"Correspondence"},"NonemptyContainer":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["A nonempty container."]},"$alias":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"NonemptyContainer"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A count of the number of `true` items in the given values."]},"$nm":"count"},"byItem":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"see":["byKey"],"doc":["A comparator for `Entry`s which compares their items \naccording to the given `comparing()` function."]},"$nm":"byItem"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Authors"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API authors."]},"$nm":"by"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["any"],"doc":["Determines if every one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"every"},"$pkg-shared":"1","Tuple":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"First"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"actual":[]},"$nm":"first"},{"$t":{"$nm":"Rest"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"actual":[]},"$nm":"rest"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$nm":"Element"}],"variance":"out","$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language","$nm":"Empty"},"variance":"out","$nm":"Rest"}],"$an":{"shared":[],"doc":["A _tuple_ is a typed linked list. Each instance of \n`Tuple` represents the value and type of a single link.\nThe attributes `first` and `rest` allow us to retrieve\na value form the list without losing its static type \ninformation.\n\n    value point = Tuple(0.0, Tuple(0.0, Tuple(\"origin\")));\n    Float x = point.first;\n    Float y = point.rest.first;\n    String label = point.rest.rest.first;\n\nUsually, we abbreviate code involving tuples.\n\n    [Float,Float,String] point = [0.0, 0.0, \"origin\"];\n    Float x = point[0];\n    Float y = point[1];\n    String label = point[2];\n\nA list of types enclosed in brackets is an abbreviated \ntuple type. An instance of `Tuple` may be constructed \nby surrounding a value list in brackets:\n\n    [String,String] words = [\"hello\", \"world\"];\n\nThe index operator with a literal integer argument is a \nshortcut for a chain of evaluations of `rest` and \n`first`. For example, `point[1]` means `point.rest.first`.\n\nA _terminated_ tuple type is a tuple where the type of\nthe last link in the chain is `Empty`. An _unterminated_ \ntuple type is a tuple where the type of the last link\nin the chain is `Sequence` or `Sequential`. Thus, a \nterminated tuple type has a length that is known\nstatically. For an unterminated tuple type only a lower\nbound on its length is known statically.\n\nHere, `point` is an unterminated tuple:\n\n    String[] labels = ... ;\n    [Float,Float,String*] point = [0.0, 0.0, *labels];\n    Float x = point[0];\n    Float y = point[1];\n    String? firstLabel = point[2];\n    String[] allLabels = point[2...];"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"current":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"current"}},"$nm":"iterator"}}},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$an":{"doc":["The first element of the resulting tuple."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"doc":["Returns a new tuple that starts with the specified\nelement, followed by the elements of this tuple."],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"end"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$nm":"Rest"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"First"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"Tuple"},"Final":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Final"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[final]]."]},"$nm":"Final"},"lessThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is less than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"lessThan"},"maxRadix":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"maxRadix"},"identityHash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["identical"],"doc":["Return the system-defined identity hash value of the \ngiven value. This hash value is consistent with \nidentity equality."]},"$nm":"identityHash"},"uncurry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$nm":"First"},"$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"prm","$pt":"f","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"uncurry"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["every"],"doc":["Determines if any one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"any"},"optional":{"$t":{"$pk":"ceylon.language","$nm":"Optional"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify that a module can be executed even if \nthe annotated dependency is not available."]},"$nm":"optional"},"Summable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Other"}],"$an":{"shared":[],"see":["String","Numeric"],"doc":["Abstraction of types which support a binary addition\noperator. For numeric types, this is just familiar \nnumeric addition. For strings, it is string \nconcatenation. In general, the addition operation \nshould be a binary associative operation."],"by":["Gavin"]},"$m":{"plus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of adding the given value to this value. \nThis operation should never perform any kind of \nmutation upon either the receiving value or the \nargument value."]},"$nm":"plus"}},"$nm":"Summable","$st":"Other"},"EmptyContainer":{"$mt":"ifc","$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["An empty container."]},"$alias":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"EmptyContainer"},"Set":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A collection of unique elements.\n\nA `Set` is a `Collection` of its elements.\n\nSets may be the subject of the binary union, \nintersection, and complement operators `|`, `&`, and \n`~`."]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements in \nthis `Set` that are not contained in the given\n`Set`."]},"$nm":"complement"},"subset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a subset of the given \n`Set`, that is, if the given set contains all of \nthe elements in this set."]},"$nm":"subset"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \nthat are present in both this `Set` and the given \n`Set`."]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \ncontained in either this `Set` or the given `Set`, \nbut no element contained in both sets."]},"$nm":"exclusiveUnion"},"superset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a superset of the \nspecified Set, that is, if this `Set` contains all \nof the elements in the specified `Set`."]},"$nm":"superset"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Set`s are considered equal if they have the \nsame size and if every element of the first set is\nalso an element of the second set, as determined\nby `contains()`."],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements of \nthis `Set` and all the elements of the given `Set`."]},"$nm":"union"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Set"},"Sequence":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Empty"],"doc":["A nonempty, immutable sequence of values. The type \n`Sequence<Element>`, may be abbreviated `[Element+]`.\n\nGiven a possibly-empty sequence of type `[Element*]`, \nthe `if (nonempty ...)` construct, or, alternatively,\nthe `assert (nonempty ...)` construct, may be used to \nnarrow to a nonempty sequence type:\n\n    [Integer*] nums = ... ;\n    if (nonempty nums) {\n        Integer first = nums.first;\n        Integer max = max(nums);\n        [Integer+] squares = nums.collect((Integer i) i**2));\n        [Integer+] sorted = nums.sort(byIncreasing((Integer i) i));\n    }\n\nOperations like `first`, `max()`, `collect()`, and \n`sort()`, which polymorphically produce a nonempty or \nnon-null output when given a nonempty input are called \n_emptiness-preserving_."],"by":["Gavin"]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The function comparing pairs of elements."]},"$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements."],"actual":[]},"$m":{"comparing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$an":{"doc":["The function comparing pairs of elements."]},"$nm":"comparing"}},"$nm":"sort"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"shorterThan"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"longerThan"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The transformation applied to the elements."]},"$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the results of \napplying the given mapping to the elements of this\nsequence."],"actual":[]},"$m":{"collecting":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"doc":["The transformation applied to the elements."]},"$nm":"collecting"}},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["Sequence.size"],"doc":["The index of the last element of the sequence."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element of the sequence, that is, the\n    element with index `sequence.lastIndex`."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `false`, since every `Sequence` contains at\n    least one element."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \n    element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new nonempty\n    sequence."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element of the sequence, that is, the\n    element with index `0`."],"actual":[]},"$nm":"first"}},"$nm":"Sequence"},"Scalable":{"of":[{"$nm":"Value"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Scale"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Scale"},{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Scalable"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["Abstract supertype of types that support scaling by\na numeric factor. Implementations should generally\nrespect the following constraints, where relevant:\n\n- `x == 1**x`\n- `-x == -1**x`\n- `x-x == 0**x`\n- `x+x == 2**x`"]},"$m":{"scale":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Scale"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"scale"}},"$nm":"Scalable","$st":"Value"},"InitializationException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}],"$mt":"cls","$an":{"shared":[],"see":["late"],"doc":["Thrown when a problem was detected with value initialization.\n\nPossible problems include:\n\n* when a value could not be initialized due to recursive access during initialization, \n* an attempt to use a `late` value before it was initialized, \n* an attempt to assign to a `late` but non-`variable` value after it was initialized."]},"$at":{"description":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$nm":"description"}},"$nm":"InitializationException"},"StringBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"shared":[],"native":[],"doc":["Since strings are immutable, this class is used for\nconstructing a string by incrementally appending \ncharacters to the empty string. This class is mutable \nbut threadsafe."]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append the characters in the given string."]},"$nm":"append"},"appendSpace":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a space character."]},"$nm":"appendSpace"},"delete":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Deletes the specified number of characters from the\ncurrent content, starting at the specified position.\nIf the position is beyond the end of the current \ncontent, nothing is deleted. If the number of \ncharacters to delete is greater than the available \ncharacters from the given position, the content is \ntruncated at the given position."]},"$nm":"delete"},"reset":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Remove all content and return to initial state."]},"$nm":"reset"},"insertCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a `Character` at the specified position. If \nthe position is beyond the end of the current string, \nthe new content is simply appended to the current \ncontent. If the position is a negative number, the \nnew content is inserted at index 0."]},"$nm":"insertCharacter"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the characters in the given strings."]},"$nm":"appendAll"},"insert":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a `String` at the specified position. If the \nposition is beyond the end of the current string, \n    the new content is simply appended to the current \n    content. If the position is a negative number, the \n    new content is inserted at index 0."]},"$nm":"insert"},"appendNewline":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a newline character."]},"$nm":"appendNewline"},"appendCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the given character."]},"$nm":"appendCharacter"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting string. If no characters have been\nappended, the empty string."],"actual":[]},"$nm":"string"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the size of the current content."]},"$nm":"size"}},"$nm":"StringBuilder"},"emptyOrSingleton":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Singleton","Empty"],"doc":["A `Singleton` if the given element is non-null, otherwise `Empty`."]},"$nm":"emptyOrSingleton"},"shared":{"$t":{"$pk":"ceylon.language","$nm":"Shared"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a type or member as shared. A `shared` \nmember is visible outside the block of code in which it is \ndeclared."]},"$nm":"shared"},"Actual":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Actual"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[actual]]."]},"$nm":"Actual"},"Binary":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Binary"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of numeric types like `Integer` that may be \nrepresented as a sequence of bits, and may be the subject\nof bitwise operations."],"by":["Stef"]},"$m":{"clear":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Returns a new number with the given bit set to 0.\nBits are indexed from right to left."]},"$nm":"clear"},"xor":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical exclusive OR operation."]},"$nm":"xor"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Retrieves a given bit from this bit sequence. Bits are indexed from\nright to left."]},"$nm":"get"},"leftLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a left logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"leftLogicalShift"},"set":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"bit"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit set to the given value.\nBits are indexed from right to left."]},"$nm":"set"},"or":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical inclusive OR operation."]},"$nm":"or"},"rightArithmeticShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right arithmetic shift. Sign is preserved. Padded with zeros."]},"$nm":"rightArithmeticShift"},"rightLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"rightLogicalShift"},"flip":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit flipped to its opposite value.\nBits are indexed from right to left."]},"$nm":"flip"},"and":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical AND operation."]},"$nm":"and"}},"$at":{"not":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The binary complement of this sequence of bits."]},"$nm":"not"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The number of bits (0 or 1) that this sequence of bits can hold."]},"$nm":"size"}},"$nm":"Binary","$st":"Other"},"commaList":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$nm":"commaList"},"Number":{"$mt":"ifc","$an":{"shared":[],"see":["Numeric"],"doc":["Abstraction of numbers. Numeric operations are provided\nby the subtype `Numeric`. This type defines operations\nwhich can be expressed without reference to the self\ntype `Other` of `Numeric`."],"by":["Gavin"]},"$at":{"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this number. Returns `1` if the number \nis positive, `-1` if it is negative, or `0` if it \nis zero."]},"$nm":"sign"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas an `Integer`"],"doc":["The number, represented as an `Integer`, after \ntruncation of any fractional part."]},"$nm":"integer"},"magnitude":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of the number."]},"$nm":"magnitude"},"wholePart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part."]},"$nm":"wholePart"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is negative."]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is positive."]},"$nm":"positive"},"fractionalPart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part."]},"$nm":"fractionalPart"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas a `Float`"],"doc":["The number, represented as a `Float`."]},"$nm":"float"}},"$nm":"Number"},"Map":{"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Entry","forKey","forItem","byItem","byKey"],"doc":["Represents a collection which maps _keys_ to _items_,\nwhere a key can map to at most one item. Each such \nmapping may be represented by an `Entry`.\n\nA `Map` is a `Collection` of its `Entry`s, and a \n`Correspondence` from keys to items.\n\nThe presence of an entry in a map may be tested\nusing the `in` operator:\n\n    if (\"lang\"->\"en_AU\" in settings) { ... }\n\nThe entries of the map may be iterated using `for`:\n\n    for (key->item in settings) { ... }\n\nThe item for a key may be obtained using the item\noperator:\n\n    String lang = settings[\"lang\"] else \"en_US\";"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Map`s are considered equal iff they have the \nsame _entry sets_. The entry set of a `Map` is the\nset of `Entry`s belonging to the map. Therefore, the\nmaps are equal iff they have same set of `keys`, and \nfor every key in the key set, the maps have equal\nitems."],"actual":[]},"$nm":"equals"},"mapItems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"},{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The function that transforms a key\/item\npair, producing the item of the resulting\nmap."]},"$nm":"mapping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["Returns a `Map` with the same keys as this map. For\nevery key, the item is the result of applying the\ngiven transformation function."]},"$nm":"mapItems"}},"$at":{"values":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Collection"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns all the values stored in this `Map`. An \nelement can be stored under more than one key in \nthe map, and so it can be contained more than once \nin the resulting collection."]},"$nm":"values"},"inverse":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns a `Map` in which every key is an `Item` in \nthis map, and every value is the set of keys that \nstored the `Item` in this map."]},"$nm":"inverse"},"keys":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the set of keys contained in this `Map`."],"actual":[]},"$nm":"keys"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Map"},"LazyMap":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"entries"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A `Map` implementation that wraps an `Iterable` of \nentries. All operations, such as lookups, size, etc. \nare performed on the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"LazyMap"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"entries"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazyMap"},"Numeric":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Invertable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float","Comparable"],"doc":["Abstraction of numeric types supporting addition,\nsubtraction, multiplication, and division, including\n`Integer` and `Float`. Additionally, a numeric type \nis expected to define a total order via an \nimplementation of `Comparable`."],"by":["Gavin"]},"$m":{"minus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The difference between this number and the given \nnumber."]},"$nm":"minus"},"times":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The product of this number and the given number."]},"$nm":"times"},"divided":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Integral"],"doc":["The quotient obtained by dividing this number by \nthe given number. For integral numeric types, this \noperation results in a remainder."]},"$nm":"divided"}},"$nm":"Numeric","$st":"Other"},"throws":{"$t":{"$pk":"ceylon.language","$nm":"ThrownException"},"$ps":[[{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"when"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a program element that throws an \nexception."]},"$nm":"throws"},"Closeable":{"$mt":"ifc","$an":{"shared":[],"doc":["Abstract supertype of types which may appear\nas the expression type of a resource expression\nin a `try` statement."]},"$m":{"open":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called before entry to a `try` block."]},"$nm":"open"},"close":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called after completion of a `try` block."]},"$nm":"close"}},"$nm":"Closeable"},"parseDigit":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"digit"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"radix"}]],"$mt":"mthd","$nm":"parseDigit"},"Tags":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"shared":[]},"$nm":"tags"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Tags"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[tagged]]."]},"$at":{"tags":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[]},"$nm":"tags"}},"$nm":"Tags"},"formal":{"$t":{"$pk":"ceylon.language","$nm":"Formal"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation must be \nprovided by subtypes."]},"$nm":"formal"},"byDecreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byIncreasing"],"doc":["A comparator which orders elements in decreasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byDecreasing"},"default":{"$t":{"$pk":"ceylon.language","$nm":"Default"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation may be \nrefined by subtypes. Non-`default` declarations may not be \nrefined."]},"$nm":"default"},"minRadix":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"minRadix"},"String":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"characters"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"doc":["A string of characters. Each character in the string is\na 32-bit Unicode character. The internal UTF-16 encoding \nis hidden from clients.\n\nA string is a `Category` of its `Character`s, and of its \nsubstrings:\n\n    'w' in greeting \n    \"hello\" in greeting\n\nStrings are summable:\n\n    String greeting = \"hello\" + \" \" + \"world\";\n\nThey are efficiently iterable:\n\n  for (char in \"hello world\") { ... }\n\nThey are `List`s of `Character`s:\n\n    value char = \"hello world\"[5];\n\nThey are ranged:\n\n    String who = \"hello world\"[6...];\n\nNote that since `string[index]` evaluates to the\noptional type `Character?`, it is often more convenient\nto write `string[index..index]`, which evaluates to a\n`String` containing a single character, or to the empty\nstring `\"\"` if `index` refers to a position outside the\nstring.\n\nIt is easy to use comprehensions to transform strings:\n\n    String { for (s in \"hello world\") if (s.letter) s.uppercased }\n\nSince a `String` has an underlying UTF-16 encoding, \ncertain operations are expensive, requiring iteration of \nthe characters of the string. In particular, `size`\nrequires iteration of the whole string, and `get()`,\n`span()`, and `segment()` require iteration from the \nbeginning of the string to the given index."],"by":["Gavin"]},"$m":{"firstCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The first index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string."]},"$nm":"firstCharacterOccurrence"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the characters of this string beginning at \nthe given index, returning a string no longer than \nthe given length. If the portion of this string\nstarting at the given index is shorter than \nthe given length, return the portion of this string\nfrom the given index until the end of this string. \nOtherwise return a string of the given length. If \nthe start index is larger than the last index of the \nstring, return the empty string."],"actual":[]},"$nm":"segment"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["size"],"doc":["Determines if this string is longer than the given\nlength. This is a more efficient operation than\n`string.size>length`."],"actual":[]},"$nm":"longerThan"},"trimLeading":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["This string, after discarding the given \ncharacters from the beginning of the string"],"actual":[]},"$nm":"trimLeading"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determines if the given object is a `String` and, \nif so, if it occurs as a substring of this string,\nor if the object is a `Character` that occurs in\nthis string. That is to say, a string is considered \na `Category` of its substrings and of its \ncharacters."],"actual":[]},"$nm":"contains"},"repeat":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns a string formed by repeating this string\nthe given number of times."]},"$nm":"repeat"},"firstOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The first index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string."]},"$nm":"firstOccurrence"},"terminal":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the last characters of the string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."],"actual":[]},"$nm":"terminal"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"initial":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the first characters of this string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."],"actual":[]},"$nm":"initial"},"occurrences":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The character indexes at which the given substring\noccurs within this string. Occurrences do not \noverlap."]},"$nm":"occurrences"},"trim":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["This string, after discarding the given \ncharacters from the beginning and end \nof the string"],"actual":[]},"$nm":"trim"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"plus":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns the concatenation of this string with the\ngiven string."],"actual":[]},"$nm":"plus"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["An iterator for the characters of the string."],"actual":[]},"$nm":"iterator"},"startsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determines if this string starts with the given \nsubstring."]},"$nm":"startsWith"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns the character at the given index in the \nstring, or `null` if the index is past the end of\nstring. The first character in the string occurs at\nindex zero. The last character in the string occurs\nat index `string.size-1`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determines if the given object is a string, and if\nso, if this string has the same length, and the \nsame characters, in the same order, as the given \nstring."],"actual":[]},"$nm":"equals"},"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Compare this string with the given string \nlexicographically, according to the Unicode values\nof the characters."],"actual":[]},"$nm":"compare"},"lastCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The last index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string."]},"$nm":"lastCharacterOccurrence"},"join":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Join the given strings, using this string as a \nseparator."]},"$nm":"join"},"replace":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"replacement"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns a string formed by replacing every \noccurrence in this string of the given substring\nwith the given replacement string, working from \nthe start of this string to the end."]},"$nm":"replace"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["size"],"doc":["Determines if this string is shorter than the given\nlength. This is a more efficient operation than\n`string.size>length`."],"actual":[]},"$nm":"shorterThan"},"trimTrailing":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["This string, after discarding the given \ncharacters from the end of the string"],"actual":[]},"$nm":"trimTrailing"},"lastOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The last index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string."]},"$nm":"lastOccurrence"},"split":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"ch"}]],"$mt":"prm","$pt":"f","$def":"1","$an":{"doc":["A predicate that determines if a character\nis a separator characters at which to split.\nDefault to split at any \n[[whitespace|Character.whitespace]] character."]},"$nm":"splitting"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["Specifies that the separator characters\noccurring in the string should be discarded.\nIf `false`, they will be included in the\nresulting iterator."]},"$nm":"discardSeparators"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["Specifies that the separator tokens should \nbe grouped eagerly and not be treated as \nsingle-character tokens. If `false` each \nseparator token will be of size `1`."]},"$nm":"groupSeparators"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Split the string into tokens, using the given\npredicate to determine which characters are \nseparator characters."]},"$nm":"split"},"endsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determines if this string ends with the given \nsubstring."]},"$nm":"endsWith"},"span":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the characters between the given indexes.\nIf the start index is the same as the end index,\nreturn a string with a single character.\nIf the start index is larger than the end index, \nreturn the characters in the reverse order from\nthe order in which they appear in this string.\nIf both the start index and the end index are \nlarger than the last index in the string, return \nthe empty string. Otherwise, if the last index is \nlarger than the last index in the sequence, return\nall characters from the start index to last \ncharacter of the string."],"actual":[]},"$nm":"span"}},"$at":{"normalized":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, after collapsing strings of \n[[whitespace|Character.whitespace]]\ninto single space characters and discarding whitespace \nfrom the beginning and end of the string."]},"$nm":"normalized"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, with all characters in lowercase."]},"$nm":"lowercased"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"hash"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, with all characters in uppercase."]},"$nm":"uppercased"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this string."],"actual":[]},"$nm":"coalesced"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"see":["longerThan","shorterThan"],"doc":["The length of the string (the number of characters\nit contains). In the case of the empty string, the\nstring has length zero. Note that this operation is\npotentially costly for long strings, since the\nunderlying representation of the characters uses a\nUTF-16 encoding."],"actual":[]},"$nm":"size"},"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the last character in the string, or\n`null` if the string has no characters. Note that \nthis operation is potentially costly for long \nstrings, since the underlying representation of the \ncharacters uses a UTF-16 encoding."],"actual":[]},"$nm":"lastIndex"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the string itself."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"see":["size"],"doc":["Determines if this string has no characters, that\nis, if it has zero `size`. This is a more efficient \noperation than `string.size==0`."],"actual":[]},"$nm":"empty"},"lines":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"native":[],"doc":["Split the string into lines of text."]},"$nm":"lines"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The rest of the string, without the first element."],"actual":[]},"$nm":"rest"},"trimmed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"native":[],"doc":["This string, after discarding \n[[whitespace|Character.whitespace]] from the \nbeginning and end of the string."]},"$nm":"trimmed"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, with the characters in reverse order."],"actual":[]},"$nm":"reversed"},"characters":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[]},"$nm":"characters"}},"$nm":"String"},"identical":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$an":{"doc":["An object with well-defined identity."]},"$nm":"x"},{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$an":{"doc":["A second object with well-defined identity."]},"$nm":"y"}]],"$mt":"mthd","$an":{"shared":[],"see":["identityHash"],"doc":["Determine if the arguments are identical. Equivalent to\n`x===y`. Only instances of `Identifiable` have \nwell-defined identity."]},"$nm":"identical"},"late":{"$t":{"$pk":"ceylon.language","$nm":"Late"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to disable definite initialization analysis for \na reference."]},"$nm":"late"},"integerRangeByIterable":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"prm","$pt":"v","$nm":"range"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"native":[],"doc":["Provides an optimized implementation of `Range<Integer>.iterator`. \nThis is necessary because we need reified generics in order to write \nthe optimized version in pure Ceylon."]},"$nm":"integerRangeByIterable"},"emptyIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$an":{"shared":[],"doc":["An iterator that returns no elements."]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$nm":"emptyIterator"},"Anything":{"abstract":"1","of":[{"$pk":"ceylon.language","$nm":"Object"},{"$pk":"ceylon.language","$nm":"Null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The abstract supertype of all types. A value of type \n`Anything` may be a definite value of type `Object`, or \nit may be the `null` value. A method declared `void` is \nconsidered to have the return type `Anything`.\n\nNote that the type `Nothing`, representing the \nintersection of all types, is a subtype of all types."],"by":["Gavin"]},"$nm":"Anything"},"parseFloat":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Float"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Float` value of the given string representation of \na decimal number or `null` if the string does not \nrepresent a decimal number.\n\nThe syntax accepted by this method is the same as the \nsyntax for a `Float` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`)."]},"$nm":"parseFloat"},"Optional":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Optional"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Import"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[optional]]."]},"$nm":"Optional"},"Exception":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["A description of the problem."]},"$nm":"description"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$an":{"shared":[],"doc":["The underlying cause of this exception."]},"$nm":"cause"}],"$mt":"cls","$an":{"shared":[],"native":[],"doc":["The supertype of all exceptions. A subclass represents\na more specific kind of problem, and may define \nadditional attributes which propagate information about\nproblems of that kind."],"by":["Gavin","Tom"]},"$m":{"printStackTrace":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print the stack trace to the standard error of\nthe virtual machine process."]},"$nm":"printStackTrace"}},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["cause"],"doc":["A message describing the problem. This default \nimplementation returns the description, if any, or \notherwise the message of the cause, if any."]},"$nm":"message"},"cause":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"attr","$an":{"shared":[],"doc":["The underlying cause of this exception."]},"$nm":"cause"},"description":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"attr","$an":{"doc":["A description of the problem."]},"$nm":"description"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"}},"$nm":"Exception"},"Doc":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"description"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Doc"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[]},"$at":{"description":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"description"}},"$nm":"Doc"},"internalSort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"native":[]},"$nm":"internalSort"},"OverflowException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a mathematical operation caused a number to \noverflow from its bounds."]},"$nm":"OverflowException"},"Late":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Late"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[late]]."]},"$nm":"Late"},"parseInteger":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$nm":"radix"}]],"$mt":"mthd","$an":{"shared":[],"throws":["AssertionException","if `radix` is not between `minRadix` and `maxRadix`"],"doc":["The `Integer` value of the given string representation \nof an integer, or `null` if the string does not represent \nan integer or if the mathematical integer it represents \nis too large in magnitude to be represented by an \n`Integer`.\n\nThe syntax accepted by this function is the same as the \nsyntax for an `Integer` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`).\n\nA radix can be given in input to specify what is the base\nto take in consideration for the parsing. radix has to be\nbetween `minRadix` and `maxRadix` included.\nThe list of available digits starts from `0` to `9` followed\nby `a` to `z`.\nWhen parsing in a specific base, the first `radix` digits\nfrom the available digits list can be used.\nThis function is not case sensitive; `a` and `A` both\ncorrespond to the `a` digit which decimal value is `10`.\n \n`_` character can be used to separate groups of digits\nfor bases 2, 10 and 16 as for `Integer` literal in the\nCeylon language. For any other bases, no grouping is\nsupported."]},"$nm":"parseInteger"},"sum":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["product"],"doc":["Given a nonempty stream of `Summable` values, return \nthe sum of the values."]},"$nm":"sum"},"Authors":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"shared":[]},"$nm":"authors"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Authors"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[by]]."]},"$at":{"authors":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[]},"$nm":"authors"}},"$nm":"Authors"},"smaller":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is smaller than the given value."],"by":["Gavin"]},"$nm":"smaller"},"infinity":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"doc":["An instance of `Float` representing positive infinity \n∞."]},"$nm":"infinity"},"computeDigitGroupingSize":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"radix"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"digitIndex"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"ii"}]],"$mt":"mthd","$nm":"computeDigitGroupingSize"},"Iterable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"see":["Collection"],"doc":["Abstract supertype of containers whose elements may be \niterated. An iterable container need not be finite, but\nits elements must at least be countable. There may not\nbe a well-defined iteration order, and so the order of\niterated elements may not be stable.\n\nThe type `Iterable<Element,Null>`, usually abbreviated\n`{Element*}` represents a possibly-empty iterable \ncontainer. The type `Iterable<Element,Nothing>`, \nusually abbreviated `{Element+}` represents a nonempty \niterable container.\n\nAn instance of `Iterable` may be constructed by \nsurrounding a value list in braces:\n\n    {String+} words = { \"hello\", \"world\" };\n\nAn instance of `Iterable` may be iterated using a `for`\nloop:\n\n    for (c in \"hello world\") { ... }\n\n`Iterable` and its subtypes define various operations\nthat return other iterable objects. Such operations \ncome in two flavors:\n\n- _Lazy_ operations return a *view* of the receiving\n  iterable object. If the underlying iterable object is\n  mutable, then changes to the underlying object will\n  be reflected in the resulting view. Lazy operations\n  are usually efficient, avoiding memory allocation or\n  iteration of the receiving iterable object.\n- _Eager_ operations return an immutable object. If the\n  receiving iterable object is mutable, changes to this\n  object will not be reflected in the resulting \n  immutable object. Eager operations are often \n  expensive, involving memory allocation and iteration\n  of the receiving iterable object.\n\nLazy operations are preferred, because they can be \nefficiently chained. For example:\n\n    string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased)\n\nis much less expensive than:\n\n    string.select((Character c) => c.letter)\n          .collect((Character c) => c.uppercased)\n\nFurthermore, it is always easy to produce a new \nimmutable iterable object given the view produced by a\nlazy operation. For example:\n\n    [ *string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased) ]\n\nLazy operations normally return an instance of \n`Iterable` or `Map`.\n\nHowever, there are certain scenarios where an eager \noperation is more useful, more convenient, or no more \nexpensive than a lazy operation, including:\n\n- sorting operations, which are eager by nature,\n- operations which preserve emptiness\/nonemptiness of\n  the receiving iterable object.\n\nEager operations normally return a sequence."],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["An iterator for the elements belonging to this \ncontainer."]},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The function comparing pairs of elements."]},"$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["byIncreasing","byDecreasing"],"doc":["A sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\n\nFor convenience, the functions `byIncreasing()` \nand `byDecreasing()` produce a suitable \ncomparison function:\n\n    \"Hello World!\".sort(byIncreasing((Character c) => c.lowercased))\n\nThis operation is eager by nature."]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate satisfied by the elements to\nbe counted."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return the number of elements in this `Iterable` \nthat satisfy the predicate function."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"doc":["The predicate satisfied by the elements to\nbe counted."]},"$nm":"selecting"}},"$nm":"count"},"takingWhile":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"take":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"take"}},"$nm":"takingWhile","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"alive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"alive"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"select":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the elements must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Iterable.filter"],"doc":["A sequence containing the elements of this \ncontainer that satisfy the given predicate. An \neager counterpart to `filter()`."]},"$nm":"select"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Iterable.size"],"doc":["Determines if this iterable object has more elements\nthan the given length. This is an efficient operation \nfor iterable objects with many elements."]},"$nm":"longerThan"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"throws":["Exception","if the given step size is nonpositive, \ni.e. `step<1`"],"doc":["Produce an `Iterable` containing every `step`th \nelement of this iterable object. If the step size \nis `1`, the `Iterable` contains the same elements \nas this iterable object. The step size must be \ngreater than zero. The expression\n\n    (0..10).by(3)\n\nresults in an iterable object with the elements\n`0`, `3`, `6`, and `9` in that order."]},"$nm":"by","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate that all elements must \nsatisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if all elements satisfy the predicate\nfunction."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"mthd","$an":{"doc":["The predicate that all elements must \nsatisfy."]},"$nm":"selecting"}},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"initial"},{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The accumulating function that accepts an\nintermediate result, and the next element."]},"$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["The result of applying the accumulating function to \neach element of this container in turn."]},"$m":{"accumulating":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$an":{"doc":["The accumulating function that accepts an\nintermediate result, and the next element."]},"$nm":"accumulating"}},"$nm":"fold"},"defaultNullElements":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"comp":"i","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$nm":"Default"}]},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Default"},"$mt":"prm","$pt":"v","$an":{"doc":["A default value that replaces `null` elements."]},"$nm":"defaultValue"}]],"$mt":"mthd","$tp":[{"$nm":"Default"}],"$an":{"shared":[],"default":[],"doc":["An `Iterable` that produces the elements of this \niterable object, replacing every `null` element \nwith the given default value. The resulting iterable\nobject does not produce the value `null`."]},"$nm":"defaultNullElements"},"taking":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the first `take`\nelements of this iterable object. If the specified \nnumber of elements is larger than the number of \nelements of this iterable object, the `Iterable` \ncontains the same elements as this iterable object."]},"$nm":"taking","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"i":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"i"}},"$nm":"iterator"}}}},"$at":{"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"}},"$nm":"iterable"}}},"following":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"head"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["An `Iterable` with the given inital element followed \nby the elements of this iterable object."]},"$nm":"following","$o":{"cons":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"first":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"first"}},"$nm":"iterator"}}}},"$nm":"cons"}}},"chain":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Absent"},{"$nm":"OtherAbsent"}]}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"OtherAbsent"}],"$an":{"shared":[],"default":[],"doc":["The elements of this iterable object, in their\noriginal order, followed by the elements of the \ngiven iterable object also in their original order."]},"$nm":"chain","$o":{"chained":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Absent"},{"$nm":"OtherAbsent"}]}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"}},"$nm":"chained"}}},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Iterable.size"],"doc":["Determines if this iterable object has fewer elements\nthan the given length. This is an efficient operation \nfor iterable objects with many elements."]},"$nm":"shorterThan"},"skippingWhile":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"skip":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"skip"}},"$nm":"skippingWhile","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"first":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"first"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate that at least one element \nmust satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if at least one element satisfies the\npredicate function."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"mthd","$an":{"doc":["The predicate that at least one element \nmust satisfy."]},"$nm":"selecting"}},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The mapping to apply to the elements."]},"$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["Iterable.collect"],"doc":["An `Iterable` containing the results of applying\nthe given mapping to the elements of to this \ncontainer."]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The last element which satisfies the given\npredicate, if any, or `null` otherwise."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}},"$nm":"findLast"},"skipping":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the elements of\nthis iterable object, after skipping the first \n`skip` elements. If this iterable object does not \ncontain more elements than the specified number of \nelements, the `Iterable` contains no elements."]},"$nm":"skipping","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"}},"$nm":"iterable"}}},"filter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the elements must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Iterable.select"],"doc":["An `Iterable` containing the elements of this \ncontainer that satisfy the given predicate."]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The first element which satisfies the given \npredicate, if any, or `null` otherwise."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}},"$nm":"find"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The transformation applied to the elements."]},"$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["Iterable.map"],"doc":["A sequence containing the results of applying the\ngiven mapping to the elements of this container. An \neager counterpart to `map()`."]},"$nm":"collect"}},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The last element returned by the iterator, if any.\nIterables are potentially infinite, so calling this\nmight never return; also, this implementation will\niterate through all the elements, which might be\nvery time-consuming."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["All entries of form `index->element` where `index` \nis the position at which `element` occurs, for every\nnon-null element of this `Iterable`, ordered by\nincreasing `index`. For a null element at a given\nposition in the original `Iterable`, there is no \nentry with the corresponding index in the resulting \niterable object. The expression \n\n    { \"hello\", null, \"world\" }.indexed\n    \nresults in an iterable object with the entries\n`0->\"hello\"` and `2->\"world\"`."]},"$nm":"indexed","$o":{"indexes":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"iter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$nm":"iter"},"i":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"i"}},"$nm":"iterator"}}}},"$at":{"orig":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$nm":"orig"}},"$nm":"indexes"}}},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A sequence containing the elements returned by the\niterator."]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this iterable is empty. If the number of items\nis very large only a certain amount of them might\nbe shown followed by \"...\"."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The non-null elements of this `Iterable`, in their\noriginal order. For null elements of the original \n`Iterable`, there is no entry in the resulting \niterable object."]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determines if the iterable object is empty, that is\nto say, if the iterator returns no elements."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns an iterable object containing all but the \nfirst element of this container."]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The first element returned by the iterator, if any.\nThis should always produce the same value as\n`iterable.iterator().head`."],"actual":[]},"$nm":"first"},"repeated":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[]},"$nm":"repeated","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"iter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"iter"}},"$nm":"iterator"}}}},"$at":{"orig":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$nm":"orig"}},"$nm":"iterable"}}},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[]},"$nm":"size"}},"$nm":"Iterable"},"flatten":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$nm":"Args"},"$mt":"prm","$pt":"v","$nm":"tuple"}]],"$mt":"prm","$pt":"f","$nm":"tupleFunction"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"flatten"},"LazyList":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of `List` that wraps an `Iterable` of\nelements. All operations on this `List` are performed on \nthe `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `List` with the elements of this `List` \nin reverse order. This operation will create copy \nthe elements to a new `List`, so changes to the \noriginal `Iterable` will no longer be reflected in \nthe new `List`."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"elems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"elems"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"first"}},"$nm":"LazyList"},"className":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"obj"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Return the name of the concrete class of the given \nobject, in a format native to the virtual machine."]},"$nm":"className"},"List":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Sequence","Empty","Array"],"doc":["Represents a collection in which every element has a \nunique non-negative integer index.\n\nA `List` is a `Collection` of its elements, and a \n`Correspondence` from indices to elements.\n\nDirect access to a list element by index produces a\nvalue of optional type. The following idiom may be\nused instead of upfront bounds-checking, as long as \nthe list element type is a non-`null` type:\n\n    value char = \"hello world\"[index];\n    if (exists char) { \/*do something*\/ }\n    else { \/*out of bounds*\/ }\n\nTo iterate the indexes of a `List`, use the following\nidiom:\n\n    for (i->char in \"hello world\".indexed) { ... }"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator","$o":{"listIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"index":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"index"}},"$nm":"listIterator"}}},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the element of this sequence with the given\n    index, or `null` if the given index is past the end\n    of the sequence, that is, if\n    `index>sequence.lastIndex`. The first element of\n    the sequence has index `0`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `List`s are considered equal iff they have the \nsame `size` and _entry sets_. The entry set of a \nlist `l` is the set of elements of `l.indexed`. \nThis definition is equivalent to the more intuitive \nnotion that two lists are equal iff they have the \nsame `size` and for every index either:\n\n- the lists both have the element `null`, or\n- the lists both have a non-null element, and the\n  two elements are equal."],"actual":[]},"$nm":"equals"},"indexes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the indexed elements must satisfy"]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The indexes in this list for which the element \nsatisfies the given predicate."]},"$nm":"indexes"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"longerThan"},"trimLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"trimming":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"trimming"}},"$nm":"trimLeading"},"occursAt":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$an":{"doc":["The index at which this list might occur"]},"$nm":"index"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"list"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determine if this list occurs at the given index \nin the given list."]},"$nm":"occursAt"},"occursIn":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"list"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determine if this list occurs as a sublist of the \ngiven list."]},"$nm":"occursIn"},"occurrencesIn":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"list"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The indexes in the given list at which this list \noccurs."]},"$nm":"occurrencesIn"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if the given index refers to an element\n    of this sequence, that is, if\n    `index<=sequence.lastIndex`."],"actual":[]},"$nm":"defines"},"withTrailing":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$an":{"doc":["The last element of the resulting sequence."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["Returns a new `List` that contains the specified\nelement appended to the end of the elements of this \n`List`."]},"$nm":"withTrailing"},"terminal":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Select the last elements of the list, returning a list \nno longer than the given length. If this list is \nshorter than the given length, return this list. \nOtherwise return a list of the given length."]},"$nm":"terminal"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"shorterThan"},"initial":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Select the first elements of this list, returning a \nlist no longer than the given length. If this list is \nshorter than the given length, return this list. \nOtherwise return a list of the given length."]},"$nm":"initial"},"trimTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"trimming":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"trimming"}},"$nm":"trimTrailing"},"occursAtStart":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"list"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determine if this list occurs at the start of the \ngiven list."]},"$nm":"occursAtStart"},"trim":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"trimming":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"trimming"}},"$nm":"trim"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"selecting"}},"$nm":"findLast"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$an":{"doc":["The first element of the resulting sequence."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"see":["following"],"doc":["Returns a new `List` that starts with the specified\nelement, followed by the elements of this `List`."]},"$nm":"withLeading"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["List.size"],"doc":["The index of the last element of the list, or\nnull if the list is empty."]},"$nm":"lastIndex"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the last element of this `List`, if any."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this list, returning a new list."]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the list, without the first element."],"actual":[]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the first element of this `List`, if any."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["List.lastIndex"],"doc":["The number of elements in this sequence, always\n`sequence.lastIndex+1`."],"actual":[]},"$nm":"size"}},"$nm":"List"},"Container":{"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"see":["Category"],"doc":["Abstract supertype of objects which may or may not\ncontain one of more other values, called *elements*,\nand provide an operation for accessing the first \nelement, if any. A container which may or may not be \nempty is a `Container<Element,Null>`. A container which \nis always empty is a `Container<Nothing,Null>`. A \ncontainer which is never empty is a \n`Container<Element,Nothing>`."],"by":["Gavin"]},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element. Should produce `null` if the\ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`."]},"$nm":"last"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the container is empty, that is, if\nit has no elements."]},"$nm":"empty"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element. Should produce `null` if the \ncontainer is empty, that is, for any instance for\n    which `empty` evaluates to `true`."]},"$nm":"first"}},"$nm":"Container"},"abstract":{"$t":{"$pk":"ceylon.language","$nm":"Abstract"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as abstract. An `abstract` class \nmay not be directly instantiated. An `abstract` class may \nhave enumerated cases."]},"$nm":"abstract"},"zip":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"items"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"doc":["Given two sequences, form a new sequence consisting of\nall entries where, for any given index in the resulting\nsequence, the key of the entry is the element occurring \nat the same index in the first sequence, and the item \nis the element occurring at the same index in the second \nsequence. The length of the resulting sequence is the \nlength of the shorter of the two given sequences. \n\nThus:\n\n    zip(xs,ys)[i]==xs[i]->ys[i]\n\nfor every `0<=i<min({xs.size,ys.size})`."]},"$nm":"zip"},"Shared":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Shared"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"Package"},{"$pk":"ceylon.language.model.declaration","$nm":"Import"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[shared]]."]},"$nm":"Shared"},"Deprecation":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"description"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Deprecation"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[deprecated]]."]},"$at":{"reason":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"gttr","$an":{"shared":[]},"$nm":"reason"},"description":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"description"}},"$nm":"Deprecation"},"equal":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is exactly equal to the given value."],"by":["Gavin"]},"$nm":"equal"},"finished":{"super":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"obj","$an":{"shared":[],"see":["Iterator"],"doc":["A value that indicates that an `Iterator`\nis exhausted and has no more values to \nreturn."]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"finished"},"Boolean":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"true"},{"$pk":"ceylon.language","$nm":"false"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A type capable of representing the values true and\nfalse of Boolean logic."],"by":["Gavin"]},"$nm":"Boolean"},"See":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"shared":[]},"$nm":"programElements"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"See"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[]},"$at":{"programElements":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[]},"$nm":"programElements"}},"$nm":"See"},"print":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print a line to the standard output of the virtual \nmachine process, printing the given value's `string`, \nor `«null»` if the value is `null`.\n\nThis method is a shortcut for:\n\n    process.writeLine(line?.string else \"«null»\")\n\nand is intended mainly for debugging purposes."],"by":["Gavin"]},"$nm":"print"},"ChainedIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"first"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"second"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"variance":"out","$nm":"Other"}],"$an":{"doc":["An `Iterator` that returns the elements of two\n`Iterable`s, as if they were chained together."],"by":["Enrique Zamudio"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"more":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"more"},"second":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"second"},"iter":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"iter"},"first":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"first"}},"$nm":"ChainedIterator"},"Basic":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The default superclass when no superclass is explicitly\nspecified using `extends`. For the sake of convenience, \nthis class inherits a default definition of value\nequality from `Identifiable`."],"by":["Gavin"]},"$nm":"Basic"},"Category":{"$mt":"ifc","$an":{"shared":[],"see":["Container"],"doc":["Abstract supertype of objects that contain other \nvalues, called *elements*, where it is possible to \nefficiently determine if a given value is an element. \n`Category` does not satisfy `Container`, because it is \nconceptually possible to have a `Category` whose \nemptiness cannot be computed.\n\nThe `in` operator may be used to determine if a value\nbelongs to a `Category`:\n\n    if (\"hello\" in \"hello world\") { ... }\n    if (69 in 0..100) { ... }\n    if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n\nOrdinarily, `x==y` implies that `x in cat == y in cat`.\nBut this contract is not required since it is possible\nto form a meaningful `Category` using a different\nequivalence relation. For example, an `IdentitySet` is\na meaningful `Category`."],"by":["Gavin"]},"$m":{"containsAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if any one of the given values belongs \nto this `Category`"]},"$nm":"containsAny"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["containsEvery","containsAny"],"doc":["Determines if the given value belongs to this\n`Category`, that is, if it is an element of this\n`Category`.\n\nFor most `Category`s, if `x==y`, then \n`category.contains(x)` evaluates to the same\nvalue as `category.contains(y)`. However, it is\npossible to form a `Category` consistent with some \nother equivalence relation, for example `===`. \nTherefore implementations of `contains()` which do \nnot satisfy this relationship are tolerated."]},"$nm":"contains"},"containsEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if every one of the given values belongs\nto this `Category`."]},"$nm":"containsEvery"}},"$nm":"Category"},"see":{"$t":{"$pk":"ceylon.language","$nm":"See"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API references to other related \nprogram elements."]},"$nm":"see"},"computeMagnitude":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"radix"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$mt":"prm","$pt":"v","$nm":"char"}]],"$mt":"mthd","$nm":"computeMagnitude"},"NegativeNumberException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a negative number is not allowed."]},"$nm":"NegativeNumberException"},"actual":{"$t":{"$pk":"ceylon.language","$nm":"Actual"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member of a type as refining a member \nof a supertype."]},"$nm":"actual"},"Integer":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"integer"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Integral"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Binary"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"doc":["A 64-bit integer, or the closest approximation to a \n64-bit integer provided by the underlying platform.\n\n- For the JVM runtime, integer values between\n  -2<sup>63<\/sup> and 2<sup>63<\/sup>-1 may be \n  represented without overflow.\n- For the JavaScript runtime, integer values with a\n  magnitude no greater than 2<sup>53<\/sup> may be\n  represented without loss of precision.\n\nOverflow or loss of precision occurs silently (with\nno exception raised)."]},"$at":{"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"integer"},"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The UTF-32 character with this UCS code point."]},"$nm":"character"}},"$nm":"Integer"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"doc":["The first of the given values (usually a comprehension),\nif any."]},"$nm":"first"},"zeroInt":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"zeroInt"}},"ceylon.language.model.declaration":{"Import":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"},{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of an `import` declaration \nwithin a module declaration."]},"$at":{"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The name of the imported module."]},"$nm":"name"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The compile-time version of the imported module."]},"$nm":"version"}},"$nm":"Import"},"AnnotatedDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"Module"},{"$pk":"ceylon.language.model.declaration","$nm":"Package"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"annotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Annotation"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Annotation"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Annotation"}],"$an":{"shared":[],"formal":[],"doc":["The annotation instances of the given \nannotation type on this declaration."]},"$nm":"annotations"}},"$nm":"AnnotatedDeclaration"},"OpenTypeVariable":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"}},"$nm":"OpenTypeVariable"},"OpenIntersection":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"satisfiedTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"satisfiedTypes"}},"$nm":"OpenIntersection"},"OpenType":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"nothingType"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"},{"$pk":"ceylon.language.model.declaration","$nm":"OpenTypeVariable"},{"$pk":"ceylon.language.model.declaration","$nm":"OpenUnion"},{"$pk":"ceylon.language.model.declaration","$nm":"OpenIntersection"}],"$mt":"ifc","$an":{"shared":[]},"$nm":"OpenType"},"SetterDeclaration":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of the setter of a `VariableDeclaration`."]},"$at":{"variable":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"VariableDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The variable this setter is for."]},"$nm":"variable"}},"$nm":"SetterDeclaration"},"Module":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"},{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of a `module` declaration\nfrom a `module.ceylon` compilation unit"]},"$m":{"findPackage":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"Package"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Finds a package by name"]},"$nm":"findPackage"}},"$at":{"dependencies":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Import"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The modules this module depends on."]},"$nm":"dependencies"},"members":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Package"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The package members of the module."]},"$nm":"members"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The version of the module."]},"$nm":"version"}},"$nm":"Module"},"InterfaceDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Interface"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Interface"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"bindAndApply"}},"$nm":"InterfaceDeclaration"},"FunctionalDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$m":{"getParameterDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"getParameterDeclaration"}},"$at":{"parameterDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"parameterDeclarations"}},"$nm":"FunctionalDeclaration"},"TypeParameter":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$mt":"ifc","$an":{"shared":[]},"$nm":"TypeParameter"},"$pkg-shared":"1","GenericDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$m":{"getTypeParameterDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"getTypeParameterDeclaration"}},"$at":{"typeParameterDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeParameterDeclarations"}},"$nm":"GenericDeclaration"},"ParameterDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$at":{"variadic":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"variadic"},"defaulted":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"defaulted"}},"$nm":"ParameterDeclaration"},"VariableDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of an attribute that is `variable` or has an `assign` block."]},"$at":{"setter":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"SetterDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Returns a model of the setter of this variable.\n\nFor modelling purposes `variable` reference \nvalues have a SetterDeclaration even though there is no \nsuch setter explicit in the source code."]},"$nm":"setter"}},"$nm":"VariableDeclaration"},"Package":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"},{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of a `package` declaration \nfrom a `package.ceylon` compilation unit"]},"$m":{"getValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The value with the given name."]},"$nm":"getValue"},"getMember":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Kind"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[],"doc":["Looks up a member of this package by name and type."]},"$nm":"getMember"},"getFunction":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The function with the given name."]},"$nm":"getFunction"},"annotatedMembers":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"},{"$nm":"Annotation"}],"$an":{"shared":[],"formal":[],"doc":["The members of this package having a particular annotation."]},"$nm":"annotatedMembers"},"getClassOrInterface":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The class or interface with the given name."]},"$nm":"getClassOrInterface"},"members":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[],"doc":["The members of this package."]},"$nm":"members"}},"$at":{"container":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Module"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The module this package belongs to."]},"$nm":"container"}},"$nm":"Package"},"TypedDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$at":{"openType":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"openType"}},"$nm":"TypedDeclaration"},"OpenUnion":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"caseTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"caseTypes"}},"$nm":"OpenUnion"},"FunctionDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"GenericDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionalDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Function"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Function"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"bindAndApply"},"memberApply":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$nm":"MethodType"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Method"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"Container"},{"$nm":"MethodType"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Arguments"}],"$an":{"shared":[],"formal":[]},"$nm":"memberApply"}},"$nm":"FunctionDeclaration"},"OpenParameterisedType":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}],"variance":"out","$nm":"DeclarationType"}],"$an":{"shared":[]},"$at":{"superclass":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclass"},"declaration":{"$t":{"$nm":"DeclarationType"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"},"interfaces":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaces"},"typeArguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeArguments"}},"$nm":"OpenParameterisedType"},"Declaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"name"}},"$nm":"Declaration"},"ClassOrInterfaceDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"GenericDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"memberDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"memberDeclarations"},"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"bindAndApply"},"memberApply":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.model","$nm":"Member"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"Container"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"memberApply"},"annotatedMemberDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"},{"$nm":"Annotation"}],"$an":{"shared":[],"formal":[]},"$nm":"annotatedMemberDeclarations"},"getMemberDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Kind"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[],"doc":["Looks up a member of this package by name and type."]},"$nm":"getMemberDeclaration"}},"$at":{"superclassDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclassDeclaration"},"interfaceDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaceDeclarations"}},"$nm":"ClassOrInterfaceDeclaration"},"TopLevelOrMemberDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"TypedDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"toplevel":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"toplevel"},"packageContainer":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Package"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"packageContainer"}},"$nm":"TopLevelOrMemberDeclaration"},"nothingType":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"obj","$an":{"shared":[]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"nothingType"},"ValueDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Value"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$def":"1","$nm":"instance"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"}},"$nm":"ValueDeclaration"},"ClassDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionalDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"bindAndApply"}},"$at":{"anonymous":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"anonymous"}},"$nm":"ClassDeclaration"},"FunctionOrValueDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"variadic":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"variadic"},"defaulted":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"defaulted"}},"$nm":"FunctionOrValueDeclaration"}},"ceylon.language.model":{"sequencedAnnotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":{"$nm":"ProgramElement"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"sequencedAnnotations"},"Member":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Kind"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Type"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"}],"variance":"out","$nm":"Kind"}],"$an":{"shared":[]},"$at":{"declaringClassOrInterface":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaringClassOrInterface"}},"$nm":"Member"},"Model":{"of":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"FunctionModel"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"AttributeModel"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"}},"$nm":"Model"},"InterfaceModel":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"}},"$nm":"InterfaceModel"},"Method":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"FunctionModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Function"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"Method"},"Type":{"$mt":"ifc","$an":{"shared":[]},"$nm":"Type"},"IntersectionType":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"satisfiedTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"satisfiedTypes"}},"$nm":"IntersectionType"},"UnionType":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"caseTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"caseTypes"}},"$nm":"UnionType"},"type":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$ps":[[{"$t":{"$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"instance"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Anything"}],"variance":"out","$nm":"Type"}],"$an":{"shared":[],"native":[]},"$nm":"type"},"$pkg-shared":"1","FunctionModel":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"type":{"$t":{"$pk":"ceylon.language.model","$nm":"Type"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"FunctionModel"},"OptionalAnnotation":{"of":[{"$nm":"Value"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur at most once\nat a single program element."]},"$nm":"OptionalAnnotation","$st":"Value"},"ClassModel":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"}},"$nm":"ClassModel"},"Attribute":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"AttributeModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$nm":"Attribute"},"Annotated":{"$mt":"ifc","$an":{"shared":[],"doc":["A program element that can\nbe annotated."]},"$nm":"Annotated"},"AttributeModel":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"type":{"$t":{"$pk":"ceylon.language.model","$nm":"Type"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"AttributeModel"},"Value":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"AttributeModel"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$m":{"get":{"$t":{"$nm":"Type"},"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"get"}},"$nm":"Value"},"Variable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Value"}],"$mt":"ifc","$tp":[{"$nm":"Type"}],"$an":{"shared":[]},"$m":{"set":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"newValue"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"set"}},"$nm":"Variable"},"Function":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"FunctionModel"},{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"Function"},"typeLiteral":{"$t":{"$pk":"ceylon.language.model","$nm":"Type"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Anything"}],"variance":"out","$nm":"Type"}],"$an":{"shared":[],"native":[]},"$nm":"typeLiteral"},"MemberClass":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"ClassModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Class"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"MemberClass"},"VariableAttribute":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Variable"}],"$pk":"ceylon.language.model","$nm":"Member"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Attribute"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"$nm":"Type"}],"$an":{"shared":[]},"$nm":"VariableAttribute"},"Interface":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"InterfaceModel"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$nm":"Interface"},"SequencedAnnotation":{"of":[{"$nm":"Value"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur multiple times\nat a single program element."]},"$nm":"SequencedAnnotation","$st":"Value"},"optionalAnnotation":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":{"$nm":"ProgramElement"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"optionalAnnotation"},"ConstrainedAnnotation":{"of":[{"$nm":"Value"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"variance":"out","$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation. This interface encodes constraints upon \nthe annotation in its type arguments."]},"$m":{"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language.model","$nm":"Annotated"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Can this annotation can occur on the given program \nelement?"]},"$nm":"occurs"}},"$nm":"ConstrainedAnnotation","$st":"Value"},"Class":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"ClassModel"},{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"Class"},"Annotation":{"of":[{"$nm":"Value"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["An annotation."]},"$nm":"Annotation","$st":"Value"},"nothingType":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"obj","$an":{"shared":[]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"nothingType"},"ClassOrInterface":{"of":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"ClassModel"},{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"InterfaceModel"}],"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"},{"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$m":{"getMethod":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Method"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Arguments"}],"$an":{"shared":[],"formal":[]},"$nm":"getMethod"},"getVariableAttribute":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"VariableAttribute"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"$nm":"Type"}],"$an":{"shared":[],"formal":[]},"$nm":"getVariableAttribute"},"getAttribute":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Attribute"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"$nm":"Type"}],"$an":{"shared":[],"formal":[]},"$nm":"getAttribute"},"getClassOrInterface":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.model","$nm":"Member"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"getClassOrInterface"}},"$at":{"superclass":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclass"},"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"interfaces":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Interface"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaces"},"typeArguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeArguments"}},"$nm":"ClassOrInterface"},"annotations":{"$t":{"$nm":"Values"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":{"$nm":"ProgramElement"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"}],"$nm":"Value"},{"$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[],"native":[]},"$nm":"annotations"},"modules":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[]},"$m":{"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"Module"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"version"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"find"}},"$at":{"default":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"Module"}]},"$mt":"attr","$an":{"shared":[],"native":[]},"$nm":"default"},"list":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Module"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[]},"$nm":"list"}},"$nm":"modules"},"MemberInterface":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"InterfaceModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Interface"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$nm":"MemberInterface"}}};
//Hand-written implementations
function getT$name() {return this.constructor.T$name;}
function getT$all() {return this.constructor.T$all;}
function initType(type, typeName) {
var cons = function() {}
type.$$ = cons;
cons.T$name = typeName;
cons.T$all = {}
cons.T$all[typeName] = type;
for (var i=2; i<arguments.length; ++i) {
var superTypes = arguments[i].$$.T$all;
for (var $ in superTypes) {cons.T$all[$] = superTypes[$]}
}
cons.prototype.getT$name = getT$name;
cons.prototype.getT$all = getT$all;
}
function initTypeProto(type, typeName) {
initType.apply(this, arguments);
var args = [].slice.call(arguments, 2);
args.unshift(type);
inheritProto.apply(this, args);
}
function initExistingType(type, cons, typeName) {
type.$$ = cons;
cons.T$name = typeName;
cons.T$all = {}
cons.T$all[typeName] = type;
for (var i=3; i<arguments.length; ++i) {
var superTypes = arguments[i].$$.T$all;
for (var $ in superTypes) {cons.T$all[$] = superTypes[$]}
}
var proto = cons.prototype;
if (proto !== undefined) {
try {
proto.getT$name = getT$name;
proto.getT$all = getT$all;
} catch (exc) {
// browser probably prevented access to the prototype
}
}
}
function initExistingTypeProto(type, cons, typeName) {
var args = [].slice.call(arguments, 0);
args.push($init$Basic());
initExistingType.apply(this, args);
var proto = cons.prototype;
if ((proto !== undefined) && (proto.getHash === undefined)) {
var origToString = proto.toString;
try {
inheritProto(type, Basic);
proto.toString = origToString;
} catch (exc) {
// browser probably prevented access to the prototype
}
}
}
function inheritProto(type) {
var proto = type.$$.prototype;
for (var i=1; i<arguments.length; ++i) {
var superProto = arguments[i].$$.prototype;
var names = Object.getOwnPropertyNames(superProto);
for (var j=0; j<names.length; ++j) {
var name = names[j];
var desc = Object.getOwnPropertyDescriptor(superProto, name);
// only copy own, enumerable properties
if (desc && desc.enumerable) {
if (desc.get) {
// defined through getter/setter, so copy the definition
Object.defineProperty(proto, name, desc);
} else {
proto[name] = desc.value;
}
}
}
}
}
// Define a property on the given object (which may be a prototype).
// "get" and "set" are getter/setter functions, and the latter is optional.
function defineAttr(obj, name, get, set, metamodel) {
Object.defineProperty(obj, name, {get: get, set: set, configurable: true, enumerable: true});
obj['$prop$'+name] = {get:get, set:set, $$metamodel$$:metamodel};
}
// Create a copy of the given property. The name of the copied property is name+suffix.
// This is used in closure mode to provide access to inherited attribute implementations.
function copySuperAttr(obj, name, suffix) {
var desc;
var o = obj;
// It may be an inherited property, so check the prototype chain.
do {
if ((desc = Object.getOwnPropertyDescriptor(o, name))) {break;}
o = o.__proto__;
} while (o);
if (desc) {
Object.defineProperty(obj, name+suffix, desc);
}
}
// read/writeAttrib return the getter/setter for the given property as defined in the
// given type. This is used in prototype mode to access inherited attribute implementations.
function attrGetter(type, name) {
return Object.getOwnPropertyDescriptor(type.$$.prototype, name).get;
}
function attrSetter(type, name, value) {
return Object.getOwnPropertyDescriptor(type.$$.prototype, name).set;
}
exports.initType=initType;
exports.initTypeProto=initTypeProto;
exports.initExistingType=initExistingType;
exports.initExistingTypeProto=initExistingTypeProto;
exports.inheritProto=inheritProto;
exports.defineAttr=defineAttr;
exports.copySuperAttr=copySuperAttr;
exports.attrGetter=attrGetter;
exports.attrSetter=attrSetter;
function Anything(wat) {
return wat;
}
initType(Anything, 'ceylon.language::Anything');
Anything.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Anything']};
function Null(wat) {
return null;
}
initType(Null, 'ceylon.language::Null', Anything);
Null.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Null']};
function Nothing(wat) {
throw "Nothing";
}
initType(Nothing, 'ceylon.language::Nothing');
Nothing.$$metamodel$$={$ps:[],$an:function(){return[shared()]},mod:$$METAMODEL$$,d:{"$mt":"cls","$an":{"shared":[]},"$nm":"Nothing"}};
function Object$(wat) {
return wat;
}
initTypeProto(Object$, 'ceylon.language::Object', Anything);
Object$.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']};
var Object$proto = Object$.$$.prototype;
defineAttr(Object$proto, 'string', function(){
return String$(className(this) + "@" + this.hash);
},undefined,{$an:function(){return[shared(),$default()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['string']});
Object$proto.toString=function() { return this.string.valueOf(); }
function $init$Object$() { return Object$; }
function $init$Object() { return Object$; }
var BasicID=1;
function $identityHash(x) {
var hash = x.BasicID;
return (hash !== undefined)
? hash : (x.BasicID = BasicID++);
}
function Identifiable(obj) {}
initType(Identifiable, "ceylon.language::Identifiable");
Identifiable.$$metamodel$$={$an:function(){return[shared()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Identifiable']};
function $init$Identifiable() { return Identifiable; }
var Identifiable$proto = Identifiable.$$.prototype;
Identifiable$proto.equals = function(that) {
return isOfType(that, {t:Identifiable}) && (that===this);
}
defineAttr(Identifiable$proto, 'hash', function(){ return $identityHash(this); },
undefined,{$an:function(){return[shared(),$default()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Identifiable']['$at']['hash']});
//INTERFACES
//Compiled from Ceylon sources
function Callable(wat) {
    return wat;
}
Callable.$$metamodel$$={mod:$$METAMODEL$$,$an:function(){return[shared()];},$tp:{Arguments:{'var':'out'},Return:{'var':'out'}},d:$$METAMODEL$$['ceylon.language']['Callable']};
exports.Callable=Callable;
function $init$Callable() {
    if (Callable.$$===undefined) {
        initType(Callable, 'ceylon.language::Callable');
    }
    return Callable;
}
exports.$init$Callable=$init$Callable;
$init$Callable();

function $JsCallable(callable,parms,targs) {
    if (callable.getT$all === undefined) {
        callable.getT$all=Callable.getT$all;
    }
    var set_meta = callable.$$metamodel$$ === undefined;
    if (set_meta) {
        callable.$$metamodel$$={$ps:[],mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Callable']};
        if (parms !== undefined) {
            callable.$$metamodel$$['$ps']=parms;
        }
    }
    if (targs !== undefined && callable.$$targs$$ === undefined) {
        callable.$$targs$$=targs;
        if (set_meta) {
            callable.$$metamodel$$['$t']=targs['Return'];
        }
    }
    return callable;
}
initExistingTypeProto($JsCallable, Function, 'ceylon.language::JsCallable', Callable);

function noop() { return null; }

//This is used for plain method references
function JsCallable(o,f) {
    Callable(o);
    if (o === null) return noop;
    var f2 = function() { return f.apply(o, arguments); };
    f2.$$metamodel$$=f.$$metamodel$$===undefined?Callable.$$metamodel$$:f.$$metamodel$$;
    return f2;
}
JsCallable.$$metamodel$$={$tp:{Return:{'var':'out'}, Arguments:{'var':'in'}},$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Callable']};

//This is used for spread method references
function JsCallableList(value) {
    return function() {
        var rval = Array(value.length);
        for (var i = 0; i < value.length; i++) {
            var c = value[i];
            rval[i] = c.f.apply(c.o, arguments);
        }
        return ArraySequence(rval);
    };
}
JsCallableList.$$metamodel$$={$tp:{Return:{'var':'out'}, Arguments:{'var':'in'}},$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Callable']};

exports.JsCallableList=JsCallableList;
exports.JsCallable=JsCallable;
exports.$JsCallable=$JsCallable;
function Comprehension(makeNextFunc, $$targs$$, compr) {
    $init$Comprehension();
    if (compr===undefined) {compr = new Comprehension.$$;}
    Basic(compr);
    compr.makeNextFunc = makeNextFunc;
    compr.$$targs$$=$$targs$$;
    return compr;
}
Comprehension.$$metamodel$$={$nm:'Comprehension',$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Iterable']};
function $init$Comprehension() {
    if (Comprehension.$$===undefined) {
        initTypeProto(Comprehension, 'ceylon.language::Comprehension', $init$Basic(), $init$Iterable());
    }
    return Comprehension;
}
$init$Comprehension();
var Comprehension$proto = Comprehension.$$.prototype;
Comprehension$proto.iterator = function() {
    return ComprehensionIterator(this.makeNextFunc(), this.$$targs$$);
}
defineAttr(Comprehension$proto, 'sequence', function() {
    var sb = SequenceBuilder(this.$$targs$$);
    sb.appendAll(this);
    return sb.sequence;
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['sequence']});
exports.Comprehension=Comprehension;

function ComprehensionIterator(nextFunc, $$targs$$, it) {
    $init$ComprehensionIterator();
    if (it===undefined) {it = new ComprehensionIterator.$$;}
    it.$$targs$$=$$targs$$;
    Basic(it);
    it.next = nextFunc;
    return it;
}
ComprehensionIterator.$$metamodel$$={$nm:'ComprehensionIterator',$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Iterator']};
function $init$ComprehensionIterator() {
    if (ComprehensionIterator.$$===undefined) {
        initTypeProto(ComprehensionIterator, 'ceylon.language::ComprehensionIterator',
                $init$Basic(), $init$Iterator());
    }
    return ComprehensionIterator;
}
$init$ComprehensionIterator();
function Basic($$basic){
    $init$Basic();
    if ($$basic===undefined)$$basic=new Basic.$$;
    Object$($$basic);
    Identifiable($$basic);
    return $$basic;
}
Basic.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Object$},satisfies:[{t:Identifiable}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),abstract()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Basic']};
exports.Basic=Basic;
function $init$Basic(){
    if (Basic.$$===undefined){
        initTypeProto(Basic,'ceylon.language::Basic',Object$,$init$Identifiable());
    }
    return Basic;
}
exports.$init$Basic=$init$Basic;
$init$Basic();
function Exception(description$1, cause, $$exception){
    $init$Exception();
    if ($$exception===undefined)$$exception=new Exception.$$;
    if(description$1===undefined){description$1=null;}
    $$exception.description$1=description$1;
    if(cause===undefined){cause=null;}
    $$exception.cause=cause;
    $$exception.cause$2_=cause;
    $$exception.description$1_=description$1;
    return $$exception;
}
Exception.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[],$an:function(){return[by([String$("Gavin",5),String$("Tom",3)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),$native()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Exception']};
exports.Exception=Exception;
function $init$Exception(){
    if (Exception.$$===undefined){
        initTypeProto(Exception,'ceylon.language::Exception',Basic);
        (function($$exception){
            defineAttr($$exception,'cause',function(){return this.cause$2_;},undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Exception}]},$cont:Exception,$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Exception']['$at']['cause']});
            defineAttr($$exception,'description$1',function(){return this.description$1_;},undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:String$}]},$cont:Exception,pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Exception']['$at']['description']});
            defineAttr($$exception,'message',function(){
                var $$exception=this;
                return (opt$3=(opt$4=$$exception.description$1,opt$4!==null?opt$4:(opt$5=$$exception.cause,opt$5!==null?opt$5.message:null)),opt$3!==null?opt$3:String$("",0));
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Exception,$an:function(){return[see([typeLiteral$model({Type:{t:$$exception.$prop$cause}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:ValueDeclaration$model$declaration},{t:Attribute$model,a:{Type:{ t:'u', l:[{t:Null},{t:Exception}]},Container:{t:Exception}}}]}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Exception']['$at']['message']});
            defineAttr($$exception,'string',function(){
                var $$exception=this;
                return className($$exception).plus(StringBuilder().appendAll([String$(" \"",2),$$exception.message.string,String$("\"",1)]).string);
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Exception,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Exception']['$at']['string']});
        })(Exception.$$.prototype);
    }
    return Exception;
}
exports.$init$Exception=$init$Exception;
$init$Exception();
var opt$3,opt$4,opt$5;
function Iterable($$targs$$,$$iterable){
    Container($$iterable.$$targs$$===undefined?$$targs$$:{Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element},$$iterable);
    set_type_args($$iterable,$$targs$$);
}
Iterable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out',},Absent:{'var':'out','satisfies':[{t:Null}],'def':{t:Null}}},satisfies:[{t:Container,a:{Absent:'Absent',Element:'Element'}}],$an:function(){return[see([typeLiteral$model({Type:{t:Collection,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']};
exports.Iterable=Iterable;
function $init$Iterable(){
    if (Iterable.$$===undefined){
        initTypeProto(Iterable,'ceylon.language::Iterable',$init$Container());
        (function($$iterable){
            defineAttr($$iterable,'empty',function(){
                var $$iterable=this;
                return isOfType($$iterable.iterator().next(),{t:Finished});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['empty']});
            defineAttr($$iterable,'size',function(){
                var $$iterable=this;
                return $$iterable.count($JsCallable(function (e$6){
                    var $$iterable=this;
                    return true;
                },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}));
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['size']});
            $$iterable.longerThan=function longerThan(length$7){
                var $$iterable=this;
                if(length$7.compare((0)).equals(getSmaller())){
                    return true;
                }
                var count$8=(0);
                var setCount$8=function(count$9){return count$8=count$9;};
                var it$10 = $$iterable.iterator();
                var element$11;while ((element$11=it$10.next())!==getFinished()){
                    if((oldcount$12=count$8,count$8=oldcount$12.successor,oldcount$12).equals(length$7)){
                        return true;
                    }
                    var oldcount$12;
                }
                return false;
            };$$iterable.longerThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[see([typeLiteral$model({Type:{t:Iterable.$$.prototype.$prop$size,a:{Absent:{t:Null},Element:].reifyCeylonType({Absent:{t:Null},Element:{t:ValueDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['longerThan']};$$iterable.shorterThan=function shorterThan(length$13){
                var $$iterable=this;
                if((length$13.compare((0))!==getLarger())){
                    return false;
                }
                var count$14=(0);
                var setCount$14=function(count$15){return count$14=count$15;};
                var it$16 = $$iterable.iterator();
                var element$17;while ((element$17=it$16.next())!==getFinished()){
                    if((count$14=count$14.successor).equals(length$13)){
                        return false;
                    }
                }
                return true;
            };$$iterable.shorterThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[see([typeLiteral$model({Type:{t:Iterable.$$.prototype.$prop$size,a:{Absent:{t:Null},Element:].reifyCeylonType({Absent:{t:Null},Element:{t:ValueDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['shorterThan']};$$iterable.contains=function (element$18){
                var $$iterable=this;
                return $$iterable.any($JsCallable(ifExists($JsCallable((opt$19=element$18,JsCallable(opt$19,opt$19!==null?opt$19.equals:null)),[{$nm:'that',$mt:'prm',$t:{t:Object$}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Return:{t:Boolean$}})),[{$nm:'p1',$mt:'prm',$t:{t:Anything}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Anything},Element:{t:Anything}}},Return:{t:Boolean$}}));
            };
            $$iterable.contains.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['contains']};
            defineAttr($$iterable,'first',function(){
                var $$iterable=this;
                return first($$iterable,{Value:$$iterable.$$targs$$.Element,Absent:$$iterable.$$targs$$.Absent});
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Element']},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['first']});
            defineAttr($$iterable,'last',function(){
                var $$iterable=this;
                var e$20=$$iterable.first;
                var setE$20=function(e$21){return e$20=e$21;};
                var it$22 = $$iterable.iterator();
                var x$23;while ((x$23=it$22.next())!==getFinished()){
                    e$20=x$23;
                }
                return e$20;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Element']},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['last']});defineAttr($$iterable,'rest',function(){
                var $$iterable=this;
                return $$iterable.skipping((1));
            },undefined,{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['rest']});
            defineAttr($$iterable,'sequence',function(){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$24=$$iterable.iterator();
                    var x$25=getFinished();
                    var next$x$25=function(){return x$25=it$24.next();}
                    next$x$25();
                    return function(){
                        if(x$25!==getFinished()){
                            var x$25$26=x$25;
                            var tmpvar$27=x$25$26;
                            next$x$25();
                            return tmpvar$27;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$iterable.$$targs$$.Element}).sequence;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['sequence']});
            $$iterable.$map=function (collecting$28,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$29=$$iterable.iterator();
                    var elem$30=getFinished();
                    var next$elem$30=function(){return elem$30=it$29.next();}
                    next$elem$30();
                    return function(){
                        if(elem$30!==getFinished()){
                            var elem$30$31=elem$30;
                            var tmpvar$32=collecting$28(elem$30$31);
                            next$elem$30();
                            return tmpvar$32;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$$mptypes.Result});
            };
            $$iterable.$map.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Iterable,$tp:{Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['map']};
            $$iterable.$filter=function (selecting$33){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$34=$$iterable.iterator();
                    var elem$35=getFinished();
                    var next$elem$35=function(){
                        while((elem$35=it$34.next())!==getFinished()){
                            if(selecting$33(elem$35)){
                                return elem$35;
                            }
                        }
                        return getFinished();
                    }
                    next$elem$35();
                    return function(){
                        if(elem$35!==getFinished()){
                            var elem$35$36=elem$35;
                            var tmpvar$37=elem$35$36;
                            next$elem$35();
                            return tmpvar$37;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
            };
            $$iterable.$filter.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[see([typeLiteral$model({Type:{t:Iterable.$$.prototype.select,a:{Absent:{t:Null},Element:].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['filter']};
            $$iterable.fold=function fold(initial$38,accumulating$39,$$$mptypes){
                var $$iterable=this;
                var r$40=initial$38;
                var setR$40=function(r$41){return r$40=r$41;};
                var it$42 = $$iterable.iterator();
                var e$43;while ((e$43=it$42.next())!==getFinished()){
                    r$40=accumulating$39(r$40,e$43);
                }
                return r$40;
            };$$iterable.fold.$$metamodel$$={mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$cont:Iterable,$tp:{Result:{}},$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['fold']};$$iterable.find=function find(selecting$44){
                var $$iterable=this;
                var it$45 = $$iterable.iterator();
                var e$46;while ((e$46=it$45.next())!==getFinished()){
                    if(selecting$44(e$46)){
                        return e$46;
                    }
                }
                return null;
            };$$iterable.find.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['find']};$$iterable.findLast=function findLast(selecting$47){
                var $$iterable=this;
                var last$48=null;
                var setLast$48=function(last$49){return last$48=last$49;};
                var it$50 = $$iterable.iterator();
                var e$51;while ((e$51=it$50.next())!==getFinished()){
                    if(selecting$47(e$51)){
                        last$48=e$51;
                    }
                }
                return last$48;
            };$$iterable.findLast.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['findLast']};$$iterable.$sort=function (comparing$52){
                var $$iterable=this;
                return internalSort($JsCallable(comparing$52,[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Comparison}}),$$iterable,{Element:$$iterable.$$targs$$.Element});
            };
            $$iterable.$sort.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Iterable,$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['sort']};
            $$iterable.collect=function (collecting$53,$$$mptypes){
                var $$iterable=this;
                return $$iterable.$map($JsCallable(collecting$53,[{$nm:'element',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:$$$mptypes.Result}),{Result:$$$mptypes.Result}).sequence;
            };
            $$iterable.collect.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Iterable,$tp:{Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['collect']};
            $$iterable.select=function (selecting$54){
                var $$iterable=this;
                return $$iterable.$filter($JsCallable(selecting$54,[{$nm:'element',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}})).sequence;
            };
            $$iterable.select.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[see([typeLiteral$model({Type:{t:Iterable.$$.prototype.$filter,a:{Absent:{t:Null},Element:].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['select']};
            $$iterable.any=function any(selecting$55){
                var $$iterable=this;
                var it$56 = $$iterable.iterator();
                var e$57;while ((e$57=it$56.next())!==getFinished()){
                    if(selecting$55(e$57)){
                        return true;
                    }
                }
                return false;
            };$$iterable.any.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['any']};$$iterable.$every=function $every(selecting$58){
                var $$iterable=this;
                var it$59 = $$iterable.iterator();
                var e$60;while ((e$60=it$59.next())!==getFinished()){
                    if((!selecting$58(e$60))){
                        return false;
                    }
                }
                return true;
            };$$iterable.$every.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['every']};$$iterable.skipping=function skipping(skip$61){
                var $$iterable=this;
                if((skip$61.compare((0))!==getLarger())){
                    return $$iterable;
                }else {
                    var cntvar$62=false;
                    var brkvar$64=false;
                    var retvar$63=(function(){
                        function iterable$65($$targs$$){
                            var $$iterable$65=new iterable$65.$$;
                            $$iterable$65.$$targs$$=$$targs$$;
                            Iterable({Absent:{t:Null},Element:$$iterable.$$targs$$.Element},$$iterable$65);
                            add_type_arg($$iterable$65,'Absent',{t:Null});
                            return $$iterable$65;
                        }
                        function $init$iterable$65(){
                            if (iterable$65.$$===undefined){
                                initTypeProto(iterable$65,'ceylon.language::Iterable.skipping.iterable',Basic,$init$Iterable());
                            }
                            return iterable$65;
                        }
                        $init$iterable$65();
                        (function($$iterable$65){
                            $$iterable$65.iterator=function iterator(){
                                var $$iterable$65=this;
                                var iter$66=$$iterable.iterator();
                                var i$67=(0);
                                var setI$67=function(i$68){return i$67=i$68;};
                                while(((oldi$69=i$67,i$67=oldi$69.successor,oldi$69).compare(skip$61).equals(getSmaller())&&(!isOfType(iter$66.next(),{t:Finished})))){}
                                var oldi$69;
                                return iter$66;
                            };$$iterable$65.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$65,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['skipping']['$o']['iterable']['$m']['iterator']};
                        })(iterable$65.$$.prototype);
                        var iterable$70=iterable$65({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$70=function(){
                            return iterable$70;
                        }
                        return getIterable$70();
                    }());if(retvar$63!==undefined){return retvar$63;}
                }
            };$$iterable.skipping.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['skipping']};$$iterable.taking=function taking(take$71){
                var $$iterable=this;
                if((take$71.compare((0))!==getLarger())){
                    return getEmpty();
                }else {
                    var cntvar$72=false;
                    var brkvar$74=false;
                    var retvar$73=(function(){
                        function iterable$75($$targs$$){
                            var $$iterable$75=new iterable$75.$$;
                            $$iterable$75.$$targs$$=$$targs$$;
                            Iterable({Absent:{t:Null},Element:$$iterable.$$targs$$.Element},$$iterable$75);
                            add_type_arg($$iterable$75,'Absent',{t:Null});
                            return $$iterable$75;
                        }
                        function $init$iterable$75(){
                            if (iterable$75.$$===undefined){
                                initTypeProto(iterable$75,'ceylon.language::Iterable.taking.iterable',Basic,$init$Iterable());
                            }
                            return iterable$75;
                        }
                        $init$iterable$75();
                        (function($$iterable$75){
                            $$iterable$75.iterator=function iterator(){
                                var $$iterable$75=this;
                                var iter$76=$$iterable.iterator();
                                function iterator$77($$targs$$){
                                    var $$iterator$77=new iterator$77.$$;
                                    $$iterator$77.$$targs$$=$$targs$$;
                                    Iterator({Element:$$iterable.$$targs$$.Element},$$iterator$77);
                                    $$iterator$77.i$78_=(0);
                                    return $$iterator$77;
                                }
                                function $init$iterator$77(){
                                    if (iterator$77.$$===undefined){
                                        initTypeProto(iterator$77,'ceylon.language::Iterable.taking.iterable.iterator.iterator',Basic,$init$Iterator());
                                    }
                                    return iterator$77;
                                }
                                $init$iterator$77();
                                (function($$iterator$77){
                                    defineAttr($$iterator$77,'i$78',function(){return this.i$78_;},function(i$79){return this.i$78_=i$79;},{mod:$$METAMODEL$$,$t:{t:Integer},$cont:iterator$77,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['taking']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$at']['i']});
                                    $$iterator$77.next=function next(){
                                        var $$iterator$77=this;
                                        return (opt$80=(($$iterator$77.i$78=$$iterator$77.i$78.successor).compare(take$71).equals(getLarger())?getFinished():null),opt$80!==null?opt$80:iter$76.next());
                                        var opt$80;
                                    };$$iterator$77.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$77,$an:function(){return[actual(),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['taking']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$m']['next']};
                                })(iterator$77.$$.prototype);
                                var iterator$81=iterator$77({Element:$$iterable.$$targs$$.Element});
                                var getIterator$81=function(){
                                    return iterator$81;
                                }
                                return getIterator$81();
                            };$$iterable$75.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$75,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['taking']['$o']['iterable']['$m']['iterator']};defineAttr($$iterable$75,'first',function(){
                                var $$iterable$75=this;
                                return $$iterable.first;
                            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:iterable$75,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['taking']['$o']['iterable']['$at']['first']});
                        })(iterable$75.$$.prototype);
                        var iterable$82=iterable$75({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$82=function(){
                            return iterable$82;
                        }
                        return getIterable$82();
                    }());if(retvar$73!==undefined){return retvar$73;}
                }
            };$$iterable.taking.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['taking']};$$iterable.skippingWhile=function skippingWhile(skip$83){
                var $$iterable=this;
                function iterable$84($$targs$$){
                    var $$iterable$84=new iterable$84.$$;
                    $$iterable$84.$$targs$$=$$targs$$;
                    Iterable({Absent:{t:Null},Element:$$iterable.$$targs$$.Element},$$iterable$84);
                    add_type_arg($$iterable$84,'Absent',{t:Null});
                    return $$iterable$84;
                }
                function $init$iterable$84(){
                    if (iterable$84.$$===undefined){
                        initTypeProto(iterable$84,'ceylon.language::Iterable.skippingWhile.iterable',Basic,$init$Iterable());
                    }
                    return iterable$84;
                }
                $init$iterable$84();
                (function($$iterable$84){
                    $$iterable$84.iterator=function iterator(){
                        var $$iterable$84=this;
                        var iter$85=$$iterable.iterator();
                        var elem$86;
                        while(!isOfType((elem$86=iter$85.next()),{t:Finished})){
                            if((!skip$83(elem$86))){
                                var cntvar$87=false;
                                var brkvar$89=false;
                                var retvar$88=(function(){
                                    function iterator$90($$targs$$){
                                        var $$iterator$90=new iterator$90.$$;
                                        $$iterator$90.$$targs$$=$$targs$$;
                                        Iterator({Element:$$iterable.$$targs$$.Element},$$iterator$90);
                                        $$iterator$90.first$91_=true;
                                        return $$iterator$90;
                                    }
                                    function $init$iterator$90(){
                                        if (iterator$90.$$===undefined){
                                            initTypeProto(iterator$90,'ceylon.language::Iterable.skippingWhile.iterable.iterator.iterator',Basic,$init$Iterator());
                                        }
                                        return iterator$90;
                                    }
                                    $init$iterator$90();
                                    (function($$iterator$90){
                                        defineAttr($$iterator$90,'first$91',function(){return this.first$91_;},function(first$92){return this.first$91_=first$92;},{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:iterator$90,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['skippingWhile']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$at']['first']});
                                        $$iterator$90.next=function next(){
                                            var $$iterator$90=this;
                                            if($$iterator$90.first$91){
                                                $$iterator$90.first$91=false;
                                                return elem$86;
                                            }else {
                                                return iter$85.next();
                                            }
                                        };$$iterator$90.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$90,$an:function(){return[actual(),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['skippingWhile']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$m']['next']};
                                    })(iterator$90.$$.prototype);
                                    var iterator$93=iterator$90({Element:$$iterable.$$targs$$.Element});
                                    var getIterator$93=function(){
                                        return iterator$93;
                                    }
                                    return getIterator$93();
                                }());if(retvar$88!==undefined){return retvar$88;}
                            }
                        }
                        return getEmptyIterator();
                    };$$iterable$84.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$84,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['skippingWhile']['$o']['iterable']['$m']['iterator']};
                })(iterable$84.$$.prototype);
                var iterable$94=iterable$84({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                var getIterable$94=function(){
                    return iterable$94;
                }
                return getIterable$94();
            };$$iterable.skippingWhile.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['skippingWhile']};$$iterable.takingWhile=function takingWhile(take$95){
                var $$iterable=this;
                function iterable$96($$targs$$){
                    var $$iterable$96=new iterable$96.$$;
                    $$iterable$96.$$targs$$=$$targs$$;
                    Iterable({Absent:{t:Null},Element:$$iterable.$$targs$$.Element},$$iterable$96);
                    add_type_arg($$iterable$96,'Absent',{t:Null});
                    return $$iterable$96;
                }
                function $init$iterable$96(){
                    if (iterable$96.$$===undefined){
                        initTypeProto(iterable$96,'ceylon.language::Iterable.takingWhile.iterable',Basic,$init$Iterable());
                    }
                    return iterable$96;
                }
                $init$iterable$96();
                (function($$iterable$96){
                    $$iterable$96.iterator=function iterator(){
                        var $$iterable$96=this;
                        var iter$97=$$iterable.iterator();
                        function iterator$98($$targs$$){
                            var $$iterator$98=new iterator$98.$$;
                            $$iterator$98.$$targs$$=$$targs$$;
                            Iterator({Element:$$iterable.$$targs$$.Element},$$iterator$98);
                            $$iterator$98.alive$99_=true;
                            return $$iterator$98;
                        }
                        function $init$iterator$98(){
                            if (iterator$98.$$===undefined){
                                initTypeProto(iterator$98,'ceylon.language::Iterable.takingWhile.iterable.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$98;
                        }
                        $init$iterator$98();
                        (function($$iterator$98){
                            defineAttr($$iterator$98,'alive$99',function(){return this.alive$99_;},function(alive$100){return this.alive$99_=alive$100;},{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:iterator$98,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['takingWhile']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$at']['alive']});
                            $$iterator$98.next=function next(){
                                var $$iterator$98=this;
                                if($$iterator$98.alive$99){
                                    var next$101=iter$97.next();
                                    var next$102;
                                    if(!isOfType((next$102=next$101),{t:Finished})){
                                        if(take$95(next$102)){
                                            return next$102;
                                        }else {
                                            $$iterator$98.alive$99=false;
                                        }
                                    }
                                }
                                return getFinished();
                            };$$iterator$98.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$98,$an:function(){return[actual(),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['takingWhile']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$m']['next']};
                        })(iterator$98.$$.prototype);
                        var iterator$103=iterator$98({Element:$$iterable.$$targs$$.Element});
                        var getIterator$103=function(){
                            return iterator$103;
                        }
                        return getIterator$103();
                    };$$iterable$96.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$96,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['takingWhile']['$o']['iterable']['$m']['iterator']};
                })(iterable$96.$$.prototype);
                var iterable$104=iterable$96({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                var getIterable$104=function(){
                    return iterable$104;
                }
                return getIterable$104();
            };$$iterable.takingWhile.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'take',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['takingWhile']};$$iterable.by=function by(step$105){
                var $$iterable=this;
                //assert at Iterable.ceylon (396:8-397:25)
                if (!(step$105.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Iterable.ceylon (397:15-397:24)'); }
                if(step$105.equals((1))){
                    return $$iterable;
                }else {
                    var cntvar$106=false;
                    var brkvar$108=false;
                    var retvar$107=(function(){
                        function iterable$109($$targs$$){
                            var $$iterable$109=new iterable$109.$$;
                            $$iterable$109.$$targs$$=$$targs$$;
                            Iterable({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element},$$iterable$109);
                            return $$iterable$109;
                        }
                        function $init$iterable$109(){
                            if (iterable$109.$$===undefined){
                                initTypeProto(iterable$109,'ceylon.language::Iterable.by.iterable',Basic,$init$Iterable());
                            }
                            return iterable$109;
                        }
                        $init$iterable$109();
                        (function($$iterable$109){
                            $$iterable$109.iterator=function iterator(){
                                var $$iterable$109=this;
                                var iter$110=$$iterable.iterator();
                                function iterator$111($$targs$$){
                                    var $$iterator$111=new iterator$111.$$;
                                    $$iterator$111.$$targs$$=$$targs$$;
                                    Iterator({Element:$$iterable.$$targs$$.Element},$$iterator$111);
                                    return $$iterator$111;
                                }
                                function $init$iterator$111(){
                                    if (iterator$111.$$===undefined){
                                        initTypeProto(iterator$111,'ceylon.language::Iterable.by.iterable.iterator.iterator',Basic,$init$Iterator());
                                    }
                                    return iterator$111;
                                }
                                $init$iterator$111();
                                (function($$iterator$111){
                                    $$iterator$111.next=function next(){
                                        var $$iterator$111=this;
                                        var next$112=iter$110.next();
                                        var i$113=(0);
                                        var setI$113=function(i$114){return i$113=i$114;};
                                        while(((i$113=i$113.successor).compare(step$105).equals(getSmaller())&&(!isOfType(iter$110.next(),{t:Finished})))){}
                                        return next$112;
                                    };$$iterator$111.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$111,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['by']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$m']['next']};
                                })(iterator$111.$$.prototype);
                                var iterator$115=iterator$111({Element:$$iterable.$$targs$$.Element});
                                var getIterator$115=function(){
                                    return iterator$115;
                                }
                                return getIterator$115();
                            };$$iterable$109.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$109,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['by']['$o']['iterable']['$m']['iterator']};
                        })(iterable$109.$$.prototype);
                        var iterable$116=iterable$109({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element});
                        var getIterable$116=function(){
                            return iterable$116;
                        }
                        return getIterable$116();
                    }());if(retvar$107!==undefined){return retvar$107;}
                }
            };$$iterable.by.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[$throws(typeLiteral$model({Type:{t:Exception}}),String$("if the given step size is nonpositive, \ni.e. `step<1`",53)),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['by']};$$iterable.count=function count(selecting$117){
                var $$iterable=this;
                var count$118=(0);
                var setCount$118=function(count$119){return count$118=count$119;};
                var it$120 = $$iterable.iterator();
                var elem$121;while ((elem$121=it$120.next())!==getFinished()){
                    var elem$122;
                    if((elem$122=elem$121)!==null){
                        if(selecting$117(elem$122)){
                            (oldcount$123=count$118,count$118=oldcount$123.successor,oldcount$123);
                            var oldcount$123;
                        }
                    }
                }
                return count$118;
            };$$iterable.count.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['count']};defineAttr($$iterable,'coalesced',function(){
                var $$iterable=this;
                return Comprehension(function(){
                    var e$126;
                    var it$124=$$iterable.iterator();
                    var e$125=getFinished();
                    var e$126;
                    var next$e$125=function(){
                        while((e$125=it$124.next())!==getFinished()){
                            if((e$126=e$125)!==null){
                                return e$125;
                            }
                        }
                        return getFinished();
                    }
                    next$e$125();
                    return function(){
                        if(e$125!==getFinished()){
                            var e$125$127=e$125;
                            var tmpvar$128=e$126;
                            next$e$125();
                            return tmpvar$128;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{ t:'i', l:['Element',{t:Object$}]}}},$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['coalesced']});
            defineAttr($$iterable,'indexed',function(){
                var $$iterable=this;
                function indexes$129($$targs$$){
                    var $$indexes$129=new indexes$129.$$;
                    $$indexes$129.$$targs$$=$$targs$$;
                    Iterable({Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}},$$indexes$129);
                    add_type_arg($$indexes$129,'Absent',{t:Null});
                    add_type_arg($$indexes$129,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                    return $$indexes$129;
                }
                function $init$indexes$129(){
                    if (indexes$129.$$===undefined){
                        initTypeProto(indexes$129,'ceylon.language::Iterable.indexed.indexes',Basic,$init$Iterable());
                    }
                    return indexes$129;
                }
                $init$indexes$129();
                (function($$indexes$129){
                    defineAttr($$indexes$129,'orig$130',function(){
                        var $$indexes$129=this;
                        return $$iterable;
                    },undefined,{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$cont:indexes$129,pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['indexed']['$o']['indexes']['$at']['orig']});
                    $$indexes$129.iterator=function iterator(){
                        var $$indexes$129=this;
                        function iterator$131($$targs$$){
                            var $$iterator$131=new iterator$131.$$;
                            $$iterator$131.$$targs$$=$$targs$$;
                            Iterator({Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}},$$iterator$131);
                            add_type_arg($$iterator$131,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                            $$iterator$131.iter$132_=$$indexes$129.orig$130.iterator();
                            $$iterator$131.i$133_=(0);
                            return $$iterator$131;
                        }
                        function $init$iterator$131(){
                            if (iterator$131.$$===undefined){
                                initTypeProto(iterator$131,'ceylon.language::Iterable.indexed.indexes.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$131;
                        }
                        $init$iterator$131();
                        (function($$iterator$131){
                            defineAttr($$iterator$131,'iter$132',function(){return this.iter$132_;},undefined,{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$131,pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['indexed']['$o']['indexes']['$m']['iterator']['$o']['iterator']['$at']['iter']});
                            defineAttr($$iterator$131,'i$133',function(){return this.i$133_;},function(i$134){return this.i$133_=i$134;},{mod:$$METAMODEL$$,$t:{t:Integer},$cont:iterator$131,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['indexed']['$o']['indexes']['$m']['iterator']['$o']['iterator']['$at']['i']});
                            $$iterator$131.next=function next(){
                                var $$iterator$131=this;
                                var next$135=$$iterator$131.iter$132.next();
                                var setNext$135=function(next$136){return next$135=next$136;};
                                while((!exists(next$135))){
                                    (oldi$137=$$iterator$131.i$133,$$iterator$131.i$133=oldi$137.successor,oldi$137);
                                    var oldi$137;
                                    next$135=$$iterator$131.iter$132.next();
                                }
                                var n$138;
                                var n$139;
                                if(!isOfType((n$138=next$135),{t:Finished})&&(n$139=n$138)!==null){
                                    return Entry((oldi$140=$$iterator$131.i$133,$$iterator$131.i$133=oldi$140.successor,oldi$140),n$139,{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
                                    var oldi$140;
                                }else {
                                    return getFinished();
                                }
                            };$$iterator$131.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}},{t:Finished}]},$ps:[],$cont:iterator$131,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['indexed']['$o']['indexes']['$m']['iterator']['$o']['iterator']['$m']['next']};
                        })(iterator$131.$$.prototype);
                        var iterator$141=iterator$131({Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                        var getIterator$141=function(){
                            return iterator$141;
                        }
                        return getIterator$141();
                    };$$indexes$129.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$ps:[],$cont:indexes$129,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['indexed']['$o']['indexes']['$m']['iterator']};
                })(indexes$129.$$.prototype);
                var indexes$142=indexes$129({Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                var getIndexes$142=function(){
                    return indexes$142;
                }
                return getIndexes$142();
            },undefined,{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['indexed']});$$iterable.following=function following(head$143,$$$mptypes){
                var $$iterable=this;
                function cons$144($$targs$$){
                    var $$cons$144=new cons$144.$$;
                    $$cons$144.$$targs$$=$$targs$$;
                    Iterable({Absent:{t:Nothing},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}},$$cons$144);
                    return $$cons$144;
                }
                function $init$cons$144(){
                    if (cons$144.$$===undefined){
                        initTypeProto(cons$144,'ceylon.language::Iterable.following.cons',Basic,$init$Iterable());
                    }
                    return cons$144;
                }
                $init$cons$144();
                (function($$cons$144){
                    $$cons$144.iterator=function iterator(){
                        var $$cons$144=this;
                        var iter$145=$$iterable.iterator();
                        function iterator$146($$targs$$){
                            var $$iterator$146=new iterator$146.$$;
                            $$iterator$146.$$targs$$=$$targs$$;
                            Iterator({Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}},$$iterator$146);
                            $$iterator$146.first$147_=true;
                            return $$iterator$146;
                        }
                        function $init$iterator$146(){
                            if (iterator$146.$$===undefined){
                                initTypeProto(iterator$146,'ceylon.language::Iterable.following.cons.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$146;
                        }
                        $init$iterator$146();
                        (function($$iterator$146){
                            defineAttr($$iterator$146,'first$147',function(){return this.first$147_;},function(first$148){return this.first$147_=first$148;},{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:iterator$146,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['following']['$o']['cons']['$m']['iterator']['$o']['iterator']['$at']['first']});
                            $$iterator$146.next=function next(){
                                var $$iterator$146=this;
                                if($$iterator$146.first$147){
                                    $$iterator$146.first$147=false;
                                    return head$143;
                                }else {
                                    return iter$145.next();
                                }
                            };$$iterator$146.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element','Other',{t:Finished}]},$ps:[],$cont:iterator$146,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['following']['$o']['cons']['$m']['iterator']['$o']['iterator']['$m']['next']};
                        })(iterator$146.$$.prototype);
                        var iterator$149=iterator$146({Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                        var getIterator$149=function(){
                            return iterator$149;
                        }
                        return getIterator$149();
                    };$$cons$144.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[],$cont:cons$144,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['following']['$o']['cons']['$m']['iterator']};
                })(cons$144.$$.prototype);
                var cons$150=cons$144({Absent:{t:Nothing},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getCons$150=function(){
                    return cons$150;
                }
                return getCons$150();
            };$$iterable.following.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Nothing},Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'head',$mt:'prm',$t:'Other'}],$cont:Iterable,$tp:{Other:{}},$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['following']};$$iterable.chain=function chain(other$151,$$$mptypes){
                var $$iterable=this;
                function chained$152($$targs$$){
                    var $$chained$152=new chained$152.$$;
                    $$chained$152.$$targs$$=$$targs$$;
                    Iterable({Absent:{ t:'i', l:[$$iterable.$$targs$$.Absent,$$$mptypes.OtherAbsent]},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}},$$chained$152);
                    return $$chained$152;
                }
                function $init$chained$152(){
                    if (chained$152.$$===undefined){
                        initTypeProto(chained$152,'ceylon.language::Iterable.chain.chained',Basic,$init$Iterable());
                    }
                    return chained$152;
                }
                $init$chained$152();
                (function($$chained$152){
                    $$chained$152.iterator=function (){
                        var $$chained$152=this;
                        return ChainedIterator($$iterable,other$151,{Other:$$$mptypes.Other,Element:$$iterable.$$targs$$.Element});
                    };
                    $$chained$152.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[],$cont:chained$152,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['chain']['$o']['chained']['$m']['iterator']};
                })(chained$152.$$.prototype);
                var chained$153=chained$152({Absent:{ t:'i', l:[$$iterable.$$targs$$.Absent,$$$mptypes.OtherAbsent]},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getChained$153=function(){
                    return chained$153;
                }
                return getChained$153();
            };$$iterable.chain.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{ t:'i', l:['Absent','OtherAbsent']},Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}}}],$cont:Iterable,$tp:{Other:{},OtherAbsent:{'satisfies':[{t:Null}]}},$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['chain']};$$iterable.defaultNullElements=function (defaultValue$154,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$155=$$iterable.iterator();
                    var elem$156=getFinished();
                    var next$elem$156=function(){return elem$156=it$155.next();}
                    next$elem$156();
                    return function(){
                        if(elem$156!==getFinished()){
                            var elem$156$157=elem$156;
                            var tmpvar$158=(opt$159=elem$156$157,opt$159!==null?opt$159:defaultValue$154);
                            next$elem$156();
                            return tmpvar$158;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'u', l:[$$$mptypes.Default,{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}]}});
            };
            $$iterable.defaultNullElements.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:{ t:'u', l:[{ t:'i', l:['Element',{t:Object$}]},'Default']}}},$ps:[{$nm:'defaultValue',$mt:'prm',$t:'Default'}],$cont:Iterable,$tp:{Default:{}},$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['defaultNullElements']};
            defineAttr($$iterable,'string',function(){
                var $$iterable=this;
                if($$iterable.empty){
                    return String$("{}",2);
                }else {
                    var list$160=commaList($$iterable.taking((30)));
                    return StringBuilder().appendAll([String$("{ ",2),(opt$161=($$iterable.longerThan((30))?list$160.plus(String$(", ...",5)):null),opt$161!==null?opt$161:list$160).string,String$(" }",2)]).string;
                    var opt$161;
                }
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['string']});defineAttr($$iterable,'repeated',function(){
                var $$iterable=this;
                function iterable$162($$targs$$){
                    var $$iterable$162=new iterable$162.$$;
                    $$iterable$162.$$targs$$=$$targs$$;
                    Iterable({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element},$$iterable$162);
                    return $$iterable$162;
                }
                function $init$iterable$162(){
                    if (iterable$162.$$===undefined){
                        initTypeProto(iterable$162,'ceylon.language::Iterable.repeated.iterable',Basic,$init$Iterable());
                    }
                    return iterable$162;
                }
                $init$iterable$162();
                (function($$iterable$162){
                    defineAttr($$iterable$162,'orig$163',function(){
                        var $$iterable$162=this;
                        return $$iterable;
                    },undefined,{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$cont:iterable$162,pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['repeated']['$o']['iterable']['$at']['orig']});
                    $$iterable$162.iterator=function iterator(){
                        var $$iterable$162=this;
                        function iterator$164($$targs$$){
                            var $$iterator$164=new iterator$164.$$;
                            $$iterator$164.$$targs$$=$$targs$$;
                            Iterator({Element:$$iterable.$$targs$$.Element},$$iterator$164);
                            $$iterator$164.iter$165_=getEmptyIterator();
                            return $$iterator$164;
                        }
                        function $init$iterator$164(){
                            if (iterator$164.$$===undefined){
                                initTypeProto(iterator$164,'ceylon.language::Iterable.repeated.iterable.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$164;
                        }
                        $init$iterator$164();
                        (function($$iterator$164){
                            defineAttr($$iterator$164,'iter$165',function(){return this.iter$165_;},function(iter$166){return this.iter$165_=iter$166;},{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$164,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['repeated']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$at']['iter']});
                            $$iterator$164.next=function next(){
                                var $$iterator$164=this;
                                var next$167;
                                if(!isOfType((next$167=$$iterator$164.iter$165.next()),{t:Finished})){
                                    return next$167;
                                }else {
                                    $$iterator$164.iter$165=$$iterable$162.orig$163.iterator();
                                    return $$iterator$164.iter$165.next();
                                }
                            };$$iterator$164.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$164,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['repeated']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$m']['next']};
                        })(iterator$164.$$.prototype);
                        var iterator$168=iterator$164({Element:$$iterable.$$targs$$.Element});
                        var getIterator$168=function(){
                            return iterator$168;
                        }
                        return getIterator$168();
                    };$$iterable$162.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$162,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['repeated']['$o']['iterable']['$m']['iterator']};
                })(iterable$162.$$.prototype);
                var iterable$169=iterable$162({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element});
                var getIterable$169=function(){
                    return iterable$169;
                }
                return getIterable$169();
            },undefined,{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$cont:Iterable,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterable']['$at']['repeated']});
        })(Iterable.$$.prototype);
    }
    return Iterable;
}
exports.$init$Iterable=$init$Iterable;
$init$Iterable();
var opt$19,opt$159;
var commaList=function (elements$170){
    return (strings$171=Comprehension(function(){
        var it$172=elements$170.iterator();
        var element$173=getFinished();
        var next$element$173=function(){return element$173=it$172.next();}
        next$element$173();
        return function(){
            if(element$173!==getFinished()){
                var element$173$174=element$173;
                var tmpvar$175=(opt$176=(opt$177=element$173$174,opt$177!==null?opt$177.string:null),opt$176!==null?opt$176:String$("null",4));
                next$element$173();
                return tmpvar$175;
            }
            return getFinished();
        }
    },{Absent:{t:Null},Element:{t:String$}}),(opt$178=String$(", ",2),JsCallable(opt$178,opt$178!==null?opt$178.join:null))(strings$171));
};
commaList.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:String$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Anything}}}}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['commaList']};
var strings$171,opt$176,opt$177,opt$178;
function ifExists(predicate$179){
    return function(val$180){
        var val$181;
        if((val$181=val$180)!==null){
            return predicate$179(val$181);
        }else {
            return false;
        }
    }
};ifExists.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'predicate',$mt:'prm',$t:{t:Boolean$}}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['ifExists']};
function Sequential($$targs$$,$$sequential){
    List($$sequential.$$targs$$===undefined?$$targs$$:{Element:$$sequential.$$targs$$.Element},$$sequential);
    Ranged($$sequential.$$targs$$===undefined?$$targs$$:{Index:{t:Integer},Span:{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}}},$$sequential);
    add_type_arg($$sequential,'Index',{t:Integer});
    add_type_arg($$sequential,'Span',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
    Cloneable($$sequential.$$targs$$===undefined?$$targs$$:{Clone:{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}}},$$sequential);
    add_type_arg($$sequential,'Clone',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
    set_type_args($$sequential,$$targs$$);
}
Sequential.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out',}},satisfies:[{t:List,a:{Element:'Element'}},{t:Ranged,a:{Index:{t:Integer},Span:{t:Sequential,a:{Element:'Element'}}}},{t:Cloneable,a:{Clone:{t:Sequential,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:{t:Tuple,a:{Rest:{t:Empty}}}})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequential']};
exports.Sequential=Sequential;
function $init$Sequential(){
    if (Sequential.$$===undefined){
        initTypeProto(Sequential,'ceylon.language::Sequential',$init$List(),$init$Ranged(),$init$Cloneable());
        (function($$sequential){
            defineAttr($$sequential,'sequence',function(){
                var $$sequential=this;
                return $$sequential;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequential']['$at']['sequence']});
            defineAttr($$sequential,'clone',function(){
                var $$sequential=this;
                return $$sequential;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequential']['$at']['clone']});
            defineAttr($$sequential,'string',function(){
                var $$sequential=this;
                return (opt$182=($$sequential.empty?String$("[]",2):null),opt$182!==null?opt$182:StringBuilder().appendAll([String$("[",1),commaList($$sequential).string,String$("]",1)]).string);
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequential']['$at']['string']});
        })(Sequential.$$.prototype);
    }
    return Sequential;
}
exports.$init$Sequential=$init$Sequential;
$init$Sequential();
var opt$182;
function Sequence($$targs$$,$$sequence){
    Sequential($$sequence.$$targs$$===undefined?$$targs$$:{Element:$$sequence.$$targs$$.Element},$$sequence);
    Iterable($$sequence.$$targs$$===undefined?$$targs$$:{Absent:{t:Nothing},Element:$$sequence.$$targs$$.Element},$$sequence);
    Cloneable($$sequence.$$targs$$===undefined?$$targs$$:{Clone:{t:Sequence,a:{Element:$$sequence.$$targs$$.Element}}},$$sequence);
    add_type_arg($$sequence,'Clone',{t:Sequence,a:{Element:$$sequence.$$targs$$.Element}});
    set_type_args($$sequence,$$targs$$);
}
Sequence.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out',}},satisfies:[{t:Sequential,a:{Element:'Element'}},{t:Iterable,a:{Absent:{t:Nothing},Element:'Element'}},{t:Cloneable,a:{Clone:{t:Sequence,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:{t:Empty}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:InterfaceDeclaration$model$declaration},{t:Interface$model,a:{Type:{t:Empty}}}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']};
exports.Sequence=Sequence;
function $init$Sequence(){
    if (Sequence.$$===undefined){
        initTypeProto(Sequence,'ceylon.language::Sequence',$init$Sequential(),$init$Iterable(),$init$Cloneable());
        (function($$sequence){
            defineAttr($$sequence,'empty',function(){
                var $$sequence=this;
                return false;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Sequence,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$at']['empty']});
            defineAttr($$sequence,'sequence',function(){
                var $$sequence=this;
                return $$sequence;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$cont:Sequence,$an:function(){return[shared(),$default(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$at']['sequence']});
            $$sequence.$sort=function $sort(comparing$183){
                var $$sequence=this;
                var s$184=internalSort($JsCallable(comparing$183,[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:{t:Comparison}}),$$sequence,{Element:$$sequence.$$targs$$.Element});
                //assert at Sequence.ceylon (63:8-63:27)
                var s$185;
                if (!(nonempty((s$185=s$184)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (63:15-63:26)'); }
                return s$185;
            };$$sequence.$sort.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Sequence,$an:function(){return[shared(),$default(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$m']['sort']};$$sequence.collect=function collect(collecting$186,$$$mptypes){
                var $$sequence=this;
                var s$187=$$sequence.$map($JsCallable(collecting$186,[{$nm:'element',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:$$$mptypes.Result}),{Result:$$$mptypes.Result}).sequence;
                //assert at Sequence.ceylon (74:8-74:27)
                var s$188;
                if (!(nonempty((s$188=s$187)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (74:15-74:26)'); }
                return s$188;
            };$$sequence.collect.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Sequence,$tp:{Result:{}},$an:function(){return[shared(),$default(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$m']['collect']};defineAttr($$sequence,'clone',function(){
                var $$sequence=this;
                return $$sequence;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$at']['clone']});
            defineAttr($$sequence,'string',function(){
                var $$sequence=this;
                return attrGetter($$sequence.getT$all()['ceylon.language::Sequential'],'string').call(this);
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$at']['string']});
            $$sequence.shorterThan=function (length$189){
                var $$sequence=this;
                return $$sequence.getT$all()['ceylon.language::List'].$$.prototype.shorterThan.call(this,length$189);
            };
            $$sequence.shorterThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$m']['shorterThan']};
            $$sequence.longerThan=function (length$190){
                var $$sequence=this;
                return $$sequence.getT$all()['ceylon.language::List'].$$.prototype.longerThan.call(this,length$190);
            };
            $$sequence.longerThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$m']['longerThan']};
            $$sequence.findLast=function (selecting$191){
                var $$sequence=this;
                return $$sequence.getT$all()['ceylon.language::List'].$$.prototype.findLast.call(this,$JsCallable(selecting$191,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$sequence.findLast.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Sequence,$an:function(){return[shared(),$default(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Sequence']['$m']['findLast']};
        })(Sequence.$$.prototype);
    }
    return Sequence;
}
exports.$init$Sequence=$init$Sequence;
$init$Sequence();
function Empty($$empty){
    Sequential({Element:{t:Nothing}},$$empty);
    Ranged({Index:{t:Integer},Span:{t:Empty}},$$empty);
    add_type_arg($$empty,'Index',{t:Integer});
    add_type_arg($$empty,'Span',{t:Empty});
    Cloneable({Clone:{t:Empty}},$$empty);
    add_type_arg($$empty,'Clone',{t:Empty});
}
Empty.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Sequential,a:{Element:{t:Nothing}}},{t:Ranged,a:{Index:{t:Integer},Span:{t:Empty}}},{t:Cloneable,a:{Clone:{t:Empty}}}],$an:function(){return[see([typeLiteral$model({Type:{t:Sequence,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']};
exports.Empty=Empty;
function $init$Empty(){
    if (Empty.$$===undefined){
        initTypeProto(Empty,'ceylon.language::Empty',$init$Sequential(),$init$Ranged(),$init$Cloneable());
        (function($$empty){
            $$empty.iterator=function (){
                var $$empty=this;
                return getEmptyIterator();
            };
            $$empty.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{t:Nothing}}},$ps:[],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['iterator']};
            $$empty.get=function (index$192){
                var $$empty=this;
                return null;
            };
            $$empty.get.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Null},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['get']};
            $$empty.segment=function (from$193,length$194){
                var $$empty=this;
                return $$empty;
            };
            $$empty.segment.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['segment']};
            $$empty.span=function (from$195,to$196){
                var $$empty=this;
                return $$empty;
            };
            $$empty.span.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['span']};
            $$empty.spanTo=function (to$197){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanTo.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['spanTo']};
            $$empty.spanFrom=function (from$198){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanFrom.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['spanFrom']};
            defineAttr($$empty,'empty',function(){
                var $$empty=this;
                return true;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['empty']});
            defineAttr($$empty,'size',function(){
                var $$empty=this;
                return (0);
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['size']});
            defineAttr($$empty,'reversed',function(){
                var $$empty=this;
                return $$empty;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['reversed']});
            defineAttr($$empty,'sequence',function(){
                var $$empty=this;
                return $$empty;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['sequence']});
            defineAttr($$empty,'string',function(){
                var $$empty=this;
                return String$("{}",2);
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['string']});
            defineAttr($$empty,'lastIndex',function(){
                var $$empty=this;
                return null;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Null},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['lastIndex']});
            defineAttr($$empty,'first',function(){
                var $$empty=this;
                return null;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Null},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['first']});
            defineAttr($$empty,'last',function(){
                var $$empty=this;
                return null;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Null},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['last']});
            defineAttr($$empty,'rest',function(){
                var $$empty=this;
                return $$empty;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['rest']});
            defineAttr($$empty,'clone',function(){
                var $$empty=this;
                return $$empty;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['clone']});
            defineAttr($$empty,'coalesced',function(){
                var $$empty=this;
                return $$empty;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['coalesced']});
            defineAttr($$empty,'indexed',function(){
                var $$empty=this;
                return $$empty;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$at']['indexed']});
            $$empty.chain=function (other$199,$$$mptypes){
                var $$empty=this;
                return other$199;
            };
            $$empty.chain.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}}}],$cont:Empty,$tp:{Other:{},OtherAbsent:{'satisfies':[{t:Null}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['chain']};
            $$empty.contains=function (element$200){
                var $$empty=this;
                return false;
            };
            $$empty.contains.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['contains']};
            $$empty.count=function (selecting$201){
                var $$empty=this;
                return (0);
            };
            $$empty.count.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['count']};
            $$empty.defines=function (index$202){
                var $$empty=this;
                return false;
            };
            $$empty.defines.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['defines']};
            $$empty.$map=function (collecting$203,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$map.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Empty,$tp:{Result:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['map']};
            $$empty.$filter=function (selecting$204){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$filter.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['filter']};
            $$empty.fold=function (initial$205,accumulating$206,$$$mptypes){
                var $$empty=this;
                return initial$205;
            };
            $$empty.fold.$$metamodel$$={mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$cont:Empty,$tp:{Result:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['fold']};
            $$empty.find=function (selecting$207){
                var $$empty=this;
                return null;
            };
            $$empty.find.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Null},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['find']};
            $$empty.$sort=function (comparing$208){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$sort.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['sort']};
            $$empty.collect=function (collecting$209,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.collect.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Empty,$tp:{Result:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['collect']};
            $$empty.select=function (selecting$210){
                var $$empty=this;
                return $$empty;
            };
            $$empty.select.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['select']};
            $$empty.any=function (selecting$211){
                var $$empty=this;
                return false;
            };
            $$empty.any.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['any']};
            $$empty.$every=function (selecting$212){
                var $$empty=this;
                return false;
            };
            $$empty.$every.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['every']};
            $$empty.skipping=function (skip$213){
                var $$empty=this;
                return $$empty;
            };
            $$empty.skipping.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['skipping']};
            $$empty.taking=function (take$214){
                var $$empty=this;
                return $$empty;
            };
            $$empty.taking.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['taking']};
            $$empty.by=function (step$215){
                var $$empty=this;
                return $$empty;
            };
            $$empty.by.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['by']};
            $$empty.withLeading=function (element$216,$$$mptypes){
                var $$empty=this;
                return Tuple(element$216,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withLeading.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:{t:Empty},First:'Element',Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:'Element'}],$cont:Empty,$tp:{Element:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['withLeading']};
            $$empty.withTrailing=function (element$217,$$$mptypes){
                var $$empty=this;
                return Tuple(element$217,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withTrailing.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:{t:Empty},First:'Element',Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:'Element'}],$cont:Empty,$tp:{Element:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['withTrailing']};
            $$empty.following=function (head$218,$$$mptypes){
                var $$empty=this;
                return Singleton(head$218,{Element:$$$mptypes.Other});
            };
            $$empty.following.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Other'}},$ps:[{$nm:'head',$mt:'prm',$t:'Other'}],$cont:Empty,$tp:{Other:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Empty']['$m']['following']};
        })(Empty.$$.prototype);
    }
    return Empty;
}
exports.$init$Empty=$init$Empty;
$init$Empty();
function empty$219(){
    var $$empty=new empty$219.$$;
    Object$($$empty);
    Empty($$empty);
    return $$empty;
}
function $init$empty$219(){
    if (empty$219.$$===undefined){
        initTypeProto(empty$219,'ceylon.language::empty',Object$,$init$Empty());
    }
    return empty$219;
}
exports.$init$empty$219=$init$empty$219;
$init$empty$219();
var empty$220=empty$219();
var getEmpty=function(){
    return empty$220;
}
exports.getEmpty=getEmpty;
function emptyIterator$221($$targs$$){
    var $$emptyIterator=new emptyIterator$221.$$;
    $$emptyIterator.$$targs$$=$$targs$$;
    Iterator({Element:{t:Nothing}},$$emptyIterator);
    return $$emptyIterator;
}
function $init$emptyIterator$221(){
    if (emptyIterator$221.$$===undefined){
        initTypeProto(emptyIterator$221,'ceylon.language::emptyIterator',Basic,$init$Iterator());
    }
    return emptyIterator$221;
}
exports.$init$emptyIterator$221=$init$emptyIterator$221;
$init$emptyIterator$221();
(function($$emptyIterator){
    $$emptyIterator.next=function (){
        var $$emptyIterator=this;
        return getFinished();
    };
})(emptyIterator$221.$$.prototype);
var emptyIterator$222=emptyIterator$221({Element:{t:Nothing}});
var getEmptyIterator=function(){
    return emptyIterator$222;
}
exports.getEmptyIterator=getEmptyIterator;
function Keys(correspondence$223, $$targs$$,$$keys){
    $init$Keys();
    if ($$keys===undefined)$$keys=new Keys.$$;
    set_type_args($$keys,$$targs$$);
    $$keys.correspondence$223=correspondence$223;
    Category($$keys);
    return $$keys;
}
Keys.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Key:{'var':'in','satisfies':[{t:Object$}]},Item:{'var':'out',}},satisfies:[{t:Category}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Keys']};
function $init$Keys(){
    if (Keys.$$===undefined){
        initTypeProto(Keys,'ceylon.language::Keys',Basic,$init$Category());
        (function($$keys){
            $$keys.contains=function contains(key$224){
                var $$keys=this;
                var key$225;
                if(isOfType((key$225=key$224),$$keys.$$targs$$.Key)){
                    return $$keys.correspondence$223.defines(key$225);
                }else {
                    return false;
                }
            };$$keys.contains.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'key',$mt:'prm',$t:{t:Object$}}],$cont:Keys,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Keys']['$m']['contains']};
        })(Keys.$$.prototype);
    }
    return Keys;
}
exports.$init$Keys=$init$Keys;
$init$Keys();
function Correspondence($$targs$$,$$correspondence){
    set_type_args($$correspondence,$$targs$$);
}
Correspondence.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Key:{'var':'in','satisfies':[{t:Object$}]},Item:{'var':'out',}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:Map,a:{}}}),typeLiteral$model({Type:{t:List,a:{}}}),typeLiteral$model({Type:{t:Category}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Correspondence']};
exports.Correspondence=Correspondence;
function $init$Correspondence(){
    if (Correspondence.$$===undefined){
        initTypeProto(Correspondence,'ceylon.language::Correspondence');
        (function($$correspondence){
            $$correspondence.defines=function (key$226){
                var $$correspondence=this;
                return exists($$correspondence.get(key$226));
            };
            $$correspondence.defines.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'key',$mt:'prm',$t:'Key'}],$cont:Correspondence,$an:function(){return[see([typeLiteral$model({Type:{t:Correspondence.$$.prototype.definesAny,a:{Key:,typeLiteral$model({Type:{t:Correspondence.$$.prototype.definesEvery,a:{Key:,typeLiteral$model({Type:{t:Correspondence.$$.prototype.$prop$keys,a:{Key:].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:FunctionDeclaration$model$declaration},{t:ValueDeclaration$model$declaration}]}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Correspondence']['$m']['defines']};
            defineAttr($$correspondence,'keys',function(){
                var $$correspondence=this;
                return Keys($$correspondence,{Key:$$correspondence.$$targs$$.Key,Item:$$correspondence.$$targs$$.Item});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Category},$cont:Correspondence,$an:function(){return[see([typeLiteral$model({Type:{t:Correspondence.$$.prototype.defines,a:{Key:].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Correspondence']['$at']['keys']});
            $$correspondence.definesEvery=function definesEvery(keys$227){
                var $$correspondence=this;
                var it$228 = keys$227.iterator();
                var key$229;while ((key$229=it$228.next())!==getFinished()){
                    if((!$$correspondence.defines(key$229))){
                        return false;
                    }
                }
                if (getFinished() === key$229){
                    return true;
                }
            };$$correspondence.definesEvery.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$cont:Correspondence,$an:function(){return[see([typeLiteral$model({Type:{t:Correspondence.$$.prototype.defines,a:{Key:].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Correspondence']['$m']['definesEvery']};$$correspondence.definesAny=function definesAny(keys$230){
                var $$correspondence=this;
                var it$231 = keys$230.iterator();
                var key$232;while ((key$232=it$231.next())!==getFinished()){
                    if($$correspondence.defines(key$232)){
                        return true;
                    }
                }
                if (getFinished() === key$232){
                    return false;
                }
            };$$correspondence.definesAny.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$cont:Correspondence,$an:function(){return[see([typeLiteral$model({Type:{t:Correspondence.$$.prototype.defines,a:{Key:].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Correspondence']['$m']['definesAny']};$$correspondence.items=function (keys$233){
                var $$correspondence=this;
                return Comprehension(function(){
                    var it$234=keys$233.iterator();
                    var key$235=getFinished();
                    var next$key$235=function(){return key$235=it$234.next();}
                    next$key$235();
                    return function(){
                        if(key$235!==getFinished()){
                            var key$235$236=key$235;
                            var tmpvar$237=$$correspondence.get(key$235$236);
                            next$key$235();
                            return tmpvar$237;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'u', l:[{t:Null},$$correspondence.$$targs$$.Item]}}).sequence;
            };
            $$correspondence.items.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{ t:'u', l:[{t:Null},'Item']}}},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$cont:Correspondence,$an:function(){return[see([typeLiteral$model({Type:{t:Correspondence.$$.prototype.get,a:{Key:].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Correspondence']['$m']['items']};
        })(Correspondence.$$.prototype);
    }
    return Correspondence;
}
exports.$init$Correspondence=$init$Correspondence;
$init$Correspondence();
function Finished($$finished){
    $init$Finished();
    if ($$finished===undefined)$$finished=new Finished.$$;
    return $$finished;
}
Finished.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:Iterator,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared(),abstract()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Finished']};
exports.Finished=Finished;
function $init$Finished(){
    if (Finished.$$===undefined){
        initTypeProto(Finished,'ceylon.language::Finished',Basic);
    }
    return Finished;
}
exports.$init$Finished=$init$Finished;
$init$Finished();
function finished$238(){
    var $$finished=new finished$238.$$;
    Finished($$finished);
    return $$finished;
}
function $init$finished$238(){
    if (finished$238.$$===undefined){
        initTypeProto(finished$238,'ceylon.language::finished',Finished);
    }
    return finished$238;
}
exports.$init$finished$238=$init$finished$238;
$init$finished$238();
(function($$finished){
    defineAttr($$finished,'string',function(){
        var $$finished=this;
        return String$("finished",8);
    },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:finished$238,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['finished']['$at']['string']});
})(finished$238.$$.prototype);
var finished$239=finished$238();
var getFinished=function(){
    return finished$239;
}
exports.getFinished=getFinished;
function Binary($$targs$$,$$binary){
    set_type_args($$binary,$$targs$$);
    $$binary.set$defs$bit=function(index$240,bit$241){return true;};
}
Binary.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Binary,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:Integer}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Integer},Element:{t:Integer}}},Type:{t:Integer}}}]}})),by([String$("Stef",4)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Binary']};
exports.Binary=Binary;
function $init$Binary(){
    if (Binary.$$===undefined){
        initTypeProto(Binary,'ceylon.language::Binary');
        (function($$binary){
            $$binary.clear=function clear(index$242){
                var $$binary=this;
                return $$binary.set(index$242,false);
            };$$binary.clear.$$metamodel$$={mod:$$METAMODEL$$,$t:'Other',$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Binary,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Binary']['$m']['clear']};
        })(Binary.$$.prototype);
    }
    return Binary;
}
exports.$init$Binary=$init$Binary;
$init$Binary();
function Cloneable($$targs$$,$$cloneable){
    set_type_args($$cloneable,$$targs$$);
}
Cloneable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Clone:{'var':'out','satisfies':[{t:Cloneable,a:{Clone:'Clone'}}]}},satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Cloneable']};
exports.Cloneable=Cloneable;
function $init$Cloneable(){
    if (Cloneable.$$===undefined){
        initTypeProto(Cloneable,'ceylon.language::Cloneable');
        (function($$cloneable){
        })(Cloneable.$$.prototype);
    }
    return Cloneable;
}
exports.$init$Cloneable=$init$Cloneable;
$init$Cloneable();
function Closeable($$closeable){
}
Closeable.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Closeable']};
exports.Closeable=Closeable;
function $init$Closeable(){
    if (Closeable.$$===undefined){
        initTypeProto(Closeable,'ceylon.language::Closeable');
        (function($$closeable){
        })(Closeable.$$.prototype);
    }
    return Closeable;
}
exports.$init$Closeable=$init$Closeable;
$init$Closeable();
function Ranged($$targs$$,$$ranged){
    set_type_args($$ranged,$$targs$$);
}
Ranged.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Index:{'var':'in','satisfies':[{t:Comparable,a:{Other:'Index'}}]},Span:{'var':'out',}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:List,a:{}}}),typeLiteral$model({Type:{t:Sequence,a:{}}}),typeLiteral$model({Type:{t:String$}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Character}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Character}}}}},Type:{t:String$}}}]}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Ranged']};
exports.Ranged=Ranged;
function $init$Ranged(){
    if (Ranged.$$===undefined){
        initTypeProto(Ranged,'ceylon.language::Ranged');
        (function($$ranged){
        })(Ranged.$$.prototype);
    }
    return Ranged;
}
exports.$init$Ranged=$init$Ranged;
$init$Ranged();
function Container($$targs$$,$$container){
    Category($$container);
    set_type_args($$container,$$targs$$);
}
Container.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out',},Absent:{'var':'out','satisfies':[{t:Null}],'def':{t:Null}}},satisfies:[{t:Category}],$an:function(){return[see([typeLiteral$model({Type:{t:Category}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:InterfaceDeclaration$model$declaration},{t:Interface$model,a:{Type:{t:Category}}}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),deprecated(String$("Will be removed in Ceylon 1.0.",30)),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Container']};
exports.Container=Container;
function $init$Container(){
    if (Container.$$===undefined){
        initTypeProto(Container,'ceylon.language::Container',$init$Category());
        (function($$container){
        })(Container.$$.prototype);
    }
    return Container;
}
exports.$init$Container=$init$Container;
$init$Container();
function Iterator($$targs$$,$$iterator){
    set_type_args($$iterator,$$targs$$);
}
Iterator.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out',}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:Iterable,a:{Absent:{t:Null}}}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Iterator']};
exports.Iterator=Iterator;
function $init$Iterator(){
    if (Iterator.$$===undefined){
        initTypeProto(Iterator,'ceylon.language::Iterator');
        (function($$iterator){
        })(Iterator.$$.prototype);
    }
    return Iterator;
}
exports.$init$Iterator=$init$Iterator;
$init$Iterator();
function Collection($$targs$$,$$collection){
    Iterable($$collection.$$targs$$===undefined?$$targs$$:{Absent:{t:Null},Element:$$collection.$$targs$$.Element},$$collection);
    add_type_arg($$collection,'Absent',{t:Null});
    Cloneable($$collection.$$targs$$===undefined?$$targs$$:{Clone:{t:Collection,a:{Element:$$collection.$$targs$$.Element}}},$$collection);
    add_type_arg($$collection,'Clone',{t:Collection,a:{Element:$$collection.$$targs$$.Element}});
    set_type_args($$collection,$$targs$$);
}
Collection.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out',}},satisfies:[{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},{t:Cloneable,a:{Clone:{t:Collection,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:{t:List,a:{}}}),typeLiteral$model({Type:{t:Map,a:{}}}),typeLiteral$model({Type:{t:Set,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Collection']};
exports.Collection=Collection;
function $init$Collection(){
    if (Collection.$$===undefined){
        initTypeProto(Collection,'ceylon.language::Collection',$init$Iterable(),$init$Cloneable());
        (function($$collection){
            defineAttr($$collection,'empty',function(){
                var $$collection=this;
                return $$collection.size.equals((0));
            },undefined,{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Collection,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Collection']['$at']['empty']});
            $$collection.contains=function contains(element$243){
                var $$collection=this;
                var it$244 = $$collection.iterator();
                var elem$245;while ((elem$245=it$244.next())!==getFinished()){
                    var elem$246;
                    if((elem$246=elem$245)!==null&&elem$246.equals(element$243)){
                        return true;
                    }
                }
                if (getFinished() === elem$245){
                    return false;
                }
            };$$collection.contains.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Collection,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Collection']['$m']['contains']};defineAttr($$collection,'string',function(){
                var $$collection=this;
                return (opt$247=($$collection.empty?String$("{}",2):null),opt$247!==null?opt$247:StringBuilder().appendAll([String$("{ ",2),commaList($$collection).string,String$(" }",2)]).string);
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Collection,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Collection']['$at']['string']});
        })(Collection.$$.prototype);
    }
    return Collection;
}
exports.$init$Collection=$init$Collection;
$init$Collection();
var opt$247;
function Category($$category){
}
Category.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:Container,a:{Absent:{t:Null}}}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Category']};
exports.Category=Category;
function $init$Category(){
    if (Category.$$===undefined){
        initTypeProto(Category,'ceylon.language::Category');
        (function($$category){
            $$category.containsEvery=function containsEvery(elements$248){
                var $$category=this;
                var it$249 = elements$248.iterator();
                var element$250;while ((element$250=it$249.next())!==getFinished()){
                    if((!$$category.contains(element$250))){
                        return false;
                    }
                }
                if (getFinished() === element$250){
                    return true;
                }
            };$$category.containsEvery.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Object$}}}}],$cont:Category,$an:function(){return[see([typeLiteral$model({Type:{t:$$category.contains}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:FunctionDeclaration$model$declaration},{t:Method$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Type:{t:Boolean$},Container:{t:Category}}}]}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Category']['$m']['containsEvery']};$$category.containsAny=function containsAny(elements$251){
                var $$category=this;
                var it$252 = elements$251.iterator();
                var element$253;while ((element$253=it$252.next())!==getFinished()){
                    if($$category.contains(element$253)){
                        return true;
                    }
                }
                if (getFinished() === element$253){
                    return false;
                }
            };$$category.containsAny.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Object$}}}}],$cont:Category,$an:function(){return[see([typeLiteral$model({Type:{t:$$category.contains}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:FunctionDeclaration$model$declaration},{t:Method$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Type:{t:Boolean$},Container:{t:Category}}}]}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Category']['$m']['containsAny']};
        })(Category.$$.prototype);
    }
    return Category;
}
exports.$init$Category=$init$Category;
$init$Category();
function List($$targs$$,$$list){
    Collection($$list.$$targs$$===undefined?$$targs$$:{Element:$$list.$$targs$$.Element},$$list);
    Correspondence($$list.$$targs$$===undefined?$$targs$$:{Key:{t:Integer},Item:$$list.$$targs$$.Element},$$list);
    add_type_arg($$list,'Key',{t:Integer});
    Ranged($$list.$$targs$$===undefined?$$targs$$:{Index:{t:Integer},Span:{t:List,a:{Element:$$list.$$targs$$.Element}}},$$list);
    add_type_arg($$list,'Index',{t:Integer});
    add_type_arg($$list,'Span',{t:List,a:{Element:$$list.$$targs$$.Element}});
    Cloneable($$list.$$targs$$===undefined?$$targs$$:{Clone:{t:List,a:{Element:$$list.$$targs$$.Element}}},$$list);
    add_type_arg($$list,'Clone',{t:List,a:{Element:$$list.$$targs$$.Element}});
    set_type_args($$list,$$targs$$);
}
List.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out',}},satisfies:[{t:Collection,a:{Element:'Element'}},{t:Correspondence,a:{Key:{t:Integer},Item:'Element'}},{t:Ranged,a:{Index:{t:Integer},Span:{t:List,a:{Element:'Element'}}}},{t:Cloneable,a:{Clone:{t:List,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:{t:Sequence,a:{}}}),typeLiteral$model({Type:{t:Empty}}),typeLiteral$model({Type:{t:Array$,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']};
exports.List=List;
function $init$List(){
    if (List.$$===undefined){
        initTypeProto(List,'ceylon.language::List',$init$Collection(),$init$Correspondence(),$init$Ranged(),$init$Cloneable());
        (function($$list){
            defineAttr($$list,'size',function(){
                var $$list=this;
                return (opt$254=$$list.lastIndex,opt$254!==null?opt$254:(-(1))).plus((1));
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:List,$an:function(){return[see([typeLiteral$model({Type:{t:List.$$.prototype.$prop$lastIndex,a:{Element:].reifyCeylonType({Absent:{t:Null},Element:{t:ValueDeclaration$model$declaration}})),shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$at']['size']});
            $$list.shorterThan=function (length$255){
                var $$list=this;
                return $$list.size.compare(length$255).equals(getSmaller());
            };
            $$list.shorterThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['shorterThan']};
            $$list.longerThan=function (length$256){
                var $$list=this;
                return $$list.size.compare(length$256).equals(getLarger());
            };
            $$list.longerThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['longerThan']};
            $$list.defines=function (index$257){
                var $$list=this;
                return (index$257.compare((opt$258=$$list.lastIndex,opt$258!==null?opt$258:(-(1))))!==getLarger());
            };
            $$list.defines.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['defines']};
            $$list.iterator=function iterator(){
                var $$list=this;
                function listIterator$259($$targs$$){
                    var $$listIterator$259=new listIterator$259.$$;
                    $$listIterator$259.$$targs$$=$$targs$$;
                    Iterator({Element:$$list.$$targs$$.Element},$$listIterator$259);
                    $$listIterator$259.index$260_=(0);
                    return $$listIterator$259;
                }
                function $init$listIterator$259(){
                    if (listIterator$259.$$===undefined){
                        initTypeProto(listIterator$259,'ceylon.language::List.iterator.listIterator',Basic,$init$Iterator());
                    }
                    return listIterator$259;
                }
                $init$listIterator$259();
                (function($$listIterator$259){
                    defineAttr($$listIterator$259,'index$260',function(){return this.index$260_;},function(index$261){return this.index$260_=index$261;},{mod:$$METAMODEL$$,$t:{t:Integer},$cont:listIterator$259,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['iterator']['$o']['listIterator']['$at']['index']});
                    $$listIterator$259.next=function next(){
                        var $$listIterator$259=this;
                        if(($$listIterator$259.index$260.compare((opt$262=$$list.lastIndex,opt$262!==null?opt$262:(-(1))))!==getLarger())){
                            //assert at List.ceylon (65:20-65:65)
                            var elem$263;
                            if (!(isOfType((elem$263=$$list.get((oldindex$264=$$listIterator$259.index$260,$$listIterator$259.index$260=oldindex$264.successor,oldindex$264))),$$list.$$targs$$.Element))) { throw AssertionException('Assertion failed: \'is Element elem = outer.get(index++)\' at List.ceylon (65:27-65:64)'); }
                            var oldindex$264;
                            return elem$263;
                        }else {
                            return getFinished();
                        }
                        var opt$262;
                    };$$listIterator$259.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:listIterator$259,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['iterator']['$o']['listIterator']['$m']['next']};
                })(listIterator$259.$$.prototype);
                var listIterator$265=listIterator$259({Element:$$list.$$targs$$.Element});
                var getListIterator$265=function(){
                    return listIterator$265;
                }
                return getListIterator$265();
            };$$list.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['iterator']};$$list.equals=function equals(that$266){
                var $$list=this;
                var that$267;
                if(isOfType((that$267=that$266),{t:List,a:{Element:{t:Anything}}})){
                    if(that$267.size.equals($$list.size)){
                        var it$268 = Range((0),$$list.size.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$269;while ((i$269=it$268.next())!==getFinished()){
                            var x$270=$$list.get(i$269);
                            var y$271=that$267.get(i$269);
                            var x$272;
                            if((x$272=x$270)!==null){
                                var y$273;
                                if((y$273=y$271)!==null){
                                    if((!x$272.equals(y$273))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$274;
                                if((y$274=y$271)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$269){
                            return true;
                        }
                    }
                }
                return false;
            };$$list.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['equals']};defineAttr($$list,'hash',function(){
                var $$list=this;
                var hash$275=(1);
                var setHash$275=function(hash$276){return hash$275=hash$276;};
                var it$277 = $$list.iterator();
                var elem$278;while ((elem$278=it$277.next())!==getFinished()){
                    (hash$275=hash$275.times((31)));
                    var elem$279;
                    if((elem$279=elem$278)!==null){
                        (hash$275=hash$275.plus(elem$279.hash));
                    }
                }
                return hash$275;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$at']['hash']});$$list.findLast=function findLast(selecting$280){
                var $$list=this;
                var l$281;
                if((l$281=$$list.lastIndex)!==null){
                    var index$282=l$281;
                    var setIndex$282=function(index$283){return index$282=index$283;};
                    while((index$282.compare((0))!==getSmaller())){
                        var elem$284;
                        if((elem$284=$$list.get((oldindex$285=index$282,index$282=oldindex$285.predecessor,oldindex$285)))!==null){
                            if(selecting$280(elem$284)){
                                return elem$284;
                            }
                        }
                        var oldindex$285;
                    }
                }
                return null;
            };$$list.findLast.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['findLast']};defineAttr($$list,'first',function(){
                var $$list=this;
                return $$list.get((0));
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$at']['first']});
            defineAttr($$list,'last',function(){
                var $$list=this;
                var i$286;
                if((i$286=$$list.lastIndex)!==null){
                    return $$list.get(i$286);
                }
                return null;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:List,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$at']['last']});$$list.withLeading=function withLeading(element$287,$$$mptypes){
                var $$list=this;
                var sb$288=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                sb$288.append(element$287);
                if((!$$list.empty)){
                    sb$288.appendAll($$list);
                }
                //assert at List.ceylon (165:8-165:41)
                var seq$289;
                if (!(nonempty((seq$289=sb$288.sequence)))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (165:15-165:40)'); }
                return seq$289;
            };$$list.withLeading.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$cont:List,$tp:{Other:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['withLeading']};$$list.withTrailing=function withTrailing(element$290,$$$mptypes){
                var $$list=this;
                var sb$291=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                if((!$$list.empty)){
                    sb$291.appendAll($$list);
                }
                sb$291.append(element$290);
                //assert at List.ceylon (180:8-180:41)
                var seq$292;
                if (!(nonempty((seq$292=sb$291.sequence)))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (180:15-180:40)'); }
                return seq$292;
            };$$list.withTrailing.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$cont:List,$tp:{Other:{}},$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['withTrailing']};$$list.occursAtStart=function (list$293){
                var $$list=this;
                return $$list.occursAt((0),list$293);
            };
            $$list.occursAtStart.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'list',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['occursAtStart']};
            $$list.occursAt=function occursAt(index$294,list$295){
                var $$list=this;
                var it$296 = (function(){var tmpvar$298=$$list.size;
                if (tmpvar$298>0){
                var tmpvar$299=index$294;
                var tmpvar$300=tmpvar$299;
                for (var i=1; i<tmpvar$298; i++){tmpvar$300=tmpvar$300.successor;}
                return Range(tmpvar$299,tmpvar$300,{Element:{t:Integer}})
                }else return getEmpty();}()).iterator();
                var i$297;while ((i$297=it$296.next())!==getFinished()){
                    var x$301=$$list.get(i$297);
                    var y$302=list$295.get(i$297);
                    var x$303;
                    if((x$303=x$301)!==null){
                        var y$304;
                        if((y$304=y$302)!==null){
                            if((!x$303.equals(y$304))){
                                return false;
                            }
                        }else {
                            return false;
                        }
                    }else {
                        var y$305;
                        if((y$305=y$302)!==null){
                            return false;
                        }
                    }
                }
                if (getFinished() === i$297){
                    return true;
                }
            };$$list.occursAt.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}},{$nm:'list',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['occursAt']};$$list.occursIn=function occursIn(list$306){
                var $$list=this;
                var it$307 = (function(){var tmpvar$309=list$306.size;
                if (tmpvar$309>0){
                var tmpvar$310=(0);
                var tmpvar$311=tmpvar$310;
                for (var i=1; i<tmpvar$309; i++){tmpvar$311=tmpvar$311.successor;}
                return Range(tmpvar$310,tmpvar$311,{Element:{t:Integer}})
                }else return getEmpty();}()).iterator();
                var i$308;while ((i$308=it$307.next())!==getFinished()){
                    if($$list.occursAtStart(list$306.spanFrom(i$308))){
                        return true;
                    }
                }
                return false;
            };$$list.occursIn.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'list',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['occursIn']};$$list.occurrencesIn=function (list$312){
                var $$list=this;
                return Comprehension(function(){
                    var it$313=(function(){var tmpvar$315=list$312.size;
                    if (tmpvar$315>0){
                    var tmpvar$316=(0);
                    var tmpvar$317=tmpvar$316;
                    for (var i=1; i<tmpvar$315; i++){tmpvar$317=tmpvar$317.successor;}
                    return Range(tmpvar$316,tmpvar$317,{Element:{t:Integer}})
                    }else return getEmpty();}()).iterator();
                    var index$314=getFinished();
                    var next$index$314=function(){
                        while((index$314=it$313.next())!==getFinished()){
                            if($$list.occursAt(index$314,list$312)){
                                return index$314;
                            }
                        }
                        return getFinished();
                    }
                    next$index$314();
                    return function(){
                        if(index$314!==getFinished()){
                            var index$314$318=index$314;
                            var tmpvar$319=index$314$318;
                            next$index$314();
                            return tmpvar$319;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Integer}});
            };
            $$list.occurrencesIn.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Integer}}},$ps:[{$nm:'list',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['occurrencesIn']};
            $$list.indexes=function (selecting$320){
                var $$list=this;
                return Comprehension(function(){
                    var it$321=(function(){var tmpvar$323=$$list.size;
                    if (tmpvar$323>0){
                    var tmpvar$324=(0);
                    var tmpvar$325=tmpvar$324;
                    for (var i=1; i<tmpvar$323; i++){tmpvar$325=tmpvar$325.successor;}
                    return Range(tmpvar$324,tmpvar$325,{Element:{t:Integer}})
                    }else return getEmpty();}()).iterator();
                    var index$322=getFinished();
                    var next$index$322=function(){
                        while((index$322=it$321.next())!==getFinished()){
                            if(selecting$320((opt$326=$$list.get(index$322),opt$326!==null?opt$326:getNothing()))){
                                return index$322;
                            }
                        }
                        return getFinished();
                    }
                    next$index$322();
                    return function(){
                        if(index$322!==getFinished()){
                            var index$322$327=index$322;
                            var tmpvar$328=index$322$327;
                            next$index$322();
                            return tmpvar$328;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Integer}});
            };
            $$list.indexes.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Integer}}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['indexes']};
            $$list.trim=function trim(trimming$329){
                var $$list=this;
                var l$330;
                if((l$330=$$list.lastIndex)!==null){
                    var from$331=(-(1));
                    var setFrom$331=function(from$332){return from$331=from$332;};
                    var to$333=(-(1));
                    var setTo$333=function(to$334){return to$333=to$334;};
                    var it$335 = Range((0),l$330,{Element:{t:Integer}}).iterator();
                    var index$336;while ((index$336=it$335.next())!==getFinished()){
                        if((!trimming$329((opt$337=$$list.get(index$336),opt$337!==null?opt$337:getNothing())))){
                            from$331=index$336;
                            break;
                        }
                        var opt$337;
                    }
                    if (getFinished() === index$336){
                        return getEmpty();
                    }
                    var it$338 = Range(l$330,(0),{Element:{t:Integer}}).iterator();
                    var index$339;while ((index$339=it$338.next())!==getFinished()){
                        if((!trimming$329((opt$340=$$list.get(index$339),opt$340!==null?opt$340:getNothing())))){
                            to$333=index$339;
                            break;
                        }
                        var opt$340;
                    }
                    if (getFinished() === index$339){
                        return getEmpty();
                    }
                    return $$list.span(from$331,to$333);
                }else {
                    return getEmpty();
                }
            };$$list.trim.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['trim']};$$list.trimLeading=function trimLeading(trimming$341){
                var $$list=this;
                var l$342;
                if((l$342=$$list.lastIndex)!==null){
                    var it$343 = Range((0),l$342,{Element:{t:Integer}}).iterator();
                    var index$344;while ((index$344=it$343.next())!==getFinished()){
                        if((!trimming$341((opt$345=$$list.get(index$344),opt$345!==null?opt$345:getNothing())))){
                            return $$list.span(index$344,l$342);
                        }
                        var opt$345;
                    }
                }
                return getEmpty();
            };$$list.trimLeading.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['trimLeading']};$$list.trimTrailing=function trimTrailing(trimming$346){
                var $$list=this;
                var l$347;
                if((l$347=$$list.lastIndex)!==null){
                    var it$348 = Range(l$347,(0),{Element:{t:Integer}}).iterator();
                    var index$349;while ((index$349=it$348.next())!==getFinished()){
                        if((!trimming$346((opt$350=$$list.get(index$349),opt$350!==null?opt$350:getNothing())))){
                            return $$list.span((0),index$349);
                        }
                        var opt$350;
                    }
                }
                return getEmpty();
            };$$list.trimTrailing.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['trimTrailing']};$$list.initial=function (length$351){
                var $$list=this;
                return $$list.segment((0),length$351);
            };
            $$list.initial.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['initial']};
            $$list.terminal=function terminal(length$352){
                var $$list=this;
                var l$353;
                if((l$353=$$list.lastIndex)!==null&&length$352.compare((0)).equals(getLarger())){
                    return $$list.span(l$353.minus(length$352).plus((1)),l$353);
                }else {
                    return getEmpty();
                }
            };$$list.terminal.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['List']['$m']['terminal']};
        })(List.$$.prototype);
    }
    return List;
}
exports.$init$List=$init$List;
$init$List();
var opt$254,opt$258,opt$326;
function Tuple(first, rest, $$targs$$,$$tuple){
    $init$Tuple();
    if ($$tuple===undefined)$$tuple=new Tuple.$$;
    set_type_args($$tuple,$$targs$$);
    $$tuple.first=first;
    $$tuple.rest=rest;
    Object$($$tuple);
    Sequence($$tuple.$$targs$$===undefined?$$targs$$:{Element:$$tuple.$$targs$$.Element},$$tuple);
    Cloneable($$tuple.$$targs$$===undefined?$$targs$$:{Clone:{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}}},$$tuple);
    add_type_arg($$tuple,'Clone',{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}});
    $$tuple.first$354_=first;
    $$tuple.rest$355_=rest;
    return $$tuple;
}
Tuple.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Element:{'var':'out',},First:{'var':'out','satisfies':['Element']},Rest:{'var':'out','satisfies':[{t:Sequential,a:{Element:'Element'}}],'def':{t:Empty}}},satisfies:[{t:Sequence,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Element'}}}}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']};
exports.Tuple=Tuple;
function $init$Tuple(){
    if (Tuple.$$===undefined){
        initTypeProto(Tuple,'ceylon.language::Tuple',Object$,$init$Sequence(),$init$Cloneable());
        (function($$tuple){
            defineAttr($$tuple,'first',function(){return this.first$354_;},undefined,{mod:$$METAMODEL$$,$t:'First',$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$at']['first']});
            defineAttr($$tuple,'rest',function(){return this.rest$355_;},undefined,{mod:$$METAMODEL$$,$t:'Rest',$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$at']['rest']});
            defineAttr($$tuple,'size',function(){
                var $$tuple=this;
                return (1).plus($$tuple.rest.size);
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$at']['size']});
            $$tuple.get=function get(index$356){
                var $$tuple=this;
                
                var case$357=index$356.compare((0));
                if (case$357===getSmaller()) {
                    return null;
                }else if (case$357===getEqual()) {
                    return $$tuple.first;
                }else if (case$357===getLarger()) {
                    return $$tuple.rest.get(index$356.minus((1)));
                }
            };$$tuple.get.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['get']};defineAttr($$tuple,'lastIndex',function(){
                var $$tuple=this;
                var restLastIndex$358;
                if((restLastIndex$358=$$tuple.rest.lastIndex)!==null){
                    return restLastIndex$358.plus((1));
                }else {
                    return (0);
                }
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$at']['lastIndex']});defineAttr($$tuple,'last',function(){
                var $$tuple=this;
                var rest$359;
                if(nonempty((rest$359=$$tuple.rest))){
                    return rest$359.last;
                }else {
                    return $$tuple.first;
                }
            },undefined,{mod:$$METAMODEL$$,$t:'Element',$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$at']['last']});defineAttr($$tuple,'reversed',function(){
                var $$tuple=this;
                return $$tuple.rest.reversed.withTrailing($$tuple.first,{Other:$$tuple.$$targs$$.First});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$at']['reversed']});
            $$tuple.segment=function segment(from$360,length$361){
                var $$tuple=this;
                if((length$361.compare((0))!==getLarger())){
                    return getEmpty();
                }
                var realFrom$362=(opt$363=(from$360.compare((0)).equals(getSmaller())?(0):null),opt$363!==null?opt$363:from$360);
                var opt$363;
                if(realFrom$362.equals((0))){
                    return (opt$364=(length$361.equals((1))?Tuple($$tuple.first,getEmpty(),{Rest:{t:Empty},First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.First}):null),opt$364!==null?opt$364:$$tuple.rest.segment((0),length$361.plus(realFrom$362).minus((1))).withLeading($$tuple.first,{Other:$$tuple.$$targs$$.First}));
                    var opt$364;
                }
                return $$tuple.rest.segment(realFrom$362.minus((1)),length$361);
            };$$tuple.segment.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['segment']};$$tuple.span=function span(from$365,end$366){
                var $$tuple=this;
                if((from$365.compare((0)).equals(getSmaller())&&end$366.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var realFrom$367=(opt$368=(from$365.compare((0)).equals(getSmaller())?(0):null),opt$368!==null?opt$368:from$365);
                var opt$368;
                var realEnd$369=(opt$370=(end$366.compare((0)).equals(getSmaller())?(0):null),opt$370!==null?opt$370:end$366);
                var opt$370;
                return (opt$371=((realFrom$367.compare(realEnd$369)!==getLarger())?$$tuple.segment(from$365,realEnd$369.minus(realFrom$367).plus((1))):null),opt$371!==null?opt$371:$$tuple.segment(realEnd$369,realFrom$367.minus(realEnd$369).plus((1))).reversed.sequence);
                var opt$371;
            };$$tuple.span.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'end',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['span']};$$tuple.spanTo=function (to$372){
                var $$tuple=this;
                return (opt$373=(to$372.compare((0)).equals(getSmaller())?getEmpty():null),opt$373!==null?opt$373:$$tuple.span((0),to$372));
            };
            $$tuple.spanTo.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['spanTo']};
            $$tuple.spanFrom=function (from$374){
                var $$tuple=this;
                return $$tuple.span(from$374,$$tuple.size);
            };
            $$tuple.spanFrom.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['spanFrom']};
            defineAttr($$tuple,'clone',function(){
                var $$tuple=this;
                return $$tuple;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Element'}},$cont:Tuple,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$at']['clone']});
            $$tuple.iterator=function iterator(){
                var $$tuple=this;
                function iterator$375($$targs$$){
                    var $$iterator$375=new iterator$375.$$;
                    $$iterator$375.$$targs$$=$$targs$$;
                    Iterator({Element:$$tuple.$$targs$$.Element},$$iterator$375);
                    $$iterator$375.current$376_=$$tuple;
                    return $$iterator$375;
                }
                function $init$iterator$375(){
                    if (iterator$375.$$===undefined){
                        initTypeProto(iterator$375,'ceylon.language::Tuple.iterator.iterator',Basic,$init$Iterator());
                    }
                    return iterator$375;
                }
                $init$iterator$375();
                (function($$iterator$375){
                    defineAttr($$iterator$375,'current$376',function(){return this.current$376_;},function(current$377){return this.current$376_=current$377;},{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:iterator$375,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['iterator']['$o']['iterator']['$at']['current']});
                    $$iterator$375.next=function next(){
                        var $$iterator$375=this;
                        var c$378;
                        if(nonempty((c$378=$$iterator$375.current$376))){
                            $$iterator$375.current$376=c$378.rest;
                            return c$378.first;
                        }else {
                            return getFinished();
                        }
                    };$$iterator$375.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$375,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['iterator']['$o']['iterator']['$m']['next']};
                })(iterator$375.$$.prototype);
                var iterator$379=iterator$375({Element:$$tuple.$$targs$$.Element});
                var getIterator$379=function(){
                    return iterator$379;
                }
                return getIterator$379();
            };$$tuple.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:Tuple,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['iterator']};$$tuple.contains=function contains(element$380){
                var $$tuple=this;
                var first$381;
                if((first$381=$$tuple.first)!==null&&first$381.equals(element$380)){
                    return true;
                }else {
                    return $$tuple.rest.contains(element$380);
                }
            };$$tuple.contains.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Tuple,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['contains']};$$tuple.withLeading=function (element$382,$$$mptypes){
                var $$tuple=this;
                return Tuple(element$382,$$tuple,{Rest:{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}},First:$$$mptypes.Other,Element:{ t:'u', l:[$$$mptypes.Other,$$tuple.$$targs$$.Element]}});
            };
            $$tuple.withLeading.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Element'}},First:'Other',Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$cont:Tuple,$tp:{Other:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tuple']['$m']['withLeading']};
        })(Tuple.$$.prototype);
    }
    return Tuple;
}
exports.$init$Tuple=$init$Tuple;
$init$Tuple();
var opt$373;
function ChainedIterator(first$383, second$384, $$targs$$,$$chainedIterator){
    $init$ChainedIterator();
    if ($$chainedIterator===undefined)$$chainedIterator=new ChainedIterator.$$;
    set_type_args($$chainedIterator,$$targs$$);
    $$chainedIterator.second$384=second$384;
    Iterator($$chainedIterator.$$targs$$===undefined?$$targs$$:{Element:{ t:'u', l:[$$chainedIterator.$$targs$$.Element,$$chainedIterator.$$targs$$.Other]}},$$chainedIterator);
    $$chainedIterator.iter$385_=first$383.iterator();
    $$chainedIterator.more$386_=true;
    return $$chainedIterator;
}
ChainedIterator.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Element:{'var':'out',},Other:{'var':'out',}},satisfies:[{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}}],$an:function(){return[by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}}))];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['ChainedIterator']};
function $init$ChainedIterator(){
    if (ChainedIterator.$$===undefined){
        initTypeProto(ChainedIterator,'ceylon.language::ChainedIterator',Basic,$init$Iterator());
        (function($$chainedIterator){
            defineAttr($$chainedIterator,'iter$385',function(){return this.iter$385_;},function(iter$387){return this.iter$385_=iter$387;},{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$cont:ChainedIterator,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['ChainedIterator']['$at']['iter']});
            defineAttr($$chainedIterator,'more$386',function(){return this.more$386_;},function(more$388){return this.more$386_=more$388;},{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:ChainedIterator,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['ChainedIterator']['$at']['more']});
            $$chainedIterator.next=function next(){
                var $$chainedIterator=this;
                var e$389=$$chainedIterator.iter$385.next();
                var setE$389=function(e$390){return e$389=e$390;};
                var f$391;
                if(isOfType((f$391=e$389),{t:Finished})){
                    if($$chainedIterator.more$386){
                        $$chainedIterator.iter$385=$$chainedIterator.second$384.iterator();
                        $$chainedIterator.more$386=false;
                        e$389=$$chainedIterator.iter$385.next();
                    }
                }
                return e$389;
            };$$chainedIterator.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element','Other',{t:Finished}]},$ps:[],$cont:ChainedIterator,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['ChainedIterator']['$m']['next']};
        })(ChainedIterator.$$.prototype);
    }
    return ChainedIterator;
}
exports.$init$ChainedIterator=$init$ChainedIterator;
$init$ChainedIterator();
function Entry(key, item, $$targs$$,$$entry){
    $init$Entry();
    if ($$entry===undefined)$$entry=new Entry.$$;
    set_type_args($$entry,$$targs$$);
    $$entry.key=key;
    $$entry.item=item;
    Object$($$entry);
    $$entry.key$392_=key;
    $$entry.item$393_=item;
    return $$entry;
}
Entry.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Entry']};
exports.Entry=Entry;
function $init$Entry(){
    if (Entry.$$===undefined){
        initTypeProto(Entry,'ceylon.language::Entry',Object$);
        (function($$entry){
            defineAttr($$entry,'key',function(){return this.key$392_;},undefined,{mod:$$METAMODEL$$,$t:'Key',$cont:Entry,$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Entry']['$at']['key']});
            defineAttr($$entry,'item',function(){return this.item$393_;},undefined,{mod:$$METAMODEL$$,$t:'Item',$cont:Entry,$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Entry']['$at']['item']});
            $$entry.equals=function equals(that$394){
                var $$entry=this;
                var that$395;
                if(isOfType((that$395=that$394),{t:Entry,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    return ($$entry.key.equals(that$395.key)&&$$entry.item.equals(that$395.item));
                }else {
                    return false;
                }
            };$$entry.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Entry,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Entry']['$m']['equals']};defineAttr($$entry,'hash',function(){
                var $$entry=this;
                return (31).plus($$entry.key.hash).times((31)).plus($$entry.item.hash);
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Entry,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Entry']['$at']['hash']});
            defineAttr($$entry,'string',function(){
                var $$entry=this;
                return StringBuilder().appendAll([$$entry.key.string,String$("->",2),$$entry.item.string]).string;
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Entry,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Entry']['$at']['string']});
        })(Entry.$$.prototype);
    }
    return Entry;
}
exports.$init$Entry=$init$Entry;
$init$Entry();
function Comparable($$targs$$,$$comparable){
    set_type_args($$comparable,$$targs$$);
}
Comparable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'var':'in','satisfies':[{t:Comparable,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Comparable']};
exports.Comparable=Comparable;
function $init$Comparable(){
    if (Comparable.$$===undefined){
        initTypeProto(Comparable,'ceylon.language::Comparable');
        (function($$comparable){
        })(Comparable.$$.prototype);
    }
    return Comparable;
}
exports.$init$Comparable=$init$Comparable;
$init$Comparable();
function Invertable($$targs$$,$$invertable){
    set_type_args($$invertable,$$targs$$);
}
Invertable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Inverse:{'var':'out','satisfies':[{t:Invertable,a:{Inverse:'Inverse'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:Integer}}),typeLiteral$model({Type:{t:Float}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Integer},Element:{t:Integer}}},Type:{t:Integer}}}]},{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Float},Element:{t:Float}}},Type:{t:Float}}}]}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Invertable']};
exports.Invertable=Invertable;
function $init$Invertable(){
    if (Invertable.$$===undefined){
        initTypeProto(Invertable,'ceylon.language::Invertable');
        (function($$invertable){
        })(Invertable.$$.prototype);
    }
    return Invertable;
}
exports.$init$Invertable=$init$Invertable;
$init$Invertable();
function Summable($$targs$$,$$summable){
    set_type_args($$summable,$$targs$$);
}
Summable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Summable,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:String$}}),typeLiteral$model({Type:{t:Numeric,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Character}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Character}}}}},Type:{t:String$}}}]},{t:InterfaceDeclaration$model$declaration}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Summable']};
exports.Summable=Summable;
function $init$Summable(){
    if (Summable.$$===undefined){
        initTypeProto(Summable,'ceylon.language::Summable');
        (function($$summable){
        })(Summable.$$.prototype);
    }
    return Summable;
}
exports.$init$Summable=$init$Summable;
$init$Summable();
function Ordinal($$targs$$,$$ordinal){
    set_type_args($$ordinal,$$targs$$);
}
Ordinal.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'var':'out','satisfies':[{t:Ordinal,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:Character}}),typeLiteral$model({Type:{t:Integer}}),typeLiteral$model({Type:{t:Integral,a:{}}}),typeLiteral$model({Type:{t:Range,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Ordinal']};
exports.Ordinal=Ordinal;
function $init$Ordinal(){
    if (Ordinal.$$===undefined){
        initTypeProto(Ordinal,'ceylon.language::Ordinal');
        (function($$ordinal){
        })(Ordinal.$$.prototype);
    }
    return Ordinal;
}
exports.$init$Ordinal=$init$Ordinal;
$init$Ordinal();
function Enumerable($$targs$$,$$enumerable){
    Ordinal($$enumerable.$$targs$$===undefined?$$targs$$:{Other:$$enumerable.$$targs$$.Other},$$enumerable);
    set_type_args($$enumerable,$$targs$$);
}
Enumerable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'var':'out','satisfies':[{t:Enumerable,a:{Other:'Other'}}]}},satisfies:[{t:Ordinal,a:{Other:'Other'}}],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Enumerable']};
exports.Enumerable=Enumerable;
function $init$Enumerable(){
    if (Enumerable.$$===undefined){
        initTypeProto(Enumerable,'ceylon.language::Enumerable',$init$Ordinal());
        (function($$enumerable){
        })(Enumerable.$$.prototype);
    }
    return Enumerable;
}
exports.$init$Enumerable=$init$Enumerable;
$init$Enumerable();
function Numeric($$targs$$,$$numeric){
    Summable($$numeric.$$targs$$===undefined?$$targs$$:{Other:$$numeric.$$targs$$.Other},$$numeric);
    Invertable($$numeric.$$targs$$===undefined?$$targs$$:{Inverse:$$numeric.$$targs$$.Other},$$numeric);
    set_type_args($$numeric,$$targs$$);
}
Numeric.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Numeric,a:{Other:'Other'}}]}},satisfies:[{t:Summable,a:{Other:'Other'}},{t:Invertable,a:{Inverse:'Other'}}],$an:function(){return[see([typeLiteral$model({Type:{t:Integer}}),typeLiteral$model({Type:{t:Float}}),typeLiteral$model({Type:{t:Comparable,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Integer},Element:{t:Integer}}},Type:{t:Integer}}}]},{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Float},Element:{t:Float}}},Type:{t:Float}}}]},{t:InterfaceDeclaration$model$declaration}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Numeric']};
exports.Numeric=Numeric;
function $init$Numeric(){
    if (Numeric.$$===undefined){
        initTypeProto(Numeric,'ceylon.language::Numeric',$init$Summable(),$init$Invertable());
        (function($$numeric){
        })(Numeric.$$.prototype);
    }
    return Numeric;
}
exports.$init$Numeric=$init$Numeric;
$init$Numeric();
function Scalar($$targs$$,$$scalar){
    Numeric($$scalar.$$targs$$===undefined?$$targs$$:{Other:$$scalar.$$targs$$.Other},$$scalar);
    Comparable($$scalar.$$targs$$===undefined?$$targs$$:{Other:$$scalar.$$targs$$.Other},$$scalar);
    Number($$scalar);
    set_type_args($$scalar,$$targs$$);
}
Scalar.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Scalar,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'Other'}},{t:Comparable,a:{Other:'Other'}},{t:Number$}],$an:function(){return[see([typeLiteral$model({Type:{t:Integer}}),typeLiteral$model({Type:{t:Float}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Integer},Element:{t:Integer}}},Type:{t:Integer}}}]},{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Float},Element:{t:Float}}},Type:{t:Float}}}]}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Scalar']};
exports.Scalar=Scalar;
function $init$Scalar(){
    if (Scalar.$$===undefined){
        initTypeProto(Scalar,'ceylon.language::Scalar',$init$Numeric(),$init$Comparable(),$init$Number$());
        (function($$scalar){
        })(Scalar.$$.prototype);
    }
    return Scalar;
}
exports.$init$Scalar=$init$Scalar;
$init$Scalar();
function Exponentiable($$targs$$,$$exponentiable){
    Numeric($$exponentiable.$$targs$$===undefined?$$targs$$:{Other:$$exponentiable.$$targs$$.This},$$exponentiable);
    set_type_args($$exponentiable,$$targs$$);
}
Exponentiable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{This:{'satisfies':[{t:Exponentiable,a:{Other:'Other',This:'This'}}]},Other:{'satisfies':[{t:Numeric,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'This'}}],$an:function(){return[see([typeLiteral$model({Type:{t:Integer}}),typeLiteral$model({Type:{t:Float}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Integer},Element:{t:Integer}}},Type:{t:Integer}}}]},{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Float},Element:{t:Float}}},Type:{t:Float}}}]}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Exponentiable']};
exports.Exponentiable=Exponentiable;
function $init$Exponentiable(){
    if (Exponentiable.$$===undefined){
        initTypeProto(Exponentiable,'ceylon.language::Exponentiable',$init$Numeric());
        (function($$exponentiable){
        })(Exponentiable.$$.prototype);
    }
    return Exponentiable;
}
exports.$init$Exponentiable=$init$Exponentiable;
$init$Exponentiable();
function Integral($$targs$$,$$integral){
    Numeric($$integral.$$targs$$===undefined?$$targs$$:{Other:$$integral.$$targs$$.Other},$$integral);
    Enumerable($$integral.$$targs$$===undefined?$$targs$$:{Other:$$integral.$$targs$$.Other},$$integral);
    set_type_args($$integral,$$targs$$);
}
Integral.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Integral,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'Other'}},{t:Enumerable,a:{Other:'Other'}}],$an:function(){return[see([typeLiteral$model({Type:{t:Integer}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:ClassDeclaration$model$declaration},{t:Class$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Integer},Element:{t:Integer}}},Type:{t:Integer}}}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Integral']};
exports.Integral=Integral;
function $init$Integral(){
    if (Integral.$$===undefined){
        initTypeProto(Integral,'ceylon.language::Integral',$init$Numeric(),$init$Enumerable());
        (function($$integral){
        })(Integral.$$.prototype);
    }
    return Integral;
}
exports.$init$Integral=$init$Integral;
$init$Integral();
function Scalable($$targs$$,$$scalable){
    set_type_args($$scalable,$$targs$$);
}
Scalable.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Scale:{'var':'in',},Value:{'var':'out','satisfies':[{t:Scalable,a:{Value:'Value',Scale:'Scale'}}]}},satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Scalable']};
exports.Scalable=Scalable;
function $init$Scalable(){
    if (Scalable.$$===undefined){
        initTypeProto(Scalable,'ceylon.language::Scalable');
        (function($$scalable){
        })(Scalable.$$.prototype);
    }
    return Scalable;
}
exports.$init$Scalable=$init$Scalable;
$init$Scalable();
function NegativeNumberException($$negativeNumberException){
    $init$NegativeNumberException();
    if ($$negativeNumberException===undefined)$$negativeNumberException=new NegativeNumberException.$$;
    Exception(String$("Negative number",15),null,$$negativeNumberException);
    return $$negativeNumberException;
}
NegativeNumberException.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['NegativeNumberException']};
exports.NegativeNumberException=NegativeNumberException;
function $init$NegativeNumberException(){
    if (NegativeNumberException.$$===undefined){
        initTypeProto(NegativeNumberException,'ceylon.language::NegativeNumberException',Exception);
    }
    return NegativeNumberException;
}
exports.$init$NegativeNumberException=$init$NegativeNumberException;
$init$NegativeNumberException();
function OverflowException($$overflowException){
    $init$OverflowException();
    if ($$overflowException===undefined)$$overflowException=new OverflowException.$$;
    Exception(String$("Numeric overflow",16),null,$$overflowException);
    return $$overflowException;
}
OverflowException.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['OverflowException']};
exports.OverflowException=OverflowException;
function $init$OverflowException(){
    if (OverflowException.$$===undefined){
        initTypeProto(OverflowException,'ceylon.language::OverflowException',Exception);
    }
    return OverflowException;
}
exports.$init$OverflowException=$init$OverflowException;
$init$OverflowException();
function InitializationException(description$396, $$initializationException){
    $init$InitializationException();
    if ($$initializationException===undefined)$$initializationException=new InitializationException.$$;
    Exception(description$396,null,$$initializationException);
    return $$initializationException;
}
InitializationException.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:{t:late}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:FunctionDeclaration$model$declaration},{t:Function$model,a:{Arguments:{t:Empty},Type:{t:Late}}}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['InitializationException']};
exports.InitializationException=InitializationException;
function $init$InitializationException(){
    if (InitializationException.$$===undefined){
        initTypeProto(InitializationException,'ceylon.language::InitializationException',Exception);
    }
    return InitializationException;
}
exports.$init$InitializationException=$init$InitializationException;
$init$InitializationException();
function Set($$targs$$,$$set){
    Collection($$set.$$targs$$===undefined?$$targs$$:{Element:$$set.$$targs$$.Element},$$set);
    Cloneable($$set.$$targs$$===undefined?$$targs$$:{Clone:{t:Set,a:{Element:$$set.$$targs$$.Element}}},$$set);
    add_type_arg($$set,'Clone',{t:Set,a:{Element:$$set.$$targs$$.Element}});
    set_type_args($$set,$$targs$$);
}
Set.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Element:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Collection,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Set,a:{Element:'Element'}}}}],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Set']};
exports.Set=Set;
function $init$Set(){
    if (Set.$$===undefined){
        initTypeProto(Set,'ceylon.language::Set',$init$Collection(),$init$Cloneable());
        (function($$set){
            $$set.superset=function superset(set$397){
                var $$set=this;
                var it$398 = set$397.iterator();
                var element$399;while ((element$399=it$398.next())!==getFinished()){
                    if((!$$set.contains(element$399))){
                        return false;
                    }
                }
                if (getFinished() === element$399){
                    return true;
                }
            };$$set.superset.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:{t:Object$}}}}],$cont:Set,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Set']['$m']['superset']};$$set.subset=function subset(set$400){
                var $$set=this;
                var it$401 = $$set.iterator();
                var element$402;while ((element$402=it$401.next())!==getFinished()){
                    if((!set$400.contains(element$402))){
                        return false;
                    }
                }
                if (getFinished() === element$402){
                    return true;
                }
            };$$set.subset.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:{t:Object$}}}}],$cont:Set,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Set']['$m']['subset']};$$set.equals=function equals(that$403){
                var $$set=this;
                var that$404;
                if(isOfType((that$404=that$403),{t:Set,a:{Element:{t:Object$}}})&&that$404.size.equals($$set.size)){
                    var it$405 = $$set.iterator();
                    var element$406;while ((element$406=it$405.next())!==getFinished()){
                        if((!that$404.contains(element$406))){
                            return false;
                        }
                    }
                    if (getFinished() === element$406){
                        return true;
                    }
                }
                return false;
            };$$set.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Set,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Set']['$m']['equals']};defineAttr($$set,'hash',function(){
                var $$set=this;
                var hashCode$407=(1);
                var setHashCode$407=function(hashCode$408){return hashCode$407=hashCode$408;};
                var it$409 = $$set.iterator();
                var elem$410;while ((elem$410=it$409.next())!==getFinished()){
                    (hashCode$407=hashCode$407.times((31)));
                    (hashCode$407=hashCode$407.plus(elem$410.hash));
                }
                return hashCode$407;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Set,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Set']['$at']['hash']});
        })(Set.$$.prototype);
    }
    return Set;
}
exports.$init$Set=$init$Set;
$init$Set();
function Range(first, last, $$targs$$,$$range){
    $init$Range();
    if ($$range===undefined)$$range=new Range.$$;
    set_type_args($$range,$$targs$$);
    $$range.first=first;
    $$range.last=last;
    Object$($$range);
    Sequence($$range.$$targs$$===undefined?$$targs$$:{Element:$$range.$$targs$$.Element},$$range);
    Cloneable($$range.$$targs$$===undefined?$$targs$$:{Clone:{t:Range,a:{Element:$$range.$$targs$$.Element}}},$$range);
    add_type_arg($$range,'Clone',{t:Range,a:{Element:$$range.$$targs$$.Element}});
    $$range.first$411_=first;
    $$range.last$412_=last;
    return $$range;
}
Range.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Element:{'satisfies':[{t:Ordinal,a:{Other:'Element'}},{t:Comparable,a:{Other:'Element'}}]}},satisfies:[{t:Sequence,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Range,a:{Element:'Element'}}}}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']};
exports.Range=Range;
function $init$Range(){
    if (Range.$$===undefined){
        initTypeProto(Range,'ceylon.language::Range',Object$,$init$Sequence(),$init$Cloneable());
        (function($$range){
            defineAttr($$range,'first',function(){return this.first$411_;},undefined,{mod:$$METAMODEL$$,$t:'Element',$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['first']});
            defineAttr($$range,'last',function(){return this.last$412_;},undefined,{mod:$$METAMODEL$$,$t:'Element',$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['last']});
            defineAttr($$range,'string',function(){
                var $$range=this;
                return $$range.first.string.plus(String$("..",2)).plus($$range.last.string);
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['string']});
            defineAttr($$range,'decreasing',function(){
                var $$range=this;
                return $$range.last.compare($$range.first).equals(getSmaller());
            },undefined,{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Range,$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['decreasing']});
            $$range.next$413=function (x$414){
                var $$range=this;
                return (opt$415=($$range.decreasing?x$414.predecessor:null),opt$415!==null?opt$415:x$414.successor);
            };
            $$range.next$413.$$metamodel$$={mod:$$METAMODEL$$,$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'}],$cont:Range,pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['next']};
            defineAttr($$range,'size',function(){
                var $$range=this;
                var last$416;
                var first$417;
                if(isOfType((last$416=$$range.last),{t:Enumerable,a:{Other:{t:Anything}}})&&isOfType((first$417=$$range.first),{t:Enumerable,a:{Other:{t:Anything}}})){
                    return last$416.integerValue.minus(first$417.integerValue).magnitude.plus((1));
                }else {
                    var size$418=(1);
                    var setSize$418=function(size$419){return size$418=size$419;};
                    var current$420=$$range.first;
                    var setCurrent$420=function(current$421){return current$420=current$421;};
                    while((!current$420.equals($$range.last))){
                        (oldsize$422=size$418,size$418=oldsize$422.successor,oldsize$422);
                        var oldsize$422;
                        current$420=$$range.next$413(current$420);
                    }
                    return size$418;
                }
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['size']});defineAttr($$range,'lastIndex',function(){
                var $$range=this;
                return $$range.size.minus((1));
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['lastIndex']});
            defineAttr($$range,'rest',function(){
                var $$range=this;
                if($$range.size.equals((1))){
                    return getEmpty();
                }
                var n$423=$$range.next$413($$range.first);
                return (opt$424=(n$423.equals($$range.last)?getEmpty():null),opt$424!==null?opt$424:Range(n$423,$$range.last,{Element:$$range.$$targs$$.Element}));
                var opt$424;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['rest']});$$range.get=function get(n$425){
                var $$range=this;
                var index$426=(0);
                var setIndex$426=function(index$427){return index$426=index$427;};
                var x$428=$$range.first;
                var setX$428=function(x$429){return x$428=x$429;};
                while(index$426.compare(n$425).equals(getSmaller())){
                    if(x$428.equals($$range.last)){
                        return null;
                    }else {
                        (index$426=index$426.successor);
                        x$428=$$range.next$413(x$428);
                    }
                }
                return x$428;
            };$$range.get.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'n',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['get']};$$range.iterator=function iterator(){
                var $$range=this;
                function RangeIterator$430($$rangeIterator$430){
                    $init$RangeIterator$430();
                    if ($$rangeIterator$430===undefined)$$rangeIterator$430=new RangeIterator$430.$$;
                    $$rangeIterator$430.$$targs$$={Element:$$range.$$targs$$.Element};
                    Iterator({Element:$$range.$$targs$$.Element},$$rangeIterator$430);
                    $$rangeIterator$430.current$431_=$$range.first;
                    return $$rangeIterator$430;
                }
                RangeIterator$430.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:Iterator,a:{Element:'Element'}}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['iterator']['$c']['RangeIterator']};
                function $init$RangeIterator$430(){
                    if (RangeIterator$430.$$===undefined){
                        initTypeProto(RangeIterator$430,'ceylon.language::Range.iterator.RangeIterator',Basic,$init$Iterator());
                        (function($$rangeIterator$430){
                            defineAttr($$rangeIterator$430,'current$431',function(){return this.current$431_;},function(current$432){return this.current$431_=current$432;},{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$cont:RangeIterator$430,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['iterator']['$c']['RangeIterator']['$at']['current']});
                            $$rangeIterator$430.next=function next(){
                                var $$rangeIterator$430=this;
                                var result$433=$$rangeIterator$430.current$431;
                                var curr$434;
                                if(!isOfType((curr$434=$$rangeIterator$430.current$431),{t:Finished})){
                                    if((opt$435=($$range.decreasing?(curr$434.compare($$range.last)!==getLarger()):null),opt$435!==null?opt$435:(curr$434.compare($$range.last)!==getSmaller()))){
                                        $$rangeIterator$430.current$431=getFinished();
                                    }else {
                                        $$rangeIterator$430.current$431=$$range.next$413(curr$434);
                                    }
                                    var opt$435;
                                }
                                return result$433;
                            };$$rangeIterator$430.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:RangeIterator$430,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['iterator']['$c']['RangeIterator']['$m']['next']};defineAttr($$rangeIterator$430,'string',function(){
                                var $$rangeIterator$430=this;
                                return String$("RangeIterator",13);
                            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:RangeIterator$430,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['iterator']['$c']['RangeIterator']['$at']['string']});
                        })(RangeIterator$430.$$.prototype);
                    }
                    return RangeIterator$430;
                }
                $init$RangeIterator$430();
                return RangeIterator$430();
            };$$range.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['iterator']};$$range.by=function by(step$436){
                var $$range=this;
                //assert at Range.ceylon (113:8-114:25)
                if (!(step$436.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Range.ceylon (114:15-114:24)'); }
                if(step$436.equals((1))){
                    return $$range;
                }
                var first$437;
                var last$438;
                if(isOfType((first$437=$$range.first),{t:Integer})&&isOfType((last$438=$$range.last),{t:Integer})){
                    return integerRangeByIterable($$range,step$436,{Element:$$range.$$targs$$.Element});
                }
                return $$range.getT$all()['ceylon.language::Iterable'].$$.prototype.by.call(this,step$436);
            };$$range.by.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Element'}},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['by']};$$range.contains=function contains(element$439){
                var $$range=this;
                var element$440;
                if(isOfType((element$440=element$439),$$range.$$targs$$.Element)){
                    return $$range.includes(element$440);
                }else {
                    return false;
                }
            };$$range.contains.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['contains']};$$range.count=function count(selecting$441){
                var $$range=this;
                var e$442=$$range.first;
                var setE$442=function(e$443){return e$442=e$443;};
                var c$444=(0);
                var setC$444=function(c$445){return c$444=c$445;};
                while($$range.includes(e$442)){
                    if(selecting$441(e$442)){
                        (oldc$446=c$444,c$444=oldc$446.successor,oldc$446);
                        var oldc$446;
                    }
                    e$442=$$range.next$413(e$442);
                }
                return c$444;
            };$$range.count.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['count']};$$range.includes=function (x$447){
                var $$range=this;
                return (opt$448=($$range.decreasing?((x$447.compare($$range.first)!==getLarger())&&(x$447.compare($$range.last)!==getSmaller())):null),opt$448!==null?opt$448:((x$447.compare($$range.first)!==getSmaller())&&(x$447.compare($$range.last)!==getLarger())));
            };
            $$range.includes.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'x',$mt:'prm',$t:'Element'}],$cont:Range,$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['includes']};
            $$range.equals=function equals(that$449){
                var $$range=this;
                var that$450;
                if(isOfType((that$450=that$449),{t:Range,a:{Element:{t:Object$}}})){
                    return (that$450.first.equals($$range.first)&&that$450.last.equals($$range.last));
                }else {
                    return $$range.getT$all()['ceylon.language::List'].$$.prototype.equals.call(this,that$449);
                }
            };$$range.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['equals']};defineAttr($$range,'clone',function(){
                var $$range=this;
                return $$range;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['clone']});
            $$range.segment=function segment(from$451,length$452){
                var $$range=this;
                if(((length$452.compare((0))!==getLarger())||from$451.compare($$range.lastIndex).equals(getLarger()))){
                    return getEmpty();
                }
                var x$453=$$range.first;
                var setX$453=function(x$454){return x$453=x$454;};
                var i$455=(0);
                var setI$455=function(i$456){return i$455=i$456;};
                while((oldi$457=i$455,i$455=oldi$457.successor,oldi$457).compare(from$451).equals(getSmaller())){
                    x$453=$$range.next$413(x$453);
                }
                var oldi$457;
                var y$458=x$453;
                var setY$458=function(y$459){return y$458=y$459;};
                var j$460=(1);
                var setJ$460=function(j$461){return j$460=j$461;};
                while(((oldj$462=j$460,j$460=oldj$462.successor,oldj$462).compare(length$452).equals(getSmaller())&&y$458.compare($$range.last).equals(getSmaller()))){
                    y$458=$$range.next$413(y$458);
                }
                var oldj$462;
                return Range(x$453,y$458,{Element:$$range.$$targs$$.Element});
            };$$range.segment.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['segment']};$$range.span=function span(from$463,to$464){
                var $$range=this;
                var toIndex$465=to$464;
                var setToIndex$465=function(toIndex$466){return toIndex$465=toIndex$466;};
                var fromIndex$467=from$463;
                var setFromIndex$467=function(fromIndex$468){return fromIndex$467=fromIndex$468;};
                if(toIndex$465.compare((0)).equals(getSmaller())){
                    if(fromIndex$467.compare((0)).equals(getSmaller())){
                        return getEmpty();
                    }
                    toIndex$465=(0);
                }else {
                    if(toIndex$465.compare($$range.lastIndex).equals(getLarger())){
                        if(fromIndex$467.compare($$range.lastIndex).equals(getLarger())){
                            return getEmpty();
                        }
                        toIndex$465=$$range.lastIndex;
                    }
                }
                if(fromIndex$467.compare((0)).equals(getSmaller())){
                    fromIndex$467=(0);
                }else {
                    if(fromIndex$467.compare($$range.lastIndex).equals(getLarger())){
                        fromIndex$467=$$range.lastIndex;
                    }
                }
                var x$469=$$range.first;
                var setX$469=function(x$470){return x$469=x$470;};
                var i$471=(0);
                var setI$471=function(i$472){return i$471=i$472;};
                while((oldi$473=i$471,i$471=oldi$473.successor,oldi$473).compare(fromIndex$467).equals(getSmaller())){
                    x$469=$$range.next$413(x$469);
                }
                var oldi$473;
                var y$474=$$range.first;
                var setY$474=function(y$475){return y$474=y$475;};
                var j$476=(0);
                var setJ$476=function(j$477){return j$476=j$477;};
                while((oldj$478=j$476,j$476=oldj$478.successor,oldj$478).compare(toIndex$465).equals(getSmaller())){
                    y$474=$$range.next$413(y$474);
                }
                var oldj$478;
                return Range(x$469,y$474,{Element:$$range.$$targs$$.Element});
            };$$range.span.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['span']};$$range.spanTo=function spanTo(to$479){
                var $$range=this;
                return (opt$480=(to$479.compare((0)).equals(getSmaller())?getEmpty():null),opt$480!==null?opt$480:$$range.span((0),to$479));
                var opt$480;
            };$$range.spanTo.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['spanTo']};$$range.spanFrom=function spanFrom(from$481){
                var $$range=this;
                return $$range.span(from$481,$$range.size);
            };$$range.spanFrom.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['spanFrom']};defineAttr($$range,'reversed',function(){
                var $$range=this;
                return Range($$range.last,$$range.first,{Element:$$range.$$targs$$.Element});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['reversed']});
            $$range.skipping=function skipping(skip$482){
                var $$range=this;
                var x$483=(0);
                var setX$483=function(x$484){return x$483=x$484;};
                var e$485=$$range.first;
                var setE$485=function(e$486){return e$485=e$486;};
                while((oldx$487=x$483,x$483=oldx$487.successor,oldx$487).compare(skip$482).equals(getSmaller())){
                    e$485=$$range.next$413(e$485);
                }
                var oldx$487;
                return (opt$488=($$range.includes(e$485)?Range(e$485,$$range.last,{Element:$$range.$$targs$$.Element}):null),opt$488!==null?opt$488:getEmpty());
                var opt$488;
            };$$range.skipping.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['skipping']};$$range.taking=function taking(take$489){
                var $$range=this;
                if(take$489.equals((0))){
                    return getEmpty();
                }
                var x$490=(0);
                var setX$490=function(x$491){return x$490=x$491;};
                var e$492=$$range.first;
                var setE$492=function(e$493){return e$492=e$493;};
                while((x$490=x$490.successor).compare(take$489).equals(getSmaller())){
                    e$492=$$range.next$413(e$492);
                }
                return (opt$494=($$range.includes(e$492)?Range($$range.first,e$492,{Element:$$range.$$targs$$.Element}):null),opt$494!==null?opt$494:$$range);
                var opt$494;
            };$$range.taking.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$m']['taking']};defineAttr($$range,'coalesced',function(){
                var $$range=this;
                return $$range;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['coalesced']});
            defineAttr($$range,'sequence',function(){
                var $$range=this;
                return $$range;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Range']['$at']['sequence']});
        })(Range.$$.prototype);
    }
    return Range;
}
exports.$init$Range=$init$Range;
$init$Range();
var opt$415,opt$448;
function Singleton(element$495, $$targs$$,$$singleton){
    $init$Singleton();
    if ($$singleton===undefined)$$singleton=new Singleton.$$;
    set_type_args($$singleton,$$targs$$);
    $$singleton.element$495=element$495;
    Object$($$singleton);
    Sequence($$singleton.$$targs$$===undefined?$$targs$$:{Element:$$singleton.$$targs$$.Element},$$singleton);
    return $$singleton;
}
Singleton.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Element:{'var':'out',}},satisfies:[{t:Sequence,a:{Element:'Element'}}],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']};
exports.Singleton=Singleton;
function $init$Singleton(){
    if (Singleton.$$===undefined){
        initTypeProto(Singleton,'ceylon.language::Singleton',Object$,$init$Sequence());
        (function($$singleton){
            defineAttr($$singleton,'lastIndex',function(){
                var $$singleton=this;
                return (0);
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['lastIndex']});
            defineAttr($$singleton,'size',function(){
                var $$singleton=this;
                return (1);
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['size']});
            defineAttr($$singleton,'first',function(){
                var $$singleton=this;
                return $$singleton.element$495;
            },undefined,{mod:$$METAMODEL$$,$t:'Element',$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['first']});
            defineAttr($$singleton,'last',function(){
                var $$singleton=this;
                return $$singleton.element$495;
            },undefined,{mod:$$METAMODEL$$,$t:'Element',$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['last']});
            defineAttr($$singleton,'rest',function(){
                var $$singleton=this;
                return getEmpty();
            },undefined,{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['rest']});
            $$singleton.get=function get(index$496){
                var $$singleton=this;
                if(index$496.equals((0))){
                    return $$singleton.element$495;
                }else {
                    return null;
                }
            };$$singleton.get.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['get']};defineAttr($$singleton,'clone',function(){
                var $$singleton=this;
                return $$singleton;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Singleton,a:{Element:'Element'}},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['clone']});
            $$singleton.iterator=function iterator(){
                var $$singleton=this;
                function SingletonIterator$497($$singletonIterator$497){
                    $init$SingletonIterator$497();
                    if ($$singletonIterator$497===undefined)$$singletonIterator$497=new SingletonIterator$497.$$;
                    $$singletonIterator$497.$$targs$$={Element:$$singleton.$$targs$$.Element};
                    Iterator({Element:$$singleton.$$targs$$.Element},$$singletonIterator$497);
                    $$singletonIterator$497.done$498_=false;
                    return $$singletonIterator$497;
                }
                SingletonIterator$497.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:Iterator,a:{Element:'Element'}}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['iterator']['$c']['SingletonIterator']};
                function $init$SingletonIterator$497(){
                    if (SingletonIterator$497.$$===undefined){
                        initTypeProto(SingletonIterator$497,'ceylon.language::Singleton.iterator.SingletonIterator',Basic,$init$Iterator());
                        (function($$singletonIterator$497){
                            defineAttr($$singletonIterator$497,'done$498',function(){return this.done$498_;},function(done$499){return this.done$498_=done$499;},{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:SingletonIterator$497,$an:function(){return[variable()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['iterator']['$c']['SingletonIterator']['$at']['done']});
                            $$singletonIterator$497.next=function next(){
                                var $$singletonIterator$497=this;
                                if($$singletonIterator$497.done$498){
                                    return getFinished();
                                }else {
                                    $$singletonIterator$497.done$498=true;
                                    return $$singleton.element$495;
                                }
                            };$$singletonIterator$497.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:SingletonIterator$497,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['iterator']['$c']['SingletonIterator']['$m']['next']};defineAttr($$singletonIterator$497,'string',function(){
                                var $$singletonIterator$497=this;
                                return String$("SingletonIterator",17);
                            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:SingletonIterator$497,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['iterator']['$c']['SingletonIterator']['$at']['string']});
                        })(SingletonIterator$497.$$.prototype);
                    }
                    return SingletonIterator$497;
                }
                $init$SingletonIterator$497();
                return SingletonIterator$497();
            };$$singleton.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:Singleton,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['iterator']};defineAttr($$singleton,'string',function(){
                var $$singleton=this;
                return StringBuilder().appendAll([String$("[",1),(opt$500=(opt$501=$$singleton.element$495,opt$501!==null?opt$501.string:null),opt$500!==null?opt$500:String$("null",4)).string,String$("]",1)]).string;
            },undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['string']});
            $$singleton.segment=function (from$502,length$503){
                var $$singleton=this;
                return (opt$504=(((from$502.compare((0))!==getLarger())&&from$502.plus(length$503).compare((0)).equals(getLarger()))?$$singleton:null),opt$504!==null?opt$504:getEmpty());
            };
            $$singleton.segment.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['segment']};
            $$singleton.span=function (from$505,to$506){
                var $$singleton=this;
                return (opt$507=((((from$505.compare((0))!==getLarger())&&(to$506.compare((0))!==getSmaller()))||((from$505.compare((0))!==getSmaller())&&(to$506.compare((0))!==getLarger())))?$$singleton:null),opt$507!==null?opt$507:getEmpty());
            };
            $$singleton.span.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['span']};
            $$singleton.spanTo=function (to$508){
                var $$singleton=this;
                return (opt$509=(to$508.compare((0)).equals(getSmaller())?getEmpty():null),opt$509!==null?opt$509:$$singleton);
            };
            $$singleton.spanTo.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['spanTo']};
            $$singleton.spanFrom=function (from$510){
                var $$singleton=this;
                return (opt$511=(from$510.compare((0)).equals(getLarger())?getEmpty():null),opt$511!==null?opt$511:$$singleton);
            };
            $$singleton.spanFrom.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['spanFrom']};
            defineAttr($$singleton,'reversed',function(){
                var $$singleton=this;
                return $$singleton;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Singleton,a:{Element:'Element'}},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['reversed']});
            $$singleton.equals=function equals(that$512){
                var $$singleton=this;
                var element$513;
                if((element$513=$$singleton.element$495)!==null){
                    var that$514;
                    if(isOfType((that$514=that$512),{t:List,a:{Element:{t:Anything}}})){
                        if(that$514.size.equals((1))){
                            var elem$515;
                            if((elem$515=that$514.first)!==null){
                                return elem$515.equals(element$513);
                            }
                        }
                    }
                    return false;
                }
                return false;
            };$$singleton.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['equals']};defineAttr($$singleton,'hash',function(){
                var $$singleton=this;
                return (31).plus((opt$516=(opt$517=$$singleton.element$495,opt$517!==null?opt$517.hash:null),opt$516!==null?opt$516:(0)));
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['hash']});
            $$singleton.contains=function contains(element$518){
                var $$singleton=this;
                var e$519;
                if((e$519=$$singleton.element$495)!==null){
                    return e$519.equals(element$518);
                }
                return false;
            };$$singleton.contains.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['contains']};$$singleton.count=function (selecting$520){
                var $$singleton=this;
                return (opt$521=(selecting$520($$singleton.element$495)?(1):null),opt$521!==null?opt$521:(0));
            };
            $$singleton.count.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['count']};
            $$singleton.$map=function (selecting$522,$$$mptypes){
                var $$singleton=this;
                return Tuple(selecting$522($$singleton.element$495),getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Result,Element:$$$mptypes.Result});
            };
            $$singleton.$map.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Result'}},$ps:[{$nm:'selecting',$mt:'prm',$t:'Result'}],$cont:Singleton,$tp:{Result:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['map']};
            $$singleton.$filter=function (selecting$523){
                var $$singleton=this;
                return (opt$524=(selecting$523($$singleton.element$495)?$$singleton:null),opt$524!==null?opt$524:getEmpty());
            };
            $$singleton.$filter.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['filter']};
            $$singleton.fold=function (initial$525,accumulating$526,$$$mptypes){
                var $$singleton=this;
                return accumulating$526(initial$525,$$singleton.element$495);
            };
            $$singleton.fold.$$metamodel$$={mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$cont:Singleton,$tp:{Result:{}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['fold']};
            $$singleton.find=function find(selecting$527){
                var $$singleton=this;
                if(selecting$527($$singleton.element$495)){
                    return $$singleton.element$495;
                }
                return null;
            };$$singleton.find.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['find']};$$singleton.findLast=function (selecting$528){
                var $$singleton=this;
                return $$singleton.find($JsCallable(selecting$528,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$singleton.$$targs$$.Element,Element:$$singleton.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$singleton.findLast.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['findLast']};
            $$singleton.$sort=function (comparing$529){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.$sort.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Singleton,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['sort']};
            $$singleton.any=function (selecting$530){
                var $$singleton=this;
                return selecting$530($$singleton.element$495);
            };
            $$singleton.any.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['any']};
            $$singleton.$every=function (selecting$531){
                var $$singleton=this;
                return selecting$531($$singleton.element$495);
            };
            $$singleton.$every.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['every']};
            $$singleton.skipping=function (skip$532){
                var $$singleton=this;
                return (opt$533=(skip$532.compare((1)).equals(getSmaller())?$$singleton:null),opt$533!==null?opt$533:getEmpty());
            };
            $$singleton.skipping.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['skipping']};
            $$singleton.taking=function (take$534){
                var $$singleton=this;
                return (opt$535=(take$534.compare((0)).equals(getLarger())?$$singleton:null),opt$535!==null?opt$535:getEmpty());
            };
            $$singleton.taking.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$m']['taking']};
            defineAttr($$singleton,'coalesced',function(){
                var $$singleton=this;
                var self$536;
                if(isOfType((self$536=$$singleton),{t:Singleton,a:{Element:{t:Object$}}})){
                    return self$536;
                }
                return getEmpty();
            },undefined,{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{ t:'i', l:['Element',{t:Object$}]}}},$cont:Singleton,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Singleton']['$at']['coalesced']});
        })(Singleton.$$.prototype);
    }
    return Singleton;
}
exports.$init$Singleton=$init$Singleton;
$init$Singleton();
var opt$500,opt$501,opt$504,opt$507,opt$509,opt$511,opt$516,opt$517,opt$521,opt$524,opt$533,opt$535;
function AssertionException(message$537, $$assertionException){
    $init$AssertionException();
    if ($$assertionException===undefined)$$assertionException=new AssertionException.$$;
    Exception(message$537,undefined,$$assertionException);
    return $$assertionException;
}
AssertionException.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['AssertionException']};
exports.AssertionException=AssertionException;
function $init$AssertionException(){
    if (AssertionException.$$===undefined){
        initTypeProto(AssertionException,'ceylon.language::AssertionException',Exception);
    }
    return AssertionException;
}
exports.$init$AssertionException=$init$AssertionException;
$init$AssertionException();
function Map($$targs$$,$$map){
    Collection($$map.$$targs$$===undefined?$$targs$$:{Element:{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}}},$$map);
    add_type_arg($$map,'Element',{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}});
    Correspondence($$map.$$targs$$===undefined?$$targs$$:{Key:{t:Object$},Item:$$map.$$targs$$.Item},$$map);
    add_type_arg($$map,'Key',{t:Object$});
    Cloneable($$map.$$targs$$===undefined?$$targs$$:{Clone:{t:Map,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}}},$$map);
    add_type_arg($$map,'Clone',{t:Map,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}});
    set_type_args($$map,$$targs$$);
}
Map.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Collection,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},{t:Correspondence,a:{Key:{t:Object$},Item:'Item'}},{t:Cloneable,a:{Clone:{t:Map,a:{Key:'Key',Item:'Item'}}}}],$an:function(){return[see([typeLiteral$model({Type:{t:Entry,a:{}}}),,,,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Map']};
exports.Map=Map;
function $init$Map(){
    if (Map.$$===undefined){
        initTypeProto(Map,'ceylon.language::Map',$init$Collection(),$init$Correspondence(),$init$Cloneable());
        (function($$map){
            $$map.equals=function equals(that$538){
                var $$map=this;
                var that$539;
                if(isOfType((that$539=that$538),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})&&that$539.size.equals($$map.size)){
                    var it$540 = $$map.iterator();
                    var entry$541;while ((entry$541=it$540.next())!==getFinished()){
                        var item$542;
                        if((item$542=that$539.get(entry$541.key))!==null&&item$542.equals(entry$541.item)){
                            continue;
                        }else {
                            return false;
                        }
                    }
                    if (getFinished() === entry$541){
                        return true;
                    }
                }else {
                    return false;
                }
            };$$map.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Map,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Map']['$m']['equals']};defineAttr($$map,'hash',function(){
                var $$map=this;
                var hashCode$543=(1);
                var setHashCode$543=function(hashCode$544){return hashCode$543=hashCode$544;};
                var it$545 = $$map.iterator();
                var elem$546;while ((elem$546=it$545.next())!==getFinished()){
                    (hashCode$543=hashCode$543.times((31)));
                    (hashCode$543=hashCode$543.plus(elem$546.hash));
                }
                return hashCode$543;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Map,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Map']['$at']['hash']});defineAttr($$map,'keys',function(){
                var $$map=this;
                return LazySet(Comprehension(function(){
                    var it$547=$$map.iterator();
                    var k$548,v$549;
                    var next$v$549=function(){
                        var entry$550;
                        if((entry$550=it$547.next())!==getFinished()){
                            k$548=entry$550.key;
                            v$549=entry$550.item;
                            return entry$550;
                        }
                        v$549=undefined;
                        return getFinished();
                    }
                    next$v$549();
                    return function(){
                        if(v$549!==undefined){
                            var k$548$551=k$548;
                            var v$549$552=v$549;
                            var tmpvar$553=k$548$551;
                            next$v$549();
                            return tmpvar$553;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Set,a:{Element:'Key'}},$cont:Map,$an:function(){return[actual(),shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Map']['$at']['keys']});
            defineAttr($$map,'values',function(){
                var $$map=this;
                return LazyList(Comprehension(function(){
                    var it$554=$$map.iterator();
                    var k$555,v$556;
                    var next$v$556=function(){
                        var entry$557;
                        if((entry$557=it$554.next())!==getFinished()){
                            k$555=entry$557.key;
                            v$556=entry$557.item;
                            return entry$557;
                        }
                        v$556=undefined;
                        return getFinished();
                    }
                    next$v$556();
                    return function(){
                        if(v$556!==undefined){
                            var k$555$558=k$555;
                            var v$556$559=v$556;
                            var tmpvar$560=v$556$559;
                            next$v$556();
                            return tmpvar$560;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$map.$$targs$$.Item}),{Element:$$map.$$targs$$.Item});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Collection,a:{Element:'Item'}},$cont:Map,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Map']['$at']['values']});
            defineAttr($$map,'inverse',function(){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$561=$$map.iterator();
                    var key$562,item$563;
                    var next$item$563=function(){
                        var entry$564;
                        if((entry$564=it$561.next())!==getFinished()){
                            key$562=entry$564.key;
                            item$563=entry$564.item;
                            return entry$564;
                        }
                        item$563=undefined;
                        return getFinished();
                    }
                    next$item$563();
                    return function(){
                        if(item$563!==undefined){
                            var key$562$565=key$562;
                            var item$563$566=item$563;
                            var tmpvar$567=Entry(item$563$566,LazySet(Comprehension(function(){
                                var it$568=$$map.iterator();
                                var k$569,i$570;
                                var next$i$570=function(){
                                    var entry$571;
                                    while((entry$571=it$568.next())!==getFinished()){
                                        k$569=entry$571.key;
                                        i$570=entry$571.item;
                                        if(i$570.equals(item$563$566)){
                                            return entry$571;
                                        }
                                    }
                                    i$570=undefined;
                                    return getFinished();
                                }
                                next$i$570();
                                return function(){
                                    if(i$570!==undefined){
                                        var k$569$572=k$569;
                                        var i$570$573=i$570;
                                        var tmpvar$574=k$569$572;
                                        next$i$570();
                                        return tmpvar$574;
                                    }
                                    return getFinished();
                                }
                            },{Absent:{t:Null},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
                            next$item$563();
                            return tmpvar$567;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}}}}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
            },undefined,{mod:$$METAMODEL$$,$t:{t:Map,a:{Key:'Item',Item:{t:Set,a:{Element:'Key'}}}},$cont:Map,$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Map']['$at']['inverse']});
            $$map.mapItems=function (mapping$575,$$$mptypes){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$576=$$map.iterator();
                    var key$577,item$578;
                    var next$item$578=function(){
                        var entry$579;
                        if((entry$579=it$576.next())!==getFinished()){
                            key$577=entry$579.key;
                            item$578=entry$579.item;
                            return entry$579;
                        }
                        item$578=undefined;
                        return getFinished();
                    }
                    next$item$578();
                    return function(){
                        if(item$578!==undefined){
                            var key$577$580=key$577;
                            var item$578$581=item$578;
                            var tmpvar$582=Entry(key$577$580,mapping$575(key$577$580,item$578$581),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
                            next$item$578();
                            return tmpvar$582;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result}}}),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
            };
            $$map.mapItems.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Map,a:{Key:'Key',Item:'Result'}},$ps:[{$nm:'mapping',$mt:'prm',$t:'Result'}],$cont:Map,$tp:{Result:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Map']['$m']['mapItems']};
        })(Map.$$.prototype);
    }
    return Map;
}
exports.$init$Map=$init$Map;
$init$Map();
function LazyMap(entries$583, $$targs$$,$$lazyMap){
    $init$LazyMap();
    if ($$lazyMap===undefined)$$lazyMap=new LazyMap.$$;
    set_type_args($$lazyMap,$$targs$$);
    $$lazyMap.entries$583=entries$583;
    Map($$lazyMap.$$targs$$===undefined?$$targs$$:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item},$$lazyMap);
    return $$lazyMap;
}
LazyMap.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Map,a:{Key:'Key',Item:'Item'}}],$an:function(){return[by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']};
exports.LazyMap=LazyMap;
function $init$LazyMap(){
    if (LazyMap.$$===undefined){
        initTypeProto(LazyMap,'ceylon.language::LazyMap',Basic,$init$Map());
        (function($$lazyMap){
            defineAttr($$lazyMap,'first',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$583.first;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Entry,a:{Key:'Key',Item:'Item'}}]},$cont:LazyMap,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$at']['first']});
            defineAttr($$lazyMap,'last',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$583.last;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Entry,a:{Key:'Key',Item:'Item'}}]},$cont:LazyMap,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$at']['last']});
            defineAttr($$lazyMap,'clone',function(){
                var $$lazyMap=this;
                return $$lazyMap;
            },undefined,{mod:$$METAMODEL$$,$t:{t:LazyMap,a:{Key:'Key',Item:'Item'}},$cont:LazyMap,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$at']['clone']});
            defineAttr($$lazyMap,'size',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$583.size;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazyMap,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$at']['size']});
            $$lazyMap.get=function (key$584){
                var $$lazyMap=this;
                return (opt$585=$$lazyMap.entries$583.find($JsCallable(function (e$586){
                    var $$lazyMap=this;
                    return e$586.key.equals(key$584);
                },[{$nm:'e',$mt:'prm',$t:{t:Entry,a:{Key:'Key',Item:'Item'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Entry,a:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item}},Element:{t:Entry,a:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item}}}},Return:{t:Boolean$}})),opt$585!==null?opt$585.item:null);
            };
            $$lazyMap.get.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Item']},$ps:[{$nm:'key',$mt:'prm',$t:{t:Object$}}],$cont:LazyMap,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$m']['get']};
            $$lazyMap.iterator=function (){
                var $$lazyMap=this;
                return $$lazyMap.entries$583.iterator();
            };
            $$lazyMap.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},$ps:[],$cont:LazyMap,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$m']['iterator']};
            $$lazyMap.equals=function equals(that$587){
                var $$lazyMap=this;
                var that$588;
                if(isOfType((that$588=that$587),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    if(that$588.size.equals($$lazyMap.size)){
                        var it$589 = $$lazyMap.iterator();
                        var entry$590;while ((entry$590=it$589.next())!==getFinished()){
                            var item$591;
                            if((item$591=that$588.get(entry$590.key))!==null){
                                if(item$591.equals(entry$590.item)){
                                    continue;
                                }
                            }
                            return false;
                        }
                        if (getFinished() === entry$590){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyMap.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:LazyMap,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$m']['equals']};defineAttr($$lazyMap,'hash',function(){
                var $$lazyMap=this;
                var hashCode$592=(1);
                var setHashCode$592=function(hashCode$593){return hashCode$592=hashCode$593;};
                var it$594 = $$lazyMap.entries$583.iterator();
                var elem$595;while ((elem$595=it$594.next())!==getFinished()){
                    (hashCode$592=hashCode$592.times((31)));
                    (hashCode$592=hashCode$592.plus(elem$595.hash));
                }
                return hashCode$592;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazyMap,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyMap']['$at']['hash']});
        })(LazyMap.$$.prototype);
    }
    return LazyMap;
}
exports.$init$LazyMap=$init$LazyMap;
$init$LazyMap();
var opt$585;
function LazyList(elems$596, $$targs$$,$$lazyList){
    $init$LazyList();
    if ($$lazyList===undefined)$$lazyList=new LazyList.$$;
    set_type_args($$lazyList,$$targs$$);
    $$lazyList.elems$596=elems$596;
    List($$lazyList.$$targs$$===undefined?$$targs$$:{Element:$$lazyList.$$targs$$.Element},$$lazyList);
    return $$lazyList;
}
LazyList.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Element:{'var':'out',}},satisfies:[{t:List,a:{Element:'Element'}}],$an:function(){return[by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']};
exports.LazyList=LazyList;
function $init$LazyList(){
    if (LazyList.$$===undefined){
        initTypeProto(LazyList,'ceylon.language::LazyList',Basic,$init$List());
        (function($$lazyList){
            defineAttr($$lazyList,'lastIndex',function(){
                var $$lazyList=this;
                var size$597=$$lazyList.elems$596.size;
                return (opt$598=(size$597.compare((0)).equals(getLarger())?size$597.minus((1)):null),opt$598!==null?opt$598:null);
                var opt$598;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$at']['lastIndex']});$$lazyList.get=function get(index$599){
                var $$lazyList=this;
                if(index$599.equals((0))){
                    return $$lazyList.elems$596.first;
                }else {
                    return $$lazyList.elems$596.skipping(index$599).first;
                }
            };$$lazyList.get.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['get']};defineAttr($$lazyList,'rest',function(){
                var $$lazyList=this;
                return LazyList($$lazyList.elems$596.rest,{Element:$$lazyList.$$targs$$.Element});
            },undefined,{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$at']['rest']});
            $$lazyList.iterator=function (){
                var $$lazyList=this;
                return $$lazyList.elems$596.iterator();
            };
            $$lazyList.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['iterator']};
            defineAttr($$lazyList,'reversed',function(){
                var $$lazyList=this;
                return $$lazyList.elems$596.sequence.reversed;
            },undefined,{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$at']['reversed']});
            defineAttr($$lazyList,'clone',function(){
                var $$lazyList=this;
                return $$lazyList;
            },undefined,{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$at']['clone']});
            $$lazyList.span=function span(from$600,to$601){
                var $$lazyList=this;
                if((to$601.compare((0)).equals(getSmaller())&&from$600.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var toIndex$602=largest(to$601,(0),{Element:{t:Integer}});
                var fromIndex$603=largest(from$600,(0),{Element:{t:Integer}});
                if((toIndex$602.compare(fromIndex$603)!==getSmaller())){
                    var els$604=(opt$605=(fromIndex$603.compare((0)).equals(getLarger())?$$lazyList.elems$596.skipping(fromIndex$603):null),opt$605!==null?opt$605:$$lazyList.elems$596);
                    var opt$605;
                    return LazyList(els$604.taking(toIndex$602.minus(fromIndex$603).plus((1))),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    var seq$606=(opt$607=(toIndex$602.compare((0)).equals(getLarger())?$$lazyList.elems$596.skipping(toIndex$602):null),opt$607!==null?opt$607:$$lazyList.elems$596);
                    var opt$607;
                    return seq$606.taking(fromIndex$603.minus(toIndex$602).plus((1))).sequence.reversed;
                }
            };$$lazyList.span.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['span']};$$lazyList.spanTo=function (to$608){
                var $$lazyList=this;
                return (opt$609=(to$608.compare((0)).equals(getSmaller())?getEmpty():null),opt$609!==null?opt$609:LazyList($$lazyList.elems$596.taking(to$608.plus((1))),{Element:$$lazyList.$$targs$$.Element}));
            };
            $$lazyList.spanTo.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['spanTo']};
            $$lazyList.spanFrom=function (from$610){
                var $$lazyList=this;
                return (opt$611=(from$610.compare((0)).equals(getLarger())?LazyList($$lazyList.elems$596.skipping(from$610),{Element:$$lazyList.$$targs$$.Element}):null),opt$611!==null?opt$611:$$lazyList);
            };
            $$lazyList.spanFrom.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['spanFrom']};
            $$lazyList.segment=function segment(from$612,length$613){
                var $$lazyList=this;
                if(length$613.compare((0)).equals(getLarger())){
                    var els$614=(opt$615=(from$612.compare((0)).equals(getLarger())?$$lazyList.elems$596.skipping(from$612):null),opt$615!==null?opt$615:$$lazyList.elems$596);
                    var opt$615;
                    return LazyList(els$614.taking(length$613),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    return getEmpty();
                }
            };$$lazyList.segment.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['segment']};$$lazyList.equals=function equals(that$616){
                var $$lazyList=this;
                var that$617;
                if(isOfType((that$617=that$616),{t:List,a:{Element:{t:Anything}}})){
                    var size$618=$$lazyList.elems$596.size;
                    if(that$617.size.equals(size$618)){
                        var it$619 = Range((0),size$618.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$620;while ((i$620=it$619.next())!==getFinished()){
                            var x$621=$$lazyList.get(i$620);
                            var y$622=that$617.get(i$620);
                            var x$623;
                            if((x$623=x$621)!==null){
                                var y$624;
                                if((y$624=y$622)!==null){
                                    if((!x$623.equals(y$624))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$625;
                                if((y$625=y$622)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$620){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyList.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['equals']};defineAttr($$lazyList,'hash',function(){
                var $$lazyList=this;
                var hash$626=(1);
                var setHash$626=function(hash$627){return hash$626=hash$627;};
                var it$628 = $$lazyList.elems$596.iterator();
                var elem$629;while ((elem$629=it$628.next())!==getFinished()){
                    (hash$626=hash$626.times((31)));
                    var elem$630;
                    if((elem$630=elem$629)!==null){
                        (hash$626=hash$626.plus(elem$630.hash));
                    }
                }
                return hash$626;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$at']['hash']});$$lazyList.findLast=function (selecting$631){
                var $$lazyList=this;
                return $$lazyList.elems$596.findLast($JsCallable(selecting$631,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$lazyList.$$targs$$.Element,Element:$$lazyList.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$lazyList.findLast.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:LazyList,$an:function(){return[shared(),$default(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$m']['findLast']};
            defineAttr($$lazyList,'first',function(){
                var $$lazyList=this;
                return $$lazyList.elems$596.first;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$at']['first']});
            defineAttr($$lazyList,'last',function(){
                var $$lazyList=this;
                return $$lazyList.elems$596.last;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazyList']['$at']['last']});
        })(LazyList.$$.prototype);
    }
    return LazyList;
}
exports.$init$LazyList=$init$LazyList;
$init$LazyList();
var opt$609,opt$611;
function LazySet(elems$632, $$targs$$,$$lazySet){
    $init$LazySet();
    if ($$lazySet===undefined)$$lazySet=new LazySet.$$;
    set_type_args($$lazySet,$$targs$$);
    $$lazySet.elems$632=elems$632;
    Set($$lazySet.$$targs$$===undefined?$$targs$$:{Element:$$lazySet.$$targs$$.Element},$$lazySet);
    return $$lazySet;
}
LazySet.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Element:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Set,a:{Element:'Element'}}],$an:function(){return[by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']};
exports.LazySet=LazySet;
function $init$LazySet(){
    if (LazySet.$$===undefined){
        initTypeProto(LazySet,'ceylon.language::LazySet',Basic,$init$Set());
        (function($$lazySet){
            defineAttr($$lazySet,'clone',function(){
                var $$lazySet=this;
                return $$lazySet;
            },undefined,{mod:$$METAMODEL$$,$t:{t:LazySet,a:{Element:'Element'}},$cont:LazySet,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$at']['clone']});
            defineAttr($$lazySet,'size',function(){
                var $$lazySet=this;
                var c$633=(0);
                var setC$633=function(c$634){return c$633=c$634;};
                var sorted$635=$$lazySet.elems$632.$sort($JsCallable(byIncreasing($JsCallable(function (e$636){
                    var $$lazySet=this;
                    return e$636.hash;
                },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Integer}}),{Value:{t:Integer},Element:$$lazySet.$$targs$$.Element}),[{$nm:'p1',$mt:'prm',$t:'Element'},{$nm:'p2',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Comparison}}));
                var l$637;
                if((l$637=sorted$635.first)!==null){
                    c$633=(1);
                    var last$638=l$637;
                    var setLast$638=function(last$639){return last$638=last$639;};
                    var it$640 = sorted$635.rest.iterator();
                    var e$641;while ((e$641=it$640.next())!==getFinished()){
                        if((!e$641.equals(last$638))){
                            (oldc$642=c$633,c$633=oldc$642.successor,oldc$642);
                            var oldc$642;
                            last$638=e$641;
                        }
                    }
                }
                return c$633;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazySet,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$at']['size']});$$lazySet.iterator=function (){
                var $$lazySet=this;
                return $$lazySet.elems$632.iterator();
            };
            $$lazySet.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:LazySet,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$m']['iterator']};
            $$lazySet.union=function (set$643,$$$mptypes){
                var $$lazySet=this;
                return LazySet($$lazySet.elems$632.chain(set$643,{Other:$$$mptypes.Other,OtherAbsent:{t:Null}}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };
            $$lazySet.union.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Set,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$m']['union']};
            $$lazySet.intersection=function (set$644,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var e$647;
                    var it$645=set$644.iterator();
                    var e$646=getFinished();
                    var e$647;
                    var next$e$646=function(){
                        while((e$646=it$645.next())!==getFinished()){
                            if(isOfType((e$647=e$646),$$lazySet.$$targs$$.Element)&&$$lazySet.contains(e$647)){
                                return e$646;
                            }
                        }
                        return getFinished();
                    }
                    next$e$646();
                    return function(){
                        if(e$646!==getFinished()){
                            var e$646$648=e$646;
                            var tmpvar$649=e$647;
                            next$e$646();
                            return tmpvar$649;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}}),{Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}});
            };
            $$lazySet.intersection.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Set,a:{Element:{ t:'i', l:['Element','Other']}}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$m']['intersection']};
            $$lazySet.exclusiveUnion=function exclusiveUnion(other$650,$$$mptypes){
                var $$lazySet=this;
                var hereNotThere$651=Comprehension(function(){
                    var it$652=$$lazySet.elems$632.iterator();
                    var e$653=getFinished();
                    var next$e$653=function(){
                        while((e$653=it$652.next())!==getFinished()){
                            if((!other$650.contains(e$653))){
                                return e$653;
                            }
                        }
                        return getFinished();
                    }
                    next$e$653();
                    return function(){
                        if(e$653!==getFinished()){
                            var e$653$654=e$653;
                            var tmpvar$655=e$653$654;
                            next$e$653();
                            return tmpvar$655;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$lazySet.$$targs$$.Element});
                var thereNotHere$656=Comprehension(function(){
                    var it$657=other$650.iterator();
                    var e$658=getFinished();
                    var next$e$658=function(){
                        while((e$658=it$657.next())!==getFinished()){
                            if((!$$lazySet.contains(e$658))){
                                return e$658;
                            }
                        }
                        return getFinished();
                    }
                    next$e$658();
                    return function(){
                        if(e$658!==getFinished()){
                            var e$658$659=e$658;
                            var tmpvar$660=e$658$659;
                            next$e$658();
                            return tmpvar$660;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$$mptypes.Other});
                return LazySet(hereNotThere$651.chain(thereNotHere$656,{Other:$$$mptypes.Other,OtherAbsent:{t:Null}}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };$$lazySet.exclusiveUnion.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Set,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$m']['exclusiveUnion']};$$lazySet.complement=function (set$661,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var it$662=$$lazySet.iterator();
                    var e$663=getFinished();
                    var next$e$663=function(){
                        while((e$663=it$662.next())!==getFinished()){
                            if((!set$661.contains(e$663))){
                                return e$663;
                            }
                        }
                        return getFinished();
                    }
                    next$e$663();
                    return function(){
                        if(e$663!==getFinished()){
                            var e$663$664=e$663;
                            var tmpvar$665=e$663$664;
                            next$e$663();
                            return tmpvar$665;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$lazySet.$$targs$$.Element}),{Element:$$lazySet.$$targs$$.Element});
            };
            $$lazySet.complement.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Set,a:{Element:'Element'}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$m']['complement']};
            $$lazySet.equals=function equals(that$666){
                var $$lazySet=this;
                var that$667;
                if(isOfType((that$667=that$666),{t:Set,a:{Element:{t:Object$}}})){
                    if(that$667.size.equals($$lazySet.size)){
                        var it$668 = $$lazySet.elems$632.iterator();
                        var element$669;while ((element$669=it$668.next())!==getFinished()){
                            if((!that$667.contains(element$669))){
                                return false;
                            }
                        }
                        if (getFinished() === element$669){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazySet.equals.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:LazySet,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$m']['equals']};defineAttr($$lazySet,'hash',function(){
                var $$lazySet=this;
                var hashCode$670=(1);
                var setHashCode$670=function(hashCode$671){return hashCode$670=hashCode$671;};
                var it$672 = $$lazySet.elems$632.iterator();
                var elem$673;while ((elem$673=it$672.next())!==getFinished()){
                    (hashCode$670=hashCode$670.times((31)));
                    (hashCode$670=hashCode$670.plus(elem$673.hash));
                }
                return hashCode$670;
            },undefined,{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazySet,$an:function(){return[shared(),actual(),$default()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['LazySet']['$at']['hash']});
        })(LazySet.$$.prototype);
    }
    return LazySet;
}
exports.$init$LazySet=$init$LazySet;
$init$LazySet();
function any(values$674){
    var it$675 = values$674.iterator();
    var val$676;while ((val$676=it$675.next())!==getFinished()){
        if(val$676){
            return true;
        }
    }
    return false;
}
exports.any=any;
any.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[see([typeLiteral$model({Type:{t:every}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:FunctionDeclaration$model$declaration},{t:Function$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Type:{t:Boolean$}}}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['any']};
var byDecreasing=function (comparable$677,$$$mptypes){
    return function(x$678,y$679){{
        return comparable$677(y$679).compare(comparable$677(x$678));
    }
}
}
;
byDecreasing.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparable',$mt:'prm',$t:'Value'}],$tp:{Element:{},Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['byDecreasing']};
exports.byDecreasing=byDecreasing;
var byIncreasing=function (comparable$680,$$$mptypes){
    return function(x$681,y$682){{
        return comparable$680(x$681).compare(comparable$680(y$682));
    }
}
}
;
byIncreasing.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparable',$mt:'prm',$t:'Value'}],$tp:{Element:{},Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['byIncreasing']};
exports.byIncreasing=byIncreasing;
var byItem=function (comparing$683,$$$mptypes){
    return function(x$684,y$685){{
        return comparing$683(x$684.item,y$685.item);
    }
}
}
;
byItem.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$tp:{Item:{'satisfies':[{t:Object$}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['byItem']};
exports.byItem=byItem;
var byKey=function (comparing$686,$$$mptypes){
    return function(x$687,y$688){{
        return comparing$686(x$687.key,y$688.key);
    }
}
}
;
byKey.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$tp:{Key:{'satisfies':[{t:Object$}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['byKey']};
exports.byKey=byKey;
var coalesce=function (values$689,$$$mptypes){
    return values$689.coalesced;
};
coalesce.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{ t:'i', l:['Element',{t:Object$}]}}},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['coalesce']};
exports.coalesce=coalesce;
function count(values$690){
    var count$691=(0);
    var setCount$691=function(count$692){return count$691=count$692;};
    var it$693 = values$690.iterator();
    var val$694;while ((val$694=it$693.next())!==getFinished()){
        if(val$694){
            (oldcount$695=count$691,count$691=oldcount$695.successor,oldcount$695);
            var oldcount$695;
        }
    }
    return count$691;
}
exports.count=count;
count.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['count']};
function emptyOrSingleton(element$696,$$$mptypes){
    var element$697;
    if((element$697=element$696)!==null){
        return Singleton(element$697,{Element:$$$mptypes.Element});
    }else {
        return getEmpty();
    }
}
exports.emptyOrSingleton=emptyOrSingleton;
emptyOrSingleton.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:{ t:'u', l:[{t:Null},'Element']}}],$tp:{Element:{'satisfies':[{t:Object$}]}},$an:function(){return[see([typeLiteral$model({Type:{t:Singleton,a:{}}}),typeLiteral$model({Type:{t:Empty}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{ t:'i', l:[{t:InterfaceDeclaration$model$declaration},{t:Interface$model,a:{Type:{t:Empty}}}]}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['emptyOrSingleton']};
var curry=function (f$698,$$$mptypes){
    return function(first$699){{
        return flatten($JsCallable(function (args$700){
            return unflatten($JsCallable(f$698,[],{Arguments:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return})(Tuple(first$699,args$700,{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return});
        },[{$nm:'args',$mt:'prm',$t:'Rest'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Rest,Element:$$$mptypes.Rest}},Return:$$$mptypes.Return}),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return});
    }
}
}
;
curry.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:'Rest',Return:'Return'}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}},Return:'Return'}}}],$tp:{Return:{},Argument:{},First:{'satisfies':['Argument']},Rest:{'satisfies':[{t:Sequential,a:{Element:'Argument'}}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['curry']};
exports.curry=curry;
var uncurry=function (f$701,$$$mptypes){
return flatten($JsCallable(function (args$702){
    return unflatten($JsCallable(f$701(args$702.first),[],{Arguments:$$$mptypes.Rest,Return:$$$mptypes.Return}),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return})(args$702.rest,{Args:$$$mptypes.Rest,Return:$$$mptypes.Return});
},[{$nm:'args',$mt:'prm',$t:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Element:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}}}},Return:$$$mptypes.Return}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return});
};
uncurry.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}},Return:'Return'}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:'Rest',Return:'Return'}}}],$tp:{Return:{},Argument:{},First:{'satisfies':['Argument']},Rest:{'satisfies':[{t:Sequential,a:{Element:'Argument'}}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['uncurry']};
exports.uncurry=uncurry;
var entries=function (elements$703,$$$mptypes){
    return elements$703.indexed;
};
entries.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['entries']};
exports.entries=entries;
var equalTo=function (val$704,$$$mptypes){
    return function(element$705){{
        return element$705.equals(val$704);
    }
}
}
;
equalTo.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Object$}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['equalTo']};
exports.equalTo=equalTo;
function every(values$706){
    var it$707 = values$706.iterator();
    var val$708;while ((val$708=it$707.next())!==getFinished()){
        if((!val$708)){
            return false;
        }
    }
    return true;
}
exports.every=every;
every.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[see([typeLiteral$model({Type:{t:any}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:FunctionDeclaration$model$declaration},{t:Function$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Type:{t:Boolean$}}}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['every']};
function first(values$709,$$$mptypes){
    var first$710;
    var next$711;
    if(!isOfType((next$711=values$709.iterator().next()),{t:Finished})){
        first$710=next$711;
    }else {
        first$710=null;
    }
    //assert at first.ceylon (12:4-12:34)
    var first$712;
    if (!(isOfType((first$712=first$710),{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}))) { throw AssertionException('Assertion failed: \'is Absent|Value first\' at first.ceylon (12:11-12:33)'); }
    return first$712;
}
exports.first=first;
first.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['first']};
var forItem=function (resulting$713,$$$mptypes){
    return function(entry$714){{
        return resulting$713(entry$714.item);
    }
}
}
;
forItem.$$metamodel$$={mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'resulting',$mt:'prm',$t:'Result'}],$tp:{Item:{'satisfies':[{t:Object$}]},Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['forItem']};
exports.forItem=forItem;
var forKey=function (resulting$715,$$$mptypes){
    return function(entry$716){{
        return resulting$715(entry$716.key);
    }
}
}
;
forKey.$$metamodel$$={mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'resulting',$mt:'prm',$t:'Result'}],$tp:{Key:{'satisfies':[{t:Object$}]},Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['forKey']};
exports.forKey=forKey;
var greaterThan=function (val$717,$$$mptypes){
    return function(element$718){{
        return element$718.compare(val$717).equals(getLarger());
    }
}
}
;
greaterThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['greaterThan']};
exports.greaterThan=greaterThan;
var join=function (iterables$719,$$$mptypes){
    if(iterables$719===undefined){iterables$719=getEmpty();}
    return Comprehension(function(){
        var it$720=iterables$719.iterator();
        var it$721=getFinished();
        var next$it$721=function(){
            if((it$721=it$720.next())!==getFinished()){
                it$722=it$721.iterator();
                next$val$723();
                return it$721;
            }
            return getFinished();
        }
        var it$722;
        var val$723=getFinished();
        var next$val$723=function(){return val$723=it$722.next();}
        next$it$721();
        return function(){
            do{
                if(val$723!==getFinished()){
                    var val$723$724=val$723;
                    var tmpvar$725=val$723$724;
                    next$val$723();
                    return tmpvar$725;
                }
            }while(next$it$721()!==getFinished());
            return getFinished();
        }
    },{Absent:{t:Null},Element:$$$mptypes.Element}).sequence;
};
join.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'iterables',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}}}],$tp:{Element:{}},$an:function(){return[see([typeLiteral$model({Type:{t:SequenceBuilder,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['join']};
exports.join=join;
var largest=function (x$726,y$727,$$$mptypes){
    return (opt$728=(x$726.compare(y$727).equals(getLarger())?x$726:null),opt$728!==null?opt$728:y$727);
};
largest.$$metamodel$$={mod:$$METAMODEL$$,$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[see([typeLiteral$model({Type:{t:Comparable,a:{}}}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['largest']};
exports.largest=largest;
var opt$728;
var lessThan=function (val$729,$$$mptypes){
    return function(element$730){{
        return element$730.compare(val$729).equals(getSmaller());
    }
}
}
;
lessThan.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['lessThan']};
exports.lessThan=lessThan;
function max(values$731,$$$mptypes){
    var first$732=values$731.first;
    var first$733;
    if((first$733=first$732)!==null){
        var max$734=first$733;
        var setMax$734=function(max$735){return max$734=max$735;};
        var it$736 = values$731.rest.iterator();
        var val$737;while ((val$737=it$736.next())!==getFinished()){
            if(val$737.compare(max$734).equals(getLarger())){
                max$734=val$737;
            }
        }
        return max$734;
    }else {
        return first$732;
    }
}
exports.max=max;
max.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[see([typeLiteral$model({Type:{t:Comparable,a:{}}}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['max']};
function min(values$738,$$$mptypes){
    var first$739=values$738.first;
    var first$740;
    if((first$740=first$739)!==null){
        var min$741=first$740;
        var setMin$741=function(min$742){return min$741=min$742;};
        var it$743 = values$738.rest.iterator();
        var val$744;while ((val$744=it$743.next())!==getFinished()){
            if(val$744.compare(min$741).equals(getSmaller())){
                min$741=val$744;
            }
        }
        return min$741;
    }else {
        return first$739;
    }
}
exports.min=min;
min.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[see([typeLiteral$model({Type:{t:Comparable,a:{}}}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['min']};
var smallest=function (x$745,y$746,$$$mptypes){
    return (opt$747=(x$745.compare(y$746).equals(getSmaller())?x$745:null),opt$747!==null?opt$747:y$746);
};
smallest.$$metamodel$$={mod:$$METAMODEL$$,$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[see([typeLiteral$model({Type:{t:Comparable,a:{}}}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['smallest']};
exports.smallest=smallest;
var opt$747;
function sum(values$748,$$$mptypes){
    var sum$749=values$748.first;
    var setSum$749=function(sum$750){return sum$749=sum$750;};
    var it$751 = values$748.rest.iterator();
    var val$752;while ((val$752=it$751.next())!==getFinished()){
        (sum$749=sum$749.plus(val$752));
    }
    return sum$749;
}
exports.sum=sum;
sum.$$metamodel$$={mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Summable,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['sum']};
function product(values$753,$$$mptypes){
    var product$754=values$753.first;
    var setProduct$754=function(product$755){return product$754=product$755;};
    var it$756 = values$753.rest.iterator();
    var val$757;while ((val$757=it$756.next())!==getFinished()){
        (product$754=product$754.times(val$757));
    }
    return product$754;
}
exports.product=product;
product.$$metamodel$$={mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Numeric,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['product']};
function zip(keys$758,items$759,$$$mptypes){
    var iter$760=items$759.iterator();
    return Comprehension(function(){
        var item$763;
        var it$761=keys$758.iterator();
        var key$762=getFinished();
        var item$763;
        var next$key$762=function(){
            while((key$762=it$761.next())!==getFinished()){
                if(!isOfType((item$763=iter$760.next()),{t:Finished})){
                    return key$762;
                }
            }
            return getFinished();
        }
        next$key$762();
        return function(){
            if(key$762!==getFinished()){
                var key$762$764=key$762;
                var tmpvar$765=Entry(key$762$764,item$763,{Key:$$$mptypes.Key,Item:$$$mptypes.Item});
                next$key$762();
                return tmpvar$765;
            }
            return getFinished();
        }
    },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$$mptypes.Key,Item:$$$mptypes.Item}}}).sequence;
}
exports.zip=zip;
zip.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}},{$nm:'items',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Item'}}}],$tp:{Key:{'satisfies':[{t:Object$}]},Item:{'satisfies':[{t:Object$}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['zip']};
function print(line$766){
    getProcess().writeLine((opt$767=(opt$768=line$766,opt$768!==null?opt$768.string:null),opt$767!==null?opt$767:String$("«null»",6)));
    var opt$767,opt$768;
}
exports.print=print;
print.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Anything},$ps:[{$nm:'line',$mt:'prm',$t:{t:Anything}}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['print']};
var getNothing=function(){
    throw Exception();
}
exports.getNothing=getNothing;
var identical=function (x$769,y$770){
    return (x$769===y$770);
};
identical.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'x',$mt:'prm',$t:{t:Identifiable}},{$nm:'y',$mt:'prm',$t:{t:Identifiable}}],$an:function(){return[see([typeLiteral$model({Type:{t:identityHash}})].reifyCeylonType({Absent:{t:Null},Element:{ t:'i', l:[{t:FunctionDeclaration$model$declaration},{t:Function$model,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Identifiable},Element:{t:Identifiable}}},Type:{t:Integer}}}]}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['identical']};
exports.identical=identical;
var compose=function (x$771,y$772,$$$mptypes){
    return flatten($JsCallable(function (args$773){
        return x$771(unflatten($JsCallable(y$772,[],{Arguments:$$$mptypes.Args,Return:$$$mptypes.Y}),{Args:$$$mptypes.Args,Return:$$$mptypes.Y})(args$773,{Args:$$$mptypes.Args,Return:$$$mptypes.Y}));
    },[{$nm:'args',$mt:'prm',$t:'Args'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Args,Element:$$$mptypes.Args}},Return:$$$mptypes.X}),{Args:$$$mptypes.Args,Return:$$$mptypes.X});
};
compose.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:'Args',Return:'X'}},$ps:[{$nm:'x',$mt:'prm',$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:'Y',Element:'Y'}},Return:'X'}}},{$nm:'y',$mt:'prm',$t:{t:Callable,a:{Arguments:'Args',Return:'Y'}}}],$tp:{X:{},Y:{},Args:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['compose']};
exports.compose=compose;
var shuffle=function (f$774,$$$mptypes){
    return flatten($JsCallable(function (secondArgs$775){
        return flatten($JsCallable(function (firstArgs$776){
            return unflatten($JsCallable(unflatten($JsCallable(f$774,[],{Arguments:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}}),{Args:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}})(firstArgs$776,{Args:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}}),[],{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}),{Args:$$$mptypes.SecondArgs,Return:$$$mptypes.Result})(secondArgs$775,{Args:$$$mptypes.SecondArgs,Return:$$$mptypes.Result});
        },[{$nm:'firstArgs',$mt:'prm',$t:'FirstArgs'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.FirstArgs,Element:$$$mptypes.FirstArgs}},Return:$$$mptypes.Result}),{Args:$$$mptypes.FirstArgs,Return:$$$mptypes.Result});
    },[{$nm:'secondArgs',$mt:'prm',$t:'SecondArgs'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.SecondArgs,Element:$$$mptypes.SecondArgs}},Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}}),{Args:$$$mptypes.SecondArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}});
};
shuffle.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:'SecondArgs',Return:{t:Callable,a:{Arguments:'FirstArgs',Return:'Result'}}}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:'FirstArgs',Return:{t:Callable,a:{Arguments:'SecondArgs',Return:'Result'}}}}}],$tp:{Result:{},FirstArgs:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]},SecondArgs:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['shuffle']};
exports.shuffle=shuffle;
var plus=function (x$777,y$778,$$$mptypes){
    return x$777.plus(y$778);
};
plus.$$metamodel$$={mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'x',$mt:'prm',$t:'Value'},{$nm:'y',$mt:'prm',$t:'Value'}],$tp:{Value:{'satisfies':[{t:Summable,a:{Other:'Value'}}]}},$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['plus']};
exports.plus=plus;
var times=function (x$779,y$780,$$$mptypes){
    return x$779.times(y$780);
};
times.$$metamodel$$={mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'x',$mt:'prm',$t:'Value'},{$nm:'y',$mt:'prm',$t:'Value'}],$tp:{Value:{'satisfies':[{t:Numeric,a:{Other:'Value'}}]}},$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['times']};
exports.times=times;
function combine(combination$781,elements$782,otherElements$783,$$$mptypes){
    function iterable$784($$targs$$){
        var $$iterable$784=new iterable$784.$$;
        $$iterable$784.$$targs$$=$$targs$$;
        Iterable({Absent:$$$mptypes.Absent,Element:$$$mptypes.Result},$$iterable$784);
        return $$iterable$784;
    }
    function $init$iterable$784(){
        if (iterable$784.$$===undefined){
            initTypeProto(iterable$784,'ceylon.language::combine.iterable',Basic,$init$Iterable());
        }
        return iterable$784;
    }
    $init$iterable$784();
    (function($$iterable$784){
        $$iterable$784.iterator=function iterator(){
            var $$iterable$784=this;
            function iterator$785($$targs$$){
                var $$iterator$785=new iterator$785.$$;
                $$iterator$785.$$targs$$=$$targs$$;
                Iterator({Element:$$$mptypes.Result},$$iterator$785);
                $$iterator$785.iter$786_=elements$782.iterator();
                $$iterator$785.otherIter$787_=otherElements$783.iterator();
                return $$iterator$785;
            }
            function $init$iterator$785(){
                if (iterator$785.$$===undefined){
                    initTypeProto(iterator$785,'ceylon.language::combine.iterable.iterator.iterator',Basic,$init$Iterator());
                }
                return iterator$785;
            }
            $init$iterator$785();
            (function($$iterator$785){
                defineAttr($$iterator$785,'iter$786',function(){return this.iter$786_;},undefined,{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$785,pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['combine']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$at']['iter']});
                defineAttr($$iterator$785,'otherIter$787',function(){return this.otherIter$787_;},undefined,{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'OtherElement'}},$cont:iterator$785,pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['combine']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$at']['otherIter']});
                $$iterator$785.next=function next(){
                    var $$iterator$785=this;
                    var elem$788=$$iterator$785.iter$786.next();
                    var otherElem$789=$$iterator$785.otherIter$787.next();
                    var elem$790;
                    var otherElem$791;
                    if(!isOfType((elem$790=elem$788),{t:Finished})&&!isOfType((otherElem$791=otherElem$789),{t:Finished})){
                        return combination$781(elem$790,otherElem$791);
                    }else {
                        return getFinished();
                    }
                };$$iterator$785.next.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:['Result',{t:Finished}]},$ps:[],$cont:iterator$785,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['combine']['$o']['iterable']['$m']['iterator']['$o']['iterator']['$m']['next']};
            })(iterator$785.$$.prototype);
            var iterator$792=iterator$785({Element:$$$mptypes.Result});
            var getIterator$792=function(){
                return iterator$792;
            }
            return getIterator$792();
        };$$iterable$784.iterator.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Result'}},$ps:[],$cont:iterable$784,$an:function(){return[shared(),actual()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['combine']['$o']['iterable']['$m']['iterator']};
    })(iterable$784.$$.prototype);
    var iterable$793=iterable$784({Absent:$$$mptypes.Absent,Element:$$$mptypes.Result});
    var getIterable$793=function(){
        return iterable$793;
    }
    return getIterable$793();
}
exports.combine=combine;
combine.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Result'}},$ps:[{$nm:'combination',$mt:'prm',$t:'Result'},{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}}},{$nm:'otherElements',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'OtherElement'}}}],$tp:{Result:{},Absent:{'satisfies':[{t:Null}]},Element:{},OtherElement:{}},$an:function(){return[by([String$("Gavin",5),String$("Enrique Zamudio",15),String$("Tako",4)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['combine']};
var sort=function (elements$794,$$$mptypes){
    return internalSort($JsCallable(byIncreasing($JsCallable(function (e$795){
        return e$795;
    },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element}},Return:$$$mptypes.Element}),{Value:$$$mptypes.Element,Element:$$$mptypes.Element}),[{$nm:'p1',$mt:'prm',$t:'Element'},{$nm:'p2',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element}},First:$$$mptypes.Element,Element:$$$mptypes.Element}},Return:{t:Comparison}}),elements$794,{Element:$$$mptypes.Element});
};
sort.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[see([typeLiteral$model({Type:{t:Comparable,a:{}}})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['sort']};
exports.sort=sort;
function Array$($$targs$$) {
    var that = new Array$.$$;
    List({Element:$$targs$$.Element}, that);
    return that;
}
Array$.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract(),native()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Array']};

initExistingType(Array$, Array, 'ceylon.language::Array', Object$,
        Cloneable, Ranged, $init$List());
var Array$proto = Array.prototype;
var origArrToString = Array$proto.toString;
inheritProto(Array$, Object$, Cloneable, Ranged, $init$List());
Array$proto.toString = origArrToString;
Array$proto.reifyCeylonType = function(typeParameters) {
    this.$$targs$$ = typeParameters;
    return this;
}
exports.Array=Array$;

function EmptyArray() {
    return [];
}
initTypeProto(EmptyArray, 'ceylon.language::EmptyArray', Array$);
function ArrayList(items) {
    return items;
}
initTypeProto(ArrayList, 'ceylon.language::ArrayList', Array$, $init$List());
function ArraySequence(/* js array */value, $$targs$$) {
    value.$seq = true;
    value.$$targs$$=$$targs$$;
    this.$$targs$$=$$targs$$;
    return value;
}
initTypeProto(ArraySequence, 'ceylon.language::ArraySequence', $init$Basic(), $init$Sequence());
Array$proto.getT$name = function() {
    return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$name;
}
Array$proto.getT$all = function() {
    return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$all;
}

exports.EmptyArray=EmptyArray;

defineAttr(Array$proto, 'size', function(){ return this.length; });
defineAttr(Array$proto,'string',function(){
    return (opt$181=(this.empty?String$("[]",2):null),opt$181!==null?opt$181:StringBuilder().appendAll([String$("[",1),commaList(this).string,String$("]",1)]).string);
});
Array$proto.set = function(idx,elem) {
    if (idx >= 0 && idx < this.length) {
        this[idx] = elem;
    }
}
Array$proto.get = function(idx) {
    var result = this[idx];
    return result!==undefined ? result:null;
}
defineAttr(Array$proto, 'lastIndex', function() {
    return this.length>0 ? (this.length-1) : null;
});
defineAttr(Array$proto, 'reversed', function() {
    if (this.length === 0) { return this; }
    var arr = this.slice(0);
    arr.reverse();
    return this.$seq ? ArraySequence(arr,this.$$targs$$) : arr.reifyCeylonType(this.$$targs$$);
});
Array$proto.chain = function(other, $$$mptypes) {
    if (this.length === 0) { return other; }
    return Iterable.$$.prototype.chain.call(this, other, $$$mptypes);
}
defineAttr(Array$proto, 'first', function(){ return this.length>0 ? this[0] : null; });
defineAttr(Array$proto, 'last', function() { return this.length>0 ? this[this.length-1] : null; });
Array$proto.segment = function(from, len) {
    if (len <= 0) { return getEmpty(); }
    var stop = from + len;
    var seq = this.slice((from>=0)?from:0, (stop>=0)?stop:0);
    return (seq.length > 0) ? ArraySequence(seq,this.$$targs$$) : getEmpty();
}
Array$proto.span = function(from, to) {
    if (from > to) {
        var arr = this.segment(to, from-to+1);
        arr.reverse();
        return arr.reifyCeylonType(this.$$targs$$);
    }
    return this.segment(from, to-from+1);
}
Array$proto.spanTo = function(to) {
    return to < 0 ? getEmpty() : this.span(0, to);
}
Array$proto.spanFrom = function(from) {
    return this.span(from, 0x7fffffff);
}
defineAttr(Array$proto, 'rest', function() {
    return this.length<=1 ? getEmpty() : ArraySequence(this.slice(1),this.$$targs$$);
});
Array$proto.items = function(keys) {
    if (keys === undefined) return getEmpty();
    var seq = [];
    for (var i = 0; i < keys.size; i++) {
        var key = keys.get(i);
        seq.push(this.get(key));
    }
    return ArraySequence(seq,this.$$targs$$);
}
defineAttr(Array$proto, 'keys', function(){ return TypeCategory(this, {t:Integer}); });
Array$proto.contains = function(elem) {
    for (var i=0; i<this.length; i++) {
        if (elem.equals(this[i])) {
            return true;
        }
    }
    return false;
}
Array$proto.iterator = function() {
    var $$$index$$$ = 0;
    var $$$arr$$$ = this;
    return new ComprehensionIterator(function() {
        return ($$$index$$$ === $$$arr$$$.length) ? getFinished() : $$$arr$$$[$$$index$$$++];
    }, this.$$targs$$);
}
Array$proto.copyTo = function(other, srcpos, dstpos, length) {
    if (length === undefined) length = this.size;
    if (srcpos === undefined) srcpos = 0;
    if (dstpos === undefined) dstpos = 0;
    var endpos = srcpos+length;
    //TODO validate range?
    for (var i=srcpos; i<endpos; i++) {
        other[dstpos]=this[i];
        dstpos++;
    }
}
Array$proto.shorterThan = function(len) {
  return this.size < len;
}
Array$proto.shorterThan.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['shorterThan']};
Array$proto.longerThan = function(len) {
  return this.size > len;
}
Array$proto.longerThan.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Iterable']['$m']['longerThan']};

exports.ArrayList=ArrayList;
exports.array=function(elems, $$$ptypes) {
    var e=[];
    if (!(elems === null || elems === undefined)) {
        var iter=elems.iterator();
        var item;while((item=iter.next())!==getFinished()) {
            e.push(item);
        }
    }
    e.$$targs$$=$$$ptypes;
    return e;
}
exports.array.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['array']};

exports.arrayOfSize=function(size, elem, $$$mptypes) {
    if (size > 0) {
        var elems = [];
        for (var i = 0; i < size; i++) {
            elems.push(elem);
        }
        elems.$$targs$$=$$$mptypes;
        return elems;
    } else return [];
}
exports.arrayOfSize.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['arrayOfSize']};

function TypeCategory(seq, type) {
    var that = new TypeCategory.$$;
    that.type = type;
    that.seq = seq;
    return that;
}
initTypeProto(TypeCategory, 'ceylon.language::TypeCategory', $init$Basic(), Category);
var TypeCategory$proto = TypeCategory.$$.prototype;
TypeCategory$proto.contains = function(k) {
    return isOfType(k, this.type) && this.seq.defines(k);
}

function SequenceBuilder($$targs$$) {
    var that = new SequenceBuilder.$$;
    that.seq = [];
    that.$$targs$$=$$targs$$;
    return that;
}
SequenceBuilder.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['SequenceBuilder']};

initTypeProto(SequenceBuilder, 'ceylon.language::SequenceBuilder', $init$Basic());
var SequenceBuilder$proto = SequenceBuilder.$$.prototype;
defineAttr(SequenceBuilder$proto, 'sequence', function() {
    return (this.seq.length > 0) ? ArraySequence(this.seq,this.$$targs$$) : getEmpty();
});
SequenceBuilder$proto.append = function(e) { this.seq.push(e); }
SequenceBuilder$proto.appendAll = function(/*Iterable*/arr) {
    if (arr === undefined) return;
    var iter = arr.iterator();
    var e; while ((e = iter.next()) !== getFinished()) {
        this.seq.push(e);
    }
}
defineAttr(SequenceBuilder$proto, 'size', function(){ return this.seq.length; });

function SequenceAppender(other, $$targs$$) {
    var that = new SequenceAppender.$$;
    that.seq = [];
    that.$$targs$$=$$targs$$;
    that.appendAll(other);
    return that;
}
SequenceAppender.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['SequenceAppender']};

initTypeProto(SequenceAppender, 'ceylon.language::SequenceAppender', SequenceBuilder);

exports.Sequence=Sequence;
exports.SequenceBuilder=SequenceBuilder;
exports.SequenceAppender=SequenceAppender;
exports.ArraySequence=ArraySequence;
function String$(/*{Character*}*/value,size) {
    if (value && value.getT$name && value.getT$name() == 'ceylon.language::String') {
        //if it's already a String just return it
        return value;
    }
    else if (typeof(value) === 'string') {
        var that = new String(value);
        that.codePoints = size;
        return that;
    }
    var _sb = StringBuilder();
    var _iter = value.iterator();
    var _c; while ((_c = _iter.next()) !== getFinished()) {
        _sb.appendCharacter(_c);
    }
    var that = _sb.string;
    if (size !== undefined) that.codePoints=size;
    return that;
}
String$.$$metamodel$$={$ps:[],$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']};

initExistingType(String$, String, 'ceylon.language::String', Object$, Sequential, Comparable,
        Ranged, Summable, Cloneable);
var origStrToString = String.prototype.toString;
inheritProto(String$, Object$, Sequential, Comparable, Ranged, Summable,
        Cloneable);
var String$proto = String$.$$.prototype;
String$proto.$$targs$$={Element:{t:Character}, Absent:{t:Null}};
String$proto.getT$name = function() {
    return String$.$$.T$name;
}
String$proto.getT$all = function() {
    return String$.$$.T$all;
}
String$proto.toString = origStrToString;
defineAttr(String$proto, 'string', function(){ return this; });
String$proto.plus = function(other) {
    var size = this.codePoints + other.codePoints;
    return String$(this+other, isNaN(size)?undefined:size);
}
String$proto.plus.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['plus']};
String$proto.equals = function(other) {
    if (other.constructor===String) {
        return other.valueOf()===this.valueOf();
    } else if (isOfType(other, {t:Iterable, a:{Element:{t:Character}}})) {
        if (other.size===this.size) {
            for (var i=0;i<this.size;i++) {
                if (!this.get(i).equals(other.get(i))) return false;
            }
            return true;
        }
    }
    return false;
}
String$proto.equals.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['equals']};
String$proto.compare = function(other) {
    var cmp = this.localeCompare(other);
    return cmp===0 ? equal : (cmp<0 ? smaller:larger);
}
String$proto.compare.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['compare']};
defineAttr(String$proto, 'uppercased', function(){ return String$(this.toUpperCase()); });
defineAttr(String$proto, 'lowercased', function(){ return String$(this.toLowerCase()); });
defineAttr(String$proto, 'size', function() {
    if (this.codePoints===undefined) {
        this.codePoints = countCodepoints(this);
    }
    return this.codePoints;
});
defineAttr(String$proto, 'lastIndex', function(){ return this.size.equals(0) ? null : this.size.predecessor; });
String$proto.span = function(from, to) {
    if (from > to) {
        return this.segment(to, from-to+1).reversed;
    }
    return this.segment(from, to-from+1);
}
String$proto.span.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['span']};
String$proto.spanFrom = function(from) {
    return this.span(from, 0x7fffffff);
}
String$proto.spanFrom.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['spanFrom']};
String$proto.spanTo = function(to) {
    return to < 0 ? String$('', 0) : this.span(0, to);
}
String$proto.spanTo.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['spanTo']};
String$proto.segment = function(from, len) {
    var fromIndex = from;
    var maxCount = len + fromIndex;
    if (fromIndex < 0) {fromIndex = 0;}
    var i1 = 0;
    var count = 0;
    for (; i1<this.length && count<fromIndex; ++i1, ++count) {
        if ((this.charCodeAt(i1)&0xfc00) === 0xd800) {++i1}
    }
    var i2 = i1;
    for (; i2<this.length && count<maxCount; ++i2, ++count) {
        if ((this.charCodeAt(i2)&0xfc00) === 0xd800) {++i2}
    }
    if (i2 >= this.length) {
        this.codePoints = count;
        if (fromIndex === 0) {return this;}
    }
    return String$(this.substring(i1, i2), count-fromIndex);
}
String$proto.segment.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['segment']};
defineAttr(String$proto, 'empty', function() {
    return this.length===0;
});
String$proto.longerThan = function(length) {
    if (this.codePoints!==undefined) {return this.codePoints>length}
    if (this.length <= length) {return false}
    if (this.length<<1 > length) {return true}
    this.codePoints = countCodepoints(this);
    return this.codePoints>length;
}
String$proto.longerThan.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['longerThan']};
String$proto.shorterThan = function(length) {
    if (this.codePoints!==undefined) {return this.codePoints<length}
    if (this.length < length) {return true}
    if (this.length<<1 >= length) {return false}
    this.codePoints = countCodepoints(this);
    return this.codePoints<length;
}
String$proto.shorterThan.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['shorterThan']};
String$proto.iterator= function() {
	return this.length === 0 ? getEmptyIterator() : StringIterator(this);
}
String$proto.iterator.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['iterator']};
String$proto.get = function(index) {
    if (index<0 || index>=this.length) {return null}
    var i = 0;
    for (var count=0; count<index; count++) {
        if ((this.charCodeAt(i)&0xfc00) === 0xd800) {++i}
        if (++i >= this.length) {return null}
    }
    return Character(codepointFromString(this, i));
}
String$proto.get.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['get']};
defineAttr(String$proto, 'trimmed', function() {
    // make use of the fact that all WS characters are single UTF-16 code units
    var from = 0;
    while (from<this.length && (this.charCodeAt(from) in $WS)) {++from}
    var to = this.length;
    if (from < to) {
        do {--to} while (from<to && (this.charCodeAt(to) in $WS));
        ++to;
    }
    if (from===0 && to===this.length) {return this;}
    var result = String$(this.substring(from, to));
    if (this.codePoints !== undefined) {
        result.codePoints = this.codePoints - from - this.length + to;
    }
    return result;
});
String$proto.trim = function(/*Category*/chars) {
    var from = 0;
    while (from<this.length && chars(this.get(from))) {++from}
    var to = this.length;
    if (from < to) {
        do {--to} while (from<to && chars(this.get(to)));
        ++to;
    }
    if (from===0 && to===this.length) {return this;}
    var result = String$(this.substring(from, to));
    if (this.codePoints !== undefined) {
        result.codePoints = this.codePoints - from - this.length + to;
    }
    return result;
}
String$proto.trim.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['trim']};
String$proto.trimLeading = function(/*Category*/chars) {
    var from = 0;
    while (from<this.length && chars(this.get(from))) {++from}
    if (from===0) {return this;}
    var result = String$(this.substring(from, this.length));
    if (this.codePoints !== undefined) {
        result.codePoints = this.codePoints - from;
    }
    return result;
}
String$proto.trimLeading.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['trimLeading']};
String$proto.trimTrailing = function(/*Category*/chars) {
    var to = this.length;
    if (to > 0) {
        do {--to} while (to>=0 && chars(this.get(to)));
        ++to;
    }
    if (to===this.length) {return this;}
    else if (to===0) { return String$("",0); }
    var result = String$(this.substring(0, to));
    if (this.codePoints !== undefined) {
        result.codePoints = this.codePoints - this.length + to;
    }
    return result;
}
String$proto.trimTrailing.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['trimTrailing']};

String$proto.initial = function(length) {
    if (length >= this.codePoints) {return this}
    var count = 0;
    var i = 0;
    for (; i<this.length && count<length; ++i, ++count) {
        if ((this.charCodeAt(i)&0xfc00) === 0xd800) {++i}
    }
    if (i >= this.length) {
        this.codePoints = count;
        return this;
    }
    return String$(this.substr(0, i), count);
}
String$proto.initial.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['initial']};
String$proto.terminal = function(length) {
    if (length >= this.codePoints) {return this}
    var count = 0;
    var i = this.length;
    for (; i>0 && count<length; ++count) {
        if ((this.charCodeAt(--i)&0xfc00) === 0xdc00) {--i}
    }
    if (i <= 0) {
        this.codePoints = count;
        return this;
    }
    return String$(this.substr(i), count);
}
String$proto.terminal.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['terminal']};
defineAttr(String$proto, 'hash', function() {
    if (this._hash === undefined) {
        for (var i = 0; i < this.length; i++) {
          var c = this.charCodeAt(i);
          this._hash += c + (this._hash << 10);
          this._hash ^= this._hash >> 6;
    }

    this._hash += this._hash << 3;
    this._hash ^= this._hash >> 11;
    this._hash += this._hash << 15;
    this._hash = this._hash & ((1 << 29) - 1);
  }
  return this._hash;
});

function cmpSubString(str, subStr, offset) {
    for (var i=0; i<subStr.length; ++i) {
        if (str.charCodeAt(offset+i)!==subStr.charCodeAt(i)) {return false}
    }
    return true;
}
String$proto.startsWith = function(str) {
    if (str.length > this.length) {return false}
    return cmpSubString(this, str, 0);
}
String$proto.startsWith.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['startsWith']};
String$proto.endsWith = function(str) {
    var start = this.length - str.length
    if (start < 0) {return false}
    return cmpSubString(this, str, start);
}
String$proto.endsWith.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['endsWith']};
String$proto.contains = function(sub) {
    var str;
    if (sub.constructor === String) {str = sub}
    else if (sub.constructor !== Character.$$) {return false}
    else {str = codepointToString(sub.value)}
    return this.indexOf(str) >= 0;
}
String$proto.contains.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['contains']};
defineAttr(String$proto, 'normalized', function() {
    // make use of the fact that all WS characters are single UTF-16 code units
    var result = "";
    var len = 0;
    var first = true;
    var i1 = 0;
    while (i1 < this.length) {
        while (this.charCodeAt(i1) in $WS) {
            if (++i1 >= this.length) {return String$(result)}
        }
        var i2 = i1;
        var cc = this.charCodeAt(i2);
        do {
            ++i2;
            if ((cc&0xfc00) === 0xd800) {++i2}
            ++len;
            cc = this.charCodeAt(i2);
        } while (i2<this.length && !(cc in $WS));
        if (!first) {
            result += " ";
            ++len;
        }
        first = false;
        result += this.substring(i1, i2);
        i1 = i2+1;
    }
    return String$(result, len);
});
String$proto.firstOccurrence = function(sub) {
    if (sub.length == 0) {return 0}
    var bound = this.length - sub.length;
    for (var i=0, count=0; i<=bound; ++count) {
        if (cmpSubString(this, sub, i)) {return count}
        if ((this.charCodeAt(i++)&0xfc00) === 0xd800) {++i}
    }
    return null;
}
String$proto.firstOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['firstOccurrence']};
String$proto.lastOccurrence = function(sub) {
    if (sub.length == 0) {return this.length>0 ? this.length-1 : 0}
    for (var i=this.length-sub.length; i>=0; --i) {
        if (cmpSubString(this, sub, i)) {
            for (var count=0; i>0; ++count) {
                if ((this.charCodeAt(--i)&0xfc00) === 0xdc00) {--i}
            }
            return count;
        }
    }
    return null;
}
String$proto.lastOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['lastOccurrence']};
String$proto.firstCharacterOccurrence = function(subc) {
    for (var i=0, count=0; i<this.length; count++) {
        var cp = this.charCodeAt(i++);
        if (((cp&0xfc00) === 0xd800) && i<this.length) {
            cp = (cp<<10) + this.charCodeAt(i++) - 0x35fdc00;
        }
        if (cp === subc.value) {return count;}
    }
    this.codePoints = count;
    return null;
}
String$proto.firstCharacterOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['firstCharacterOccurrence']};
String$proto.lastCharacterOccurrence = function(subc) {
    for (var i=this.length-1, count=0; i>=0; count++) {
        var cp = this.charCodeAt(i--);
        if (((cp%0xfc00) === 0xdc00) && i>=0) {
           cp = (this.charCodeAt(i--)<<10) + cp - 0x35fdc00;
        }
        if (cp === subc.value) {
            if (this.codePoints === undefined) {this.codePoints = countCodepoints(this);}
            return this.codePoints - count - 1;
        }
    }
    this.codePoints = count;
    return null;
}
String$proto.lastCharacterOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['lastCharacterOccurrence']};
defineAttr(String$proto, 'characters', function() {
    return this.size>0 ? this:getEmpty();
});
defineAttr(String$proto, 'first', function() { return this.get(0); });
defineAttr(String$proto, 'last', function(){ return this.size>0?this.get(this.size.predecessor):null; });
defineAttr(String$proto, 'keys', function() {
    //TODO implement!!!
    return this.size > 0 ? Range(0, this.size.predecessor, {Element:{t:Integer}}) : getEmpty();
});
String$proto.join = function(strings) {
    var it = strings.iterator();
    var str = it.next();
    if (str === getFinished()) {return String$("", 0);}
    if (this.codePoints === undefined) {this.codePoints = countCodepoints(this)}
    var result = str;
    var len = str.codePoints;
    while ((str = it.next()) !== getFinished()) {
        result += this;
        result += str;
        len += this.codePoints + str.codePoints;
    }
    return String$(result, isNaN(len)?undefined:len);
}
String$proto.join.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['join']};
function isWhitespace(c) { return c.value in $WS; }
String$proto.$split = function(sep, discard, group) {
    // shortcut for empty input
    if (this.length === 0) {return Singleton(this, {Element:{t:String$}}); }

    if (sep === undefined) {sep = isWhitespace}
    if (discard === undefined) {discard = true}
    if (group === undefined) {group = true}

    //TODO: return an iterable which determines the next token on demand
    var tokens = [];
    var tokenBegin = 0;
    var tokenBeginCount = 0;
    var count = 0;
    var value = this;
    var separator = true;

    function pushToken(tokenEnd) {
        tokens.push(String$(value.substring(tokenBegin, tokenEnd), count-tokenBeginCount));
    }
    if (isOfType(sep, {t:Iterable})) {
        var sepChars = {}
        var it = sep.iterator();
        var c; while ((c=it.next()) !== getFinished()) {sepChars[c.value] = true}
        for (var i=0; i<this.length;) {
            var j = i;
            var cp = this.charCodeAt(i++);
            if ((cp&0xfc00)===0xd800 && i<this.length) {
                cp = (cp<<10) + this.charCodeAt(i++) - 0x35fdc00;
            }
            if (cp in sepChars) {
                if (!group) {
                    // ungrouped separator: store preceding token
                    pushToken(j);
                    if (!discard) {
                        // store separator as token
                        tokens.push(String$(this.substring(j, i), 1));
                    }
                    // next token begins after this character
                    tokenBegin = i;
                    tokenBeginCount = count + 1;
                } else if (!separator || (j == 0)) {
                    // begin of grouped separator: store preceding token
                    pushToken(j);
                    // separator token begins at this character
                    tokenBegin = j;
                    tokenBeginCount = count;
                }
                separator = true;

            } else if (separator) {
                // first non-separator after separators or at beginning
                if (!discard && (tokenBegin != j)) {
                    // store preceding grouped separator (if group=false then tokenBegin=j)
                    pushToken(j);
                }
                // non-separator token begins at this character
                tokenBegin = j;
                tokenBeginCount = count;
                separator = false;
            }
        }
        if (tokenBegin != i) {
            pushToken(i);
        }
    } else {
        for (var i=0; i<this.length; ++count) {
            var j = i;
            var cp = this.charCodeAt(i++);
            if ((cp&0xfc00)===0xd800 && i<this.length) {
                cp = (cp<<10) + this.charCodeAt(i++) - 0x35fdc00;
            }

            if (sep(Character(cp))) {
                if (!group) {
                    // ungrouped separator: store preceding token
                    pushToken(j);
                    if (!discard) {
                        // store separator as token
                        tokens.push(String$(this.substring(j, i), 1));
                    }
                    // next token begins after this character
                    tokenBegin = i;
                    tokenBeginCount = count + 1;
                } else if (!separator || (j == 0)) {
                    // begin of grouped separator: store preceding token
                    pushToken(j);
                    // separator token begins at this character
                    tokenBegin = j;
                    tokenBeginCount = count;
                }
                separator = true;

            } else if (separator) {
                // first non-separator after separators or at beginning
                if (!discard && (tokenBegin != j)) {
                    // store preceding grouped separator (if group=false then tokenBegin=j)
                    pushToken(j);
                }
                // non-separator token begins at this character
                tokenBegin = j;
                tokenBeginCount = count;
                separator = false;
            }
        }

        if ((tokenBegin != i) && !(separator && discard)) {
            // store preceding token (may be a grouped separator)
            pushToken(i);
        }
        if (separator) {
            // if last character was a separator then there's another empty token
            tokens.push(String$("", 0));
        }
    }

    this.codePoints = count;
    return ArraySequence(tokens, {Element:{t:String$}});
}
String$proto.$split.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['split']};
defineAttr(String$proto, 'reversed', function() {
    var result = "";
    for (var i=this.length; i>0;) {
        var cc = this.charCodeAt(--i);
        if ((cc&0xfc00)!==0xdc00 || i===0) {
            result += this.charAt(i);
        } else {
            result += this.substr(--i, 2);
        }
    }
    return String$(result);
});
String$proto.$replace = function(sub, repl) {
    return String$(this.replace(new RegExp(sub, 'g'), repl));
}
String$proto.$replace.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['replace']};
String$proto.repeat = function(times) {
    var sb = StringBuilder();
    for (var i = 0; i < times; i++) {
        sb.append(this);
    }
    return sb.string;
}
String$proto.repeat.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['repeat']};
function isNewline(c) { return c.value===10; }
defineAttr(String$proto, 'lines', function() {
    return this.$split(isNewline, true);
});
String$proto.occurrences = function(sub) {
    if (sub.length == 0) {return 0}
    var ocs = [];
    var bound = this.length - sub.length;
    for (var i=0, count=0; i<=bound; ++count) {
        if (cmpSubString(this, sub, i)) {
            ocs.push(count);
            i+=sub.length;
        } else if ((this.charCodeAt(i++)&0xfc00) === 0xd800) {++i;}
    }
    return ocs.length > 0 ? ocs : getEmpty();
}
String$proto.occurrences.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['occurrences']};
String$proto.$filter = function(f) {
    var r = Iterable.$$.prototype.$filter.apply(this, [f]);
    return String$(r);
}
String$proto.$filter.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['filter']};
String$proto.skipping = function(skip) {
    if (skip==0) return this;
    return this.segment(skip, this.size);
}
String$proto.skipping.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['skipping']};
String$proto.taking = function(take) {
    if (take==0) return getEmpty();
    return this.segment(0, take);
}
String$proto.taking.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['taking']};
String$proto.by = function(step) {
    var r = Iterable.$$.prototype.by.apply(this, [step]);
    return String$(r);
}
String$proto.by.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['by']};
String$proto.$sort = function(f) {
    var r = Iterable.$$.prototype.$sort.apply(this, [f]);
    return String$(r);
}
String$proto.$sort.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['String']['$m']['sort']};
defineAttr(String$proto, 'coalesced', function(){ return this; });

function StringIterator(string) {
    var that = new StringIterator.$$;
    that.str = string;
    that.index = 0;
    return that;
}
StringIterator.$$metamodel$$={$nm:'StringIterator',$mt:'cls',$ps:[{t:String$}],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Iterator']};

initTypeProto(StringIterator, 'ceylon.language::StringIterator', $init$Basic(), Iterator);
var StringIterator$proto = StringIterator.$$.prototype;
StringIterator$proto.$$targs$$={Element:{t:Character}, Absent:{t:Null}};
StringIterator$proto.next = function() {
    if (this.index >= this.str.length) { return getFinished(); }
    var first = this.str.charCodeAt(this.index++);
    if ((first&0xfc00) !== 0xd800 || this.index >= this.str.length) {
        return Character(first);
    }
    return Character((first<<10) + this.str.charCodeAt(this.index++) - 0x35fdc00);
}

function countCodepoints(str) {
    var count = 0;
    for (var i=0; i<str.length; ++i) {
        ++count;
        if ((str.charCodeAt(i)&0xfc00) === 0xd800) {++i}
    }
    return count;
}
function codepointToString(cp) {
    if (cp <= 0xffff) {
        return String.fromCharCode(cp);
    }
    return String.fromCharCode((cp>>10)+0xd7c0, (cp&0x3ff)+0xdc00);
}
function codepointFromString(str, index) {
    var first = str.charCodeAt(index);
    if ((first&0xfc00) !== 0xd800) {return first}
    var second = str.charCodeAt(index+1);
    return isNaN(second) ? first : ((first<<10) + second - 0x35fdc00);
}
exports.codepointFromString=codepointFromString;

function Character(value) {
    if (value && value.getT$name && value.getT$name() === 'ceylon.language::Character') {
        return value;
    }
    var that = new Character.$$;
    that.value = value;
    return that;
}
Character.$$metamodel$$={$ps:[],$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Character']};

initTypeProto(Character, 'ceylon.language::Character', Object$, Comparable, $init$Enumerable());
var Character$proto = Character.$$.prototype;
defineAttr(Character$proto, 'string', function(){ return String$(codepointToString(this.value)); });
Character$proto.equals = function(other) {
    return other.constructor===Character.$$ && other.value===this.value;
}
defineAttr(Character$proto, 'hash', function(){ return this.value; });
Character$proto.compare = function(other) {
    return this.value===other.value ? equal
                                    : (this.value<other.value ? smaller:larger);
}
defineAttr(Character$proto, 'uppercased', function() {
    var ucstr = codepointToString(this.value).toUpperCase();
    return Character(codepointFromString(ucstr, 0));
});
defineAttr(Character$proto, 'lowercased', function() {
    var lcstr = codepointToString(this.value).toLowerCase();
    return Character(codepointFromString(lcstr, 0));
});
defineAttr(Character$proto, 'titlecased', function() {
    var tc = $toTitlecase[this.value];
    return tc===undefined ? this.uppercased : Character(tc);
});
var $WS={0x9:true, 0xa:true, 0xb:true, 0xc:true, 0xd:true, 0x20:true, 0x85:true,
    0x1680:true, 0x180e:true, 0x2028:true, 0x2029:true, 0x205f:true, 0x3000:true,
    0x1c:true, 0x1d:true, 0x1e:true, 0x1f:true};
for (var i=0x2000; i<0x2007; i++) { $WS[i]=true; }
for (var i=0x2008; i<=0x200a; i++) { $WS[i]=true; }
var $digit={0x30:true, 0x660:true, 0x6f0:true, 0x7c0:true, 0x966:true, 0x9e6:true, 0xa66:true,
    0xae6:true, 0xb66:true, 0xbe6:true, 0xc66:true, 0xce6:true, 0xd66:true, 0xe50:true,
    0xed0:true, 0xf20:true, 0x1040:true, 0x1090:true, 0x17e0:true, 0x1810:true, 0x1946:true,
    0x19d0:true, 0x1a80:true, 0x1a90:true, 0x1b50:true, 0x1bb0:true, 0x1c40:true, 0x1c50:true,
    0xa620:true, 0xa8d0:true, 0xa900:true, 0xa9d0:true, 0xaa50:true, 0xabf0:true, 0xff10:true,
    0x104a0:true, 0x11066:true, 0x110f0:true, 0x11136:true, 0x111d0:true, 0x116c0:true}
var $titlecase={
    0x1c5: [0x1c4, 0x1c6], 0x1c8: [0x1c7, 0x1c9], 0x1cb: [0x1ca, 0x1cc], 0x1f2: [0x1f1, 0x1f3],
    0x1f88: [undefined, 0x1f80], 0x1f89: [undefined, 0x1f81], 0x1f8a: [undefined, 0x1f82],
    0x1f8b: [undefined, 0x1f83], 0x1f8c: [undefined, 0x1f84], 0x1f8d: [undefined, 0x1f85],
    0x1f8e: [undefined, 0x1f86], 0x1f8f: [undefined, 0x1f87], 0x1f98: [undefined, 0x1f90],
    0x1f99: [undefined, 0x1f91], 0x1f9a: [undefined, 0x1f92], 0x1f9b: [undefined, 0x1f93],
    0x1f9c: [undefined, 0x1f94], 0x1f9d: [undefined, 0x1f95], 0x1f9e: [undefined, 0x1f96],
    0x1f9f: [undefined, 0x1f97], 0x1fa8: [undefined, 0x1fa0], 0x1fa9: [undefined, 0x1fa1],
    0x1faa: [undefined, 0x1fa2], 0x1fab: [undefined, 0x1fa3], 0x1fac: [undefined, 0x1fa4],
    0x1fad: [undefined, 0x1fa5], 0x1fae: [undefined, 0x1fa6], 0x1faf: [undefined, 0x1fa7],
    0x1fbc: [undefined, 0x1fb3], 0x1fcc: [undefined, 0x1fc3], 0x1ffc: [undefined, 0x1ff3]
}
var $toTitlecase={
    0x1c6:0x1c5, 0x1c7:0x1c8, 0x1ca:0x1cb, 0x1f1:0x1f2,
    0x1c4:0x1c5, 0x1c9:0x1c8, 0x1cc:0x1cb, 0x1f3:0x1f2, 0x1f80:0x1f88, 0x1f81:0x1f89, 0x1f82:0x1f8a,
    0x1f83:0x1f8b, 0x1f84:0x1f8c, 0x1f85:0x1f8d, 0x1f86:0x1f8e, 0x1f87:0x1f8f, 0x1f90:0x1f98,
    0x1f91:0x1f99, 0x1f92:0x1f9a, 0x1f93:0x1f9b, 0x1f94:0x1f9c, 0x1f95:0x1f9d, 0x1f96:0x1f9e,
    0x1f97:0x1f9f, 0x1fa0:0x1fa8, 0x1fa1:0x1fa9, 0x1fa2:0x1faa, 0x1fa3:0x1fab, 0x1fa4:0x1fac,
    0x1fa5:0x1fad, 0x1fa6:0x1fae, 0x1fa7:0x1faf, 0x1fb3:0x1fbc, 0x1fc3:0x1fcc, 0x1ff3:0x1ffc
}
defineAttr(Character$proto, 'whitespace', function(){ return this.value in $WS; });
defineAttr(Character$proto, 'control', function(){ return this.value<32 || this.value===127; });
defineAttr(Character$proto, 'digit', function() {
    var check = this.value & 0xfffffff0;
    if (check in $digit) {
        return (this.value&0xf) <= 9;
    }
    if ((check|6) in $digit) {
        return (this.value&0xf) >= 6;
    }
    return this.value>=0x1d7ce && this.value<=0x1d7ff;
});
defineAttr(Character$proto, 'integerValue', function(){ return this.value; });
defineAttr(Character$proto, 'integer',function(){ return this.value; });
defineAttr(Character$proto, 'uppercase', function() {
    var str = codepointToString(this.value);
    return str.toLowerCase()!==str && !(this.value in $titlecase);
});
defineAttr(Character$proto, 'lowercase', function() {
    var str = codepointToString(this.value);
    return str.toUpperCase()!==str && !(this.value in $titlecase);
});
defineAttr(Character$proto, 'titlecase', function(){ return this.value in $titlecase; });
defineAttr(Character$proto, 'letter', function() {
    //TODO: this captures only letters that have case
    var str = codepointToString(this.value);
    return str.toUpperCase()!==str || str.toLowerCase()!==str || (this.value in $titlecase);
});
defineAttr(Character$proto, 'successor', function() {
    var succ = this.value+1;
    if ((succ&0xf800) === 0xd800) {return Character(0xe000)}
    return Character((succ<=0x10ffff) ? succ:0);
});
defineAttr(Character$proto, 'predecessor', function() {
    var succ = this.value-1;
    if ((succ&0xf800) === 0xd800) {return Character(0xd7ff)}
    return Character((succ>=0) ? succ:0x10ffff);
});
Character$proto.distanceFrom = function(other) {
    return this.value - other.value;
}

function StringBuilder(/*String...*/comps) {
    var that = new StringBuilder.$$;
    that.value = "";
    if (comps !== undefined) {
        that.appendAll(comps);
    }
    return that;
}
StringBuilder.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['StringBuilder']};

initTypeProto(StringBuilder, 'ceylon.language::StringBuilder', $init$Basic());
var StringBuilder$proto = StringBuilder.$$.prototype;
defineAttr(StringBuilder$proto, 'string', function(){ return String$(this.value); });
StringBuilder$proto.append = function(s) {
    this.value = this.value + s;
    return this;
}
StringBuilder$proto.appendAll = function(strings) {
    var iter = strings.iterator();
    var _s; while ((_s = iter.next()) !== getFinished()) {
        this.value += _s?_s:"null";
    }
    return this;
}
StringBuilder$proto.appendCharacter = function(c) {
    this.append(c.string);
    return this;
}
StringBuilder$proto.appendNewline = function() {
    this.value = this.value + "\n";
    return this;
}
StringBuilder$proto.appendSpace = function() {
    this.value = this.value + " ";
    return this;
}
defineAttr(StringBuilder$proto, 'size', function() {
    return countCodepoints(this.value);
});
StringBuilder$proto.reset = function() {
    this.value = "";
    return this;
}
StringBuilder$proto.insert = function(pos, content) {
    if (pos <= 0) {
        this.value = content + this.value;
    } else if (pos >= this.size) {
        this.value = this.value + content;
    } else {
        this.value = this.value.slice(0, pos) + content + this.value.slice(pos);
    }
    return this;
}
StringBuilder$proto.insertCharacter = function(pos, c) {
    if (pos <= 0) {
        this.value = c.string + this.value;
    } else if (pos >= this.size) {
        this.value = this.value + c.string;
    } else {
        this.value = this.value.slice(0, pos) + c.string + this.value.slice(pos);
    }
    return this;
}
StringBuilder$proto.$delete = function(pos, count) {
    if (pos < 0) pos=0; else if (pos>this.size) return this;
    if (count > 0) {
        this.value = this.value.slice(0, pos) + this.value.slice(pos+count);
    }
    return this;
}

exports.String=String$;
exports.Character=Character;
exports.StringBuilder=StringBuilder;
var minRadix$796=(2);
var getMinRadix=function(){return minRadix$796;};
exports.getMinRadix=getMinRadix;
var maxRadix$797=(36);
var getMaxRadix=function(){return maxRadix$797;};
exports.getMaxRadix=getMaxRadix;
function parseInteger(string$798,radix$799){
    if(radix$799===undefined){radix$799=(10);}
    //assert at parseInteger.ceylon (32:4-32:49)
    if (!((radix$799.compare(getMinRadix())!==getSmaller())&&(radix$799.compare(getMaxRadix())!==getLarger()))) { throw AssertionException('Assertion failed: \'radix >= minRadix, radix <= maxRadix\' at parseInteger.ceylon (32:11-32:48)'); }
    var ii$800=(0);
    var setIi$800=function(ii$801){return ii$800=ii$801;};
    var max$802=getMinIntegerValue().divided(radix$799);
    var negative$803;
    var char$804;
    if((char$804=string$798.get(ii$800))!==null){
        if(char$804.equals(Character(45))){
            negative$803=true;
            (oldii$805=ii$800,ii$800=oldii$805.successor,oldii$805);
            var oldii$805;
        }else {
            if(char$804.equals(Character(43))){
                negative$803=false;
                (oldii$806=ii$800,ii$800=oldii$806.successor,oldii$806);
                var oldii$806;
            }else {
                negative$803=false;
            }
        }
    }else {
        return null;
    }
    var limit$807=(opt$808=(negative$803?getMinIntegerValue():null),opt$808!==null?opt$808:(-getMaxIntegerValue()));
    var opt$808;
    var length$809=string$798.size;
    var result$810=(0);
    var setResult$810=function(result$811){return result$810=result$811;};
    var sep$812=(-(1));
    var setSep$812=function(sep$813){return sep$812=sep$813;};
    var digitIndex$814=(0);
    var setDigitIndex$814=function(digitIndex$815){return digitIndex$814=digitIndex$815;};
    var groupingSize$816=(-(1));
    var setGroupingSize$816=function(groupingSize$817){return groupingSize$816=groupingSize$817;};
    while(ii$800.compare(length$809).equals(getSmaller())){
        var ch$818;
        var char$819;
        if((char$819=string$798.get(ii$800))!==null){
            ch$818=char$819;
        }else {
            return null;
        }
        if(ch$818.equals(Character(95))){
            if(sep$812.equals((-(1)))){
                var digitGroupSize$820;
                if((digitGroupSize$820=computeDigitGroupingSize(radix$799,digitIndex$814,string$798,ii$800))!==null&&(digitIndex$814.compare(digitGroupSize$820)!==getLarger())){
                    groupingSize$816=digitGroupSize$820;
                    sep$812=digitIndex$814;
                }else {
                    return null;
                }
            }else {
                if(digitIndex$814.minus(sep$812).equals(groupingSize$816)){
                    return null;
                }else {
                    sep$812=digitIndex$814;
                }
            }
        }else {
            if(((!sep$812.equals((-(1))))&&digitIndex$814.minus(sep$812).equals(groupingSize$816.plus((1))))){
                return null;
            }
            if(((ii$800.plus((1)).equals(length$809)&&radix$799.equals((10)))&&Tuple(Character(107),Tuple(Character(77),Tuple(Character(71),Tuple(Character(84),Tuple(Character(80),getEmpty(),{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}).contains(ch$818))){
                var magnitude$821;
                if((magnitude$821=computeMagnitude(radix$799,string$798.get((oldii$822=ii$800,ii$800=oldii$822.successor,oldii$822))))!==null){
                    if(limit$807.divided(magnitude$821).compare(result$810).equals(getSmaller())){
                        (result$810=result$810.times(magnitude$821));
                        break;
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                var oldii$822;
            }else {
                var digit$823;
                if((digit$823=parseDigit(ch$818,radix$799))!==null){
                    if(result$810.compare(max$802).equals(getSmaller())){
                        return null;
                    }
                    (result$810=result$810.times(radix$799));
                    if(result$810.compare(limit$807.plus(digit$823)).equals(getSmaller())){
                        return null;
                    }
                    (result$810=result$810.minus(digit$823));
                }else {
                    return null;
                }
            }
        }
        (oldii$824=ii$800,ii$800=oldii$824.successor,oldii$824);
        var oldii$824;
        (olddigitIndex$825=digitIndex$814,digitIndex$814=olddigitIndex$825.successor,olddigitIndex$825);
        var olddigitIndex$825;
    }
    if(((!sep$812.equals((-(1))))&&(!digitIndex$814.minus(sep$812).equals(groupingSize$816.plus((1)))))){
        return null;
    }
    if(digitIndex$814.equals((0))){
        return null;
    }
    return (opt$826=(negative$803?result$810:null),opt$826!==null?opt$826:(-result$810));
    var opt$826;
}
exports.parseInteger=parseInteger;
parseInteger.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'string',$mt:'prm',$t:{t:String$}},{$nm:'radix',$mt:'prm',$def:1,$t:{t:Integer}}],$an:function(){return[$throws(typeLiteral$model({Type:{t:AssertionException}}),String$("if `radix` is not between `minRadix` and `maxRadix`",51)),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['parseInteger']};
function computeDigitGroupingSize(radix$827,digitIndex$828,string$829,ii$830){
    var groupingSize$831;
    if(radix$827.equals((2))){
        groupingSize$831=(4);
    }else {
        if(radix$827.equals((10))){
            groupingSize$831=(3);
        }else {
            if(radix$827.equals((16))){
                var char$832;
                if((digitIndex$828.compare((2))!==getLarger())&&(char$832=string$829.get(ii$830.plus((3))))!==null&&char$832.equals(Character(95))){
                    groupingSize$831=(2);
                }else {
                    groupingSize$831=(4);
                }
            }else {
                groupingSize$831=null;
            }
        }
    }
    return groupingSize$831;
};computeDigitGroupingSize.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'radix',$mt:'prm',$t:{t:Integer}},{$nm:'digitIndex',$mt:'prm',$t:{t:Integer}},{$nm:'string',$mt:'prm',$t:{t:String$}},{$nm:'ii',$mt:'prm',$t:{t:Integer}}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['computeDigitGroupingSize']};
function computeMagnitude(radix$833,char$834){
    var power$835;
    var char$836;
    if((char$836=char$834)!==null){
        if(char$836.equals(Character(80))){
            power$835=(15);
        }else {
            if(char$836.equals(Character(84))){
                power$835=(12);
            }else {
                if(char$836.equals(Character(71))){
                    power$835=(9);
                }else {
                    if(char$836.equals(Character(77))){
                        power$835=(6);
                    }else {
                        if(char$836.equals(Character(107))){
                            power$835=(3);
                        }else {
                            power$835=null;
                        }
                    }
                }
            }
        }
    }else {
        power$835=null;
    }
    var power$837;
    if((power$837=power$835)!==null){
        return radix$833.power(power$837);
    }
    return null;
};computeMagnitude.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'radix',$mt:'prm',$t:{t:Integer}},{$nm:'char',$mt:'prm',$t:{ t:'u', l:[{t:Null},{t:Character}]}}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['computeMagnitude']};
var aInt$838=Character(97).integer;
var getAInt=function(){return aInt$838;};
exports.getAInt=getAInt;
var zeroInt$839=Character(48).integer;
var getZeroInt=function(){return zeroInt$839;};
exports.getZeroInt=getZeroInt;
function parseDigit(digit$840,radix$841){
    var figure$842;
    var digitInt$843=digit$840.integer;
    if((tmpvar$844=digitInt$843.minus(getZeroInt()),tmpvar$844.compare((0))!==getSmaller()&&tmpvar$844.compare((10))===getSmaller())){
        figure$842=digitInt$843.minus(getZeroInt());
    }else {
        if((tmpvar$845=digitInt$843.minus(getAInt()),tmpvar$845.compare((0))!==getSmaller()&&tmpvar$845.compare((26))===getSmaller())){
            figure$842=digitInt$843.minus(getAInt()).plus((10));
        }else {
            return null;
        }
    }
    return (figure$842.compare(radix$841).equals(getSmaller())?figure$842:null);
};parseDigit.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'digit',$mt:'prm',$t:{t:Character}},{$nm:'radix',$mt:'prm',$t:{t:Integer}}],pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['parseDigit']};
function formatInteger(integer$846,radix$847){
    if(radix$847===undefined){radix$847=(10);}
    //assert at parseInteger.ceylon (195:4-195:49)
    if (!((radix$847.compare(getMinRadix())!==getSmaller())&&(radix$847.compare(getMaxRadix())!==getLarger()))) { throw AssertionException('Assertion failed: \'radix >= minRadix, radix <= maxRadix\' at parseInteger.ceylon (195:11-195:48)'); }
    if(integer$846.equals((0))){
        return String$("0",1);
    }
    var digits$848=StringBuilder();
    var insertIndex$849;
    var i$850;
    var setI$850=function(i$851){return i$850=i$851;};
    if(integer$846.compare((0)).equals(getSmaller())){
        digits$848.append(String$("-",1));
        insertIndex$849=(1);
        i$850=integer$846;
    }else {
        insertIndex$849=(0);
        i$850=(-integer$846);
    }
    while((!i$850.equals((0)))){
        var d$852=(-i$850.remainder(radix$847));
        var c$853;
        if((tmpvar$854=d$852,tmpvar$854.compare((0))!==getSmaller()&&tmpvar$854.compare((10))===getSmaller())){
            c$853=d$852.plus(getZeroInt()).character;
        }else {
            if((tmpvar$855=d$852,tmpvar$855.compare((10))!==getSmaller()&&tmpvar$855.compare((36))===getSmaller())){
                c$853=d$852.minus((10)).plus(getAInt()).character;
            }else {
                //assert at parseInteger.ceylon (220:12-220:26)
                if (!(false)) { throw AssertionException('Assertion failed: \'false\' at parseInteger.ceylon (220:19-220:25)'); }
            }
        }
        digits$848.insertCharacter(insertIndex$849,c$853);
        i$850=i$850.plus(d$852).divided(radix$847);
    }
    return digits$848.string;
}
exports.formatInteger=formatInteger;
formatInteger.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:String$},$ps:[{$nm:'integer',$mt:'prm',$t:{t:Integer}},{$nm:'radix',$mt:'prm',$def:1,$t:{t:Integer}}],$an:function(){return[$throws(typeLiteral$model({Type:{t:AssertionException}}),String$("if `radix` is not between `minRadix` and `maxRadix`",51)),shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['formatInteger']};
function Annotated$model($$annotated){
}
Annotated$model.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Annotated']};
exports.Annotated$model=Annotated$model;
function $init$Annotated$model(){
    if (Annotated$model.$$===undefined){
        initTypeProto(Annotated$model,'ceylon.language.model::Annotated');
    }
    return Annotated$model;
}
exports.$init$Annotated$model=$init$Annotated$model;
$init$Annotated$model();
function Annotation$model($$targs$$,$$annotation){
    set_type_args($$annotation,$$targs$$);
}
Annotation$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]}},satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Annotation']};
exports.Annotation$model=Annotation$model;
function $init$Annotation$model(){
    if (Annotation$model.$$===undefined){
        initTypeProto(Annotation$model,'ceylon.language.model::Annotation');
    }
    return Annotation$model;
}
exports.$init$Annotation$model=$init$Annotation$model;
$init$Annotation$model();
function Attribute$model($$targs$$,$$attribute){
    AttributeModel$model($$attribute.$$targs$$===undefined?$$targs$$:{Type:$$attribute.$$targs$$.Type},$$attribute);
    Member$model($$attribute.$$targs$$===undefined?$$targs$$:{Type:$$attribute.$$targs$$.Container,Kind:{t:Value$model,a:{Type:$$attribute.$$targs$$.Type}}},$$attribute);
    add_type_arg($$attribute,'Kind',{t:Value$model,a:{Type:$$attribute.$$targs$$.Type}});
    set_type_args($$attribute,$$targs$$);
}
Attribute$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Container:{'var':'in',},Type:{'var':'out',}},satisfies:[{t:AttributeModel$model,a:{Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Value$model,a:{Type:'Type'}}}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Attribute']};
exports.Attribute$model=Attribute$model;
function $init$Attribute$model(){
    if (Attribute$model.$$===undefined){
        initTypeProto(Attribute$model,'ceylon.language.model::Attribute',$init$AttributeModel$model(),$init$Member$model());
    }
    return Attribute$model;
}
exports.$init$Attribute$model=$init$Attribute$model;
$init$Attribute$model();
function AttributeModel$model($$targs$$,$$attributeModel){
    Model$model($$attributeModel);
    set_type_args($$attributeModel,$$targs$$);
}
AttributeModel$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',}},satisfies:[{t:Model$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['AttributeModel']};
exports.AttributeModel$model=AttributeModel$model;
function $init$AttributeModel$model(){
    if (AttributeModel$model.$$===undefined){
        initTypeProto(AttributeModel$model,'ceylon.language.model::AttributeModel',$init$Model$model());
        (function($$attributeModel){
        })(AttributeModel$model.$$.prototype);
    }
    return AttributeModel$model;
}
exports.$init$AttributeModel$model=$init$AttributeModel$model;
$init$AttributeModel$model();
function Class$model($$targs$$,$$class){
    ClassModel$model($$class.$$targs$$===undefined?$$targs$$:{Arguments:$$class.$$targs$$.Arguments,Type:$$class.$$targs$$.Type},$$class);
    Callable($$class.$$targs$$===undefined?$$targs$$:{Arguments:$$class.$$targs$$.Arguments,Return:$$class.$$targs$$.Type},$$class);
    set_type_args($$class,$$targs$$);
}
Class$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:ClassModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Callable,a:{Arguments:'Arguments',Return:'Type'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Class']};
exports.Class$model=Class$model;
function $init$Class$model(){
    if (Class$model.$$===undefined){
        initTypeProto(Class$model,'ceylon.language.model::Class',$init$ClassModel$model(),$init$Callable());
    }
    return Class$model;
}
exports.$init$Class$model=$init$Class$model;
$init$Class$model();
function ClassModel$model($$targs$$,$$classModel){
    ClassOrInterface$model($$classModel.$$targs$$===undefined?$$targs$$:{Type:$$classModel.$$targs$$.Type},$$classModel);
    set_type_args($$classModel,$$targs$$);
}
ClassModel$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:ClassOrInterface$model,a:{Type:'Type'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['ClassModel']};
exports.ClassModel$model=ClassModel$model;
function $init$ClassModel$model(){
    if (ClassModel$model.$$===undefined){
        initTypeProto(ClassModel$model,'ceylon.language.model::ClassModel',$init$ClassOrInterface$model());
        (function($$classModel){
        })(ClassModel$model.$$.prototype);
    }
    return ClassModel$model;
}
exports.$init$ClassModel$model=$init$ClassModel$model;
$init$ClassModel$model();
function ClassOrInterface$model($$targs$$,$$classOrInterface){
    Model$model($$classOrInterface);
    Type$model($$classOrInterface);
    set_type_args($$classOrInterface,$$targs$$);
}
ClassOrInterface$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',}},satisfies:[{t:Model$model},{t:Type$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['ClassOrInterface']};
exports.ClassOrInterface$model=ClassOrInterface$model;
function $init$ClassOrInterface$model(){
    if (ClassOrInterface$model.$$===undefined){
        initTypeProto(ClassOrInterface$model,'ceylon.language.model::ClassOrInterface',$init$Model$model(),$init$Type$model());
        (function($$classOrInterface){
        })(ClassOrInterface$model.$$.prototype);
    }
    return ClassOrInterface$model;
}
exports.$init$ClassOrInterface$model=$init$ClassOrInterface$model;
$init$ClassOrInterface$model();
function ConstrainedAnnotation$model($$targs$$,$$constrainedAnnotation){
    Annotation$model($$constrainedAnnotation.$$targs$$===undefined?$$targs$$:{Value:$$constrainedAnnotation.$$targs$$.Value},$$constrainedAnnotation);
    set_type_args($$constrainedAnnotation,$$targs$$);
}
ConstrainedAnnotation$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]},Values:{'var':'out',},ProgramElement:{'var':'in','satisfies':[{t:Annotated$model}]}},satisfies:[{t:Annotation$model,a:{Value:'Value'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['ConstrainedAnnotation']};
exports.ConstrainedAnnotation$model=ConstrainedAnnotation$model;
function $init$ConstrainedAnnotation$model(){
    if (ConstrainedAnnotation$model.$$===undefined){
        initTypeProto(ConstrainedAnnotation$model,'ceylon.language.model::ConstrainedAnnotation',$init$Annotation$model());
        (function($$constrainedAnnotation){
            $$constrainedAnnotation.occurs=function (programElement$856){
                var $$constrainedAnnotation=this;
                return isOfType(programElement$856,$$constrainedAnnotation.$$targs$$.ProgramElement);
            };
            $$constrainedAnnotation.occurs.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Boolean},$ps:[{$nm:'programElement',$mt:'prm',$t:{t:Annotated$model}}],$cont:ConstrainedAnnotation$model,$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['ConstrainedAnnotation']['$m']['occurs']};
        })(ConstrainedAnnotation$model.$$.prototype);
    }
    return ConstrainedAnnotation$model;
}
exports.$init$ConstrainedAnnotation$model=$init$ConstrainedAnnotation$model;
$init$ConstrainedAnnotation$model();
function Function$model($$targs$$,$$function){
    FunctionModel$model($$function.$$targs$$===undefined?$$targs$$:{Arguments:$$function.$$targs$$.Arguments,Type:$$function.$$targs$$.Type},$$function);
    Callable($$function.$$targs$$===undefined?$$targs$$:{Arguments:$$function.$$targs$$.Arguments,Return:$$function.$$targs$$.Type},$$function);
    set_type_args($$function,$$targs$$);
}
Function$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:FunctionModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Callable,a:{Arguments:'Arguments',Return:'Type'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Function']};
exports.Function$model=Function$model;
function $init$Function$model(){
    if (Function$model.$$===undefined){
        initTypeProto(Function$model,'ceylon.language.model::Function',$init$FunctionModel$model(),$init$Callable());
    }
    return Function$model;
}
exports.$init$Function$model=$init$Function$model;
$init$Function$model();
function FunctionModel$model($$targs$$,$$functionModel){
    Model$model($$functionModel);
    set_type_args($$functionModel,$$targs$$);
}
FunctionModel$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:Model$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['FunctionModel']};
exports.FunctionModel$model=FunctionModel$model;
function $init$FunctionModel$model(){
    if (FunctionModel$model.$$===undefined){
        initTypeProto(FunctionModel$model,'ceylon.language.model::FunctionModel',$init$Model$model());
        (function($$functionModel){
        })(FunctionModel$model.$$.prototype);
    }
    return FunctionModel$model;
}
exports.$init$FunctionModel$model=$init$FunctionModel$model;
$init$FunctionModel$model();
function Interface$model($$targs$$,$$interface){
    InterfaceModel$model($$interface.$$targs$$===undefined?$$targs$$:{Type:$$interface.$$targs$$.Type},$$interface);
    set_type_args($$interface,$$targs$$);
}
Interface$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',}},satisfies:[{t:InterfaceModel$model,a:{Type:'Type'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Interface']};
exports.Interface$model=Interface$model;
function $init$Interface$model(){
    if (Interface$model.$$===undefined){
        initTypeProto(Interface$model,'ceylon.language.model::Interface',$init$InterfaceModel$model());
    }
    return Interface$model;
}
exports.$init$Interface$model=$init$Interface$model;
$init$Interface$model();
function InterfaceModel$model($$targs$$,$$interfaceModel){
    ClassOrInterface$model($$interfaceModel.$$targs$$===undefined?$$targs$$:{Type:$$interfaceModel.$$targs$$.Type},$$interfaceModel);
    set_type_args($$interfaceModel,$$targs$$);
}
InterfaceModel$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',}},satisfies:[{t:ClassOrInterface$model,a:{Type:'Type'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['InterfaceModel']};
exports.InterfaceModel$model=InterfaceModel$model;
function $init$InterfaceModel$model(){
    if (InterfaceModel$model.$$===undefined){
        initTypeProto(InterfaceModel$model,'ceylon.language.model::InterfaceModel',$init$ClassOrInterface$model());
        (function($$interfaceModel){
        })(InterfaceModel$model.$$.prototype);
    }
    return InterfaceModel$model;
}
exports.$init$InterfaceModel$model=$init$InterfaceModel$model;
$init$InterfaceModel$model();
function IntersectionType$model($$intersectionType){
    Type$model($$intersectionType);
}
IntersectionType$model.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Type$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['IntersectionType']};
exports.IntersectionType$model=IntersectionType$model;
function $init$IntersectionType$model(){
    if (IntersectionType$model.$$===undefined){
        initTypeProto(IntersectionType$model,'ceylon.language.model::IntersectionType',$init$Type$model());
        (function($$intersectionType){
        })(IntersectionType$model.$$.prototype);
    }
    return IntersectionType$model;
}
exports.$init$IntersectionType$model=$init$IntersectionType$model;
$init$IntersectionType$model();
function Member$model($$targs$$,$$member){
    Callable($$member.$$targs$$===undefined?$$targs$$:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$member.$$targs$$.Type,Element:$$member.$$targs$$.Type}},Return:$$member.$$targs$$.Kind},$$member);
    add_type_arg($$member,'Arguments',{t:Tuple,a:{Rest:{t:Empty},First:$$member.$$targs$$.Type,Element:$$member.$$targs$$.Type}});
    set_type_args($$member,$$targs$$);
}
Member$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'in',},Kind:{'var':'out','satisfies':[{t:Model$model}]}},satisfies:[{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:'Type',Element:'Type'}},Return:'Kind'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Member']};
exports.Member$model=Member$model;
function $init$Member$model(){
    if (Member$model.$$===undefined){
        initTypeProto(Member$model,'ceylon.language.model::Member',$init$Callable());
        (function($$member){
        })(Member$model.$$.prototype);
    }
    return Member$model;
}
exports.$init$Member$model=$init$Member$model;
$init$Member$model();
function MemberClass$model($$targs$$,$$memberClass){
    ClassModel$model($$memberClass.$$targs$$===undefined?$$targs$$:{Arguments:$$memberClass.$$targs$$.Arguments,Type:$$memberClass.$$targs$$.Type},$$memberClass);
    Member$model($$memberClass.$$targs$$===undefined?$$targs$$:{Type:$$memberClass.$$targs$$.Container,Kind:{t:Class$model,a:{Arguments:$$memberClass.$$targs$$.Arguments,Type:$$memberClass.$$targs$$.Type}}},$$memberClass);
    add_type_arg($$memberClass,'Kind',{t:Class$model,a:{Arguments:$$memberClass.$$targs$$.Arguments,Type:$$memberClass.$$targs$$.Type}});
    set_type_args($$memberClass,$$targs$$);
}
MemberClass$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Container:{'var':'in',},Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:ClassModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Class$model,a:{Arguments:'Arguments',Type:'Type'}}}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['MemberClass']};
exports.MemberClass$model=MemberClass$model;
function $init$MemberClass$model(){
    if (MemberClass$model.$$===undefined){
        initTypeProto(MemberClass$model,'ceylon.language.model::MemberClass',$init$ClassModel$model(),$init$Member$model());
    }
    return MemberClass$model;
}
exports.$init$MemberClass$model=$init$MemberClass$model;
$init$MemberClass$model();
function MemberInterface$model($$targs$$,$$memberInterface){
    InterfaceModel$model($$memberInterface.$$targs$$===undefined?$$targs$$:{Type:$$memberInterface.$$targs$$.Type},$$memberInterface);
    Member$model($$memberInterface.$$targs$$===undefined?$$targs$$:{Type:$$memberInterface.$$targs$$.Container,Kind:{t:Interface$model,a:{Type:$$memberInterface.$$targs$$.Type}}},$$memberInterface);
    add_type_arg($$memberInterface,'Kind',{t:Interface$model,a:{Type:$$memberInterface.$$targs$$.Type}});
    set_type_args($$memberInterface,$$targs$$);
}
MemberInterface$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Container:{'var':'in',},Type:{'var':'out',}},satisfies:[{t:InterfaceModel$model,a:{Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Interface$model,a:{Type:'Type'}}}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['MemberInterface']};
exports.MemberInterface$model=MemberInterface$model;
function $init$MemberInterface$model(){
    if (MemberInterface$model.$$===undefined){
        initTypeProto(MemberInterface$model,'ceylon.language.model::MemberInterface',$init$InterfaceModel$model(),$init$Member$model());
    }
    return MemberInterface$model;
}
exports.$init$MemberInterface$model=$init$MemberInterface$model;
$init$MemberInterface$model();
function Method$model($$targs$$,$$method){
    FunctionModel$model($$method.$$targs$$===undefined?$$targs$$:{Arguments:$$method.$$targs$$.Arguments,Type:$$method.$$targs$$.Type},$$method);
    Member$model($$method.$$targs$$===undefined?$$targs$$:{Type:$$method.$$targs$$.Container,Kind:{t:Function$model,a:{Arguments:$$method.$$targs$$.Arguments,Type:$$method.$$targs$$.Type}}},$$method);
    add_type_arg($$method,'Kind',{t:Function$model,a:{Arguments:$$method.$$targs$$.Arguments,Type:$$method.$$targs$$.Type}});
    set_type_args($$method,$$targs$$);
}
Method$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Container:{'var':'in',},Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:FunctionModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Function$model,a:{Arguments:'Arguments',Type:'Type'}}}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Method']};
exports.Method$model=Method$model;
function $init$Method$model(){
    if (Method$model.$$===undefined){
        initTypeProto(Method$model,'ceylon.language.model::Method',$init$FunctionModel$model(),$init$Member$model());
    }
    return Method$model;
}
exports.$init$Method$model=$init$Method$model;
$init$Method$model();
function Model$model($$model){
}
Model$model.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Model']};
exports.Model$model=Model$model;
function $init$Model$model(){
    if (Model$model.$$===undefined){
        initTypeProto(Model$model,'ceylon.language.model::Model');
        (function($$model){
        })(Model$model.$$.prototype);
    }
    return Model$model;
}
exports.$init$Model$model=$init$Model$model;
$init$Model$model();
function OptionalAnnotation$model($$targs$$,$$optionalAnnotation){
    ConstrainedAnnotation$model($$optionalAnnotation.$$targs$$===undefined?$$targs$$:{Values:{ t:'u', l:[{t:Null},$$optionalAnnotation.$$targs$$.Value]},Value:$$optionalAnnotation.$$targs$$.Value,ProgramElement:$$optionalAnnotation.$$targs$$.ProgramElement},$$optionalAnnotation);
    set_type_args($$optionalAnnotation,$$targs$$);
}
OptionalAnnotation$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]},ProgramElement:{'var':'in','satisfies':[{t:Annotated$model}]}},satisfies:[{t:ConstrainedAnnotation$model,a:{Values:{ t:'u', l:[{t:Null},'Value']},Value:'Value',ProgramElement:'ProgramElement'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['OptionalAnnotation']};
exports.OptionalAnnotation$model=OptionalAnnotation$model;
function $init$OptionalAnnotation$model(){
    if (OptionalAnnotation$model.$$===undefined){
        initTypeProto(OptionalAnnotation$model,'ceylon.language.model::OptionalAnnotation',$init$ConstrainedAnnotation$model());
    }
    return OptionalAnnotation$model;
}
exports.$init$OptionalAnnotation$model=$init$OptionalAnnotation$model;
$init$OptionalAnnotation$model();
function optionalAnnotation$model(annotationType$857,programElement$858,$$$mptypes){
    return annotations$model(annotationType$857,programElement$858,{Values:{ t:'u', l:[{t:Null},$$$mptypes.Value]},Value:$$$mptypes.Value,ProgramElement:$$$mptypes.ProgramElement});
}
exports.optionalAnnotation$model=optionalAnnotation$model;
optionalAnnotation$model.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Value']},$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:ClassOrInterface$model,a:{Type:{t:OptionalAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:OptionalAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}]},ProgramElement:{'satisfies':[{t:Annotated$model}]}},$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['optionalAnnotation']};
function SequencedAnnotation$model($$targs$$,$$sequencedAnnotation){
    ConstrainedAnnotation$model($$sequencedAnnotation.$$targs$$===undefined?$$targs$$:{Values:{t:Sequential,a:{Element:$$sequencedAnnotation.$$targs$$.Value}},Value:$$sequencedAnnotation.$$targs$$.Value,ProgramElement:$$sequencedAnnotation.$$targs$$.ProgramElement},$$sequencedAnnotation);
    add_type_arg($$sequencedAnnotation,'Values',{t:Sequential,a:{Element:$$sequencedAnnotation.$$targs$$.Value}});
    set_type_args($$sequencedAnnotation,$$targs$$);
}
SequencedAnnotation$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]},ProgramElement:{'var':'in','satisfies':[{t:Annotated$model}]}},satisfies:[{t:ConstrainedAnnotation$model,a:{Values:{t:Sequential,a:{Element:'Value'}},Value:'Value',ProgramElement:'ProgramElement'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['SequencedAnnotation']};
exports.SequencedAnnotation$model=SequencedAnnotation$model;
function $init$SequencedAnnotation$model(){
    if (SequencedAnnotation$model.$$===undefined){
        initTypeProto(SequencedAnnotation$model,'ceylon.language.model::SequencedAnnotation',$init$ConstrainedAnnotation$model());
    }
    return SequencedAnnotation$model;
}
exports.$init$SequencedAnnotation$model=$init$SequencedAnnotation$model;
$init$SequencedAnnotation$model();
function sequencedAnnotations$model(annotationType$859,programElement$860,$$$mptypes){
    return annotations$model(annotationType$859,programElement$860,{Values:{t:Sequential,a:{Element:$$$mptypes.Value}},Value:$$$mptypes.Value,ProgramElement:$$$mptypes.ProgramElement});
}
exports.sequencedAnnotations$model=sequencedAnnotations$model;
sequencedAnnotations$model.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Value'}},$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:ClassOrInterface$model,a:{Type:{t:SequencedAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:SequencedAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}]},ProgramElement:{'satisfies':[{t:Annotated$model}]}},$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['sequencedAnnotations']};
function Type$model($$type){
}
Type$model.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Type']};
exports.Type$model=Type$model;
function $init$Type$model(){
    if (Type$model.$$===undefined){
        initTypeProto(Type$model,'ceylon.language.model::Type');
    }
    return Type$model;
}
exports.$init$Type$model=$init$Type$model;
$init$Type$model();
function UnionType$model($$unionType){
    Type$model($$unionType);
}
UnionType$model.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Type$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['UnionType']};
exports.UnionType$model=UnionType$model;
function $init$UnionType$model(){
    if (UnionType$model.$$===undefined){
        initTypeProto(UnionType$model,'ceylon.language.model::UnionType',$init$Type$model());
        (function($$unionType){
        })(UnionType$model.$$.prototype);
    }
    return UnionType$model;
}
exports.$init$UnionType$model=$init$UnionType$model;
$init$UnionType$model();
function Value$model($$targs$$,$$value){
    AttributeModel$model($$value.$$targs$$===undefined?$$targs$$:{Type:$$value.$$targs$$.Type},$$value);
    set_type_args($$value,$$targs$$);
}
Value$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{'var':'out',}},satisfies:[{t:AttributeModel$model,a:{Type:'Type'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Value']};
exports.Value$model=Value$model;
function $init$Value$model(){
    if (Value$model.$$===undefined){
        initTypeProto(Value$model,'ceylon.language.model::Value',$init$AttributeModel$model());
        (function($$value){
        })(Value$model.$$.prototype);
    }
    return Value$model;
}
exports.$init$Value$model=$init$Value$model;
$init$Value$model();
function Variable$model($$targs$$,$$variable){
    Value$model($$variable.$$targs$$===undefined?$$targs$$:{Type:$$variable.$$targs$$.Type},$$variable);
    set_type_args($$variable,$$targs$$);
}
Variable$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Type:{}},satisfies:[{t:Value$model,a:{Type:'Type'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Variable']};
exports.Variable$model=Variable$model;
function $init$Variable$model(){
    if (Variable$model.$$===undefined){
        initTypeProto(Variable$model,'ceylon.language.model::Variable',$init$Value$model());
        (function($$variable){
        })(Variable$model.$$.prototype);
    }
    return Variable$model;
}
exports.$init$Variable$model=$init$Variable$model;
$init$Variable$model();
function VariableAttribute$model($$targs$$,$$variableAttribute){
    Member$model($$variableAttribute.$$targs$$===undefined?$$targs$$:{Type:$$variableAttribute.$$targs$$.Container,Kind:{t:Variable$model,a:{Type:$$variableAttribute.$$targs$$.Type}}},$$variableAttribute);
    add_type_arg($$variableAttribute,'Kind',{t:Variable$model,a:{Type:$$variableAttribute.$$targs$$.Type}});
    Attribute$model($$variableAttribute.$$targs$$===undefined?$$targs$$:{Type:$$variableAttribute.$$targs$$.Type,Container:$$variableAttribute.$$targs$$.Container},$$variableAttribute);
    set_type_args($$variableAttribute,$$targs$$);
}
VariableAttribute$model.$$metamodel$$={mod:$$METAMODEL$$,$tp:{Container:{'var':'in',},Type:{}},satisfies:[{t:Member$model,a:{Type:'Container',Kind:{t:Variable$model,a:{Type:'Type'}}}},{t:Attribute$model,a:{Type:'Type',Container:'Container'}}],$an:function(){return[shared()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['VariableAttribute']};
exports.VariableAttribute$model=VariableAttribute$model;
function $init$VariableAttribute$model(){
    if (VariableAttribute$model.$$===undefined){
        initTypeProto(VariableAttribute$model,'ceylon.language.model::VariableAttribute',$init$Member$model(),$init$Attribute$model());
    }
    return VariableAttribute$model;
}
exports.$init$VariableAttribute$model=$init$VariableAttribute$model;
$init$VariableAttribute$model();
function annotations$model(annotationType$861,programElement$862,$$$mptypes){
    throw Exception();
}
exports.annotations$model=annotations$model;
annotations$model.$$metamodel$$={mod:$$METAMODEL$$,$t:'Values',$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:ClassOrInterface$model,a:{Type:{t:ConstrainedAnnotation$model,a:{Values:'Values',Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:ConstrainedAnnotation$model,a:{Values:'Values',Value:'Value',ProgramElement:'ProgramElement'}}]},Values:{},ProgramElement:{'satisfies':[{t:Annotated$model}]}},$an:function(){return[shared(),$native()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['annotations']};
function modules$863$model(){
    var $$modules=new modules$863$model.$$;
    return $$modules;
}
function $init$modules$863$model(){
    if (modules$863$model.$$===undefined){
        initTypeProto(modules$863$model,'ceylon.language.model::modules',Basic);
    }
    return modules$863$model;
}
exports.$init$modules$863$model=$init$modules$863$model;
$init$modules$863$model();
(function($$modules){
})(modules$863$model.$$.prototype);
var modules$864$model=modules$863$model();
var getModules$model=function(){
    return modules$864$model;
}
exports.getModules$model=getModules$model;
function nothingType$865$model(){
    var $$nothingType=new nothingType$865$model.$$;
    Type$model($$nothingType);
    return $$nothingType;
}
function $init$nothingType$865$model(){
    if (nothingType$865$model.$$===undefined){
        initTypeProto(nothingType$865$model,'ceylon.language.model::nothingType',Basic,$init$Type$model());
    }
    return nothingType$865$model;
}
exports.$init$nothingType$865$model=$init$nothingType$865$model;
$init$nothingType$865$model();
(function($$nothingType){
    defineAttr($$nothingType,'string',function(){
        var $$nothingType=this;
        return String$("Nothing",7);
    },undefined,{mod:$$METAMODEL$$,$t:{t:String},$cont:nothingType$865$model,pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['nothingType']['$at']['string']});
})(nothingType$865$model.$$.prototype);
var nothingType$866$model=nothingType$865$model();
var getNothingType$model=function(){
    return nothingType$866$model;
}
exports.getNothingType$model=getNothingType$model;
function AnnotatedDeclaration$model$declaration($$annotatedDeclaration){
    Declaration$model$declaration($$annotatedDeclaration);
    Annotated$model($$annotatedDeclaration);
}
AnnotatedDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Declaration$model$declaration},{t:Annotated$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['AnnotatedDeclaration']};
exports.AnnotatedDeclaration$model$declaration=AnnotatedDeclaration$model$declaration;
function $init$AnnotatedDeclaration$model$declaration(){
    if (AnnotatedDeclaration$model$declaration.$$===undefined){
        initTypeProto(AnnotatedDeclaration$model$declaration,'ceylon.language.model.declaration::AnnotatedDeclaration',$init$Declaration$model$declaration(),$init$Annotated$model());
        (function($$annotatedDeclaration){
        })(AnnotatedDeclaration$model$declaration.$$.prototype);
    }
    return AnnotatedDeclaration$model$declaration;
}
exports.$init$AnnotatedDeclaration$model$declaration=$init$AnnotatedDeclaration$model$declaration;
$init$AnnotatedDeclaration$model$declaration();
function ValueDeclaration$model$declaration($$valueDeclaration){
    FunctionOrValueDeclaration$model$declaration($$valueDeclaration);
    $$valueDeclaration.apply$defs$instance=function(instance$867){return null;};
}
ValueDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:FunctionOrValueDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['ValueDeclaration']};
exports.ValueDeclaration$model$declaration=ValueDeclaration$model$declaration;
function $init$ValueDeclaration$model$declaration(){
    if (ValueDeclaration$model$declaration.$$===undefined){
        initTypeProto(ValueDeclaration$model$declaration,'ceylon.language.model.declaration::ValueDeclaration',$init$FunctionOrValueDeclaration$model$declaration());
        (function($$valueDeclaration){
        })(ValueDeclaration$model$declaration.$$.prototype);
    }
    return ValueDeclaration$model$declaration;
}
exports.$init$ValueDeclaration$model$declaration=$init$ValueDeclaration$model$declaration;
$init$ValueDeclaration$model$declaration();
function ClassDeclaration$model$declaration($$classDeclaration){
    ClassOrInterfaceDeclaration$model$declaration($$classDeclaration);
    FunctionalDeclaration$model$declaration($$classDeclaration);
}
ClassDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:ClassOrInterfaceDeclaration$model$declaration},{t:FunctionalDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['ClassDeclaration']};
exports.ClassDeclaration$model$declaration=ClassDeclaration$model$declaration;
function $init$ClassDeclaration$model$declaration(){
    if (ClassDeclaration$model$declaration.$$===undefined){
        initTypeProto(ClassDeclaration$model$declaration,'ceylon.language.model.declaration::ClassDeclaration',$init$ClassOrInterfaceDeclaration$model$declaration(),$init$FunctionalDeclaration$model$declaration());
        (function($$classDeclaration){
        })(ClassDeclaration$model$declaration.$$.prototype);
    }
    return ClassDeclaration$model$declaration;
}
exports.$init$ClassDeclaration$model$declaration=$init$ClassDeclaration$model$declaration;
$init$ClassDeclaration$model$declaration();
function ClassOrInterfaceDeclaration$model$declaration($$classOrInterfaceDeclaration){
    TopLevelOrMemberDeclaration$model$declaration($$classOrInterfaceDeclaration);
    GenericDeclaration$model$declaration($$classOrInterfaceDeclaration);
}
ClassOrInterfaceDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:TopLevelOrMemberDeclaration$model$declaration},{t:GenericDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['ClassOrInterfaceDeclaration']};
exports.ClassOrInterfaceDeclaration$model$declaration=ClassOrInterfaceDeclaration$model$declaration;
function $init$ClassOrInterfaceDeclaration$model$declaration(){
    if (ClassOrInterfaceDeclaration$model$declaration.$$===undefined){
        initTypeProto(ClassOrInterfaceDeclaration$model$declaration,'ceylon.language.model.declaration::ClassOrInterfaceDeclaration',$init$TopLevelOrMemberDeclaration$model$declaration(),$init$GenericDeclaration$model$declaration());
        (function($$classOrInterfaceDeclaration){
        })(ClassOrInterfaceDeclaration$model$declaration.$$.prototype);
    }
    return ClassOrInterfaceDeclaration$model$declaration;
}
exports.$init$ClassOrInterfaceDeclaration$model$declaration=$init$ClassOrInterfaceDeclaration$model$declaration;
$init$ClassOrInterfaceDeclaration$model$declaration();
function Declaration$model$declaration($$declaration){
}
Declaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['Declaration']};
exports.Declaration$model$declaration=Declaration$model$declaration;
function $init$Declaration$model$declaration(){
    if (Declaration$model$declaration.$$===undefined){
        initTypeProto(Declaration$model$declaration,'ceylon.language.model.declaration::Declaration');
        (function($$declaration){
        })(Declaration$model$declaration.$$.prototype);
    }
    return Declaration$model$declaration;
}
exports.$init$Declaration$model$declaration=$init$Declaration$model$declaration;
$init$Declaration$model$declaration();
function FunctionDeclaration$model$declaration($$functionDeclaration){
    FunctionOrValueDeclaration$model$declaration($$functionDeclaration);
    GenericDeclaration$model$declaration($$functionDeclaration);
    FunctionalDeclaration$model$declaration($$functionDeclaration);
}
FunctionDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:FunctionOrValueDeclaration$model$declaration},{t:GenericDeclaration$model$declaration},{t:FunctionalDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['FunctionDeclaration']};
exports.FunctionDeclaration$model$declaration=FunctionDeclaration$model$declaration;
function $init$FunctionDeclaration$model$declaration(){
    if (FunctionDeclaration$model$declaration.$$===undefined){
        initTypeProto(FunctionDeclaration$model$declaration,'ceylon.language.model.declaration::FunctionDeclaration',$init$FunctionOrValueDeclaration$model$declaration(),$init$GenericDeclaration$model$declaration(),$init$FunctionalDeclaration$model$declaration());
        (function($$functionDeclaration){
        })(FunctionDeclaration$model$declaration.$$.prototype);
    }
    return FunctionDeclaration$model$declaration;
}
exports.$init$FunctionDeclaration$model$declaration=$init$FunctionDeclaration$model$declaration;
$init$FunctionDeclaration$model$declaration();
function FunctionOrValueDeclaration$model$declaration($$functionOrValueDeclaration){
    TopLevelOrMemberDeclaration$model$declaration($$functionOrValueDeclaration);
}
FunctionOrValueDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:TopLevelOrMemberDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['FunctionOrValueDeclaration']};
exports.FunctionOrValueDeclaration$model$declaration=FunctionOrValueDeclaration$model$declaration;
function $init$FunctionOrValueDeclaration$model$declaration(){
    if (FunctionOrValueDeclaration$model$declaration.$$===undefined){
        initTypeProto(FunctionOrValueDeclaration$model$declaration,'ceylon.language.model.declaration::FunctionOrValueDeclaration',$init$TopLevelOrMemberDeclaration$model$declaration());
        (function($$functionOrValueDeclaration){
        })(FunctionOrValueDeclaration$model$declaration.$$.prototype);
    }
    return FunctionOrValueDeclaration$model$declaration;
}
exports.$init$FunctionOrValueDeclaration$model$declaration=$init$FunctionOrValueDeclaration$model$declaration;
$init$FunctionOrValueDeclaration$model$declaration();
function FunctionalDeclaration$model$declaration($$functionalDeclaration){
}
FunctionalDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['FunctionalDeclaration']};
exports.FunctionalDeclaration$model$declaration=FunctionalDeclaration$model$declaration;
function $init$FunctionalDeclaration$model$declaration(){
    if (FunctionalDeclaration$model$declaration.$$===undefined){
        initTypeProto(FunctionalDeclaration$model$declaration,'ceylon.language.model.declaration::FunctionalDeclaration');
        (function($$functionalDeclaration){
        })(FunctionalDeclaration$model$declaration.$$.prototype);
    }
    return FunctionalDeclaration$model$declaration;
}
exports.$init$FunctionalDeclaration$model$declaration=$init$FunctionalDeclaration$model$declaration;
$init$FunctionalDeclaration$model$declaration();
function GenericDeclaration$model$declaration($$genericDeclaration){
}
GenericDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['GenericDeclaration']};
exports.GenericDeclaration$model$declaration=GenericDeclaration$model$declaration;
function $init$GenericDeclaration$model$declaration(){
    if (GenericDeclaration$model$declaration.$$===undefined){
        initTypeProto(GenericDeclaration$model$declaration,'ceylon.language.model.declaration::GenericDeclaration');
        (function($$genericDeclaration){
        })(GenericDeclaration$model$declaration.$$.prototype);
    }
    return GenericDeclaration$model$declaration;
}
exports.$init$GenericDeclaration$model$declaration=$init$GenericDeclaration$model$declaration;
$init$GenericDeclaration$model$declaration();
function InterfaceDeclaration$model$declaration($$interfaceDeclaration){
    ClassOrInterfaceDeclaration$model$declaration($$interfaceDeclaration);
}
InterfaceDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:ClassOrInterfaceDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['InterfaceDeclaration']};
exports.InterfaceDeclaration$model$declaration=InterfaceDeclaration$model$declaration;
function $init$InterfaceDeclaration$model$declaration(){
    if (InterfaceDeclaration$model$declaration.$$===undefined){
        initTypeProto(InterfaceDeclaration$model$declaration,'ceylon.language.model.declaration::InterfaceDeclaration',$init$ClassOrInterfaceDeclaration$model$declaration());
        (function($$interfaceDeclaration){
        })(InterfaceDeclaration$model$declaration.$$.prototype);
    }
    return InterfaceDeclaration$model$declaration;
}
exports.$init$InterfaceDeclaration$model$declaration=$init$InterfaceDeclaration$model$declaration;
$init$InterfaceDeclaration$model$declaration();
function Module$model$declaration($$module){
    Identifiable($$module);
    AnnotatedDeclaration$model$declaration($$module);
}
Module$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Identifiable},{t:AnnotatedDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['Module']};
exports.Module$model$declaration=Module$model$declaration;
function $init$Module$model$declaration(){
    if (Module$model$declaration.$$===undefined){
        initTypeProto(Module$model$declaration,'ceylon.language.model.declaration::Module',$init$Identifiable(),$init$AnnotatedDeclaration$model$declaration());
        (function($$module){
        })(Module$model$declaration.$$.prototype);
    }
    return Module$model$declaration;
}
exports.$init$Module$model$declaration=$init$Module$model$declaration;
$init$Module$model$declaration();
function Import$model$declaration($$import){
    Identifiable($$import);
    Annotated$model($$import);
}
Import$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Identifiable},{t:Annotated$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['Import']};
exports.Import$model$declaration=Import$model$declaration;
function $init$Import$model$declaration(){
    if (Import$model$declaration.$$===undefined){
        initTypeProto(Import$model$declaration,'ceylon.language.model.declaration::Import',$init$Identifiable(),$init$Annotated$model());
        (function($$import){
        })(Import$model$declaration.$$.prototype);
    }
    return Import$model$declaration;
}
exports.$init$Import$model$declaration=$init$Import$model$declaration;
$init$Import$model$declaration();
function Package$model$declaration($$package){
    Identifiable($$package);
    AnnotatedDeclaration$model$declaration($$package);
}
Package$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Identifiable},{t:AnnotatedDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['Package']};
exports.Package$model$declaration=Package$model$declaration;
function $init$Package$model$declaration(){
    if (Package$model$declaration.$$===undefined){
        initTypeProto(Package$model$declaration,'ceylon.language.model.declaration::Package',$init$Identifiable(),$init$AnnotatedDeclaration$model$declaration());
        (function($$package){
        })(Package$model$declaration.$$.prototype);
    }
    return Package$model$declaration;
}
exports.$init$Package$model$declaration=$init$Package$model$declaration;
$init$Package$model$declaration();
function OpenIntersection$model$declaration($$openIntersection){
    OpenType$model$declaration($$openIntersection);
}
OpenIntersection$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['OpenIntersection']};
exports.OpenIntersection$model$declaration=OpenIntersection$model$declaration;
function $init$OpenIntersection$model$declaration(){
    if (OpenIntersection$model$declaration.$$===undefined){
        initTypeProto(OpenIntersection$model$declaration,'ceylon.language.model.declaration::OpenIntersection',$init$OpenType$model$declaration());
        (function($$openIntersection){
        })(OpenIntersection$model$declaration.$$.prototype);
    }
    return OpenIntersection$model$declaration;
}
exports.$init$OpenIntersection$model$declaration=$init$OpenIntersection$model$declaration;
$init$OpenIntersection$model$declaration();
function OpenParameterisedType$model$declaration($$targs$$,$$openParameterisedType){
    OpenType$model$declaration($$openParameterisedType);
    set_type_args($$openParameterisedType,$$targs$$);
}
OpenParameterisedType$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,$tp:{DeclarationType:{'var':'out','satisfies':[{t:ClassOrInterfaceDeclaration$model$declaration}]}},satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['OpenParameterisedType']};
exports.OpenParameterisedType$model$declaration=OpenParameterisedType$model$declaration;
function $init$OpenParameterisedType$model$declaration(){
    if (OpenParameterisedType$model$declaration.$$===undefined){
        initTypeProto(OpenParameterisedType$model$declaration,'ceylon.language.model.declaration::OpenParameterisedType',$init$OpenType$model$declaration());
        (function($$openParameterisedType){
        })(OpenParameterisedType$model$declaration.$$.prototype);
    }
    return OpenParameterisedType$model$declaration;
}
exports.$init$OpenParameterisedType$model$declaration=$init$OpenParameterisedType$model$declaration;
$init$OpenParameterisedType$model$declaration();
function OpenType$model$declaration($$openType){
}
OpenType$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['OpenType']};
exports.OpenType$model$declaration=OpenType$model$declaration;
function $init$OpenType$model$declaration(){
    if (OpenType$model$declaration.$$===undefined){
        initTypeProto(OpenType$model$declaration,'ceylon.language.model.declaration::OpenType');
    }
    return OpenType$model$declaration;
}
exports.$init$OpenType$model$declaration=$init$OpenType$model$declaration;
$init$OpenType$model$declaration();
function OpenTypeVariable$model$declaration($$openTypeVariable){
    OpenType$model$declaration($$openTypeVariable);
}
OpenTypeVariable$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['OpenTypeVariable']};
exports.OpenTypeVariable$model$declaration=OpenTypeVariable$model$declaration;
function $init$OpenTypeVariable$model$declaration(){
    if (OpenTypeVariable$model$declaration.$$===undefined){
        initTypeProto(OpenTypeVariable$model$declaration,'ceylon.language.model.declaration::OpenTypeVariable',$init$OpenType$model$declaration());
        (function($$openTypeVariable){
        })(OpenTypeVariable$model$declaration.$$.prototype);
    }
    return OpenTypeVariable$model$declaration;
}
exports.$init$OpenTypeVariable$model$declaration=$init$OpenTypeVariable$model$declaration;
$init$OpenTypeVariable$model$declaration();
function OpenUnion$model$declaration($$openUnion){
    OpenType$model$declaration($$openUnion);
}
OpenUnion$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['OpenUnion']};
exports.OpenUnion$model$declaration=OpenUnion$model$declaration;
function $init$OpenUnion$model$declaration(){
    if (OpenUnion$model$declaration.$$===undefined){
        initTypeProto(OpenUnion$model$declaration,'ceylon.language.model.declaration::OpenUnion',$init$OpenType$model$declaration());
        (function($$openUnion){
        })(OpenUnion$model$declaration.$$.prototype);
    }
    return OpenUnion$model$declaration;
}
exports.$init$OpenUnion$model$declaration=$init$OpenUnion$model$declaration;
$init$OpenUnion$model$declaration();
function ParameterDeclaration$model$declaration($$parameterDeclaration){
}
ParameterDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['ParameterDeclaration']};
exports.ParameterDeclaration$model$declaration=ParameterDeclaration$model$declaration;
function $init$ParameterDeclaration$model$declaration(){
    if (ParameterDeclaration$model$declaration.$$===undefined){
        initTypeProto(ParameterDeclaration$model$declaration,'ceylon.language.model.declaration::ParameterDeclaration');
        (function($$parameterDeclaration){
        })(ParameterDeclaration$model$declaration.$$.prototype);
    }
    return ParameterDeclaration$model$declaration;
}
exports.$init$ParameterDeclaration$model$declaration=$init$ParameterDeclaration$model$declaration;
$init$ParameterDeclaration$model$declaration();
function SetterDeclaration$model$declaration($$setterDeclaration){
    Annotated$model($$setterDeclaration);
}
SetterDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Annotated$model}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['SetterDeclaration']};
exports.SetterDeclaration$model$declaration=SetterDeclaration$model$declaration;
function $init$SetterDeclaration$model$declaration(){
    if (SetterDeclaration$model$declaration.$$===undefined){
        initTypeProto(SetterDeclaration$model$declaration,'ceylon.language.model.declaration::SetterDeclaration',$init$Annotated$model());
        (function($$setterDeclaration){
        })(SetterDeclaration$model$declaration.$$.prototype);
    }
    return SetterDeclaration$model$declaration;
}
exports.$init$SetterDeclaration$model$declaration=$init$SetterDeclaration$model$declaration;
$init$SetterDeclaration$model$declaration();
function TopLevelOrMemberDeclaration$model$declaration($$topLevelOrMemberDeclaration){
    AnnotatedDeclaration$model$declaration($$topLevelOrMemberDeclaration);
    TypedDeclaration$model$declaration($$topLevelOrMemberDeclaration);
}
TopLevelOrMemberDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:AnnotatedDeclaration$model$declaration},{t:TypedDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['TopLevelOrMemberDeclaration']};
exports.TopLevelOrMemberDeclaration$model$declaration=TopLevelOrMemberDeclaration$model$declaration;
function $init$TopLevelOrMemberDeclaration$model$declaration(){
    if (TopLevelOrMemberDeclaration$model$declaration.$$===undefined){
        initTypeProto(TopLevelOrMemberDeclaration$model$declaration,'ceylon.language.model.declaration::TopLevelOrMemberDeclaration',$init$AnnotatedDeclaration$model$declaration(),$init$TypedDeclaration$model$declaration());
        (function($$topLevelOrMemberDeclaration){
        })(TopLevelOrMemberDeclaration$model$declaration.$$.prototype);
    }
    return TopLevelOrMemberDeclaration$model$declaration;
}
exports.$init$TopLevelOrMemberDeclaration$model$declaration=$init$TopLevelOrMemberDeclaration$model$declaration;
$init$TopLevelOrMemberDeclaration$model$declaration();
function TypeParameter$model$declaration($$typeParameter){
    Declaration$model$declaration($$typeParameter);
}
TypeParameter$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:Declaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['TypeParameter']};
exports.TypeParameter$model$declaration=TypeParameter$model$declaration;
function $init$TypeParameter$model$declaration(){
    if (TypeParameter$model$declaration.$$===undefined){
        initTypeProto(TypeParameter$model$declaration,'ceylon.language.model.declaration::TypeParameter',$init$Declaration$model$declaration());
    }
    return TypeParameter$model$declaration;
}
exports.$init$TypeParameter$model$declaration=$init$TypeParameter$model$declaration;
$init$TypeParameter$model$declaration();
function TypedDeclaration$model$declaration($$typedDeclaration){
}
TypedDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['TypedDeclaration']};
exports.TypedDeclaration$model$declaration=TypedDeclaration$model$declaration;
function $init$TypedDeclaration$model$declaration(){
    if (TypedDeclaration$model$declaration.$$===undefined){
        initTypeProto(TypedDeclaration$model$declaration,'ceylon.language.model.declaration::TypedDeclaration');
        (function($$typedDeclaration){
        })(TypedDeclaration$model$declaration.$$.prototype);
    }
    return TypedDeclaration$model$declaration;
}
exports.$init$TypedDeclaration$model$declaration=$init$TypedDeclaration$model$declaration;
$init$TypedDeclaration$model$declaration();
function VariableDeclaration$model$declaration($$variableDeclaration){
    ValueDeclaration$model$declaration($$variableDeclaration);
}
VariableDeclaration$model$declaration.$$metamodel$$={mod:$$METAMODEL$$,satisfies:[{t:ValueDeclaration$model$declaration}],$an:function(){return[shared()];},pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['VariableDeclaration']};
exports.VariableDeclaration$model$declaration=VariableDeclaration$model$declaration;
function $init$VariableDeclaration$model$declaration(){
    if (VariableDeclaration$model$declaration.$$===undefined){
        initTypeProto(VariableDeclaration$model$declaration,'ceylon.language.model.declaration::VariableDeclaration',$init$ValueDeclaration$model$declaration());
        (function($$variableDeclaration){
        })(VariableDeclaration$model$declaration.$$.prototype);
    }
    return VariableDeclaration$model$declaration;
}
exports.$init$VariableDeclaration$model$declaration=$init$VariableDeclaration$model$declaration;
$init$VariableDeclaration$model$declaration();
function nothingType$868$model$declaration(){
    var $$nothingType=new nothingType$868$model$declaration.$$;
    OpenType$model$declaration($$nothingType);
    return $$nothingType;
}
function $init$nothingType$868$model$declaration(){
    if (nothingType$868$model$declaration.$$===undefined){
        initTypeProto(nothingType$868$model$declaration,'ceylon.language.model.declaration::nothingType',Basic,$init$OpenType$model$declaration());
    }
    return nothingType$868$model$declaration;
}
exports.$init$nothingType$868$model$declaration=$init$nothingType$868$model$declaration;
$init$nothingType$868$model$declaration();
(function($$nothingType){
    defineAttr($$nothingType,'string',function(){
        var $$nothingType=this;
        return String$("Nothing",7);
    },undefined,{mod:$$METAMODEL$$,$t:{t:String},$cont:nothingType$868$model$declaration,pkg:'ceylon.language.model.declaration',d:$$METAMODEL$$['ceylon.language.model.declaration']['nothingType']['$at']['string']});
})(nothingType$868$model$declaration.$$.prototype);
var nothingType$869$model$declaration=nothingType$868$model$declaration();
var getNothingType$model$declaration=function(){
    return nothingType$869$model$declaration;
}
exports.getNothingType$model$declaration=getNothingType$model$declaration;
function Annotation($$annotation){
    $init$Annotation();
    if ($$annotation===undefined)$$annotation=new Annotation.$$;
    $$annotation.$$targs$$={Value:{t:Annotation},ProgramElement:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Annotation},ProgramElement:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}},$$annotation);
    add_type_arg($$annotation,'Value',{t:Annotation});
    return $$annotation;
}
Annotation.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Annotation},ProgramElement:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Annotation']};
exports.Annotation=Annotation;
function $init$Annotation(){
    if (Annotation.$$===undefined){
        initTypeProto(Annotation,'ceylon.language::Annotation',Basic,$init$OptionalAnnotation$model());
    }
    return Annotation;
}
exports.$init$Annotation=$init$Annotation;
$init$Annotation();
var annotation=function (){
    return Annotation();
};
annotation.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Annotation},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['annotation']};
exports.annotation=annotation;
function Shared($$shared){
    $init$Shared();
    if ($$shared===undefined)$$shared=new Shared.$$;
    $$shared.$$targs$$={Value:{t:Shared},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration},{t:Package$model$declaration},{t:Import$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Shared},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration},{t:Package$model$declaration},{t:Import$model$declaration}]}},$$shared);
    add_type_arg($$shared,'Value',{t:Shared});
    return $$shared;
}
Shared.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Shared},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration},{t:Package$model$declaration},{t:Import$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Shared']};
exports.Shared=Shared;
function $init$Shared(){
    if (Shared.$$===undefined){
        initTypeProto(Shared,'ceylon.language::Shared',Basic,$init$OptionalAnnotation$model());
    }
    return Shared;
}
exports.$init$Shared=$init$Shared;
$init$Shared();
var shared=function (){
    return Shared();
};
shared.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Shared},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['shared']};
exports.shared=shared;
function Variable($$variable){
    $init$Variable();
    if ($$variable===undefined)$$variable=new Variable.$$;
    $$variable.$$targs$$={Value:{t:Variable},ProgramElement:{t:ValueDeclaration$model$declaration}};
    OptionalAnnotation$model({Value:{t:Variable},ProgramElement:{t:ValueDeclaration$model$declaration}},$$variable);
    add_type_arg($$variable,'Value',{t:Variable});
    add_type_arg($$variable,'ProgramElement',{t:ValueDeclaration$model$declaration});
    return $$variable;
}
Variable.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Variable},ProgramElement:{t:ValueDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Variable']};
exports.Variable=Variable;
function $init$Variable(){
    if (Variable.$$===undefined){
        initTypeProto(Variable,'ceylon.language::Variable',Basic,$init$OptionalAnnotation$model());
    }
    return Variable;
}
exports.$init$Variable=$init$Variable;
$init$Variable();
var variable=function (){
    return Variable();
};
variable.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Variable},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['variable']};
exports.variable=variable;
function Abstract($$abstract){
    $init$Abstract();
    if ($$abstract===undefined)$$abstract=new Abstract.$$;
    $$abstract.$$targs$$={Value:{t:Abstract},ProgramElement:{t:ClassDeclaration$model$declaration}};
    OptionalAnnotation$model({Value:{t:Abstract},ProgramElement:{t:ClassDeclaration$model$declaration}},$$abstract);
    add_type_arg($$abstract,'Value',{t:Abstract});
    add_type_arg($$abstract,'ProgramElement',{t:ClassDeclaration$model$declaration});
    return $$abstract;
}
Abstract.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Abstract},ProgramElement:{t:ClassDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Abstract']};
exports.Abstract=Abstract;
function $init$Abstract(){
    if (Abstract.$$===undefined){
        initTypeProto(Abstract,'ceylon.language::Abstract',Basic,$init$OptionalAnnotation$model());
    }
    return Abstract;
}
exports.$init$Abstract=$init$Abstract;
$init$Abstract();
var abstract=function (){
    return Abstract();
};
abstract.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Abstract},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['abstract']};
exports.abstract=abstract;
function Final($$final){
    $init$Final();
    if ($$final===undefined)$$final=new Final.$$;
    $$final.$$targs$$={Value:{t:Final},ProgramElement:{t:ClassDeclaration$model$declaration}};
    OptionalAnnotation$model({Value:{t:Final},ProgramElement:{t:ClassDeclaration$model$declaration}},$$final);
    add_type_arg($$final,'Value',{t:Final});
    add_type_arg($$final,'ProgramElement',{t:ClassDeclaration$model$declaration});
    return $$final;
}
Final.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Final},ProgramElement:{t:ClassDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Final']};
exports.Final=Final;
function $init$Final(){
    if (Final.$$===undefined){
        initTypeProto(Final,'ceylon.language::Final',Basic,$init$OptionalAnnotation$model());
    }
    return Final;
}
exports.$init$Final=$init$Final;
$init$Final();
var $final=function (){
    return Final();
};
$final.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Final},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['final']};
exports.$final=$final;
function Actual($$actual){
    $init$Actual();
    if ($$actual===undefined)$$actual=new Actual.$$;
    $$actual.$$targs$$={Value:{t:Actual},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Actual},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}},$$actual);
    add_type_arg($$actual,'Value',{t:Actual});
    return $$actual;
}
Actual.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Actual},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Actual']};
exports.Actual=Actual;
function $init$Actual(){
    if (Actual.$$===undefined){
        initTypeProto(Actual,'ceylon.language::Actual',Basic,$init$OptionalAnnotation$model());
    }
    return Actual;
}
exports.$init$Actual=$init$Actual;
$init$Actual();
var actual=function (){
    return Actual();
};
actual.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Actual},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['actual']};
exports.actual=actual;
function Formal($$formal){
    $init$Formal();
    if ($$formal===undefined)$$formal=new Formal.$$;
    $$formal.$$targs$$={Value:{t:Formal},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Formal},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}},$$formal);
    add_type_arg($$formal,'Value',{t:Formal});
    return $$formal;
}
Formal.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Formal},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Formal']};
exports.Formal=Formal;
function $init$Formal(){
    if (Formal.$$===undefined){
        initTypeProto(Formal,'ceylon.language::Formal',Basic,$init$OptionalAnnotation$model());
    }
    return Formal;
}
exports.$init$Formal=$init$Formal;
$init$Formal();
var formal=function (){
    return Formal();
};
formal.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Formal},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['formal']};
exports.formal=formal;
function Default($$default){
    $init$Default();
    if ($$default===undefined)$$default=new Default.$$;
    $$default.$$targs$$={Value:{t:Default},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Default},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}},$$default);
    add_type_arg($$default,'Value',{t:Default});
    return $$default;
}
Default.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Default},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Default']};
exports.Default=Default;
function $init$Default(){
    if (Default.$$===undefined){
        initTypeProto(Default,'ceylon.language::Default',Basic,$init$OptionalAnnotation$model());
    }
    return Default;
}
exports.$init$Default=$init$Default;
$init$Default();
var $default=function (){
    return Default();
};
$default.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Default},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['default']};
exports.$default=$default;
function Late($$late){
    $init$Late();
    if ($$late===undefined)$$late=new Late.$$;
    $$late.$$targs$$={Value:{t:Late},ProgramElement:{t:ValueDeclaration$model$declaration}};
    OptionalAnnotation$model({Value:{t:Late},ProgramElement:{t:ValueDeclaration$model$declaration}},$$late);
    add_type_arg($$late,'Value',{t:Late});
    add_type_arg($$late,'ProgramElement',{t:ValueDeclaration$model$declaration});
    return $$late;
}
Late.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Late},ProgramElement:{t:ValueDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Late']};
exports.Late=Late;
function $init$Late(){
    if (Late.$$===undefined){
        initTypeProto(Late,'ceylon.language::Late',Basic,$init$OptionalAnnotation$model());
    }
    return Late;
}
exports.$init$Late=$init$Late;
$init$Late();
var late=function (){
    return Late();
};
late.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Late},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['late']};
exports.late=late;
function Native($$native){
    $init$Native();
    if ($$native===undefined)$$native=new Native.$$;
    $$native.$$targs$$={Value:{t:Native},ProgramElement:{t:Annotated$model}};
    OptionalAnnotation$model({Value:{t:Native},ProgramElement:{t:Annotated$model}},$$native);
    add_type_arg($$native,'Value',{t:Native});
    add_type_arg($$native,'ProgramElement',{t:Annotated$model});
    return $$native;
}
Native.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Native},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Native']};
exports.Native=Native;
function $init$Native(){
    if (Native.$$===undefined){
        initTypeProto(Native,'ceylon.language::Native',Basic,$init$OptionalAnnotation$model());
    }
    return Native;
}
exports.$init$Native=$init$Native;
$init$Native();
var $native=function (){
    return Native();
};
$native.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Native},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['native']};
exports.$native=$native;
function Doc(description, $$doc){
    $init$Doc();
    if ($$doc===undefined)$$doc=new Doc.$$;
    $$doc.$$targs$$={Value:{t:Doc},ProgramElement:{t:Annotated$model}};
    OptionalAnnotation$model({Value:{t:Doc},ProgramElement:{t:Annotated$model}},$$doc);
    add_type_arg($$doc,'Value',{t:Doc});
    add_type_arg($$doc,'ProgramElement',{t:Annotated$model});
    return $$doc;
}
Doc.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Doc},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Doc']};
exports.Doc=Doc;
function $init$Doc(){
    if (Doc.$$===undefined){
        initTypeProto(Doc,'ceylon.language::Doc',Basic,$init$OptionalAnnotation$model());
    }
    return Doc;
}
exports.$init$Doc=$init$Doc;
$init$Doc();
var doc=function (description$870){
    return Doc(description$870);
};
doc.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Doc},$ps:[{$nm:'description',$mt:'prm',$t:{t:String$}}],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['doc']};
exports.doc=doc;
function See(programElements, $$see){
    $init$See();
    if ($$see===undefined)$$see=new See.$$;
    $$see.$$targs$$={Value:{t:See},ProgramElement:{t:Annotated$model}};
    if(programElements===undefined){programElements=getEmpty();}
    SequencedAnnotation$model({Value:{t:See},ProgramElement:{t:Annotated$model}},$$see);
    add_type_arg($$see,'Value',{t:See});
    add_type_arg($$see,'ProgramElement',{t:Annotated$model});
    return $$see;
}
See.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:SequencedAnnotation$model,a:{Value:{t:See},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['See']};
exports.See=See;
function $init$See(){
    if (See.$$===undefined){
        initTypeProto(See,'ceylon.language::See',Basic,$init$SequencedAnnotation$model());
    }
    return See;
}
exports.$init$See=$init$See;
$init$See();
var see=function (programElements$871){
    if(programElements$871===undefined){programElements$871=getEmpty();}
    return See(programElements$871);
};
see.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:See},$ps:[{$nm:'programElements',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Declaration$model$declaration}}}}],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['see']};
exports.see=see;
function Authors(authors, $$authors){
    $init$Authors();
    if ($$authors===undefined)$$authors=new Authors.$$;
    $$authors.$$targs$$={Value:{t:Authors},ProgramElement:{t:Annotated$model}};
    if(authors===undefined){authors=getEmpty();}
    OptionalAnnotation$model({Value:{t:Authors},ProgramElement:{t:Annotated$model}},$$authors);
    add_type_arg($$authors,'Value',{t:Authors});
    add_type_arg($$authors,'ProgramElement',{t:Annotated$model});
    return $$authors;
}
Authors.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Authors},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Authors']};
exports.Authors=Authors;
function $init$Authors(){
    if (Authors.$$===undefined){
        initTypeProto(Authors,'ceylon.language::Authors',Basic,$init$OptionalAnnotation$model());
    }
    return Authors;
}
exports.$init$Authors=$init$Authors;
$init$Authors();
var by=function (authors$872){
    if(authors$872===undefined){authors$872=getEmpty();}
    return Authors(authors$872);
};
by.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Authors},$ps:[{$nm:'authors',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:String$}}}}],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['by']};
exports.by=by;
function ThrownException(type, when, $$thrownException){
    $init$ThrownException();
    if ($$thrownException===undefined)$$thrownException=new ThrownException.$$;
    $$thrownException.$$targs$$={Value:{t:ThrownException},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}};
    SequencedAnnotation$model({Value:{t:ThrownException},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}},$$thrownException);
    add_type_arg($$thrownException,'Value',{t:ThrownException});
    return $$thrownException;
}
ThrownException.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:SequencedAnnotation$model,a:{Value:{t:ThrownException},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['ThrownException']};
exports.ThrownException=ThrownException;
function $init$ThrownException(){
    if (ThrownException.$$===undefined){
        initTypeProto(ThrownException,'ceylon.language::ThrownException',Basic,$init$SequencedAnnotation$model());
    }
    return ThrownException;
}
exports.$init$ThrownException=$init$ThrownException;
$init$ThrownException();
var $throws=function (type$873,when$874){
    if(when$874===undefined){when$874=String$("",0);}
    return ThrownException(type$873,when$874);
};
$throws.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:ThrownException},$ps:[{$nm:'type',$mt:'prm',$t:{t:Declaration$model$declaration}},{$nm:'when',$mt:'prm',$def:1,$t:{t:String$}}],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['throws']};
exports.$throws=$throws;
function Deprecation(description, $$deprecation){
    $init$Deprecation();
    if ($$deprecation===undefined)$$deprecation=new Deprecation.$$;
    $$deprecation.$$targs$$={Value:{t:Deprecation},ProgramElement:{t:Annotated$model}};
    $$deprecation.description=description;
    OptionalAnnotation$model({Value:{t:Deprecation},ProgramElement:{t:Annotated$model}},$$deprecation);
    add_type_arg($$deprecation,'Value',{t:Deprecation});
    add_type_arg($$deprecation,'ProgramElement',{t:Annotated$model});
    return $$deprecation;
}
Deprecation.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Deprecation},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Deprecation']};
exports.Deprecation=Deprecation;
function $init$Deprecation(){
    if (Deprecation.$$===undefined){
        initTypeProto(Deprecation,'ceylon.language::Deprecation',Basic,$init$OptionalAnnotation$model());
        (function($$deprecation){
            defineAttr($$deprecation,'reason',function(){
                var $$deprecation=this;
                if($$deprecation.description.empty){
                    return null;
                }
                return $$deprecation.description;
            },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:String$}]},$cont:Deprecation,$an:function(){return[shared()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Deprecation']['$at']['reason']});
        })(Deprecation.$$.prototype);
    }
    return Deprecation;
}
exports.$init$Deprecation=$init$Deprecation;
$init$Deprecation();
var deprecated=function (reason$875){
    if(reason$875===undefined){reason$875=String$("",0);}
    return Deprecation(reason$875);
};
deprecated.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Deprecation},$ps:[{$nm:'reason',$mt:'prm',$def:1,$t:{t:String$}}],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['deprecated']};
exports.deprecated=deprecated;
function Tags(tags, $$tags){
    $init$Tags();
    if ($$tags===undefined)$$tags=new Tags.$$;
    $$tags.$$targs$$={Value:{t:Tags},ProgramElement:{t:Annotated$model}};
    if(tags===undefined){tags=getEmpty();}
    OptionalAnnotation$model({Value:{t:Tags},ProgramElement:{t:Annotated$model}},$$tags);
    add_type_arg($$tags,'Value',{t:Tags});
    add_type_arg($$tags,'ProgramElement',{t:Annotated$model});
    return $$tags;
}
Tags.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Tags},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Tags']};
exports.Tags=Tags;
function $init$Tags(){
    if (Tags.$$===undefined){
        initTypeProto(Tags,'ceylon.language::Tags',Basic,$init$OptionalAnnotation$model());
    }
    return Tags;
}
exports.$init$Tags=$init$Tags;
$init$Tags();
var tagged=function (tags$876){
    if(tags$876===undefined){tags$876=getEmpty();}
    return Tags(tags$876);
};
tagged.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Tags},$ps:[{$nm:'tags',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:String$}}}}],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['tagged']};
exports.tagged=tagged;
function License(url, $$license){
    $init$License();
    if ($$license===undefined)$$license=new License.$$;
    $$license.$$targs$$={Value:{t:License},ProgramElement:{t:Module$model$declaration}};
    OptionalAnnotation$model({Value:{t:License},ProgramElement:{t:Module$model$declaration}},$$license);
    add_type_arg($$license,'Value',{t:License});
    add_type_arg($$license,'ProgramElement',{t:Module$model$declaration});
    return $$license;
}
License.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:License},ProgramElement:{t:Module$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['License']};
exports.License=License;
function $init$License(){
    if (License.$$===undefined){
        initTypeProto(License,'ceylon.language::License',Basic,$init$OptionalAnnotation$model());
    }
    return License;
}
exports.$init$License=$init$License;
$init$License();
var license=function (url$877){
    return License(url$877);
};
license.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:License},$ps:[{$nm:'url',$mt:'prm',$t:{t:String$}}],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['license']};
exports.license=license;
function Optional($$optional){
    $init$Optional();
    if ($$optional===undefined)$$optional=new Optional.$$;
    $$optional.$$targs$$={Value:{t:Optional},ProgramElement:{t:Import$model$declaration}};
    OptionalAnnotation$model({Value:{t:Optional},ProgramElement:{t:Import$model$declaration}},$$optional);
    add_type_arg($$optional,'Value',{t:Optional});
    add_type_arg($$optional,'ProgramElement',{t:Import$model$declaration});
    return $$optional;
}
Optional.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Optional},ProgramElement:{t:Import$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['Optional']};
exports.Optional=Optional;
function $init$Optional(){
    if (Optional.$$===undefined){
        initTypeProto(Optional,'ceylon.language::Optional',Basic,$init$OptionalAnnotation$model());
    }
    return Optional;
}
exports.$init$Optional=$init$Optional;
$init$Optional();
var optional=function (){
    return Optional();
};
optional.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Optional},$ps:[],$an:function(){return[shared(),annotation()];},pkg:'ceylon.language',d:$$METAMODEL$$['ceylon.language']['optional']};
exports.optional=optional;
function type$model(x) {
    if (x === null) {
        return getNothingType$model();
    } else {
        //Search for metamodel
        var mm = x.$$metamodel$$;
        if (mm === undefined && x.constructor && x.constructor.T$name && x.constructor.T$all) {
            //It's probably an instance of a Ceylon type
            var _x = x.constructor.T$all[x.constructor.T$name];
            if (_x) {
                mm = _x.$$metamodel$$;
                x=_x;
            }
        }
        if (mm && mm.d['$mt']) {
            var metatype = mm.d['$mt'];
            if (metatype === 'ifc') { //Interface
                //
            } else if (metatype === 'cls') { //Class
                return typeLiteral$model({Type:{t:x}});
            } else if (metatype === 'mthd') { //Method
                return typeLiteral$model({Type:{t:$JsCallable(x)}});
            } else {
                console.log("type(" + metatype + ")WTF?");
            }
        } else {
            throw Exception(String$("No metamodel available for "+x));
        }
    }
    return "UNIMPLEMENTED";
}
type$model.$$metamodel$$={$ps:[{t:Anything}],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language.model']['type']};
exports.type$model=type$model;

function typeLiteral$model($$targs$$) {
    if ($$targs$$ === undefined || $$targs$$.Type === undefined || $$targs$$.Type.t === undefined) {
        throw Exception("JS Interop not supported " + require('util').inspect($$targs$$));
    } else if ($$targs$$.Type.t === 'u' || $$targs$$.Type.t === 'i') {
        return $$targs$$.Type.t === 'u' ? applyUnionType($$targs$$.Type) :
            applyIntersectionType($$targs$$.Type);
    } else if ($$targs$$.Type.t.$$metamodel$$ === undefined) {
        throw Exception("JS Interop not supported / incomplete metamodel for " + require('util').inspect($$targs$$.Type.t));
    } else {
        var mdl = $$targs$$.Type.t.$$metamodel$$;
        if (mdl.d['$mt'] === 'cls') {
            return AppliedClass$model($$targs$$.Type.t,$$targs$$.Type.t['$$metamodel$$']['$tp']);
        } else if (mdl.d['$mt'] === 'ifc') {
            return AppliedInterface$model($$targs$$.Type.t,$$targs$$.Type.t['$$metamodel$$']['$tp']);
        } else if (mdl.d['$mt'] === 'mthd') {
            return AppliedFunction$model($$targs$$.Type.t,{Type:mdl.$t,Arguments:mdl.$ps});
        } else if (mdl.d['$mt'] === 'attr' || mdl.d['$mt'] === 'gttr') {
            return AppliedValue$model($$targs$$.Type.t,{Container:{t:mdl.$cont},Type:mdl.$t});
        } else {
console.log("WTF is a metatype " + mdl.d['$mt'] + "???????");
        }
        console.log("typeLiteral<" + $$targs$$.Type.t.getT$name() + ">");
    }
    throw Exception("typeLiteral UNIMPLEMENTED for " + require('util').inspect($$targs$$));
}
typeLiteral$model.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language.model']['typeLiteral']};
exports.typeLiteral$model=typeLiteral$model;

function pushTypes(list, types) {
  for (var i=0; i<types.length; i++) {
    var t = types[i];
    if (t.t === 'u') {
      list.push(applyUnionType(t, t.l));
    } else if (t.t === 'i') {
      list.push(applyIntersectionType(t, t.l));
    } else {
      list.push(typeLiteral$model({Type:t}));
    }
  }
  return list;
}

function applyUnionType(ut) { //return AppliedUnionType
  var cases = [];
  pushTypes(cases, ut.l);
  return AppliedUnionType$model(ut, cases.reifyCeylonType({Absent:{t:Null},Element:{t:Type$model}}));
}
function applyIntersectionType(it) { //return AppliedIntersectionType
  var sats = [];
  pushTypes(sats, it.l);
  return AppliedIntersectionType$model(it, sats.reifyCeylonType({Absent:{t:Null},Element:{t:Type$model}}));
}
function applyType(t) { //return AppliedType
  return typeLiteral$model({Type:t});
}
function AppliedClass$model(tipo,$$targs$$,that){
    $init$AppliedClass$model();
    if (that===undefined)that=new AppliedClass$model.$$;
    set_type_args(that,$$targs$$);
    Class$model($$targs$$,that);
    that.tipo=tipo;
    return that;
}
AppliedClass$model.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Type:{'var':'out',},A:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:Class$model,a:{Arguments:'A',Type:'Type'}}],pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Class']};
function $init$AppliedClass$model(){
    if (AppliedClass$model.$$===undefined){
        initTypeProto(AppliedClass$model,'ceylon.language.model:: AppliedClass',Basic,Class$model);
        (function($$clase){
            
            //declaration
            defineAttr($$clase,'declaration',function(){
                var $$clase=this;
                throw Exception(String$("decl",4));
            });
            //superclass
            defineAttr($$clase,'superclass',function(){
                var $$clase=this;
                throw Exception(String$("super",5));
            });
            //interfaces
            defineAttr($$clase,'interfaces',function(){
                var $$clase=this;
                throw Exception(String$("ifaces",6));
            });

            $$clase.getFunction=function getFunction(name$2,types$3,$$$mptypes){
                var $$clase=this;
                if(types$3===undefined){types$3=getEmpty();}
                throw Exception(String$("func",4));
            };//$$clase.getFunction.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$metamodel}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Function$metamodel,a:{Arguments:{t:Nothing},Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.metamodel',d:$$METAMODEL$$['ceylon.language.metamodel']['ClassOrInterface']['$m']['getFunction']};

            $$clase.getClassOrInterface=function getClassOrInterface(name$4,types$5,$$$mptypes){
                var $$clase=this;
                if(types$5===undefined){types$5=getEmpty();}
                throw Exception(String$("class/iface",11));
            };//$$clase.getClassOrInterface.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$metamodel}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:ClassOrInterface$metamodel,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.metamodel',d:$$METAMODEL$$['ceylon.language.metamodel']['ClassOrInterface']['$m']['getClassOrInterface']};

            $$clase.getAttribute=function getAttribute(name$6,$$$mptypes){
                var $$clase=this;
                throw Exception(String$("attrib",6));
            };//$$clase.getAttribute.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Attribute$metamodel,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.metamodel',d:$$METAMODEL$$['ceylon.language.metamodel']['ClassOrInterface']['$m']['getAttribute']};

            defineAttr($$clase,'typeArguments',function(){
                var $$clase=this;
                throw Exception(String$("type args",9));
            });
        })(AppliedClass$model.$$.prototype);
    }
    return AppliedClass$model;
}
exports.AppliedClass$model=$init$AppliedClass$model;
$init$AppliedClass$model();

function AppliedInterface$model(tipo,$$targs$$,$$interfaz){
    $init$AppliedInterface$model();
    if ($$interfaz===undefined)$$interfaz=new AppliedInterface$model.$$;
    set_type_args($$interfaz,$$targs$$);
    Interface$model($$targs$$,$$interfaz);
    
    //AttributeGetterDefinition declaration at test.ceylon (5:4-5:78)
    defineAttr($$interfaz,'declaration',function() {
        throw Exception(String$("decl",4));
    });
    
    //AttributeGetterDefinition superclass at test.ceylon (6:4-6:83)
    defineAttr($$interfaz,'superclass',function() {
        throw Exception(String$("super",5));
    });
    
    //AttributeGetterDefinition interfaces at test.ceylon (7:4-7:80)
    defineAttr($$interfaz,'interfaces',function() {
        throw Exception(String$("ifaces",6));
    });
    
    //MethodDefinition getFunction at test.ceylon (8:4-9:83)
    function getFunction(name$2,types$3,$$$mptypes){
        if(types$3===undefined){types$3=getEmpty();}
        throw Exception(String$("func",4));
    }
    $$interfaz.getFunction=getFunction;
    getFunction.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$model,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Function$model,a:{Arguments:{t:Nothing},Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['ClassOrInterface']['$m']['getFunction']};//getFunction.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Sequential,a:{Element:{t:Type$metamodel}}},Element:{t:Sequential,a:{Element:{t:Type$metamodel}}}}},Return:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:$$$mptypes.SubType,Kind:$$$mptypes.Kind}}]}};
    
    //MethodDefinition getClassOrInterface at test.ceylon (10:4-11:89)
    function getClassOrInterface(name$4,types$5,$$$mptypes){
        if(types$5===undefined){types$5=getEmpty();}
        throw Exception(String$("class/iface",11));
    }
    $$interfaz.getClassOrInterface=getClassOrInterface;
    getClassOrInterface.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$model,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:ClassOrInterface$model,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['ClassOrInterface']['$m']['getClassOrInterface']};//getClassOrInterface.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Sequential,a:{Element:{t:Type$metamodel}}},Element:{t:Sequential,a:{Element:{t:Type$metamodel}}}}},Return:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:$$$mptypes.SubType,Kind:$$$mptypes.Kind}}]}};
    
    //MethodDefinition getAttribute at test.ceylon (12:4-13:77)
    function getAttribute(name$6,$$$mptypes){
        throw Exception(String$("attrib",6));
    }
    $$interfaz.getAttribute=getAttribute;
    getAttribute.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$model,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Attribute$model,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['ClassOrInterface']['$m']['getAttribute']};//getAttribute.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:String$},Element:{t:String$}}},Return:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:$$$mptypes.SubType,Kind:$$$mptypes.Kind}}]}};
    
    //AttributeGetterDefinition typeArguments at test.ceylon (14:4-14:89)
    defineAttr($$interfaz,'typeArguments',function() {
        throw Exception(String$("type args",9));
    });
    $$interfaz.tipo=tipo;
    return $$interfaz;
}
AppliedInterface$model.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Type:{'var':'out',}},satisfies:[{t:Interface$model,a:{Type:'Type'}}],pkg:'ceylon.language.model',d:$$METAMODEL$$['ceylon.language.model']['Interface']};
function $init$AppliedInterface$model(){
    if (AppliedInterface$model.$$===undefined){
        initTypeProto(AppliedInterface$model,'ceylon.language.model::AppliedInterface',Basic,Interface$model);
    }
    return AppliedInterface$model;
}
exports.$init$AppliedInterface$model=$init$AppliedInterface$model;
$init$AppliedInterface$model();

function AppliedUnionType$model(tipo,types$2, $$appliedUnionType){
    $init$AppliedUnionType$model();
    if ($$appliedUnionType===undefined)$$appliedUnionType=new AppliedUnionType$model.$$;
    $$appliedUnionType.types$2=types$2;
    UnionType$model($$appliedUnionType);
    $$appliedUnionType.tipo=tipo;
    return $$appliedUnionType;
}
AppliedUnionType$model.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:UnionType$model}],pkg:'',d:$$METAMODEL$$['ceylon.language.model']['AppliedUnionType']};
function $init$AppliedUnionType$model(){
    if (AppliedUnionType$model.$$===undefined){
        initTypeProto(AppliedUnionType$model,'AppliedUnionType',Basic,UnionType$model);
        (function($$appliedUnionType){
            
            defineAttr($$appliedUnionType,'caseTypes',function(){
                var $$appliedUnionType=this;
                return $$appliedUnionType.types$2;
            });
        })(AppliedUnionType$model.$$.prototype);
    }
    return AppliedUnionType$model;
}
exports.$init$AppliedUnionType$model=$init$AppliedUnionType$model;
$init$AppliedUnionType$model();

function AppliedIntersectionType$model(tipo,types$3, $$appliedIntersectionType){
    $init$AppliedIntersectionType$model();
    if ($$appliedIntersectionType===undefined)$$appliedIntersectionType=new AppliedIntersectionType$model.$$;
    $$appliedIntersectionType.types$3=types$3;
    IntersectionType$model($$appliedIntersectionType);
    $$appliedIntersectionType.tipo=tipo;
    return $$appliedIntersectionType;
}
AppliedIntersectionType$model.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:IntersectionType$model}],pkg:'',d:$$METAMODEL$$['ceylon.language.model']['AppliedIntersectionType']};
function $init$AppliedIntersectionType$model(){
    if (AppliedIntersectionType$model.$$===undefined){
        initTypeProto(AppliedIntersectionType$model,'AppliedIntersectionType',Basic,IntersectionType$model);
        (function($$appliedIntersectionType){
            
            defineAttr($$appliedIntersectionType,'satisfiedTypes',function(){
                var $$appliedIntersectionType=this;
                return $$appliedIntersectionType.types$3;
            });
        })(AppliedIntersectionType$model.$$.prototype);
    }
    return AppliedIntersectionType$model;
}
exports.$init$AppliedIntersectionType$model=$init$AppliedIntersectionType$model;
$init$AppliedIntersectionType$model();

function AppliedFunction$model(f) {
  return f;
}


function AppliedValue$model(attr,$$targs$$,$$appliedAttribute){
    $init$AppliedValue$model();
    if ($$appliedAttribute===undefined)$$appliedAttribute=new AppliedValue$model.$$;
    set_type_args($$appliedAttribute,$$targs$$);
    Attribute$model($$appliedAttribute.$$targs$$===undefined?$$targs$$:{Type:$$appliedAttribute.$$targs$$.Type,Container:$$appliedAttribute.$$targs$$.Container},$$appliedAttribute);
    $$appliedAttribute.attr=attr;
    return $$appliedAttribute;
}
AppliedValue$model.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Container:{'var':'in',},Type:{'var':'out',}},satisfies:[{t:Attribute$model,a:{Type:'Type',Container:'Container'}}],$an:function(){return[shared()];},pkg:'',d:$$METAMODEL$$['ceylon.language.model']['Attribute']};
exports.AppliedValue$model=AppliedValue$model;
function $init$AppliedValue$model(){
    if (AppliedValue$model.$$===undefined){
        initTypeProto(AppliedValue$model,'AppliedValue',Basic,Attribute$model);
        (function($$appliedAttribute){
            
            //AttributeGetterDefinition declaration at caca.ceylon (5:2-5:58)
            defineAttr($$appliedAttribute,'declaration',function(){
                var $$appliedAttribute=this;
                throw Exception();
            },undefined,{mod:$$METAMODEL$$,$an:function(){return[shared(),actual()];},pkg:'',d:$$METAMODEL$$['ceylon.language.model']['AttributeModel']['$at']['declaration']});
            //AttributeGetterDefinition declaringClassOrInterface at caca.ceylon (6:2-6:76)
            defineAttr($$appliedAttribute,'declaringClassOrInterface',function(){
                var $$appliedAttribute=this;
                throw Exception();
            },undefined,{mod:$$METAMODEL$$,$an:function(){return[shared(),actual()];},pkg:'',d:$$METAMODEL$$['ceylon.language.model']['AttributeModel']['$at']['declaringClassOrInterface']});
            //AttributeGetterDefinition type at caca.ceylon (7:2-7:31)
            defineAttr($$appliedAttribute,'type',function(){
                var $$appliedAttribute=this;
                throw Exception();
            },undefined,{mod:$$METAMODEL$$,$an:function(){return[shared(),actual()];},pkg:'',d:$$METAMODEL$$['ceylon.language.model']['AttributeModel']['$at']['type']});
        })(AppliedValue$model.$$.prototype);
    }
    return AppliedValue$model;
}
exports.$init$AppliedValue$model=$init$AppliedValue$model;
$init$AppliedValue$model();
function Number$(wat) {
    return wat;
}
initType(Number$, 'ceylon.language::Number');
Number$.$$metamodel$$={$an:function(){return[shared()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']};
exports.Number=Number$;
function $init$Number$() {
    if (Number$.$$===undefined) {
        initType(Number$, 'ceylon.language::Number');
    }
    return Number$;
}

var toInt = function(float) {
    return (float >= 0) ? Math.floor(float) : Math.ceil(float);
}

function JSNumber(value) { return Number(value); }
initExistingType(JSNumber, Number, 'ceylon.language::JSNumber');
JSNumber.$$metamodel$$={$nm:'JSNumber',$mt:'cls',$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']};

var origNumToString = Number.prototype.toString;
inheritProto(JSNumber, Object$, Scalar, $init$Integral(), Exponentiable);

function Integer(value) {
    if (value && value.getT$name && value.getT$name() === 'ceylon.language::Integer') {
        return value;
    }
    return Number(value);
}
initTypeProto(Integer, 'ceylon.language::Integer', Object$, Scalar, 
        $init$Integral(), Exponentiable, Binary);
Integer.$$metamodel$$={$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Integer']};

function Float(value) {
    if (value && value.getT$name && value.getT$name() === 'ceylon.language::Float') {
        return value;
    }
    var that = new Number(value);
    that.float$ = true;
    return that;
}
initTypeProto(Float, 'ceylon.language::Float', Object$, Scalar, Exponentiable);
Float.$$metamodel$$={$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Float']};

var JSNum$proto = Number.prototype;
JSNum$proto.getT$all = function() {
    return (this.float$ ? Float : Integer).$$.T$all;
}
JSNum$proto.getT$name = function() {
    return (this.float$ ? Float : Integer).$$.T$name;
}
JSNum$proto.toString = origNumToString;
defineAttr(JSNum$proto, 'string', function(){ return String$(this.toString()); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['string']});
JSNum$proto.plus = function(other) {
    return (this.float$||other.float$) ? Float(this+other) : (this+other);
}
JSNum$proto.minus = function(other) {
    return (this.float$||other.float$) ? Float(this-other) : (this-other);
}
JSNum$proto.times = function(other) {
    return (this.float$||other.float$) ? Float(this*other) : (this*other);
}
JSNum$proto.divided = function(other) {
    if (this.float$||other.float$) { return Float(this/other); }
    if (other == 0) {
        throw Exception(String$("Division by Zero"));
    }
    return toInt(this/other);
}
JSNum$proto.remainder = function(other) { return this%other; }
JSNum$proto.power = function(exp) {
    if (this.float$||exp.float$) { return Float(Math.pow(this, exp)); }
    if (exp<0 && this!=1 && this!=-1) {
        throw Exception(String$("Negative exponent"));
    }
    return toInt(Math.pow(this, exp));
}
defineAttr(JSNum$proto, 'negativeValue', function() {
    return this.float$ ? Float(-this) : -this;
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Invertable']['$at']['negativeValue']});
defineAttr(JSNum$proto, 'positiveValue', function() {
    return this.float$ ? this : this.valueOf();
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Invertable']['$at']['positiveValue']});
JSNum$proto.equals = function(other) { return (typeof(other)==='number' || other.constructor===Number) && other==this.valueOf(); }
JSNum$proto.compare = function(other) {
    var value = this.valueOf();
    return value==other ? equal : (value<other ? smaller:larger);
}
defineAttr(JSNum$proto, '$float', function(){ return Float(this.valueOf()); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']['$at']['float']});
defineAttr(JSNum$proto, 'integer', function(){ return toInt(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']['$at']['integer']});
defineAttr(JSNum$proto, 'integerValue', function(){ return toInt(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Ordinal']['$at']['integerValue']});
defineAttr(JSNum$proto, 'character', function(){ return Character(this.valueOf()); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Integer']['$at']['character']});
defineAttr(JSNum$proto, 'successor', function(){ return this+1; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Ordinal']['$at']['successor']});
defineAttr(JSNum$proto, 'predecessor', function(){ return this-1; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Ordinal']['$at']['predecessor']});
defineAttr(JSNum$proto, 'unit', function(){ return this == 1; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Integral']['$at']['unit']});
defineAttr(JSNum$proto, 'zero', function(){ return this == 0; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Integral']['$at']['zero']});
defineAttr(JSNum$proto, 'fractionalPart', function() {
    if (!this.float$) { return 0; }
    return Float(this - (this>=0 ? Math.floor(this) : Math.ceil(this)));
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']['$at']['fractionalPart']});
defineAttr(JSNum$proto, 'wholePart', function() {
    if (!this.float$) { return this.valueOf(); }
    return Float(this>=0 ? Math.floor(this) : Math.ceil(this));
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']['$at']['wholePart']});
defineAttr(JSNum$proto, 'sign', function(){ return this > 0 ? 1 : this < 0 ? -1 : 0; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']['$at']['sign']});
defineAttr(JSNum$proto, 'hash', function() {
    return this.float$ ? String$(this.toPrecision()).hash : this.valueOf();
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['hash']});
JSNum$proto.distanceFrom = function(other) {
    return (this.float$ ? this.wholePart : this) - other;
}
//Binary interface
defineAttr(JSNum$proto, 'not', function(){ return ~this; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Binary']['$at']['not']});
JSNum$proto.leftLogicalShift = function(i) { return this << i; }
JSNum$proto.rightLogicalShift = function(i) { return this >> i; }
JSNum$proto.rightArithmeticShift = function(i) { return this >>> i; }
JSNum$proto.and = function(x) { return this & x; }
JSNum$proto.or = function(x) { return this | x; }
JSNum$proto.xor = function(x) { return this ^ x; }
JSNum$proto.get = function(idx) {
    var mask = 1 << idx;
    return (this & mask) != 0 ? $true : $false;
}
JSNum$proto.set = function(idx,bit) {
    if (bit === undefined) { bit = $true; }
        var mask = idx > 1 ? 1 << idx : 1;
    return (bit === $true) ? this | mask : this & ~mask;
}
JSNum$proto.flip = function(idx) {
    var mask = 1 << idx;
    return this ^ mask;
}
JSNum$proto.clear = function(index) {
    return this.set(index, false);
}
defineAttr(JSNum$proto, 'size', function(){ return 53; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Binary']['$at']['size']});
defineAttr(JSNum$proto, 'magnitude', function(){ return Math.abs(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Number']['$at']['magnitude']});

//-(2^53-1)
var $minIntegerValue = Integer(-9007199254740991);
function getMinIntegerValue() { return $minIntegerValue; }
//(2^53-3) => ((2^53)-2 is NaN)
var $maxIntegerValue = Integer(9007199254740989);
function getMaxIntegerValue() { return $maxIntegerValue; }

function $parseFloat(s) { return Float(parseFloat(s)); }

defineAttr(JSNum$proto, 'undefined', function(){ return isNaN(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Float']['$at']['undefined']});
defineAttr(JSNum$proto, 'finite', function(){ return this!=Infinity && this!=-Infinity && !isNaN(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Float']['$at']['finite']});
defineAttr(JSNum$proto, 'infinite', function(){ return this==Infinity || this==-Infinity; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Float']['$at']['infinite']});
defineAttr(JSNum$proto, 'strictlyPositive', function(){ return this>0 || (this==0 && (1/this==Infinity)); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Float']['$at']['strictlyPositive']});
defineAttr(JSNum$proto, 'strictlyNegative', function() { return this<0 || (this==0 && (1/this==-Infinity)); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Float']['$at']['strictlyNegative']});

var $infinity = Float(Infinity);
function getInfinity() { return $infinity; }
getInfinity.$$metamodel$$={mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['infinity']};
//TODO metamodel
//function getNegativeInfinity() { return Float(-Infinity); }

exports.Integer=Integer;
exports.Float=Float;
exports.getInfinity=getInfinity;
exports.getMinIntegerValue=getMinIntegerValue;
exports.getMaxIntegerValue=getMaxIntegerValue;
exports.parseFloat=$parseFloat;
function getNull() { return null }
function Boolean$(value) {return Boolean(value)}
initExistingTypeProto(Boolean$, Boolean, 'ceylon.language::Boolean');
Boolean$.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Boolean']};
function trueClass() {}
initType(trueClass, "ceylon.language::true", Boolean$);
function falseClass() {}
initType(falseClass, "ceylon.language::false", Boolean$);
Boolean.prototype.getT$name = function() {
    return (this.valueOf()?trueClass:falseClass).$$.T$name;
}
Boolean.prototype.getT$all = function() {
    return (this.valueOf()?trueClass:falseClass).$$.T$all;
}
Boolean.prototype.equals = function(other) {return other.constructor===Boolean && other==this;}
defineAttr(Boolean.prototype, 'hash', function(){ return this.valueOf()?1:0; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['hash']});
var trueString = String$("true", 4);
var falseString = String$("false", 5);
defineAttr(Boolean.prototype, 'string', function(){ return this.valueOf()?trueString:falseString; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['string']});
function getTrue() {return true}
function getFalse() {return false}
var $true = true;
var $false = false;

function Comparison(name) {
    var that = new Comparison.$$;
    that.name = String$(name);
    return that;
}
initTypeProto(Comparison, 'ceylon.language::Comparison', $init$Basic());
Comparison.$$metamodel$$={$ps:[{t:String$}],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Comparison']};
var Comparison$proto = Comparison.$$.prototype;
defineAttr(Comparison$proto, 'string', function(){ return this.name; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['string']});
var larger = Comparison("larger");
function getLarger() { return larger }
var smaller = Comparison("smaller");
function getSmaller() { return smaller }
var equal = Comparison("equal");
function getEqual() { return equal }

exports.getLarger=getLarger;
exports.getSmaller=getSmaller;
exports.getEqual=getEqual;

//These are operators for handling nulls
function exists(value) {
    return value !== null && value !== undefined;
}
function nonempty(value) {
    return value !== null && value !== undefined && !value.empty;
}

function isOfType(obj, type) {
    if (type && type.t) {
        if (type.t == 'i' || type.t == 'u') {
            return isOfTypes(obj, type);
        }
        if (obj === null || obj === undefined) {
            return type.t===Null || type.t===Anything;
        }
        if (obj.getT$all === undefined) {
            if (obj.$$metamodel$$) {
                //We can navigate the metamodel
                if (obj.$$metamodel$$.d['$mt'] === 'mthd') {
                    if (type.t === Callable) { //It's a callable reference
                        if (type.a && type.a.Return && obj.$$metamodel$$['$t']) {
                            //Check if return type matches
                            if (extendsType(obj.$$metamodel$$['$t'], type.a.Return)) {
                                if (type.a.Arguments && obj.$$metamodel$$['$ps'] !== undefined) {
                                    var metaparams = obj.$$metamodel$$['$ps'];
                                    if (metaparams.length == 0) {
                                        return type.a.Arguments.t === Empty;
                                    } else {
                                        //check if arguments match
                                        var comptype = type.a.Arguments;
                                        for (var i=0; i < metaparams.length; i++) {
                                            if (comptype.t !== Tuple || !extendsType(metaparams[i]['$t'], comptype.a.First)) {
                                                return false;
                                            }
                                            comptype = comptype.a.Rest;
                                        }
                                    }
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        if (type.t.$$.T$name in obj.getT$all()) {
            if (type.a && obj.$$targs$$) {
                for (var i in type.a) {
                    var cmptype = type.a[i];
                    var tmpobj = obj;
                    var iance = null;
                    if (type.t.$$metamodel$$ && type.t.$$metamodel$$.$tp && type.t.$$metamodel$$.$tp[i]) iance=type.t.$$metamodel$$.$tp[i]['var'];
                    if (iance === null) {
                        //Type parameter may be in the outer type
                        while (iance===null && tmpobj.$$outer !== undefined) {
                            tmpobj=tmpobj.$$outer;
                            var _tmpf = tmpobj.constructor.T$all[tmpobj.constructor.T$name];
                            if (_tmpf.$$metamodel$$ && _tmpf.$$metamodel$$.$tp && _tmpf.$$metamodel$$.$tp[i]) {
                                iance=_tmpf.$$metamodel$$.$tp[i]['var'];
                            }
                        }
                    }
                    if (iance === 'out') {
                        if (!extendsType(tmpobj.$$targs$$[i], cmptype)) {
                            return false;
                        }
                    } else if (iance === 'in') {
                        if (!extendsType(cmptype, tmpobj.$$targs$$[i])) {
                            return false;
                        }
                    } else if (iance === undefined) {
                        if (!(tmpobj.$$targs$$[i] && tmpobj.$$targs$$[i].t.$$ && tmpobj.$$targs$$[i].t.$$.T$name && cmptype && cmptype.t.$$ && cmptype.t.$$.T$name && tmpobj.$$targs$$[i].t.$$.T$name === cmptype.t.$$.T$name)) {
                            return false;
                        }
                    } else if (iance === null) {
                        console.log("Possible missing metamodel for " + type.t.$$.T$name + "<" + i + ">");
                    } else {
                        console.log("Don't know what to do about variance '" + iance + "'");
                    }
                }
            }
            return true;
        }
    }
    return false;
}
function isOfTypes(obj, types) {
    if (obj===null) {
        for (var i=0; i < types.l.length; i++) {
            if(types.l[i].t===Null || types.l[i].t===Anything) return true;
            else if (types.l[i].t==='u') {
                if (isOfTypes(null, types.l[i])) return true;
            }
        }
        return false;
    }
    if (obj === undefined || obj.getT$all === undefined) { return false; }
    var unions = false;
    var inters = true;
    var _ints=false;
    var objTypes = obj.getT$all();
    for (var i = 0; i < types.l.length; i++) {
        var t = types.l[i];
        var partial = isOfType(obj, t);
        if (types.t==='u') {
            unions = partial || unions;
        } else {
            inters = partial && inters;
            _ints=true;
        }
    }
    return _ints ? inters||unions : unions;
}
function extendsType(t1, t2) {
    if (t1 === undefined) {
        return true;//t2 === undefined;
    } else if (t1 === null) {
        return t2.t === Null;
    }
    if (t1.t === 'u' || t1.t === 'i') {
        if (t1.t==='i')removeSupertypes(t1.l);
        var unions = false;
        var inters = true;
        var _ints = false;
        for (var i = 0; i < t1.l.length; i++) {
            var partial = extendsType(t1.l[i], t2);
            if (t1.t==='u') {
                unions = partial||unions;
            } else {
                inters = partial&&inters;
                _ints=true;
            }
        }
        return _ints ? inters||unions : unions;
    }
    if (t2.t === 'u' || t2.t === 'i') {
        if (t2.t==='i') removeSupertypes(t2.l);
        var unions = false;
        var inters = true;
        var _ints = false;
        for (var i = 0; i < t2.l.length; i++) {
            var partial = extendsType(t1, t2.l[i]);
            if (t2.t==='u') {
                unions = partial||unions;
            } else {
                inters = partial&&inters;
                _ints=true;
            }
        }
        return _ints ? inters||unions : unions;
    }
    for (t in t1.t.$$.T$all) {
        if (t === t2.t.$$.T$name || t === 'ceylon.language::Nothing') {
            if (t1.a && t2.a) {
                //Compare type arguments
                for (ta in t1.a) {
                    if (!extendsType(t1.a[ta], t2.a[ta])) return false;
                }
            }
            return true;
        }
    }
    return false;
}
function removeSupertypes(list) {
    for (var i=0; i < list.length; i++) {
        for (var j=i; i < list.length; i++) {
            if (i!==j) {
                if (extendsType(list[i],list[j])) {
                    list[j]=list[i];
                } else if (extendsType(list[j],list[i])) {
                    list[i]=list[j];
                }
            }
        }
    }
}

function className(obj) {
    function _typename(t) {
        if (t.t==='i' || t.t==='u') {
            var _sep = t.t==='i'?'&':'|';
            var ct = '';
            for (var i=0; i < t.l.length; i++) {
                if (i>0) { ct+=_sep; }
                ct += _typename(t.l[i]);
            }
            return String$(ct);
        } else {
            var tn = t.t.$$.T$name;
            if (t.a) {
                tn += '<';
                for (var i = 0; i < t.a.length; i++) {
                    if (i>0) { tn += ','; }
                    tn += _typename(t.a[i]);
                }
                tn += '>';
            }
            return tn;
        }
    }
    if (obj === null) return String$('ceylon.language::Null');
    var tn = obj.getT$name === undefined ? 'UNKNOWN' : obj.getT$name();
    if (tn === 'UNKNOWN') {
        if (typeof obj === 'function') {
            tn = 'ceylon.language::Callable';
        }
    }
    else if (obj.$$targs$$) {
        /*tn += '<';
        for (var i=0; i < obj.$$targs$$.length; i++) {
            if (i>0) { tn += ','; }
            tn += _typename(obj.$$targs$$[i]);
        }
        tn += '>';*/
    }
    return String$(tn);
}
className.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['className']};

function identityHash(obj) {
    return obj.BasicID;
}
identityHash.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['identityHash']};

function set_type_args(obj, targs) {
    if (obj===undefined)return;
    if (obj.$$targs$$ === undefined) {
        obj.$$targs$$=targs;
    } else {
        for (x in targs) {
            obj.$$targs$$[x] = targs[x];
        }
    }
}
function add_type_arg(obj, name, type) {
    if (obj.$$targs$$ === undefined) {
        obj.$$targs$$={};
    }
    obj.$$targs$$[name]=type;
}
function throwexc(msg) {
    throw Exception(msg.getT$all?msg:String$(msg));
}
exports.set_type_args=set_type_args;
exports.add_type_arg=add_type_arg;
exports.exists=exists;
exports.nonempty=nonempty;
exports.isOfType=isOfType;
exports.className=className;
exports.identityHash=identityHash;
exports.throwexc=throwexc;
function internalSort(comp, elems, $$$mptypes) {
    if (elems===undefined) {return getEmpty();}
    var arr = [];
    var it = elems.iterator();
    var e;
    while ((e=it.next()) !== getFinished()) {arr.push(e);}
    if (arr.length === 0) {return getEmpty();}
    arr.sort(function(a, b) {
        var cmp = comp(a,b);
        return (cmp===larger) ? 1 : ((cmp===smaller) ? -1 : 0);
    });
    return ArraySequence(arr, $$$mptypes);
}
internalSort.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['internalSort']};

function flatten(tf, $$$mptypes) {
    var rf = function() {
        var t = getEmpty();
        var e = null;
        var argc = arguments.length;
        var last = argc>0 ? arguments[argc-1] : undefined;
        if (typeof(last) === 'object' && typeof(last.Args) === 'object' && typeof(last.Args.t) === 'function') {
            argc--;
        }
        for (var i=0; i < argc; i++) {
            var c = arguments[i]===null ? Null :
                arguments[i] === undefined ? Empty :
                arguments[i].getT$all ? arguments[i].getT$all() :
                Anything;
            if (e === null) {
                e = c;
            } else if (e.t === 'u' && e.l.length > 0) {
                var l = [c];
                for (var j=0; j < e.l.length; j++) {
                    l[j+1] = e.l[j];
                }
            } else {
                e = {t:'u', l:[e, c]};
            }
            var rest;
            if (t === getEmpty()) {
                rest={t:Empty};
            } else {
                rest={t:Tuple, a:t.$$$targs$$$};
            }
            t = Tuple(arguments[i], t, {First:c, Element:e, Rest:rest});
        }
        return tf(t, t.$$targs$$);
    };
    rf.$$targs$$=$$$mptypes;
    return rf;
}
flatten.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['flatten']};

function unflatten(ff, $$$mptypes) {
    if (ff.$$metamodel$$ && ff.$$metamodel$$['$ps']) {
        var ru = function ru(seq) {
            if (seq===undefined || seq.size === 0) { return ff(); }
            var pmeta = ff.$$metamodel$$['$ps'];
            var a = [];
            for (var i = 0; i < pmeta.length; i++) {
                if (pmeta[i]['seq'] == 1) {
                    a[i] = seq.skipping(i).sequence;
                } else if (seq.size > i) {
                    a[i] = seq.get(i);
                } else {
                    a[i] = undefined;
                }
            }
            a[i]=ru.$$targs$$;
            return ff.apply(ru, a);
        }
    } else {
        var ru = function ru(seq) {
            if (seq===undefined || seq.size === 0) { return ff(); }
            var a = [];
            for (var i = 0; i < seq.size; i++) {
                a[i] = seq.get(i);
            }
            a[i]=ru.$$targs$$;
            return ff.apply(ru, a);
        }
    }
    ru.$$targs$$=$$$mptypes;
    return ru;
}
unflatten.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['unflatten']};
exports.flatten=flatten;
exports.unflatten=unflatten;

//internal
function toTuple(iterable) {
  var seq = iterable.sequence;
  return Tuple(seq.first, seq.rest.sequence,
    {First:seq.$$targs$$.Element, Element:seq.$$targs$$.Element, Rest:{t:Sequential, a:seq.$$targs$$}});
}
exports.toTuple=toTuple;

function integerRangeByIterable(range, step, $$$mptypes) {
    return Comprehension(function(){
        var a = range.first;
        var b = range.last;
        if (a>b) {
            a += step;
            return function() {
                a -= step;
                return a<b ? getFinished() : a;
            }
        }
        a-=step;
        return function() {
            a += step;
            return a>b ? getFinished() : a;
        }
    }, {Element:range.$$targs$$.Element, Absent:range.$$targs$$.Absent});
}
integerRangeByIterable.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['integerRangeByIterable']};
exports.integerRangeByIterable=integerRangeByIterable;
// implementation of object "process" in ceylon.language
function languageClass() {
    var lang = new languageClass.$$;
    Basic(lang);
    return lang;
}
languageClass.$$metamodel$$={$nm:'languageClass',$mt:'cls',$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']};
initTypeProto(languageClass, "ceylon.language::language", $init$Basic());
var lang$proto=languageClass.$$.prototype;
defineAttr(lang$proto, 'version', function() {
    return String$("0.6",3);
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']['$at']['version']});
defineAttr(lang$proto, 'majorVersion', function(){ return 0; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']['$at']['majorVersion']});
defineAttr(lang$proto, 'minorVersion', function(){ return 6; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']['$at']['minorVersion']});
defineAttr(lang$proto, 'releaseVersion', function(){ return 0; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']['$at']['releaseVersion']});
defineAttr(lang$proto, 'versionName', function(){ return String$("Transmogrifier",14); },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']['$at']['versionName']});
defineAttr(lang$proto, 'majorVersionBinary', function(){ return 5; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']['$at']['majorVersionBinary']});
defineAttr(lang$proto, 'minorVersionBinary', function(){ return 0; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['language']['$at']['minorVersionBinary']});
var languageString = String$("language", 8);
defineAttr(lang$proto, 'string', function() {
    return languageString;
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['string']});

var language$ = languageClass();
function getLanguage() { return language$; }
exports.getLanguage=getLanguage;

function processClass() {
    var proc = new processClass.$$;
    Basic(proc);
    return proc;
}
processClass.$$metamodel$$={$nm:'processClass',$mt:'cls',$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']};
initTypeProto(processClass, "ceylon.language::process", $init$Basic());
var process$proto = processClass.$$.prototype;

var argv = getEmpty();
var namedArgs = {};
if ((typeof process !== "undefined") && (process.argv !== undefined)) {
    // parse command line arguments
    if (process.argv.length > 1) {
        var args = process.argv.slice(1);
        for (var i=0; i<args.length; ++i) {
            var arg = args[i];
            if (arg.charAt(0) == '-') {
                var pos = 1;
                if (arg.charAt(1) == '-') { pos = 2; }
                arg = arg.substr(pos);
                pos = arg.indexOf('=');
                if (pos >= 0) {
                    namedArgs[arg.substr(0, pos)] = String$(arg.substr(pos+1));
                } else {
                    var value = args[i+1];
                    if ((value !== undefined) && (value.charAt(0) != '-')) {
                        namedArgs[arg] = String$(value);
                        ++i;
                    } else {
                        namedArgs[arg] = null;
                    }
                }
            }
            args[i] = String$(args[i]);
        }
        argv = ArraySequence(args, {Element:{t:String$}});
    }
} else if (typeof window !== "undefined") {
    // parse URL parameters
    var parts = window.location.search.substr(1).replace('+', ' ').split('&');
    if ((parts.length > 1) || ((parts.length > 0) && (parts[0].length > 0))) {
        var argStrings = new Array(parts.length);
        //can't do "for (i in parts)" anymore because of the added stuff to arrays
        var i;
        for (i=0; i<parts.length; i++) { argStrings[i] = String$(parts[i]); }
        argv = ArraySequence(argStrings, {Element:{t:String$}});
        
        for (i=0; i < parts.length; i++) {
            var part = parts[i];
            var pos = part.indexOf('=');
            if (pos >= 0) {
                var value = decodeURIComponent(part.substr(pos+1));
                namedArgs[part.substr(0, pos)] = String$(value);
            } else {
                namedArgs[part] = null;
            }
        }
    }
}
defineAttr(process$proto, 'arguments', function(){ return argv; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['arguments']});
process$proto.namedArgumentPresent = function(name) {
    return (name in namedArgs);
}
process$proto.namedArgumentValue = function(name) {
    var value = namedArgs[name];
    return (value !== undefined) ? value : null;
}

var properties = {};
if (typeof navigator !== "undefined") {
    if (navigator.language !== undefined) {
        properties["user.language"] = String$(navigator.language);
    }
    if (navigator.platform !== undefined) {
        properties["os.name"] = String$(navigator.platform);
    }
}
if (typeof process !== "undefined") {
    if (process.platform !== undefined) {
        properties["os.name"] = String$(process.platform);
    }
    if (process.arch !== undefined) {
        properties["os.arch"] = String$(process.arch);
    }
}
if (typeof document !== "undefined") {
    if (document.defaultCharset !== undefined) {
        properties["file.encoding"] = String$(document.defaultCharset);
    }
}

var linesep = String$('\n', 1);
var filesep = String$('/', 1);
var pathsep = String$(':', 1);
var osname = properties["os.name"];
if ((osname !== undefined) && (osname.search(/windows/i) >= 0)) {
    linesep = String$("\r\n", 2);
    filesep = String$('\\', 1);
    pathsep = String$(';', 1);
}
properties["line.separator"] = linesep;
properties["file.separator"] = filesep;
properties["path.separator"] = pathsep;

process$proto.propertyValue = function(name) {
    var value = properties[name];
    return (value !== undefined) ? value : null;
}

defineAttr(process$proto, 'newline', function(){ return linesep; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['newline']});

if ((typeof process !== "undefined") && (process.stdout !== undefined)) {
    process$proto.write = function(string) {
        process.stdout.write(string.valueOf());
    }
    process$proto.writeLine = function(line) {
        this.write(line.valueOf());
        this.write(linesep.valueOf());
    }
} else if ((typeof console !== "undefined") && (console.log !== undefined)) {
    process$proto.writeLine = function(line) {
        console.log(line.valueOf());
    }
    process$proto.write = process$proto.writeLine;
} else {
    process$proto.write = function() {};
    process$proto.writeLine = function() {};    
}

if ((typeof process !== "undefined") && (process.stderr !== undefined)) {
    process$proto.writeError = function(string) {
        process.stderr.write(string.valueOf());
    }
    process$proto.writeErrorLine = function(line) {
        this.writeError(line.valueOf());
        this.writeError(linesep.valueOf());
    }
} else if ((typeof console !== "undefined") && (console.error !== undefined)) {
    process$proto.writeErrorLine = function(line) {
        console.error(line.valueOf());
    }
    process$proto.writeError = process$proto.writeErrorLine;
} else {
    process$proto.writeError = process$proto.write;
    process$proto.writeErrorLine = process$proto.writeLine;    
}

process$proto.readLine = function() {
    return String$("", 0);//TODO
}

defineAttr(process$proto, 'milliseconds', function() {
    return Date.now();
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['milliseconds']});
defineAttr(process$proto, 'nanoseconds', function() {
    return Date.now()*1000000;
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['nanoseconds']});

if ((typeof process !== "undefined") && (process.exit !== undefined)) {
    process$proto.exit = function(code) {
        process.exit(code);
    }
} else {
    process$proto.exit = function() {}
}

var processString = String$("process", 7);
defineAttr(process$proto, 'string', function() {
    return processString;
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Object']['$at']['string']});
defineAttr(process$proto, 'vm', function() {
    if (typeof process !== "undefined" && process.execPath && process.execPath.match(/node(\.exe)?$/)) {
        return String$("node.js", 7);
    } else if (typeof window === 'object') {
        return String$("Browser", 7);
    }
    return String$("Unknown JavaScript environment", 30);
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['vm']});
defineAttr(process$proto, 'vmVersion', function() {
    if (typeof process !== "undefined" && typeof process.version === 'string') {
        return String$(process.version);
    }
    return String$("Unknown");
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['vmVersion']});
defineAttr(process$proto, 'os',function() {
    if (typeof process !== "undefined" && typeof process.platform === 'string') {
        return String$(process.platform);
    }
    return String$("Unknown");
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['os']});
defineAttr(process$proto, 'osVersion', function() {
    return String$("Unknown");
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['process']['$at']['osVersion']});

var process$ = processClass();
function getProcess() { return process$; }
exports.getProcess=getProcess;
function addSuppressedException(/*Exception*/sup,/*Exception*/e) {
    if (e.$sups$===undefined) {
        e.$sups$=[];
    }
    if (sup.getT$name === undefined) sup = NativeException(sup);
    e.$sups$.push(sup);
}
exports.addSuppressedException=addSuppressedException;
function suppressedExceptions(/*Exception*/e) {
    return e.$sups$===undefined?getEmpty():e.$sups$;
}
exports.suppressedExceptions=suppressedExceptions;
/*Native Implementation of annotations() */
function annotations$model(anntype, progelem, $$targs$$) {
  var mm = progelem.tipo?progelem.tipo.$$metamodel$$:progelem.attr?progelem.attr.$$metamodel$$:progelem.$$metamodel$$;
  if (mm && mm['$an']) {
    var anns = mm['$an'];
    if (typeof(anns) === 'function') {
      anns = anns();
      mm['$an'] = anns;
    }
    if (anntype.tipo.$$.T$all['ceylon.language.model::OptionalAnnotation'] !== undefined) {
      //find the first one and return it
      for (var i=0; i < anns.length; i++) {
        if (isOfType(anns[i], {t:anntype.tipo}))return anns[i];
      }
      return null;
    }
    //gather all annotations of the required type and return them
    var r=[];
    for (var i=0; i < anns.length; i++) {
      if (isOfType(anns[i], {t:anntype.tipo}))r.push(anns[i]);
    }
    return r.length==0?getEmpty():r.reifyCeylonType({Absent:{t:Null},Element:{t:ConstrainedAnnotation$model,
      a:{Value:$$targs$$.Value,Values:$$targs$$.Values,ProgramElement:$$targs$$.ProgramElement}}});
  }
  return null;
}
exports.annotations$model=annotations$model;
//Ends compiled from Ceylon sources
function NativeException(e) {
var that = new NativeException.$$;
var msg;
if (typeof e === 'string') {
msg = String$(e);
} else if (e) {
msg = String$(e.toString());
} else {
msg = String$("Native JavaScript Exception",27);
}
Exception(msg,null,that);
return that;
}
initTypeProto(NativeException, 'ceylon.language::NativeException', $init$Exception());
NativeException.$$metamodel$$={$nm:'NativeException',$mt:'cls',$ps:[{t:Exception}],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:$$METAMODEL$$['ceylon.language']['Exception']};
exports.Identifiable=Identifiable;
exports.identityHash=$identityHash;
exports.Object=Object$;
exports.Anything=Anything;
exports.Null=Null;
exports.Nothing=Nothing;
exports.Boolean=Boolean$;
exports.Comparison=Comparison;
exports.getNull=getNull;
exports.getTrue=getTrue;
exports.getFalse=getFalse;
exports.NativeException=NativeException;
});
}(typeof define==='function' && define.amd ?
define : function (factory) {
if (typeof exports!=='undefined') {
factory(require, exports, module);
} else {
throw "no module loader";
}
}));
