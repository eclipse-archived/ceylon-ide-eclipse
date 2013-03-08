(function(define) {
define(function(require, exports, module) {
//the Ceylon language module
var $$metamodel$$={"$mod-name":"ceylon.language","$mod-version":"0.5","ceylon.language":{"Iterator":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Iterable"],"doc":["Produces elements of an `Iterable` object. Classes that \nimplement this interface should be immutable."],"by":["Gavin"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The next element, or `finished` if there are no \nmore elements to be iterated."]},"$nm":"next"}},"$nm":"Iterator"},"Callable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[],"doc":["A reference to a method or function."]},"$nm":"Callable"},"copyArray":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"source"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"target"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Efficiently copy the elements of one array to another \narray."]},"$nm":"copyArray"},"Array":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Cloneable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Ranged"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"abstract":[],"shared":[],"native":[],"doc":["A fixed-size array of elements. An array may have zero\nsize (an empty array). Arrays are mutable. Any element\nof an array may be set to a new value.\n\nThis class is provided primarily to support interoperation \nwith Java, and for some performance-critical low-level \nprogramming tasks."]},"$m":{"setItem":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Replace the existing element at the specified index \nwith the given element. Does nothing if the specified \nindex is negative or larger than the index of the \nlast element in the array."]},"$nm":"setItem"}},"$at":{"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the array, without the first element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this array, returning a new array."],"actual":[]},"$nm":"reversed"}},"$nm":"Array"},"Singleton":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A sequence with exactly one element, which may be null."]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"a"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns the contained element, if the specified \nindex is `0`."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `1` if this `Singleton`'s element\nsatisfies the predicate, or `0` otherwise."],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A `Singleton` can be equal to another `List` if \nthat `List` has only one element which is equal to \nthis `Singleton`'s element."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0` and the given `length` is greater than `0`.\nOtherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `true` if the specified element is this \n`Singleton`'s element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \nis `0`. Otherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `0`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `Singleton` with the same element."],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the Singleton itself, or empty"],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["Return this singleton."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `Empty`."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `1`."],"actual":[]},"$nm":"size"}},"$nm":"Singleton"},"byKey":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Key","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"}],"$an":{"shared":[],"see":["byItem"],"doc":["A comparator for `Entry`s which compares their keys \naccording to the given `comparing()` function."]},"$nm":"byKey"},"Comparable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Other"}],"$an":{"shared":[],"doc":["The general contract for values whose magnitude can be \ncompared. `Comparable` imposes a total ordering upon\ninstances of any type that satisfies the interface.\nIf a type `T` satisfies `Comparable<T>`, then instances\nof `T` may be compared using the comparison operators\n`<`, `>`, `<=`, >=`, and `<=>`.\n\nThe total order of a type must be consistent with the \ndefinition of equality for the type. That is, there\nare three mutually exclusive possibilities:\n\n- `x<y`,\n- `x>y`, or\n- `x==y`"],"by":["Gavin"]},"$m":{"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["equals"],"doc":["Compares this value with the given value. \nImplementations must respect the constraints that: \n\n- `x==y` if and only if `x<=>y == equal` \n   (consistency with `equals()`), \n- if `x>y` then `y<x` (symmetry), and \n- if `x>y` and `y>z` then `x>z` (transitivity)."]},"$nm":"compare"}},"$nm":"Comparable","$st":"Other"},"Comparison":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"larger"},{"$pk":"ceylon.language","$nm":"smaller"},{"$pk":"ceylon.language","$nm":"equal"}],"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Comparable"],"doc":["The result of a comparison between two `Comparable` \nobjects."],"by":["Gavin"]},"$m":{"largerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"largerThan"},"equal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"equal"},"asSmallAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asSmallAs"},"asLargeAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asLargeAs"},"smallerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"smallerThan"},"unequal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"unequal"}},"$nm":"Comparison"},"Empty":{"of":[{"$pk":"ceylon.language","$nm":"empty"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$an":{"shared":[],"see":["Sequence"],"doc":["A sequence with no elements. The type `Empty` may be\nabbreviated `[]`, and an instance is produced by the \nexpression `[]`. That is, in the following expression,\n`e` has type `[]` and refers to the value `[]`:\n\n    [] none = [];\n\n(Whether the syntax `[]` refers to the type or the \nvalue depends upon how it occurs grammatically.)"]},"$m":{"trailedBy":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"trailing"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"trailedBy"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["Returns an iterator that is already exhausted."],"actual":[]},"$nm":"iterator"},"sort":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `null` for any given index."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns 0 for any given predicate."],"actual":[]},"$nm":"count"},"select":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"select"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given segment."],"actual":[]},"$nm":"segment"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `false` for any given element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"withTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"defines"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanTo"},"chain":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"doc":["Returns `other`."],"actual":[]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"leadBy":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"leading"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"leadBy"},"withLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"collect":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"collect"},"span":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"clone"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"indexed"},"sequence":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a string description of the empty sequence: \n`{}`."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `true`."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns 0."],"actual":[]},"$nm":"size"}},"$nm":"Empty"},"Enumerable":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["Abstraction of ordinal types whose instances can be \nmapped to the integers or to a range of integers."]},"$at":{"integerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The corresponding integer. The implementation must\nsatisfy these constraints:\n\n    (x.successor).integerValue = x.integerValue+1\n    (x.predecessor).integerValue = x.integerValue-1\n\nfor every instance `x` of the enumerable type."]},"$nm":"integerValue"}},"$nm":"Enumerable","$st":"Other"},"empty":{"super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Empty"}],"$mt":"obj","$an":{"shared":[],"doc":["A sequence with no elements, abbreviated `[]`. The \nunique instance of the type `[]`."]},"$nm":"empty"},"combine":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"},{"$t":"OtherElement","$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"prm","$pt":"f","$nm":"combination"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"otherElements"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"},{"$nm":"Element"},{"$nm":"OtherElement"}],"$an":{"shared":[],"doc":["Applies a function to each element of two `Iterable`s\nand returns an `Iterable` with the results."],"by":["Gavin","Enrique Zamudio","Tako"]},"$nm":"combine"},"compose":{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"X"},{"$nm":"Y"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[]},"$nm":"compose"},"false":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing falsity in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"false"},"Sequential":{"of":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A possibly-empty, immutable sequence of values. The \ntype `Sequential<Element>` may be abbreviated \n`[Element*]` or `Element[]`. \n\n`Sequential` has two enumerated subtypes:\n\n- `Empty`, abbreviated `[]`, represents an empty \n   sequence, and\n- `Sequence<Element>`, abbreviated `[Element+]` \n   represents a non-empty sequence, and has the very\n   important subclass `Tuple`."]},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"[ x, y, z ]\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \nelement."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new sequence."],"actual":[]},"$nm":"reversed"}},"$nm":"Sequential"},"Finished":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"finished"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Iterator"],"doc":["The type of the value that indicates that \nan `Iterator` is exhausted and has no more \nvalues to return."]},"$nm":"Finished"},"final":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a class as final. A `final` class \nmay not be extended."]},"$nm":"final"},"coalesce":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Return a sequence containing the given values which are\nnot null. If there are no values which are not null,\nreturn an empty sequence."]},"$nm":"coalesce"},"plus":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["times","sum"],"doc":["Add the given `Summable` values."]},"$nm":"plus"},"Entry":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Key","$hdn":"1","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$hdn":"1","$mt":"prm","$pt":"v","$nm":"item"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A pair containing a _key_ and an associated value called \nthe _item_. Used primarily to represent the elements of \na `Map`. The type `Entry<Key,Item>` may be abbreviated \n`Key->Item`. An instance of `Entry` may be constructed \nusing the `->` operator:\n\n    String->Person entry = person.name->person;\n"],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if this entry is equal to the given\nentry. Two entries are equal if they have the same\nkey and the same value."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a description of the entry in the form \n`key->item`."],"actual":[]},"$nm":"string"},"item":{"$t":{"$nm":"Item"},"$mt":"attr","$an":{"shared":[],"doc":["The value associated with the key."]},"$nm":"item"},"key":{"$t":{"$nm":"Key"},"$mt":"attr","$an":{"shared":[],"doc":["The key used to access the entry."]},"$nm":"key"}},"$nm":"Entry"},"Cloneable":{"of":[{"$nm":"Clone"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Clone"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"variance":"out","$nm":"Clone"}],"$an":{"shared":[],"doc":["Abstract supertype of objects whose value can be \ncloned."]},"$at":{"clone":{"$t":{"$nm":"Clone"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Obtain a clone of this object. For a mutable \nobject, this should return a copy of the object. \nFor an immutable object, it is acceptable to return\nthe object itself."]},"$nm":"clone"}},"$nm":"Cloneable","$st":"Clone"},"Invertable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Inverse"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of types which support a unary additive inversion\noperation. For a numeric type, this should return the \nnegative of the argument value. Note that the type \nparameter of this interface is not restricted to be a \nself type, in order to accommodate the possibility of \ntypes whose additive inverse can only be expressed in terms of \na wider type."],"by":["Gavin"]},"$at":{"positiveValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The value itself, expressed as an instance of the\nwider type."]},"$nm":"positiveValue"},"negativeValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The additive inverse of the value, which may be expressed\nas an instance of a wider type."]},"$nm":"negativeValue"}},"$nm":"Invertable"},"Ordinal":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"see":["Character","Integer","Integral","Range"],"doc":["Abstraction of ordinal types, that is, types with \nsuccessor and predecessor operations, including\n`Integer` and other `Integral` numeric types.\n`Character` is also considered an ordinal type. \n`Ordinal` types may be used to generate a `Range`."],"by":["Gavin"]},"$at":{"predecessor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if this is the minimum value"],"doc":["The predecessor of this value."]},"$nm":"predecessor"},"successor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if this is the maximum value"],"doc":["The successor of this value."]},"$nm":"successor"}},"$nm":"Ordinal","$st":"Other"},"largest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","smallest","max"],"doc":["Given two `Comparable` values, return largest of the\ntwo."]},"$nm":"largest"},"unflatten":{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"flatFunction"}],[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"args"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"unflatten"},"native":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member whose implementation is \nbe provided by platform-native code."]},"$nm":"native"},"greaterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is greater than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"greaterThan"},"Identifiable":{"$mt":"ifc","$an":{"shared":[],"doc":["The abstract supertype of all types with a well-defined\nnotion of identity. Values of type `Identifiable` may \nbe compared using the `===` operator to determine if \nthey are references to the same object instance. For\nthe sake of convenience, this interface defines a\ndefault implementation of value equality equivalent\nto identity. Of course, subtypes are encouraged to\nrefine this implementation."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Identity equality comparing the identity of the two \nvalues. May be refined by subtypes for which value \nequality is more appropriate. Implementations must\nrespect the constraint that if `x===y` then `x==y` \n(equality is consistent with identity)."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["identityHash"],"doc":["The system-defined identity hash value of the \ninstance. Subtypes which refine `equals()` must \nalso refine `hash`, according to the general \ncontract defined by `Object`."],"actual":[]},"$nm":"hash"}},"$nm":"Identifiable"},"language":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Contains information about the language"],"by":["The Ceylon Team"]},"$at":{"majorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language major version."]},"$nm":"majorVersion"},"majorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The major version of the code generated for the underlying runtime."]},"$nm":"majorVersionBinary"},"minorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language minor version."]},"$nm":"minorVersion"},"versionName":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release name."]},"$nm":"versionName"},"releaseVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release version."]},"$nm":"releaseVersion"},"minorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The minor version of the code generated for the underlying runtime."]},"$nm":"minorVersionBinary"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language version."]},"$nm":"version"}},"$nm":"language"},"Null":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"of":[{"$pk":"ceylon.language","$nm":"null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["null"],"doc":["The type of the `null` value. Any union type of form \n`Null|T` is considered an optional type, whose values\ninclude `null`. Any type of this form may be written as\n`T?` for convenience.\n\nThe `if (exists ... )` construct, or, alternatively,\nthe `assert (exists ...)` construct, may be used to\nnarrow an optional type to a _definite_ type, that is,\na subtype of `Object`:\n\n    String? firstArg = process.arguments.first;\n    if (exists firstArg) {\n        print(\"hello \" + firstArg);\n    }\n\nThe `else` operator evaluates its second operand if \nand only if its first operand is `null`:\n\n    String name = process.arguments.first else \"world\";\n\nThe `then` operator evaluates its second operand when\nits first operand evaluates to `true`, and to `null` \notherwise:\n\n    Float? diff = x>=y then x-y;\n\n"],"by":["Gavin"]},"$nm":"Null"},"array":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array containing the given elements. If no\nelements are provided, create an empty array of the\ngiven element type."]},"$nm":"array"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable"],"doc":["Sort a given elements, returning a new sequence."]},"$nm":"sort"},"equalTo":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if they're equal.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"equalTo"},"AssertionException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"message"}],"$mt":"cls","$an":{"shared":[],"doc":["An exception that occurs when an assertion fails, that\nis, when a condition in an `assert` statement evaluates\nto false at runtime."]},"$nm":"AssertionException"},"arrayOfSize":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"size"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array of the specified size, populating every \nindex with the given element. If the specified size is\nsmaller than `1`, return an empty array of the given\nelement type."]},"$nm":"arrayOfSize"},"Ranged":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Index"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Index"},{"variance":"out","$nm":"Span"}],"$an":{"shared":[],"see":["List","Sequence","String"],"doc":["Abstract supertype of ranged objects which map a range\nof `Comparable` keys to ranges of values. The type\nparameter `Span` abstracts the type of the resulting\nrange.\n\nA span may be obtained from an instance of `Ranged`\nusing the span operator:\n\n    print(\"hello world\"[0..5])\n"]},"$m":{"spanTo":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe start of the receiver and the end index."]},"$nm":"spanTo"},"segment":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a segment containing the mapped values\nstarting from the given index, with the given \nlength."]},"$nm":"segment"},"spanFrom":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\nthe starting index and the end of the receiver."]},"$nm":"spanFrom"},"span":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between \nthe two given indices."]},"$nm":"span"}},"$nm":"Ranged"},"times":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["plus","product"],"doc":["Multiply the given `Numeric` values."]},"$nm":"times"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Produces a sequence of each index to element `Entry` \nfor the given sequence of values."]},"$nm":"entries"},"license":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify the URL of the license of a module \nor package."]},"$nm":"license"},"null":{"super":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"obj","$an":{"shared":[],"doc":["The null value."],"by":["Gavin"]},"$nm":"null"},"Object":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Basic","Null"],"doc":["The abstract supertype of all types representing \ndefinite values. Any two `Object`s may be compared\nfor value equality using the `==` and `!=` operators:\n\n    true==false\n    1==\"hello world\"\n    \"hello\"+ \" \" + \"world\"==\"hello world\"\n    Singleton(\"hello world\")=={ \"hello world\" }\n\nHowever, since `Null` is not a subtype of `Object`, the \nvalue `null` cannot be compared to any other value\nusing `==`. Thus, value equality is not defined for \noptional types. This neatly voids the problem of \ndeciding the value of the expression `null==null`, \nwhich is simply illegal."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determine if two values are equal. Implementations\nshould respect the constraints that:\n\n- if `x===y` then `x==y` (reflexivity), \n- if `x==y` then `y==x` (symmetry), \n- if `x==y` and `y==z` then `x==z` (transitivity).\n\nFurthermore it is recommended that implementations\nensure that if `x==y` then `x` and `y` have the\nsame concrete class."]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The hash value of the value, which allows the value\nto be an element of a hash-based set or key of a\nhash-based map. Implementations must respect the\nconstraint that if `x==y` then `x.hash==y.hash`."]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A developer-friendly string representing the \ninstance. Concatenates the name of the concrete \nclass of the instance with the `hash` of the \ninstance. Subclasses are encouraged to refine this \nimplementation to produce a more meaningful \nrepresentation."]},"$nm":"string"}},"$nm":"Object"},"Float":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A 64-bit floating point number. A `Float` is capable of\napproximately representing numeric values between\n2<sup>-1022<\/sup> and \n(2-2<sup>-52<\/sup>)×2<sup>1023<\/sup>, along with \nthe special values `infinity` and `-infinity`, and \nundefined values (Not a Number). Zero is represented by \ndistinct instances `+0`, `-0`, but these instances are \nequal. An undefined value is not equal to any other\nvalue, not even to itself."]},"$at":{"strictlyNegative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a negative number, `-0`, \nor `-infinity`. Produces `false` for a positive \nnumber, `+0`, or undefined."]},"$nm":"strictlyNegative"},"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this value. Produces `1` for a positive \nnumber or `infinity`. Produces `-1` for a negative\nnumber or `-infinity`. Produces `0` for `+0`, `-0`, \nor undefined."],"actual":[]},"$nm":"sign"},"infinite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinity","finite"],"doc":["Determines whether this value is infinite in \nmagnitude. Produces `true` for `infinity` and \n`-infinity`. Produces `false` for a finite number, \n`+0`, `-0`, or undefined."]},"$nm":"infinite"},"undefined":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines whether this value is undefined (that is, \nNot a Number or NaN). The undefined value has the \nproperty that it is not equal (`==`) to itself, as \na consequence the undefined value cannot sensibly \nbe used in most collections."]},"$nm":"undefined"},"strictlyPositive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a positive number, `+0`, \nor `infinity`. Produces `false` for a negative \nnumber, `-0`, or undefined."]},"$nm":"strictlyPositive"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a negative number or\n`-infinity`. Produces `false` for a positive number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a positive number or\n`infinity`. Produces `false` for a negative number, \n`+0`, `-0`, or undefined."],"actual":[]},"$nm":"positive"},"finite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinite","infinity"],"doc":["Determines whether this value is finite. Produces\n`false` for `infinity`, `-infinity`, and undefined."]},"$nm":"finite"}},"$nm":"Float"},"min":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","max","smallest"],"doc":["Given a nonempty sequence of `Comparable` values, \nreturn the smallest value in the sequence."]},"$nm":"min"},"LazySet":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of Set that wraps an `Iterable` of\nelements. All operations on this Set are performed\non the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"complement"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"intersection"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"exclusiveUnion"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"union"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"LazySet"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazySet"},"Collection":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$pk":"ceylon.language","$nm":"Category"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["List","Map","Set"],"doc":["Represents an iterable collection of elements of finite \nsize. `Collection` is the abstract supertype of `List`,\n`Map`, and `Set`.\n\nA `Collection` forms a `Category` of its elements.\n\nAll `Collection`s are `Cloneable`. If a collection is\nimmutable, it is acceptable that `clone` produce a\nreference to the collection itself. If a collection is\nmutable, `clone` should produce an immutable collection\ncontaining references to the same elements, with the\nsame structure as the original collection&mdash;that \nis, it should produce an immutable shallow copy of the\ncollection."]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if the given object is an element of\nthis collection. In this default implementation,\nand in most refining implementations, return `false`\notherwise. An acceptable refining implementation\nmay return `true` for objects which are not \nelements of the collection, but this is not \nrecommended. (For example, the `contains()` method \nof `String` returns `true` for any substring of the\nstring.)"],"actual":[]},"$nm":"contains"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determine if the collection is empty, that is, if \nit has no elements."],"actual":[]},"$nm":"empty"}},"$nm":"Collection"},"deprecated":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"reason"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark program elements which should not be \nused anymore."]},"$nm":"deprecated"},"Range":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"last"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Represents the range of totally ordered, ordinal values \ngenerated by two endpoints of type `Ordinal` and \n`Comparable`. If the first value is smaller than the\nlast value, the range is increasing. If the first value\nis larger than the last value, the range is decreasing.\nIf the two values are equal, the range contains exactly\none element. The range is always nonempty, containing \nat least one value.\n\nA range may be produced using the `..` operator:\n\n    for (i in min..max) { ... }\n    if (char in `A`..`Z`) { ... }\n"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"doc":["An iterator for the elements of the range."],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"n"}]],"$mt":"mthd","$an":{"shared":[],"doc":["The element of the range that occurs `n` values after\nthe start of the range. Note that this operation \nis inefficient for large ranges."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"count"},"segment":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"next":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$nm":"next"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given object."],"actual":[]},"$nm":"contains"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given value."]},"$nm":"includes"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"span":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the end of the range."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since ranges are \nimmutable."],"actual":[]},"$nm":"clone"},"decreasing":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determines if the range is decreasing."]},"$nm":"decreasing"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The end of the range."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this range."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the range itself, since a Range cannot\ncontain nulls."],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["Reverse this range, returning a new range."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"doc":["The rest of the range, without the start of the\nrange."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The start of the range."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The nonzero number of elements in the range."],"actual":[]},"$nm":"size"}},"$nm":"Range"},"Integral":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Integral"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of integral numeric types. That is, types \nwith no fractional part, including `Integer`. The \ndivision operation for integral numeric types results \nin a remainder. Therefore, integral numeric types have \nan operation to determine the remainder of any division \noperation."],"by":["Gavin"]},"$m":{"remainder":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["divided"],"doc":["The remainder, after dividing this number by the \ngiven number."]},"$nm":"remainder"}},"$at":{"unit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is one."]},"$nm":"unit"},"zero":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is zero."]},"$nm":"zero"}},"$nm":"Integral","$st":"Other"},"max":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","min","largest"],"doc":["Given a nonempty sequence of `Comparable` values, \nreturn the largest value in the sequence."]},"$nm":"max"},"SequenceAppender":{"super":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder"],"doc":["This class is used for constructing a new nonempty \nsequence by incrementally appending elements to an\nexisting nonempty sequence. The existing sequence is\nnot modified, since `Sequence`s are immutable. This \nclass is mutable but threadsafe."]},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting nonempty sequence. If no elements \nhave been appended, the original nonempty \nsequence."],"actual":[]},"$nm":"sequence"}},"$nm":"SequenceAppender"},"byIncreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byDecreasing"],"doc":["A comparator which orders elements in increasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byIncreasing"},"larger":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is larger than the given value."],"by":["Gavin"]},"$nm":"larger"},"smallest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","largest","min"],"doc":["Given two `Comparable` values, return smallest of the\ntwo."]},"$nm":"smallest"},"limitedCommaList":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"maxLength"}]],"$mt":"mthd","$nm":"limitedCommaList"},"true":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing truth in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"true"},"join":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"iterables"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["Given a list of iterable objects, return a new sequence \nof all elements of the all given objects. If there are\nno arguments, or if none of the arguments contains any\nelements, return the empty sequence."]},"$nm":"join"},"Exponentiable":{"of":[{"$nm":"This"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"},{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$nm":"This"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types that may be raised to a\npower. Note that the type of the exponent may be\ndifferent to the numeric type which can be \nexponentiated."]},"$m":{"power":{"$t":{"$nm":"This"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of raising this number to the given\npower."]},"$nm":"power"}},"$nm":"Exponentiable","$st":"This"},"curry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}],[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"curry"},"forKey":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forItem"],"doc":["A function that returns the result of the given `resulting()` function \non the key of a given `Entry`."]},"$nm":"forKey"},"Character":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["String"],"doc":["A 32-bit Unicode character."],"by":["Gavin"]},"$at":{"digit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a numeric digit."]},"$nm":"digit"},"uppercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is an uppercase representation of\nthe character."]},"$nm":"uppercase"},"control":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is an ISO control \ncharacter."]},"$nm":"control"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The code point of the character."]},"$nm":"integer"},"letter":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a letter."]},"$nm":"letter"},"lowercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is a lowercase representation of\nthe character."]},"$nm":"lowercase"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The lowercase representation of this character."]},"$nm":"lowercased"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The uppercase representation of this character."]},"$nm":"uppercased"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["A string containg just this character."],"actual":[]},"$nm":"string"},"whitespace":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a whitespace \ncharacter. A whitespace character is:\n* U+0009 HORIZONTAL TABULATION (`\\t`),\n* U+000A LINE FEED (`\\n`),\n* U+000B VERTICAL TABULATION,\n* U+000C FORM FEED (`\\f`),\n* U+000D CARRIAGE RETURN (`\\r`),\n* U+001C FILE SEPARATOR,\n* U+001D GROUP SEPARATOR,\n* U+001E RECORD SEPARATOR,\n* U+001F UNIT SEPARATOR or\n* any Unicode character in the general category *Zs* (space separator), \n  *Zl* (line separator) or *Zp* (paragraph separator)\n  that is not also a non-breaking space.\n"]},"$nm":"whitespace"},"titlecased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The title case representation of this character."]},"$nm":"titlecased"},"titlecase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is a title case representation of\nthe character."]},"$nm":"titlecase"}},"$nm":"Character"},"Keys":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"prm","$pt":"v","$nm":"correspondence"}],"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"contains"}},"$nm":"Keys"},"process":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Represents the current process (instance of the virtual\nmachine)."],"by":["Gavin","Tako"]},"$m":{"readLine":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Read a line of input text from the standard input \nof the virtual machine process."]},"$nm":"readLine"},"writeErrorLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print a line to the standard error of the \nvirtual machine process."]},"$nm":"writeErrorLine"},"writeError":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard error of the \nvirtual machine process."]},"$nm":"writeError"},"propertyValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the given system property of the virtual\nmachine, if any."]},"$nm":"propertyValue"},"namedArgumentValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the first argument of form `-name=value`, \n`--name=value`, or `-name value` specified among the \ncommand line arguments to the virtual machine, if\nany."]},"$nm":"namedArgumentValue"},"write":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard output of the \nvirtual machine process."]},"$nm":"write"},"namedArgumentPresent":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determine if an argument of form `-name` or `--name` \nwas specified among the command line arguments to \nthe virtual machine."]},"$nm":"namedArgumentPresent"},"writeLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["print"],"doc":["Print a line to the standard output of the \nvirtual machine process."]},"$nm":"writeLine"},"exit":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"code"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"exit"}},"$at":{"os":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the operating system this \nprocess is running on."]},"$nm":"os"},"vmVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the virtual machine this \nprocess is running on."]},"$nm":"vmVersion"},"vm":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the virtual machine this \nprocess is running on."]},"$nm":"vm"},"osVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the operating system this \nprocess is running on."]},"$nm":"osVersion"},"newline":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The line ending character sequence on this platform."]},"$nm":"newline"},"arguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The command line arguments to the virtual machine."]},"$nm":"arguments"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"nanoseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in nanoseconds since an arbitrary\nstarting point."]},"$nm":"nanoseconds"},"milliseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in milliseconds since midnight, \n1 January 1970."]},"$nm":"milliseconds"}},"$nm":"process"},"product":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["sum"],"doc":["Given a nonempty sequence of `Numeric` values, return \nthe product of the values."]},"$nm":"product"},"forItem":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forKey"],"doc":["A function that returns the result of the given `resulting()` function \non the item of a given `Entry`."]},"$nm":"forItem"},"shuffle":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"FirstArgs"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"SecondArgs"}],"$an":{"shared":[]},"$nm":"shuffle"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Create a new string containing the given characters."]},"$nm":"string"},"nothing":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"doc":["A value that is assignable to any type, but that \nresults in an exception when evaluated. This is most \nuseful for generating members in an IDE."]},"$nm":"nothing"},"doc":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify API documentation of a program\nelement."]},"$nm":"doc"},"Scalar":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$pk":"ceylon.language","$nm":"Number"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Scalar"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types representing scalar\nvalues, including `Integer` and `Float`."],"by":["Gavin"]},"$at":{"magnitude":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of this number."],"actual":[]},"$nm":"magnitude"},"wholePart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part. For integral numeric types,\nthe integral value of a number is the number \nitself."],"actual":[]},"$nm":"wholePart"},"fractionalPart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part. For integral numeric types,\nthe fractional part is always zero."],"actual":[]},"$nm":"fractionalPart"}},"$nm":"Scalar","$st":"Other"},"tagged":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to categorize the API by tag."]},"$nm":"tagged"},"ifExists":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"prm","$pt":"f","$nm":"predicate"}],[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$nm":"ifExists"},"SequenceBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceAppender","join","Singleton"],"doc":["Since sequences are immutable, this class is used for\nconstructing a new sequence by incrementally appending \nelements to the empty sequence. This class is mutable\nbut threadsafe."]},"$m":{"append":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append an element to the sequence and return this \n`SequenceBuilder`"]},"$nm":"append"},"appendAll":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Append multiple elements to the sequence and return \nthis `SequenceBuilder`"]},"$nm":"appendAll"}},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"native":[],"doc":["The resulting sequence. If no elements have been\nappended, the empty sequence."]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Determine if the resulting sequence is empty."]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The size of the resulting sequence."]},"$nm":"size"}},"$nm":"SequenceBuilder"},"variable":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark an value as variable. A `variable` \nvalue must be assigned multiple times."]},"$nm":"variable"},"Correspondence":{"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Map","List","Category"],"doc":["Abstract supertype of objects which associate values \nwith keys. `Correspondence` does not satisfy `Category`,\nsince in some cases&mdash;`List`, for example&mdash;it is \nconvenient to consider the subtype a `Category` of its\nvalues, and in other cases&mdash;`Map`, for example&mdash;it \nis convenient to treat the subtype as a `Category` of its\nentries.\n\nThe item corresponding to a given key may be obtained \nfrom a `Correspondence` using the item operator:\n\n    value bg = settings[\"backgroundColor\"] else white;\n\nThe `get()` operation and item operator result in an\noptional type, to reflect the possibility that there is\nno item for the given key."],"by":["Gavin"]},"$m":{"definesAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["Determines if this `Correspondence` defines a value\nfor any one of the given keys."]},"$nm":"definesAny"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["definesAny","definesEvery","keys"],"doc":["Determines if there is a value defined for the \ngiven key."]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["items"],"doc":["Returns the value defined for the given key, or \n`null` if there is no value defined for the given \nkey."]},"$nm":"get"},"items":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["get"],"doc":["Returns the items defined for the given keys, in\nthe same order as the corresponding keys."]},"$nm":"items"},"definesEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["Determines if this `Correspondence` defines a value\nfor every one of the given keys."]},"$nm":"definesEvery"}},"$at":{"keys":{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["defines"],"doc":["The `Category` of all keys for which a value is \ndefined by this `Correspondence`."]},"$nm":"keys"}},"$nm":"Correspondence"},"NonemptyContainer":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["A nonempty container."]},"$alias":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"NonemptyContainer"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A count of the number of `true` items in the given values."]},"$nm":"count"},"byItem":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"see":["byKey"],"doc":["A comparator for `Entry`s which compares their items \naccording to the given `comparing()` function."]},"$nm":"byItem"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify API authors."]},"$nm":"by"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["any"],"doc":["Determines if every one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"every"},"$pkg-shared":"1","Tuple":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"First","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Rest","$hdn":"1","$mt":"prm","$pt":"v","$nm":"rest"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$nm":"Element"}],"variance":"out","$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language","$nm":"Empty"},"variance":"out","$nm":"Rest"}],"$an":{"shared":[],"doc":["A _tuple_ is a typed linked list. Each instance of \n`Tuple` represents the value and type of a single link.\nThe attributes `first` and `rest` allow us to retrieve\na value form the list without losing its static type \ninformation.\n\n    value point = Tuple(0.0, Tuple(0.0, Tuple(\"origin\")));\n    Float x = point.first;\n    Float y = point.rest.first;\n    String label = point.rest.rest.first;\n\nUsually, we abbreviate code involving tuples.\n\n    [Float,Float,String] point = [0.0, 0.0, \"origin\"];\n    Float x = point[0];\n    Float y = point[1];\n    String label = point[2];\n\nA list of types enclosed in brackets is an abbreviated \ntuple type. An instance of `Tuple` may be constructed \nby surrounding a value list in brackets:\n\n    [String,String] words = [\"hello\", \"world\"];\n\nThe index operator with a literal integer argument is a \nshortcut for a chain of evaluations of `rest` and \n`first`. For example, `point[1]` means `point.rest.first`.\n\nA _terminated_ tuple type is a tuple where the type of\nthe last link in the chain is `Empty`. An _unterminated_ \ntuple type is a tuple where the type of the last link\nin the chain is `Sequence` or `Sequential`. Thus, a \nterminated tuple type has a length that is known\nstatically. For an unterminated tuple type only a lower\nbound on its length is known statically.\n\nHere, `point` is an unterminated tuple:\n\n    String[] labels = ... ;\n    [Float,Float,String*] point = [0.0, 0.0, *labels];\n    Float x = point[0];\n    Float y = point[1];\n    String? firstLabel = point[2];\n    String[] allLabels = point[2...];\n\n"],"by":["Gavin"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"end"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$nm":"Rest"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"First"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"Tuple"},"lessThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is less than its element.\nThis is useful in conjunction with methods that receive\na predicate function."]},"$nm":"lessThan"},"identityHash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["identical"],"doc":["Return the system-defined identity hash value of the \ngiven value. This hash value is consistent with \nidentity equality."]},"$nm":"identityHash"},"uncurry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"prm","$pt":"f","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"uncurry"},"optional":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify that a module can be executed \neven if the annotated dependency is not available."]},"$nm":"optional"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["every"],"doc":["Determines if any one of the given boolean values \n(usually a comprehension) is `true`."]},"$nm":"any"},"Summable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Other"}],"$an":{"shared":[],"see":["String","Numeric"],"doc":["Abstraction of types which support a binary addition\noperator. For numeric types, this is just familiar \nnumeric addition. For strings, it is string \nconcatenation. In general, the addition operation \nshould be a binary associative operation."],"by":["Gavin"]},"$m":{"plus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of adding the given value to this value. \nThis operation should never perform any kind of \nmutation upon either the receiving value or the \nargument value."]},"$nm":"plus"}},"$nm":"Summable","$st":"Other"},"EmptyContainer":{"$mt":"ifc","$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["An empty container."]},"$alias":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"EmptyContainer"},"Set":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A collection of unique elements.\n\nA `Set` is a `Collection` of its elements.\n\nSets may be the subject of the binary union, \nintersection, exclusive union, and complement operators \n`|`, `&`, `^`, and `~`."]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements in \nthis `Set` that are not contained in the given\n`Set`."]},"$nm":"complement"},"subset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a subset of the given \n`Set`, that is, if the given set contains all of \nthe elements in this set."]},"$nm":"subset"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \nthat are present in both this `Set` and the given \n`Set`."]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \ncontained in either this `Set` or the given `Set`, \nbut no element contained in both sets."]},"$nm":"exclusiveUnion"},"superset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a superset of the \nspecified Set, that is, if this `Set` contains all \nof the elements in the specified `Set`."]},"$nm":"superset"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Set`s are considered equal if they have the \nsame size and if every element of the first set is\nalso an element of the second set, as determined\nby `contains()`."],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements of \nthis `Set` and all the elements of the given `Set`."]},"$nm":"union"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Set"},"Sequence":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Empty"],"doc":["A nonempty, immutable sequence of values. The type \n`Sequence<Element>`, may be abbreviated `[Element+]`.\n\nGiven a possibly-empty sequence of type `[Element*], \nthe `if (nonempty ...)` construct, or, alternatively,\nthe `assert (nonempty ...)` construct, may be used to \nnarrow to a nonempty sequence type:\n\n    [Integer*] nums = ... ;\n    if (nonempty nums) {\n        Integer first = nums.first;\n        Integer max = max(nums);\n        [Integer+] squares = nums.collect((Integer i) i**2));\n        [Integer+] sorted = nums.sort(byIncreasing((Integer i) i));\n    }\n\nOperations like `first`, `max()`, `collect()`, and \n`sort()`, which polymorphically produce a nonempty or \nnon-null output when given a nonempty input are called \n_emptiness-preserving_."],"by":["Gavin"]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements."],"actual":[]},"$nm":"sort"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the results of \napplying the given mapping to the elements of this\nsequence."],"actual":[]},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["The index of the last element of the sequence."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element of the sequence, that is, the\nelement with index `sequence.lastIndex`."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns `false`, since every `Sequence` contains at\nleast one element."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \nelement."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new nonempty\nsequence."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element of the sequence, that is, the\nelement with index `0`."],"actual":[]},"$nm":"first"}},"$nm":"Sequence"},"InitializationException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}],"$mt":"cls","$an":{"shared":[],"see":["late"],"doc":["Thrown when a problem was detected with value initialization.\n\nPossible problems include:\n\n* when a value could not be initialized due to recursive access during initialization, \n* an attempt to use a `late` value before it was initialized, \n* an attempt to assign to a `late` but non-`variable` value after it was initialized."]},"$nm":"InitializationException"},"StringBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}],"$mt":"cls","$an":{"shared":[],"native":[],"doc":["Since strings are immutable, this class is used for\nconstructing a string by incrementally appending \ncharacters to the empty string. This class is mutable \nbut threadsafe."]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append the characters in the given string."]},"$nm":"append"},"appendSpace":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a space character."]},"$nm":"appendSpace"},"delete":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"count"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Deletes the specified number of characters from the\ncurrent content, starting at the specified position.\nIf the position is beyond the end of the current content,\nnothing is deleted. If the number of characters to delete\nis greater than the available characters from the given\nposition, the content is truncated at the given position."]},"$nm":"delete"},"reset":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Remove all content and return to initial state."]},"$nm":"reset"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the characters in the given strings."]},"$nm":"appendAll"},"insert":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Character"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$nm":"content"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a String or Character at the specified position.\nIf the position is beyond the end of the current\nstring, the new content is simply appended to the\ncurrent content. If the position is a negative number,\nthe new content is inserted at index 0."]},"$nm":"insert"},"appendNewline":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a newline character."]},"$nm":"appendNewline"},"appendCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the given character."]},"$nm":"appendCharacter"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting string. If no characters have been\nappended, the empty string."],"actual":[]},"$nm":"string"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the size of the current content."]},"$nm":"size"}},"$nm":"StringBuilder"},"emptyOrSingleton":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Singleton","Empty"],"doc":["A `Singleton` if the given element is non-null, otherwise `Empty`."]},"$nm":"emptyOrSingleton"},"shared":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a type or member as shared. A `shared` \nmember is visible outside the block of code in which it\nis declared."]},"$nm":"shared"},"Binary":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Binary"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of numeric types that consist in\na sequence of bits, like `Integer`."],"by":["Stef"]},"$m":{"clear":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Returns a new number with the given bit set to 0.\nBits are indexed from right to left."]},"$nm":"clear"},"xor":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical exclusive OR operation."]},"$nm":"xor"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Retrieves a given bit from this bit sequence. Bits are indexed from\nright to left."]},"$nm":"get"},"leftLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a left logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"leftLogicalShift"},"set":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"bit"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit set to the given value.\nBits are indexed from right to left."]},"$nm":"set"},"or":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical inclusive OR operation."]},"$nm":"or"},"rightArithmeticShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right arithmetic shift. Sign is preserved. Padded with zeros."]},"$nm":"rightArithmeticShift"},"rightLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"rightLogicalShift"},"flip":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit flipped to its opposite value.\nBits are indexed from right to left."]},"$nm":"flip"},"and":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical AND operation."]},"$nm":"and"}},"$at":{"not":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The binary complement of this sequence of bits."]},"$nm":"not"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The number of bits (0 or 1) that this sequence of bits can hold."]},"$nm":"size"}},"$nm":"Binary","$st":"Other"},"commaList":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$nm":"commaList"},"Number":{"$mt":"ifc","$an":{"shared":[],"see":["Numeric"],"doc":["Abstraction of numbers. Numeric operations are provided\nby the subtype `Numeric`. This type defines operations\nwhich can be expressed without reference to the self\ntype `Other` of `Numeric`."],"by":["Gavin"]},"$at":{"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this number. Returns `1` if the number \nis positive, `-1` if it is negative, or `0` if it \nis zero."]},"$nm":"sign"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas an `Integer`"],"doc":["The number, represented as an `Integer`, after \ntruncation of any fractional part."]},"$nm":"integer"},"magnitude":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of the number."]},"$nm":"magnitude"},"wholePart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \nof the fractional part."]},"$nm":"wholePart"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is negative."]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is positive."]},"$nm":"positive"},"fractionalPart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \nof the integral part."]},"$nm":"fractionalPart"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas a `Float`"],"doc":["The number, represented as a `Float`."]},"$nm":"float"}},"$nm":"Number"},"LazyMap":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"entries"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A Map implementation that wraps an `Iterable` of \nentries. All operations, such as lookups, size, etc. \nare performed on the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"LazyMap"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazyMap"},"Map":{"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Entry","forKey","forItem","byItem","byKey"],"doc":["Represents a collection which maps _keys_ to _items_,\nwhere a key can map to at most one item. Each such \nmapping may be represented by an `Entry`.\n\nA `Map` is a `Collection` of its `Entry`s, and a \n`Correspondence` from keys to items.\n\nThe prescence of an entry in a map may be tested\nusing the `in` operator:\n\n    if (\"lang\"->\"en_AU\" in settings) { ... }\n\nThe entries of the map may be iterated using `for`:\n\n    for (key->item in settings) { ... }\n\nThe item for a key may be obtained using the item\noperator:\n\n    String lang = settings[\"lang\"] else \"en_US\";"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Map`s are considered equal iff they have the \nsame _entry sets_. The entry set of a `Map` is the\nset of `Entry`s belonging to the map. Therefore, the\nmaps are equal iff they have same set of `keys`, and \nfor every key in the key set, the maps have equal\nitems."],"actual":[]},"$nm":"equals"},"mapItems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"mapping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["Returns a `Map` with the same keys as this map. For\nevery key, the item is the result of applying the\ngiven transformation function."]},"$nm":"mapItems"}},"$at":{"values":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Collection"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns all the values stored in this `Map`. An \nelement can be stored under more than one key in \nthe map, and so it can be contained more than once \nin the resulting collection."]},"$nm":"values"},"inverse":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns a `Map` in which every key is an `Item` in \nthis map, and every value is the set of keys that \nstored the `Item` in this map."]},"$nm":"inverse"},"keys":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the set of keys contained in this `Map`."],"actual":[]},"$nm":"keys"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Map"},"Numeric":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Invertable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float","Comparable"],"doc":["Abstraction of numeric types supporting addition,\nsubtraction, multiplication, and division, including\n`Integer` and `Float`. Additionally, a numeric type \nis expected to define a total order via an \nimplementation of `Comparable`."],"by":["Gavin"]},"$m":{"minus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The difference between this number and the given \nnumber."]},"$nm":"minus"},"times":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The product of this number and the given number."]},"$nm":"times"},"divided":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Integral"],"doc":["The quotient obtained by dividing this number by \nthe given number. For integral numeric types, this \noperation results in a remainder."]},"$nm":"divided"}},"$nm":"Numeric","$st":"Other"},"throws":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"when"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a program element that throws an \nexception."]},"$nm":"throws"},"Closeable":{"$mt":"ifc","$an":{"shared":[],"doc":["Abstract supertype of types which may appear\nas the expression type of a resource expression\nin a `try` statement."]},"$m":{"open":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called before entry to a `try` block."]},"$nm":"open"},"close":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called after completion of a `try` block."]},"$nm":"close"}},"$nm":"Closeable"},"byDecreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byIncreasing"],"doc":["A comparator which orders elements in decreasing order \naccording to the `Comparable` returned by the given \n`comparable()` function."]},"$nm":"byDecreasing"},"formal":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member whose implementation must \nbe provided by subtypes."]},"$nm":"formal"},"default":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member whose implementation may be \nrefined by subtypes. Non-`default` declarations may not \nbe refined."]},"$nm":"default"},"String":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"native":[],"see":["string"],"doc":["A string of characters. Each character in the string is \na 32-bit Unicode character. The internal UTF-16 \nencoding is hidden from clients.\n\nA string is a `Category` of its `Character`s, and of\nits substrings:\n\n    'w' in greeting \n    \"hello\" in greeting\n\nStrings are summable:\n\n    String greeting = \"hello\" + \" \" + \"world\";\n\nThey are efficiently iterable:\n\n    for (char in \"hello world\") { ... }\n\nThey are `List`s of `Character`s:\n\n    value char = \"hello world\"[5];\n\nThey are ranged:\n\n    String who = \"hello world\"[6...];\n\nNote that since `string[index]` evaluates to the \noptional type `Character?`, it is often more convenient\nto write `string[index..index]`, which evaluates to a\n`String` containing a single character, or to the empty\nstring \"\" if `index` refers to a position outside the\nstring.\n\nThe `string()` function makes it possible to use \ncomprehensions to transform strings:\n\n    string(for (s in \"hello world\") if (s.letter) s.uppercased)\n\nSince a `String` has an underlying UTF-16 encoding, \ncertain operations are expensive, requiring iteration\nof the characters of the string. In particular, `size`\nrequires iteration of the whole string, and `get()`,\n`span()`, and `segment()` require iteration from the \nbeginning of the string to the given index."],"by":["Gavin"]},"$m":{"firstCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The first index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string."]},"$nm":"firstCharacterOccurrence"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the characters of this string beginning at \nthe given index, returning a string no longer than \nthe given length. If the portion of this string\nstarting at the given index is shorter than \nthe given length, return the portion of this string\nfrom the given index until the end of this string. \nOtherwise return a string of the given length. If \nthe start index is larger than the last index of the \nstring, return the empty string."],"actual":[]},"$nm":"segment"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string is longer than the given\nlength. This is a more efficient operation than\n`string.size>length`."]},"$nm":"longerThan"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if the given object is a `String` and, \nif so, if it occurs as a substring of this string,\nor if the object is a `Character` that occurs in\nthis string. That is to say, a string is considered \na `Category` of its substrings and of its \ncharacters."],"actual":[]},"$nm":"contains"},"repeat":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a string formed by repeating this string\nthe given number of times."]},"$nm":"repeat"},"firstOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The first index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string."]},"$nm":"firstOccurrence"},"terminal":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the last characters of the string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."]},"$nm":"terminal"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"initial":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the first characters of this string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length."]},"$nm":"initial"},"occurrences":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The character indexes at which the given substring\noccurs within this string. Occurrences do not \noverlap."]},"$nm":"occurrences"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"trimTrailingCharacters":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["This string, after discarding the given \ncharacters from the end of the string"]},"$nm":"trimTrailingCharacters"},"plus":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the concatenation of this string with the\ngiven string."],"actual":[]},"$nm":"plus"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["An iterator for the characters of the string."],"actual":[]},"$nm":"iterator"},"startsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if this string starts with the given \nsubstring."]},"$nm":"startsWith"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the character at the given index in the \nstring, or `null` if the index is past the end of\nstring. The first character in the string occurs at\nindex zero. The last character in the string occurs\nat index `string.size-1`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if the given object is a string, and if\nso, if this string has the same length, and the \nsame characters, in the same order, as the given \nstring."],"actual":[]},"$nm":"equals"},"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Compare this string with the given string \nlexicographically, according to the Unicode values\nof the characters."],"actual":[]},"$nm":"compare"},"trimLeadingCharacters":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["This string, after discarding the given \ncharacters from the beginning of the string"]},"$nm":"trimLeadingCharacters"},"trimCharacters":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["This string, after discarding the given \ncharacters from the beginning and end \nof the string"]},"$nm":"trimCharacters"},"lastCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The last index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string."]},"$nm":"lastCharacterOccurrence"},"join":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Join the given strings, using this string as a \nseparator."]},"$nm":"join"},"replace":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"replacement"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a string formed by replacing every \noccurrence in this string of the given substring\nwith the given replacement string, working from \nthe start of this string to the end."]},"$nm":"replace"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string is shorter than the given\nlength. This is a more efficient operation than\n`string.size>length`."]},"$nm":"shorterThan"},"lastOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The last index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string."]},"$nm":"lastOccurrence"},"split":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"separator"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"discardSeparators"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"groupSeparators"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Split the string into tokens, using the given\npredicate to determine which characters are \nseparator characters."]},"$nm":"split"},"endsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if this string ends with the given \nsubstring."]},"$nm":"endsWith"},"span":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the characters between the given indexes.\nIf the start index is the same as the end index,\nreturn a string with a single character.\nIf the start index is larger than the end index, \nreturn the characters in the reverse order from\nthe order in which they appear in this string.\nIf both the start index and the end index are \nlarger than the last index in the string, return \nthe empty string. Otherwise, if the last index is \nlarger than the last index in the sequence, return\nall characters from the start index to last \ncharacter of the string."],"actual":[]},"$nm":"span"}},"$at":{"normalized":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, after collapsing strings of \n[[whitespace|Character.whitespace]]\ninto single space characters and discarding whitespace \nfrom the beginning and end of the string."]},"$nm":"normalized"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with all characters in lowercase."]},"$nm":"lowercased"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"hash"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with all characters in uppercase."]},"$nm":"uppercased"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns this string."],"actual":[]},"$nm":"coalesced"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["longerThan","shorterThan"],"doc":["The length of the string (the number of characters\nit contains). In the case of the empty string, the\nstring has length zero. Note that this operation is\npotentially costly for long strings, since the\nunderlying representation of the characters uses a\nUTF-16 encoding."],"actual":[]},"$nm":"size"},"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the last character in the string, or\n`null` if the string has no characters. Note that \nthis operation is potentially costly for long \nstrings, since the underlying representation of the \ncharacters uses a UTF-16 encoding."],"actual":[]},"$nm":"lastIndex"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns the string itself."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string has no characters, that\nis, if it has zero `size`. This is a more efficient \noperation than `string.size==0`."],"actual":[]},"$nm":"empty"},"lines":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Split the string into lines of text."]},"$nm":"lines"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the string, without the first element."],"actual":[]},"$nm":"rest"},"trimmed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, after discarding \n[[whitespace|Character.whitespace]] from the \nbeginning and end of the string."]},"$nm":"trimmed"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with the characters in reverse order."],"actual":[]},"$nm":"reversed"},"characters":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The characters in this string."]},"$nm":"characters"}},"$nm":"String"},"identical":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$an":{"shared":[],"see":["identityHash"],"doc":["Determine if the arguments are identical. Equivalent to\n`x===y`. Only instances of `Identifiable` have \nwell-defined identity."]},"$nm":"identical"},"late":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to disable definite initialization analysis\nfor a reference."]},"$nm":"late"},"emptyIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$an":{"shared":[],"doc":["An iterator that returns no elements."]},"$nm":"emptyIterator"},"integerRangeByIterable":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"prm","$pt":"v","$nm":"range"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"native":[],"doc":["Provides an optimized implementation of `Range<Integer>.iterator`. \nThis is necessary because we need reified generics in order to write \nthe optimized version in pure Ceylon."]},"$nm":"integerRangeByIterable"},"parseFloat":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Float"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Float` value of the given string representation of \na decimal number or `null` if the string does not \nrepresent a decimal number.\n\nThe syntax accepted by this method is the same as the \nsyntax for a `Float` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`)."]},"$nm":"parseFloat"},"Anything":{"abstract":"1","of":[{"$pk":"ceylon.language","$nm":"Object"},{"$pk":"ceylon.language","$nm":"Null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The abstract supertype of all types. A value of type \n`Anything` may be a definite value of type `Object`, or \nit may be the `null` value. A method declared `void` is \nconsidered to have the return type `Anything`.\n\nNote that the type `Nothing`, representing the \nintersection of all types, is a subtype of all types."],"by":["Gavin"]},"$nm":"Anything"},"Exception":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"description"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"cause"}],"$mt":"cls","$an":{"shared":[],"native":[],"doc":["The supertype of all exceptions. A subclass represents\na more specific kind of problem, and may define \nadditional attributes which propagate information about\nproblems of that kind."],"by":["Gavin","Tom"]},"$m":{"printStackTrace":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print the stack trace to the standard error of\nthe virtual machine process."]},"$nm":"printStackTrace"}},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["description","cause"],"doc":["A message describing the problem. This default \nimplementation returns the description, if any, or \notherwise the message of the cause, if any."]},"$nm":"message"},"cause":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"attr","$an":{"shared":[],"doc":["The underlying cause of this exception."]},"$nm":"cause"},"description":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"attr","$an":{"doc":["A description of the problem."]},"$nm":"description"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"}},"$nm":"Exception"},"internalSort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"native":[]},"$nm":"internalSort"},"OverflowException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a mathematical operation caused a number to overflow from its bounds."]},"$nm":"OverflowException"},"parseInteger":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Integer` value of the given string representation \nof an integer, or `null` if the string does not represent \nan integer or if the mathematical integer it represents \nis too large in magnitude to be represented by an \n`Integer`.\n\nThe syntax accepted by this method is the same as the \nsyntax for an `Integer` literal in the Ceylon language \nexcept that it may optionally begin with a sign \ncharacter (`+` or `-`)."]},"$nm":"parseInteger"},"sum":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["product"],"doc":["Given a nonempty sequence of `Summable` values, return \nthe sum of the values."]},"$nm":"sum"},"infinity":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"doc":["An instance of `Float` representing positive infinity \n∞."]},"$nm":"infinity"},"smaller":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is smaller than the given value."],"by":["Gavin"]},"$nm":"smaller"},"flatten":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"Return","$ps":[[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"tuple"}]],"$mt":"prm","$pt":"f","$nm":"tupleFunction"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"flatten"},"Iterable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"see":["Collection"],"doc":["Abstract supertype of containers whose elements may be \niterated. An iterable container need not be finite, but\nits elements must at least be countable. There may not\nbe a well-defined iteration order, and so the order of\niterated elements may not be stable.\n\nThe type `Iterable<Element,Null>`, usually abbreviated\n`{Element*}` represents a possibly-empty iterable \ncontainer. The type `Iterable<Element,Nothing>`, \nusually abbreviated `{Element+}` represents a nonempty \niterable container.\n\nAn instance of `Iterable` may be constructed by \nsurrounding a value list in braces:\n\n    {String+} words = { \"hello\", \"world\" };\n\nAn instance of `Iterable` may be iterated using a `for`\nloop:\n\n    for (c in \"hello world\") { ... }\n\n`Iterable` and its subtypes define various operations\nthat return other iterable objects. Such operations \ncome in two flavors:\n\n- _Lazy_ operations return a \"view\" of the receiving\n  iterable object. If the underlying iterable object is\n  mutable, then changes to the underlying object will\n  be reflected in the resulting view. Lazy operations\n  are usually efficient, avoiding memory allocation or\n  iteration of the receiving iterable object.\n  \n- _Eager_ operations return an immutable object. If the\n  receiving iterable object is mutable, changes to this\n  object will not be reflected in the resulting \n  immutable object. Eager operations are often \n  expensive, involving memory allocation and iteration\n  of the receiving iterable object.\n\nLazy operations are preferred, because they can be \nefficiently chained. For example:\n\n    string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased)\n\nis much less expensive than:\n\n    string.select((Character c) => c.letter)\n          .collect((Character c) => c.uppercased)\n\nFurthermore, it is always easy to produce a new \nimmutable iterable object given the view produced by a\nlazy operation. For example:\n\n    [ string.filter((Character c) => c.letter)\n            .map((Character c) => c.uppercased)... ]\n\nLazy operations normally return an instance of \n`Iterable` or `Map`.\n\nHowever, there are certain scenarios where an eager \noperation is more useful, more convenient, or no more \nexpensive than a lazy operation, including:\n\n- sorting operations, which are eager by nature,\n- operations which preserve emptiness\/nonemptiness of\n  the receiving iterable object.\n\nEager operations normally return a sequence."],"by":["Gavin"]},"$m":{"trailedBy":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"trailing"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["An Iterable with the elements of this iterable followed by the given \nelement."]},"$nm":"trailedBy"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["An iterator for the elements belonging to this \ncontainer."]},"$nm":"iterator"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["byIncreasing","byDecreasing"],"doc":["A sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\n\nFor convenience, the functions `byIncreasing()` \nand `byDecreasing()` produce a suitable \ncomparison function:\n\n    \"Hello World!\".sort(byIncreasing((Character c) => c.lowercased))\n\nThis operation is eager by nature."]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return the number of elements in this `Iterable` \nthat satisfy the predicate function."]},"$nm":"count"},"select":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["filter"],"doc":["A sequence containing the elements of this \ncontainer that satisfy the given predicate. An \neager counterpart to `filter()`."]},"$nm":"select"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"throws":["Exception","if the given step size is nonpositive, \ni.e. `step<1`"],"doc":["Produce an `Iterable` containing every `step`th \nelement of this iterable object. If the step size \nis `1`, the `Iterable` contains the same elements \nas this iterable object. The step size must be \ngreater than zero. The expression\n\n    (0..10).by(3)\n\nresults in an iterable object with the elements\n`0`, `3`, `6`, and `9` in that order."]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["The result of applying the accumulating function to \neach element of this container in turn."]},"$nm":"fold"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if all elements satisfy the predicate\nfunction."]},"$nm":"every"},"defaultNullElements":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"comp":"i","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$nm":"Default"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Default","$mt":"prm","$pt":"v","$nm":"defaultValue"}]],"$mt":"mthd","$tp":[{"$nm":"Default"}],"$an":{"shared":[],"default":[],"doc":["Returns an Iterable that contains this `Iterable`'s elements but that\nwill return `defaultValue` instead of `null` for `null` elements of\nthat `Iterable`.\n\nCalling this method on an `Iterable` that cannot have `null` values\nwill not change the `Iterable` behavior. This means that calling this\nmethod on an `Iterable` which has been obtained using this method will\nnot change the default value as there is no `null` value anymore."]},"$nm":"defaultNullElements"},"taking":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the first `take`\nelements of this iterable object. If the specified \nnumber of elements is larger than the number of \nelements of this iterable object, the `Iterable` \ncontains the same elements as this iterable object."]},"$nm":"taking"},"chain":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["The elements of this iterable object, in their\noriginal order, followed by the elements of the \ngiven iterable object also in their original\norder."]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if at least one element satisfies the\npredicate function."]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["collect"],"doc":["An `Iterable` containing the results of applying\nthe given mapping to the elements of to this \ncontainer."]},"$nm":"map"},"leadBy":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"leading"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["An Iterable with the given inital element followed by the elements of \nthis iterable."]},"$nm":"leadBy"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The last element which satisfies the given\npredicate, if any, or `null` otherwise."]},"$nm":"findLast"},"skipping":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the elements of\nthis iterable object, after skipping the first \n`skip` elements. If this iterable object does not \ncontain more elements than the specified number of \nelements, the `Iterable` contains no elements."]},"$nm":"skipping"},"filter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["select"],"doc":["An `Iterable` containing the elements of this \ncontainer that satisfy the given predicate."]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The first element which satisfies the given \npredicate, if any, or `null` otherwise."]},"$nm":"find"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["map"],"doc":["A sequence containing the results of applying the\ngiven mapping to the elements of this container. An \neager counterpart to `map()`."]},"$nm":"collect"}},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The last element returned by the iterator, if any.\nIterables are potentially infinite, so calling this\nmight never return; also, this implementation will\niterate through all the elements, which might be\nvery time-consuming."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["All entries of form `index->element` where `index` \nis the position at which `element` occurs, for every\nnon-null element of this `Iterable`, ordered by\nincreasing `index`. For a null element at a given\nposition in the original `Iterable`, there is no \nentry with the corresponding index in the resulting \niterable object. The expression \n\n    { \"hello\", null, \"world\" }.indexed\n    \nresults in an iterable object with the entries\n`0->\"hello\"` and `2->\"world\"`."]},"$nm":"indexed"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A sequence containing the elements returned by the\niterator."]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this iterable is empty. If the number of items\nis very large only a certain amount of them might\nbe shown followed by \"...\"."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The non-null elements of this `Iterable`, in their\noriginal order. For null elements of the original \n`Iterable`, there is no entry in the resulting \niterable object."]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Determines if the iterable object is empty, that is\nto say, if the iterator returns no elements."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns an iterable object containing all but the \nfirst element of this container."]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The first element returned by the iterator, if any.\nThis should always produce the same value as\n`iterable.iterator().head`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[]},"$nm":"size"}},"$nm":"Iterable"},"LazyList":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of List that wraps an `Iterable` of\nelements. All operations on this List are performed on \nthe Iterable."],"by":["Enrique Zamudio"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"doc":["Returns a `List` with the elements of this `List` \nin reverse order. This operation will create copy \nthe elements to a new `List`, so changes to the \noriginal `Iterable` will no longer be reflected in \nthe new `List`."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"first"}},"$nm":"LazyList"},"className":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"obj"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Return the name of the concrete class of the given \nobject."]},"$nm":"className"},"List":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Sequence","Empty","Array"],"doc":["Represents a collection in which every element has a \nunique non-negative integer index.\n\nA `List` is a `Collection` of its elements, and a \n`Correspondence` from indices to elements.\n\nDirect access to a list element by index produces a\nvalue of optional type. The following idiom may be\nused instead of upfront bounds-checking, as long as \nthe list element type is a non-`null` type:\n\n    value char = \"hello world\"[index];\n    if (exists char) { \/*do something*\/ }\n    else { \/*out of bounds*\/ }\n\nTo iterate the indexes of a `List`, use the following\nidiom:\n\n    for (i->char in \"hello world\".indexed) { ... }\n\n"]},"$m":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"withTrailing":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"see":["trailedBy"],"doc":["Returns a new `List` that contains the specified\nelement appended to the end of the elements of this \n`List`."]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if the given index refers to an element\nof this sequence, that is, if\n`index<=sequence.lastIndex`."],"actual":[]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the element of this sequence with the given\nindex, or `null` if the given index is past the end\nof the sequence, that is, if\n`index>sequence.lastIndex`. The first element of\nthe sequence has index `0`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `List`s are considered equal iff they have the \nsame `size` and _entry sets_. The entry set of a \nlist `l` is the set of elements of `l.indexed`. \nThis definition is equivalent to the more intuitive \nnotion that two lists are equal iff they have the \nsame `size` and for every index either:\n\n- the lists both have the element `null`, or\n- the lists both have a non-null element, and the\n  two elements are equal."],"actual":[]},"$nm":"equals"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"see":["leadBy"],"doc":["Returns a new `List` that starts with the specified\nelement, followed by the elements of this `List`."]},"$nm":"withLeading"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["The index of the last element of the list, or\nnull if the list is empty."]},"$nm":"lastIndex"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the last element of this `List`, if any."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this list, returning a new list."]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the list, without the first element."],"actual":[]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the first element of this `List`, if any."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["lastIndex"],"doc":["The number of elements in this sequence, always\n`sequence.lastIndex+1`."],"actual":[]},"$nm":"size"}},"$nm":"List"},"Container":{"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"see":["Category"],"doc":["Abstract supertype of objects which may or may not\ncontain one of more other values, called *elements*,\nand provide an operation for accessing the first \nelement, if any. A container which may or may not be \nempty is a `Container<Element,Null>`. A container which \nis always empty is a `Container<Nothing,Null>`. A \ncontainer which is never empty is a \n`Container<Element,Nothing>`."],"by":["Gavin"]},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element. Should produce `null` if the\ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`."]},"$nm":"last"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the container is empty, that is, if\nit has no elements."]},"$nm":"empty"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element. Should produce `null` if the \ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`."]},"$nm":"first"}},"$nm":"Container"},"abstract":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a class as abstract. An `abstract` \nclass may not be directly instantiated. An `abstract`\nclass may have enumerated cases."]},"$nm":"abstract"},"zip":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"items"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"doc":["Given two sequences, form a new sequence consisting of\nall entries where, for any given index in the resulting\nsequence, the key of the entry is the element occurring \nat the same index in the first sequence, and the item \nis the element occurring at the same index in the second \nsequence. The length of the resulting sequence is the \nlength of the shorter of the two given sequences. \n\nThus:\n\n    zip(xs,ys)[i]==xs[i]->ys[i]\n\nfor every `0<=i<min({xs.size,ys.size})`."]},"$nm":"zip"},"equal":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is exactly equal to the given value."],"by":["Gavin"]},"$nm":"equal"},"finished":{"super":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"obj","$an":{"shared":[],"see":["Iterator"],"doc":["A value that indicates that an `Iterator`\nis exhausted and has no more values to \nreturn."]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"finished"},"Boolean":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"true"},{"$pk":"ceylon.language","$nm":"false"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A type capable of representing the values true and\nfalse of Boolean logic."],"by":["Gavin"]},"$nm":"Boolean"},"print":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["process.writeLine"],"doc":["Print a line to the standard output of the virtual \nmachine process, printing the given value's `string`, \nor `«null»` if the value is `null`.\n\nThis method is a shortcut for:\n\n    process.writeLine(line?.string else \"«null»\")\n\nand is intended mainly for debugging purposes."],"by":["Gavin"]},"$nm":"print"},"ChainedIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"first"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"second"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"variance":"out","$nm":"Other"}],"$an":{"doc":["An `Iterator` that returns the elements of two\n`Iterable`s, as if they were chained together."],"by":["Enrique Zamudio"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"more":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"more"},"iter":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"iter"}},"$nm":"ChainedIterator"},"Basic":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The default superclass when no superclass is explicitly\nspecified using `extends`. For the sake of convenience, \nthis class inherits a default definition of value\nequality from `Identifiable`."],"by":["Gavin"]},"$nm":"Basic"},"Category":{"$mt":"ifc","$an":{"shared":[],"see":["Container"],"doc":["Abstract supertype of objects that contain other \nvalues, called *elements*, where it is possible to \nefficiently determine if a given value is an element. \n`Category` does not satisfy `Container`, because it is \nconceptually possible to have a `Category` whose \nemptiness cannot be computed.\n\nThe `in` operator may be used to determine if a value\nbelongs to a `Category`:\n\n    if (\"hello\" in \"hello world\") { ... }\n    if (69 in 0..100) { ... }\n    if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n\nOrdinarily, `x==y` implies that `x in cat == y in cat`.\nBut this contract is not required since it is possible\nto form a meaningful `Category` using a different\nequivalence relation. For example, an `IdentitySet` is\na meaningful `Category`."],"by":["Gavin"]},"$m":{"containsAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if any one of the given values belongs \nto this `Category`"]},"$nm":"containsAny"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["containsEvery","containsAny"],"doc":["Determines if the given value belongs to this\n`Category`, that is, if it is an element of this\n`Category`.\n\nFor most `Category`s, if `x==y`, then \n`category.contains(x)` evaluates to the same\nvalue as `category.contains(y)`. However, it is\npossible to form a `Category` consistent with some \nother equivalence relation, for example `===`. \nTherefore implementations of `contains()` which do \nnot satisfy this relationship are tolerated."]},"$nm":"contains"},"containsEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if every one of the given values belongs\nto this `Category`."]},"$nm":"containsEvery"}},"$nm":"Category"},"see":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify API references to other related \nprogram elements."]},"$nm":"see"},"NegativeNumberException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a negative number is not allowed."]},"$nm":"NegativeNumberException"},"actual":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member of a type as refining a \nmember of a supertype."]},"$nm":"actual"},"Integer":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Integral"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Binary"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A 64-bit integer (or the closest approximation to a \n64-bit integer provided by the underlying platform)."]},"$at":{"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The UTF-32 character with this UCS code point."]},"$nm":"character"}},"$nm":"Integer"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"doc":["The first of the given elements (usually a comprehension),\nif any."]},"$nm":"first"}},"ceylon.language.metamodel":{"sequencedAnnotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"sequencedAnnotations"},"SequencedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur multiple times\nat a single program element."]},"$nm":"SequencedAnnotation"},"optionalAnnotation":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"optionalAnnotation"},"OptionalAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur at most once\nat a single program element."]},"$nm":"OptionalAnnotation"},"Type":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Instance"}],"$an":{"shared":[]},"$nm":"Type"},"Annotation":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["An annotation."]},"$nm":"Annotation"},"ConstrainedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"variance":"out","$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation. This interface encodes\nconstraints upon the annotation in its\ntype arguments."]},"$m":{"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language.metamodel","$nm":"Annotated"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$an":{"shared":[]},"$nm":"occurs"}},"$nm":"ConstrainedAnnotation"},"annotations":{"$t":{"$nm":"Values"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$nm":"Value"},{"$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"annotations"},"Annotated":{"$mt":"ifc","$an":{"shared":[],"doc":["A program element that can\nbe annotated."]},"$nm":"Annotated"}}};
exports.$$metamodel$$=$$metamodel$$;
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
initType(Callable, 'ceylon.language::Callable');
exports.Callable=Callable;
function $JsCallable(callable) {
return callable;
}
initExistingTypeProto($JsCallable, Function, 'ceylon.language::JsCallable', Callable);
function noop() { return null; }
//This is used for plain method references
function JsCallable(o,f) {
return (o !== null) ? function() { return f.apply(o, arguments); }
: noop;
}
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
exports.JsCallableList=JsCallableList;
exports.JsCallable=JsCallable;
function Comprehension(makeNextFunc, $$targs$$, compr) {
$init$Comprehension();
if (compr===undefined) {compr = new Comprehension.$$;}
Basic(compr);
compr.makeNextFunc = makeNextFunc;
compr.$$targs$$=$$targs$$;
return compr;
}
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
exports.Basic=Basic;
function $init$Basic(){
    if (Basic.$$===undefined){
        initTypeProto(Basic,'ceylon.language::Basic',Object$,$init$Identifiable());
    }
    return Basic;
}
exports.$init$Basic=$init$Basic;
$init$Basic();
function Iterable($$iterable){
    Container($$iterable);
}
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
                return $$iterable.count(function (e$1){
                    var $$iterable=this;
                    return true;
                });
            });
            $$iterable.contains=function (element$2){
                var $$iterable=this;
                return $$iterable.any(ifExists((opt$3=element$2,JsCallable(opt$3,opt$3!==null?opt$3.equals:null))));
            };
            defineAttr($$iterable,'first',function(){
                var $$iterable=this;
                return first($$iterable,{Value:$$iterable.$$targs$$.Element,Absent:$$iterable.$$targs$$.Absent});
            });
            defineAttr($$iterable,'last',function(){
                var $$iterable=this;
                var e$4=$$iterable.first;
                var setE$4=function(e$5){return e$4=e$5;};
                var it$6 = $$iterable.iterator();
                var x$7;while ((x$7=it$6.next())!==getFinished()){
                    e$4=x$7;
                }
                return e$4;
            });defineAttr($$iterable,'rest',function(){
                var $$iterable=this;
                return $$iterable.skipping((1));
            });
            defineAttr($$iterable,'sequence',function(){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$8=$$iterable.iterator();
                    var x$9=getFinished();
                    var next$x$9=function(){return x$9=it$8.next();}
                    next$x$9();
                    return function(){
                        if(x$9!==getFinished()){
                            var x$9$10=x$9;
                            var tmpvar$11=x$9$10;
                            next$x$9();
                            return tmpvar$11;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$iterable.$$targs$$.Element}).sequence;
            });
            $$iterable.$map=function (collecting$12,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$13=$$iterable.iterator();
                    var elem$14=getFinished();
                    var next$elem$14=function(){return elem$14=it$13.next();}
                    next$elem$14();
                    return function(){
                        if(elem$14!==getFinished()){
                            var elem$14$15=elem$14;
                            var tmpvar$16=collecting$12(elem$14$15);
                            next$elem$14();
                            return tmpvar$16;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$$mptypes.Result});
            };
            $$iterable.$filter=function (selecting$17){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$18=$$iterable.iterator();
                    var elem$19=getFinished();
                    var next$elem$19=function(){
                        while((elem$19=it$18.next())!==getFinished()){
                            if(selecting$17(elem$19)){
                                return elem$19;
                            }
                        }
                        return getFinished();
                    }
                    next$elem$19();
                    return function(){
                        if(elem$19!==getFinished()){
                            var elem$19$20=elem$19;
                            var tmpvar$21=elem$19$20;
                            next$elem$19();
                            return tmpvar$21;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$iterable.$$targs$$.Element});
            };
            $$iterable.fold=function fold(initial$22,accumulating$23,$$$mptypes){
                var $$iterable=this;
                var r$24=initial$22;
                var setR$24=function(r$25){return r$24=r$25;};
                var it$26 = $$iterable.iterator();
                var e$27;while ((e$27=it$26.next())!==getFinished()){
                    r$24=accumulating$23(r$24,e$27);
                }
                return r$24;
            };$$iterable.find=function find(selecting$28){
                var $$iterable=this;
                var it$29 = $$iterable.iterator();
                var e$30;while ((e$30=it$29.next())!==getFinished()){
                    if(selecting$28(e$30)){
                        return e$30;
                    }
                }
                return null;
            };$$iterable.findLast=function findLast(selecting$31){
                var $$iterable=this;
                var last$32=null;
                var setLast$32=function(last$33){return last$32=last$33;};
                var it$34 = $$iterable.iterator();
                var e$35;while ((e$35=it$34.next())!==getFinished()){
                    if(selecting$31(e$35)){
                        last$32=e$35;
                    }
                }
                return last$32;
            };$$iterable.$sort=function (comparing$36){
                var $$iterable=this;
                return internalSort(comparing$36,$$iterable,{Element:$$iterable.$$targs$$.Element});
            };
            $$iterable.collect=function (collecting$37,$$$mptypes){
                var $$iterable=this;
                return $$iterable.$map(collecting$37,{Result:$$$mptypes.Result}).sequence;
            };
            $$iterable.select=function (selecting$38){
                var $$iterable=this;
                return $$iterable.$filter(selecting$38).sequence;
            };
            $$iterable.any=function any(selecting$39){
                var $$iterable=this;
                var it$40 = $$iterable.iterator();
                var e$41;while ((e$41=it$40.next())!==getFinished()){
                    if(selecting$39(e$41)){
                        return true;
                    }
                }
                return false;
            };$$iterable.$every=function $every(selecting$42){
                var $$iterable=this;
                var it$43 = $$iterable.iterator();
                var e$44;while ((e$44=it$43.next())!==getFinished()){
                    if((!selecting$42(e$44))){
                        return false;
                    }
                }
                return true;
            };$$iterable.skipping=function skipping(skip$45){
                var $$iterable=this;
                if((skip$45.compare((0))!==getLarger())){
                    return $$iterable;
                }else {
                    var cntvar$46=false;
                    var brkvar$48=false;
                    var retvar$47=(function(){
                        function iterable$49($$targs$$){
                            var $$iterable$49=new iterable$49.$$;
                            $$iterable$49.$$targs$$=$$targs$$;
                            Iterable($$iterable$49);
                            add_type_arg($$iterable$49,'Absent',{t:Null});
                            return $$iterable$49;
                        }
                        function $init$iterable$49(){
                            if (iterable$49.$$===undefined){
                                initTypeProto(iterable$49,'ceylon.language::Iterable.skipping.iterable',Basic,$init$Iterable());
                            }
                            return iterable$49;
                        }
                        $init$iterable$49();
                        (function($$iterable$49){
                            $$iterable$49.iterator=function iterator(){
                                var $$iterable$49=this;
                                var iter$50=$$iterable.iterator();
                                var i$51=(0);
                                var setI$51=function(i$52){return i$51=i$52;};
                                while(((oldi$53=i$51,i$51=oldi$53.successor,oldi$53).compare(skip$45).equals(getSmaller())&&(!isOfType(iter$50.next(),{t:Finished})))){}
                                var oldi$53;
                                return iter$50;
                            };defineAttr($$iterable$49,'first',function(){
                                var $$iterable$49=this;
                                var first$54;
                                if(!isOfType((first$54=$$iterable$49.iterator().next()),{t:Finished})){
                                    return first$54;
                                }else {
                                    return null;
                                }
                            });defineAttr($$iterable$49,'last',function(){
                                var $$iterable$49=this;
                                return $$iterable.last;
                            });
                        })(iterable$49.$$.prototype);
                        var iterable$55=iterable$49({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$55=function(){
                            return iterable$55;
                        }
                        return getIterable$55();
                    }());if(retvar$47!==undefined){return retvar$47;}
                }
            };$$iterable.taking=function taking(take$56){
                var $$iterable=this;
                if((take$56.compare((0))!==getLarger())){
                    return getEmpty();
                }else {
                    var cntvar$57=false;
                    var brkvar$59=false;
                    var retvar$58=(function(){
                        function iterable$60($$targs$$){
                            var $$iterable$60=new iterable$60.$$;
                            $$iterable$60.$$targs$$=$$targs$$;
                            Iterable($$iterable$60);
                            add_type_arg($$iterable$60,'Absent',{t:Null});
                            return $$iterable$60;
                        }
                        function $init$iterable$60(){
                            if (iterable$60.$$===undefined){
                                initTypeProto(iterable$60,'ceylon.language::Iterable.taking.iterable',Basic,$init$Iterable());
                            }
                            return iterable$60;
                        }
                        $init$iterable$60();
                        (function($$iterable$60){
                            $$iterable$60.iterator=function iterator(){
                                var $$iterable$60=this;
                                var iter$61=$$iterable.iterator();
                                function iterator$62($$targs$$){
                                    var $$iterator$62=new iterator$62.$$;
                                    $$iterator$62.$$targs$$=$$targs$$;
                                    Iterator($$iterator$62);
                                    $$iterator$62.i$63_=(0);
                                    return $$iterator$62;
                                }
                                function $init$iterator$62(){
                                    if (iterator$62.$$===undefined){
                                        initTypeProto(iterator$62,'ceylon.language::Iterable.taking.iterable.iterator.iterator',Basic,$init$Iterator());
                                    }
                                    return iterator$62;
                                }
                                $init$iterator$62();
                                (function($$iterator$62){
                                    defineAttr($$iterator$62,'i$63',function(){return this.i$63_;},function(i$64){return this.i$63_=i$64;});
                                    $$iterator$62.next=function next(){
                                        var $$iterator$62=this;
                                        return (opt$65=(($$iterator$62.i$63=$$iterator$62.i$63.successor).compare(take$56).equals(getLarger())?getFinished():null),opt$65!==null?opt$65:iter$61.next());
                                        var opt$65;
                                    };
                                })(iterator$62.$$.prototype);
                                var iterator$66=iterator$62({Element:$$iterable.$$targs$$.Element});
                                var getIterator$66=function(){
                                    return iterator$66;
                                }
                                return getIterator$66();
                            };defineAttr($$iterable$60,'first',function(){
                                var $$iterable$60=this;
                                return $$iterable.first;
                            });
                            defineAttr($$iterable$60,'last',function(){
                                var $$iterable$60=this;
                                return $$iterable.last;
                            });
                        })(iterable$60.$$.prototype);
                        var iterable$67=iterable$60({Absent:{t:Null},Element:$$iterable.$$targs$$.Element});
                        var getIterable$67=function(){
                            return iterable$67;
                        }
                        return getIterable$67();
                    }());if(retvar$58!==undefined){return retvar$58;}
                }
            };$$iterable.by=function by(step$68){
                var $$iterable=this;
                //assert at Iterable.ceylon (319:8-320:25)
                if (!(step$68.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Iterable.ceylon (320:15-320:24)'); }
                if(step$68.equals((1))){
                    return $$iterable;
                }else {
                    var cntvar$69=false;
                    var brkvar$71=false;
                    var retvar$70=(function(){
                        function iterable$72($$targs$$){
                            var $$iterable$72=new iterable$72.$$;
                            $$iterable$72.$$targs$$=$$targs$$;
                            Iterable($$iterable$72);
                            return $$iterable$72;
                        }
                        function $init$iterable$72(){
                            if (iterable$72.$$===undefined){
                                initTypeProto(iterable$72,'ceylon.language::Iterable.by.iterable',Basic,$init$Iterable());
                            }
                            return iterable$72;
                        }
                        $init$iterable$72();
                        (function($$iterable$72){
                            $$iterable$72.iterator=function iterator(){
                                var $$iterable$72=this;
                                var iter$73=$$iterable.iterator();
                                function iterator$74($$targs$$){
                                    var $$iterator$74=new iterator$74.$$;
                                    $$iterator$74.$$targs$$=$$targs$$;
                                    Iterator($$iterator$74);
                                    return $$iterator$74;
                                }
                                function $init$iterator$74(){
                                    if (iterator$74.$$===undefined){
                                        initTypeProto(iterator$74,'ceylon.language::Iterable.by.iterable.iterator.iterator',Basic,$init$Iterator());
                                    }
                                    return iterator$74;
                                }
                                $init$iterator$74();
                                (function($$iterator$74){
                                    $$iterator$74.next=function next(){
                                        var $$iterator$74=this;
                                        var next$75=iter$73.next();
                                        var i$76=(0);
                                        var setI$76=function(i$77){return i$76=i$77;};
                                        while(((i$76=i$76.successor).compare(step$68).equals(getSmaller())&&(!isOfType(iter$73.next(),{t:Finished})))){}
                                        return next$75;
                                    };
                                })(iterator$74.$$.prototype);
                                var iterator$78=iterator$74({Element:$$iterable.$$targs$$.Element});
                                var getIterator$78=function(){
                                    return iterator$78;
                                }
                                return getIterator$78();
                            };
                        })(iterable$72.$$.prototype);
                        var iterable$79=iterable$72({Absent:$$iterable.$$targs$$.Absent,Element:$$iterable.$$targs$$.Element});
                        var getIterable$79=function(){
                            return iterable$79;
                        }
                        return getIterable$79();
                    }());if(retvar$70!==undefined){return retvar$70;}
                }
            };$$iterable.count=function count(selecting$80){
                var $$iterable=this;
                var count$81=(0);
                var setCount$81=function(count$82){return count$81=count$82;};
                var it$83 = $$iterable.iterator();
                var elem$84;while ((elem$84=it$83.next())!==getFinished()){
                    var elem$85;
                    if((elem$85=elem$84)!==null){
                        if(selecting$80(elem$85)){
                            (oldcount$86=count$81,count$81=oldcount$86.successor,oldcount$86);
                            var oldcount$86;
                        }
                    }
                }
                return count$81;
            };defineAttr($$iterable,'coalesced',function(){
                var $$iterable=this;
                return Comprehension(function(){
                    var e$89;
                    var it$87=$$iterable.iterator();
                    var e$88=getFinished();
                    var e$89;
                    var next$e$88=function(){
                        while((e$88=it$87.next())!==getFinished()){
                            if((e$89=e$88)!==null){
                                return e$88;
                            }
                        }
                        return getFinished();
                    }
                    next$e$88();
                    return function(){
                        if(e$88!==getFinished()){
                            var e$88$90=e$88;
                            var tmpvar$91=e$89;
                            next$e$88();
                            return tmpvar$91;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
            });
            defineAttr($$iterable,'indexed',function(){
                var $$iterable=this;
                function indexes$92($$targs$$){
                    var $$indexes$92=new indexes$92.$$;
                    $$indexes$92.$$targs$$=$$targs$$;
                    Iterable($$indexes$92);
                    add_type_arg($$indexes$92,'Absent',{t:Null});
                    add_type_arg($$indexes$92,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                    $$indexes$92.orig$93_=$$iterable;
                    return $$indexes$92;
                }
                function $init$indexes$92(){
                    if (indexes$92.$$===undefined){
                        initTypeProto(indexes$92,'ceylon.language::Iterable.indexed.indexes',Basic,$init$Iterable());
                    }
                    return indexes$92;
                }
                $init$indexes$92();
                (function($$indexes$92){
                    defineAttr($$indexes$92,'orig$93',function(){return this.orig$93_;});
                    $$indexes$92.iterator=function iterator(){
                        var $$indexes$92=this;
                        function iterator$94($$targs$$){
                            var $$iterator$94=new iterator$94.$$;
                            $$iterator$94.$$targs$$=$$targs$$;
                            Iterator($$iterator$94);
                            add_type_arg($$iterator$94,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                            $$iterator$94.iter$95_=$$indexes$92.orig$93.iterator();
                            $$iterator$94.i$96_=(0);
                            return $$iterator$94;
                        }
                        function $init$iterator$94(){
                            if (iterator$94.$$===undefined){
                                initTypeProto(iterator$94,'ceylon.language::Iterable.indexed.indexes.iterator.iterator',Basic,$init$Iterator());
                            }
                            return iterator$94;
                        }
                        $init$iterator$94();
                        (function($$iterator$94){
                            defineAttr($$iterator$94,'iter$95',function(){return this.iter$95_;});
                            defineAttr($$iterator$94,'i$96',function(){return this.i$96_;},function(i$97){return this.i$96_=i$97;});
                            $$iterator$94.next=function next(){
                                var $$iterator$94=this;
                                var next$98=$$iterator$94.iter$95.next();
                                var setNext$98=function(next$99){return next$98=next$99;};
                                while((!exists(next$98))){
                                    (oldi$100=$$iterator$94.i$96,$$iterator$94.i$96=oldi$100.successor,oldi$100);
                                    var oldi$100;
                                    next$98=$$iterator$94.iter$95.next();
                                }
                                var n$101;
                                var n$102;
                                if(!isOfType((n$101=next$98),{t:Finished})&&(n$102=n$101)!==null){
                                    return Entry((oldi$103=$$iterator$94.i$96,$$iterator$94.i$96=oldi$103.successor,oldi$103),n$102,{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
                                    var oldi$103;
                                }else {
                                    return getFinished();
                                }
                            };
                        })(iterator$94.$$.prototype);
                        var iterator$104=iterator$94({Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                        var getIterator$104=function(){
                            return iterator$104;
                        }
                        return getIterator$104();
                    };
                })(indexes$92.$$.prototype);
                var indexes$105=indexes$92({Absent:{t:Null},Element:{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}}});
                var getIndexes$105=function(){
                    return indexes$105;
                }
                return getIndexes$105();
            });$$iterable.leadBy=function leadBy(leading$106,$$$mptypes){
                var $$iterable=this;
                function lead$107($$targs$$){
                    var $$lead$107=new lead$107.$$;
                    $$lead$107.$$targs$$=$$targs$$;
                    Iterable($$lead$107);
                    return $$lead$107;
                }
                function $init$lead$107(){
                    if (lead$107.$$===undefined){
                        initTypeProto(lead$107,'ceylon.language::Iterable.leadBy.lead',Basic,$init$Iterable());
                    }
                    return lead$107;
                }
                $init$lead$107();
                (function($$lead$107){
                    $$lead$107.iterator=function (){
                        var $$lead$107=this;
                        return ChainedIterator(Singleton(leading$106,{Element:$$$mptypes.Other}),$$iterable,{Other:$$iterable.$$targs$$.Element,Element:$$$mptypes.Other});
                    };
                })(lead$107.$$.prototype);
                var lead$108=lead$107({Absent:{t:Nothing},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getLead$108=function(){
                    return lead$108;
                }
                return getLead$108();
            };$$iterable.trailedBy=function trailedBy(trailing$109,$$$mptypes){
                var $$iterable=this;
                function trailed$110($$targs$$){
                    var $$trailed$110=new trailed$110.$$;
                    $$trailed$110.$$targs$$=$$targs$$;
                    Iterable($$trailed$110);
                    return $$trailed$110;
                }
                function $init$trailed$110(){
                    if (trailed$110.$$===undefined){
                        initTypeProto(trailed$110,'ceylon.language::Iterable.trailedBy.trailed',Basic,$init$Iterable());
                    }
                    return trailed$110;
                }
                $init$trailed$110();
                (function($$trailed$110){
                    $$trailed$110.iterator=function (){
                        var $$trailed$110=this;
                        return ChainedIterator($$iterable,Singleton(trailing$109,{Element:$$$mptypes.Other}),{Other:$$$mptypes.Other,Element:$$iterable.$$targs$$.Element});
                    };
                })(trailed$110.$$.prototype);
                var trailed$111=trailed$110({Absent:{t:Nothing},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getTrailed$111=function(){
                    return trailed$111;
                }
                return getTrailed$111();
            };$$iterable.chain=function chain(other$112,$$$mptypes){
                var $$iterable=this;
                function chained$113($$targs$$){
                    var $$chained$113=new chained$113.$$;
                    $$chained$113.$$targs$$=$$targs$$;
                    Iterable($$chained$113);
                    add_type_arg($$chained$113,'Absent',{t:Null});
                    return $$chained$113;
                }
                function $init$chained$113(){
                    if (chained$113.$$===undefined){
                        initTypeProto(chained$113,'ceylon.language::Iterable.chain.chained',Basic,$init$Iterable());
                    }
                    return chained$113;
                }
                $init$chained$113();
                (function($$chained$113){
                    $$chained$113.iterator=function (){
                        var $$chained$113=this;
                        return ChainedIterator($$iterable,other$112,{Other:$$$mptypes.Other,Element:$$iterable.$$targs$$.Element});
                    };
                })(chained$113.$$.prototype);
                var chained$114=chained$113({Absent:{t:Null},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getChained$114=function(){
                    return chained$114;
                }
                return getChained$114();
            };$$iterable.defaultNullElements=function (defaultValue$115,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$116=$$iterable.iterator();
                    var elem$117=getFinished();
                    var next$elem$117=function(){return elem$117=it$116.next();}
                    next$elem$117();
                    return function(){
                        if(elem$117!==getFinished()){
                            var elem$117$118=elem$117;
                            var tmpvar$119=(opt$120=elem$117$118,opt$120!==null?opt$120:defaultValue$115);
                            next$elem$117();
                            return tmpvar$119;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'u', l:[$$$mptypes.Default,{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}]}});
            };
            defineAttr($$iterable,'string',function(){
                var $$iterable=this;
                return (opt$121=($$iterable.empty?String$("{}",2):null),opt$121!==null?opt$121:StringBuilder().appendAll([String$("{ ",2),limitedCommaList($$iterable.taking((31)),(30)).string,String$(" }",2)]).string);
            });
        })(Iterable.$$.prototype);
    }
    return Iterable;
}
exports.$init$Iterable=$init$Iterable;
$init$Iterable();
var opt$3,opt$120,opt$121;
var limitedCommaList=function (elements$122,maxLength$123){
    return commaList(elements$122.taking(maxLength$123)).plus((opt$124=(elements$122.size.compare(maxLength$123).equals(getLarger())?String$(", ...",5):null),opt$124!==null?opt$124:String$("",0)));
};
var opt$124;
function ifExists(predicate$125){
    return function(val$126){
        var val$127;
        if((val$127=val$126)!==null){
            return predicate$125(val$127);
        }else {
            return false;
        }
    }
};
function Sequence($$sequence){
    Sequential($$sequence);
    Iterable($$sequence);
    Cloneable($$sequence);
    add_type_arg($$sequence,'Clone',{t:Sequence,a:{Element:$$sequence.$$targs$$.Element}});
}
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
            $$sequence.$sort=function $sort(comparing$128){
                var $$sequence=this;
                var s$129=internalSort(comparing$128,$$sequence,{Element:$$sequence.$$targs$$.Element});
                //assert at Sequence.ceylon (63:8-63:27)
                var s$130;
                if (!(nonempty((s$130=s$129)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (63:15-63:26)'); }
                return s$130;
            };$$sequence.collect=function collect(collecting$131,$$$mptypes){
                var $$sequence=this;
                var s$132=$$sequence.$map(collecting$131,{Result:$$$mptypes.Result}).sequence;
                //assert at Sequence.ceylon (74:8-74:27)
                var s$133;
                if (!(nonempty((s$133=s$132)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (74:15-74:26)'); }
                return s$133;
            };defineAttr($$sequence,'clone',function(){
                var $$sequence=this;
                return $$sequence;
            });
        })(Sequence.$$.prototype);
    }
    return Sequence;
}
exports.$init$Sequence=$init$Sequence;
$init$Sequence();
function Sequential($$sequential){
    List($$sequential);
    Ranged($$sequential);
    add_type_arg($$sequential,'Index',{t:Integer});
    add_type_arg($$sequential,'Span',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
    Cloneable($$sequential);
    add_type_arg($$sequential,'Clone',{t:Sequential,a:{Element:$$sequential.$$targs$$.Element}});
}
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
                return (opt$134=($$sequential.empty?String$("[]",2):null),opt$134!==null?opt$134:StringBuilder().appendAll([String$("[",1),commaList($$sequential).string,String$("]",1)]).string);
            });
        })(Sequential.$$.prototype);
    }
    return Sequential;
}
exports.$init$Sequential=$init$Sequential;
$init$Sequential();
var opt$134;
function Empty($$empty){
    Sequential($$empty);
    Ranged($$empty);
    add_type_arg($$empty,'Index',{t:Integer});
    add_type_arg($$empty,'Span',{t:Empty});
    Cloneable($$empty);
    add_type_arg($$empty,'Clone',{t:Empty});
}
exports.Empty=Empty;
function $init$Empty(){
    if (Empty.$$===undefined){
        initTypeProto(Empty,'ceylon.language::Empty',$init$Sequential(),$init$Ranged(),$init$Cloneable());
        (function($$empty){
            $$empty.iterator=function (){
                var $$empty=this;
                return getEmptyIterator();
            };
            $$empty.get=function (index$135){
                var $$empty=this;
                return null;
            };
            $$empty.segment=function (from$136,length$137){
                var $$empty=this;
                return $$empty;
            };
            $$empty.span=function (from$138,to$139){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanTo=function (to$140){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanFrom=function (from$141){
                var $$empty=this;
                return $$empty;
            };
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
            $$empty.chain=function (other$142,$$$mptypes){
                var $$empty=this;
                return other$142;
            };
            $$empty.contains=function (element$143){
                var $$empty=this;
                return false;
            };
            $$empty.count=function (selecting$144){
                var $$empty=this;
                return (0);
            };
            $$empty.defines=function (index$145){
                var $$empty=this;
                return false;
            };
            $$empty.$map=function (collecting$146,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$filter=function (selecting$147){
                var $$empty=this;
                return $$empty;
            };
            $$empty.fold=function (initial$148,accumulating$149,$$$mptypes){
                var $$empty=this;
                return initial$148;
            };
            $$empty.find=function (selecting$150){
                var $$empty=this;
                return null;
            };
            $$empty.$sort=function (comparing$151){
                var $$empty=this;
                return $$empty;
            };
            $$empty.collect=function (collecting$152,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.select=function (selecting$153){
                var $$empty=this;
                return $$empty;
            };
            $$empty.any=function (selecting$154){
                var $$empty=this;
                return false;
            };
            $$empty.$every=function (selecting$155){
                var $$empty=this;
                return false;
            };
            $$empty.skipping=function (skip$156){
                var $$empty=this;
                return $$empty;
            };
            $$empty.taking=function (take$157){
                var $$empty=this;
                return $$empty;
            };
            $$empty.by=function (step$158){
                var $$empty=this;
                return $$empty;
            };
            $$empty.withLeading=function (element$159,$$$mptypes){
                var $$empty=this;
                return Tuple(element$159,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withTrailing=function (element$160,$$$mptypes){
                var $$empty=this;
                return Tuple(element$160,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.leadBy=function (leading$161,$$$mptypes){
                var $$empty=this;
                return Singleton(leading$161,{Element:$$$mptypes.Other});
            };
            $$empty.trailedBy=function (trailing$162,$$$mptypes){
                var $$empty=this;
                return Singleton(trailing$162,{Element:$$$mptypes.Other});
            };
        })(Empty.$$.prototype);
    }
    return Empty;
}
exports.$init$Empty=$init$Empty;
$init$Empty();
function empty$163(){
    var $$empty=new empty$163.$$;
    Object$($$empty);
    Empty($$empty);
    return $$empty;
}
function $init$empty$163(){
    if (empty$163.$$===undefined){
        initTypeProto(empty$163,'ceylon.language::empty',Object$,$init$Empty());
    }
    return empty$163;
}
exports.$init$empty$163=$init$empty$163;
$init$empty$163();
var empty$164=empty$163();
var getEmpty=function(){
    return empty$164;
}
exports.getEmpty=getEmpty;
function emptyIterator$165($$targs$$){
    var $$emptyIterator=new emptyIterator$165.$$;
    $$emptyIterator.$$targs$$=$$targs$$;
    Iterator($$emptyIterator);
    return $$emptyIterator;
}
function $init$emptyIterator$165(){
    if (emptyIterator$165.$$===undefined){
        initTypeProto(emptyIterator$165,'ceylon.language::emptyIterator',Basic,$init$Iterator());
    }
    return emptyIterator$165;
}
exports.$init$emptyIterator$165=$init$emptyIterator$165;
$init$emptyIterator$165();
(function($$emptyIterator){
    $$emptyIterator.next=function (){
        var $$emptyIterator=this;
        return getFinished();
    };
})(emptyIterator$165.$$.prototype);
var emptyIterator$166=emptyIterator$165({Element:{t:Nothing}});
var getEmptyIterator=function(){
    return emptyIterator$166;
}
exports.getEmptyIterator=getEmptyIterator;
function Correspondence($$correspondence){
}
exports.Correspondence=Correspondence;
function $init$Correspondence(){
    if (Correspondence.$$===undefined){
        initTypeProto(Correspondence,'ceylon.language::Correspondence');
        (function($$correspondence){
            $$correspondence.defines=function (key$167){
                var $$correspondence=this;
                return exists($$correspondence.get(key$167));
            };
            defineAttr($$correspondence,'keys',function(){
                var $$correspondence=this;
                return Keys($$correspondence,{Key:$$correspondence.$$targs$$.Key,Item:$$correspondence.$$targs$$.Item});
            });
            $$correspondence.definesEvery=function definesEvery(keys$168){
                var $$correspondence=this;
                var it$169 = keys$168.iterator();
                var key$170;while ((key$170=it$169.next())!==getFinished()){
                    if((!$$correspondence.defines(key$170))){
                        return false;
                    }
                }
                if (getFinished() === key$170){
                    return true;
                }
            };$$correspondence.definesAny=function definesAny(keys$171){
                var $$correspondence=this;
                var it$172 = keys$171.iterator();
                var key$173;while ((key$173=it$172.next())!==getFinished()){
                    if($$correspondence.defines(key$173)){
                        return true;
                    }
                }
                if (getFinished() === key$173){
                    return false;
                }
            };$$correspondence.items=function (keys$174){
                var $$correspondence=this;
                return Comprehension(function(){
                    var it$175=keys$174.iterator();
                    var key$176=getFinished();
                    var next$key$176=function(){return key$176=it$175.next();}
                    next$key$176();
                    return function(){
                        if(key$176!==getFinished()){
                            var key$176$177=key$176;
                            var tmpvar$178=$$correspondence.get(key$176$177);
                            next$key$176();
                            return tmpvar$178;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'u', l:[{t:Null},$$correspondence.$$targs$$.Item]}}).sequence;
            };
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
exports.Finished=Finished;
function $init$Finished(){
    if (Finished.$$===undefined){
        initTypeProto(Finished,'ceylon.language::Finished',Basic);
    }
    return Finished;
}
exports.$init$Finished=$init$Finished;
$init$Finished();
function finished$179(){
    var $$finished=new finished$179.$$;
    Finished($$finished);
    return $$finished;
}
function $init$finished$179(){
    if (finished$179.$$===undefined){
        initTypeProto(finished$179,'ceylon.language::finished',Finished);
    }
    return finished$179;
}
exports.$init$finished$179=$init$finished$179;
$init$finished$179();
(function($$finished){
    defineAttr($$finished,'string',function(){
        var $$finished=this;
        return String$("finished",8);
    });
})(finished$179.$$.prototype);
var finished$180=finished$179();
var getFinished=function(){
    return finished$180;
}
exports.getFinished=getFinished;
function Keys(correspondence$181, $$targs$$,$$keys){
    $init$Keys();
    if ($$keys===undefined)$$keys=new Keys.$$;
    set_type_args($$keys,$$targs$$);
    $$keys.correspondence$181=correspondence$181;
    Category($$keys);
    return $$keys;
}
function $init$Keys(){
    if (Keys.$$===undefined){
        initTypeProto(Keys,'ceylon.language::Keys',Basic,$init$Category());
        (function($$keys){
            $$keys.contains=function contains(key$182){
                var $$keys=this;
                var key$183;
                if(isOfType((key$183=key$182),$$keys.$$targs$$.Key)){
                    return $$keys.correspondence$181.defines(key$183);
                }else {
                    return false;
                }
            };
        })(Keys.$$.prototype);
    }
    return Keys;
}
exports.$init$Keys=$init$Keys;
$init$Keys();
function Collection($$collection){
    Iterable($$collection);
    add_type_arg($$collection,'Absent',{t:Null});
    Category($$collection);
    Cloneable($$collection);
    add_type_arg($$collection,'Clone',{t:Collection,a:{Element:$$collection.$$targs$$.Element}});
}
exports.Collection=Collection;
function $init$Collection(){
    if (Collection.$$===undefined){
        initTypeProto(Collection,'ceylon.language::Collection',$init$Iterable(),$init$Category(),$init$Cloneable());
        (function($$collection){
            defineAttr($$collection,'empty',function(){
                var $$collection=this;
                return $$collection.size.equals((0));
            });
            $$collection.contains=function contains(element$184){
                var $$collection=this;
                var it$185 = $$collection.iterator();
                var elem$186;while ((elem$186=it$185.next())!==getFinished()){
                    var elem$187;
                    if((elem$187=elem$186)!==null&&elem$187.equals(element$184)){
                        return true;
                    }
                }
                if (getFinished() === elem$186){
                    return false;
                }
            };defineAttr($$collection,'string',function(){
                var $$collection=this;
                return (opt$188=($$collection.empty?String$("{}",2):null),opt$188!==null?opt$188:StringBuilder().appendAll([String$("{ ",2),commaList($$collection).string,String$(" }",2)]).string);
            });
        })(Collection.$$.prototype);
    }
    return Collection;
}
exports.$init$Collection=$init$Collection;
$init$Collection();
var opt$188;
var commaList=function (elements$189){
    return String$(", ",2).join(Comprehension(function(){
        var it$190=elements$189.iterator();
        var element$191=getFinished();
        var next$element$191=function(){return element$191=it$190.next();}
        next$element$191();
        return function(){
            if(element$191!==getFinished()){
                var element$191$192=element$191;
                var tmpvar$193=(opt$194=(opt$195=element$191$192,opt$195!==null?opt$195.string:null),opt$194!==null?opt$194:String$("null",4));
                next$element$191();
                return tmpvar$193;
            }
            return getFinished();
        }
    },{Absent:{t:Anything},Element:{t:String$}}));
};
var opt$194,opt$195;
function Ranged($$ranged){
}
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
function Category($$category){
}
exports.Category=Category;
function $init$Category(){
    if (Category.$$===undefined){
        initTypeProto(Category,'ceylon.language::Category');
        (function($$category){
            $$category.containsEvery=function containsEvery(elements$196){
                var $$category=this;
                var it$197 = elements$196.iterator();
                var element$198;while ((element$198=it$197.next())!==getFinished()){
                    if((!$$category.contains(element$198))){
                        return false;
                    }
                }
                if (getFinished() === element$198){
                    return true;
                }
            };$$category.containsAny=function containsAny(elements$199){
                var $$category=this;
                var it$200 = elements$199.iterator();
                var element$201;while ((element$201=it$200.next())!==getFinished()){
                    if($$category.contains(element$201)){
                        return true;
                    }
                }
                if (getFinished() === element$201){
                    return false;
                }
            };
        })(Category.$$.prototype);
    }
    return Category;
}
exports.$init$Category=$init$Category;
$init$Category();
function Closeable($$closeable){
}
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
function Binary($$binary){
}
exports.Binary=Binary;
function $init$Binary(){
    if (Binary.$$===undefined){
        initTypeProto(Binary,'ceylon.language::Binary');
        (function($$binary){
            $$binary.clear=function clear(index$202){
                var $$binary=this;
                return $$binary.set(index$202,false);
            };
        })(Binary.$$.prototype);
    }
    return Binary;
}
exports.$init$Binary=$init$Binary;
$init$Binary();
function Container($$container){
    Category($$container);
}
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
function Cloneable($$cloneable){
}
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
function Iterator($$iterator){
}
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
function Tuple(first$203, rest$204, $$targs$$,$$tuple){
    $init$Tuple();
    if ($$tuple===undefined)$$tuple=new Tuple.$$;
    set_type_args($$tuple,$$targs$$);
    Object$($$tuple);
    Sequence($$tuple);
    Cloneable($$tuple);
    add_type_arg($$tuple,'Clone',{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}});
    $$tuple.first$205_=first$203;
    $$tuple.rest$206_=rest$204;
    return $$tuple;
}
exports.Tuple=Tuple;
function $init$Tuple(){
    if (Tuple.$$===undefined){
        initTypeProto(Tuple,'ceylon.language::Tuple',Object$,$init$Sequence(),$init$Cloneable());
        (function($$tuple){
            defineAttr($$tuple,'first',function(){return this.first$205_;});
            defineAttr($$tuple,'rest',function(){return this.rest$206_;});
            defineAttr($$tuple,'size',function(){
                var $$tuple=this;
                return (1).plus($$tuple.rest.size);
            });
            $$tuple.get=function get(index$207){
                var $$tuple=this;
                
                var case$208=index$207.compare((0));
                if (case$208===getSmaller()) {
                    return null;
                }else if (case$208===getEqual()) {
                    return $$tuple.first;
                }else if (case$208===getLarger()) {
                    return $$tuple.rest.get(index$207.minus((1)));
                }
            };defineAttr($$tuple,'lastIndex',function(){
                var $$tuple=this;
                var restLastIndex$209;
                if((restLastIndex$209=$$tuple.rest.lastIndex)!==null){
                    return restLastIndex$209.plus((1));
                }else {
                    return (0);
                }
            });defineAttr($$tuple,'last',function(){
                var $$tuple=this;
                var rest$210;
                if(nonempty((rest$210=$$tuple.rest))){
                    return rest$210.last;
                }else {
                    return $$tuple.first;
                }
            });defineAttr($$tuple,'reversed',function(){
                var $$tuple=this;
                return $$tuple.rest.reversed.withTrailing($$tuple.first,{Other:$$tuple.$$targs$$.First});
            });
            $$tuple.segment=function segment(from$211,length$212){
                var $$tuple=this;
                if(length$212.compare((0)).equals(getSmaller())){
                    return getEmpty();
                }
                var realFrom$213=(opt$214=(from$211.compare((0)).equals(getSmaller())?(0):null),opt$214!==null?opt$214:from$211);
                var opt$214;
                if(realFrom$213.equals((0))){
                    return (opt$215=(length$212.equals((1))?Tuple($$tuple.first,getEmpty(),{Rest:{t:Empty},First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.First}):null),opt$215!==null?opt$215:$$tuple.rest.segment((0),length$212.plus(realFrom$213).minus((1))).withLeading($$tuple.first,{Other:$$tuple.$$targs$$.First}));
                    var opt$215;
                }
                return $$tuple.rest.segment(realFrom$213.minus((1)),length$212);
            };$$tuple.span=function span(from$216,end$217){
                var $$tuple=this;
                if((from$216.compare((0)).equals(getSmaller())&&end$217.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var realFrom$218=(opt$219=(from$216.compare((0)).equals(getSmaller())?(0):null),opt$219!==null?opt$219:from$216);
                var opt$219;
                var realEnd$220=(opt$221=(end$217.compare((0)).equals(getSmaller())?(0):null),opt$221!==null?opt$221:end$217);
                var opt$221;
                return (opt$222=((realFrom$218.compare(realEnd$220)!==getLarger())?$$tuple.segment(from$216,realEnd$220.minus(realFrom$218).plus((1))):null),opt$222!==null?opt$222:$$tuple.segment(realEnd$220,realFrom$218.minus(realEnd$220).plus((1))).reversed.sequence);
                var opt$222;
            };$$tuple.spanTo=function (to$223){
                var $$tuple=this;
                return (opt$224=(to$223.compare((0)).equals(getSmaller())?getEmpty():null),opt$224!==null?opt$224:$$tuple.span((0),to$223));
            };
            $$tuple.spanFrom=function (from$225){
                var $$tuple=this;
                return $$tuple.span(from$225,$$tuple.size);
            };
            defineAttr($$tuple,'clone',function(){
                var $$tuple=this;
                return $$tuple;
            });
            $$tuple.iterator=function iterator(){
                var $$tuple=this;
                function iterator$226($$targs$$){
                    var $$iterator$226=new iterator$226.$$;
                    $$iterator$226.$$targs$$=$$targs$$;
                    Iterator($$iterator$226);
                    $$iterator$226.current$227_=$$tuple;
                    return $$iterator$226;
                }
                function $init$iterator$226(){
                    if (iterator$226.$$===undefined){
                        initTypeProto(iterator$226,'ceylon.language::Tuple.iterator.iterator',Basic,$init$Iterator());
                    }
                    return iterator$226;
                }
                $init$iterator$226();
                (function($$iterator$226){
                    defineAttr($$iterator$226,'current$227',function(){return this.current$227_;},function(current$228){return this.current$227_=current$228;});
                    $$iterator$226.next=function next(){
                        var $$iterator$226=this;
                        var c$229;
                        if(nonempty((c$229=$$iterator$226.current$227))){
                            $$iterator$226.current$227=c$229.rest;
                            return c$229.first;
                        }else {
                            return getFinished();
                        }
                    };
                })(iterator$226.$$.prototype);
                var iterator$230=iterator$226({Element:$$tuple.$$targs$$.Element});
                var getIterator$230=function(){
                    return iterator$230;
                }
                return getIterator$230();
            };$$tuple.contains=function contains(element$231){
                var $$tuple=this;
                var first$232;
                if((first$232=$$tuple.first)!==null&&first$232.equals(element$231)){
                    return true;
                }else {
                    return $$tuple.rest.contains(element$231);
                }
            };defineAttr($$tuple,'string',function(){
                var $$tuple=this;
                return attrGetter($$tuple.getT$all()['ceylon.language::Sequential'],'string').call(this);
            });
        })(Tuple.$$.prototype);
    }
    return Tuple;
}
exports.$init$Tuple=$init$Tuple;
$init$Tuple();
var opt$224;
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
exports.List=List;
function $init$List(){
    if (List.$$===undefined){
        initTypeProto(List,'ceylon.language::List',$init$Collection(),$init$Correspondence(),$init$Ranged(),$init$Cloneable());
        (function($$list){
            defineAttr($$list,'size',function(){
                var $$list=this;
                return (opt$233=$$list.lastIndex,opt$233!==null?opt$233:(-(1))).plus((1));
            });
            $$list.defines=function (index$234){
                var $$list=this;
                return (index$234.compare((opt$235=$$list.lastIndex,opt$235!==null?opt$235:(-(1))))!==getLarger());
            };
            $$list.iterator=function iterator(){
                var $$list=this;
                function listIterator$236($$targs$$){
                    var $$listIterator$236=new listIterator$236.$$;
                    $$listIterator$236.$$targs$$=$$targs$$;
                    Iterator($$listIterator$236);
                    $$listIterator$236.index$237_=(0);
                    return $$listIterator$236;
                }
                function $init$listIterator$236(){
                    if (listIterator$236.$$===undefined){
                        initTypeProto(listIterator$236,'ceylon.language::List.iterator.listIterator',Basic,$init$Iterator());
                    }
                    return listIterator$236;
                }
                $init$listIterator$236();
                (function($$listIterator$236){
                    defineAttr($$listIterator$236,'index$237',function(){return this.index$237_;},function(index$238){return this.index$237_=index$238;});
                    $$listIterator$236.next=function next(){
                        var $$listIterator$236=this;
                        if(($$listIterator$236.index$237.compare((opt$239=$$list.lastIndex,opt$239!==null?opt$239:(-(1))))!==getLarger())){
                            //assert at List.ceylon (61:20-61:65)
                            var elem$240;
                            if (!(isOfType((elem$240=$$list.get((oldindex$241=$$listIterator$236.index$237,$$listIterator$236.index$237=oldindex$241.successor,oldindex$241))),$$list.$$targs$$.Element))) { throw AssertionException('Assertion failed: \'is Element elem = outer.get(index++)\' at List.ceylon (61:27-61:64)'); }
                            var oldindex$241;
                            return elem$240;
                        }else {
                            return getFinished();
                        }
                        var opt$239;
                    };
                })(listIterator$236.$$.prototype);
                var listIterator$242=listIterator$236({Element:$$list.$$targs$$.Element});
                var getListIterator$242=function(){
                    return listIterator$242;
                }
                return getListIterator$242();
            };$$list.equals=function equals(that$243){
                var $$list=this;
                var that$244;
                if(isOfType((that$244=that$243),{t:List,a:{Element:{t:Anything}}})){
                    if(that$244.size.equals($$list.size)){
                        var it$245 = Range((0),$$list.size.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$246;while ((i$246=it$245.next())!==getFinished()){
                            var x$247=$$list.get(i$246);
                            var y$248=that$244.get(i$246);
                            var x$249;
                            if((x$249=x$247)!==null){
                                var y$250;
                                if((y$250=y$248)!==null){
                                    if((!x$249.equals(y$250))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$251;
                                if((y$251=y$248)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$246){
                            return true;
                        }
                    }
                }
                return false;
            };defineAttr($$list,'hash',function(){
                var $$list=this;
                var hash$252=(1);
                var setHash$252=function(hash$253){return hash$252=hash$253;};
                var it$254 = $$list.iterator();
                var elem$255;while ((elem$255=it$254.next())!==getFinished()){
                    (hash$252=hash$252.times((31)));
                    var elem$256;
                    if((elem$256=elem$255)!==null){
                        (hash$252=hash$252.plus(elem$256.hash));
                    }
                }
                return hash$252;
            });defineAttr($$list,'first',function(){
                var $$list=this;
                return $$list.get((0));
            });
            defineAttr($$list,'last',function(){
                var $$list=this;
                var i$257;
                if((i$257=$$list.lastIndex)!==null){
                    return $$list.get(i$257);
                }
                return null;
            });$$list.withLeading=function withLeading(element$258,$$$mptypes){
                var $$list=this;
                var sb$259=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                sb$259.append(element$258);
                if((!$$list.empty)){
                    sb$259.appendAll($$list.sequence);
                }
                //assert at List.ceylon (163:8-163:41)
                var seq$260;
                if (!(nonempty((seq$260=sb$259.sequence)))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (163:15-163:40)'); }
                return seq$260;
            };$$list.withTrailing=function withTrailing(element$261,$$$mptypes){
                var $$list=this;
                var sb$262=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                if((!$$list.empty)){
                    sb$262.appendAll($$list.sequence);
                }
                sb$262.append(element$261);
                //assert at List.ceylon (179:8-179:41)
                var seq$263;
                if (!(nonempty((seq$263=sb$262.sequence)))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (179:15-179:40)'); }
                return seq$263;
            };
        })(List.$$.prototype);
    }
    return List;
}
exports.$init$List=$init$List;
$init$List();
var opt$233,opt$235;
function ChainedIterator(first$264, second$265, $$targs$$,$$chainedIterator){
    $init$ChainedIterator();
    if ($$chainedIterator===undefined)$$chainedIterator=new ChainedIterator.$$;
    set_type_args($$chainedIterator,$$targs$$);
    $$chainedIterator.second$265=second$265;
    Iterator($$chainedIterator);
    $$chainedIterator.iter$266_=first$264.iterator();
    $$chainedIterator.more$267_=true;
    return $$chainedIterator;
}
function $init$ChainedIterator(){
    if (ChainedIterator.$$===undefined){
        initTypeProto(ChainedIterator,'ceylon.language::ChainedIterator',Basic,$init$Iterator());
        (function($$chainedIterator){
            defineAttr($$chainedIterator,'iter$266',function(){return this.iter$266_;},function(iter$268){return this.iter$266_=iter$268;});
            defineAttr($$chainedIterator,'more$267',function(){return this.more$267_;},function(more$269){return this.more$267_=more$269;});
            $$chainedIterator.next=function next(){
                var $$chainedIterator=this;
                var e$270=$$chainedIterator.iter$266.next();
                var setE$270=function(e$271){return e$270=e$271;};
                var f$272;
                if(isOfType((f$272=e$270),{t:Finished})){
                    if($$chainedIterator.more$267){
                        $$chainedIterator.iter$266=$$chainedIterator.second$265.iterator();
                        $$chainedIterator.more$267=false;
                        e$270=$$chainedIterator.iter$266.next();
                    }
                }
                return e$270;
            };
        })(ChainedIterator.$$.prototype);
    }
    return ChainedIterator;
}
exports.$init$ChainedIterator=$init$ChainedIterator;
$init$ChainedIterator();
function Entry(key$273, item$274, $$targs$$,$$entry){
    $init$Entry();
    if ($$entry===undefined)$$entry=new Entry.$$;
    set_type_args($$entry,$$targs$$);
    Object$($$entry);
    $$entry.key$275_=key$273;
    $$entry.item$276_=item$274;
    return $$entry;
}
exports.Entry=Entry;
function $init$Entry(){
    if (Entry.$$===undefined){
        initTypeProto(Entry,'ceylon.language::Entry',Object$);
        (function($$entry){
            defineAttr($$entry,'key',function(){return this.key$275_;});
            defineAttr($$entry,'item',function(){return this.item$276_;});
            $$entry.equals=function equals(that$277){
                var $$entry=this;
                var that$278;
                if(isOfType((that$278=that$277),{t:Entry,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    return ($$entry.key.equals(that$278.key)&&$$entry.item.equals(that$278.item));
                }else {
                    return false;
                }
            };defineAttr($$entry,'hash',function(){
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
function Integral($$integral){
    Numeric($$integral);
    Enumerable($$integral);
}
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
function Ordinal($$ordinal){
}
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
function Numeric($$numeric){
    Summable($$numeric);
    Invertable($$numeric);
}
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
function Invertable($$invertable){
}
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
function Scalar($$scalar){
    Numeric($$scalar);
    Comparable($$scalar);
    Number($$scalar);
}
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
function Summable($$summable){
}
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
function Exception(description$279, cause$280, $$exception){
    $init$Exception();
    if ($$exception===undefined)$$exception=new Exception.$$;
    if(description$279===undefined){description$279=null;}
    if(cause$280===undefined){cause$280=null;}
    $$exception.cause$281_=cause$280;
    $$exception.description$282_=description$279;
    return $$exception;
}
exports.Exception=Exception;
function $init$Exception(){
    if (Exception.$$===undefined){
        initTypeProto(Exception,'ceylon.language::Exception',Basic);
        (function($$exception){
            defineAttr($$exception,'cause',function(){return this.cause$281_;});
            defineAttr($$exception,'description$282',function(){return this.description$282_;});
            defineAttr($$exception,'message',function(){
                var $$exception=this;
                return (opt$283=(opt$284=$$exception.description$282,opt$284!==null?opt$284:(opt$285=$$exception.cause,opt$285!==null?opt$285.message:null)),opt$283!==null?opt$283:String$("",0));
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
var opt$283,opt$284,opt$285;
function NegativeNumberException($$negativeNumberException){
    $init$NegativeNumberException();
    if ($$negativeNumberException===undefined)$$negativeNumberException=new NegativeNumberException.$$;
    Exception(String$("Negative number",15),null,$$negativeNumberException);
    return $$negativeNumberException;
}
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
exports.OverflowException=OverflowException;
function $init$OverflowException(){
    if (OverflowException.$$===undefined){
        initTypeProto(OverflowException,'ceylon.language::OverflowException',Exception);
    }
    return OverflowException;
}
exports.$init$OverflowException=$init$OverflowException;
$init$OverflowException();
function InitializationException(description$286, $$initializationException){
    $init$InitializationException();
    if ($$initializationException===undefined)$$initializationException=new InitializationException.$$;
    Exception(description$286,null,$$initializationException);
    return $$initializationException;
}
exports.InitializationException=InitializationException;
function $init$InitializationException(){
    if (InitializationException.$$===undefined){
        initTypeProto(InitializationException,'ceylon.language::InitializationException',Exception);
    }
    return InitializationException;
}
exports.$init$InitializationException=$init$InitializationException;
$init$InitializationException();
function Range(first$287, last$288, $$targs$$,$$range){
    $init$Range();
    if ($$range===undefined)$$range=new Range.$$;
    set_type_args($$range,$$targs$$);
    Object$($$range);
    Sequence($$range);
    Category($$range);
    $$range.first$289_=first$287;
    $$range.last$290_=last$288;
    return $$range;
}
exports.Range=Range;
function $init$Range(){
    if (Range.$$===undefined){
        initTypeProto(Range,'ceylon.language::Range',Object$,$init$Sequence(),$init$Category());
        (function($$range){
            defineAttr($$range,'first',function(){return this.first$289_;});
            defineAttr($$range,'last',function(){return this.last$290_;});
            defineAttr($$range,'string',function(){
                var $$range=this;
                return $$range.first.string.plus(String$("..",2)).plus($$range.last.string);
            });
            defineAttr($$range,'decreasing',function(){
                var $$range=this;
                return $$range.last.compare($$range.first).equals(getSmaller());
            });
            $$range.next$291=function (x$292){
                var $$range=this;
                return (opt$293=($$range.decreasing?x$292.predecessor:null),opt$293!==null?opt$293:x$292.successor);
            };
            defineAttr($$range,'size',function(){
                var $$range=this;
                var last$294;
                var first$295;
                if(isOfType((last$294=$$range.last),{t:Enumerable,a:{Other:{t:Anything}}})&&isOfType((first$295=$$range.first),{t:Enumerable,a:{Other:{t:Anything}}})){
                    return last$294.integerValue.minus(first$295.integerValue).magnitude.plus((1));
                }else {
                    var size$296=(1);
                    var setSize$296=function(size$297){return size$296=size$297;};
                    var current$298=$$range.first;
                    var setCurrent$298=function(current$299){return current$298=current$299;};
                    while((!current$298.equals($$range.last))){
                        (oldsize$300=size$296,size$296=oldsize$300.successor,oldsize$300);
                        var oldsize$300;
                        current$298=$$range.next$291(current$298);
                    }
                    return size$296;
                }
            });defineAttr($$range,'lastIndex',function(){
                var $$range=this;
                return $$range.size.minus((1));
            });
            defineAttr($$range,'rest',function(){
                var $$range=this;
                if($$range.size.equals((1))){
                    return getEmpty();
                }
                var n$301=$$range.next$291($$range.first);
                return (opt$302=(n$301.equals($$range.last)?getEmpty():null),opt$302!==null?opt$302:Range(n$301,$$range.last,{Element:$$range.$$targs$$.Element}));
                var opt$302;
            });$$range.get=function get(n$303){
                var $$range=this;
                var index$304=(0);
                var setIndex$304=function(index$305){return index$304=index$305;};
                var x$306=$$range.first;
                var setX$306=function(x$307){return x$306=x$307;};
                while(index$304.compare(n$303).equals(getSmaller())){
                    if(x$306.equals($$range.last)){
                        return null;
                    }else {
                        (index$304=index$304.successor);
                        x$306=$$range.next$291(x$306);
                    }
                }
                return x$306;
            };$$range.iterator=function iterator(){
                var $$range=this;
                function RangeIterator$308($$rangeIterator$308){
                    $init$RangeIterator$308();
                    if ($$rangeIterator$308===undefined)$$rangeIterator$308=new RangeIterator$308.$$;
                    $$rangeIterator$308.$$targs$$={Element:$$range.$$targs$$.Element};
                    Iterator($$rangeIterator$308);
                    $$rangeIterator$308.current$309_=$$range.first;
                    return $$rangeIterator$308;
                }
                function $init$RangeIterator$308(){
                    if (RangeIterator$308.$$===undefined){
                        initTypeProto(RangeIterator$308,'ceylon.language::Range.iterator.RangeIterator',Basic,$init$Iterator());
                        (function($$rangeIterator$308){
                            defineAttr($$rangeIterator$308,'current$309',function(){return this.current$309_;},function(current$310){return this.current$309_=current$310;});
                            $$rangeIterator$308.next=function next(){
                                var $$rangeIterator$308=this;
                                var result$311=$$rangeIterator$308.current$309;
                                var curr$312;
                                if(!isOfType((curr$312=$$rangeIterator$308.current$309),{t:Finished})){
                                    if((opt$313=($$range.decreasing?(curr$312.compare($$range.last)!==getLarger()):null),opt$313!==null?opt$313:(curr$312.compare($$range.last)!==getSmaller()))){
                                        $$rangeIterator$308.current$309=getFinished();
                                    }else {
                                        $$rangeIterator$308.current$309=$$range.next$291(curr$312);
                                    }
                                    var opt$313;
                                }
                                return result$311;
                            };defineAttr($$rangeIterator$308,'string',function(){
                                var $$rangeIterator$308=this;
                                return String$("RangeIterator",13);
                            });
                        })(RangeIterator$308.$$.prototype);
                    }
                    return RangeIterator$308;
                }
                $init$RangeIterator$308();
                return RangeIterator$308();
            };$$range.by=function by(step$314){
                var $$range=this;
                //assert at Range.ceylon (112:8-113:25)
                if (!(step$314.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Range.ceylon (113:15-113:24)'); }
                if(step$314.equals((1))){
                    return $$range;
                }
                var first$315;
                var last$316;
                if(isOfType((first$315=$$range.first),{t:Integer})&&isOfType((last$316=$$range.last),{t:Integer})){
                    return integerRangeByIterable($$range,step$314,{Element:$$range.$$targs$$.Element});
                }
                return $$range.getT$all()['ceylon.language::Iterable'].$$.prototype.by.call(this,step$314);
            };$$range.contains=function contains(element$317){
                var $$range=this;
                var it$318 = $$range.iterator();
                var e$319;while ((e$319=it$318.next())!==getFinished()){
                    if(e$319.equals(element$317)){
                        return true;
                    }
                }
                if (getFinished() === e$319){
                    return false;
                }
            };$$range.count=function count(selecting$320){
                var $$range=this;
                var e$321=$$range.first;
                var setE$321=function(e$322){return e$321=e$322;};
                var c$323=(0);
                var setC$323=function(c$324){return c$323=c$324;};
                while($$range.includes(e$321)){
                    if(selecting$320(e$321)){
                        (oldc$325=c$323,c$323=oldc$325.successor,oldc$325);
                        var oldc$325;
                    }
                    e$321=$$range.next$291(e$321);
                }
                return c$323;
            };$$range.includes=function (x$326){
                var $$range=this;
                return (opt$327=($$range.decreasing?((x$326.compare($$range.first)!==getLarger())&&(x$326.compare($$range.last)!==getSmaller())):null),opt$327!==null?opt$327:((x$326.compare($$range.first)!==getSmaller())&&(x$326.compare($$range.last)!==getLarger())));
            };
            defineAttr($$range,'clone',function(){
                var $$range=this;
                return $$range;
            });
            $$range.segment=function segment(from$328,length$329){
                var $$range=this;
                if(((length$329.compare((0))!==getLarger())||from$328.compare($$range.lastIndex).equals(getLarger()))){
                    return getEmpty();
                }
                var x$330=$$range.first;
                var setX$330=function(x$331){return x$330=x$331;};
                var i$332=(0);
                var setI$332=function(i$333){return i$332=i$333;};
                while((oldi$334=i$332,i$332=oldi$334.successor,oldi$334).compare(from$328).equals(getSmaller())){
                    x$330=$$range.next$291(x$330);
                }
                var oldi$334;
                var y$335=x$330;
                var setY$335=function(y$336){return y$335=y$336;};
                var j$337=(1);
                var setJ$337=function(j$338){return j$337=j$338;};
                while(((oldj$339=j$337,j$337=oldj$339.successor,oldj$339).compare(length$329).equals(getSmaller())&&y$335.compare($$range.last).equals(getSmaller()))){
                    y$335=$$range.next$291(y$335);
                }
                var oldj$339;
                return Range(x$330,y$335,{Element:$$range.$$targs$$.Element});
            };$$range.span=function span(from$340,to$341){
                var $$range=this;
                var toIndex$342=to$341;
                var setToIndex$342=function(toIndex$343){return toIndex$342=toIndex$343;};
                var fromIndex$344=from$340;
                var setFromIndex$344=function(fromIndex$345){return fromIndex$344=fromIndex$345;};
                if(toIndex$342.compare((0)).equals(getSmaller())){
                    if(fromIndex$344.compare((0)).equals(getSmaller())){
                        return getEmpty();
                    }
                    toIndex$342=(0);
                }else {
                    if(toIndex$342.compare($$range.lastIndex).equals(getLarger())){
                        if(fromIndex$344.compare($$range.lastIndex).equals(getLarger())){
                            return getEmpty();
                        }
                        toIndex$342=$$range.lastIndex;
                    }
                }
                if(fromIndex$344.compare((0)).equals(getSmaller())){
                    fromIndex$344=(0);
                }else {
                    if(fromIndex$344.compare($$range.lastIndex).equals(getLarger())){
                        fromIndex$344=$$range.lastIndex;
                    }
                }
                var x$346=$$range.first;
                var setX$346=function(x$347){return x$346=x$347;};
                var i$348=(0);
                var setI$348=function(i$349){return i$348=i$349;};
                while((oldi$350=i$348,i$348=oldi$350.successor,oldi$350).compare(fromIndex$344).equals(getSmaller())){
                    x$346=$$range.next$291(x$346);
                }
                var oldi$350;
                var y$351=$$range.first;
                var setY$351=function(y$352){return y$351=y$352;};
                var j$353=(0);
                var setJ$353=function(j$354){return j$353=j$354;};
                while((oldj$355=j$353,j$353=oldj$355.successor,oldj$355).compare(toIndex$342).equals(getSmaller())){
                    y$351=$$range.next$291(y$351);
                }
                var oldj$355;
                return Range(x$346,y$351,{Element:$$range.$$targs$$.Element});
            };$$range.spanTo=function spanTo(to$356){
                var $$range=this;
                return (opt$357=(to$356.compare((0)).equals(getSmaller())?getEmpty():null),opt$357!==null?opt$357:$$range.span((0),to$356));
                var opt$357;
            };$$range.spanFrom=function spanFrom(from$358){
                var $$range=this;
                return $$range.span(from$358,$$range.size);
            };defineAttr($$range,'reversed',function(){
                var $$range=this;
                return Range($$range.last,$$range.first,{Element:$$range.$$targs$$.Element});
            });
            $$range.skipping=function skipping(skip$359){
                var $$range=this;
                var x$360=(0);
                var setX$360=function(x$361){return x$360=x$361;};
                var e$362=$$range.first;
                var setE$362=function(e$363){return e$362=e$363;};
                while((oldx$364=x$360,x$360=oldx$364.successor,oldx$364).compare(skip$359).equals(getSmaller())){
                    e$362=$$range.next$291(e$362);
                }
                var oldx$364;
                return (opt$365=($$range.includes(e$362)?Range(e$362,$$range.last,{Element:$$range.$$targs$$.Element}):null),opt$365!==null?opt$365:getEmpty());
                var opt$365;
            };$$range.taking=function taking(take$366){
                var $$range=this;
                if(take$366.equals((0))){
                    return getEmpty();
                }
                var x$367=(0);
                var setX$367=function(x$368){return x$367=x$368;};
                var e$369=$$range.first;
                var setE$369=function(e$370){return e$369=e$370;};
                while((x$367=x$367.successor).compare(take$366).equals(getSmaller())){
                    e$369=$$range.next$291(e$369);
                }
                return (opt$371=($$range.includes(e$369)?Range($$range.first,e$369,{Element:$$range.$$targs$$.Element}):null),opt$371!==null?opt$371:$$range);
                var opt$371;
            };defineAttr($$range,'coalesced',function(){
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
var opt$293,opt$327;
function Singleton(element$372, $$targs$$,$$singleton){
    $init$Singleton();
    if ($$singleton===undefined)$$singleton=new Singleton.$$;
    set_type_args($$singleton,$$targs$$);
    $$singleton.element$372=element$372;
    Object$($$singleton);
    Sequence($$singleton);
    return $$singleton;
}
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
                return $$singleton.element$372;
            });
            defineAttr($$singleton,'last',function(){
                var $$singleton=this;
                return $$singleton.element$372;
            });
            defineAttr($$singleton,'rest',function(){
                var $$singleton=this;
                return getEmpty();
            });
            $$singleton.get=function get(index$373){
                var $$singleton=this;
                if(index$373.equals((0))){
                    return $$singleton.element$372;
                }else {
                    return null;
                }
            };defineAttr($$singleton,'clone',function(){
                var $$singleton=this;
                return $$singleton;
            });
            $$singleton.iterator=function iterator(){
                var $$singleton=this;
                function SingletonIterator$374($$singletonIterator$374){
                    $init$SingletonIterator$374();
                    if ($$singletonIterator$374===undefined)$$singletonIterator$374=new SingletonIterator$374.$$;
                    $$singletonIterator$374.$$targs$$={Element:$$singleton.$$targs$$.Element};
                    Iterator($$singletonIterator$374);
                    $$singletonIterator$374.done$375_=false;
                    return $$singletonIterator$374;
                }
                function $init$SingletonIterator$374(){
                    if (SingletonIterator$374.$$===undefined){
                        initTypeProto(SingletonIterator$374,'ceylon.language::Singleton.iterator.SingletonIterator',Basic,$init$Iterator());
                        (function($$singletonIterator$374){
                            defineAttr($$singletonIterator$374,'done$375',function(){return this.done$375_;},function(done$376){return this.done$375_=done$376;});
                            $$singletonIterator$374.next=function next(){
                                var $$singletonIterator$374=this;
                                if($$singletonIterator$374.done$375){
                                    return getFinished();
                                }else {
                                    $$singletonIterator$374.done$375=true;
                                    return $$singleton.element$372;
                                }
                            };defineAttr($$singletonIterator$374,'string',function(){
                                var $$singletonIterator$374=this;
                                return String$("SingletonIterator",17);
                            });
                        })(SingletonIterator$374.$$.prototype);
                    }
                    return SingletonIterator$374;
                }
                $init$SingletonIterator$374();
                return SingletonIterator$374();
            };defineAttr($$singleton,'string',function(){
                var $$singleton=this;
                return StringBuilder().appendAll([String$("[",1),(opt$377=(opt$378=$$singleton.element$372,opt$378!==null?opt$378.string:null),opt$377!==null?opt$377:String$("null",4)).string,String$("]",1)]).string;
            });
            $$singleton.segment=function (from$379,length$380){
                var $$singleton=this;
                return (opt$381=(((from$379.compare((0))!==getLarger())&&from$379.plus(length$380).compare((0)).equals(getLarger()))?$$singleton:null),opt$381!==null?opt$381:getEmpty());
            };
            $$singleton.span=function (from$382,to$383){
                var $$singleton=this;
                return (opt$384=((((from$382.compare((0))!==getLarger())&&(to$383.compare((0))!==getSmaller()))||((from$382.compare((0))!==getSmaller())&&(to$383.compare((0))!==getLarger())))?$$singleton:null),opt$384!==null?opt$384:getEmpty());
            };
            $$singleton.spanTo=function (to$385){
                var $$singleton=this;
                return (opt$386=(to$385.compare((0)).equals(getSmaller())?getEmpty():null),opt$386!==null?opt$386:$$singleton);
            };
            $$singleton.spanFrom=function (from$387){
                var $$singleton=this;
                return (opt$388=(from$387.compare((0)).equals(getLarger())?getEmpty():null),opt$388!==null?opt$388:$$singleton);
            };
            defineAttr($$singleton,'reversed',function(){
                var $$singleton=this;
                return $$singleton;
            });
            $$singleton.equals=function equals(that$389){
                var $$singleton=this;
                var element$390;
                if((element$390=$$singleton.element$372)!==null){
                    var that$391;
                    if(isOfType((that$391=that$389),{t:List,a:{Element:{t:Anything}}})){
                        if(that$391.size.equals((1))){
                            var elem$392;
                            if((elem$392=that$391.first)!==null){
                                return elem$392.equals(element$390);
                            }
                        }
                    }
                    return false;
                }
                return false;
            };defineAttr($$singleton,'hash',function(){
                var $$singleton=this;
                return (31).plus((opt$393=(opt$394=$$singleton.element$372,opt$394!==null?opt$394.hash:null),opt$393!==null?opt$393:(0)));
            });
            $$singleton.contains=function contains(element$395){
                var $$singleton=this;
                var e$396;
                if((e$396=$$singleton.element$372)!==null){
                    return e$396.equals(element$395);
                }
                return false;
            };$$singleton.count=function (selecting$397){
                var $$singleton=this;
                return (opt$398=(selecting$397($$singleton.element$372)?(1):null),opt$398!==null?opt$398:(0));
            };
            $$singleton.$map=function (selecting$399,$$$mptypes){
                var $$singleton=this;
                return Tuple(selecting$399($$singleton.element$372),getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Result,Element:$$$mptypes.Result});
            };
            $$singleton.$filter=function (selecting$400){
                var $$singleton=this;
                return (opt$401=(selecting$400($$singleton.element$372)?$$singleton:null),opt$401!==null?opt$401:getEmpty());
            };
            $$singleton.fold=function (initial$402,accumulating$403,$$$mptypes){
                var $$singleton=this;
                return accumulating$403(initial$402,$$singleton.element$372);
            };
            $$singleton.find=function find(selecting$404){
                var $$singleton=this;
                if(selecting$404($$singleton.element$372)){
                    return $$singleton.element$372;
                }
                return null;
            };$$singleton.findLast=function (selecting$405){
                var $$singleton=this;
                return $$singleton.find(selecting$405);
            };
            $$singleton.$sort=function (comparing$406){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.any=function (selecting$407){
                var $$singleton=this;
                return selecting$407($$singleton.element$372);
            };
            $$singleton.$every=function (selecting$408){
                var $$singleton=this;
                return selecting$408($$singleton.element$372);
            };
            $$singleton.skipping=function (skip$409){
                var $$singleton=this;
                return (opt$410=(skip$409.compare((1)).equals(getSmaller())?$$singleton:null),opt$410!==null?opt$410:getEmpty());
            };
            $$singleton.taking=function (take$411){
                var $$singleton=this;
                return (opt$412=(take$411.compare((0)).equals(getLarger())?$$singleton:null),opt$412!==null?opt$412:getEmpty());
            };
            defineAttr($$singleton,'coalesced',function(){
                var $$singleton=this;
                var self$413;
                if(isOfType((self$413=$$singleton),{t:Singleton,a:{Element:{t:Object$}}})){
                    return self$413;
                }
                return getEmpty();
            });
        })(Singleton.$$.prototype);
    }
    return Singleton;
}
exports.$init$Singleton=$init$Singleton;
$init$Singleton();
var opt$377,opt$378,opt$381,opt$384,opt$386,opt$388,opt$393,opt$394,opt$398,opt$401,opt$410,opt$412;
function Set($$set){
    Collection($$set);
    Cloneable($$set);
    add_type_arg($$set,'Clone',{t:Set,a:{Element:$$set.$$targs$$.Element}});
}
exports.Set=Set;
function $init$Set(){
    if (Set.$$===undefined){
        initTypeProto(Set,'ceylon.language::Set',$init$Collection(),$init$Cloneable());
        (function($$set){
            $$set.superset=function superset(set$414){
                var $$set=this;
                var it$415 = set$414.iterator();
                var element$416;while ((element$416=it$415.next())!==getFinished()){
                    if((!$$set.contains(element$416))){
                        return false;
                    }
                }
                if (getFinished() === element$416){
                    return true;
                }
            };$$set.subset=function subset(set$417){
                var $$set=this;
                var it$418 = $$set.iterator();
                var element$419;while ((element$419=it$418.next())!==getFinished()){
                    if((!set$417.contains(element$419))){
                        return false;
                    }
                }
                if (getFinished() === element$419){
                    return true;
                }
            };$$set.equals=function equals(that$420){
                var $$set=this;
                var that$421;
                if(isOfType((that$421=that$420),{t:Set,a:{Element:{t:Object$}}})&&that$421.size.equals($$set.size)){
                    var it$422 = $$set.iterator();
                    var element$423;while ((element$423=it$422.next())!==getFinished()){
                        if((!that$421.contains(element$423))){
                            return false;
                        }
                    }
                    if (getFinished() === element$423){
                        return true;
                    }
                }
                return false;
            };defineAttr($$set,'hash',function(){
                var $$set=this;
                var hashCode$424=(1);
                var setHashCode$424=function(hashCode$425){return hashCode$424=hashCode$425;};
                var it$426 = $$set.iterator();
                var elem$427;while ((elem$427=it$426.next())!==getFinished()){
                    (hashCode$424=hashCode$424.times((31)));
                    (hashCode$424=hashCode$424.plus(elem$427.hash));
                }
                return hashCode$424;
            });
        })(Set.$$.prototype);
    }
    return Set;
}
exports.$init$Set=$init$Set;
$init$Set();
function AssertionException(message$428, $$assertionException){
    $init$AssertionException();
    if ($$assertionException===undefined)$$assertionException=new AssertionException.$$;
    Exception(message$428,undefined,$$assertionException);
    return $$assertionException;
}
exports.AssertionException=AssertionException;
function $init$AssertionException(){
    if (AssertionException.$$===undefined){
        initTypeProto(AssertionException,'ceylon.language::AssertionException',Exception);
    }
    return AssertionException;
}
exports.$init$AssertionException=$init$AssertionException;
$init$AssertionException();
function LazyList(elems$429, $$targs$$,$$lazyList){
    $init$LazyList();
    if ($$lazyList===undefined)$$lazyList=new LazyList.$$;
    set_type_args($$lazyList,$$targs$$);
    $$lazyList.elems$429=elems$429;
    List($$lazyList);
    return $$lazyList;
}
exports.LazyList=LazyList;
function $init$LazyList(){
    if (LazyList.$$===undefined){
        initTypeProto(LazyList,'ceylon.language::LazyList',Basic,$init$List());
        (function($$lazyList){
            defineAttr($$lazyList,'lastIndex',function(){
                var $$lazyList=this;
                var size$430=$$lazyList.elems$429.size;
                return (opt$431=(size$430.compare((0)).equals(getLarger())?size$430.minus((1)):null),opt$431!==null?opt$431:null);
                var opt$431;
            });$$lazyList.get=function get(index$432){
                var $$lazyList=this;
                if(index$432.equals((0))){
                    return $$lazyList.elems$429.first;
                }else {
                    return $$lazyList.elems$429.skipping(index$432).first;
                }
            };defineAttr($$lazyList,'rest',function(){
                var $$lazyList=this;
                return LazyList($$lazyList.elems$429.rest,{Element:$$lazyList.$$targs$$.Element});
            });
            $$lazyList.iterator=function (){
                var $$lazyList=this;
                return $$lazyList.elems$429.iterator();
            };
            defineAttr($$lazyList,'reversed',function(){
                var $$lazyList=this;
                return $$lazyList.elems$429.sequence.reversed;
            });
            defineAttr($$lazyList,'clone',function(){
                var $$lazyList=this;
                return $$lazyList;
            });
            $$lazyList.span=function span(from$433,to$434){
                var $$lazyList=this;
                if((to$434.compare((0)).equals(getSmaller())&&from$433.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var toIndex$435=largest(to$434,(0),{Element:{t:Integer}});
                var fromIndex$436=largest(from$433,(0),{Element:{t:Integer}});
                if((toIndex$435.compare(fromIndex$436)!==getSmaller())){
                    var els$437=(opt$438=(fromIndex$436.compare((0)).equals(getLarger())?$$lazyList.elems$429.skipping(fromIndex$436):null),opt$438!==null?opt$438:$$lazyList.elems$429);
                    var opt$438;
                    return LazyList(els$437.taking(toIndex$435.minus(fromIndex$436).plus((1))),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    var seq$439=(opt$440=(toIndex$435.compare((0)).equals(getLarger())?$$lazyList.elems$429.skipping(toIndex$435):null),opt$440!==null?opt$440:$$lazyList.elems$429);
                    var opt$440;
                    return seq$439.taking(fromIndex$436.minus(toIndex$435).plus((1))).sequence.reversed;
                }
            };$$lazyList.spanTo=function (to$441){
                var $$lazyList=this;
                return (opt$442=(to$441.compare((0)).equals(getSmaller())?getEmpty():null),opt$442!==null?opt$442:LazyList($$lazyList.elems$429.taking(to$441.plus((1))),{Element:$$lazyList.$$targs$$.Element}));
            };
            $$lazyList.spanFrom=function (from$443){
                var $$lazyList=this;
                return (opt$444=(from$443.compare((0)).equals(getLarger())?LazyList($$lazyList.elems$429.skipping(from$443),{Element:$$lazyList.$$targs$$.Element}):null),opt$444!==null?opt$444:$$lazyList);
            };
            $$lazyList.segment=function segment(from$445,length$446){
                var $$lazyList=this;
                if(length$446.compare((0)).equals(getLarger())){
                    var els$447=(opt$448=(from$445.compare((0)).equals(getLarger())?$$lazyList.elems$429.skipping(from$445):null),opt$448!==null?opt$448:$$lazyList.elems$429);
                    var opt$448;
                    return LazyList(els$447.taking(length$446),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    return getEmpty();
                }
            };$$lazyList.equals=function equals(that$449){
                var $$lazyList=this;
                var that$450;
                if(isOfType((that$450=that$449),{t:List,a:{Element:{t:Anything}}})){
                    var size$451=$$lazyList.elems$429.size;
                    if(that$450.size.equals(size$451)){
                        var it$452 = Range((0),size$451.minus((1)),{Element:{t:Integer}}).iterator();
                        var i$453;while ((i$453=it$452.next())!==getFinished()){
                            var x$454=$$lazyList.get(i$453);
                            var y$455=that$450.get(i$453);
                            var x$456;
                            if((x$456=x$454)!==null){
                                var y$457;
                                if((y$457=y$455)!==null){
                                    if((!x$456.equals(y$457))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$458;
                                if((y$458=y$455)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$453){
                            return true;
                        }
                    }
                }
                return false;
            };defineAttr($$lazyList,'hash',function(){
                var $$lazyList=this;
                var hash$459=(1);
                var setHash$459=function(hash$460){return hash$459=hash$460;};
                var it$461 = $$lazyList.elems$429.iterator();
                var elem$462;while ((elem$462=it$461.next())!==getFinished()){
                    (hash$459=hash$459.times((31)));
                    var elem$463;
                    if((elem$463=elem$462)!==null){
                        (hash$459=hash$459.plus(elem$463.hash));
                    }
                }
                return hash$459;
            });$$lazyList.findLast=function (selecting$464){
                var $$lazyList=this;
                return $$lazyList.elems$429.findLast(selecting$464);
            };
            defineAttr($$lazyList,'first',function(){
                var $$lazyList=this;
                return $$lazyList.elems$429.first;
            });
            defineAttr($$lazyList,'last',function(){
                var $$lazyList=this;
                return $$lazyList.elems$429.last;
            });
        })(LazyList.$$.prototype);
    }
    return LazyList;
}
exports.$init$LazyList=$init$LazyList;
$init$LazyList();
var opt$442,opt$444;
function Map($$map){
    Collection($$map);
    add_type_arg($$map,'Element',{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}});
    Correspondence($$map);
    add_type_arg($$map,'Key',{t:Object$});
    Cloneable($$map);
    add_type_arg($$map,'Clone',{t:Map,a:{Key:$$map.$$targs$$.Key,Item:$$map.$$targs$$.Item}});
}
exports.Map=Map;
function $init$Map(){
    if (Map.$$===undefined){
        initTypeProto(Map,'ceylon.language::Map',$init$Collection(),$init$Correspondence(),$init$Cloneable());
        (function($$map){
            $$map.equals=function equals(that$465){
                var $$map=this;
                var that$466;
                if(isOfType((that$466=that$465),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})&&that$466.size.equals($$map.size)){
                    var it$467 = $$map.iterator();
                    var entry$468;while ((entry$468=it$467.next())!==getFinished()){
                        var item$469;
                        if((item$469=that$466.get(entry$468.key))!==null&&item$469.equals(entry$468.item)){
                            continue;
                        }else {
                            return false;
                        }
                    }
                    if (getFinished() === entry$468){
                        return true;
                    }
                }else {
                    return false;
                }
            };defineAttr($$map,'hash',function(){
                var $$map=this;
                var hashCode$470=(1);
                var setHashCode$470=function(hashCode$471){return hashCode$470=hashCode$471;};
                var it$472 = $$map.iterator();
                var elem$473;while ((elem$473=it$472.next())!==getFinished()){
                    (hashCode$470=hashCode$470.times((31)));
                    (hashCode$470=hashCode$470.plus(elem$473.hash));
                }
                return hashCode$470;
            });defineAttr($$map,'keys',function(){
                var $$map=this;
                return LazySet(Comprehension(function(){
                    var it$474=$$map.iterator();
                    var k$475,v$476;
                    var next$v$476=function(){
                        var entry$477;
                        if((entry$477=it$474.next())!==getFinished()){
                            k$475=entry$477.key;
                            v$476=entry$477.item;
                            return entry$477;
                        }
                        v$476=undefined;
                        return getFinished();
                    }
                    next$v$476();
                    return function(){
                        if(v$476!==undefined){
                            var k$475$478=k$475;
                            var v$476$479=v$476;
                            var tmpvar$480=k$475$478;
                            next$v$476();
                            return tmpvar$480;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key});
            });
            defineAttr($$map,'values',function(){
                var $$map=this;
                return LazyList(Comprehension(function(){
                    var it$481=$$map.iterator();
                    var k$482,v$483;
                    var next$v$483=function(){
                        var entry$484;
                        if((entry$484=it$481.next())!==getFinished()){
                            k$482=entry$484.key;
                            v$483=entry$484.item;
                            return entry$484;
                        }
                        v$483=undefined;
                        return getFinished();
                    }
                    next$v$483();
                    return function(){
                        if(v$483!==undefined){
                            var k$482$485=k$482;
                            var v$483$486=v$483;
                            var tmpvar$487=v$483$486;
                            next$v$483();
                            return tmpvar$487;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$map.$$targs$$.Item}),{Element:$$map.$$targs$$.Item});
            });
            defineAttr($$map,'inverse',function(){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$488=$$map.iterator();
                    var key$489,item$490;
                    var next$item$490=function(){
                        var entry$491;
                        if((entry$491=it$488.next())!==getFinished()){
                            key$489=entry$491.key;
                            item$490=entry$491.item;
                            return entry$491;
                        }
                        item$490=undefined;
                        return getFinished();
                    }
                    next$item$490();
                    return function(){
                        if(item$490!==undefined){
                            var key$489$492=key$489;
                            var item$490$493=item$490;
                            var tmpvar$494=Entry(item$490$493,LazySet(Comprehension(function(){
                                var it$495=$$map.iterator();
                                var k$496,i$497;
                                var next$i$497=function(){
                                    var entry$498;
                                    while((entry$498=it$495.next())!==getFinished()){
                                        k$496=entry$498.key;
                                        i$497=entry$498.item;
                                        if(i$497.equals(item$490$493)){
                                            return entry$498;
                                        }
                                    }
                                    i$497=undefined;
                                    return getFinished();
                                }
                                next$i$497();
                                return function(){
                                    if(i$497!==undefined){
                                        var k$496$499=k$496;
                                        var i$497$500=i$497;
                                        var tmpvar$501=k$496$499;
                                        next$i$497();
                                        return tmpvar$501;
                                    }
                                    return getFinished();
                                }
                            },{Absent:{t:Anything},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
                            next$item$490();
                            return tmpvar$494;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{t:Entry,a:{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}}}}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
            });
            $$map.mapItems=function (mapping$502,$$$mptypes){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$503=$$map.iterator();
                    var key$504,item$505;
                    var next$item$505=function(){
                        var entry$506;
                        if((entry$506=it$503.next())!==getFinished()){
                            key$504=entry$506.key;
                            item$505=entry$506.item;
                            return entry$506;
                        }
                        item$505=undefined;
                        return getFinished();
                    }
                    next$item$505();
                    return function(){
                        if(item$505!==undefined){
                            var key$504$507=key$504;
                            var item$505$508=item$505;
                            var tmpvar$509=Entry(key$504$507,mapping$502(key$504$507,item$505$508),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
                            next$item$505();
                            return tmpvar$509;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{t:Entry,a:{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result}}}),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
            };
        })(Map.$$.prototype);
    }
    return Map;
}
exports.$init$Map=$init$Map;
$init$Map();
function LazySet(elems$510, $$targs$$,$$lazySet){
    $init$LazySet();
    if ($$lazySet===undefined)$$lazySet=new LazySet.$$;
    set_type_args($$lazySet,$$targs$$);
    $$lazySet.elems$510=elems$510;
    Set($$lazySet);
    return $$lazySet;
}
exports.LazySet=LazySet;
function $init$LazySet(){
    if (LazySet.$$===undefined){
        initTypeProto(LazySet,'ceylon.language::LazySet',Basic,$init$Set());
        (function($$lazySet){
            defineAttr($$lazySet,'clone',function(){
                var $$lazySet=this;
                return $$lazySet;
            });
            defineAttr($$lazySet,'size',function(){
                var $$lazySet=this;
                var c$511=(0);
                var setC$511=function(c$512){return c$511=c$512;};
                var sorted$513=$$lazySet.elems$510.$sort(byIncreasing(function (e$514){
                    var $$lazySet=this;
                    return e$514.hash;
                },{Value:{t:Integer},Element:$$lazySet.$$targs$$.Element}));
                var l$515;
                if((l$515=sorted$513.first)!==null){
                    c$511=(1);
                    var last$516=l$515;
                    var setLast$516=function(last$517){return last$516=last$517;};
                    var it$518 = sorted$513.rest.iterator();
                    var e$519;while ((e$519=it$518.next())!==getFinished()){
                        if((!e$519.equals(last$516))){
                            (oldc$520=c$511,c$511=oldc$520.successor,oldc$520);
                            var oldc$520;
                            last$516=e$519;
                        }
                    }
                }
                return c$511;
            });$$lazySet.iterator=function (){
                var $$lazySet=this;
                return $$lazySet.elems$510.iterator();
            };
            $$lazySet.union=function (set$521,$$$mptypes){
                var $$lazySet=this;
                return LazySet($$lazySet.elems$510.chain(set$521,{Other:$$$mptypes.Other}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };
            $$lazySet.intersection=function (set$522,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var e$525;
                    var it$523=set$522.iterator();
                    var e$524=getFinished();
                    var e$525;
                    var next$e$524=function(){
                        while((e$524=it$523.next())!==getFinished()){
                            if(isOfType((e$525=e$524),$$lazySet.$$targs$$.Element)&&$$lazySet.contains(e$525)){
                                return e$524;
                            }
                        }
                        return getFinished();
                    }
                    next$e$524();
                    return function(){
                        if(e$524!==getFinished()){
                            var e$524$526=e$524;
                            var tmpvar$527=e$525;
                            next$e$524();
                            return tmpvar$527;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}}),{Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}});
            };
            $$lazySet.exclusiveUnion=function exclusiveUnion(other$528,$$$mptypes){
                var $$lazySet=this;
                var hereNotThere$529=Comprehension(function(){
                    var it$530=$$lazySet.elems$510.iterator();
                    var e$531=getFinished();
                    var next$e$531=function(){
                        while((e$531=it$530.next())!==getFinished()){
                            if((!other$528.contains(e$531))){
                                return e$531;
                            }
                        }
                        return getFinished();
                    }
                    next$e$531();
                    return function(){
                        if(e$531!==getFinished()){
                            var e$531$532=e$531;
                            var tmpvar$533=e$531$532;
                            next$e$531();
                            return tmpvar$533;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$lazySet.$$targs$$.Element});
                var thereNotHere$534=Comprehension(function(){
                    var it$535=other$528.iterator();
                    var e$536=getFinished();
                    var next$e$536=function(){
                        while((e$536=it$535.next())!==getFinished()){
                            if((!$$lazySet.contains(e$536))){
                                return e$536;
                            }
                        }
                        return getFinished();
                    }
                    next$e$536();
                    return function(){
                        if(e$536!==getFinished()){
                            var e$536$537=e$536;
                            var tmpvar$538=e$536$537;
                            next$e$536();
                            return tmpvar$538;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$$mptypes.Other});
                return LazySet(hereNotThere$529.chain(thereNotHere$534,{Other:$$$mptypes.Other}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };$$lazySet.complement=function (set$539,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var it$540=$$lazySet.iterator();
                    var e$541=getFinished();
                    var next$e$541=function(){
                        while((e$541=it$540.next())!==getFinished()){
                            if((!set$539.contains(e$541))){
                                return e$541;
                            }
                        }
                        return getFinished();
                    }
                    next$e$541();
                    return function(){
                        if(e$541!==getFinished()){
                            var e$541$542=e$541;
                            var tmpvar$543=e$541$542;
                            next$e$541();
                            return tmpvar$543;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$lazySet.$$targs$$.Element}),{Element:$$lazySet.$$targs$$.Element});
            };
            $$lazySet.equals=function equals(that$544){
                var $$lazySet=this;
                var that$545;
                if(isOfType((that$545=that$544),{t:Set,a:{Element:{t:Object$}}})){
                    if(that$545.size.equals($$lazySet.size)){
                        var it$546 = $$lazySet.elems$510.iterator();
                        var element$547;while ((element$547=it$546.next())!==getFinished()){
                            if((!that$545.contains(element$547))){
                                return false;
                            }
                        }
                        if (getFinished() === element$547){
                            return true;
                        }
                    }
                }
                return false;
            };defineAttr($$lazySet,'hash',function(){
                var $$lazySet=this;
                var hashCode$548=(1);
                var setHashCode$548=function(hashCode$549){return hashCode$548=hashCode$549;};
                var it$550 = $$lazySet.elems$510.iterator();
                var elem$551;while ((elem$551=it$550.next())!==getFinished()){
                    (hashCode$548=hashCode$548.times((31)));
                    (hashCode$548=hashCode$548.plus(elem$551.hash));
                }
                return hashCode$548;
            });
        })(LazySet.$$.prototype);
    }
    return LazySet;
}
exports.$init$LazySet=$init$LazySet;
$init$LazySet();
function LazyMap(entries$552, $$targs$$,$$lazyMap){
    $init$LazyMap();
    if ($$lazyMap===undefined)$$lazyMap=new LazyMap.$$;
    set_type_args($$lazyMap,$$targs$$);
    $$lazyMap.entries$552=entries$552;
    Map($$lazyMap);
    return $$lazyMap;
}
exports.LazyMap=LazyMap;
function $init$LazyMap(){
    if (LazyMap.$$===undefined){
        initTypeProto(LazyMap,'ceylon.language::LazyMap',Basic,$init$Map());
        (function($$lazyMap){
            defineAttr($$lazyMap,'first',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$552.first;
            });
            defineAttr($$lazyMap,'last',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$552.last;
            });
            defineAttr($$lazyMap,'clone',function(){
                var $$lazyMap=this;
                return $$lazyMap;
            });
            defineAttr($$lazyMap,'size',function(){
                var $$lazyMap=this;
                return $$lazyMap.entries$552.size;
            });
            $$lazyMap.get=function (key$553){
                var $$lazyMap=this;
                return (opt$554=$$lazyMap.entries$552.find(function (e$555){
                    var $$lazyMap=this;
                    return e$555.key.equals(key$553);
                }),opt$554!==null?opt$554.item:null);
            };
            $$lazyMap.iterator=function (){
                var $$lazyMap=this;
                return $$lazyMap.entries$552.iterator();
            };
            $$lazyMap.equals=function equals(that$556){
                var $$lazyMap=this;
                var that$557;
                if(isOfType((that$557=that$556),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    if(that$557.size.equals($$lazyMap.size)){
                        var it$558 = $$lazyMap.iterator();
                        var entry$559;while ((entry$559=it$558.next())!==getFinished()){
                            var item$560;
                            if((item$560=that$557.get(entry$559.key))!==null){
                                if(item$560.equals(entry$559.item)){
                                    continue;
                                }
                            }
                            return false;
                        }
                        if (getFinished() === entry$559){
                            return true;
                        }
                    }
                }
                return false;
            };defineAttr($$lazyMap,'hash',function(){
                var $$lazyMap=this;
                var hashCode$561=(1);
                var setHashCode$561=function(hashCode$562){return hashCode$561=hashCode$562;};
                var it$563 = $$lazyMap.entries$552.iterator();
                var elem$564;while ((elem$564=it$563.next())!==getFinished()){
                    (hashCode$561=hashCode$561.times((31)));
                    (hashCode$561=hashCode$561.plus(elem$564.hash));
                }
                return hashCode$561;
            });
        })(LazyMap.$$.prototype);
    }
    return LazyMap;
}
exports.$init$LazyMap=$init$LazyMap;
$init$LazyMap();
var opt$554;
var byIncreasing=function (comparable$565,$$$mptypes){
    return function(x$566,y$567){{
        return comparable$565(x$566).compare(comparable$565(y$567));
    }
}
}
;
exports.byIncreasing=byIncreasing;
var coalesce=function (values$568,$$$mptypes){
    return values$568.coalesced;
};
exports.coalesce=coalesce;
var equalTo=function (val$569,$$$mptypes){
    return function(element$570){{
        return element$570.equals(val$569);
    }
}
}
;
exports.equalTo=equalTo;
var entries=function (elements$571,$$$mptypes){
    return elements$571.indexed;
};
exports.entries=entries;
function any(values$572){
    var it$573 = values$572.iterator();
    var val$574;while ((val$574=it$573.next())!==getFinished()){
        if(val$574){
            return true;
        }
    }
    return false;
}
exports.any=any;
var byKey=function (comparing$575,$$$mptypes){
    return function(x$576,y$577){{
        return comparing$575(x$576.key,y$577.key);
    }
}
}
;
exports.byKey=byKey;
function count(values$578){
    var count$579=(0);
    var setCount$579=function(count$580){return count$579=count$580;};
    var it$581 = values$578.iterator();
    var val$582;while ((val$582=it$581.next())!==getFinished()){
        if(val$582){
            (oldcount$583=count$579,count$579=oldcount$583.successor,oldcount$583);
            var oldcount$583;
        }
    }
    return count$579;
}
exports.count=count;
var byDecreasing=function (comparable$584,$$$mptypes){
    return function(x$585,y$586){{
        return comparable$584(y$586).compare(comparable$584(x$585));
    }
}
}
;
exports.byDecreasing=byDecreasing;
var byItem=function (comparing$587,$$$mptypes){
    return function(x$588,y$589){{
        return comparing$587(x$588.item,y$589.item);
    }
}
}
;
exports.byItem=byItem;
var curry=function (f$590,$$$mptypes){
    return function(first$591){{
        return flatten(function (args$592){
            return unflatten(f$590,{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return})(Tuple(first$591,args$592,{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}));
        },{Args:$$$mptypes.Rest,Return:$$$mptypes.Return});
    }
}
}
;
exports.curry=curry;
var uncurry=function (f$593,$$$mptypes){
return flatten(function (args$594){
    return unflatten(f$593(args$594.first),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return})(args$594.rest);
},{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return});
};
exports.uncurry=uncurry;
function emptyOrSingleton(element$595,$$$mptypes){
    var element$596;
    if((element$596=element$595)!==null){
        return Singleton(element$596,{Element:$$$mptypes.Element});
    }else {
        return getEmpty();
    }
}
exports.emptyOrSingleton=emptyOrSingleton;
function zip(keys$597,items$598,$$$mptypes){
    var iter$599=items$598.iterator();
    return Comprehension(function(){
        var item$602;
        var it$600=keys$597.iterator();
        var key$601=getFinished();
        var item$602;
        var next$key$601=function(){
            while((key$601=it$600.next())!==getFinished()){
                if(!isOfType((item$602=iter$599.next()),{t:Finished})){
                    return key$601;
                }
            }
            return getFinished();
        }
        next$key$601();
        return function(){
            if(key$601!==getFinished()){
                var key$601$603=key$601;
                var tmpvar$604=Entry(key$601$603,item$602,{Key:$$$mptypes.Key,Item:$$$mptypes.Item});
                next$key$601();
                return tmpvar$604;
            }
            return getFinished();
        }
    },{Absent:{t:Anything},Element:{t:Entry,a:{Key:$$$mptypes.Key,Item:$$$mptypes.Item}}}).sequence;
}
exports.zip=zip;
function max(values$605,$$$mptypes){
    var first$606=values$605.first;
    var first$607;
    if((first$607=first$606)!==null){
        var max$608=first$607;
        var setMax$608=function(max$609){return max$608=max$609;};
        var it$610 = values$605.rest.iterator();
        var val$611;while ((val$611=it$610.next())!==getFinished()){
            if(val$611.compare(max$608).equals(getLarger())){
                max$608=val$611;
            }
        }
        return max$608;
    }else {
        return first$606;
    }
}
exports.max=max;
var smallest=function (x$612,y$613,$$$mptypes){
    return (opt$614=(x$612.compare(y$613).equals(getSmaller())?x$612:null),opt$614!==null?opt$614:y$613);
};
exports.smallest=smallest;
var opt$614;
var forItem=function (resulting$615,$$$mptypes){
    return function(entry$616){{
        return resulting$615(entry$616.item);
    }
}
}
;
exports.forItem=forItem;
var lessThan=function (val$617,$$$mptypes){
    return function(element$618){{
        return element$618.compare(val$617).equals(getSmaller());
    }
}
}
;
exports.lessThan=lessThan;
var forKey=function (resulting$619,$$$mptypes){
    return function(entry$620){{
        return resulting$619(entry$620.key);
    }
}
}
;
exports.forKey=forKey;
var join=function (iterables$621,$$$mptypes){
    if(iterables$621===undefined){iterables$621=getEmpty();}
    return Comprehension(function(){
        var it$622=iterables$621.iterator();
        var it$623=getFinished();
        var next$it$623=function(){
            if((it$623=it$622.next())!==getFinished()){
                it$624=it$623.iterator();
                next$val$625();
                return it$623;
            }
            return getFinished();
        }
        var it$624;
        var val$625=getFinished();
        var next$val$625=function(){return val$625=it$624.next();}
        next$it$623();
        return function(){
            do{
                if(val$625!==getFinished()){
                    var val$625$626=val$625;
                    var tmpvar$627=val$625$626;
                    next$val$625();
                    return tmpvar$627;
                }
            }while(next$it$623()!==getFinished());
            return getFinished();
        }
    },{Absent:{t:Anything},Element:$$$mptypes.Element}).sequence;
};
exports.join=join;
function sum(values$628,$$$mptypes){
    var sum$629=values$628.first;
    var setSum$629=function(sum$630){return sum$629=sum$630;};
    var it$631 = values$628.rest.iterator();
    var val$632;while ((val$632=it$631.next())!==getFinished()){
        (sum$629=sum$629.plus(val$632));
    }
    return sum$629;
}
exports.sum=sum;
function print(line$633){
    getProcess().writeLine((opt$634=(opt$635=line$633,opt$635!==null?opt$635.string:null),opt$634!==null?opt$634:String$("«null»",6)));
    var opt$634,opt$635;
}
exports.print=print;
function product(values$636,$$$mptypes){
    var product$637=values$636.first;
    var setProduct$637=function(product$638){return product$637=product$638;};
    var it$639 = values$636.rest.iterator();
    var val$640;while ((val$640=it$639.next())!==getFinished()){
        (product$637=product$637.times(val$640));
    }
    return product$637;
}
exports.product=product;
function min(values$641,$$$mptypes){
    var first$642=values$641.first;
    var first$643;
    if((first$643=first$642)!==null){
        var min$644=first$643;
        var setMin$644=function(min$645){return min$644=min$645;};
        var it$646 = values$641.rest.iterator();
        var val$647;while ((val$647=it$646.next())!==getFinished()){
            if(val$647.compare(min$644).equals(getSmaller())){
                min$644=val$647;
            }
        }
        return min$644;
    }else {
        return first$642;
    }
}
exports.min=min;
var greaterThan=function (val$648,$$$mptypes){
    return function(element$649){{
        return element$649.compare(val$648).equals(getLarger());
    }
}
}
;
exports.greaterThan=greaterThan;
var getNothing=function(){
    throw Exception();
}
exports.getNothing=getNothing;
var largest=function (x$650,y$651,$$$mptypes){
    return (opt$652=(x$650.compare(y$651).equals(getLarger())?x$650:null),opt$652!==null?opt$652:y$651);
};
exports.largest=largest;
var opt$652;
function first(values$653,$$$mptypes){
    var first$654;
    var next$655;
    if(!isOfType((next$655=values$653.iterator().next()),{t:Finished})){
        first$654=next$655;
    }else {
        first$654=null;
    }
    //assert at first.ceylon (12:4-12:34)
    var first$656;
    if (!(isOfType((first$656=first$654),{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}))) { throw AssertionException('Assertion failed: \'is Absent|Value first\' at first.ceylon (12:11-12:33)'); }
    return first$656;
}
exports.first=first;
function every(values$657){
    var it$658 = values$657.iterator();
    var val$659;while ((val$659=it$658.next())!==getFinished()){
        if((!val$659)){
            return false;
        }
    }
    return true;
}
exports.every=every;
var times=function (x$660,y$661,$$$mptypes){
    return x$660.times(y$661);
};
exports.times=times;
var plus=function (x$662,y$663,$$$mptypes){
    return x$662.plus(y$663);
};
exports.plus=plus;
function combine(combination$664,elements$665,otherElements$666,$$$mptypes){
    function CombineIterable$667($$combineIterable$667){
        $init$CombineIterable$667();
        if ($$combineIterable$667===undefined)$$combineIterable$667=new CombineIterable$667.$$;
        $$combineIterable$667.$$targs$$={Absent:$$$mptypes.Absent,Element:$$$mptypes.Result};
        Iterable($$combineIterable$667);
        return $$combineIterable$667;
    }
    function $init$CombineIterable$667(){
        if (CombineIterable$667.$$===undefined){
            initTypeProto(CombineIterable$667,'ceylon.language::combine.CombineIterable',Basic,$init$Iterable());
            (function($$combineIterable$667){
                $$combineIterable$667.iterator=function iterator(){
                    var $$combineIterable$667=this;
                    function CombineIterator$668($$combineIterator$668){
                        $init$CombineIterator$668();
                        if ($$combineIterator$668===undefined)$$combineIterator$668=new CombineIterator$668.$$;
                        $$combineIterator$668.$$targs$$={Element:$$$mptypes.Result};
                        Iterator($$combineIterator$668);
                        $$combineIterator$668.iter$669_=elements$665.iterator();
                        $$combineIterator$668.otherIter$670_=otherElements$666.iterator();
                        return $$combineIterator$668;
                    }
                    function $init$CombineIterator$668(){
                        if (CombineIterator$668.$$===undefined){
                            initTypeProto(CombineIterator$668,'ceylon.language::combine.CombineIterable.iterator.CombineIterator',Basic,$init$Iterator());
                            (function($$combineIterator$668){
                                defineAttr($$combineIterator$668,'iter$669',function(){return this.iter$669_;});
                                defineAttr($$combineIterator$668,'otherIter$670',function(){return this.otherIter$670_;});
                                $$combineIterator$668.next=function next(){
                                    var $$combineIterator$668=this;
                                    var elem$671=$$combineIterator$668.iter$669.next();
                                    var otherElem$672=$$combineIterator$668.otherIter$670.next();
                                    var elem$673;
                                    var otherElem$674;
                                    if(!isOfType((elem$673=elem$671),{t:Finished})&&!isOfType((otherElem$674=otherElem$672),{t:Finished})){
                                        return combination$664(elem$673,otherElem$674);
                                    }else {
                                        return getFinished();
                                    }
                                };
                            })(CombineIterator$668.$$.prototype);
                        }
                        return CombineIterator$668;
                    }
                    $init$CombineIterator$668();
                    return CombineIterator$668();
                };
            })(CombineIterable$667.$$.prototype);
        }
        return CombineIterable$667;
    }
    $init$CombineIterable$667();
    return CombineIterable$667();
}
exports.combine=combine;
var sort=function (elements$675,$$$mptypes){
    return internalSort(byIncreasing(function (e$676){
        return e$676;
    },{Value:$$$mptypes.Element,Element:$$$mptypes.Element}),elements$675,{Element:$$$mptypes.Element});
};
exports.sort=sort;
var identical=function (x$677,y$678){
    return (x$677===y$678);
};
exports.identical=identical;
var compose=function (x$679,y$680,$$$mptypes){
    return flatten(function (args$681){
        return x$679(unflatten(y$680,{Args:$$$mptypes.Args,Return:$$$mptypes.Y})(args$681));
    },{Args:$$$mptypes.Args,Return:$$$mptypes.X});
};
exports.compose=compose;
var shuffle=function (f$682,$$$mptypes){
    return flatten(function (secondArgs$683){
        return flatten(function (firstArgs$684){
            return unflatten(unflatten(f$682,{Args:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}})(firstArgs$684),{Args:$$$mptypes.SecondArgs,Return:$$$mptypes.Result})(secondArgs$683);
        },{Args:$$$mptypes.FirstArgs,Return:$$$mptypes.Result});
    },{Args:$$$mptypes.SecondArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}});
};
exports.shuffle=shuffle;
//Ends compiled from Ceylon sources
function Number$(wat) {
return wat;
}
initType(Number$, 'ceylon.language::Number');
exports.Number=Number$;
function $init$Number$() {
if (Number$.$$===undefined) {
initType(Number$, 'ceylon.language::Number');
}
return Number$;
}
function JSNumber(value) { return Number(value); }
initExistingType(JSNumber, Number, 'ceylon.language::JSNumber');
var origNumToString = Number.prototype.toString;
inheritProto(JSNumber, Object$, Scalar, $init$Integral(), Exponentiable);
function Integer(value) { return Number(value); }
initTypeProto(Integer, 'ceylon.language::Integer', Object$, Scalar,
$init$Integral(), Exponentiable, Binary);
function Float(value) {
var that = new Number(value);
that.float$ = true;
return that;
}
initTypeProto(Float, 'ceylon.language::Float', Object$, Scalar, Exponentiable);
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
return this.size > 0 ? Range(0, this.size.predecessor, [{t:Integer}]) : getEmpty();
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
if (this.length === 0) {return Singleton(this, [{t:String$}, {t:String$}]); }
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
return ArraySequence(tokens, [{t:String$},{t:Null}]);
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
var $WS={0x9:true, 0xa:true, 0xb:true, 0xc:true, 0xd:true, 0x20:true, 0x85:true, 0xa0:true,
0x1680:true, 0x180e:true, 0x2028:true, 0x2029:true, 0x202f:true, 0x205f:true, 0x3000:true}
for (var i=0x2000; i<=0x200a; i++) { $WS[i]=true }
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
initTypeProto(StringBuilder, 'ceylon.language::StringBuilder', $init$Basic());
var StringBuilder$proto = StringBuilder.$$.prototype;
defineAttr(StringBuilder$proto, 'string', function(){ return String$(this.value); });
StringBuilder$proto.append = function(s) {
this.value = this.value + s;
return this;
}
StringBuilder$proto.appendAll = function(strings) {
if (strings === null || strings === undefined) { return this; }
for (var i = 0; i < strings.length; i++) {
var _s = strings[i];
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
if (obj === null) {
return type.t===Null || type.t===Anything;
}
if (obj === undefined || obj.getT$all === undefined) { return false; }
var typeName = type.t.$$.T$name;
if (obj.getT$all && typeName in obj.getT$all()) {
if (type.a && obj.$$targs$$) {
for (var i=0; i<type.a.length; i++) {
if (!extendsType(obj.$$targs$$[i], type.a[i])) {
return false;
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
if (t1.t === 'u' || t1.t === 'i') {
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
if (t2.t == 'u' || t2.t == 'i') {
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
return true;
}
}
return false;
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
var tn = obj.getT$name();
if (obj.$$targs$$) {
/*tn += '<';
for (var i=0; i < obj.$$targs$$.length; i++) {
if (i>0) { tn += ','; }
tn += _typename(obj.$$targs$$[i]);
}
tn += '>';*/
}
return String$(tn);
}
function identityHash(obj) {
return obj.BasicID;
}
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
function unflatten(ff, $$$mptypes) {
var ru = function ru(seq) {
if (seq===undefined || seq.size === 0) { return ff(); }
var a = [];
for (var i = 0; i < seq.size; i++) {
a[i] = seq.get(i);
}
a[i]=ru.$$targs$$;
return ff.apply(ru, a);
}
ru.$$targs$$=$$$mptypes;
return ru;
}
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
exports.integerRangeByIterable=integerRangeByIterable;
function Array$() {
var that = new Array$.$$;
List(that);
return that;
}
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
initTypeProto(ArraySequence, 'ceylon.language::ArraySequence', $init$Basic(), Sequence);
Array$proto.getT$name = function() {
return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$name;
}
Array$proto.getT$all = function() {
return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$all;
}
exports.EmptyArray=EmptyArray;
defineAttr(Array$proto, 'size', function(){ return this.length; });
Array$proto.setItem = function(idx,elem) {
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
};
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
initTypeProto(languageClass, "ceylon.language::language", $init$Basic());
var lang$proto=languageClass.$$.prototype;
defineAttr(lang$proto, 'version', function() {
return String$("0.5",3);
});
defineAttr(lang$proto, 'majorVersion', function(){ return 0; });
defineAttr(lang$proto, 'minorVersion', function(){ return 5; });
defineAttr(lang$proto, 'releaseVersion', function(){ return 0; });
defineAttr(lang$proto, 'versionName', function(){ return String$("Zaphod Beeblebrox",11); });
defineAttr(lang$proto, 'majorVersionBinary', function(){ return 4; });
defineAttr(lang$proto, 'minorVersionBinary', function(){ return 0; });
var languageString = String$("language", 7);
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
argv = ArraySequence(args, [{t:String$}]);
}
} else if (typeof window !== "undefined") {
// parse URL parameters
var parts = window.location.search.substr(1).replace('+', ' ').split('&');
if ((parts.length > 1) || ((parts.length > 0) && (parts[0].length > 0))) {
var argStrings = new Array(parts.length);
//can't do "for (i in parts)" anymore because of the added stuff to arrays
var i;
for (i=0; i<parts.length; i++) { argStrings[i] = String$(parts[i]); }
argv = ArraySequence(argStrings, [{t:String$}]);
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
