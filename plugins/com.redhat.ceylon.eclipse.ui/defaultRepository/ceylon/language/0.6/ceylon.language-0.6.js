(function(define) {
define(function(require, exports, module) {
//the Ceylon language module
//!!!METAMODEL:{"$mod-name":"ceylon.language","$mod-version":"0.6","ceylon.language":{"Iterator":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Iterable"],"doc":["Produces elements of an `Iterable` object. Classes that \nimplement this interface should be immutable."],"by":["Gavin"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The next element, or `finished` if there are no \nmore elements to be iterated."]},"$nm":"next"}},"$nm":"Iterator"},"Callable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A reference to a function. The type arguments encode \nthe function return type and parameter types. The \nparameter types are typically represented as a tuple\ntype. For example, the type of the function reference \n`plus<Float>` is:\n\n    Callable<Float,[Float,Float]>\n\nwhich we usually abbreviate `Float(Float,Float)`. Any\ninstance of `Callable` may be _invoked_ by supplying a \npositional argument list:\n\n    Float(Float,Float) add = plus<Float>;\n    value four = add(2.0, 2.0);\n\nor by supplying a tuple containing the arguments:\n\n    Float(Float,Float) add = plus<Float>;\n    [Float,Float] twoAndTwo = [2.0, 2.0];\n    value four = add(*twoAndTwo);\n\nThis interface may not be implemented by user code."]},"$nm":"Callable"},"Array":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Cloneable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Ranged"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"abstract":[],"shared":[],"native":[],"doc":["A fixed-size array of elements. An array may have zero\nsize (an empty array). Arrays are mutable. Any element\nof an array may be set to a new value.\n\nThis class is provided primarily to support interoperation \nwith Java, and for some performance-critical low-level \nprogramming tasks."]},"$m":{"copyTo":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"other"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$nm":"sourcePosition"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$nm":"destinationPosition"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$def":"1","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Efficiently copy the elements in the segment \n`sourcePosition:length` of this array to the segment \n`destinationPosition:length` of the given array."]},"$nm":"copyTo"},"set":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Replace the existing element at the specified index \nwith the given element. Does nothing if the specified \nindex is negative or larger than the index of the \nlast element in the array."]},"$nm":"set"}},"$at":{"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the array, without the first element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this array, returning a new array."],"actual":[]},"$nm":"reversed"}},"$nm":"Array"},"ArraySequence":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceAppender"],"doc":["An immutable `Sequence` implemented using the platform's \nnative array type. Where possible copying of the underlying \narray is avoided."]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"iterator"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"count"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"spanTo"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"contains"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"ArraySequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"ArraySequence"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"actual":[]},"$nm":"size"}},"$nm":"ArraySequence"},"byKey":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Key","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"}],"$an":{"shared":[],"see":["byItem"],"doc":["A comparator for `Entry`s which compares their keys \naccording to the given `comparing()` function."]},"$nm":"byKey"},"Singleton":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A sequence with exactly one element, which may be null."]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"a"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns the contained element, if the specified \nindex is `0`."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `1` if this `Singleton`'s element\nsatisfies the predicate, or `0` otherwise."],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A `Singleton` can be equal to another `List` if \nthat `List` has only one element which is equal to \nthis `Singleton`'s element."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0` and the given `length` is greater than `0`.\nOtherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `true` if the specified element is this \n`Singleton`'s element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0`. Otherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `0`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `Singleton` with the same element."],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the Singleton itself, or empty"],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Return this singleton."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `Empty`."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `1`."],"actual":[]},"$nm":"size"}},"$nm":"Singleton"},"Comparable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Other"}],"$an":{"shared":[],"doc":["The general contract for values whose magnitude can be \ncompared. `Comparable` imposes a total ordering upon\ninstances of any type that satisfies the interface.\nIf a type `T` satisfies `Comparable<T>`, then instances\nof `T` may be compared using the comparison operators\n`<`, `>`, `<=`, >=`, and `<=>`.\n\nThe total order of a type must be consistent with the \ndefinition of equality for the type. That is, there\nare three mutually exclusive possibilities:\n\n- `x<y`,\n- `x>y`, or\n- `x==y`"],"by":["Gavin"]},"$m":{"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["equals"],"doc":["Compares this value with the given value. \nImplementations must respect the constraints that: \n\n- `x==y` if and only if `x<=>y == equal` \n   (consistency with `equals()`), \n- if `x>y` then `y<x` (symmetry), and \n- if `x>y` and `y>z` then `x>z` (transitivity)."]},"$nm":"compare"}},"$nm":"Comparable","$st":"Other"},"sizeExceeds":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"iterable"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"size"}]],"$mt":"mthd","$nm":"sizeExceeds"},"Comparison":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"larger"},{"$pk":"ceylon.language","$nm":"smaller"},{"$pk":"ceylon.language","$nm":"equal"}],"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Comparable"],"doc":["The result of a comparison between two `Comparable` \nobjects."],"by":["Gavin"]},"$m":{"largerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"largerThan"},"equal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"equal"},"asSmallAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asSmallAs"},"asLargeAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asLargeAs"},"smallerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"smallerThan"},"unequal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"unequal"}},"$nm":"Comparison"},"Empty":{"of":[{"$pk":"ceylon.language","$nm":"empty"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$an":{"shared":[],"see":["Sequence"],"doc":["A sequence with no elements. The type `Empty` may be\nabbreviated `[]`, and an instance is produced by the \nexpression `[]`. That is, in the following expression,\n`e` has type `[]` and refers to the value `[]`:\n\n    [] none = [];\n\n(Whether the syntax `[]` refers to the type or the \nvalue depends upon how it occurs grammatically.)"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["Returns an iterator that is already exhausted."],"actual":[]},"$nm":"iterator"},"sort":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `null` for any given index."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns 0 for any given predicate."],"actual":[]},"$nm":"count"},"select":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"select"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given segment."],"actual":[]},"$nm":"segment"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `false` for any given element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"following":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"head"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"following"},"withTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"defines"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanTo"},"chain":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"OtherAbsent"}],"$an":{"shared":[],"doc":["Returns `other`."],"actual":[]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"withLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"collect":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"collect"},"span":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"clone"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"indexed"},"sequence":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a string description of the empty sequence: \n`{}`."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `true`."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns 0."],"actual":[]},"$nm":"size"}},"$nm":"Empty"},"annotation":{"$t":{"$pk":"ceylon.language","$nm":"Annotation"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as an annotation class, or \na method as an annotation method."]},"$nm":"annotation"},"Enumerable":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["Abstraction of ordinal types whose instances can be \nmapped to the integers or to a range of integers."]},"$at":{"integerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The corresponding integer. The implementation must\nsatisfy these constraints:\n\n    (x.successor).integerValue = x.integerValue+1\n    (x.predecessor).integerValue = x.integerValue-1\n\nfor every instance `x` of the enumerable type."]},"$nm":"integerValue"}},"$nm":"Enumerable","$st":"Other"},"empty":{"super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Empty"}],"$mt":"obj","$an":{"shared":[],"doc":["A sequence with no elements, abbreviated `[]`. The \nunique instance of the type `[]`."]},"$nm":"empty"},"combine":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"},{"$t":"OtherElement","$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"prm","$pt":"f","$nm":"combination"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"otherElements"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"},{"$nm":"Element"},{"$nm":"OtherElement"}],"$an":{"shared":[],"doc":["Applies a function to each element of two `Iterable`s\nand returns an `Iterable` with the results."],"by":["Gavin","Enrique Zamudio","Tako"]},"$nm":"combine"},"false":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing falsity in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"false"},"compose":{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"X"},{"$nm":"Y"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[]},"$nm":"compose"},"Formal":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[formal]]."]},"$nm":"Formal"},"Sequential":{"of":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A possibly-empty, immutable sequence of values. The \ntype `Sequential<Element>` may be abbreviated \n`[Element*]` or `Element[]`. \n\n`Sequential` has two enumerated subtypes:\n\n- `Empty`, abbreviated `[]`, represents an empty \n   sequence, and\n- `Sequence<Element>`, abbreviated `[Element+]` \n   represents a non-empty sequence, and has the very\n   important subclass `Tuple`."]},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"[ x, y, z ]\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \nelement."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new sequence."],"actual":[]},"$nm":"reversed"}},"$nm":"Sequential"},"Finished":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"finished"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Iterator"],"doc":["The type of the value that indicates that \nan `Iterator` is exhausted and has no more \nvalues to return."]},"$nm":"Finished"},"Default":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[default]]."]},"$nm":"Default"},"coalesce":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Return a sequence containing the given values which are\nnot null. If there are no values which are not null,\nreturn an empty sequence."]},"$nm":"coalesce"},"final":{"$t":{"$pk":"ceylon.language","$nm":"Final"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as final. A `final` class \nmay not be extended."]},"$nm":"final"},"plus":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["times","sum"],"doc":["Add the given `Summable` values."]},"$nm":"plus"},"Entry":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Key","$hdn":"1","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$hdn":"1","$mt":"prm","$pt":"v","$nm":"item"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A pair containing a _key_ and an associated value called \nthe _item_. Used primarily to represent the elements of \na `Map`. The type `Entry<Key,Item>` may be abbreviated \n`Key->Item`. An instance of `Entry` may be constructed \nusing the `->` operator:\n\n    String->Person entry = person.name->person;\n"],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if this entry is equal to the given\nentry. Two entries are equal if they have the same\nkey and the same value."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a description of the entry in the form \n`key->item`."],"actual":[]},"$nm":"string"},"item":{"$t":{"$nm":"Item"},"$mt":"attr","$an":{"shared":[],"doc":["The value associated with the key."]},"$nm":"item"},"key":{"$t":{"$nm":"Key"},"$mt":"attr","$an":{"shared":[],"doc":["The key used to access the entry."]},"$nm":"key"}},"$nm":"Entry"},"Variable":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[variable]]."]},"$nm":"Variable"},"ThrownException":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"when"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"See"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"Type"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[]},"$nm":"ThrownException"},"Cloneable":{"of":[{"$nm":"Clone"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Clone"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"variance":"out","$nm":"Clone"}],"$an":{"shared":[],"doc":["Abstract supertype of objects whose value can be \ncloned."]},"$at":{"clone":{"$t":{"$nm":"Clone"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Obtain a clone of this object. For a mutable \nobject, this should return a copy of the object. \nFor an immutable object, it is acceptable to return\nthe object itself."]},"$nm":"clone"}},"$nm":"Cloneable","$st":"Clone"},"Invertable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Inverse"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of types which support a unary additive inversion\noperation. For a numeric type, this should return the \nnegative of the argument value. Note that the type \nparameter of this interface is not restricted to be a \nself type, in order to accommodate the possibility of \ntypes whose additive inverse can only be expressed in terms of \na wider type."],"by":["Gavin"]},"$at":{"positiveValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The value itself, expressed as an instance of the\nwider type."]},"$nm":"positiveValue"},"negativeValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The additive inverse of the value, which may be expressed\nas an instance of a wider type."]},"$nm":"negativeValue"}},"$nm":"Invertable"},"Ordinal":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"see":["Character","Integer","Integral","Range"],"doc":["Abstraction of ordinal types, that is, types with \nsuccessor and predecessor operations, including\n`Integer` and other `Integral` numeric types.\n`Character` is also considered an ordinal type. \n`Ordinal` types may be used to generate a `Range`."],"by":["Gavin"]},"$at":{"predecessor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if this is the minimum value"],"doc":["The predecessor of this value."]},"$nm":"predecessor"},"successor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if this is the maximum value"],"doc":["The successor of this value."]},"$nm":"successor"}},"$nm":"Ordinal","$st":"Other"},"largest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","smallest","max"],"doc":["Given two `Comparable` values, return largest of the\ntwo."]},"$nm":"largest"},"native":{"$t":{"$pk":"ceylon.language","$nm":"Native"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation is \nbe provided by platform-native code."]},"$nm":"native"},"unflatten":{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"flatFunction"}],[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"args"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"unflatten"},"greaterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is greater than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"greaterThan"},"Identifiable":{"$mt":"ifc","$an":{"shared":[],"doc":["The abstract supertype of all types with a well-defined\nnotion of identity. Values of type `Identifiable` may \nbe compared using the `===` operator to determine if \nthey are references to the same object instance. For\nthe sake of convenience, this interface defines a\ndefault implementation of value equality equivalent\nto identity. Of course, subtypes are encouraged to\nrefine this implementation."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Identity equality comparing the identity of the two \nvalues. May be refined by subtypes for which value \nequality is more appropriate. Implementations must\nrespect the constraint that if `x===y` then `x==y` \n(equality is consistent with identity)."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["identityHash"],"doc":["The system-defined identity hash value of the \ninstance. Subtypes which refine `equals()` must \nalso refine `hash`, according to the general \ncontract defined by `Object`."],"actual":[]},"$nm":"hash"}},"$nm":"Identifiable"},"language":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Contains information about the language"],"by":["The Ceylon Team"]},"$at":{"majorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language major version."]},"$nm":"majorVersion"},"majorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The major version of the code generated for the underlying runtime."]},"$nm":"majorVersionBinary"},"minorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language minor version."]},"$nm":"minorVersion"},"versionName":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release name."]},"$nm":"versionName"},"releaseVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release version."]},"$nm":"releaseVersion"},"minorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The minor version of the code generated for the underlying runtime."]},"$nm":"minorVersionBinary"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language version."]},"$nm":"version"}},"$nm":"language"},"Native":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[native]]."]},"$nm":"Native"},"Null":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"of":[{"$pk":"ceylon.language","$nm":"null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["null"],"doc":["The type of the `null` value. Any union type of form \n`Null|T` is considered an optional type, whose values\ninclude `null`. Any type of this form may be written as\n`T?` for convenience.\n\nThe `if (exists ... )` construct, or, alternatively,\nthe `assert (exists ...)` construct, may be used to\nnarrow an optional type to a _definite_ type, that is,\na subtype of `Object`:\n\n    String? firstArg = process.arguments.first;\n    if (exists firstArg) {\n        print(\"hello \" + firstArg);\n    }\n\nThe `else` operator evaluates its second operand if \nand only if its first operand is `null`:\n\n    String name = process.arguments.first else \"world\";\n\nThe `then` operator evaluates its second operand when\nits first operand evaluates to `true`, and to `null` \notherwise:\n\n    Float? diff = x>=y then x-y;\n\n"],"by":["Gavin"]},"$nm":"Null"},"array":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array containing the given elements. If no\nelements are provided, create an empty array of the\ngiven element type."]},"$nm":"array"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable"],"doc":["Sort the given elements, returning a new sequence."]},"$nm":"sort"},"equalTo":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if they're equal.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"equalTo"},"AssertionException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"message"}],"$mt":"cls","$an":{"shared":[],"doc":["An exception that occurs when an assertion fails, that\nis, when a condition in an `assert` statement evaluates\nto false at runtime."]},"$nm":"AssertionException"},"Ranged":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Index"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Index"},{"variance":"out","$nm":"Span"}],"$an":{"shared":[],"see":["List","Sequence","String"],"doc":["Abstract supertype of ranged objects which map a range\nof `Comparable` keys to ranges of values. The type\nparameter `Span` abstracts the type of the resulting\nrange.\n\nA span may be obtained from an instance of `Ranged`\nusing the span operator:\n\n    print(\"hello world\"[0..5])\n"]},"$m":{"spanTo":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe start of the receiver and the end index."]},"$nm":"spanTo"},"segment":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a segment containing the mapped values\nstarting from the given index, with the given \nlength."]},"$nm":"segment"},"spanFrom":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe starting index and the end of the receiver."]},"$nm":"spanFrom"},"span":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between \nthe two given indices."]},"$nm":"span"}},"$nm":"Ranged"},"arrayOfSize":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"size"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array of the specified size, populating every \nindex with the given element. If the specified size is\nsmaller than `1`, return an empty array of the given\nelement type."]},"$nm":"arrayOfSize"},"times":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["plus","product"],"doc":["Multiply the given `Numeric` values."]},"$nm":"times"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Produces a sequence of each index to element `Entry` \nfor the given sequence of values."]},"$nm":"entries"},"license":{"$t":{"$pk":"ceylon.language","$nm":"License"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify the URL of the license of a module \nor package."]},"$nm":"license"},"null":{"super":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"obj","$an":{"shared":[],"doc":["The null value."],"by":["Gavin"]},"$nm":"null"},"Object":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Basic","Null"],"doc":["The abstract supertype of all types representing \ndefinite values. Any two `Object`s may be compared\nfor value equality using the `==` and `!=` operators:\n\n    true==false\n    1==\"hello world\"\n    \"hello\"+ \" \" + \"world\"==\"hello world\"\n    Singleton(\"hello world\")=={ \"hello world\" }\n\nHowever, since `Null` is not a subtype of `Object`, the \nvalue `null` cannot be compared to any other value\nusing `==`. Thus, value equality is not defined for \noptional types. This neatly voids the problem of \ndeciding the value of the expression `null==null`, \nwhich is simply illegal."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determine if two values are equal. Implementations\nshould respect the constraints that:\n\n- if `x===y` then `x==y` (reflexivity), \n- if `x==y` then `y==x` (symmetry), \n- if `x==y` and `y==z` then `x==z` (transitivity).\n\nFurthermore it is recommended that implementations\nensure that if `x==y` then `x` and `y` have the\nsame concrete class."]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The hash value of the value, which allows the value\nto be an element of a hash-based set or key of a\nhash-based map. Implementations must respect the\nconstraint that if `x==y` then `x.hash==y.hash`."]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A developer-friendly string representing the \ninstance. Concatenates the name of the concrete \nclass of the instance with the `hash` of the \ninstance. Subclasses are encouraged to refine this \nimplementation to produce a more meaningful \nrepresentation."]},"$nm":"string"}},"$nm":"Object"},"LazySet":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of Set that wraps an `Iterable` of\nelements. All operations on this Set are performed\non the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"complement"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"intersection"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"exclusiveUnion"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"union"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"LazySet"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazySet"},"Float":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["An IEEE 754 64-bit [floating point number][]. A `Float` \nis capable of approximately representing numeric values \nbetween 2<sup>-1022<\/sup> and \n(2-2<sup>-52<\/sup>)×2<sup>1023<\/sup>, along with \nthe special values `infinity` and `-infinity`, and \nundefined values (Not a Number). Zero is represented by \ndistinct instances `+0`, `-0`, but these instances are \nequal. An undefined value is not equal to any other\nvalue, not even to itself.\n\n[floating point number]: http:\/\/www.validlab.com\/goldberg\/paper.pdf"]},"$at":{"strictlyNegative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a negative number, `-0`, \nor `-infinity`. Produces `false` for a positive \nnumber, `+0`, or undefined."]},"$nm":"strictlyNegative"},"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this value. Produces `1` for a positive \nnumber or `infinity`. Produces `-1` for a negative\nnumber or `-infinity`. Produces `0` for `+0`, `-0`, \nor undefined."],"actual":[]},"$nm":"sign"},"infinite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinity","finite"],"doc":["Determines whether this value is infinite in \nmagnitude. Produces `true` for `infinity` and \n`-infinity`. Produces `false` for a finite number, \n`+0`, `-0`, or undefined."]},"$nm":"infinite"},"undefined":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines whether this value is undefined (that is, \nNot a Number or NaN). The undefined value has the \nproperty that it is not equal (`==`) to itself, as \na consequence the undefined value cannot sensibly \nbe used in most collections."]},"$nm":"undefined"},"strictlyPositive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a positive number, `+0`, \nor `infinity`. Produces `false` for a negative \nnumber, `-0`, or undefined."]},"$nm":"strictlyPositive"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a negative number or\n`-infinity`. Produces `false` for a positive number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a positive number or\n`infinity`. Produces `false` for a negative number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"positive"},"finite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinite","infinity"],"doc":["Determines whether this value is finite. Produces\n`false` for `infinity`, `-infinity`, and undefined."]},"$nm":"finite"}},"$nm":"Float"},"min":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","max","smallest"],"doc":["Given a nonempty sequence of `Comparable` values, \nreturn the smallest value in the sequence."]},"$nm":"min"},"deprecated":{"$t":{"$pk":"ceylon.language","$nm":"Deprecation"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"reason"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark program elements which should not be \nused anymore."]},"$nm":"deprecated"},"Collection":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["List","Map","Set"],"doc":["Represents an iterable collection of elements of finite \nsize. `Collection` is the abstract supertype of `List`,\n`Map`, and `Set`.\n\nA `Collection` forms a `Category` of its elements.\n\nAll `Collection`s are `Cloneable`. If a collection is\nimmutable, it is acceptable that `clone` produce a\nreference to the collection itself. If a collection is\nmutable, `clone` should produce an immutable collection\ncontaining references to the same elements, with the\nsame structure as the original collection&mdash;that \nis, it should produce an immutable shallow copy of the\ncollection."]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if the given object is an element of\nthis collection. In this default implementation,\nand in most refining implementations, return `false`\notherwise. An acceptable refining implementation\nmay return `true` for objects which are not \nelements of the collection, but this is not \nrecommended. (For example, the `contains()` method \nof `String` returns `true` for any substring of the\nstring.)"],"actual":[]},"$nm":"contains"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determine if the collection is empty, that is, if \nit has no elements."],"actual":[]},"$nm":"empty"}},"$nm":"Collection"},"Range":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"last"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Represents the range of totally ordered, ordinal values \ngenerated by two endpoints of type `Ordinal` and \n`Comparable`. If the first value is smaller than the\nlast value, the range is increasing. If the first value\nis larger than the last value, the range is decreasing.\nIf the two values are equal, the range contains exactly\none element. The range is always nonempty, containing \nat least one value.\n\nA range may be produced using the `..` operator:\n\n    for (i in min..max) { ... }\n    if (char in `A`..`Z`) { ... }\n"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["An iterator for the elements of the range."],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"n"}]],"$mt":"mthd","$an":{"shared":[],"doc":["The element of the range that occurs `n` values after\nthe start of the range. Note that this operation \nis inefficient for large ranges."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if two ranges are the same by comparing\ntheir endpoints."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"next":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$nm":"next"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given object."],"actual":[]},"$nm":"contains"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given value."]},"$nm":"includes"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"span":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the end of the range."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since ranges are \nimmutable."],"actual":[]},"$nm":"clone"},"decreasing":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines if the range is decreasing."]},"$nm":"decreasing"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The end of the range."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this range."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since a Range cannot\ncontain nulls."],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Reverse this range, returning a new range."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"doc":["The rest of the range, without the start of the\nrange."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The start of the range."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The nonzero number of elements in the range."],"actual":[]},"$nm":"size"}},"$nm":"Range"},"max":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","min","largest"],"doc":["Given a nonempty sequence of `Comparable` values, \nreturn the largest value in the sequence."]},"$nm":"max"},"Integral":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Integral"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of integral numeric types. That is, types \nwith no fractional part, including `Integer`. The \ndivision operation for integral numeric types results \nin a remainder. Therefore, integral numeric types have \nan operation to determine the remainder of any division \noperation."],"by":["Gavin"]},"$m":{"remainder":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["divided"],"doc":["The remainder, after dividing this number by the \ngiven number."]},"$nm":"remainder"}},"$at":{"unit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is one."]},"$nm":"unit"},"zero":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is zero."]},"$nm":"zero"}},"$nm":"Integral","$st":"Other"},"SequenceAppender":{"super":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder"],"doc":["This class is used for constructing a new nonempty \nsequence by incrementally appending elements to an\nexisting nonempty sequence. The existing sequence is\nnot modified, since `Sequence`s are immutable. This \nclass is mutable but threadsafe."]},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting nonempty sequence. If no elements \nhave been appended, the original nonempty \nsequence."],"actual":[]},"$nm":"sequence"}},"$nm":"SequenceAppender"},"smallest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","largest","min"],"doc":["Given two `Comparable` values, return smallest of the\ntwo."]},"$nm":"smallest"},"byIncreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byDecreasing"],"doc":["A comparator which orders elements in increasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byIncreasing"},"larger":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is larger than the given value."],"by":["Gavin"]},"$nm":"larger"},"true":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing truth in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"true"},"join":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"iterables"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["Given a list of iterable objects, return a new sequence \nof all elements of the all given objects. If there are\nno arguments, or if none of the arguments contains any\nelements, return the empty sequence."]},"$nm":"join"},"Exponentiable":{"of":[{"$nm":"This"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"},{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$nm":"This"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types that may be raised to a\npower. Note that the type of the exponent may be\ndifferent to the numeric type which can be \nexponentiated."]},"$m":{"power":{"$t":{"$nm":"This"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of raising this number to the given\npower."]},"$nm":"power"}},"$nm":"Exponentiable","$st":"This"},"curry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}],[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"curry"},"Character":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["String"],"doc":["A 32-bit Unicode character."],"by":["Gavin"]},"$at":{"digit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a numeric digit."]},"$nm":"digit"},"uppercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is an uppercase representation of\nthe character."]},"$nm":"uppercase"},"control":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is an ISO control \ncharacter."]},"$nm":"control"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The code point of the character."]},"$nm":"integer"},"letter":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a letter."]},"$nm":"letter"},"lowercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is a lowercase representation of\nthe character."]},"$nm":"lowercase"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The lowercase representation of this character."]},"$nm":"lowercased"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The uppercase representation of this character."]},"$nm":"uppercased"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["A string containg just this character."],"actual":[]},"$nm":"string"},"whitespace":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a whitespace \ncharacter. A whitespace character is:\n* U+0009 HORIZONTAL TABULATION (`\\t`),\n* U+000A LINE FEED (`\\n`),\n* U+000B VERTICAL TABULATION,\n* U+000C FORM FEED (`\\f`),\n* U+000D CARRIAGE RETURN (`\\r`),\n* U+001C FILE SEPARATOR,\n* U+001D GROUP SEPARATOR,\n* U+001E RECORD SEPARATOR,\n* U+001F UNIT SEPARATOR or\n* any Unicode character in the general category *Zs* (space separator), \n  *Zl* (line separator) or *Zp* (paragraph separator)\n  that is not also a non-breaking space.\n"]},"$nm":"whitespace"},"titlecased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The title case representation of this character."]},"$nm":"titlecased"},"titlecase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is a title case representation of\nthe character."]},"$nm":"titlecase"}},"$nm":"Character"},"Keys":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"prm","$pt":"v","$nm":"correspondence"}],"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"contains"}},"$nm":"Keys"},"forKey":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forItem"],"doc":["A function that returns the result of the given `resulting()` function \non the key of a given `Entry`."]},"$nm":"forKey"},"product":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["sum"],"doc":["Given a nonempty sequence of `Numeric` values, return \nthe product of the values."]},"$nm":"product"},"process":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Represents the current process (instance of the virtual\nmachine)."],"by":["Gavin","Tako"]},"$m":{"readLine":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Read a line of input text from the standard input \nof the virtual machine process."]},"$nm":"readLine"},"writeErrorLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print a line to the standard error of the \nvirtual machine process."]},"$nm":"writeErrorLine"},"writeError":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard error of the \nvirtual machine process."]},"$nm":"writeError"},"propertyValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the given system property of the virtual\nmachine, if any."]},"$nm":"propertyValue"},"namedArgumentValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the first argument of form `-name=value`, \n`--name=value`, or `-name value` specified among the \ncommand line arguments to the virtual machine, if\nany."]},"$nm":"namedArgumentValue"},"write":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard output of the \nvirtual machine process."]},"$nm":"write"},"namedArgumentPresent":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determine if an argument of form `-name` or `--name` \nwas specified among the command line arguments to \nthe virtual machine."]},"$nm":"namedArgumentPresent"},"writeLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["print"],"doc":["Print a line to the standard output of the \nvirtual machine process."]},"$nm":"writeLine"},"exit":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"code"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"exit"}},"$at":{"os":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the operating system this \nprocess is running on."]},"$nm":"os"},"vmVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the virtual machine this \nprocess is running on."]},"$nm":"vmVersion"},"vm":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the virtual machine this \nprocess is running on."]},"$nm":"vm"},"osVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the operating system this \nprocess is running on."]},"$nm":"osVersion"},"newline":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The line ending character sequence on this platform."]},"$nm":"newline"},"arguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The command line arguments to the virtual machine."]},"$nm":"arguments"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"nanoseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in nanoseconds since an arbitrary\nstarting point."]},"$nm":"nanoseconds"},"milliseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in milliseconds since midnight, \n1 January 1970."]},"$nm":"milliseconds"}},"$nm":"process"},"forItem":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forKey"],"doc":["A function that returns the result of the given `resulting()` function \non the item of a given `Entry`."]},"$nm":"forItem"},"License":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[license]]."]},"$nm":"License"},"shuffle":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"FirstArgs"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"SecondArgs"}],"$an":{"shared":[]},"$nm":"shuffle"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Create a new string containing the given characters."]},"$nm":"string"},"Annotation":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[annotation]]."]},"$nm":"Annotation"},"doc":{"$t":{"$pk":"ceylon.language","$nm":"Doc"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API documentation of a program\nelement."]},"$nm":"doc"},"nothing":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"doc":["A value that is assignable to any type, but that \nresults in an exception when evaluated. This is most \nuseful for generating members in an IDE."]},"$nm":"nothing"},"Scalar":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$pk":"ceylon.language","$nm":"Number"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Scalar"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types representing scalar\nvalues, including `Integer` and `Float`."],"by":["Gavin"]},"$at":{"magnitude":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of this number."],"actual":[]},"$nm":"magnitude"},"wholePart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part. For integral numeric types,\nthe integral value of a number is the number \nitself."],"actual":[]},"$nm":"wholePart"},"fractionalPart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part. For integral numeric types,\nthe fractional part is always zero."],"actual":[]},"$nm":"fractionalPart"}},"$nm":"Scalar","$st":"Other"},"tagged":{"$t":{"$pk":"ceylon.language","$nm":"Tags"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to categorize the API by tag."]},"$nm":"tagged"},"SequenceBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceAppender","join","Singleton"],"doc":["Since sequences are immutable, this class is used for\nconstructing a new sequence by incrementally appending \nelements to the empty sequence. This class is mutable\nbut threadsafe."]},"$m":{"append":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append an element to the sequence and return this \n`SequenceBuilder`"]},"$nm":"append"},"appendAll":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Append multiple elements to the sequence and return \nthis `SequenceBuilder`"]},"$nm":"appendAll"}},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"native":[],"doc":["The resulting sequence. If no elements have been\nappended, the empty sequence."]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determine if the resulting sequence is empty."]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The size of the resulting sequence."]},"$nm":"size"}},"$nm":"SequenceBuilder"},"ifExists":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"prm","$pt":"f","$nm":"predicate"}],[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$nm":"ifExists"},"variable":{"$t":{"$pk":"ceylon.language","$nm":"Variable"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark an value as variable. A `variable` \nvalue must be assigned multiple times."]},"$nm":"variable"},"Abstract":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[abstract]]."]},"$nm":"Abstract"},"Correspondence":{"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Map","List","Category"],"doc":["Abstract supertype of objects which associate values \nwith keys. `Correspondence` does not satisfy `Category`,\nsince in some cases&mdash;`List`, for example&mdash;it is \nconvenient to consider the subtype a `Category` of its\nvalues, and in other cases&mdash;`Map`, for example&mdash;it \nis convenient to treat the subtype as a `Category` of its\nentries.\n\nThe item corresponding to a given key may be obtained \nfrom a `Correspondence` using the item operator:\n\n    value bg = settings[\"backgroundColor\"] else white;\n\nThe `get()` operation and item operator result in an\noptional type, to reflect the possibility that there is\nno item for the given key."],"by":["Gavin"]},"$m":{"definesAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["Determines if this `Correspondence` defines a value\nfor any one of the given keys."]},"$nm":"definesAny"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["definesAny","definesEvery","keys"],"doc":["Determines if there is a value defined for the \ngiven key."]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["items"],"doc":["Returns the value defined for the given key, or \n`null` if there is no value defined for the given \nkey."]},"$nm":"get"},"items":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["get"],"doc":["Returns the items defined for the given keys, in\nthe same order as the corresponding keys."]},"$nm":"items"},"definesEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["Determines if this `Correspondence` defines a value\nfor every one of the given keys."]},"$nm":"definesEvery"}},"$at":{"keys":{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["defines"],"doc":["The `Category` of all keys for which a value is \ndefined by this `Correspondence`."]},"$nm":"keys"}},"$nm":"Correspondence"},"NonemptyContainer":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["A nonempty container."]},"$alias":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"NonemptyContainer"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A count of the number of `true` items in the given values."]},"$nm":"count"},"byItem":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"see":["byKey"],"doc":["A comparator for `Entry`s which compares their items \naccording to the given `comparing()` function."]},"$nm":"byItem"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Authors"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API authors."]},"$nm":"by"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["any"],"doc":["Determines if every one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"every"},"$pkg-shared":"1","Tuple":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"First","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Rest","$hdn":"1","$mt":"prm","$pt":"v","$nm":"rest"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$nm":"Element"}],"variance":"out","$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language","$nm":"Empty"},"variance":"out","$nm":"Rest"}],"$an":{"shared":[],"doc":["A _tuple_ is a typed linked list. Each instance of \n`Tuple` represents the value and type of a single link.\nThe attributes `first` and `rest` allow us to retrieve\na value form the list without losing its static type \ninformation.\n\n    value point = Tuple(0.0, Tuple(0.0, Tuple(\"origin\")));\n    Float x = point.first;\n    Float y = point.rest.first;\n    String label = point.rest.rest.first;\n\nUsually, we abbreviate code involving tuples.\n\n    [Float,Float,String] point = [0.0, 0.0, \"origin\"];\n    Float x = point[0];\n    Float y = point[1];\n    String label = point[2];\n\nA list of types enclosed in brackets is an abbreviated \ntuple type. An instance of `Tuple` may be constructed \nby surrounding a value list in brackets:\n\n    [String,String] words = [\"hello\", \"world\"];\n\nThe index operator with a literal integer argument is a \nshortcut for a chain of evaluations of `rest` and \n`first`. For example, `point[1]` means `point.rest.first`.\n\nA _terminated_ tuple type is a tuple where the type of\nthe last link in the chain is `Empty`. An _unterminated_ \ntuple type is a tuple where the type of the last link\nin the chain is `Sequence` or `Sequential`. Thus, a \nterminated tuple type has a length that is known\nstatically. For an unterminated tuple type only a lower\nbound on its length is known statically.\n\nHere, `point` is an unterminated tuple:\n\n    String[] labels = ... ;\n    [Float,Float,String*] point = [0.0, 0.0, *labels];\n    Float x = point[0];\n    Float y = point[1];\n    String? firstLabel = point[2];\n    String[] allLabels = point[2...];\n\n"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"end"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$nm":"Rest"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"First"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"Tuple"},"Final":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[final]]."]},"$nm":"Final"},"lessThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is less than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"lessThan"},"identityHash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["identical"],"doc":["Return the system-defined identity hash value of the \ngiven value. This hash value is consistent with \nidentity equality."]},"$nm":"identityHash"},"uncurry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"prm","$pt":"f","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"uncurry"},"optional":{"$t":{"$pk":"ceylon.language","$nm":"Optional"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify that a module can be executed \neven if the annotated dependency is not available."]},"$nm":"optional"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["every"],"doc":["Determines if any one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"any"},"Summable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Other"}],"$an":{"shared":[],"see":["String","Numeric"],"doc":["Abstraction of types which support a binary addition\noperator. For numeric types, this is just familiar \nnumeric addition. For strings, it is string \nconcatenation. In general, the addition operation \nshould be a binary associative operation."],"by":["Gavin"]},"$m":{"plus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of adding the given value to this value. \nThis operation should never perform any kind of \nmutation upon either the receiving value or the \nargument value."]},"$nm":"plus"}},"$nm":"Summable","$st":"Other"},"EmptyContainer":{"$mt":"ifc","$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["An empty container."]},"$alias":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"EmptyContainer"},"Set":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A collection of unique elements.\n\nA `Set` is a `Collection` of its elements.\n\nSets may be the subject of the binary union, \nintersection, exclusive union, and complement operators \n`|`, `&`, `^`, and `~`."]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements in \nthis `Set` that are not contained in the given\n`Set`."]},"$nm":"complement"},"subset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a subset of the given \n`Set`, that is, if the given set contains all of \nthe elements in this set."]},"$nm":"subset"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \nthat are present in both this `Set` and the given \n`Set`."]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \ncontained in either this `Set` or the given `Set`, \nbut no element contained in both sets."]},"$nm":"exclusiveUnion"},"superset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a superset of the \nspecified Set, that is, if this `Set` contains all \nof the elements in the specified `Set`."]},"$nm":"superset"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Set`s are considered equal if they have the \nsame size and if every element of the first set is\nalso an element of the second set, as determined\nby `contains()`."],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements of \nthis `Set` and all the elements of the given `Set`."]},"$nm":"union"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Set"},"Sequence":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Empty"],"doc":["A nonempty, immutable sequence of values. The type \n`Sequence<Element>`, may be abbreviated `[Element+]`.\n\nGiven a possibly-empty sequence of type `[Element*]`, \nthe `if (nonempty ...)` construct, or, alternatively,\nthe `assert (nonempty ...)` construct, may be used to \nnarrow to a nonempty sequence type:\n\n    [Integer*] nums = ... ;\n    if (nonempty nums) {\n        Integer first = nums.first;\n        Integer max = max(nums);\n        [Integer+] squares = nums.collect((Integer i) i**2));\n        [Integer+] sorted = nums.sort(byIncreasing((Integer i) i));\n    }\n\nOperations like `first`, `max()`, `collect()`, and \n`sort()`, which polymorphically produce a nonempty or \nnon-null output when given a nonempty input are called \n_emptiness-preserving_."],"by":["Gavin"]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements."],"actual":[]},"$nm":"sort"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the results of \napplying the given mapping to the elements of this\nsequence."],"actual":[]},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["The index of the last element of the sequence."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element of the sequence, that is, the\nelement with index `sequence.lastIndex`."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `false`, since every `Sequence` contains at\nleast one element."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \nelement."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new nonempty\nsequence."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element of the sequence, that is, the\nelement with index `0`."],"actual":[]},"$nm":"first"}},"$nm":"Sequence"},"InitializationException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}],"$mt":"cls","$an":{"shared":[],"see":["late"],"doc":["Thrown when a problem was detected with value initialization.\n\nPossible problems include:\n\n* when a value could not be initialized due to recursive access during initialization, \n* an attempt to use a `late` value before it was initialized, \n* an attempt to assign to a `late` but non-`variable` value after it was initialized."]},"$nm":"InitializationException"},"StringBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"shared":[],"native":[],"doc":["Since strings are immutable, this class is used for\nconstructing a string by incrementally appending \ncharacters to the empty string. This class is mutable \nbut threadsafe."]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append the characters in the given string."]},"$nm":"append"},"appendSpace":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a space character."]},"$nm":"appendSpace"},"delete":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Deletes the specified number of characters from the\ncurrent content, starting at the specified position.\nIf the position is beyond the end of the current \ncontent, nothing is deleted. If the number of \ncharacters to delete is greater than the available \ncharacters from the given position, the content is \ntruncated at the given position."]},"$nm":"delete"},"reset":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Remove all content and return to initial state."]},"$nm":"reset"},"insertCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a `Character` at the specified position. If \nthe position is beyond the end of the current string, \nthe new content is simply appended to the current \ncontent. If the position is a negative number, the \nnew content is inserted at index 0."]},"$nm":"insertCharacter"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the characters in the given strings."]},"$nm":"appendAll"},"insert":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a `String` at the specified position. If the \nposition is beyond the end of the current string, \nthe new content is simply appended to the current \ncontent. If the position is a negative number, the \nnew content is inserted at index 0."]},"$nm":"insert"},"appendNewline":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a newline character."]},"$nm":"appendNewline"},"appendCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the given character."]},"$nm":"appendCharacter"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting string. If no characters have been\nappended, the empty string."],"actual":[]},"$nm":"string"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the size of the current content."]},"$nm":"size"}},"$nm":"StringBuilder"},"emptyOrSingleton":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Singleton","Empty"],"doc":["A `Singleton` if the given element is non-null, otherwise `Empty`."]},"$nm":"emptyOrSingleton"},"shared":{"$t":{"$pk":"ceylon.language","$nm":"Shared"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a type or member as shared. A `shared` \nmember is visible outside the block of code in which it\nis declared."]},"$nm":"shared"},"Actual":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[actual]]."]},"$nm":"Actual"},"Binary":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Binary"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of numeric types that consist in\na sequence of bits, like `Integer`."],"by":["Stef"]},"$m":{"clear":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Returns a new number with the given bit set to 0.\nBits are indexed from right to left."]},"$nm":"clear"},"xor":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical exclusive OR operation."]},"$nm":"xor"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Retrieves a given bit from this bit sequence. Bits are indexed from\nright to left."]},"$nm":"get"},"leftLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a left logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"leftLogicalShift"},"set":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"bit"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit set to the given value.\nBits are indexed from right to left."]},"$nm":"set"},"or":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical inclusive OR operation."]},"$nm":"or"},"rightArithmeticShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right arithmetic shift. Sign is preserved. Padded with zeros."]},"$nm":"rightArithmeticShift"},"rightLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"rightLogicalShift"},"flip":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit flipped to its opposite value.\nBits are indexed from right to left."]},"$nm":"flip"},"and":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical AND operation."]},"$nm":"and"}},"$at":{"not":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The binary complement of this sequence of bits."]},"$nm":"not"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The number of bits (0 or 1) that this sequence of bits can hold."]},"$nm":"size"}},"$nm":"Binary","$st":"Other"},"commaList":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$nm":"commaList"},"LazyMap":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"entries"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A Map implementation that wraps an `Iterable` of \nentries. All operations, such as lookups, size, etc. \nare performed on the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"LazyMap"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazyMap"},"Map":{"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Entry","forKey","forItem","byItem","byKey"],"doc":["Represents a collection which maps _keys_ to _items_,\nwhere a key can map to at most one item. Each such \nmapping may be represented by an `Entry`.\n\nA `Map` is a `Collection` of its `Entry`s, and a \n`Correspondence` from keys to items.\n\nThe presence of an entry in a map may be tested\nusing the `in` operator:\n\n    if (\"lang\"->\"en_AU\" in settings) { ... }\n\nThe entries of the map may be iterated using `for`:\n\n    for (key->item in settings) { ... }\n\nThe item for a key may be obtained using the item\noperator:\n\n    String lang = settings[\"lang\"] else \"en_US\";"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Map`s are considered equal iff they have the \nsame _entry sets_. The entry set of a `Map` is the\nset of `Entry`s belonging to the map. Therefore, the\nmaps are equal iff they have same set of `keys`, and \nfor every key in the key set, the maps have equal\nitems."],"actual":[]},"$nm":"equals"},"mapItems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"mapping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["Returns a `Map` with the same keys as this map. For\nevery key, the item is the result of applying the\ngiven transformation function."]},"$nm":"mapItems"}},"$at":{"values":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Collection"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns all the values stored in this `Map`. An \nelement can be stored under more than one key in \nthe map, and so it can be contained more than once \nin the resulting collection."]},"$nm":"values"},"inverse":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns a `Map` in which every key is an `Item` in \nthis map, and every value is the set of keys that \nstored the `Item` in this map."]},"$nm":"inverse"},"keys":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the set of keys contained in this `Map`."],"actual":[]},"$nm":"keys"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Map"},"Number":{"$mt":"ifc","$an":{"shared":[],"see":["Numeric"],"doc":["Abstraction of numbers. Numeric operations are provided\nby the subtype `Numeric`. This type defines operations\nwhich can be expressed without reference to the self\ntype `Other` of `Numeric`."],"by":["Gavin"]},"$at":{"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this number. Returns `1` if the number \nis positive, `-1` if it is negative, or `0` if it \nis zero."]},"$nm":"sign"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas an `Integer`"],"doc":["The number, represented as an `Integer`, after \ntruncation of any fractional part."]},"$nm":"integer"},"magnitude":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of the number."]},"$nm":"magnitude"},"wholePart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part."]},"$nm":"wholePart"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is negative."]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is positive."]},"$nm":"positive"},"fractionalPart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part."]},"$nm":"fractionalPart"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas a `Float`"],"doc":["The number, represented as a `Float`."]},"$nm":"float"}},"$nm":"Number"},"throws":{"$t":{"$pk":"ceylon.language","$nm":"ThrownException"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"when"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a program element that throws an \nexception."]},"$nm":"throws"},"Numeric":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Invertable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float","Comparable"],"doc":["Abstraction of numeric types supporting addition,\nsubtraction, multiplication, and division, including\n`Integer` and `Float`. Additionally, a numeric type \nis expected to define a total order via an \nimplementation of `Comparable`."],"by":["Gavin"]},"$m":{"minus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The difference between this number and the given \nnumber."]},"$nm":"minus"},"times":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The product of this number and the given number."]},"$nm":"times"},"divided":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Integral"],"doc":["The quotient obtained by dividing this number by \nthe given number. For integral numeric types, this \noperation results in a remainder."]},"$nm":"divided"}},"$nm":"Numeric","$st":"Other"},"Closeable":{"$mt":"ifc","$an":{"shared":[],"doc":["Abstract supertype of types which may appear\nas the expression type of a resource expression\nin a `try` statement."]},"$m":{"open":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called before entry to a `try` block."]},"$nm":"open"},"close":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called after completion of a `try` block."]},"$nm":"close"}},"$nm":"Closeable"},"Tags":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[tagged]]."]},"$nm":"Tags"},"byDecreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byIncreasing"],"doc":["A comparator which orders elements in decreasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byDecreasing"},"formal":{"$t":{"$pk":"ceylon.language","$nm":"Formal"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation must \nbe provided by subtypes."]},"$nm":"formal"},"default":{"$t":{"$pk":"ceylon.language","$nm":"Default"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member whose implementation may be \nrefined by subtypes. Non-`default` declarations may not \nbe refined."]},"$nm":"default"},"String":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"native":[],"see":["string"],"doc":["A string of characters. Each character in the string is \na 32-bit Unicode character. The internal UTF-16 \nencoding is hidden from clients.\n\nA string is a `Category` of its `Character`s, and of\nits substrings:\n\n    'w' in greeting \n    \"hello\" in greeting\n\nStrings are summable:\n\n    String greeting = \"hello\" + \" \" + \"world\";\n\nThey are efficiently iterable:\n\n    for (char in \"hello world\") { ... }\n\nThey are `List`s of `Character`s:\n\n    value char = \"hello world\"[5];\n\nThey are ranged:\n\n    String who = \"hello world\"[6...];\n\nNote that since `string[index]` evaluates to the \noptional type `Character?`, it is often more convenient\nto write `string[index..index]`, which evaluates to a\n`String` containing a single character, or to the empty\nstring `\"\"` if `index` refers to a position outside the\nstring.\n\nThe `string()` function makes it possible to use \ncomprehensions to transform strings:\n\n    string(for (s in \"hello world\") if (s.letter) s.uppercased)\n\nSince a `String` has an underlying UTF-16 encoding, \ncertain operations are expensive, requiring iteration\nof the characters of the string. In particular, `size`\nrequires iteration of the whole string, and `get()`,\n`span()`, and `segment()` require iteration from the \nbeginning of the string to the given index."],"by":["Gavin"]},"$m":{"firstCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The first index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string."]},"$nm":"firstCharacterOccurrence"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the characters of this string beginning at \nthe given index, returning a string no longer than \nthe given length. If the portion of this string\nstarting at the given index is shorter than \nthe given length, return the portion of this string\nfrom the given index until the end of this string. \nOtherwise return a string of the given length. If \nthe start index is larger than the last index of the \nstring, return the empty string."],"actual":[]},"$nm":"segment"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string is longer than the given\nlength. This is a more efficient operation than\n`string.size>length`."]},"$nm":"longerThan"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if the given object is a `String` and, \nif so, if it occurs as a substring of this string,\nor if the object is a `Character` that occurs in\nthis string. That is to say, a string is considered \na `Category` of its substrings and of its \ncharacters."],"actual":[]},"$nm":"contains"},"repeat":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a string formed by repeating this string\nthe given number of times."]},"$nm":"repeat"},"firstOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The first index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string."]},"$nm":"firstOccurrence"},"terminal":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the last characters of the string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."]},"$nm":"terminal"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"initial":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the first characters of this string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."]},"$nm":"initial"},"occurrences":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The character indexes at which the given substring\noccurs within this string. Occurrences do not \noverlap."]},"$nm":"occurrences"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"trimTrailingCharacters":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["This string, after discarding the given \ncharacters from the end of the string"]},"$nm":"trimTrailingCharacters"},"plus":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the concatenation of this string with the\ngiven string."],"actual":[]},"$nm":"plus"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["An iterator for the characters of the string."],"actual":[]},"$nm":"iterator"},"startsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if this string starts with the given \nsubstring."]},"$nm":"startsWith"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the character at the given index in the \nstring, or `null` if the index is past the end of\nstring. The first character in the string occurs at\nindex zero. The last character in the string occurs\nat index `string.size-1`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if the given object is a string, and if\nso, if this string has the same length, and the \nsame characters, in the same order, as the given \nstring."],"actual":[]},"$nm":"equals"},"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Compare this string with the given string \nlexicographically, according to the Unicode values\nof the characters."],"actual":[]},"$nm":"compare"},"trimLeadingCharacters":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["This string, after discarding the given \ncharacters from the beginning of the string"]},"$nm":"trimLeadingCharacters"},"trimCharacters":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["This string, after discarding the given \ncharacters from the beginning and end \nof the string"]},"$nm":"trimCharacters"},"lastCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The last index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string."]},"$nm":"lastCharacterOccurrence"},"join":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Join the given strings, using this string as a \nseparator."]},"$nm":"join"},"replace":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"replacement"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a string formed by replacing every \noccurrence in this string of the given substring\nwith the given replacement string, working from \nthe start of this string to the end."]},"$nm":"replace"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string is shorter than the given\nlength. This is a more efficient operation than\n`string.size>length`."]},"$nm":"shorterThan"},"lastOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The last index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string."]},"$nm":"lastOccurrence"},"split":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"separator"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"discardSeparators"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"groupSeparators"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Split the string into tokens, using the given\npredicate to determine which characters are \nseparator characters."]},"$nm":"split"},"endsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if this string ends with the given \nsubstring."]},"$nm":"endsWith"},"span":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the characters between the given indexes.\nIf the start index is the same as the end index,\nreturn a string with a single character.\nIf the start index is larger than the end index, \nreturn the characters in the reverse order from\nthe order in which they appear in this string.\nIf both the start index and the end index are \nlarger than the last index in the string, return \nthe empty string. Otherwise, if the last index is \nlarger than the last index in the sequence, return\nall characters from the start index to last \ncharacter of the string."],"actual":[]},"$nm":"span"}},"$at":{"normalized":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, after collapsing strings of \n[[whitespace|Character.whitespace]]\ninto single space characters and discarding whitespace \nfrom the beginning and end of the string."]},"$nm":"normalized"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with all characters in lowercase."]},"$nm":"lowercased"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"hash"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with all characters in uppercase."]},"$nm":"uppercased"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this string."],"actual":[]},"$nm":"coalesced"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["longerThan","shorterThan"],"doc":["The length of the string (the number of characters\nit contains). In the case of the empty string, the\nstring has length zero. Note that this operation is\npotentially costly for long strings, since the\nunderlying representation of the characters uses a\nUTF-16 encoding."],"actual":[]},"$nm":"size"},"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the last character in the string, or\n`null` if the string has no characters. Note that \nthis operation is potentially costly for long \nstrings, since the underlying representation of the \ncharacters uses a UTF-16 encoding."],"actual":[]},"$nm":"lastIndex"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the string itself."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string has no characters, that\nis, if it has zero `size`. This is a more efficient \noperation than `string.size==0`."],"actual":[]},"$nm":"empty"},"lines":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Split the string into lines of text."]},"$nm":"lines"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the string, without the first element."],"actual":[]},"$nm":"rest"},"trimmed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, after discarding \n[[whitespace|Character.whitespace]] from the \nbeginning and end of the string."]},"$nm":"trimmed"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with the characters in reverse order."],"actual":[]},"$nm":"reversed"},"characters":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The characters in this string."]},"$nm":"characters"}},"$nm":"String"},"late":{"$t":{"$pk":"ceylon.language","$nm":"Late"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to disable definite initialization analysis\nfor a reference."]},"$nm":"late"},"identical":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$an":{"shared":[],"see":["identityHash"],"doc":["Determine if the arguments are identical. Equivalent to\n`x===y`. Only instances of `Identifiable` have \nwell-defined identity."]},"$nm":"identical"},"emptyIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$an":{"shared":[],"doc":["An iterator that returns no elements."]},"$nm":"emptyIterator"},"integerRangeByIterable":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"prm","$pt":"v","$nm":"range"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"native":[],"doc":["Provides an optimized implementation of `Range<Integer>.iterator`. \nThis is necessary because we need reified generics in order to write \nthe optimized version in pure Ceylon."]},"$nm":"integerRangeByIterable"},"Anything":{"abstract":"1","of":[{"$pk":"ceylon.language","$nm":"Object"},{"$pk":"ceylon.language","$nm":"Null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The abstract supertype of all types. A value of type \n`Anything` may be a definite value of type `Object`, or \nit may be the `null` value. A method declared `void` is \nconsidered to have the return type `Anything`.\n\nNote that the type `Nothing`, representing the \nintersection of all types, is a subtype of all types."],"by":["Gavin"]},"$nm":"Anything"},"parseFloat":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Float"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Float` value of the given string representation of \na decimal number or `null` if the string does not \nrepresent a decimal number.\n\nThe syntax accepted by this method is the same as the \nsyntax for a `Float` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`)."]},"$nm":"parseFloat"},"Optional":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[optional]]."]},"$nm":"Optional"},"Exception":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"description"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"cause"}],"$mt":"cls","$an":{"shared":[],"native":[],"doc":["The supertype of all exceptions. A subclass represents\na more specific kind of problem, and may define \nadditional attributes which propagate information about\nproblems of that kind."],"by":["Gavin","Tom"]},"$m":{"printStackTrace":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print the stack trace to the standard error of\nthe virtual machine process."]},"$nm":"printStackTrace"}},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["description","cause"],"doc":["A message describing the problem. This default \nimplementation returns the description, if any, or \notherwise the message of the cause, if any."]},"$nm":"message"},"cause":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"attr","$an":{"shared":[],"doc":["The underlying cause of this exception."]},"$nm":"cause"},"description":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"attr","$an":{"doc":["A description of the problem."]},"$nm":"description"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"}},"$nm":"Exception"},"Doc":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}],"$mt":"cls","$an":{"annotation":[],"shared":[]},"$nm":"Doc"},"internalSort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"native":[]},"$nm":"internalSort"},"Late":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[late]]."]},"$nm":"Late"},"OverflowException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a mathematical operation caused a number to overflow from its bounds."]},"$nm":"OverflowException"},"parseInteger":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Integer` value of the given string representation \nof an integer, or `null` if the string does not represent \nan integer or if the mathematical integer it represents \nis too large in magnitude to be represented by an \n`Integer`.\n\nThe syntax accepted by this method is the same as the \nsyntax for an `Integer` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`)."]},"$nm":"parseInteger"},"sum":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["product"],"doc":["Given a nonempty sequence of `Summable` values, return \nthe sum of the values."]},"$nm":"sum"},"Authors":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[by]]."]},"$nm":"Authors"},"infinity":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"doc":["An instance of `Float` representing positive infinity \n∞."]},"$nm":"infinity"},"smaller":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is smaller than the given value."],"by":["Gavin"]},"$nm":"smaller"},"flatten":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"Return","$ps":[[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"tuple"}]],"$mt":"prm","$pt":"f","$nm":"tupleFunction"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"flatten"},"Iterable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"see":["Collection"],"doc":["Abstract supertype of containers whose elements may be \niterated. An iterable container need not be finite, but\nits elements must at least be countable. There may not\nbe a well-defined iteration order, and so the order of\niterated elements may not be stable.\n\nThe type `Iterable<Element,Null>`, usually abbreviated\n`{Element*}` represents a possibly-empty iterable \ncontainer. The type `Iterable<Element,Nothing>`, \nusually abbreviated `{Element+}` represents a nonempty \niterable container.\n\nAn instance of `Iterable` may be constructed by \nsurrounding a value list in braces:\n\n    {String+} words = { \"hello\", \"world\" };\n\nAn instance of `Iterable` may be iterated using a `for`\nloop:\n\n    for (c in \"hello world\") { ... }\n\n`Iterable` and its subtypes define various operations\nthat return other iterable objects. Such operations \ncome in two flavors:\n\n- _Lazy_ operations return a \"view\" of the receiving\n  iterable object. If the underlying iterable object is\n  mutable, then changes to the underlying object will\n  be reflected in the resulting view. Lazy operations\n  are usually efficient, avoiding memory allocation or\n  iteration of the receiving iterable object.\n  \n- _Eager_ operations return an immutable object. If the\n  receiving iterable object is mutable, changes to this\n  object will not be reflected in the resulting \n  immutable object. Eager operations are often \n  expensive, involving memory allocation and iteration\n  of the receiving iterable object.\n\nLazy operations are preferred, because they can be \nefficiently chained. For example:\n\n    string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased)\n\nis much less expensive than:\n\n    string.select((Character c) => c.letter)\n          .collect((Character c) => c.uppercased)\n\nFurthermore, it is always easy to produce a new \nimmutable iterable object given the view produced by a\nlazy operation. For example:\n\n    [ *string.filter((Character c) => c.letter)\n            .map((Character c) => c.uppercased) ]\n\nLazy operations normally return an instance of \n`Iterable` or `Map`.\n\nHowever, there are certain scenarios where an eager \noperation is more useful, more convenient, or no more \nexpensive than a lazy operation, including:\n\n- sorting operations, which are eager by nature,\n- operations which preserve emptiness\/nonemptiness of\n  the receiving iterable object.\n\nEager operations normally return a sequence."],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["An iterator for the elements belonging to this \ncontainer."]},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["byIncreasing","byDecreasing"],"doc":["A sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\n\nFor convenience, the functions `byIncreasing()` \nand `byDecreasing()` produce a suitable \ncomparison function:\n\n    \"Hello World!\".sort(byIncreasing((Character c) => c.lowercased))\n\nThis operation is eager by nature."]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return the number of elements in this `Iterable` \nthat satisfy the predicate function."]},"$nm":"count"},"select":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["filter"],"doc":["A sequence containing the elements of this \ncontainer that satisfy the given predicate. An \neager counterpart to `filter()`."]},"$nm":"select"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"throws":["Exception","if the given step size is nonpositive, \ni.e. `step<1`"],"doc":["Produce an `Iterable` containing every `step`th \nelement of this iterable object. If the step size \nis `1`, the `Iterable` contains the same elements \nas this iterable object. The step size must be \ngreater than zero. The expression\n\n    (0..10).by(3)\n\nresults in an iterable object with the elements\n`0`, `3`, `6`, and `9` in that order."]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["The result of applying the accumulating function to \neach element of this container in turn."]},"$nm":"fold"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if all elements satisfy the predicate\nfunction."]},"$nm":"every"},"defaultNullElements":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"comp":"i","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$nm":"Default"}]},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Default","$mt":"prm","$pt":"v","$nm":"defaultValue"}]],"$mt":"mthd","$tp":[{"$nm":"Default"}],"$an":{"shared":[],"default":[],"doc":["An `Iterable` that produces the elements of this \niterable object, replacing every `null` element \nwith the given default value. The resulting iterable\nobject does not produce the value `null`."]},"$nm":"defaultNullElements"},"taking":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the first `take`\nelements of this iterable object. If the specified \nnumber of elements is larger than the number of \nelements of this iterable object, the `Iterable` \ncontains the same elements as this iterable object."]},"$nm":"taking"},"following":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"head"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["An `Iterable` with the given inital element followed \nby the elements of this iterable object."]},"$nm":"following"},"chain":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Absent"},{"$nm":"OtherAbsent"}]}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$nm":"OtherAbsent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"OtherAbsent"}],"$an":{"shared":[],"default":[],"doc":["The elements of this iterable object, in their\noriginal order, followed by the elements of the \ngiven iterable object also in their original order."]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if at least one element satisfies the\npredicate function."]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["collect"],"doc":["An `Iterable` containing the results of applying\nthe given mapping to the elements of to this \ncontainer."]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The last element which satisfies the given\npredicate, if any, or `null` otherwise."]},"$nm":"findLast"},"skipping":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the elements of\nthis iterable object, after skipping the first \n`skip` elements. If this iterable object does not \ncontain more elements than the specified number of \nelements, the `Iterable` contains no elements."]},"$nm":"skipping"},"filter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["select"],"doc":["An `Iterable` containing the elements of this \ncontainer that satisfy the given predicate."]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The first element which satisfies the given \npredicate, if any, or `null` otherwise."]},"$nm":"find"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["map"],"doc":["A sequence containing the results of applying the\ngiven mapping to the elements of this container. An \neager counterpart to `map()`."]},"$nm":"collect"}},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The last element returned by the iterator, if any.\nIterables are potentially infinite, so calling this\nmight never return; also, this implementation will\niterate through all the elements, which might be\nvery time-consuming."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["All entries of form `index->element` where `index` \nis the position at which `element` occurs, for every\nnon-null element of this `Iterable`, ordered by\nincreasing `index`. For a null element at a given\nposition in the original `Iterable`, there is no \nentry with the corresponding index in the resulting \niterable object. The expression \n\n    { \"hello\", null, \"world\" }.indexed\n    \nresults in an iterable object with the entries\n`0->\"hello\"` and `2->\"world\"`."]},"$nm":"indexed"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A sequence containing the elements returned by the\niterator."]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this iterable is empty. If the number of items\nis very large only a certain amount of them might\nbe shown followed by \"...\"."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The non-null elements of this `Iterable`, in their\noriginal order. For null elements of the original \n`Iterable`, there is no entry in the resulting \niterable object."]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determines if the iterable object is empty, that is\nto say, if the iterator returns no elements."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns an iterable object containing all but the \nfirst element of this container."]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The first element returned by the iterator, if any.\nThis should always produce the same value as\n`iterable.iterator().head`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[]},"$nm":"size"}},"$nm":"Iterable"},"LazyList":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of List that wraps an `Iterable` of\nelements. All operations on this List are performed on \nthe Iterable."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `List` with the elements of this `List` \nin reverse order. This operation will create copy \nthe elements to a new `List`, so changes to the \noriginal `Iterable` will no longer be reflected in \nthe new `List`."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"first"}},"$nm":"LazyList"},"className":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"obj"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Return the name of the concrete class of the given \nobject."]},"$nm":"className"},"List":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Sequence","Empty","Array"],"doc":["Represents a collection in which every element has a \nunique non-negative integer index.\n\nA `List` is a `Collection` of its elements, and a \n`Correspondence` from indices to elements.\n\nDirect access to a list element by index produces a\nvalue of optional type. The following idiom may be\nused instead of upfront bounds-checking, as long as \nthe list element type is a non-`null` type:\n\n    value char = \"hello world\"[index];\n    if (exists char) { \/*do something*\/ }\n    else { \/*out of bounds*\/ }\n\nTo iterate the indexes of a `List`, use the following\nidiom:\n\n    for (i->char in \"hello world\".indexed) { ... }\n\n"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"withTrailing":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["Returns a new `List` that contains the specified\nelement appended to the end of the elements of this \n`List`."]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if the given index refers to an element\nof this sequence, that is, if\n`index<=sequence.lastIndex`."],"actual":[]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the element of this sequence with the given\nindex, or `null` if the given index is past the end\nof the sequence, that is, if\n`index>sequence.lastIndex`. The first element of\nthe sequence has index `0`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `List`s are considered equal iff they have the \nsame `size` and _entry sets_. The entry set of a \nlist `l` is the set of elements of `l.indexed`. \nThis definition is equivalent to the more intuitive \nnotion that two lists are equal iff they have the \nsame `size` and for every index either:\n\n- the lists both have the element `null`, or\n- the lists both have a non-null element, and the\n  two elements are equal."],"actual":[]},"$nm":"equals"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"see":["following"],"doc":["Returns a new `List` that starts with the specified\nelement, followed by the elements of this `List`."]},"$nm":"withLeading"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["The index of the last element of the list, or\nnull if the list is empty."]},"$nm":"lastIndex"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the last element of this `List`, if any."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this list, returning a new list."]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the list, without the first element."],"actual":[]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the first element of this `List`, if any."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["lastIndex"],"doc":["The number of elements in this sequence, always\n`sequence.lastIndex+1`."],"actual":[]},"$nm":"size"}},"$nm":"List"},"Container":{"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"see":["Category"],"doc":["Abstract supertype of objects which may or may not\ncontain one of more other values, called *elements*,\nand provide an operation for accessing the first \nelement, if any. A container which may or may not be \nempty is a `Container<Element,Null>`. A container which \nis always empty is a `Container<Nothing,Null>`. A \ncontainer which is never empty is a \n`Container<Element,Nothing>`."],"by":["Gavin"]},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element. Should produce `null` if the\ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`."]},"$nm":"last"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the container is empty, that is, if\nit has no elements."]},"$nm":"empty"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element. Should produce `null` if the \ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`."]},"$nm":"first"}},"$nm":"Container"},"abstract":{"$t":{"$pk":"ceylon.language","$nm":"Abstract"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a class as abstract. An `abstract` \nclass may not be directly instantiated. An `abstract`\nclass may have enumerated cases."]},"$nm":"abstract"},"zip":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"items"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"doc":["Given two sequences, form a new sequence consisting of\nall entries where, for any given index in the resulting\nsequence, the key of the entry is the element occurring \nat the same index in the first sequence, and the item \nis the element occurring at the same index in the second \nsequence. The length of the resulting sequence is the \nlength of the shorter of the two given sequences. \n\nThus:\n\n    zip(xs,ys)[i]==xs[i]->ys[i]\n\nfor every `0<=i<min({xs.size,ys.size})`."]},"$nm":"zip"},"Shared":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[shared]]."]},"$nm":"Shared"},"Deprecation":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}],"$mt":"cls","$an":{"annotation":[],"shared":[],"doc":["The annotation class for [[deprecated]]."]},"$at":{"reason":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"gttr","$an":{"shared":[]},"$nm":"reason"}},"$nm":"Deprecation"},"finished":{"super":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"obj","$an":{"shared":[],"see":["Iterator"],"doc":["A value that indicates that an `Iterator`\nis exhausted and has no more values to \nreturn."]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"finished"},"equal":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is exactly equal to the given value."],"by":["Gavin"]},"$nm":"equal"},"Boolean":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"true"},{"$pk":"ceylon.language","$nm":"false"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A type capable of representing the values true and\nfalse of Boolean logic."],"by":["Gavin"]},"$nm":"Boolean"},"See":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"See"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"Type"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$mt":"cls","$an":{"annotation":[],"shared":[]},"$nm":"See"},"print":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["process.writeLine"],"doc":["Print a line to the standard output of the virtual \nmachine process, printing the given value's `string`, \nor `«null»` if the value is `null`.\n\nThis method is a shortcut for:\n\n    process.writeLine(line?.string else \"«null»\")\n\nand is intended mainly for debugging purposes."],"by":["Gavin"]},"$nm":"print"},"ChainedIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"first"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"second"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"variance":"out","$nm":"Other"}],"$an":{"doc":["An `Iterator` that returns the elements of two\n`Iterable`s, as if they were chained together."],"by":["Enrique Zamudio"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"more":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"more"},"iter":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"iter"}},"$nm":"ChainedIterator"},"Basic":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The default superclass when no superclass is explicitly\nspecified using `extends`. For the sake of convenience, \nthis class inherits a default definition of value\nequality from `Identifiable`."],"by":["Gavin"]},"$nm":"Basic"},"Category":{"$mt":"ifc","$an":{"shared":[],"see":["Container"],"doc":["Abstract supertype of objects that contain other \nvalues, called *elements*, where it is possible to \nefficiently determine if a given value is an element. \n`Category` does not satisfy `Container`, because it is \nconceptually possible to have a `Category` whose \nemptiness cannot be computed.\n\nThe `in` operator may be used to determine if a value\nbelongs to a `Category`:\n\n    if (\"hello\" in \"hello world\") { ... }\n    if (69 in 0..100) { ... }\n    if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n\nOrdinarily, `x==y` implies that `x in cat == y in cat`.\nBut this contract is not required since it is possible\nto form a meaningful `Category` using a different\nequivalence relation. For example, an `IdentitySet` is\na meaningful `Category`."],"by":["Gavin"]},"$m":{"containsAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if any one of the given values belongs \nto this `Category`"]},"$nm":"containsAny"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["containsEvery","containsAny"],"doc":["Determines if the given value belongs to this\n`Category`, that is, if it is an element of this\n`Category`.\n\nFor most `Category`s, if `x==y`, then \n`category.contains(x)` evaluates to the same\nvalue as `category.contains(y)`. However, it is\npossible to form a `Category` consistent with some \nother equivalence relation, for example `===`. \nTherefore implementations of `contains()` which do \nnot satisfy this relationship are tolerated."]},"$nm":"contains"},"containsEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if every one of the given values belongs\nto this `Category`."]},"$nm":"containsEvery"}},"$nm":"Category"},"see":{"$t":{"$pk":"ceylon.language","$nm":"See"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}]],"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to specify API references to other related \nprogram elements."]},"$nm":"see"},"NegativeNumberException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a negative number is not allowed."]},"$nm":"NegativeNumberException"},"actual":{"$t":{"$pk":"ceylon.language","$nm":"Actual"},"$mt":"mthd","$an":{"annotation":[],"shared":[],"doc":["Annotation to mark a member of a type as refining a \nmember of a supertype."]},"$nm":"actual"},"Integer":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Integral"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Binary"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A 64-bit integer, or the closest approximation to a \n64-bit integer provided by the underlying platform.\n\n- For the JVM runtime, integer values between\n  -2<sup>63<\/sup> and 2<sup>63<\/sup>-1 may be \n  represented without overflow.\n- For the JavaScript runtime, integer values with a\n  magnitude no greater than 2<sup>53<\/sup> may be\n  represented without loss of precision.\n\nOverflow or loss of precision occurs silently (with\nno exception raised)."]},"$at":{"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The UTF-32 character with this UCS code point."]},"$nm":"character"}},"$nm":"Integer"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"doc":["The first of the given elements (usually a comprehension),\nif any."]},"$nm":"first"}},"ceylon.language.metamodel":{"sequencedAnnotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"sequencedAnnotations"},"Value":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Declaration"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$m":{"get":{"$t":{"$nm":"Type"},"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"get"}},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Value"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"type":{"$t":{"$pk":"ceylon.language.metamodel","$nm":"AppliedType"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"Value"},"Member":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Kind"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"$nm":"Type"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Declaration"}],"$nm":"Kind"}],"$an":{"shared":[]},"$at":{"declaringClassOrInterface":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.metamodel","$nm":"ClassOrInterface"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaringClassOrInterface"}},"$nm":"Member"},"Variable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.metamodel","$nm":"Value"}],"$mt":"ifc","$tp":[{"$nm":"Type"}],"$an":{"shared":[]},"$m":{"set":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":"Type","$mt":"prm","$pt":"v","$nm":"newValue"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"set"}},"$nm":"Variable"},"Type":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Instance"}],"$an":{"shared":[]},"$nm":"Type"},"IntersectionType":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"satisfiedTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"satisfiedTypes"}},"$nm":"IntersectionType"},"Function":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language","$nm":"Callable"},{"$pk":"ceylon.language.metamodel","$nm":"Declaration"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Function"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"type":{"$t":{"$pk":"ceylon.language.metamodel","$nm":"AppliedType"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"Function"},"Declaration":{"of":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"ClassOrInterface"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"Nothing"}],"$pk":"ceylon.language.metamodel","$nm":"Function"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"Value"}],"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"}},"$nm":"Declaration"},"UnionType":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"caseTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"caseTypes"}},"$nm":"UnionType"},"type":{"$t":{"$pk":"ceylon.language.metamodel","$nm":"AppliedType"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"instance"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"type"},"Interface":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.metamodel","$nm":"ClassOrInterface"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Interface"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"}},"$nm":"Interface"},"$pkg-shared":"1","optionalAnnotation":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"optionalAnnotation"},"SequencedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur multiple times\nat a single program element."]},"$nm":"SequencedAnnotation"},"OptionalAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur at most once\nat a single program element."]},"$nm":"OptionalAnnotation"},"Annotation":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["An annotation."]},"$nm":"Annotation"},"ConstrainedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"variance":"out","$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation. This interface encodes\nconstraints upon the annotation in its\ntype arguments."]},"$m":{"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language.metamodel","$nm":"Annotated"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$an":{"shared":[]},"$nm":"occurs"}},"$nm":"ConstrainedAnnotation"},"Class":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.metamodel","$nm":"ClassOrInterface"},{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$nm":"Arguments"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Class"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"}},"$nm":"Class"},"nothingType":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$mt":"obj","$an":{"shared":[]},"$nm":"nothingType"},"ClassOrInterface":{"of":[{"$tp":[{"$mt":"tpm","$nm":"Type"},{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"Nothing"}],"$pk":"ceylon.language.metamodel","$nm":"Class"},{"$tp":[{"$mt":"tpm","$nm":"Type"}],"$pk":"ceylon.language.metamodel","$nm":"Interface"}],"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Declaration"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Type"}],"$an":{"shared":[]},"$m":{"getFunction":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.metamodel","$nm":"Member"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"Nothing"}],"$pk":"ceylon.language.metamodel","$nm":"Function"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"getFunction"},"getAttribute":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.metamodel","$nm":"Member"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"Value"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"getAttribute"},"getClassOrInterface":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"SubType"},{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.metamodel","$nm":"Member"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$tp":[{"$nm":"SubType"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"ClassOrInterface"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"getClassOrInterface"}},"$at":{"superclass":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"Nothing"}],"$pk":"ceylon.language.metamodel","$nm":"Class"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclass"},"declaration":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"ClassOrInterface"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"declaration"},"interfaces":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"Interface"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaces"},"typeArguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"TypeParameter"},{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeArguments"}},"$nm":"ClassOrInterface"},"AppliedType":{"$mt":"ifc","$an":{"shared":[]},"$nm":"AppliedType"},"annotations":{"$t":{"$nm":"Values"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$nm":"Value"},{"$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"annotations"},"Annotated":{"$mt":"ifc","$an":{"shared":[],"doc":["A program element that can\nbe annotated."]},"$nm":"Annotated"}},"ceylon.language.metamodel.untyped":{"Value":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"Value"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$def":"1","$nm":"instance"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"}},"$at":{"type":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Type"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"Value"},"Member":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Kind"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Callable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"}],"$nm":"Kind"}],"$an":{"shared":[]},"$at":{"declaringClassOrInterface":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"ClassOrInterface"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaringClassOrInterface"}},"$nm":"Member"},"TypeParameterType":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"declaration":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"TypeParameter"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"}},"$nm":"TypeParameterType"},"Type":{"of":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"nothingType"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"ClassOrInterface"}],"$pk":"ceylon.language.metamodel.untyped","$nm":"ParameterisedType"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"TypeParameterType"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"UnionType"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"IntersectionType"}],"$mt":"ifc","$an":{"shared":[]},"$nm":"Type"},"IntersectionType":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"satisfiedTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"satisfiedTypes"}},"$nm":"IntersectionType"},"Function":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"Parameterised"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Nothing"}],"$pk":"ceylon.language.metamodel","$nm":"Function"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"}},"$at":{"type":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Type"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"type"}},"$nm":"Function"},"Declaration":{"of":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Value"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"Function"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"ClassOrInterface"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"annotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Annotation"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Annotation"}],"$an":{"shared":[],"formal":[]},"$nm":"annotations"}},"$at":{"toplevel":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"toplevel"},"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"name"},"packageContainer":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Package"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"packageContainer"}},"$nm":"Declaration"},"UnionType":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"caseTypes":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"caseTypes"}},"$nm":"UnionType"},"Module":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"ifc","$an":{"shared":[]},"$at":{"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"name"},"members":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Package"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"members"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"version"}},"$nm":"Module"},"TypeParameter":{"$mt":"ifc","$an":{"shared":[]},"$at":{"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"name"}},"$nm":"TypeParameter"},"Interface":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"ClassOrInterface"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"Interface"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"apply"}},"$nm":"Interface"},"$pkg-shared":"1","Class":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"ClassOrInterface"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Nothing"}],"$pk":"ceylon.language.metamodel","$nm":"Class"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"apply"}},"$nm":"Class"},"nothingType":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$mt":"obj","$an":{"shared":[]},"$nm":"nothingType"},"ClassOrInterface":{"of":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Class"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"Interface"}],"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"Parameterised"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"apply":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language.metamodel","$nm":"ClassOrInterface"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel","$nm":"AppliedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"types"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"apply"},"annotatedMembers":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.metamodel.untyped","$nm":"Member"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"}],"$nm":"Kind"},{"$nm":"Annotation"}],"$an":{"shared":[],"formal":[]},"$nm":"annotatedMembers"},"members":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language.metamodel.untyped","$nm":"Member"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"members"}},"$at":{"superclass":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Class"}],"$pk":"ceylon.language.metamodel.untyped","$nm":"ParameterisedType"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclass"},"interfaces":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Interface"}],"$pk":"ceylon.language.metamodel.untyped","$nm":"ParameterisedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaces"}},"$nm":"ClassOrInterface"},"Package":{"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"ifc","$an":{"shared":[]},"$m":{"getFunction":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"Function"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"getFunction"},"annotatedMembers":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"}],"$nm":"Kind"},{"$nm":"Annotation"}],"$an":{"shared":[],"formal":[]},"$nm":"annotatedMembers"},"getAttribute":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"Value"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"getAttribute"},"members":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Kind"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Declaration"}],"$nm":"Kind"}],"$an":{"shared":[],"formal":[]},"$nm":"members"}},"$at":{"container":{"$t":{"$pk":"ceylon.language.metamodel.untyped","$nm":"Module"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"container"},"name":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"name"}},"$nm":"Package"},"ParameterisedType":{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language.metamodel.untyped","$nm":"ClassOrInterface"}],"variance":"out","$nm":"DeclarationType"}],"$an":{"shared":[]},"$at":{"superclass":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Class"}],"$pk":"ceylon.language.metamodel.untyped","$nm":"ParameterisedType"}]},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"superclass"},"declaration":{"$t":{"$nm":"DeclarationType"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"declaration"},"interfaces":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Interface"}],"$pk":"ceylon.language.metamodel.untyped","$nm":"ParameterisedType"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"interfaces"},"typeArguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"TypeParameter"},{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"Type"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeArguments"}},"$nm":"ParameterisedType"},"Parameterised":{"$mt":"ifc","$an":{"shared":[]},"$m":{"getTypeParameter":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language.metamodel.untyped","$nm":"TypeParameter"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"formal":[]},"$nm":"getTypeParameter"}},"$at":{"typeParameters":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language.metamodel.untyped","$nm":"TypeParameter"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[]},"$nm":"typeParameters"}},"$nm":"Parameterised"}}}
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
function defineAttr(obj, name, get, set) {
Object.defineProperty(obj, name, {get: get, set: set, configurable: true, enumerable: true});
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
function Null(wat) {
return null;
}
initType(Null, 'ceylon.language::Null', Anything);
function Nothing(wat) {
throw "Nothing";
}
initType(Nothing, 'ceylon.language::Nothing');
function Object$(wat) {
return wat;
}
initTypeProto(Object$, 'ceylon.language::Object', Anything);
Object$.$$metamodel$$={$nm:'Object',$mt:'cls',$ps:[],$an:function(){return[shared(),abstract()]}};
var Object$proto = Object$.$$.prototype;
defineAttr(Object$proto, 'string', function(){
return String$(className(this) + "@" + this.hash);
});
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
Identifiable.$$metamodel$$={$nm:'Identifiable',$mt:'ifc',$an:function(){return[shared()]}};
function $init$Identifiable() { return Identifiable; }
var Identifiable$proto = Identifiable.$$.prototype;
Identifiable$proto.equals = function(that) {
return isOfType(that, {t:Identifiable}) && (that===this);
}
defineAttr(Identifiable$proto, 'hash', function(){ return $identityHash(this); });
//INTERFACES
function Callable(wat) {
return wat;
}
Callable.$$metamodel$$={$nm:'Callable',$mt:'ifc',$an:function(){return[shared()];},$tp:{Arguments:{'var':'out'},Return:{'var':'out'}}};
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
callable.$$metamodel$$={$mt:'mthd',$ps:[]};
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
JsCallable.$$metamodel$$={$nm:'Callable',$tp:{Return:{'var':'out'}, Arguments:{'var':'in'}},$an:function(){return[shared()];}};
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
JsCallableList.$$metamodel$$={$nm:'Callable',$tp:{Return:{'var':'out'}, Arguments:{'var':'in'}},$an:function(){return[shared()];}};
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
Comprehension.$$metamodel$$={$nm:'Comprehension',$an:function(){return[shared()];},$mt:'cls'};
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
});
exports.Comprehension=Comprehension;
function ComprehensionIterator(nextFunc, $$targs$$, it) {
$init$ComprehensionIterator();
if (it===undefined) {it = new ComprehensionIterator.$$;}
it.$$targs$$=$$targs$$;
Basic(it);
it.next = nextFunc;
return it;
}
ComprehensionIterator.$$metamodel$$={$nm:'ComprehensionIterator',$mt:'cls',$an:function(){return[shared()];}};
function $init$ComprehensionIterator() {
if (ComprehensionIterator.$$===undefined) {
initTypeProto(ComprehensionIterator, 'ceylon.language::ComprehensionIterator',
$init$Basic(), $init$Iterator());
}
return ComprehensionIterator;
}
$init$ComprehensionIterator();
//Compiled from Ceylon sources
function Basic($$basic){
    $init$Basic();
    if ($$basic===undefined)$$basic=new Basic.$$;
    Object$($$basic);
    Identifiable($$basic);
    return $$basic;
}
Basic.$$metamodel$$={$nm:'Basic',$mt:'cls','super':{t:Object$},satisfies:[{t:Identifiable}],$an:function(){return[doc(String$("The default superclass when no superclass is explicitly\nspecified using `extends`. For the sake of convenience, \nthis class inherits a default definition of value\nequality from `Identifiable`.",192)),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),abstract()];}};
exports.Basic=Basic;
function $init$Basic(){
    if (Basic.$$===undefined){
        initTypeProto(Basic,'ceylon.language::Basic',Object$,$init$Identifiable());
    }
    return Basic;
}
exports.$init$Basic=$init$Basic;
$init$Basic();
function Exception(description$1, cause$2, $$exception){
    $init$Exception();
    if ($$exception===undefined)$$exception=new Exception.$$;
    if(description$1===undefined){description$1=null;}
    if(cause$2===undefined){cause$2=null;}
    $$exception.cause$3_=cause$2;
    $$exception.description$4_=description$1;
    return $$exception;
}
Exception.$$metamodel$$={$nm:'Exception',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[doc(String$("The supertype of all exceptions. A subclass represents\na more specific kind of problem, and may define \nadditional attributes which propagate information about\nproblems of that kind.",182)),by([String$("Gavin",5),String$("Tom",3)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared(),$native()];}};
exports.Exception=Exception;
function $init$Exception(){
    if (Exception.$$===undefined){
        initTypeProto(Exception,'ceylon.language::Exception',Basic);
        (function($$exception){
            defineAttr($$exception,'cause',function(){return this.cause$3_;});
            defineAttr($$exception,'description$4',function(){return this.description$4_;});
            defineAttr($$exception,'message',function(){
                var $$exception=this;
                return (opt$5=(opt$6=$$exception.description$4,opt$6!==null?opt$6:(opt$7=$$exception.cause,opt$7!==null?opt$7.message:null)),opt$5!==null?opt$5:String$("",0));
            });
            defineAttr($$exception,'string',function(){
                var $$exception=this;
                return className($$exception).plus(StringBuilder().appendAll([String$(" \"",2),$$exception.message.string,String$("\"",1)]).string);
            });
        })(Exception.$$.prototype);
    }
    return Exception;
}
exports.$init$Exception=$init$Exception;
$init$Exception();
var opt$5,opt$6,opt$7;
function Iterable($$iterable){
    Container($$iterable);
}
Iterable.$$metamodel$$={$nm:'Iterable',$mt:'ifc',$tp:{Element:{'var':'out',},Absent:{'var':'out','satisfies':[{t:Null}],'def':{t:Null}}},satisfies:[{t:Container,a:{Absent:'Absent',Element:'Element'}}],$an:function(){return[doc(String$("Abstract supertype of containers whose elements may be \niterated. An iterable container need not be finite, but\nits elements must at least be countable. There may not\nbe a well-defined iteration order, and so the order of\niterated elements may not be stable.\n\nThe type `Iterable<Element,Null>`, usually abbreviated\n`{Element*}` represents a possibly-empty iterable \ncontainer. The type `Iterable<Element,Nothing>`, \nusually abbreviated `{Element+}` represents a nonempty \niterable container.\n\nAn instance of `Iterable` may be constructed by \nsurrounding a value list in braces:\n\n    {String+} words = { \"hello\", \"world\" };\n\nAn instance of `Iterable` may be iterated using a `for`\nloop:\n\n    for (c in \"hello world\") { ... }\n\n`Iterable` and its subtypes define various operations\nthat return other iterable objects. Such operations \ncome in two flavors:\n\n- _Lazy_ operations return a \"view\" of the receiving\n  iterable object. If the underlying iterable object is\n  mutable, then changes to the underlying object will\n  be reflected in the resulting view. Lazy operations\n  are usually efficient, avoiding memory allocation or\n  iteration of the receiving iterable object.\n  \n- _Eager_ operations return an immutable object. If the\n  receiving iterable object is mutable, changes to this\n  object will not be reflected in the resulting \n  immutable object. Eager operations are often \n  expensive, involving memory allocation and iteration\n  of the receiving iterable object.\n\nLazy operations are preferred, because they can be \nefficiently chained. For example:\n\n    string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased)\n\nis much less expensive than:\n\n    string.select((Character c) => c.letter)\n          .collect((Character c) => c.uppercased)\n\nFurthermore, it is always easy to produce a new \nimmutable iterable object given the view produced by a\nlazy operation. For example:\n\n    [ *string.filter((Character c) => c.letter)\n            .map((Character c) => c.uppercased) ]\n\nLazy operations normally return an instance of \n`Iterable` or `Map`.\n\nHowever, there are certain scenarios where an eager \noperation is more useful, more convenient, or no more \nexpensive than a lazy operation, including:\n\n- sorting operations, which are eager by nature,\n- operations which preserve emptiness/nonemptiness of\n  the receiving iterable object.\n\nEager operations normally return a sequence.",2418)),see([Collection].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Iterable=Iterable;
function $init$Iterable(){
    if (Iterable.$$===undefined){
        initTypeProto(Iterable,'ceylon.language::Iterable',$init$Container());
        (function($$iterable){
            defineAttr($$iterable,'empty',function(){
                var $$iterable=this;
                return isOfType($$iterable.iterator().next(),{t:Finished});
            });
            defineAttr($$iterable,'size',function(){
                var $$iterable=this;
                return $$iterable.count($JsCallable(function (e$8){
                    var $$iterable=this;
                    return true;
                },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}));
            });
            $$iterable.contains=function (element$9){
                var $$iterable=this;
                return $$iterable.any($JsCallable(ifExists($JsCallable((opt$10=element$9,JsCallable(opt$10,opt$10!==null?opt$10.equals:null)),[{$nm:'that',$mt:'prm',$t:{t:Object$}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Return:{t:Boolean$}})),[{$nm:'p1',$mt:'prm',$t:{t:Anything}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Anything},Element:{t:Anything}}},Return:{t:Boolean$}}));
            };
            $$iterable.contains.$$metamodel$$={$nm:'contains',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$an:function(){return[shared(),actual(),$default()];}};
            defineAttr($$iterable,'first',function(){
                var $$iterable=this;
                return first($$iterable,{Value:$$iterable.$$targs$$.Element,Absent:$$iterable.$$targs$$.Absent});
            });
            defineAttr($$iterable,'last',function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$iterable=this;
                var e$11=$$iterable.first;
                var setE$11=function(e$12){return e$11=e$12;};
                var it$13 = $$iterable.iterator();
                var x$14;while ((x$14=it$13.next())!==getFinished()){
                    e$11=x$14;
                }
                return e$11;
            });defineAttr($$iterable,'rest',function(){
                var $$iterable=this;
                return $$iterable.skipping((1));
            });
            defineAttr($$iterable,'sequence',function(){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$15=$$iterable.iterator();
                    var x$16=getFinished();
                    var next$x$16=function(){return x$16=it$15.next();}
                    next$x$16();
                    return function(){
                        if(x$16!==getFinished()){
                            var x$16$17=x$16;
                            var tmpvar$18=x$16$17;
                            next$x$16();
                            return tmpvar$18;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$iterable.$$targs$$.Element}).sequence;
            });
            $$iterable.$map=function (collecting$19,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$20=$$iterable.iterator();
                    var elem$21=getFinished();
                    var next$elem$21=function(){return elem$21=it$20.next();}
                    next$elem$21();
                    return function(){
                        if(elem$21!==getFinished()){
                            var elem$21$22=elem$21;
                            var tmpvar$23=collecting$19(elem$21$22);
                            next$elem$21();
                            return tmpvar$23;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$$mptypes.Result});
            };
            $$iterable.$map.$$metamodel$$={$nm:'map',$mt:'mthd',$t:{t:Iterable,a:{Absent:'Absent',Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[doc(String$("An `Iterable` containing the results of applying\nthe given mapping to the elements of to this \ncontainer.",105)),see([$$iterable.collect].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared(),$default()];}};
            $$iterable.$filter=function (selecting$24){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$25=$$iterable.iterator();
                    var elem$26=getFinished();
                    var next$elem$26=function(){
                        while((elem$26=it$25.next())!==getFinished()){
                            if(selecting$24(elem$26)){
                                return elem$26;
                            }
                        }
                        return getFinished();
                    }
                    next$elem$26();
                    return function(){
                        if(elem$26!==getFinished()){
                            var elem$26$27=elem$26;
                            var tmpvar$28=elem$26$27;
                            next$elem$26();
                            return tmpvar$28;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
            };
            $$iterable.$filter.$$metamodel$$={$nm:'filter',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("An `Iterable` containing the elements of this \ncontainer that satisfy the given predicate.",90)),see([$JsCallable($$iterable.select,[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}}}},Return:{t:Sequential,a:{Element:$$iterable.$$targs$$.Element}}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}}}},Return:{t:Sequential,a:{Element:$$iterable.$$targs$$.Element}}}}})),shared(),$default()];}};
            $$iterable.fold=function fold(initial$29,accumulating$30,$$$mptypes){
                var $$iterable=this;
                var r$31=initial$29;
                var setR$31=function(r$32){return r$31=r$32;};
                var it$33 = $$iterable.iterator();
                var e$34;while ((e$34=it$33.next())!==getFinished()){
                    r$31=accumulating$30(r$31,e$34);
                }
                return r$31;
            };$$iterable.fold.$$metamodel$$={$nm:'fold',$mt:'mthd',$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[doc(String$("The result of applying the accumulating function to \neach element of this container in turn.",92)),shared(),$default()];}};$$iterable.find=function find(selecting$35){
                var $$iterable=this;
                var it$36 = $$iterable.iterator();
                var e$37;while ((e$37=it$36.next())!==getFinished()){
                    if(selecting$35(e$37)){
                        return e$37;
                    }
                }
                return null;
            };$$iterable.find.$$metamodel$$={$nm:'find',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("The first element which satisfies the given \npredicate, if any, or `null` otherwise.",84)),shared(),$default()];}};$$iterable.findLast=function findLast(selecting$38){
                var $$iterable=this;
                var last$39=null;
                var setLast$39=function(last$40){return last$39=last$40;};
                var it$41 = $$iterable.iterator();
                var e$42;while ((e$42=it$41.next())!==getFinished()){
                    if(selecting$38(e$42)){
                        last$39=e$42;
                    }
                }
                return last$39;
            };$$iterable.findLast.$$metamodel$$={$nm:'findLast',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("The last element which satisfies the given\npredicate, if any, or `null` otherwise.",82)),shared(),$default()];}};$$iterable.$sort=function (comparing$43){
                var $$iterable=this;
                return internalSort($JsCallable(comparing$43,[],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Comparison}}),$$iterable,{Element:$$iterable.$$targs$$.Element});
            };
            $$iterable.$sort.$$metamodel$$={$nm:'sort',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$an:function(){return[doc(String$("A sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\n\nFor convenience, the functions `byIncreasing()` \nand `byDecreasing()` produce a suitable \ncomparison function:\n\n    \"Hello World!\".sort(byIncreasing((Character c) => c.lowercased))\n\nThis operation is eager by nature.",347)),see([byIncreasing,byDecreasing].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared(),$default()];}};
            $$iterable.collect=function (collecting$44,$$$mptypes){
                var $$iterable=this;
                return $$iterable.$map($JsCallable(collecting$44,[],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:$$$mptypes.Result}),{Result:$$$mptypes.Result}).sequence;
            };
            $$iterable.collect.$$metamodel$$={$nm:'collect',$mt:'mthd',$t:{t:Sequential,a:{Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[doc(String$("A sequence containing the results of applying the\ngiven mapping to the elements of this container. An \neager counterpart to `map()`.",132)),see([$$iterable.$map].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared(),$default()];}};
            $$iterable.select=function (selecting$45){
                var $$iterable=this;
                return $$iterable.$filter($JsCallable(selecting$45,[],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}})).sequence;
            };
            $$iterable.select.$$metamodel$$={$nm:'select',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("A sequence containing the elements of this \ncontainer that satisfy the given predicate. An \neager counterpart to `filter()`.",124)),see([$JsCallable($$iterable.$filter,[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}}}},Return:{t:Iterable,a:{Absent:{t:Null},Element:$$iterable.$$targs$$.Element}}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$iterable.$$targs$$.Element,Element:$$iterable.$$targs$$.Element}},Return:{t:Boolean$}}}}},Return:{t:Iterable,a:{Absent:{t:Null},Element:$$iterable.$$targs$$.Element}}}}})),shared(),$default()];}};
            $$iterable.any=function any(selecting$46){
                var $$iterable=this;
                var it$47 = $$iterable.iterator();
                var e$48;while ((e$48=it$47.next())!==getFinished()){
                    if(selecting$46(e$48)){
                        return true;
                    }
                }
                return false;
            };$$iterable.any.$$metamodel$$={$nm:'any',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("Return `true` if at least one element satisfies the\npredicate function.",71)),shared(),$default()];}};$$iterable.$every=function $every(selecting$49){
                var $$iterable=this;
                var it$50 = $$iterable.iterator();
                var e$51;while ((e$51=it$50.next())!==getFinished()){
                    if((!selecting$49(e$51))){
                        return false;
                    }
                }
                return true;
            };$$iterable.$every.$$metamodel$$={$nm:'every',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("Return `true` if all elements satisfy the predicate\nfunction.",61)),shared(),$default()];}};$$iterable.skipping=function skipping(skip$52){
                var $$iterable=this;
                if((skip$52.compare((0))!==getLarger())){
                    return $$iterable;
                }else {
                    var cntvar$53=false;
                    var brkvar$55=false;
                    var retvar$54=(function(){
                        function iterable$56($$targs$$){
                            var $$iterable$56=new iterable$56.$$;
                            $$iterable$56.$$targs$$=$$targs$$;
                            Iterable($$iterable$56);
                            add_type_arg($$iterable$56,'Absent',{t:Null});
                            return $$iterable$56;
                        }
                        function $init$iterable$56(){
                            if (iterable$56.$$===undefined){
                                initTypeProto(iterable$56,'ceylon.language::Iterable.skipping.iterable',Basic,$init$Iterable());
                            }
                            return iterable$56;
                        }
                        $init$iterable$56();
                        (function($$iterable$56){
                            $$iterable$56.iterator=function iterator(){
                                var $$iterable$56=this;
                                var iter$57=$$iterable.iterator();
                                var i$58=(0);
                                var setI$58=function(i$59){return i$58=i$59;};
                                while(((oldi$60=i$58,i$58=oldi$60.successor,oldi$60).compare(skip$52).equals(getSmaller())&&(!isOfType(iter$57.next(),{t:Finished})))){}
                                var oldi$60;
                                return iter$57;
                            };$$iterable$56.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual()];}};defineAttr($$iterable$56,'first',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,*/{
                                var $$iterable$56=this;
                                var first$61;
                                if(!isOfType((first$61=$$iterable$56.iterator().next()),{t:Finished})){
                                    return first$61;
                                }else {
                                    return null;
                                }
                            });defineAttr($$iterable$56,'last',function(){
                                var $$iterable$56=this;
                                return $$iterable.last;
                            });
                        })(iterable$56.$$.prototype);
                        var iterable$62=iterable$56({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$62=function(){
                            return iterable$62;
                        }
                        return getIterable$62();
                    }());if(retvar$54!==undefined){return retvar$54;}
                }
            };$$iterable.skipping.$$metamodel$$={$nm:'skipping',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Produce an `Iterable` containing the elements of\nthis iterable object, after skipping the first \n`skip` elements. If this iterable object does not \ncontain more elements than the specified number of \nelements, the `Iterable` contains no elements.",246)),shared(),$default()];}};$$iterable.taking=function taking(take$63){
                var $$iterable=this;
                if((take$63.compare((0))!==getLarger())){
                    return getEmpty();
                }else {
                    var cntvar$64=false;
                    var brkvar$66=false;
                    var retvar$65=(function(){
                        function iterable$67($$targs$$){
                            var $$iterable$67=new iterable$67.$$;
                            $$iterable$67.$$targs$$=$$targs$$;
                            Iterable($$iterable$67);
                            add_type_arg($$iterable$67,'Absent',{t:Null});
                            return $$iterable$67;
                        }
                        function $init$iterable$67(){
                            if (iterable$67.$$===undefined){
                                initTypeProto(iterable$67,'ceylon.language::Iterable.taking.iterable',Basic,$init$Iterable());
                            }
                            return iterable$67;
                        }
                        $init$iterable$67();
                        (function($$iterable$67){
                            $$iterable$67.iterator=function iterator(){
                                var $$iterable$67=this;
                                var iter$68=$$iterable.iterator();
                                function iterator$69($$targs$$){
                                    var $$iterator$69=new iterator$69.$$;
                                    $$iterator$69.$$targs$$=$$targs$$;
                                    Iterator($$iterator$69);
                                    $$iterator$69.i$70_=/*anotaciones:ceylon.language::Variable,*/(0);
                                    return $$iterator$69;
                                }
                                function $init$iterator$69(){
                                    if (iterator$69.$$===undefined){
                                        initTypeProto(iterator$69,'ceylon.language::Iterable.taking.iterable.iterator.iterator',Basic,$init$Iterator());
                                    }
                                    return iterator$69;
                                }
                                $init$iterator$69();
                                (function($$iterator$69){
                                    defineAttr($$iterator$69,'i$70',function(){return this.i$70_;},function(i$71){return this.i$70_=i$71;});
                                    $$iterator$69.next=function next(){
                                        var $$iterator$69=this;
                                        return (opt$72=(($$iterator$69.i$70=$$iterator$69.i$70.successor).compare(take$63).equals(getLarger())?getFinished():null),opt$72!==null?opt$72:iter$68.next());
                                        var opt$72;
                                    };$$iterator$69.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$an:function(){return[actual(),shared()];}};
                                })(iterator$69.$$.prototype);
                                var iterator$73=iterator$69({Element:$$iterable.$$targs$$.Element});
                                var getIterator$73=function(){
                                    return iterator$73;
                                }
                                return getIterator$73();
                            };$$iterable$67.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual()];}};defineAttr($$iterable$67,'first',function(){
                                var $$iterable$67=this;
                                return $$iterable.first;
                            });
                            defineAttr($$iterable$67,'last',function(){
                                var $$iterable$67=this;
                                return $$iterable.last;
                            });
                        })(iterable$67.$$.prototype);
                        var iterable$74=iterable$67({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$74=function(){
                            return iterable$74;
                        }
                        return getIterable$74();
                    }());if(retvar$65!==undefined){return retvar$65;}
                }
            };$$iterable.taking.$$metamodel$$={$nm:'taking',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Produce an `Iterable` containing the first `take`\nelements of this iterable object. If the specified \nnumber of elements is larger than the number of \nelements of this iterable object, the `Iterable` \ncontains the same elements as this iterable object.",252)),shared(),$default()];}};$$iterable.by=function by(step$75){
                var $$iterable=this;
                //assert at Iterable.ceylon (319:8-320:25)
                if (!(step$75.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Iterable.ceylon (320:15-320:24)'); }
                if(step$75.equals((1))){
                    return $$iterable;
                }else {
                    var cntvar$76=false;
                    var brkvar$78=false;
                    var retvar$77=(function(){
                        function iterable$79($$targs$$){
                            var $$iterable$79=new iterable$79.$$;
                            $$iterable$79.$$targs$$=$$targs$$;
                            Iterable($$iterable$79);
                            return $$iterable$79;
                        }
                        function $init$iterable$79(){
                            if (iterable$79.$$===undefined){
                                initTypeProto(iterable$79,'ceylon.language::Iterable.by.iterable',Basic,$init$Iterable());
                            }
                            return iterable$79;
                        }
                        $init$iterable$79();
                        (function($$iterable$79){
                            $$iterable$79.iterator=function iterator(){
                                var $$iterable$79=this;
                                var iter$80=$$iterable.iterator();
                                function iterator$81($$targs$$){
                                    var $$iterator$81=new iterator$81.$$;
                                    $$iterator$81.$$targs$$=$$targs$$;
                                    Iterator($$iterator$81);
                                    return $$iterator$81;
                                }
                                function $init$iterator$81(){
                                    if (iterator$81.$$===undefined){
                                        initTypeProto(iterator$81,'ceylon.language::Iterable.by.iterable.iterator.iterator',Basic,$init$Iterator());
                                    }
                                    return iterator$81;
                                }
                                $init$iterator$81();
                                (function($$iterator$81){
                                    $$iterator$81.next=function next(){
                                        var $$iterator$81=this;
                                        var next$82=iter$80.next();
                                        var i$83=(0);
                                        var setI$83=function(i$84){return i$83=i$84;};
                                        while(((i$83=i$83.successor).compare(step$75).equals(getSmaller())&&(!isOfType(iter$80.next(),{t:Finished})))){}
                                        return next$82;
                                    };$$iterator$81.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};
                                })(iterator$81.$$.prototype);
                                var iterator$85=iterator$81({Element:$$iterable.$$targs$$.Element});
                                var getIterator$85=function(){
                                    return iterator$85;
                                }
                                return getIterator$85();
                            };$$iterable$79.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual()];}};
                        })(iterable$79.$$.prototype);
                        var iterable$86=iterable$79({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element});
                        var getIterable$86=function(){
                            return iterable$86;
                        }
                        return getIterable$86();
                    }());if(retvar$77!==undefined){return retvar$77;}
                }
            };$$iterable.by.$$metamodel$$={$nm:'by',$mt:'mthd',$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Produce an `Iterable` containing every `step`th \nelement of this iterable object. If the step size \nis `1`, the `Iterable` contains the same elements \nas this iterable object. The step size must be \ngreater than zero. The expression\n\n    (0..10).by(3)\n\nresults in an iterable object with the elements\n`0`, `3`, `6`, and `9` in that order.",338)),$throws($JsCallable(Exception,[],{Arguments:{ t:'u', l:[{t:Empty},{t:Tuple,a:{Rest:{ t:'u', l:[{t:Empty},{t:Tuple,a:{Rest:{t:Empty},First:{ t:'u', l:[{t:Null},{t:Exception}]},Element:{ t:'u', l:[{t:Null},{t:Exception}]}}}]},First:{ t:'u', l:[{t:Null},{t:String$}]},Element:{ t:'u', l:[{t:Null},{t:String$},{t:Exception}]}}}]},Return:{t:Exception}}),String$("if the given step size is nonpositive, \ni.e. `step<1`",53)),shared(),$default()];}};$$iterable.count=function count(selecting$87){
                var $$iterable=this;
                var count$88=(0);
                var setCount$88=function(count$89){return count$88=count$89;};
                var it$90 = $$iterable.iterator();
                var elem$91;while ((elem$91=it$90.next())!==getFinished()){
                    var elem$92;
                    if((elem$92=elem$91)!==null){
                        if(selecting$87(elem$92)){
                            (oldcount$93=count$88,count$88=oldcount$93.successor,oldcount$93);
                            var oldcount$93;
                        }
                    }
                }
                return count$88;
            };$$iterable.count.$$metamodel$$={$nm:'count',$mt:'mthd',$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("Return the number of elements in this `Iterable` \nthat satisfy the predicate function.",86)),shared(),$default()];}};defineAttr($$iterable,'coalesced',function(){
                var $$iterable=this;
                return Comprehension(function(){
                    var e$96;
                    var it$94=$$iterable.iterator();
                    var e$95=getFinished();
                    var e$96;
                    var next$e$95=function(){
                        while((e$95=it$94.next())!==getFinished()){
                            if((e$96=e$95)!==null){
                                return e$95;
                            }
                        }
                        return getFinished();
                    }
                    next$e$95();
                    return function(){
                        if(e$95!==getFinished()){
                            var e$95$97=e$95;
                            var tmpvar$98=e$96;
                            next$e$95();
                            return tmpvar$98;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
            });
            defineAttr($$iterable,'indexed',function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,ceylon.language::Default,*/{
                var $$iterable=this;
                function indexes$99($$targs$$){
                    var $$indexes$99=new indexes$99.$$;
                    $$indexes$99.$$targs$$=$$targs$$;
                    Iterable($$indexes$99);
                    add_type_arg($$indexes$99,'Absent',{t:Null});
                    add_type_arg($$indexes$99,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                    $$indexes$99.orig$100_=/*anotaciones:*/$$iterable;
                    return $$indexes$99;
                }
                function $init$indexes$99(){
                    if (indexes$99.$$===undefined){
                        initTypeProto(indexes$99,'ceylon.language::Iterable.indexed.indexes',Basic,$init$Iterable());
                    }
                    return indexes$99;
                }
                $init$indexes$99();
                (function($$indexes$99){
                    defineAttr($$indexes$99,'orig$100',function(){return this.orig$100_;});
                    $$indexes$99.iterator=function iterator(){
                        var $$indexes$99=this;
                        function iterator$101($$targs$$){
                            var $$iterator$101=new iterator$101.$$;
                            $$iterator$101.$$targs$$=$$targs$$;
                            Iterator($$iterator$101);
                            add_type_arg($$iterator$101,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                            $$iterator$101.iter$102_=/*anotaciones:*/$$indexes$99.orig$100.iterator();
                            $$iterator$101.i$103_=/*anotaciones:ceylon.language::Variable,*/(0);
                            return $$iterator$101;
                        }
                        function $init$iterator$101(){
                            if (iterator$101.$$===undefined){
                                initTypeProto(iterator$101,'ceylon.language::Iterable.indexed.indexes.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$101;
                        }
                        $init$iterator$101();
                        (function($$iterator$101){
                            defineAttr($$iterator$101,'iter$102',function(){return this.iter$102_;});
                            defineAttr($$iterator$101,'i$103',function(){return this.i$103_;},function(i$104){return this.i$103_=i$104;});
                            $$iterator$101.next=function next(){
                                var $$iterator$101=this;
                                var next$105=$$iterator$101.iter$102.next();
                                var setNext$105=function(next$106){return next$105=next$106;};
                                while((!exists(next$105))){
                                    (oldi$107=$$iterator$101.i$103,$$iterator$101.i$103=oldi$107.successor,oldi$107);
                                    var oldi$107;
                                    next$105=$$iterator$101.iter$102.next();
                                }
                                var n$108;
                                var n$109;
                                if(!isOfType((n$108=next$105),{t:Finished})&&(n$109=n$108)!==null){
                                    return Entry((oldi$110=$$iterator$101.i$103,$$iterator$101.i$103=oldi$110.successor,oldi$110),n$109,{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
                                    var oldi$110;
                                }else {
                                    return getFinished();
                                }
                            };$$iterator$101.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:[{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}},{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};
                        })(iterator$101.$$.prototype);
                        var iterator$111=iterator$101({Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                        var getIterator$111=function(){
                            return iterator$111;
                        }
                        return getIterator$111();
                    };$$indexes$99.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$ps:[],$an:function(){return[shared(),actual()];}};
                })(indexes$99.$$.prototype);
                var indexes$112=indexes$99({Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                var getIndexes$112=function(){
                    return indexes$112;
                }
                return getIndexes$112();
            });$$iterable.following=function following(head$113,$$$mptypes){
                var $$iterable=this;
                function cons$114($$targs$$){
                    var $$cons$114=new cons$114.$$;
                    $$cons$114.$$targs$$=$$targs$$;
                    Iterable($$cons$114);
                    return $$cons$114;
                }
                function $init$cons$114(){
                    if (cons$114.$$===undefined){
                        initTypeProto(cons$114,'ceylon.language::Iterable.following.cons',Basic,$init$Iterable());
                    }
                    return cons$114;
                }
                $init$cons$114();
                (function($$cons$114){
                    $$cons$114.iterator=function iterator(){
                        var $$cons$114=this;
                        var iter$115=$$iterable.iterator();
                        function iterator$116($$targs$$){
                            var $$iterator$116=new iterator$116.$$;
                            $$iterator$116.$$targs$$=$$targs$$;
                            Iterator($$iterator$116);
                            $$iterator$116.first$117_=/*anotaciones:ceylon.language::Variable,*/true;
                            return $$iterator$116;
                        }
                        function $init$iterator$116(){
                            if (iterator$116.$$===undefined){
                                initTypeProto(iterator$116,'ceylon.language::Iterable.following.cons.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$116;
                        }
                        $init$iterator$116();
                        (function($$iterator$116){
                            defineAttr($$iterator$116,'first$117',function(){return this.first$117_;},function(first$118){return this.first$117_=first$118;});
                            $$iterator$116.next=function next(){
                                var $$iterator$116=this;
                                if($$iterator$116.first$117){
                                    $$iterator$116.first$117=false;
                                    return head$113;
                                }else {
                                    return iter$115.next();
                                }
                            };$$iterator$116.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element','Other',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};
                        })(iterator$116.$$.prototype);
                        var iterator$119=iterator$116({Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                        var getIterator$119=function(){
                            return iterator$119;
                        }
                        return getIterator$119();
                    };$$cons$114.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[],$an:function(){return[shared(),actual()];}};
                })(cons$114.$$.prototype);
                var cons$120=cons$114({Absent:{t:Nothing},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getCons$120=function(){
                    return cons$120;
                }
                return getCons$120();
            };$$iterable.following.$$metamodel$$={$nm:'following',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'head',$mt:'prm',$t:'Other'}],$tp:{Other:{}},$an:function(){return[doc(String$("An `Iterable` with the given inital element followed \nby the elements of this iterable object.",94)),shared(),$default()];}};$$iterable.chain=function chain(other$121,$$$mptypes){
                var $$iterable=this;
                function chained$122($$targs$$){
                    var $$chained$122=new chained$122.$$;
                    $$chained$122.$$targs$$=$$targs$$;
                    Iterable($$chained$122);
                    return $$chained$122;
                }
                function $init$chained$122(){
                    if (chained$122.$$===undefined){
                        initTypeProto(chained$122,'ceylon.language::Iterable.chain.chained',Basic,$init$Iterable());
                    }
                    return chained$122;
                }
                $init$chained$122();
                (function($$chained$122){
                    $$chained$122.iterator=function (){
                        var $$chained$122=this;
                        return ChainedIterator($$iterable,other$121,{Other:$$$mptypes.Other,Element:$$iterable.$$targs$$.Element});
                    };
                    $$chained$122.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[],$an:function(){return[shared(),actual()];}};
                })(chained$122.$$.prototype);
                var chained$123=chained$122({Absent:{ t:'i', l:[$$iterable.$$targs$$.Absent,$$$mptypes.OtherAbsent]},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getChained$123=function(){
                    return chained$123;
                }
                return getChained$123();
            };$$iterable.chain.$$metamodel$$={$nm:'chain',$mt:'mthd',$t:{t:Iterable,a:{Absent:{ t:'i', l:['Absent','OtherAbsent']},Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}}}],$tp:{Other:{},OtherAbsent:{'satisfies':[{t:Null}]}},$an:function(){return[doc(String$("The elements of this iterable object, in their\noriginal order, followed by the elements of the \ngiven iterable object also in their original order.",147)),shared(),$default()];}};$$iterable.defaultNullElements=function (defaultValue$124,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$125=$$iterable.iterator();
                    var elem$126=getFinished();
                    var next$elem$126=function(){return elem$126=it$125.next();}
                    next$elem$126();
                    return function(){
                        if(elem$126!==getFinished()){
                            var elem$126$127=elem$126;
                            var tmpvar$128=(opt$129=elem$126$127,opt$129!==null?opt$129:defaultValue$124);
                            next$elem$126();
                            return tmpvar$128;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'u', l:[$$$mptypes.Default,{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}]}});
            };
            $$iterable.defaultNullElements.$$metamodel$$={$nm:'defaultNullElements',$mt:'mthd',$t:{t:Iterable,a:{Absent:'Absent',Element:{ t:'u', l:[{ t:'i', l:['Element',{t:Object$}]},'Default']}}},$ps:[{$nm:'defaultValue',$mt:'prm',$t:'Default'}],$tp:{Default:{}},$an:function(){return[doc(String$("An `Iterable` that produces the elements of this \niterable object, replacing every `null` element \nwith the given default value. The resulting iterable\nobject does not produce the value `null`.",193)),shared(),$default()];}};
            defineAttr($$iterable,'string',function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$iterable=this;
                if($$iterable.empty){
                    return String$("{}",2);
                }else {
                    var list$130=commaList($$iterable.taking((30)));
                    return StringBuilder().appendAll([String$("{ ",2),(opt$131=(sizeExceeds($$iterable,(30))?list$130.plus(String$(", ...",5)):null),opt$131!==null?opt$131:list$130).string,String$(" }",2)]).string;
                    var opt$131;
                }
            });
        })(Iterable.$$.prototype);
    }
    return Iterable;
}
exports.$init$Iterable=$init$Iterable;
$init$Iterable();
var opt$10,opt$129;
function sizeExceeds(iterable$132,size$133){
    var count$134=(0);
    var setCount$134=function(count$135){return count$134=count$135;};
    var it$136 = iterable$132.iterator();
    var x$137;while ((x$137=it$136.next())!==getFinished()){
        if(size$133.compare((oldcount$138=count$134,count$134=oldcount$138.successor,oldcount$138)).equals(getSmaller())){
            return true;
        }
        var oldcount$138;
    }
    if (getFinished() === x$137){
        return false;
    }
};sizeExceeds.$$metamodel$$={$nm:'sizeExceeds',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'iterable',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Anything}}}},{$nm:'size',$mt:'prm',$t:{t:Integer}}]};//sizeExceeds.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Integer},Element:{t:Integer}}},Return:{t:Boolean$}};
var commaList=function (elements$139){
    return (strings$140=Comprehension(function(){
        var it$141=elements$139.iterator();
        var element$142=getFinished();
        var next$element$142=function(){return element$142=it$141.next();}
        next$element$142();
        return function(){
            if(element$142!==getFinished()){
                var element$142$143=element$142;
                var tmpvar$144=(opt$145=(opt$146=element$142$143,opt$146!==null?opt$146.string:null),opt$145!==null?opt$145:String$("null",4));
                next$element$142();
                return tmpvar$144;
            }
            return getFinished();
        }
    },{Absent:{t:Null},Element:{t:String$}}),(opt$147=String$(", ",2),JsCallable(opt$147,opt$147!==null?opt$147.join:null))(strings$140));
};
commaList.$$metamodel$$={$nm:'commaList',$mt:'mthd',$t:{t:String$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Anything}}}}]};
var strings$140,opt$145,opt$146,opt$147;
function ifExists(predicate$148){
    return function(val$149){
        var val$150;
        if((val$150=val$149)!==null){
            return predicate$148(val$150);
        }else {
            return false;
        }
    }
};ifExists.$$metamodel$$={$nm:'ifExists',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'predicate',$mt:'prm',$t:{t:Boolean$}}]};//ifExists.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Boolean$},Element:{t:Boolean$}}},Return:{t:Boolean$}};
function Sequential($$sequential){
    List($$sequential);
    Ranged($$sequential);
    add_type_arg($$sequential,'Index',{t:Integer});
    add_type_arg($$sequential,'Span',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
    Cloneable($$sequential);
    add_type_arg($$sequential,'Clone',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
}
Sequential.$$metamodel$$={$nm:'Sequential',$mt:'ifc',$tp:{Element:{'var':'out',}},satisfies:[{t:List,a:{Element:'Element'}},{t:Ranged,a:{Index:{t:Integer},Span:{t:Sequential,a:{Element:'Element'}}}},{t:Cloneable,a:{Clone:{t:Sequential,a:{Element:'Element'}}}}],$an:function(){return[doc(String$("A possibly-empty, immutable sequence of values. The \ntype `Sequential<Element>` may be abbreviated \n`[Element*]` or `Element[]`. \n\n`Sequential` has two enumerated subtypes:\n\n- `Empty`, abbreviated `[]`, represents an empty \n   sequence, and\n- `Sequence<Element>`, abbreviated `[Element+]` \n   represents a non-empty sequence, and has the very\n   important subclass `Tuple`.",373)),see([Tuple].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.Sequential=Sequential;
function $init$Sequential(){
    if (Sequential.$$===undefined){
        initTypeProto(Sequential,'ceylon.language::Sequential',$init$List(),$init$Ranged(),$init$Cloneable());
        (function($$sequential){
            defineAttr($$sequential,'sequence',function(){
                var $$sequential=this;
                return $$sequential;
            });
            defineAttr($$sequential,'clone',function(){
                var $$sequential=this;
                return $$sequential;
            });
            defineAttr($$sequential,'string',function(){
                var $$sequential=this;
                return (opt$151=($$sequential.empty?String$("[]",2):null),opt$151!==null?opt$151:StringBuilder().appendAll([String$("[",1),commaList($$sequential).string,String$("]",1)]).string);
            });
        })(Sequential.$$.prototype);
    }
    return Sequential;
}
exports.$init$Sequential=$init$Sequential;
$init$Sequential();
var opt$151;
function Sequence($$sequence){
    Sequential($$sequence);
    Iterable($$sequence);
    Cloneable($$sequence);
    add_type_arg($$sequence,'Clone',{t:Sequence,a:{Element:$$sequence.$$targs$$.Element}});
}
Sequence.$$metamodel$$={$nm:'Sequence',$mt:'ifc',$tp:{Element:{'var':'out',}},satisfies:[{t:Sequential,a:{Element:'Element'}},{t:Iterable,a:{Absent:{t:Nothing},Element:'Element'}},{t:Cloneable,a:{Clone:{t:Sequence,a:{Element:'Element'}}}}],$an:function(){return[doc(String$("A nonempty, immutable sequence of values. The type \n`Sequence<Element>`, may be abbreviated `[Element+]`.\n\nGiven a possibly-empty sequence of type `[Element*]`, \nthe `if (nonempty ...)` construct, or, alternatively,\nthe `assert (nonempty ...)` construct, may be used to \nnarrow to a nonempty sequence type:\n\n    [Integer*] nums = ... ;\n    if (nonempty nums) {\n        Integer first = nums.first;\n        Integer max = max(nums);\n        [Integer+] squares = nums.collect((Integer i) i**2));\n        [Integer+] sorted = nums.sort(byIncreasing((Integer i) i));\n    }\n\nOperations like `first`, `max()`, `collect()`, and \n`sort()`, which polymorphically produce a nonempty or \nnon-null output when given a nonempty input are called \n_emptiness-preserving_.",753)),see([Empty].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Sequence=Sequence;
function $init$Sequence(){
    if (Sequence.$$===undefined){
        initTypeProto(Sequence,'ceylon.language::Sequence',$init$Sequential(),$init$Iterable(),$init$Cloneable());
        (function($$sequence){
            defineAttr($$sequence,'empty',function(){
                var $$sequence=this;
                return false;
            });
            defineAttr($$sequence,'sequence',function(){
                var $$sequence=this;
                return $$sequence;
            });
            $$sequence.$sort=function $sort(comparing$152){
                var $$sequence=this;
                var s$153=internalSort($JsCallable(comparing$152,[],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:{t:Comparison}}),$$sequence,{Element:$$sequence.$$targs$$.Element});
                //assert at Sequence.ceylon (63:8-63:27)
                var s$154;
                if (!(nonempty((s$154=s$153)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (63:15-63:26)'); }
                return s$154;
            };$$sequence.$sort.$$metamodel$$={$nm:'sort',$mt:'mthd',$t:{t:Sequence,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$an:function(){return[doc(String$("A nonempty sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.",138)),shared(),$default(),actual()];}};$$sequence.collect=function collect(collecting$155,$$$mptypes){
                var $$sequence=this;
                var s$156=$$sequence.$map($JsCallable(collecting$155,[],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$sequence.$$targs$$.Element,Element:$$sequence.$$targs$$.Element}},Return:$$$mptypes.Result}),{Result:$$$mptypes.Result}).sequence;
                //assert at Sequence.ceylon (74:8-74:27)
                var s$157;
                if (!(nonempty((s$157=s$156)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (74:15-74:26)'); }
                return s$157;
            };$$sequence.collect.$$metamodel$$={$nm:'collect',$mt:'mthd',$t:{t:Sequence,a:{Element:'Result'}},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[doc(String$("A nonempty sequence containing the results of \napplying the given mapping to the elements of this\nsequence.",107)),shared(),$default(),actual()];}};defineAttr($$sequence,'clone',function(){
                var $$sequence=this;
                return $$sequence;
            });
            defineAttr($$sequence,'string',function(){
                var $$sequence=this;
                return attrGetter($$sequence.getT$all()['ceylon.language::Sequential'],'string').call(this);
            });
        })(Sequence.$$.prototype);
    }
    return Sequence;
}
exports.$init$Sequence=$init$Sequence;
$init$Sequence();
function Empty($$empty){
    Sequential($$empty);
    Ranged($$empty);
    add_type_arg($$empty,'Index',{t:Integer});
    add_type_arg($$empty,'Span',{t:Empty});
    Cloneable($$empty);
    add_type_arg($$empty,'Clone',{t:Empty});
}
Empty.$$metamodel$$={$nm:'Empty',$mt:'ifc',satisfies:[{t:Sequential,a:{Element:{t:Nothing}}},{t:Ranged,a:{Index:{t:Integer},Span:{t:Empty}}},{t:Cloneable,a:{Clone:{t:Empty}}}],$an:function(){return[doc(String$("A sequence with no elements. The type `Empty` may be\nabbreviated `[]`, and an instance is produced by the \nexpression `[]`. That is, in the following expression,\n`e` has type `[]` and refers to the value `[]`:\n\n    [] none = [];\n\n(Whether the syntax `[]` refers to the type or the \nvalue depends upon how it occurs grammatically.)",330)),see([Sequence].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.Empty=Empty;
function $init$Empty(){
    if (Empty.$$===undefined){
        initTypeProto(Empty,'ceylon.language::Empty',$init$Sequential(),$init$Ranged(),$init$Cloneable());
        (function($$empty){
            $$empty.iterator=function (){
                var $$empty=this;
                return getEmptyIterator();
            };
            $$empty.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:{t:Nothing}}},$ps:[],$an:function(){return[doc(String$("Returns an iterator that is already exhausted.",46)),shared(),actual()];}};
            $$empty.get=function (index$158){
                var $$empty=this;
                return null;
            };
            $$empty.get.$$metamodel$$={$nm:'get',$mt:'mthd',$t:{t:Null},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns `null` for any given index.",35)),shared(),actual()];}};
            $$empty.segment=function (from$159,length$160){
                var $$empty=this;
                return $$empty;
            };
            $$empty.segment.$$metamodel$$={$nm:'segment',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns an `Empty` for any given segment.",41)),shared(),actual()];}};
            $$empty.span=function (from$161,to$162){
                var $$empty=this;
                return $$empty;
            };
            $$empty.span.$$metamodel$$={$nm:'span',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns an `Empty` for any given span.",38)),shared(),actual()];}};
            $$empty.spanTo=function (to$163){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanTo.$$metamodel$$={$nm:'spanTo',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns an `Empty` for any given span.",38)),shared(),actual()];}};
            $$empty.spanFrom=function (from$164){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanFrom.$$metamodel$$={$nm:'spanFrom',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns an `Empty` for any given span.",38)),shared(),actual()];}};
            defineAttr($$empty,'empty',function(){
                var $$empty=this;
                return true;
            });
            defineAttr($$empty,'size',function(){
                var $$empty=this;
                return (0);
            });
            defineAttr($$empty,'reversed',function(){
                var $$empty=this;
                return $$empty;
            });
            defineAttr($$empty,'sequence',function(){
                var $$empty=this;
                return $$empty;
            });
            defineAttr($$empty,'string',function(){
                var $$empty=this;
                return String$("{}",2);
            });
            defineAttr($$empty,'lastIndex',function(){
                var $$empty=this;
                return null;
            });
            defineAttr($$empty,'first',function(){
                var $$empty=this;
                return null;
            });
            defineAttr($$empty,'last',function(){
                var $$empty=this;
                return null;
            });
            defineAttr($$empty,'rest',function(){
                var $$empty=this;
                return $$empty;
            });
            defineAttr($$empty,'clone',function(){
                var $$empty=this;
                return $$empty;
            });
            defineAttr($$empty,'coalesced',function(){
                var $$empty=this;
                return $$empty;
            });
            defineAttr($$empty,'indexed',function(){
                var $$empty=this;
                return $$empty;
            });
            $$empty.chain=function (other$165,$$$mptypes){
                var $$empty=this;
                return other$165;
            };
            $$empty.chain.$$metamodel$$={$nm:'chain',$mt:'mthd',$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Iterable,a:{Absent:'OtherAbsent',Element:'Other'}}}],$tp:{Other:{},OtherAbsent:{'satisfies':[{t:Null}]}},$an:function(){return[doc(String$("Returns `other`.",16)),shared(),actual()];}};
            $$empty.contains=function (element$166){
                var $$empty=this;
                return false;
            };
            $$empty.contains.$$metamodel$$={$nm:'contains',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Returns `false` for any given element.",38)),shared(),actual()];}};
            $$empty.count=function (selecting$167){
                var $$empty=this;
                return (0);
            };
            $$empty.count.$$metamodel$$={$nm:'count',$mt:'mthd',$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("Returns 0 for any given predicate.",34)),shared(),actual()];}};
            $$empty.defines=function (index$168){
                var $$empty=this;
                return false;
            };
            $$empty.defines.$$metamodel$$={$nm:'defines',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$empty.$map=function (collecting$169,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$map.$$metamodel$$={$nm:'map',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[shared(),actual()];}};
            $$empty.$filter=function (selecting$170){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$filter.$$metamodel$$={$nm:'filter',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$empty.fold=function (initial$171,accumulating$172,$$$mptypes){
                var $$empty=this;
                return initial$171;
            };
            $$empty.fold.$$metamodel$$={$nm:'fold',$mt:'mthd',$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[shared(),actual()];}};
            $$empty.find=function (selecting$173){
                var $$empty=this;
                return null;
            };
            $$empty.find.$$metamodel$$={$nm:'find',$mt:'mthd',$t:{t:Null},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$empty.$sort=function (comparing$174){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$sort.$$metamodel$$={$nm:'sort',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$an:function(){return[shared(),actual()];}};
            $$empty.collect=function (collecting$175,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.collect.$$metamodel$$={$nm:'collect',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'collecting',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[shared(),actual()];}};
            $$empty.select=function (selecting$176){
                var $$empty=this;
                return $$empty;
            };
            $$empty.select.$$metamodel$$={$nm:'select',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$empty.any=function (selecting$177){
                var $$empty=this;
                return false;
            };
            $$empty.any.$$metamodel$$={$nm:'any',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$empty.$every=function (selecting$178){
                var $$empty=this;
                return false;
            };
            $$empty.$every.$$metamodel$$={$nm:'every',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$empty.skipping=function (skip$179){
                var $$empty=this;
                return $$empty;
            };
            $$empty.skipping.$$metamodel$$={$nm:'skipping',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$empty.taking=function (take$180){
                var $$empty=this;
                return $$empty;
            };
            $$empty.taking.$$metamodel$$={$nm:'taking',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$empty.by=function (step$181){
                var $$empty=this;
                return $$empty;
            };
            $$empty.by.$$metamodel$$={$nm:'by',$mt:'mthd',$t:{t:Empty},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$empty.withLeading=function (element$182,$$$mptypes){
                var $$empty=this;
                return Tuple(element$182,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withLeading.$$metamodel$$={$nm:'withLeading',$mt:'mthd',$t:{t:Tuple,a:{Rest:{t:Empty},First:'Element',Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:'Element'}],$tp:{Element:{}},$an:function(){return[shared(),actual()];}};
            $$empty.withTrailing=function (element$183,$$$mptypes){
                var $$empty=this;
                return Tuple(element$183,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withTrailing.$$metamodel$$={$nm:'withTrailing',$mt:'mthd',$t:{t:Tuple,a:{Rest:{t:Empty},First:'Element',Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:'Element'}],$tp:{Element:{}},$an:function(){return[shared(),actual()];}};
            $$empty.following=function (head$184,$$$mptypes){
                var $$empty=this;
                return Singleton(head$184,{Element:$$$mptypes.Other});
            };
            $$empty.following.$$metamodel$$={$nm:'following',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Other'}},$ps:[{$nm:'head',$mt:'prm',$t:'Other'}],$tp:{Other:{}},$an:function(){return[shared(),actual()];}};
        })(Empty.$$.prototype);
    }
    return Empty;
}
exports.$init$Empty=$init$Empty;
$init$Empty();
function empty$185(){
    var $$empty=new empty$185.$$;
    Object$($$empty);
    Empty($$empty);
    return $$empty;
}
function $init$empty$185(){
    if (empty$185.$$===undefined){
        initTypeProto(empty$185,'ceylon.language::empty',Object$,$init$Empty());
    }
    return empty$185;
}
exports.$init$empty$185=$init$empty$185;
$init$empty$185();
var empty$186=empty$185();
var getEmpty=function(){
    return empty$186;
}
exports.getEmpty=getEmpty;
function emptyIterator$187($$targs$$){
    var $$emptyIterator=new emptyIterator$187.$$;
    $$emptyIterator.$$targs$$=$$targs$$;
    Iterator($$emptyIterator);
    return $$emptyIterator;
}
function $init$emptyIterator$187(){
    if (emptyIterator$187.$$===undefined){
        initTypeProto(emptyIterator$187,'ceylon.language::emptyIterator',Basic,$init$Iterator());
    }
    return emptyIterator$187;
}
exports.$init$emptyIterator$187=$init$emptyIterator$187;
$init$emptyIterator$187();
(function($$emptyIterator){
    $$emptyIterator.next=function (){
        var $$emptyIterator=this;
        return getFinished();
    };
})(emptyIterator$187.$$.prototype);
var emptyIterator$188=emptyIterator$187({Element:{t:Nothing}});
var getEmptyIterator=function(){
    return emptyIterator$188;
}
exports.getEmptyIterator=getEmptyIterator;
function Finished($$finished){
    $init$Finished();
    if ($$finished===undefined)$$finished=new Finished.$$;
    return $$finished;
}
Finished.$$metamodel$$={$nm:'Finished',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[doc(String$("The type of the value that indicates that \nan `Iterator` is exhausted and has no more \nvalues to return.",104)),see([Iterator].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared(),abstract()];}};
exports.Finished=Finished;
function $init$Finished(){
    if (Finished.$$===undefined){
        initTypeProto(Finished,'ceylon.language::Finished',Basic);
    }
    return Finished;
}
exports.$init$Finished=$init$Finished;
$init$Finished();
function finished$189(){
    var $$finished=new finished$189.$$;
    Finished($$finished);
    return $$finished;
}
function $init$finished$189(){
    if (finished$189.$$===undefined){
        initTypeProto(finished$189,'ceylon.language::finished',Finished);
    }
    return finished$189;
}
exports.$init$finished$189=$init$finished$189;
$init$finished$189();
(function($$finished){
    defineAttr($$finished,'string',function(){
        var $$finished=this;
        return String$("finished",8);
    });
})(finished$189.$$.prototype);
var finished$190=finished$189();
var getFinished=function(){
    return finished$190;
}
exports.getFinished=getFinished;
function Keys(correspondence$191, $$targs$$,$$keys){
    $init$Keys();
    if ($$keys===undefined)$$keys=new Keys.$$;
    set_type_args($$keys,$$targs$$);
    $$keys.correspondence$191=correspondence$191;
    Category($$keys);
    return $$keys;
}
Keys.$$metamodel$$={$nm:'Keys',$mt:'cls','super':{t:Basic},$tp:{Key:{'var':'in','satisfies':[{t:Object$}]},Item:{'var':'out',}},satisfies:[{t:Category}]};
function $init$Keys(){
    if (Keys.$$===undefined){
        initTypeProto(Keys,'ceylon.language::Keys',Basic,$init$Category());
        (function($$keys){
            $$keys.contains=function contains(key$192){
                var $$keys=this;
                var key$193;
                if(isOfType((key$193=key$192),$$keys.$$targs$$.Key)){
                    return $$keys.correspondence$191.defines(key$193);
                }else {
                    return false;
                }
            };$$keys.contains.$$metamodel$$={$nm:'contains',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'key',$mt:'prm',$t:{t:Object$}}],$an:function(){return[shared(),actual()];}};
        })(Keys.$$.prototype);
    }
    return Keys;
}
exports.$init$Keys=$init$Keys;
$init$Keys();
function Correspondence($$correspondence){
}
Correspondence.$$metamodel$$={$nm:'Correspondence',$mt:'ifc',$tp:{Key:{'var':'in','satisfies':[{t:Object$}]},Item:{'var':'out',}},satisfies:[],$an:function(){return[doc(String$("Abstract supertype of objects which associate values \nwith keys. `Correspondence` does not satisfy `Category`,\nsince in some cases&mdash;`List`, for example&mdash;it is \nconvenient to consider the subtype a `Category` of its\nvalues, and in other cases&mdash;`Map`, for example&mdash;it \nis convenient to treat the subtype as a `Category` of its\nentries.\n\nThe item corresponding to a given key may be obtained \nfrom a `Correspondence` using the item operator:\n\n    value bg = settings[\"backgroundColor\"] else white;\n\nThe `get()` operation and item operator result in an\noptional type, to reflect the possibility that there is\nno item for the given key.",651)),see([Map,List,Category].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Correspondence=Correspondence;
function $init$Correspondence(){
    if (Correspondence.$$===undefined){
        initTypeProto(Correspondence,'ceylon.language::Correspondence');
        (function($$correspondence){
            $$correspondence.defines=function (key$194){
                var $$correspondence=this;
                return exists($$correspondence.get(key$194));
            };
            $$correspondence.defines.$$metamodel$$={$nm:'defines',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'key',$mt:'prm',$t:'Key'}],$an:function(){return[doc(String$("Determines if there is a value defined for the \ngiven key.",58)),see([$JsCallable($$correspondence.definesAny,[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:$$correspondence.$$targs$$.Key}},Element:{t:Iterable,a:{Absent:{t:Null},Element:$$correspondence.$$targs$$.Key}}}},Return:{t:Boolean$}}),$JsCallable($$correspondence.definesEvery,[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:$$correspondence.$$targs$$.Key}},Element:{t:Iterable,a:{Absent:{t:Null},Element:$$correspondence.$$targs$$.Key}}}},Return:{t:Boolean$}}),$$correspondence.keys].reifyCeylonType({Absent:{t:Null},Element:{ t:'u', l:[{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:$$correspondence.$$targs$$.Key}},Element:{t:Iterable,a:{Absent:{t:Null},Element:$$correspondence.$$targs$$.Key}}}},Return:{t:Boolean$}}},{t:Category}]}})),shared(),$default()];}};
            defineAttr($$correspondence,'keys',function(){
                var $$correspondence=this;
                return Keys($$correspondence,{Key:$$correspondence.$$targs$$.Key,Item:$$correspondence.$$targs$$.Item});
            });
            $$correspondence.definesEvery=function definesEvery(keys$195){
                var $$correspondence=this;
                var it$196 = keys$195.iterator();
                var key$197;while ((key$197=it$196.next())!==getFinished()){
                    if((!$$correspondence.defines(key$197))){
                        return false;
                    }
                }
                if (getFinished() === key$197){
                    return true;
                }
            };$$correspondence.definesEvery.$$metamodel$$={$nm:'definesEvery',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$an:function(){return[doc(String$("Determines if this `Correspondence` defines a value\nfor every one of the given keys.",84)),see([$JsCallable($$correspondence.defines,[{$nm:'key',$mt:'prm',$t:'Key'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$correspondence.$$targs$$.Key,Element:$$correspondence.$$targs$$.Key}},Return:{t:Boolean$}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$correspondence.$$targs$$.Key,Element:$$correspondence.$$targs$$.Key}},Return:{t:Boolean$}}}})),shared(),$default()];}};$$correspondence.definesAny=function definesAny(keys$198){
                var $$correspondence=this;
                var it$199 = keys$198.iterator();
                var key$200;while ((key$200=it$199.next())!==getFinished()){
                    if($$correspondence.defines(key$200)){
                        return true;
                    }
                }
                if (getFinished() === key$200){
                    return false;
                }
            };$$correspondence.definesAny.$$metamodel$$={$nm:'definesAny',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$an:function(){return[doc(String$("Determines if this `Correspondence` defines a value\nfor any one of the given keys.",82)),see([$JsCallable($$correspondence.defines,[{$nm:'key',$mt:'prm',$t:'Key'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$correspondence.$$targs$$.Key,Element:$$correspondence.$$targs$$.Key}},Return:{t:Boolean$}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$correspondence.$$targs$$.Key,Element:$$correspondence.$$targs$$.Key}},Return:{t:Boolean$}}}})),shared(),$default()];}};$$correspondence.items=function (keys$201){
                var $$correspondence=this;
                return Comprehension(function(){
                    var it$202=keys$201.iterator();
                    var key$203=getFinished();
                    var next$key$203=function(){return key$203=it$202.next();}
                    next$key$203();
                    return function(){
                        if(key$203!==getFinished()){
                            var key$203$204=key$203;
                            var tmpvar$205=$$correspondence.get(key$203$204);
                            next$key$203();
                            return tmpvar$205;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'u', l:[{t:Null},$$correspondence.$$targs$$.Item]}}).sequence;
            };
            $$correspondence.items.$$metamodel$$={$nm:'items',$mt:'mthd',$t:{t:Sequential,a:{Element:{ t:'u', l:[{t:Null},'Item']}}},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}}],$an:function(){return[doc(String$("Returns the items defined for the given keys, in\nthe same order as the corresponding keys.",90)),see([$JsCallable($$correspondence.get,[{$nm:'key',$mt:'prm',$t:'Key'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$correspondence.$$targs$$.Key,Element:$$correspondence.$$targs$$.Key}},Return:{ t:'u', l:[{t:Null},$$correspondence.$$targs$$.Item]}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$correspondence.$$targs$$.Key,Element:$$correspondence.$$targs$$.Key}},Return:{ t:'u', l:[{t:Null},$$correspondence.$$targs$$.Item]}}}})),shared(),$default()];}};
        })(Correspondence.$$.prototype);
    }
    return Correspondence;
}
exports.$init$Correspondence=$init$Correspondence;
$init$Correspondence();
function Closeable($$closeable){
}
Closeable.$$metamodel$$={$nm:'Closeable',$mt:'ifc',satisfies:[],$an:function(){return[doc(String$("Abstract supertype of types which may appear\nas the expression type of a resource expression\nin a `try` statement.",114)),shared()];}};
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
function Collection($$collection){
    Iterable($$collection);
    add_type_arg($$collection,'Absent',{t:Null});
    Cloneable($$collection);
    add_type_arg($$collection,'Clone',{t:Collection,a:{Element:$$collection.$$targs$$.Element}});
}
Collection.$$metamodel$$={$nm:'Collection',$mt:'ifc',$tp:{Element:{'var':'out',}},satisfies:[{t:Iterable,a:{Absent:{t:Null},Element:'Element'}},{t:Cloneable,a:{Clone:{t:Collection,a:{Element:'Element'}}}}],$an:function(){return[doc(String$("Represents an iterable collection of elements of finite \nsize. `Collection` is the abstract supertype of `List`,\n`Map`, and `Set`.\n\nA `Collection` forms a `Category` of its elements.\n\nAll `Collection`s are `Cloneable`. If a collection is\nimmutable, it is acceptable that `clone` produce a\nreference to the collection itself. If a collection is\nmutable, `clone` should produce an immutable collection\ncontaining references to the same elements, with the\nsame structure as the original collection&mdash;that \nis, it should produce an immutable shallow copy of the\ncollection.",573)),see([List,Map,Set].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.Collection=Collection;
function $init$Collection(){
    if (Collection.$$===undefined){
        initTypeProto(Collection,'ceylon.language::Collection',$init$Iterable(),$init$Cloneable());
        (function($$collection){
            defineAttr($$collection,'empty',function(){
                var $$collection=this;
                return $$collection.size.equals((0));
            });
            $$collection.contains=function contains(element$206){
                var $$collection=this;
                var it$207 = $$collection.iterator();
                var elem$208;while ((elem$208=it$207.next())!==getFinished()){
                    var elem$209;
                    if((elem$209=elem$208)!==null&&elem$209.equals(element$206)){
                        return true;
                    }
                }
                if (getFinished() === elem$208){
                    return false;
                }
            };$$collection.contains.$$metamodel$$={$nm:'contains',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Return `true` if the given object is an element of\nthis collection. In this default implementation,\nand in most refining implementations, return `false`\notherwise. An acceptable refining implementation\nmay return `true` for objects which are not \nelements of the collection, but this is not \nrecommended. (For example, the `contains()` method \nof `String` returns `true` for any substring of the\nstring.)",404)),shared(),actual(),$default()];}};defineAttr($$collection,'string',function(){
                var $$collection=this;
                return (opt$210=($$collection.empty?String$("{}",2):null),opt$210!==null?opt$210:StringBuilder().appendAll([String$("{ ",2),commaList($$collection).string,String$(" }",2)]).string);
            });
        })(Collection.$$.prototype);
    }
    return Collection;
}
exports.$init$Collection=$init$Collection;
$init$Collection();
var opt$210;
function Cloneable($$cloneable){
}
Cloneable.$$metamodel$$={$nm:'Cloneable',$mt:'ifc',$tp:{Clone:{'var':'out','satisfies':[{t:Cloneable,a:{Clone:'Clone'}}]}},satisfies:[],$an:function(){return[doc(String$("Abstract supertype of objects whose value can be \ncloned.",57)),shared()];}};
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
function Container($$container){
    Category($$container);
}
Container.$$metamodel$$={$nm:'Container',$mt:'ifc',$tp:{Element:{'var':'out',},Absent:{'var':'out','satisfies':[{t:Null}],'def':{t:Null}}},satisfies:[{t:Category}],$an:function(){return[doc(String$("Abstract supertype of objects which may or may not\ncontain one of more other values, called *elements*,\nand provide an operation for accessing the first \nelement, if any. A container which may or may not be \nempty is a `Container<Element,Null>`. A container which \nis always empty is a `Container<Nothing,Null>`. A \ncontainer which is never empty is a \n`Container<Element,Nothing>`.",382)),see([Category].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),deprecated(String$("Will be removed in Ceylon 1.0.",30)),shared()];}};
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
function Binary($$binary){
}
Binary.$$metamodel$$={$nm:'Binary',$mt:'ifc',$tp:{Other:{'satisfies':[{t:Binary,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[doc(String$("Abstraction of numeric types that consist in\na sequence of bits, like `Integer`.",80)),see([Integer].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Stef",4)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Binary=Binary;
function $init$Binary(){
    if (Binary.$$===undefined){
        initTypeProto(Binary,'ceylon.language::Binary');
        (function($$binary){
            $$binary.clear=function clear(index$211){
                var $$binary=this;
                return $$binary.set(index$211,false);
            };$$binary.clear.$$metamodel$$={$nm:'clear',$mt:'mthd',$t:'Other',$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns a new number with the given bit set to 0.\nBits are indexed from right to left.",86)),shared(),$default()];}};
        })(Binary.$$.prototype);
    }
    return Binary;
}
exports.$init$Binary=$init$Binary;
$init$Binary();
function Category($$category){
}
Category.$$metamodel$$={$nm:'Category',$mt:'ifc',satisfies:[],$an:function(){return[doc(String$("Abstract supertype of objects that contain other \nvalues, called *elements*, where it is possible to \nefficiently determine if a given value is an element. \n`Category` does not satisfy `Container`, because it is \nconceptually possible to have a `Category` whose \nemptiness cannot be computed.\n\nThe `in` operator may be used to determine if a value\nbelongs to a `Category`:\n\n    if (\"hello\" in \"hello world\") { ... }\n    if (69 in 0..100) { ... }\n    if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n\nOrdinarily, `x==y` implies that `x in cat == y in cat`.\nBut this contract is not required since it is possible\nto form a meaningful `Category` using a different\nequivalence relation. For example, an `IdentitySet` is\na meaningful `Category`.",755)),see([Container].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Category=Category;
function $init$Category(){
    if (Category.$$===undefined){
        initTypeProto(Category,'ceylon.language::Category');
        (function($$category){
            $$category.containsEvery=function containsEvery(elements$212){
                var $$category=this;
                var it$213 = elements$212.iterator();
                var element$214;while ((element$214=it$213.next())!==getFinished()){
                    if((!$$category.contains(element$214))){
                        return false;
                    }
                }
                if (getFinished() === element$214){
                    return true;
                }
            };$$category.containsEvery.$$metamodel$$={$nm:'containsEvery',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Object$}}}}],$an:function(){return[doc(String$("Determines if every one of the given values belongs\nto this `Category`.",71)),see([$JsCallable($$category.contains,[{$nm:'element',$mt:'prm',$t:{t:Object$}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Return:{t:Boolean$}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Return:{t:Boolean$}}}})),shared(),$default()];}};$$category.containsAny=function containsAny(elements$215){
                var $$category=this;
                var it$216 = elements$215.iterator();
                var element$217;while ((element$217=it$216.next())!==getFinished()){
                    if($$category.contains(element$217)){
                        return true;
                    }
                }
                if (getFinished() === element$217){
                    return false;
                }
            };$$category.containsAny.$$metamodel$$={$nm:'containsAny',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Object$}}}}],$an:function(){return[doc(String$("Determines if any one of the given values belongs \nto this `Category`",69)),see([$JsCallable($$category.contains,[{$nm:'element',$mt:'prm',$t:{t:Object$}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Return:{t:Boolean$}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Object$},Element:{t:Object$}}},Return:{t:Boolean$}}}})),shared(),$default()];}};
        })(Category.$$.prototype);
    }
    return Category;
}
exports.$init$Category=$init$Category;
$init$Category();
function Iterator($$iterator){
}
Iterator.$$metamodel$$={$nm:'Iterator',$mt:'ifc',$tp:{Element:{'var':'out',}},satisfies:[],$an:function(){return[doc(String$("Produces elements of an `Iterable` object. Classes that \nimplement this interface should be immutable.",102)),see([Iterable].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function Ranged($$ranged){
}
Ranged.$$metamodel$$={$nm:'Ranged',$mt:'ifc',$tp:{Index:{'var':'in','satisfies':[{t:Comparable,a:{Other:'Index'}}]},Span:{'var':'out',}},satisfies:[],$an:function(){return[doc(String$("Abstract supertype of ranged objects which map a range\nof `Comparable` keys to ranges of values. The type\nparameter `Span` abstracts the type of the resulting\nrange.\n\nA span may be obtained from an instance of `Ranged`\nusing the span operator:\n\n    print(\"hello world\"[0..5])\n",276)),see([List,Sequence,String].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
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
function ChainedIterator(first$218, second$219, $$targs$$,$$chainedIterator){
    $init$ChainedIterator();
    if ($$chainedIterator===undefined)$$chainedIterator=new ChainedIterator.$$;
    set_type_args($$chainedIterator,$$targs$$);
    $$chainedIterator.second$219=second$219;
    Iterator($$chainedIterator);
    $$chainedIterator.iter$220_=/*anotaciones:ceylon.language::Variable,*/first$218.iterator();
    $$chainedIterator.more$221_=/*anotaciones:ceylon.language::Variable,*/true;
    return $$chainedIterator;
}
ChainedIterator.$$metamodel$$={$nm:'ChainedIterator',$mt:'cls','super':{t:Basic},$tp:{Element:{'var':'out',},Other:{'var':'out',}},satisfies:[{t:Iterator,a:{Element:{ t:'u', l:['Element','Other']}}}],$an:function(){return[doc(String$("An `Iterator` that returns the elements of two\n`Iterable`s, as if they were chained together.",93)),by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}}))];}};
function $init$ChainedIterator(){
    if (ChainedIterator.$$===undefined){
        initTypeProto(ChainedIterator,'ceylon.language::ChainedIterator',Basic,$init$Iterator());
        (function($$chainedIterator){
            defineAttr($$chainedIterator,'iter$220',function(){return this.iter$220_;},function(iter$222){return this.iter$220_=iter$222;});
            defineAttr($$chainedIterator,'more$221',function(){return this.more$221_;},function(more$223){return this.more$221_=more$223;});
            $$chainedIterator.next=function next(){
                var $$chainedIterator=this;
                var e$224=$$chainedIterator.iter$220.next();
                var setE$224=function(e$225){return e$224=e$225;};
                var f$226;
                if(isOfType((f$226=e$224),{t:Finished})){
                    if($$chainedIterator.more$221){
                        $$chainedIterator.iter$220=$$chainedIterator.second$219.iterator();
                        $$chainedIterator.more$221=false;
                        e$224=$$chainedIterator.iter$220.next();
                    }
                }
                return e$224;
            };$$chainedIterator.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element','Other',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};
        })(ChainedIterator.$$.prototype);
    }
    return ChainedIterator;
}
exports.$init$ChainedIterator=$init$ChainedIterator;
$init$ChainedIterator();
function Tuple(first$227, rest$228, $$targs$$,$$tuple){
    $init$Tuple();
    if ($$tuple===undefined)$$tuple=new Tuple.$$;
    set_type_args($$tuple,$$targs$$);
    Object$($$tuple);
    Sequence($$tuple);
    Cloneable($$tuple);
    add_type_arg($$tuple,'Clone',{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}});
    $$tuple.first$229_=first$227;
    $$tuple.rest$230_=rest$228;
    return $$tuple;
}
Tuple.$$metamodel$$={$nm:'Tuple',$mt:'cls','super':{t:Object$},$tp:{Element:{'var':'out',},First:{'var':'out','satisfies':['Element']},Rest:{'var':'out','satisfies':[{t:Sequential,a:{Element:'Element'}}],'def':{t:Empty}}},satisfies:[{t:Sequence,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Element'}}}}],$an:function(){return[doc(String$("A _tuple_ is a typed linked list. Each instance of \n`Tuple` represents the value and type of a single link.\nThe attributes `first` and `rest` allow us to retrieve\na value form the list without losing its static type \ninformation.\n\n    value point = Tuple(0.0, Tuple(0.0, Tuple(\"origin\")));\n    Float x = point.first;\n    Float y = point.rest.first;\n    String label = point.rest.rest.first;\n\nUsually, we abbreviate code involving tuples.\n\n    [Float,Float,String] point = [0.0, 0.0, \"origin\"];\n    Float x = point[0];\n    Float y = point[1];\n    String label = point[2];\n\nA list of types enclosed in brackets is an abbreviated \ntuple type. An instance of `Tuple` may be constructed \nby surrounding a value list in brackets:\n\n    [String,String] words = [\"hello\", \"world\"];\n\nThe index operator with a literal integer argument is a \nshortcut for a chain of evaluations of `rest` and \n`first`. For example, `point[1]` means `point.rest.first`.\n\nA _terminated_ tuple type is a tuple where the type of\nthe last link in the chain is `Empty`. An _unterminated_ \ntuple type is a tuple where the type of the last link\nin the chain is `Sequence` or `Sequential`. Thus, a \nterminated tuple type has a length that is known\nstatically. For an unterminated tuple type only a lower\nbound on its length is known statically.\n\nHere, `point` is an unterminated tuple:\n\n    String[] labels = ... ;\n    [Float,Float,String*] point = [0.0, 0.0, *labels];\n    Float x = point[0];\n    Float y = point[1];\n    String? firstLabel = point[2];\n    String[] allLabels = point[2...];\n\n",1555)),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Tuple=Tuple;
function $init$Tuple(){
    if (Tuple.$$===undefined){
        initTypeProto(Tuple,'ceylon.language::Tuple',Object$,$init$Sequence(),$init$Cloneable());
        (function($$tuple){
            defineAttr($$tuple,'first',function(){return this.first$229_;});
            defineAttr($$tuple,'rest',function(){return this.rest$230_;});
            defineAttr($$tuple,'size',function(){
                var $$tuple=this;
                return (1).plus($$tuple.rest.size);
            });
            $$tuple.get=function get(index$231){
                var $$tuple=this;
                
                var case$232=index$231.compare((0));
                if (case$232===getSmaller()) {
                    return null;
                }else if (case$232===getEqual()) {
                    return $$tuple.first;
                }else if (case$232===getLarger()) {
                    return $$tuple.rest.get(index$231.minus((1)));
                }
            };$$tuple.get.$$metamodel$$={$nm:'get',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};defineAttr($$tuple,'lastIndex',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,*/{
                var $$tuple=this;
                var restLastIndex$233;
                if((restLastIndex$233=$$tuple.rest.lastIndex)!==null){
                    return restLastIndex$233.plus((1));
                }else {
                    return (0);
                }
            });defineAttr($$tuple,'last',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,*/{
                var $$tuple=this;
                var rest$234;
                if(nonempty((rest$234=$$tuple.rest))){
                    return rest$234.last;
                }else {
                    return $$tuple.first;
                }
            });defineAttr($$tuple,'reversed',function(){
                var $$tuple=this;
                return $$tuple.rest.reversed.withTrailing($$tuple.first,{Other:$$tuple.$$targs$$.First});
            });
            $$tuple.segment=function segment(from$235,length$236){
                var $$tuple=this;
                if((length$236.compare((0))!==getLarger())){
                    return getEmpty();
                }
                var realFrom$237=(opt$238=(from$235.compare((0)).equals(getSmaller())?(0):null),opt$238!==null?opt$238:from$235);
                var opt$238;
                if(realFrom$237.equals((0))){
                    return (opt$239=(length$236.equals((1))?Tuple($$tuple.first,getEmpty(),{Rest:{t:Empty},First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.First}):null),opt$239!==null?opt$239:$$tuple.rest.segment((0),length$236.plus(realFrom$237).minus((1))).withLeading($$tuple.first,{Other:$$tuple.$$targs$$.First}));
                    var opt$239;
                }
                return $$tuple.rest.segment(realFrom$237.minus((1)),length$236);
            };$$tuple.segment.$$metamodel$$={$nm:'segment',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$tuple.span=function span(from$240,end$241){
                var $$tuple=this;
                if((from$240.compare((0)).equals(getSmaller())&&end$241.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var realFrom$242=(opt$243=(from$240.compare((0)).equals(getSmaller())?(0):null),opt$243!==null?opt$243:from$240);
                var opt$243;
                var realEnd$244=(opt$245=(end$241.compare((0)).equals(getSmaller())?(0):null),opt$245!==null?opt$245:end$241);
                var opt$245;
                return (opt$246=((realFrom$242.compare(realEnd$244)!==getLarger())?$$tuple.segment(from$240,realEnd$244.minus(realFrom$242).plus((1))):null),opt$246!==null?opt$246:$$tuple.segment(realEnd$244,realFrom$242.minus(realEnd$244).plus((1))).reversed.sequence);
                var opt$246;
            };$$tuple.span.$$metamodel$$={$nm:'span',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'end',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$tuple.spanTo=function (to$247){
                var $$tuple=this;
                return (opt$248=(to$247.compare((0)).equals(getSmaller())?getEmpty():null),opt$248!==null?opt$248:$$tuple.span((0),to$247));
            };
            $$tuple.spanTo.$$metamodel$$={$nm:'spanTo',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$tuple.spanFrom=function (from$249){
                var $$tuple=this;
                return $$tuple.span(from$249,$$tuple.size);
            };
            $$tuple.spanFrom.$$metamodel$$={$nm:'spanFrom',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            defineAttr($$tuple,'clone',function(){
                var $$tuple=this;
                return $$tuple;
            });
            $$tuple.iterator=function iterator(){
                var $$tuple=this;
                function iterator$250($$targs$$){
                    var $$iterator$250=new iterator$250.$$;
                    $$iterator$250.$$targs$$=$$targs$$;
                    Iterator($$iterator$250);
                    $$iterator$250.current$251_=/*anotaciones:ceylon.language::Variable,*/$$tuple;
                    return $$iterator$250;
                }
                function $init$iterator$250(){
                    if (iterator$250.$$===undefined){
                        initTypeProto(iterator$250,'ceylon.language::Tuple.iterator.iterator',Basic,$init$Iterator());
                    }
                    return iterator$250;
                }
                $init$iterator$250();
                (function($$iterator$250){
                    defineAttr($$iterator$250,'current$251',function(){return this.current$251_;},function(current$252){return this.current$251_=current$252;});
                    $$iterator$250.next=function next(){
                        var $$iterator$250=this;
                        var c$253;
                        if(nonempty((c$253=$$iterator$250.current$251))){
                            $$iterator$250.current$251=c$253.rest;
                            return c$253.first;
                        }else {
                            return getFinished();
                        }
                    };$$iterator$250.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};
                })(iterator$250.$$.prototype);
                var iterator$254=iterator$250({Element:$$tuple.$$targs$$.Element});
                var getIterator$254=function(){
                    return iterator$254;
                }
                return getIterator$254();
            };$$tuple.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual(),$default()];}};$$tuple.contains=function contains(element$255){
                var $$tuple=this;
                var first$256;
                if((first$256=$$tuple.first)!==null&&first$256.equals(element$255)){
                    return true;
                }else {
                    return $$tuple.rest.contains(element$255);
                }
            };$$tuple.contains.$$metamodel$$={$nm:'contains',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$an:function(){return[shared(),actual(),$default()];}};
        })(Tuple.$$.prototype);
    }
    return Tuple;
}
exports.$init$Tuple=$init$Tuple;
$init$Tuple();
var opt$248;
function List($$list){
    Collection($$list);
    Correspondence($$list);
    add_type_arg($$list,'Key',{t:Integer});
    Ranged($$list);
    add_type_arg($$list,'Index',{t:Integer});
    add_type_arg($$list,'Span',{t:List,a:{Element:$$list.$$targs$$.Element}});
    Cloneable($$list);
    add_type_arg($$list,'Clone',{t:List,a:{Element:$$list.$$targs$$.Element}});
}
List.$$metamodel$$={$nm:'List',$mt:'ifc',$tp:{Element:{'var':'out',}},satisfies:[{t:Collection,a:{Element:'Element'}},{t:Correspondence,a:{Key:{t:Integer},Item:'Element'}},{t:Ranged,a:{Index:{t:Integer},Span:{t:List,a:{Element:'Element'}}}},{t:Cloneable,a:{Clone:{t:List,a:{Element:'Element'}}}}],$an:function(){return[doc(String$("Represents a collection in which every element has a \nunique non-negative integer index.\n\nA `List` is a `Collection` of its elements, and a \n`Correspondence` from indices to elements.\n\nDirect access to a list element by index produces a\nvalue of optional type. The following idiom may be\nused instead of upfront bounds-checking, as long as \nthe list element type is a non-`null` type:\n\n    value char = \"hello world\"[index];\n    if (exists char) { /*do something*/ }\n    else { /*out of bounds*/ }\n\nTo iterate the indexes of a `List`, use the following\nidiom:\n\n    for (i->char in \"hello world\".indexed) { ... }\n\n",613)),see([Sequence,Empty,Array].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.List=List;
function $init$List(){
    if (List.$$===undefined){
        initTypeProto(List,'ceylon.language::List',$init$Collection(),$init$Correspondence(),$init$Ranged(),$init$Cloneable());
        (function($$list){
            defineAttr($$list,'size',function(){
                var $$list=this;
                return (opt$257=$$list.lastIndex,opt$257!==null?opt$257:(-(1))).plus((1));
            });
            $$list.defines=function (index$258){
                var $$list=this;
                return (index$258.compare((opt$259=$$list.lastIndex,opt$259!==null?opt$259:(-(1))))!==getLarger());
            };
            $$list.defines.$$metamodel$$={$nm:'defines',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Determines if the given index refers to an element\nof this sequence, that is, if\n`index<=sequence.lastIndex`.",109)),shared(),actual(),$default()];}};
            $$list.iterator=function iterator(){
                var $$list=this;
                function listIterator$260($$targs$$){
                    var $$listIterator$260=new listIterator$260.$$;
                    $$listIterator$260.$$targs$$=$$targs$$;
                    Iterator($$listIterator$260);
                    $$listIterator$260.index$261_=/*anotaciones:ceylon.language::Variable,*/(0);
                    return $$listIterator$260;
                }
                function $init$listIterator$260(){
                    if (listIterator$260.$$===undefined){
                        initTypeProto(listIterator$260,'ceylon.language::List.iterator.listIterator',Basic,$init$Iterator());
                    }
                    return listIterator$260;
                }
                $init$listIterator$260();
                (function($$listIterator$260){
                    defineAttr($$listIterator$260,'index$261',function(){return this.index$261_;},function(index$262){return this.index$261_=index$262;});
                    $$listIterator$260.next=function next(){
                        var $$listIterator$260=this;
                        if(($$listIterator$260.index$261.compare((opt$263=$$list.lastIndex,opt$263!==null?opt$263:(-(1))))!==getLarger())){
                            //assert at List.ceylon (61:20-61:65)
                            var elem$264;
                            if (!(isOfType((elem$264=$$list.get((oldindex$265=$$listIterator$260.index$261,$$listIterator$260.index$261=oldindex$265.successor,oldindex$265))),$$list.$$targs$$.Element))) { throw AssertionException('Assertion failed: \'is Element elem = outer.get(index++)\' at List.ceylon (61:27-61:64)'); }
                            var oldindex$265;
                            return elem$264;
                        }else {
                            return getFinished();
                        }
                        var opt$263;
                    };$$listIterator$260.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};
                })(listIterator$260.$$.prototype);
                var listIterator$266=listIterator$260({Element:$$list.$$targs$$.Element});
                var getListIterator$266=function(){
                    return listIterator$266;
                }
                return getListIterator$266();
            };$$list.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual(),$default()];}};$$list.equals=function equals(that$267){
                var $$list=this;
                var that$268;
                if(isOfType((that$268=that$267),{t:List,a:{Element:{t:Anything}}})){
                    if(that$268.size.equals($$list.size)){
                        var it$269 = Range((0),$$list.size.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$270;while ((i$270=it$269.next())!==getFinished()){
                            var x$271=$$list.get(i$270);
                            var y$272=that$268.get(i$270);
                            var x$273;
                            if((x$273=x$271)!==null){
                                var y$274;
                                if((y$274=y$272)!==null){
                                    if((!x$273.equals(y$274))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$275;
                                if((y$275=y$272)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$270){
                            return true;
                        }
                    }
                }
                return false;
            };$$list.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Two `List`s are considered equal iff they have the \nsame `size` and _entry sets_. The entry set of a \nlist `l` is the set of elements of `l.indexed`. \nThis definition is equivalent to the more intuitive \nnotion that two lists are equal iff they have the \nsame `size` and for every index either:\n\n- the lists both have the element `null`, or\n- the lists both have a non-null element, and the\n  two elements are equal.",416)),shared(),actual(),$default()];}};defineAttr($$list,'hash',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$list=this;
                var hash$276=(1);
                var setHash$276=function(hash$277){return hash$276=hash$277;};
                var it$278 = $$list.iterator();
                var elem$279;while ((elem$279=it$278.next())!==getFinished()){
                    (hash$276=hash$276.times((31)));
                    var elem$280;
                    if((elem$280=elem$279)!==null){
                        (hash$276=hash$276.plus(elem$280.hash));
                    }
                }
                return hash$276;
            });defineAttr($$list,'first',function(){
                var $$list=this;
                return $$list.get((0));
            });
            defineAttr($$list,'last',function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$list=this;
                var i$281;
                if((i$281=$$list.lastIndex)!==null){
                    return $$list.get(i$281);
                }
                return null;
            });$$list.withLeading=function withLeading(element$282,$$$mptypes){
                var $$list=this;
                var sb$283=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                sb$283.append(element$282);
                if((!$$list.empty)){
                    sb$283.appendAll($$list);
                }
                //assert at List.ceylon (163:8-163:41)
                var seq$284;
                if (!(nonempty((seq$284=sb$283.sequence)))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (163:15-163:40)'); }
                return seq$284;
            };$$list.withLeading.$$metamodel$$={$nm:'withLeading',$mt:'mthd',$t:{t:Sequence,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$tp:{Other:{}},$an:function(){return[doc(String$("Returns a new `List` that starts with the specified\nelement, followed by the elements of this `List`.",101)),see([$$list.following].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared(),$default()];}};$$list.withTrailing=function withTrailing(element$285,$$$mptypes){
                var $$list=this;
                var sb$286=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                if((!$$list.empty)){
                    sb$286.appendAll($$list);
                }
                sb$286.append(element$285);
                //assert at List.ceylon (178:8-178:41)
                var seq$287;
                if (!(nonempty((seq$287=sb$286.sequence)))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (178:15-178:40)'); }
                return seq$287;
            };$$list.withTrailing.$$metamodel$$={$nm:'withTrailing',$mt:'mthd',$t:{t:Sequence,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'element',$mt:'prm',$t:'Other'}],$tp:{Other:{}},$an:function(){return[doc(String$("Returns a new `List` that contains the specified\nelement appended to the end of the elements of this \n`List`.",109)),shared(),$default()];}};
        })(List.$$.prototype);
    }
    return List;
}
exports.$init$List=$init$List;
$init$List();
var opt$257,opt$259;
function Entry(key$288, item$289, $$targs$$,$$entry){
    $init$Entry();
    if ($$entry===undefined)$$entry=new Entry.$$;
    set_type_args($$entry,$$targs$$);
    Object$($$entry);
    $$entry.key$290_=key$288;
    $$entry.item$291_=item$289;
    return $$entry;
}
Entry.$$metamodel$$={$nm:'Entry',$mt:'cls','super':{t:Object$},$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[],$an:function(){return[doc(String$("A pair containing a _key_ and an associated value called \nthe _item_. Used primarily to represent the elements of \na `Map`. The type `Entry<Key,Item>` may be abbreviated \n`Key->Item`. An instance of `Entry` may be constructed \nusing the `->` operator:\n\n    String->Person entry = person.name->person;\n",301)),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Entry=Entry;
function $init$Entry(){
    if (Entry.$$===undefined){
        initTypeProto(Entry,'ceylon.language::Entry',Object$);
        (function($$entry){
            defineAttr($$entry,'key',function(){return this.key$290_;});
            defineAttr($$entry,'item',function(){return this.item$291_;});
            $$entry.equals=function equals(that$292){
                var $$entry=this;
                var that$293;
                if(isOfType((that$293=that$292),{t:Entry,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    return ($$entry.key.equals(that$293.key)&&$$entry.item.equals(that$293.item));
                }else {
                    return false;
                }
            };$$entry.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Determines if this entry is equal to the given\nentry. Two entries are equal if they have the same\nkey and the same value.",121)),shared(),actual()];}};defineAttr($$entry,'hash',function(){
                var $$entry=this;
                return (31).plus($$entry.key.hash).times((31)).plus($$entry.item.hash);
            });
            defineAttr($$entry,'string',function(){
                var $$entry=this;
                return StringBuilder().appendAll([$$entry.key.string,String$("->",2),$$entry.item.string]).string;
            });
        })(Entry.$$.prototype);
    }
    return Entry;
}
exports.$init$Entry=$init$Entry;
$init$Entry();
function Invertable($$invertable){
}
Invertable.$$metamodel$$={$nm:'Invertable',$mt:'ifc',$tp:{Inverse:{'var':'out',}},satisfies:[],$an:function(){return[doc(String$("Abstraction of types which support a unary additive inversion\noperation. For a numeric type, this should return the \nnegative of the argument value. Note that the type \nparameter of this interface is not restricted to be a \nself type, in order to accommodate the possibility of \ntypes whose additive inverse can only be expressed in terms of \na wider type.",356)),see([Integer,Float].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function Ordinal($$ordinal){
}
Ordinal.$$metamodel$$={$nm:'Ordinal',$mt:'ifc',$tp:{Other:{'var':'out','satisfies':[{t:Ordinal,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[doc(String$("Abstraction of ordinal types, that is, types with \nsuccessor and predecessor operations, including\n`Integer` and other `Integral` numeric types.\n`Character` is also considered an ordinal type. \n`Ordinal` types may be used to generate a `Range`.",244)),see([Character,Integer,Integral,Range].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function Enumerable($$enumerable){
    Ordinal($$enumerable);
}
Enumerable.$$metamodel$$={$nm:'Enumerable',$mt:'ifc',$tp:{Other:{'var':'out','satisfies':[{t:Enumerable,a:{Other:'Other'}}]}},satisfies:[{t:Ordinal,a:{Other:'Other'}}],$an:function(){return[doc(String$("Abstraction of ordinal types whose instances can be \nmapped to the integers or to a range of integers.",102)),shared()];}};
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
function Integral($$integral){
    Numeric($$integral);
    Enumerable($$integral);
}
Integral.$$metamodel$$={$nm:'Integral',$mt:'ifc',$tp:{Other:{'satisfies':[{t:Integral,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'Other'}},{t:Enumerable,a:{Other:'Other'}}],$an:function(){return[doc(String$("Abstraction of integral numeric types. That is, types \nwith no fractional part, including `Integer`. The \ndivision operation for integral numeric types results \nin a remainder. Therefore, integral numeric types have \nan operation to determine the remainder of any division \noperation.",284)),see([Integer].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function Numeric($$numeric){
    Summable($$numeric);
    Invertable($$numeric);
}
Numeric.$$metamodel$$={$nm:'Numeric',$mt:'ifc',$tp:{Other:{'satisfies':[{t:Numeric,a:{Other:'Other'}}]}},satisfies:[{t:Summable,a:{Other:'Other'}},{t:Invertable,a:{Inverse:'Other'}}],$an:function(){return[doc(String$("Abstraction of numeric types supporting addition,\nsubtraction, multiplication, and division, including\n`Integer` and `Float`. Additionally, a numeric type \nis expected to define a total order via an \nimplementation of `Comparable`.",231)),see([Integer,Float,Comparable].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function Exponentiable($$exponentiable){
    Numeric($$exponentiable);
}
Exponentiable.$$metamodel$$={$nm:'Exponentiable',$mt:'ifc',$tp:{This:{'satisfies':[{t:Exponentiable,a:{Other:'Other',This:'This'}}]},Other:{'satisfies':[{t:Numeric,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'This'}}],$an:function(){return[doc(String$("Abstraction of numeric types that may be raised to a\npower. Note that the type of the exponent may be\ndifferent to the numeric type which can be \nexponentiated.",160)),see([Integer,Float].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
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
function Comparable($$comparable){
}
Comparable.$$metamodel$$={$nm:'Comparable',$mt:'ifc',$tp:{Other:{'var':'in','satisfies':[{t:Comparable,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[doc(String$("The general contract for values whose magnitude can be \ncompared. `Comparable` imposes a total ordering upon\ninstances of any type that satisfies the interface.\nIf a type `T` satisfies `Comparable<T>`, then instances\nof `T` may be compared using the comparison operators\n`<`, `>`, `<=`, >=`, and `<=>`.\n\nThe total order of a type must be consistent with the \ndefinition of equality for the type. That is, there\nare three mutually exclusive possibilities:\n\n- `x<y`,\n- `x>y`, or\n- `x==y`",485)),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function Summable($$summable){
}
Summable.$$metamodel$$={$nm:'Summable',$mt:'ifc',$tp:{Other:{'satisfies':[{t:Summable,a:{Other:'Other'}}]}},satisfies:[],$an:function(){return[doc(String$("Abstraction of types which support a binary addition\noperator. For numeric types, this is just familiar \nnumeric addition. For strings, it is string \nconcatenation. In general, the addition operation \nshould be a binary associative operation.",242)),see([String,Numeric].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function Scalar($$scalar){
    Numeric($$scalar);
    Comparable($$scalar);
    Number($$scalar);
}
Scalar.$$metamodel$$={$nm:'Scalar',$mt:'ifc',$tp:{Other:{'satisfies':[{t:Scalar,a:{Other:'Other'}}]}},satisfies:[{t:Numeric,a:{Other:'Other'}},{t:Comparable,a:{Other:'Other'}},{t:Number$}],$an:function(){return[doc(String$("Abstraction of numeric types representing scalar\nvalues, including `Integer` and `Float`.",89)),see([Integer,Float].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
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
function InitializationException(description$294, $$initializationException){
    $init$InitializationException();
    if ($$initializationException===undefined)$$initializationException=new InitializationException.$$;
    Exception(description$294,null,$$initializationException);
    return $$initializationException;
}
InitializationException.$$metamodel$$={$nm:'InitializationException',$mt:'cls','super':{t:Exception},satisfies:[],$an:function(){return[doc(String$("Thrown when a problem was detected with value initialization.\n\nPossible problems include:\n\n* when a value could not be initialized due to recursive access during initialization, \n* an attempt to use a `late` value before it was initialized, \n* an attempt to assign to a `late` but non-`variable` value after it was initialized.",327)),see([$JsCallable(late,[],{Arguments:{t:Empty},Return:{t:Late}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Empty},Return:{t:Late}}}})),shared()];}};
exports.InitializationException=InitializationException;
function $init$InitializationException(){
    if (InitializationException.$$===undefined){
        initTypeProto(InitializationException,'ceylon.language::InitializationException',Exception);
    }
    return InitializationException;
}
exports.$init$InitializationException=$init$InitializationException;
$init$InitializationException();
function OverflowException($$overflowException){
    $init$OverflowException();
    if ($$overflowException===undefined)$$overflowException=new OverflowException.$$;
    Exception(String$("Numeric overflow",16),null,$$overflowException);
    return $$overflowException;
}
OverflowException.$$metamodel$$={$nm:'OverflowException',$mt:'cls','super':{t:Exception},satisfies:[],$an:function(){return[doc(String$("Thrown when a mathematical operation caused a number to overflow from its bounds.",81)),shared()];}};
exports.OverflowException=OverflowException;
function $init$OverflowException(){
    if (OverflowException.$$===undefined){
        initTypeProto(OverflowException,'ceylon.language::OverflowException',Exception);
    }
    return OverflowException;
}
exports.$init$OverflowException=$init$OverflowException;
$init$OverflowException();
function NegativeNumberException($$negativeNumberException){
    $init$NegativeNumberException();
    if ($$negativeNumberException===undefined)$$negativeNumberException=new NegativeNumberException.$$;
    Exception(String$("Negative number",15),null,$$negativeNumberException);
    return $$negativeNumberException;
}
NegativeNumberException.$$metamodel$$={$nm:'NegativeNumberException',$mt:'cls','super':{t:Exception},satisfies:[],$an:function(){return[doc(String$("Thrown when a negative number is not allowed.",45)),shared()];}};
exports.NegativeNumberException=NegativeNumberException;
function $init$NegativeNumberException(){
    if (NegativeNumberException.$$===undefined){
        initTypeProto(NegativeNumberException,'ceylon.language::NegativeNumberException',Exception);
    }
    return NegativeNumberException;
}
exports.$init$NegativeNumberException=$init$NegativeNumberException;
$init$NegativeNumberException();
function Set($$set){
    Collection($$set);
    Cloneable($$set);
    add_type_arg($$set,'Clone',{t:Set,a:{Element:$$set.$$targs$$.Element}});
}
Set.$$metamodel$$={$nm:'Set',$mt:'ifc',$tp:{Element:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Collection,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Set,a:{Element:'Element'}}}}],$an:function(){return[doc(String$("A collection of unique elements.\n\nA `Set` is a `Collection` of its elements.\n\nSets may be the subject of the binary union, \nintersection, exclusive union, and complement operators \n`|`, `&`, `^`, and `~`.",204)),shared()];}};
exports.Set=Set;
function $init$Set(){
    if (Set.$$===undefined){
        initTypeProto(Set,'ceylon.language::Set',$init$Collection(),$init$Cloneable());
        (function($$set){
            $$set.superset=function superset(set$295){
                var $$set=this;
                var it$296 = set$295.iterator();
                var element$297;while ((element$297=it$296.next())!==getFinished()){
                    if((!$$set.contains(element$297))){
                        return false;
                    }
                }
                if (getFinished() === element$297){
                    return true;
                }
            };$$set.superset.$$metamodel$$={$nm:'superset',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:{t:Object$}}}}],$an:function(){return[doc(String$("Determines if this `Set` is a superset of the \nspecified Set, that is, if this `Set` contains all \nof the elements in the specified `Set`.",138)),shared(),$default()];}};$$set.subset=function subset(set$298){
                var $$set=this;
                var it$299 = $$set.iterator();
                var element$300;while ((element$300=it$299.next())!==getFinished()){
                    if((!set$298.contains(element$300))){
                        return false;
                    }
                }
                if (getFinished() === element$300){
                    return true;
                }
            };$$set.subset.$$metamodel$$={$nm:'subset',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:{t:Object$}}}}],$an:function(){return[doc(String$("Determines if this `Set` is a subset of the given \n`Set`, that is, if the given set contains all of \nthe elements in this set.",126)),shared(),$default()];}};$$set.equals=function equals(that$301){
                var $$set=this;
                var that$302;
                if(isOfType((that$302=that$301),{t:Set,a:{Element:{t:Object$}}})&&that$302.size.equals($$set.size)){
                    var it$303 = $$set.iterator();
                    var element$304;while ((element$304=it$303.next())!==getFinished()){
                        if((!that$302.contains(element$304))){
                            return false;
                        }
                    }
                    if (getFinished() === element$304){
                        return true;
                    }
                }
                return false;
            };$$set.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Two `Set`s are considered equal if they have the \nsame size and if every element of the first set is\nalso an element of the second set, as determined\nby `contains()`.",166)),shared(),actual(),$default()];}};defineAttr($$set,'hash',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$set=this;
                var hashCode$305=(1);
                var setHashCode$305=function(hashCode$306){return hashCode$305=hashCode$306;};
                var it$307 = $$set.iterator();
                var elem$308;while ((elem$308=it$307.next())!==getFinished()){
                    (hashCode$305=hashCode$305.times((31)));
                    (hashCode$305=hashCode$305.plus(elem$308.hash));
                }
                return hashCode$305;
            });
        })(Set.$$.prototype);
    }
    return Set;
}
exports.$init$Set=$init$Set;
$init$Set();
function Range(first$309, last$310, $$targs$$,$$range){
    $init$Range();
    if ($$range===undefined)$$range=new Range.$$;
    set_type_args($$range,$$targs$$);
    Object$($$range);
    Sequence($$range);
    Cloneable($$range);
    add_type_arg($$range,'Clone',{t:Range,a:{Element:$$range.$$targs$$.Element}});
    $$range.first$311_=first$309;
    $$range.last$312_=last$310;
    return $$range;
}
Range.$$metamodel$$={$nm:'Range',$mt:'cls','super':{t:Object$},$tp:{Element:{'satisfies':[{t:Ordinal,a:{Other:'Element'}},{t:Comparable,a:{Other:'Element'}}]}},satisfies:[{t:Sequence,a:{Element:'Element'}},{t:Cloneable,a:{Clone:{t:Range,a:{Element:'Element'}}}}],$an:function(){return[doc(String$("Represents the range of totally ordered, ordinal values \ngenerated by two endpoints of type `Ordinal` and \n`Comparable`. If the first value is smaller than the\nlast value, the range is increasing. If the first value\nis larger than the last value, the range is decreasing.\nIf the two values are equal, the range contains exactly\none element. The range is always nonempty, containing \nat least one value.\n\nA range may be produced using the `..` operator:\n\n    for (i in min..max) { ... }\n    if (char in `A`..`Z`) { ... }\n",520)),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.Range=Range;
function $init$Range(){
    if (Range.$$===undefined){
        initTypeProto(Range,'ceylon.language::Range',Object$,$init$Sequence(),$init$Cloneable());
        (function($$range){
            defineAttr($$range,'first',function(){return this.first$311_;});
            defineAttr($$range,'last',function(){return this.last$312_;});
            defineAttr($$range,'string',function(){
                var $$range=this;
                return $$range.first.string.plus(String$("..",2)).plus($$range.last.string);
            });
            defineAttr($$range,'decreasing',function(){
                var $$range=this;
                return $$range.last.compare($$range.first).equals(getSmaller());
            });
            $$range.next$313=function (x$314){
                var $$range=this;
                return (opt$315=($$range.decreasing?x$314.predecessor:null),opt$315!==null?opt$315:x$314.successor);
            };
            $$range.next$313.$$metamodel$$={$nm:'next',$mt:'mthd',$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'}]};
            defineAttr($$range,'size',function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,ceylon.language::Actual,*/{
                var $$range=this;
                var last$316;
                var first$317;
                if(isOfType((last$316=$$range.last),{t:Enumerable,a:{Other:{t:Anything}}})&&isOfType((first$317=$$range.first),{t:Enumerable,a:{Other:{t:Anything}}})){
                    return last$316.integerValue.minus(first$317.integerValue).magnitude.plus((1));
                }else {
                    var size$318=(1);
                    var setSize$318=function(size$319){return size$318=size$319;};
                    var current$320=$$range.first;
                    var setCurrent$320=function(current$321){return current$320=current$321;};
                    while((!current$320.equals($$range.last))){
                        (oldsize$322=size$318,size$318=oldsize$322.successor,oldsize$322);
                        var oldsize$322;
                        current$320=$$range.next$313(current$320);
                    }
                    return size$318;
                }
            });defineAttr($$range,'lastIndex',function(){
                var $$range=this;
                return $$range.size.minus((1));
            });
            defineAttr($$range,'rest',function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,ceylon.language::Actual,*/{
                var $$range=this;
                if($$range.size.equals((1))){
                    return getEmpty();
                }
                var n$323=$$range.next$313($$range.first);
                return (opt$324=(n$323.equals($$range.last)?getEmpty():null),opt$324!==null?opt$324:Range(n$323,$$range.last,{Element:$$range.$$targs$$.Element}));
                var opt$324;
            });$$range.get=function get(n$325){
                var $$range=this;
                var index$326=(0);
                var setIndex$326=function(index$327){return index$326=index$327;};
                var x$328=$$range.first;
                var setX$328=function(x$329){return x$328=x$329;};
                while(index$326.compare(n$325).equals(getSmaller())){
                    if(x$328.equals($$range.last)){
                        return null;
                    }else {
                        (index$326=index$326.successor);
                        x$328=$$range.next$313(x$328);
                    }
                }
                return x$328;
            };$$range.get.$$metamodel$$={$nm:'get',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'n',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("The element of the range that occurs `n` values after\nthe start of the range. Note that this operation \nis inefficient for large ranges.",136)),shared(),actual()];}};$$range.iterator=function iterator(){
                var $$range=this;
                function RangeIterator$330($$rangeIterator$330){
                    $init$RangeIterator$330();
                    if ($$rangeIterator$330===undefined)$$rangeIterator$330=new RangeIterator$330.$$;
                    $$rangeIterator$330.$$targs$$={Element:$$range.$$targs$$.Element};
                    Iterator($$rangeIterator$330);
                    $$rangeIterator$330.current$331_=/*anotaciones:ceylon.language::Variable,*/$$range.first;
                    return $$rangeIterator$330;
                }
                RangeIterator$330.$$metamodel$$={$nm:'RangeIterator',$mt:'cls','super':{t:Basic},satisfies:[{t:Iterator,a:{Element:'Element'}}]};
                function $init$RangeIterator$330(){
                    if (RangeIterator$330.$$===undefined){
                        initTypeProto(RangeIterator$330,'ceylon.language::Range.iterator.RangeIterator',Basic,$init$Iterator());
                        (function($$rangeIterator$330){
                            defineAttr($$rangeIterator$330,'current$331',function(){return this.current$331_;},function(current$332){return this.current$331_=current$332;});
                            $$rangeIterator$330.next=function next(){
                                var $$rangeIterator$330=this;
                                var result$333=$$rangeIterator$330.current$331;
                                var curr$334;
                                if(!isOfType((curr$334=$$rangeIterator$330.current$331),{t:Finished})){
                                    if((opt$335=($$range.decreasing?(curr$334.compare($$range.last)!==getLarger()):null),opt$335!==null?opt$335:(curr$334.compare($$range.last)!==getSmaller()))){
                                        $$rangeIterator$330.current$331=getFinished();
                                    }else {
                                        $$rangeIterator$330.current$331=$$range.next$313(curr$334);
                                    }
                                    var opt$335;
                                }
                                return result$333;
                            };$$rangeIterator$330.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};defineAttr($$rangeIterator$330,'string',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,*/{
                                var $$rangeIterator$330=this;
                                return String$("RangeIterator",13);
                            });
                        })(RangeIterator$330.$$.prototype);
                    }
                    return RangeIterator$330;
                }
                $init$RangeIterator$330();
                return RangeIterator$330();
            };$$range.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[doc(String$("An iterator for the elements of the range.",42)),shared(),actual()];}};$$range.by=function by(step$336){
                var $$range=this;
                //assert at Range.ceylon (113:8-114:25)
                if (!(step$336.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Range.ceylon (114:15-114:24)'); }
                if(step$336.equals((1))){
                    return $$range;
                }
                var first$337;
                var last$338;
                if(isOfType((first$337=$$range.first),{t:Integer})&&isOfType((last$338=$$range.last),{t:Integer})){
                    return integerRangeByIterable($$range,step$336,{Element:$$range.$$targs$$.Element});
                }
                return $$range.getT$all()['ceylon.language::Iterable'].$$.prototype.by.call(this,step$336);
            };$$range.by.$$metamodel$$={$nm:'by',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Element'}},$ps:[{$nm:'step',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$range.contains=function contains(element$339){
                var $$range=this;
                var element$340;
                if(isOfType((element$340=element$339),$$range.$$targs$$.Element)){
                    return $$range.includes(element$340);
                }else {
                    return false;
                }
            };$$range.contains.$$metamodel$$={$nm:'contains',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Determines if the range includes the given object.",50)),shared(),actual()];}};$$range.count=function count(selecting$341){
                var $$range=this;
                var e$342=$$range.first;
                var setE$342=function(e$343){return e$342=e$343;};
                var c$344=(0);
                var setC$344=function(c$345){return c$344=c$345;};
                while($$range.includes(e$342)){
                    if(selecting$341(e$342)){
                        (oldc$346=c$344,c$344=oldc$346.successor,oldc$346);
                        var oldc$346;
                    }
                    e$342=$$range.next$313(e$342);
                }
                return c$344;
            };$$range.count.$$metamodel$$={$nm:'count',$mt:'mthd',$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};$$range.includes=function (x$347){
                var $$range=this;
                return (opt$348=($$range.decreasing?((x$347.compare($$range.first)!==getLarger())&&(x$347.compare($$range.last)!==getSmaller())):null),opt$348!==null?opt$348:((x$347.compare($$range.first)!==getSmaller())&&(x$347.compare($$range.last)!==getLarger())));
            };
            $$range.includes.$$metamodel$$={$nm:'includes',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'x',$mt:'prm',$t:'Element'}],$an:function(){return[doc(String$("Determines if the range includes the given value.",49)),shared()];}};
            $$range.equals=function equals(that$349){
                var $$range=this;
                var that$350;
                if(isOfType((that$350=that$349),{t:Range,a:{Element:{t:Object$}}})){
                    return (that$350.first.equals($$range.first)&&that$350.last.equals($$range.last));
                }else {
                    return $$range.getT$all()['ceylon.language::List'].$$.prototype.equals.call(this,that$349);
                }
            };$$range.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Determines if two ranges are the same by comparing\ntheir endpoints.",67)),shared(),actual()];}};defineAttr($$range,'clone',function(){
                var $$range=this;
                return $$range;
            });
            $$range.segment=function segment(from$351,length$352){
                var $$range=this;
                if(((length$352.compare((0))!==getLarger())||from$351.compare($$range.lastIndex).equals(getLarger()))){
                    return getEmpty();
                }
                var x$353=$$range.first;
                var setX$353=function(x$354){return x$353=x$354;};
                var i$355=(0);
                var setI$355=function(i$356){return i$355=i$356;};
                while((oldi$357=i$355,i$355=oldi$357.successor,oldi$357).compare(from$351).equals(getSmaller())){
                    x$353=$$range.next$313(x$353);
                }
                var oldi$357;
                var y$358=x$353;
                var setY$358=function(y$359){return y$358=y$359;};
                var j$360=(1);
                var setJ$360=function(j$361){return j$360=j$361;};
                while(((oldj$362=j$360,j$360=oldj$362.successor,oldj$362).compare(length$352).equals(getSmaller())&&y$358.compare($$range.last).equals(getSmaller()))){
                    y$358=$$range.next$313(y$358);
                }
                var oldj$362;
                return Range(x$353,y$358,{Element:$$range.$$targs$$.Element});
            };$$range.segment.$$metamodel$$={$nm:'segment',$mt:'mthd',$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$range.span=function span(from$363,to$364){
                var $$range=this;
                var toIndex$365=to$364;
                var setToIndex$365=function(toIndex$366){return toIndex$365=toIndex$366;};
                var fromIndex$367=from$363;
                var setFromIndex$367=function(fromIndex$368){return fromIndex$367=fromIndex$368;};
                if(toIndex$365.compare((0)).equals(getSmaller())){
                    if(fromIndex$367.compare((0)).equals(getSmaller())){
                        return getEmpty();
                    }
                    toIndex$365=(0);
                }else {
                    if(toIndex$365.compare($$range.lastIndex).equals(getLarger())){
                        if(fromIndex$367.compare($$range.lastIndex).equals(getLarger())){
                            return getEmpty();
                        }
                        toIndex$365=$$range.lastIndex;
                    }
                }
                if(fromIndex$367.compare((0)).equals(getSmaller())){
                    fromIndex$367=(0);
                }else {
                    if(fromIndex$367.compare($$range.lastIndex).equals(getLarger())){
                        fromIndex$367=$$range.lastIndex;
                    }
                }
                var x$369=$$range.first;
                var setX$369=function(x$370){return x$369=x$370;};
                var i$371=(0);
                var setI$371=function(i$372){return i$371=i$372;};
                while((oldi$373=i$371,i$371=oldi$373.successor,oldi$373).compare(fromIndex$367).equals(getSmaller())){
                    x$369=$$range.next$313(x$369);
                }
                var oldi$373;
                var y$374=$$range.first;
                var setY$374=function(y$375){return y$374=y$375;};
                var j$376=(0);
                var setJ$376=function(j$377){return j$376=j$377;};
                while((oldj$378=j$376,j$376=oldj$378.successor,oldj$378).compare(toIndex$365).equals(getSmaller())){
                    y$374=$$range.next$313(y$374);
                }
                var oldj$378;
                return Range(x$369,y$374,{Element:$$range.$$targs$$.Element});
            };$$range.span.$$metamodel$$={$nm:'span',$mt:'mthd',$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$range.spanTo=function spanTo(to$379){
                var $$range=this;
                return (opt$380=(to$379.compare((0)).equals(getSmaller())?getEmpty():null),opt$380!==null?opt$380:$$range.span((0),to$379));
                var opt$380;
            };$$range.spanTo.$$metamodel$$={$nm:'spanTo',$mt:'mthd',$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$range.spanFrom=function spanFrom(from$381){
                var $$range=this;
                return $$range.span(from$381,$$range.size);
            };$$range.spanFrom.$$metamodel$$={$nm:'spanFrom',$mt:'mthd',$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};defineAttr($$range,'reversed',function(){
                var $$range=this;
                return Range($$range.last,$$range.first,{Element:$$range.$$targs$$.Element});
            });
            $$range.skipping=function skipping(skip$382){
                var $$range=this;
                var x$383=(0);
                var setX$383=function(x$384){return x$383=x$384;};
                var e$385=$$range.first;
                var setE$385=function(e$386){return e$385=e$386;};
                while((oldx$387=x$383,x$383=oldx$387.successor,oldx$387).compare(skip$382).equals(getSmaller())){
                    e$385=$$range.next$313(e$385);
                }
                var oldx$387;
                return (opt$388=($$range.includes(e$385)?Range(e$385,$$range.last,{Element:$$range.$$targs$$.Element}):null),opt$388!==null?opt$388:getEmpty());
                var opt$388;
            };$$range.skipping.$$metamodel$$={$nm:'skipping',$mt:'mthd',$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$range.taking=function taking(take$389){
                var $$range=this;
                if(take$389.equals((0))){
                    return getEmpty();
                }
                var x$390=(0);
                var setX$390=function(x$391){return x$390=x$391;};
                var e$392=$$range.first;
                var setE$392=function(e$393){return e$392=e$393;};
                while((x$390=x$390.successor).compare(take$389).equals(getSmaller())){
                    e$392=$$range.next$313(e$392);
                }
                return (opt$394=($$range.includes(e$392)?Range($$range.first,e$392,{Element:$$range.$$targs$$.Element}):null),opt$394!==null?opt$394:$$range);
                var opt$394;
            };$$range.taking.$$metamodel$$={$nm:'taking',$mt:'mthd',$t:{ t:'u', l:[{t:Range,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};defineAttr($$range,'coalesced',function(){
                var $$range=this;
                return $$range;
            });
            defineAttr($$range,'sequence',function(){
                var $$range=this;
                return $$range;
            });
        })(Range.$$.prototype);
    }
    return Range;
}
exports.$init$Range=$init$Range;
$init$Range();
var opt$315,opt$348;
function AssertionException(message$395, $$assertionException){
    $init$AssertionException();
    if ($$assertionException===undefined)$$assertionException=new AssertionException.$$;
    Exception(message$395,undefined,$$assertionException);
    return $$assertionException;
}
AssertionException.$$metamodel$$={$nm:'AssertionException',$mt:'cls','super':{t:Exception},satisfies:[],$an:function(){return[doc(String$("An exception that occurs when an assertion fails, that\nis, when a condition in an `assert` statement evaluates\nto false at runtime.",131)),shared()];}};
exports.AssertionException=AssertionException;
function $init$AssertionException(){
    if (AssertionException.$$===undefined){
        initTypeProto(AssertionException,'ceylon.language::AssertionException',Exception);
    }
    return AssertionException;
}
exports.$init$AssertionException=$init$AssertionException;
$init$AssertionException();
function Singleton(element$396, $$targs$$,$$singleton){
    $init$Singleton();
    if ($$singleton===undefined)$$singleton=new Singleton.$$;
    set_type_args($$singleton,$$targs$$);
    $$singleton.element$396=element$396;
    Object$($$singleton);
    Sequence($$singleton);
    return $$singleton;
}
Singleton.$$metamodel$$={$nm:'Singleton',$mt:'cls','super':{t:Object$},$tp:{Element:{'var':'out',}},satisfies:[{t:Sequence,a:{Element:'Element'}}],$an:function(){return[doc(String$("A sequence with exactly one element, which may be null.",55)),shared()];}};
exports.Singleton=Singleton;
function $init$Singleton(){
    if (Singleton.$$===undefined){
        initTypeProto(Singleton,'ceylon.language::Singleton',Object$,$init$Sequence());
        (function($$singleton){
            defineAttr($$singleton,'lastIndex',function(){
                var $$singleton=this;
                return (0);
            });
            defineAttr($$singleton,'size',function(){
                var $$singleton=this;
                return (1);
            });
            defineAttr($$singleton,'first',function(){
                var $$singleton=this;
                return $$singleton.element$396;
            });
            defineAttr($$singleton,'last',function(){
                var $$singleton=this;
                return $$singleton.element$396;
            });
            defineAttr($$singleton,'rest',function(){
                var $$singleton=this;
                return getEmpty();
            });
            $$singleton.get=function get(index$397){
                var $$singleton=this;
                if(index$397.equals((0))){
                    return $$singleton.element$396;
                }else {
                    return null;
                }
            };$$singleton.get.$$metamodel$$={$nm:'get',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns the contained element, if the specified \nindex is `0`.",62)),shared(),actual()];}};defineAttr($$singleton,'clone',function(){
                var $$singleton=this;
                return $$singleton;
            });
            $$singleton.iterator=function iterator(){
                var $$singleton=this;
                function SingletonIterator$398($$singletonIterator$398){
                    $init$SingletonIterator$398();
                    if ($$singletonIterator$398===undefined)$$singletonIterator$398=new SingletonIterator$398.$$;
                    $$singletonIterator$398.$$targs$$={Element:$$singleton.$$targs$$.Element};
                    Iterator($$singletonIterator$398);
                    $$singletonIterator$398.done$399_=/*anotaciones:ceylon.language::Variable,*/false;
                    return $$singletonIterator$398;
                }
                SingletonIterator$398.$$metamodel$$={$nm:'SingletonIterator',$mt:'cls','super':{t:Basic},satisfies:[{t:Iterator,a:{Element:'Element'}}]};
                function $init$SingletonIterator$398(){
                    if (SingletonIterator$398.$$===undefined){
                        initTypeProto(SingletonIterator$398,'ceylon.language::Singleton.iterator.SingletonIterator',Basic,$init$Iterator());
                        (function($$singletonIterator$398){
                            defineAttr($$singletonIterator$398,'done$399',function(){return this.done$399_;},function(done$400){return this.done$399_=done$400;});
                            $$singletonIterator$398.next=function next(){
                                var $$singletonIterator$398=this;
                                if($$singletonIterator$398.done$399){
                                    return getFinished();
                                }else {
                                    $$singletonIterator$398.done$399=true;
                                    return $$singleton.element$396;
                                }
                            };$$singletonIterator$398.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Element',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};defineAttr($$singletonIterator$398,'string',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,*/{
                                var $$singletonIterator$398=this;
                                return String$("SingletonIterator",17);
                            });
                        })(SingletonIterator$398.$$.prototype);
                    }
                    return SingletonIterator$398;
                }
                $init$SingletonIterator$398();
                return SingletonIterator$398();
            };$$singleton.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual(),$default()];}};defineAttr($$singleton,'string',function(){
                var $$singleton=this;
                return StringBuilder().appendAll([String$("[",1),(opt$401=(opt$402=$$singleton.element$396,opt$402!==null?opt$402.string:null),opt$401!==null?opt$401:String$("null",4)).string,String$("]",1)]).string;
            });
            $$singleton.segment=function (from$403,length$404){
                var $$singleton=this;
                return (opt$405=(((from$403.compare((0))!==getLarger())&&from$403.plus(length$404).compare((0)).equals(getLarger()))?$$singleton:null),opt$405!==null?opt$405:getEmpty());
            };
            $$singleton.segment.$$metamodel$$={$nm:'segment',$mt:'mthd',$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns a `Singleton` if the given starting index \nis `0` and the given `length` is greater than `0`.\nOtherwise, returns an instance of `Empty`.",144)),shared(),actual()];}};
            $$singleton.span=function (from$406,to$407){
                var $$singleton=this;
                return (opt$408=((((from$406.compare((0))!==getLarger())&&(to$407.compare((0))!==getSmaller()))||((from$406.compare((0))!==getSmaller())&&(to$407.compare((0))!==getLarger())))?$$singleton:null),opt$408!==null?opt$408:getEmpty());
            };
            $$singleton.span.$$metamodel$$={$nm:'span',$mt:'mthd',$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[doc(String$("Returns a `Singleton` if the given starting index \nis `0`. Otherwise, returns an instance of `Empty`.",101)),shared(),actual()];}};
            $$singleton.spanTo=function (to$409){
                var $$singleton=this;
                return (opt$410=(to$409.compare((0)).equals(getSmaller())?getEmpty():null),opt$410!==null?opt$410:$$singleton);
            };
            $$singleton.spanTo.$$metamodel$$={$nm:'spanTo',$mt:'mthd',$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$singleton.spanFrom=function (from$411){
                var $$singleton=this;
                return (opt$412=(from$411.compare((0)).equals(getLarger())?getEmpty():null),opt$412!==null?opt$412:$$singleton);
            };
            $$singleton.spanFrom.$$metamodel$$={$nm:'spanFrom',$mt:'mthd',$t:{ t:'u', l:[{t:Empty},{t:Singleton,a:{Element:'Element'}}]},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            defineAttr($$singleton,'reversed',function(){
                var $$singleton=this;
                return $$singleton;
            });
            $$singleton.equals=function equals(that$413){
                var $$singleton=this;
                var element$414;
                if((element$414=$$singleton.element$396)!==null){
                    var that$415;
                    if(isOfType((that$415=that$413),{t:List,a:{Element:{t:Anything}}})){
                        if(that$415.size.equals((1))){
                            var elem$416;
                            if((elem$416=that$415.first)!==null){
                                return elem$416.equals(element$414);
                            }
                        }
                    }
                    return false;
                }
                return false;
            };$$singleton.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("A `Singleton` can be equal to another `List` if \nthat `List` has only one element which is equal to \nthis `Singleton`\'s element.",128)),shared(),actual()];}};defineAttr($$singleton,'hash',function(){
                var $$singleton=this;
                return (31).plus((opt$417=(opt$418=$$singleton.element$396,opt$418!==null?opt$418.hash:null),opt$417!==null?opt$417:(0)));
            });
            $$singleton.contains=function contains(element$419){
                var $$singleton=this;
                var e$420;
                if((e$420=$$singleton.element$396)!==null){
                    return e$420.equals(element$419);
                }
                return false;
            };$$singleton.contains.$$metamodel$$={$nm:'contains',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'element',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Returns `true` if the specified element is this \n`Singleton`\'s element.",71)),shared(),actual()];}};$$singleton.count=function (selecting$421){
                var $$singleton=this;
                return (opt$422=(selecting$421($$singleton.element$396)?(1):null),opt$422!==null?opt$422:(0));
            };
            $$singleton.count.$$metamodel$$={$nm:'count',$mt:'mthd',$t:{t:Integer},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[doc(String$("Returns `1` if this `Singleton`\'s element\nsatisfies the predicate, or `0` otherwise.",84)),shared(),actual()];}};
            $$singleton.$map=function (selecting$423,$$$mptypes){
                var $$singleton=this;
                return Tuple(selecting$423($$singleton.element$396),getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Result,Element:$$$mptypes.Result});
            };
            $$singleton.$map.$$metamodel$$={$nm:'map',$mt:'mthd',$t:{t:Sequence,a:{Element:'Result'}},$ps:[{$nm:'selecting',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[shared(),actual()];}};
            $$singleton.$filter=function (selecting$424){
                var $$singleton=this;
                return (opt$425=(selecting$424($$singleton.element$396)?$$singleton:null),opt$425!==null?opt$425:getEmpty());
            };
            $$singleton.$filter.$$metamodel$$={$nm:'filter',$mt:'mthd',$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$singleton.fold=function (initial$426,accumulating$427,$$$mptypes){
                var $$singleton=this;
                return accumulating$427(initial$426,$$singleton.element$396);
            };
            $$singleton.fold.$$metamodel$$={$nm:'fold',$mt:'mthd',$t:'Result',$ps:[{$nm:'initial',$mt:'prm',$t:'Result'},{$nm:'accumulating',$mt:'prm',$t:'Result'}],$tp:{Result:{}},$an:function(){return[shared(),actual()];}};
            $$singleton.find=function find(selecting$428){
                var $$singleton=this;
                if(selecting$428($$singleton.element$396)){
                    return $$singleton.element$396;
                }
                return null;
            };$$singleton.find.$$metamodel$$={$nm:'find',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};$$singleton.findLast=function (selecting$429){
                var $$singleton=this;
                return $$singleton.find($JsCallable(selecting$429,[],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$singleton.$$targs$$.Element,Element:$$singleton.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$singleton.findLast.$$metamodel$$={$nm:'findLast',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual(),$default()];}};
            $$singleton.$sort=function (comparing$430){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.$sort.$$metamodel$$={$nm:'sort',$mt:'mthd',$t:{t:Singleton,a:{Element:'Element'}},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$an:function(){return[shared(),actual()];}};
            $$singleton.any=function (selecting$431){
                var $$singleton=this;
                return selecting$431($$singleton.element$396);
            };
            $$singleton.any.$$metamodel$$={$nm:'any',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$singleton.$every=function (selecting$432){
                var $$singleton=this;
                return selecting$432($$singleton.element$396);
            };
            $$singleton.$every.$$metamodel$$={$nm:'every',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),actual()];}};
            $$singleton.skipping=function (skip$433){
                var $$singleton=this;
                return (opt$434=(skip$433.compare((1)).equals(getSmaller())?$$singleton:null),opt$434!==null?opt$434:getEmpty());
            };
            $$singleton.skipping.$$metamodel$$={$nm:'skipping',$mt:'mthd',$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'skip',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$singleton.taking=function (take$435){
                var $$singleton=this;
                return (opt$436=(take$435.compare((0)).equals(getLarger())?$$singleton:null),opt$436!==null?opt$436:getEmpty());
            };
            $$singleton.taking.$$metamodel$$={$nm:'taking',$mt:'mthd',$t:{ t:'u', l:[{t:Singleton,a:{Element:'Element'}},{t:Empty}]},$ps:[{$nm:'take',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            defineAttr($$singleton,'coalesced',function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,ceylon.language::Actual,*/{
                var $$singleton=this;
                var self$437;
                if(isOfType((self$437=$$singleton),{t:Singleton,a:{Element:{t:Object$}}})){
                    return self$437;
                }
                return getEmpty();
            });
        })(Singleton.$$.prototype);
    }
    return Singleton;
}
exports.$init$Singleton=$init$Singleton;
$init$Singleton();
var opt$401,opt$402,opt$405,opt$408,opt$410,opt$412,opt$417,opt$418,opt$422,opt$425,opt$434,opt$436;
function Map($$map){
    Collection($$map);
    add_type_arg($$map,'Element',{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}});
    Correspondence($$map);
    add_type_arg($$map,'Key',{t:Object$});
    Cloneable($$map);
    add_type_arg($$map,'Clone',{t:Map,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}});
}
Map.$$metamodel$$={$nm:'Map',$mt:'ifc',$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Collection,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},{t:Correspondence,a:{Key:{t:Object$},Item:'Item'}},{t:Cloneable,a:{Clone:{t:Map,a:{Key:'Key',Item:'Item'}}}}],$an:function(){return[doc(String$("Represents a collection which maps _keys_ to _items_,\nwhere a key can map to at most one item. Each such \nmapping may be represented by an `Entry`.\n\nA `Map` is a `Collection` of its `Entry`s, and a \n`Correspondence` from keys to items.\n\nThe presence of an entry in a map may be tested\nusing the `in` operator:\n\n    if (\"lang\"->\"en_AU\" in settings) { ... }\n\nThe entries of the map may be iterated using `for`:\n\n    for (key->item in settings) { ... }\n\nThe item for a key may be obtained using the item\noperator:\n\n    String lang = settings[\"lang\"] else \"en_US\";",560)),see([Entry,forKey,forItem,byItem,byKey].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.Map=Map;
function $init$Map(){
    if (Map.$$===undefined){
        initTypeProto(Map,'ceylon.language::Map',$init$Collection(),$init$Correspondence(),$init$Cloneable());
        (function($$map){
            $$map.equals=function equals(that$438){
                var $$map=this;
                var that$439;
                if(isOfType((that$439=that$438),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})&&that$439.size.equals($$map.size)){
                    var it$440 = $$map.iterator();
                    var entry$441;while ((entry$441=it$440.next())!==getFinished()){
                        var item$442;
                        if((item$442=that$439.get(entry$441.key))!==null&&item$442.equals(entry$441.item)){
                            continue;
                        }else {
                            return false;
                        }
                    }
                    if (getFinished() === entry$441){
                        return true;
                    }
                }else {
                    return false;
                }
            };$$map.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[doc(String$("Two `Map`s are considered equal iff they have the \nsame _entry sets_. The entry set of a `Map` is the\nset of `Entry`s belonging to the map. Therefore, the\nmaps are equal iff they have same set of `keys`, and \nfor every key in the key set, the maps have equal\nitems.",265)),shared(),actual(),$default()];}};defineAttr($$map,'hash',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$map=this;
                var hashCode$443=(1);
                var setHashCode$443=function(hashCode$444){return hashCode$443=hashCode$444;};
                var it$445 = $$map.iterator();
                var elem$446;while ((elem$446=it$445.next())!==getFinished()){
                    (hashCode$443=hashCode$443.times((31)));
                    (hashCode$443=hashCode$443.plus(elem$446.hash));
                }
                return hashCode$443;
            });defineAttr($$map,'keys',function(){
                var $$map=this;
                return LazySet(Comprehension(function(){
                    var it$447=$$map.iterator();
                    var k$448,v$449;
                    var next$v$449=function(){
                        var entry$450;
                        if((entry$450=it$447.next())!==getFinished()){
                            k$448=entry$450.key;
                            v$449=entry$450.item;
                            return entry$450;
                        }
                        v$449=undefined;
                        return getFinished();
                    }
                    next$v$449();
                    return function(){
                        if(v$449!==undefined){
                            var k$448$451=k$448;
                            var v$449$452=v$449;
                            var tmpvar$453=k$448$451;
                            next$v$449();
                            return tmpvar$453;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key});
            });
            defineAttr($$map,'values',function(){
                var $$map=this;
                return LazyList(Comprehension(function(){
                    var it$454=$$map.iterator();
                    var k$455,v$456;
                    var next$v$456=function(){
                        var entry$457;
                        if((entry$457=it$454.next())!==getFinished()){
                            k$455=entry$457.key;
                            v$456=entry$457.item;
                            return entry$457;
                        }
                        v$456=undefined;
                        return getFinished();
                    }
                    next$v$456();
                    return function(){
                        if(v$456!==undefined){
                            var k$455$458=k$455;
                            var v$456$459=v$456;
                            var tmpvar$460=v$456$459;
                            next$v$456();
                            return tmpvar$460;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$map.$$targs$$.Item}),{Element:$$map.$$targs$$.Item});
            });
            defineAttr($$map,'inverse',function(){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$461=$$map.iterator();
                    var key$462,item$463;
                    var next$item$463=function(){
                        var entry$464;
                        if((entry$464=it$461.next())!==getFinished()){
                            key$462=entry$464.key;
                            item$463=entry$464.item;
                            return entry$464;
                        }
                        item$463=undefined;
                        return getFinished();
                    }
                    next$item$463();
                    return function(){
                        if(item$463!==undefined){
                            var key$462$465=key$462;
                            var item$463$466=item$463;
                            var tmpvar$467=Entry(item$463$466,LazySet(Comprehension(function(){
                                var it$468=$$map.iterator();
                                var k$469,i$470;
                                var next$i$470=function(){
                                    var entry$471;
                                    while((entry$471=it$468.next())!==getFinished()){
                                        k$469=entry$471.key;
                                        i$470=entry$471.item;
                                        if(i$470.equals(item$463$466)){
                                            return entry$471;
                                        }
                                    }
                                    i$470=undefined;
                                    return getFinished();
                                }
                                next$i$470();
                                return function(){
                                    if(i$470!==undefined){
                                        var k$469$472=k$469;
                                        var i$470$473=i$470;
                                        var tmpvar$474=k$469$472;
                                        next$i$470();
                                        return tmpvar$474;
                                    }
                                    return getFinished();
                                }
                            },{Absent:{t:Null},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
                            next$item$463();
                            return tmpvar$467;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}}}}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
            });
            $$map.mapItems=function (mapping$475,$$$mptypes){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$476=$$map.iterator();
                    var key$477,item$478;
                    var next$item$478=function(){
                        var entry$479;
                        if((entry$479=it$476.next())!==getFinished()){
                            key$477=entry$479.key;
                            item$478=entry$479.item;
                            return entry$479;
                        }
                        item$478=undefined;
                        return getFinished();
                    }
                    next$item$478();
                    return function(){
                        if(item$478!==undefined){
                            var key$477$480=key$477;
                            var item$478$481=item$478;
                            var tmpvar$482=Entry(key$477$480,mapping$475(key$477$480,item$478$481),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
                            next$item$478();
                            return tmpvar$482;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result}}}),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
            };
            $$map.mapItems.$$metamodel$$={$nm:'mapItems',$mt:'mthd',$t:{t:Map,a:{Key:'Key',Item:'Result'}},$ps:[{$nm:'mapping',$mt:'prm',$t:'Result'}],$tp:{Result:{'satisfies':[{t:Object$}]}},$an:function(){return[doc(String$("Returns a `Map` with the same keys as this map. For\nevery key, the item is the result of applying the\ngiven transformation function.",132)),shared(),$default()];}};
        })(Map.$$.prototype);
    }
    return Map;
}
exports.$init$Map=$init$Map;
$init$Map();
function LazyMap(entries$483, $$targs$$,$$lazyMap){
    $init$LazyMap();
    if ($$lazyMap===undefined)$$lazyMap=new LazyMap.$$;
    set_type_args($$lazyMap,$$targs$$);
    $$lazyMap.entries$483=entries$483;
    Map($$lazyMap);
    return $$lazyMap;
}
LazyMap.$$metamodel$$={$nm:'LazyMap',$mt:'cls','super':{t:Basic},$tp:{Key:{'var':'out','satisfies':[{t:Object$}]},Item:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Map,a:{Key:'Key',Item:'Item'}}],$an:function(){return[doc(String$("A Map implementation that wraps an `Iterable` of \nentries. All operations, such as lookups, size, etc. \nare performed on the `Iterable`.",136)),by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.LazyMap=LazyMap;
function $init$LazyMap(){
    if (LazyMap.$$===undefined){
        initTypeProto(LazyMap,'ceylon.language::LazyMap',Basic,$init$Map());
        (function($$lazyMap){
            defineAttr($$lazyMap,'first',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$483.first;
            });
            defineAttr($$lazyMap,'last',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$483.last;
            });
            defineAttr($$lazyMap,'clone',function(){
                var $$lazyMap=this;
                return $$lazyMap;
            });
            defineAttr($$lazyMap,'size',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$483.size;
            });
            $$lazyMap.get=function (key$484){
                var $$lazyMap=this;
                return (opt$485=$$lazyMap.entries$483.find($JsCallable(function (e$486){
                    var $$lazyMap=this;
                    return e$486.key.equals(key$484);
                },[{$nm:'e',$mt:'prm',$t:{t:Entry,a:{Key:'Key',Item:'Item'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Entry,a:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item}},Element:{t:Entry,a:{Key:$$lazyMap.$$targs$$.Key,Item:$$lazyMap.$$targs$$.Item}}}},Return:{t:Boolean$}})),opt$485!==null?opt$485.item:null);
            };
            $$lazyMap.get.$$metamodel$$={$nm:'get',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Item']},$ps:[{$nm:'key',$mt:'prm',$t:{t:Object$}}],$an:function(){return[shared(),actual()];}};
            $$lazyMap.iterator=function (){
                var $$lazyMap=this;
                return $$lazyMap.entries$483.iterator();
            };
            $$lazyMap.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},$ps:[],$an:function(){return[shared(),actual()];}};
            $$lazyMap.equals=function equals(that$487){
                var $$lazyMap=this;
                var that$488;
                if(isOfType((that$488=that$487),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    if(that$488.size.equals($$lazyMap.size)){
                        var it$489 = $$lazyMap.iterator();
                        var entry$490;while ((entry$490=it$489.next())!==getFinished()){
                            var item$491;
                            if((item$491=that$488.get(entry$490.key))!==null){
                                if(item$491.equals(entry$490.item)){
                                    continue;
                                }
                            }
                            return false;
                        }
                        if (getFinished() === entry$490){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyMap.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[shared(),actual(),$default()];}};defineAttr($$lazyMap,'hash',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$lazyMap=this;
                var hashCode$492=(1);
                var setHashCode$492=function(hashCode$493){return hashCode$492=hashCode$493;};
                var it$494 = $$lazyMap.entries$483.iterator();
                var elem$495;while ((elem$495=it$494.next())!==getFinished()){
                    (hashCode$492=hashCode$492.times((31)));
                    (hashCode$492=hashCode$492.plus(elem$495.hash));
                }
                return hashCode$492;
            });
        })(LazyMap.$$.prototype);
    }
    return LazyMap;
}
exports.$init$LazyMap=$init$LazyMap;
$init$LazyMap();
var opt$485;
function LazySet(elems$496, $$targs$$,$$lazySet){
    $init$LazySet();
    if ($$lazySet===undefined)$$lazySet=new LazySet.$$;
    set_type_args($$lazySet,$$targs$$);
    $$lazySet.elems$496=elems$496;
    Set($$lazySet);
    return $$lazySet;
}
LazySet.$$metamodel$$={$nm:'LazySet',$mt:'cls','super':{t:Basic},$tp:{Element:{'var':'out','satisfies':[{t:Object$}]}},satisfies:[{t:Set,a:{Element:'Element'}}],$an:function(){return[doc(String$("An implementation of Set that wraps an `Iterable` of\nelements. All operations on this Set are performed\non the `Iterable`.",122)),by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.LazySet=LazySet;
function $init$LazySet(){
    if (LazySet.$$===undefined){
        initTypeProto(LazySet,'ceylon.language::LazySet',Basic,$init$Set());
        (function($$lazySet){
            defineAttr($$lazySet,'clone',function(){
                var $$lazySet=this;
                return $$lazySet;
            });
            defineAttr($$lazySet,'size',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,*/{
                var $$lazySet=this;
                var c$497=(0);
                var setC$497=function(c$498){return c$497=c$498;};
                var sorted$499=$$lazySet.elems$496.$sort($JsCallable(byIncreasing($JsCallable(function (e$500){
                    var $$lazySet=this;
                    return e$500.hash;
                },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Integer}}),{Value:{t:Integer},Element:$$lazySet.$$targs$$.Element}),[{$nm:'p1',$mt:'prm',$t:'Element'},{$nm:'p2',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},First:$$lazySet.$$targs$$.Element,Element:$$lazySet.$$targs$$.Element}},Return:{t:Comparison}}));
                var l$501;
                if((l$501=sorted$499.first)!==null){
                    c$497=(1);
                    var last$502=l$501;
                    var setLast$502=function(last$503){return last$502=last$503;};
                    var it$504 = sorted$499.rest.iterator();
                    var e$505;while ((e$505=it$504.next())!==getFinished()){
                        if((!e$505.equals(last$502))){
                            (oldc$506=c$497,c$497=oldc$506.successor,oldc$506);
                            var oldc$506;
                            last$502=e$505;
                        }
                    }
                }
                return c$497;
            });$$lazySet.iterator=function (){
                var $$lazySet=this;
                return $$lazySet.elems$496.iterator();
            };
            $$lazySet.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual()];}};
            $$lazySet.union=function (set$507,$$$mptypes){
                var $$lazySet=this;
                return LazySet($$lazySet.elems$496.chain(set$507,{Other:$$$mptypes.Other,OtherAbsent:{t:Null}}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };
            $$lazySet.union.$$metamodel$$={$nm:'union',$mt:'mthd',$t:{t:Set,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];}};
            $$lazySet.intersection=function (set$508,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var e$511;
                    var it$509=set$508.iterator();
                    var e$510=getFinished();
                    var e$511;
                    var next$e$510=function(){
                        while((e$510=it$509.next())!==getFinished()){
                            if(isOfType((e$511=e$510),$$lazySet.$$targs$$.Element)&&$$lazySet.contains(e$511)){
                                return e$510;
                            }
                        }
                        return getFinished();
                    }
                    next$e$510();
                    return function(){
                        if(e$510!==getFinished()){
                            var e$510$512=e$510;
                            var tmpvar$513=e$511;
                            next$e$510();
                            return tmpvar$513;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}}),{Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}});
            };
            $$lazySet.intersection.$$metamodel$$={$nm:'intersection',$mt:'mthd',$t:{t:Set,a:{Element:{ t:'i', l:['Element','Other']}}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];}};
            $$lazySet.exclusiveUnion=function exclusiveUnion(other$514,$$$mptypes){
                var $$lazySet=this;
                var hereNotThere$515=Comprehension(function(){
                    var it$516=$$lazySet.elems$496.iterator();
                    var e$517=getFinished();
                    var next$e$517=function(){
                        while((e$517=it$516.next())!==getFinished()){
                            if((!other$514.contains(e$517))){
                                return e$517;
                            }
                        }
                        return getFinished();
                    }
                    next$e$517();
                    return function(){
                        if(e$517!==getFinished()){
                            var e$517$518=e$517;
                            var tmpvar$519=e$517$518;
                            next$e$517();
                            return tmpvar$519;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$lazySet.$$targs$$.Element});
                var thereNotHere$520=Comprehension(function(){
                    var it$521=other$514.iterator();
                    var e$522=getFinished();
                    var next$e$522=function(){
                        while((e$522=it$521.next())!==getFinished()){
                            if((!$$lazySet.contains(e$522))){
                                return e$522;
                            }
                        }
                        return getFinished();
                    }
                    next$e$522();
                    return function(){
                        if(e$522!==getFinished()){
                            var e$522$523=e$522;
                            var tmpvar$524=e$522$523;
                            next$e$522();
                            return tmpvar$524;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$$mptypes.Other});
                return LazySet(hereNotThere$515.chain(thereNotHere$520,{Other:$$$mptypes.Other,OtherAbsent:{t:Null}}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };$$lazySet.exclusiveUnion.$$metamodel$$={$nm:'exclusiveUnion',$mt:'mthd',$t:{t:Set,a:{Element:{ t:'u', l:['Element','Other']}}},$ps:[{$nm:'other',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];}};$$lazySet.complement=function (set$525,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var it$526=$$lazySet.iterator();
                    var e$527=getFinished();
                    var next$e$527=function(){
                        while((e$527=it$526.next())!==getFinished()){
                            if((!set$525.contains(e$527))){
                                return e$527;
                            }
                        }
                        return getFinished();
                    }
                    next$e$527();
                    return function(){
                        if(e$527!==getFinished()){
                            var e$527$528=e$527;
                            var tmpvar$529=e$527$528;
                            next$e$527();
                            return tmpvar$529;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Null},Element:$$lazySet.$$targs$$.Element}),{Element:$$lazySet.$$targs$$.Element});
            };
            $$lazySet.complement.$$metamodel$$={$nm:'complement',$mt:'mthd',$t:{t:Set,a:{Element:'Element'}},$ps:[{$nm:'set',$mt:'prm',$t:{t:Set,a:{Element:'Other'}}}],$tp:{Other:{'satisfies':[{t:Object$}]}},$an:function(){return[shared(),actual()];}};
            $$lazySet.equals=function equals(that$530){
                var $$lazySet=this;
                var that$531;
                if(isOfType((that$531=that$530),{t:Set,a:{Element:{t:Object$}}})){
                    if(that$531.size.equals($$lazySet.size)){
                        var it$532 = $$lazySet.elems$496.iterator();
                        var element$533;while ((element$533=it$532.next())!==getFinished()){
                            if((!that$531.contains(element$533))){
                                return false;
                            }
                        }
                        if (getFinished() === element$533){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazySet.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[shared(),actual(),$default()];}};defineAttr($$lazySet,'hash',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$lazySet=this;
                var hashCode$534=(1);
                var setHashCode$534=function(hashCode$535){return hashCode$534=hashCode$535;};
                var it$536 = $$lazySet.elems$496.iterator();
                var elem$537;while ((elem$537=it$536.next())!==getFinished()){
                    (hashCode$534=hashCode$534.times((31)));
                    (hashCode$534=hashCode$534.plus(elem$537.hash));
                }
                return hashCode$534;
            });
        })(LazySet.$$.prototype);
    }
    return LazySet;
}
exports.$init$LazySet=$init$LazySet;
$init$LazySet();
function LazyList(elems$538, $$targs$$,$$lazyList){
    $init$LazyList();
    if ($$lazyList===undefined)$$lazyList=new LazyList.$$;
    set_type_args($$lazyList,$$targs$$);
    $$lazyList.elems$538=elems$538;
    List($$lazyList);
    return $$lazyList;
}
LazyList.$$metamodel$$={$nm:'LazyList',$mt:'cls','super':{t:Basic},$tp:{Element:{'var':'out',}},satisfies:[{t:List,a:{Element:'Element'}}],$an:function(){return[doc(String$("An implementation of List that wraps an `Iterable` of\nelements. All operations on this List are performed on \nthe Iterable.",123)),by([String$("Enrique Zamudio",15)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};
exports.LazyList=LazyList;
function $init$LazyList(){
    if (LazyList.$$===undefined){
        initTypeProto(LazyList,'ceylon.language::LazyList',Basic,$init$List());
        (function($$lazyList){
            defineAttr($$lazyList,'lastIndex',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,*/{
                var $$lazyList=this;
                var size$539=$$lazyList.elems$538.size;
                return (opt$540=(size$539.compare((0)).equals(getLarger())?size$539.minus((1)):null),opt$540!==null?opt$540:null);
                var opt$540;
            });$$lazyList.get=function get(index$541){
                var $$lazyList=this;
                if(index$541.equals((0))){
                    return $$lazyList.elems$538.first;
                }else {
                    return $$lazyList.elems$538.skipping(index$541).first;
                }
            };$$lazyList.get.$$metamodel$$={$nm:'get',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'index',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};defineAttr($$lazyList,'rest',function(){
                var $$lazyList=this;
                return LazyList($$lazyList.elems$538.rest,{Element:$$lazyList.$$targs$$.Element});
            });
            $$lazyList.iterator=function (){
                var $$lazyList=this;
                return $$lazyList.elems$538.iterator();
            };
            $$lazyList.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Element'}},$ps:[],$an:function(){return[shared(),actual()];}};
            defineAttr($$lazyList,'reversed',function(){
                var $$lazyList=this;
                return $$lazyList.elems$538.sequence.reversed;
            });
            defineAttr($$lazyList,'clone',function(){
                var $$lazyList=this;
                return $$lazyList;
            });
            $$lazyList.span=function span(from$542,to$543){
                var $$lazyList=this;
                if((to$543.compare((0)).equals(getSmaller())&&from$542.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var toIndex$544=largest(to$543,(0),{Element:{t:Integer}});
                var fromIndex$545=largest(from$542,(0),{Element:{t:Integer}});
                if((toIndex$544.compare(fromIndex$545)!==getSmaller())){
                    var els$546=(opt$547=(fromIndex$545.compare((0)).equals(getLarger())?$$lazyList.elems$538.skipping(fromIndex$545):null),opt$547!==null?opt$547:$$lazyList.elems$538);
                    var opt$547;
                    return LazyList(els$546.taking(toIndex$544.minus(fromIndex$545).plus((1))),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    var seq$548=(opt$549=(toIndex$544.compare((0)).equals(getLarger())?$$lazyList.elems$538.skipping(toIndex$544):null),opt$549!==null?opt$549:$$lazyList.elems$538);
                    var opt$549;
                    return seq$548.taking(fromIndex$545.minus(toIndex$544).plus((1))).sequence.reversed;
                }
            };$$lazyList.span.$$metamodel$$={$nm:'span',$mt:'mthd',$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$lazyList.spanTo=function (to$550){
                var $$lazyList=this;
                return (opt$551=(to$550.compare((0)).equals(getSmaller())?getEmpty():null),opt$551!==null?opt$551:LazyList($$lazyList.elems$538.taking(to$550.plus((1))),{Element:$$lazyList.$$targs$$.Element}));
            };
            $$lazyList.spanTo.$$metamodel$$={$nm:'spanTo',$mt:'mthd',$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'to',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$lazyList.spanFrom=function (from$552){
                var $$lazyList=this;
                return (opt$553=(from$552.compare((0)).equals(getLarger())?LazyList($$lazyList.elems$538.skipping(from$552),{Element:$$lazyList.$$targs$$.Element}):null),opt$553!==null?opt$553:$$lazyList);
            };
            $$lazyList.spanFrom.$$metamodel$$={$nm:'spanFrom',$mt:'mthd',$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};
            $$lazyList.segment=function segment(from$554,length$555){
                var $$lazyList=this;
                if(length$555.compare((0)).equals(getLarger())){
                    var els$556=(opt$557=(from$554.compare((0)).equals(getLarger())?$$lazyList.elems$538.skipping(from$554):null),opt$557!==null?opt$557:$$lazyList.elems$538);
                    var opt$557;
                    return LazyList(els$556.taking(length$555),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    return getEmpty();
                }
            };$$lazyList.segment.$$metamodel$$={$nm:'segment',$mt:'mthd',$t:{t:List,a:{Element:'Element'}},$ps:[{$nm:'from',$mt:'prm',$t:{t:Integer}},{$nm:'length',$mt:'prm',$t:{t:Integer}}],$an:function(){return[shared(),actual()];}};$$lazyList.equals=function equals(that$558){
                var $$lazyList=this;
                var that$559;
                if(isOfType((that$559=that$558),{t:List,a:{Element:{t:Anything}}})){
                    var size$560=$$lazyList.elems$538.size;
                    if(that$559.size.equals(size$560)){
                        var it$561 = Range((0),size$560.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$562;while ((i$562=it$561.next())!==getFinished()){
                            var x$563=$$lazyList.get(i$562);
                            var y$564=that$559.get(i$562);
                            var x$565;
                            if((x$565=x$563)!==null){
                                var y$566;
                                if((y$566=y$564)!==null){
                                    if((!x$565.equals(y$566))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$567;
                                if((y$567=y$564)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$562){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyList.equals.$$metamodel$$={$nm:'equals',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'that',$mt:'prm',$t:{t:Object$}}],$an:function(){return[shared(),actual(),$default()];}};defineAttr($$lazyList,'hash',function()/*anotaciones:ceylon.language::Shared,ceylon.language::Actual,ceylon.language::Default,*/{
                var $$lazyList=this;
                var hash$568=(1);
                var setHash$568=function(hash$569){return hash$568=hash$569;};
                var it$570 = $$lazyList.elems$538.iterator();
                var elem$571;while ((elem$571=it$570.next())!==getFinished()){
                    (hash$568=hash$568.times((31)));
                    var elem$572;
                    if((elem$572=elem$571)!==null){
                        (hash$568=hash$568.plus(elem$572.hash));
                    }
                }
                return hash$568;
            });$$lazyList.findLast=function (selecting$573){
                var $$lazyList=this;
                return $$lazyList.elems$538.findLast($JsCallable(selecting$573,[],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$lazyList.$$targs$$.Element,Element:$$lazyList.$$targs$$.Element}},Return:{t:Boolean$}}));
            };
            $$lazyList.findLast.$$metamodel$$={$nm:'findLast',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Element']},$ps:[{$nm:'selecting',$mt:'prm',$t:{t:Boolean$}}],$an:function(){return[shared(),$default(),actual()];}};
            defineAttr($$lazyList,'first',function(){
                var $$lazyList=this;
                return $$lazyList.elems$538.first;
            });
            defineAttr($$lazyList,'last',function(){
                var $$lazyList=this;
                return $$lazyList.elems$538.last;
            });
        })(LazyList.$$.prototype);
    }
    return LazyList;
}
exports.$init$LazyList=$init$LazyList;
$init$LazyList();
var opt$551,opt$553;
function count(values$574){
    var count$575=(0);
    var setCount$575=function(count$576){return count$575=count$576;};
    var it$577 = values$574.iterator();
    var val$578;while ((val$578=it$577.next())!==getFinished()){
        if(val$578){
            (oldcount$579=count$575,count$575=oldcount$579.successor,oldcount$579);
            var oldcount$579;
        }
    }
    return count$575;
}
exports.count=count;
count.$$metamodel$$={$nm:'count',$mt:'mthd',$t:{t:Integer},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[doc(String$("A count of the number of `true` items in the given values.",58)),shared()];}};//count.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Return:{t:Integer}};
function any(values$580){
    var it$581 = values$580.iterator();
    var val$582;while ((val$582=it$581.next())!==getFinished()){
        if(val$582){
            return true;
        }
    }
    return false;
}
exports.any=any;
any.$$metamodel$$={$nm:'any',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[doc(String$("Determines if any one of the given boolean values \n(usually a comprehension) is `true`.",87)),see([$JsCallable(every,[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Return:{t:Boolean$}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Return:{t:Boolean$}}}})),shared()];}};//any.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Return:{t:Boolean$}};
var byIncreasing=function (comparable$583,$$$mptypes){
    return function(x$584,y$585){{
        return comparable$583(x$584).compare(comparable$583(y$585));
    }
}
}
;
byIncreasing.$$metamodel$$={$nm:'byIncreasing',$mt:'mthd',$t:{t:Comparison},$ps:[{$nm:'comparable',$mt:'prm',$t:'Value'}],$tp:{Element:{},Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]}},$an:function(){return[doc(String$("A comparator which orders elements in increasing order \naccording to the `Comparable` returned by the given \n`comparable()` function.",133)),see([byDecreasing].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.byIncreasing=byIncreasing;
var entries=function (elements$586,$$$mptypes){
    return elements$586.indexed;
};
entries.$$metamodel$$={$nm:'entries',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:['Element',{t:Object$}]}}}}},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{}},$an:function(){return[doc(String$("Produces a sequence of each index to element `Entry` \nfor the given sequence of values.",87)),shared()];}};
exports.entries=entries;
var byItem=function (comparing$587,$$$mptypes){
    return function(x$588,y$589){{
        return comparing$587(x$588.item,y$589.item);
    }
}
}
;
byItem.$$metamodel$$={$nm:'byItem',$mt:'mthd',$t:{t:Comparison},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$tp:{Item:{'satisfies':[{t:Object$}]}},$an:function(){return[doc(String$("A comparator for `Entry`s which compares their items \naccording to the given `comparing()` function.",100)),see([byKey].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.byItem=byItem;
var equalTo=function (val$590,$$$mptypes){
    return function(element$591){{
        return element$591.equals(val$590);
    }
}
}
;
equalTo.$$metamodel$$={$nm:'equalTo',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Object$}]}},$an:function(){return[doc(String$("Returns a partial function that will compare an element\nto any other element and returns true if they\'re equal.\nThis is useful in conjunction with methods that receive\na predicate function.",189)),shared()];}};
exports.equalTo=equalTo;
var byKey=function (comparing$592,$$$mptypes){
    return function(x$593,y$594){{
        return comparing$592(x$593.key,y$594.key);
    }
}
}
;
byKey.$$metamodel$$={$nm:'byKey',$mt:'mthd',$t:{t:Comparison},$ps:[{$nm:'comparing',$mt:'prm',$t:{t:Comparison}}],$tp:{Key:{'satisfies':[{t:Object$}]}},$an:function(){return[doc(String$("A comparator for `Entry`s which compares their keys \naccording to the given `comparing()` function.",99)),see([byItem].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.byKey=byKey;
var byDecreasing=function (comparable$595,$$$mptypes){
    return function(x$596,y$597){{
        return comparable$595(y$597).compare(comparable$595(x$596));
    }
}
}
;
byDecreasing.$$metamodel$$={$nm:'byDecreasing',$mt:'mthd',$t:{t:Comparison},$ps:[{$nm:'comparable',$mt:'prm',$t:'Value'}],$tp:{Element:{},Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]}},$an:function(){return[doc(String$("A comparator which orders elements in decreasing order \naccording to the `Comparable` returned by the given \n`comparable()` function.",133)),see([byIncreasing].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.byDecreasing=byDecreasing;
var curry=function (f$598,$$$mptypes){
    return function(first$599){{
        return flatten($JsCallable(function (args$600){
            return unflatten($JsCallable(f$598,[],{Arguments:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return})(Tuple(first$599,args$600,{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}));
        },[{$nm:'args',$mt:'prm',$t:'Rest'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Rest,Element:$$$mptypes.Rest}},Return:$$$mptypes.Return}),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return});
    }
}
}
;
curry.$$metamodel$$={$nm:'curry',$mt:'mthd',$t:{t:Callable,a:{Arguments:'Rest',Return:'Return'}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}},Return:'Return'}}}],$tp:{Return:{},Argument:{},First:{'satisfies':['Argument']},Rest:{'satisfies':[{t:Sequential,a:{Element:'Argument'}}]}},$an:function(){return[shared()];}};
exports.curry=curry;
var uncurry=function (f$601,$$$mptypes){
return flatten($JsCallable(function (args$602){
    return unflatten($JsCallable(f$601(args$602.first),[],{Arguments:$$$mptypes.Rest,Return:$$$mptypes.Return}),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return})(args$602.rest);
},[{$nm:'args',$mt:'prm',$t:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Element:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}}}},Return:$$$mptypes.Return}),{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return});
};
uncurry.$$metamodel$$={$nm:'uncurry',$mt:'mthd',$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:'Rest',First:'First',Element:'Argument'}},Return:'Return'}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:'Rest',Return:'Return'}}}],$tp:{Return:{},Argument:{},First:{'satisfies':['Argument']},Rest:{'satisfies':[{t:Sequential,a:{Element:'Argument'}}]}},$an:function(){return[shared()];}};
exports.uncurry=uncurry;
var coalesce=function (values$603,$$$mptypes){
    return values$603.coalesced;
};
coalesce.$$metamodel$$={$nm:'coalesce',$mt:'mthd',$t:{t:Iterable,a:{Absent:{t:Null},Element:{ t:'i', l:['Element',{t:Object$}]}}},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{}},$an:function(){return[doc(String$("Return a sequence containing the given values which are\nnot null. If there are no values which are not null,\nreturn an empty sequence.",134)),shared()];}};
exports.coalesce=coalesce;
function emptyOrSingleton(element$604,$$$mptypes){
    var element$605;
    if((element$605=element$604)!==null){
        return Singleton(element$605,{Element:$$$mptypes.Element});
    }else {
        return getEmpty();
    }
}
exports.emptyOrSingleton=emptyOrSingleton;
emptyOrSingleton.$$metamodel$$={$nm:'emptyOrSingleton',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'element',$mt:'prm',$t:{ t:'u', l:[{t:Null},'Element']}}],$tp:{Element:{'satisfies':[{t:Object$}]}},$an:function(){return[doc(String$("A `Singleton` if the given element is non-null, otherwise `Empty`.",66)),see([Singleton,Empty].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};//emptyOrSingleton.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{ t:'u', l:[{t:Null},$$$mptypes.Element]},Element:{ t:'u', l:[{t:Null},$$$mptypes.Element]}}},Return:{t:Sequential,a:{Element:$$$mptypes.Element}}};
function product(values$606,$$$mptypes){
    var product$607=values$606.first;
    var setProduct$607=function(product$608){return product$607=product$608;};
    var it$609 = values$606.rest.iterator();
    var val$610;while ((val$610=it$609.next())!==getFinished()){
        (product$607=product$607.times(val$610));
    }
    return product$607;
}
exports.product=product;
product.$$metamodel$$={$nm:'product',$mt:'mthd',$t:'Value',$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Numeric,a:{Other:'Value'}}]}},$an:function(){return[doc(String$("Given a nonempty sequence of `Numeric` values, return \nthe product of the values.",81)),see([sum].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};//product.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Nothing},Element:$$$mptypes.Value}},Element:{t:Iterable,a:{Absent:{t:Nothing},Element:$$$mptypes.Value}}}},Return:$$$mptypes.Value};
function sum(values$611,$$$mptypes){
    var sum$612=values$611.first;
    var setSum$612=function(sum$613){return sum$612=sum$613;};
    var it$614 = values$611.rest.iterator();
    var val$615;while ((val$615=it$614.next())!==getFinished()){
        (sum$612=sum$612.plus(val$615));
    }
    return sum$612;
}
exports.sum=sum;
sum.$$metamodel$$={$nm:'sum',$mt:'mthd',$t:'Value',$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Nothing},Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Summable,a:{Other:'Value'}}]}},$an:function(){return[doc(String$("Given a nonempty sequence of `Summable` values, return \nthe sum of the values.",78)),see([product].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};//sum.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Nothing},Element:$$$mptypes.Value}},Element:{t:Iterable,a:{Absent:{t:Nothing},Element:$$$mptypes.Value}}}},Return:$$$mptypes.Value};
var largest=function (x$616,y$617,$$$mptypes){
    return (opt$618=(x$616.compare(y$617).equals(getLarger())?x$616:null),opt$618!==null?opt$618:y$617);
};
largest.$$metamodel$$={$nm:'largest',$mt:'mthd',$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[doc(String$("Given two `Comparable` values, return largest of the\ntwo.",57)),see([Comparable,smallest,max].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.largest=largest;
var opt$618;
function print(line$619){
    getProcess().writeLine((opt$620=(opt$621=line$619,opt$621!==null?opt$621.string:null),opt$620!==null?opt$620:String$("«null»",6)));
    var opt$620,opt$621;
}
exports.print=print;
print.$$metamodel$$={$nm:'print',$mt:'mthd',$t:{t:Anything},$ps:[{$nm:'line',$mt:'prm',$t:{t:Anything}}],$an:function(){return[doc(String$("Print a line to the standard output of the virtual \nmachine process, printing the given value\'s `string`, \nor `«null»` if the value is `null`.\n\nThis method is a shortcut for:\n\n    process.writeLine(line?.string else \"«null»\")\n\nand is intended mainly for debugging purposes.",273)),see([$JsCallable((opt$622=getProcess(),JsCallable(opt$622,opt$622!==null?opt$622.writeLine:null)),[{$nm:'line',$mt:'prm',$def:1,$t:{t:String$}}],{Arguments:{ t:'u', l:[{t:Empty},{t:Tuple,a:{Rest:{t:Empty},First:{t:String$},Element:{t:String$}}}]},Return:{t:Anything}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{ t:'u', l:[{t:Empty},{t:Tuple,a:{Rest:{t:Empty},First:{t:String$},Element:{t:String$}}}]},Return:{t:Anything}}}})),by([String$("Gavin",5)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};//print.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Anything},Element:{t:Anything}}},Return:{t:Anything}};
var opt$622;
var lessThan=function (val$623,$$$mptypes){
    return function(element$624){{
        return element$624.compare(val$623).equals(getSmaller());
    }
}
}
;
lessThan.$$metamodel$$={$nm:'lessThan',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[doc(String$("Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is less than its element.\nThis is useful in conjunction with methods that receive\na predicate function.",221)),shared()];}};
exports.lessThan=lessThan;
var smallest=function (x$625,y$626,$$$mptypes){
    return (opt$627=(x$625.compare(y$626).equals(getSmaller())?x$625:null),opt$627!==null?opt$627:y$626);
};
smallest.$$metamodel$$={$nm:'smallest',$mt:'mthd',$t:'Element',$ps:[{$nm:'x',$mt:'prm',$t:'Element'},{$nm:'y',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[doc(String$("Given two `Comparable` values, return smallest of the\ntwo.",58)),see([Comparable,largest,min].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.smallest=smallest;
var opt$627;
function first(values$628,$$$mptypes){
    var first$629;
    var next$630;
    if(!isOfType((next$630=values$628.iterator().next()),{t:Finished})){
        first$629=next$630;
    }else {
        first$629=null;
    }
    //assert at first.ceylon (12:4-12:34)
    var first$631;
    if (!(isOfType((first$631=first$629),{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}))) { throw AssertionException('Assertion failed: \'is Absent|Value first\' at first.ceylon (12:11-12:33)'); }
    return first$631;
}
exports.first=first;
first.$$metamodel$$={$nm:'first',$mt:'mthd',$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[doc(String$("The first of the given elements (usually a comprehension),\nif any.",66)),shared()];}};//first.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.Value}},Element:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.Value}}}},Return:{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}};
var join=function (iterables$632,$$$mptypes){
    if(iterables$632===undefined){iterables$632=getEmpty();}
    return Comprehension(function(){
        var it$633=iterables$632.iterator();
        var it$634=getFinished();
        var next$it$634=function(){
            if((it$634=it$633.next())!==getFinished()){
                it$635=it$634.iterator();
                next$val$636();
                return it$634;
            }
            return getFinished();
        }
        var it$635;
        var val$636=getFinished();
        var next$val$636=function(){return val$636=it$635.next();}
        next$it$634();
        return function(){
            do{
                if(val$636!==getFinished()){
                    var val$636$637=val$636;
                    var tmpvar$638=val$636$637;
                    next$val$636();
                    return tmpvar$638;
                }
            }while(next$it$634()!==getFinished());
            return getFinished();
        }
    },{Absent:{t:Null},Element:$$$mptypes.Element}).sequence;
};
join.$$metamodel$$={$nm:'join',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'iterables',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}}}],$tp:{Element:{}},$an:function(){return[doc(String$("Given a list of iterable objects, return a new sequence \nof all elements of the all given objects. If there are\nno arguments, or if none of the arguments contains any\nelements, return the empty sequence.",203)),see([SequenceBuilder].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.join=join;
function every(values$639){
    var it$640 = values$639.iterator();
    var val$641;while ((val$641=it$640.next())!==getFinished()){
        if((!val$641)){
            return false;
        }
    }
    return true;
}
exports.every=every;
every.$$metamodel$$={$nm:'every',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],$an:function(){return[doc(String$("Determines if every one of the given boolean values \n(usually a comprehension) is `true`.",89)),see([$JsCallable(any,[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Return:{t:Boolean$}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Return:{t:Boolean$}}}})),shared()];}};//every.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}},Element:{t:Iterable,a:{Absent:{t:Null},Element:{t:Boolean$}}}}},Return:{t:Boolean$}};
var getNothing=function()/*anotaciones:ceylon.language::Doc,ceylon.language::Shared,*/{
    throw Exception();
}
exports.getNothing=getNothing;
function min(values$642,$$$mptypes){
    var first$643=values$642.first;
    var first$644;
    if((first$644=first$643)!==null){
        var min$645=first$644;
        var setMin$645=function(min$646){return min$645=min$646;};
        var it$647 = values$642.rest.iterator();
        var val$648;while ((val$648=it$647.next())!==getFinished()){
            if(val$648.compare(min$645).equals(getSmaller())){
                min$645=val$648;
            }
        }
        return min$645;
    }else {
        return first$643;
    }
}
exports.min=min;
min.$$metamodel$$={$nm:'min',$mt:'mthd',$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[doc(String$("Given a nonempty sequence of `Comparable` values, \nreturn the smallest value in the sequence.",93)),see([Comparable,max,smallest].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};//min.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.Value}},Element:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.Value}}}},Return:{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}};
var forKey=function (resulting$649,$$$mptypes){
    return function(entry$650){{
        return resulting$649(entry$650.key);
    }
}
}
;
forKey.$$metamodel$$={$nm:'forKey',$mt:'mthd',$t:'Result',$ps:[{$nm:'resulting',$mt:'prm',$t:'Result'}],$tp:{Key:{'satisfies':[{t:Object$}]},Result:{}},$an:function(){return[doc(String$("A function that returns the result of the given `resulting()` function \non the key of a given `Entry`.",102)),see([forItem].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.forKey=forKey;
var forItem=function (resulting$651,$$$mptypes){
    return function(entry$652){{
        return resulting$651(entry$652.item);
    }
}
}
;
forItem.$$metamodel$$={$nm:'forItem',$mt:'mthd',$t:'Result',$ps:[{$nm:'resulting',$mt:'prm',$t:'Result'}],$tp:{Item:{'satisfies':[{t:Object$}]},Result:{}},$an:function(){return[doc(String$("A function that returns the result of the given `resulting()` function \non the item of a given `Entry`.",103)),see([forKey].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.forItem=forItem;
function max(values$653,$$$mptypes){
    var first$654=values$653.first;
    var first$655;
    if((first$655=first$654)!==null){
        var max$656=first$655;
        var setMax$656=function(max$657){return max$656=max$657;};
        var it$658 = values$653.rest.iterator();
        var val$659;while ((val$659=it$658.next())!==getFinished()){
            if(val$659.compare(max$656).equals(getLarger())){
                max$656=val$659;
            }
        }
        return max$656;
    }else {
        return first$654;
    }
}
exports.max=max;
max.$$metamodel$$={$nm:'max',$mt:'mthd',$t:{ t:'u', l:['Absent','Value']},$ps:[{$nm:'values',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Value'}}}],$tp:{Value:{'satisfies':[{t:Comparable,a:{Other:'Value'}}]},Absent:{'satisfies':[{t:Null}]}},$an:function(){return[doc(String$("Given a nonempty sequence of `Comparable` values, \nreturn the largest value in the sequence.",92)),see([Comparable,min,largest].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};//max.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.Value}},Element:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.Value}}}},Return:{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}};
function zip(keys$660,items$661,$$$mptypes){
    var iter$662=items$661.iterator();
    return Comprehension(function(){
        var item$665;
        var it$663=keys$660.iterator();
        var key$664=getFinished();
        var item$665;
        var next$key$664=function(){
            while((key$664=it$663.next())!==getFinished()){
                if(!isOfType((item$665=iter$662.next()),{t:Finished})){
                    return key$664;
                }
            }
            return getFinished();
        }
        next$key$664();
        return function(){
            if(key$664!==getFinished()){
                var key$664$666=key$664;
                var tmpvar$667=Entry(key$664$666,item$665,{Key:$$$mptypes.Key,Item:$$$mptypes.Item});
                next$key$664();
                return tmpvar$667;
            }
            return getFinished();
        }
    },{Absent:{t:Null},Element:{t:Entry,a:{Key:$$$mptypes.Key,Item:$$$mptypes.Item}}}).sequence;
}
exports.zip=zip;
zip.$$metamodel$$={$nm:'zip',$mt:'mthd',$t:{t:Sequential,a:{Element:{t:Entry,a:{Key:'Key',Item:'Item'}}}},$ps:[{$nm:'keys',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Key'}}},{$nm:'items',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Item'}}}],$tp:{Key:{'satisfies':[{t:Object$}]},Item:{'satisfies':[{t:Object$}]}},$an:function(){return[doc(String$("Given two sequences, form a new sequence consisting of\nall entries where, for any given index in the resulting\nsequence, the key of the entry is the element occurring \nat the same index in the first sequence, and the item \nis the element occurring at the same index in the second \nsequence. The length of the resulting sequence is the \nlength of the shorter of the two given sequences. \n\nThus:\n\n    zip(xs,ys)[i]==xs[i]->ys[i]\n\nfor every `0<=i<min({xs.size,ys.size})`.",468)),shared()];}};//zip.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:{t:Null},Element:$$$mptypes.Item}},Element:{t:Iterable,a:{Absent:{t:Null},Element:$$$mptypes.Item}}}},Return:{t:Sequential,a:{Element:{t:Entry,a:{Key:$$$mptypes.Key,Item:$$$mptypes.Item}}}}};
var greaterThan=function (val$668,$$$mptypes){
    return function(element$669){{
        return element$669.compare(val$668).equals(getLarger());
    }
}
}
;
greaterThan.$$metamodel$$={$nm:'greaterThan',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'val',$mt:'prm',$t:'Element'}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[doc(String$("Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is greater than its element.\nThis is useful in conjunction with methods that receive\na predicate function.",224)),shared()];}};
exports.greaterThan=greaterThan;
var compose=function (x$670,y$671,$$$mptypes){
    return flatten($JsCallable(function (args$672){
        return x$670(unflatten($JsCallable(y$671,[],{Arguments:$$$mptypes.Args,Return:$$$mptypes.Y}),{Args:$$$mptypes.Args,Return:$$$mptypes.Y})(args$672));
    },[{$nm:'args',$mt:'prm',$t:'Args'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Args,Element:$$$mptypes.Args}},Return:$$$mptypes.X}),{Args:$$$mptypes.Args,Return:$$$mptypes.X});
};
compose.$$metamodel$$={$nm:'compose',$mt:'mthd',$t:{t:Callable,a:{Arguments:'Args',Return:'X'}},$ps:[{$nm:'x',$mt:'prm',$t:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:'Y',Element:'Y'}},Return:'X'}}},{$nm:'y',$mt:'prm',$t:{t:Callable,a:{Arguments:'Args',Return:'Y'}}}],$tp:{X:{},Y:{},Args:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},$an:function(){return[shared()];}};
exports.compose=compose;
function combine(combination$673,elements$674,otherElements$675,$$$mptypes){
    function iterable$676($$targs$$){
        var $$iterable$676=new iterable$676.$$;
        $$iterable$676.$$targs$$=$$targs$$;
        Iterable($$iterable$676);
        return $$iterable$676;
    }
    function $init$iterable$676(){
        if (iterable$676.$$===undefined){
            initTypeProto(iterable$676,'ceylon.language::combine.iterable',Basic,$init$Iterable());
        }
        return iterable$676;
    }
    $init$iterable$676();
    (function($$iterable$676){
        $$iterable$676.iterator=function iterator(){
            var $$iterable$676=this;
            function iterator$677($$targs$$){
                var $$iterator$677=new iterator$677.$$;
                $$iterator$677.$$targs$$=$$targs$$;
                Iterator($$iterator$677);
                $$iterator$677.iter$678_=/*anotaciones:*/elements$674.iterator();
                $$iterator$677.otherIter$679_=/*anotaciones:*/otherElements$675.iterator();
                return $$iterator$677;
            }
            function $init$iterator$677(){
                if (iterator$677.$$===undefined){
                    initTypeProto(iterator$677,'ceylon.language::combine.iterable.iterator.iterator',Basic,$init$Iterator());
                }
                return iterator$677;
            }
            $init$iterator$677();
            (function($$iterator$677){
                defineAttr($$iterator$677,'iter$678',function(){return this.iter$678_;});
                defineAttr($$iterator$677,'otherIter$679',function(){return this.otherIter$679_;});
                $$iterator$677.next=function next(){
                    var $$iterator$677=this;
                    var elem$680=$$iterator$677.iter$678.next();
                    var otherElem$681=$$iterator$677.otherIter$679.next();
                    var elem$682;
                    var otherElem$683;
                    if(!isOfType((elem$682=elem$680),{t:Finished})&&!isOfType((otherElem$683=otherElem$681),{t:Finished})){
                        return combination$673(elem$682,otherElem$683);
                    }else {
                        return getFinished();
                    }
                };$$iterator$677.next.$$metamodel$$={$nm:'next',$mt:'mthd',$t:{ t:'u', l:['Result',{t:Finished}]},$ps:[],$an:function(){return[shared(),actual()];}};
            })(iterator$677.$$.prototype);
            var iterator$684=iterator$677({Element:$$$mptypes.Result});
            var getIterator$684=function(){
                return iterator$684;
            }
            return getIterator$684();
        };$$iterable$676.iterator.$$metamodel$$={$nm:'iterator',$mt:'mthd',$t:{t:Iterator,a:{Element:'Result'}},$ps:[],$an:function(){return[shared(),actual()];}};
    })(iterable$676.$$.prototype);
    var iterable$685=iterable$676({Absent:$$$mptypes.Absent,Element:$$$mptypes.Result});
    var getIterable$685=function(){
        return iterable$685;
    }
    return getIterable$685();
}
exports.combine=combine;
combine.$$metamodel$$={$nm:'combine',$mt:'mthd',$t:{t:Iterable,a:{Absent:'Absent',Element:'Result'}},$ps:[{$nm:'combination',$mt:'prm',$t:'Result'},{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'Element'}}},{$nm:'otherElements',$mt:'prm',$t:{t:Iterable,a:{Absent:'Absent',Element:'OtherElement'}}}],$tp:{Result:{},Absent:{'satisfies':[{t:Null}]},Element:{},OtherElement:{}},$an:function(){return[doc(String$("Applies a function to each element of two `Iterable`s\nand returns an `Iterable` with the results.",97)),by([String$("Gavin",5),String$("Enrique Zamudio",15),String$("Tako",4)].reifyCeylonType({Absent:{t:Null},Element:{t:String$}})),shared()];}};//combine.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.OtherElement}},Element:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.OtherElement}}}},Return:{t:Iterable,a:{Absent:$$$mptypes.Absent,Element:$$$mptypes.Result}}};
var plus=function (x$686,y$687,$$$mptypes){
    return x$686.plus(y$687);
};
plus.$$metamodel$$={$nm:'plus',$mt:'mthd',$t:'Value',$ps:[{$nm:'x',$mt:'prm',$t:'Value'},{$nm:'y',$mt:'prm',$t:'Value'}],$tp:{Value:{'satisfies':[{t:Summable,a:{Other:'Value'}}]}},$an:function(){return[doc(String$("Add the given `Summable` values.",32)),see([times,sum].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.plus=plus;
var sort=function (elements$688,$$$mptypes){
    return internalSort($JsCallable(byIncreasing($JsCallable(function (e$689){
        return e$689;
    },[{$nm:'e',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element}},Return:$$$mptypes.Element}),{Value:$$$mptypes.Element,Element:$$$mptypes.Element}),[{$nm:'p1',$mt:'prm',$t:'Element'},{$nm:'p2',$mt:'prm',$t:'Element'}],{Arguments:{t:Tuple,a:{Rest:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element}},First:$$$mptypes.Element,Element:$$$mptypes.Element}},Return:{t:Comparison}}),elements$688,{Element:$$$mptypes.Element});
};
sort.$$metamodel$$={$nm:'sort',$mt:'mthd',$t:{t:Sequential,a:{Element:'Element'}},$ps:[{$nm:'elements',$mt:'prm',$t:{t:Iterable,a:{Absent:{t:Null},Element:'Element'}}}],$tp:{Element:{'satisfies':[{t:Comparable,a:{Other:'Element'}}]}},$an:function(){return[doc(String$("Sort the given elements, returning a new sequence.",50)),see([Comparable].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.sort=sort;
var identical=function (x$690,y$691){
    return (x$690===y$691);
};
identical.$$metamodel$$={$nm:'identical',$mt:'mthd',$t:{t:Boolean$},$ps:[{$nm:'x',$mt:'prm',$t:{t:Identifiable}},{$nm:'y',$mt:'prm',$t:{t:Identifiable}}],$an:function(){return[doc(String$("Determine if the arguments are identical. Equivalent to\n`x===y`. Only instances of `Identifiable` have \nwell-defined identity.",126)),see([$JsCallable(identityHash,[{$nm:'x',$mt:'prm',$t:{t:Identifiable}}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Identifiable},Element:{t:Identifiable}}},Return:{t:Integer}})].reifyCeylonType({Absent:{t:Null},Element:{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:{t:Identifiable},Element:{t:Identifiable}}},Return:{t:Integer}}}})),shared()];}};
exports.identical=identical;
var shuffle=function (f$692,$$$mptypes){
    return flatten($JsCallable(function (secondArgs$693){
        return flatten($JsCallable(function (firstArgs$694){
            return unflatten($JsCallable(unflatten($JsCallable(f$692,[],{Arguments:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}}),{Args:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}})(firstArgs$694),[],{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}),{Args:$$$mptypes.SecondArgs,Return:$$$mptypes.Result})(secondArgs$693);
        },[{$nm:'firstArgs',$mt:'prm',$t:'FirstArgs'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.FirstArgs,Element:$$$mptypes.FirstArgs}},Return:$$$mptypes.Result}),{Args:$$$mptypes.FirstArgs,Return:$$$mptypes.Result});
    },[{$nm:'secondArgs',$mt:'prm',$t:'SecondArgs'}],{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.SecondArgs,Element:$$$mptypes.SecondArgs}},Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}}),{Args:$$$mptypes.SecondArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}});
};
shuffle.$$metamodel$$={$nm:'shuffle',$mt:'mthd',$t:{t:Callable,a:{Arguments:'SecondArgs',Return:{t:Callable,a:{Arguments:'FirstArgs',Return:'Result'}}}},$ps:[{$nm:'f',$mt:'prm',$t:{t:Callable,a:{Arguments:'FirstArgs',Return:{t:Callable,a:{Arguments:'SecondArgs',Return:'Result'}}}}}],$tp:{Result:{},FirstArgs:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]},SecondArgs:{'satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},$an:function(){return[shared()];}};
exports.shuffle=shuffle;
var times=function (x$695,y$696,$$$mptypes){
    return x$695.times(y$696);
};
times.$$metamodel$$={$nm:'times',$mt:'mthd',$t:'Value',$ps:[{$nm:'x',$mt:'prm',$t:'Value'},{$nm:'y',$mt:'prm',$t:'Value'}],$tp:{Value:{'satisfies':[{t:Numeric,a:{Other:'Value'}}]}},$an:function(){return[doc(String$("Multiply the given `Numeric` values.",36)),see([plus,product].reifyCeylonType({Absent:{t:Null},Element:{t:Anything}})),shared()];}};
exports.times=times;
function Function$metamodel$untyped($$function){
    Declaration$metamodel$untyped($$function);
    Parameterised$metamodel$untyped($$function);
}
Function$metamodel$untyped.$$metamodel$$={$nm:'Function',$mt:'ifc',satisfies:[{t:Declaration$metamodel$untyped},{t:Parameterised$metamodel$untyped}],$an:function(){return[shared()];}};
exports.Function$metamodel$untyped=Function$metamodel$untyped;
function $init$Function$metamodel$untyped(){
    if (Function$metamodel$untyped.$$===undefined){
        initTypeProto(Function$metamodel$untyped,'ceylon.language.metamodel.untyped::Function',$init$Declaration$metamodel$untyped(),$init$Parameterised$metamodel$untyped());
        (function($$function){
        })(Function$metamodel$untyped.$$.prototype);
    }
    return Function$metamodel$untyped;
}
exports.$init$Function$metamodel$untyped=$init$Function$metamodel$untyped;
$init$Function$metamodel$untyped();
function Value$metamodel$untyped($$value){
    Declaration$metamodel$untyped($$value);
}
Value$metamodel$untyped.$$metamodel$$={$nm:'Value',$mt:'ifc',satisfies:[{t:Declaration$metamodel$untyped}],$an:function(){return[shared()];}};
exports.Value$metamodel$untyped=Value$metamodel$untyped;
function $init$Value$metamodel$untyped(){
    if (Value$metamodel$untyped.$$===undefined){
        initTypeProto(Value$metamodel$untyped,'ceylon.language.metamodel.untyped::Value',$init$Declaration$metamodel$untyped());
        (function($$value){
        })(Value$metamodel$untyped.$$.prototype);
    }
    return Value$metamodel$untyped;
}
exports.$init$Value$metamodel$untyped=$init$Value$metamodel$untyped;
$init$Value$metamodel$untyped();
function Module$metamodel$untyped($$module){
    Identifiable($$module);
}
Module$metamodel$untyped.$$metamodel$$={$nm:'Module',$mt:'ifc',satisfies:[{t:Identifiable}],$an:function(){return[shared()];}};
exports.Module$metamodel$untyped=Module$metamodel$untyped;
function $init$Module$metamodel$untyped(){
    if (Module$metamodel$untyped.$$===undefined){
        initTypeProto(Module$metamodel$untyped,'ceylon.language.metamodel.untyped::Module',$init$Identifiable());
        (function($$module){
        })(Module$metamodel$untyped.$$.prototype);
    }
    return Module$metamodel$untyped;
}
exports.$init$Module$metamodel$untyped=$init$Module$metamodel$untyped;
$init$Module$metamodel$untyped();
function Package$metamodel$untyped($$package){
    Identifiable($$package);
}
Package$metamodel$untyped.$$metamodel$$={$nm:'Package',$mt:'ifc',satisfies:[{t:Identifiable}],$an:function(){return[shared()];}};
exports.Package$metamodel$untyped=Package$metamodel$untyped;
function $init$Package$metamodel$untyped(){
    if (Package$metamodel$untyped.$$===undefined){
        initTypeProto(Package$metamodel$untyped,'ceylon.language.metamodel.untyped::Package',$init$Identifiable());
        (function($$package){
        })(Package$metamodel$untyped.$$.prototype);
    }
    return Package$metamodel$untyped;
}
exports.$init$Package$metamodel$untyped=$init$Package$metamodel$untyped;
$init$Package$metamodel$untyped();
function Parameterised$metamodel$untyped($$parameterised){
}
Parameterised$metamodel$untyped.$$metamodel$$={$nm:'Parameterised',$mt:'ifc',satisfies:[],$an:function(){return[shared()];}};
exports.Parameterised$metamodel$untyped=Parameterised$metamodel$untyped;
function $init$Parameterised$metamodel$untyped(){
    if (Parameterised$metamodel$untyped.$$===undefined){
        initTypeProto(Parameterised$metamodel$untyped,'ceylon.language.metamodel.untyped::Parameterised');
        (function($$parameterised){
        })(Parameterised$metamodel$untyped.$$.prototype);
    }
    return Parameterised$metamodel$untyped;
}
exports.$init$Parameterised$metamodel$untyped=$init$Parameterised$metamodel$untyped;
$init$Parameterised$metamodel$untyped();
function Type$metamodel$untyped($$type){
}
Type$metamodel$untyped.$$metamodel$$={$nm:'Type',$mt:'ifc',satisfies:[],$an:function(){return[shared()];}};
exports.Type$metamodel$untyped=Type$metamodel$untyped;
function $init$Type$metamodel$untyped(){
    if (Type$metamodel$untyped.$$===undefined){
        initTypeProto(Type$metamodel$untyped,'ceylon.language.metamodel.untyped::Type');
    }
    return Type$metamodel$untyped;
}
exports.$init$Type$metamodel$untyped=$init$Type$metamodel$untyped;
$init$Type$metamodel$untyped();
function ParameterisedType$metamodel$untyped($$parameterisedType){
    Type$metamodel$untyped($$parameterisedType);
}
ParameterisedType$metamodel$untyped.$$metamodel$$={$nm:'ParameterisedType',$mt:'ifc',$tp:{DeclarationType:{'var':'out','satisfies':[{t:ClassOrInterface$metamodel$untyped}]}},satisfies:[{t:Type$metamodel$untyped}],$an:function(){return[shared()];}};
exports.ParameterisedType$metamodel$untyped=ParameterisedType$metamodel$untyped;
function $init$ParameterisedType$metamodel$untyped(){
    if (ParameterisedType$metamodel$untyped.$$===undefined){
        initTypeProto(ParameterisedType$metamodel$untyped,'ceylon.language.metamodel.untyped::ParameterisedType',$init$Type$metamodel$untyped());
        (function($$parameterisedType){
        })(ParameterisedType$metamodel$untyped.$$.prototype);
    }
    return ParameterisedType$metamodel$untyped;
}
exports.$init$ParameterisedType$metamodel$untyped=$init$ParameterisedType$metamodel$untyped;
$init$ParameterisedType$metamodel$untyped();
function Member$metamodel$untyped($$member){
    Callable($$member);
    add_type_arg($$member,'Arguments',{t:Empty});
}
Member$metamodel$untyped.$$metamodel$$={$nm:'Member',$mt:'ifc',$tp:{Kind:{'satisfies':[{t:Declaration$metamodel$untyped}]}},satisfies:[{t:Callable,a:{Arguments:{t:Empty},Return:'Kind'}}],$an:function(){return[shared()];}};
exports.Member$metamodel$untyped=Member$metamodel$untyped;
function $init$Member$metamodel$untyped(){
    if (Member$metamodel$untyped.$$===undefined){
        initTypeProto(Member$metamodel$untyped,'ceylon.language.metamodel.untyped::Member',$init$Callable());
        (function($$member){
        })(Member$metamodel$untyped.$$.prototype);
    }
    return Member$metamodel$untyped;
}
exports.$init$Member$metamodel$untyped=$init$Member$metamodel$untyped;
$init$Member$metamodel$untyped();
function TypeParameter$metamodel$untyped($$typeParameter){
}
TypeParameter$metamodel$untyped.$$metamodel$$={$nm:'TypeParameter',$mt:'ifc',satisfies:[],$an:function(){return[shared()];}};
exports.TypeParameter$metamodel$untyped=TypeParameter$metamodel$untyped;
function $init$TypeParameter$metamodel$untyped(){
    if (TypeParameter$metamodel$untyped.$$===undefined){
        initTypeProto(TypeParameter$metamodel$untyped,'ceylon.language.metamodel.untyped::TypeParameter');
        (function($$typeParameter){
        })(TypeParameter$metamodel$untyped.$$.prototype);
    }
    return TypeParameter$metamodel$untyped;
}
exports.$init$TypeParameter$metamodel$untyped=$init$TypeParameter$metamodel$untyped;
$init$TypeParameter$metamodel$untyped();
function Class$metamodel$untyped($$class){
    ClassOrInterface$metamodel$untyped($$class);
}
Class$metamodel$untyped.$$metamodel$$={$nm:'Class',$mt:'ifc',satisfies:[{t:ClassOrInterface$metamodel$untyped}],$an:function(){return[shared()];}};
exports.Class$metamodel$untyped=Class$metamodel$untyped;
function $init$Class$metamodel$untyped(){
    if (Class$metamodel$untyped.$$===undefined){
        initTypeProto(Class$metamodel$untyped,'ceylon.language.metamodel.untyped::Class',$init$ClassOrInterface$metamodel$untyped());
        (function($$class){
        })(Class$metamodel$untyped.$$.prototype);
    }
    return Class$metamodel$untyped;
}
exports.$init$Class$metamodel$untyped=$init$Class$metamodel$untyped;
$init$Class$metamodel$untyped();
function nothingType$697$metamodel$untyped(){
    var $$nothingType=new nothingType$697$metamodel$untyped.$$;
    Type$metamodel$untyped($$nothingType);
    return $$nothingType;
}
function $init$nothingType$697$metamodel$untyped(){
    if (nothingType$697$metamodel$untyped.$$===undefined){
        initTypeProto(nothingType$697$metamodel$untyped,'ceylon.language.metamodel.untyped::nothingType',Basic,$init$Type$metamodel$untyped());
    }
    return nothingType$697$metamodel$untyped;
}
exports.$init$nothingType$697$metamodel$untyped=$init$nothingType$697$metamodel$untyped;
$init$nothingType$697$metamodel$untyped();
var nothingType$698$metamodel$untyped=nothingType$697$metamodel$untyped();
var getNothingType$metamodel$untyped=function(){
    return nothingType$698$metamodel$untyped;
}
exports.getNothingType$metamodel$untyped=getNothingType$metamodel$untyped;
function TypeParameterType$metamodel$untyped($$typeParameterType){
    Type$metamodel$untyped($$typeParameterType);
}
TypeParameterType$metamodel$untyped.$$metamodel$$={$nm:'TypeParameterType',$mt:'ifc',satisfies:[{t:Type$metamodel$untyped}],$an:function(){return[shared()];}};
exports.TypeParameterType$metamodel$untyped=TypeParameterType$metamodel$untyped;
function $init$TypeParameterType$metamodel$untyped(){
    if (TypeParameterType$metamodel$untyped.$$===undefined){
        initTypeProto(TypeParameterType$metamodel$untyped,'ceylon.language.metamodel.untyped::TypeParameterType',$init$Type$metamodel$untyped());
        (function($$typeParameterType){
        })(TypeParameterType$metamodel$untyped.$$.prototype);
    }
    return TypeParameterType$metamodel$untyped;
}
exports.$init$TypeParameterType$metamodel$untyped=$init$TypeParameterType$metamodel$untyped;
$init$TypeParameterType$metamodel$untyped();
function Interface$metamodel$untyped($$interface){
    ClassOrInterface$metamodel$untyped($$interface);
}
Interface$metamodel$untyped.$$metamodel$$={$nm:'Interface',$mt:'ifc',satisfies:[{t:ClassOrInterface$metamodel$untyped}],$an:function(){return[shared()];}};
exports.Interface$metamodel$untyped=Interface$metamodel$untyped;
function $init$Interface$metamodel$untyped(){
    if (Interface$metamodel$untyped.$$===undefined){
        initTypeProto(Interface$metamodel$untyped,'ceylon.language.metamodel.untyped::Interface',$init$ClassOrInterface$metamodel$untyped());
        (function($$interface){
        })(Interface$metamodel$untyped.$$.prototype);
    }
    return Interface$metamodel$untyped;
}
exports.$init$Interface$metamodel$untyped=$init$Interface$metamodel$untyped;
$init$Interface$metamodel$untyped();
function UnionType$metamodel$untyped($$unionType){
    Type$metamodel$untyped($$unionType);
}
UnionType$metamodel$untyped.$$metamodel$$={$nm:'UnionType',$mt:'ifc',satisfies:[{t:Type$metamodel$untyped}],$an:function(){return[shared()];}};
exports.UnionType$metamodel$untyped=UnionType$metamodel$untyped;
function $init$UnionType$metamodel$untyped(){
    if (UnionType$metamodel$untyped.$$===undefined){
        initTypeProto(UnionType$metamodel$untyped,'ceylon.language.metamodel.untyped::UnionType',$init$Type$metamodel$untyped());
        (function($$unionType){
        })(UnionType$metamodel$untyped.$$.prototype);
    }
    return UnionType$metamodel$untyped;
}
exports.$init$UnionType$metamodel$untyped=$init$UnionType$metamodel$untyped;
$init$UnionType$metamodel$untyped();
function IntersectionType$metamodel$untyped($$intersectionType){
    Type$metamodel$untyped($$intersectionType);
}
IntersectionType$metamodel$untyped.$$metamodel$$={$nm:'IntersectionType',$mt:'ifc',satisfies:[{t:Type$metamodel$untyped}],$an:function(){return[shared()];}};
exports.IntersectionType$metamodel$untyped=IntersectionType$metamodel$untyped;
function $init$IntersectionType$metamodel$untyped(){
    if (IntersectionType$metamodel$untyped.$$===undefined){
        initTypeProto(IntersectionType$metamodel$untyped,'ceylon.language.metamodel.untyped::IntersectionType',$init$Type$metamodel$untyped());
        (function($$intersectionType){
        })(IntersectionType$metamodel$untyped.$$.prototype);
    }
    return IntersectionType$metamodel$untyped;
}
exports.$init$IntersectionType$metamodel$untyped=$init$IntersectionType$metamodel$untyped;
$init$IntersectionType$metamodel$untyped();
function Declaration$metamodel$untyped($$declaration){
}
Declaration$metamodel$untyped.$$metamodel$$={$nm:'Declaration',$mt:'ifc',satisfies:[],$an:function(){return[shared()];}};
exports.Declaration$metamodel$untyped=Declaration$metamodel$untyped;
function $init$Declaration$metamodel$untyped(){
    if (Declaration$metamodel$untyped.$$===undefined){
        initTypeProto(Declaration$metamodel$untyped,'ceylon.language.metamodel.untyped::Declaration');
        (function($$declaration){
        })(Declaration$metamodel$untyped.$$.prototype);
    }
    return Declaration$metamodel$untyped;
}
exports.$init$Declaration$metamodel$untyped=$init$Declaration$metamodel$untyped;
$init$Declaration$metamodel$untyped();
function ClassOrInterface$metamodel$untyped($$classOrInterface){
    Declaration$metamodel$untyped($$classOrInterface);
    Parameterised$metamodel$untyped($$classOrInterface);
}
ClassOrInterface$metamodel$untyped.$$metamodel$$={$nm:'ClassOrInterface',$mt:'ifc',satisfies:[{t:Declaration$metamodel$untyped},{t:Parameterised$metamodel$untyped}],$an:function(){return[shared()];}};
exports.ClassOrInterface$metamodel$untyped=ClassOrInterface$metamodel$untyped;
function $init$ClassOrInterface$metamodel$untyped(){
    if (ClassOrInterface$metamodel$untyped.$$===undefined){
        initTypeProto(ClassOrInterface$metamodel$untyped,'ceylon.language.metamodel.untyped::ClassOrInterface',$init$Declaration$metamodel$untyped(),$init$Parameterised$metamodel$untyped());
        (function($$classOrInterface){
        })(ClassOrInterface$metamodel$untyped.$$.prototype);
    }
    return ClassOrInterface$metamodel$untyped;
}
exports.$init$ClassOrInterface$metamodel$untyped=$init$ClassOrInterface$metamodel$untyped;
$init$ClassOrInterface$metamodel$untyped();
function Annotated$metamodel($$annotated){
}
Annotated$metamodel.$$metamodel$$={$nm:'Annotated',$mt:'ifc',satisfies:[],$an:function(){return[doc(String$("A program element that can\nbe annotated.",40)),shared()];}};
exports.Annotated$metamodel=Annotated$metamodel;
function $init$Annotated$metamodel(){
    if (Annotated$metamodel.$$===undefined){
        initTypeProto(Annotated$metamodel,'ceylon.language.metamodel::Annotated');
    }
    return Annotated$metamodel;
}
exports.$init$Annotated$metamodel=$init$Annotated$metamodel;
$init$Annotated$metamodel();
function SequencedAnnotation$metamodel($$sequencedAnnotation){
    ConstrainedAnnotation$metamodel($$sequencedAnnotation);
    add_type_arg($$sequencedAnnotation,'Values',{t:Sequential,a:{Element:$$sequencedAnnotation.$$targs$$.Value}});
}
SequencedAnnotation$metamodel.$$metamodel$$={$nm:'SequencedAnnotation',$mt:'ifc',$tp:{Value:{'var':'out','satisfies':[{t:Annotation$metamodel,a:{Value:'Value'}}]},ProgramElement:{'var':'in','satisfies':[{t:Annotated$metamodel}]}},satisfies:[{t:ConstrainedAnnotation$metamodel,a:{Values:{t:Sequential,a:{Element:'Value'}},Value:'Value',ProgramElement:'ProgramElement'}}],$an:function(){return[doc(String$("An annotation that may occur multiple times\nat a single program element.",72)),shared()];}};
exports.SequencedAnnotation$metamodel=SequencedAnnotation$metamodel;
function $init$SequencedAnnotation$metamodel(){
    if (SequencedAnnotation$metamodel.$$===undefined){
        initTypeProto(SequencedAnnotation$metamodel,'ceylon.language.metamodel::SequencedAnnotation',$init$ConstrainedAnnotation$metamodel());
    }
    return SequencedAnnotation$metamodel;
}
exports.$init$SequencedAnnotation$metamodel=$init$SequencedAnnotation$metamodel;
$init$SequencedAnnotation$metamodel();
function Function$metamodel($$function){
    Callable($$function);
    Declaration$metamodel($$function);
}
Function$metamodel.$$metamodel$$={$nm:'Function',$mt:'ifc',$tp:{Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:Callable,a:{Arguments:'Arguments',Return:'Type'}},{t:Declaration$metamodel}],$an:function(){return[shared()];}};
exports.Function$metamodel=Function$metamodel;
function $init$Function$metamodel(){
    if (Function$metamodel.$$===undefined){
        initTypeProto(Function$metamodel,'ceylon.language.metamodel::Function',$init$Callable(),$init$Declaration$metamodel());
        (function($$function){
        })(Function$metamodel.$$.prototype);
    }
    return Function$metamodel;
}
exports.$init$Function$metamodel=$init$Function$metamodel;
$init$Function$metamodel();
function Value$metamodel($$value){
    Declaration$metamodel($$value);
}
Value$metamodel.$$metamodel$$={$nm:'Value',$mt:'ifc',$tp:{Type:{'var':'out',}},satisfies:[{t:Declaration$metamodel}],$an:function(){return[shared()];}};
exports.Value$metamodel=Value$metamodel;
function $init$Value$metamodel(){
    if (Value$metamodel.$$===undefined){
        initTypeProto(Value$metamodel,'ceylon.language.metamodel::Value',$init$Declaration$metamodel());
        (function($$value){
        })(Value$metamodel.$$.prototype);
    }
    return Value$metamodel;
}
exports.$init$Value$metamodel=$init$Value$metamodel;
$init$Value$metamodel();
function Type$metamodel($$type){
    Annotated$metamodel($$type);
}
Type$metamodel.$$metamodel$$={$nm:'Type',$mt:'ifc',$tp:{Instance:{'var':'out',}},satisfies:[{t:Annotated$metamodel}],$an:function(){return[shared()];}};
exports.Type$metamodel=Type$metamodel;
function $init$Type$metamodel(){
    if (Type$metamodel.$$===undefined){
        initTypeProto(Type$metamodel,'ceylon.language.metamodel::Type',$init$Annotated$metamodel());
    }
    return Type$metamodel;
}
exports.$init$Type$metamodel=$init$Type$metamodel;
$init$Type$metamodel();
function AppliedType$metamodel($$appliedType){
}
AppliedType$metamodel.$$metamodel$$={$nm:'AppliedType',$mt:'ifc',satisfies:[],$an:function(){return[shared()];}};
exports.AppliedType$metamodel=AppliedType$metamodel;
function $init$AppliedType$metamodel(){
    if (AppliedType$metamodel.$$===undefined){
        initTypeProto(AppliedType$metamodel,'ceylon.language.metamodel::AppliedType');
    }
    return AppliedType$metamodel;
}
exports.$init$AppliedType$metamodel=$init$AppliedType$metamodel;
$init$AppliedType$metamodel();
function OptionalAnnotation$metamodel($$optionalAnnotation){
    ConstrainedAnnotation$metamodel($$optionalAnnotation);
}
OptionalAnnotation$metamodel.$$metamodel$$={$nm:'OptionalAnnotation',$mt:'ifc',$tp:{Value:{'var':'out','satisfies':[{t:Annotation$metamodel,a:{Value:'Value'}}]},ProgramElement:{'var':'in','satisfies':[{t:Annotated$metamodel}]}},satisfies:[{t:ConstrainedAnnotation$metamodel,a:{Values:{ t:'u', l:[{t:Null},'Value']},Value:'Value',ProgramElement:'ProgramElement'}}],$an:function(){return[doc(String$("An annotation that may occur at most once\nat a single program element.",70)),shared()];}};
exports.OptionalAnnotation$metamodel=OptionalAnnotation$metamodel;
function $init$OptionalAnnotation$metamodel(){
    if (OptionalAnnotation$metamodel.$$===undefined){
        initTypeProto(OptionalAnnotation$metamodel,'ceylon.language.metamodel::OptionalAnnotation',$init$ConstrainedAnnotation$metamodel());
    }
    return OptionalAnnotation$metamodel;
}
exports.$init$OptionalAnnotation$metamodel=$init$OptionalAnnotation$metamodel;
$init$OptionalAnnotation$metamodel();
function Member$metamodel($$member){
    Callable($$member);
    add_type_arg($$member,'Arguments',{t:Tuple,a:{Rest:{t:Empty},First:$$member.$$targs$$.Type,Element:$$member.$$targs$$.Type}});
}
Member$metamodel.$$metamodel$$={$nm:'Member',$mt:'ifc',$tp:{Type:{},Kind:{'satisfies':[{t:Declaration$metamodel}]}},satisfies:[{t:Callable,a:{Arguments:{t:Tuple,a:{Rest:{t:Empty},First:'Type',Element:'Type'}},Return:'Kind'}}],$an:function(){return[shared()];}};
exports.Member$metamodel=Member$metamodel;
function $init$Member$metamodel(){
    if (Member$metamodel.$$===undefined){
        initTypeProto(Member$metamodel,'ceylon.language.metamodel::Member',$init$Callable());
        (function($$member){
        })(Member$metamodel.$$.prototype);
    }
    return Member$metamodel;
}
exports.$init$Member$metamodel=$init$Member$metamodel;
$init$Member$metamodel();
function Class$metamodel($$class){
    ClassOrInterface$metamodel($$class);
    Callable($$class);
}
Class$metamodel.$$metamodel$$={$nm:'Class',$mt:'ifc',$tp:{Type:{'var':'out',},Arguments:{'var':'in','satisfies':[{t:Sequential,a:{Element:{t:Anything}}}]}},satisfies:[{t:ClassOrInterface$metamodel,a:{Type:'Type'}},{t:Callable,a:{Arguments:'Arguments',Return:'Type'}}],$an:function(){return[shared()];}};
exports.Class$metamodel=Class$metamodel;
function $init$Class$metamodel(){
    if (Class$metamodel.$$===undefined){
        initTypeProto(Class$metamodel,'ceylon.language.metamodel::Class',$init$ClassOrInterface$metamodel(),$init$Callable());
        (function($$class){
        })(Class$metamodel.$$.prototype);
    }
    return Class$metamodel;
}
exports.$init$Class$metamodel=$init$Class$metamodel;
$init$Class$metamodel();
function nothingType$699$metamodel(){
    var $$nothingType=new nothingType$699$metamodel.$$;
    AppliedType$metamodel($$nothingType);
    return $$nothingType;
}
function $init$nothingType$699$metamodel(){
    if (nothingType$699$metamodel.$$===undefined){
        initTypeProto(nothingType$699$metamodel,'ceylon.language.metamodel::nothingType',Basic,$init$AppliedType$metamodel());
    }
    return nothingType$699$metamodel;
}
exports.$init$nothingType$699$metamodel=$init$nothingType$699$metamodel;
$init$nothingType$699$metamodel();
var nothingType$700$metamodel=nothingType$699$metamodel();
var getNothingType$metamodel=function(){
    return nothingType$700$metamodel;
}
exports.getNothingType$metamodel=getNothingType$metamodel;
function Variable$metamodel($$variable){
    Value$metamodel($$variable);
}
Variable$metamodel.$$metamodel$$={$nm:'Variable',$mt:'ifc',$tp:{Type:{}},satisfies:[{t:Value$metamodel,a:{Type:'Type'}}],$an:function(){return[shared()];}};
exports.Variable$metamodel=Variable$metamodel;
function $init$Variable$metamodel(){
    if (Variable$metamodel.$$===undefined){
        initTypeProto(Variable$metamodel,'ceylon.language.metamodel::Variable',$init$Value$metamodel());
        (function($$variable){
        })(Variable$metamodel.$$.prototype);
    }
    return Variable$metamodel;
}
exports.$init$Variable$metamodel=$init$Variable$metamodel;
$init$Variable$metamodel();
function Interface$metamodel($$interface){
    ClassOrInterface$metamodel($$interface);
}
Interface$metamodel.$$metamodel$$={$nm:'Interface',$mt:'ifc',$tp:{Type:{'var':'out',}},satisfies:[{t:ClassOrInterface$metamodel,a:{Type:'Type'}}],$an:function(){return[shared()];}};
exports.Interface$metamodel=Interface$metamodel;
function $init$Interface$metamodel(){
    if (Interface$metamodel.$$===undefined){
        initTypeProto(Interface$metamodel,'ceylon.language.metamodel::Interface',$init$ClassOrInterface$metamodel());
        (function($$interface){
        })(Interface$metamodel.$$.prototype);
    }
    return Interface$metamodel;
}
exports.$init$Interface$metamodel=$init$Interface$metamodel;
$init$Interface$metamodel();
function ConstrainedAnnotation$metamodel($$constrainedAnnotation){
    Annotation$metamodel($$constrainedAnnotation);
}
ConstrainedAnnotation$metamodel.$$metamodel$$={$nm:'ConstrainedAnnotation',$mt:'ifc',$tp:{Value:{'var':'out','satisfies':[{t:Annotation$metamodel,a:{Value:'Value'}}]},Values:{'var':'out',},ProgramElement:{'var':'in','satisfies':[{t:Annotated$metamodel}]}},satisfies:[{t:Annotation$metamodel,a:{Value:'Value'}}],$an:function(){return[doc(String$("An annotation. This interface encodes\nconstraints upon the annotation in its\ntype arguments.",92)),shared()];}};
exports.ConstrainedAnnotation$metamodel=ConstrainedAnnotation$metamodel;
function $init$ConstrainedAnnotation$metamodel(){
    if (ConstrainedAnnotation$metamodel.$$===undefined){
        initTypeProto(ConstrainedAnnotation$metamodel,'ceylon.language.metamodel::ConstrainedAnnotation',$init$Annotation$metamodel());
        (function($$constrainedAnnotation){
            $$constrainedAnnotation.occurs=function occurs(programElement$701){
                var $$constrainedAnnotation=this;
                throw Exception();
            };$$constrainedAnnotation.occurs.$$metamodel$$={$nm:'occurs',$mt:'mthd',$t:{t:Boolean},$ps:[{$nm:'programElement',$mt:'prm',$t:{t:Annotated$metamodel}}],$an:function(){return[shared()];}};
        })(ConstrainedAnnotation$metamodel.$$.prototype);
    }
    return ConstrainedAnnotation$metamodel;
}
exports.$init$ConstrainedAnnotation$metamodel=$init$ConstrainedAnnotation$metamodel;
$init$ConstrainedAnnotation$metamodel();
function UnionType$metamodel($$unionType){
    AppliedType$metamodel($$unionType);
}
UnionType$metamodel.$$metamodel$$={$nm:'UnionType',$mt:'ifc',satisfies:[{t:AppliedType$metamodel}],$an:function(){return[shared()];}};
exports.UnionType$metamodel=UnionType$metamodel;
function $init$UnionType$metamodel(){
    if (UnionType$metamodel.$$===undefined){
        initTypeProto(UnionType$metamodel,'ceylon.language.metamodel::UnionType',$init$AppliedType$metamodel());
        (function($$unionType){
        })(UnionType$metamodel.$$.prototype);
    }
    return UnionType$metamodel;
}
exports.$init$UnionType$metamodel=$init$UnionType$metamodel;
$init$UnionType$metamodel();
function annotations$metamodel(annotationType$702,programElement$703,$$$mptypes){
    throw Exception();
}
exports.annotations$metamodel=annotations$metamodel;
annotations$metamodel.$$metamodel$$={$nm:'annotations',$mt:'mthd',$t:'Values',$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:Type$metamodel,a:{Instance:{t:ConstrainedAnnotation$metamodel,a:{Values:'Values',Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:ConstrainedAnnotation$metamodel,a:{Values:'Values',Value:'Value',ProgramElement:'ProgramElement'}}]},Values:{},ProgramElement:{'satisfies':[{t:Annotated$metamodel}]}},$an:function(){return[shared()];}};//annotations$metamodel.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.ProgramElement,Element:$$$mptypes.ProgramElement}},Return:$$$mptypes.Values};
function optionalAnnotation$metamodel(annotationType$704,programElement$705,$$$mptypes){
    return annotations$metamodel(annotationType$704,programElement$705,{Values:{ t:'u', l:[{t:Null},$$$mptypes.Value]},Value:$$$mptypes.Value,ProgramElement:$$$mptypes.ProgramElement});
}
exports.optionalAnnotation$metamodel=optionalAnnotation$metamodel;
optionalAnnotation$metamodel.$$metamodel$$={$nm:'optionalAnnotation',$mt:'mthd',$t:{ t:'u', l:[{t:Null},'Value']},$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:Type$metamodel,a:{Instance:{t:OptionalAnnotation$metamodel,a:{Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:OptionalAnnotation$metamodel,a:{Value:'Value',ProgramElement:'ProgramElement'}}]},ProgramElement:{'satisfies':[{t:Annotated$metamodel}]}},$an:function(){return[shared()];}};//optionalAnnotation$metamodel.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.ProgramElement,Element:$$$mptypes.ProgramElement}},Return:{ t:'u', l:[{t:Null},$$$mptypes.Value]}};
function sequencedAnnotations$metamodel(annotationType$706,programElement$707,$$$mptypes){
    return annotations$metamodel(annotationType$706,programElement$707,{Values:{t:Sequential,a:{Element:$$$mptypes.Value}},Value:$$$mptypes.Value,ProgramElement:$$$mptypes.ProgramElement});
}
exports.sequencedAnnotations$metamodel=sequencedAnnotations$metamodel;
sequencedAnnotations$metamodel.$$metamodel$$={$nm:'sequencedAnnotations',$mt:'mthd',$t:{t:Sequential,a:{Element:'Value'}},$ps:[{$nm:'annotationType',$mt:'prm',$t:{t:Type$metamodel,a:{Instance:{t:SequencedAnnotation$metamodel,a:{Value:'Value',ProgramElement:'ProgramElement'}}}}},{$nm:'programElement',$mt:'prm',$t:'ProgramElement'}],$tp:{Value:{'satisfies':[{t:SequencedAnnotation$metamodel,a:{Value:'Value',ProgramElement:'ProgramElement'}}]},ProgramElement:{'satisfies':[{t:Annotated$metamodel}]}},$an:function(){return[shared()];}};//sequencedAnnotations$metamodel.$$targs$$={Arguments:{t:Tuple,a:{Rest:{t:Empty},First:$$$mptypes.ProgramElement,Element:$$$mptypes.ProgramElement}},Return:{t:Sequential,a:{Element:$$$mptypes.Value}}};
function IntersectionType$metamodel($$intersectionType){
    AppliedType$metamodel($$intersectionType);
}
IntersectionType$metamodel.$$metamodel$$={$nm:'IntersectionType',$mt:'ifc',satisfies:[{t:AppliedType$metamodel}],$an:function(){return[shared()];}};
exports.IntersectionType$metamodel=IntersectionType$metamodel;
function $init$IntersectionType$metamodel(){
    if (IntersectionType$metamodel.$$===undefined){
        initTypeProto(IntersectionType$metamodel,'ceylon.language.metamodel::IntersectionType',$init$AppliedType$metamodel());
        (function($$intersectionType){
        })(IntersectionType$metamodel.$$.prototype);
    }
    return IntersectionType$metamodel;
}
exports.$init$IntersectionType$metamodel=$init$IntersectionType$metamodel;
$init$IntersectionType$metamodel();
function Declaration$metamodel($$declaration){
    AppliedType$metamodel($$declaration);
}
Declaration$metamodel.$$metamodel$$={$nm:'Declaration',$mt:'ifc',satisfies:[{t:AppliedType$metamodel}],$an:function(){return[shared()];}};
exports.Declaration$metamodel=Declaration$metamodel;
function $init$Declaration$metamodel(){
    if (Declaration$metamodel.$$===undefined){
        initTypeProto(Declaration$metamodel,'ceylon.language.metamodel::Declaration',$init$AppliedType$metamodel());
        (function($$declaration){
        })(Declaration$metamodel.$$.prototype);
    }
    return Declaration$metamodel;
}
exports.$init$Declaration$metamodel=$init$Declaration$metamodel;
$init$Declaration$metamodel();
function Annotation$metamodel($$annotation){
}
Annotation$metamodel.$$metamodel$$={$nm:'Annotation',$mt:'ifc',$tp:{Value:{'var':'out','satisfies':[{t:Annotation$metamodel,a:{Value:'Value'}}]}},satisfies:[],$an:function(){return[doc(String$("An annotation.",14)),shared()];}};
exports.Annotation$metamodel=Annotation$metamodel;
function $init$Annotation$metamodel(){
    if (Annotation$metamodel.$$===undefined){
        initTypeProto(Annotation$metamodel,'ceylon.language.metamodel::Annotation');
    }
    return Annotation$metamodel;
}
exports.$init$Annotation$metamodel=$init$Annotation$metamodel;
$init$Annotation$metamodel();
function ClassOrInterface$metamodel($$classOrInterface){
    Declaration$metamodel($$classOrInterface);
}
ClassOrInterface$metamodel.$$metamodel$$={$nm:'ClassOrInterface',$mt:'ifc',$tp:{Type:{'var':'out',}},satisfies:[{t:Declaration$metamodel}],$an:function(){return[shared()];}};
exports.ClassOrInterface$metamodel=ClassOrInterface$metamodel;
function $init$ClassOrInterface$metamodel(){
    if (ClassOrInterface$metamodel.$$===undefined){
        initTypeProto(ClassOrInterface$metamodel,'ceylon.language.metamodel::ClassOrInterface',$init$Declaration$metamodel());
        (function($$classOrInterface){
        })(ClassOrInterface$metamodel.$$.prototype);
    }
    return ClassOrInterface$metamodel;
}
exports.$init$ClassOrInterface$metamodel=$init$ClassOrInterface$metamodel;
$init$ClassOrInterface$metamodel();
function Annotation($$annotation){
    $init$Annotation();
    if ($$annotation===undefined)$$annotation=new Annotation.$$;
    return $$annotation;
}
Annotation.$$metamodel$$={$nm:'Annotation',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Annotation=Annotation;
function $init$Annotation(){
    if (Annotation.$$===undefined){
        initTypeProto(Annotation,'ceylon.language::Annotation',Basic);
    }
    return Annotation;
}
exports.$init$Annotation=$init$Annotation;
$init$Annotation();
var annotation=function (){
    return Annotation();
};
annotation.$$metamodel$$={$nm:'annotation',$mt:'mthd',$t:{t:Annotation},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.annotation=annotation;
function Shared($$shared){
    $init$Shared();
    if ($$shared===undefined)$$shared=new Shared.$$;
    return $$shared;
}
Shared.$$metamodel$$={$nm:'Shared',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Shared=Shared;
function $init$Shared(){
    if (Shared.$$===undefined){
        initTypeProto(Shared,'ceylon.language::Shared',Basic);
    }
    return Shared;
}
exports.$init$Shared=$init$Shared;
$init$Shared();
var shared=function (){
    return Shared();
};
shared.$$metamodel$$={$nm:'shared',$mt:'mthd',$t:{t:Shared},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.shared=shared;
function Variable($$variable){
    $init$Variable();
    if ($$variable===undefined)$$variable=new Variable.$$;
    return $$variable;
}
Variable.$$metamodel$$={$nm:'Variable',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Variable=Variable;
function $init$Variable(){
    if (Variable.$$===undefined){
        initTypeProto(Variable,'ceylon.language::Variable',Basic);
    }
    return Variable;
}
exports.$init$Variable=$init$Variable;
$init$Variable();
var variable=function (){
    return Variable();
};
variable.$$metamodel$$={$nm:'variable',$mt:'mthd',$t:{t:Variable},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.variable=variable;
function Abstract($$abstract){
    $init$Abstract();
    if ($$abstract===undefined)$$abstract=new Abstract.$$;
    return $$abstract;
}
Abstract.$$metamodel$$={$nm:'Abstract',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Abstract=Abstract;
function $init$Abstract(){
    if (Abstract.$$===undefined){
        initTypeProto(Abstract,'ceylon.language::Abstract',Basic);
    }
    return Abstract;
}
exports.$init$Abstract=$init$Abstract;
$init$Abstract();
var abstract=function (){
    return Abstract();
};
abstract.$$metamodel$$={$nm:'abstract',$mt:'mthd',$t:{t:Abstract},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.abstract=abstract;
function Final($$final){
    $init$Final();
    if ($$final===undefined)$$final=new Final.$$;
    return $$final;
}
Final.$$metamodel$$={$nm:'Final',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Final=Final;
function $init$Final(){
    if (Final.$$===undefined){
        initTypeProto(Final,'ceylon.language::Final',Basic);
    }
    return Final;
}
exports.$init$Final=$init$Final;
$init$Final();
var $final=function (){
    return Final();
};
$final.$$metamodel$$={$nm:'final',$mt:'mthd',$t:{t:Final},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.$final=$final;
function Actual($$actual){
    $init$Actual();
    if ($$actual===undefined)$$actual=new Actual.$$;
    return $$actual;
}
Actual.$$metamodel$$={$nm:'Actual',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Actual=Actual;
function $init$Actual(){
    if (Actual.$$===undefined){
        initTypeProto(Actual,'ceylon.language::Actual',Basic);
    }
    return Actual;
}
exports.$init$Actual=$init$Actual;
$init$Actual();
var actual=function (){
    return Actual();
};
actual.$$metamodel$$={$nm:'actual',$mt:'mthd',$t:{t:Actual},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.actual=actual;
function Formal($$formal){
    $init$Formal();
    if ($$formal===undefined)$$formal=new Formal.$$;
    return $$formal;
}
Formal.$$metamodel$$={$nm:'Formal',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Formal=Formal;
function $init$Formal(){
    if (Formal.$$===undefined){
        initTypeProto(Formal,'ceylon.language::Formal',Basic);
    }
    return Formal;
}
exports.$init$Formal=$init$Formal;
$init$Formal();
var formal=function (){
    return Formal();
};
formal.$$metamodel$$={$nm:'formal',$mt:'mthd',$t:{t:Formal},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.formal=formal;
function Default($$default){
    $init$Default();
    if ($$default===undefined)$$default=new Default.$$;
    return $$default;
}
Default.$$metamodel$$={$nm:'Default',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Default=Default;
function $init$Default(){
    if (Default.$$===undefined){
        initTypeProto(Default,'ceylon.language::Default',Basic);
    }
    return Default;
}
exports.$init$Default=$init$Default;
$init$Default();
var $default=function (){
    return Default();
};
$default.$$metamodel$$={$nm:'default',$mt:'mthd',$t:{t:Default},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.$default=$default;
function Late($$late){
    $init$Late();
    if ($$late===undefined)$$late=new Late.$$;
    return $$late;
}
Late.$$metamodel$$={$nm:'Late',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Late=Late;
function $init$Late(){
    if (Late.$$===undefined){
        initTypeProto(Late,'ceylon.language::Late',Basic);
    }
    return Late;
}
exports.$init$Late=$init$Late;
$init$Late();
var late=function (){
    return Late();
};
late.$$metamodel$$={$nm:'late',$mt:'mthd',$t:{t:Late},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.late=late;
function Native($$native){
    $init$Native();
    if ($$native===undefined)$$native=new Native.$$;
    return $$native;
}
Native.$$metamodel$$={$nm:'Native',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Native=Native;
function $init$Native(){
    if (Native.$$===undefined){
        initTypeProto(Native,'ceylon.language::Native',Basic);
    }
    return Native;
}
exports.$init$Native=$init$Native;
$init$Native();
var $native=function (){
    return Native();
};
$native.$$metamodel$$={$nm:'native',$mt:'mthd',$t:{t:Native},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.$native=$native;
function Doc(description, $$doc){
    $init$Doc();
    if ($$doc===undefined)$$doc=new Doc.$$;
    return $$doc;
}
Doc.$$metamodel$$={$nm:'Doc',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Doc=Doc;
function $init$Doc(){
    if (Doc.$$===undefined){
        initTypeProto(Doc,'ceylon.language::Doc',Basic);
    }
    return Doc;
}
exports.$init$Doc=$init$Doc;
$init$Doc();
var doc=function (description$708){
    return Doc(description$708);
};
doc.$$metamodel$$={$nm:'doc',$mt:'mthd',$t:{t:Doc},$ps:[{$nm:'description',$mt:'prm',$t:{t:String$}}],$an:function(){return[shared(),annotation()];}};
exports.doc=doc;
function See(programElements, $$see){
    $init$See();
    if ($$see===undefined)$$see=new See.$$;
    $$see.$$targs$$={Value:{t:See},ProgramElement:{t:Type$metamodel,a:{Instance:{t:Anything}}}};
    if(programElements===undefined){programElements=getEmpty();}
    SequencedAnnotation$metamodel($$see);
    add_type_arg($$see,'Value',{t:See});
    add_type_arg($$see,'ProgramElement',{t:Type$metamodel,a:{Instance:{t:Anything}}});
    return $$see;
}
See.$$metamodel$$={$nm:'See',$mt:'cls','super':{t:Basic},satisfies:[{t:SequencedAnnotation$metamodel,a:{Value:{t:See},ProgramElement:{t:Type$metamodel,a:{Instance:{t:Anything}}}}}],$an:function(){return[shared(),annotation()];}};
exports.See=See;
function $init$See(){
    if (See.$$===undefined){
        initTypeProto(See,'ceylon.language::See',Basic,$init$SequencedAnnotation$metamodel());
    }
    return See;
}
exports.$init$See=$init$See;
$init$See();
var see=function (programElements$709){
    if(programElements$709===undefined){programElements$709=getEmpty();}
    return See(programElements$709);
};
see.$$metamodel$$={$nm:'see',$mt:'mthd',$t:{t:See},$ps:[{$nm:'programElements',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:Anything}}}}],$an:function(){return[shared(),annotation()];}};
exports.see=see;
function Authors(authors, $$authors){
    $init$Authors();
    if ($$authors===undefined)$$authors=new Authors.$$;
    if(authors===undefined){authors=getEmpty();}
    return $$authors;
}
Authors.$$metamodel$$={$nm:'Authors',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Authors=Authors;
function $init$Authors(){
    if (Authors.$$===undefined){
        initTypeProto(Authors,'ceylon.language::Authors',Basic);
    }
    return Authors;
}
exports.$init$Authors=$init$Authors;
$init$Authors();
var by=function (authors$710){
    if(authors$710===undefined){authors$710=getEmpty();}
    return Authors(authors$710);
};
by.$$metamodel$$={$nm:'by',$mt:'mthd',$t:{t:Authors},$ps:[{$nm:'authors',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:String$}}}}],$an:function(){return[shared(),annotation()];}};
exports.by=by;
function ThrownException(type, when, $$thrownException){
    $init$ThrownException();
    if ($$thrownException===undefined)$$thrownException=new ThrownException.$$;
    $$thrownException.$$targs$$={Value:{t:See},ProgramElement:{t:Type$metamodel,a:{Instance:{t:Anything}}}};
    SequencedAnnotation$metamodel($$thrownException);
    add_type_arg($$thrownException,'Value',{t:See});
    add_type_arg($$thrownException,'ProgramElement',{t:Type$metamodel,a:{Instance:{t:Anything}}});
    return $$thrownException;
}
ThrownException.$$metamodel$$={$nm:'ThrownException',$mt:'cls','super':{t:Basic},satisfies:[{t:SequencedAnnotation$metamodel,a:{Value:{t:See},ProgramElement:{t:Type$metamodel,a:{Instance:{t:Anything}}}}}],$an:function(){return[shared(),annotation()];}};
exports.ThrownException=ThrownException;
function $init$ThrownException(){
    if (ThrownException.$$===undefined){
        initTypeProto(ThrownException,'ceylon.language::ThrownException',Basic,$init$SequencedAnnotation$metamodel());
    }
    return ThrownException;
}
exports.$init$ThrownException=$init$ThrownException;
$init$ThrownException();
var $throws=function (type$711,when$712){
    if(when$712===undefined){when$712=String$("",0);}
    return ThrownException(type$711,when$712);
};
$throws.$$metamodel$$={$nm:'throws',$mt:'mthd',$t:{t:ThrownException},$ps:[{$nm:'type',$mt:'prm',$t:{t:Anything}},{$nm:'when',$mt:'prm',$def:1,$t:{t:String$}}],$an:function(){return[shared(),annotation()];}};
exports.$throws=$throws;
function Deprecation(description, $$deprecation){
    $init$Deprecation();
    if ($$deprecation===undefined)$$deprecation=new Deprecation.$$;
    $$deprecation.description=description;
    return $$deprecation;
}
Deprecation.$$metamodel$$={$nm:'Deprecation',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Deprecation=Deprecation;
function $init$Deprecation(){
    if (Deprecation.$$===undefined){
        initTypeProto(Deprecation,'ceylon.language::Deprecation',Basic);
        (function($$deprecation){
            defineAttr($$deprecation,'reason',function()/*anotaciones:ceylon.language::Shared,*/{
                var $$deprecation=this;
                if($$deprecation.description.empty){
                    return null;
                }
                return $$deprecation.description;
            });
        })(Deprecation.$$.prototype);
    }
    return Deprecation;
}
exports.$init$Deprecation=$init$Deprecation;
$init$Deprecation();
var deprecated=function (reason$713){
    if(reason$713===undefined){reason$713=String$("",0);}
    return Deprecation(reason$713);
};
deprecated.$$metamodel$$={$nm:'deprecated',$mt:'mthd',$t:{t:Deprecation},$ps:[{$nm:'reason',$mt:'prm',$def:1,$t:{t:String$}}],$an:function(){return[shared(),annotation()];}};
exports.deprecated=deprecated;
function Tags(tags, $$tags){
    $init$Tags();
    if ($$tags===undefined)$$tags=new Tags.$$;
    if(tags===undefined){tags=getEmpty();}
    return $$tags;
}
Tags.$$metamodel$$={$nm:'Tags',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Tags=Tags;
function $init$Tags(){
    if (Tags.$$===undefined){
        initTypeProto(Tags,'ceylon.language::Tags',Basic);
    }
    return Tags;
}
exports.$init$Tags=$init$Tags;
$init$Tags();
var tagged=function (tags$714){
    if(tags$714===undefined){tags$714=getEmpty();}
    return Tags(tags$714);
};
tagged.$$metamodel$$={$nm:'tagged',$mt:'mthd',$t:{t:Tags},$ps:[{$nm:'tags',$mt:'prm',seq:1,$t:{t:Sequential,a:{Element:{t:String$}}}}],$an:function(){return[shared(),annotation()];}};
exports.tagged=tagged;
function License(url, $$license){
    $init$License();
    if ($$license===undefined)$$license=new License.$$;
    return $$license;
}
License.$$metamodel$$={$nm:'License',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.License=License;
function $init$License(){
    if (License.$$===undefined){
        initTypeProto(License,'ceylon.language::License',Basic);
    }
    return License;
}
exports.$init$License=$init$License;
$init$License();
var license=function (url$715){
    return License(url$715);
};
license.$$metamodel$$={$nm:'license',$mt:'mthd',$t:{t:License},$ps:[{$nm:'url',$mt:'prm',$t:{t:String$}}],$an:function(){return[shared(),annotation()];}};
exports.license=license;
function Optional($$optional){
    $init$Optional();
    if ($$optional===undefined)$$optional=new Optional.$$;
    return $$optional;
}
Optional.$$metamodel$$={$nm:'Optional',$mt:'cls','super':{t:Basic},satisfies:[],$an:function(){return[shared(),annotation()];}};
exports.Optional=Optional;
function $init$Optional(){
    if (Optional.$$===undefined){
        initTypeProto(Optional,'ceylon.language::Optional',Basic);
    }
    return Optional;
}
exports.$init$Optional=$init$Optional;
$init$Optional();
var optional=function (){
    return Optional();
};
optional.$$metamodel$$={$nm:'optional',$mt:'mthd',$t:{t:Optional},$ps:[],$an:function(){return[shared(),annotation()];}};
exports.optional=optional;
//Ends compiled from Ceylon sources
function type$metamodel(x) {
if (x === null) {
return getNothingType$metamodel();
} else {
//Search for metamodel
var mm = x.$$metamodel$$;
if (mm === undefined && x.constructor && x.constructor.T$name && x.constructor.T$all) {
//It's probably an instance of a Ceylon type
var _x = x.constructor.T$all[x.constructor.T$name];
if (_x) {
mm = _x.$$metamodel$$;
}
}
if (mm && mm['$mt']) {
var metatype = mm['$mt'];
if (metatype === 'ifc') { //Interface
//
} else if (metatype === 'cls') { //Class
} else if (metatype === 'mthd') { //Method
} else {
}
} else {
throw Exception(String$("No metamodel available for "+x));
}
}
return null;
}
type$metamodel.$$metamodel$$={$nm:'type',$mt:'mthd',$ps:[{t:Anything}],$an:function(){return[shared()];}};
exports.type$metamodel=type$metamodel;
function Number$(wat) {
return wat;
}
initType(Number$, 'ceylon.language::Number');
Number$.$$metamodel$$={$nm:'Number',$mt:'ifc',$an:function(){return[shared()]}};
exports.Number=Number$;
function $init$Number$() {
if (Number$.$$===undefined) {
initType(Number$, 'ceylon.language::Number');
}
return Number$;
}
function JSNumber(value) { return Number(value); }
initExistingType(JSNumber, Number, 'ceylon.language::JSNumber');
JSNumber.$$metamodel$$={$nm:'JSNumber',$mt:'cls',$an:function(){return[shared()];}};
var origNumToString = Number.prototype.toString;
inheritProto(JSNumber, Object$, Scalar, $init$Integral(), Exponentiable);
function Integer(value) { return Number(value); }
initTypeProto(Integer, 'ceylon.language::Integer', Object$, Scalar,
$init$Integral(), Exponentiable, Binary);
Integer.$$metamodel$$={$nm:'Integer',$mt:'cls',$an:function(){return[shared(),abstract()];}};
function Float(value) {
var that = new Number(value);
that.float$ = true;
return that;
}
initTypeProto(Float, 'ceylon.language::Float', Object$, Scalar, Exponentiable);
Float.$$metamodel$$={$nm:'Float',$mt:'cls',$an:function(){return[shared(),abstract()];}};
var JSNum$proto = Number.prototype;
JSNum$proto.getT$all = function() {
return (this.float$ ? Float : Integer).$$.T$all;
}
JSNum$proto.getT$name = function() {
return (this.float$ ? Float : Integer).$$.T$name;
}
JSNum$proto.toString = origNumToString;
defineAttr(JSNum$proto, 'string', function(){ return String$(this.toString()); });
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
return (this/other)|0;
}
JSNum$proto.remainder = function(other) { return this%other; }
JSNum$proto.power = function(exp) {
if (this.float$||exp.float$) { return Float(Math.pow(this, exp)); }
if (exp<0 && this!=1 && this!=-1) {
throw Exception(String$("Negative exponent"));
}
return Math.pow(this, exp)|0;
}
defineAttr(JSNum$proto, 'negativeValue', function() {
return this.float$ ? Float(-this) : -this;
});
defineAttr(JSNum$proto, 'positiveValue', function() {
return this.float$ ? this : this.valueOf();
});
JSNum$proto.equals = function(other) { return other==this.valueOf(); }
JSNum$proto.compare = function(other) {
var value = this.valueOf();
return value==other ? equal : (value<other ? smaller:larger);
}
defineAttr(JSNum$proto, '$float', function(){ return Float(this.valueOf()); });
defineAttr(JSNum$proto, 'integer', function(){ return this|0; });
defineAttr(JSNum$proto, 'integerValue', function(){ return this|0; });
defineAttr(JSNum$proto, 'character', function(){ return Character(this.valueOf()); });
defineAttr(JSNum$proto, 'successor', function(){ return this+1; });
defineAttr(JSNum$proto, 'predecessor', function(){ return this-1; });
defineAttr(JSNum$proto, 'unit', function(){ return this == 1; });
defineAttr(JSNum$proto, 'zero', function(){ return this == 0; });
defineAttr(JSNum$proto, 'fractionalPart', function() {
if (!this.float$) { return 0; }
return Float(this - (this>=0 ? Math.floor(this) : Math.ceil(this)));
});
defineAttr(JSNum$proto, 'wholePart', function() {
if (!this.float$) { return this.valueOf(); }
return Float(this>=0 ? Math.floor(this) : Math.ceil(this));
});
defineAttr(JSNum$proto, 'sign', function(){ return this > 0 ? 1 : this < 0 ? -1 : 0; });
defineAttr(JSNum$proto, 'hash', function() {
return this.float$ ? String$(this.toPrecision()).hash : this.valueOf();
});
JSNum$proto.distanceFrom = function(other) {
return (this.float$ ? this.wholePart : this) - other;
}
//Binary interface
defineAttr(JSNum$proto, 'not', function(){ return ~this; });
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
defineAttr(JSNum$proto, 'size', function(){ return 53; });
defineAttr(JSNum$proto, 'magnitude', function(){ return Math.abs(this); });
function $parseInteger(s) {
//xkcd.com/208/
if (s.match(/^[+-]?\d+(_\d+)*[kMGPT]?$/g) === null) {
return null;
}
s = s.replace(/_/g, "");
var mag = null;
if (s.match(/[kMGTP]$/g) !== null) {
mag = s[s.length-1];
s = s.slice(0,-1);
}
var i = parseInt(s);
if (s[0]=='+') s = s.substring(1);
if (s[0]=='-') {
while (s[1]=='0') s='-'+s.substring(2);
} else {
while (s[0]=='0') s=s.substring(1);
}
if (i.toString()!==s) return null;
var factor=1;
switch(mag) {
case 'P':factor*=1000;
case 'T':factor*=1000;
case 'G':factor*=1000;
case 'M':factor*=1000;
case 'k':factor*=1000;
}
return isNaN(i) ? null : i*factor;
}
function $parseFloat(s) { return Float(parseFloat(s)); }
defineAttr(JSNum$proto, 'undefined', function(){ return isNaN(this); });
defineAttr(JSNum$proto, 'finite', function(){ return this!=Infinity && this!=-Infinity && !isNaN(this); });
defineAttr(JSNum$proto, 'infinite', function(){ return this==Infinity || this==-Infinity; });
defineAttr(JSNum$proto, 'strictlyPositive', function(){ return this>0 || (this==0 && (1/this==Infinity)); });
defineAttr(JSNum$proto, 'strictlyNegative', function() { return this<0 || (this==0 && (1/this==-Infinity)); });
var $infinity = Float(Infinity);
function getInfinity() { return $infinity; }
//TODO metamodel
//function getNegativeInfinity() { return Float(-Infinity); }
exports.Integer=Integer;
exports.Float=Float;
exports.getInfinity=getInfinity;
exports.parseInteger=$parseInteger;
exports.parseFloat=$parseFloat;
function String$(value,size) {
var that = new String(value);
that.codePoints = size;
return that;
}
String$.$$metamodel$$={$nm:'String',$mt:'cls',$ps:[],$an:function(){return[shared(),abstract(),native()];}};
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
String$proto.compare = function(other) {
var cmp = this.localeCompare(other);
return cmp===0 ? equal : (cmp<0 ? smaller:larger);
}
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
String$proto.spanFrom = function(from) {
return this.span(from, 0x7fffffff);
}
String$proto.spanTo = function(to) {
return to < 0 ? String$('', 0) : this.span(0, to);
}
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
String$proto.shorterThan = function(length) {
if (this.codePoints!==undefined) {return this.codePoints<length}
if (this.length < length) {return true}
if (this.length<<1 >= length) {return false}
this.codePoints = countCodepoints(this);
return this.codePoints<length;
}
String$proto.iterator= function() {
return this.length === 0 ? getEmptyIterator() : StringIterator(this);
}
String$proto.get = function(index) {
if (index<0 || index>=this.length) {return null}
var i = 0;
for (var count=0; count<index; count++) {
if ((this.charCodeAt(i)&0xfc00) === 0xd800) {++i}
if (++i >= this.length) {return null}
}
return Character(codepointFromString(this, i));
}
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
String$proto.trimCharacters = function(/*Category*/chars) {
var from = 0;
while (from<this.length && chars.contains(this.get(from))) {++from}
var to = this.length;
if (from < to) {
do {--to} while (from<to && chars.contains(this.get(to)));
++to;
}
if (from===0 && to===this.length) {return this;}
var result = String$(this.substring(from, to));
if (this.codePoints !== undefined) {
result.codePoints = this.codePoints - from - this.length + to;
}
return result;
}
String$proto.trimLeadingCharacters = function(/*Category*/chars) {
var from = 0;
while (from<this.length && chars.contains(this.get(from))) {++from}
if (from===0) {return this;}
var result = String$(this.substring(from, this.length));
if (this.codePoints !== undefined) {
result.codePoints = this.codePoints - from;
}
return result;
}
String$proto.trimTrailingCharacters = function(/*Category*/chars) {
var to = this.length;
if (to > 0) {
do {--to} while (to>=0 && chars.contains(this.get(to)));
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
String$proto.endsWith = function(str) {
var start = this.length - str.length
if (start < 0) {return false}
return cmpSubString(this, str, start);
}
String$proto.contains = function(sub) {
var str;
if (sub.constructor === String) {str = sub}
else if (sub.constructor !== Character.$$) {return false}
else {str = codepointToString(sub.value)}
return this.indexOf(str) >= 0;
}
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
defineAttr(String$proto, 'characters', function() {
return this.length>0 ? this:getEmpty();
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
String$proto.repeat = function(times) {
var sb = StringBuilder();
for (var i = 0; i < times; i++) {
sb.append(this);
}
return sb.string;
}
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
String$proto.$filter = function(f) {
var r = Iterable.$$.prototype.$filter.apply(this, [f]);
return string(r);
}
String$proto.skipping = function(skip) {
if (skip==0) return this;
return this.segment(skip, this.size);
}
String$proto.taking = function(take) {
if (take==0) return getEmpty();
return this.segment(0, take);
}
String$proto.by = function(step) {
var r = Iterable.$$.prototype.by.apply(this, [step]);
return string(r);
}
String$proto.$sort = function(f) {
var r = Iterable.$$.prototype.$sort.apply(this, [f]);
return string(r);
}
defineAttr(String$proto, 'coalesced', function(){ return this; });
function StringIterator(string) {
var that = new StringIterator.$$;
that.str = string;
that.index = 0;
return that;
}
StringIterator.$$metamodel$$={$nm:'StringIterator',$mt:'cls',$ps:[{t:String$}],$an:function(){return[shared()];}};
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
var that = new Character.$$;
that.value = value;
return that;
}
Character.$$metamodel$$={$nm:'Character',$mt:'cls',$ps:[],$an:function(){return[shared(),abstract()];}};
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
StringBuilder.$$metamodel$$={$nm:'StringBuilder',$mt:'cls',$ps:[],$an:function(){return[shared()];}};
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
function getNull() { return null }
function Boolean$(value) {return Boolean(value)}
initExistingTypeProto(Boolean$, Boolean, 'ceylon.language::Boolean');
Boolean$.$$metamodel$$={$nm:'Boolean',$mt:'cls',$ps:[],$an:function(){return[shared(),abstract()]}};
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
defineAttr(Boolean.prototype, 'hash', function(){ return this.valueOf()?1:0; });
var trueString = String$("true", 4);
var falseString = String$("false", 5);
defineAttr(Boolean.prototype, 'string', function(){ return this.valueOf()?trueString:falseString; });
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
Comparison.$$metamodel$$={$nm:'Comparison',$mt:'cls',$ps:[{t:String$}],$an:function(){return[shared(),abstract()]}};
var Comparison$proto = Comparison.$$.prototype;
defineAttr(Comparison$proto, 'string', function(){ return this.name; });
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
if (obj.$$metamodel$$['$mt'] === 'mthd') {
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
className.$$metamodel$$={$nm:'className',$mt:'mthd',$an:function(){return[shared()];}};
function identityHash(obj) {
return obj.BasicID;
}
identityHash.$$metamodel$$={$nm:'identityHash',$mt:'mthd',$an:function(){return[shared()];}};
function set_type_args(obj, targs) {
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
function string(/*Iterable<Character>*/chars) {
if (chars === undefined) return String$('',0);
var s = StringBuilder();
var iter = chars.iterator();
var c; while ((c = iter.next()) !== getFinished()) {
s.appendCharacter(c);
}
return s.string;
}
string.$$metamodel$$={$nm:'string',$mt:'mthd',$an:function(){return[shared()];}};
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
string.$$metamodel$$={$nm:'internalSort',$mt:'mthd',$an:function(){return[shared()];}};
exports.string=string;
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
flatten.$$metamodel$$={$nm:'flatten',$mt:'mthd',$an:function(){return[shared()];}};
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
unflatten.$$metamodel$$={$nm:'unflatten',$mt:'mthd',$an:function(){return[shared()];}};
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
integerRangeByIterable.$$metamodel$$={$nm:'integerRangeByIterable',$mt:'mthd',$an:function(){return[shared()];}};
exports.integerRangeByIterable=integerRangeByIterable;
function Array$() {
var that = new Array$.$$;
List(that);
return that;
}
Array$.$$metamodel$$={$nm:'Array',$mt:'cls',$ps:[],$an:function(){return[shared(),abstract(),native()];}};
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
exports.array.$$metamodel$$={$nm:'array',$mt:'mthd',$an:function(){return[shared()];}};
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
exports.arrayOfSize.$$metamodel$$={$nm:'arrayOfSize',$mt:'mthd',$an:function(){return[shared()];}};
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
SequenceBuilder.$$metamodel$$={$nm:'SequenceBuilder',$mt:'cls',$ps:[],$an:function(){return[shared()];}};
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
SequenceAppender.$$metamodel$$={$nm:'SequenceAppender',$mt:'cls',$ps:[],$an:function(){return[shared()];}};
initTypeProto(SequenceAppender, 'ceylon.language::SequenceAppender', SequenceBuilder);
exports.Sequence=Sequence;
exports.SequenceBuilder=SequenceBuilder;
exports.SequenceAppender=SequenceAppender;
exports.ArraySequence=ArraySequence;
// implementation of object "process" in ceylon.language
function languageClass() {
var lang = new languageClass.$$;
Basic(lang);
return lang;
}
languageClass.$$metamodel$$={$nm:'languageClass',$mt:'cls',$ps:[],$an:function(){return[shared()];}};
initTypeProto(languageClass, "ceylon.language::language", $init$Basic());
var lang$proto=languageClass.$$.prototype;
defineAttr(lang$proto, 'version', function() {
return String$("0.6",3);
});
defineAttr(lang$proto, 'majorVersion', function(){ return 0; });
defineAttr(lang$proto, 'minorVersion', function(){ return 6; });
defineAttr(lang$proto, 'releaseVersion', function(){ return 0; });
defineAttr(lang$proto, 'versionName', function(){ return String$("Transmogrifier",14); });
defineAttr(lang$proto, 'majorVersionBinary', function(){ return 5; });
defineAttr(lang$proto, 'minorVersionBinary', function(){ return 0; });
var languageString = String$("language", 8);
defineAttr(lang$proto, 'string', function() {
return languageString;
});
var language$ = languageClass();
function getLanguage() { return language$; }
exports.getLanguage=getLanguage;
function processClass() {
var proc = new processClass.$$;
Basic(proc);
return proc;
}
processClass.$$metamodel$$={$nm:'processClass',$mt:'cls',$ps:[],$an:function(){return[shared()];}};
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
defineAttr(process$proto, 'arguments', function(){ return argv; });
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
defineAttr(process$proto, 'newline', function(){ return linesep; });
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
});
defineAttr(process$proto, 'nanoseconds', function() {
return Date.now()*1000000;
});
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
});
defineAttr(process$proto, 'vm', function() {
if (typeof process !== "undefined" && process.execPath && process.execPath.match(/node(\.exe)?$/)) {
return String$("node.js", 7);
} else if (typeof window === 'object') {
return String$("Browser", 7);
}
return String$("Unknown JavaScript environment", 30);
});
defineAttr(process$proto, 'vmVersion', function() {
if (typeof process !== "undefined" && typeof process.version === 'string') {
return String$(process.version);
}
return String$("Unknown");
});
defineAttr(process$proto, 'os',function() {
if (typeof process !== "undefined" && typeof process.platform === 'string') {
return String$(process.platform);
}
return String$("Unknown");
});
defineAttr(process$proto, 'osVersion', function() {
return String$("Unknown");
});
var process$ = processClass();
function getProcess() { return process$; }
exports.getProcess=getProcess;
//Turns out we need to have empty implementations of these annotations
exports.see=function(){};
exports.by=function(){};
exports.tagged=function(){};
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
NativeException.$$metamodel$$={$nm:'NativeException',$mt:'cls',$ps:[{t:Exception}],$an:function(){return[shared()];}};
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
