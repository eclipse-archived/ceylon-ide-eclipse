(function(define) {
define(function(require, exports, module) {
//the Ceylon language module
var $$METAMODEL$$={"$mod-name":"ceylon.language","$mod-version":"0.6","ceylon.language":{"Iterator":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Iterable"],"doc":["Produces elements of an `Iterable` object. Classes that \nimplement this interface should be immutable."],"by":["Gavin"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The next element, or `finished` if there are no \nmore elements to be iterated."]},"$nm":"next"}},"$nm":"Iterator"},"Callable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A reference to a function. The type arguments encode \nthe function return type and parameter types. The \nparameter types are typically represented as a tuple\ntype. For example, the type of the function reference \n`plus<Float>` is:\n\n    Callable<Float,[Float,Float]>\n\nwhich we usually abbreviate `Float(Float,Float)`. Any\ninstance of `Callable` may be _invoked_ by supplying a \npositional argument list:\n\n    Float(Float,Float) add = plus<Float>;\n    value four = add(2.0, 2.0);\n\nor by supplying a tuple containing the arguments:\n\n    Float(Float,Float) add = plus<Float>;\n    [Float,Float] twoAndTwo = [2.0, 2.0];\n    value four = add(*twoAndTwo);\n\nThis interface may not be implemented by user code."]},"$nm":"Callable"},"Array":{"super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Cloneable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Ranged"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"final":[],"native":[],"doc":["A fixed-size array of elements. An array may have zero\nsize (an empty array). Arrays are mutable. Any element\nof an array may be set to a new value.\n\nThis class is provided primarily to support interoperation \nwith Java, and for some performance-critical low-level \nprogramming tasks."]},"$m":{"copyTo":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$an":{"doc":["The array into which to copy the elements."]},"$nm":"other"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["The index of the first element in this array to copy."]},"$nm":"sourcePosition"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["The index in the given array into which to \ncopy the first element."]},"$nm":"destinationPosition"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["The number of elements to copy."]},"$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Efficiently copy the elements in the segment \n`sourcePosition:length` of this array to the segment \n`destinationPosition:length` of the given array."]},"$nm":"copyTo"},"set":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$an":{"doc":["The index of the element to replace."]},"$nm":"index"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$an":{"doc":["The new element."]},"$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Replace the existing element at the specified index \nwith the given element. Does nothing if the specified \nindex is negative or larger than the index of the \nlast element in the array."]},"$nm":"set"}},"$at":{"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The rest of the array, without the first element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Reverse this array, returning a new array."],"actual":[]},"$nm":"reversed"}},"$nm":"Array"},"ArraySequence":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder","SequenceAppender"],"doc":["An immutable `Sequence` implemented using the platform's \nnative array type. Where possible copying of the underlying \narray is avoided."]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"iterator"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"count"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"spanTo"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"contains"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"ArraySequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"ArraySequence"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"first"},"elements":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"elements"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"size"}},"$nm":"ArraySequence"},"Singleton":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A sequence with exactly one element, which may be null."]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$c":{"SingletonIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"done":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"done"}},"$nm":"SingletonIterator"}},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns the contained element, if the specified \nindex is `0`."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `1` if this `Singleton`'s element\nsatisfies the predicate, or `0` otherwise."],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A `Singleton` can be equal to another `List` if \nthat `List` has only one element which is equal to \nthis `Singleton`'s element."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0` and the given `length` is greater than `0`.\nOtherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `true` if the specified element is this \n`Singleton`'s element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"initial"},{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"mthd","$nm":"selecting"}},"$nm":"find"},"filter":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0`. Otherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `0`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `Singleton` with the same element."],"actual":[]},"$nm":"clone"},"element":{"$t":{"$nm":"Element"},"$mt":"attr","$nm":"element"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the Singleton itself, or empty"],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Return this singleton."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `Empty`."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `1`."],"actual":[]},"$nm":"size"}},"$nm":"Singleton"},"byKey":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"}],"$an":{"shared":[],"see":["byItem"],"doc":["A comparator for `Entry`s which compares their keys \naccording to the given `comparing()` function."]},"$nm":"byKey"},"Comparable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Other"}],"$an":{"shared":[],"doc":["The general contract for values whose magnitude can be \ncompared. `Comparable` imposes a total ordering upon\ninstances of any type that satisfies the interface.\nIf a type `T` satisfies `Comparable<T>`, then instances\nof `T` may be compared using the comparison operators\n`<`, `>`, `<=`, >=`, and `<=>`.\n\nThe total order of a type must be consistent with the \ndefinition of equality for the type. That is, there\nare three mutually exclusive possibilities:\n\n- `x<y`,\n- `x>y`, or\n- `x==y`"],"by":["Gavin"]},"$m":{"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["equals"],"doc":["Compares this value with the given value. \nImplementations must respect the constraints that: \n\n- `x==y` if and only if `x<=>y == equal` \n   (consistency with `equals()`), \n- if `x>y` then `y<x` (symmetry), and \n- if `x>y` and `y>z` then `x>z` (transitivity)."]},"$nm":"compare"}},"$nm":"Comparable","$st":"Other"},"Comparison":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"larger"},{"$pk":"ceylon.language","$nm":"smaller"},{"$pk":"ceylon.language","$nm":"equal"}],"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[],"actual":[]},"$nm":"string"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Comparable"],"doc":["The result of a comparison between two `Comparable` \nobjects."],"by":["Gavin"]},"$m":{"largerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"largerThan"},"equal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"equal"},"asSmallAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asSmallAs"},"asLargeAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asLargeAs"},"smallerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"smallerThan"},"unequal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"unequal"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"Comparison"},"annotation":{"$t":{"$pk":"ceylon.language","$nm":"Annotation"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as an annotation class, or a \nmethod as an annotation method."]},"$annot":"1","$nm":"annotation"},"Empty":{"of":[{"$pk":"ceylon.language","$nm":"empty"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$an":{"shared":[],"see":["Sequence"],"doc":["A sequence with no elements. The type `Empty` may be\nabbreviated `[]`, and an instance is produced by the \nexpression `[]`. That is, in the following expression,\n`e` has type `[]` and refers to the value `[]`:\n\n    [] none = [];\n\n(Whether the syntax `[]` refers to the type or the \nvalue depends upon how it occurs grammatically.)"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["Returns an iterator that is already exhausted."],"actual":[]},"$nm":"iterator"},"sort":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `null` for any given index."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns 0 for any given predicate."],"actual":[]},"$nm":"count"},"select":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"select"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given segment."],"actual":[]},"$nm":"segment"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `false` for any given element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"initial"},{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"following":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"head"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"following"},"withTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"defines"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanTo"},"chain":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"OtherAbsent"}],"$an":{"shared":[],"doc":["Returns `other`."],"actual":[]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"withLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"collect":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"collect"},"span":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"clone"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"indexed"},"sequence":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a string description of the empty sequence: \n`{}`."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `true`."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns 0."],"actual":[]},"$nm":"size"}},"$nm":"Empty"},"Enumerable":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["Abstraction of ordinal types whose instances can be \nmapped to the integers or to a range of integers."]},"$at":{"integerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The corresponding integer. The implementation must\nsatisfy these constraints:\n\n    (x.successor).integerValue = x.integerValue+1\n    (x.predecessor).integerValue = x.integerValue-1\n\nfor every instance `x` of the enumerable type."]},"$nm":"integerValue"}},"$nm":"Enumerable","$st":"Other"},"combine":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"},{"$t":{"$nm":"OtherElement"},"$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"prm","$pt":"f","$nm":"combination"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"otherElements"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"},{"$nm":"Element"},{"$nm":"OtherElement"}],"$an":{"shared":[],"doc":["Applies a function to each element of two `Iterable`s\nand returns an `Iterable` with the results."],"by":["Gavin","Enrique Zamudio","Tako"]},"$m":{"combination":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"},{"$t":{"$nm":"OtherElement"},"$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"mthd","$nm":"combination"}},"$nm":"combine","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Result"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"otherIter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$nm":"otherIter"},"iter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$nm":"iter"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"empty":{"super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Empty"}],"$mt":"obj","$an":{"shared":[],"doc":["A sequence with no elements, abbreviated `[]`. The \nunique instance of the type `[]`."]},"$nm":"empty"},"false":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing falsity in Boolean logic."]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"false"},"compose":{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"X"},{"$nm":"Y"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"see":["curry","uncurry"],"doc":["Composes two functions, returning a function equivalent to \ninvoking `x(y(args))`."]},"$nm":"compose"},"Formal":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Formal"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[formal]]."]},"$annot":"1","$nm":"Formal"},"Sequential":{"of":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A possibly-empty, immutable sequence of values. The \ntype `Sequential<Element>` may be abbreviated \n`[Element*]` or `Element[]`. \n\n`Sequential` has two enumerated subtypes:\n\n- `Empty`, abbreviated `[]`, represents an empty \n   sequence, and\n- `Sequence<Element>`, abbreviated `[Element+]` \n   represents a non-empty sequence, and has the very\n   important subclass `Tuple`."]},"$m":{"terminal":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"terminal"},"trimTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"trimTrailing"},"initial":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"initial"},"trimLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"trimLeading"},"repeat":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Returns a sequence formed by repeating the elements of \nthis sequence the given number of times, or an empty \nsequence if `times<=0`."],"actual":[]},"$nm":"repeat"},"trim":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"trim"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"[ x, y, z ]\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \nelement."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new sequence."],"actual":[]},"$nm":"reversed"}},"$nm":"Sequential"},"minIntegerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The minimum `Integer` value that can be represented\nby the backend"]},"$nm":"minIntegerValue"},"Finished":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"finished"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Iterator"],"doc":["The type of the value that indicates that \nan `Iterator` is exhausted and has no more \nvalues to return."]},"$nm":"Finished"},"Default":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Default"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[default]]."]},"$annot":"1","$nm":"Default"},"plus":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["times","sum"],"doc":["Add the given `Summable` values."]},"$nm":"plus"},"final":{"$t":{"$pk":"ceylon.language","$nm":"Final"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as final. A `final` class may \nnot be extended. Marking a class as final affects disjoint\ntype analysis."]},"$annot":"1","$nm":"final"},"coalesce":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$an":{"doc":["The values, some of which may be null."]},"$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Return a sequence containing the given values which are\nnot null. If there are no values which are not null,\nreturn an empty sequence."]},"$nm":"coalesce"},"Entry":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"Key"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The key used to access the entry."]},"$nm":"key"},{"$t":{"$nm":"Item"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The value associated with the key."]},"$nm":"item"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"final":[],"doc":["A pair containing a _key_ and an associated value called \nthe _item_. Used primarily to represent the elements of \na `Map`. The type `Entry<Key,Item>` may be abbreviated \n`Key->Item`. An instance of `Entry` may be constructed \nusing the `->` operator:\n\n    String->Person entry = person.name->person;\n"],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if this entry is equal to the given\nentry. Two entries are equal if they have the same\nkey and the same value."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a description of the entry in the form \n`key->item`."],"actual":[]},"$nm":"string"},"item":{"$t":{"$nm":"Item"},"$mt":"attr","$an":{"shared":[],"doc":["The value associated with the key."]},"$nm":"item"},"key":{"$t":{"$nm":"Key"},"$mt":"attr","$an":{"shared":[],"doc":["The key used to access the entry."]},"$nm":"key"}},"$nm":"Entry"},"aInt":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"aInt"},"Variable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Variable"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[variable]]."]},"$annot":"1","$nm":"Variable"},"Invertable":{"of":[{"$nm":"Inverse"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Inverse"}],"$pk":"ceylon.language","$nm":"Invertable"}],"variance":"out","$nm":"Inverse"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of types which support a unary additive inversion\noperation. For a numeric type, this should return the \nnegative of the argument value. Note that the type \nparameter of this interface is not restricted to be a \nself type, in order to accommodate the possibility of \ntypes whose additive inverse can only be expressed in terms of \na wider type."],"by":["Gavin"]},"$at":{"positiveValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The value itself, expressed as an instance of the\nwider type."]},"$nm":"positiveValue"},"negativeValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The additive inverse of the value, which may be expressed\nas an instance of a wider type."]},"$nm":"negativeValue"}},"$nm":"Invertable","$st":"Inverse"},"ThrownException":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"type"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"when"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"ThrownException"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[]},"$annot":"1","$at":{"when":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"when"},"type":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},"$mt":"attr","$an":{"shared":[]},"$nm":"type"}},"$nm":"ThrownException"},"Cloneable":{"of":[{"$nm":"Clone"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Clone"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"variance":"out","$nm":"Clone"}],"$an":{"shared":[],"doc":["Abstract supertype of objects whose value can be \ncloned."]},"$at":{"clone":{"$t":{"$nm":"Clone"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Obtain a clone of this object. For a mutable \nobject, this should return a copy of the object. \nFor an immutable object, it is acceptable to return\nthe object itself."]},"$nm":"clone"}},"$nm":"Cloneable","$st":"Clone"},"Ordinal":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"see":["Character","Integer","Integral","Range"],"doc":["Abstraction of ordinal types, that is, types with \nsuccessor and predecessor operations, including\n`Integer` and other `Integral` numeric types.\n`Character` is also considered an ordinal type. \n`Ordinal` types may be used to generate a `Range`."],"by":["Gavin"]},"$at":{"predecessor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The predecessor of this value."]},"$nm":"predecessor"},"successor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The successor of this value."]},"$nm":"successor"}},"$nm":"Ordinal","$st":"Other"},"maxIntegerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The maximum `Integer` value that can be represented\nby the backend"]},"$nm":"maxIntegerValue"},"largest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","smallest","max"],"doc":["Given two `Comparable` values, return largest of the\ntwo."]},"$nm":"largest"},"native":{"$t":{"$pk":"ceylon.language","$nm":"Native"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation is defined \nin platform-native code."]},"$annot":"1","$nm":"native"},"unflatten":{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"flatFunction"}],[{"$t":{"$nm":"Args"},"$mt":"prm","$pt":"v","$nm":"args"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[],"see":["flatten"],"doc":["Given an arbitrary `Callable` with parameter types \n`P1`, `P2`, ..., `Pn` returns an equivalent `Callable` which \ntakes a single tuple argument of type `[P1, P2, ..., Pn]`."]},"$nm":"unflatten"},"formatInteger":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"integer"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$nm":"radix"}]],"$mt":"mthd","$an":{"shared":[],"throws":["AssertionException","if `radix` is not between `minRadix` and `maxRadix`"],"doc":["The string representation of `integer` in the `radix` base.\n`radix` must be between `minRadix` and `maxRadix` included.\n\nIf `integer` is negative, returned string will start by character `-`"]},"$nm":"formatInteger"},"greaterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is greater than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"greaterThan"},"Identifiable":{"$mt":"ifc","$an":{"shared":[],"doc":["The abstract supertype of all types with a well-defined\nnotion of identity. Values of type `Identifiable` may \nbe compared using the `===` operator to determine if \nthey are references to the same object instance. For\nthe sake of convenience, this interface defines a\ndefault implementation of value equality equivalent\nto identity. Of course, subtypes are encouraged to\nrefine this implementation."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Identity equality comparing the identity of the two \nvalues. May be refined by subtypes for which value \nequality is more appropriate. Implementations must\nrespect the constraint that if `x===y` then `x==y` \n(equality is consistent with identity)."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["identityHash"],"doc":["The system-defined identity hash value of the \ninstance. Subtypes which refine `equals()` must \nalso refine `hash`, according to the general \ncontract defined by `Object`."],"actual":[]},"$nm":"hash"}},"$nm":"Identifiable"},"language":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Contains information about the language"]},"$at":{"majorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language major version."]},"$nm":"majorVersion"},"majorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The major version of the code generated for the underlying runtime."]},"$nm":"majorVersionBinary"},"minorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language minor version."]},"$nm":"minorVersion"},"versionName":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release name."]},"$nm":"versionName"},"releaseVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release version."]},"$nm":"releaseVersion"},"minorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The minor version of the code generated for the underlying runtime."]},"$nm":"minorVersionBinary"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language version."]},"$nm":"version"}},"$nm":"language"},"Native":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Native"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[native]]."]},"$annot":"1","$nm":"Native"},"Null":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"of":[{"$pk":"ceylon.language","$nm":"null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["null"],"doc":["The type of the `null` value. Any union type of form \n`Null|T` is considered an _optional_ type, whose values\ninclude `null`. Any type of this form may be written as\n`T?` for convenience.\n\nThe `if (exists ... )` construct, or, alternatively,\nthe `assert (exists ...)` construct, may be used to\nnarrow an optional type to a _definite_ type, that is,\na subtype of `Object`:\n\n    String? firstArg = process.arguments.first;\n    if (exists firstArg) {\n        print(\"hello \" + firstArg);\n    }\n\nThe `else` operator evaluates its second operand if \nand only if its first operand is `null`:\n\n    String name = process.arguments.first else \"world\";\n\nThe `then` operator evaluates its second operand when\nits first operand evaluates to `true`, and to `null` \notherwise:\n\n    Float? diff = x>=y then x-y;"],"by":["Gavin"]},"$nm":"Null"},"array":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array containing the given elements. If no\nelements are provided, create an empty array of the\ngiven element type."]},"$nm":"array"},"stringify":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$nm":"stringify"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable"],"doc":["Sort the given elements, returning a new sequence."]},"$nm":"sort"},"equalTo":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if they're equal.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"equalTo"},"AssertionException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"message"}],"$mt":"cls","$an":{"shared":[],"doc":["An exception that occurs when an assertion fails, that\nis, when a condition in an `assert` statement evaluates\nto false at runtime."]},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$nm":"message"}},"$nm":"AssertionException"},"suppressedExceptions":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Exception"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns the exceptions that were suppressed (if any) during the handling of the given exception."]},"$nm":"suppressedExceptions"},"Ranged":{"of":[{"$nm":"Span"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Index"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Index"},{"variance":"out","$nm":"Span"}],"$an":{"shared":[],"see":["List","Sequence","String"],"doc":["Abstract supertype of ranged objects which map a range\nof `Comparable` keys to ranges of values. The type\nparameter `Span` abstracts the type of the resulting\nrange.\n\nA span may be obtained from an instance of `Ranged`\nusing the span operator:\n\n    print(\"hello world\"[0..5])\n"]},"$m":{"spanTo":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe start of the receiver and the end index."]},"$nm":"spanTo"},"segment":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a segment containing the mapped values\nstarting from the given index, with the given \nlength."]},"$nm":"segment"},"spanFrom":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe starting index and the end of the receiver."]},"$nm":"spanFrom"},"span":{"$t":{"$nm":"Span"},"$ps":[[{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$nm":"Index"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between \nthe two given indices."]},"$nm":"span"}},"$nm":"Ranged","$st":"Span"},"arrayOfSize":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$an":{"doc":["The size of the resulting array. If the size\nis non-positive, an empty array will be \ncreated."]},"$nm":"size"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$an":{"doc":["The element value with which to populate the\narray. All elements of the resulting array \nwill have the same value."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array of the specified size, populating every \nindex with the given element. If the specified size is\nsmaller than `1`, return an empty array of the given\nelement type."]},"$nm":"arrayOfSize"},"times":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Value"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["plus","product"],"doc":["Multiply the given `Numeric` values."]},"$nm":"times"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Produces a sequence of each index to element `Entry` \nfor the given sequence of values."]},"$nm":"entries"},"license":{"$t":{"$pk":"ceylon.language","$nm":"License"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify the URL of the license of a module or \npackage."]},"$annot":"1","$nm":"license"},"Object":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Basic","Null"],"doc":["The abstract supertype of all types representing \ndefinite values. Any two `Object`s may be compared\nfor value equality using the `==` and `!=` operators:\n\n    true==false\n    1==\"hello world\"\n    \"hello\"+\" \"+\"world\"==\"hello world\"\n    Singleton(\"hello world\")=={ \"hello world\" }\n\nHowever, since `Null` is not a subtype of `Object`, the\nvalue `null` cannot be compared to any other value\nusing `==`. Thus, value equality is not defined for\noptional types. This neatly voids the problem of\ndeciding the value of the expression `null==null`,\nwhich is simply illegal."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determine if two values are equal. Implementations\nshould respect the constraints that:\n\n- if `x===y` then `x==y` (reflexivity), \n- if `x==y` then `y==x` (symmetry), \n- if `x==y` and `y==z` then `x==z` (transitivity).\n\nFurthermore it is recommended that implementations\nensure that if `x==y` then `x` and `y` have the\nsame concrete class."]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The hash value of the value, which allows the value\nto be an element of a hash-based set or key of a\nhash-based map. Implementations must respect the\nconstraint that if `x==y` then `x.hash==y.hash`."]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A developer-friendly string representing the \ninstance. Concatenates the name of the concrete \nclass of the instance with the `hash` of the \ninstance. Subclasses are encouraged to refine this \nimplementation to produce a more meaningful \nrepresentation."]},"$nm":"string"}},"$nm":"Object"},"null":{"super":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"obj","$an":{"shared":[],"doc":["The null value."],"by":["Gavin"]},"$nm":"null"},"min":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","max","smallest"],"doc":["Given a nonempty stream of `Comparable` values, \nreturn the smallest value in the stream."]},"$nm":"min"},"LazySet":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of `Set` that wraps an `Iterable` of\nelements. All operations on this `Set` are performed\non the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"complement"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"intersection"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"sorted":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$nm":"sorted"},"ready":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"ready"}},"$nm":"iterator"}}},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"exclusiveUnion"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"union"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"LazySet"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"elems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"elems"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazySet"},"Float":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"prm","$pt":"v","$nm":"float"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"doc":["An IEEE 754 64-bit [floating point number][]. A `Float` \nis capable of approximately representing numeric values \nbetween 2<sup>-1022<\/sup> and \n(2-2<sup>-52<\/sup>)×2<sup>1023<\/sup>, along with \nthe special values `infinity` and `-infinity`, and \nundefined values (Not a Number). Zero is represented by \ndistinct instances `+0`, `-0`, but these instances are \nequal. An undefined value is not equal to any other\nvalue, not even to itself.\n\n[floating point number]: http:\/\/www.validlab.com\/goldberg\/paper.pdf"]},"$at":{"strictlyNegative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a negative number, `-0`, \nor `-infinity`. Produces `false` for a positive \nnumber, `+0`, or undefined."]},"$nm":"strictlyNegative"},"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The sign of this value. Produces `1` for a positive \nnumber or `infinity`. Produces `-1` for a negative\nnumber or `-infinity`. Produces `0` for `+0`, `-0`, \nor undefined."],"actual":[]},"$nm":"sign"},"infinite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinity","finite"],"doc":["Determines whether this value is infinite in \nmagnitude. Produces `true` for `infinity` and \n`-infinity`. Produces `false` for a finite number, \n`+0`, `-0`, or undefined."]},"$nm":"infinite"},"undefined":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines whether this value is undefined (that is, \nNot a Number or NaN). The undefined value has the \nproperty that it is not equal (`==`) to itself, as \na consequence the undefined value cannot sensibly \nbe used in most collections."]},"$nm":"undefined"},"strictlyPositive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a positive number, `+0`, \nor `infinity`. Produces `false` for a negative \nnumber, `-0`, or undefined."]},"$nm":"strictlyPositive"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a negative number or\n`-infinity`. Produces `false` for a positive number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determines if this value is a positive number or\n`infinity`. Produces `false` for a negative number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"positive"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$nm":"float"},"finite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinite","infinity"],"doc":["Determines whether this value is finite. Produces\n`false` for `infinity`, `-infinity`, and undefined."]},"$nm":"finite"}},"$nm":"Float"},"deprecated":{"$t":{"$pk":"ceylon.language","$nm":"Deprecation"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"reason"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark program elements which should not be \nused anymore."]},"$annot":"1","$nm":"deprecated"},"Collection":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["List","Map","Set"],"doc":["Represents an iterable collection of elements of finite \nsize. `Collection` is the abstract supertype of `List`,\n`Map`, and `Set`.\n\nA `Collection` forms a `Category` of its elements.\n\nAll `Collection`s are `Cloneable`. If a collection is\nimmutable, it is acceptable that `clone` produce a\nreference to the collection itself. If a collection is\nmutable, `clone` should produce an immutable collection\ncontaining references to the same elements, with the\nsame structure as the original collection&mdash;that \nis, it should produce an immutable shallow copy of the\ncollection."]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if the given object is an element of\nthis collection. In this default implementation,\nand in most refining implementations, return `false`\notherwise. An acceptable refining implementation\nmay return `true` for objects which are not \nelements of the collection, but this is not \nrecommended. (For example, the `contains()` method \nof `String` returns `true` for any substring of the\nstring.)"],"actual":[]},"$nm":"contains"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determine if the collection is empty, that is, if \nit has no elements."],"actual":[]},"$nm":"empty"}},"$nm":"Collection"},"Range":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"Element"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The start of the range."],"actual":[]},"$nm":"first"},{"$t":{"$nm":"Element"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"doc":["The end of the range."],"actual":[]},"$nm":"last"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"final":[],"doc":["Represents the range of totally ordered, ordinal values \ngenerated by two endpoints of type `Ordinal` and \n`Comparable`. If the first value is smaller than the\nlast value, the range is increasing. If the first value\nis larger than the last value, the range is decreasing.\nIf the two values are equal, the range contains exactly\none element. The range is always nonempty, containing \nat least one value.\n\nA range may be produced using the `..` operator:\n\n    for (i in min..max) { ... }\n    if (char in `A`..`Z`) { ... }\n"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["An iterator for the elements of the range."],"actual":[]},"$c":{"RangeIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"current":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"current"}},"$nm":"RangeIterator"}},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"n"}]],"$mt":"mthd","$an":{"shared":[],"doc":["The element of the range that occurs `n` values after\nthe start of the range. Note that this operation \nis inefficient for large ranges."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$nm":"selecting"}},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if two ranges are the same by comparing\ntheir endpoints."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"next":{"$t":{"$nm":"Element"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$nm":"next"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given object."],"actual":[]},"$nm":"contains"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"includes"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"occurs"},"spanTo":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"containsElement":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given value."]},"$nm":"containsElement"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"span":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the end of the range."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since ranges are \nimmutable."],"actual":[]},"$nm":"clone"},"decreasing":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines if the range is decreasing."]},"$nm":"decreasing"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The end of the range."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this range."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since a Range cannot\ncontain nulls."],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Reverse this range, returning a new range."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"doc":["The rest of the range, without the start of the\nrange."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The start of the range."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The nonzero number of elements in the range."],"actual":[]},"$nm":"size"}},"$nm":"Range"},"max":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","min","largest"],"doc":["Given a nonempty stream of `Comparable` values, \nreturn the largest value in the stream."]},"$nm":"max"},"Integral":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Integral"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of integral numeric types. That is, types \nwith no fractional part, including `Integer`. The \ndivision operation for integral numeric types results \nin a remainder. Therefore, integral numeric types have \nan operation to determine the remainder of any division \noperation."],"by":["Gavin"]},"$m":{"remainder":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Numeric.divided"],"doc":["The remainder, after dividing this number by the \ngiven number."]},"$nm":"remainder"},"divides":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determine if this number is a factor of the given \nnumber."]},"$nm":"divides"}},"$at":{"unit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is one."]},"$nm":"unit"},"zero":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is zero."]},"$nm":"zero"}},"$nm":"Integral","$st":"Other"},"SequenceAppender":{"super":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder"],"doc":["This class is used for constructing a new nonempty \nsequence by incrementally appending elements to an\nexisting nonempty sequence. The existing sequence is\nnot modified, since `Sequence`s are immutable. This \nclass is mutable but threadsafe."]},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting nonempty sequence. If no elements \nhave been appended, the original nonempty \nsequence."],"actual":[]},"$nm":"sequence"},"elements":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$nm":"elements"}},"$nm":"SequenceAppender"},"smallest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","largest","min"],"doc":["Given two `Comparable` values, return smallest of the\ntwo."]},"$nm":"smallest"},"byIncreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byDecreasing"],"doc":["A comparator which orders elements in increasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byIncreasing"},"larger":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is larger than the given value."]},"$nm":"larger"},"true":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing truth in Boolean logic."]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"true"},"join":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"doc":["The iterable objects to join."]},"$nm":"iterables"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["Given a list of iterable objects, return a new sequence \nof all elements of the all given objects. If there are\nno arguments, or if none of the arguments contains any\nelements, return the empty sequence."]},"$nm":"join"},"Exponentiable":{"of":[{"$nm":"This"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"},{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$nm":"This"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types that may be raised to a\npower. Note that the type of the exponent may be\ndifferent to the numeric type which can be \nexponentiated."]},"$m":{"power":{"$t":{"$nm":"This"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of raising this number to the given\npower."]},"$nm":"power"}},"$nm":"Exponentiable","$st":"This"},"Keys":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"prm","$pt":"v","$nm":"correspondence"}],"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"contains"}},"$at":{"correspondence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"attr","$nm":"correspondence"}},"$nm":"Keys"},"Character":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"see":["String"],"doc":["A 32-bit Unicode character."],"by":["Gavin"]},"$at":{"control":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is an ISO control \ncharacter."]},"$nm":"control"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The code point of the character."]},"$nm":"integer"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The lowercase representation of this character."]},"$nm":"lowercased"},"letter":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is a letter. That is,\nif its Unicode general category is *Lu*, *Ll*, \n*Lt*, *Lm*, or *Lo*."]},"$nm":"letter"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The uppercase representation of this character."]},"$nm":"uppercased"},"whitespace":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is a whitespace character. \nThe following characters are whitespace characters:\n\n* *LINE FEED*, `\\n` or `\\{#000A}`,\n* *FORM FEED*, `\\f` or `\\{#000C}`,\n* *CARRIAGE RETURN*, `\\r` or `\\{#000D}`,\n* *HORIZONTAL TABULATION*, `\\t` or `\\{#0009}`,\n* *VERTICAL TABULATION*, `\\{#000B}`,\n* *FILE SEPARATOR*, `\\{#001C}`,\n* *GROUP SEPARATOR*, `\\{#001D}`,\n* *RECORD SEPARATOR*, `\\{#001E}`,\n* *UNIT SEPARATOR*, `\\{#001F}`, and\n* any Unicode character in the general category \n  *Zs*, *Zl*, or *Zp* that is not a non-breaking \n  space."]},"$nm":"whitespace"},"titlecased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The title case representation of this character."]},"$nm":"titlecased"},"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$nm":"character"},"uppercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this is an uppercase representation of\nthe character. That is, if its Unicode general \ncategory is *Lu*."]},"$nm":"uppercase"},"digit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this character is a numeric digit.\nThat is, if its Unicode general category is *Nd*."]},"$nm":"digit"},"lowercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this is a lowercase representation of\nthe character. That is, if its Unicode general \ncategory is *Ll*."]},"$nm":"lowercase"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["A string containg just this character."],"actual":[]},"$nm":"string"},"titlecase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Determine if this is a title case representation of\nthe character. That is, if its Unicode general \ncategory is *Lt*."]},"$nm":"titlecase"}},"$nm":"Character"},"curry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}],[{"$t":{"$nm":"First"},"$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[],"see":["uncurry","compose"],"doc":["Curries a function, returning a function of multiple parameter lists,\ngiven a function of multiple parameters."]},"$nm":"curry"},"forKey":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forItem"],"doc":["A function that returns the result of the given `resulting()` function \non the key of a given `Entry`."]},"$nm":"forKey"},"product":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["sum"],"doc":["Given a nonempty stream of `Numeric` values, return \nthe product of the values."]},"$nm":"product"},"process":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Represents the current process (instance of the virtual\nmachine)."],"by":["Gavin","Tako"]},"$m":{"readLine":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Read a line of input text from the standard input \nof the virtual machine process."]},"$nm":"readLine"},"writeErrorLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print a line to the standard error of the \nvirtual machine process."]},"$nm":"writeErrorLine"},"writeError":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard error of the \nvirtual machine process."]},"$nm":"writeError"},"propertyValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the given system property of the virtual\nmachine, if any."]},"$nm":"propertyValue"},"namedArgumentValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the first argument of form `-name=value`, \n`--name=value`, or `-name value` specified among the \ncommand line arguments to the virtual machine, if\nany."]},"$nm":"namedArgumentValue"},"write":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard output of the \nvirtual machine process."]},"$nm":"write"},"namedArgumentPresent":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determine if an argument of form `-name` or `--name` \nwas specified among the command line arguments to \nthe virtual machine."]},"$nm":"namedArgumentPresent"},"writeLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["print"],"doc":["Print a line to the standard output of the \nvirtual machine process."]},"$nm":"writeLine"},"exit":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"code"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"exit"}},"$at":{"os":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the operating system this \nprocess is running on."]},"$nm":"os"},"vmVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the virtual machine this \nprocess is running on."]},"$nm":"vmVersion"},"vm":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the virtual machine this \nprocess is running on."]},"$nm":"vm"},"osVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the operating system this \nprocess is running on."]},"$nm":"osVersion"},"newline":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The line ending character sequence on this platform."]},"$nm":"newline"},"arguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The command line arguments to the virtual machine."]},"$nm":"arguments"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"locale":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the IETF language tag representing the\ndefault locale for this virtual machine."]},"$nm":"locale"},"nanoseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in nanoseconds since an arbitrary\nstarting point."]},"$nm":"nanoseconds"},"timezoneOffset":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the offset from UTC, in milliseconds, of\nthe default timezone for this virtual machine."]},"$nm":"timezoneOffset"},"milliseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in milliseconds since midnight, \n1 January 1970."]},"$nm":"milliseconds"}},"$nm":"process"},"forItem":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forKey"],"doc":["A function that returns the result of the given `resulting()` function \non the item of a given `Entry`."]},"$nm":"forItem"},"License":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"url"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"License"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Module"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[license]]."]},"$annot":"1","$at":{"url":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"url"}},"$nm":"License"},"shuffle":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"FirstArgs"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"SecondArgs"}],"$an":{"shared":[]},"$nm":"shuffle"},"Annotation":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Annotation"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[annotation]]."]},"$annot":"1","$nm":"Annotation"},"nothing":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"doc":["A value that is assignable to any type, but that \nresults in an exception when evaluated. This is most \nuseful for generating members in an IDE."]},"$nm":"nothing"},"doc":{"$t":{"$pk":"ceylon.language","$nm":"Doc"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API documentation of a program\nelement."]},"$annot":"1","$nm":"doc"},"Scalar":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$pk":"ceylon.language","$nm":"Number"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Scalar"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types representing scalar\nvalues, including `Integer` and `Float`."],"by":["Gavin"]},"$at":{"magnitude":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of this number."],"actual":[]},"$nm":"magnitude"},"wholePart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part. For integral numeric types,\nthe integral value of a number is the number \nitself."],"actual":[]},"$nm":"wholePart"},"fractionalPart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part. For integral numeric types,\nthe fractional part is always zero."],"actual":[]},"$nm":"fractionalPart"}},"$nm":"Scalar","$st":"Other"},"SequenceBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceAppender","join","Singleton"],"doc":["Since sequences are immutable, this class is used for\nconstructing a new sequence by incrementally appending \nelements to the empty sequence. This class is mutable\nbut threadsafe."]},"$m":{"append":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append an element to the sequence and return this \n`SequenceBuilder`"]},"$nm":"append"},"appendAll":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Append multiple elements to the sequence and return \nthis `SequenceBuilder`"]},"$nm":"appendAll"}},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"native":[],"doc":["The resulting sequence. If no elements have been\nappended, the empty sequence."]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determine if the resulting sequence is empty."]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The size of the resulting sequence."]},"$nm":"size"}},"$nm":"SequenceBuilder"},"printAll":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["A character sequence to use to separate the values"]},"$nm":"separator"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print multiple values to the standard output of the virtual \nmachine process as a single line of text, separated by a\ngiven character sequence."],"by":["Gavin"]},"$nm":"printAll"},"ifExists":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"prm","$pt":"f","$nm":"predicate"}],[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$m":{"predicate":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$nm":"predicate"}},"$nm":"ifExists"},"tagged":{"$t":{"$pk":"ceylon.language","$nm":"Tags"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to categorize the API by tag."]},"$annot":"1","$nm":"tagged"},"variable":{"$t":{"$pk":"ceylon.language","$nm":"Variable"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark an value as variable. A `variable` value \nmay be assigned multiple times."]},"$annot":"1","$nm":"variable"},"Abstract":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Abstract"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[abstract]]."]},"$annot":"1","$nm":"Abstract"},"Correspondence":{"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Map","List","Category"],"doc":["Abstract supertype of objects which associate values \nwith keys. `Correspondence` does not satisfy `Category`,\nsince in some cases&mdash;`List`, for example&mdash;it is \nconvenient to consider the subtype a `Category` of its\nvalues, and in other cases&mdash;`Map`, for example&mdash;it \nis convenient to treat the subtype as a `Category` of its\nentries.\n\nThe item corresponding to a given key may be obtained \nfrom a `Correspondence` using the item operator:\n\n    value bg = settings[\"backgroundColor\"] else white;\n\nThe `get()` operation and item operator result in an\noptional type, to reflect the possibility that there is\nno item for the given key."],"by":["Gavin"]},"$m":{"definesAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.defines"],"doc":["Determines if this `Correspondence` defines a value\nfor any one of the given keys."]},"$nm":"definesAny"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.definesAny","Correspondence.definesEvery","Correspondence.keys"],"doc":["Determines if there is a value defined for the \ngiven key."]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Correspondence.items"],"doc":["Returns the value defined for the given key, or \n`null` if there is no value defined for the given \nkey."]},"$nm":"get"},"items":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.get"],"doc":["Returns the items defined for the given keys, in\nthe same order as the corresponding keys."]},"$nm":"items"},"definesEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["Correspondence.defines"],"doc":["Determines if this `Correspondence` defines a value\nfor every one of the given keys."]},"$nm":"definesEvery"}},"$at":{"keys":{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["Correspondence.defines"],"doc":["The `Category` of all keys for which a value is \ndefined by this `Correspondence`."]},"$nm":"keys"}},"$nm":"Correspondence"},"NonemptyContainer":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["A nonempty container."]},"$alias":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"NonemptyContainer"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A count of the number of `true` items in the given values."]},"$nm":"count"},"byItem":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"see":["byKey"],"doc":["A comparator for `Entry`s which compares their items \naccording to the given `comparing()` function."]},"$nm":"byItem"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Authors"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API authors."]},"$annot":"1","$nm":"by"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["any"],"doc":["Determines if every one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"every"},"$pkg-shared":"1","Tuple":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$nm":"First"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"actual":[]},"$nm":"first"},{"$t":{"$nm":"Rest"},"$hdn":"1","$mt":"prm","$pt":"v","$an":{"shared":[],"actual":[]},"$nm":"rest"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$nm":"Element"}],"variance":"out","$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language","$nm":"Empty"},"variance":"out","$nm":"Rest"}],"$an":{"shared":[],"final":[],"doc":["A _tuple_ is a typed linked list. Each instance of \n`Tuple` represents the value and type of a single link.\nThe attributes `first` and `rest` allow us to retrieve\na value form the list without losing its static type \ninformation.\n\n    value point = Tuple(0.0, Tuple(0.0, Tuple(\"origin\")));\n    Float x = point.first;\n    Float y = point.rest.first;\n    String label = point.rest.rest.first;\n\nUsually, we abbreviate code involving tuples.\n\n    [Float,Float,String] point = [0.0, 0.0, \"origin\"];\n    Float x = point[0];\n    Float y = point[1];\n    String label = point[2];\n\nA list of types enclosed in brackets is an abbreviated \ntuple type. An instance of `Tuple` may be constructed \nby surrounding a value list in brackets:\n\n    [String,String] words = [\"hello\", \"world\"];\n\nThe index operator with a literal integer argument is a \nshortcut for a chain of evaluations of `rest` and \n`first`. For example, `point[1]` means `point.rest.first`.\n\nA _terminated_ tuple type is a tuple where the type of\nthe last link in the chain is `Empty`. An _unterminated_ \ntuple type is a tuple where the type of the last link\nin the chain is `Sequence` or `Sequential`. Thus, a \nterminated tuple type has a length that is known\nstatically. For an unterminated tuple type only a lower\nbound on its length is known statically.\n\nHere, `point` is an unterminated tuple:\n\n    String[] labels = ... ;\n    [Float,Float,String*] point = [0.0, 0.0, *labels];\n    Float x = point[0];\n    Float y = point[1];\n    String? firstLabel = point[2];\n    String[] allLabels = point[2...];"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"current":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"current"}},"$nm":"iterator"}}},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"contains"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$an":{"doc":["The first element of the resulting tuple."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"doc":["Returns a new tuple that starts with the specified\nelement, followed by the elements of this tuple."],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"end"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$nm":"Rest"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"First"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"Tuple"},"Final":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Final"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[final]]."]},"$annot":"1","$nm":"Final"},"lessThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is less than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"lessThan"},"maxRadix":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"maxRadix"},"identityHash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["identical"],"doc":["Return the system-defined identity hash value of the \ngiven value. This hash value is consistent with \nidentity equality."]},"$nm":"identityHash"},"uncurry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$nm":"First"},"$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"prm","$pt":"f","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[],"see":["curry","compose"],"doc":["Uncurries a function, returning a function with multiple parameters, \ngiven a function with multiple parameter lists."]},"$nm":"uncurry"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["every"],"doc":["Determines if any one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"any"},"optional":{"$t":{"$pk":"ceylon.language","$nm":"Optional"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify that a module can be executed even if \nthe annotated dependency is not available."]},"$annot":"1","$nm":"optional"},"Summable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Other"}],"$an":{"shared":[],"see":["String","Numeric"],"doc":["Abstraction of types which support a binary addition\noperator. For numeric types, this is just familiar \nnumeric addition. For strings, it is string \nconcatenation. In general, the addition operation \nshould be a binary associative operation."],"by":["Gavin"]},"$m":{"plus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of adding the given value to this value. \nThis operation should never perform any kind of \nmutation upon either the receiving value or the \nargument value."]},"$nm":"plus"}},"$nm":"Summable","$st":"Other"},"EmptyContainer":{"$mt":"ifc","$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["An empty container."]},"$alias":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"EmptyContainer"},"Set":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A collection of unique elements.\n\nA `Set` is a `Collection` of its elements.\n\nSets may be the subject of the binary union, \nintersection, and complement operators `|`, `&`, and \n`~`."]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements in \nthis `Set` that are not contained in the given\n`Set`."]},"$nm":"complement"},"subset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a subset of the given \n`Set`, that is, if the given set contains all of \nthe elements in this set."]},"$nm":"subset"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \nthat are present in both this `Set` and the given \n`Set`."]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \ncontained in either this `Set` or the given `Set`, \nbut no element contained in both sets."]},"$nm":"exclusiveUnion"},"superset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a superset of the \nspecified Set, that is, if this `Set` contains all \nof the elements in the specified `Set`."]},"$nm":"superset"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Set`s are considered equal if they have the \nsame size and if every element of the first set is\nalso an element of the second set, as determined\nby `contains()`."],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements of \nthis `Set` and all the elements of the given `Set`."]},"$nm":"union"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Set"},"Sequence":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Empty"],"doc":["A nonempty, immutable sequence of values. The type \n`Sequence<Element>`, may be abbreviated `[Element+]`.\n\nGiven a possibly-empty sequence of type `[Element*]`, \nthe `if (nonempty ...)` construct, or, alternatively,\nthe `assert (nonempty ...)` construct, may be used to \nnarrow to a nonempty sequence type:\n\n    [Integer*] nums = ... ;\n    if (nonempty nums) {\n        Integer first = nums.first;\n        Integer max = max(nums);\n        [Integer+] squares = nums.collect((Integer i) i**2));\n        [Integer+] sorted = nums.sort(byIncreasing((Integer i) i));\n    }\n\nOperations like `first`, `max()`, `collect()`, and \n`sort()`, which polymorphically produce a nonempty or \nnon-null output when given a nonempty input are called \n_emptiness-preserving_."],"by":["Gavin"]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The function comparing pairs of elements."]},"$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements."],"actual":[]},"$m":{"comparing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$an":{"doc":["The function comparing pairs of elements."]},"$nm":"comparing"}},"$nm":"sort"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"shorterThan"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"longerThan"},"repeat":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"repeat"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The transformation applied to the elements."]},"$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the results of \napplying the given mapping to the elements of this\nsequence."],"actual":[]},"$m":{"collecting":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"doc":["The transformation applied to the elements."]},"$nm":"collecting"}},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["Sequence.size"],"doc":["The index of the last element of the sequence."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element of the sequence, that is, the\n    element with index `sequence.lastIndex`."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `false`, since every `Sequence` contains at\n    least one element."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \n    element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new nonempty\n    sequence."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element of the sequence, that is, the\n    element with index `0`."],"actual":[]},"$nm":"first"}},"$nm":"Sequence"},"Scalable":{"of":[{"$nm":"Value"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Scale"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Scale"},{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Scalable"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["Abstract supertype of types that support scaling by\na numeric factor. Implementations should generally\nrespect the following constraints, where relevant:\n\n- `x == 1**x`\n- `-x == -1**x`\n- `x-x == 0**x`\n- `x+x == 2**x`"]},"$m":{"scale":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Scale"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"scale"}},"$nm":"Scalable","$st":"Value"},"InitializationException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}],"$mt":"cls","$an":{"shared":[],"see":["late"],"doc":["Thrown when a problem was detected with value initialization.\n\nPossible problems include:\n\n* when a value could not be initialized due to recursive access during initialization, \n* an attempt to use a `late` value before it was initialized, \n* an attempt to assign to a `late` but non-`variable` value after it was initialized."]},"$at":{"description":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$nm":"description"}},"$nm":"InitializationException"},"StringBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"shared":[],"native":[],"doc":["Since strings are immutable, this class is used for\nconstructing a string by incrementally appending \ncharacters to the empty string. This class is mutable \nbut threadsafe."]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append the characters in the given string."]},"$nm":"append"},"appendSpace":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a space character."]},"$nm":"appendSpace"},"delete":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Deletes the specified number of characters from the\ncurrent content, starting at the specified position.\nIf the position is beyond the end of the current \ncontent, nothing is deleted. If the number of \ncharacters to delete is greater than the available \ncharacters from the given position, the content is \ntruncated at the given position."]},"$nm":"delete"},"reset":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Remove all content and return to initial state."]},"$nm":"reset"},"insertCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a `Character` at the specified position. If \nthe position is beyond the end of the current string, \nthe new content is simply appended to the current \ncontent. If the position is a negative number, the \nnew content is inserted at index 0."]},"$nm":"insertCharacter"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the characters in the given strings."]},"$nm":"appendAll"},"insert":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a `String` at the specified position. If the \nposition is beyond the end of the current string, \n    the new content is simply appended to the current \n    content. If the position is a negative number, the \n    new content is inserted at index 0."]},"$nm":"insert"},"appendNewline":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a newline character."]},"$nm":"appendNewline"},"appendCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the given character."]},"$nm":"appendCharacter"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting string. If no characters have been\nappended, the empty string."],"actual":[]},"$nm":"string"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the size of the current content."]},"$nm":"size"}},"$nm":"StringBuilder"},"emptyOrSingleton":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Singleton","Empty"],"doc":["A `Singleton` if the given element is non-null, otherwise `Empty`."]},"$nm":"emptyOrSingleton"},"shared":{"$t":{"$pk":"ceylon.language","$nm":"Shared"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a type or member as shared. A `shared` \nmember is visible outside the block of code in which it is \ndeclared."]},"$annot":"1","$nm":"shared"},"Actual":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Actual"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[actual]]."]},"$annot":"1","$nm":"Actual"},"Binary":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Binary"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of numeric types like `Integer` that may be \nrepresented as a sequence of bits, and may be the subject\nof bitwise operations."],"by":["Stef"]},"$m":{"clear":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Returns a new number with the given bit set to 0.\nBits are indexed from right to left."]},"$nm":"clear"},"xor":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical exclusive OR operation."]},"$nm":"xor"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Retrieves a given bit from this bit sequence. Bits are indexed from\nright to left."]},"$nm":"get"},"leftLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a left logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"leftLogicalShift"},"set":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"bit"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit set to the given value.\nBits are indexed from right to left."]},"$nm":"set"},"or":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical inclusive OR operation."]},"$nm":"or"},"rightArithmeticShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right arithmetic shift. Sign is preserved. Padded with zeros."]},"$nm":"rightArithmeticShift"},"rightLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"rightLogicalShift"},"flip":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit flipped to its opposite value.\nBits are indexed from right to left."]},"$nm":"flip"},"and":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical AND operation."]},"$nm":"and"}},"$at":{"not":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The binary complement of this sequence of bits."]},"$nm":"not"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The number of bits (0 or 1) that this sequence of bits can hold."]},"$nm":"size"}},"$nm":"Binary","$st":"Other"},"commaList":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$nm":"commaList"},"Number":{"$mt":"ifc","$an":{"shared":[],"see":["Numeric"],"doc":["Abstraction of numbers. Numeric operations are provided\nby the subtype `Numeric`. This type defines operations\nwhich can be expressed without reference to the self\ntype `Other` of `Numeric`."],"by":["Gavin"]},"$at":{"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this number. Returns `1` if the number \nis positive, `-1` if it is negative, or `0` if it \nis zero."]},"$nm":"sign"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas an `Integer`"],"doc":["The number, represented as an `Integer`, after \ntruncation of any fractional part."]},"$nm":"integer"},"magnitude":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of the number."]},"$nm":"magnitude"},"wholePart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part."]},"$nm":"wholePart"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is negative."]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is positive."]},"$nm":"positive"},"fractionalPart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part."]},"$nm":"fractionalPart"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas a `Float`"],"doc":["The number, represented as a `Float`."]},"$nm":"float"}},"$nm":"Number"},"Map":{"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Entry","forKey","forItem","byItem","byKey"],"doc":["Represents a collection which maps _keys_ to _items_,\nwhere a key can map to at most one item. Each such \nmapping may be represented by an `Entry`.\n\nA `Map` is a `Collection` of its `Entry`s, and a \n`Correspondence` from keys to items.\n\nThe presence of an entry in a map may be tested\nusing the `in` operator:\n\n    if (\"lang\"->\"en_AU\" in settings) { ... }\n\nThe entries of the map may be iterated using `for`:\n\n    for (key->item in settings) { ... }\n\nThe item for a key may be obtained using the item\noperator:\n\n    String lang = settings[\"lang\"] else \"en_US\";"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Map`s are considered equal iff they have the \nsame _entry sets_. The entry set of a `Map` is the\nset of `Entry`s belonging to the map. Therefore, the\nmaps are equal iff they have same set of `keys`, and \nfor every key in the key set, the maps have equal\nitems."],"actual":[]},"$nm":"equals"},"mapItems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Key"},"$mt":"prm","$pt":"v","$nm":"key"},{"$t":{"$nm":"Item"},"$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The function that transforms a key\/item\npair, producing the item of the resulting\nmap."]},"$nm":"mapping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["Returns a `Map` with the same keys as this map. For\nevery key, the item is the result of applying the\ngiven transformation function."]},"$nm":"mapItems"}},"$at":{"values":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Collection"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns all the values stored in this `Map`. An \nelement can be stored under more than one key in \nthe map, and so it can be contained more than once \nin the resulting collection."]},"$nm":"values"},"inverse":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns a `Map` in which every key is an `Item` in \nthis map, and every value is the set of keys that \nstored the `Item` in this map."]},"$nm":"inverse"},"keys":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the set of keys contained in this `Map`."],"actual":[]},"$nm":"keys"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Map"},"LazyMap":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"entries"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A `Map` implementation that wraps an `Iterable` of \nentries. All operations, such as lookups, size, etc. \nare performed on the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"LazyMap"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"entries"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazyMap"},"Numeric":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Invertable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float","Comparable"],"doc":["Abstraction of numeric types supporting addition,\nsubtraction, multiplication, and division, including\n`Integer` and `Float`. Additionally, a numeric type \nis expected to define a total order via an \nimplementation of `Comparable`."],"by":["Gavin"]},"$m":{"minus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The difference between this number and the given \nnumber."]},"$nm":"minus"},"times":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The product of this number and the given number."]},"$nm":"times"},"divided":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Integral"],"doc":["The quotient obtained by dividing this number by \nthe given number. For integral numeric types, this \noperation results in a remainder."]},"$nm":"divided"}},"$nm":"Numeric","$st":"Other"},"throws":{"$t":{"$pk":"ceylon.language","$nm":"ThrownException"},"$ps":[[{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"when"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a program element that throws an \nexception."]},"$annot":"1","$nm":"throws"},"Closeable":{"$mt":"ifc","$an":{"shared":[],"doc":["Abstract supertype of types which may appear\nas the expression type of a resource expression\nin a `try` statement."]},"$m":{"open":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called before entry to a `try` block."]},"$nm":"open"},"close":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called after completion of a `try` block."]},"$nm":"close"}},"$nm":"Closeable"},"parseDigit":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"digit"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"radix"}]],"$mt":"mthd","$nm":"parseDigit"},"Tags":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"shared":[]},"$nm":"tags"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Tags"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[tagged]]."]},"$annot":"1","$at":{"tags":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[]},"$nm":"tags"}},"$nm":"Tags"},"formal":{"$t":{"$pk":"ceylon.language","$nm":"Formal"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation must be \nprovided by subtypes."]},"$annot":"1","$nm":"formal"},"byDecreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byIncreasing"],"doc":["A comparator which orders elements in decreasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byDecreasing"},"default":{"$t":{"$pk":"ceylon.language","$nm":"Default"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation may be \nrefined by subtypes. Non-`default` declarations may not be \nrefined."]},"$annot":"1","$nm":"default"},"minRadix":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"minRadix"},"String":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"characters"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"doc":["A string of characters. Each character in the string is\na 32-bit Unicode character. The internal UTF-16 encoding \nis hidden from clients.\n\nA string is a `Category` of its `Character`s, and of its \nsubstrings:\n\n    'w' in greeting \n    \"hello\" in greeting\n\nStrings are summable:\n\n    String greeting = \"hello\" + \" \" + \"world\";\n\nThey are efficiently iterable:\n\n  for (char in \"hello world\") { ... }\n\nThey are `List`s of `Character`s:\n\n    value char = \"hello world\"[5];\n\nThey are ranged:\n\n    String who = \"hello world\"[6...];\n\nNote that since `string[index]` evaluates to the\noptional type `Character?`, it is often more convenient\nto write `string[index..index]`, which evaluates to a\n`String` containing a single character, or to the empty\nstring `\"\"` if `index` refers to a position outside the\nstring.\n\nIt is easy to use comprehensions to transform strings:\n\n    String { for (s in \"hello world\") if (s.letter) s.uppercased }\n\nSince a `String` has an underlying UTF-16 encoding, \ncertain operations are expensive, requiring iteration of \nthe characters of the string. In particular, `size`\nrequires iteration of the whole string, and `get()`,\n`span()`, and `segment()` require iteration from the \nbeginning of the string to the given index."],"by":["Gavin"]},"$m":{"plus":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns the concatenation of this string with the\ngiven string."],"actual":[]},"$nm":"plus"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["An iterator for the characters of the string."],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns the character at the given index in the \nstring, or `null` if the index is past the end of\nstring. The first character in the string occurs at\nindex zero. The last character in the string occurs\nat index `string.size-1`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determines if the given object is a string, and if\nso, if this string has the same length, and the \nsame characters, in the same order, as the given \nstring."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the characters of this string beginning at \nthe given index, returning a string no longer than \nthe given length. If the portion of this string\nstarting at the given index is shorter than \nthe given length, return the portion of this string\nfrom the given index until the end of this string. \nOtherwise return a string of the given length. If \nthe start index is larger than the last index of the \nstring, return the empty string."],"actual":[]},"$nm":"segment"},"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Compare this string with the given string \nlexicographically, according to the Unicode values\nof the characters."],"actual":[]},"$nm":"compare"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["size"],"doc":["Determines if this string is longer than the given\nlength. This is a more efficient operation than\n`string.size>length`."],"actual":[]},"$nm":"longerThan"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determines if the given object is a `String` and, \nif so, if it occurs as a substring of this string,\nor if the object is a `Character` that occurs in\nthis string. That is to say, a string is considered \na `Category` of its substrings and of its \ncharacters."],"actual":[]},"$nm":"contains"},"trimLeading":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["This string, after discarding the given \ncharacters from the beginning of the string"],"actual":[]},"$nm":"trimLeading"},"repeat":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns a string formed by repeating this string\nthe given number of times, or the empty string if\n`times<=0`."],"actual":[]},"$nm":"repeat"},"join":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Join the given strings, using this string as a \nseparator."]},"$nm":"join"},"replace":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"replacement"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Returns a string formed by replacing every \noccurrence in this string of the given substring\nwith the given replacement string, working from \nthe start of this string to the end."]},"$nm":"replace"},"terminal":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the last characters of the string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."],"actual":[]},"$nm":"terminal"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["size"],"doc":["Determines if this string is shorter than the given\nlength. This is a more efficient operation than\n`string.size>length`."],"actual":[]},"$nm":"shorterThan"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"trimTrailing":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["This string, after discarding the given \ncharacters from the end of the string"],"actual":[]},"$nm":"trimTrailing"},"initial":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the first characters of this string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."],"actual":[]},"$nm":"initial"},"split":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"ch"}]],"$mt":"prm","$pt":"f","$def":"1","$an":{"doc":["A predicate that determines if a character\nis a separator characters at which to split.\nDefault to split at any \n[[whitespace|Character.whitespace]] character."]},"$nm":"splitting"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["Specifies that the separator characters\noccurring in the string should be discarded.\nIf `false`, they will be included in the\nresulting iterator."]},"$nm":"discardSeparators"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["Specifies that the separator tokens should \nbe grouped eagerly and not be treated as \nsingle-character tokens. If `false` each \nseparator token will be of size `1`."]},"$nm":"groupSeparators"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Split the string into tokens, using the given\npredicate to determine which characters are \nseparator characters."]},"$nm":"split"},"trim":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["This string, after discarding the given \ncharacters from the beginning and end \nof the string"],"actual":[]},"$nm":"trim"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Select the characters between the given indexes.\nIf the start index is the same as the end index,\nreturn a string with a single character.\nIf the start index is larger than the end index, \nreturn the characters in the reverse order from\nthe order in which they appear in this string.\nIf both the start index and the end index are \nlarger than the last index in the string, return \nthe empty string. Otherwise, if the last index is \nlarger than the last index in the sequence, return\nall characters from the start index to last \ncharacter of the string."],"actual":[]},"$nm":"span"}},"$at":{"normalized":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, after collapsing strings of \n[[whitespace|Character.whitespace]]\ninto single space characters and discarding whitespace \nfrom the beginning and end of the string."]},"$nm":"normalized"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, with all characters in lowercase."]},"$nm":"lowercased"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"hash"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, with all characters in uppercase."]},"$nm":"uppercased"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this string."],"actual":[]},"$nm":"coalesced"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"see":["longerThan","shorterThan"],"doc":["The length of the string (the number of characters\nit contains). In the case of the empty string, the\nstring has length zero. Note that this operation is\npotentially costly for long strings, since the\nunderlying representation of the characters uses a\nUTF-16 encoding."],"actual":[]},"$nm":"size"},"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the last character in the string, or\n`null` if the string has no characters. Note that \nthis operation is potentially costly for long \nstrings, since the underlying representation of the \ncharacters uses a UTF-16 encoding."],"actual":[]},"$nm":"lastIndex"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the string itself."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"native":[],"see":["size"],"doc":["Determines if this string has no characters, that\nis, if it has zero `size`. This is a more efficient \noperation than `string.size==0`."],"actual":[]},"$nm":"empty"},"lines":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"native":[],"doc":["Split the string into lines of text."]},"$nm":"lines"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The rest of the string, without the first element."],"actual":[]},"$nm":"rest"},"trimmed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"native":[],"doc":["This string, after discarding \n[[whitespace|Character.whitespace]] from the \nbeginning and end of the string."]},"$nm":"trimmed"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["This string, with the characters in reverse order."],"actual":[]},"$nm":"reversed"},"characters":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[]},"$nm":"characters"}},"$nm":"String"},"identical":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$an":{"doc":["An object with well-defined identity."]},"$nm":"x"},{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$an":{"doc":["A second object with well-defined identity."]},"$nm":"y"}]],"$mt":"mthd","$an":{"shared":[],"see":["identityHash"],"doc":["Determine if the arguments are identical. Equivalent to\n`x===y`. Only instances of `Identifiable` have \nwell-defined identity."]},"$nm":"identical"},"late":{"$t":{"$pk":"ceylon.language","$nm":"Late"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to disable definite initialization analysis for \na reference."]},"$annot":"1","$nm":"late"},"integerRangeByIterable":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"prm","$pt":"v","$nm":"range"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"native":[],"doc":["Provides an optimized implementation of `Range<Integer>.iterator`. \nThis is necessary because we need reified generics in order to write \nthe optimized version in pure Ceylon."]},"$nm":"integerRangeByIterable"},"emptyIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$an":{"shared":[],"doc":["An iterator that returns no elements."]},"$m":{"next":{"$t":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$nm":"emptyIterator"},"Anything":{"abstract":"1","of":[{"$pk":"ceylon.language","$nm":"Object"},{"$pk":"ceylon.language","$nm":"Null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The abstract supertype of all types. A value of type \n`Anything` may be a definite value of type `Object`, or \nit may be the `null` value. A method declared `void` is \nconsidered to have the return type `Anything`.\n\nNote that the type `Nothing`, representing the \nintersection of all types, is a subtype of all types."],"by":["Gavin"]},"$nm":"Anything"},"parseFloat":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Float"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Float` value of the given string representation of \na decimal number or `null` if the string does not \nrepresent a decimal number.\n\nThe syntax accepted by this method is the same as the \nsyntax for a `Float` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`)."]},"$nm":"parseFloat"},"Optional":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Optional"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Import"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[optional]]."]},"$annot":"1","$nm":"Optional"},"Exception":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$an":{"doc":["A description of the problem."]},"$nm":"description"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$an":{"shared":[],"doc":["The underlying cause of this exception."]},"$nm":"cause"}],"$mt":"cls","$an":{"shared":[],"native":[],"doc":["The supertype of all exceptions. A subclass represents\na more specific kind of problem, and may define \nadditional attributes which propagate information about\nproblems of that kind."],"by":["Gavin","Tom"]},"$m":{"printStackTrace":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print the stack trace to the standard error of\nthe virtual machine process."]},"$nm":"printStackTrace"}},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["cause"],"doc":["A message describing the problem. This default \nimplementation returns the description, if any, or \notherwise the message of the cause, if any."]},"$nm":"message"},"cause":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"attr","$an":{"shared":[],"doc":["The underlying cause of this exception."]},"$nm":"cause"},"description":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"attr","$an":{"doc":["A description of the problem."]},"$nm":"description"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"}},"$nm":"Exception"},"Doc":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"description"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Doc"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for the [[doc]] annotation."]},"$annot":"1","$at":{"description":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"description"}},"$nm":"Doc"},"internalSort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"native":[]},"$nm":"internalSort"},"OverflowException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a mathematical operation caused a number to \noverflow from its bounds."]},"$nm":"OverflowException"},"Late":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Late"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[late]]."]},"$annot":"1","$nm":"Late"},"parseInteger":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$nm":"radix"}]],"$mt":"mthd","$an":{"shared":[],"throws":["AssertionException","if `radix` is not between `minRadix` and `maxRadix`"],"doc":["The `Integer` value of the given string representation \nof an integer, or `null` if the string does not represent \nan integer or if the mathematical integer it represents \nis too large in magnitude to be represented by an \n`Integer`.\n\nThe syntax accepted by this function is the same as the \nsyntax for an `Integer` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`).\n\nA radix can be given in input to specify what is the base\nto take in consideration for the parsing. radix has to be\nbetween `minRadix` and `maxRadix` included.\nThe list of available digits starts from `0` to `9` followed\nby `a` to `z`.\nWhen parsing in a specific base, the first `radix` digits\nfrom the available digits list can be used.\nThis function is not case sensitive; `a` and `A` both\ncorrespond to the `a` digit which decimal value is `10`.\n \n`_` character can be used to separate groups of digits\nfor bases 2, 10 and 16 as for `Integer` literal in the\nCeylon language. For any other bases, no grouping is\nsupported."]},"$nm":"parseInteger"},"sum":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["product"],"doc":["Given a nonempty stream of `Summable` values, return \nthe sum of the values."]},"$nm":"sum"},"Authors":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"shared":[]},"$nm":"authors"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Authors"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[by]]."]},"$annot":"1","$at":{"authors":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[]},"$nm":"authors"}},"$nm":"Authors"},"smaller":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is smaller than the given value."]},"$nm":"smaller"},"infinity":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"doc":["An instance of `Float` representing positive infinity \n∞."]},"$nm":"infinity"},"computeDigitGroupingSize":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"radix"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"digitIndex"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"ii"}]],"$mt":"mthd","$nm":"computeDigitGroupingSize"},"Iterable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"see":["Collection"],"doc":["Abstract supertype of containers whose elements may be \niterated. An iterable container need not be finite, but\nits elements must at least be countable. There may not\nbe a well-defined iteration order, and so the order of\niterated elements may not be stable.\n\nThe type `Iterable<Element,Null>`, usually abbreviated\n`{Element*}` represents a possibly-empty iterable \ncontainer. The type `Iterable<Element,Nothing>`, \nusually abbreviated `{Element+}` represents a nonempty \niterable container.\n\nAn instance of `Iterable` may be constructed by \nsurrounding a value list in braces:\n\n    {String+} words = { \"hello\", \"world\" };\n\nAn instance of `Iterable` may be iterated using a `for`\nloop:\n\n    for (c in \"hello world\") { ... }\n\n`Iterable` and its subtypes define various operations\nthat return other iterable objects. Such operations \ncome in two flavors:\n\n- _Lazy_ operations return a *view* of the receiving\n  iterable object. If the underlying iterable object is\n  mutable, then changes to the underlying object will\n  be reflected in the resulting view. Lazy operations\n  are usually efficient, avoiding memory allocation or\n  iteration of the receiving iterable object.\n- _Eager_ operations return an immutable object. If the\n  receiving iterable object is mutable, changes to this\n  object will not be reflected in the resulting \n  immutable object. Eager operations are often \n  expensive, involving memory allocation and iteration\n  of the receiving iterable object.\n\nLazy operations are preferred, because they can be \nefficiently chained. For example:\n\n    string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased)\n\nis much less expensive than:\n\n    string.select((Character c) => c.letter)\n          .collect((Character c) => c.uppercased)\n\nFurthermore, it is always easy to produce a new \nimmutable iterable object given the view produced by a\nlazy operation. For example:\n\n    [ *string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased) ]\n\nLazy operations normally return an instance of \n`Iterable` or `Map`.\n\nHowever, there are certain scenarios where an eager \noperation is more useful, more convenient, or no more \nexpensive than a lazy operation, including:\n\n- sorting operations, which are eager by nature,\n- operations which preserve emptiness\/nonemptiness of\n  the receiving iterable object.\n\nEager operations normally return a sequence."],"by":["Gavin"]},"$m":{"cycle":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["cycled","repeat"],"doc":["A finite iterable object that produces the elements of \nthis iterable object, repeatedly, the given number of\ntimes."]},"$nm":"cycle","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"count"},"iter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"iter"}},"$nm":"iterator"}}}},"$at":{"orig":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$nm":"orig"}},"$nm":"iterable"}}},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["An iterator for the elements belonging to this \ncontainer."]},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The function comparing pairs of elements."]},"$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["byIncreasing","byDecreasing"],"doc":["A sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\n\nFor convenience, the functions `byIncreasing()` \nand `byDecreasing()` produce a suitable \ncomparison function:\n\n    \"Hello World!\".sort(byIncreasing((Character c) => c.lowercased))\n\nThis operation is eager by nature."]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate satisfied by the elements to\nbe counted."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return the number of elements in this `Iterable` \nthat satisfy the predicate function."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"doc":["The predicate satisfied by the elements to\nbe counted."]},"$nm":"selecting"}},"$nm":"count"},"takingWhile":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"take":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"take"}},"$nm":"takingWhile","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"alive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"alive"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"select":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the elements must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["filter"],"doc":["A sequence containing the elements of this \ncontainer that satisfy the given predicate. An \neager counterpart to `filter()`."]},"$nm":"select"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["size"],"doc":["Determines if this iterable object has more elements\nthan the given length. This is an efficient operation \nfor iterable objects with many elements."]},"$nm":"longerThan"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"throws":["AssertionException","if the given step size is nonpositive, \ni.e. `step<1`"],"doc":["Produce an `Iterable` containing every `step`th \nelement of this iterable object. If the step size \nis `1`, the `Iterable` contains the same elements \nas this iterable object. The step size must be \ngreater than zero. The expression\n\n    (0..10).by(3)\n\nresults in an iterable object with the elements\n`0`, `3`, `6`, and `9` in that order."]},"$nm":"by","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"repeat":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["cycle"],"doc":["Returns a list formed by repeating the elements of this\niterable object the given number of times, or an empty \nlist if `times<=0`. An eager counterpart to `cycle()`."]},"$nm":"repeat"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate that all elements must \nsatisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if all elements satisfy the predicate\nfunction."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"mthd","$an":{"doc":["The predicate that all elements must \nsatisfy."]},"$nm":"selecting"}},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"initial"},{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The accumulating function that accepts an\nintermediate result, and the next element."]},"$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["The result of applying the accumulating function to \neach element of this container in turn."]},"$m":{"accumulating":{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Result"},"$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$an":{"doc":["The accumulating function that accepts an\nintermediate result, and the next element."]},"$nm":"accumulating"}},"$nm":"fold"},"defaultNullElements":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"comp":"i","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$nm":"Default"}]},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Default"},"$mt":"prm","$pt":"v","$an":{"doc":["A default value that replaces `null` elements."]},"$nm":"defaultValue"}]],"$mt":"mthd","$tp":[{"$nm":"Default"}],"$an":{"shared":[],"default":[],"doc":["An `Iterable` that produces the elements of this \niterable object, replacing every `null` element \nwith the given default value. The resulting iterable\nobject does not produce the value `null`."]},"$nm":"defaultNullElements"},"taking":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the first `take`\nelements of this iterable object. If the specified \nnumber of elements is larger than the number of \nelements of this iterable object, the `Iterable` \ncontains the same elements as this iterable object."]},"$nm":"taking","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"i":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"i"}},"$nm":"iterator"}}}},"$at":{"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"}},"$nm":"iterable"}}},"following":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$nm":"head"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["An `Iterable` with the given inital element followed \nby the elements of this iterable object."]},"$nm":"following","$o":{"cons":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"first":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"first"}},"$nm":"iterator"}}}},"$nm":"cons"}}},"chain":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Absent"},{"$nm":"OtherAbsent"}]}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"OtherAbsent"}],"$an":{"shared":[],"default":[],"doc":["The elements of this iterable object, in their\noriginal order, followed by the elements of the \ngiven iterable object also in their original order."]},"$nm":"chain","$o":{"chained":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Absent"},{"$nm":"OtherAbsent"}]}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"}},"$nm":"chained"}}},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["size"],"doc":["Determines if this iterable object has fewer elements\nthan the given length. This is an efficient operation \nfor iterable objects with many elements."]},"$nm":"shorterThan"},"skippingWhile":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"skip":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"skip"}},"$nm":"skippingWhile","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"first":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"first"}},"$nm":"iterator"}}}},"$nm":"iterable"}}},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate that at least one element \nmust satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if at least one element satisfies the\npredicate function."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"mthd","$an":{"doc":["The predicate that at least one element \nmust satisfy."]},"$nm":"selecting"}},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The mapping to apply to the elements."]},"$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["collect"],"doc":["An `Iterable` containing the results of applying\nthe given mapping to the elements of to this \ncontainer."]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The last element which satisfies the given\npredicate, if any, or `null` otherwise."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}},"$nm":"findLast"},"skipping":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the elements of\nthis iterable object, after skipping the first \n`skip` elements. If this iterable object does not \ncontain more elements than the specified number of \nelements, the `Iterable` contains no elements."]},"$nm":"skipping","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"}},"$nm":"iterable"}}},"filter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the elements must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["select"],"doc":["An `Iterable` containing the elements of this \ncontainer that satisfy the given predicate."]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The first element which satisfies the given \npredicate, if any, or `null` otherwise."]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$an":{"doc":["The predicate the element must satisfy."]},"$nm":"selecting"}},"$nm":"find"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$nm":"Result"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The transformation applied to the elements."]},"$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["map"],"doc":["A sequence containing the results of applying the\ngiven mapping to the elements of this container. An \neager counterpart to `map()`."]},"$nm":"collect"}},"$at":{"cycled":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["cycle"],"doc":["A non-finite iterable object that produces the elements \nof this iterable object, repeatedly."]},"$nm":"cycled","$o":{"iterable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"iter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"iter"}},"$nm":"iterator"}}}},"$at":{"orig":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$nm":"orig"}},"$nm":"iterable"}}},"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The last element returned by the iterator, if any.\nIterables are potentially infinite, so calling this\nmight never return; also, this implementation will\niterate through all the elements, which might be\nvery time-consuming."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["All entries of form `index->element` where `index` \nis the position at which `element` occurs, for every\nnon-null element of this `Iterable`, ordered by\nincreasing `index`. For a null element at a given\nposition in the original `Iterable`, there is no \nentry with the corresponding index in the resulting \niterable object. The expression \n\n    { \"hello\", null, \"world\" }.indexed\n    \nresults in an iterable object with the entries\n`0->\"hello\"` and `2->\"world\"`."]},"$nm":"indexed","$o":{"indexes":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$mt":"obj","$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator","$o":{"iterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"iter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$nm":"iter"},"i":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"i"}},"$nm":"iterator"}}}},"$at":{"orig":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$nm":"orig"}},"$nm":"indexes"}}},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A sequence containing the elements returned by the\niterator."]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this iterable is empty. If the number of items\nis very large only a certain amount of them might\nbe shown followed by \"...\"."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The non-null elements of this `Iterable`, in their\noriginal order. For null elements of the original \n`Iterable`, there is no entry in the resulting \niterable object."]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determines if the iterable object is empty, that is\nto say, if the iterator returns no elements."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns an iterable object containing all but the \nfirst element of this container."]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The first element returned by the iterator, if any.\nThis should always produce the same value as\n`iterable.iterator().head`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[]},"$nm":"size"}},"$nm":"Iterable"},"flatten":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$nm":"Args"},"$mt":"prm","$pt":"v","$nm":"tuple"}]],"$mt":"prm","$pt":"f","$nm":"tupleFunction"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[],"see":["unflatten"],"doc":["Given a `Callable` with a single tuple parameter of type `[P1, P2, ..., Pn]`\nreturns an equivalent `Callable` with the parameter types `P1`, `P2`, ..., `Pn`."]},"$nm":"flatten"},"LazyList":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of `List` that wraps an `Iterable` of\nelements. All operations on this `List` are performed on \nthe `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `List` with the elements of this `List` \nin reverse order. This operation will create copy \nthe elements to a new `List`, so changes to the \noriginal `Iterable` will no longer be reflected in \nthe new `List`."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"elems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"elems"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"first"}},"$nm":"LazyList"},"className":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"obj"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Return the name of the concrete class of the given \nobject, in a format native to the virtual machine."]},"$nm":"className"},"List":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Sequence","Empty","Array"],"doc":["Represents a collection in which every element has a \nunique non-negative integer index.\n\nA `List` is a `Collection` of its elements, and a \n`Correspondence` from indices to elements.\n\nDirect access to a list element by index produces a\nvalue of optional type. The following idiom may be\nused instead of upfront bounds-checking, as long as \nthe list element type is a non-`null` type:\n\n    value char = \"hello world\"[index];\n    if (exists char) { \/*do something*\/ }\n    else { \/*out of bounds*\/ }\n\nTo iterate the indexes of a `List`, use the following\nidiom:\n\n    for (i->char in \"hello world\".indexed) { ... }"]},"$m":{"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"longerThan"},"trimLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"trimming":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"trimming"}},"$nm":"trimLeading"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determine if the given list occurs at some index in \nthis list."]},"$nm":"includes"},"firstInclusion":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The first index in this list at which the given list \noccurs."]},"$nm":"firstInclusion"},"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$nm":"occurs"},"withTrailing":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$an":{"doc":["The last element of the resulting sequence."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["Returns a new `List` that contains the specified\nelement appended to the end of the elements of this \n`List`."]},"$nm":"withTrailing"},"firstOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The first index in this list at which the given element \noccurs."]},"$nm":"firstOccurrence"},"terminal":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["List.initial"],"doc":["Select the last elements of the list, returning a list \nno longer than the given length. If this list is \nshorter than the given length, return this list. \nOtherwise return a list of the given length."]},"$nm":"terminal"},"initial":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["List.terminal"],"doc":["Select the first elements of this list, returning a \nlist no longer than the given length. If this list is \nshorter than the given length, return this list. \nOtherwise return a list of the given length."]},"$nm":"initial"},"inclusions":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The indexes in this list at which the given list \noccurs."]},"$nm":"inclusions"},"occurrences":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The indexes in this list at which the given element \noccurs."]},"$nm":"occurrences"},"trim":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"trimming":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"trimming"}},"$nm":"trim"},"startsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determine if the given list occurs at the start of this \nlist."]},"$nm":"startsWith"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator","$o":{"listIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"index":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"index"}},"$nm":"listIterator"}}},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the element of this sequence with the given\n    index, or `null` if the given index is past the end\n    of the sequence, that is, if\n    `index>sequence.lastIndex`. The first element of\n    the sequence has index `0`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `List`s are considered equal iff they have the \nsame `size` and _entry sets_. The entry set of a \nlist `l` is the set of elements of `l.indexed`. \nThis definition is equivalent to the more intuitive \nnotion that two lists are equal iff they have the \nsame `size` and for every index either:\n\n- the lists both have the element `null`, or\n- the lists both have a non-null element, and the\n  two elements are equal."],"actual":[]},"$nm":"equals"},"indexes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$an":{"doc":["The predicate the indexed elements must satisfy"]},"$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The indexes in this list for which the element \nsatisfies the given predicate."]},"$nm":"indexes"},"occursAt":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$nm":"occursAt"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if the given index refers to an element\n    of this sequence, that is, if\n    `index<=sequence.lastIndex`."],"actual":[]},"$nm":"defines"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"shorterThan"},"lastInclusion":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The last index in this list at which the given list \noccurs."]},"$nm":"lastInclusion"},"trimTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"trimming"}]],"$mt":"mthd","$an":{"shared":[],"default":[]},"$m":{"trimming":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"trimming"}},"$nm":"trimTrailing"},"includesAt":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$an":{"doc":["The index at which this list might occur"]},"$nm":"index"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determine if the given list occurs at the given index \nof this list."]},"$nm":"includesAt"},"lastOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The last index in this list at which the given element \noccurs."]},"$nm":"lastOccurrence"},"endsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"prm","$pt":"v","$nm":"sublist"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determine if the given list occurs at the end of this\nlist."]},"$nm":"endsWith"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$m":{"selecting":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$nm":"Element"},"$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"mthd","$nm":"selecting"}},"$nm":"findLast"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$nm":"Other"},"$mt":"prm","$pt":"v","$an":{"doc":["The first element of the resulting sequence."]},"$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"see":["following"],"doc":["Returns a new `List` that starts with the specified\nelement, followed by the elements of this `List`."]},"$nm":"withLeading"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["List.size"],"doc":["The index of the last element of the list, or\nnull if the list is empty."]},"$nm":"lastIndex"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the last element of this `List`, if any."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this list, returning a new list."]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the list, without the first element."],"actual":[]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the first element of this `List`, if any."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["List.lastIndex"],"doc":["The number of elements in this sequence, always\n`sequence.lastIndex+1`."],"actual":[]},"$nm":"size"}},"$nm":"List"},"Container":{"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"see":["Category"],"doc":["Abstract supertype of objects which may or may not\ncontain one of more other values, called *elements*,\nand provide an operation for accessing the first \nelement, if any. A container which may or may not be \nempty is a `Container<Element,Null>`. A container which \nis always empty is a `Container<Nothing,Null>`. A \ncontainer which is never empty is a \n`Container<Element,Nothing>`."],"by":["Gavin"]},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element. Should produce `null` if the\ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`."]},"$nm":"last"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the container is empty, that is, if\nit has no elements."]},"$nm":"empty"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element. Should produce `null` if the \ncontainer is empty, that is, for any instance for\n    which `empty` evaluates to `true`."]},"$nm":"first"}},"$nm":"Container"},"abstract":{"$t":{"$pk":"ceylon.language","$nm":"Abstract"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as abstract. An `abstract` class \nmay not be directly instantiated. An `abstract` class may \nhave enumerated cases."]},"$annot":"1","$nm":"abstract"},"zip":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"items"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"doc":["Given two sequences, form a new sequence consisting of\nall entries where, for any given index in the resulting\nsequence, the key of the entry is the element occurring \nat the same index in the first sequence, and the item \nis the element occurring at the same index in the second \nsequence. The length of the resulting sequence is the \nlength of the shorter of the two given sequences. \n\nThus:\n\n    zip(xs,ys)[i]==xs[i]->ys[i]\n\nfor every `0<=i<min({xs.size,ys.size})`."]},"$nm":"zip"},"Shared":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Shared"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"Package"},{"$pk":"ceylon.language.model.declaration","$nm":"Import"}]}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[shared]]."]},"$annot":"1","$nm":"Shared"},"Deprecation":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$an":{"shared":[]},"$nm":"description"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Deprecation"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[],"doc":["The annotation class for [[deprecated]]."]},"$annot":"1","$at":{"reason":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"gttr","$an":{"shared":[]},"$nm":"reason"},"description":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[]},"$nm":"description"}},"$nm":"Deprecation"},"equal":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is exactly equal to the given value."]},"$nm":"equal"},"finished":{"super":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"obj","$an":{"shared":[],"see":["Iterator"],"doc":["A value that indicates that an `Iterator`\nis exhausted and has no more values to \nreturn."]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"finished"},"Boolean":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"true"},{"$pk":"ceylon.language","$nm":"false"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A type capable of representing the values true and\nfalse of Boolean logic."],"by":["Gavin"]},"$nm":"Boolean"},"See":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$an":{"shared":[]},"$nm":"programElements"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"See"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Annotated"}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"final":[]},"$annot":"1","$at":{"programElements":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[]},"$nm":"programElements"}},"$nm":"See"},"print":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print a line to the standard output of the virtual machine \nprocess, printing the given value's `string`, or `<null>` \nif the value is `null`.\n\nThis method is a shortcut for:\n\n    process.writeLine(line?.string else \"<null>\")\n\nand is intended mainly for debugging purposes."],"by":["Gavin"]},"$nm":"print"},"ChainedIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"first"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"second"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"variance":"out","$nm":"Other"}],"$an":{"see":["Iterable.chain"],"doc":["An `Iterator` that returns the elements of two\n`Iterable`s, as if they were chained together."],"by":["Enrique Zamudio"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"more":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"more"},"second":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"second"},"iter":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"shared":[],"actual":[],"variable":[]},"$nm":"iter"},"first":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$nm":"first"}},"$nm":"ChainedIterator"},"Basic":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The default superclass when no superclass is explicitly\nspecified using `extends`. For the sake of convenience, \nthis class inherits a default definition of value\nequality from `Identifiable`."],"by":["Gavin"]},"$nm":"Basic"},"Category":{"$mt":"ifc","$an":{"shared":[],"see":["Container"],"doc":["Abstract supertype of objects that contain other \nvalues, called *elements*, where it is possible to \nefficiently determine if a given value is an element. \n`Category` does not satisfy `Container`, because it is \nconceptually possible to have a `Category` whose \nemptiness cannot be computed.\n\nThe `in` operator may be used to determine if a value\nbelongs to a `Category`:\n\n    if (\"hello\" in \"hello world\") { ... }\n    if (69 in 0..100) { ... }\n    if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n\nOrdinarily, `x==y` implies that `x in cat == y in cat`.\nBut this contract is not required since it is possible\nto form a meaningful `Category` using a different\nequivalence relation. For example, an `IdentitySet` is\na meaningful `Category`."],"by":["Gavin"]},"$m":{"containsAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if any one of the given values belongs \nto this `Category`"]},"$nm":"containsAny"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["containsEvery","containsAny"],"doc":["Determines if the given value belongs to this\n`Category`, that is, if it is an element of this\n`Category`.\n\nFor most `Category`s, if `x==y`, then \n`category.contains(x)` evaluates to the same\nvalue as `category.contains(y)`. However, it is\npossible to form a `Category` consistent with some \nother equivalence relation, for example `===`. \nTherefore implementations of `contains()` which do \nnot satisfy this relationship are tolerated."]},"$nm":"contains"},"containsEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if every one of the given values belongs\nto this `Category`."]},"$nm":"containsEvery"}},"$nm":"Category"},"see":{"$t":{"$pk":"ceylon.language","$nm":"See"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API references to other related \nprogram elements."]},"$annot":"1","$nm":"see"},"computeMagnitude":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"radix"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$mt":"prm","$pt":"v","$nm":"char"}]],"$mt":"mthd","$nm":"computeMagnitude"},"NegativeNumberException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a negative number is not allowed."]},"$nm":"NegativeNumberException"},"actual":{"$t":{"$pk":"ceylon.language","$nm":"Actual"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member of a type as refining a member \nof a supertype."]},"$annot":"1","$nm":"actual"},"Integer":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"integer"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Integral"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Binary"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"shared":[],"final":[],"native":[],"doc":["A 64-bit integer, or the closest approximation to a \n64-bit integer provided by the underlying platform.\n\n- For the JVM runtime, integer values between\n  -2<sup>63<\/sup> and 2<sup>63<\/sup>-1 may be \n  represented without overflow.\n- For the JavaScript runtime, integer values with a\n  magnitude no greater than 2<sup>53<\/sup> may be\n  represented without loss of precision.\n\nOverflow or loss of precision occurs silently (with\nno exception raised)."]},"$at":{"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"integer"},"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The UTF-32 character with this UCS code point."]},"$nm":"character"}},"$nm":"Integer"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"doc":["The first of the given values (usually a comprehension),\nif any."]},"$nm":"first"},"zeroInt":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"zeroInt"}},"ceylon.language.model.declaration":{"Import":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"},{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of an `import` declaration \nwithin a module declaration."]},"$at":{"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The name of the imported module."]},"$nm":"name"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The compile-time version of the imported module."]},"$nm":"version"}},"$nm":"Import"},"AnnotatedDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"Module"},{"$pk":"ceylon.language.model.declaration","$nm":"Package"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"},{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"annotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Annotation"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Annotation"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Annotation"}],"$an":{"shared":[],"formal":[],"doc":["The annotation instances of the given \nannotation type on this declaration."]},"$nm":"annotations"}},"$nm":"AnnotatedDeclaration"},"OpenTypeVariable":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"}},"$nm":"OpenTypeVariable"},"OpenIntersection":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"satisfiedTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"satisfiedTypes"}},"$nm":"OpenIntersection"},"OpenType":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"nothingType"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"},{"$pk":"ceylon.language.model.declaration","$nm":"OpenTypeVariable"},{"$pk":"ceylon.language.model.declaration","$nm":"OpenUnion"},{"$pk":"ceylon.language.model.declaration","$nm":"OpenIntersection"}],"$mt":"ifc","$an":{"shared":[]},"$nm":"OpenType"},"SetterDeclaration":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of the setter of a `VariableDeclaration`."]},"$at":{"variable":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"VariableDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The variable this setter is for."]},"$nm":"variable"}},"$nm":"SetterDeclaration"},"Module":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"},{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of a `module` declaration\nfrom a `module.ceylon` compilation unit"]},"$m":{"findPackage":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"Package"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Finds a package by name"]},"$nm":"findPackage"}},"$at":{"dependencies":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Import"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The modules this module depends on."]},"$nm":"dependencies"},"members":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Package"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The package members of the module."]},"$nm":"members"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The version of the module."]},"$nm":"version"}},"$nm":"Module"},"InterfaceDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Interface"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Interface"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"bindAndApply"}},"$nm":"InterfaceDeclaration"},"TypeParameter":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"Declaration"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"invariant":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"invariant"},"covariant":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"covariant"},"enumeratedBounds":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"enumeratedBounds"},"upperBounds":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"upperBounds"},"contravariant":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"contravariant"},"defaultValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"defaultValue"},"defaulted":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"defaulted"}},"$nm":"TypeParameter"},"FunctionalDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$m":{"getParameterDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"getParameterDeclaration"}},"$at":{"parameterDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"parameterDeclarations"}},"$nm":"FunctionalDeclaration"},"$pkg-shared":"1","GenericDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$m":{"getTypeParameterDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"getTypeParameterDeclaration"}},"$at":{"typeParameterDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeParameterDeclarations"}},"$nm":"GenericDeclaration"},"ParameterDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$at":{"variadic":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"variadic"},"defaulted":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"defaulted"}},"$nm":"ParameterDeclaration"},"VariableDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of an attribute that is `variable` or has an `assign` block."]},"$at":{"setter":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"SetterDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Returns a model of the setter of this variable.\n\nFor modelling purposes `variable` reference \nvalues have a SetterDeclaration even though there is no \nsuch setter explicit in the source code."]},"$nm":"setter"}},"$nm":"VariableDeclaration"},"Package":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"},{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"}],"$mt":"ifc","$an":{"shared":[],"doc":["Model of a `package` declaration \nfrom a `package.ceylon` compilation unit"]},"$m":{"getValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The value with the given name."]},"$nm":"getValue"},"getAlias":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"AliasDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The type alias with the given name."]},"$nm":"getAlias"},"getMember":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Kind"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[],"doc":["Looks up a member of this package by name and type."]},"$nm":"getMember"},"getFunction":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The function with the given name."]},"$nm":"getFunction"},"annotatedMembers":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"},{"$nm":"Annotation"}],"$an":{"shared":[],"formal":[],"doc":["The members of this package having a particular annotation."]},"$nm":"annotatedMembers"},"getClassOrInterface":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The class or interface with the given name."]},"$nm":"getClassOrInterface"},"members":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[],"doc":["The members of this package."]},"$nm":"members"}},"$at":{"container":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Module"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The module this package belongs to."]},"$nm":"container"}},"$nm":"Package"},"TypedDeclaration":{"$mt":"ifc","$an":{"shared":[]},"$at":{"openType":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"openType"}},"$nm":"TypedDeclaration"},"OpenUnion":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"caseTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"caseTypes"}},"$nm":"OpenUnion"},"FunctionDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"GenericDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionalDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Function"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Function"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"bindAndApply"},"memberApply":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$nm":"MethodType"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Method"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"Container"},{"$nm":"MethodType"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Arguments"}],"$an":{"shared":[],"formal":[]},"$nm":"memberApply"}},"$nm":"FunctionDeclaration"},"OpenParameterisedType":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"}],"variance":"out","$nm":"DeclarationType"}],"$an":{"shared":[]},"$at":{"superclass":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclass"},"declaration":{"$t":{"$nm":"DeclarationType"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"},"interfaces":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaces"},"typeArguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeArguments"}},"$nm":"OpenParameterisedType"},"Declaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"name"}},"$nm":"Declaration"},"ClassOrInterfaceDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"GenericDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"memberDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"memberDeclarations"},"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"bindAndApply"},"memberApply":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.model","$nm":"Member"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"Container"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"memberApply"},"annotatedMemberDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"},{"$nm":"Annotation"}],"$an":{"shared":[],"formal":[]},"$nm":"annotatedMemberDeclarations"},"getMemberDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Kind"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[],"doc":["Looks up a member of this package by name and type."]},"$nm":"getMemberDeclaration"}},"$at":{"superclassDeclaration":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclassDeclaration"},"interfaceDeclarations":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"}],"$pk":"ceylon.language.model.declaration","$nm":"OpenParameterisedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaceDeclarations"}},"$nm":"ClassOrInterfaceDeclaration"},"TopLevelOrMemberDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"AliasDeclaration"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"AnnotatedDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"TypedDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"toplevel":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"toplevel"},"packageContainer":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"Package"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"packageContainer"}},"$nm":"TopLevelOrMemberDeclaration"},"nothingType":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"OpenType"}],"$mt":"obj","$an":{"shared":[]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"nothingType"},"ValueDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionOrValueDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Value"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$def":"1","$nm":"instance"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"}},"$nm":"ValueDeclaration"},"ClassDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"FunctionalDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"bindAndApply"}},"$at":{"anonymous":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"anonymous"}},"$nm":"ClassDeclaration"},"FunctionOrValueDeclaration":{"of":[{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"}],"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"variadic":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"variadic"},"defaulted":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"defaulted"}},"$nm":"FunctionOrValueDeclaration"},"AliasDeclaration":{"satisfies":[{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"},{"$pk":"ceylon.language.model.declaration","$nm":"GenericDeclaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"},"bindAndApply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"instance"},{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"bindAndApply"}},"$nm":"AliasDeclaration"}},"ceylon.language.model":{"sequencedAnnotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":{"$nm":"ProgramElement"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"SequencedAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"sequencedAnnotations"},"Member":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Kind"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Type"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"}],"variance":"out","$nm":"Kind"}],"$an":{"shared":[]},"$at":{"declaringClassOrInterface":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaringClassOrInterface"}},"$nm":"Member"},"Model":{"of":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"FunctionModel"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ValueModel"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"TopLevelOrMemberDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"}},"$nm":"Model"},"InterfaceModel":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"InterfaceDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"}},"$nm":"InterfaceModel"},"Method":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"FunctionModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Function"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language.model","$nm":"Nothing"},"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"Method"},"Type":{"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$nm":"Type"},"IntersectionType":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Intersection"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Intersection"}],"$an":{"shared":[]},"$at":{"satisfiedTypes":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"satisfiedTypes"}},"$nm":"IntersectionType"},"UnionType":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Union"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Union"}],"$an":{"shared":[]},"$at":{"caseTypes":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Union"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"caseTypes"}},"$nm":"UnionType"},"type":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$ps":[[{"$t":{"$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"instance"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Anything"}],"variance":"out","$nm":"Type"}],"$an":{"shared":[],"native":[]},"$nm":"type"},"$pkg-shared":"1","FunctionModel":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language.model","$nm":"Nothing"},"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"FunctionDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"type":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Type"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"FunctionModel"},"OptionalAnnotation":{"of":[{"$nm":"Value"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$def":{"$pk":"ceylon.language.model","$nm":"Annotated"},"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur at most once\nat a single program element."]},"$nm":"OptionalAnnotation","$st":"Value"},"ValueModel":{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"ValueDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"type":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Type"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"ValueModel"},"ClassModel":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language.model","$nm":"Nothing"},"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"ClassDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"}},"$nm":"ClassModel"},"Attribute":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"ValueModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$nm":"Attribute"},"Annotated":{"$mt":"ifc","$an":{"shared":[],"doc":["A program element that can\nbe annotated."]},"$nm":"Annotated"},"Value":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"ValueModel"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$m":{"get":{"$t":{"$nm":"Type"},"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"get"}},"$nm":"Value"},"Variable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Value"}],"$mt":"ifc","$tp":[{"$nm":"Type"}],"$an":{"shared":[]},"$m":{"set":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"newValue"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"set"}},"$nm":"Variable"},"Function":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"FunctionModel"},{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language.model","$nm":"Nothing"},"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"Function"},"typeLiteral":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Type"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Anything"}],"variance":"out","$nm":"Type"}],"$an":{"shared":[],"native":[]},"$nm":"typeLiteral"},"MemberClass":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"ClassModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Class"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language.model","$nm":"Nothing"},"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"MemberClass"},"VariableAttribute":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Variable"}],"$pk":"ceylon.language.model","$nm":"Member"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Attribute"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"$nm":"Type"}],"$an":{"shared":[]},"$nm":"VariableAttribute"},"Interface":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"InterfaceModel"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$nm":"Interface"},"SequencedAnnotation":{"of":[{"$nm":"Value"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$def":{"$pk":"ceylon.language.model","$nm":"Annotated"},"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur multiple times\nat a single program element."]},"$nm":"SequencedAnnotation","$st":"Value"},"optionalAnnotation":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":{"$nm":"ProgramElement"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"OptionalAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"optionalAnnotation"},"ConstrainedAnnotation":{"of":[{"$nm":"Value"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"variance":"out","$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation. This interface encodes constraints upon \nthe annotation in its type arguments."]},"$m":{"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language.model","$nm":"Annotated"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Can this annotation can occur on the given program \nelement?"]},"$nm":"occurs"}},"$nm":"ConstrainedAnnotation","$st":"Value"},"Class":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"ClassModel"},{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language.model","$nm":"Nothing"},"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$nm":"Class"},"Annotation":{"of":[{"$nm":"Value"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.model","$nm":"Annotation"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["An annotation."]},"$nm":"Annotation","$st":"Value"},"nothingType":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"obj","$an":{"shared":[]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"nothingType"},"ClassOrInterface":{"of":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"ClassModel"},{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"InterfaceModel"}],"satisfies":[{"$pk":"ceylon.language.model","$nm":"Model"},{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$mt":"ifc","$tp":[{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$m":{"getMethod":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language.model","$nm":"Method"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Arguments"}],"$an":{"shared":[],"formal":[]},"$nm":"getMethod"},"getVariableAttribute":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"VariableAttribute"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"$nm":"Type"}],"$an":{"shared":[],"formal":[]},"$nm":"getVariableAttribute"},"getAttribute":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Attribute"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"$nm":"Type"}],"$an":{"shared":[],"formal":[]},"$nm":"getAttribute"},"getClassOrInterface":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.model","$nm":"Member"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"ClassOrInterface"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"getClassOrInterface"}},"$at":{"superclass":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclass"},"declaration":{"$t":{"$pk":"ceylon.language.model.declaration","$nm":"ClassOrInterfaceDeclaration"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"interfaces":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Interface"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaces"},"typeArguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"TypeParameter"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.model","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeArguments"}},"$nm":"ClassOrInterface"},"annotations":{"$t":{"$nm":"Values"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"},{"$mt":"tpm","$pk":"ceylon.language.model","$nm":"Nothing"}],"$pk":"ceylon.language.model","$nm":"Class"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":{"$nm":"ProgramElement"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.model","$nm":"ConstrainedAnnotation"}],"$nm":"Value"},{"$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.model","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[],"native":[]},"$nm":"annotations"},"modules":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[]},"$m":{"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"Module"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"version"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"find"}},"$at":{"default":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.model.declaration","$nm":"Module"}]},"$mt":"attr","$an":{"shared":[],"native":[]},"$nm":"default"},"list":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.model.declaration","$nm":"Module"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[]},"$nm":"list"}},"$nm":"modules"},"MemberInterface":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"InterfaceModel"},{"$tp":[{"$mt":"tpm","$nm":"Container"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.model","$nm":"Interface"}],"$pk":"ceylon.language.model","$nm":"Member"}],"$mt":"ifc","$tp":[{"variance":"in","$nm":"Container"},{"$def":{"$pk":"ceylon.language","$nm":"Anything"},"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$nm":"MemberInterface"}}};
exports.$$METAMODEL$$=function(){return $$METAMODEL$$;};
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
Anything.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:['ceylon.language','Anything']};
function Null(wat) {
return null;
}
initType(Null, 'ceylon.language::Null', Anything);
Null.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:['ceylon.language','Null']};
function Nothing(wat) {
throw "Nothing";
}
initType(Nothing, 'ceylon.language::Nothing');
//This is quite a special case, since Nothing is not in the model, we need to insert it there
$$METAMODEL$$['ceylon.language'].Nothing={"$mt":"cls","$an":{"shared":[]},"$nm":"Nothing"};
Nothing.$$metamodel$$={$ps:[],$an:function(){return[shared()]},mod:$$METAMODEL$$,d:['ceylon.language','Nothing']};
function Object$(wat) {
return wat;
}
initTypeProto(Object$, 'ceylon.language::Object', Anything);
Object$.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:['ceylon.language','Object']};
var Object$proto = Object$.$$.prototype;
defineAttr(Object$proto, 'string', function(){
return String$(className(this) + "@" + this.hash);
},undefined,{$an:function(){return[shared(),$default()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','string']});
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
Identifiable.$$metamodel$$={$an:function(){return[shared()]},mod:$$METAMODEL$$,d:['ceylon.language','Identifiable']};
function $init$Identifiable() { return Identifiable; }
var Identifiable$proto = Identifiable.$$.prototype;
Identifiable$proto.equals = function(that) {
return isOfType(that, {t:Identifiable}) && (that===this);
}
defineAttr(Identifiable$proto, 'hash', function(){ return $identityHash(this); },
undefined,{$an:function(){return[shared(),$default()]},mod:$$METAMODEL$$,d:['ceylon.language','Identifiable','$at','hash']});
//INTERFACES
//Compiled from Ceylon sources
function Callable(wat) {
    return wat;
}
Callable.$$metamodel$$={mod:$$METAMODEL$$,$an:function(){return[shared()];},$tp:{Arguments:{'var':'out'},Return:{'var':'out'}},d:['ceylon.language','Callable']};
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
        callable.$$metamodel$$={$ps:[],mod:$$METAMODEL$$,d:['ceylon.language','Callable']};
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
JsCallable.$$metamodel$$={$tp:{Return:{'var':'out'}, Arguments:{'var':'in'}},$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','Callable']};

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
JsCallableList.$$metamodel$$={$tp:{Return:{'var':'out'}, Arguments:{'var':'in'}},$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','Callable']};

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
Comprehension.$$metamodel$$={$nm:'Comprehension',$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','Iterable']};
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
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Iterable','$at','sequence']});
exports.Comprehension=Comprehension;

function ComprehensionIterator(nextFunc, $$targs$$, it) {
    $init$ComprehensionIterator();
    if (it===undefined) {it = new ComprehensionIterator.$$;}
    it.$$targs$$=$$targs$$;
    Basic(it);
    it.next = nextFunc;
    return it;
}
ComprehensionIterator.$$metamodel$$={$nm:'ComprehensionIterator',$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','Iterator']};
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
Basic.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Object$},satisfies:[{t:Identifiable}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),abstract()];},d:['ceylon.language','Basic']};};
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
    if(cause===undefined){cause=null;}
    if (typeof($init$native$Exception$before)==='function')$init$native$Exception$before($$exception);
    $$exception.cause$2_=cause;
    $$exception.description$1_=description$1;
    if (typeof($init$native$Exception$after)==='function')$init$native$Exception$after($$exception);
    return $$exception;
}
Exception.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[],$an:function(){return[by([String$("Gavin",5),String$("Tom",3)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),$native()];},d:['ceylon.language','Exception']};};
exports.Exception=Exception;
function $init$Exception(){
    if (Exception.$$===undefined){
        initTypeProto(Exception,'ceylon.language::Exception',Basic);
        (function($$exception){
            defineAttr($$exception,'cause',function(){return this.cause$2_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Exception}]},$cont:Exception,$an:function(){return[shared()];},d:['ceylon.language','Exception','$at','cause']};});
            defineAttr($$exception,'description$1',function(){return this.description$1_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:String$}]},$cont:Exception,d:['ceylon.language','Exception','$at','description']};});
            defineAttr($$exception,'message',function(){
                var $$exception=this;
                return (opt$3=(opt$4=$$exception.description$1,opt$4!==null?opt$4:(opt$5=$$exception.cause,opt$5!==null?opt$5.message:null)),opt$3!==null?opt$3:String$("",0));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Exception,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:ValueDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Exception','$at','message']};});
            defineAttr($$exception,'string',function(){
                var $$exception=this;
                return className($$exception).plus(StringBuilder().appendAll([String$(" \"",2),$$exception.message.string,String$("\"",1)]).string);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Exception,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Exception','$at','string']};});
        })(Exception.$$.prototype);
    }
    return Exception;
}
exports.$init$Exception=$init$Exception;
$init$Exception();
var opt$3,opt$4,opt$5;
function $init$native$Exception$before(exc) {
  var _caller=arguments.callee.caller.caller;
  exc.stack_trace=[];
  while(_caller) {
    exc.stack_trace.push(_caller);
    _caller = _caller.caller;
  }
}
Exception.$$.prototype.printStackTrace = function() {
  var _c = className(this);
  if (this.message.size > 0) {
    _c += ' "' + this.message + '"';
  }
  print(_c);
  for (var i=0; i<this.stack_trace.length; i++) {
    var f = this.stack_trace[i];
    var mm = f.$$metamodel$$;
    if (typeof(mm)==='function') {
      mm = mm();
      f.$$metamodel$$=mm;
    }
    if (mm) {
      var _src = '';
      if (i==0) {
        if (this.$loc && this.$file) _src = ' (' + this.$file + " " + this.$loc + ')';
      }
      print("    at " + mm.d[0] + "::" + mm.d[mm.d.length-1] + _src);
    }
  }
}
function Iterable($$targs$$,$$iterable){
    Container($$iterable.$$targs$$===undefined?$$targs$$:{Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element},$$iterable);
    set_type_args($$iterable,$$targs$$);
}
Iterable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out'},Absent:{'var':'out','satisfies':[{t:Null}],'def':{t:Null}}},satisfies:[{t:Container,a:{Absent:'Absent',Element:'Element'}}],$an:function(){return[see([typeLiteral$model({Type:Collection})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Iterable']};};
exports.Iterable=Iterable;
function $init$Iterable(){
    if (Iterable.$$===undefined){
        initTypeProto(Iterable,'ceylon.language::Iterable',$init$Container());
        (function($$iterable){
            defineAttr($$iterable,'empty',function(){
                var $$iterable=this;
                return isOfType($$iterable.iterator().next(),{t:Finished});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Iterable','$at','empty']};});
            defineAttr($$iterable,'size',function(){
                var $$iterable=this;
                return $$iterable.count($JsCallable(function (e$6){
                    var $$iterable=this;
                    return true;
                },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$at','size']};});
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
            };$$iterable.longerThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:ValueDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','longerThan']};};
            $$iterable.shorterThan=function shorterThan(length$13){
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
            };$$iterable.shorterThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:ValueDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','shorterThan']};};
            $$iterable.contains=function (element$18){
                var $$iterable=this;
                return $$iterable.any($JsCallable(ifExists($JsCallable((opt$19=element$18,JsCallable(opt$19,opt$19!==null?opt$19.equals:null)),[{$nm:'that',$mt:'prm',$t:{t:Object$}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Return:{t:Boolean$}})),[{$nm:'p1',$mt:'prm',$t:{t:Anything}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Anything},Element:{t:Anything}}},Return:{t:Boolean$}}));
            };
            $$iterable.contains.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Iterable','$m','contains']};};
            defineAttr($$iterable,'first',function(){
                var $$iterable=this;
                return first($$iterable,{Value:$$iterable.$$targs$$.Element,Absent:$$iterable.$$targs$$.Absent});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Element']},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Iterable','$at','first']};});
            defineAttr($$iterable,'last',function(){
                var $$iterable=this;
                var e$20=$$iterable.first;
                var setE$20=function(e$21){return e$20=e$21;};
                var it$22 = $$iterable.iterator();
                var x$23;while ((x$23=it$22.next())!==getFinished()){
                    e$20=x$23;
                }
                return e$20;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Element']},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Iterable','$at','last']};});defineAttr($$iterable,'rest',function(){
                var $$iterable=this;
                return $$iterable.skipping((1));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$at','rest']};});
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
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$at','sequence']};});
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
            $$iterable.$map.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Iterable,$tp:{Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','map']};};
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
            $$iterable.$filter.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','filter']};};
            $$iterable.fold=function fold(initial$38,accumulating$39,$$$mptypes){
                var $$iterable=this;
                var r$40=initial$38;
                var setR$40=function(r$41){return r$40=r$41;};
                var it$42 = $$iterable.iterator();
                var e$43;while ((e$43=it$42.next())!==getFinished()){
                    r$40=accumulating$39(r$40,e$43);
                }
                return r$40;
            };$$iterable.fold.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$cont:Iterable,$tp:{Result:{}},$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','fold']};};
            $$iterable.find=function find(selecting$44){
                var $$iterable=this;
                var it$45 = $$iterable.iterator();
                var e$46;while ((e$46=it$45.next())!==getFinished()){
                    if(selecting$44(e$46)){
                        return e$46;
                    }
                }
                return null;
            };$$iterable.find.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','find']};};
            $$iterable.findLast=function findLast(selecting$47){
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
            };$$iterable.findLast.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','findLast']};};
            $$iterable.$sort=function (comparing$52){
                var $$iterable=this;
                return internalSort($JsCallable(comparing$52,[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Comparison}}),$$iterable,{Element:$$iterable.$$targs$$.Element});
            };
            $$iterable.$sort.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Iterable,$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','sort']};};
            $$iterable.collect=function (collecting$53,$$$mptypes){
                var $$iterable=this;
                return $$iterable.$map($JsCallable(collecting$53,[{$nm:'element',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:$$$mptypes.Result}),{Result:$$$mptypes.Result}).sequence;
            };
            $$iterable.collect.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Iterable,$tp:{Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','collect']};};
            $$iterable.select=function (selecting$54){
                var $$iterable=this;
                return $$iterable.$filter($JsCallable(selecting$54,[{$nm:'element',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}})).sequence;
            };
            $$iterable.select.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','select']};};
            $$iterable.any=function any(selecting$55){
                var $$iterable=this;
                var it$56 = $$iterable.iterator();
                var e$57;while ((e$57=it$56.next())!==getFinished()){
                    if(selecting$55(e$57)){
                        return true;
                    }
                }
                return false;
            };$$iterable.any.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','any']};};
            $$iterable.$every=function $every(selecting$58){
                var $$iterable=this;
                var it$59 = $$iterable.iterator();
                var e$60;while ((e$60=it$59.next())!==getFinished()){
                    if((!selecting$58(e$60))){
                        return false;
                    }
                }
                return true;
            };$$iterable.$every.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','every']};};
            $$iterable.skipping=function skipping(skip$61){
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
                            };$$iterable$65.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$65,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','skipping','$o','iterable','$m','iterator']};};
                        })(iterable$65.$$.prototype);
                        var iterable$70=iterable$65({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$70=function(){
                            return iterable$70;
                        }
                        return getIterable$70();
                    }());if(retvar$63!==undefined){return retvar$63;}
                }
            };$$iterable.skipping.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','skipping']};};
            $$iterable.taking=function taking(take$71){
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
                                    defineAttr($$iterator$77,'i$78',function(){return this.i$78_;},function(i$79){return this.i$78_=i$79;},function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:iterator$77,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$m','taking','$o','iterable','$m','iterator','$o','iterator','$at','i']};});
                                    $$iterator$77.next=function next(){
                                        var $$iterator$77=this;
                                        return (opt$80=(($$iterator$77.i$78=$$iterator$77.i$78.successor).compare(take$71).equals(getLarger())?getFinished():null),opt$80!==null?opt$80:iter$76.next());
                                        var opt$80;
                                    };$$iterator$77.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$77,$an:function(){return[actual(),shared()];},d:['ceylon.language','Iterable','$m','taking','$o','iterable','$m','iterator','$o','iterator','$m','next']};};
                                })(iterator$77.$$.prototype);
                                var iterator$81=iterator$77({Element:$$iterable.$$targs$$.Element});
                                var getIterator$81=function(){
                                    return iterator$81;
                                }
                                return getIterator$81();
                            };$$iterable$75.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$75,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','taking','$o','iterable','$m','iterator']};};
                            defineAttr($$iterable$75,'first',function(){
                                var $$iterable$75=this;
                                return $$iterable.first;
                            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:iterable$75,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','taking','$o','iterable','$at','first']};});
                        })(iterable$75.$$.prototype);
                        var iterable$82=iterable$75({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$82=function(){
                            return iterable$82;
                        }
                        return getIterable$82();
                    }());if(retvar$73!==undefined){return retvar$73;}
                }
            };$$iterable.taking.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','taking']};};
            $$iterable.skippingWhile=function skippingWhile(skip$83){
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
                                        defineAttr($$iterator$90,'first$91',function(){return this.first$91_;},function(first$92){return this.first$91_=first$92;},function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:iterator$90,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$m','skippingWhile','$o','iterable','$m','iterator','$o','iterator','$at','first']};});
                                        $$iterator$90.next=function next(){
                                            var $$iterator$90=this;
                                            if($$iterator$90.first$91){
                                                $$iterator$90.first$91=false;
                                                return elem$86;
                                            }else {
                                                return iter$85.next();
                                            }
                                        };$$iterator$90.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$90,$an:function(){return[actual(),shared()];},d:['ceylon.language','Iterable','$m','skippingWhile','$o','iterable','$m','iterator','$o','iterator','$m','next']};};
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
                    };$$iterable$84.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$84,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','skippingWhile','$o','iterable','$m','iterator']};};
                })(iterable$84.$$.prototype);
                var iterable$94=iterable$84({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                var getIterable$94=function(){
                    return iterable$94;
                }
                return getIterable$94();
            };$$iterable.skippingWhile.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','skippingWhile']};};
            $$iterable.takingWhile=function takingWhile(take$95){
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
                            defineAttr($$iterator$98,'alive$99',function(){return this.alive$99_;},function(alive$100){return this.alive$99_=alive$100;},function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:iterator$98,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$m','takingWhile','$o','iterable','$m','iterator','$o','iterator','$at','alive']};});
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
                            };$$iterator$98.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$98,$an:function(){return[actual(),shared()];},d:['ceylon.language','Iterable','$m','takingWhile','$o','iterable','$m','iterator','$o','iterator','$m','next']};};
                        })(iterator$98.$$.prototype);
                        var iterator$103=iterator$98({Element:$$iterable.$$targs$$.Element});
                        var getIterator$103=function(){
                            return iterator$103;
                        }
                        return getIterator$103();
                    };$$iterable$96.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$96,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','takingWhile','$o','iterable','$m','iterator']};};
                })(iterable$96.$$.prototype);
                var iterable$104=iterable$96({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                var getIterable$104=function(){
                    return iterable$104;
                }
                return getIterable$104();
            };$$iterable.takingWhile.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'take',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','takingWhile']};};
            $$iterable.by=function by(step$105){
                var $$iterable=this;
                //assert at Iterable.ceylon (397:8-398:25)
                if (!(step$105.compare((0)).equals(getLarger()))) {throw wrapexc(AssertionException("step size must be greater than zero: \'step > 0\' at Iterable.ceylon (398:15-398:24)"),'397:8-398:25','Iterable.ceylon'); }
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
                                    };$$iterator$111.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$111,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','by','$o','iterable','$m','iterator','$o','iterator','$m','next']};};
                                })(iterator$111.$$.prototype);
                                var iterator$115=iterator$111({Element:$$iterable.$$targs$$.Element});
                                var getIterator$115=function(){
                                    return iterator$115;
                                }
                                return getIterator$115();
                            };$$iterable$109.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$109,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','by','$o','iterable','$m','iterator']};};
                        })(iterable$109.$$.prototype);
                        var iterable$116=iterable$109({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element});
                        var getIterable$116=function(){
                            return iterable$116;
                        }
                        return getIterable$116();
                    }());if(retvar$107!==undefined){return retvar$107;}
                }
            };$$iterable.by.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[$throws(typeLiteral$model({Type:AssertionException}),String$("if the given step size is nonpositive, \ni.e. `step<1`",53)),shared(),$default()];},d:['ceylon.language','Iterable','$m','by']};};
            $$iterable.count=function count(selecting$117){
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
            };$$iterable.count.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','count']};};
            defineAttr($$iterable,'coalesced',function(){
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
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{ t:'i', l:['Element',{t:Object$}]}}},$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$at','coalesced']};});
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
                    },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$cont:indexes$129,d:['ceylon.language','Iterable','$at','indexed','$o','indexes','$at','orig']};});
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
                            defineAttr($$iterator$131,'iter$132',function(){return this.iter$132_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$131,d:['ceylon.language','Iterable','$at','indexed','$o','indexes','$m','iterator','$o','iterator','$at','iter']};});
                            defineAttr($$iterator$131,'i$133',function(){return this.i$133_;},function(i$134){return this.i$133_=i$134;},function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:iterator$131,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$at','indexed','$o','indexes','$m','iterator','$o','iterator','$at','i']};});
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
                            };$$iterator$131.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}},{t:Finished}]},$ps:[],$cont:iterator$131,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$at','indexed','$o','indexes','$m','iterator','$o','iterator','$m','next']};};
                        })(iterator$131.$$.prototype);
                        var iterator$141=iterator$131({Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                        var getIterator$141=function(){
                            return iterator$141;
                        }
                        return getIterator$141();
                    };$$indexes$129.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$ps:[],$cont:indexes$129,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$at','indexed','$o','indexes','$m','iterator']};};
                })(indexes$129.$$.prototype);
                var indexes$142=indexes$129({Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                var getIndexes$142=function(){
                    return indexes$142;
                }
                return getIndexes$142();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$cont:Iterable,$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$at','indexed']};});$$iterable.following=function following(head$143,$$$mptypes){
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
                            defineAttr($$iterator$146,'first$147',function(){return this.first$147_;},function(first$148){return this.first$147_=first$148;},function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:iterator$146,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$m','following','$o','cons','$m','iterator','$o','iterator','$at','first']};});
                            $$iterator$146.next=function next(){
                                var $$iterator$146=this;
                                if($$iterator$146.first$147){
                                    $$iterator$146.first$147=false;
                                    return head$143;
                                }else {
                                    return iter$145.next();
                                }
                            };$$iterator$146.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element','Other',{t:Finished}]},$ps:[],$cont:iterator$146,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','following','$o','cons','$m','iterator','$o','iterator','$m','next']};};
                        })(iterator$146.$$.prototype);
                        var iterator$149=iterator$146({Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                        var getIterator$149=function(){
                            return iterator$149;
                        }
                        return getIterator$149();
                    };$$cons$144.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[],$cont:cons$144,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','following','$o','cons','$m','iterator']};};
                })(cons$144.$$.prototype);
                var cons$150=cons$144({Absent:{t:Nothing},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getCons$150=function(){
                    return cons$150;
                }
                return getCons$150();
            };$$iterable.following.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Nothing},Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'head',$mt:'prm',$t:'Other'}],$cont:Iterable,$tp:{Other:{}},$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','following']};};
            $$iterable.chain=function chain(other$151,$$$mptypes){
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
                    $$chained$152.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[],$cont:chained$152,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','chain','$o','chained','$m','iterator']};};
                })(chained$152.$$.prototype);
                var chained$153=chained$152({Absent:{ t:'i', l:[$$iterable.$$targs$$.Absent,$$$mptypes.OtherAbsent]},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getChained$153=function(){
                    return chained$153;
                }
                return getChained$153();
            };$$iterable.chain.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{ t:'i', l:['Absent','OtherAbsent']},Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}}}],$cont:Iterable,$tp:{Other:{},OtherAbsent:{'satisfies':[{t:Null}]}},$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','chain']};};
            $$iterable.defaultNullElements=function (defaultValue$154,$$$mptypes){
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
            $$iterable.defaultNullElements.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:{ t:'u', l:[{ t:'i', l:['Element',{t:Object$}]},'Default']}}},$ps:[{$nm:'defaultValue',$mt:'prm',$t:'Default'}],$cont:Iterable,$tp:{Default:{}},$an:function(){return[shared(),$default()];},d:['ceylon.language','Iterable','$m','defaultNullElements']};};
            defineAttr($$iterable,'string',function(){
                var $$iterable=this;
                if($$iterable.empty){
                    return String$("{}",2);
                }else {
                    var list$160=commaList($$iterable.taking((30)));
                    return StringBuilder().appendAll([String$("{ ",2),(opt$161=($$iterable.longerThan((30))?list$160.plus(String$(", ...",5)):null),opt$161!==null?opt$161:list$160).string,String$(" }",2)]).string;
                    var opt$161;
                }
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Iterable,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Iterable','$at','string']};});defineAttr($$iterable,'cycled',function(){
                var $$iterable=this;
                function iterable$162($$targs$$){
                    var $$iterable$162=new iterable$162.$$;
                    $$iterable$162.$$targs$$=$$targs$$;
                    Iterable({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element},$$iterable$162);
                    return $$iterable$162;
                }
                function $init$iterable$162(){
                    if (iterable$162.$$===undefined){
                        initTypeProto(iterable$162,'ceylon.language::Iterable.cycled.iterable',Basic,$init$Iterable());
                    }
                    return iterable$162;
                }
                $init$iterable$162();
                (function($$iterable$162){
                    defineAttr($$iterable$162,'orig$163',function(){
                        var $$iterable$162=this;
                        return $$iterable;
                    },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$cont:iterable$162,d:['ceylon.language','Iterable','$at','cycled','$o','iterable','$at','orig']};});
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
                                initTypeProto(iterator$164,'ceylon.language::Iterable.cycled.iterable.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$164;
                        }
                        $init$iterator$164();
                        (function($$iterator$164){
                            defineAttr($$iterator$164,'iter$165',function(){return this.iter$165_;},function(iter$166){return this.iter$165_=iter$166;},function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$164,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$at','cycled','$o','iterable','$m','iterator','$o','iterator','$at','iter']};});
                            $$iterator$164.next=function next(){
                                var $$iterator$164=this;
                                var next$167;
                                if(!isOfType((next$167=$$iterator$164.iter$165.next()),{t:Finished})){
                                    return next$167;
                                }else {
                                    $$iterator$164.iter$165=$$iterable$162.orig$163.iterator();
                                    return $$iterator$164.iter$165.next();
                                }
                            };$$iterator$164.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$164,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$at','cycled','$o','iterable','$m','iterator','$o','iterator','$m','next']};};
                        })(iterator$164.$$.prototype);
                        var iterator$168=iterator$164({Element:$$iterable.$$targs$$.Element});
                        var getIterator$168=function(){
                            return iterator$168;
                        }
                        return getIterator$168();
                    };$$iterable$162.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$162,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$at','cycled','$o','iterable','$m','iterator']};};
                })(iterable$162.$$.prototype);
                var iterable$169=iterable$162({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element});
                var getIterable$169=function(){
                    return iterable$169;
                }
                return getIterable$169();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$cont:Iterable,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$at','cycled']};});$$iterable.cycle=function cycle(times$170){
                var $$iterable=this;
                function iterable$171($$targs$$){
                    var $$iterable$171=new iterable$171.$$;
                    $$iterable$171.$$targs$$=$$targs$$;
                    Iterable({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element},$$iterable$171);
                    return $$iterable$171;
                }
                function $init$iterable$171(){
                    if (iterable$171.$$===undefined){
                        initTypeProto(iterable$171,'ceylon.language::Iterable.cycle.iterable',Basic,$init$Iterable());
                    }
                    return iterable$171;
                }
                $init$iterable$171();
                (function($$iterable$171){
                    defineAttr($$iterable$171,'orig$172',function(){
                        var $$iterable$171=this;
                        return $$iterable;
                    },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$cont:iterable$171,d:['ceylon.language','Iterable','$m','cycle','$o','iterable','$at','orig']};});
                    $$iterable$171.iterator=function iterator(){
                        var $$iterable$171=this;
                        function iterator$173($$targs$$){
                            var $$iterator$173=new iterator$173.$$;
                            $$iterator$173.$$targs$$=$$targs$$;
                            Iterator({Element:$$iterable.$$targs$$.Element},$$iterator$173);
                            $$iterator$173.iter$174_=getEmptyIterator();
                            $$iterator$173.count$175_=(0);
                            return $$iterator$173;
                        }
                        function $init$iterator$173(){
                            if (iterator$173.$$===undefined){
                                initTypeProto(iterator$173,'ceylon.language::Iterable.cycle.iterable.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$173;
                        }
                        $init$iterator$173();
                        (function($$iterator$173){
                            defineAttr($$iterator$173,'iter$174',function(){return this.iter$174_;},function(iter$176){return this.iter$174_=iter$176;},function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$173,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$m','cycle','$o','iterable','$m','iterator','$o','iterator','$at','iter']};});
                            defineAttr($$iterator$173,'count$175',function(){return this.count$175_;},function(count$177){return this.count$175_=count$177;},function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:iterator$173,$an:function(){return[variable()];},d:['ceylon.language','Iterable','$m','cycle','$o','iterable','$m','iterator','$o','iterator','$at','count']};});
                            $$iterator$173.next=function next(){
                                var $$iterator$173=this;
                                var next$178;
                                if(!isOfType((next$178=$$iterator$173.iter$174.next()),{t:Finished})){
                                    return next$178;
                                }else {
                                    if($$iterator$173.count$175.compare(times$170).equals(getSmaller())){
                                        (oldcount$179=$$iterator$173.count$175,$$iterator$173.count$175=oldcount$179.successor,oldcount$179);
                                        var oldcount$179;
                                        $$iterator$173.iter$174=$$iterable$171.orig$172.iterator();
                                    }else {
                                        $$iterator$173.iter$174=getEmptyIterator();
                                    }
                                    return $$iterator$173.iter$174.next();
                                }
                            };$$iterator$173.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$173,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','cycle','$o','iterable','$m','iterator','$o','iterator','$m','next']};};
                        })(iterator$173.$$.prototype);
                        var iterator$180=iterator$173({Element:$$iterable.$$targs$$.Element});
                        var getIterator$180=function(){
                            return iterator$180;
                        }
                        return getIterator$180();
                    };$$iterable$171.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:iterable$171,$an:function(){return[shared(),actual()];},d:['ceylon.language','Iterable','$m','cycle','$o','iterable','$m','iterator']};};
                })(iterable$171.$$.prototype);
                var iterable$181=iterable$171({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element});
                var getIterable$181=function(){
                    return iterable$181;
                }
                return getIterable$181();
            };$$iterable.cycle.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$ps:[{$nm:'times',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','cycle']};};
            $$iterable.repeat=function repeat(times$182){
                var $$iterable=this;
                var sb$183=SequenceBuilder({Element:$$iterable.$$targs$$.Element});
                var count$184=(0);
                var setCount$184=function(count$185){return count$184=count$185;};
                while((oldcount$186=count$184,count$184=oldcount$186.successor,oldcount$186).compare(times$182).equals(getSmaller())){
                    sb$183.appendAll($$iterable);
                }
                var oldcount$186;
                return sb$183.sequence;
            };$$iterable.repeat.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'times',$mt:'prm',$t:{t:Integer}}],$cont:Iterable,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Iterable','$m','repeat']};};
        })(Iterable.$$.prototype);
    }
    return Iterable;
}
exports.$init$Iterable=$init$Iterable;
$init$Iterable();
var opt$19,opt$159;
var commaList=function (elements$187){
    return (strings$188=Comprehension(function(){
        var it$189=elements$187.iterator();
        var element$190=getFinished();
        var next$element$190=function(){return element$190=it$189.next();}
        next$element$190();
        return function(){
            if(element$190!==getFinished()){
                var element$190$191=element$190;
                var tmpvar$192=(opt$193=(opt$194=element$190$191,opt$194!==null?opt$194.string:null),opt$193!==null?opt$193:String$("null",4));
                next$element$190();
                return tmpvar$192;
            }
            return getFinished();
        }
    },{Absent:{t:Null},Element:{t:String$}}),(opt$195=String$(", ",2),JsCallable(opt$195,opt$195!==null?opt$195.join:null))(strings$188));
};
commaList.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:String$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Anything}}}}],d:['ceylon.language','commaList']};};
var strings$188,opt$193,opt$194,opt$195;
function ifExists(predicate$196){
    return function(val$197){
        var val$198;
        if((val$198=val$197)!==null){
            return predicate$196(val$198);
        }else {
            return false;
        }
    }
};ifExists.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'predicate',$mt:'prm',$t:{t:Boolean$}}],d:['ceylon.language','ifExists']};};
function Sequential($$targs$$,$$sequential){
    List($$sequential.$$targs$$===undefined?$$targs$$:{Element:$$sequential.$$targs$$.Element},$$sequential);
    Ranged($$sequential.$$targs$$===undefined?$$targs$$:{Index:{t:Integer},Span:{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}}},$$sequential);
    add_type_arg($$sequential,'Index',{t:Integer});
    add_type_arg($$sequential,'Span',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
    Cloneable($$sequential.$$targs$$===undefined?$$targs$$:{Clone:{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}}},$$sequential);
    add_type_arg($$sequential,'Clone',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
    set_type_args($$sequential,$$targs$$);
}
Sequential.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out'}},satisfies:[{t:List,a:{Element:'Element'}},{t:Ranged,a:{Index:{t:Integer},Span:{t:Sequential,a:{Element:'Element'}}}},{t:Cloneable,a:{Clone:{t:Sequential,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:Tuple})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),shared()];},d:['ceylon.language','Sequential']};};
exports.Sequential=Sequential;
function $init$Sequential(){
    if (Sequential.$$===undefined){
        initTypeProto(Sequential,'ceylon.language::Sequential',$init$List(),$init$Ranged(),$init$Cloneable());
        (function($$sequential){
            defineAttr($$sequential,'sequence',function(){
                var $$sequential=this;
                return $$sequential;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$at','sequence']};});
            $$sequential.repeat=function (times$199){
                var $$sequential=this;
                return $$sequential.cycle(times$199).sequence;
            };
            $$sequential.repeat.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'times',$mt:'prm',$t:{t:Integer}}],$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$m','repeat']};};
            $$sequential.initial=function (length$200){
                var $$sequential=this;
                return $$sequential.segment((0),length$200);
            };
            $$sequential.initial.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$m','initial']};};
            $$sequential.terminal=function terminal(length$201){
                var $$sequential=this;
                var l$202;
                if((l$202=$$sequential.lastIndex)!==null&&length$201.compare((0)).equals(getLarger())){
                    return $$sequential.span(l$202.minus(length$201).plus((1)),l$202);
                }else {
                    return getEmpty();
                }
            };$$sequential.terminal.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$m','terminal']};};
            $$sequential.trim=function (trimming$203){
                var $$sequential=this;
                return $$sequential.getT$all()['ceylon.language::List'].$$.prototype.trim.call(this,$JsCallable(trimming$203,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequential.$$targs$$.Element,Element:$$sequential.$$targs$$.Element}},Return:{t:Boolean$}})).sequence;
            };
            $$sequential.trim.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$m','trim']};};
            $$sequential.trimLeading=function (trimming$204){
                var $$sequential=this;
                return $$sequential.getT$all()['ceylon.language::List'].$$.prototype.trimLeading.call(this,$JsCallable(trimming$204,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequential.$$targs$$.Element,Element:$$sequential.$$targs$$.Element}},Return:{t:Boolean$}})).sequence;
            };
            $$sequential.trimLeading.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$m','trimLeading']};};
            $$sequential.trimTrailing=function (trimming$205){
                var $$sequential=this;
                return $$sequential.getT$all()['ceylon.language::List'].$$.prototype.trimTrailing.call(this,$JsCallable(trimming$205,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequential.$$targs$$.Element,Element:$$sequential.$$targs$$.Element}},Return:{t:Boolean$}})).sequence;
            };
            $$sequential.trimTrailing.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$m','trimTrailing']};};
            defineAttr($$sequential,'clone',function(){
                var $$sequential=this;
                return $$sequential;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$at','clone']};});
            defineAttr($$sequential,'string',function(){
                var $$sequential=this;
                return (opt$206=($$sequential.empty?String$("[]",2):null),opt$206!==null?opt$206:StringBuilder().appendAll([String$("[",1),commaList($$sequential).string,String$("]",1)]).string);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Sequential,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequential','$at','string']};});
        })(Sequential.$$.prototype);
    }
    return Sequential;
}
exports.$init$Sequential=$init$Sequential;
$init$Sequential();
var opt$206;
function Sequence($$targs$$,$$sequence){
    Sequential($$sequence.$$targs$$===undefined?$$targs$$:{Element:$$sequence.$$targs$$.Element},$$sequence);
    Iterable($$sequence.$$targs$$===undefined?$$targs$$:{Absent:{t:Nothing},Element:$$sequence.$$targs$$.Element},$$sequence);
    Cloneable($$sequence.$$targs$$===undefined?$$targs$$:{Clone:{t:Sequence,a:{Element:$$sequence.$$targs$$.Element}}},$$sequence);
    add_type_arg($$sequence,'Clone',{t:Sequence,a:{Element:$$sequence.$$targs$$.Element}});
    set_type_args($$sequence,$$targs$$);
}
Sequence.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out'}},satisfies:[{t:Sequential,a:{Element:'Element'}},{t:Iterable,a:{Absent:{t:Nothing},Element:'Element'}},{t:Cloneable,a:{Clone:{t:Sequence,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:Empty})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Sequence']};};
exports.Sequence=Sequence;
function $init$Sequence(){
    if (Sequence.$$===undefined){
        initTypeProto(Sequence,'ceylon.language::Sequence',$init$Sequential(),$init$Iterable(),$init$Cloneable());
        (function($$sequence){
            defineAttr($$sequence,'empty',function(){
                var $$sequence=this;
                return false;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Sequence,$an:function(){return[shared(),actual()];},d:['ceylon.language','Sequence','$at','empty']};});
            defineAttr($$sequence,'sequence',function(){
                var $$sequence=this;
                return $$sequence;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$cont:Sequence,$an:function(){return[shared(),$default(),actual()];},d:['ceylon.language','Sequence','$at','sequence']};});
            $$sequence.$sort=function $sort(comparing$207){
                var $$sequence=this;
                var s$208=internalSort($JsCallable(comparing$207,[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:{t:Comparison}}),$$sequence,{Element:$$sequence.$$targs$$.Element});
                //assert at Sequence.ceylon (63:8-63:27)
                var s$209;
                if (!(nonempty((s$209=s$208)))) {throw wrapexc(AssertionException("Assertion failed: \'nonempty s\' at Sequence.ceylon (63:15-63:26)"),'63:8-63:27','Sequence.ceylon'); }
                return s$209;
            };$$sequence.$sort.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Sequence,$an:function(){return[shared(),$default(),actual()];},d:['ceylon.language','Sequence','$m','sort']};};
            $$sequence.collect=function collect(collecting$210,$$$mptypes){
                var $$sequence=this;
                var s$211=$$sequence.$map($JsCallable(collecting$210,[{$nm:'element',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:$$$mptypes.Result}),{Result:$$$mptypes.Result}).sequence;
                //assert at Sequence.ceylon (74:8-74:27)
                var s$212;
                if (!(nonempty((s$212=s$211)))) {throw wrapexc(AssertionException("Assertion failed: \'nonempty s\' at Sequence.ceylon (74:15-74:26)"),'74:8-74:27','Sequence.ceylon'); }
                return s$212;
            };$$sequence.collect.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Sequence,$tp:{Result:{}},$an:function(){return[shared(),$default(),actual()];},d:['ceylon.language','Sequence','$m','collect']};};
            defineAttr($$sequence,'clone',function(){
                var $$sequence=this;
                return $$sequence;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequence','$at','clone']};});
            defineAttr($$sequence,'string',function(){
                var $$sequence=this;
                return attrGetter($$sequence.getT$all()['ceylon.language::Sequential'],'string').call(this);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequence','$at','string']};});
            $$sequence.shorterThan=function (length$213){
                var $$sequence=this;
                return $$sequence.getT$all()['ceylon.language::List'].$$.prototype.shorterThan.call(this,length$213);
            };
            $$sequence.shorterThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequence','$m','shorterThan']};};
            $$sequence.longerThan=function (length$214){
                var $$sequence=this;
                return $$sequence.getT$all()['ceylon.language::List'].$$.prototype.longerThan.call(this,length$214);
            };
            $$sequence.longerThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequence','$m','longerThan']};};
            $$sequence.findLast=function (selecting$215){
                var $$sequence=this;
                return $$sequence.getT$all()['ceylon.language::List'].$$.prototype.findLast.call(this,$JsCallable(selecting$215,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$sequence.findLast.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Sequence,$an:function(){return[shared(),$default(),actual()];},d:['ceylon.language','Sequence','$m','findLast']};};
            $$sequence.repeat=function (times$216){
                var $$sequence=this;
                return $$sequence.getT$all()['ceylon.language::Sequential'].$$.prototype.repeat.call(this,times$216);
            };
            $$sequence.repeat.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'times',$mt:'prm',$t:{t:Integer}}],$cont:Sequence,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Sequence','$m','repeat']};};
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
Empty.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:Sequential,a:{Element:{t:Nothing}}},{t:Ranged,a:{Index:{t:Integer},Span:{t:Empty}}},{t:Cloneable,a:{Clone:{t:Empty}}}],$an:function(){return[see([typeLiteral$model({Type:Sequence})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared()];},d:['ceylon.language','Empty']};};
exports.Empty=Empty;
function $init$Empty(){
    if (Empty.$$===undefined){
        initTypeProto(Empty,'ceylon.language::Empty',$init$Sequential(),$init$Ranged(),$init$Cloneable());
        (function($$empty){
            $$empty.iterator=function (){
                var $$empty=this;
                return getEmptyIterator();
            };
            $$empty.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{t:Nothing}}},$ps:[],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','iterator']};};
            $$empty.get=function (index$217){
                var $$empty=this;
                return null;
            };
            $$empty.get.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Null},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','get']};};
            $$empty.segment=function (from$218,length$219){
                var $$empty=this;
                return $$empty;
            };
            $$empty.segment.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','segment']};};
            $$empty.span=function (from$220,to$221){
                var $$empty=this;
                return $$empty;
            };
            $$empty.span.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','span']};};
            $$empty.spanTo=function (to$222){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanTo.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','spanTo']};};
            $$empty.spanFrom=function (from$223){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanFrom.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','spanFrom']};};
            defineAttr($$empty,'empty',function(){
                var $$empty=this;
                return true;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','empty']};});
            defineAttr($$empty,'size',function(){
                var $$empty=this;
                return (0);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','size']};});
            defineAttr($$empty,'reversed',function(){
                var $$empty=this;
                return $$empty;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','reversed']};});
            defineAttr($$empty,'sequence',function(){
                var $$empty=this;
                return $$empty;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','sequence']};});
            defineAttr($$empty,'string',function(){
                var $$empty=this;
                return String$("{}",2);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','string']};});
            defineAttr($$empty,'lastIndex',function(){
                var $$empty=this;
                return null;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Null},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','lastIndex']};});
            defineAttr($$empty,'first',function(){
                var $$empty=this;
                return null;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Null},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','first']};});
            defineAttr($$empty,'last',function(){
                var $$empty=this;
                return null;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Null},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','last']};});
            defineAttr($$empty,'rest',function(){
                var $$empty=this;
                return $$empty;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','rest']};});
            defineAttr($$empty,'clone',function(){
                var $$empty=this;
                return $$empty;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','clone']};});
            defineAttr($$empty,'coalesced',function(){
                var $$empty=this;
                return $$empty;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','coalesced']};});
            defineAttr($$empty,'indexed',function(){
                var $$empty=this;
                return $$empty;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$at','indexed']};});
            $$empty.chain=function (other$224,$$$mptypes){
                var $$empty=this;
                return other$224;
            };
            $$empty.chain.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}}}],$cont:Empty,$tp:{Other:{},OtherAbsent:{'satisfies':[{t:Null}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','chain']};};
            $$empty.contains=function (element$225){
                var $$empty=this;
                return false;
            };
            $$empty.contains.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','contains']};};
            $$empty.count=function (selecting$226){
                var $$empty=this;
                return (0);
            };
            $$empty.count.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','count']};};
            $$empty.defines=function (index$227){
                var $$empty=this;
                return false;
            };
            $$empty.defines.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','defines']};};
            $$empty.$map=function (collecting$228,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$map.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Empty,$tp:{Result:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','map']};};
            $$empty.$filter=function (selecting$229){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$filter.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','filter']};};
            $$empty.fold=function (initial$230,accumulating$231,$$$mptypes){
                var $$empty=this;
                return initial$230;
            };
            $$empty.fold.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$cont:Empty,$tp:{Result:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','fold']};};
            $$empty.find=function (selecting$232){
                var $$empty=this;
                return null;
            };
            $$empty.find.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Null},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','find']};};
            $$empty.$sort=function (comparing$233){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$sort.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','sort']};};
            $$empty.collect=function (collecting$234,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.collect.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$cont:Empty,$tp:{Result:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','collect']};};
            $$empty.select=function (selecting$235){
                var $$empty=this;
                return $$empty;
            };
            $$empty.select.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','select']};};
            $$empty.any=function (selecting$236){
                var $$empty=this;
                return false;
            };
            $$empty.any.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','any']};};
            $$empty.$every=function (selecting$237){
                var $$empty=this;
                return false;
            };
            $$empty.$every.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','every']};};
            $$empty.skipping=function (skip$238){
                var $$empty=this;
                return $$empty;
            };
            $$empty.skipping.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','skipping']};};
            $$empty.taking=function (take$239){
                var $$empty=this;
                return $$empty;
            };
            $$empty.taking.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','taking']};};
            $$empty.by=function (step$240){
                var $$empty=this;
                return $$empty;
            };
            $$empty.by.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$cont:Empty,$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','by']};};
            $$empty.withLeading=function (element$241,$$$mptypes){
                var $$empty=this;
                return Tuple(element$241,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withLeading.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:{t:Empty},First:'Element',Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:'Element'}],$cont:Empty,$tp:{Element:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','withLeading']};};
            $$empty.withTrailing=function (element$242,$$$mptypes){
                var $$empty=this;
                return Tuple(element$242,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withTrailing.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:{t:Empty},First:'Element',Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:'Element'}],$cont:Empty,$tp:{Element:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','withTrailing']};};
            $$empty.following=function (head$243,$$$mptypes){
                var $$empty=this;
                return Singleton(head$243,{Element:$$$mptypes.Other});
            };
            $$empty.following.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Other'}},$ps:[{$nm:'head',$mt:'prm',$t:'Other'}],$cont:Empty,$tp:{Other:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Empty','$m','following']};};
        })(Empty.$$.prototype);
    }
    return Empty;
}
exports.$init$Empty=$init$Empty;
$init$Empty();
function empty$244(){
    var $$empty=new empty$244.$$;
    Object$($$empty);
    Empty($$empty);
    return $$empty;
}
function $init$empty$244(){
    if (empty$244.$$===undefined){
        initTypeProto(empty$244,'ceylon.language::empty',Object$,$init$Empty());
    }
    return empty$244;
}
exports.$init$empty$244=$init$empty$244;
$init$empty$244();
var empty$245=empty$244();
var getEmpty=function(){
    return empty$245;
}
exports.getEmpty=getEmpty;
exports.getEmpty.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:empty$244},$an:function(){return[shared()];},d:['ceylon.language','empty']};};
function emptyIterator$246($$targs$$){
    var $$emptyIterator=new emptyIterator$246.$$;
    $$emptyIterator.$$targs$$=$$targs$$;
    Iterator({Element:{t:Nothing}},$$emptyIterator);
    return $$emptyIterator;
}
function $init$emptyIterator$246(){
    if (emptyIterator$246.$$===undefined){
        initTypeProto(emptyIterator$246,'ceylon.language::emptyIterator',Basic,$init$Iterator());
    }
    return emptyIterator$246;
}
exports.$init$emptyIterator$246=$init$emptyIterator$246;
$init$emptyIterator$246();
(function($$emptyIterator){
    $$emptyIterator.next=function (){
        var $$emptyIterator=this;
        return getFinished();
    };
})(emptyIterator$246.$$.prototype);
var emptyIterator$247=emptyIterator$246({Element:{t:Nothing}});
var getEmptyIterator=function(){
    return emptyIterator$247;
}
exports.getEmptyIterator=getEmptyIterator;
exports.getEmptyIterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:emptyIterator$246},$an:function(){return[shared()];},d:['ceylon.language','emptyIterator']};};
function Keys(correspondence$248, $$targs$$,$$keys){
    $init$Keys();
    if ($$keys===undefined)$$keys=new Keys.$$;
    set_type_args($$keys,$$targs$$);
    $$keys.correspondence$248_=correspondence$248;
    Category($$keys);
    return $$keys;
}
Keys.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Key:{'var':'in','satisfies':[{t:Object$}]},Item:{'var':'out'}},satisfies:[{t:Category}],d:['ceylon.language','Keys']};};
function $init$Keys(){
    if (Keys.$$===undefined){
        initTypeProto(Keys,'ceylon.language::Keys',Basic,$init$Category());
        (function($$keys){
            $$keys.contains=function contains(key$249){
                var $$keys=this;
                var key$250;
                if(isOfType((key$250=key$249),$$keys.$$targs$$.Key)){
                    return $$keys.correspondence$248.defines(key$250);
                }else {
                    return false;
                }
            };$$keys.contains.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'key',$mt:'prm',$t:{t:Object$}}],$cont:Keys,$an:function(){return[shared(),actual()];},d:['ceylon.language','Keys','$m','contains']};};
            defineAttr($$keys,'correspondence$248',function(){return this.correspondence$248_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Correspondence,a:{Key:'Key',Item:'Item'}},$cont:Keys,d:['ceylon.language','Keys','$at','correspondence']};});
        })(Keys.$$.prototype);
    }
    return Keys;
}
exports.$init$Keys=$init$Keys;
$init$Keys();
function Correspondence($$targs$$,$$correspondence){
    set_type_args($$correspondence,$$targs$$);
}
Correspondence.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Key:{'var':'in','satisfies':[{t:Object$}]},Item:{'var':'out'}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:Map}),typeLiteral$model({Type:List}),typeLiteral$model({Type:Category})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Correspondence']};};
exports.Correspondence=Correspondence;
function $init$Correspondence(){
    if (Correspondence.$$===undefined){
        initTypeProto(Correspondence,'ceylon.language::Correspondence');
        (function($$correspondence){
            $$correspondence.defines=function (key$251){
                var $$correspondence=this;
                return exists($$correspondence.get(key$251));
            };
            $$correspondence.defines.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'key',$mt:'prm',$t:'Key'}],$cont:Correspondence,$an:function(){return[see([,,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:FunctionDeclaration$model$declaration},{t:ValueDeclaration$model$declaration}]}})),shared(),$default()];},d:['ceylon.language','Correspondence','$m','defines']};};
            defineAttr($$correspondence,'keys',function(){
                var $$correspondence=this;
                return Keys($$correspondence,{Key:$$correspondence.$$targs$$.Key,Item:$$correspondence.$$targs$$.Item});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Category},$cont:Correspondence,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Correspondence','$at','keys']};});
            $$correspondence.definesEvery=function definesEvery(keys$252){
                var $$correspondence=this;
                var it$253 = keys$252.iterator();
                var key$254;while ((key$254=it$253.next())!==getFinished()){
                    if((!$$correspondence.defines(key$254))){
                        return false;
                    }
                }
                if (getFinished() === key$254){
                    return true;
                }
            };$$correspondence.definesEvery.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$cont:Correspondence,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Correspondence','$m','definesEvery']};};
            $$correspondence.definesAny=function definesAny(keys$255){
                var $$correspondence=this;
                var it$256 = keys$255.iterator();
                var key$257;while ((key$257=it$256.next())!==getFinished()){
                    if($$correspondence.defines(key$257)){
                        return true;
                    }
                }
                if (getFinished() === key$257){
                    return false;
                }
            };$$correspondence.definesAny.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$cont:Correspondence,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Correspondence','$m','definesAny']};};
            $$correspondence.items=function (keys$258){
                var $$correspondence=this;
                return Comprehension(function(){
                    var it$259=keys$258.iterator();
                    var key$260=getFinished();
                    var next$key$260=function(){return key$260=it$259.next();}
                    next$key$260();
                    return function(){
                        if(key$260!==getFinished()){
                            var key$260$261=key$260;
                            var tmpvar$262=$$correspondence.get(key$260$261);
                            next$key$260();
                            return tmpvar$262;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'u', l:[{t:Null},$$correspondence.$$targs$$.Item]}}).sequence;
            };
            $$correspondence.items.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{ t:'u', l:[{t:Null},'Item']}}},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$cont:Correspondence,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Correspondence','$m','items']};};
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
Finished.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:Iterator})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared(),abstract()];},d:['ceylon.language','Finished']};};
exports.Finished=Finished;
function $init$Finished(){
    if (Finished.$$===undefined){
        initTypeProto(Finished,'ceylon.language::Finished',Basic);
    }
    return Finished;
}
exports.$init$Finished=$init$Finished;
$init$Finished();
function finished$263(){
    var $$finished=new finished$263.$$;
    Finished($$finished);
    return $$finished;
}
function $init$finished$263(){
    if (finished$263.$$===undefined){
        initTypeProto(finished$263,'ceylon.language::finished',Finished);
    }
    return finished$263;
}
exports.$init$finished$263=$init$finished$263;
$init$finished$263();
(function($$finished){
    defineAttr($$finished,'string',function(){
        var $$finished=this;
        return String$("finished",8);
    },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:finished$263,$an:function(){return[shared(),actual()];},d:['ceylon.language','finished','$at','string']};});
})(finished$263.$$.prototype);
var finished$264=finished$263();
var getFinished=function(){
    return finished$264;
}
exports.getFinished=getFinished;
exports.getFinished.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:finished$263},$an:function(){return[see([typeLiteral$model({Type:Iterator})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared()];},d:['ceylon.language','finished']};};
function Binary($$targs$$,$$binary){
    set_type_args($$binary,$$targs$$);
    $$binary.set$defs$bit=function(index$265,bit$266){return true;};
}
Binary.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Binary,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:Integer})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),by([String$("Stef",4)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Binary']};};
exports.Binary=Binary;
function $init$Binary(){
    if (Binary.$$===undefined){
        initTypeProto(Binary,'ceylon.language::Binary');
        (function($$binary){
            $$binary.clear=function clear(index$267){
                var $$binary=this;
                return $$binary.set(index$267,false);
            };$$binary.clear.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Other',$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Binary,$an:function(){return[shared(),$default()];},d:['ceylon.language','Binary','$m','clear']};};
        })(Binary.$$.prototype);
    }
    return Binary;
}
exports.$init$Binary=$init$Binary;
$init$Binary();
function Cloneable($$targs$$,$$cloneable){
    set_type_args($$cloneable,$$targs$$);
}
Cloneable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Clone:{'var':'out','satisfies':[{t:Cloneable,a:{Clone:'Clone'}}]}},satisfies:[],$an:function(){return[shared()];},d:['ceylon.language','Cloneable']};};
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
Closeable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language','Closeable']};};
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
Ranged.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Index:{'var':'in','satisfies':[{t:Comparable,a:{Other:'Index'}}]},Span:{'var':'out'}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:List}),typeLiteral$model({Type:Sequence}),typeLiteral$model({Type:String})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','Ranged']};};
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
Container.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out'},Absent:{'var':'out','satisfies':[{t:Null}],'def':{t:Null}}},satisfies:[{t:Category}],$an:function(){return[see([typeLiteral$model({Type:Category})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),deprecated(String$("Will be removed in Ceylon 1.0.",30)),shared()];},d:['ceylon.language','Container']};};
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
Iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out'}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:Iterable})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Iterator']};};
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
Collection.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out'}},satisfies:[{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},{t:Cloneable,a:{Clone:{t:Collection,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:List}),typeLiteral$model({Type:Map}),typeLiteral$model({Type:Set})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared()];},d:['ceylon.language','Collection']};};
exports.Collection=Collection;
function $init$Collection(){
    if (Collection.$$===undefined){
        initTypeProto(Collection,'ceylon.language::Collection',$init$Iterable(),$init$Cloneable());
        (function($$collection){
            defineAttr($$collection,'empty',function(){
                var $$collection=this;
                return $$collection.size.equals((0));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Collection,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Collection','$at','empty']};});
            $$collection.contains=function contains(element$268){
                var $$collection=this;
                var it$269 = $$collection.iterator();
                var elem$270;while ((elem$270=it$269.next())!==getFinished()){
                    var elem$271;
                    if((elem$271=elem$270)!==null&&elem$271.equals(element$268)){
                        return true;
                    }
                }
                if (getFinished() === elem$270){
                    return false;
                }
            };$$collection.contains.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Collection,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Collection','$m','contains']};};
            defineAttr($$collection,'string',function(){
                var $$collection=this;
                return (opt$272=($$collection.empty?String$("{}",2):null),opt$272!==null?opt$272:StringBuilder().appendAll([String$("{ ",2),commaList($$collection).string,String$(" }",2)]).string);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Collection,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Collection','$at','string']};});
        })(Collection.$$.prototype);
    }
    return Collection;
}
exports.$init$Collection=$init$Collection;
$init$Collection();
var opt$272;
function Category($$category){
}
Category.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[see([typeLiteral$model({Type:Container})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Category']};};
exports.Category=Category;
function $init$Category(){
    if (Category.$$===undefined){
        initTypeProto(Category,'ceylon.language::Category');
        (function($$category){
            $$category.containsEvery=function containsEvery(elements$273){
                var $$category=this;
                var it$274 = elements$273.iterator();
                var element$275;while ((element$275=it$274.next())!==getFinished()){
                    if((!$$category.contains(element$275))){
                        return false;
                    }
                }
                if (getFinished() === element$275){
                    return true;
                }
            };$$category.containsEvery.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Object$}}}}],$cont:Category,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Category','$m','containsEvery']};};
            $$category.containsAny=function containsAny(elements$276){
                var $$category=this;
                var it$277 = elements$276.iterator();
                var element$278;while ((element$278=it$277.next())!==getFinished()){
                    if($$category.contains(element$278)){
                        return true;
                    }
                }
                if (getFinished() === element$278){
                    return false;
                }
            };$$category.containsAny.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Object$}}}}],$cont:Category,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','Category','$m','containsAny']};};
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
List.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out'}},satisfies:[{t:Collection,a:{Element:'Element'}},{t:Correspondence,a:{Key:{t:Integer},Item:'Element'}},{t:Ranged,a:{Index:{t:Integer},Span:{t:List,a:{Element:'Element'}}}},{t:Cloneable,a:{Clone:{t:List,a:{Element:'Element'}}}}],$an:function(){return[see([typeLiteral$model({Type:Sequence}),typeLiteral$model({Type:Empty}),typeLiteral$model({Type:Array})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','List']};};
exports.List=List;
function $init$List(){
    if (List.$$===undefined){
        initTypeProto(List,'ceylon.language::List',$init$Collection(),$init$Correspondence(),$init$Ranged(),$init$Cloneable());
        (function($$list){
            defineAttr($$list,'size',function(){
                var $$list=this;
                return (opt$279=$$list.lastIndex,opt$279!==null?opt$279:(-(1))).plus((1));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:List,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:ValueDeclaration$model$declaration}})),shared(),actual(),$default()];},d:['ceylon.language','List','$at','size']};});
            $$list.shorterThan=function (length$280){
                var $$list=this;
                return $$list.size.compare(length$280).equals(getSmaller());
            };
            $$list.shorterThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$m','shorterThan']};};
            $$list.longerThan=function (length$281){
                var $$list=this;
                return $$list.size.compare(length$281).equals(getLarger());
            };
            $$list.longerThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$m','longerThan']};};
            $$list.defines=function (index$282){
                var $$list=this;
                return (index$282.compare((opt$283=$$list.lastIndex,opt$283!==null?opt$283:(-(1))))!==getLarger());
            };
            $$list.defines.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$m','defines']};};
            $$list.iterator=function iterator(){
                var $$list=this;
                function listIterator$284($$targs$$){
                    var $$listIterator$284=new listIterator$284.$$;
                    $$listIterator$284.$$targs$$=$$targs$$;
                    Iterator({Element:$$list.$$targs$$.Element},$$listIterator$284);
                    $$listIterator$284.index$285_=(0);
                    return $$listIterator$284;
                }
                function $init$listIterator$284(){
                    if (listIterator$284.$$===undefined){
                        initTypeProto(listIterator$284,'ceylon.language::List.iterator.listIterator',Basic,$init$Iterator());
                    }
                    return listIterator$284;
                }
                $init$listIterator$284();
                (function($$listIterator$284){
                    defineAttr($$listIterator$284,'index$285',function(){return this.index$285_;},function(index$286){return this.index$285_=index$286;},function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:listIterator$284,$an:function(){return[variable()];},d:['ceylon.language','List','$m','iterator','$o','listIterator','$at','index']};});
                    $$listIterator$284.next=function next(){
                        var $$listIterator$284=this;
                        if(($$listIterator$284.index$285.compare((opt$287=$$list.lastIndex,opt$287!==null?opt$287:(-(1))))!==getLarger())){
                            //assert at List.ceylon (67:20-67:65)
                            var elem$288;
                            if (!(isOfType((elem$288=$$list.get((oldindex$289=$$listIterator$284.index$285,$$listIterator$284.index$285=oldindex$289.successor,oldindex$289))),$$list.$$targs$$.Element))) {throw wrapexc(AssertionException("Assertion failed: \'is Element elem = outer.get(index++)\' at List.ceylon (67:27-67:64)"),'67:20-67:65','List.ceylon'); }
                            var oldindex$289;
                            return elem$288;
                        }else {
                            return getFinished();
                        }
                        var opt$287;
                    };$$listIterator$284.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:listIterator$284,$an:function(){return[shared(),actual()];},d:['ceylon.language','List','$m','iterator','$o','listIterator','$m','next']};};
                })(listIterator$284.$$.prototype);
                var listIterator$290=listIterator$284({Element:$$list.$$targs$$.Element});
                var getListIterator$290=function(){
                    return listIterator$290;
                }
                return getListIterator$290();
            };$$list.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$m','iterator']};};
            $$list.equals=function equals(that$291){
                var $$list=this;
                var that$292;
                if(isOfType((that$292=that$291),{t:List,a:{Element:{t:Anything}}})){
                    if(that$292.size.equals($$list.size)){
                        var it$293 = Range((0),$$list.size.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$294;while ((i$294=it$293.next())!==getFinished()){
                            var x$295=$$list.get(i$294);
                            var y$296=that$292.get(i$294);
                            var x$297;
                            if((x$297=x$295)!==null){
                                var y$298;
                                if((y$298=y$296)!==null){
                                    if((!x$297.equals(y$298))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$299;
                                if((y$299=y$296)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$294){
                            return true;
                        }
                    }
                }
                return false;
            };$$list.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$m','equals']};};
            defineAttr($$list,'hash',function(){
                var $$list=this;
                var hash$300=(1);
                var setHash$300=function(hash$301){return hash$300=hash$301;};
                var it$302 = $$list.iterator();
                var elem$303;while ((elem$303=it$302.next())!==getFinished()){
                    (hash$300=hash$300.times((31)));
                    var elem$304;
                    if((elem$304=elem$303)!==null){
                        (hash$300=hash$300.plus(elem$304.hash));
                    }
                }
                return hash$300;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$at','hash']};});$$list.findLast=function findLast(selecting$305){
                var $$list=this;
                var l$306;
                if((l$306=$$list.lastIndex)!==null){
                    var index$307=l$306;
                    var setIndex$307=function(index$308){return index$307=index$308;};
                    while((index$307.compare((0))!==getSmaller())){
                        var elem$309;
                        if((elem$309=$$list.get((oldindex$310=index$307,index$307=oldindex$310.predecessor,oldindex$310)))!==null){
                            if(selecting$305(elem$309)){
                                return elem$309;
                            }
                        }
                        var oldindex$310;
                    }
                }
                return null;
            };$$list.findLast.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default(),actual()];},d:['ceylon.language','List','$m','findLast']};};
            defineAttr($$list,'first',function(){
                var $$list=this;
                return $$list.get((0));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$at','first']};});
            defineAttr($$list,'last',function(){
                var $$list=this;
                var i$311;
                if((i$311=$$list.lastIndex)!==null){
                    return $$list.get(i$311);
                }else {
                    return null;
                }
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:List,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','List','$at','last']};});$$list.withLeading=function withLeading(element$312,$$$mptypes){
                var $$list=this;
                var sb$313=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                sb$313.append(element$312);
                if((!$$list.empty)){
                    sb$313.appendAll($$list);
                }
                //assert at List.ceylon (169:8-169:41)
                var seq$314;
                if (!(nonempty((seq$314=sb$313.sequence)))) {throw wrapexc(AssertionException("Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (169:15-169:40)"),'169:8-169:41','List.ceylon'); }
                return seq$314;
            };$$list.withLeading.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$cont:List,$tp:{Other:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','List','$m','withLeading']};};
            $$list.withTrailing=function withTrailing(element$315,$$$mptypes){
                var $$list=this;
                var sb$316=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                if((!$$list.empty)){
                    sb$316.appendAll($$list);
                }
                sb$316.append(element$315);
                //assert at List.ceylon (184:8-184:41)
                var seq$317;
                if (!(nonempty((seq$317=sb$316.sequence)))) {throw wrapexc(AssertionException("Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (184:15-184:40)"),'184:8-184:41','List.ceylon'); }
                return seq$317;
            };$$list.withTrailing.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$cont:List,$tp:{Other:{}},$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','withTrailing']};};
            $$list.startsWith=function (sublist$318){
                var $$list=this;
                return $$list.includesAt((0),sublist$318);
            };
            $$list.startsWith.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','startsWith']};};
            $$list.endsWith=function (sublist$319){
                var $$list=this;
                return $$list.includesAt($$list.size.minus(sublist$319.size),sublist$319);
            };
            $$list.endsWith.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','endsWith']};};
            $$list.includesAt=function includesAt(index$320,sublist$321){
                var $$list=this;
                var it$322 = (function(){var tmpvar$324=sublist$321.size;
                if (tmpvar$324>0){
                var tmpvar$325=(0);
                var tmpvar$326=tmpvar$325;
                for (var i=1; i<tmpvar$324; i++){tmpvar$326=tmpvar$326.successor;}
                return Range(tmpvar$325,tmpvar$326,{Element:{t:Integer}})
                }else return getEmpty();}()).iterator();
                var i$323;while ((i$323=it$322.next())!==getFinished()){
                    var x$327=$$list.get(index$320.plus(i$323));
                    var y$328=sublist$321.get(i$323);
                    var x$329;
                    if((x$329=x$327)!==null){
                        var y$330;
                        if((y$330=y$328)!==null){
                            if((!x$329.equals(y$330))){
                                return false;
                            }
                        }else {
                            return false;
                        }
                    }else {
                        var y$331;
                        if((y$331=y$328)!==null){
                            return false;
                        }
                    }
                }
                if (getFinished() === i$323){
                    return true;
                }
            };$$list.includesAt.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}},{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','includesAt']};};
            $$list.includes=function includes(sublist$332){
                var $$list=this;
                var it$333 = (function(){var tmpvar$335=$$list.size;
                if (tmpvar$335>0){
                var tmpvar$336=(0);
                var tmpvar$337=tmpvar$336;
                for (var i=1; i<tmpvar$335; i++){tmpvar$337=tmpvar$337.successor;}
                return Range(tmpvar$336,tmpvar$337,{Element:{t:Integer}})
                }else return getEmpty();}()).iterator();
                var index$334;while ((index$334=it$333.next())!==getFinished()){
                    if($$list.includesAt(index$334,sublist$332)){
                        return true;
                    }
                }
                return false;
            };$$list.includes.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','includes']};};
            $$list.inclusions=function (sublist$338){
                var $$list=this;
                return Comprehension(function(){
                    var it$339=(function(){var tmpvar$341=$$list.size;
                    if (tmpvar$341>0){
                    var tmpvar$342=(0);
                    var tmpvar$343=tmpvar$342;
                    for (var i=1; i<tmpvar$341; i++){tmpvar$343=tmpvar$343.successor;}
                    return Range(tmpvar$342,tmpvar$343,{Element:{t:Integer}})
                    }else return getEmpty();}()).iterator();
                    var index$340=getFinished();
                    var next$index$340=function(){
                        while((index$340=it$339.next())!==getFinished()){
                            if($$list.includesAt(index$340,sublist$338)){
                                return index$340;
                            }
                        }
                        return getFinished();
                    }
                    next$index$340();
                    return function(){
                        if(index$340!==getFinished()){
                            var index$340$344=index$340;
                            var tmpvar$345=index$340$344;
                            next$index$340();
                            return tmpvar$345;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Integer}});
            };
            $$list.inclusions.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Integer}}},$ps:[{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','inclusions']};};
            $$list.firstInclusion=function firstInclusion(sublist$346){
                var $$list=this;
                var it$347 = (function(){var tmpvar$349=$$list.size;
                if (tmpvar$349>0){
                var tmpvar$350=(0);
                var tmpvar$351=tmpvar$350;
                for (var i=1; i<tmpvar$349; i++){tmpvar$351=tmpvar$351.successor;}
                return Range(tmpvar$350,tmpvar$351,{Element:{t:Integer}})
                }else return getEmpty();}()).iterator();
                var index$348;while ((index$348=it$347.next())!==getFinished()){
                    if($$list.includesAt(index$348,sublist$346)){
                        return index$348;
                    }
                }
                if (getFinished() === index$348){
                    return null;
                }
            };$$list.firstInclusion.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','firstInclusion']};};
            $$list.lastInclusion=function lastInclusion(sublist$352){
                var $$list=this;
                var it$353 = (function(){var tmpvar$355=$$list.size;
                if (tmpvar$355>0){
                var tmpvar$356=(0);
                var tmpvar$357=tmpvar$356;
                for (var i=1; i<tmpvar$355; i++){tmpvar$357=tmpvar$357.successor;}
                return Range(tmpvar$356,tmpvar$357,{Element:{t:Integer}})
                }else return getEmpty();}()).reversed.iterator();
                var index$354;while ((index$354=it$353.next())!==getFinished()){
                    if($$list.includesAt(index$354,sublist$352)){
                        return index$354;
                    }
                }
                if (getFinished() === index$354){
                    return null;
                }
            };$$list.lastInclusion.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','lastInclusion']};};
            $$list.occursAt=function occursAt(index$358,element$359){
                var $$list=this;
                var elem$360=$$list.get(index$358);
                var element$361;
                if((element$361=element$359)!==null){
                    var elem$362;
                    if((elem$362=elem$360)!==null){
                        return elem$362.equals(element$361);
                    }else {
                        return false;
                    }
                }else {
                    return exists(elem$360);
                }
            };$$list.occursAt.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}},{$nm:'element',$mt:'prm',$t:{t:Anything}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','occursAt']};};
            $$list.occurs=function occurs(element$363){
                var $$list=this;
                var it$364 = (function(){var tmpvar$366=$$list.size;
                if (tmpvar$366>0){
                var tmpvar$367=(0);
                var tmpvar$368=tmpvar$367;
                for (var i=1; i<tmpvar$366; i++){tmpvar$368=tmpvar$368.successor;}
                return Range(tmpvar$367,tmpvar$368,{Element:{t:Integer}})
                }else return getEmpty();}()).iterator();
                var index$365;while ((index$365=it$364.next())!==getFinished()){
                    if($$list.occursAt(index$365,element$363)){
                        return true;
                    }
                }
                return false;
            };$$list.occurs.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Anything}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','occurs']};};
            $$list.occurrences=function (element$369){
                var $$list=this;
                return Comprehension(function(){
                    var it$370=(function(){var tmpvar$372=$$list.size;
                    if (tmpvar$372>0){
                    var tmpvar$373=(0);
                    var tmpvar$374=tmpvar$373;
                    for (var i=1; i<tmpvar$372; i++){tmpvar$374=tmpvar$374.successor;}
                    return Range(tmpvar$373,tmpvar$374,{Element:{t:Integer}})
                    }else return getEmpty();}()).iterator();
                    var index$371=getFinished();
                    var next$index$371=function(){
                        while((index$371=it$370.next())!==getFinished()){
                            if($$list.occursAt(index$371,element$369)){
                                return index$371;
                            }
                        }
                        return getFinished();
                    }
                    next$index$371();
                    return function(){
                        if(index$371!==getFinished()){
                            var index$371$375=index$371;
                            var tmpvar$376=index$371$375;
                            next$index$371();
                            return tmpvar$376;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Integer}});
            };
            $$list.occurrences.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Integer}}},$ps:[{$nm:'element',$mt:'prm',$t:{t:Anything}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','occurrences']};};
            $$list.firstOccurrence=function firstOccurrence(element$377){
                var $$list=this;
                var it$378 = (function(){var tmpvar$380=$$list.size;
                if (tmpvar$380>0){
                var tmpvar$381=(0);
                var tmpvar$382=tmpvar$381;
                for (var i=1; i<tmpvar$380; i++){tmpvar$382=tmpvar$382.successor;}
                return Range(tmpvar$381,tmpvar$382,{Element:{t:Integer}})
                }else return getEmpty();}()).iterator();
                var index$379;while ((index$379=it$378.next())!==getFinished()){
                    if($$list.occursAt(index$379,element$377)){
                        return index$379;
                    }
                }
                if (getFinished() === index$379){
                    return null;
                }
            };$$list.firstOccurrence.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'element',$mt:'prm',$t:{t:Anything}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','firstOccurrence']};};
            $$list.lastOccurrence=function lastOccurrence(element$383){
                var $$list=this;
                var it$384 = (function(){var tmpvar$386=$$list.size;
                if (tmpvar$386>0){
                var tmpvar$387=(0);
                var tmpvar$388=tmpvar$387;
                for (var i=1; i<tmpvar$386; i++){tmpvar$388=tmpvar$388.successor;}
                return Range(tmpvar$387,tmpvar$388,{Element:{t:Integer}})
                }else return getEmpty();}()).reversed.iterator();
                var index$385;while ((index$385=it$384.next())!==getFinished()){
                    if($$list.occursAt(index$385,element$383)){
                        return index$385;
                    }
                }
                if (getFinished() === index$385){
                    return null;
                }
            };$$list.lastOccurrence.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'element',$mt:'prm',$t:{t:Anything}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','lastOccurrence']};};
            $$list.indexes=function (selecting$389){
                var $$list=this;
                return Comprehension(function(){
                    var it$390=(function(){var tmpvar$392=$$list.size;
                    if (tmpvar$392>0){
                    var tmpvar$393=(0);
                    var tmpvar$394=tmpvar$393;
                    for (var i=1; i<tmpvar$392; i++){tmpvar$394=tmpvar$394.successor;}
                    return Range(tmpvar$393,tmpvar$394,{Element:{t:Integer}})
                    }else return getEmpty();}()).iterator();
                    var index$391=getFinished();
                    var next$index$391=function(){
                        while((index$391=it$390.next())!==getFinished()){
                            if(selecting$389((opt$395=$$list.get(index$391),opt$395!==null?opt$395:getNothing()))){
                                return index$391;
                            }
                        }
                        return getFinished();
                    }
                    next$index$391();
                    return function(){
                        if(index$391!==getFinished()){
                            var index$391$396=index$391;
                            var tmpvar$397=index$391$396;
                            next$index$391();
                            return tmpvar$397;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Integer}});
            };
            $$list.indexes.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Integer}}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','indexes']};};
            $$list.trim=function trim(trimming$398){
                var $$list=this;
                var l$399;
                if((l$399=$$list.lastIndex)!==null){
                    var from$400=(-(1));
                    var setFrom$400=function(from$401){return from$400=from$401;};
                    var to$402=(-(1));
                    var setTo$402=function(to$403){return to$402=to$403;};
                    var it$404 = Range((0),l$399,{Element:{t:Integer}}).iterator();
                    var index$405;while ((index$405=it$404.next())!==getFinished()){
                        if((!trimming$398((opt$406=$$list.get(index$405),opt$406!==null?opt$406:getNothing())))){
                            from$400=index$405;
                            break;
                        }
                        var opt$406;
                    }
                    if (getFinished() === index$405){
                        return getEmpty();
                    }
                    var it$407 = Range(l$399,(0),{Element:{t:Integer}}).iterator();
                    var index$408;while ((index$408=it$407.next())!==getFinished()){
                        if((!trimming$398((opt$409=$$list.get(index$408),opt$409!==null?opt$409:getNothing())))){
                            to$402=index$408;
                            break;
                        }
                        var opt$409;
                    }
                    if (getFinished() === index$408){
                        return getEmpty();
                    }
                    return $$list.span(from$400,to$402);
                }else {
                    return getEmpty();
                }
            };$$list.trim.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','trim']};};
            $$list.trimLeading=function trimLeading(trimming$410){
                var $$list=this;
                var l$411;
                if((l$411=$$list.lastIndex)!==null){
                    var it$412 = Range((0),l$411,{Element:{t:Integer}}).iterator();
                    var index$413;while ((index$413=it$412.next())!==getFinished()){
                        if((!trimming$410((opt$414=$$list.get(index$413),opt$414!==null?opt$414:getNothing())))){
                            return $$list.span(index$413,l$411);
                        }
                        var opt$414;
                    }
                }
                return getEmpty();
            };$$list.trimLeading.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','trimLeading']};};
            $$list.trimTrailing=function trimTrailing(trimming$415){
                var $$list=this;
                var l$416;
                if((l$416=$$list.lastIndex)!==null){
                    var it$417 = Range(l$416,(0),{Element:{t:Integer}}).iterator();
                    var index$418;while ((index$418=it$417.next())!==getFinished()){
                        if((!trimming$415((opt$419=$$list.get(index$418),opt$419!==null?opt$419:getNothing())))){
                            return $$list.span((0),index$418);
                        }
                        var opt$419;
                    }
                }
                return getEmpty();
            };$$list.trimTrailing.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'trimming',$mt:'prm',$t:{t:Boolean$}}],$cont:List,$an:function(){return[shared(),$default()];},d:['ceylon.language','List','$m','trimTrailing']};};
            $$list.initial=function (length$420){
                var $$list=this;
                return $$list.segment((0),length$420);
            };
            $$list.initial.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','List','$m','initial']};};
            $$list.terminal=function terminal(length$421){
                var $$list=this;
                var l$422;
                if((l$422=$$list.lastIndex)!==null&&length$421.compare((0)).equals(getLarger())){
                    return $$list.span(l$422.minus(length$421).plus((1)),l$422);
                }else {
                    return getEmpty();
                }
            };$$list.terminal.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:List,$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared(),$default()];},d:['ceylon.language','List','$m','terminal']};};
        })(List.$$.prototype);
    }
    return List;
}
exports.$init$List=$init$List;
$init$List();
var opt$279,opt$283,opt$395;
function Tuple(first, rest, $$targs$$,$$tuple){
    $init$Tuple();
    if ($$tuple===undefined)$$tuple=new Tuple.$$;
    set_type_args($$tuple,$$targs$$);
    Object$($$tuple);
    Sequence($$tuple.$$targs$$===undefined?$$targs$$:{Element:$$tuple.$$targs$$.Element},$$tuple);
    Cloneable($$tuple.$$targs$$===undefined?$$targs$$:{Clone:{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}}},$$tuple);
    add_type_arg($$tuple,'Clone',{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}});
    $$tuple.first$423_=first;
    $$tuple.rest$424_=rest;
    return $$tuple;
}
Tuple.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Element:{'var':'out'},First:{'var':'out','satisfies':['Element']},Rest:{'var':'out','satisfies':[{t:Sequential,a:{Element:'Element'}}],'def':{t:Empty}}},satisfies:[{t:Sequence,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Element'}}}}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),$final()];},d:['ceylon.language','Tuple']};};
exports.Tuple=Tuple;
function $init$Tuple(){
    if (Tuple.$$===undefined){
        initTypeProto(Tuple,'ceylon.language::Tuple',Object$,$init$Sequence(),$init$Cloneable());
        (function($$tuple){
            defineAttr($$tuple,'first',function(){return this.first$423_;},undefined,function(){return{mod:$$METAMODEL$$,$t:'First',$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$at','first']};});
            defineAttr($$tuple,'rest',function(){return this.rest$424_;},undefined,function(){return{mod:$$METAMODEL$$,$t:'Rest',$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$at','rest']};});
            defineAttr($$tuple,'size',function(){
                var $$tuple=this;
                return (1).plus($$tuple.rest.size);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$at','size']};});
            $$tuple.get=function get(index$425){
                var $$tuple=this;
                
                var case$426=index$425.compare((0));
                if (case$426===getSmaller()) {
                    return null;
                }else if (case$426===getEqual()) {
                    return $$tuple.first;
                }else if (case$426===getLarger()) {
                    return $$tuple.rest.get(index$425.minus((1)));
                }
            };$$tuple.get.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','get']};};
            defineAttr($$tuple,'lastIndex',function(){
                var $$tuple=this;
                var restLastIndex$427;
                if((restLastIndex$427=$$tuple.rest.lastIndex)!==null){
                    return restLastIndex$427.plus((1));
                }else {
                    return (0);
                }
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$at','lastIndex']};});defineAttr($$tuple,'last',function(){
                var $$tuple=this;
                var rest$428;
                if(nonempty((rest$428=$$tuple.rest))){
                    return rest$428.last;
                }else {
                    return $$tuple.first;
                }
            },undefined,function(){return{mod:$$METAMODEL$$,$t:'Element',$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$at','last']};});defineAttr($$tuple,'reversed',function(){
                var $$tuple=this;
                return $$tuple.rest.reversed.withTrailing($$tuple.first,{Other:$$tuple.$$targs$$.First});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Element'}},$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$at','reversed']};});
            $$tuple.segment=function segment(from$429,length$430){
                var $$tuple=this;
                if((length$430.compare((0))!==getLarger())){
                    return getEmpty();
                }
                var realFrom$431=(opt$432=(from$429.compare((0)).equals(getSmaller())?(0):null),opt$432!==null?opt$432:from$429);
                var opt$432;
                if(realFrom$431.equals((0))){
                    return (opt$433=(length$430.equals((1))?Tuple($$tuple.first,getEmpty(),{Rest:{t:Empty},First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.First}):null),opt$433!==null?opt$433:$$tuple.rest.segment((0),length$430.plus(realFrom$431).minus((1))).withLeading($$tuple.first,{Other:$$tuple.$$targs$$.First}));
                    var opt$433;
                }
                return $$tuple.rest.segment(realFrom$431.minus((1)),length$430);
            };$$tuple.segment.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','segment']};};
            $$tuple.span=function span(from$434,end$435){
                var $$tuple=this;
                if((from$434.compare((0)).equals(getSmaller())&&end$435.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var realFrom$436=(opt$437=(from$434.compare((0)).equals(getSmaller())?(0):null),opt$437!==null?opt$437:from$434);
                var opt$437;
                var realEnd$438=(opt$439=(end$435.compare((0)).equals(getSmaller())?(0):null),opt$439!==null?opt$439:end$435);
                var opt$439;
                return (opt$440=((realFrom$436.compare(realEnd$438)!==getLarger())?$$tuple.segment(from$434,realEnd$438.minus(realFrom$436).plus((1))):null),opt$440!==null?opt$440:$$tuple.segment(realEnd$438,realFrom$436.minus(realEnd$438).plus((1))).reversed.sequence);
                var opt$440;
            };$$tuple.span.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'end',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','span']};};
            $$tuple.spanTo=function (to$441){
                var $$tuple=this;
                return (opt$442=(to$441.compare((0)).equals(getSmaller())?getEmpty():null),opt$442!==null?opt$442:$$tuple.span((0),to$441));
            };
            $$tuple.spanTo.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','spanTo']};};
            $$tuple.spanFrom=function (from$443){
                var $$tuple=this;
                return $$tuple.span(from$443,$$tuple.size);
            };
            $$tuple.spanFrom.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','spanFrom']};};
            defineAttr($$tuple,'clone',function(){
                var $$tuple=this;
                return $$tuple;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Element'}},$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$at','clone']};});
            $$tuple.iterator=function iterator(){
                var $$tuple=this;
                function iterator$444($$targs$$){
                    var $$iterator$444=new iterator$444.$$;
                    $$iterator$444.$$targs$$=$$targs$$;
                    Iterator({Element:$$tuple.$$targs$$.Element},$$iterator$444);
                    $$iterator$444.current$445_=$$tuple;
                    return $$iterator$444;
                }
                function $init$iterator$444(){
                    if (iterator$444.$$===undefined){
                        initTypeProto(iterator$444,'ceylon.language::Tuple.iterator.iterator',Basic,$init$Iterator());
                    }
                    return iterator$444;
                }
                $init$iterator$444();
                (function($$iterator$444){
                    defineAttr($$iterator$444,'current$445',function(){return this.current$445_;},function(current$446){return this.current$445_=current$446;},function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:iterator$444,$an:function(){return[variable()];},d:['ceylon.language','Tuple','$m','iterator','$o','iterator','$at','current']};});
                    $$iterator$444.next=function next(){
                        var $$iterator$444=this;
                        var c$447;
                        if(nonempty((c$447=$$iterator$444.current$445))){
                            $$iterator$444.current$445=c$447.rest;
                            return c$447.first;
                        }else {
                            return getFinished();
                        }
                    };$$iterator$444.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$444,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','iterator','$o','iterator','$m','next']};};
                })(iterator$444.$$.prototype);
                var iterator$448=iterator$444({Element:$$tuple.$$targs$$.Element});
                var getIterator$448=function(){
                    return iterator$448;
                }
                return getIterator$448();
            };$$tuple.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','iterator']};};
            $$tuple.contains=function contains(element$449){
                var $$tuple=this;
                var first$450;
                if((first$450=$$tuple.first)!==null&&first$450.equals(element$449)){
                    return true;
                }else {
                    return $$tuple.rest.contains(element$449);
                }
            };$$tuple.contains.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Tuple,$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','contains']};};
            $$tuple.withLeading=function (element$451,$$$mptypes){
                var $$tuple=this;
                return Tuple(element$451,$$tuple,{Rest:{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}},First:$$$mptypes.Other,Element:{ t:'u', l:[$$$mptypes.Other,$$tuple.$$targs$$.Element]}});
            };
            $$tuple.withLeading.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Element'}},First:'Other',Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$cont:Tuple,$tp:{Other:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Tuple','$m','withLeading']};};
        })(Tuple.$$.prototype);
    }
    return Tuple;
}
exports.$init$Tuple=$init$Tuple;
$init$Tuple();
var opt$442;
function ChainedIterator(first$452, second$453, $$targs$$,$$chainedIterator){
    $init$ChainedIterator();
    if ($$chainedIterator===undefined)$$chainedIterator=new ChainedIterator.$$;
    set_type_args($$chainedIterator,$$targs$$);
    $$chainedIterator.first$452_=first$452;
    $$chainedIterator.second$453_=second$453;
    Iterator($$chainedIterator.$$targs$$===undefined?$$targs$$:{Element:{ t:'u', l:[$$chainedIterator.$$targs$$.Element,$$chainedIterator.$$targs$$.Other]}},$$chainedIterator);
    $$chainedIterator.iter$454_=$$chainedIterator.first$452.iterator();
    $$chainedIterator.more$455_=true;
    return $$chainedIterator;
}
ChainedIterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Element:{'var':'out'},Other:{'var':'out'}},satisfies:[{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}}],$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}}))];},d:['ceylon.language','ChainedIterator']};};
function $init$ChainedIterator(){
    if (ChainedIterator.$$===undefined){
        initTypeProto(ChainedIterator,'ceylon.language::ChainedIterator',Basic,$init$Iterator());
        (function($$chainedIterator){
            defineAttr($$chainedIterator,'iter$454',function(){return this.iter$454_;},function(iter$456){return this.iter$454_=iter$456;},function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$cont:ChainedIterator,$an:function(){return[variable()];},d:['ceylon.language','ChainedIterator','$at','iter']};});
            defineAttr($$chainedIterator,'more$455',function(){return this.more$455_;},function(more$457){return this.more$455_=more$457;},function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:ChainedIterator,$an:function(){return[variable()];},d:['ceylon.language','ChainedIterator','$at','more']};});
            $$chainedIterator.next=function next(){
                var $$chainedIterator=this;
                var e$458=$$chainedIterator.iter$454.next();
                var setE$458=function(e$459){return e$458=e$459;};
                var f$460;
                if(isOfType((f$460=e$458),{t:Finished})){
                    if($$chainedIterator.more$455){
                        $$chainedIterator.iter$454=$$chainedIterator.second$453.iterator();
                        $$chainedIterator.more$455=false;
                        e$458=$$chainedIterator.iter$454.next();
                    }
                }
                return e$458;
            };$$chainedIterator.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element','Other',{t:Finished}]},$ps:[],$cont:ChainedIterator,$an:function(){return[shared(),actual()];},d:['ceylon.language','ChainedIterator','$m','next']};};
            defineAttr($$chainedIterator,'first$452',function(){return this.first$452_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$cont:ChainedIterator,d:['ceylon.language','ChainedIterator','$at','first']};});
            defineAttr($$chainedIterator,'second$453',function(){return this.second$453_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Other'}},$cont:ChainedIterator,d:['ceylon.language','ChainedIterator','$at','second']};});
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
    Object$($$entry);
    $$entry.key$461_=key;
    $$entry.item$462_=item;
    return $$entry;
}
Entry.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),$final()];},d:['ceylon.language','Entry']};};
exports.Entry=Entry;
function $init$Entry(){
    if (Entry.$$===undefined){
        initTypeProto(Entry,'ceylon.language::Entry',Object$);
        (function($$entry){
            defineAttr($$entry,'key',function(){return this.key$461_;},undefined,function(){return{mod:$$METAMODEL$$,$t:'Key',$cont:Entry,$an:function(){return[shared()];},d:['ceylon.language','Entry','$at','key']};});
            defineAttr($$entry,'item',function(){return this.item$462_;},undefined,function(){return{mod:$$METAMODEL$$,$t:'Item',$cont:Entry,$an:function(){return[shared()];},d:['ceylon.language','Entry','$at','item']};});
            $$entry.equals=function equals(that$463){
                var $$entry=this;
                var that$464;
                if(isOfType((that$464=that$463),{t:Entry,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    return ($$entry.key.equals(that$464.key)&&$$entry.item.equals(that$464.item));
                }else {
                    return false;
                }
            };$$entry.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Entry,$an:function(){return[shared(),actual()];},d:['ceylon.language','Entry','$m','equals']};};
            defineAttr($$entry,'hash',function(){
                var $$entry=this;
                return (31).plus($$entry.key.hash).times((31)).plus($$entry.item.hash);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Entry,$an:function(){return[shared(),actual()];},d:['ceylon.language','Entry','$at','hash']};});
            defineAttr($$entry,'string',function(){
                var $$entry=this;
                return StringBuilder().appendAll([$$entry.key.string,String$("->",2),$$entry.item.string]).string;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Entry,$an:function(){return[shared(),actual()];},d:['ceylon.language','Entry','$at','string']};});
        })(Entry.$$.prototype);
    }
    return Entry;
}
exports.$init$Entry=$init$Entry;
$init$Entry();
function Comparable($$targs$$,$$comparable){
    set_type_args($$comparable,$$targs$$);
}
Comparable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'var':'in','satisfies':[{t:Comparable,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Comparable']};};
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
Invertable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Inverse:{'var':'out','satisfies':[{t:Invertable,a:{Inverse:'Inverse'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:Integer}),typeLiteral$model({Type:Float})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Invertable']};};
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
Summable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Summable,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:String}),typeLiteral$model({Type:Numeric})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:InterfaceDeclaration$model$declaration}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Summable']};};
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
Ordinal.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'var':'out','satisfies':[{t:Ordinal,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[see([typeLiteral$model({Type:Character}),typeLiteral$model({Type:Integer}),typeLiteral$model({Type:Integral}),typeLiteral$model({Type:Range})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:InterfaceDeclaration$model$declaration}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Ordinal']};};
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
Enumerable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'var':'out','satisfies':[{t:Enumerable,a:{Other:'Other'}}]}},satisfies:[{t:Ordinal,a:{Other:'Other'}}],$an:function(){return[shared()];},d:['ceylon.language','Enumerable']};};
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
Numeric.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Numeric,a:{Other:'Other'}}]}},satisfies:[{t:Summable,a:{Other:'Other'}},{t:Invertable,a:{Inverse:'Other'}}],$an:function(){return[see([typeLiteral$model({Type:Integer}),typeLiteral$model({Type:Float}),typeLiteral$model({Type:Comparable})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:InterfaceDeclaration$model$declaration}]}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Numeric']};};
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
Scalar.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Scalar,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'Other'}},{t:Comparable,a:{Other:'Other'}},{t:Number$}],$an:function(){return[see([typeLiteral$model({Type:Integer}),typeLiteral$model({Type:Float})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Scalar']};};
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
Exponentiable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{This:{'satisfies':[{t:Exponentiable,a:{Other:'Other',This:'This'}}]},Other:{'satisfies':[{t:Numeric,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'This'}}],$an:function(){return[see([typeLiteral$model({Type:Integer}),typeLiteral$model({Type:Float})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),shared()];},d:['ceylon.language','Exponentiable']};};
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
Integral.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Other:{'satisfies':[{t:Integral,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'Other'}},{t:Enumerable,a:{Other:'Other'}}],$an:function(){return[see([typeLiteral$model({Type:Integer})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','Integral']};};
exports.Integral=Integral;
function $init$Integral(){
    if (Integral.$$===undefined){
        initTypeProto(Integral,'ceylon.language::Integral',$init$Numeric(),$init$Enumerable());
        (function($$integral){
            $$integral.divides=function (other$465){
                var $$integral=this;
                return other$465.remainder($$integral).zero;
            };
            $$integral.divides.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'other',$mt:'prm',$t:'Other'}],$cont:Integral,$an:function(){return[shared()];},d:['ceylon.language','Integral','$m','divides']};};
        })(Integral.$$.prototype);
    }
    return Integral;
}
exports.$init$Integral=$init$Integral;
$init$Integral();
function Scalable($$targs$$,$$scalable){
    set_type_args($$scalable,$$targs$$);
}
Scalable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Scale:{'var':'in'},Value:{'var':'out','satisfies':[{t:Scalable,a:{Value:'Value',Scale:'Scale'}}]}},satisfies:[],$an:function(){return[shared()];},d:['ceylon.language','Scalable']};};
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
NegativeNumberException.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[shared()];},d:['ceylon.language','NegativeNumberException']};};
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
OverflowException.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[shared()];},d:['ceylon.language','OverflowException']};};
exports.OverflowException=OverflowException;
function $init$OverflowException(){
    if (OverflowException.$$===undefined){
        initTypeProto(OverflowException,'ceylon.language::OverflowException',Exception);
    }
    return OverflowException;
}
exports.$init$OverflowException=$init$OverflowException;
$init$OverflowException();
function InitializationException(description$466, $$initializationException){
    $init$InitializationException();
    if ($$initializationException===undefined)$$initializationException=new InitializationException.$$;
    $$initializationException.description$466_=description$466;
    Exception($$initializationException.description$466,null,$$initializationException);
    return $$initializationException;
}
InitializationException.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','InitializationException']};};
exports.InitializationException=InitializationException;
function $init$InitializationException(){
    if (InitializationException.$$===undefined){
        initTypeProto(InitializationException,'ceylon.language::InitializationException',Exception);
        (function($$initializationException){
            defineAttr($$initializationException,'description$466',function(){return this.description$466_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:InitializationException,d:['ceylon.language','InitializationException','$at','description']};});
        })(InitializationException.$$.prototype);
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
Set.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Element:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Collection,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Set,a:{Element:'Element'}}}}],$an:function(){return[shared()];},d:['ceylon.language','Set']};};
exports.Set=Set;
function $init$Set(){
    if (Set.$$===undefined){
        initTypeProto(Set,'ceylon.language::Set',$init$Collection(),$init$Cloneable());
        (function($$set){
            $$set.superset=function superset(set$467){
                var $$set=this;
                var it$468 = set$467.iterator();
                var element$469;while ((element$469=it$468.next())!==getFinished()){
                    if((!$$set.contains(element$469))){
                        return false;
                    }
                }
                if (getFinished() === element$469){
                    return true;
                }
            };$$set.superset.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:{t:Object$}}}}],$cont:Set,$an:function(){return[shared(),$default()];},d:['ceylon.language','Set','$m','superset']};};
            $$set.subset=function subset(set$470){
                var $$set=this;
                var it$471 = $$set.iterator();
                var element$472;while ((element$472=it$471.next())!==getFinished()){
                    if((!set$470.contains(element$472))){
                        return false;
                    }
                }
                if (getFinished() === element$472){
                    return true;
                }
            };$$set.subset.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:{t:Object$}}}}],$cont:Set,$an:function(){return[shared(),$default()];},d:['ceylon.language','Set','$m','subset']};};
            $$set.equals=function equals(that$473){
                var $$set=this;
                var that$474;
                if(isOfType((that$474=that$473),{t:Set,a:{Element:{t:Object$}}})&&that$474.size.equals($$set.size)){
                    var it$475 = $$set.iterator();
                    var element$476;while ((element$476=it$475.next())!==getFinished()){
                        if((!that$474.contains(element$476))){
                            return false;
                        }
                    }
                    if (getFinished() === element$476){
                        return true;
                    }
                }
                return false;
            };$$set.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Set,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Set','$m','equals']};};
            defineAttr($$set,'hash',function(){
                var $$set=this;
                var hashCode$477=(1);
                var setHashCode$477=function(hashCode$478){return hashCode$477=hashCode$478;};
                var it$479 = $$set.iterator();
                var elem$480;while ((elem$480=it$479.next())!==getFinished()){
                    (hashCode$477=hashCode$477.times((31)));
                    (hashCode$477=hashCode$477.plus(elem$480.hash));
                }
                return hashCode$477;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Set,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Set','$at','hash']};});
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
    Object$($$range);
    Sequence($$range.$$targs$$===undefined?$$targs$$:{Element:$$range.$$targs$$.Element},$$range);
    Cloneable($$range.$$targs$$===undefined?$$targs$$:{Clone:{t:Range,a:{Element:$$range.$$targs$$.Element}}},$$range);
    add_type_arg($$range,'Clone',{t:Range,a:{Element:$$range.$$targs$$.Element}});
    $$range.first$481_=first;
    $$range.last$482_=last;
    return $$range;
}
Range.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Element:{'satisfies':[{t:Ordinal,a:{Other:'Element'}},{t:Comparable,a:{Other:'Element'}}]}},satisfies:[{t:Sequence,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Range,a:{Element:'Element'}}}}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),$final()];},d:['ceylon.language','Range']};};
exports.Range=Range;
function $init$Range(){
    if (Range.$$===undefined){
        initTypeProto(Range,'ceylon.language::Range',Object$,$init$Sequence(),$init$Cloneable());
        (function($$range){
            defineAttr($$range,'first',function(){return this.first$481_;},undefined,function(){return{mod:$$METAMODEL$$,$t:'Element',$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','first']};});
            defineAttr($$range,'last',function(){return this.last$482_;},undefined,function(){return{mod:$$METAMODEL$$,$t:'Element',$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','last']};});
            defineAttr($$range,'string',function(){
                var $$range=this;
                return $$range.first.string.plus(String$("..",2)).plus($$range.last.string);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','string']};});
            defineAttr($$range,'decreasing',function(){
                var $$range=this;
                return $$range.last.compare($$range.first).equals(getSmaller());
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:Range,$an:function(){return[shared()];},d:['ceylon.language','Range','$at','decreasing']};});
            $$range.next$483=function (x$484){
                var $$range=this;
                return (opt$485=($$range.decreasing?x$484.predecessor:null),opt$485!==null?opt$485:x$484.successor);
            };
            $$range.next$483.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'}],$cont:Range,d:['ceylon.language','Range','$m','next']};};
            defineAttr($$range,'size',function(){
                var $$range=this;
                var last$486;
                var first$487;
                if(isOfType((last$486=$$range.last),{t:Enumerable,a:{Other:{t:Anything}}})&&isOfType((first$487=$$range.first),{t:Enumerable,a:{Other:{t:Anything}}})){
                    return last$486.integerValue.minus(first$487.integerValue).magnitude.plus((1));
                }else {
                    var size$488=(1);
                    var setSize$488=function(size$489){return size$488=size$489;};
                    var current$490=$$range.first;
                    var setCurrent$490=function(current$491){return current$490=current$491;};
                    while((!current$490.equals($$range.last))){
                        (oldsize$492=size$488,size$488=oldsize$492.successor,oldsize$492);
                        var oldsize$492;
                        current$490=$$range.next$483(current$490);
                    }
                    return size$488;
                }
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','size']};});defineAttr($$range,'lastIndex',function(){
                var $$range=this;
                return $$range.size.minus((1));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','lastIndex']};});
            defineAttr($$range,'rest',function(){
                var $$range=this;
                if($$range.size.equals((1))){
                    return getEmpty();
                }
                var n$493=$$range.next$483($$range.first);
                return (opt$494=(n$493.equals($$range.last)?getEmpty():null),opt$494!==null?opt$494:Range(n$493,$$range.last,{Element:$$range.$$targs$$.Element}));
                var opt$494;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','rest']};});$$range.get=function get(n$495){
                var $$range=this;
                var index$496=(0);
                var setIndex$496=function(index$497){return index$496=index$497;};
                var x$498=$$range.first;
                var setX$498=function(x$499){return x$498=x$499;};
                while(index$496.compare(n$495).equals(getSmaller())){
                    if(x$498.equals($$range.last)){
                        return null;
                    }else {
                        (index$496=index$496.successor);
                        x$498=$$range.next$483(x$498);
                    }
                }
                return x$498;
            };$$range.get.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'n',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','get']};};
            $$range.iterator=function iterator(){
                var $$range=this;
                function RangeIterator$500($$rangeIterator$500){
                    $init$RangeIterator$500();
                    if ($$rangeIterator$500===undefined)$$rangeIterator$500=new RangeIterator$500.$$;
                    $$rangeIterator$500.$$targs$$={Element:$$range.$$targs$$.Element};
                    Iterator({Element:$$range.$$targs$$.Element},$$rangeIterator$500);
                    $$rangeIterator$500.current$501_=$$range.first;
                    return $$rangeIterator$500;
                }
                RangeIterator$500.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:Iterator,a:{Element:'Element'}}],d:['ceylon.language','Range','$m','iterator','$c','RangeIterator']};};
                function $init$RangeIterator$500(){
                    if (RangeIterator$500.$$===undefined){
                        initTypeProto(RangeIterator$500,'ceylon.language::Range.iterator.RangeIterator',Basic,$init$Iterator());
                        (function($$rangeIterator$500){
                            defineAttr($$rangeIterator$500,'current$501',function(){return this.current$501_;},function(current$502){return this.current$501_=current$502;},function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$cont:RangeIterator$500,$an:function(){return[variable()];},d:['ceylon.language','Range','$m','iterator','$c','RangeIterator','$at','current']};});
                            $$rangeIterator$500.next=function next(){
                                var $$rangeIterator$500=this;
                                var result$503=$$rangeIterator$500.current$501;
                                var curr$504;
                                if(!isOfType((curr$504=$$rangeIterator$500.current$501),{t:Finished})){
                                    if((opt$505=($$range.decreasing?(curr$504.compare($$range.last)!==getLarger()):null),opt$505!==null?opt$505:(curr$504.compare($$range.last)!==getSmaller()))){
                                        $$rangeIterator$500.current$501=getFinished();
                                    }else {
                                        $$rangeIterator$500.current$501=$$range.next$483(curr$504);
                                    }
                                    var opt$505;
                                }
                                return result$503;
                            };$$rangeIterator$500.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:RangeIterator$500,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','iterator','$c','RangeIterator','$m','next']};};
                            defineAttr($$rangeIterator$500,'string',function(){
                                var $$rangeIterator$500=this;
                                return String$("RangeIterator",13);
                            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:RangeIterator$500,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','iterator','$c','RangeIterator','$at','string']};});
                        })(RangeIterator$500.$$.prototype);
                    }
                    return RangeIterator$500;
                }
                $init$RangeIterator$500();
                return RangeIterator$500();
            };$$range.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','iterator']};};
            $$range.by=function by(step$506){
                var $$range=this;
                //assert at Range.ceylon (113:8-114:25)
                if (!(step$506.compare((0)).equals(getLarger()))) {throw wrapexc(AssertionException("step size must be greater than zero: \'step > 0\' at Range.ceylon (114:15-114:24)"),'113:8-114:25','Range.ceylon'); }
                if(step$506.equals((1))){
                    return $$range;
                }
                var first$507;
                var last$508;
                if(isOfType((first$507=$$range.first),{t:Integer})&&isOfType((last$508=$$range.last),{t:Integer})){
                    return integerRangeByIterable($$range,step$506,{Element:$$range.$$targs$$.Element});
                }
                return $$range.getT$all()['ceylon.language::Iterable'].$$.prototype.by.call(this,step$506);
            };$$range.by.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Element'}},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','by']};};
            $$range.contains=function contains(element$509){
                var $$range=this;
                var element$510;
                if(isOfType((element$510=element$509),$$range.$$targs$$.Element)){
                    return $$range.containsElement(element$510);
                }else {
                    return false;
                }
            };$$range.contains.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','contains']};};
            $$range.count=function count(selecting$511){
                var $$range=this;
                var e$512=$$range.first;
                var setE$512=function(e$513){return e$512=e$513;};
                var c$514=(0);
                var setC$514=function(c$515){return c$514=c$515;};
                while($$range.containsElement(e$512)){
                    if(selecting$511(e$512)){
                        (oldc$516=c$514,c$514=oldc$516.successor,oldc$516);
                        var oldc$516;
                    }
                    e$512=$$range.next$483(e$512);
                }
                return c$514;
            };$$range.count.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','count']};};
            $$range.containsElement=function (x$517){
                var $$range=this;
                return (opt$518=($$range.decreasing?((x$517.compare($$range.first)!==getLarger())&&(x$517.compare($$range.last)!==getSmaller())):null),opt$518!==null?opt$518:((x$517.compare($$range.first)!==getSmaller())&&(x$517.compare($$range.last)!==getLarger())));
            };
            $$range.containsElement.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'x',$mt:'prm',$t:'Element'}],$cont:Range,$an:function(){return[shared()];},d:['ceylon.language','Range','$m','containsElement']};};
            $$range.occurs=function occurs(element$519){
                var $$range=this;
                var element$520;
                if(isOfType((element$520=element$519),$$range.$$targs$$.Element)){
                    return $$range.containsElement(element$520);
                }else {
                    return false;
                }
            };$$range.occurs.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Anything}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','occurs']};};
            $$range.includes=function includes(sublist$521){
                var $$range=this;
                var sublist$522;
                if(isOfType((sublist$522=sublist$521),{t:Range,a:{Element:$$range.$$targs$$.Element}})){
                    return ((tmpvar$523=sublist$522.first,tmpvar$523.compare($$range.first)!==getSmaller()&&tmpvar$523.compare($$range.last)!==getLarger())&&(tmpvar$524=sublist$522.last,tmpvar$524.compare($$range.first)!==getSmaller()&&tmpvar$524.compare($$range.last)!==getLarger()));
                }else {
                    return $$range.getT$all()['ceylon.language::List'].$$.prototype.includes.call(this,sublist$521);
                }
            };$$range.includes.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'sublist',$mt:'prm',$t:{t:List,a:{Element:{t:Anything}}}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','includes']};};
            $$range.equals=function equals(that$525){
                var $$range=this;
                var that$526;
                if(isOfType((that$526=that$525),{t:Range,a:{Element:{t:Object$}}})){
                    return (that$526.first.equals($$range.first)&&that$526.last.equals($$range.last));
                }else {
                    return $$range.getT$all()['ceylon.language::List'].$$.prototype.equals.call(this,that$525);
                }
            };$$range.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','equals']};};
            defineAttr($$range,'clone',function(){
                var $$range=this;
                return $$range;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','clone']};});
            $$range.segment=function segment(from$527,length$528){
                var $$range=this;
                if(((length$528.compare((0))!==getLarger())||from$527.compare($$range.lastIndex).equals(getLarger()))){
                    return getEmpty();
                }
                var x$529=$$range.first;
                var setX$529=function(x$530){return x$529=x$530;};
                var i$531=(0);
                var setI$531=function(i$532){return i$531=i$532;};
                while((oldi$533=i$531,i$531=oldi$533.successor,oldi$533).compare(from$527).equals(getSmaller())){
                    x$529=$$range.next$483(x$529);
                }
                var oldi$533;
                var y$534=x$529;
                var setY$534=function(y$535){return y$534=y$535;};
                var j$536=(1);
                var setJ$536=function(j$537){return j$536=j$537;};
                while(((oldj$538=j$536,j$536=oldj$538.successor,oldj$538).compare(length$528).equals(getSmaller())&&y$534.compare($$range.last).equals(getSmaller()))){
                    y$534=$$range.next$483(y$534);
                }
                var oldj$538;
                return Range(x$529,y$534,{Element:$$range.$$targs$$.Element});
            };$$range.segment.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','segment']};};
            $$range.span=function span(from$539,to$540){
                var $$range=this;
                var toIndex$541=to$540;
                var setToIndex$541=function(toIndex$542){return toIndex$541=toIndex$542;};
                var fromIndex$543=from$539;
                var setFromIndex$543=function(fromIndex$544){return fromIndex$543=fromIndex$544;};
                if(toIndex$541.compare((0)).equals(getSmaller())){
                    if(fromIndex$543.compare((0)).equals(getSmaller())){
                        return getEmpty();
                    }
                    toIndex$541=(0);
                }else {
                    if(toIndex$541.compare($$range.lastIndex).equals(getLarger())){
                        if(fromIndex$543.compare($$range.lastIndex).equals(getLarger())){
                            return getEmpty();
                        }
                        toIndex$541=$$range.lastIndex;
                    }
                }
                if(fromIndex$543.compare((0)).equals(getSmaller())){
                    fromIndex$543=(0);
                }else {
                    if(fromIndex$543.compare($$range.lastIndex).equals(getLarger())){
                        fromIndex$543=$$range.lastIndex;
                    }
                }
                var x$545=$$range.first;
                var setX$545=function(x$546){return x$545=x$546;};
                var i$547=(0);
                var setI$547=function(i$548){return i$547=i$548;};
                while((oldi$549=i$547,i$547=oldi$549.successor,oldi$549).compare(fromIndex$543).equals(getSmaller())){
                    x$545=$$range.next$483(x$545);
                }
                var oldi$549;
                var y$550=$$range.first;
                var setY$550=function(y$551){return y$550=y$551;};
                var j$552=(0);
                var setJ$552=function(j$553){return j$552=j$553;};
                while((oldj$554=j$552,j$552=oldj$554.successor,oldj$554).compare(toIndex$541).equals(getSmaller())){
                    y$550=$$range.next$483(y$550);
                }
                var oldj$554;
                return Range(x$545,y$550,{Element:$$range.$$targs$$.Element});
            };$$range.span.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','span']};};
            $$range.spanTo=function spanTo(to$555){
                var $$range=this;
                return (opt$556=(to$555.compare((0)).equals(getSmaller())?getEmpty():null),opt$556!==null?opt$556:$$range.span((0),to$555));
                var opt$556;
            };$$range.spanTo.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','spanTo']};};
            $$range.spanFrom=function spanFrom(from$557){
                var $$range=this;
                return $$range.span(from$557,$$range.size);
            };$$range.spanFrom.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','spanFrom']};};
            defineAttr($$range,'reversed',function(){
                var $$range=this;
                return Range($$range.last,$$range.first,{Element:$$range.$$targs$$.Element});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','reversed']};});
            $$range.skipping=function skipping(skip$558){
                var $$range=this;
                var x$559=(0);
                var setX$559=function(x$560){return x$559=x$560;};
                var e$561=$$range.first;
                var setE$561=function(e$562){return e$561=e$562;};
                while((oldx$563=x$559,x$559=oldx$563.successor,oldx$563).compare(skip$558).equals(getSmaller())){
                    e$561=$$range.next$483(e$561);
                }
                var oldx$563;
                return (opt$564=($$range.containsElement(e$561)?Range(e$561,$$range.last,{Element:$$range.$$targs$$.Element}):null),opt$564!==null?opt$564:getEmpty());
                var opt$564;
            };$$range.skipping.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','skipping']};};
            $$range.taking=function taking(take$565){
                var $$range=this;
                if(take$565.equals((0))){
                    return getEmpty();
                }
                var x$566=(0);
                var setX$566=function(x$567){return x$566=x$567;};
                var e$568=$$range.first;
                var setE$568=function(e$569){return e$568=e$569;};
                while((x$566=x$566.successor).compare(take$565).equals(getSmaller())){
                    e$568=$$range.next$483(e$568);
                }
                return (opt$570=($$range.containsElement(e$568)?Range($$range.first,e$568,{Element:$$range.$$targs$$.Element}):null),opt$570!==null?opt$570:$$range);
                var opt$570;
            };$$range.taking.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$m','taking']};};
            defineAttr($$range,'coalesced',function(){
                var $$range=this;
                return $$range;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','coalesced']};});
            defineAttr($$range,'sequence',function(){
                var $$range=this;
                return $$range;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Range,a:{Element:'Element'}},$cont:Range,$an:function(){return[shared(),actual()];},d:['ceylon.language','Range','$at','sequence']};});
        })(Range.$$.prototype);
    }
    return Range;
}
exports.$init$Range=$init$Range;
$init$Range();
var opt$485,opt$518;
function Singleton(element$571, $$targs$$,$$singleton){
    $init$Singleton();
    if ($$singleton===undefined)$$singleton=new Singleton.$$;
    set_type_args($$singleton,$$targs$$);
    $$singleton.element$571_=element$571;
    Object$($$singleton);
    Sequence($$singleton.$$targs$$===undefined?$$targs$$:{Element:$$singleton.$$targs$$.Element},$$singleton);
    return $$singleton;
}
Singleton.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Object$},$tp:{Element:{'var':'out'}},satisfies:[{t:Sequence,a:{Element:'Element'}}],$an:function(){return[shared()];},d:['ceylon.language','Singleton']};};
exports.Singleton=Singleton;
function $init$Singleton(){
    if (Singleton.$$===undefined){
        initTypeProto(Singleton,'ceylon.language::Singleton',Object$,$init$Sequence());
        (function($$singleton){
            defineAttr($$singleton,'lastIndex',function(){
                var $$singleton=this;
                return (0);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','lastIndex']};});
            defineAttr($$singleton,'size',function(){
                var $$singleton=this;
                return (1);
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','size']};});
            defineAttr($$singleton,'first',function(){
                var $$singleton=this;
                return $$singleton.element$571;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:'Element',$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','first']};});
            defineAttr($$singleton,'last',function(){
                var $$singleton=this;
                return $$singleton.element$571;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:'Element',$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','last']};});
            defineAttr($$singleton,'rest',function(){
                var $$singleton=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Empty},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','rest']};});
            $$singleton.get=function get(index$572){
                var $$singleton=this;
                if(index$572.equals((0))){
                    return $$singleton.element$571;
                }else {
                    return null;
                }
            };$$singleton.get.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','get']};};
            defineAttr($$singleton,'clone',function(){
                var $$singleton=this;
                return $$singleton;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Singleton,a:{Element:'Element'}},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','clone']};});
            $$singleton.iterator=function iterator(){
                var $$singleton=this;
                function SingletonIterator$573($$singletonIterator$573){
                    $init$SingletonIterator$573();
                    if ($$singletonIterator$573===undefined)$$singletonIterator$573=new SingletonIterator$573.$$;
                    $$singletonIterator$573.$$targs$$={Element:$$singleton.$$targs$$.Element};
                    Iterator({Element:$$singleton.$$targs$$.Element},$$singletonIterator$573);
                    $$singletonIterator$573.done$574_=false;
                    return $$singletonIterator$573;
                }
                SingletonIterator$573.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:Iterator,a:{Element:'Element'}}],d:['ceylon.language','Singleton','$m','iterator','$c','SingletonIterator']};};
                function $init$SingletonIterator$573(){
                    if (SingletonIterator$573.$$===undefined){
                        initTypeProto(SingletonIterator$573,'ceylon.language::Singleton.iterator.SingletonIterator',Basic,$init$Iterator());
                        (function($$singletonIterator$573){
                            defineAttr($$singletonIterator$573,'done$574',function(){return this.done$574_;},function(done$575){return this.done$574_=done$575;},function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:SingletonIterator$573,$an:function(){return[variable()];},d:['ceylon.language','Singleton','$m','iterator','$c','SingletonIterator','$at','done']};});
                            $$singletonIterator$573.next=function next(){
                                var $$singletonIterator$573=this;
                                if($$singletonIterator$573.done$574){
                                    return getFinished();
                                }else {
                                    $$singletonIterator$573.done$574=true;
                                    return $$singleton.element$571;
                                }
                            };$$singletonIterator$573.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:SingletonIterator$573,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','iterator','$c','SingletonIterator','$m','next']};};
                            defineAttr($$singletonIterator$573,'string',function(){
                                var $$singletonIterator$573=this;
                                return String$("SingletonIterator",17);
                            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:SingletonIterator$573,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','iterator','$c','SingletonIterator','$at','string']};});
                        })(SingletonIterator$573.$$.prototype);
                    }
                    return SingletonIterator$573;
                }
                $init$SingletonIterator$573();
                return SingletonIterator$573();
            };$$singleton.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:Singleton,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Singleton','$m','iterator']};};
            defineAttr($$singleton,'string',function(){
                var $$singleton=this;
                return StringBuilder().appendAll([String$("[",1),(opt$576=(opt$577=$$singleton.element$571,opt$577!==null?opt$577.string:null),opt$576!==null?opt$576:String$("null",4)).string,String$("]",1)]).string;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','string']};});
            $$singleton.segment=function (from$578,length$579){
                var $$singleton=this;
                return (opt$580=(((from$578.compare((0))!==getLarger())&&from$578.plus(length$579).compare((0)).equals(getLarger()))?$$singleton:null),opt$580!==null?opt$580:getEmpty());
            };
            $$singleton.segment.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','segment']};};
            $$singleton.span=function (from$581,to$582){
                var $$singleton=this;
                return (opt$583=((((from$581.compare((0))!==getLarger())&&(to$582.compare((0))!==getSmaller()))||((from$581.compare((0))!==getSmaller())&&(to$582.compare((0))!==getLarger())))?$$singleton:null),opt$583!==null?opt$583:getEmpty());
            };
            $$singleton.span.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','span']};};
            $$singleton.spanTo=function (to$584){
                var $$singleton=this;
                return (opt$585=(to$584.compare((0)).equals(getSmaller())?getEmpty():null),opt$585!==null?opt$585:$$singleton);
            };
            $$singleton.spanTo.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','spanTo']};};
            $$singleton.spanFrom=function (from$586){
                var $$singleton=this;
                return (opt$587=(from$586.compare((0)).equals(getLarger())?getEmpty():null),opt$587!==null?opt$587:$$singleton);
            };
            $$singleton.spanFrom.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','spanFrom']};};
            defineAttr($$singleton,'reversed',function(){
                var $$singleton=this;
                return $$singleton;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Singleton,a:{Element:'Element'}},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','reversed']};});
            $$singleton.equals=function equals(that$588){
                var $$singleton=this;
                var element$589;
                if((element$589=$$singleton.element$571)!==null){
                    var that$590;
                    if(isOfType((that$590=that$588),{t:List,a:{Element:{t:Anything}}})){
                        if(that$590.size.equals((1))){
                            var elem$591;
                            if((elem$591=that$590.first)!==null){
                                return elem$591.equals(element$589);
                            }
                        }
                    }
                    return false;
                }
                return false;
            };$$singleton.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','equals']};};
            defineAttr($$singleton,'hash',function(){
                var $$singleton=this;
                return (31).plus((opt$592=(opt$593=$$singleton.element$571,opt$593!==null?opt$593.hash:null),opt$592!==null?opt$592:(0)));
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','hash']};});
            $$singleton.contains=function contains(element$594){
                var $$singleton=this;
                var e$595;
                if((e$595=$$singleton.element$571)!==null){
                    return e$595.equals(element$594);
                }
                return false;
            };$$singleton.contains.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','contains']};};
            $$singleton.count=function (selecting$596){
                var $$singleton=this;
                return (opt$597=(selecting$596($$singleton.element$571)?(1):null),opt$597!==null?opt$597:(0));
            };
            $$singleton.count.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','count']};};
            $$singleton.$map=function (selecting$598,$$$mptypes){
                var $$singleton=this;
                return Tuple(selecting$598($$singleton.element$571),getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Result,Element:$$$mptypes.Result});
            };
            $$singleton.$map.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequence,a:{Element:'Result'}},$ps:[{$nm:'selecting',$mt:'prm',$t:'Result'}],$cont:Singleton,$tp:{Result:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','map']};};
            $$singleton.$filter=function (selecting$599){
                var $$singleton=this;
                return (opt$600=(selecting$599($$singleton.element$571)?$$singleton:null),opt$600!==null?opt$600:getEmpty());
            };
            $$singleton.$filter.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','filter']};};
            $$singleton.fold=function (initial$601,accumulating$602,$$$mptypes){
                var $$singleton=this;
                return accumulating$602(initial$601,$$singleton.element$571);
            };
            $$singleton.fold.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$cont:Singleton,$tp:{Result:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','fold']};};
            $$singleton.find=function find(selecting$603){
                var $$singleton=this;
                if(selecting$603($$singleton.element$571)){
                    return $$singleton.element$571;
                }
                return null;
            };$$singleton.find.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','find']};};
            $$singleton.findLast=function (selecting$604){
                var $$singleton=this;
                return $$singleton.find($JsCallable(selecting$604,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$singleton.$$targs$$.Element,Element:$$singleton.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$singleton.findLast.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Singleton','$m','findLast']};};
            $$singleton.$sort=function (comparing$605){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.$sort.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Singleton,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','sort']};};
            $$singleton.any=function (selecting$606){
                var $$singleton=this;
                return selecting$606($$singleton.element$571);
            };
            $$singleton.any.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','any']};};
            $$singleton.$every=function (selecting$607){
                var $$singleton=this;
                return selecting$607($$singleton.element$571);
            };
            $$singleton.$every.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','every']};};
            $$singleton.skipping=function (skip$608){
                var $$singleton=this;
                return (opt$609=(skip$608.compare((1)).equals(getSmaller())?$$singleton:null),opt$609!==null?opt$609:getEmpty());
            };
            $$singleton.skipping.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','skipping']};};
            $$singleton.taking=function (take$610){
                var $$singleton=this;
                return (opt$611=(take$610.compare((0)).equals(getLarger())?$$singleton:null),opt$611!==null?opt$611:getEmpty());
            };
            $$singleton.taking.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$m','taking']};};
            defineAttr($$singleton,'coalesced',function(){
                var $$singleton=this;
                var self$612;
                if(isOfType((self$612=$$singleton),{t:Singleton,a:{Element:{t:Object$}}})){
                    return self$612;
                }
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{ t:'i', l:['Element',{t:Object$}]}}},$cont:Singleton,$an:function(){return[shared(),actual()];},d:['ceylon.language','Singleton','$at','coalesced']};});defineAttr($$singleton,'element$571',function(){return this.element$571_;},undefined,function(){return{mod:$$METAMODEL$$,$t:'Element',$cont:Singleton,d:['ceylon.language','Singleton','$at','element']};});
        })(Singleton.$$.prototype);
    }
    return Singleton;
}
exports.$init$Singleton=$init$Singleton;
$init$Singleton();
var opt$576,opt$577,opt$580,opt$583,opt$585,opt$587,opt$592,opt$593,opt$597,opt$600,opt$609,opt$611;
function AssertionException(message$613, $$assertionException){
    $init$AssertionException();
    if ($$assertionException===undefined)$$assertionException=new AssertionException.$$;
    $$assertionException.message$613_=message$613;
    Exception($$assertionException.message$613,undefined,$$assertionException);
    return $$assertionException;
}
AssertionException.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Exception},satisfies:[],$an:function(){return[shared()];},d:['ceylon.language','AssertionException']};};
exports.AssertionException=AssertionException;
function $init$AssertionException(){
    if (AssertionException.$$===undefined){
        initTypeProto(AssertionException,'ceylon.language::AssertionException',Exception);
        (function($$assertionException){
            defineAttr($$assertionException,'message$613',function(){return this.message$613_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:AssertionException,d:['ceylon.language','AssertionException','$at','message']};});
        })(AssertionException.$$.prototype);
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
Map.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Collection,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},{t:Correspondence,a:{Key:{t:Object$},Item:'Item'}},{t:Cloneable,a:{Clone:{t:Map,a:{Key:'Key',Item:'Item'}}}}],$an:function(){return[see([typeLiteral$model({Type:Entry}),,,,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','Map']};};
exports.Map=Map;
function $init$Map(){
    if (Map.$$===undefined){
        initTypeProto(Map,'ceylon.language::Map',$init$Collection(),$init$Correspondence(),$init$Cloneable());
        (function($$map){
            $$map.equals=function equals(that$614){
                var $$map=this;
                var that$615;
                if(isOfType((that$615=that$614),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})&&that$615.size.equals($$map.size)){
                    var it$616 = $$map.iterator();
                    var entry$617;while ((entry$617=it$616.next())!==getFinished()){
                        var item$618;
                        if((item$618=that$615.get(entry$617.key))!==null&&item$618.equals(entry$617.item)){
                            continue;
                        }else {
                            return false;
                        }
                    }
                    if (getFinished() === entry$617){
                        return true;
                    }
                }else {
                    return false;
                }
            };$$map.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:Map,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Map','$m','equals']};};
            defineAttr($$map,'hash',function(){
                var $$map=this;
                var hashCode$619=(1);
                var setHashCode$619=function(hashCode$620){return hashCode$619=hashCode$620;};
                var it$621 = $$map.iterator();
                var elem$622;while ((elem$622=it$621.next())!==getFinished()){
                    (hashCode$619=hashCode$619.times((31)));
                    (hashCode$619=hashCode$619.plus(elem$622.hash));
                }
                return hashCode$619;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:Map,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','Map','$at','hash']};});defineAttr($$map,'keys',function(){
                var $$map=this;
                return LazySet(Comprehension(function(){
                    var it$623=$$map.iterator();
                    var k$624,v$625;
                    var next$v$625=function(){
                        var entry$626;
                        if((entry$626=it$623.next())!==getFinished()){
                            k$624=entry$626.key;
                            v$625=entry$626.item;
                            return entry$626;
                        }
                        v$625=undefined;
                        return getFinished();
                    }
                    next$v$625();
                    return function(){
                        if(v$625!==undefined){
                            var k$624$627=k$624;
                            var v$625$628=v$625;
                            var tmpvar$629=k$624$627;
                            next$v$625();
                            return tmpvar$629;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Set,a:{Element:'Key'}},$cont:Map,$an:function(){return[actual(),shared(),$default()];},d:['ceylon.language','Map','$at','keys']};});
            defineAttr($$map,'values',function(){
                var $$map=this;
                return LazyList(Comprehension(function(){
                    var it$630=$$map.iterator();
                    var k$631,v$632;
                    var next$v$632=function(){
                        var entry$633;
                        if((entry$633=it$630.next())!==getFinished()){
                            k$631=entry$633.key;
                            v$632=entry$633.item;
                            return entry$633;
                        }
                        v$632=undefined;
                        return getFinished();
                    }
                    next$v$632();
                    return function(){
                        if(v$632!==undefined){
                            var k$631$634=k$631;
                            var v$632$635=v$632;
                            var tmpvar$636=v$632$635;
                            next$v$632();
                            return tmpvar$636;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$map.$$targs$$.Item}),{Element:$$map.$$targs$$.Item});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Collection,a:{Element:'Item'}},$cont:Map,$an:function(){return[shared(),$default()];},d:['ceylon.language','Map','$at','values']};});
            defineAttr($$map,'inverse',function(){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$637=$$map.iterator();
                    var key$638,item$639;
                    var next$item$639=function(){
                        var entry$640;
                        if((entry$640=it$637.next())!==getFinished()){
                            key$638=entry$640.key;
                            item$639=entry$640.item;
                            return entry$640;
                        }
                        item$639=undefined;
                        return getFinished();
                    }
                    next$item$639();
                    return function(){
                        if(item$639!==undefined){
                            var key$638$641=key$638;
                            var item$639$642=item$639;
                            var tmpvar$643=Entry(item$639$642,LazySet(Comprehension(function(){
                                var it$644=$$map.iterator();
                                var k$645,i$646;
                                var next$i$646=function(){
                                    var entry$647;
                                    while((entry$647=it$644.next())!==getFinished()){
                                        k$645=entry$647.key;
                                        i$646=entry$647.item;
                                        if(i$646.equals(item$639$642)){
                                            return entry$647;
                                        }
                                    }
                                    i$646=undefined;
                                    return getFinished();
                                }
                                next$i$646();
                                return function(){
                                    if(i$646!==undefined){
                                        var k$645$648=k$645;
                                        var i$646$649=i$646;
                                        var tmpvar$650=k$645$648;
                                        next$i$646();
                                        return tmpvar$650;
                                    }
                                    return getFinished();
                                }
                            },{Absent:{t:Null},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
                            next$item$639();
                            return tmpvar$643;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}}}}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Map,a:{Key:'Item',Item:{t:Set,a:{Element:'Key'}}}},$cont:Map,$an:function(){return[shared(),$default()];},d:['ceylon.language','Map','$at','inverse']};});
            $$map.mapItems=function (mapping$651,$$$mptypes){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$652=$$map.iterator();
                    var key$653,item$654;
                    var next$item$654=function(){
                        var entry$655;
                        if((entry$655=it$652.next())!==getFinished()){
                            key$653=entry$655.key;
                            item$654=entry$655.item;
                            return entry$655;
                        }
                        item$654=undefined;
                        return getFinished();
                    }
                    next$item$654();
                    return function(){
                        if(item$654!==undefined){
                            var key$653$656=key$653;
                            var item$654$657=item$654;
                            var tmpvar$658=Entry(key$653$656,mapping$651(key$653$656,item$654$657),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
                            next$item$654();
                            return tmpvar$658;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result}}}),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
            };
            $$map.mapItems.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Map,a:{Key:'Key',Item:'Result'}},$ps:[{$nm:'mapping',$mt:'prm',$t:'Result'}],$cont:Map,$tp:{Result:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),$default()];},d:['ceylon.language','Map','$m','mapItems']};};
        })(Map.$$.prototype);
    }
    return Map;
}
exports.$init$Map=$init$Map;
$init$Map();
function LazyMap(entries$659, $$targs$$,$$lazyMap){
    $init$LazyMap();
    if ($$lazyMap===undefined)$$lazyMap=new LazyMap.$$;
    set_type_args($$lazyMap,$$targs$$);
    $$lazyMap.entries$659_=entries$659;
    Map($$lazyMap.$$targs$$===undefined?$$targs$$:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item},$$lazyMap);
    return $$lazyMap;
}
LazyMap.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Map,a:{Key:'Key',Item:'Item'}}],$an:function(){return[by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','LazyMap']};};
exports.LazyMap=LazyMap;
function $init$LazyMap(){
    if (LazyMap.$$===undefined){
        initTypeProto(LazyMap,'ceylon.language::LazyMap',Basic,$init$Map());
        (function($$lazyMap){
            defineAttr($$lazyMap,'first',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$659.first;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Entry,a:{Key:'Key',Item:'Item'}}]},$cont:LazyMap,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyMap','$at','first']};});
            defineAttr($$lazyMap,'last',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$659.last;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Entry,a:{Key:'Key',Item:'Item'}}]},$cont:LazyMap,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyMap','$at','last']};});
            defineAttr($$lazyMap,'clone',function(){
                var $$lazyMap=this;
                return $$lazyMap;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:LazyMap,a:{Key:'Key',Item:'Item'}},$cont:LazyMap,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyMap','$at','clone']};});
            defineAttr($$lazyMap,'size',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$659.size;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazyMap,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyMap','$at','size']};});
            $$lazyMap.get=function (key$660){
                var $$lazyMap=this;
                return (opt$661=$$lazyMap.entries$659.find($JsCallable(function (e$662){
                    var $$lazyMap=this;
                    return e$662.key.equals(key$660);
                },[{$nm:'e',$mt:'prm',$t:{t:Entry,a:{Key:'Key',Item:'Item'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Entry,a:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item}},Element:{t:Entry,a:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item}}}},Return:{t:Boolean$}})),opt$661!==null?opt$661.item:null);
            };
            $$lazyMap.get.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Item']},$ps:[{$nm:'key',$mt:'prm',$t:{t:Object$}}],$cont:LazyMap,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyMap','$m','get']};};
            $$lazyMap.iterator=function (){
                var $$lazyMap=this;
                return $$lazyMap.entries$659.iterator();
            };
            $$lazyMap.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},$ps:[],$cont:LazyMap,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyMap','$m','iterator']};};
            $$lazyMap.equals=function equals(that$663){
                var $$lazyMap=this;
                var that$664;
                if(isOfType((that$664=that$663),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    if(that$664.size.equals($$lazyMap.size)){
                        var it$665 = $$lazyMap.iterator();
                        var entry$666;while ((entry$666=it$665.next())!==getFinished()){
                            var item$667;
                            if((item$667=that$664.get(entry$666.key))!==null){
                                if(item$667.equals(entry$666.item)){
                                    continue;
                                }
                            }
                            return false;
                        }
                        if (getFinished() === entry$666){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyMap.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:LazyMap,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazyMap','$m','equals']};};
            defineAttr($$lazyMap,'hash',function(){
                var $$lazyMap=this;
                var hashCode$668=(1);
                var setHashCode$668=function(hashCode$669){return hashCode$668=hashCode$669;};
                var it$670 = $$lazyMap.entries$659.iterator();
                var elem$671;while ((elem$671=it$670.next())!==getFinished()){
                    (hashCode$668=hashCode$668.times((31)));
                    (hashCode$668=hashCode$668.plus(elem$671.hash));
                }
                return hashCode$668;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazyMap,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazyMap','$at','hash']};});defineAttr($$lazyMap,'entries$659',function(){return this.entries$659_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},$cont:LazyMap,d:['ceylon.language','LazyMap','$at','entries']};});
        })(LazyMap.$$.prototype);
    }
    return LazyMap;
}
exports.$init$LazyMap=$init$LazyMap;
$init$LazyMap();
var opt$661;
function LazyList(elems$672, $$targs$$,$$lazyList){
    $init$LazyList();
    if ($$lazyList===undefined)$$lazyList=new LazyList.$$;
    set_type_args($$lazyList,$$targs$$);
    $$lazyList.elems$672_=elems$672;
    List($$lazyList.$$targs$$===undefined?$$targs$$:{Element:$$lazyList.$$targs$$.Element},$$lazyList);
    return $$lazyList;
}
LazyList.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Element:{'var':'out'}},satisfies:[{t:List,a:{Element:'Element'}}],$an:function(){return[by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','LazyList']};};
exports.LazyList=LazyList;
function $init$LazyList(){
    if (LazyList.$$===undefined){
        initTypeProto(LazyList,'ceylon.language::LazyList',Basic,$init$List());
        (function($$lazyList){
            defineAttr($$lazyList,'lastIndex',function(){
                var $$lazyList=this;
                var size$673=$$lazyList.elems$672.size;
                return (opt$674=(size$673.compare((0)).equals(getLarger())?size$673.minus((1)):null),opt$674!==null?opt$674:null);
                var opt$674;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$at','lastIndex']};});$$lazyList.get=function get(index$675){
                var $$lazyList=this;
                if(index$675.equals((0))){
                    return $$lazyList.elems$672.first;
                }else {
                    return $$lazyList.elems$672.skipping(index$675).first;
                }
            };$$lazyList.get.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$m','get']};};
            defineAttr($$lazyList,'rest',function(){
                var $$lazyList=this;
                return LazyList($$lazyList.elems$672.rest,{Element:$$lazyList.$$targs$$.Element});
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$at','rest']};});
            $$lazyList.iterator=function (){
                var $$lazyList=this;
                return $$lazyList.elems$672.iterator();
            };
            $$lazyList.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$m','iterator']};};
            defineAttr($$lazyList,'reversed',function(){
                var $$lazyList=this;
                return $$lazyList.elems$672.sequence.reversed;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$at','reversed']};});
            defineAttr($$lazyList,'clone',function(){
                var $$lazyList=this;
                return $$lazyList;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$at','clone']};});
            $$lazyList.span=function span(from$676,to$677){
                var $$lazyList=this;
                if((to$677.compare((0)).equals(getSmaller())&&from$676.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var toIndex$678=largest(to$677,(0),{Element:{t:Integer}});
                var fromIndex$679=largest(from$676,(0),{Element:{t:Integer}});
                if((toIndex$678.compare(fromIndex$679)!==getSmaller())){
                    var els$680=(opt$681=(fromIndex$679.compare((0)).equals(getLarger())?$$lazyList.elems$672.skipping(fromIndex$679):null),opt$681!==null?opt$681:$$lazyList.elems$672);
                    var opt$681;
                    return LazyList(els$680.taking(toIndex$678.minus(fromIndex$679).plus((1))),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    var seq$682=(opt$683=(toIndex$678.compare((0)).equals(getLarger())?$$lazyList.elems$672.skipping(toIndex$678):null),opt$683!==null?opt$683:$$lazyList.elems$672);
                    var opt$683;
                    return seq$682.taking(fromIndex$679.minus(toIndex$678).plus((1))).sequence.reversed;
                }
            };$$lazyList.span.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$m','span']};};
            $$lazyList.spanTo=function (to$684){
                var $$lazyList=this;
                return (opt$685=(to$684.compare((0)).equals(getSmaller())?getEmpty():null),opt$685!==null?opt$685:LazyList($$lazyList.elems$672.taking(to$684.plus((1))),{Element:$$lazyList.$$targs$$.Element}));
            };
            $$lazyList.spanTo.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$m','spanTo']};};
            $$lazyList.spanFrom=function (from$686){
                var $$lazyList=this;
                return (opt$687=(from$686.compare((0)).equals(getLarger())?LazyList($$lazyList.elems$672.skipping(from$686),{Element:$$lazyList.$$targs$$.Element}):null),opt$687!==null?opt$687:$$lazyList);
            };
            $$lazyList.spanFrom.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$m','spanFrom']};};
            $$lazyList.segment=function segment(from$688,length$689){
                var $$lazyList=this;
                if(length$689.compare((0)).equals(getLarger())){
                    var els$690=(opt$691=(from$688.compare((0)).equals(getLarger())?$$lazyList.elems$672.skipping(from$688):null),opt$691!==null?opt$691:$$lazyList.elems$672);
                    var opt$691;
                    return LazyList(els$690.taking(length$689),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    return getEmpty();
                }
            };$$lazyList.segment.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$cont:LazyList,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazyList','$m','segment']};};
            $$lazyList.equals=function equals(that$692){
                var $$lazyList=this;
                var that$693;
                if(isOfType((that$693=that$692),{t:List,a:{Element:{t:Anything}}})){
                    var size$694=$$lazyList.elems$672.size;
                    if(that$693.size.equals(size$694)){
                        var it$695 = Range((0),size$694.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$696;while ((i$696=it$695.next())!==getFinished()){
                            var x$697=$$lazyList.get(i$696);
                            var y$698=that$693.get(i$696);
                            var x$699;
                            if((x$699=x$697)!==null){
                                var y$700;
                                if((y$700=y$698)!==null){
                                    if((!x$699.equals(y$700))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$701;
                                if((y$701=y$698)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$696){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyList.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazyList','$m','equals']};};
            defineAttr($$lazyList,'hash',function(){
                var $$lazyList=this;
                var hash$702=(1);
                var setHash$702=function(hash$703){return hash$702=hash$703;};
                var it$704 = $$lazyList.elems$672.iterator();
                var elem$705;while ((elem$705=it$704.next())!==getFinished()){
                    (hash$702=hash$702.times((31)));
                    var elem$706;
                    if((elem$706=elem$705)!==null){
                        (hash$702=hash$702.plus(elem$706.hash));
                    }
                }
                return hash$702;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazyList','$at','hash']};});$$lazyList.findLast=function (selecting$707){
                var $$lazyList=this;
                return $$lazyList.elems$672.findLast($JsCallable(selecting$707,[{$nm:'elem',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$lazyList.$$targs$$.Element,Element:$$lazyList.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$lazyList.findLast.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$cont:LazyList,$an:function(){return[shared(),$default(),actual()];},d:['ceylon.language','LazyList','$m','findLast']};};
            defineAttr($$lazyList,'first',function(){
                var $$lazyList=this;
                return $$lazyList.elems$672.first;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazyList','$at','first']};});
            defineAttr($$lazyList,'last',function(){
                var $$lazyList=this;
                return $$lazyList.elems$672.last;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Element']},$cont:LazyList,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazyList','$at','last']};});
            defineAttr($$lazyList,'elems$672',function(){return this.elems$672_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$cont:LazyList,d:['ceylon.language','LazyList','$at','elems']};});
        })(LazyList.$$.prototype);
    }
    return LazyList;
}
exports.$init$LazyList=$init$LazyList;
$init$LazyList();
var opt$685,opt$687;
function LazySet(elems$708, $$targs$$,$$lazySet){
    $init$LazySet();
    if ($$lazySet===undefined)$$lazySet=new LazySet.$$;
    set_type_args($$lazySet,$$targs$$);
    $$lazySet.elems$708_=elems$708;
    Set($$lazySet.$$targs$$===undefined?$$targs$$:{Element:$$lazySet.$$targs$$.Element},$$lazySet);
    return $$lazySet;
}
LazySet.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Element:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Set,a:{Element:'Element'}}],$an:function(){return[by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','LazySet']};};
exports.LazySet=LazySet;
function $init$LazySet(){
    if (LazySet.$$===undefined){
        initTypeProto(LazySet,'ceylon.language::LazySet',Basic,$init$Set());
        (function($$lazySet){
            defineAttr($$lazySet,'clone',function(){
                var $$lazySet=this;
                return $$lazySet;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:LazySet,a:{Element:'Element'}},$cont:LazySet,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$at','clone']};});
            defineAttr($$lazySet,'size',function(){
                var $$lazySet=this;
                var c$709=(0);
                var setC$709=function(c$710){return c$709=c$710;};
                var sorted$711=$$lazySet.elems$708.$sort($JsCallable(byIncreasing($JsCallable(function (e$712){
                    var $$lazySet=this;
                    return e$712.hash;
                },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Integer}}),{Value:{t:Integer},Element:$$lazySet.$$targs$$.Element}),[{$nm:'p1',$mt:'prm',$t:'Element'},{$nm:'p2',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Comparison}}));
                var l$713;
                if((l$713=sorted$711.first)!==null){
                    c$709=(1);
                    var last$714=l$713;
                    var setLast$714=function(last$715){return last$714=last$715;};
                    var it$716 = sorted$711.rest.iterator();
                    var e$717;while ((e$717=it$716.next())!==getFinished()){
                        if((!e$717.equals(last$714))){
                            (oldc$718=c$709,c$709=oldc$718.successor,oldc$718);
                            var oldc$718;
                            last$714=e$717;
                        }
                    }
                }
                return c$709;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazySet,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$at','size']};});$$lazySet.iterator=function iterator(){
                var $$lazySet=this;
                function iterator$719($$targs$$){
                    var $$iterator$719=new iterator$719.$$;
                    $$iterator$719.$$targs$$=$$targs$$;
                    Iterator({Element:$$lazySet.$$targs$$.Element},$$iterator$719);
                    $$iterator$719.sorted$720_=$$lazySet.elems$708.$sort($JsCallable(byIncreasing($JsCallable(function (e$721){
                        var $$lazySet=this;
                        return e$721.hash;
                    },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Integer}}),{Value:{t:Integer},Element:$$lazySet.$$targs$$.Element}),[{$nm:'p1',$mt:'prm',$t:'Element'},{$nm:'p2',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Comparison}})).iterator();
                    $$iterator$719.ready$722_=$$iterator$719.sorted$720.next();
                    return $$iterator$719;
                }
                function $init$iterator$719(){
                    if (iterator$719.$$===undefined){
                        initTypeProto(iterator$719,'ceylon.language::LazySet.iterator.iterator',Basic,$init$Iterator());
                    }
                    return iterator$719;
                }
                $init$iterator$719();
                (function($$iterator$719){
                    defineAttr($$iterator$719,'sorted$720',function(){return this.sorted$720_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$719,d:['ceylon.language','LazySet','$m','iterator','$o','iterator','$at','sorted']};});
                    defineAttr($$iterator$719,'ready$722',function(){return this.ready$722_;},function(ready$723){return this.ready$722_=ready$723;},function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$cont:iterator$719,$an:function(){return[variable()];},d:['ceylon.language','LazySet','$m','iterator','$o','iterator','$at','ready']};});
                    $$iterator$719.next=function next(){
                        var $$iterator$719=this;
                        var next$724=$$iterator$719.ready$722;
                        var next$725;
                        if(!isOfType((next$725=next$724),{t:Finished})){
                            while(next$725.equals($$iterator$719.ready$722)){
                                $$iterator$719.ready$722=$$iterator$719.sorted$720.next();
                            }
                        }
                        return next$724;
                    };$$iterator$719.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$cont:iterator$719,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$m','iterator','$o','iterator','$m','next']};};
                })(iterator$719.$$.prototype);
                var iterator$726=iterator$719({Element:$$lazySet.$$targs$$.Element});
                var getIterator$726=function(){
                    return iterator$726;
                }
                return getIterator$726();
            };$$lazySet.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$cont:LazySet,$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$m','iterator']};};
            $$lazySet.union=function (set$727,$$$mptypes){
                var $$lazySet=this;
                return LazySet($$lazySet.elems$708.chain(set$727,{Other:$$$mptypes.Other,OtherAbsent:{t:Null}}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };
            $$lazySet.union.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Set,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$m','union']};};
            $$lazySet.intersection=function (set$728,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var e$731;
                    var it$729=set$728.iterator();
                    var e$730=getFinished();
                    var e$731;
                    var next$e$730=function(){
                        while((e$730=it$729.next())!==getFinished()){
                            if(isOfType((e$731=e$730),$$lazySet.$$targs$$.Element)&&$$lazySet.contains(e$731)){
                                return e$730;
                            }
                        }
                        return getFinished();
                    }
                    next$e$730();
                    return function(){
                        if(e$730!==getFinished()){
                            var e$730$732=e$730;
                            var tmpvar$733=e$731;
                            next$e$730();
                            return tmpvar$733;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}}),{Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}});
            };
            $$lazySet.intersection.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Set,a:{Element:{ t:'i', l:['Element','Other']}}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$m','intersection']};};
            $$lazySet.exclusiveUnion=function exclusiveUnion(other$734,$$$mptypes){
                var $$lazySet=this;
                var hereNotThere$735=Comprehension(function(){
                    var it$736=$$lazySet.elems$708.iterator();
                    var e$737=getFinished();
                    var next$e$737=function(){
                        while((e$737=it$736.next())!==getFinished()){
                            if((!other$734.contains(e$737))){
                                return e$737;
                            }
                        }
                        return getFinished();
                    }
                    next$e$737();
                    return function(){
                        if(e$737!==getFinished()){
                            var e$737$738=e$737;
                            var tmpvar$739=e$737$738;
                            next$e$737();
                            return tmpvar$739;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$lazySet.$$targs$$.Element});
                var thereNotHere$740=Comprehension(function(){
                    var it$741=other$734.iterator();
                    var e$742=getFinished();
                    var next$e$742=function(){
                        while((e$742=it$741.next())!==getFinished()){
                            if((!$$lazySet.contains(e$742))){
                                return e$742;
                            }
                        }
                        return getFinished();
                    }
                    next$e$742();
                    return function(){
                        if(e$742!==getFinished()){
                            var e$742$743=e$742;
                            var tmpvar$744=e$742$743;
                            next$e$742();
                            return tmpvar$744;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$$mptypes.Other});
                return LazySet(hereNotThere$735.chain(thereNotHere$740,{Other:$$$mptypes.Other,OtherAbsent:{t:Null}}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };$$lazySet.exclusiveUnion.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Set,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$m','exclusiveUnion']};};
            $$lazySet.complement=function (set$745,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var it$746=$$lazySet.iterator();
                    var e$747=getFinished();
                    var next$e$747=function(){
                        while((e$747=it$746.next())!==getFinished()){
                            if((!set$745.contains(e$747))){
                                return e$747;
                            }
                        }
                        return getFinished();
                    }
                    next$e$747();
                    return function(){
                        if(e$747!==getFinished()){
                            var e$747$748=e$747;
                            var tmpvar$749=e$747$748;
                            next$e$747();
                            return tmpvar$749;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$lazySet.$$targs$$.Element}),{Element:$$lazySet.$$targs$$.Element});
            };
            $$lazySet.complement.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Set,a:{Element:'Element'}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$cont:LazySet,$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language','LazySet','$m','complement']};};
            $$lazySet.equals=function equals(that$750){
                var $$lazySet=this;
                var that$751;
                if(isOfType((that$751=that$750),{t:Set,a:{Element:{t:Object$}}})){
                    if(that$751.size.equals($$lazySet.size)){
                        var it$752 = $$lazySet.elems$708.iterator();
                        var element$753;while ((element$753=it$752.next())!==getFinished()){
                            if((!that$751.contains(element$753))){
                                return false;
                            }
                        }
                        if (getFinished() === element$753){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazySet.equals.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$cont:LazySet,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazySet','$m','equals']};};
            defineAttr($$lazySet,'hash',function(){
                var $$lazySet=this;
                var hashCode$754=(1);
                var setHashCode$754=function(hashCode$755){return hashCode$754=hashCode$755;};
                var it$756 = $$lazySet.elems$708.iterator();
                var elem$757;while ((elem$757=it$756.next())!==getFinished()){
                    (hashCode$754=hashCode$754.times((31)));
                    (hashCode$754=hashCode$754.plus(elem$757.hash));
                }
                return hashCode$754;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$cont:LazySet,$an:function(){return[shared(),actual(),$default()];},d:['ceylon.language','LazySet','$at','hash']};});defineAttr($$lazySet,'elems$708',function(){return this.elems$708_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$cont:LazySet,d:['ceylon.language','LazySet','$at','elems']};});
        })(LazySet.$$.prototype);
    }
    return LazySet;
}
exports.$init$LazySet=$init$LazySet;
$init$LazySet();
function any(values$758){
    var it$759 = values$758.iterator();
    var val$760;while ((val$760=it$759.next())!==getFinished()){
        if(val$760){
            return true;
        }
    }
    return false;
}
exports.any=any;
any.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','any']};};
var byDecreasing=function (comparable$761,$$$mptypes){
    return function(x$762,y$763){{
        return comparable$761(y$763).compare(comparable$761(x$762));
    }
}
}
;
byDecreasing.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparable',$mt:'prm',$t:'Value'}],$tp:{Element:{},Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','byDecreasing']};};
exports.byDecreasing=byDecreasing;
var byIncreasing=function (comparable$764,$$$mptypes){
    return function(x$765,y$766){{
        return comparable$764(x$765).compare(comparable$764(y$766));
    }
}
}
;
byIncreasing.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparable',$mt:'prm',$t:'Value'}],$tp:{Element:{},Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','byIncreasing']};};
exports.byIncreasing=byIncreasing;
var byItem=function (comparing$767,$$$mptypes){
    return function(x$768,y$769){{
        return comparing$767(x$768.item,y$769.item);
    }
}
}
;
byItem.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$tp:{Item:{'satisfies':[{t:Object$}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','byItem']};};
exports.byItem=byItem;
var byKey=function (comparing$770,$$$mptypes){
    return function(x$771,y$772){{
        return comparing$770(x$771.key,y$772.key);
    }
}
}
;
byKey.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Comparison},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$tp:{Key:{'satisfies':[{t:Object$}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','byKey']};};
exports.byKey=byKey;
var coalesce=function (values$773,$$$mptypes){
    return values$773.coalesced;
};
coalesce.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{ t:'i', l:['Element',{t:Object$}]}}},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{}},$an:function(){return[shared()];},d:['ceylon.language','coalesce']};};
exports.coalesce=coalesce;
function count(values$774){
    var count$775=(0);
    var setCount$775=function(count$776){return count$775=count$776;};
    var it$777 = values$774.iterator();
    var val$778;while ((val$778=it$777.next())!==getFinished()){
        if(val$778){
            (oldcount$779=count$775,count$775=oldcount$779.successor,oldcount$779);
            var oldcount$779;
        }
    }
    return count$775;
}
exports.count=count;
count.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Integer},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[shared()];},d:['ceylon.language','count']};};
function emptyOrSingleton(element$780,$$$mptypes){
    var element$781;
    if((element$781=element$780)!==null){
        return Singleton(element$781,{Element:$$$mptypes.Element});
    }else {
        return getEmpty();
    }
}
exports.emptyOrSingleton=emptyOrSingleton;
emptyOrSingleton.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:{ t:'u', l:[{t:Null},'Element']}}],$tp:{Element:{'satisfies':[{t:Object$}]}},$an:function(){return[see([typeLiteral$model({Type:Singleton}),typeLiteral$model({Type:Empty})].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:InterfaceDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','emptyOrSingleton']};};
var curry=function (f$782,$$$mptypes){
    return function(first$783){{
        return flatten($JsCallable(function (args$784){
            return unflatten($JsCallable(f$782,[],{Arguments:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return})(Tuple(first$783,args$784,{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return});
        },[{$nm:'args',$mt:'prm',$t:'Rest'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Rest,Element:$$$mptypes.Rest}},Return:$$$mptypes.Return}),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return});
    }
}
}
;
curry.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:'Rest',Return:'Return'}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}},Return:'Return'}}}],$tp:{Return:{},Argument:{},First:{'satisfies':['Argument']},Rest:{'satisfies':[{t:Sequential,a:{Element:'Argument'}}]}},$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','curry']};};
exports.curry=curry;
var uncurry=function (f$785,$$$mptypes){
return flatten($JsCallable(function (args$786){
    return unflatten($JsCallable(f$785(args$786.first),[],{Arguments:$$$mptypes.Rest,Return:$$$mptypes.Return}),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return})(args$786.rest,{Args:$$$mptypes.Rest,Return:$$$mptypes.Return});
},[{$nm:'args',$mt:'prm',$t:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Element:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}}}},Return:$$$mptypes.Return}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return});
};
uncurry.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}},Return:'Return'}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:'Rest',Return:'Return'}}}],$tp:{Return:{},Argument:{},First:{'satisfies':['Argument']},Rest:{'satisfies':[{t:Sequential,a:{Element:'Argument'}}]}},$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','uncurry']};};
exports.uncurry=uncurry;
var entries=function (elements$787,$$$mptypes){
    return elements$787.indexed;
};
entries.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{}},$an:function(){return[shared()];},d:['ceylon.language','entries']};};
exports.entries=entries;
var equalTo=function (val$788,$$$mptypes){
    return function(element$789){{
        return element$789.equals(val$788);
    }
}
}
;
equalTo.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Object$}]}},$an:function(){return[shared()];},d:['ceylon.language','equalTo']};};
exports.equalTo=equalTo;
function every(values$790){
    var it$791 = values$790.iterator();
    var val$792;while ((val$792=it$791.next())!==getFinished()){
        if((!val$792)){
            return false;
        }
    }
    return true;
}
exports.every=every;
every.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','every']};};
function first(values$793,$$$mptypes){
    var first$794;
    var next$795;
    if(!isOfType((next$795=values$793.iterator().next()),{t:Finished})){
        first$794=next$795;
    }else {
        first$794=null;
    }
    //assert at first.ceylon (12:4-12:34)
    var first$796;
    if (!(isOfType((first$796=first$794),{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}))) {throw wrapexc(AssertionException("Assertion failed: \'is Absent|Value first\' at first.ceylon (12:11-12:33)"),'12:4-12:34','first.ceylon'); }
    return first$796;
}
exports.first=first;
first.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[shared()];},d:['ceylon.language','first']};};
var forItem=function (resulting$797,$$$mptypes){
    return function(entry$798){{
        return resulting$797(entry$798.item);
    }
}
}
;
forItem.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'resulting',$mt:'prm',$t:'Result'}],$tp:{Item:{'satisfies':[{t:Object$}]},Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','forItem']};};
exports.forItem=forItem;
var forKey=function (resulting$799,$$$mptypes){
    return function(entry$800){{
        return resulting$799(entry$800.key);
    }
}
}
;
forKey.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Result',$ps:[{$nm:'resulting',$mt:'prm',$t:'Result'}],$tp:{Key:{'satisfies':[{t:Object$}]},Result:{}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','forKey']};};
exports.forKey=forKey;
var greaterThan=function (val$801,$$$mptypes){
    return function(element$802){{
        return element$802.compare(val$801).equals(getLarger());
    }
}
}
;
greaterThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[shared()];},d:['ceylon.language','greaterThan']};};
exports.greaterThan=greaterThan;
var join=function (iterables$803,$$$mptypes){
    if(iterables$803===undefined){iterables$803=getEmpty();}
    return Comprehension(function(){
        var it$804=iterables$803.iterator();
        var it$805=getFinished();
        var next$it$805=function(){
            if((it$805=it$804.next())!==getFinished()){
                it$806=it$805.iterator();
                next$val$807();
                return it$805;
            }
            return getFinished();
        }
        var it$806;
        var val$807=getFinished();
        var next$val$807=function(){return val$807=it$806.next();}
        next$it$805();
        return function(){
            do{
                if(val$807!==getFinished()){
                    var val$807$808=val$807;
                    var tmpvar$809=val$807$808;
                    next$val$807();
                    return tmpvar$809;
                }
            }while(next$it$805()!==getFinished());
            return getFinished();
        }
    },{Absent:{t:Null},Element:$$$mptypes.Element}).sequence;
};
join.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'iterables',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}}}],$tp:{Element:{}},$an:function(){return[see([typeLiteral$model({Type:SequenceBuilder})].reifyCeylonType({Absent:{t:Null},Element:{t:ClassDeclaration$model$declaration}})),shared()];},d:['ceylon.language','join']};};
exports.join=join;
var largest=function (x$810,y$811,$$$mptypes){
    return (opt$812=(x$810.compare(y$811).equals(getLarger())?x$810:null),opt$812!==null?opt$812:y$811);
};
largest.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[see([typeLiteral$model({Type:Comparable}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','largest']};};
exports.largest=largest;
var opt$812;
var lessThan=function (val$813,$$$mptypes){
    return function(element$814){{
        return element$814.compare(val$813).equals(getSmaller());
    }
}
}
;
lessThan.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[shared()];},d:['ceylon.language','lessThan']};};
exports.lessThan=lessThan;
function max(values$815,$$$mptypes){
    var first$816=values$815.first;
    var first$817;
    if((first$817=first$816)!==null){
        var max$818=first$817;
        var setMax$818=function(max$819){return max$818=max$819;};
        var it$820 = values$815.rest.iterator();
        var val$821;while ((val$821=it$820.next())!==getFinished()){
            if(val$821.compare(max$818).equals(getLarger())){
                max$818=val$821;
            }
        }
        return max$818;
    }else {
        return first$816;
    }
}
exports.max=max;
max.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[see([typeLiteral$model({Type:Comparable}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','max']};};
function min(values$822,$$$mptypes){
    var first$823=values$822.first;
    var first$824;
    if((first$824=first$823)!==null){
        var min$825=first$824;
        var setMin$825=function(min$826){return min$825=min$826;};
        var it$827 = values$822.rest.iterator();
        var val$828;while ((val$828=it$827.next())!==getFinished()){
            if(val$828.compare(min$825).equals(getSmaller())){
                min$825=val$828;
            }
        }
        return min$825;
    }else {
        return first$823;
    }
}
exports.min=min;
min.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[see([typeLiteral$model({Type:Comparable}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','min']};};
var smallest=function (x$829,y$830,$$$mptypes){
    return (opt$831=(x$829.compare(y$830).equals(getSmaller())?x$829:null),opt$831!==null?opt$831:y$830);
};
smallest.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[see([typeLiteral$model({Type:Comparable}),,].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:InterfaceDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}})),shared()];},d:['ceylon.language','smallest']};};
exports.smallest=smallest;
var opt$831;
function sum(values$832,$$$mptypes){
    var sum$833=values$832.first;
    var setSum$833=function(sum$834){return sum$833=sum$834;};
    var it$835 = values$832.rest.iterator();
    var val$836;while ((val$836=it$835.next())!==getFinished()){
        (sum$833=sum$833.plus(val$836));
    }
    return sum$833;
}
exports.sum=sum;
sum.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Summable,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','sum']};};
function product(values$837,$$$mptypes){
    var product$838=values$837.first;
    var setProduct$838=function(product$839){return product$838=product$839;};
    var it$840 = values$837.rest.iterator();
    var val$841;while ((val$841=it$840.next())!==getFinished()){
        (product$838=product$838.times(val$841));
    }
    return product$838;
}
exports.product=product;
product.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Numeric,a:{Other:'Value'}}]}},$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','product']};};
function zip(keys$842,items$843,$$$mptypes){
    var iter$844=items$843.iterator();
    return Comprehension(function(){
        var item$847;
        var it$845=keys$842.iterator();
        var key$846=getFinished();
        var item$847;
        var next$key$846=function(){
            while((key$846=it$845.next())!==getFinished()){
                if(!isOfType((item$847=iter$844.next()),{t:Finished})){
                    return key$846;
                }
            }
            return getFinished();
        }
        next$key$846();
        return function(){
            if(key$846!==getFinished()){
                var key$846$848=key$846;
                var tmpvar$849=Entry(key$846$848,item$847,{Key:$$$mptypes.Key,Item:$$$mptypes.Item});
                next$key$846();
                return tmpvar$849;
            }
            return getFinished();
        }
    },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$$mptypes.Key,Item:$$$mptypes.Item}}}).sequence;
}
exports.zip=zip;
zip.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}},{$nm:'items',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Item'}}}],$tp:{Key:{'satisfies':[{t:Object$}]},Item:{'satisfies':[{t:Object$}]}},$an:function(){return[shared()];},d:['ceylon.language','zip']};};
var print=function (val$850){
    return getProcess().writeLine(stringify(val$850));
};
print.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Anything},$ps:[{$nm:'val',$mt:'prm',$t:{t:Anything}}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','print']};};
exports.print=print;
function printAll(values$851,separator$852){
    if(separator$852===undefined){separator$852=String$(", ",2);}
    var first$853;
    if((first$853=values$851.first)!==null){
        getProcess().write(stringify(first$853));
        var it$854 = values$851.rest.iterator();
        var val$855;while ((val$855=it$854.next())!==getFinished()){
            getProcess().write(separator$852);
            getProcess().write(stringify(val$855));
        }
    }
    getProcess().write(getProcess().newline);
}
exports.printAll=printAll;
printAll.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Anything},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Anything}}}},{$nm:'separator',$mt:'prm',$def:1,$t:{t:String$}}],$an:function(){return[by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','printAll']};};
var stringify=function (val$856){
    return (opt$857=(opt$858=val$856,opt$858!==null?opt$858.string:null),opt$857!==null?opt$857:String$("<null>",6));
};
stringify.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:String$},$ps:[{$nm:'val',$mt:'prm',$t:{t:Anything}}],d:['ceylon.language','stringify']};};
var opt$857,opt$858;
var getNothing=function(){
    throw wrapexc(Exception(),'4:25-4:30','ceylon/language/nothing.ceylon');
}
exports.getNothing=getNothing;
var identical=function (x$859,y$860){
    return (x$859===y$860);
};
identical.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$ps:[{$nm:'x',$mt:'prm',$t:{t:Identifiable}},{$nm:'y',$mt:'prm',$t:{t:Identifiable}}],$an:function(){return[see([].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','identical']};};
exports.identical=identical;
var compose=function (x$861,y$862,$$$mptypes){
    return flatten($JsCallable(function (args$863){
        return x$861(unflatten($JsCallable(y$862,[],{Arguments:$$$mptypes.Args,Return:$$$mptypes.Y}),{Args:$$$mptypes.Args,Return:$$$mptypes.Y})(args$863,{Args:$$$mptypes.Args,Return:$$$mptypes.Y}));
    },[{$nm:'args',$mt:'prm',$t:'Args'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Args,Element:$$$mptypes.Args}},Return:$$$mptypes.X}),{Args:$$$mptypes.Args,Return:$$$mptypes.X});
};
compose.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:'Args',Return:'X'}},$ps:[{$nm:'x',$mt:'prm',$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:'Y',Element:'Y'}},Return:'X'}}},{$nm:'y',$mt:'prm',$t:{t:Callable,a:{Arguments:'Args',Return:'Y'}}}],$tp:{X:{},Y:{},Args:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','compose']};};
exports.compose=compose;
var shuffle=function (f$864,$$$mptypes){
    return flatten($JsCallable(function (secondArgs$865){
        return flatten($JsCallable(function (firstArgs$866){
            return unflatten($JsCallable(unflatten($JsCallable(f$864,[],{Arguments:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}}),{Args:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}})(firstArgs$866,{Args:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}}),[],{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}),{Args:$$$mptypes.SecondArgs,Return:$$$mptypes.Result})(secondArgs$865,{Args:$$$mptypes.SecondArgs,Return:$$$mptypes.Result});
        },[{$nm:'firstArgs',$mt:'prm',$t:'FirstArgs'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.FirstArgs,Element:$$$mptypes.FirstArgs}},Return:$$$mptypes.Result}),{Args:$$$mptypes.FirstArgs,Return:$$$mptypes.Result});
    },[{$nm:'secondArgs',$mt:'prm',$t:'SecondArgs'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.SecondArgs,Element:$$$mptypes.SecondArgs}},Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}}),{Args:$$$mptypes.SecondArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}});
};
shuffle.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Callable,a:{Arguments:'SecondArgs',Return:{t:Callable,a:{Arguments:'FirstArgs',Return:'Result'}}}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:'FirstArgs',Return:{t:Callable,a:{Arguments:'SecondArgs',Return:'Result'}}}}}],$tp:{Result:{},FirstArgs:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]},SecondArgs:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},$an:function(){return[shared()];},d:['ceylon.language','shuffle']};};
exports.shuffle=shuffle;
var plus=function (x$867,y$868,$$$mptypes){
    return x$867.plus(y$868);
};
plus.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'x',$mt:'prm',$t:'Value'},{$nm:'y',$mt:'prm',$t:'Value'}],$tp:{Value:{'satisfies':[{t:Summable,a:{Other:'Value'}}]}},$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','plus']};};
exports.plus=plus;
var times=function (x$869,y$870,$$$mptypes){
    return x$869.times(y$870);
};
times.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Value',$ps:[{$nm:'x',$mt:'prm',$t:'Value'},{$nm:'y',$mt:'prm',$t:'Value'}],$tp:{Value:{'satisfies':[{t:Numeric,a:{Other:'Value'}}]}},$an:function(){return[see([,].reifyCeylonType({Absent:{t:Null},Element:{t:FunctionDeclaration$model$declaration}})),shared()];},d:['ceylon.language','times']};};
exports.times=times;
function combine(combination$871,elements$872,otherElements$873,$$$mptypes){
    function iterable$874($$targs$$){
        var $$iterable$874=new iterable$874.$$;
        $$iterable$874.$$targs$$=$$targs$$;
        Iterable({Absent:$$$mptypes.Absent,Element:$$$mptypes.Result},$$iterable$874);
        return $$iterable$874;
    }
    function $init$iterable$874(){
        if (iterable$874.$$===undefined){
            initTypeProto(iterable$874,'ceylon.language::combine.iterable',Basic,$init$Iterable());
        }
        return iterable$874;
    }
    $init$iterable$874();
    (function($$iterable$874){
        $$iterable$874.iterator=function iterator(){
            var $$iterable$874=this;
            function iterator$875($$targs$$){
                var $$iterator$875=new iterator$875.$$;
                $$iterator$875.$$targs$$=$$targs$$;
                Iterator({Element:$$$mptypes.Result},$$iterator$875);
                $$iterator$875.iter$876_=elements$872.iterator();
                $$iterator$875.otherIter$877_=otherElements$873.iterator();
                return $$iterator$875;
            }
            function $init$iterator$875(){
                if (iterator$875.$$===undefined){
                    initTypeProto(iterator$875,'ceylon.language::combine.iterable.iterator.iterator',Basic,$init$Iterator());
                }
                return iterator$875;
            }
            $init$iterator$875();
            (function($$iterator$875){
                defineAttr($$iterator$875,'iter$876',function(){return this.iter$876_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Element'}},$cont:iterator$875,d:['ceylon.language','combine','$o','iterable','$m','iterator','$o','iterator','$at','iter']};});
                defineAttr($$iterator$875,'otherIter$877',function(){return this.otherIter$877_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'OtherElement'}},$cont:iterator$875,d:['ceylon.language','combine','$o','iterable','$m','iterator','$o','iterator','$at','otherIter']};});
                $$iterator$875.next=function next(){
                    var $$iterator$875=this;
                    var elem$878=$$iterator$875.iter$876.next();
                    var otherElem$879=$$iterator$875.otherIter$877.next();
                    var elem$880;
                    var otherElem$881;
                    if(!isOfType((elem$880=elem$878),{t:Finished})&&!isOfType((otherElem$881=otherElem$879),{t:Finished})){
                        return combination$871(elem$880,otherElem$881);
                    }else {
                        return getFinished();
                    }
                };$$iterator$875.next.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:['Result',{t:Finished}]},$ps:[],$cont:iterator$875,$an:function(){return[shared(),actual()];},d:['ceylon.language','combine','$o','iterable','$m','iterator','$o','iterator','$m','next']};};
            })(iterator$875.$$.prototype);
            var iterator$882=iterator$875({Element:$$$mptypes.Result});
            var getIterator$882=function(){
                return iterator$882;
            }
            return getIterator$882();
        };$$iterable$874.iterator.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterator,a:{Element:'Result'}},$ps:[],$cont:iterable$874,$an:function(){return[shared(),actual()];},d:['ceylon.language','combine','$o','iterable','$m','iterator']};};
    })(iterable$874.$$.prototype);
    var iterable$883=iterable$874({Absent:$$$mptypes.Absent,Element:$$$mptypes.Result});
    var getIterable$883=function(){
        return iterable$883;
    }
    return getIterable$883();
}
exports.combine=combine;
combine.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Iterable,a:{Absent:'Absent',Element:'Result'}},$ps:[{$nm:'combination',$mt:'prm',$t:'Result'},{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}}},{$nm:'otherElements',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'OtherElement'}}}],$tp:{Result:{},Absent:{'satisfies':[{t:Null}]},Element:{},OtherElement:{}},$an:function(){return[by([String$("Gavin",5),String$("Enrique Zamudio",15),String$("Tako",4)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];},d:['ceylon.language','combine']};};
var sort=function (elements$884,$$$mptypes){
    return internalSort($JsCallable(byIncreasing($JsCallable(function (e$885){
        return e$885;
    },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element}},Return:$$$mptypes.Element}),{Value:$$$mptypes.Element,Element:$$$mptypes.Element}),[{$nm:'p1',$mt:'prm',$t:'Element'},{$nm:'p2',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element}},First:$$$mptypes.Element,Element:$$$mptypes.Element}},Return:{t:Comparison}}),elements$884,{Element:$$$mptypes.Element});
};
sort.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[see([typeLiteral$model({Type:Comparable})].reifyCeylonType({Absent:{t:Null},Element:{t:InterfaceDeclaration$model$declaration}})),shared()];},d:['ceylon.language','sort']};};
exports.sort=sort;
function Array$($$targs$$) {
    var that = new Array$.$$;
    List({Element:$$targs$$.Element}, that);
    return that;
}
Array$.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract(),native()];},mod:$$METAMODEL$$,d:['ceylon.language','Array']};

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
Array$proto.shorterThan.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','Iterable','$m','shorterThan']};
Array$proto.longerThan = function(len) {
  return this.size > len;
}
Array$proto.longerThan.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','Iterable','$m','longerThan']};

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
exports.array.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','array']};

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
exports.arrayOfSize.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','arrayOfSize']};

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
SequenceBuilder.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','SequenceBuilder']};

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
SequenceAppender.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','SequenceAppender']};

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
String$.$$metamodel$$={$ps:[],$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:['ceylon.language','String']};

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
String$proto.plus.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','plus']};
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
String$proto.equals.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','equals']};
String$proto.compare = function(other) {
    var cmp = this.localeCompare(other);
    return cmp===0 ? equal : (cmp<0 ? smaller:larger);
}
String$proto.compare.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','compare']};
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
String$proto.span.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','span']};
String$proto.spanFrom = function(from) {
    return this.span(from, 0x7fffffff);
}
String$proto.spanFrom.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','spanFrom']};
String$proto.spanTo = function(to) {
    return to < 0 ? String$('', 0) : this.span(0, to);
}
String$proto.spanTo.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','spanTo']};
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
String$proto.segment.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','segment']};
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
String$proto.longerThan.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','longerThan']};
String$proto.shorterThan = function(length) {
    if (this.codePoints!==undefined) {return this.codePoints<length}
    if (this.length < length) {return true}
    if (this.length<<1 >= length) {return false}
    this.codePoints = countCodepoints(this);
    return this.codePoints<length;
}
String$proto.shorterThan.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','shorterThan']};
String$proto.iterator= function() {
	return this.length === 0 ? getEmptyIterator() : StringIterator(this);
}
String$proto.iterator.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','iterator']};
String$proto.get = function(index) {
    if (index<0 || index>=this.length) {return null}
    var i = 0;
    for (var count=0; count<index; count++) {
        if ((this.charCodeAt(i)&0xfc00) === 0xd800) {++i}
        if (++i >= this.length) {return null}
    }
    return Character(codepointFromString(this, i));
}
String$proto.get.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','get']};
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
String$proto.trim.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','trim']};
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
String$proto.trimLeading.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','trimLeading']};
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
String$proto.trimTrailing.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','trimTrailing']};

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
String$proto.initial.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','initial']};
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
String$proto.terminal.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','terminal']};
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
String$proto.startsWith.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','startsWith']};
String$proto.endsWith = function(str) {
    var start = this.length - str.length
    if (start < 0) {return false}
    return cmpSubString(this, str, start);
}
String$proto.endsWith.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','endsWith']};
String$proto.contains = function(sub) {
    var str;
    if (sub.constructor === String) {str = sub}
    else if (sub.constructor !== Character.$$) {return false}
    else {str = codepointToString(sub.value)}
    return this.indexOf(str) >= 0;
}
String$proto.contains.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','contains']};
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
/*String$proto.firstOccurrence = function(sub) {
    if (sub.length == 0) {return 0}
    var bound = this.length - sub.length;
    for (var i=0, count=0; i<=bound; ++count) {
        if (cmpSubString(this, sub, i)) {return count}
        if ((this.charCodeAt(i++)&0xfc00) === 0xd800) {++i}
    }
    return null;
}
String$proto.firstOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','firstOccurrence']};
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
String$proto.lastOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','lastOccurrence']};
*/
String$proto.firstOccurrence = function(subc) {
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
String$proto.firstOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','firstOccurrence']};
String$proto.lastOccurrence = function(subc) {
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
String$proto.lastOccurrence.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','lastOccurrence']};
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
String$proto.join.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','join']};
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
String$proto.$split.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','split']};
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
String$proto.$replace.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','replace']};
String$proto.repeat = function(times) {
    var sb = StringBuilder();
    for (var i = 0; i < times; i++) {
        sb.append(this);
    }
    return sb.string;
}
String$proto.repeat.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','repeat']};
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
String$proto.occurrences.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','occurrences']};
String$proto.$filter = function(f) {
    var r = Iterable.$$.prototype.$filter.apply(this, [f]);
    return String$(r);
}
String$proto.$filter.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','filter']};
String$proto.skipping = function(skip) {
    if (skip==0) return this;
    return this.segment(skip, this.size);
}
String$proto.skipping.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','skipping']};
String$proto.taking = function(take) {
    if (take==0) return getEmpty();
    return this.segment(0, take);
}
String$proto.taking.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','taking']};
String$proto.by = function(step) {
    var r = Iterable.$$.prototype.by.apply(this, [step]);
    return String$(r);
}
String$proto.by.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','by']};
String$proto.$sort = function(f) {
    var r = Iterable.$$.prototype.$sort.apply(this, [f]);
    return String$(r);
}
String$proto.$sort.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','String','$m','sort']};
defineAttr(String$proto, 'coalesced', function(){ return this; });

function StringIterator(string) {
    var that = new StringIterator.$$;
    that.str = string;
    that.index = 0;
    return that;
}
StringIterator.$$metamodel$$={$nm:'StringIterator',$mt:'cls',$ps:[{t:String$}],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','Iterator']};

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
Character.$$metamodel$$={$ps:[],$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:['ceylon.language','Character']};

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
StringBuilder.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','StringBuilder']};

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
var minRadix$886=(2);
var getMinRadix=function(){return minRadix$886;};
exports.getMinRadix=getMinRadix;
$prop$minRadix$886={$$metamodel$$:function(){return{mod:$$METAMODEL$$,$t:{t:Integer},d:['ceylon.language','minRadix']};},get:getMinRadix};
exports.$prop$minRadix$886=$prop$minRadix$886;
var maxRadix$887=(36);
var getMaxRadix=function(){return maxRadix$887;};
exports.getMaxRadix=getMaxRadix;
$prop$maxRadix$887={$$metamodel$$:function(){return{mod:$$METAMODEL$$,$t:{t:Integer},d:['ceylon.language','maxRadix']};},get:getMaxRadix};
exports.$prop$maxRadix$887=$prop$maxRadix$887;
function parseInteger(string$888,radix$889){
    if(radix$889===undefined){radix$889=(10);}
    //assert at parseInteger.ceylon (32:4-32:49)
    if (!((radix$889.compare(getMinRadix())!==getSmaller())&&(radix$889.compare(getMaxRadix())!==getLarger()))) {throw wrapexc(AssertionException("Assertion failed: \'radix >= minRadix, radix <= maxRadix\' at parseInteger.ceylon (32:11-32:48)"),'32:4-32:49','parseInteger.ceylon'); }
    var ii$890=(0);
    var setIi$890=function(ii$891){return ii$890=ii$891;};
    var max$892=getMinIntegerValue().divided(radix$889);
    var negative$893;
    var char$894;
    if((char$894=string$888.get(ii$890))!==null){
        if(char$894.equals(Character(45))){
            negative$893=true;
            (oldii$895=ii$890,ii$890=oldii$895.successor,oldii$895);
            var oldii$895;
        }else {
            if(char$894.equals(Character(43))){
                negative$893=false;
                (oldii$896=ii$890,ii$890=oldii$896.successor,oldii$896);
                var oldii$896;
            }else {
                negative$893=false;
            }
        }
    }else {
        return null;
    }
    var limit$897=(opt$898=(negative$893?getMinIntegerValue():null),opt$898!==null?opt$898:(-getMaxIntegerValue()));
    var opt$898;
    var length$899=string$888.size;
    var result$900=(0);
    var setResult$900=function(result$901){return result$900=result$901;};
    var sep$902=(-(1));
    var setSep$902=function(sep$903){return sep$902=sep$903;};
    var digitIndex$904=(0);
    var setDigitIndex$904=function(digitIndex$905){return digitIndex$904=digitIndex$905;};
    var groupingSize$906=(-(1));
    var setGroupingSize$906=function(groupingSize$907){return groupingSize$906=groupingSize$907;};
    while(ii$890.compare(length$899).equals(getSmaller())){
        var ch$908;
        var char$909;
        if((char$909=string$888.get(ii$890))!==null){
            ch$908=char$909;
        }else {
            return null;
        }
        if(ch$908.equals(Character(95))){
            if(sep$902.equals((-(1)))){
                var digitGroupSize$910;
                if((digitGroupSize$910=computeDigitGroupingSize(radix$889,digitIndex$904,string$888,ii$890))!==null&&(digitIndex$904.compare(digitGroupSize$910)!==getLarger())){
                    groupingSize$906=digitGroupSize$910;
                    sep$902=digitIndex$904;
                }else {
                    return null;
                }
            }else {
                if(digitIndex$904.minus(sep$902).equals(groupingSize$906)){
                    return null;
                }else {
                    sep$902=digitIndex$904;
                }
            }
        }else {
            if(((!sep$902.equals((-(1))))&&digitIndex$904.minus(sep$902).equals(groupingSize$906.plus((1))))){
                return null;
            }
            if(((ii$890.plus((1)).equals(length$899)&&radix$889.equals((10)))&&Tuple(Character(107),Tuple(Character(77),Tuple(Character(71),Tuple(Character(84),Tuple(Character(80),getEmpty(),{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}),{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}},First:{t:Character},Element:{t:Character}}).contains(ch$908))){
                var magnitude$911;
                if((magnitude$911=computeMagnitude(radix$889,string$888.get((oldii$912=ii$890,ii$890=oldii$912.successor,oldii$912))))!==null){
                    if(limit$897.divided(magnitude$911).compare(result$900).equals(getSmaller())){
                        (result$900=result$900.times(magnitude$911));
                        break;
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                var oldii$912;
            }else {
                var digit$913;
                if((digit$913=parseDigit(ch$908,radix$889))!==null){
                    if(result$900.compare(max$892).equals(getSmaller())){
                        return null;
                    }
                    (result$900=result$900.times(radix$889));
                    if(result$900.compare(limit$897.plus(digit$913)).equals(getSmaller())){
                        return null;
                    }
                    (result$900=result$900.minus(digit$913));
                }else {
                    return null;
                }
            }
        }
        (oldii$914=ii$890,ii$890=oldii$914.successor,oldii$914);
        var oldii$914;
        (olddigitIndex$915=digitIndex$904,digitIndex$904=olddigitIndex$915.successor,olddigitIndex$915);
        var olddigitIndex$915;
    }
    if(((!sep$902.equals((-(1))))&&(!digitIndex$904.minus(sep$902).equals(groupingSize$906.plus((1)))))){
        return null;
    }
    if(digitIndex$904.equals((0))){
        return null;
    }
    return (opt$916=(negative$893?result$900:null),opt$916!==null?opt$916:(-result$900));
    var opt$916;
}
exports.parseInteger=parseInteger;
parseInteger.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'string',$mt:'prm',$t:{t:String$}},{$nm:'radix',$mt:'prm',$def:1,$t:{t:Integer}}],$an:function(){return[$throws(typeLiteral$model({Type:AssertionException}),String$("if `radix` is not between `minRadix` and `maxRadix`",51)),shared()];},d:['ceylon.language','parseInteger']};};
function computeDigitGroupingSize(radix$917,digitIndex$918,string$919,ii$920){
    var groupingSize$921;
    if(radix$917.equals((2))){
        groupingSize$921=(4);
    }else {
        if(radix$917.equals((10))){
            groupingSize$921=(3);
        }else {
            if(radix$917.equals((16))){
                var char$922;
                if((digitIndex$918.compare((2))!==getLarger())&&(char$922=string$919.get(ii$920.plus((3))))!==null&&char$922.equals(Character(95))){
                    groupingSize$921=(2);
                }else {
                    groupingSize$921=(4);
                }
            }else {
                groupingSize$921=null;
            }
        }
    }
    return groupingSize$921;
};computeDigitGroupingSize.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'radix',$mt:'prm',$t:{t:Integer}},{$nm:'digitIndex',$mt:'prm',$t:{t:Integer}},{$nm:'string',$mt:'prm',$t:{t:String$}},{$nm:'ii',$mt:'prm',$t:{t:Integer}}],d:['ceylon.language','computeDigitGroupingSize']};};
function computeMagnitude(radix$923,char$924){
    var power$925;
    var char$926;
    if((char$926=char$924)!==null){
        if(char$926.equals(Character(80))){
            power$925=(15);
        }else {
            if(char$926.equals(Character(84))){
                power$925=(12);
            }else {
                if(char$926.equals(Character(71))){
                    power$925=(9);
                }else {
                    if(char$926.equals(Character(77))){
                        power$925=(6);
                    }else {
                        if(char$926.equals(Character(107))){
                            power$925=(3);
                        }else {
                            power$925=null;
                        }
                    }
                }
            }
        }
    }else {
        power$925=null;
    }
    var power$927;
    if((power$927=power$925)!==null){
        return radix$923.power(power$927);
    }
    return null;
};computeMagnitude.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'radix',$mt:'prm',$t:{t:Integer}},{$nm:'char',$mt:'prm',$t:{ t:'u', l:[{t:Null},{t:Character}]}}],d:['ceylon.language','computeMagnitude']};};
var aInt$928=Character(97).integer;
var getAInt=function(){return aInt$928;};
exports.getAInt=getAInt;
$prop$aInt$928={$$metamodel$$:function(){return{mod:$$METAMODEL$$,$t:{t:Integer},d:['ceylon.language','aInt']};},get:getAInt};
exports.$prop$aInt$928=$prop$aInt$928;
var zeroInt$929=Character(48).integer;
var getZeroInt=function(){return zeroInt$929;};
exports.getZeroInt=getZeroInt;
$prop$zeroInt$929={$$metamodel$$:function(){return{mod:$$METAMODEL$$,$t:{t:Integer},d:['ceylon.language','zeroInt']};},get:getZeroInt};
exports.$prop$zeroInt$929=$prop$zeroInt$929;
function parseDigit(digit$930,radix$931){
    var figure$932;
    var digitInt$933=digit$930.integer;
    if((tmpvar$934=digitInt$933.minus(getZeroInt()),tmpvar$934.compare((0))!==getSmaller()&&tmpvar$934.compare((10))===getSmaller())){
        figure$932=digitInt$933.minus(getZeroInt());
    }else {
        if((tmpvar$935=digitInt$933.minus(getAInt()),tmpvar$935.compare((0))!==getSmaller()&&tmpvar$935.compare((26))===getSmaller())){
            figure$932=digitInt$933.minus(getAInt()).plus((10));
        }else {
            return null;
        }
    }
    return (figure$932.compare(radix$931).equals(getSmaller())?figure$932:null);
};parseDigit.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Integer}]},$ps:[{$nm:'digit',$mt:'prm',$t:{t:Character}},{$nm:'radix',$mt:'prm',$t:{t:Integer}}],d:['ceylon.language','parseDigit']};};
function formatInteger(integer$936,radix$937){
    if(radix$937===undefined){radix$937=(10);}
    //assert at parseInteger.ceylon (195:4-195:49)
    if (!((radix$937.compare(getMinRadix())!==getSmaller())&&(radix$937.compare(getMaxRadix())!==getLarger()))) {throw wrapexc(AssertionException("Assertion failed: \'radix >= minRadix, radix <= maxRadix\' at parseInteger.ceylon (195:11-195:48)"),'195:4-195:49','parseInteger.ceylon'); }
    if(integer$936.equals((0))){
        return String$("0",1);
    }
    var digits$938=StringBuilder();
    var insertIndex$939;
    var i$940;
    var setI$940=function(i$941){return i$940=i$941;};
    if(integer$936.compare((0)).equals(getSmaller())){
        digits$938.append(String$("-",1));
        insertIndex$939=(1);
        i$940=integer$936;
    }else {
        insertIndex$939=(0);
        i$940=(-integer$936);
    }
    while((!i$940.equals((0)))){
        var d$942=(-i$940.remainder(radix$937));
        var c$943;
        if((tmpvar$944=d$942,tmpvar$944.compare((0))!==getSmaller()&&tmpvar$944.compare((10))===getSmaller())){
            c$943=d$942.plus(getZeroInt()).character;
        }else {
            if((tmpvar$945=d$942,tmpvar$945.compare((10))!==getSmaller()&&tmpvar$945.compare((36))===getSmaller())){
                c$943=d$942.minus((10)).plus(getAInt()).character;
            }else {
                //assert at parseInteger.ceylon (220:12-220:26)
                if (!(false)) {throw wrapexc(AssertionException("Assertion failed: \'false\' at parseInteger.ceylon (220:19-220:25)"),'220:12-220:26','parseInteger.ceylon'); }
            }
        }
        digits$938.insertCharacter(insertIndex$939,c$943);
        i$940=i$940.plus(d$942).divided(radix$937);
    }
    return digits$938.string;
}
exports.formatInteger=formatInteger;
formatInteger.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:String$},$ps:[{$nm:'integer',$mt:'prm',$t:{t:Integer}},{$nm:'radix',$mt:'prm',$def:1,$t:{t:Integer}}],$an:function(){return[$throws(typeLiteral$model({Type:AssertionException}),String$("if `radix` is not between `minRadix` and `maxRadix`",51)),shared()];},d:['ceylon.language','formatInteger']};};
function Annotated$model($$annotated){
}
Annotated$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model','Annotated']};};
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
Annotation$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]}},satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model','Annotation']};};
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
    ValueModel$model($$attribute.$$targs$$===undefined?$$targs$$:{Type:$$attribute.$$targs$$.Type},$$attribute);
    Member$model($$attribute.$$targs$$===undefined?$$targs$$:{Type:$$attribute.$$targs$$.Container,Kind:{t:Value$model,a:{Type:$$attribute.$$targs$$.Type}}},$$attribute);
    add_type_arg($$attribute,'Kind',{t:Value$model,a:{Type:$$attribute.$$targs$$.Type}});
    set_type_args($$attribute,$$targs$$);
}
Attribute$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Container:{'var':'in'},Type:{'var':'out','def':{t:Anything}}},satisfies:[{t:ValueModel$model,a:{Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Value$model,a:{Type:'Type'}}}}],$an:function(){return[shared()];},d:['ceylon.language.model','Attribute']};};
exports.Attribute$model=Attribute$model;
function $init$Attribute$model(){
    if (Attribute$model.$$===undefined){
        initTypeProto(Attribute$model,'ceylon.language.model::Attribute',$init$ValueModel$model(),$init$Member$model());
    }
    return Attribute$model;
}
exports.$init$Attribute$model=$init$Attribute$model;
$init$Attribute$model();
function ValueModel$model($$targs$$,$$valueModel){
    Model$model($$valueModel);
    set_type_args($$valueModel,$$targs$$);
}
ValueModel$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}}},satisfies:[{t:Model$model}],$an:function(){return[shared()];},d:['ceylon.language.model','ValueModel']};};
exports.ValueModel$model=ValueModel$model;
function $init$ValueModel$model(){
    if (ValueModel$model.$$===undefined){
        initTypeProto(ValueModel$model,'ceylon.language.model::ValueModel',$init$Model$model());
        (function($$valueModel){
        })(ValueModel$model.$$.prototype);
    }
    return ValueModel$model;
}
exports.$init$ValueModel$model=$init$ValueModel$model;
$init$ValueModel$model();
function Class$model($$targs$$,$$class){
    ClassModel$model($$class.$$targs$$===undefined?$$targs$$:{Arguments:$$class.$$targs$$.Arguments,Type:$$class.$$targs$$.Type},$$class);
    Callable($$class.$$targs$$===undefined?$$targs$$:{Arguments:$$class.$$targs$$.Arguments,Return:$$class.$$targs$$.Type},$$class);
    set_type_args($$class,$$targs$$);
}
Class$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}],'def':{t:Nothing}}},satisfies:[{t:ClassModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Callable,a:{Arguments:'Arguments',Return:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','Class']};};
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
ClassModel$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}],'def':{t:Nothing}}},satisfies:[{t:ClassOrInterface$model,a:{Type:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','ClassModel']};};
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
    Type$model($$classOrInterface.$$targs$$===undefined?$$targs$$:{Type:$$classOrInterface.$$targs$$.Type},$$classOrInterface);
    set_type_args($$classOrInterface,$$targs$$);
}
ClassOrInterface$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}}},satisfies:[{t:Model$model},{t:Type$model,a:{Type:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','ClassOrInterface']};};
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
ConstrainedAnnotation$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]},Values:{'var':'out'},ProgramElement:{'var':'in','satisfies':[{t:Annotated$model}]}},satisfies:[{t:Annotation$model,a:{Value:'Value'}}],$an:function(){return[shared()];},d:['ceylon.language.model','ConstrainedAnnotation']};};
exports.ConstrainedAnnotation$model=ConstrainedAnnotation$model;
function $init$ConstrainedAnnotation$model(){
    if (ConstrainedAnnotation$model.$$===undefined){
        initTypeProto(ConstrainedAnnotation$model,'ceylon.language.model::ConstrainedAnnotation',$init$Annotation$model());
        (function($$constrainedAnnotation){
            $$constrainedAnnotation.occurs=function (programElement$946){
                var $$constrainedAnnotation=this;
                return isOfType(programElement$946,$$constrainedAnnotation.$$targs$$.ProgramElement);
            };
            $$constrainedAnnotation.occurs.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Boolean},$ps:[{$nm:'programElement',$mt:'prm',$t:{t:Annotated$model}}],$cont:ConstrainedAnnotation$model,$an:function(){return[shared()];},d:['ceylon.language.model','ConstrainedAnnotation','$m','occurs']};};
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
Function$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}],'def':{t:Nothing}}},satisfies:[{t:FunctionModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Callable,a:{Arguments:'Arguments',Return:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','Function']};};
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
FunctionModel$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}],'def':{t:Nothing}}},satisfies:[{t:Model$model}],$an:function(){return[shared()];},d:['ceylon.language.model','FunctionModel']};};
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
Interface$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}}},satisfies:[{t:InterfaceModel$model,a:{Type:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','Interface']};};
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
InterfaceModel$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}}},satisfies:[{t:ClassOrInterface$model,a:{Type:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','InterfaceModel']};};
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
function IntersectionType$model($$targs$$,$$intersectionType){
    Type$model($$intersectionType.$$targs$$===undefined?$$targs$$:{Type:$$intersectionType.$$targs$$.Intersection},$$intersectionType);
    set_type_args($$intersectionType,$$targs$$);
}
IntersectionType$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Intersection:{'var':'out','def':{t:Anything}}},satisfies:[{t:Type$model,a:{Type:'Intersection'}}],$an:function(){return[shared()];},d:['ceylon.language.model','IntersectionType']};};
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
Member$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'in'},Kind:{'var':'out','satisfies':[{t:Model$model}]}},satisfies:[{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:'Type',Element:'Type'}},Return:'Kind'}}],$an:function(){return[shared()];},d:['ceylon.language.model','Member']};};
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
MemberClass$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Container:{'var':'in'},Type:{'var':'out','def':{t:Anything}},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}],'def':{t:Nothing}}},satisfies:[{t:ClassModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Class$model,a:{Arguments:'Arguments',Type:'Type'}}}}],$an:function(){return[shared()];},d:['ceylon.language.model','MemberClass']};};
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
MemberInterface$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Container:{'var':'in'},Type:{'var':'out','def':{t:Anything}}},satisfies:[{t:InterfaceModel$model,a:{Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Interface$model,a:{Type:'Type'}}}}],$an:function(){return[shared()];},d:['ceylon.language.model','MemberInterface']};};
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
Method$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Container:{'var':'in'},Type:{'var':'out','def':{t:Anything}},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}],'def':{t:Nothing}}},satisfies:[{t:FunctionModel$model,a:{Arguments:'Arguments',Type:'Type'}},{t:Member$model,a:{Type:'Container',Kind:{t:Function$model,a:{Arguments:'Arguments',Type:'Type'}}}}],$an:function(){return[shared()];},d:['ceylon.language.model','Method']};};
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
Model$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model','Model']};};
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
OptionalAnnotation$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]},ProgramElement:{'var':'in','satisfies':[{t:Annotated$model}],'def':{t:Annotated$model}}},satisfies:[{t:ConstrainedAnnotation$model,a:{Values:{ t:'u', l:[{t:Null},'Value']},Value:'Value',ProgramElement:'ProgramElement'}}],$an:function(){return[shared()];},d:['ceylon.language.model','OptionalAnnotation']};};
exports.OptionalAnnotation$model=OptionalAnnotation$model;
function $init$OptionalAnnotation$model(){
    if (OptionalAnnotation$model.$$===undefined){
        initTypeProto(OptionalAnnotation$model,'ceylon.language.model::OptionalAnnotation',$init$ConstrainedAnnotation$model());
    }
    return OptionalAnnotation$model;
}
exports.$init$OptionalAnnotation$model=$init$OptionalAnnotation$model;
$init$OptionalAnnotation$model();
function optionalAnnotation$model(annotationType$947,programElement$948,$$$mptypes){
    return annotations$model(annotationType$947,programElement$948,{Values:{ t:'u', l:[{t:Null},$$$mptypes.Value]},Value:$$$mptypes.Value,ProgramElement:$$$mptypes.ProgramElement});
}
exports.optionalAnnotation$model=optionalAnnotation$model;
optionalAnnotation$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Value']},$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:Class$model,a:{Arguments:{t:Nothing},Type:{t:OptionalAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:OptionalAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}]},ProgramElement:{'satisfies':[{t:Annotated$model}]}},$an:function(){return[shared()];},d:['ceylon.language.model','optionalAnnotation']};};
function SequencedAnnotation$model($$targs$$,$$sequencedAnnotation){
    ConstrainedAnnotation$model($$sequencedAnnotation.$$targs$$===undefined?$$targs$$:{Values:{t:Sequential,a:{Element:$$sequencedAnnotation.$$targs$$.Value}},Value:$$sequencedAnnotation.$$targs$$.Value,ProgramElement:$$sequencedAnnotation.$$targs$$.ProgramElement},$$sequencedAnnotation);
    add_type_arg($$sequencedAnnotation,'Values',{t:Sequential,a:{Element:$$sequencedAnnotation.$$targs$$.Value}});
    set_type_args($$sequencedAnnotation,$$targs$$);
}
SequencedAnnotation$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Value:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Value'}}]},ProgramElement:{'var':'in','satisfies':[{t:Annotated$model}],'def':{t:Annotated$model}}},satisfies:[{t:ConstrainedAnnotation$model,a:{Values:{t:Sequential,a:{Element:'Value'}},Value:'Value',ProgramElement:'ProgramElement'}}],$an:function(){return[shared()];},d:['ceylon.language.model','SequencedAnnotation']};};
exports.SequencedAnnotation$model=SequencedAnnotation$model;
function $init$SequencedAnnotation$model(){
    if (SequencedAnnotation$model.$$===undefined){
        initTypeProto(SequencedAnnotation$model,'ceylon.language.model::SequencedAnnotation',$init$ConstrainedAnnotation$model());
    }
    return SequencedAnnotation$model;
}
exports.$init$SequencedAnnotation$model=$init$SequencedAnnotation$model;
$init$SequencedAnnotation$model();
function sequencedAnnotations$model(annotationType$949,programElement$950,$$$mptypes){
    return annotations$model(annotationType$949,programElement$950,{Values:{t:Sequential,a:{Element:$$$mptypes.Value}},Value:$$$mptypes.Value,ProgramElement:$$$mptypes.ProgramElement});
}
exports.sequencedAnnotations$model=sequencedAnnotations$model;
sequencedAnnotations$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Value'}},$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:Class$model,a:{Arguments:{t:Nothing},Type:{t:SequencedAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:SequencedAnnotation$model,a:{Value:'Value',ProgramElement:'ProgramElement'}}]},ProgramElement:{'satisfies':[{t:Annotated$model}]}},$an:function(){return[shared()];},d:['ceylon.language.model','sequencedAnnotations']};};
function Type$model($$targs$$,$$type){
    set_type_args($$type,$$targs$$);
}
Type$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}}},satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model','Type']};};
exports.Type$model=Type$model;
function $init$Type$model(){
    if (Type$model.$$===undefined){
        initTypeProto(Type$model,'ceylon.language.model::Type');
    }
    return Type$model;
}
exports.$init$Type$model=$init$Type$model;
$init$Type$model();
function UnionType$model($$targs$$,$$unionType){
    Type$model($$unionType.$$targs$$===undefined?$$targs$$:{Type:$$unionType.$$targs$$.Union},$$unionType);
    set_type_args($$unionType,$$targs$$);
}
UnionType$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Union:{'var':'out','def':{t:Anything}}},satisfies:[{t:Type$model,a:{Type:'Union'}}],$an:function(){return[shared()];},d:['ceylon.language.model','UnionType']};};
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
    ValueModel$model($$value.$$targs$$===undefined?$$targs$$:{Type:$$value.$$targs$$.Type},$$value);
    set_type_args($$value,$$targs$$);
}
Value$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{'var':'out','def':{t:Anything}}},satisfies:[{t:ValueModel$model,a:{Type:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','Value']};};
exports.Value$model=Value$model;
function $init$Value$model(){
    if (Value$model.$$===undefined){
        initTypeProto(Value$model,'ceylon.language.model::Value',$init$ValueModel$model());
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
Variable$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Type:{}},satisfies:[{t:Value$model,a:{Type:'Type'}}],$an:function(){return[shared()];},d:['ceylon.language.model','Variable']};};
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
VariableAttribute$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{Container:{'var':'in'},Type:{}},satisfies:[{t:Member$model,a:{Type:'Container',Kind:{t:Variable$model,a:{Type:'Type'}}}},{t:Attribute$model,a:{Type:'Type',Container:'Container'}}],$an:function(){return[shared()];},d:['ceylon.language.model','VariableAttribute']};};
exports.VariableAttribute$model=VariableAttribute$model;
function $init$VariableAttribute$model(){
    if (VariableAttribute$model.$$===undefined){
        initTypeProto(VariableAttribute$model,'ceylon.language.model::VariableAttribute',$init$Member$model(),$init$Attribute$model());
    }
    return VariableAttribute$model;
}
exports.$init$VariableAttribute$model=$init$VariableAttribute$model;
$init$VariableAttribute$model();
function nothingType$951$model($$targs$$){
    var $$nothingType=new nothingType$951$model.$$;
    $$nothingType.$$targs$$=$$targs$$;
    Type$model({Type:{t:Nothing}},$$nothingType);
    return $$nothingType;
}
function $init$nothingType$951$model(){
    if (nothingType$951$model.$$===undefined){
        initTypeProto(nothingType$951$model,'ceylon.language.model::nothingType',Basic,$init$Type$model());
    }
    return nothingType$951$model;
}
exports.$init$nothingType$951$model=$init$nothingType$951$model;
$init$nothingType$951$model();
(function($$nothingType){
    defineAttr($$nothingType,'string',function(){
        var $$nothingType=this;
        return String$("Nothing",7);
    },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String},$cont:nothingType$951$model,$an:function(){return[shared(),actual()];},d:['ceylon.language.model','nothingType','$at','string']};});
})(nothingType$951$model.$$.prototype);
var nothingType$952$model=nothingType$951$model({Type:{t:Nothing}});
var getNothingType$model=function(){
    return nothingType$952$model;
}
exports.getNothingType$model=getNothingType$model;
exports.getNothingType$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:nothingType$951$model},$an:function(){return[shared()];},d:['ceylon.language.model','nothingType']};};
function Declaration$model$declaration($$declaration){
}
Declaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','Declaration']};};
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
function AnnotatedDeclaration$model$declaration($$annotatedDeclaration){
    Declaration$model$declaration($$annotatedDeclaration);
    Annotated$model($$annotatedDeclaration);
}
AnnotatedDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:Declaration$model$declaration},{t:Annotated$model}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','AnnotatedDeclaration']};};
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
//Add-on to AnnotatedDeclaration            //MethodDeclaration annotations at caca.ceylon (82:2-83:72)
AnnotatedDeclaration$model$declaration.$$.prototype.annotations=function ($$$mptypes) {
    var $$openInterface=this;
    var ans = [];
    var _mdl = $$openInterface.tipo.$$metamodel$$;
    if (typeof(_mdl)==='function') {
      _mdl = _mdl();
      $$openInterface.tipo.$$metamodel$$=_mdl;
    }
    var _ans = _mdl.$an;
    if (typeof(_ans)==='function') {
      _ans = _ans();
      _mdl.$an=_ans;
    }
    for (var i=0; i<_ans.length;i++) {
      if (isOfType(_ans[i], $$$mptypes.Annotation)) {
        ans.push(_ans[i]);
      }
    }
    return ans.length == 0 ? getEmpty() : ans.reifyCeylonType({Element:$$$mptypes.Annotation});
};
AnnotatedDeclaration$model$declaration.$$.prototype.annotations.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Annotation'}},$ps:[],$cont:AnnotatedDeclaration$model$declaration,$tp:{Annotation:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Annotation'}}]}},$an:function(){return[shared(),formal()];},d:['ceylon.language.model.declaration','AnnotatedDeclaration','$m','annotations']};};
function TopLevelOrMemberDeclaration$model$declaration($$topLevelOrMemberDeclaration){
    AnnotatedDeclaration$model$declaration($$topLevelOrMemberDeclaration);
    TypedDeclaration$model$declaration($$topLevelOrMemberDeclaration);
}
TopLevelOrMemberDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:AnnotatedDeclaration$model$declaration},{t:TypedDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','TopLevelOrMemberDeclaration']};};
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
function GenericDeclaration$model$declaration($$genericDeclaration){
}
GenericDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','GenericDeclaration']};};
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
function ClassOrInterfaceDeclaration$model$declaration($$classOrInterfaceDeclaration){
    TopLevelOrMemberDeclaration$model$declaration($$classOrInterfaceDeclaration);
    GenericDeclaration$model$declaration($$classOrInterfaceDeclaration);
}
ClassOrInterfaceDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:TopLevelOrMemberDeclaration$model$declaration},{t:GenericDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','ClassOrInterfaceDeclaration']};};
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
//Addendum to model.declaration.ClassOrInterfaceDeclaration
ClassOrInterfaceDeclaration$model$declaration.$$.prototype.getMemberDeclaration=function (name$20,$$$mptypes){
  var $$oi=this;
  if (extendsType($$$mptypes.Kind, {t:ValueDeclaration$model$declaration})) {
    var _d = $$oi.meta.$at ? $$oi.meta.$at[name$20] : undefined;
    return _d ? OpenValue(name$20, $$oi.packageContainer, false, _d) : null;
  } else if (extendsType($$$mptypes.Kind, {t:FunctionDeclaration$model$declaration})) {
    var _d = $$oi.meta.$m ? $$oi.meta.$m[name$20] : undefined;
    return _d ? OpenFunction(name$20, $$oi.packageContainer, false, _d) : null;
  } else if (extendsType($$$mptypes.Kind, {t:ClassDeclaration$model$declaration})) {
    var _d = $$oi.meta.$c ? $$oi.meta.$c[name$20] : undefined;
    return _d ? OpenClass(name$20, $$oi.packageContainer, false, _d) : null;
  } else if (extendsType($$$mptypes.Kind, {t:InterfaceDeclaration$model$declaration})) {
    var _d = $$oi.meta.$i ? $$oi.meta.$i[name$20] : undefined;
    return _d ? OpenInterface(name$20, $$oi.packageContainer, false, _d) : null;
  }
  return null;
};
ClassOrInterfaceDeclaration$model$declaration.$$.prototype.getMemberDeclaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Kind']},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:OpenClass,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassOrInterfaceDeclaration','$m','getMemberDeclaration']};};

function FunctionOrValueDeclaration$model$declaration($$functionOrValueDeclaration){
    TopLevelOrMemberDeclaration$model$declaration($$functionOrValueDeclaration);
}
FunctionOrValueDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:TopLevelOrMemberDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','FunctionOrValueDeclaration']};};
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
function ValueDeclaration$model$declaration($$valueDeclaration){
    FunctionOrValueDeclaration$model$declaration($$valueDeclaration);
    $$valueDeclaration.apply$defs$instance=function(instance$953){return null;};
}
ValueDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:FunctionOrValueDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','ValueDeclaration']};};
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
ClassDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:ClassOrInterfaceDeclaration$model$declaration},{t:FunctionalDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','ClassDeclaration']};};
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
function FunctionDeclaration$model$declaration($$functionDeclaration){
    FunctionOrValueDeclaration$model$declaration($$functionDeclaration);
    GenericDeclaration$model$declaration($$functionDeclaration);
    FunctionalDeclaration$model$declaration($$functionDeclaration);
}
FunctionDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:FunctionOrValueDeclaration$model$declaration},{t:GenericDeclaration$model$declaration},{t:FunctionalDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','FunctionDeclaration']};};
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
function FunctionalDeclaration$model$declaration($$functionalDeclaration){
}
FunctionalDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','FunctionalDeclaration']};};
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
function InterfaceDeclaration$model$declaration($$interfaceDeclaration){
    ClassOrInterfaceDeclaration$model$declaration($$interfaceDeclaration);
}
InterfaceDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:ClassOrInterfaceDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','InterfaceDeclaration']};};
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
Module$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:Identifiable},{t:AnnotatedDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','Module']};};
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
Import$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:Identifiable},{t:Annotated$model}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','Import']};};
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
Package$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:Identifiable},{t:AnnotatedDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','Package']};};
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
OpenIntersection$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','OpenIntersection']};};
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
OpenParameterisedType$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$tp:{DeclarationType:{'var':'out','satisfies':[{t:ClassOrInterfaceDeclaration$model$declaration}]}},satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','OpenParameterisedType']};};
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
OpenType$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','OpenType']};};
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
OpenTypeVariable$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','OpenTypeVariable']};};
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
OpenUnion$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:OpenType$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','OpenUnion']};};
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
ParameterDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','ParameterDeclaration']};};
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
SetterDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:Annotated$model}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','SetterDeclaration']};};
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
function TypeParameter$model$declaration($$typeParameter){
    Declaration$model$declaration($$typeParameter);
}
TypeParameter$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:Declaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','TypeParameter']};};
exports.TypeParameter$model$declaration=TypeParameter$model$declaration;
function $init$TypeParameter$model$declaration(){
    if (TypeParameter$model$declaration.$$===undefined){
        initTypeProto(TypeParameter$model$declaration,'ceylon.language.model.declaration::TypeParameter',$init$Declaration$model$declaration());
        (function($$typeParameter){
        })(TypeParameter$model$declaration.$$.prototype);
    }
    return TypeParameter$model$declaration;
}
exports.$init$TypeParameter$model$declaration=$init$TypeParameter$model$declaration;
$init$TypeParameter$model$declaration();
function TypedDeclaration$model$declaration($$typedDeclaration){
}
TypedDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','TypedDeclaration']};};
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
VariableDeclaration$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,satisfies:[{t:ValueDeclaration$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','VariableDeclaration']};};
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
function nothingType$954$model$declaration(){
    var $$nothingType=new nothingType$954$model$declaration.$$;
    OpenType$model$declaration($$nothingType);
    return $$nothingType;
}
function $init$nothingType$954$model$declaration(){
    if (nothingType$954$model$declaration.$$===undefined){
        initTypeProto(nothingType$954$model$declaration,'ceylon.language.model.declaration::nothingType',Basic,$init$OpenType$model$declaration());
    }
    return nothingType$954$model$declaration;
}
exports.$init$nothingType$954$model$declaration=$init$nothingType$954$model$declaration;
$init$nothingType$954$model$declaration();
(function($$nothingType){
    defineAttr($$nothingType,'string',function(){
        var $$nothingType=this;
        return String$("Nothing",7);
    },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String},$cont:nothingType$954$model$declaration,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','nothingType','$at','string']};});
})(nothingType$954$model$declaration.$$.prototype);
var nothingType$955$model$declaration=nothingType$954$model$declaration();
var getNothingType$model$declaration=function(){
    return nothingType$955$model$declaration;
}
exports.getNothingType$model$declaration=getNothingType$model$declaration;
exports.getNothingType$model$declaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:nothingType$954$model$declaration},$an:function(){return[shared()];},d:['ceylon.language.model.declaration','nothingType']};};
function Annotation($$annotation){
    $init$Annotation();
    if ($$annotation===undefined)$$annotation=new Annotation.$$;
    $$annotation.$$targs$$={Value:{t:Annotation},ProgramElement:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Annotation},ProgramElement:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}},$$annotation);
    add_type_arg($$annotation,'Value',{t:Annotation});
    return $$annotation;
}
Annotation.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Annotation},ProgramElement:{ t:'u', l:[{t:ClassDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Annotation']};};
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
annotation.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Annotation},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','annotation']};};
exports.annotation=annotation;
function Shared($$shared){
    $init$Shared();
    if ($$shared===undefined)$$shared=new Shared.$$;
    $$shared.$$targs$$={Value:{t:Shared},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration},{t:Package$model$declaration},{t:Import$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Shared},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration},{t:Package$model$declaration},{t:Import$model$declaration}]}},$$shared);
    add_type_arg($$shared,'Value',{t:Shared});
    return $$shared;
}
Shared.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Shared},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration},{t:Package$model$declaration},{t:Import$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Shared']};};
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
shared.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Shared},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','shared']};};
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
Variable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Variable},ProgramElement:{t:ValueDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Variable']};};
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
variable.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Variable},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','variable']};};
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
Abstract.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Abstract},ProgramElement:{t:ClassDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Abstract']};};
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
abstract.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Abstract},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','abstract']};};
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
Final.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Final},ProgramElement:{t:ClassDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Final']};};
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
$final.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Final},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','final']};};
exports.$final=$final;
function Actual($$actual){
    $init$Actual();
    if ($$actual===undefined)$$actual=new Actual.$$;
    $$actual.$$targs$$={Value:{t:Actual},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Actual},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}},$$actual);
    add_type_arg($$actual,'Value',{t:Actual});
    return $$actual;
}
Actual.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Actual},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Actual']};};
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
actual.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Actual},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','actual']};};
exports.actual=actual;
function Formal($$formal){
    $init$Formal();
    if ($$formal===undefined)$$formal=new Formal.$$;
    $$formal.$$targs$$={Value:{t:Formal},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Formal},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}},$$formal);
    add_type_arg($$formal,'Value',{t:Formal});
    return $$formal;
}
Formal.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Formal},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Formal']};};
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
formal.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Formal},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','formal']};};
exports.formal=formal;
function Default($$default){
    $init$Default();
    if ($$default===undefined)$$default=new Default.$$;
    $$default.$$targs$$={Value:{t:Default},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}};
    OptionalAnnotation$model({Value:{t:Default},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}},$$default);
    add_type_arg($$default,'Value',{t:Default});
    return $$default;
}
Default.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Default},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassOrInterfaceDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Default']};};
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
$default.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Default},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','default']};};
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
Late.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Late},ProgramElement:{t:ValueDeclaration$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Late']};};
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
late.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Late},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','late']};};
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
Native.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Native},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Native']};};
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
$native.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Native},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','native']};};
exports.$native=$native;
function Doc(description, $$doc){
    $init$Doc();
    if ($$doc===undefined)$$doc=new Doc.$$;
    $$doc.$$targs$$={Value:{t:Doc},ProgramElement:{t:Annotated$model}};
    $$doc.description_=description;
    OptionalAnnotation$model({Value:{t:Doc},ProgramElement:{t:Annotated$model}},$$doc);
    add_type_arg($$doc,'Value',{t:Doc});
    add_type_arg($$doc,'ProgramElement',{t:Annotated$model});
    return $$doc;
}
Doc.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Doc},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Doc']};};
exports.Doc=Doc;
function $init$Doc(){
    if (Doc.$$===undefined){
        initTypeProto(Doc,'ceylon.language::Doc',Basic,$init$OptionalAnnotation$model());
        (function($$doc){
            defineAttr($$doc,'description',function(){return this.description_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Doc,$an:function(){return[shared()];},d:['ceylon.language','Doc','$at','description']};});
        })(Doc.$$.prototype);
    }
    return Doc;
}
exports.$init$Doc=$init$Doc;
$init$Doc();
var doc=function (description$956){
    return Doc(description$956);
};
doc.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Doc},$ps:[{$nm:'description',$mt:'prm',$t:{t:String$}}],$an:function(){return[shared(),annotation()];},d:['ceylon.language','doc']};};
exports.doc=doc;
function See(programElements, $$see){
    $init$See();
    if ($$see===undefined)$$see=new See.$$;
    $$see.$$targs$$={Value:{t:See},ProgramElement:{t:Annotated$model}};
    if(programElements===undefined){programElements=getEmpty();}
    $$see.programElements_=programElements;
    SequencedAnnotation$model({Value:{t:See},ProgramElement:{t:Annotated$model}},$$see);
    add_type_arg($$see,'Value',{t:See});
    add_type_arg($$see,'ProgramElement',{t:Annotated$model});
    return $$see;
}
See.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:SequencedAnnotation$model,a:{Value:{t:See},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','See']};};
exports.See=See;
function $init$See(){
    if (See.$$===undefined){
        initTypeProto(See,'ceylon.language::See',Basic,$init$SequencedAnnotation$model());
        (function($$see){
            defineAttr($$see,'programElements',function(){return this.programElements_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:Declaration$model$declaration}}},$cont:See,$an:function(){return[shared()];},d:['ceylon.language','See','$at','programElements']};});
        })(See.$$.prototype);
    }
    return See;
}
exports.$init$See=$init$See;
$init$See();
var see=function (programElements$957){
    if(programElements$957===undefined){programElements$957=getEmpty();}
    return See(programElements$957);
};
see.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:See},$ps:[{$nm:'programElements',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Declaration$model$declaration}}}}],$an:function(){return[shared(),annotation()];},d:['ceylon.language','see']};};
exports.see=see;
function Authors(authors, $$authors){
    $init$Authors();
    if ($$authors===undefined)$$authors=new Authors.$$;
    $$authors.$$targs$$={Value:{t:Authors},ProgramElement:{t:Annotated$model}};
    if(authors===undefined){authors=getEmpty();}
    $$authors.authors_=authors;
    OptionalAnnotation$model({Value:{t:Authors},ProgramElement:{t:Annotated$model}},$$authors);
    add_type_arg($$authors,'Value',{t:Authors});
    add_type_arg($$authors,'ProgramElement',{t:Annotated$model});
    return $$authors;
}
Authors.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Authors},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Authors']};};
exports.Authors=Authors;
function $init$Authors(){
    if (Authors.$$===undefined){
        initTypeProto(Authors,'ceylon.language::Authors',Basic,$init$OptionalAnnotation$model());
        (function($$authors){
            defineAttr($$authors,'authors',function(){return this.authors_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:String$}}},$cont:Authors,$an:function(){return[shared()];},d:['ceylon.language','Authors','$at','authors']};});
        })(Authors.$$.prototype);
    }
    return Authors;
}
exports.$init$Authors=$init$Authors;
$init$Authors();
var by=function (authors$958){
    if(authors$958===undefined){authors$958=getEmpty();}
    return Authors(authors$958);
};
by.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Authors},$ps:[{$nm:'authors',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:String$}}}}],$an:function(){return[shared(),annotation()];},d:['ceylon.language','by']};};
exports.by=by;
function ThrownException(type, when, $$thrownException){
    $init$ThrownException();
    if ($$thrownException===undefined)$$thrownException=new ThrownException.$$;
    $$thrownException.$$targs$$={Value:{t:ThrownException},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}};
    $$thrownException.type_=type;
    $$thrownException.when_=when;
    SequencedAnnotation$model({Value:{t:ThrownException},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}},$$thrownException);
    add_type_arg($$thrownException,'Value',{t:ThrownException});
    return $$thrownException;
}
ThrownException.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:SequencedAnnotation$model,a:{Value:{t:ThrownException},ProgramElement:{ t:'u', l:[{t:ValueDeclaration$model$declaration},{t:FunctionDeclaration$model$declaration},{t:ClassDeclaration$model$declaration}]}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','ThrownException']};};
exports.ThrownException=ThrownException;
function $init$ThrownException(){
    if (ThrownException.$$===undefined){
        initTypeProto(ThrownException,'ceylon.language::ThrownException',Basic,$init$SequencedAnnotation$model());
        (function($$thrownException){
            defineAttr($$thrownException,'type',function(){return this.type_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Declaration$model$declaration},$cont:ThrownException,$an:function(){return[shared()];},d:['ceylon.language','ThrownException','$at','type']};});
            defineAttr($$thrownException,'when',function(){return this.when_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:ThrownException,$an:function(){return[shared()];},d:['ceylon.language','ThrownException','$at','when']};});
        })(ThrownException.$$.prototype);
    }
    return ThrownException;
}
exports.$init$ThrownException=$init$ThrownException;
$init$ThrownException();
var $throws=function (type$959,when$960){
    if(when$960===undefined){when$960=String$("",0);}
    return ThrownException(type$959,when$960);
};
$throws.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:ThrownException},$ps:[{$nm:'type',$mt:'prm',$t:{t:Declaration$model$declaration}},{$nm:'when',$mt:'prm',$def:1,$t:{t:String$}}],$an:function(){return[shared(),annotation()];},d:['ceylon.language','throws']};};
exports.$throws=$throws;
function Deprecation(description, $$deprecation){
    $init$Deprecation();
    if ($$deprecation===undefined)$$deprecation=new Deprecation.$$;
    $$deprecation.$$targs$$={Value:{t:Deprecation},ProgramElement:{t:Annotated$model}};
    $$deprecation.description_=description;
    OptionalAnnotation$model({Value:{t:Deprecation},ProgramElement:{t:Annotated$model}},$$deprecation);
    add_type_arg($$deprecation,'Value',{t:Deprecation});
    add_type_arg($$deprecation,'ProgramElement',{t:Annotated$model});
    return $$deprecation;
}
Deprecation.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Deprecation},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Deprecation']};};
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
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:String$}]},$cont:Deprecation,$an:function(){return[shared()];},d:['ceylon.language','Deprecation','$at','reason']};});defineAttr($$deprecation,'description',function(){return this.description_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:Deprecation,$an:function(){return[shared()];},d:['ceylon.language','Deprecation','$at','description']};});
        })(Deprecation.$$.prototype);
    }
    return Deprecation;
}
exports.$init$Deprecation=$init$Deprecation;
$init$Deprecation();
var deprecated=function (reason$961){
    if(reason$961===undefined){reason$961=String$("",0);}
    return Deprecation(reason$961);
};
deprecated.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Deprecation},$ps:[{$nm:'reason',$mt:'prm',$def:1,$t:{t:String$}}],$an:function(){return[shared(),annotation()];},d:['ceylon.language','deprecated']};};
exports.deprecated=deprecated;
function Tags(tags, $$tags){
    $init$Tags();
    if ($$tags===undefined)$$tags=new Tags.$$;
    $$tags.$$targs$$={Value:{t:Tags},ProgramElement:{t:Annotated$model}};
    if(tags===undefined){tags=getEmpty();}
    $$tags.tags_=tags;
    OptionalAnnotation$model({Value:{t:Tags},ProgramElement:{t:Annotated$model}},$$tags);
    add_type_arg($$tags,'Value',{t:Tags});
    add_type_arg($$tags,'ProgramElement',{t:Annotated$model});
    return $$tags;
}
Tags.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Tags},ProgramElement:{t:Annotated$model}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Tags']};};
exports.Tags=Tags;
function $init$Tags(){
    if (Tags.$$===undefined){
        initTypeProto(Tags,'ceylon.language::Tags',Basic,$init$OptionalAnnotation$model());
        (function($$tags){
            defineAttr($$tags,'tags',function(){return this.tags_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:String$}}},$cont:Tags,$an:function(){return[shared()];},d:['ceylon.language','Tags','$at','tags']};});
        })(Tags.$$.prototype);
    }
    return Tags;
}
exports.$init$Tags=$init$Tags;
$init$Tags();
var tagged=function (tags$962){
    if(tags$962===undefined){tags$962=getEmpty();}
    return Tags(tags$962);
};
tagged.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Tags},$ps:[{$nm:'tags',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:String$}}}}],$an:function(){return[shared(),annotation()];},d:['ceylon.language','tagged']};};
exports.tagged=tagged;
function License(url, $$license){
    $init$License();
    if ($$license===undefined)$$license=new License.$$;
    $$license.$$targs$$={Value:{t:License},ProgramElement:{t:Module$model$declaration}};
    $$license.url_=url;
    OptionalAnnotation$model({Value:{t:License},ProgramElement:{t:Module$model$declaration}},$$license);
    add_type_arg($$license,'Value',{t:License});
    add_type_arg($$license,'ProgramElement',{t:Module$model$declaration});
    return $$license;
}
License.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:License},ProgramElement:{t:Module$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','License']};};
exports.License=License;
function $init$License(){
    if (License.$$===undefined){
        initTypeProto(License,'ceylon.language::License',Basic,$init$OptionalAnnotation$model());
        (function($$license){
            defineAttr($$license,'url',function(){return this.url_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:License,$an:function(){return[shared()];},d:['ceylon.language','License','$at','url']};});
        })(License.$$.prototype);
    }
    return License;
}
exports.$init$License=$init$License;
$init$License();
var license=function (url$963){
    return License(url$963);
};
license.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:License},$ps:[{$nm:'url',$mt:'prm',$t:{t:String$}}],$an:function(){return[shared(),annotation()];},d:['ceylon.language','license']};};
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
Optional.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:OptionalAnnotation$model,a:{Value:{t:Optional},ProgramElement:{t:Import$model$declaration}}}],$an:function(){return[shared(),$final(),annotation()];},d:['ceylon.language','Optional']};};
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
optional.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Optional},$ps:[],$an:function(){return[shared(),annotation()];},d:['ceylon.language','optional']};};
exports.optional=optional;
function Number$(wat) {
    return wat;
}
initType(Number$, 'ceylon.language::Number');
Number$.$$metamodel$$={$an:function(){return[shared()]},mod:$$METAMODEL$$,d:['ceylon.language','Number']};
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
JSNumber.$$metamodel$$={$nm:'JSNumber',$mt:'cls',$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','Number']};

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
Integer.$$metamodel$$={$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:['ceylon.language','Integer']};

function Float(value) {
    if (value && value.getT$name && value.getT$name() === 'ceylon.language::Float') {
        return value;
    }
    var that = new Number(value);
    that.float$ = true;
    return that;
}
initTypeProto(Float, 'ceylon.language::Float', Object$, Scalar, Exponentiable);
Float.$$metamodel$$={$an:function(){return[shared(),native(),final()];},mod:$$METAMODEL$$,d:['ceylon.language','Float']};

var JSNum$proto = Number.prototype;
JSNum$proto.getT$all = function() {
    return (this.float$ ? Float : Integer).$$.T$all;
}
JSNum$proto.getT$name = function() {
    return (this.float$ ? Float : Integer).$$.T$name;
}
JSNum$proto.toString = origNumToString;
defineAttr(JSNum$proto, 'string', function(){ return String$(this.toString()); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','string']});
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
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Invertable','$at','negativeValue']});
defineAttr(JSNum$proto, 'positiveValue', function() {
    return this.float$ ? this : this.valueOf();
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Invertable','$at','positiveValue']});
JSNum$proto.equals = function(other) { return (typeof(other)==='number' || other.constructor===Number) && other==this.valueOf(); }
JSNum$proto.compare = function(other) {
    var value = this.valueOf();
    return value==other ? equal : (value<other ? smaller:larger);
}
defineAttr(JSNum$proto, '$float', function(){ return Float(this.valueOf()); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Number','$at','float']});
defineAttr(JSNum$proto, 'integer', function(){ return toInt(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Number','$at','integer']});
defineAttr(JSNum$proto, 'integerValue', function(){ return toInt(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Ordinal','$at','integerValue']});
defineAttr(JSNum$proto, 'character', function(){ return Character(this.valueOf()); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Integer','$at','character']});
defineAttr(JSNum$proto, 'successor', function(){ return this+1; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Ordinal','$at','successor']});
defineAttr(JSNum$proto, 'predecessor', function(){ return this-1; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Ordinal','$at','predecessor']});
defineAttr(JSNum$proto, 'unit', function(){ return this == 1; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Integral','$at','unit']});
defineAttr(JSNum$proto, 'zero', function(){ return this == 0; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Integral','$at','zero']});
defineAttr(JSNum$proto, 'fractionalPart', function() {
    if (!this.float$) { return 0; }
    return Float(this - (this>=0 ? Math.floor(this) : Math.ceil(this)));
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Number','$at','fractionalPart']});
defineAttr(JSNum$proto, 'wholePart', function() {
    if (!this.float$) { return this.valueOf(); }
    return Float(this>=0 ? Math.floor(this) : Math.ceil(this));
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Number','$at','wholePart']});
defineAttr(JSNum$proto, 'sign', function(){ return this > 0 ? 1 : this < 0 ? -1 : 0; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Number','$at','sign']});
defineAttr(JSNum$proto, 'hash', function() {
    return this.float$ ? String$(this.toPrecision()).hash : this.valueOf();
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','hash']});
JSNum$proto.distanceFrom = function(other) {
    return (this.float$ ? this.wholePart : this) - other;
}
//Binary interface
defineAttr(JSNum$proto, 'not', function(){ return ~this; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Binary','$at','not']});
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
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Binary','$at','size']});
defineAttr(JSNum$proto, 'magnitude', function(){ return Math.abs(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Number','$at','magnitude']});

//-(2^53-1)
var $minIntegerValue = Integer(-9007199254740991);
function getMinIntegerValue() { return $minIntegerValue; }
//(2^53-3) => ((2^53)-2 is NaN)
var $maxIntegerValue = Integer(9007199254740989);
function getMaxIntegerValue() { return $maxIntegerValue; }

function $parseFloat(s) { return Float(parseFloat(s)); }

defineAttr(JSNum$proto, 'undefined', function(){ return isNaN(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Float','$at','undefined']});
defineAttr(JSNum$proto, 'finite', function(){ return this!=Infinity && this!=-Infinity && !isNaN(this); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Float','$at','finite']});
defineAttr(JSNum$proto, 'infinite', function(){ return this==Infinity || this==-Infinity; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Float','$at','infinite']});
defineAttr(JSNum$proto, 'strictlyPositive', function(){ return this>0 || (this==0 && (1/this==Infinity)); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Float','$at','strictlyPositive']});
defineAttr(JSNum$proto, 'strictlyNegative', function() { return this<0 || (this==0 && (1/this==-Infinity)); },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Float','$at','strictlyNegative']});

var $infinity = Float(Infinity);
function getInfinity() { return $infinity; }
getInfinity.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language','infinity']};
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
Boolean$.$$metamodel$$={$ps:[],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:['ceylon.language','Boolean']};
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
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','hash']});
var trueString = String$("true", 4);
var falseString = String$("false", 5);
defineAttr(Boolean.prototype, 'string', function(){ return this.valueOf()?trueString:falseString; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','string']});
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
Comparison.$$metamodel$$={$ps:[{t:String$}],$an:function(){return[shared(),abstract()]},mod:$$METAMODEL$$,d:['ceylon.language','Comparison']};
var Comparison$proto = Comparison.$$.prototype;
defineAttr(Comparison$proto, 'string', function(){ return this.name; },
  undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','string']});
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
                var _mm = obj.$$metamodel$$;
                if (typeof(_mm)==='function') {
                  _mm=_mm();
                  obj.$$metamodel$$=_mm;
                }
                //We can navigate the metamodel
                if (_mm.d['$mt'] === 'mthd') {
                    if (type.t === Callable) { //It's a callable reference
                        if (type.a && type.a.Return && _mm['$t']) {
                            //Check if return type matches
                            if (extendsType(_mm['$t'], type.a.Return)) {
                                if (type.a.Arguments && _mm['$ps'] !== undefined) {
                                    var metaparams = _mm['$ps'];
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
                    var _mm = type.t.$$metamodel$$;
                    if (typeof(_mm)==='function') {
                      _mm = _mm();
                      type.t.$$metamodel$$=_mm;
                    }
                    if (_mm && _mm.$tp && _mm.$tp[i]) iance=_mm.$tp[i]['var'];
                    if (iance === null) {
                        //Type parameter may be in the outer type
                        while (iance===null && tmpobj.$$outer !== undefined) {
                            tmpobj=tmpobj.$$outer;
                            var _tmpf = tmpobj.constructor.T$all[tmpobj.constructor.T$name];
                            var _mmf = typeof(_tmpf.$$metamodel$$)==='function'?_tmpf.$$metamodel$$():_tmpf.$$metamodel$$;
                            if (_mmf && _mmf.$tp && _mmf.$tp[i]) {
                                iance=_mmf.$tp[i]['var'];
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
    if (t1 === undefined || t1.t === undefined) {
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
className.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','className']};

function identityHash(obj) {
    return obj.BasicID;
}
identityHash.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','identityHash']};

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
function wrapexc(e,loc,file) {
  if (loc !== undefined) e.$loc=loc;
  if (file !== undefined) e.$file=file;
  return e;
}
function throwexc(e,loc,file) {
  if (loc !== undefined) e.$loc=loc;
  if (file !== undefined) e.$file=file;
  throw e;
}
exports.set_type_args=set_type_args;
exports.add_type_arg=add_type_arg;
exports.exists=exists;
exports.nonempty=nonempty;
exports.isOfType=isOfType;
exports.className=className;
exports.identityHash=identityHash;
exports.throwexc=throwexc;
exports.wrapexc=wrapexc;
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
internalSort.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','internalSort']};

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
flatten.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','flatten']};

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
unflatten.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','unflatten']};
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
integerRangeByIterable.$$metamodel$$={$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','integerRangeByIterable']};
exports.integerRangeByIterable=integerRangeByIterable;
// implementation of object "process" in ceylon.language
function languageClass() {
    var lang = new languageClass.$$;
    Basic(lang);
    return lang;
}
languageClass.$$metamodel$$={$nm:'languageClass',$mt:'cls',$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','language']};
initTypeProto(languageClass, "ceylon.language::language", $init$Basic());
var lang$proto=languageClass.$$.prototype;
defineAttr(lang$proto, 'version', function() {
    return String$("0.6",3);
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','language','$at','version']});
defineAttr(lang$proto, 'majorVersion', function(){ return 0; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','language','$at','majorVersion']});
defineAttr(lang$proto, 'minorVersion', function(){ return 6; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','language','$at','minorVersion']});
defineAttr(lang$proto, 'releaseVersion', function(){ return 0; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','language','$at','releaseVersion']});
defineAttr(lang$proto, 'versionName', function(){ return String$("Transmogrifier",14); },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','language','$at','versionName']});
defineAttr(lang$proto, 'majorVersionBinary', function(){ return 5; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','language','$at','majorVersionBinary']});
defineAttr(lang$proto, 'minorVersionBinary', function(){ return 0; },undefined,
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','language','$at','minorVersionBinary']});
var languageString = String$("language", 8);
defineAttr(lang$proto, 'string', function() {
    return languageString;
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','string']});

var language$ = languageClass();
function getLanguage() { return language$; }
exports.getLanguage=getLanguage;

function processClass() {
    var proc = new processClass.$$;
    Basic(proc);
    return proc;
}
processClass.$$metamodel$$={$nm:'processClass',$mt:'cls',$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','process']};
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
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','arguments']});
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
  {$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','newline']});

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
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','milliseconds']});
defineAttr(process$proto, 'nanoseconds', function() {
    return Date.now()*1000000;
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','nanoseconds']});

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
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','Object','$at','string']});
defineAttr(process$proto, 'vm', function() {
    if (typeof process !== "undefined" && process.execPath && process.execPath.match(/node(\.exe)?$/)) {
        return String$("node.js", 7);
    } else if (typeof window === 'object') {
        return String$("Browser", 7);
    }
    return String$("Unknown JavaScript environment", 30);
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','vm']});
defineAttr(process$proto, 'vmVersion', function() {
    if (typeof process !== "undefined" && typeof process.version === 'string') {
        return String$(process.version);
    }
    return String$("Unknown");
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','vmVersion']});
defineAttr(process$proto, 'os',function() {
    if (typeof process !== "undefined" && typeof process.platform === 'string') {
        return String$(process.platform);
    }
    return String$("Unknown");
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','os']});
defineAttr(process$proto, 'osVersion', function() {
    return String$("Unknown");
},undefined,{$an:function(){return[shared(),actual()]},mod:$$METAMODEL$$,d:['ceylon.language','process','$at','osVersion']});

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
  if (typeof(mm) === 'function') {
    mm = mm();
  }
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
annotations$model.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:'Values',$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:ClassOrInterface$model,a:{Type:{t:ConstrainedAnnotation$model,a:{Values:'Values',Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:ConstrainedAnnotation$model,a:{Values:'Values',Value:'Value',ProgramElement:'ProgramElement'}}]},Values:{},ProgramElement:{'satisfies':[{t:Annotated$model}]}},$an:function(){return[shared(),$native()];},d:['ceylon.language.model','annotations']};};
exports.annotations$model=annotations$model;
/* Metamodel module and package objects */
var $loadedModules$={};
exports.$loadedModules$=$loadedModules$;
function $addmod$(mod, modname) {
  $loadedModules$[modname] = mod;
}
exports.$addmod$=$addmod$;
function modules$2(){
    var $$modules=new modules$2.$$;
    defineAttr($$modules,'list',function(){
        var mods=[];
        for (var m in $loadedModules$) {
          var slashPos = m.lastIndexOf('/');
          mods.push(this.find(m.substring(0,slashPos), m.substring(slashPos+1)));
        }
        return mods.reifyCeylonType({Element:{t:Module$model$declaration}});
    },undefined,{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:Module$model$declaration}}},$cont:modules$2,$an:function(){return[shared()];},d:['ceylon.language.model','modules','$at','list']});
    function find(name,version){
        var modname = name + "/" + (version?version:"unversioned");
        var lm = $loadedModules$[modname];
        if (!lm) {
          var mpath;
          if (name === 'default' && version=='unversioned') {
            mpath = name + "/" + name;
          } else {
            mpath = name.replace(/\./g,'/') + '/' + version + "/" + name + "-" + version;
          }
          lm = require(mpath);
        }
        if (lm && lm.$$METAMODEL$$) {
          lm = Modulo(lm);
          $loadedModules$[modname] = lm;
        }
        return lm === undefined ? null : lm;
    }
    $$modules.find=find;
    find.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Module$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'version',$mt:'prm',$t:{t:String$}}],$cont:modules$2,$an:function(){return[shared()];},d:['ceylon.language.model','modules','$m','find']};
    defineAttr($$modules,'$default',function(){
        return find('default',"unversioned");
    },undefined,{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Module$model$declaration}]},$cont:modules$2,$an:function(){return[shared()];},d:['ceylon.language.model','modules','$at','default']});
    return $$modules;
}
function $init$modules$model(){
    if (modules$2.$$===undefined){
        initTypeProto(modules$2,'ceylon.language.model::modules',Basic);
    }
    return modules$2;
}
exports.$init$modules$model=$init$modules$model;
$init$modules$model();
var modules$model=modules$2();
var getModules$model=function(){
    return modules$model;
}
exports.getModules$model=getModules$model;

function Modulo(meta, $$modulo){
    $init$Modulo();
    if ($$modulo===undefined)$$modulo=new Modulo.$$;
    Module$model$declaration($$modulo);
    $$modulo.meta=meta;
    if (typeof(meta.$$METAMODEL$$)==='function') {
      var mm = meta.$$METAMODEL$$();
      meta.$$METAMODEL$$=mm;
    }
    var name=String$(meta.$$METAMODEL$$['$mod-name']);
    defineAttr($$modulo,'name',function(){return name;},undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Modulo,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Module','$at','name']});
    var version=String$(meta.$$METAMODEL$$['$mod-version']);
    defineAttr($$modulo,'version',function(){return version;},undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Modulo,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Module','$at','version']});
    defineAttr($$modulo,'members',function(){
      if (this.meta.$$METAMODEL$$['$pks$'] === undefined) {
        this.meta.$$METAMODEL$$['$pks$'] = {};
        for (mem in this.meta.$$METAMODEL$$) {
          if (typeof(mem) === 'string' && mem[0]!=='$') {
            this.meta.$$METAMODEL$$['$pks$'][mem] = Paquete(mem, this, this.meta.$$METAMODEL$$[mem]);
          }
        }
      }
      var m = [];
      for (mem in this.meta.$$METAMODEL$$['$pks$']) {
        if (typeof(mem) === 'string') {
          m.push(this.meta.$$METAMODEL$$['$pks$'][mem]);
        }
      }
      return m.reifyCeylonType({Element:{t:Package$model$declaration}});
    },undefined,{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:Package$model$declaration}}},$cont:Modulo,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Module','$at','members']});
    defineAttr($$modulo,'dependencies',function(){
      var deps = this.meta.$$METAMODEL$$['$mod-deps'];
      if (deps.length === 0) return getEmpty();
      if (typeof(deps[0]) === 'string') {
        var _d = [];
        for (var i=0; i<deps.length;i++) {
          var spos = deps[i].lastIndexOf('/');
          _d.push(Importa(String$(deps[i].substring(0,spos)), String$(deps[i].substring(spos+1))));
        }
        deps = _d.reifyCeylonType({Element:{t:Import$model$declaration}});
        this.meta.$$METAMODEL$$['$mod-deps'] = deps;
      }
      return deps;
    },undefined,{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:Import$model$declaration}}},$cont:Modulo,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Module','$at','dependencies']});
    function findPackage(pknm){
      if (this.meta.$$METAMODEL$$['$pks$'] === undefined) this.members;
      var pk = this.meta.$$METAMODEL$$['$pks$'][pknm];
      return pk===undefined ? null : pk;
    }
    $$modulo.findPackage=findPackage;
    findPackage.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Package$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:Modulo,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Module','$m','findPackage']};
    function annotations($$$mptypes){
      var anns = this.meta.$mod$ans$;
      if (typeof(anns) === 'function') {
        anns = anns();
        this.meta.$mod$ans$=anns;
      } else if (anns === undefined) {
        anns = [];
      }
      var r = [];
      for (var i=0; i < anns.length; i++) {
        var an = anns[i];
        if (isOfType(an, $$$mptypes.Annotation)) r.push(an);
      }
      return r.reifyCeylonType({Element:$$$mptypes.Annotation});
    }
    $$modulo.annotations=annotations;
    annotations.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Annotation'}},$ps:[],$cont:Modulo,$tp:{Annotation:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Annotation'}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Module','$m','annotations']};
    return $$modulo;
}
Modulo.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:Module$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','Module']};
exports.Modulo=Modulo;
function $init$Modulo(){
    if (Modulo.$$===undefined){
        initTypeProto(Modulo,'Modulo',Basic,Module$model$declaration);
    }
    return Modulo;
}
exports.$init$Modulo=$init$Modulo;
$init$Modulo();
function Importa(name, version, $$importa){
    $init$Importa();
    if ($$importa===undefined)$$importa=new Importa.$$;
    Import$model$declaration($$importa);
    var name=name;
    defineAttr($$importa,'name',function(){return name;},undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Importa,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Import','$at','name']});
    var version=version;
    defineAttr($$importa,'version',function(){return version;},undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Importa,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Import','$at','version']});
    return $$importa;
}
Importa.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:Import$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','Import']};
exports.Importa=Importa;
function $init$Importa(){
    if (Importa.$$===undefined){
        initTypeProto(Importa,'Importa',Basic,Import$model$declaration);
    }
    return Importa;
}
exports.$init$Importa=$init$Importa;
$init$Importa();
function Paquete(name, container, pkg, $$paquete){
    $init$Paquete();
    if ($$paquete===undefined)$$paquete=new Paquete.$$;
    Package$model$declaration($$paquete);
    $$paquete.pkg=pkg;
    var name=name;
    //determine suffix for declarations
    var suffix = '';
    if (name!==container.name) {
      var _s = name.substring(container.name.length);
      suffix = _s.replace(/\./g, '$');
    }
    $$paquete.suffix=suffix;
    defineAttr($$paquete,'name',function(){return name;},undefined,{mod:$$METAMODEL$$,$t:{t:String$},$cont:Paquete,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$at','name']});
    var container=container;
    defineAttr($$paquete,'container',function(){return container;},undefined,{mod:$$METAMODEL$$,$t:{t:Module$model$declaration},$cont:Paquete,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$at','container']});
    function members($$$mptypes){
      var filter;
      if (extendsType($$$mptypes.Kind,{t:FunctionDeclaration$model$declaration})) {
        filter = function(m) { return m['$mt']==='mthd'; };
      } else if (extendsType($$$mptypes.Kind,{t:ValueDeclaration$model$declaration})) {
        filter = function(m) { return m['$mt']==='attr' || m['$mt']==='gttr' || m['$mt']==='obj'; };
      } else if (extendsType($$$mptypes.Kind,{t:ClassDeclaration$model$declaration})) {
        filter = function(m) { return m['$mt']==='cls'; };
      } else if (extendsType($$$mptypes.Kind,{t:InterfaceDeclaration$model$declaration})) {
        filter = function(m) { return m['$mt']==='ifc'; };
      } else if (extendsType($$$mptypes.Kind,{t:FunctionOrValueDeclaration$model$declaration})) {
        filter = function(m) { return m['$mt']==='mthd' || m['$mt']==='attr' || m['$mt']==='gttr' || m['$mt']==='obj'; };
      } else if (extendsType($$$mptypes.Kind,{t:ClassOrInterfaceDeclaration$model$declaration})) {
        filter = function(m) { return m['$mt']==='cls' || m['$mt']==='ifc'; };
      } else {
        //Dejamos pasar todo
        filter = function(m) { return true; }
      }
      var r=[];
      for (var mn in this.pkg) {
        var m = this.pkg[mn];
        if (filter(m)) {
          var mt = m['$mt'];
          if (mt === 'mthd') {
            r.push(OpenFunction(String$(m['$nm']), this, true, m));
          } else if (mt==='cls') {
            r.push(OpenClass(String$(m['$nm']), this, true, m));
          } else if (mt==='ifc') {
            r.push(OpenInterface(String$(m['$nm']), this, true, m));
          } else if (mt==='attr'||mt==='gttr'||mt==='obj') {
            r.push(OpenValue(String$(m['$nm']), this, true, m));
          }
        }
      }
      return r.reifyCeylonType({Element:$$$mptypes.Kind});
    }
    $$paquete.members=members;
    members.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Kind'}},$ps:[],$cont:Paquete,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$m','members']};
    function annotatedMembers($$$mptypes){
        return getEmpty();
    }
    $$paquete.annotatedMembers=annotatedMembers;
    annotatedMembers.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Kind'}},$ps:[],$cont:Paquete,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]},Annotation:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$m','annotatedMembers']};
    function getMember(name$3,$$$mptypes){
      var m = this.pkg[name$3];
      if (m) {
        var mt = m['$mt'];
        //There's a member alright, but check its type
        if (extendsType({t:ValueDeclaration$model$declaration}, $$$mptypes.Kind)) {
          if (mt==='attr'||m==='gttr'||m==='obj') {
            return OpenValue(name$3, this, true, m);
          }
          return null;
        } else if (extendsType({t:FunctionDeclaration$model$declaration}, $$$mptypes.Kind)) {
          if (mt==='mthd') {
            return OpenFunction(name$3, this, true, m);
          }
          return null;
        } else if (extendsType({t:FunctionOrValueDeclaration$model$declaration}, $$$mptypes.Kind)) {
          if (mt==='attr'||m==='gttr'||m==='obj') {
            return OpenValue(name$3, this, true, m);
          } else if (mt==='mthd') {
            return OpenFunction(name$3, this, true, m);
          }
          return null;
        } else if (extendsType({t:ClassDeclaration$model$declaration}, $$$mptypes.Kind)) {
          if (mt==='cls') {
            return OpenClass(name$3, this, true, m);
          }
          return null;
        } else if (extendsType({t:InterfaceDeclaration$model$declaration}, $$$mptypes.Kind)) {
          if (mt==='ifc') {
            return OpenInterface(name$3, this, true, m);
          }
          return null;
        } else if (extendsType({t:ClassOrInterfaceDeclaration$model$declaration}, $$$mptypes.Kind)) {
          if (mt==='ifc') {
            return OpenInterface(name$3, this, true, m);
          } else if (mt==='cls') {
            return OpenClass(name$3, this, true, m);
          }
          return null;
        } else {
console.log("WTF do I do with this " + name$3 + " Kind " + className($$$mptypes.Kind));
        }
      }
      return null;
    }
    $$paquete.getMember=getMember;
    getMember.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},'Kind']},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:Paquete,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$m','getMember']};
    function getValue(name$4) {
      var m = this.pkg[name$4];
      if (m && m['$mt']==='attr') {
        return OpenValue(name$4, this, true, m);
      }
      return null;
    }
    $$paquete.getValue=getValue;
    getValue.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:ValueDeclaration$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:Paquete,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$m','getValue']};
    function getClassOrInterface(name$5){
      var ci = this.pkg[name$5];
      if (ci && ci['$mt']==='cls') {
        return OpenClass(name$5, this, true, ci);
      } else if (ci && ci['$mt']==='ifc') {
        return OpenInterface(name$5, this, true, ci);
      }
      return null;
    }
    $$paquete.getClassOrInterface=getClassOrInterface;
    getClassOrInterface.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:ClassOrInterfaceDeclaration$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:Paquete,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$m','getClassOrInterface']};
    function getFunction(name$6){
      var f = this.pkg[name$6];
      if (f && f['$mt']==='mthd') {
        return OpenFunction(name$6, this, true, f);
      }
      return null;
    }
    $$paquete.getFunction=getFunction;
    getFunction.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:FunctionDeclaration$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:Paquete,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$m','getFunction']};
    function annotations($$$mptypes){
      var _k = '$pkg$ans$' + this.name.replace(/\./g,'$');
      var anns = this.container.meta[_k];
      if (typeof(anns) === 'function') {
        anns = anns();
        this.container.meta[_k]=anns;
      } else if (anns === undefined) {
        anns = [];
      }
      var r = [];
      for (var i=0; i < anns.length; i++) {
        var an = anns[i];
        if (isOfType(an, $$$mptypes.Annotation)) r.push(an);
      }
      return r.reifyCeylonType({Element:$$$mptypes.Annotation});
        return getEmpty();
    }
    $$paquete.annotations=annotations;
    annotations.$$metamodel$$={mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Annotation'}},$ps:[],$cont:Paquete,$tp:{Annotation:{'var':'out','satisfies':[{t:Annotation$model,a:{Value:'Annotation'}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','Package','$m','annotations']};
    return $$paquete;
}
Paquete.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:Package$model$declaration}],$an:function(){return[shared()];},d:['ceylon.language.model.declaration','Package']};
exports.Paquete=Paquete;
function $init$Paquete(){
    if (Paquete.$$===undefined){
        initTypeProto(Paquete,'Paquete',Basic,Package$model$declaration);
    }
    return Paquete;
}
exports.$init$Paquete=$init$Paquete;
$init$Paquete();

function AppliedClass(tipo,$$targs$$,that){
    $init$AppliedClass();
    if (that===undefined)that=new AppliedClass.$$;
    set_type_args(that,$$targs$$);
    Class$model($$targs$$,that);
    that.tipo=tipo;
    return that;
}
AppliedClass.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Type:{'var':'out',},A:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:Class$model,a:{Arguments:'A',Type:'Type'}}],d:['ceylon.language.model','Class']};
function $init$AppliedClass(){
    if (AppliedClass.$$===undefined){
        initTypeProto(AppliedClass,'ceylon.language.model:: AppliedClass',Basic,Class$model);
        (function($$clase){
            
            //declaration
            defineAttr($$clase,'declaration',function(){
              var $$clase=this;
              if ($$clase._decl)return $$clase._decl;
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
            };//$$clase.getFunction.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$metamodel}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Function$metamodel,a:{Arguments:{t:Nothing},Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.metamodel',d:['ceylon.language.metamodel','ClassOrInterface','$m','getFunction']};

            $$clase.getClassOrInterface=function getClassOrInterface(name$4,types$5,$$$mptypes){
                var $$clase=this;
                if(types$5===undefined){types$5=getEmpty();}
                throw Exception(String$("class/iface",11));
            };//$$clase.getClassOrInterface.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$metamodel}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:ClassOrInterface$metamodel,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.metamodel',d:['ceylon.language.metamodel','ClassOrInterface','$m','getClassOrInterface']};

            $$clase.getAttribute=function getAttribute(name$6,$$$mptypes){
                var $$clase=this;
                throw Exception(String$("attrib",6));
            };//$$clase.getAttribute.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Attribute$metamodel,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},pkg:'ceylon.language.metamodel',d:['ceylon.language.metamodel','ClassOrInterface','$m','getAttribute']};

            defineAttr($$clase,'typeArguments',function(){
                var $$clase=this;
                throw Exception(String$("type args",9));
            });
        })(AppliedClass.$$.prototype);
    }
    return AppliedClass;
}
exports.AppliedClass$model=$init$AppliedClass;
$init$AppliedClass();

function AppliedInterface(tipo,$$targs$$,$$interfaz){
    $init$AppliedInterface();
    if ($$interfaz===undefined)$$interfaz=new AppliedInterface.$$;
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
    getFunction.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$model,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Function$model,a:{Arguments:{t:Nothing},Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model','ClassOrInterface','$m','getFunction']};//getFunction.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Sequential,a:{Element:{t:Type$metamodel}}},Element:{t:Sequential,a:{Element:{t:Type$metamodel}}}}},Return:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:$$$mptypes.SubType,Kind:$$$mptypes.Kind}}]}};
    
    //MethodDefinition getClassOrInterface at test.ceylon (10:4-11:89)
    function getClassOrInterface(name$4,types$5,$$$mptypes){
        if(types$5===undefined){types$5=getEmpty();}
        throw Exception(String$("class/iface",11));
    }
    $$interfaz.getClassOrInterface=getClassOrInterface;
    getClassOrInterface.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$model,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$tp:{SubType:{},Kind:{'satisfies':[{t:ClassOrInterface$model,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model','ClassOrInterface','$m','getClassOrInterface']};//getClassOrInterface.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Sequential,a:{Element:{t:Type$metamodel}}},Element:{t:Sequential,a:{Element:{t:Type$metamodel}}}}},Return:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:$$$mptypes.SubType,Kind:$$$mptypes.Kind}}]}};
    
    //MethodDefinition getAttribute at test.ceylon (12:4-13:77)
    function getAttribute(name$6,$$$mptypes){
        throw Exception(String$("attrib",6));
    }
    $$interfaz.getAttribute=getAttribute;
    getAttribute.$$metamodel$$={mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:Member$model,a:{Type:'SubType',Kind:'Kind'}}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$tp:{SubType:{},Kind:{'satisfies':[{t:Attribute$model,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model','ClassOrInterface','$m','getAttribute']};//getAttribute.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:String$},Element:{t:String$}}},Return:{ t:'u', l:[{t:Null},{t:Member$metamodel,a:{Type:$$$mptypes.SubType,Kind:$$$mptypes.Kind}}]}};
    
    //AttributeGetterDefinition typeArguments at test.ceylon (14:4-14:89)
    defineAttr($$interfaz,'typeArguments',function() {
        throw Exception(String$("type args",9));
    });
    $$interfaz.tipo=tipo;
    return $$interfaz;
}
AppliedInterface.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Type:{'var':'out',}},satisfies:[{t:Interface$model,a:{Type:'Type'}}],d:['ceylon.language.model','Interface']};
function $init$AppliedInterface(){
    if (AppliedInterface.$$===undefined){
        initTypeProto(AppliedInterface,'ceylon.language.model::AppliedInterface',Basic,Interface$model);
    }
    return AppliedInterface;
}
exports.$init$AppliedInterface$model=$init$AppliedInterface;
$init$AppliedInterface();

function AppliedUnionType(tipo,types$2, $$appliedUnionType){
    $init$AppliedUnionType();
    if ($$appliedUnionType===undefined)$$appliedUnionType=new AppliedUnionType.$$;
    $$appliedUnionType.types$2=types$2;
    UnionType$model($$appliedUnionType);
    $$appliedUnionType.tipo=tipo;
    return $$appliedUnionType;
}
AppliedUnionType.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:UnionType$model}],d:['ceylon.language.model','AppliedUnionType']};
function $init$AppliedUnionType(){
    if (AppliedUnionType.$$===undefined){
        initTypeProto(AppliedUnionType,'AppliedUnionType',Basic,UnionType$model);
        (function($$appliedUnionType){
            
            defineAttr($$appliedUnionType,'caseTypes',function(){
                var $$appliedUnionType=this;
                return $$appliedUnionType.types$2;
            });
        })(AppliedUnionType.$$.prototype);
    }
    return AppliedUnionType;
}
exports.$init$AppliedUnionType$model=$init$AppliedUnionType;
$init$AppliedUnionType();

function AppliedIntersectionType(tipo,types$3, $$appliedIntersectionType){
    $init$AppliedIntersectionType();
    if ($$appliedIntersectionType===undefined)$$appliedIntersectionType=new AppliedIntersectionType.$$;
    $$appliedIntersectionType.types$3=types$3;
    IntersectionType$model($$appliedIntersectionType);
    $$appliedIntersectionType.tipo=tipo;
    return $$appliedIntersectionType;
}
AppliedIntersectionType.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:IntersectionType$model}],d:['ceylon.language.model','AppliedIntersectionType']};
function $init$AppliedIntersectionType(){
    if (AppliedIntersectionType.$$===undefined){
        initTypeProto(AppliedIntersectionType,'AppliedIntersectionType',Basic,IntersectionType$model);
        (function($$appliedIntersectionType){
            
            defineAttr($$appliedIntersectionType,'satisfiedTypes',function(){
                var $$appliedIntersectionType=this;
                return $$appliedIntersectionType.types$3;
            });
        })(AppliedIntersectionType.$$.prototype);
    }
    return AppliedIntersectionType;
}
exports.$init$AppliedIntersectionType$model=$init$AppliedIntersectionType;
$init$AppliedIntersectionType();

function AppliedFunction(m,o) {
  var f = o===undefined?function(){return m.apply(this,arguments);}:function(){return m.apply(o,arguments);}
  var mm=m.$$metamodel$$;
  if (typeof(mm)==='function') {mm=mm();m.$$metamodel$$=mm;}
  f.$$metamodel$$={mod:$$METAMODEL$$,d:['ceylon.language.model','Function'],$t:mm.$t,$ps:mm.$ps,$an:mm.$an};
  var T$all={'ceylon.language.model::Function':Function$model};
  for (x in f.getT$all()) { T$all[x]=f.getT$all()[x]; }
  f.getT$all=function() {return T$all; };
  //TODO add type arguments
  var types = {t:Empty};
  var t2s = [];
  for (var i=mm.$ps.length-1; i>=0; i--) {
    var e;
    t2s.push(mm.$ps[i].$t);
    if (t2s.length == 1) {
      e = mm.$ps[i].$t;
    } else {
      var lt=[];
      for (var j=0;j<t2s.legth;j++)lt.push(t2s[j]);
      e = {t:'u', l:lt};
    }
    types = {t:Tuple,a:{Rest:types,First:mm.$ps[i].$t,Element:e}};
  }
  f.$$targs$$={Type:mm.$t,Arguments:types};
  return f;
}


function AppliedValue(attr,$$targs$$,$$appliedAttribute){
    $init$AppliedValue();
    if ($$appliedAttribute===undefined)$$appliedAttribute=new AppliedValue.$$;
    set_type_args($$appliedAttribute,$$targs$$);
    Attribute$model($$appliedAttribute.$$targs$$===undefined?$$targs$$:{Type:$$appliedAttribute.$$targs$$.Type,Container:$$appliedAttribute.$$targs$$.Container},$$appliedAttribute);
    $$appliedAttribute.attr=attr;
    return $$appliedAttribute;
}
AppliedValue.$$metamodel$$={mod:$$METAMODEL$$,'super':{t:Basic},$tp:{Container:{'var':'in',},Type:{'var':'out',}},satisfies:[{t:Attribute$model,a:{Type:'Type',Container:'Container'}}],$an:function(){return[shared()];},d:['ceylon.language.model','Attribute']};
exports.AppliedValue$model=AppliedValue;
function $init$AppliedValue(){
    if (AppliedValue.$$===undefined){
        initTypeProto(AppliedValue,'AppliedValue',Basic,Attribute$model);
        (function($$appliedAttribute){
            
            //AttributeGetterDefinition declaration at caca.ceylon (5:2-5:58)
            defineAttr($$appliedAttribute,'declaration',function(){
                var $$appliedAttribute=this;
                throw Exception();
            },undefined,{mod:$$METAMODEL$$,$an:function(){return[shared(),actual()];},d:['ceylon.language.model','AttributeModel','$at','declaration']});
            //AttributeGetterDefinition declaringClassOrInterface at caca.ceylon (6:2-6:76)
            defineAttr($$appliedAttribute,'declaringClassOrInterface',function(){
                var $$appliedAttribute=this;
                throw Exception();
            },undefined,{mod:$$METAMODEL$$,$an:function(){return[shared(),actual()];},d:['ceylon.language.model','AttributeModel','$at','declaringClassOrInterface']});
            //AttributeGetterDefinition type at caca.ceylon (7:2-7:31)
            defineAttr($$appliedAttribute,'type',function(){
                var $$appliedAttribute=this;
                throw Exception();
            },undefined,{mod:$$METAMODEL$$,$an:function(){return[shared(),actual()];},d:['ceylon.language.model','AttributeModel','$at','type']});
        })(AppliedValue.$$.prototype);
    }
    return AppliedValue;
}
exports.$init$AppliedValue$model=$init$AppliedValue;
$init$AppliedValue();
//Find the real declaration of something from its model definition
function _findTypeFromModel(pkg,mdl) {
  var mod = pkg.container;
  //TODO this is very primitive needs a lot of rules replicated from the JsIdentifierNames
  var nm = mdl.$nm + pkg.suffix;
  return mod.meta[nm];
}

//ClassDefinition OpenFunction at caca.ceylon (18:0-36:0)
function OpenFunction(name, packageContainer, toplevel, meta, that){
    $init$OpenFunction();
    if (that===undefined)that=new OpenFunction.$$;
    that.name_=name;
    that.packageContainer_=packageContainer;
    that.toplevel_=toplevel;
    if (meta.$$metamodel$$ === undefined) {
      //it's a metamodel
      that.meta=meta;
      that.tipo=_findTypeFromModel(packageContainer,meta);
    } else {
      //it's a type
      that.tipo = meta;
      var _mm=meta.$$metamodel$$;
      if (typeof(_mm)==='function') {
        _mm=_mm();
        meta.$$metamodel$$=_mm;
      }
      that.meta = get_model(_mm);
    }
    FunctionDeclaration$model$declaration(that);
    return that;
}
OpenFunction.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:FunctionDeclaration$model$declaration}],d:['ceylon.language.model.declaration','FunctionDeclaration']};};
function $init$OpenFunction(){
    if (OpenFunction.$$===undefined){
        initTypeProto(OpenFunction,'OpenFunction',Basic,FunctionDeclaration$model$declaration);
        (function($$openFunction){
            
            //MethodDefinition apply at caca.ceylon (20:4-20:81)
            $$openFunction.apply=function apply(types$2){
                var $$openFunction=this;
                if(types$2===undefined){types$2=getEmpty();}
                throw Exception();
            };$$openFunction.apply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Function$model,a:{Arguments:{t:Nothing},Type:{t:Anything}}},$ps:[{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$m','apply']};};
            
            //MethodDefinition bindAndApply at caca.ceylon (21:4-21:105)
            $$openFunction.bindAndApply=function bindAndApply(instance$3,types$4){
              var $$openFunction=this;
              if(types$4===undefined){types$4=getEmpty();}
              //TODO check for naming rules
              //WTF is types argument for?
              return AppliedFunction(instance$3[this.name],instance$3);
            };$$openFunction.bindAndApply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Function$model,a:{Arguments:{t:Nothing},Type:{t:Anything}}},$ps:[{$nm:'instance',$mt:'prm',$t:{t:Object$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$m','bindAndApply']};};
            
            //MethodDefinition memberApply at caca.ceylon (22:4-23:54)
            $$openFunction.memberApply=function memberApply(types$5,$$$mptypes){
                var $$openFunction=this;
                if(types$5===undefined){types$5=getEmpty();}
                throw Exception();
            };$$openFunction.memberApply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Method$model,a:{Arguments:'Arguments',Type:'MethodType',Container:'Container'}},$ps:[{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenFunction,$tp:{Container:{},MethodType:{},Arguments:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$m','memberApply']};};
            
            //AttributeDeclaration defaulted at caca.ceylon (25:4-25:44)
            defineAttr($$openFunction,'defaulted',function(){
                var $$openFunction=this;
                return false;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','defaulted']};});
            
            //AttributeDeclaration variadic at caca.ceylon (26:4-26:43)
            defineAttr($$openFunction,'variadic',function(){
                var $$openFunction=this;
                return false;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','variadic']};});
            
            //AttributeDeclaration parameterDeclarations at caca.ceylon (28:4-28:74)
            defineAttr($$openFunction,'parameterDeclarations',function(){
                var $$openFunction=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:FunctionOrValueDeclaration$model$declaration}}},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','parameterDeclarations']};});
            
            //MethodDeclaration getParameterDeclaration at caca.ceylon (29:4-29:90)
            $$openFunction.getParameterDeclaration=function (name$6){
                var $$openFunction=this;
                return null;
            };
            $$openFunction.getParameterDeclaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:FunctionOrValueDeclaration$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$m','getParameterDeclaration']};};
            
            //AttributeGetterDefinition openType at caca.ceylon (33:2-33:43)
            defineAttr($$openFunction,'openType',function(){
                var $$openFunction=this;
                throw Exception();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:OpenType$model$declaration},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','openType']};});
            //AttributeDeclaration typeParameterDeclarations at caca.ceylon (34:2-34:63)
            defineAttr($$openFunction,'typeParameterDeclarations',function(){
                var $$openFunction=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:TypeParameter$model$declaration}}},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','typeParameterDeclarations']};});
            
            //MethodDeclaration getTypeParameterDeclaration at caca.ceylon (35:2-35:79)
            $$openFunction.getTypeParameterDeclaration=function (name$7){
                var $$openFunction=this;
                return null;
            };
            $$openFunction.getTypeParameterDeclaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:TypeParameter$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$m','getTypeParameterDeclaration']};};
            defineAttr($$openFunction,'name',function(){return this.name_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','name']};});
            defineAttr($$openFunction,'packageContainer',function(){return this.packageContainer_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Package$model$declaration},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','packageContainer']};});
            defineAttr($$openFunction,'toplevel',function(){return this.toplevel_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenFunction,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','FunctionDeclaration','$at','toplevel']};});
        })(OpenFunction.$$.prototype);
    }
    return OpenFunction;
}
exports.$init$OpenFunction=$init$OpenFunction;
$init$OpenFunction();

//ClassDefinition OpenValue at caca.ceylon (38:0-45:0)
function OpenValue(name, packageContainer, toplevel, meta, that){
    $init$OpenValue();
    if (that===undefined)that=new OpenValue.$$;
    that.name_=name;
    that.packageContainer_=packageContainer;
    that.toplevel_=toplevel;
    if (meta.$$metamodel$$ === undefined) {
      //it's a metamodel
      that.meta=meta;
      that.tipo=_findTypeFromModel(packageContainer,meta);
    } else {
      //it's a type
      that.tipo = meta;
      var _mm=meta.$$metamodel$$;
      if (typeof(_mm)==='function') {
        _mm=_mm();
        meta.$$metamodel$$=_mm;
      }
      that.meta = get_model(_mm);
    }
    ValueDeclaration$model$declaration(that);
    return that;
}
OpenValue.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:ValueDeclaration$model$declaration}],d:['ceylon.language.model.declaration','ValueDeclaration']};};
function $init$OpenValue(){
    if (OpenValue.$$===undefined){
        initTypeProto(OpenValue,'OpenValue',Basic,ValueDeclaration$model$declaration);
        (function($$openValue){
            
            //MethodDefinition apply at caca.ceylon (39:4-39:68)
            $$openValue.apply=function apply(instance$8){
              if (instance$8===null || instance$8===undefined)return null;
              return instance$8[this.name];
            };$$openValue.apply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Value$model,a:{Type:{t:Anything}}},$ps:[{$nm:'instance',$mt:'prm',$def:1,$t:{t:Anything}}],$cont:OpenValue,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ValueDeclaration','$m','apply']};};
            
            //AttributeDeclaration defaulted at caca.ceylon (40:4-40:44)
            defineAttr($$openValue,'defaulted',function(){
                var $$openValue=this;
                return false;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenValue,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ValueDeclaration','$at','defaulted']};});
            
            //AttributeDeclaration variadic at caca.ceylon (41:4-41:43)
            defineAttr($$openValue,'variadic',function(){
                var $$openValue=this;
                return false;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenValue,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ValueDeclaration','$at','variadic']};});
            
            //AttributeGetterDefinition openType at caca.ceylon (44:2-44:43)
            defineAttr($$openValue,'openType',function(){
                var $$openValue=this;
                throw Exception();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:OpenType$model$declaration},$cont:OpenValue,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ValueDeclaration','$at','openType']};});defineAttr($$openValue,'name',function(){return this.name_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:OpenValue,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ValueDeclaration','$at','name']};});
            defineAttr($$openValue,'packageContainer',function(){return this.packageContainer_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Package$model$declaration},$cont:OpenValue,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ValueDeclaration','$at','packageContainer']};});
            defineAttr($$openValue,'toplevel',function(){return this.toplevel_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenValue,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ValueDeclaration','$at','toplevel']};});
        })(OpenValue.$$.prototype);
    }
    return OpenValue;
}
exports.$init$OpenValue=$init$OpenValue;
$init$OpenValue();

//ClassDefinition OpenClass at caca.ceylon (47:0-70:0)
function OpenClass(name, packageContainer, toplevel, meta, that){
    $init$OpenClass();
    if (that===undefined)that=new OpenClass.$$;
    that.name_=name;
    that.packageContainer_=packageContainer;
    that.toplevel_=toplevel;
    if (meta.$$metamodel$$ === undefined) {
      //it's a metamodel
      that.meta=meta;
      that.tipo=_findTypeFromModel(packageContainer,meta);
    } else {
      //it's a type
      that.tipo = meta;
      var _mm=meta.$$metamodel$$;
      if (typeof(_mm)==='function') {
        _mm=_mm();
        meta.$$metamodel$$=_mm;
      }
      that.meta = get_model(_mm);
    }
    ClassDeclaration$model$declaration(that);
    return that;
}
OpenClass.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:ClassDeclaration$model$declaration}],d:['ceylon.language.model.declaration','ClassDeclaration']};};
function $init$OpenClass(){
    if (OpenClass.$$===undefined){
        initTypeProto(OpenClass,'OpenClass',Basic,ClassDeclaration$model$declaration);
        (function($$openClass){
            
            //AttributeDeclaration anonymous at caca.ceylon (48:2-48:42)
            defineAttr($$openClass,'anonymous',function(){
                var $$openClass=this;
                return false;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','anonymous']};});
            
            //MethodDefinition apply at caca.ceylon (49:2-49:76)
            $$openClass.apply=function apply(types$9){
                var $$openClass=this;
                if(types$9===undefined){types$9=getEmpty();}
                throw Exception();
            };$$openClass.apply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Class$model,a:{Arguments:{t:Nothing},Type:{t:Anything}}},$ps:[{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$m','apply']};};
            
            //MethodDefinition bindAndApply at caca.ceylon (50:2-50:100)
            $$openClass.bindAndApply=function bindAndApply(instance$10,types$11){
                var $$openClass=this;
                if(types$11===undefined){types$11=getEmpty();}
                throw Exception();
            };$$openClass.bindAndApply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Class$model,a:{Arguments:{t:Nothing},Type:{t:Anything}}},$ps:[{$nm:'instance',$mt:'prm',$t:{t:Object$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$m','bindAndApply']};};
            
            //MethodDefinition memberApply at caca.ceylon (51:2-52:72)
            $$openClass.memberApply=function memberApply(types$12,$$$mptypes){
                var $$openClass=this;
                if(types$12===undefined){types$12=getEmpty();}
                throw Exception();
            };$$openClass.memberApply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Member$model,a:{Type:'Container',Kind:'Kind'}},$ps:[{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenClass,$tp:{Container:{},Kind:{'satisfies':[{t:ClassOrInterface$model,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$m','memberApply']};};
            
            //MethodDeclaration memberDeclarations at caca.ceylon (53:2-54:66)
            $$openClass.memberDeclarations=function ($$$mptypes){
                var $$openClass=this;
                return getEmpty();
            };
            $$openClass.memberDeclarations.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Kind'}},$ps:[],$cont:OpenClass,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$m','memberDeclarations']};};
            
            //MethodDeclaration annotatedMemberDeclarations at caca.ceylon (55:2-56:66)
            $$openClass.annotatedMemberDeclarations=function ($$$mptypes){
                var $$openClass=this;
                return getEmpty();
            };
            $$openClass.annotatedMemberDeclarations.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Kind'}},$ps:[],$cont:OpenClass,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]},Annotation:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$m','annotatedMemberDeclarations']};};
            
            //AttributeGetterDefinition openType at caca.ceylon (61:2-61:43)
            defineAttr($$openClass,'openType',function(){
                var $$openClass=this;
                throw Exception();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:OpenType$model$declaration},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','openType']};});
            //AttributeDeclaration typeParameterDeclarations at caca.ceylon (62:2-62:63)
            defineAttr($$openClass,'typeParameterDeclarations',function(){
                var $$openClass=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:TypeParameter$model$declaration}}},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','typeParameterDeclarations']};});
            
            //MethodDeclaration getTypeParameterDeclaration at caca.ceylon (63:2-63:79)
            $$openClass.getTypeParameterDeclaration=function (name$14){
                var $$openClass=this;
                return null;
            };
            $$openClass.getTypeParameterDeclaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:TypeParameter$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$m','getTypeParameterDeclaration']};};
            
            //AttributeDeclaration parameterDeclarations at caca.ceylon (65:2-65:72)
            defineAttr($$openClass,'parameterDeclarations',function(){
                var $$openClass=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:FunctionOrValueDeclaration$model$declaration}}},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','parameterDeclarations']};});
            
            //MethodDeclaration getParameterDeclaration at caca.ceylon (66:2-66:88)
            $$openClass.getParameterDeclaration=function (name$15){
                var $$openClass=this;
                return null;
            };
            $$openClass.getParameterDeclaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:FunctionOrValueDeclaration$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$m','getParameterDeclaration']};};
            
            //AttributeDeclaration superclassDeclaration at caca.ceylon (68:2-68:86)
            defineAttr($$openClass,'superclassDeclaration',function(){
                var $$openClass=this;
                return null;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:OpenParameterisedType$model$declaration,a:{DeclarationType:{t:ClassDeclaration$model$declaration}}}]},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','superclassDeclaration']};});
            
            //AttributeDeclaration interfaceDeclarations at caca.ceylon (69:2-69:89)
            defineAttr($$openClass,'interfaceDeclarations',function(){
                var $$openClass=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:OpenParameterisedType$model$declaration,a:{DeclarationType:{t:InterfaceDeclaration$model$declaration}}}}},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','interfaceDeclarations']};});
            defineAttr($$openClass,'name',function(){return this.name_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','name']};});
            defineAttr($$openClass,'packageContainer',function(){return this.packageContainer_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Package$model$declaration},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','packageContainer']};});
            defineAttr($$openClass,'toplevel',function(){return this.toplevel_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenClass,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','ClassDeclaration','$at','toplevel']};});
        })(OpenClass.$$.prototype);
    }
    return OpenClass;
}
exports.$init$OpenClass=$init$OpenClass;
$init$OpenClass();

//ClassDefinition OpenInterface at caca.ceylon (72:0-92:0)
function OpenInterface(name, packageContainer, toplevel, meta, that) {
    $init$OpenInterface();
    if (that===undefined)that=new OpenInterface.$$;
    that.name_=name;
    that.packageContainer_=packageContainer;
    that.toplevel_=toplevel;
    if (meta.$$metamodel$$ === undefined) {
      //it's a metamodel
      that.meta=meta;
      that.tipo=_findTypeFromModel(packageContainer,meta);
    } else {
      //it's a type
      that.tipo = meta;
      var _mm=meta.$$metamodel$$;
      if (typeof(_mm)==='function') {
        _mm=_mm();
        meta.$$metamodel$$=_mm;
      }
      that.meta = get_model(_mm);
    }
    InterfaceDeclaration$model$declaration(that);
    return that;
}
OpenInterface.$$metamodel$$=function(){return{mod:$$METAMODEL$$,'super':{t:Basic},satisfies:[{t:InterfaceDeclaration$model$declaration}],d:['ceylon.language.model.declaration','InterfaceDeclaration']};};
function $init$OpenInterface(){
    if (OpenInterface.$$===undefined){
        initTypeProto(OpenInterface,'OpenInterface',Basic,InterfaceDeclaration$model$declaration);
        (function($$openInterface){
            
            //MethodDefinition apply at caca.ceylon (73:2-73:71)
            $$openInterface.apply=function apply(types$16){
                var $$openInterface=this;
                if(types$16===undefined){types$16=getEmpty();}
                throw Exception();
            };$$openInterface.apply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Interface$model,a:{Type:{t:Anything}}},$ps:[{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$m','apply']};};
            
            //MethodDefinition bindAndApply at caca.ceylon (74:2-74:95)
            $$openInterface.bindAndApply=function bindAndApply(instance$17,types$18){
                var $$openInterface=this;
                if(types$18===undefined){types$18=getEmpty();}
                throw Exception();
            };$$openInterface.bindAndApply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Interface$model,a:{Type:{t:Anything}}},$ps:[{$nm:'instance',$mt:'prm',$t:{t:Object$}},{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$m','bindAndApply']};};
            
            //MethodDefinition memberApply at caca.ceylon (75:2-76:72)
            $$openInterface.memberApply=function memberApply(types$19,$$$mptypes){
                var $$openInterface=this;
                if(types$19===undefined){types$19=getEmpty();}
                throw Exception();
            };$$openInterface.memberApply.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Member$model,a:{Type:'Container',Kind:'Kind'}},$ps:[{$nm:'types',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Type$model}}}}],$cont:OpenInterface,$tp:{Container:{},Kind:{'satisfies':[{t:ClassOrInterface$model,a:{Type:{t:Anything}}}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$m','memberApply']};};
            
            //MethodDeclaration memberDeclarations at caca.ceylon (78:2-79:66)
            $$openInterface.memberDeclarations=function ($$$mptypes){
                var $$openInterface=this;
                return getEmpty();
            };
            $$openInterface.memberDeclarations.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Kind'}},$ps:[],$cont:OpenInterface,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$m','memberDeclarations']};};
            
            //MethodDeclaration annotatedMemberDeclarations at caca.ceylon (80:2-81:66)
            $$openInterface.annotatedMemberDeclarations=function ($$$mptypes){
                var $$openInterface=this;
                return getEmpty();
            };
            $$openInterface.annotatedMemberDeclarations.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:'Kind'}},$ps:[],$cont:OpenInterface,$tp:{Kind:{'satisfies':[{t:TopLevelOrMemberDeclaration$model$declaration}]},Annotation:{}},$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$m','annotatedMemberDeclarations']};};
            
           
            //AttributeGetterDefinition openType at caca.ceylon (86:2-86:43)
            defineAttr($$openInterface,'openType',function(){
                var $$openInterface=this;
                throw Exception();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:OpenType$model$declaration},$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$at','openType']};});
            //AttributeDeclaration typeParameterDeclarations at caca.ceylon (87:2-87:63)
            defineAttr($$openInterface,'typeParameterDeclarations',function(){
                var $$openInterface=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:TypeParameter$model$declaration}}},$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$at','typeParameterDeclarations']};});
            
            //MethodDeclaration getTypeParameterDeclaration at caca.ceylon (88:2-88:79)
            $$openInterface.getTypeParameterDeclaration=function (name$21){
                var $$openInterface=this;
                return null;
            };
            $$openInterface.getTypeParameterDeclaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:TypeParameter$model$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$m','getTypeParameterDeclaration']};};
            
            //AttributeDeclaration superclassDeclaration at caca.ceylon (90:2-90:86)
            defineAttr($$openInterface,'superclassDeclaration',function(){
                var $$openInterface=this;
                return null;
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:OpenParameterisedType$model$declaration,a:{DeclarationType:{t:ClassDeclaration$model$declaration}}}]},$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$at','superclassDeclaration']};});
            
            //AttributeDeclaration interfaceDeclarations at caca.ceylon (91:2-91:89)
            defineAttr($$openInterface,'interfaceDeclarations',function(){
                var $$openInterface=this;
                return getEmpty();
            },undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:OpenParameterisedType$model$declaration,a:{DeclarationType:{t:InterfaceDeclaration$model$declaration}}}}},$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$at','interfaceDeclarations']};});
            defineAttr($$openInterface,'name',function(){return this.name_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:String$},$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$at','name']};});
            defineAttr($$openInterface,'packageContainer',function(){return this.packageContainer_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Package$model$declaration},$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$at','packageContainer']};});
            defineAttr($$openInterface,'toplevel',function(){return this.toplevel_;},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Boolean$},$cont:OpenInterface,$an:function(){return[shared(),actual()];},d:['ceylon.language.model.declaration','InterfaceDeclaration','$at','toplevel']};});
        })(OpenInterface.$$.prototype);
    }
    return OpenInterface;
}
exports.$init$OpenInterface=$init$OpenInterface;
$init$OpenInterface();
//From a runtime metamodel, get the model definition by following the path into the module's model.
function get_model(mm) {
  var map=mm.mod;
  var path=mm.d;
  for (var i=0; i < path.length; i++) {
    map = map[path[i]];
  }
  return map;
}

function type$model(x) {
    if (x === null) {
        return getNothingType$model();
    } else {
        //Search for metamodel
        var mm = typeof(x.$$metamodel$$)==='function'?x.$$metamodel$$():x.$$metamodel$$;
        if (mm === undefined && x.constructor && x.constructor.T$name && x.constructor.T$all) {
            //It's probably an instance of a Ceylon type
            var _x = x.constructor.T$all[x.constructor.T$name];
            if (_x) {
                mm = _x.$$metamodel$$;
                x=_x;
            }
        }
        if (typeof(mm) == 'function') mm = mm();
        if (mm) {
            var metatype = get_model(mm)['$mt'];
            if (metatype === 'ifc' || metatype === 'cls') { //Interface or Class
                return typeLiteral$model({Type:x});
            } else if (metatype === 'mthd') { //Method
                return typeLiteral$model({Type:$JsCallable(x)});
            } else {
                console.log("type(" + metatype + ")WTF?");
            }
        } else {
            throw Exception(String$("No metamodel available for "+x));
        }
    }
    return "UNIMPLEMENTED";
}
type$model.$$metamodel$$={$ps:[{t:Anything}],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language.model','type']};
exports.type$model=type$model;

function typeLiteral$model($$targs$$) {
  if ($$targs$$ === undefined || $$targs$$.Type === undefined) {
    throw Exception("Missing type argument 'Type' " + require('util').inspect($$targs$$));
  } else if ($$targs$$.Type.$$metamodel$$ == undefined) {
    //closed type
    var t = $$targs$$.Type.t
    if (t === undefined) {
      throw Exception("'Type' argument should be an open or closed type");
    } else if (t === 'u' || t === 'i') {
      return t === 'u' ? applyUnionType($$targs$$.Type) :
      applyIntersectionType($$targs$$.Type);
    } else if (t.$$metamodel$$ === undefined) {
      throw Exception("JS Interop not supported / incomplete metamodel for " + require('util').inspect(t));
    } else {
      var mm = t.$$metamodel$$;
      if (typeof(mm)==='function')mm=mm();
      var mdl = get_model(mm);
      if (mdl['$mt'] === 'cls') {
        return AppliedClass(t,mdl['$tp']);
      } else if (mdl['$mt'] === 'ifc') {
        return AppliedInterface(t,mdl['$tp']);
      } else if (mdl['$mt'] === 'mthd') {
        return AppliedFunction(t);
      } else if (mdl['$mt'] === 'attr' || mdl['$mt'] === 'gttr') {
        return AppliedValue(t,{Container:{t:mm.$cont},Type:mm.$t});
      } else {
        console.log("WTF is a metatype " + mdl['$mt'] + " on a closed type???????");
      }
      console.log("typeLiteral<" + t.getT$name() + "> (closed type)");
    }
  } else {
    //open type
    var t = $$targs$$.Type;
    var mm = t.$$metamodel$$;
    if (typeof(mm)==='function')mm=mm();
    var mdl = get_model(mm);
    //We need the module
    var _mod = modules$model.find(mm.mod['$mod-name'],mm.mod['$mod-version']);
    var _pkg = _mod.findPackage(mm.d[0]);
    if (mdl['$mt'] === 'cls') {
      return OpenClass(mdl['$nm'], _pkg, mm.$cont===undefined, t);
    } else if (mdl['$mt'] === 'ifc') {
      return OpenInterface(mdl['$nm'], _pkg, mm.$cont===undefined, t);
    } else if (mdl['$mt'] === 'mthd') {
      return OpenFunction(mdl['$nm'], _pkg, mm.$cont===undefined, t);
    } else if (mdl['$mt'] === 'attr' || mdl['$mt'] === 'gttr') {
      return OpenValue(mdl['$nm'], _pkg, mm.$cont===undefined, t);
    } else {
      console.log("WTF is a metatype " + mdl['$mt'] + " on an open type???????");
    }
    console.log("typeLiteral<" + t.getT$name() + "> (open type)");
  }
  throw Exception("typeLiteral UNIMPLEMENTED for " + require('util').inspect($$targs$$));
}
typeLiteral$model.$$metamodel$$={$ps:[],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language.model','typeLiteral']};
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
  return AppliedUnionType(ut, cases.reifyCeylonType({Absent:{t:Null},Element:{t:Type$model}}));
}
function applyIntersectionType(it) { //return AppliedIntersectionType
  var sats = [];
  pushTypes(sats, it.l);
  return AppliedIntersectionType(it, sats.reifyCeylonType({Absent:{t:Null},Element:{t:Type$model}}));
}
function applyType(t) { //return AppliedType
  return typeLiteral$model({Type:t});
}
exports.$mod$ans$=function(){return[by([String$("Gavin King",10),String$("Tom Bentley",11),String$("Tako Schotanus",14),String$("Stephane Epardaud",17),String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),license(String$("http://www.apache.org/licenses/LICENSE-2.0.html",47))];};
exports.$pkg$ans$ceylon$language=function(){return[by([String$("Gavin King",10),String$("Tom Bentley",11),String$("Tako Schotanus",14),String$("Stephane Epardaud",17),String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];};
exports.$pkg$ans$ceylon$language$model=function(){return[by([String$("Gavin King",10),String$("Stephane Epardaud",17)].reifyCeylonType({Absent:{t:Null},Element:{t:String}})),shared()];};
exports.$pkg$ans$ceylon$language$model$declaration=function(){return[by([String$("Gavin King",10),String$("Stephane Epardaud",17)].reifyCeylonType({Absent:{t:Null},Element:{t:String}})),shared()];};
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
NativeException.$$metamodel$$={$nm:'NativeException',$mt:'cls',$ps:[{t:Exception}],$an:function(){return[shared()];},mod:$$METAMODEL$$,d:['ceylon.language','Exception']};
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
