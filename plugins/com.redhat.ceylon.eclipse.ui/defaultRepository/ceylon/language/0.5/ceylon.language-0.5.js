(function(define) {
define(function(require, exports, module) {
//the Ceylon language module
$$metamodel$$={"$mod-name":"ceylon.language","$mod-version":"0.5","ceylon.language":{"Iterator":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Iterable"],"doc":["\"Produces elements of an `Iterable` object. Classes that \nimplement this interface should be immutable.\""],"by":["\"Gavin\""]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The next element, or `finished` if there are no \nmore elements to be iterated.\""]},"$nm":"next"}},"$nm":"Iterator"},"Callable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[],"doc":["\"A reference to a method or function.\""]},"$nm":"Callable"},"Castable":{"$mt":"ifc","$tp":[{"variance":"in","$nm":"Types"}],"$an":{"shared":[],"see":["Integer"],"doc":["\"Abstract supertype for types which can be automatically\nwidened to a different type in numeric operator \nexpressions. The type argument is a union of wider \ntypes to which the subtype can be cast. \n\nFor example, `Integer` satisfies `Castable<Integer|Float>`,\n so `Integer` can be promoted to `Float` in an \n expression like `-1\/2.0`.\""],"by":["\"Gavin\""]},"$m":{"castTo":{"$t":{"$nm":"CastValue"},"$mt":"mthd","$tp":[{"satisfies":[{"$nm":"Types"}],"$nm":"CastValue"}],"$an":{"shared":[],"formal":[],"doc":["\"Cast this object to the given type.\""]},"$nm":"castTo"}},"$nm":"Castable"},"copyArray":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"source"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"target"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["\"Efficiently copy the elements of one array to another \narray.\""]},"$nm":"copyArray"},"Array":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Cloneable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Ranged"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"abstract":[],"shared":[],"doc":["\"A fixed-size array of elements. An array may have zero\nsize (an empty array). Arrays are mutable. Any element\nof an array may be set to a new value.\n\nThis class is provided primarily to support interoperation \nwith Java, and for some performance-critical low-level \nprogramming tasks.\""]},"$m":{"setItem":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Replace the existing element at the specified index \nwith the given element. Does nothing if the specified \nindex is negative or larger than the index of the \nlast element in the array.\""]},"$nm":"setItem"}},"$at":{"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Reverse this array, returning a new array.\""],"actual":[]},"$nm":"reversed"}},"$nm":"Array"},"Singleton":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"A sequence with exactly one element.\""]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"a"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns the contained element, if the specified \nindex is `0`.\""],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `1` if this Singleton's element\nsatisfies the predicate, or `0` otherwise.\""],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"A `Singleton` can be equal to another `List` if \nthat `List` has only one element which is equal to \nthis `Singleton`'s element.\""],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns a `Singleton` if the given starting index \nis `0` and the given `length` is greater than `0`.\nOtherwise, returns an instance of `Empty`.\""],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `true` if the specified element is this \n`Singleton`'s element.\""],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"filter":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns a `Singleton` if the given starting index \nis `0`. Otherwise, returns an instance of `Empty`.\""],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `0`.\""],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns a `Singleton` with the same element.\""],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns the element contained in this `Singleton`.\""],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns the Singleton itself, since a Singleton\ncannot contain a null.\""],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"attr","$an":{"shared":[],"doc":["\"Return this singleton.\""],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `Empty`.\""],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns the element contained in this `Singleton`.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `1`.\""],"actual":[]},"$nm":"size"}},"$nm":"Singleton"},"byKey":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Key","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"}],"$an":{"shared":[],"see":["byItem"],"doc":["\"A comparator for `Entry`s which compares their keys \naccording to the given `comparing()` function.\""]},"$nm":"byKey"},"Comparable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Other"}],"$an":{"shared":[],"doc":["\"The general contract for values whose magnitude can be \ncompared. `Comparable` imposes a total ordering upon\ninstances of any type that satisfies the interface.\nIf a type `T` satisfies `Comparable<T>`, then instances\nof `T` may be compared using the comparison operators\n`<`, `>`, `<=`, >=`, and `<=>`.\n\nThe total order of a type must be consistent with the \ndefinition of equality for the type. That is, there\nare three mutually exclusive possibilities:\n\n- `x<y`,\n- `x>y`, or\n- `x==y`\""],"by":["\"Gavin\""]},"$m":{"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["equals"],"doc":["\"Compares this value with the given value. \nImplementations must respect the constraints that: \n\n- `x==y` if and only if `x<=>y == equal` \n   (consistency with `equals()`), \n- if `x>y` then `y<x` (symmetry), and \n- if `x>y` and `y>z` then `x>z` (transitivity).\""]},"$nm":"compare"}},"$nm":"Comparable","$st":"Other"},"Comparison":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"larger"},{"$pk":"ceylon.language","$nm":"smaller"},{"$pk":"ceylon.language","$nm":"equal"}],"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Comparable"],"doc":["\"The result of a comparison between two `Comparable` \nobjects.\""],"by":["\"Gavin\""]},"$m":{"largerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"largerThan"},"equal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"equal"},"asSmallAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asSmallAs"},"asLargeAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asLargeAs"},"smallerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"smallerThan"},"unequal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"unequal"}},"$nm":"Comparison"},"Empty":{"of":[{"$pk":"ceylon.language","$nm":"empty"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$an":{"shared":[],"see":["Sequence"],"doc":["\"A sequence with no elements. The type `Empty` may be\nabbreviated `[]`, and an instance is produced by the \nexpression `[]`. That is, in the following expression,\n`e` has type `[]` and refers to the value `[]`:\n\n    [] none = [];\n\n(Whether the syntax `[]` refers to the type or the \nvalue depends upon how it occurs grammatically.)\""]},"$m":{"sort":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `null` for any given index.\""],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns 0 for any given predicate.\""],"actual":[]},"$nm":"count"},"select":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"select"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns an `Empty` for any given segment.\""],"actual":[]},"$nm":"segment"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `false` for any given element.\""],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"withTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"defines"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns an `Empty` for any given span.\""],"actual":[]},"$nm":"spanTo"},"chain":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"doc":["\"Returns `other`.\""],"actual":[]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"withLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns an `Empty` for any given span.\""],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"collect":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"collect"},"span":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns an `Empty` for any given span.\""],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `null`.\""],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns an iterator that is already exhausted.\""],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `null`.\""],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"indexed"},"sequence":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns a string description of the empty sequence: \n`{}`.\""],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `true`.\""],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `null`.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns 0.\""],"actual":[]},"$nm":"size"}},"$nm":"Empty"},"Enumerable":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["\"Abstraction of ordinal types whose instances can be \nmapped to the integers or to a range of integers.\""]},"$at":{"integerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The corresponding integer. The implementation must\nsatisfy these constraints:\n\n    (x.successor).integerValue = x.integerValue+1\n    (x.predecessor).integerValue = x.integerValue-1\n\nfor every instance `x` of the enumerable type.\""]},"$nm":"integerValue"}},"$nm":"Enumerable","$st":"Other"},"empty":{"super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Empty"}],"$mt":"obj","$an":{"shared":[],"doc":["\"A sequence with no elements, abbreviated `[]`. The \nunique instance of the type `[]`.\""]},"$nm":"empty"},"combine":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"},{"$t":"OtherElement","$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"prm","$pt":"f","$nm":"combination"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"otherElements"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"},{"$nm":"Element"},{"$nm":"OtherElement"}],"$an":{"shared":[],"doc":["\"Applies a function to each element of two `Iterable`s\nand returns an `Iterable` with the results.\""],"by":["\"Gavin\"","\"Enrique Zamudio\"","\"Tako\""]},"$nm":"combine"},"compose":{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"X"},{"$nm":"Y"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[]},"$nm":"compose"},"false":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["\"A value representing falsity in Boolean logic.\""],"by":["\"Gavin\""]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"false"},"Sequential":{"of":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Tuple"],"doc":["\"A possibly-empty, immutable sequence of values. The \ntype `Sequential<Element>` may be abbreviated \n`[Element*]` or `Element[]`. \n\n`Sequential` has two enumerated subtypes:\n\n- `Empty`, abbreviated `[]`, represents an empty \n   sequence, and\n- `Sequence<Element>`, abbreviated `[Element+]` \n   represents a non-empty sequence, and has the very\n   important subclass `Tuple`.\""]},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"This sequence.\""],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"A string of form `\"[ x, y, z ]\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`.\""],"actual":[]},"$nm":"string"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The rest of the sequence, without the first \nelement.\""],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Reverse this sequence, returning a new sequence.\""],"actual":[]},"$nm":"reversed"}},"$nm":"Sequential"},"Finished":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"finished"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Iterator"],"doc":["\"The type of the value that indicates that \nan `Iterator` is exhausted and has no more \nvalues to return.\""]},"$nm":"Finished"},"coalesce":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Return a sequence containing the given values which are\nnot null. If there are no values which are not null,\nreturn an empty sequence.\""]},"$nm":"coalesce"},"plus":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["times","sum"],"doc":["\"Add the given `Summable` values.\""]},"$nm":"plus"},"Entry":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Key","$hdn":"1","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$hdn":"1","$mt":"prm","$pt":"v","$nm":"item"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["\"A pair containing a _key_ and an associated value called \nthe _item_. Used primarily to represent the elements of \na `Map`. The type `Entry<Key,Item>` may be abbreviated \n`Key->Item`. An instance of `Entry` may be constructed \nusing the `->` operator:\n\n    String->Person entry = person.name->person;\n\""],"by":["\"Gavin\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determines if this entry is equal to the given\nentry. Two entries are equal if they have the same\nkey and the same value.\""],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns a description of the entry in the form \n`key->item`.\""],"actual":[]},"$nm":"string"},"item":{"$t":{"$nm":"Item"},"$mt":"attr","$an":{"shared":[],"doc":["\"The value associated with the key.\""]},"$nm":"item"},"key":{"$t":{"$nm":"Key"},"$mt":"attr","$an":{"shared":[],"doc":["\"The key used to access the entry.\""]},"$nm":"key"}},"$nm":"Entry"},"Cloneable":{"of":[{"$nm":"Clone"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Clone"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"variance":"out","$nm":"Clone"}],"$an":{"shared":[],"doc":["\"Abstract supertype of objects whose value can be \ncloned.\""]},"$at":{"clone":{"$t":{"$nm":"Clone"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Obtain a clone of this object. For a mutable \nobject, this should return a copy of the object. \nFor an immutable object, it is acceptable to return\nthe object itself.\""]},"$nm":"clone"}},"$nm":"Cloneable","$st":"Clone"},"Invertable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Inverse"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["\"Abstraction of types which support a unary additive inversion\noperation. For a numeric type, this should return the \nnegative of the argument value. Note that the type \nparameter of this interface is not restricted to be a \nself type, in order to accommodate the possibility of \ntypes whose additive inverse can only be expressed in terms of \na wider type.\""],"by":["\"Gavin\""]},"$at":{"positiveValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The value itself, expressed as an instance of the\nwider type.\""]},"$nm":"positiveValue"},"negativeValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The additive inverse of the value, which may be expressed\nas an instance of a wider type.\""]},"$nm":"negativeValue"}},"$nm":"Invertable"},"Ordinal":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"see":["Character","Integer","Integral","Range"],"doc":["\"Abstraction of ordinal types, that is, types with \nsuccessor and predecessor operations, including\n`Integer` and other `Integral` numeric types.\n`Character` is also considered an ordinal type. \n`Ordinal` types may be used to generate a `Range`.\""],"by":["\"Gavin\""]},"$at":{"predecessor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if this is the minimum value\""],"doc":["\"The predecessor of this value.\""]},"$nm":"predecessor"},"successor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if this is the maximum value\""],"doc":["\"The successor of this value.\""]},"$nm":"successor"}},"$nm":"Ordinal","$st":"Other"},"largest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","smallest","max"],"doc":["\"Given two `Comparable` values, return largest of the\ntwo.\""]},"$nm":"largest"},"unflatten":{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"flatFunction"}],[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"args"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"unflatten"},"native":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a member whose implementation is \nbe provided by platform-native code.\""]},"$nm":"native"},"greaterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is greater than its element.\nThis is useful in conjunction with methods that receive\na predicate function.\""]},"$nm":"greaterThan"},"Identifiable":{"$mt":"ifc","$an":{"shared":[],"doc":["\"The abstract supertype of all types with a well-defined\nnotion of identity. Values of type `Identifiable` may \nbe compared using the `===` operator to determine if \nthey are references to the same object instance. For\nthe sake of convenience, this interface defines a\ndefault implementation of value equality equivalent\nto identity. Of course, subtypes are encouraged to\nrefine this implementation.\""],"by":["\"Gavin\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Identity equality comparing the identity of the two \nvalues. May be refined by subtypes for which value \nequality is more appropriate. Implementations must\nrespect the constraint that if `x===y` then `x==y` \n(equality is consistent with identity).\""],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["identityHash"],"doc":["\"The system-defined identity hash value of the \ninstance. Subtypes which refine `equals()` must \nalso refine `hash`, according to the general \ncontract defined by `Object`.\""],"actual":[]},"$nm":"hash"}},"$nm":"Identifiable"},"language":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["\"Contains information about the language\""],"by":["\"The Ceylon Team\""]},"$at":{"majorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The Ceylon language major version.\""]},"$nm":"majorVersion"},"majorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The major version of the code generated for the underlying runtime.\""]},"$nm":"majorVersionBinary"},"minorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The Ceylon language minor version.\""]},"$nm":"minorVersion"},"versionName":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The Ceylon language release name.\""]},"$nm":"versionName"},"releaseVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The Ceylon language release version.\""]},"$nm":"releaseVersion"},"minorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The minor version of the code generated for the underlying runtime.\""]},"$nm":"minorVersionBinary"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The Ceylon language version.\""]},"$nm":"version"}},"$nm":"language"},"Null":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"of":[{"$pk":"ceylon.language","$nm":"null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["null"],"doc":["'The type of the `null` value. Any union type of form \n`Null|T` is considered an optional type, whose values\ninclude `null`. Any type of this form may be written as\n`T?` for convenience.\n\nThe `if (exists ... )` construct, or, alternatively,\nthe `assert (exists ...)` construct, may be used to\nnarrow an optional type to a _definite_ type, that is,\na subtype of `Object`:\n\n    String? firstArg = process.arguments.first;\n    if (exists firstArg) {\n        print(\"hello \" + firstArg);\n    }\n\nThe `else` operator evaluates its second operand if \nand only if its first operand is `null`:\n\n    String name = process.arguments.first else \"world\";\n\nThe `then` operator evaluates its second operand when\nits first operand evaluates to `true`, and to `null` \notherwise:\n\n    Float? diff = x>=y then x-y;\n\n'"],"by":["\"Gavin\""]},"$nm":"Null"},"array":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["\"Create an array containing the given elements. If no\nelements are provided, create an empty array of the\ngiven element type.\""]},"$nm":"array"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable"],"doc":["\"Sort a given elements, returning a new sequence.\""]},"$nm":"sort"},"equalTo":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Returns a partial function that will compare an element\nto any other element and returns true if they're equal.\nThis is useful in conjunction with methods that receive\na predicate function.\""]},"$nm":"equalTo"},"arrayOfSize":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"size"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["\"Create an array of the specified size, populating every \nindex with the given element. If the specified size is\nsmaller than `1`, return an empty array of the given\nelement type.\""]},"$nm":"arrayOfSize"},"Ranged":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Index"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Index"},{"variance":"out","$nm":"Span"}],"$an":{"shared":[],"see":["List","Sequence","String"],"doc":["\"Abstract supertype of ranged objects which map a range\nof `Comparable` keys to ranges of values. The type\nparameter `Span` abstracts the type of the resulting\nrange.\n\nA span may be obtained from an instance of `Ranged`\nusing the span operator:\n\n    print(\"hello world\"[0..5])\n\""]},"$m":{"spanTo":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Obtain a span containing the mapped values between\nthe start of the receiver and the end index.\""]},"$nm":"spanTo"},"segment":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Obtain a segment containing the mapped values\nstarting from the given index, with the given \nlength.\""]},"$nm":"segment"},"spanFrom":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Obtain a span containing the mapped values between\nthe starting index and the end of the receiver.\""]},"$nm":"spanFrom"},"span":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Obtain a span containing the mapped values between \nthe two given indices.\""]},"$nm":"span"}},"$nm":"Ranged"},"times":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["plus","product"],"doc":["\"Multiply the given `Numeric` values.\""]},"$nm":"times"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Produces a sequence of each index to element `Entry` \nfor the given sequence of values.\""]},"$nm":"entries"},"license":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify the URL of the license of a module \nor package.\""]},"$nm":"license"},"null":{"super":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"obj","$an":{"shared":[],"doc":["\"The null value.\""],"by":["\"Gavin\""]},"$nm":"null"},"Object":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Basic","Null"],"doc":["\"The abstract supertype of all types representing \ndefinite values. Any two `Object`s may be compared\nfor value equality using the `==` and `!=` operators:\n\n    true==false\n    1==\"hello world\"\n    \"hello\"+ \" \" + \"world\"==\"hello world\"\n    Singleton(\"hello world\")=={ \"hello world\" }\n\nHowever, since `Null` is not a subtype of `Object`, the \nvalue `null` cannot be compared to any other value\nusing `==`. Thus, value equality is not defined for \noptional types. This neatly voids the problem of \ndeciding the value of the expression `null==null`, \nwhich is simply illegal.\""],"by":["\"Gavin\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determine if two values are equal. Implementations\nshould respect the constraints that:\n\n- if `x===y` then `x==y` (reflexivity), \n- if `x==y` then `y==x` (symmetry), \n- if `x==y` and `y==z` then `x==z` (transitivity).\n\nFurthermore it is recommended that implementations\nensure that if `x==y` then `x` and `y` have the\nsame concrete class.\""]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The hash value of the value, which allows the value\nto be an element of a hash-based set or key of a\nhash-based map. Implementations must respect the\nconstraint that if `x==y` then `x.hash==y.hash`.\""]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"A developer-friendly string representing the \ninstance. Concatenates the name of the concrete \nclass of the instance with the `hash` of the \ninstance. Subclasses are encouraged to refine this \nimplementation to produce a more meaningful \nrepresentation.\""]},"$nm":"string"}},"$nm":"Object"},"Float":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Exponentiable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Castable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"A 64-bit floating point number. A `Float` is capable of\napproximately representing numeric values between\n2<sup>-1022<\/sup> and (2-2<sup>-52<\/sup>)×2<sup>1023<\/sup>,\nalong with the special values `infinity` and `-infinity`, \nand undefined values (Not a Number). Zero is represented \nby distinct instances `+0`, `-0`, but these instances \nare equal. An undefined value is not equal to any other\nvalue, not even to itself.\""]},"$at":{"strictlyNegative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a negative number,\n`-0`, or `-infinity`. Produces `false` for a\npositive number, `+0`, or undefined.\""]},"$nm":"strictlyNegative"},"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The sign of this value. Produces `1` for a positive \nnumber or `infinity`. Produces `-1` for a negative\nnumber or `-infinity`. Produces `0` for `+0`, `-0`, \nor undefined.\""],"actual":[]},"$nm":"sign"},"infinite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"see":["infinity","finite"],"doc":["\"Determines whether this value is infinite in magnitude\nProduces `true` for `infinity` and `-infinity`.\nProduces `false` for a finite number, `+0`, `-0`, or\nundefined.\""]},"$nm":"infinite"},"undefined":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["\"Determines whether this value is undefined (that is, Not a Number or NaN).\nThe undefined value has the property that it is not equal (`==`) \nto itself, as a consequence the undefined value cannot sensibly be \nused in most collections.\""]},"$nm":"undefined"},"strictlyPositive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a positive number,\n`+0`, or `infinity`. Produces `false` for a \nnegative number, `-0`, or undefined.\""]},"$nm":"strictlyPositive"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a negative number or\n`-infinity`. Produces `false` for a positive number, \n`+0`, `-0`, or undefined.\""],"actual":[]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a positive number or\n`infinity`. Produces `false` for a negative number, \n`+0`, `-0`, or undefined.\""],"actual":[]},"$nm":"positive"},"finite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"see":["infinite","infinity"],"doc":["\"Determines whether this value is finite. Produces\n`false` for `infinity`, `-infinity`, and undefined.\""]},"$nm":"finite"}},"$nm":"Float"},"min":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","max","smallest"],"doc":["\"Given a nonempty sequence of `Comparable` values, \nreturn the smallest value in the sequence.\""]},"$nm":"min"},"LazySet":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"An implementation of Set that wraps an `Iterable` of\nelements. All operations on this Set are performed\non the `Iterable`.\""],"by":["\"Enrique Zamudio\""]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"complement"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"exclusiveUnion"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"union"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"LazySet"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazySet"},"Collection":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$pk":"ceylon.language","$nm":"Category"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["List","Map","Set"],"doc":["\"Represents an iterable collection of elements of finite \nsize. `Collection` is the abstract supertype of `List`,\n`Map`, and `Set`.\n\nA `Collection` forms a `Category` of its elements.\n\nAll `Collection`s are `Cloneable`. If a collection is\nimmutable, it is acceptable that `clone` produce a\nreference to the collection itself. If a collection is\nmutable, `clone` should produce an immutable collection\ncontaining references to the same elements, with the\nsame structure as the original collection&mdash;that \nis, it should produce an immutable shallow copy of the\ncollection.\""]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return `true` if the given object is an element of\nthis collection. In this default implementation,\nand in most refining implementations, return `false`\notherwise. An acceptable refining implementation\nmay return `true` for objects which are not \nelements of the collection, but this is not \nrecommended. (For example, the `contains()` method \nof `String` returns `true` for any substring of the\nstring.)\""],"actual":[]},"$nm":"contains"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`.\""],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"Determine if the collection is empty, that is, if \nit has no elements.\""],"actual":[]},"$nm":"empty"}},"$nm":"Collection"},"deprecated":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"reason"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark program elements which should not be \nused anymore.\""]},"$nm":"deprecated"},"Range":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"last"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Represents the range of totally ordered, ordinal values \ngenerated by two endpoints of type `Ordinal` and \n`Comparable`. If the first value is smaller than the\nlast value, the range is increasing. If the first value\nis larger than the last value, the range is decreasing.\nIf the two values are equal, the range contains exactly\none element. The range is always nonempty, containing \nat least one value.\n\nA range may be produced using the `..` operator:\n\n    for (i in min..max) { ... }\n    if (char in `A`..`Z`) { ... }\n\""],"by":["\"Gavin\""]},"$m":{"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"count"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"n"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"The element of the range that occurs `n` values after\nthe start of the range. Note that this operation \nis inefficient for large ranges.\""],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"next":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$nm":"next"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determines if the range includes the given object.\""],"actual":[]},"$nm":"contains"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determines if the range includes the given value.\""]},"$nm":"includes"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"span":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["\"The index of the end of the range.\""],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns the range itself, since ranges are \nimmutable.\""],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"doc":["\"An iterator for the elements of the range.\""],"actual":[]},"$nm":"iterator"},"decreasing":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["\"Determines if the range is decreasing.\""]},"$nm":"decreasing"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["\"The end of the range.\""],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns this range.\""],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns the range itself, since a Range cannot\ncontain nulls.\""],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["\"Reverse this range, returning a new range.\""],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The rest of the range, without the start of the\nrange.\""],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["\"The start of the range.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The nonzero number of elements in the range.\""],"actual":[]},"$nm":"size"}},"$nm":"Range"},"Integral":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Integral"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["\"Abstraction of integral numeric types. That is, types \nwith no fractional part, including `Integer`. The \ndivision operation for integral numeric types results \nin a remainder. Therefore, integral numeric types have \nan operation to determine the remainder of any division \noperation.\""],"by":["\"Gavin\""]},"$m":{"remainder":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["divided"],"doc":["\"The remainder, after dividing this number by the \ngiven number.\""]},"$nm":"remainder"}},"$at":{"unit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is one.\""]},"$nm":"unit"},"zero":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is zero.\""]},"$nm":"zero"}},"$nm":"Integral","$st":"Other"},"max":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","min","largest"],"doc":["\"Given a nonempty sequence of `Comparable` values, \nreturn the largest value in the sequence.\""]},"$nm":"max"},"SequenceAppender":{"super":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder"],"doc":["\"This class is used for constructing a new nonempty \nsequence by incrementally appending elements to an\nexisting nonempty sequence. The existing sequence is\nnot modified, since `Sequence`s are immutable. This \nclass is mutable but threadsafe.\""]},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The resulting nonempty sequence. If no elements \nhave been appended, the original nonempty \nsequence.\""],"actual":[]},"$nm":"sequence"}},"$nm":"SequenceAppender"},"RecursiveInitializationException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["\"Thrown when name could not be initialized due to recursive access during initialization.\""]},"$nm":"RecursiveInitializationException"},"byIncreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byDecreasing"],"doc":["\"A comparator which orders elements in increasing order \naccording to the `Comparable` returned by the given \n`comparable()` function.\""]},"$nm":"byIncreasing"},"larger":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["\"The value is larger than the given value.\""],"by":["\"Gavin\""]},"$nm":"larger"},"smallest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","largest","min"],"doc":["\"Given two `Comparable` values, return smallest of the\ntwo.\""]},"$nm":"smallest"},"true":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["\"A value representing truth in Boolean logic.\""],"by":["\"Gavin\""]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"true"},"join":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"iterables"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["\"Given a list of iterable objects, return a new sequence \nof all elements of the all given objects. If there are\nno arguments, or if none of the arguments contains any\nelements, return the empty sequence.\""]},"$nm":"join"},"Exponentiable":{"of":[{"$nm":"This"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"},{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$nm":"This"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["\"Abstraction of numeric types that may be raised to a\npower. Note that the type of the exponent may be\ndifferent to the numeric type which can be \nexponentiated.\""]},"$m":{"power":{"$t":{"$nm":"This"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The result of raising this number to the given\npower.\""]},"$nm":"power"}},"$nm":"Exponentiable","$st":"This"},"curry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}],[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"curry"},"forKey":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forItem"],"doc":["\"A function that returns the result of the given `resulting()` function \non the key of a given `Entry`.\""]},"$nm":"forKey"},"Character":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["String"],"doc":["\"A 32-bit Unicode character.\""],"by":["\"Gavin\""]},"$at":{"digit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is a numeric digit.\""]},"$nm":"digit"},"uppercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this is an uppercase representation of\nthe character.\""]},"$nm":"uppercase"},"control":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is an ISO control \ncharacter.\""]},"$nm":"control"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The code point of the character.\""]},"$nm":"integer"},"letter":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is a letter.\""]},"$nm":"letter"},"lowercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this is a lowercase representation of\nthe character.\""]},"$nm":"lowercase"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The lowercase representation of this character.\""]},"$nm":"lowercased"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The uppercase representation of this character.\""]},"$nm":"uppercased"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"A string containg just this character.\""],"actual":[]},"$nm":"string"},"whitespace":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is a whitespace \ncharacter.\""]},"$nm":"whitespace"},"titlecased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The title case representation of this character.\""]},"$nm":"titlecased"},"titlecase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this is a title case representation of\nthe character.\""]},"$nm":"titlecase"}},"$nm":"Character"},"Keys":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"prm","$pt":"v","$nm":"correspondence"}],"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"native":[]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"contains"}},"$nm":"Keys"},"process":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["\"Represents the current process (instance of the virtual\nmachine).\""],"by":["\"Gavin\"","\"Tako\""]},"$m":{"readLine":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Read a line of input text from the standard input \nof the virtual machine process.\""]},"$nm":"readLine"},"writeErrorLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Print a line to the standard error of the \nvirtual machine process.\""]},"$nm":"writeErrorLine"},"writeError":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Print a string to the standard error of the \nvirtual machine process.\""]},"$nm":"writeError"},"propertyValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"The value of the given system property of the virtual\nmachine, if any.\""]},"$nm":"propertyValue"},"namedArgumentValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"The value of the first argument of form `-name=value`, \n`--name=value`, or `-name value` specified among the \ncommand line arguments to the virtual machine, if\nany.\""]},"$nm":"namedArgumentValue"},"write":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Print a string to the standard output of the \nvirtual machine process.\""]},"$nm":"write"},"namedArgumentPresent":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Determine if an argument of form `-name` or `--name` \nwas specified among the command line arguments to \nthe virtual machine.\""]},"$nm":"namedArgumentPresent"},"writeLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["print"],"doc":["\"Print a line to the standard output of the \nvirtual machine process.\""]},"$nm":"writeLine"},"exit":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"code"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"exit"}},"$at":{"os":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"Returns the name of the operating system this process is running on.\""]},"$nm":"os"},"vmVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"Returns the version of the virtual machine this process is running on.\""]},"$nm":"vmVersion"},"vm":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"Returns the name of the virtual machine this process is running on.\""]},"$nm":"vm"},"osVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"Returns the version of the operating system this process is running on.\""]},"$nm":"osVersion"},"newline":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The line ending character sequence on this platform.\""]},"$nm":"newline"},"arguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The command line arguments to the virtual machine.\""]},"$nm":"arguments"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"},"nanoseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The elapsed time in nanoseconds since an arbitrary\nstarting point.\""]},"$nm":"nanoseconds"},"milliseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The elapsed time in milliseconds since midnight, \n1 January 1970.\""]},"$nm":"milliseconds"}},"$nm":"process"},"product":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["sum"],"doc":["\"Given a nonempty sequence of `Numeric` values, return \nthe product of the values.\""]},"$nm":"product"},"forItem":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forKey"],"doc":["\"A function that returns the result of the given `resulting()` function \non the item of a given `Entry`.\""]},"$nm":"forItem"},"shuffle":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"FirstArgs"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"SecondArgs"}],"$an":{"shared":[]},"$nm":"shuffle"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Create a new string containing the given characters.\""]},"$nm":"string"},"nothing":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"doc":["\"A value that is assignable to any type, but that \nresults in an exception when evaluated. This is most \nuseful for generating members in an IDE.\""]},"$nm":"nothing"},"doc":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify API documentation of a program\nelement.\""]},"$nm":"doc"},"Scalar":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$pk":"ceylon.language","$nm":"Number"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Scalar"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["\"Abstraction of numeric types representing scalar\nvalues, including `Integer` and `Float`.\""],"by":["\"Gavin\""]},"$at":{"magnitude":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The magnitude of this number.\""],"actual":[]},"$nm":"magnitude"},"wholePart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The integral value of the number after truncation \nof the fractional part. For integral numeric types,\nthe integral value of a number is the number \nitself.\""],"actual":[]},"$nm":"wholePart"},"fractionalPart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The fractional part of the number, after truncation \nof the integral part. For integral numeric types,\nthe fractional part is always zero.\""],"actual":[]},"$nm":"fractionalPart"}},"$nm":"Scalar","$st":"Other"},"tagged":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to categorize the API by tag.\""]},"$nm":"tagged"},"ifExists":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"prm","$pt":"f","$nm":"predicate"}],[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$nm":"ifExists"},"SequenceBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceAppender","join","Singleton"],"doc":["\"Since sequences are immutable, this class is used for\nconstructing a new sequence by incrementally appending \nelements to the empty sequence. This class is mutable\nbut threadsafe.\""]},"$m":{"append":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Append an element to the sequence and return this \n`SequenceBuilder`\""]},"$nm":"append"},"appendAll":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Append multiple elements to the sequence and return \nthis `SequenceBuilder`\""]},"$nm":"appendAll"}},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"native":[],"doc":["\"The resulting sequence. If no elements have been\nappended, the empty sequence.\""]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["\"Determine if the resulting sequence is empty.\""]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["\"The size of the resulting sequence.\""]},"$nm":"size"}},"$nm":"SequenceBuilder"},"variable":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark an attribute as variable. A `variable` \nattribute must be assigned with `=` and may be \nreassigned over time.\""]},"$nm":"variable"},"Correspondence":{"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Map","List","Category"],"doc":["\"Abstract supertype of objects which associate values \nwith keys. `Correspondence` does not satisfy `Category`,\nsince in some cases&mdash;`List`, for example&mdash;it is \nconvenient to consider the subtype a `Category` of its\nvalues, and in other cases&mdash;`Map`, for example&mdash;it \nis convenient to treat the subtype as a `Category` of its\nentries.\n\nThe item corresponding to a given key may be obtained \nfrom a `Correspondence` using the item operator:\n\n    value bg = settings[\"backgroundColor\"] else white;\n\nThe `get()` operation and item operator result in an\noptional type, to reflect the possibility that there is\nno item for the given key.\""],"by":["\"Gavin\""]},"$m":{"definesAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["\"Determines if this `Correspondence` defines a value\nfor any one of the given keys.\""]},"$nm":"definesAny"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["definesAny","definesEvery","keys"],"doc":["\"Determines if there is a value defined for the \ngiven key.\""]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["items"],"doc":["\"Returns the value defined for the given key, or \n`null` if there is no value defined for the given \nkey.\""]},"$nm":"get"},"items":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["get"],"doc":["\"Returns the items defined for the given keys, in\nthe same order as the corresponding keys.\""]},"$nm":"items"},"definesEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["\"Determines if this `Correspondence` defines a value\nfor every one of the given keys.\""]},"$nm":"definesEvery"}},"$at":{"keys":{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["defines"],"doc":["\"The `Category` of all keys for which a value is \ndefined by this `Correspondence`.\""]},"$nm":"keys"}},"$nm":"Correspondence"},"NonemptyContainer":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"deprecated":["\"Will be removed in Ceylon 1.0.\""],"doc":["\"A nonempty container.\""]},"$alias":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"NonemptyContainer"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"A count of the number of `true` items in the given values.\""]},"$nm":"count"},"internalFirst":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"native":[]},"$nm":"internalFirst"},"byItem":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"see":["byKey"],"doc":["\"A comparator for `Entry`s which compares their items \naccording to the given `comparing()` function.\""]},"$nm":"byItem"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify API authors.\""]},"$nm":"by"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["any"],"doc":["\"Determines if every one of the given boolean values \n(usually a comprehension) is `true`.\""]},"$nm":"every"},"$pkg-shared":"1","Tuple":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"First","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Rest","$hdn":"1","$mt":"prm","$pt":"v","$nm":"rest"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$nm":"Element"}],"variance":"out","$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language","$nm":"Empty"},"variance":"out","$nm":"Rest"}],"$an":{"shared":[],"doc":["\"A _tuple_ is a typed linked list. Each instance of \n`Tuple` represents the value and type of a single link.\nThe attributes `first` and `rest` allow us to retrieve\na value form the list without losing its static type \ninformation.\n\n    value point = Tuple(0.0, Tuple(0.0, Tuple(\"origin\")));\n    Float x = point.first;\n    Float y = point.rest.first;\n    String label = point.rest.rest.first;\n\nUsually, we abbreviate code involving tuples.\n\n    [Float,Float,String] point = [0.0, 0.0, \"origin\"];\n    Float x = point[0];\n    Float y = point[1];\n    String label = point[2];\n\nA list of types enclosed in brackets is an abbreviated \ntuple type. An instance of `Tuple` may be constructed \nby surrounding a value list in brackets:\n\n    [String,String] words = [\"hello\", \"world\"];\n\nThe index operator with a literal integer argument is a \nshortcut for a chain of evaluations of `rest` and \n`first`. For example, `point[1]` means `point.rest.first`.\n\nA _terminated_ tuple type is a tuple where the type of\nthe last link in the chain is `Empty`. An _unterminated_ \ntuple type is a tuple where the type of the last link\nin the chain is `Sequence` or `Sequential`. Thus, a \nterminated tuple type has a length that is known\nstatically. For an unterminated tuple type only a lower\nbound on its length is known statically.\n\nHere, `point` is an unterminated tuple:\n\n    String[] labels = ... ;\n    [Float,Float,String*] point = [0.0, 0.0, *labels];\n    Float x = point[0];\n    Float y = point[1];\n    String? firstLabel = point[2];\n    String[] allLabels = point[2...];\n\n\""],"by":["\"Gavin\""]},"$m":{"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"end"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$nm":"Rest"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"First"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"Tuple"},"lessThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is less than its element.\nThis is useful in conjunction with methods that receive\na predicate function.\""]},"$nm":"lessThan"},"identityHash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["identical"],"doc":["\"Return the system-defined identity hash value of the \ngiven value. This hash value is consistent with \nidentity equality.\""]},"$nm":"identityHash"},"uncurry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"prm","$pt":"f","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"uncurry"},"optional":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[]},"$nm":"optional"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["every"],"doc":["\"Determines if any one of the given boolean values \n(usually a comprehension) is `true`.\""]},"$nm":"any"},"Summable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Other"}],"$an":{"shared":[],"see":["String","Numeric"],"doc":["\"Abstraction of types which support a binary addition\noperator. For numeric types, this is just familiar \nnumeric addition. For strings, it is string \nconcatenation. In general, the addition operation \nshould be a binary associative operation.\""],"by":["\"Gavin\""]},"$m":{"plus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The result of adding the given value to this value. \nThis operation should never perform any kind of \nmutation upon either the receiving value or the \nargument value.\""]},"$nm":"plus"}},"$nm":"Summable","$st":"Other"},"EmptyContainer":{"$mt":"ifc","$an":{"shared":[],"deprecated":["\"Will be removed in Ceylon 1.0.\""],"doc":["\"An empty container.\""]},"$alias":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"EmptyContainer"},"Set":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"A collection of unique elements.\n\nA `Set` is a `Collection` of its elements.\n\nSets may be the subject of the binary union, \nintersection, exclusive union, and complement operators \n`|`, `&`, `^`, and `~`.\""]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing all the elements in \nthis `Set` that are not contained in the given\n`Set`.\""]},"$nm":"complement"},"subset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Determines if this `Set` is a subset of the given \n`Set`, that is, if the given set contains all of \nthe elements in this set.\""]},"$nm":"subset"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing only the elements \nthat are present in both this `Set` and the given \n`Set`.\""]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing only the elements \ncontained in either this `Set` or the given `Set`, \nbut no element contained in both sets.\""]},"$nm":"exclusiveUnion"},"superset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Determines if this `Set` is a superset of the \nspecified Set, that is, if this `Set` contains all \nof the elements in the specified `Set`.\""]},"$nm":"superset"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Two `Set`s are considered equal if they have the \nsame size and if every element of the first set is\nalso an element of the second set, as determined\nby `contains()`.\""],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing all the elements of \nthis `Set` and all the elements of the given `Set`.\""]},"$nm":"union"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Set"},"Sequence":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Empty"],"doc":["\"A nonempty, immutable sequence of values. The type \n`Sequence<Element>`, may be abbreviated `[Element+]`.\n\nGiven a possibly-empty sequence of type `[Element*], \nthe `if (nonempty ...)` construct, or, alternatively,\nthe `assert (nonempty ...)` construct, may be used to \nnarrow to a nonempty sequence type:\n\n    [Integer*] nums = ... ;\n    if (nonempty nums) {\n        Integer first = nums.first;\n        Integer max = max(nums);\n        [Integer+] squares = nums.collect((Integer i) i**2));\n        [Integer+] sorted = nums.sort(byIncreasing((Integer i) i));\n    }\n\nOperations like `first`, `max()`, `collect()`, and \n`sort()`, which polymorphically produce a nonempty or \nnon-null output when given a nonempty input are called \n_emptiness-preserving_.\""],"by":["\"Gavin\""]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"A nonempty sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\""],"actual":[]},"$nm":"sort"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["\"A nonempty sequence containing the results of \napplying the given mapping to the elements of this\nsequence.\""],"actual":[]},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"The index of the last element of the sequence.\""],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The last element of the sequence, that is, the\nelement with index `sequence.lastIndex`.\""],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"This sequence.\""],"actual":[]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns `false`, since every `Sequence` contains at\nleast one element.\""],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The rest of the sequence, without the first \nelement.\""],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Reverse this sequence, returning a new nonempty\nsequence.\""],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The first element of the sequence, that is, the\nelement with index `0`.\""],"actual":[]},"$nm":"first"}},"$nm":"Sequence"},"StringBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"shared":[],"native":[],"doc":["\"Since strings are immutable, this class is used for\nconstructing a string by incrementally appending \ncharacters to the empty string. This class is mutable \nbut threadsafe.\""]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Append the characters in the given string.\""]},"$nm":"append"},"appendSpace":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Append a space character.\""]},"$nm":"appendSpace"},"delete":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"count"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Deletes the specified number of characters from the\ncurrent content, starting at the specified position.\nIf the position is beyond the end of the current content,\nnothing is deleted. If the number of characters to delete\nis greater than the available characters from the given\nposition, the content is truncated at the given position.\""]},"$nm":"delete"},"reset":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Remove all content and return to initial state.\""]},"$nm":"reset"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Append the characters in the given strings.\""]},"$nm":"appendAll"},"insert":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Character"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$nm":"content"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Insert a String or Character at the specified position.\nIf the position is beyond the end of the current\nstring, the new content is simply appended to the\ncurrent content. If the position is a negative number,\nthe new content is inserted at index 0.\""]},"$nm":"insert"},"appendNewline":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Append a newline character.\""]},"$nm":"appendNewline"},"appendCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Append the given character.\""]},"$nm":"appendCharacter"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"The resulting string. If no characters have been\nappended, the empty string.\""],"actual":[]},"$nm":"string"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["\"Returns the size of the current content.\""]},"$nm":"size"}},"$nm":"StringBuilder"},"emptyOrSingleton":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Singleton","Empty"],"doc":["\"A `Singleton` if the given element is non-null, otherwise `Empty`.\""]},"$nm":"emptyOrSingleton"},"shared":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a type or member as shared. A `shared` \nmember is visible outside the block of code in which it\nis declared.\""]},"$nm":"shared"},"Binary":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Binary"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["\"Abstraction of numeric types that consist in\na sequence of bits, like `Integer`.\""],"by":["\"Stef\""]},"$m":{"clear":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Returns a new number with the given bit set to 0.\nBits are indexed from right to left.\""]},"$nm":"clear"},"xor":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a logical exclusive OR operation.\""]},"$nm":"xor"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Retrieves a given bit from this bit sequence. Bits are indexed from\nright to left.\""]},"$nm":"get"},"leftLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a left logical shift. Sign is not preserved. Padded with zeros.\""]},"$nm":"leftLogicalShift"},"set":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"bit"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a new number with the given bit set to the given value.\nBits are indexed from right to left.\""]},"$nm":"set"},"or":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a logical inclusive OR operation.\""]},"$nm":"or"},"rightArithmeticShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a right arithmetic shift. Sign is preserved. Padded with zeros.\""]},"$nm":"rightArithmeticShift"},"rightLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a right logical shift. Sign is not preserved. Padded with zeros.\""]},"$nm":"rightLogicalShift"},"flip":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a new number with the given bit flipped to its opposite value.\nBits are indexed from right to left.\""]},"$nm":"flip"},"and":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a logical AND operation.\""]},"$nm":"and"}},"$at":{"not":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The binary complement of this sequence of bits.\""]},"$nm":"not"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The number of bits (0 or 1) that this sequence of bits can hold.\""]},"$nm":"size"}},"$nm":"Binary","$st":"Other"},"commaList":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$nm":"commaList"},"Number":{"$mt":"ifc","$an":{"shared":[],"see":["Numeric"],"doc":["\"Abstraction of numbers. Numeric operations are provided\nby the subtype `Numeric`. This type defines operations\nwhich can be expressed without reference to the self\ntype `Other` of `Numeric`.\""],"by":["\"Gavin\""]},"$at":{"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The sign of this number. Returns `1` if the number \nis positive, `-1` if it is negative, or `0` if it \nis zero.\""]},"$nm":"sign"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if the number is too large to be represented \nas an `Integer`\""],"doc":["\"The number, represented as an `Integer`, after \ntruncation of any fractional part.\""]},"$nm":"integer"},"magnitude":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The magnitude of the number.\""]},"$nm":"magnitude"},"wholePart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The integral value of the number after truncation \nof the fractional part.\""]},"$nm":"wholePart"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is negative.\""]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is positive.\""]},"$nm":"positive"},"fractionalPart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The fractional part of the number, after truncation \nof the integral part.\""]},"$nm":"fractionalPart"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if the number is too large to be represented \nas a `Float`\""],"doc":["\"The number, represented as a `Float`.\""]},"$nm":"float"}},"$nm":"Number"},"LazyMap":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"entries"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["\"A Map implementation that wraps an `Iterable` of \nentries. All operations, such as lookups, size, etc. \nare performed on the `Iterable`.\""],"by":["\"Enrique Zamudio\""]},"$m":{"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"LazyMap"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazyMap"},"Map":{"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Entry","forKey","forItem","byItem","byKey"],"doc":["\"Represents a collection which maps _keys_ to _items_,\nwhere a key can map to at most one item. Each such \nmapping may be represented by an `Entry`.\n\nA `Map` is a `Collection` of its `Entry`s, and a \n`Correspondence` from keys to items.\n\nThe prescence of an entry in a map may be tested\nusing the `in` operator:\n\n    if (\"lang\"->\"en_AU\" in settings) { ... }\n\nThe entries of the map may be iterated using `for`:\n\n    for (key->item in settings) { ... }\n\nThe item for a key may be obtained using the item\noperator:\n\n    String lang = settings[\"lang\"] else \"en_US\";\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Two `Map`s are considered equal iff they have the \nsame _entry sets_. The entry set of a `Map` is the\nset of `Entry`s belonging to the map. Therefore, the\nmaps are equal iff they have same set of `keys`, and \nfor every key in the key set, the maps have equal\nitems.\""],"actual":[]},"$nm":"equals"},"mapItems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"mapping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["\"Returns a `Map` with the same keys as this map. For\nevery key, the item is the result of applying the\ngiven transformation function.\""]},"$nm":"mapItems"}},"$at":{"values":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Collection"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"Returns all the values stored in this `Map`. An \nelement can be stored under more than one key in \nthe map, and so it can be contained more than once \nin the resulting collection.\""]},"$nm":"values"},"inverse":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"Returns a `Map` in which every key is an `Item` in \nthis map, and every value is the set of keys that \nstored the `Item` in this map.\""]},"$nm":"inverse"},"keys":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"Returns the set of keys contained in this `Map`.\""],"actual":[]},"$nm":"keys"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Map"},"Numeric":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Invertable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float","Comparable"],"doc":["\"Abstraction of numeric types supporting addition,\nsubtraction, multiplication, and division, including\n`Integer` and `Float`. Additionally, a numeric type \nis expected to define a total order via an \nimplementation of `Comparable`.\""],"by":["\"Gavin\""]},"$m":{"minus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The difference between this number and the given \nnumber.\""]},"$nm":"minus"},"times":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The product of this number and the given number.\""]},"$nm":"times"},"divided":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Integral"],"doc":["\"The quotient obtained by dividing this number by \nthe given number. For integral numeric types, this \noperation results in a remainder.\""]},"$nm":"divided"}},"$nm":"Numeric","$st":"Other"},"throws":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"when"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a program element that throws an \nexception.\""]},"$nm":"throws"},"Closeable":{"$mt":"ifc","$an":{"shared":[],"doc":["\"Abstract supertype of types which may appear\nas the expression type of a resource expression\nin a `try` statement.\""]},"$m":{"open":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Called before entry to a `try` block.\""]},"$nm":"open"},"close":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Called after completion of a `try` block.\""]},"$nm":"close"}},"$nm":"Closeable"},"byDecreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byIncreasing"],"doc":["\"A comparator which orders elements in decreasing order \naccording to the `Comparable` returned by the given \n`comparable()` function.\""]},"$nm":"byDecreasing"},"formal":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a member whose implementation must \nbe provided by subtypes.\""]},"$nm":"formal"},"default":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a member whose implementation may be \nrefined by subtypes. Non-`default` declarations may not \nbe refined.\""]},"$nm":"default"},"String":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Castable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"native":[],"see":["string"],"doc":["\"A string of characters. Each character in the string is \na 32-bit Unicode character. The internal UTF-16 \nencoding is hidden from clients.\n\nA string is a `Category` of its `Character`s, and of\nits substrings:\n\n    `w` in greeting \n    \"hello\" in greeting\n\nStrings are summable:\n\n    String greeting = \"hello\" + \" \" + \"world\";\n\nThey are efficiently iterable:\n\n    for (char in \"hello world\") { ... }\n\nThey are `List`s of `Character`s:\n\n    value char = \"hello world\"[5];\n\nThey are ranged:\n\n    String who = \"hello world\"[6...];\n\nNote that since `string[index]` evaluates to the \noptional type `Character?`, it is often more convenient\nto write `string[index..index]`, which evaluates to a\n`String` containing a single character, or to the empty\nstring \"\" if `index` refers to a position outside the\nstring.\n\nThe `string()` function makes it possible to use \ncomprehensions to transform strings:\n\n    string(for (s in \"hello world\") if (s.letter) s.uppercased)\n\nSince a `String` has an underlying UTF-16 encoding, \ncertain operations are expensive, requiring iteration\nof the characters of the string. In particular, `size`\nrequires iteration of the whole string, and `get()`,\n`span()`, and `segment()` require iteration from the \nbeginning of the string to the given index.\""],"by":["\"Gavin\""]},"$m":{"plus":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns the concatenation of this string with the\ngiven string.\""],"actual":[]},"$nm":"plus"},"firstCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The first index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string.\""]},"$nm":"firstCharacterOccurrence"},"startsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if this string starts with the given \nsubstring.\""]},"$nm":"startsWith"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns the character at the given index in the \nstring, or `null` if the index is past the end of\nstring. The first character in the string occurs at\nindex zero. The last character in the string occurs\nat index `string.size-1`.\""],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if the given object is a string, and if\nso, if this string has the same length, and the \nsame characters, in the same order, as the given \nstring.\""],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the characters of this string beginning at \nthe given index, returning a string no longer than \nthe given length. If the portion of this string\nstarting at the given index is shorter than \nthe given length, return the portion of this string\nfrom the given index until the end of this string. \nOtherwise return a string of the given length. If \nthe start index is larger than the last index of the \nstring, return the empty string.\""],"actual":[]},"$nm":"segment"},"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Compare this string with the given string \nlexicographically, according to the Unicode values\nof the characters.\""],"actual":[]},"$nm":"compare"},"lastCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The last index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string.\""]},"$nm":"lastCharacterOccurrence"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"Determines if this string is longer than the given\nlength. This is a more efficient operation than\n`string.size>length`.\""]},"$nm":"longerThan"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if the given object is a `String` and, \nif so, if it occurs as a substring of this string,\nor if the object is a `Character` that occurs in\nthis string. That is to say, a string is considered \na `Category` of its substrings and of its \ncharacters.\""],"actual":[]},"$nm":"contains"},"repeat":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a string formed by repeating this string\nthe given number of times.\""]},"$nm":"repeat"},"join":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Join the given strings, using this string as a \nseparator.\""]},"$nm":"join"},"replace":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"replacement"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a string formed by replacing every \noccurrence in this string of the given substring\nwith the given replacement string, working from \nthe start of this string to the end.\""]},"$nm":"replace"},"firstOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The first index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string.\""]},"$nm":"firstOccurrence"},"terminal":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the last characters of the string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length.\""]},"$nm":"terminal"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"Determines if this string is shorter than the given\nlength. This is a more efficient operation than\n`string.size>length`.\""]},"$nm":"shorterThan"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"initial":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the first characters of this string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length.\""]},"$nm":"initial"},"occurrences":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The character indexes at which the given substring\noccurs within this string. Occurrences do not \noverlap.\""]},"$nm":"occurrences"},"lastOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The last index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string.\""]},"$nm":"lastOccurrence"},"split":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"separator"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"discardSeparators"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"groupSeparators"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Split the string into tokens, using the given\npredicate to determine which characters are \nseparator characters.\""]},"$nm":"split"},"endsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if this string ends with the given \nsubstring.\""]},"$nm":"endsWith"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the characters between the given indexes.\nIf the start index is the same as the end index,\nreturn a string with a single character.\nIf the start index is larger than the end index, \nreturn the characters in the reverse order from\nthe order in which they appear in this string.\nIf both the start index and the end index are \nlarger than the last index in the string, return \nthe empty string. Otherwise, if the last index is \nlarger than the last index in the sequence, return\nall characters from the start index to last \ncharacter of the string.\""],"actual":[]},"$nm":"span"}},"$at":{"normalized":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, after collapsing strings of whitespace \ninto single space characters and discarding whitespace \nfrom the beginning and end of the string.\""]},"$nm":"normalized"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"An iterator for the characters of the string.\""],"actual":[]},"$nm":"iterator"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, with all characters in lowercase.\""]},"$nm":"lowercased"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"hash"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, with all characters in uppercase.\""]},"$nm":"uppercased"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns this string.\""],"actual":[]},"$nm":"coalesced"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["longerThan","shorterThan"],"doc":["\"The length of the string (the number of characters\nit contains). In the case of the empty string, the\nstring has length zero. Note that this operation is\npotentially costly for long strings, since the\nunderlying representation of the characters uses a\nUTF-16 encoding.\""],"actual":[]},"$nm":"size"},"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"doc":["\"The index of the last character in the string, or\n`null` if the string has no characters. Note that \nthis operation is potentially costly for long \nstrings, since the underlying representation of the \ncharacters uses a UTF-16 encoding.\""],"actual":[]},"$nm":"lastIndex"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns the string itself.\""],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"Determines if this string has no characters, that\nis, if it has zero `size`. This is a more efficient \noperation than `string.size==0`.\""],"actual":[]},"$nm":"empty"},"lines":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Split the string into lines of text.\""]},"$nm":"lines"},"trimmed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, after discarding whitespace from the \nbeginning and end of the string.\""]},"$nm":"trimmed"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, with the characters in reverse order.\""],"actual":[]},"$nm":"reversed"},"characters":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The characters in this string.\""]},"$nm":"characters"}},"$nm":"String"},"identical":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$an":{"shared":[],"see":["identityHash"],"doc":["\"Determine if the arguments are identical. Equivalent to\n`x===y`. Only instances of `Identifiable` have \nwell-defined identity.\""]},"$nm":"identical"},"late":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to disable definite initialization analysis\nfor a simple attribute.\""]},"$nm":"late"},"emptyIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$an":{"shared":[],"doc":["\"An iterator that returns no elements.\""]},"$nm":"emptyIterator"},"parseFloat":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Float"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"The `Float` value of the given string representation of \na decimal number or `null` if the string does not represent \na decimal number.\n\nThe syntax accepted by this method is the same as the \nsyntax for a `Float` literal in the Ceylon language except \nthat it may optionally begin with a sign character (`+` or \n`-`).\""]},"$nm":"parseFloat"},"Anything":{"abstract":"1","of":[{"$pk":"ceylon.language","$nm":"Object"},{"$pk":"ceylon.language","$nm":"Null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"The abstract supertype of all types. A value of type \n`Anything` may be a definite value of type `Object`, or \nit may be the `null` value. A method declared `void` is \nconsidered to have the return type `Anything`.\n\nNote that the type `Nothing`, representing the \nintersection of all types, is a subtype of all types.\""],"by":["\"Gavin\""]},"$nm":"Anything"},"Exception":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"description"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"cause"}],"$mt":"cls","$an":{"shared":[],"native":[],"doc":["\"The supertype of all exceptions. A subclass represents\na more specific kind of problem, and may define \nadditional attributes which propagate information about\nproblems of that kind.\""],"by":["\"Gavin\"","\"Tom\""]},"$m":{"printStackTrace":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Print the stack trace to the standard error of\nthe virtual machine process.\""]},"$nm":"printStackTrace"}},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["description","cause"],"doc":["\"A message describing the problem. This default \nimplementation returns the description, if any, or \notherwise the message of the cause, if any.\""]},"$nm":"message"},"cause":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"attr","$an":{"shared":[],"doc":["\"The underlying cause of this exception.\""]},"$nm":"cause"},"description":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"attr","$an":{"doc":["\"A description of the problem.\""]},"$nm":"description"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"}},"$nm":"Exception"},"internalSort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"native":[]},"$nm":"internalSort"},"OverflowException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["\"Thrown when a mathematical operation caused a number to overflow from its bounds.\""]},"$nm":"OverflowException"},"parseInteger":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"The `Integer` value of the given string representation of an \ninteger, or `null` if the string does not represent an integer \nor if the mathematical integer it represents is too large in magnitude \nto be represented by an `Integer`.\n\nThe syntax accepted by this method is the same as the syntax for an \n`Integer` literal in the Ceylon language except that it may optionally \nbegin with a sign character (`+` or `-`).\""]},"$nm":"parseInteger"},"sum":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["product"],"doc":["\"Given a nonempty sequence of `Summable` values, return \nthe sum of the values.\""]},"$nm":"sum"},"infinity":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"doc":["\"An instance of `Float` representing \npositive infinity ∞.\""]},"$nm":"infinity"},"smaller":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["\"The value is smaller than the given value.\""],"by":["\"Gavin\""]},"$nm":"smaller"},"flatten":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"Return","$ps":[[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"tuple"}]],"$mt":"prm","$pt":"f","$nm":"tupleFunction"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"flatten"},"Iterable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"native":[],"see":["Collection"],"doc":["\"Abstract supertype of containers whose elements may be \niterated. An iterable container need not be finite, but\nits elements must at least be countable. There may not\nbe a well-defined iteration order, and so the order of\niterated elements may not be stable.\n\nThe type `Iterable<Element,Null>`, usually abbreviated\n`{Element*}` represents a possibly-empty iterable \ncontainer. The type `Iterable<Element,Nothing>`, \nusually abbreviated `{Element+}` represents a nonempty \niterable container.\n\nAn instance of `Iterable` may be constructed by \nsurrounding a value list in braces:\n\n    {String+} words = { \"hello\", \"world\" };\n\nAn instance of `Iterable` may be iterated using a `for`\nloop:\n\n    for (c in \"hello world\") { ... }\n\n`Iterable` and its subtypes define various operations\nthat return other iterable objects. Such operations \ncome in two flavors:\n\n- _Lazy_ operations return a \"view\" of the receiving\n  iterable object. If the underlying iterable object is\n  mutable, then changes to the underlying object will\n  be reflected in the resulting view. Lazy operations\n  are usually efficient, avoiding memory allocation or\n  iteration of the receiving iterable object.\n  \n- _Eager_ operations return an immutable object. If the\n  receiving iterable object is mutable, changes to this\n  object will not be reflected in the resulting \n  immutable object. Eager operations are often \n  expensive, involving memory allocation and iteration\n  of the receiving iterable object.\n\nLazy operations are preferred, because they can be \nefficiently chained. For example:\n\n    string.filter((Character c) => c.letter)\n          .map((Character c) => c.uppercased)\n\nis much less expensive than:\n\n    string.select((Character c) => c.letter)\n          .collect((Character c) => c.uppercased)\n\nFurthermore, it is always easy to produce a new \nimmutable iterable object given the view produced by a\nlazy operation. For example:\n\n    [ string.filter((Character c) => c.letter)\n            .map((Character c) => c.uppercased)... ]\n\nLazy operations normally return an instance of \n`Iterable` or `Map`.\n\nHowever, there are certain scenarios where an eager \noperation is more useful, more convenient, or no more \nexpensive than a lazy operation, including:\n\n- sorting operations, which are eager by nature,\n- operations which preserve emptiness\/nonemptiness of\n  the receiving iterable object.\n\nEager operations normally return a sequence.\""],"by":["\"Gavin\""]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["byIncreasing","byDecreasing"],"doc":["\"A sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\n\nFor convenience, the functions `byIncreasing()` \nand `byDecreasing()` produce a suitable \ncomparison function:\n\n    \"Hello World!\".sort(byIncreasing((Character c) => c.lowercased))\n\nThis operation is eager by nature.\""]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return the number of elements in this `Iterable` \nthat satisfy the predicate function.\""]},"$nm":"count"},"select":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["filter"],"doc":["\"A sequence containing the elements of this \ncontainer that satisfy the given predicate. An \neager counterpart to `filter()`.\""]},"$nm":"select"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"throws":["Exception","\"if the given step size is nonpositive, \ni.e. `step<1`\""],"doc":["\"Produce an `Iterable` containing every `step`th \nelement of this iterable object. If the step size \nis `1`, the `Iterable` contains the same elements \nas this iterable object. The step size must be \ngreater than zero. The expression\n\n    (0..10).by(3)\n\nresults in an iterable object with the elements\n`0`, `3`, `6`, and `9` in that order.\""]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["\"The result of applying the accumulating function to \neach element of this container in turn.\""]},"$nm":"fold"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return `true` if all elements satisfy the predicate\nfunction.\""]},"$nm":"every"},"taking":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Produce an `Iterable` containing the first `take`\nelements of this iterable object. If the specified \nnumber of elements is larger than the number of \nelements of this iterable object, the `Iterable` \ncontains the same elements as this iterable object.\""]},"$nm":"taking"},"chain":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["\"The elements of this iterable object, in their\noriginal order, followed by the elements of the \ngiven iterable object also in their original\norder.\""]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return `true` if at least one element satisfies the\npredicate function.\""]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["collect"],"doc":["\"An `Iterable` containing the results of applying\nthe given mapping to the elements of to this \ncontainer.\""]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"The last element which satisfies the given\npredicate, if any, or `null` otherwise.\""]},"$nm":"findLast"},"filter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["select"],"doc":["\"An `Iterable` containing the elements of this \ncontainer that satisfy the given predicate.\""]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"The first element which satisfies the given \npredicate, if any, or `null` otherwise.\""]},"$nm":"find"},"skipping":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Produce an `Iterable` containing the elements of\nthis iterable object, after skipping the first \n`skip` elements. If this iterable object does not \ncontain more elements than the specified number of \nelements, the `Iterable` contains no elements.\""]},"$nm":"skipping"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["map"],"doc":["\"A sequence containing the results of applying the\ngiven mapping to the elements of this container. An \neager counterpart to `map()`.\""]},"$nm":"collect"}},"$at":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"An iterator for the elements belonging to this \ncontainer.\""]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"The last element returned by the iterator, if any.\nIterables are potentially infinite, so calling this\nmight never return; also, this implementation will\niterate through all the elements, which might be\nvery time-consuming.\""],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"All entries of form `index->element` where `index` \nis the position at which `element` occurs, for every\nnon-null element of this `Iterable`, ordered by\nincreasing `index`. For a null element at a given\nposition in the original `Iterable`, there is no \nentry with the corresponding index in the resulting \niterable object. The expression \n\n    { \"hello\", null, \"world\" }.indexed\n    \nresults in an iterable object with the entries\n`0->\"hello\"` and `2->\"world\"`.\""]},"$nm":"indexed"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"A sequence containing the elements returned by the\niterator.\""]},"$nm":"sequence"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"The non-null elements of this `Iterable`, in their\noriginal order. For null elements of the original \n`Iterable`, there is no entry in the resulting \niterable object.\""]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"Determines if the iterable object is empty, that is\nto say, if the iterator returns no elements.\""],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"Returns an iterable object containing all but the \nfirst element of this container.\""]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"The first element returned by the iterator, if any.\nThis should always produce the same value as\n`iterable.iterator.head`.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"default":[]},"$nm":"size"}},"$nm":"Iterable"},"LazyList":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"An implementation of List that wraps an `Iterable` of\nelements. All operations on this List are performed on \nthe Iterable.\""],"by":["\"Enrique Zamudio\""]},"$m":{"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"doc":["\"Returns a `List` with the elements of this `List` \nin reverse order. This operation will create copy \nthe elements to a new `List`, so changes to the \noriginal `Iterable` will no longer be reflected in \nthe new `List`.\""],"actual":[]},"$nm":"reversed"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"first"}},"$nm":"LazyList"},"className":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"obj"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["\"Return the name of the concrete class of the given \nobject.\""]},"$nm":"className"},"List":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Sequence","Empty","Array"],"doc":["\"Represents a collection in which every element has a \nunique non-negative integer index.\n\nA `List` is a `Collection` of its elements, and a \n`Correspondence` from indices to elements.\n\nDirect access to a list element by index produces a\nvalue of optional type. The following idiom may be\nused instead of upfront bounds-checking, as long as \nthe list element type is a non-`null` type:\n\n    value char = \"hello world\"[index];\n    if (exists char) { \/*do something*\/ }\n    else { \/*out of bounds*\/ }\n\nTo iterate the indexes of a `List`, use the following\nidiom:\n\n    for (i->char in \"hello world\".indexed) { ... }\n\n\""]},"$m":{"withTrailing":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["\"Returns a new `List` that contains the specified\nelement appended to the end of this `List`s'\nelements.\""]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Determines if the given index refers to an element\nof this sequence, that is, if\n`index<=sequence.lastIndex`.\""],"actual":[]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns the element of this sequence with the given\nindex, or `null` if the given index is past the end\nof the sequence, that is, if\n`index>sequence.lastIndex`. The first element of\nthe sequence has index `0`.\""],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Two `List`s are considered equal iff they have the \nsame `size` and _entry sets_. The entry set of a \nlist `l` is the set of elements of `l.indexed`. \nThis definition is equivalent to the more intuitive \nnotion that two lists are equal iff they have the \nsame `size` and for every index either:\n\n- the lists both have the element `null`, or\n- the lists both have a non-null element, and the\n  two elements are equal.\""],"actual":[]},"$nm":"equals"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["\"Returns a new `List` that starts with the specified\nelement, followed by the elements of this `List`.\""]},"$nm":"withLeading"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"The index of the last element of the list, or\nnull if the list is empty.\""]},"$nm":"lastIndex"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns the last element of this `List`, if any.\""],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Reverse this list, returning a new list.\""]},"$nm":"reversed"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["\"Returns the first element of this `List`, if any.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["lastIndex"],"doc":["\"The number of elements in this sequence, always\n`sequence.lastIndex+1`.\""],"actual":[]},"$nm":"size"}},"$nm":"List"},"Container":{"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"deprecated":["\"Will be removed in Ceylon 1.0.\""],"see":["Category"],"doc":["\"Abstract supertype of objects which may or may not\ncontain one of more other values, called *elements*,\nand provide an operation for accessing the first \nelement, if any. A container which may or may not be \nempty is a `Container<Element,Null>`. A container which \nis always empty is a `Container<Nothing,Null>`. A \ncontainer which is never empty is a \n`Container<Element,Nothing>`.\""],"by":["\"Gavin\""]},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The last element. Should produce `null` if the\ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`.\""]},"$nm":"last"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the container is empty, that is, if\nit has no elements.\""]},"$nm":"empty"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The first element. Should produce `null` if the \ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`.\""]},"$nm":"first"}},"$nm":"Container"},"abstract":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a class as abstract. An `abstract` \nclass may not be directly instantiated. An `abstract`\nclass may have enumerated cases.\""]},"$nm":"abstract"},"zip":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"items"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"doc":["\"Given two sequences, form a new sequence consisting of\nall entries where, for any given index in the resulting\nsequence, the key of the entry is the element occurring \nat the same index in the first sequence, and the item \nis the element occurring at the same index in the second \nsequence. The length of the resulting sequence is the \nlength of the shorter of the two given sequences. \n\nThus:\n\n    zip(xs,ys)[i]==xs[i]->ys[i]\n\nfor every `0<=i<min({xs.size,ys.size})`.\""]},"$nm":"zip"},"export":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[]},"$nm":"export"},"equal":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["\"The value is exactly equal to the given value.\""],"by":["\"Gavin\""]},"$nm":"equal"},"finished":{"super":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"obj","$an":{"shared":[],"see":["Iterator"],"doc":["\"A value that indicates that an `Iterator`\nis exhausted and has no more values to \nreturn.\""]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"finished"},"Boolean":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"true"},{"$pk":"ceylon.language","$nm":"false"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"A type capable of representing the values true and\nfalse of Boolean logic.\""],"by":["\"Gavin\""]},"$nm":"Boolean"},"print":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":[],"doc":["\"Print a line to the standard output of the virtual \nmachine process, printing the given value's `string`, \nor `«null»` if the value is `null`.\n\nThis method is a shortcut for:\n\n    process.writeLine(line?.string else \"«null»\")\n\nand is intended mainly for debugging purposes.\""],"by":["\"Gavin\""]},"$nm":"print"},"ChainedIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"first"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"second"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["\"An `Iterator` that returns the elements of two\n`Iterable`s, as if they were chained together.\""],"by":["\"Enrique Zamudio\""]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"more":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"more"},"iter":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"iter"}},"$nm":"ChainedIterator"},"Basic":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"The default superclass when no superclass is explicitly\nspecified using `extends`. For the sake of convenience, \nthis class inherits a default definition of value\nequality from `Identifiable`.\""],"by":["\"Gavin\""]},"$nm":"Basic"},"Category":{"$mt":"ifc","$an":{"shared":[],"see":["Container"],"doc":["\"Abstract supertype of objects that contain other \nvalues, called *elements*, where it is possible to \nefficiently determine if a given value is an element. \n`Category` does not satisfy `Container`, because it is \nconceptually possible to have a `Category` whose \nemptiness cannot be computed.\n\nThe `in` operator may be used to determine if a value\nbelongs to a `Category`:\n\n    if (\"hello\" in \"hello world\") { ... }\n    if (69 in 0..100) { ... }\n    if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n\nOrdinarily, `x==y` implies that `x in cat == y in cat`.\nBut this contract is not required since it is possible\nto form a meaningful `Category` using a different\nequivalence relation. For example, an `IdentitySet` is\na meaningful `Category`.\""],"by":["\"Gavin\""]},"$m":{"containsAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["\"Determines if any one of the given values belongs \nto this `Category`\""]},"$nm":"containsAny"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["containsEvery","containsAny"],"doc":["\"Determines if the given value belongs to this\n`Category`, that is, if it is an element of this\n`Category`.\n\nFor most `Category`s, if `x==y`, then \n`category.contains(x)` evaluates to the same\nvalue as `category.contains(y)`. However, it is\npossible to form a `Category` consistent with some \nother equivalence relation, for example `===`. \nTherefore implementations of `contains()` which do \nnot satisfy this relationship are tolerated.\""]},"$nm":"contains"},"containsEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["\"Determines if every one of the given values belongs\nto this `Category`.\""]},"$nm":"containsEvery"}},"$nm":"Category"},"see":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify API references to other related \nprogram elements.\""]},"$nm":"see"},"NegativeNumberException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["\"Thrown when a negative number is not allowed.\""]},"$nm":"NegativeNumberException"},"actual":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a member of a type as refining a \nmember of a supertype.\""]},"$nm":"actual"},"Integer":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Integral"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Binary"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Exponentiable"},{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Integer"},{"$pk":"ceylon.language","$nm":"Float"}]}],"$pk":"ceylon.language","$nm":"Castable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"A 64-bit integer (or the closest approximation to a 64-bit integer \nprovided by the underlying platform).\""]},"$at":{"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The UTF-32 character with this UCS code point.\""]},"$nm":"character"}},"$nm":"Integer"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"doc":["\"The first of the given elements (usually a comprehension),\nif any.\""]},"$nm":"first"}},"ceylon.language.metamodel":{"sequencedAnnotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"sequencedAnnotations"},"SequencedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["\"An annotation that may occur multiple times\nat a single program element.\""]},"$nm":"SequencedAnnotation"},"optionalAnnotation":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"optionalAnnotation"},"OptionalAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["\"An annotation that may occur at most once\nat a single program element.\""]},"$nm":"OptionalAnnotation"},"Type":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Instance"}],"$an":{"shared":[]},"$nm":"Type"},"Annotation":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["\"An annotation.\""]},"$nm":"Annotation"},"ConstrainedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"variance":"out","$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["\"An annotation. This interface encodes\nconstraints upon the annotation in its\ntype arguments.\""]},"$m":{"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language.metamodel","$nm":"Annotated"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$an":{"shared":[]},"$nm":"occurs"}},"$nm":"ConstrainedAnnotation"},"annotations":{"$t":{"$nm":"Values"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$nm":"Value"},{"$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"annotations"},"Annotated":{"$mt":"ifc","$an":{"shared":[],"doc":["\"A program element that can\nbe annotated.\""]},"$nm":"Annotated"}}};
exports.$$metamodel$$=$$metamodel$$;
//Hand-written implementations
function getT$name() {return (this.$$||this.constructor).T$name;}
function getT$all() {return (this.$$||this.constructor).T$all;}
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
args.push(Basic);
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
for (var $ in superProto) {proto[$] = superProto[$]}
}
}
exports.initType=initType;
exports.initTypeProto=initTypeProto;
exports.initExistingType=initExistingType;
exports.initExistingTypeProto=initExistingTypeProto;
exports.inheritProto=inheritProto;
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
Object$proto.getString = function() { return String$(className(this) + "@" + this.getHash()); }
//Object$proto.getString=function() { String$(Object.prototype.toString.apply(this)) };
Object$proto.toString=function() { return this.getString().valueOf(); }
var BasicID=1;
function $identityHash(x) {
var hash = x.BasicID;
return (hash !== undefined)
? hash : (x.BasicID = BasicID++);
}
function Identifiable(obj) {}
initType(Identifiable, "ceylon.language::Identifiable");
var Identifiable$proto = Identifiable.$$.prototype;
Identifiable$proto.equals = function(that) {
return isOfType(that, {t:Identifiable}) && (that===this);
}
Identifiable$proto.getHash = function() { return $identityHash(this); }
function Basic(obj) {
return obj;
}
initTypeProto(Basic, 'ceylon.language::Basic', Object$, Identifiable);
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
function Iterable(wat) {
return wat;
}
initTypeProto(Iterable, 'ceylon.language::Iterable', $init$Container());
function $init$Iterable() { return Iterable; }
var Iterable$proto=Iterable.$$.prototype;
Iterable$proto.getEmpty = function() {
return this.getIterator().next() === $finished;
}
Iterable$proto.getFirst = function() {
var e = this.getIterator().next();
return e === $finished ? null : e;
}
Iterable$proto.getRest = function() {
return this.skipping(1);
}
Iterable$proto.getSequence = function() {
var a = [];
var iter = this.getIterator();
var next;
while ((next = iter.next()) !== $finished) {
a.push(next);
}
return ArraySequence(a, this.$$targs$$);
}
Iterable$proto.$map = function(mapper) {
var iter = this;
return Comprehension(function() {
var it = iter.getIterator();
return function() {
var e = it.next();
if(e !== $finished) {return mapper(e);}
return $finished;
}
});
}
Iterable$proto.$filter = function(select) {
var iter = this;
return Comprehension(function() {
var it = iter.getIterator();
return function() {
do {
var e = it.next();
} while ((e !== $finished) && !select(e));
return e;
}
});
}
Iterable$proto.fold = function(ini, accum) {
var r = ini;
var iter = this.getIterator();
var e; while ((e = iter.next()) !== $finished) {
r = accum(r, e);
}
return r;
}
Iterable$proto.find = function(select) {
var iter = this.getIterator();
var e; while ((e = iter.next()) !== $finished) {
if (select(e)) {
return e;
}
}
return null;
}
Iterable$proto.findLast = function(select) {
var iter = this.getIterator();
var last = null;
var e; while ((e = iter.next()) !== $finished) {
if (select(e)) {
last = e;
}
}
return last;
}
Iterable$proto.$sort = function(/*Callable<Comparison?,Element,Element>*/comparing) {
var a = [];
var iter = this.getIterator();
var e; while ((e = iter.next()) !== $finished) {
a.push(e);
}
a.sort(function(x,y) {
var r = comparing(x,y);
if (r === larger) return 1;
if (r === smaller) return -1;
return 0;
});
return ArraySequence(a, this.$$targs$$);
}
Iterable$proto.any = function(/*Callable<Boolean,Element>*/selecting) {
var iter = this.getIterator();
var e; while ((e = iter.next()) !== $finished) {
if (selecting(e)) {
return true;
}
}
return false;
}
Iterable$proto.$every = function(/*Callable<Boolean,Element>*/selecting) {
var iter = this.getIterator();
var e; while ((e = iter.next()) !== $finished) {
if (!selecting(e)) {
return false;
}
}
return true;
}
Iterable$proto.skipping = function(skip) {
function skip$iter(iter,skip){
var $cmp$=new skip$iter.$$;
Basic($cmp$);
$cmp$.iter=iter;
$cmp$.skip=skip;
$cmp$.getIterator=function(){
var iter = this.iter.getIterator();
for (var i=0; i < this.skip; i++) {
iter.next();
}
return iter;
};
return $cmp$;
}
initTypeProto(skip$iter, 'ceylon.language::SkipIterable', Basic, Iterable);
return skip$iter(this,skip);
}
Iterable$proto.taking = function(take) {
if (take <= 0) return empty;
var iter = this;
return Comprehension(function() {
var it = iter.getIterator();
var i = 0;
return function() {
if (i >= take) {return $finished;}
++i;
return it.next();
}
});
}
Iterable$proto.by = function(step) {
if (step == 1) return this;
if (step < 1) throw Exception(String$("Step must be positive"));
var iter = this;
return Comprehension(function() {
var it = iter.getIterator();
return function() {
var e = it.next();
for (var i=1; i<step && (it.next()!==$finished); i++);
return e;
}
});
}
Iterable$proto.count = function(sel) {
var c = 0;
var iter = this.getIterator();
var e; while ((e = iter.next()) !== $finished) {
if (sel(e)) c++;
}
return c;
}
Iterable$proto.getCoalesced = function() {
var iter = this;
return Comprehension(function() {
var it = iter.getIterator();
return function() {
var e;
while ((e = it.next()) === null);
return e;
}
});
}
Iterable$proto.getIndexed = function() {
var iter = this;
return Comprehension(function() {
var it = iter.getIterator();
var idx = 0;
return function() {
var e;
while ((e = it.next()) === null) {idx++;}
return e === $finished ? e : Entry(idx++, e);
}
});
}
Iterable$proto.getLast = function() {
var iter = this.getIterator();
var l=null;
var e; while ((e = iter.next()) !== $finished) {
l=e;
}
return l;
}
Iterable$proto.collect = function(collecting) {
return this.$map(collecting).getSequence();
}
Iterable$proto.select = function(selecting) {
return this.$filter(selecting).getSequence();
}
Iterable$proto.group = function(grouping, $$$mptypes) {
var map = HashMap();
var it = this.getIterator();
var elem;
var newSeq = ArraySequence([], this.$$targs$$);
while ((elem=it.next()) !== $finished) {
var key = grouping(elem);
var seq = map.put(Entry(key, newSeq, [$$$mptypes[0], {t:Sequence, a:this.$$targs$$}]), true);
if (seq === null) {
seq = newSeq;
newSeq = ArraySequence([], this.$$targs$$);
}
seq.push(elem);
}
return map;
}
Iterable$proto.chain = function(other, $$$mptypes) {
return ChainedIterable(this, other, [this.$$targs$$[0], $$$mptypes[0]]);
}
Iterable$proto.getSize = function() {
return this.count(function() { return true; });
}
exports.Iterable=Iterable;
function ChainedIterable(first, second, $$targs$$, chained) {
if (chained===undefined) {chained = new ChainedIterable.$$;}
Basic(chained);
chained.first = first;
chained.second = second;
chained.$$targs$$=$$targs$$;
return chained;
}
initTypeProto(ChainedIterable, "ceylon.language::ChainedIterable",
Basic, Iterable);
var ChainedIterable$proto = ChainedIterable.$$.prototype;
ChainedIterable$proto.getIterator = function() {
return ChainedIterator(this.first, this.second);
}
function Sequential($$sequential) {
return $$sequential;
}
function $init$Sequential() {
if (Sequential.$$===undefined) {
initTypeProto(Sequential, 'ceylon.language::Sequential', $init$List(), $init$Ranged(), $init$Cloneable());
}
return Sequential;
}
$init$Sequential();
Sequential.$$.prototype.getString=function() {
return this.getEmpty()?String$("[]",2) :
StringBuilder().appendAll([String$("[ ",2),commaList(this),String$(" ]",2)]).getString();
}
exports.Sequential=Sequential;
function Empty() {
var that = new Empty.$$;
that.value = [];
that.$$targs$$=[{t:Nothing}];
return that;
}
initTypeProto(Empty, 'ceylon.language::Empty', Sequential, $init$Ranged(), $init$Cloneable());
var Empty$proto = Empty.$$.prototype;
Empty$proto.getEmpty = function() { return true; }
Empty$proto.defines = function(x) { return false; }
Empty$proto.getKeys = function() { return TypeCategory(this, {t:Integer}); }
Empty$proto.definesEvery = function(x) { return false; }
Empty$proto.definesAny = function(x) { return false; }
Empty$proto.items = function(x) { return this; }
Empty$proto.getSize = function() { return 0; }
Empty$proto.get = function(x) { return null; }
Empty$proto.getFirst = function() { return null; }
Empty$proto.segment = function(a,b) { return this; }
Empty$proto.span = function(a,b) { return this; }
Empty$proto.spanTo = function(a) { return this; }
Empty$proto.spanFrom = function(a) { return this; }
Empty$proto.getIterator = function() { return getEmptyIterator(); }
Empty$proto.getString = function() { return String$("{}"); }
Empty$proto.contains = function(x) { return false; }
Empty$proto.getLastIndex = function() { return null; }
Empty$proto.getClone = function() { return this; }
Empty$proto.count = function(x) { return 0; }
Empty$proto.getReversed = function() { return this; }
Empty$proto.skipping = function(skip) { return this; }
Empty$proto.taking = function(take) { return this; }
Empty$proto.by = function(step) { return this; }
Empty$proto.$every = function(f) { return false; }
Empty$proto.any = function(f) { return false; }
Empty$proto.$sort = function(f) { return this; }
Empty$proto.$map = function(f) { return this; }
Empty$proto.fold = function(i,r) { return i; }
Empty$proto.find = function(f) { return null; }
Empty$proto.findLast = function(f) { return null; }
Empty$proto.$filter = function(f) { return this; }
Empty$proto.getCoalesced = function() { return this; }
Empty$proto.getIndexed = function() { return this; }
Empty$proto.withLeading = function(other) {
return new ArraySequence([other], [{t:other.getT$all()[other.getT$name()]}]);
}
Empty$proto.withTrailing = function(other) {
return new ArraySequence([other], [{t:other.getT$all()[other.getT$name()]}]);
}
Empty$proto.chain = function(other) { return other; }
var empty = Empty();
empty.$$targs$$=[{t:Nothing},{t:Null}];
exports.empty=empty;
exports.Empty=Empty;
function emptyIterator(){
var $$emptyIterator=new emptyIterator.$$;
Iterator($$emptyIterator);
$$emptyIterator.$$targs$$=[{t:Nothing}];
return $$emptyIterator;
}
function $init$emptyIterator(){
if (emptyIterator.$$===undefined){
initTypeProto(emptyIterator,'ceylon.language::emptyIterator',Basic,$init$Iterator());
}
return emptyIterator;
}
exports.$init$emptyIterator=$init$emptyIterator;
$init$emptyIterator();
(function($$emptyIterator){
$$emptyIterator.next=function (){
var $$emptyIterator=this;
return $finished;
};
})(emptyIterator.$$.prototype);
var emptyIterator$2=emptyIterator();
var getEmptyIterator=function(){
return emptyIterator$2;
}
exports.getEmptyIterator=getEmptyIterator;
function Comprehension(makeNextFunc, $$targs$$, compr) {
if (compr===undefined) {compr = new Comprehension.$$;}
Basic(compr);
compr.makeNextFunc = makeNextFunc;
compr.$$targs$$=$$targs$$;
return compr;
}
initTypeProto(Comprehension, 'ceylon.language::Comprehension', Basic, Iterable);
var Comprehension$proto = Comprehension.$$.prototype;
Comprehension$proto.getIterator = function() {
return ComprehensionIterator(this.makeNextFunc(), this.$$targs$$);
}
exports.Comprehension=Comprehension;
function ComprehensionIterator(nextFunc, $$targs$$, it) {
if (it===undefined) {it = new ComprehensionIterator.$$;}
it.$$targs$$=$$targs$$;
Basic(it);
it.next = nextFunc;
return it;
}
initTypeProto(ComprehensionIterator, 'ceylon.language::ComprehensionIterator',
Basic, $init$Iterator());
//Compiled from Ceylon sources
function Correspondence($$correspondence){
}
exports.Correspondence=Correspondence;
function $init$Correspondence(){
    if (Correspondence.$$===undefined){
        initTypeProto(Correspondence,'ceylon.language::Correspondence');
        (function($$correspondence){
            $$correspondence.defines=function (key$1){
                var $$correspondence=this;
                return exists($$correspondence.get(key$1));
            };
            $$correspondence.getKeys=function getKeys(){
                var $$correspondence=this;
                return Keys($$correspondence,[$$correspondence.$$targs$$[0],$$correspondence.$$targs$$[1]]);
            };
            $$correspondence.definesEvery=function definesEvery(keys$2){
                var $$correspondence=this;
                var it$3 = keys$2.getIterator();
                var key$4;while ((key$4=it$3.next())!==getFinished()){
                    if((!$$correspondence.defines(key$4))){
                        return false;
                    }
                }
                if (getFinished() === key$4){
                    return true;
                }
            };$$correspondence.definesAny=function definesAny(keys$5){
                var $$correspondence=this;
                var it$6 = keys$5.getIterator();
                var key$7;while ((key$7=it$6.next())!==getFinished()){
                    if($$correspondence.defines(key$7)){
                        return true;
                    }
                }
                if (getFinished() === key$7){
                    return false;
                }
            };$$correspondence.items=function (keys$8){
                var $$correspondence=this;
                return Comprehension(function(){
                    var it$9=keys$8.getIterator();
                    var key$10=getFinished();
                    var next$key$10=function(){return key$10=it$9.next();}
                    next$key$10();
                    return function(){
                        if(key$10!==getFinished()){
                            var tmpvar$11=$$correspondence.get(key$10);
                            next$key$10();
                            return tmpvar$11;
                        }
                        return getFinished();
                    }
                },[{ t:'u', l:[{t:Null},$$correspondence.$$targs$$[1]]},{t:Null}]).getSequence();
            };
        })(Correspondence.$$.prototype);
    }
    return Correspondence;
}
exports.$init$Correspondence=$init$Correspondence;
$init$Correspondence();
function Keys(correspondence$12, $$targs$$,$$keys){
    $init$Keys();
    if ($$keys===undefined)$$keys=new Keys.$$;
    $$keys.$$targs$$=$$targs$$;
    $$keys.correspondence$12=correspondence$12;
    Category($$keys);
    return $$keys;
}
function $init$Keys(){
    if (Keys.$$===undefined){
        initTypeProto(Keys,'ceylon.language::Keys',Basic,$init$Category());
        (function($$keys){
            $$keys.contains=function contains(key$13){
                var $$keys=this;
                var key$14;
                if(isOfType((key$14=key$13),$$keys.$$targs$$[0])){
                    return $$keys.correspondence$12.defines(key$14);
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
            $$binary.clear=function clear(index$15){
                var $$binary=this;
                return $$binary.set(index$15,false);
            };
        })(Binary.$$.prototype);
    }
    return Binary;
}
exports.$init$Binary=$init$Binary;
$init$Binary();
function Castable($$castable){
}
exports.Castable=Castable;
function $init$Castable(){
    if (Castable.$$===undefined){
        initTypeProto(Castable,'ceylon.language::Castable');
        (function($$castable){
        })(Castable.$$.prototype);
    }
    return Castable;
}
exports.$init$Castable=$init$Castable;
$init$Castable();
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
function Collection($$collection){
    Iterable($$collection);
    Category($$collection);
    Cloneable($$collection);
}
exports.Collection=Collection;
function $init$Collection(){
    if (Collection.$$===undefined){
        initTypeProto(Collection,'ceylon.language::Collection',$init$Iterable(),$init$Category(),$init$Cloneable());
        (function($$collection){
            $$collection.getEmpty=function getEmpty(){
                var $$collection=this;
                return $$collection.getSize().equals((0));
            };
            $$collection.contains=function contains(element$16){
                var $$collection=this;
                var it$17 = $$collection.getIterator();
                var elem$18;while ((elem$18=it$17.next())!==getFinished()){
                    var elem$19;
                    if((elem$19=elem$18)!==null&&elem$19.equals(element$16)){
                        return true;
                    }
                }
                if (getFinished() === elem$18){
                    return false;
                }
            };$$collection.getString=function getString(){
                var $$collection=this;
                return (opt$20=($$collection.getEmpty()?String$("{}",2):null),opt$20!==null?opt$20:StringBuilder().appendAll([String$("{ ",2),commaList($$collection).getString(),String$(" }",2)]).getString());
            };
        })(Collection.$$.prototype);
    }
    return Collection;
}
exports.$init$Collection=$init$Collection;
$init$Collection();
var opt$20;
var commaList=function (elements$21){
    return String$(", ",2).join(Comprehension(function(){
        var it$22=elements$21.getIterator();
        var element$23=getFinished();
        var next$element$23=function(){return element$23=it$22.next();}
        next$element$23();
        return function(){
            if(element$23!==getFinished()){
                var tmpvar$24=(opt$25=(opt$26=element$23,opt$26!==null?opt$26.getString():null),opt$25!==null?opt$25:String$("null",4));
                next$element$23();
                return tmpvar$24;
            }
            return getFinished();
        }
    },[{t:String$},{t:Null}]));
};
var opt$25,opt$26;
function Category($$category){
}
exports.Category=Category;
function $init$Category(){
    if (Category.$$===undefined){
        initTypeProto(Category,'ceylon.language::Category');
        (function($$category){
            $$category.containsEvery=function containsEvery(elements$27){
                var $$category=this;
                var it$28 = elements$27.getIterator();
                var element$29;while ((element$29=it$28.next())!==getFinished()){
                    if((!$$category.contains(element$29))){
                        return false;
                    }
                }
                if (getFinished() === element$29){
                    return true;
                }
            };$$category.containsAny=function containsAny(elements$30){
                var $$category=this;
                var it$31 = elements$30.getIterator();
                var element$32;while ((element$32=it$31.next())!==getFinished()){
                    if($$category.contains(element$32)){
                        return true;
                    }
                }
                if (getFinished() === element$32){
                    return false;
                }
            };
        })(Category.$$.prototype);
    }
    return Category;
}
exports.$init$Category=$init$Category;
$init$Category();
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
function Tuple(first$33, rest$34, $$targs$$,$$tuple){
    $init$Tuple();
    if ($$tuple===undefined)$$tuple=new Tuple.$$;
    $$tuple.$$targs$$=$$targs$$;
    Object$($$tuple);
    Sequence($$tuple);
    Cloneable($$tuple);
    $$tuple.first$35=first$33;
    $$tuple.rest$36=rest$34;
    return $$tuple;
}
exports.Tuple=Tuple;
function $init$Tuple(){
    if (Tuple.$$===undefined){
        initTypeProto(Tuple,'ceylon.language::Tuple',Object$,$init$Sequence(),$init$Cloneable());
        (function($$tuple){
            $$tuple.getFirst=function getFirst(){
                return this.first$35;
            };
            $$tuple.getRest=function getRest(){
                return this.rest$36;
            };
            $$tuple.getSize=function getSize(){
                var $$tuple=this;
                return (1).plus($$tuple.getRest().getSize());
            };
            $$tuple.get=function get(index$37){
                var $$tuple=this;
                
                var switch$38=index$37.compare((0));
                if (switch$38===getSmaller()) {
                    return null;
                }else if (switch$38===getEqual()) {
                    return $$tuple.getFirst();
                }else if (switch$38===getLarger()) {
                    return $$tuple.getRest().get(index$37.minus((1)));
                }
            };$$tuple.getLastIndex=function getLastIndex(){
                var $$tuple=this;
                var restLastIndex$39;
                if((restLastIndex$39=$$tuple.getRest().getLastIndex())!==null){
                    return restLastIndex$39.plus((1));
                }else {
                    return (0);
                }
            };$$tuple.getLast=function getLast(){
                var $$tuple=this;
                var rest$40;
                if(nonempty((rest$40=$$tuple.getRest()))){
                    return rest$40.getLast();
                }else {
                    return $$tuple.getFirst();
                }
            };$$tuple.getReversed=function getReversed(){
                var $$tuple=this;
                return $$tuple.getRest().getReversed().withTrailing($$tuple.getFirst(),[$$tuple.$$targs$$[1]]);
            };
            $$tuple.segment=function segment(from$41,length$42){
                var $$tuple=this;
                if(length$42.compare((0)).equals(getSmaller())){
                    return empty;
                }
                var realFrom$43=(opt$44=(from$41.compare((0)).equals(getSmaller())?(0):null),opt$44!==null?opt$44:from$41);
                var opt$44;
                if(realFrom$43.equals((0))){
                    return (opt$45=(length$42.equals((1))?Tuple($$tuple.getFirst(),empty,[$$tuple.$$targs$$[1],$$tuple.$$targs$$[1],{t:Empty}]):null),opt$45!==null?opt$45:$$tuple.getRest().segment((0),length$42.plus(realFrom$43).minus((1))).withLeading($$tuple.getFirst(),[$$tuple.$$targs$$[1]]));
                    var opt$45;
                }
                return $$tuple.getRest().segment(realFrom$43.minus((1)),length$42);
            };$$tuple.span=function span(from$46,end$47){
                var $$tuple=this;
                var realFrom$48=(opt$49=(from$46.compare((0)).equals(getSmaller())?(0):null),opt$49!==null?opt$49:from$46);
                var opt$49;
                return (opt$50=((realFrom$48.compare(end$47)!==getLarger())?$$tuple.segment(from$46,end$47.minus(realFrom$48).plus((1))):null),opt$50!==null?opt$50:$$tuple.segment(end$47,realFrom$48.minus(end$47).plus((1))).getReversed().getSequence());
                var opt$50;
            };$$tuple.spanTo=function (to$51){
                var $$tuple=this;
                return (opt$52=(to$51.compare((0)).equals(getSmaller())?empty:null),opt$52!==null?opt$52:$$tuple.span((0),to$51));
            };
            $$tuple.spanFrom=function (from$53){
                var $$tuple=this;
                return $$tuple.span(from$53,$$tuple.getSize());
            };
            $$tuple.getClone=function getClone(){
                var $$tuple=this;
                return $$tuple;
            };
            $$tuple.getIterator=function getIterator(){
                var $$tuple=this;
                function iterator$54(){
                    var $$iterator$54=new iterator$54.$$;
                    Iterator($$iterator$54);
                    $$iterator$54.current$55=$$tuple;
                    return $$iterator$54;
                }
                function $init$iterator$54(){
                    if (iterator$54.$$===undefined){
                        initTypeProto(iterator$54,'ceylon.language::Tuple.iterator.iterator',Basic,$init$Iterator());
                    }
                    return iterator$54;
                }
                $init$iterator$54();
                (function($$iterator$54){
                    $$iterator$54.getCurrent$55=function getCurrent$55(){
                        return this.current$55;
                    };
                    $$iterator$54.setCurrent$55=function setCurrent$55(current$56){
                        return this.current$55=current$56;
                    };
                    $$iterator$54.next=function next(){
                        var $$iterator$54=this;
                        var c$57;
                        if(nonempty((c$57=$$iterator$54.getCurrent$55()))){
                            $$iterator$54.setCurrent$55(c$57.getRest());
                            return c$57.getFirst();
                        }else {
                            return getFinished();
                        }
                    };
                })(iterator$54.$$.prototype);
                var iterator$58=iterator$54(new iterator$54.$$);
                var getIterator$58=function(){
                    return iterator$58;
                }
                return getIterator$58();
            };$$tuple.contains=function contains(element$59){
                var $$tuple=this;
                var first$60;
                if((first$60=$$tuple.getFirst())!==null&&first$60.equals(element$59)){
                    return true;
                }else {
                    return $$tuple.getRest().contains(element$59);
                }
            };
        })(Tuple.$$.prototype);
    }
    return Tuple;
}
exports.$init$Tuple=$init$Tuple;
$init$Tuple();
var opt$52;
function List($$list){
    Collection($$list);
    Correspondence($$list);
    Ranged($$list);
    Cloneable($$list);
}
exports.List=List;
function $init$List(){
    if (List.$$===undefined){
        initTypeProto(List,'ceylon.language::List',$init$Collection(),$init$Correspondence(),$init$Ranged(),$init$Cloneable());
        (function($$list){
            $$list.getSize=function getSize(){
                var $$list=this;
                return (opt$61=$$list.getLastIndex(),opt$61!==null?opt$61:(-(1))).plus((1));
            };
            $$list.defines=function (index$62){
                var $$list=this;
                return (index$62.compare((opt$63=$$list.getLastIndex(),opt$63!==null?opt$63:(-(1))))!==getLarger());
            };
            $$list.getIterator=function getIterator(){
                var $$list=this;
                function listIterator$64(){
                    var $$listIterator$64=new listIterator$64.$$;
                    Iterator($$listIterator$64);
                    $$listIterator$64.index$65=(0);
                    return $$listIterator$64;
                }
                function $init$listIterator$64(){
                    if (listIterator$64.$$===undefined){
                        initTypeProto(listIterator$64,'ceylon.language::List.iterator.listIterator',Basic,$init$Iterator());
                    }
                    return listIterator$64;
                }
                $init$listIterator$64();
                (function($$listIterator$64){
                    $$listIterator$64.getIndex$65=function getIndex$65(){
                        return this.index$65;
                    };
                    $$listIterator$64.setIndex$65=function setIndex$65(index$66){
                        return this.index$65=index$66;
                    };
                    $$listIterator$64.next=function next(){
                        var $$listIterator$64=this;
                        if(($$listIterator$64.getIndex$65().compare((opt$67=$$list.getLastIndex(),opt$67!==null?opt$67:(-(1))))!==getLarger())){
                            var elem$68=$$list.get((oldindex$69=$$listIterator$64.getIndex$65(),$$listIterator$64.setIndex$65(oldindex$69.getSuccessor()),oldindex$69));
                            var oldindex$69;
                            //assert at List.ceylon (59:20-59:40)
                            var elem$70;
                            if (!((elem$70=elem$68)!==null)) { throw Exception('Assertion failed: \'exists elem\' at List.ceylon (59:27-59:39)'); }
                            return elem$70;
                        }else {
                            return getFinished();
                        }
                        var opt$67;
                    };
                })(listIterator$64.$$.prototype);
                var listIterator$71=listIterator$64(new listIterator$64.$$);
                var getListIterator$71=function(){
                    return listIterator$71;
                }
                return getListIterator$71();
            };$$list.equals=function equals(that$72){
                var $$list=this;
                var that$73;
                if(isOfType((that$73=that$72),{t:List,a:[{t:Anything}]})){
                    if(that$73.getSize().equals($$list.getSize())){
                        var it$74 = Range((0),$$list.getSize().minus((1)),[{t:Integer}]).getIterator();
                        var i$75;while ((i$75=it$74.next())!==getFinished()){
                            var x$76=$$list.get(i$75);
                            var y$77=that$73.get(i$75);
                            var x$78;
                            if((x$78=x$76)!==null){
                                var y$79;
                                if((y$79=y$77)!==null){
                                    if((!x$78.equals(y$79))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$80;
                                if((y$80=y$77)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$75){
                            return true;
                        }
                    }
                }
                return false;
            };$$list.getHash=function getHash(){
                var $$list=this;
                var hash$81=(1);
                var setHash$81=function(hash$82){return hash$81=hash$82;};
                var it$83 = $$list.getIterator();
                var elem$84;while ((elem$84=it$83.next())!==getFinished()){
                    (hash$81=hash$81.times((31)));
                    var elem$85;
                    if((elem$85=elem$84)!==null){
                        (hash$81=hash$81.plus(elem$85.getHash()));
                    }
                }
                return hash$81;
            };$$list.getFirst=function getFirst(){
                var $$list=this;
                return $$list.get((0));
            };
            $$list.getLast=function getLast(){
                var $$list=this;
                var i$86;
                if((i$86=$$list.getLastIndex())!==null){
                    return $$list.get(i$86);
                }
                return null;
            };$$list.withLeading=function withLeading(element$87,$$$mptypes){
                var $$list=this;
                var sb$88=SequenceBuilder([{ t:'u', l:[$$list.$$targs$$[0],$$$mptypes[0]]}]);
                sb$88.append(element$87);
                if((!$$list.getEmpty())){
                    sb$88.appendAll($$list.getSequence());
                }
                //assert at List.ceylon (160:8-160:41)
                var seq$89;
                if (!(nonempty((seq$89=sb$88.getSequence())))) { throw Exception('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (160:15-160:40)'); }
                return seq$89;
            };$$list.withTrailing=function withTrailing(element$90,$$$mptypes){
                var $$list=this;
                var sb$91=SequenceBuilder([{ t:'u', l:[$$list.$$targs$$[0],$$$mptypes[0]]}]);
                if((!$$list.getEmpty())){
                    sb$91.appendAll($$list.getSequence());
                }
                sb$91.append(element$90);
                //assert at List.ceylon (175:8-175:41)
                var seq$92;
                if (!(nonempty((seq$92=sb$91.getSequence())))) { throw Exception('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (175:15-175:40)'); }
                return seq$92;
            };
        })(List.$$.prototype);
    }
    return List;
}
exports.$init$List=$init$List;
$init$List();
var opt$61,opt$63;
function ChainedIterator(first$93, second$94, $$targs$$,$$chainedIterator){
    $init$ChainedIterator();
    if ($$chainedIterator===undefined)$$chainedIterator=new ChainedIterator.$$;
    $$chainedIterator.$$targs$$=$$targs$$;
    $$chainedIterator.second$94=second$94;
    Iterator($$chainedIterator);
    $$chainedIterator.iter$95=first$93.getIterator();
    $$chainedIterator.more$96=true;
    return $$chainedIterator;
}
exports.ChainedIterator=ChainedIterator;
function $init$ChainedIterator(){
    if (ChainedIterator.$$===undefined){
        initTypeProto(ChainedIterator,'ceylon.language::ChainedIterator',Basic,$init$Iterator());
        (function($$chainedIterator){
            $$chainedIterator.getIter$95=function getIter$95(){
                return this.iter$95;
            };
            $$chainedIterator.setIter$95=function setIter$95(iter$97){
                return this.iter$95=iter$97;
            };
            $$chainedIterator.getMore$96=function getMore$96(){
                return this.more$96;
            };
            $$chainedIterator.setMore$96=function setMore$96(more$98){
                return this.more$96=more$98;
            };
            $$chainedIterator.next=function next(){
                var $$chainedIterator=this;
                var e$99=$$chainedIterator.getIter$95().next();
                var setE$99=function(e$100){return e$99=e$100;};
                var f$101;
                if(isOfType((f$101=e$99),{t:Finished})){
                    if($$chainedIterator.getMore$96()){
                        $$chainedIterator.setIter$95($$chainedIterator.second$94.getIterator());
                        $$chainedIterator.setMore$96(false);
                        e$99=$$chainedIterator.getIter$95().next();
                    }
                }
                return e$99;
            };
        })(ChainedIterator.$$.prototype);
    }
    return ChainedIterator;
}
exports.$init$ChainedIterator=$init$ChainedIterator;
$init$ChainedIterator();
function Entry(key$102, item$103, $$targs$$,$$entry){
    $init$Entry();
    if ($$entry===undefined)$$entry=new Entry.$$;
    $$entry.$$targs$$=$$targs$$;
    Object$($$entry);
    $$entry.key$104=key$102;
    $$entry.item$105=item$103;
    return $$entry;
}
exports.Entry=Entry;
function $init$Entry(){
    if (Entry.$$===undefined){
        initTypeProto(Entry,'ceylon.language::Entry',Object$);
        (function($$entry){
            $$entry.getKey=function getKey(){
                return this.key$104;
            };
            $$entry.getItem=function getItem(){
                return this.item$105;
            };
            $$entry.equals=function equals(that$106){
                var $$entry=this;
                var that$107;
                if(isOfType((that$107=that$106),{t:Entry,a:[{t:Object$},{t:Object$}]})){
                    return ($$entry.getKey().equals(that$107.getKey())&&$$entry.getItem().equals(that$107.getItem()));
                }else {
                    return false;
                }
            };$$entry.getHash=function getHash(){
                var $$entry=this;
                return (31).plus($$entry.getKey().getHash()).times((31)).plus($$entry.getItem().getHash());
            };
            $$entry.getString=function getString(){
                var $$entry=this;
                return $$entry.getKey().getString().plus(String$("->",2)).plus($$entry.getItem().getString());
            };
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
function Exception(description$108, cause$109, $$exception){
    $init$Exception();
    if ($$exception===undefined)$$exception=new Exception.$$;
    if(description$108===undefined){description$108=null;}
    if(cause$109===undefined){cause$109=null;}
    $$exception.cause$110=cause$109;
    $$exception.description$111=description$108;
    return $$exception;
}
exports.Exception=Exception;
function $init$Exception(){
    if (Exception.$$===undefined){
        initTypeProto(Exception,'ceylon.language::Exception',Basic);
        (function($$exception){
            $$exception.getCause=function getCause(){
                return this.cause$110;
            };
            $$exception.getDescription$111=function getDescription$111(){
                return this.description$111;
            };
            $$exception.getMessage=function getMessage(){
                var $$exception=this;
                return (opt$112=(opt$113=$$exception.getDescription$111(),opt$113!==null?opt$113:(opt$114=$$exception.getCause(),opt$114!==null?opt$114.getMessage():null)),opt$112!==null?opt$112:String$("",0));
            };
            $$exception.getString=function getString(){
                var $$exception=this;
                return className($$exception).plus(StringBuilder().appendAll([String$(" \"",2),$$exception.getMessage().getString(),String$("\"",1)]).getString());
            };
        })(Exception.$$.prototype);
    }
    return Exception;
}
exports.$init$Exception=$init$Exception;
$init$Exception();
var opt$112,opt$113,opt$114;
function RecursiveInitializationException($$recursiveInitializationException){
    $init$RecursiveInitializationException();
    if ($$recursiveInitializationException===undefined)$$recursiveInitializationException=new RecursiveInitializationException.$$;
    Exception(String$("Name could not be initialized due to recursive access during initialization",75),null,$$recursiveInitializationException);
    return $$recursiveInitializationException;
}
exports.RecursiveInitializationException=RecursiveInitializationException;
function $init$RecursiveInitializationException(){
    if (RecursiveInitializationException.$$===undefined){
        initTypeProto(RecursiveInitializationException,'ceylon.language::RecursiveInitializationException',Exception);
    }
    return RecursiveInitializationException;
}
exports.$init$RecursiveInitializationException=$init$RecursiveInitializationException;
$init$RecursiveInitializationException();
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
function Range(first$115, last$116, $$targs$$,$$range){
    $init$Range();
    if ($$range===undefined)$$range=new Range.$$;
    $$range.$$targs$$=$$targs$$;
    Object$($$range);
    Sequence($$range);
    Category($$range);
    $$range.first$117=first$115;
    $$range.last$118=last$116;
    return $$range;
}
exports.Range=Range;
function $init$Range(){
    if (Range.$$===undefined){
        initTypeProto(Range,'ceylon.language::Range',Object$,$init$Sequence(),$init$Category());
        (function($$range){
            $$range.getFirst=function getFirst(){
                return this.first$117;
            };
            $$range.getLast=function getLast(){
                return this.last$118;
            };
            $$range.getString=function getString(){
                var $$range=this;
                return $$range.getFirst().getString().plus(String$("..",2)).plus($$range.getLast().getString());
            };
            $$range.getDecreasing=function getDecreasing(){
                var $$range=this;
                return $$range.getLast().compare($$range.getFirst()).equals(getSmaller());
            };
            $$range.next$119=function (x$120){
                var $$range=this;
                return (opt$121=($$range.getDecreasing()?x$120.getPredecessor():null),opt$121!==null?opt$121:x$120.getSuccessor());
            };
            $$range.getSize=function getSize(){
                var $$range=this;
                var last$122;
                var first$123;
                if(isOfType((last$122=$$range.getLast()),{t:Enumerable,a:[{t:Anything}]})&&isOfType((first$123=$$range.getFirst()),{t:Enumerable,a:[{t:Anything}]})){
                    return last$122.getIntegerValue().minus(first$123.getIntegerValue()).getMagnitude().plus((1));
                }else {
                    var size$124=(1);
                    var setSize$124=function(size$125){return size$124=size$125;};
                    var current$126=$$range.getFirst();
                    var setCurrent$126=function(current$127){return current$126=current$127;};
                    while((!current$126.equals($$range.getLast()))){
                        (oldsize$128=size$124,size$124=oldsize$128.getSuccessor(),oldsize$128);
                        var oldsize$128;
                        current$126=$$range.next$119(current$126);
                    }
                    return size$124;
                }
            };$$range.getLastIndex=function getLastIndex(){
                var $$range=this;
                return $$range.getSize().minus((1));
            };
            $$range.getRest=function getRest(){
                var $$range=this;
                if($$range.getSize().equals((1))){
                    return empty;
                }
                var n$129=$$range.next$119($$range.getFirst());
                return (opt$130=(n$129.equals($$range.getLast())?empty:null),opt$130!==null?opt$130:Range(n$129,$$range.getLast(),[$$range.$$targs$$[0]]));
                var opt$130;
            };$$range.get=function get(n$131){
                var $$range=this;
                var index$132=(0);
                var setIndex$132=function(index$133){return index$132=index$133;};
                var x$134=$$range.getFirst();
                var setX$134=function(x$135){return x$134=x$135;};
                while(index$132.compare(n$131).equals(getSmaller())){
                    if(x$134.equals($$range.getLast())){
                        return null;
                    }else {
                        (index$132=index$132.getSuccessor());
                        x$134=$$range.next$119(x$134);
                    }
                }
                return x$134;
            };$$range.getIterator=function getIterator(){
                var $$range=this;
                function RangeIterator$136($$rangeIterator$136){
                    $init$RangeIterator$136();
                    if ($$rangeIterator$136===undefined)$$rangeIterator$136=new RangeIterator$136.$$;
                    $$rangeIterator$136.$$targs$$=[$$range.$$targs$$[0]];
                    Iterator($$rangeIterator$136);
                    $$rangeIterator$136.current$137=$$range.getFirst();
                    return $$rangeIterator$136;
                }
                function $init$RangeIterator$136(){
                    if (RangeIterator$136.$$===undefined){
                        initTypeProto(RangeIterator$136,'ceylon.language::Range.iterator.RangeIterator',Basic,$init$Iterator());
                        (function($$rangeIterator$136){
                            $$rangeIterator$136.getCurrent$137=function getCurrent$137(){
                                return this.current$137;
                            };
                            $$rangeIterator$136.setCurrent$137=function setCurrent$137(current$138){
                                return this.current$137=current$138;
                            };
                            $$rangeIterator$136.next=function next(){
                                var $$rangeIterator$136=this;
                                var result$139=$$rangeIterator$136.getCurrent$137();
                                var curr$140;
                                if(!isOfType((curr$140=$$rangeIterator$136.getCurrent$137()),{t:Finished})){
                                    if((opt$141=($$range.getDecreasing()?(curr$140.compare($$range.getLast())!==getLarger()):null),opt$141!==null?opt$141:(curr$140.compare($$range.getLast())!==getSmaller()))){
                                        $$rangeIterator$136.setCurrent$137(getFinished());
                                    }else {
                                        $$rangeIterator$136.setCurrent$137($$range.next$119(curr$140));
                                    }
                                    var opt$141;
                                }
                                return result$139;
                            };$$rangeIterator$136.getString=function getString(){
                                var $$rangeIterator$136=this;
                                return String$("RangeIterator",13);
                            };
                        })(RangeIterator$136.$$.prototype);
                    }
                    return RangeIterator$136;
                }
                $init$RangeIterator$136();
                return RangeIterator$136();
            };$$range.contains=function contains(element$142){
                var $$range=this;
                var it$143 = $$range.getIterator();
                var e$144;while ((e$144=it$143.next())!==getFinished()){
                    if(e$144.equals(element$142)){
                        return true;
                    }
                }
                if (getFinished() === e$144){
                    return false;
                }
            };$$range.count=function count(selecting$145){
                var $$range=this;
                var e$146=$$range.getFirst();
                var setE$146=function(e$147){return e$146=e$147;};
                var c$148=(0);
                var setC$148=function(c$149){return c$148=c$149;};
                while($$range.includes(e$146)){
                    if(selecting$145(e$146)){
                        (oldc$150=c$148,c$148=oldc$150.getSuccessor(),oldc$150);
                        var oldc$150;
                    }
                    e$146=$$range.next$119(e$146);
                }
                return c$148;
            };$$range.includes=function (x$151){
                var $$range=this;
                return (opt$152=($$range.getDecreasing()?((x$151.compare($$range.getFirst())!==getLarger())&&(x$151.compare($$range.getLast())!==getSmaller())):null),opt$152!==null?opt$152:((x$151.compare($$range.getFirst())!==getSmaller())&&(x$151.compare($$range.getLast())!==getLarger())));
            };
            $$range.getClone=function getClone(){
                var $$range=this;
                return $$range;
            };
            $$range.segment=function segment(from$153,length$154){
                var $$range=this;
                if(((length$154.compare((0))!==getLarger())||from$153.compare($$range.getLastIndex()).equals(getLarger()))){
                    return empty;
                }
                var x$155=$$range.getFirst();
                var setX$155=function(x$156){return x$155=x$156;};
                var i$157=(0);
                var setI$157=function(i$158){return i$157=i$158;};
                while((oldi$159=i$157,i$157=oldi$159.getSuccessor(),oldi$159).compare(from$153).equals(getSmaller())){
                    x$155=$$range.next$119(x$155);
                }
                var oldi$159;
                var y$160=x$155;
                var setY$160=function(y$161){return y$160=y$161;};
                var j$162=(1);
                var setJ$162=function(j$163){return j$162=j$163;};
                while(((oldj$164=j$162,j$162=oldj$164.getSuccessor(),oldj$164).compare(length$154).equals(getSmaller())&&y$160.compare($$range.getLast()).equals(getSmaller()))){
                    y$160=$$range.next$119(y$160);
                }
                var oldj$164;
                return Range(x$155,y$160,[$$range.$$targs$$[0]]);
            };$$range.span=function span(from$165,to$166){
                var $$range=this;
                var toIndex$167=to$166;
                var setToIndex$167=function(toIndex$168){return toIndex$167=toIndex$168;};
                var fromIndex$169=from$165;
                var setFromIndex$169=function(fromIndex$170){return fromIndex$169=fromIndex$170;};
                if(toIndex$167.compare((0)).equals(getSmaller())){
                    if(fromIndex$169.compare((0)).equals(getSmaller())){
                        return empty;
                    }
                    toIndex$167=(0);
                }else {
                    if(toIndex$167.compare($$range.getLastIndex()).equals(getLarger())){
                        if(fromIndex$169.compare($$range.getLastIndex()).equals(getLarger())){
                            return empty;
                        }
                        toIndex$167=$$range.getLastIndex();
                    }
                }
                if(fromIndex$169.compare((0)).equals(getSmaller())){
                    fromIndex$169=(0);
                }else {
                    if(fromIndex$169.compare($$range.getLastIndex()).equals(getLarger())){
                        fromIndex$169=$$range.getLastIndex();
                    }
                }
                var x$171=$$range.getFirst();
                var setX$171=function(x$172){return x$171=x$172;};
                var i$173=(0);
                var setI$173=function(i$174){return i$173=i$174;};
                while((oldi$175=i$173,i$173=oldi$175.getSuccessor(),oldi$175).compare(fromIndex$169).equals(getSmaller())){
                    x$171=$$range.next$119(x$171);
                }
                var oldi$175;
                var y$176=$$range.getFirst();
                var setY$176=function(y$177){return y$176=y$177;};
                var j$178=(0);
                var setJ$178=function(j$179){return j$178=j$179;};
                while((oldj$180=j$178,j$178=oldj$180.getSuccessor(),oldj$180).compare(toIndex$167).equals(getSmaller())){
                    y$176=$$range.next$119(y$176);
                }
                var oldj$180;
                return Range(x$171,y$176,[$$range.$$targs$$[0]]);
            };$$range.spanTo=function spanTo(to$181){
                var $$range=this;
                return (opt$182=(to$181.compare((0)).equals(getSmaller())?empty:null),opt$182!==null?opt$182:$$range.span((0),to$181));
                var opt$182;
            };$$range.spanFrom=function spanFrom(from$183){
                var $$range=this;
                return $$range.span(from$183,$$range.getSize());
            };$$range.getReversed=function getReversed(){
                var $$range=this;
                return Range($$range.getLast(),$$range.getFirst(),[$$range.$$targs$$[0]]);
            };
            $$range.skipping=function skipping(skip$184){
                var $$range=this;
                var x$185=(0);
                var setX$185=function(x$186){return x$185=x$186;};
                var e$187=$$range.getFirst();
                var setE$187=function(e$188){return e$187=e$188;};
                while((oldx$189=x$185,x$185=oldx$189.getSuccessor(),oldx$189).compare(skip$184).equals(getSmaller())){
                    e$187=$$range.next$119(e$187);
                }
                var oldx$189;
                return (opt$190=($$range.includes(e$187)?Range(e$187,$$range.getLast(),[$$range.$$targs$$[0]]):null),opt$190!==null?opt$190:empty);
                var opt$190;
            };$$range.taking=function taking(take$191){
                var $$range=this;
                if(take$191.equals((0))){
                    return empty;
                }
                var x$192=(0);
                var setX$192=function(x$193){return x$192=x$193;};
                var e$194=$$range.getFirst();
                var setE$194=function(e$195){return e$194=e$195;};
                while((x$192=x$192.getSuccessor()).compare(take$191).equals(getSmaller())){
                    e$194=$$range.next$119(e$194);
                }
                return (opt$196=($$range.includes(e$194)?Range($$range.getFirst(),e$194,[$$range.$$targs$$[0]]):null),opt$196!==null?opt$196:$$range);
                var opt$196;
            };$$range.getCoalesced=function getCoalesced(){
                var $$range=this;
                return $$range;
            };
            $$range.getSequence=function getSequence(){
                var $$range=this;
                return $$range;
            };
        })(Range.$$.prototype);
    }
    return Range;
}
exports.$init$Range=$init$Range;
$init$Range();
var opt$121,opt$152;
function Set($$set){
    Collection($$set);
    Cloneable($$set);
}
exports.Set=Set;
function $init$Set(){
    if (Set.$$===undefined){
        initTypeProto(Set,'ceylon.language::Set',$init$Collection(),$init$Cloneable());
        (function($$set){
            $$set.superset=function superset(set$197){
                var $$set=this;
                var it$198 = set$197.getIterator();
                var element$199;while ((element$199=it$198.next())!==getFinished()){
                    if((!$$set.contains(element$199))){
                        return false;
                    }
                }
                if (getFinished() === element$199){
                    return true;
                }
            };$$set.subset=function subset(set$200){
                var $$set=this;
                var it$201 = $$set.getIterator();
                var element$202;while ((element$202=it$201.next())!==getFinished()){
                    if((!set$200.contains(element$202))){
                        return false;
                    }
                }
                if (getFinished() === element$202){
                    return true;
                }
            };$$set.equals=function equals(that$203){
                var $$set=this;
                var that$204;
                if(isOfType((that$204=that$203),{t:Set,a:[{t:Object$}]})&&that$204.getSize().equals($$set.getSize())){
                    var it$205 = $$set.getIterator();
                    var element$206;while ((element$206=it$205.next())!==getFinished()){
                        if((!that$204.contains(element$206))){
                            return false;
                        }
                    }
                    if (getFinished() === element$206){
                        return true;
                    }
                }
                return false;
            };$$set.getHash=function getHash(){
                var $$set=this;
                var hashCode$207=(1);
                var setHashCode$207=function(hashCode$208){return hashCode$207=hashCode$208;};
                var it$209 = $$set.getIterator();
                var elem$210;while ((elem$210=it$209.next())!==getFinished()){
                    (hashCode$207=hashCode$207.times((31)));
                    (hashCode$207=hashCode$207.plus(elem$210.getHash()));
                }
                return hashCode$207;
            };
        })(Set.$$.prototype);
    }
    return Set;
}
exports.$init$Set=$init$Set;
$init$Set();
function LazyList(elems$211, $$targs$$,$$lazyList){
    $init$LazyList();
    if ($$lazyList===undefined)$$lazyList=new LazyList.$$;
    $$lazyList.$$targs$$=$$targs$$;
    $$lazyList.elems$211=elems$211;
    List($$lazyList);
    return $$lazyList;
}
exports.LazyList=LazyList;
function $init$LazyList(){
    if (LazyList.$$===undefined){
        initTypeProto(LazyList,'ceylon.language::LazyList',Basic,$init$List());
        (function($$lazyList){
            $$lazyList.getLastIndex=function getLastIndex(){
                var $$lazyList=this;
                var size$212=$$lazyList.elems$211.getSize();
                return (opt$213=(size$212.compare((0)).equals(getLarger())?size$212.minus((1)):null),opt$213!==null?opt$213:null);
                var opt$213;
            };$$lazyList.get=function get(index$214){
                var $$lazyList=this;
                if(index$214.equals((0))){
                    return $$lazyList.elems$211.getFirst();
                }else {
                    return $$lazyList.elems$211.skipping(index$214).getFirst();
                }
            };$$lazyList.getIterator=function getIterator(){
                var $$lazyList=this;
                return $$lazyList.elems$211.getIterator();
            };
            $$lazyList.getReversed=function getReversed(){
                var $$lazyList=this;
                return $$lazyList.elems$211.getSequence().getReversed();
            };
            $$lazyList.getClone=function getClone(){
                var $$lazyList=this;
                return $$lazyList;
            };
            $$lazyList.span=function span(from$215,to$216){
                var $$lazyList=this;
                if((to$216.compare((0)).equals(getSmaller())&&from$215.compare((0)).equals(getSmaller()))){
                    return empty;
                }
                var toIndex$217=largest(to$216,(0),[{t:Integer}]);
                var fromIndex$218=largest(from$215,(0),[{t:Integer}]);
                if((toIndex$217.compare(fromIndex$218)!==getSmaller())){
                    var els$219=(opt$220=(fromIndex$218.compare((0)).equals(getLarger())?$$lazyList.elems$211.skipping(fromIndex$218):null),opt$220!==null?opt$220:$$lazyList.elems$211);
                    var opt$220;
                    return LazyList(els$219.taking(toIndex$217.minus(fromIndex$218).plus((1))),[$$lazyList.$$targs$$[0]]);
                }else {
                    var seq$221=(opt$222=(toIndex$217.compare((0)).equals(getLarger())?$$lazyList.elems$211.skipping(toIndex$217):null),opt$222!==null?opt$222:$$lazyList.elems$211);
                    var opt$222;
                    return seq$221.taking(fromIndex$218.minus(toIndex$217).plus((1))).getSequence().getReversed();
                }
            };$$lazyList.spanTo=function (to$223){
                var $$lazyList=this;
                return (opt$224=(to$223.compare((0)).equals(getSmaller())?empty:null),opt$224!==null?opt$224:LazyList($$lazyList.elems$211.taking(to$223.plus((1))),[$$lazyList.$$targs$$[0]]));
            };
            $$lazyList.spanFrom=function (from$225){
                var $$lazyList=this;
                return (opt$226=(from$225.compare((0)).equals(getLarger())?LazyList($$lazyList.elems$211.skipping(from$225),[$$lazyList.$$targs$$[0]]):null),opt$226!==null?opt$226:$$lazyList);
            };
            $$lazyList.segment=function segment(from$227,length$228){
                var $$lazyList=this;
                if(length$228.compare((0)).equals(getLarger())){
                    var els$229=(opt$230=(from$227.compare((0)).equals(getLarger())?$$lazyList.elems$211.skipping(from$227):null),opt$230!==null?opt$230:$$lazyList.elems$211);
                    var opt$230;
                    return LazyList(els$229.taking(length$228),[$$lazyList.$$targs$$[0]]);
                }else {
                    return empty;
                }
            };$$lazyList.equals=function equals(that$231){
                var $$lazyList=this;
                var that$232;
                if(isOfType((that$232=that$231),{t:List,a:[{t:Anything}]})){
                    var size$233=$$lazyList.elems$211.getSize();
                    if(that$232.getSize().equals(size$233)){
                        var it$234 = Range((0),size$233.minus((1)),[{t:Integer}]).getIterator();
                        var i$235;while ((i$235=it$234.next())!==getFinished()){
                            var x$236=$$lazyList.get(i$235);
                            var y$237=that$232.get(i$235);
                            var x$238;
                            if((x$238=x$236)!==null){
                                var y$239;
                                if((y$239=y$237)!==null){
                                    if((!x$238.equals(y$239))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$240;
                                if((y$240=y$237)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$235){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyList.getHash=function getHash(){
                var $$lazyList=this;
                var hash$241=(1);
                var setHash$241=function(hash$242){return hash$241=hash$242;};
                var it$243 = $$lazyList.elems$211.getIterator();
                var elem$244;while ((elem$244=it$243.next())!==getFinished()){
                    (hash$241=hash$241.times((31)));
                    var elem$245;
                    if((elem$245=elem$244)!==null){
                        (hash$241=hash$241.plus(elem$245.getHash()));
                    }
                }
                return hash$241;
            };$$lazyList.findLast=function (selecting$246){
                var $$lazyList=this;
                return $$lazyList.elems$211.findLast(selecting$246);
            };
            $$lazyList.getFirst=function getFirst(){
                var $$lazyList=this;
                return $$lazyList.elems$211.getFirst();
            };
            $$lazyList.getLast=function getLast(){
                var $$lazyList=this;
                return $$lazyList.elems$211.getLast();
            };
        })(LazyList.$$.prototype);
    }
    return LazyList;
}
exports.$init$LazyList=$init$LazyList;
$init$LazyList();
var opt$224,opt$226;
function LazySet(elems$247, $$targs$$,$$lazySet){
    $init$LazySet();
    if ($$lazySet===undefined)$$lazySet=new LazySet.$$;
    $$lazySet.$$targs$$=$$targs$$;
    $$lazySet.elems$247=elems$247;
    Set($$lazySet);
    return $$lazySet;
}
exports.LazySet=LazySet;
function $init$LazySet(){
    if (LazySet.$$===undefined){
        initTypeProto(LazySet,'ceylon.language::LazySet',Basic,$init$Set());
        (function($$lazySet){
            $$lazySet.getClone=function getClone(){
                var $$lazySet=this;
                return $$lazySet;
            };
            $$lazySet.getSize=function getSize(){
                var $$lazySet=this;
                var c$248=(0);
                var setC$248=function(c$249){return c$248=c$249;};
                var sorted$250=$$lazySet.elems$247.$sort(byIncreasing(function (e$251){
                    var $$lazySet=this;
                    return e$251.getHash();
                },[$$lazySet.$$targs$$[0],{t:Integer}]));
                var l$252;
                if((l$252=sorted$250.getFirst())!==null){
                    c$248=(1);
                    var last$253=l$252;
                    var setLast$253=function(last$254){return last$253=last$254;};
                    var it$255 = sorted$250.getRest().getIterator();
                    var e$256;while ((e$256=it$255.next())!==getFinished()){
                        if((!e$256.equals(last$253))){
                            (oldc$257=c$248,c$248=oldc$257.getSuccessor(),oldc$257);
                            var oldc$257;
                            last$253=e$256;
                        }
                    }
                }
                return c$248;
            };$$lazySet.getIterator=function getIterator(){
                var $$lazySet=this;
                return $$lazySet.elems$247.getIterator();
            };
            $$lazySet.union=function (set$258,$$$mptypes){
                var $$lazySet=this;
                return LazySet($$lazySet.elems$247.chain(set$258,[$$$mptypes[0]]),[{ t:'u', l:[$$lazySet.$$targs$$[0],$$$mptypes[0]]}]);
            };
            $$lazySet.intersection=function (set$259,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var e$262;
                    var it$260=set$259.getIterator();
                    var e$261=getFinished();
                    var e$262;
                    var next$e$261=function(){
                        while((e$261=it$260.next())!==getFinished()){
                            if(isOfType((e$262=e$261),$$lazySet.$$targs$$[0])&&$$lazySet.contains(e$262)){
                                return e$261;
                            }
                        }
                        return getFinished();
                    }
                    next$e$261();
                    return function(){
                        if(e$261!==getFinished()){
                            var tmpvar$263=e$262;
                            next$e$261();
                            return tmpvar$263;
                        }
                        return getFinished();
                    }
                },[{ t:'i', l:[$$$mptypes[0],$$lazySet.$$targs$$[0]]},{t:Null}]),[{ t:'i', l:[$$$mptypes[0],$$lazySet.$$targs$$[0]]}]);
            };
            $$lazySet.exclusiveUnion=function exclusiveUnion(other$264,$$$mptypes){
                var $$lazySet=this;
                var hereNotThere$265=Comprehension(function(){
                    var it$266=$$lazySet.elems$247.getIterator();
                    var e$267=getFinished();
                    var next$e$267=function(){
                        while((e$267=it$266.next())!==getFinished()){
                            if((!other$264.contains(e$267))){
                                return e$267;
                            }
                        }
                        return getFinished();
                    }
                    next$e$267();
                    return function(){
                        if(e$267!==getFinished()){
                            var tmpvar$268=e$267;
                            next$e$267();
                            return tmpvar$268;
                        }
                        return getFinished();
                    }
                },[$$lazySet.$$targs$$[0],{t:Null}]);
                var thereNotHere$269=Comprehension(function(){
                    var it$270=other$264.getIterator();
                    var e$271=getFinished();
                    var next$e$271=function(){
                        while((e$271=it$270.next())!==getFinished()){
                            if((!$$lazySet.contains(e$271))){
                                return e$271;
                            }
                        }
                        return getFinished();
                    }
                    next$e$271();
                    return function(){
                        if(e$271!==getFinished()){
                            var tmpvar$272=e$271;
                            next$e$271();
                            return tmpvar$272;
                        }
                        return getFinished();
                    }
                },[$$$mptypes[0],{t:Null}]);
                return LazySet(hereNotThere$265.chain(thereNotHere$269,[$$$mptypes[0]]),[{ t:'u', l:[$$lazySet.$$targs$$[0],$$$mptypes[0]]}]);
            };$$lazySet.complement=function (set$273,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var it$274=$$lazySet.getIterator();
                    var e$275=getFinished();
                    var next$e$275=function(){
                        while((e$275=it$274.next())!==getFinished()){
                            if((!set$273.contains(e$275))){
                                return e$275;
                            }
                        }
                        return getFinished();
                    }
                    next$e$275();
                    return function(){
                        if(e$275!==getFinished()){
                            var tmpvar$276=e$275;
                            next$e$275();
                            return tmpvar$276;
                        }
                        return getFinished();
                    }
                },[$$lazySet.$$targs$$[0],{t:Null}]),[$$lazySet.$$targs$$[0]]);
            };
            $$lazySet.equals=function equals(that$277){
                var $$lazySet=this;
                var that$278;
                if(isOfType((that$278=that$277),{t:Set,a:[{t:Object$}]})){
                    if(that$278.getSize().equals($$lazySet.getSize())){
                        var it$279 = $$lazySet.elems$247.getIterator();
                        var element$280;while ((element$280=it$279.next())!==getFinished()){
                            if((!that$278.contains(element$280))){
                                return false;
                            }
                        }
                        if (getFinished() === element$280){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazySet.getHash=function getHash(){
                var $$lazySet=this;
                var hashCode$281=(1);
                var setHashCode$281=function(hashCode$282){return hashCode$281=hashCode$282;};
                var it$283 = $$lazySet.elems$247.getIterator();
                var elem$284;while ((elem$284=it$283.next())!==getFinished()){
                    (hashCode$281=hashCode$281.times((31)));
                    (hashCode$281=hashCode$281.plus(elem$284.getHash()));
                }
                return hashCode$281;
            };
        })(LazySet.$$.prototype);
    }
    return LazySet;
}
exports.$init$LazySet=$init$LazySet;
$init$LazySet();
function LazyMap(entries$285, $$targs$$,$$lazyMap){
    $init$LazyMap();
    if ($$lazyMap===undefined)$$lazyMap=new LazyMap.$$;
    $$lazyMap.$$targs$$=$$targs$$;
    $$lazyMap.entries$285=entries$285;
    Map($$lazyMap);
    return $$lazyMap;
}
exports.LazyMap=LazyMap;
function $init$LazyMap(){
    if (LazyMap.$$===undefined){
        initTypeProto(LazyMap,'ceylon.language::LazyMap',Basic,$init$Map());
        (function($$lazyMap){
            $$lazyMap.getFirst=function getFirst(){
                var $$lazyMap=this;
                return $$lazyMap.entries$285.getFirst();
            };
            $$lazyMap.getLast=function getLast(){
                var $$lazyMap=this;
                return $$lazyMap.entries$285.getLast();
            };
            $$lazyMap.getClone=function getClone(){
                var $$lazyMap=this;
                return $$lazyMap;
            };
            $$lazyMap.getSize=function getSize(){
                var $$lazyMap=this;
                return $$lazyMap.entries$285.getSize();
            };
            $$lazyMap.get=function (key$286){
                var $$lazyMap=this;
                return (opt$287=$$lazyMap.entries$285.find(function (e$288){
                    var $$lazyMap=this;
                    return e$288.getKey().equals(key$286);
                }),opt$287!==null?opt$287.getItem():null);
            };
            $$lazyMap.getIterator=function getIterator(){
                var $$lazyMap=this;
                return $$lazyMap.entries$285.getIterator();
            };
            $$lazyMap.equals=function equals(that$289){
                var $$lazyMap=this;
                var that$290;
                if(isOfType((that$290=that$289),{t:Map,a:[{t:Object$},{t:Object$}]})){
                    if(that$290.getSize().equals($$lazyMap.getSize())){
                        var it$291 = $$lazyMap.getIterator();
                        var entry$292;while ((entry$292=it$291.next())!==getFinished()){
                            var item$293;
                            if((item$293=that$290.get(entry$292.getKey()))!==null){
                                if(item$293.equals(entry$292.getItem())){
                                    continue;
                                }
                            }
                            return false;
                        }
                        if (getFinished() === entry$292){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyMap.getHash=function getHash(){
                var $$lazyMap=this;
                var hashCode$294=(1);
                var setHashCode$294=function(hashCode$295){return hashCode$294=hashCode$295;};
                var it$296 = $$lazyMap.entries$285.getIterator();
                var elem$297;while ((elem$297=it$296.next())!==getFinished()){
                    (hashCode$294=hashCode$294.times((31)));
                    (hashCode$294=hashCode$294.plus(elem$297.getHash()));
                }
                return hashCode$294;
            };
        })(LazyMap.$$.prototype);
    }
    return LazyMap;
}
exports.$init$LazyMap=$init$LazyMap;
$init$LazyMap();
var opt$287;
function Singleton(element$298, $$targs$$,$$singleton){
    $init$Singleton();
    if ($$singleton===undefined)$$singleton=new Singleton.$$;
    $$singleton.$$targs$$=$$targs$$;
    $$singleton.element$298=element$298;
    Object$($$singleton);
    Sequence($$singleton);
    return $$singleton;
}
exports.Singleton=Singleton;
function $init$Singleton(){
    if (Singleton.$$===undefined){
        initTypeProto(Singleton,'ceylon.language::Singleton',Object$,$init$Sequence());
        (function($$singleton){
            $$singleton.getLastIndex=function getLastIndex(){
                var $$singleton=this;
                return (0);
            };
            $$singleton.getSize=function getSize(){
                var $$singleton=this;
                return (1);
            };
            $$singleton.getFirst=function getFirst(){
                var $$singleton=this;
                return $$singleton.element$298;
            };
            $$singleton.getLast=function getLast(){
                var $$singleton=this;
                return $$singleton.element$298;
            };
            $$singleton.getRest=function getRest(){
                var $$singleton=this;
                return empty;
            };
            $$singleton.get=function get(index$299){
                var $$singleton=this;
                if(index$299.equals((0))){
                    return $$singleton.element$298;
                }else {
                    return null;
                }
            };$$singleton.getClone=function getClone(){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.getIterator=function getIterator(){
                var $$singleton=this;
                function SingletonIterator$300($$singletonIterator$300){
                    $init$SingletonIterator$300();
                    if ($$singletonIterator$300===undefined)$$singletonIterator$300=new SingletonIterator$300.$$;
                    $$singletonIterator$300.$$targs$$=[$$singleton.$$targs$$[0]];
                    Iterator($$singletonIterator$300);
                    $$singletonIterator$300.done$301=false;
                    return $$singletonIterator$300;
                }
                function $init$SingletonIterator$300(){
                    if (SingletonIterator$300.$$===undefined){
                        initTypeProto(SingletonIterator$300,'ceylon.language::Singleton.iterator.SingletonIterator',Basic,$init$Iterator());
                        (function($$singletonIterator$300){
                            $$singletonIterator$300.getDone$301=function getDone$301(){
                                return this.done$301;
                            };
                            $$singletonIterator$300.setDone$301=function setDone$301(done$302){
                                return this.done$301=done$302;
                            };
                            $$singletonIterator$300.next=function next(){
                                var $$singletonIterator$300=this;
                                if($$singletonIterator$300.getDone$301()){
                                    return getFinished();
                                }else {
                                    $$singletonIterator$300.setDone$301(true);
                                    return $$singleton.element$298;
                                }
                            };$$singletonIterator$300.getString=function getString(){
                                var $$singletonIterator$300=this;
                                return String$("SingletonIterator",17);
                            };
                        })(SingletonIterator$300.$$.prototype);
                    }
                    return SingletonIterator$300;
                }
                $init$SingletonIterator$300();
                return SingletonIterator$300();
            };$$singleton.getString=function getString(){
                var $$singleton=this;
                return StringBuilder().appendAll([String$("[ ",2),$$singleton.getFirst().getString().getString(),String$(" ]",2)]).getString();
            };
            $$singleton.segment=function (from$303,length$304){
                var $$singleton=this;
                return (opt$305=(((from$303.compare((0))!==getLarger())&&from$303.plus(length$304).compare((0)).equals(getLarger()))?$$singleton:null),opt$305!==null?opt$305:empty);
            };
            $$singleton.span=function (from$306,to$307){
                var $$singleton=this;
                return (opt$308=((((from$306.compare((0))!==getLarger())&&(to$307.compare((0))!==getSmaller()))||((from$306.compare((0))!==getSmaller())&&(to$307.compare((0))!==getLarger())))?$$singleton:null),opt$308!==null?opt$308:empty);
            };
            $$singleton.spanTo=function (to$309){
                var $$singleton=this;
                return (opt$310=(to$309.compare((0)).equals(getSmaller())?empty:null),opt$310!==null?opt$310:$$singleton);
            };
            $$singleton.spanFrom=function (from$311){
                var $$singleton=this;
                return (opt$312=(from$311.compare((0)).equals(getLarger())?empty:null),opt$312!==null?opt$312:$$singleton);
            };
            $$singleton.getReversed=function getReversed(){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.equals=function equals(that$313){
                var $$singleton=this;
                var that$314;
                if(isOfType((that$314=that$313),{t:List,a:[{t:Anything}]})){
                    if(that$314.getSize().equals((1))){
                        var elem$315;
                        if((elem$315=that$314.getFirst())!==null){
                            return elem$315.equals($$singleton.element$298);
                        }
                    }
                }
                return false;
            };$$singleton.getHash=function getHash(){
                var $$singleton=this;
                return (31).plus($$singleton.element$298.getHash());
            };
            $$singleton.contains=function (element$316){
                var $$singleton=this;
                return $$singleton.element$298.equals(element$316);
            };
            $$singleton.count=function (selecting$317){
                var $$singleton=this;
                return (opt$318=(selecting$317($$singleton.element$298)?(1):null),opt$318!==null?opt$318:(0));
            };
            $$singleton.$map=function (selecting$319,$$$mptypes){
                var $$singleton=this;
                return Tuple(selecting$319($$singleton.element$298),empty,[$$$mptypes[0],$$$mptypes[0],{t:Empty}]);
            };
            $$singleton.$filter=function (selecting$320){
                var $$singleton=this;
                return (opt$321=(selecting$320($$singleton.element$298)?$$singleton:null),opt$321!==null?opt$321:empty);
            };
            $$singleton.fold=function (initial$322,accumulating$323,$$$mptypes){
                var $$singleton=this;
                return accumulating$323(initial$322,$$singleton.element$298);
            };
            $$singleton.find=function (selecting$324){
                var $$singleton=this;
                return (selecting$324($$singleton.element$298)?$$singleton.element$298:null);
            };
            $$singleton.findLast=function (selecting$325){
                var $$singleton=this;
                return $$singleton.find(selecting$325);
            };
            $$singleton.$sort=function (comparing$326){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.any=function (selecting$327){
                var $$singleton=this;
                return selecting$327($$singleton.element$298);
            };
            $$singleton.$every=function (selecting$328){
                var $$singleton=this;
                return selecting$328($$singleton.element$298);
            };
            $$singleton.skipping=function (skip$329){
                var $$singleton=this;
                return (opt$330=(skip$329.compare((1)).equals(getSmaller())?$$singleton:null),opt$330!==null?opt$330:empty);
            };
            $$singleton.taking=function (take$331){
                var $$singleton=this;
                return (opt$332=(take$331.compare((0)).equals(getLarger())?$$singleton:null),opt$332!==null?opt$332:empty);
            };
            $$singleton.getCoalesced=function getCoalesced(){
                var $$singleton=this;
                return $$singleton;
            };
        })(Singleton.$$.prototype);
    }
    return Singleton;
}
exports.$init$Singleton=$init$Singleton;
$init$Singleton();
var opt$305,opt$308,opt$310,opt$312,opt$318,opt$321,opt$330,opt$332;
var byIncreasing=function (comparable$333,$$$mptypes){
    return function(x$334,y$335,$$$mptypes){{
        return comparable$333(x$334).compare(comparable$333(y$335));
    }
}
}
;
exports.byIncreasing=byIncreasing;
var coalesce=function (values$336,$$$mptypes){
    return values$336.getCoalesced();
};
exports.coalesce=coalesce;
var equalTo=function (val$337,$$$mptypes){
    return function(element$338,$$$mptypes){{
        return element$338.equals(val$337);
    }
}
}
;
exports.equalTo=equalTo;
var entries=function (elements$339,$$$mptypes){
    return elements$339.getIndexed();
};
exports.entries=entries;
function any(values$340){
    var it$341 = values$340.getIterator();
    var val$342;while ((val$342=it$341.next())!==getFinished()){
        if(val$342){
            return true;
        }
    }
    return false;
}
exports.any=any;
var byKey=function (comparing$343){
    return function(x$344,y$345,$$$mptypes){{
        return comparing$343(x$344.getKey(),y$345.getKey());
    }
}
}
;
exports.byKey=byKey;
function count(values$346){
    var count$347=(0);
    var setCount$347=function(count$348){return count$347=count$348;};
    var it$349 = values$346.getIterator();
    var val$350;while ((val$350=it$349.next())!==getFinished()){
        if(val$350){
            (oldcount$351=count$347,count$347=oldcount$351.getSuccessor(),oldcount$351);
            var oldcount$351;
        }
    }
    return count$347;
}
exports.count=count;
var byDecreasing=function (comparable$352,$$$mptypes){
    return function(x$353,y$354,$$$mptypes){{
        return comparable$352(y$354).compare(comparable$352(x$353));
    }
}
}
;
exports.byDecreasing=byDecreasing;
var byItem=function (comparing$355){
    return function(x$356,y$357,$$$mptypes){{
        return comparing$355(x$356.getItem(),y$357.getItem());
    }
}
}
;
exports.byItem=byItem;
var curry=function (f$358,$$$mptypes){
    return function(first$359,$$$mptypes){{
        return flatten(function (args$360){
            return unflatten(f$358,[$$$mptypes[0],{t:Tuple,a:[$$$mptypes[0],$$$mptypes[0],$$$mptypes[0]]}])(Tuple(first$359,args$360,[$$$mptypes[0],$$$mptypes[0],$$$mptypes[0]]));
        },[$$$mptypes[0],$$$mptypes[0]]);
    }
}
}
;
exports.curry=curry;
var uncurry=function (f$361,$$$mptypes){
return flatten(function (args$362){
    return unflatten(f$361(args$362.getFirst()),[$$$mptypes[0],$$$mptypes[0]])(args$362.getRest());
},[$$$mptypes[0],{t:Tuple,a:[$$$mptypes[0],$$$mptypes[0],$$$mptypes[0]]}]);
};
exports.uncurry=uncurry;
function emptyOrSingleton(element$363,$$$mptypes){
    var element$364;
    if((element$364=element$363)!==null){
        return Singleton(element$364,[$$$mptypes[0]]);
    }else {
        return empty;
    }
}
exports.emptyOrSingleton=emptyOrSingleton;
function zip(keys$365,items$366,$$$mptypes){
    var iter$367=items$366.getIterator();
    return Comprehension(function(){
        var item$370;
        var it$368=keys$365.getIterator();
        var key$369=getFinished();
        var item$370;
        var next$key$369=function(){
            while((key$369=it$368.next())!==getFinished()){
                if(!isOfType((item$370=iter$367.next()),{t:Finished})){
                    return key$369;
                }
            }
            return getFinished();
        }
        next$key$369();
        return function(){
            if(key$369!==getFinished()){
                var tmpvar$371=Entry(key$369,item$370,[$$$mptypes[0],$$$mptypes[1]]);
                next$key$369();
                return tmpvar$371;
            }
            return getFinished();
        }
    },[{t:Entry,a:[$$$mptypes[0],$$$mptypes[1]]},{t:Null}]).getSequence();
}
exports.zip=zip;
function max(values$372,$$$mptypes){
    var first$373=values$372.getFirst();
    var first$374;
    if((first$374=first$373)!==null){
        var max$375=first$374;
        var setMax$375=function(max$376){return max$375=max$376;};
        var it$377 = values$372.getRest().getIterator();
        var val$378;while ((val$378=it$377.next())!==getFinished()){
            if(val$378.compare(max$375).equals(getLarger())){
                max$375=val$378;
            }
        }
        return max$375;
    }else {
        return first$373;
    }
}
exports.max=max;
var smallest=function (x$379,y$380,$$$mptypes){
    return (opt$381=(x$379.compare(y$380).equals(getSmaller())?x$379:null),opt$381!==null?opt$381:y$380);
};
exports.smallest=smallest;
var opt$381;
var forItem=function (resulting$382,$$$mptypes){
    return function(entry$383,$$$mptypes){{
        return resulting$382(entry$383.getItem());
    }
}
}
;
exports.forItem=forItem;
var lessThan=function (val$384,$$$mptypes){
    return function(element$385,$$$mptypes){{
        return element$385.compare(val$384).equals(getSmaller());
    }
}
}
;
exports.lessThan=lessThan;
var forKey=function (resulting$386,$$$mptypes){
    return function(entry$387,$$$mptypes){{
        return resulting$386(entry$387.getKey());
    }
}
}
;
exports.forKey=forKey;
var join=function (iterables$388,$$$mptypes){
    if(iterables$388===undefined){iterables$388=empty;}
    return Comprehension(function(){
        var it$389=iterables$388.getIterator();
        var it$390=getFinished();
        var next$it$390=function(){
            if((it$390=it$389.next())!==getFinished()){
                it$391=it$390.getIterator();
                next$val$392();
                return it$390;
            }
            return getFinished();
        }
        var it$391;
        var val$392=getFinished();
        var next$val$392=function(){return val$392=it$391.next();}
        next$it$390();
        return function(){
            do{
                if(val$392!==getFinished()){
                    var tmpvar$393=val$392;
                    next$val$392();
                    return tmpvar$393;
                }
            }while(next$it$390()!==getFinished());
            return getFinished();
        }
    },[$$$mptypes[0],{t:Null}]).getSequence();
};
exports.join=join;
function sum(values$394,$$$mptypes){
    var sum$395=values$394.getFirst();
    var setSum$395=function(sum$396){return sum$395=sum$396;};
    var it$397 = values$394.getRest().getIterator();
    var val$398;while ((val$398=it$397.next())!==getFinished()){
        (sum$395=sum$395.plus(val$398));
    }
    return sum$395;
}
exports.sum=sum;
function print(line$399){
    getProcess().writeLine((opt$400=(opt$401=line$399,opt$401!==null?opt$401.getString():null),opt$400!==null?opt$400:String$("«null»",6)));
    var opt$400,opt$401;
}
exports.print=print;
function product(values$402,$$$mptypes){
    var product$403=values$402.getFirst();
    var setProduct$403=function(product$404){return product$403=product$404;};
    var it$405 = values$402.getRest().getIterator();
    var val$406;while ((val$406=it$405.next())!==getFinished()){
        (product$403=product$403.times(val$406));
    }
    return product$403;
}
exports.product=product;
function min(values$407,$$$mptypes){
    var first$408=values$407.getFirst();
    var first$409;
    if((first$409=first$408)!==null){
        var min$410=first$409;
        var setMin$410=function(min$411){return min$410=min$411;};
        var it$412 = values$407.getRest().getIterator();
        var val$413;while ((val$413=it$412.next())!==getFinished()){
            if(val$413.compare(min$410).equals(getSmaller())){
                min$410=val$413;
            }
        }
        return min$410;
    }else {
        return first$408;
    }
}
exports.min=min;
var greaterThan=function (val$414,$$$mptypes){
    return function(element$415,$$$mptypes){{
        return element$415.compare(val$414).equals(getLarger());
    }
}
}
;
exports.greaterThan=greaterThan;
var getNothing=function(){
    throw Exception();
}
exports.getNothing=getNothing;
var largest=function (x$416,y$417,$$$mptypes){
    return (opt$418=(x$416.compare(y$417).equals(getLarger())?x$416:null),opt$418!==null?opt$418:y$417);
};
exports.largest=largest;
var opt$418;
var first=function (values$419,$$$mptypes){
    return internalFirst(values$419,[$$$mptypes[0],$$$mptypes[0]]);
};
exports.first=first;
function every(values$420){
    var it$421 = values$420.getIterator();
    var val$422;while ((val$422=it$421.next())!==getFinished()){
        if((!val$422)){
            return false;
        }
    }
    return true;
}
exports.every=every;
var times=function (x$423,y$424,$$$mptypes){
    return x$423.times(y$424);
};
exports.times=times;
var plus=function (x$425,y$426,$$$mptypes){
    return x$425.plus(y$426);
};
exports.plus=plus;
function internalFirst(values$427,$$$mptypes){
    var first$428;
    var next$429;
    if(!isOfType((next$429=values$427.getIterator().next()),{t:Finished})){
        first$428=next$429;
    }else {
        first$428=null;
    }
    //assert at internalFirst.ceylon (10:4-10:34)
    var first$430;
    if (!(isOfType((first$430=first$428),{ t:'u', l:[$$$mptypes[0],$$$mptypes[0]]}))) { throw Exception('Assertion failed: \'is Absent|Value first\' at internalFirst.ceylon (10:11-10:33)'); }
    return first$430;
}
exports.internalFirst=internalFirst;
function combine(combination$431,elements$432,otherElements$433,$$$mptypes){
    function CombineIterable$434($$combineIterable$434){
        $init$CombineIterable$434();
        if ($$combineIterable$434===undefined)$$combineIterable$434=new CombineIterable$434.$$;
        $$combineIterable$434.$$targs$$=[$$$mptypes[0],$$$mptypes[1]];
        Iterable($$combineIterable$434);
        return $$combineIterable$434;
    }
    function $init$CombineIterable$434(){
        if (CombineIterable$434.$$===undefined){
            initTypeProto(CombineIterable$434,'ceylon.language::combine.CombineIterable',Basic,$init$Iterable());
            (function($$combineIterable$434){
                $$combineIterable$434.getIterator=function getIterator(){
                    var $$combineIterable$434=this;
                    function CombineIterator$435($$combineIterator$435){
                        $init$CombineIterator$435();
                        if ($$combineIterator$435===undefined)$$combineIterator$435=new CombineIterator$435.$$;
                        $$combineIterator$435.$$targs$$=[$$$mptypes[0]];
                        Iterator($$combineIterator$435);
                        $$combineIterator$435.iter$436=elements$432.getIterator();
                        $$combineIterator$435.otherIter$437=otherElements$433.getIterator();
                        return $$combineIterator$435;
                    }
                    function $init$CombineIterator$435(){
                        if (CombineIterator$435.$$===undefined){
                            initTypeProto(CombineIterator$435,'ceylon.language::combine.CombineIterable.iterator.CombineIterator',Basic,$init$Iterator());
                            (function($$combineIterator$435){
                                $$combineIterator$435.getIter$436=function getIter$436(){
                                    return this.iter$436;
                                };
                                $$combineIterator$435.getOtherIter$437=function getOtherIter$437(){
                                    return this.otherIter$437;
                                };
                                $$combineIterator$435.next=function next(){
                                    var $$combineIterator$435=this;
                                    var elem$438=$$combineIterator$435.getIter$436().next();
                                    var otherElem$439=$$combineIterator$435.getOtherIter$437().next();
                                    var elem$440;
                                    var otherElem$441;
                                    if(!isOfType((elem$440=elem$438),{t:Finished})&&!isOfType((otherElem$441=otherElem$439),{t:Finished})){
                                        return combination$431(elem$440,otherElem$441);
                                    }else {
                                        return getFinished();
                                    }
                                };
                            })(CombineIterator$435.$$.prototype);
                        }
                        return CombineIterator$435;
                    }
                    $init$CombineIterator$435();
                    return CombineIterator$435();
                };
            })(CombineIterable$434.$$.prototype);
        }
        return CombineIterable$434;
    }
    $init$CombineIterable$434();
    return CombineIterable$434();
}
exports.combine=combine;
var sort=function (elements$442,$$$mptypes){
    return internalSort(byIncreasing(function (e$443){
        return e$443;
    },[$$$mptypes[0],$$$mptypes[0]]),elements$442,[$$$mptypes[0]]);
};
exports.sort=sort;
var identical=function (x$444,y$445){
    return (x$444===y$445);
};
exports.identical=identical;
var compose=function (x$446,y$447,$$$mptypes){
    return flatten(function (args$448){
        return x$446(unflatten(y$447,[$$$mptypes[0],$$$mptypes[1]])(args$448));
    },[$$$mptypes[0],$$$mptypes[1]]);
};
exports.compose=compose;
var shuffle=function (f$449,$$$mptypes){
    return flatten(function (secondArgs$450){
        return flatten(function (firstArgs$451){
            return unflatten(unflatten(f$449,[{t:Callable,a:[$$$mptypes[0],$$$mptypes[0]]},$$$mptypes[0]])(firstArgs$451),[$$$mptypes[0],$$$mptypes[0]])(secondArgs$450);
        },[$$$mptypes[0],$$$mptypes[0]]);
    },[{t:Callable,a:[$$$mptypes[0],$$$mptypes[0]]},$$$mptypes[0]]);
};
exports.shuffle=shuffle;
//Ends compiled from Ceylon sources
/****************************************************************
* Overwriting of some methods not yet working in compiled code *
****************************************************************/
//Singleton.$$.prototype.getKeys = function() { return TypeCategory(this, {t:Integer}); }
function Map(wat) {
return wat;
}
function $init$Map() {
if (Map.$$===undefined) {
initTypeProto(Map, 'ceylon.language::Map', Collection, $init$Correspondence(), Cloneable);
}
return Map;
}
$init$Map();
var Map$proto = Map.$$.prototype;
Map$proto.equals = function(other) {
if (isOfType(other, {t:Map}) && other.getSize().equals(this.getSize())) {
var iter = this.getIterator();
var entry; while ((entry = iter.next()) !== $finished) {
var oi = other.get(entry.getKey());
if (oi === null || !entry.getItem().equals(oi)) {
return false;
}
}
return true;
}
return false;
}
Map$proto.getHash = function() {
var hc=1;
var iter=this.getIterator();
var elem; while((elem=iter.next())!=$finished) {
hc*=31;
hc += elem.getHash();
}
return hc;
}
Map$proto.getValues = function() { return MapValues(this); }
function MapValues(map, $$targs$$) {
var val = new MapValues.$$;
val.map = map;
val.$$targs$$=$$targs$$;
return val;
}
initTypeProto(MapValues, 'ceylon.language::MapValues', Basic, Collection);
var MapValues$proto = MapValues.$$.prototype;
MapValues$proto.getSize = function() { return this.map.getSize(); }
MapValues$proto.getEmpty = function() { return this.map.getEmpty(); }
MapValues$proto.getClone = function() { return this; }
MapValues$proto.getIterator = function() { return MapValuesIterator(this.map); }
function MapValuesIterator(map) {
var iter = new MapValuesIterator.$$;
iter.it = map.getIterator();
iter.$$targs$$=[map.$$targs$$[1]];
return iter;
}
initTypeProto(MapValuesIterator, 'ceylon.language::MapValuesIterator', Basic, Iterator);
MapValuesIterator.$$.prototype.next = function() {
var entry = this.it.next();
return (entry!==$finished) ? entry.getItem() : $finished;
}
Map$proto.getKeys = function() { return KeySet(this); }
function KeySet(map, $$targs$$) {
var set = new KeySet.$$;
set.map = map;
set.$$targs$$=[map.$$targs$$[0]];
return set;
}
initTypeProto(KeySet, 'ceylon.language::KeySet', Basic, Set);
var KeySet$proto = KeySet.$$.prototype;
KeySet$proto.getSize = function() { return this.map.getSize(); }
KeySet$proto.getEmpty = function() { return this.map.getEmpty(); }
KeySet$proto.contains = function(elem) { return this.map.defines(elem); }
KeySet$proto.getClone = function() { return this; }
KeySet$proto.getIterator = function() { return KeySetIterator(this.map); }
function KeySetIterator(map) {
var iter = new KeySetIterator.$$;
iter.it = map.getIterator();
iter.$$targs$$=[map.$$targs$$[0]];
return iter;
}
initTypeProto(KeySetIterator, 'ceylon.language::KeySetIterator', Basic, Iterator);
KeySetIterator.$$.prototype.next = function() {
var entry = this.it.next();
return (entry!==$finished) ? entry.getKey() : $finished;
}
KeySet$proto.union = function(other, $$$mptypes) {
var set = hashSetFromMap(this.map);
set.addAll(other);
return set;
}
KeySet$proto.intersection = function(other, $$$mptypes) {
var set = HashSet(undefined, [{t:'i', l:[this.$$targs$$[0], $$$mptypes[0]]}]);
var it = this.map.getIterator();
var entry;
while ((entry=it.next()) !== $finished) {
var key = entry.getKey();
if (other.contains(key)) { set.add(key); }
}
return set;
}
KeySet$proto.exclusiveUnion = function(other, $$$mptypes) {
var set = this.complement(other, $$$mptypes);
var it = other.getIterator();
var elem;
while ((elem=it.next()) !== $finished) {
if (!this.map.defines(elem)) { set.add(elem); }
}
return set;
}
KeySet$proto.complement = function(other, $$$mptypes) {
var set = HashSet(undefined, [{t:'u', l:[this.$$targs$$[0], $$$mptypes[0]]}]);
var it = this.map.getIterator();
var entry;
while ((entry=it.next()) !== $finished) {
var key = entry.getKey();
if (!other.contains(key)) { set.add(key); }
}
return set;
}
Map$proto.getInverse = function() {
var inv = HashMap(undefined, [this.$$targs$$[1], {t:Set, a:[this.$$targs$$[0]]}]);
var it = this.getIterator();
var entry;
var newSet = HashSet(undefined, [this.$$targs$$[0]]);
while ((entry=it.next()) !== $finished) {
var item = entry.getItem();
var set = inv.put(Entry(item, newSet, [this.$$targs$$[1], {t:Set, a:[this.$$targs$$[0]]}]), true);
if (set === null) {
set = newSet;
newSet = HashSet(undefined, [this.$$targs$$[0]]);
}
set.add(entry.getKey());
}
return inv;
}
Map$proto.mapItems = function(mapping, $$$mptypes) {
function EmptyMap(orig, $$targs$$) {
var em = new EmptyMap.$$;
em.$$targs$$=$$targs$$;
Basic(em);
em.orig=orig;
em.clone=function() { return this; }
em.getItem=function() { return null; }
em.getIterator=function() {
function miter(iter, $$targs1$$) {
var $i = new miter.$$;
$i.iter = iter;
$i.$$targs$$=$$targs1$$;
$i.next = function() {
var e = this.iter.next();
return e===$finished ? e : Entry(e.getKey(), mapping(e.getKey(), e.getItem()), this.$$targs$$);
};
return $i;
}
initTypeProto(miter, 'ceylon.language::MappedIterator', Basic, Iterator);
return miter(orig.getIterator(), [{t:Entry, a:em.$$targs$$}]);
}
em.getSize=function() { return this.orig.getSize(); }
em.getString=function() { return String$('',0); }
return em;
}
initTypeProto(EmptyMap, 'ceylon.language::EmptyMap', Basic, Map);
return EmptyMap(this, [this.$$targs$$[0], $$$mptypes[0]]);
}
exports.Map=Map;
function HashMap(entries, $$targs$$, map) {
if (map===undefined) { map = new HashMap.$$; }
Basic(map);
map.$$targs$$=$$targs$$;
map.map = {};
map.size = 0;
if (entries !== undefined) { map.putAll(entries); }
return map;
}
initTypeProto(HashMap, 'ceylon.language::HashMap', Basic, Map);
function copyHashMap(orig) {
var map = HashMap(undefined, orig.$$targs$$);
for (var hash in Object.keys(orig.map)) {
map.map[hash] = orig.map[hash].slice(0);
}
map.size = orig.size;
return map;
}
var HashMap$proto = HashMap.$$.prototype;
HashMap$proto.put = function(entry, keepOldItem) {
var key = entry.getKey();
var hash = key.getHash();
var arr = this.map[hash];
if (arr === undefined) {
arr = [];
this.map[hash] = arr;
}
for (var i=0; i<arr.length; ++i) {
var e = arr[i];
if (e.getKey().equals(key)) {
if (!keepOldItem) { arr[i] = entry; }
return e.getItem();
}
}
arr.push(entry);
++this.size;
return null;
}
HashMap$proto.putAll = function(entries) {
var it = entries.getIterator();
var entry;
while ((entry=it.next()) !== $finished) { this.put(entry); }
}
HashMap$proto.getSize = function() { return this.size; }
HashMap$proto.getEmpty = function() { return this.size===0; }
HashMap$proto.getLast = function() {
var hashs = Object.keys(this.map);
if (hashs.length === 0) { return null; }
var arr = this.map[hashs[hashs.length - 1]];
return arr[arr.length - 1];
}
HashMap$proto.getIterator = function() { return HashMapIterator(this.map); }
HashMap$proto.getClone = function() { return this; }
HashMap$proto.get = function(key) {
var hash = key.getHash();
var arr = this.map[hash];
if (arr !== undefined) {
for (var i=0; i<arr.length; ++i) {
var entry = arr[i];
if (entry.getKey().equals(key)) { return entry.getItem(); }
}
}
return null;
}
HashMap$proto.contains = function(elem) {
if (isOfType(elem, {t:Entry})) {
var item = this.get(elem.getKey());
if (item !== null) { return item.equals(elem.getItem()); }
}
return false;
}
HashMap$proto.defines = function(key) { return this.get(key) !== null; }
function HashSet(elems, $$targs$$, set) {
if (set===undefined) { set = new HashSet.$$; }
Basic(set);
set.$$targs$$=$$targs$$;
set.map = HashMap(undefined, [$$targs$$[0], {t:Boolean$}]);
if (elems !== undefined) { set.addAll(elems); }
return set;
}
initTypeProto(HashSet, 'ceylon.language::HashSet', Basic, Set);
function hashSetFromMap(map) {
var set = new HashSet.$$;
Basic(set);
set.map = this;
set.$$targs$$=[map.$$targs$$[0]];
return set;
}
var HashSet$proto = HashSet.$$.prototype;
HashSet$proto.add = function(elem) { this.map.put(Entry(elem, true, [this.$$targs$$[0], {t:Boolean$}])); }
HashSet$proto.addAll = function(elems) {
var it = elems.getIterator();
var elem;
while ((elem=it.next()) !== $finished) { this.map.put(Entry(elem, true, [this.$$targs$$[0], {t:Boolean$}])); }
}
HashSet$proto.getSize = function() { return this.map.size; }
HashSet$proto.getEmpty = function() { return this.map.size===0; }
HashSet$proto.getLast = function() {
var entry = this.map.getLast();
return (entry !== null) ? entry.getKey() : null;
}
HashSet$proto.getIterator = function() { return HashSetIterator(this.map, this.$$targs$$); }
HashSet$proto.getClone = function() { return this; }
HashSet$proto.contains = function(elem) { return this.map.get(elem) !== null; }
HashSet$proto.union = function(other, $$$mptypes) {
var set = hashSetFromMap(copyHashMap(this.map));
set.$$targs$$=[{t:'u', l:[this.$$targs$$[0], $$$mptypes[0]]}];
set.addAll(other);
return set;
}
HashSet$proto.intersection = function(other, $$$mptypes) {
var set = HashSet(undefined, [{t:'i', l:[this.$$targs$$[0], $$$mptypes[0]]}]);
var it = this.getIterator();
var elem;
while ((elem=it.next()) !== $finished) {
if (other.contains(elem)) { set.map.put(Entry(elem, true)); }
}
return set;
}
HashSet$proto.exclusiveUnion = function(other) {
var set = this.complement(other);
var it = other.getIterator();
var elem;
while ((elem=it.next()) !== $finished) {
if (this.map.get(elem) === null) { set.map.put(Entry(elem, true)); }
}
return set;
}
HashSet$proto.complement = function(other) {
var set = HashSet(undfined, [{t:'u', l:[this.$$targs$$[0], $$$mptypes[0]]}]);
var it = this.getIterator();
var elem;
while ((elem=it.next()) !== $finished) {
if (!other.contains(elem)) { set.map.put(Entry(elem, true)); }
}
return set;
}
function HashMapIterator(map, $$targs$$) {
var it = new HashMapIterator.$$;
it.map = map;
it.$$targs$$=$$targs$$;
it.hashs = Object.keys(map);
it.hashIndex = 0;
it.arrIndex = 0;
return it;
}
initTypeProto(HashMapIterator, 'ceylon.language::HashMapIterator', Basic, Iterator);
HashMapIterator.$$.prototype.next = function() {
var hash = this.hashs[this.hashIndex];
if (hash !== undefined) {
var arr = this.map[hash];
var entry = arr[this.arrIndex++];
if (this.arrIndex >= arr.length) {
++this.hashIndex;
this.arrIndex = 0;
}
return entry;
}
return $finished;
}
function HashSetIterator(map, $$targs$$) {
var it = new HashSetIterator.$$;
it.mapIt = map.getIterator();
it.$$targs$$=$$targs$$;
return it;
}
initTypeProto(HashSetIterator, 'ceylon.language::HashSetIterator', Basic, Iterator);
HashSetIterator.$$.prototype.next = function() {
var entry = this.mapIt.next();
return (entry !== $finished) ? entry.getKey() : $finished;
}
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
inheritProto(JSNumber, Object$, Scalar, Castable, $init$Integral(), Exponentiable);
function Integer(value) { return Number(value); }
initTypeProto(Integer, 'ceylon.language::Integer', Object$, Scalar, Castable,
$init$Integral(), Exponentiable, Binary);
function Float(value) {
var that = new Number(value);
that.$float = true;
return that;
}
initTypeProto(Float, 'ceylon.language::Float', Object$, Scalar, Castable, Exponentiable);
var JSNum$proto = Number.prototype;
JSNum$proto.getT$all = function() {
return (this.$float ? Float : Integer).$$.T$all;
}
JSNum$proto.getT$name = function() {
return (this.$float ? Float : Integer).$$.T$name;
}
JSNum$proto.toString = origNumToString;
JSNum$proto.getString = function() { return String$(this.toString()) }
JSNum$proto.plus = function(other) {
return (this.$float||other.$float) ? Float(this+other) : (this+other);
}
JSNum$proto.minus = function(other) {
return (this.$float||other.$float) ? Float(this-other) : (this-other);
}
JSNum$proto.times = function(other) {
return (this.$float||other.$float) ? Float(this*other) : (this*other);
}
JSNum$proto.divided = function(other) {
if (this.$float||other.$float) { return Float(this/other); }
if (other == 0) {
throw Exception(String$("Division by Zero"));
}
return (this/other)|0;
}
JSNum$proto.remainder = function(other) { return this%other; }
JSNum$proto.power = function(exp) {
if (this.$float||exp.$float) { return Float(Math.pow(this, exp)); }
if (exp<0 && this!=1 && this!=-1) {
throw Exception(String$("Negative exponent"));
}
return Math.pow(this, exp)|0;
}
JSNum$proto.getNegativeValue = function() {
return this.$float ? Float(-this) : -this;
}
JSNum$proto.getPositiveValue = function() {
return this.$float ? this : this.valueOf();
}
JSNum$proto.equals = function(other) { return other==this.valueOf(); }
JSNum$proto.compare = function(other) {
var value = this.valueOf();
return value==other ? equal : (value<other ? smaller:larger);
}
JSNum$proto.getFloat = function() { return Float(this.valueOf()); }
JSNum$proto.getInteger = function() { return this|0; }
JSNum$proto.getIntegerValue = function() { return this|0; }
JSNum$proto.getCharacter = function() { return Character(this.valueOf()); }
JSNum$proto.getSuccessor = function() { return this+1 }
JSNum$proto.getPredecessor = function() { return this-1 }
JSNum$proto.getUnit = function() { return this == 1 }
JSNum$proto.getZero = function() { return this == 0 }
JSNum$proto.getFractionalPart = function() {
if (!this.$float) { return 0; }
return Float(this - (this>=0 ? Math.floor(this) : Math.ceil(this)));
}
JSNum$proto.getWholePart = function() {
if (!this.$float) { return this.valueOf(); }
return Float(this>=0 ? Math.floor(this) : Math.ceil(this));
}
JSNum$proto.getSign = function() { return this > 0 ? 1 : this < 0 ? -1 : 0; }
JSNum$proto.getHash = function() {
return this.$float ? String$(this.toPrecision()).getHash() : this.valueOf();
}
JSNum$proto.distanceFrom = function(other) {
return (this.$float ? this.getWholePart() : this) - other;
}
//Binary interface
JSNum$proto.getNot = function() { return ~this; }
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
JSNum$proto.getSize = function() { return 53; }
JSNum$proto.getMagnitude = function() { return Math.abs(this); }
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
JSNum$proto.getUndefined = function() { return isNaN(this); }
JSNum$proto.getFinite = function() { return this!=Infinity && this!=-Infinity && !isNaN(this); }
JSNum$proto.getInfinite = function() { return this==Infinity || this==-Infinity; }
JSNum$proto.getStrictlyPositive = function() { return this>0 || (this==0 && (1/this==Infinity)); }
JSNum$proto.getStrictlyNegative = function() { return this<0 || (this==0 && (1/this==-Infinity)); }
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
Ranged, Summable, Castable, Cloneable);
var origStrToString = String.prototype.toString;
inheritProto(String$, Object$, Sequential, Comparable, Ranged, Summable, Castable,
Cloneable);
function SequenceString() {}
initType(SequenceString, "ceylon.language::SequenceString", String$, Sequence);
function EmptyString() {}
initType(EmptyString, "ceylon.language::EmptyString", String$, Empty);
var String$proto = String$.$$.prototype;
String$proto.$$targs$$=[{t:Character}, {t:Null}];
String$proto.getT$name = function() {
return ((this.length!==0)?SequenceString:EmptyString).$$.T$name;
}
String$proto.getT$all = function() {
return ((this.length!==0)?SequenceString:EmptyString).$$.T$all;
}
String$proto.toString = origStrToString;
String$proto.getString = function() { return this }
String$proto.plus = function(other) {
var size = this.codePoints + other.codePoints;
return String$(this+other, isNaN(size)?undefined:size);
}
String$proto.equals = function(other) { return other.constructor===String && other.valueOf()===this.valueOf(); }
String$proto.compare = function(other) {
var cmp = this.localeCompare(other);
return cmp===0 ? equal : (cmp<0 ? smaller:larger);
}
String$proto.getUppercased = function() { return String$(this.toUpperCase()) }
String$proto.getLowercased = function() { return String$(this.toLowerCase()) }
String$proto.getSize = function() {
if (this.codePoints===undefined) {
this.codePoints = countCodepoints(this);
}
return this.codePoints;
}
String$proto.getLastIndex = function() { return this.getSize().equals(0) ? null : this.getSize().getPredecessor(); }
String$proto.span = function(from, to) {
if (from > to) {
return this.segment(to, from-to+1).getReversed();
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
String$proto.getEmpty = function() {
return this.length===0;
}
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
String$proto.getIterator = function() {
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
String$proto.getTrimmed = function() {
// make use of the fact that all WS characters are single UTF-16 code units
var from = 0;
while (from<this.length && (this.charCodeAt(from) in $WS)) {++from}
var to = this.length;
if (from < to) {
do {--to} while (from<to && (this.charCodeAt(to) in $WS));
++to;
}
if (from===0 && to===this.length) {return this}
var result = String$(this.substring(from, to));
if (this.codePoints !== undefined) {
result.codePoints = this.codePoints - from - this.length + to;
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
String$proto.getHash = function() {
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
}
String$proto.getFirst = function() {
return this.get(0);
}
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
String$proto.getNormalized = function() {
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
}
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
String$proto.getCharacters = function() {
return this.length>0 ? this:empty;
}
String$proto.getFirst = function() { return this.getSize()>0?this.get(0):null; }
String$proto.getLast = function() { return this.getSize()>0?this.get(this.getSize().getPredecessor()):null; }
String$proto.getKeys = function() {
//TODO implement!!!
return this.getSize() > 0 ? Range(0, this.getSize().getPredecessor(), [{t:Integer}]) : empty;
}
String$proto.join = function(strings) {
if (strings === undefined) {return String$("", 0)}
var it = strings.getIterator();
var str = it.next();
if (str === $finished) {return String$("", 0);}
if (this.codePoints === undefined) {this.codePoints = countCodepoints(this)}
var result = str;
var len = str.codePoints;
while ((str = it.next()) !== $finished) {
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
var it = sep.getIterator();
var c; while ((c=it.next()) !== $finished) {sepChars[c.value] = true}
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
String$proto.getReversed = function() {
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
}
String$proto.$replace = function(sub, repl) {
return String$(this.replace(new RegExp(sub, 'g'), repl));
}
String$proto.repeat = function(times) {
var sb = StringBuilder();
for (var i = 0; i < times; i++) {
sb.append(this);
}
return sb.getString();
}
function isNewline(c) { return c.value===10; }
String$proto.getLines = function() {
return this.$split(isNewline, true);
}
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
return ocs.length > 0 ? ocs : empty;
}
String$proto.$filter = function(f) {
var r = Iterable.$$.prototype.$filter.apply(this, [f]);
return string(r);
}
String$proto.skipping = function(skip) {
if (skip==0) return this;
return this.segment(skip, this.getSize());
}
String$proto.taking = function(take) {
if (take==0) return empty;
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
String$proto.getCoalesced = function() { return this; }
function StringIterator(string) {
var that = new StringIterator.$$;
that.string = string;
that.index = 0;
return that;
}
initTypeProto(StringIterator, 'ceylon.language::StringIterator', Basic, Iterator);
var StringIterator$proto = StringIterator.$$.prototype;
StringIterator$proto.$$targs$$=[{t:Character}, {t:Null}];
StringIterator$proto.next = function() {
if (this.index >= this.string.length) { return $finished }
var first = this.string.charCodeAt(this.index++);
if ((first&0xfc00) !== 0xd800 || this.index >= this.string.length) {
return Character(first);
}
return Character((first<<10) + this.string.charCodeAt(this.index++) - 0x35fdc00);
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
Character$proto.getString = function() { return String$(codepointToString(this.value)) }
Character$proto.equals = function(other) {
return other.constructor===Character.$$ && other.value===this.value;
}
Character$proto.getHash = function() {return this.value}
Character$proto.compare = function(other) {
return this.value===other.value ? equal
: (this.value<other.value ? smaller:larger);
}
Character$proto.getUppercased = function() {
var ucstr = codepointToString(this.value).toUpperCase();
return Character(codepointFromString(ucstr, 0));
}
Character$proto.getLowercased = function() {
var lcstr = codepointToString(this.value).toLowerCase();
return Character(codepointFromString(lcstr, 0));
}
Character$proto.getTitlecased = function() {
var tc = $toTitlecase[this.value];
return tc===undefined ? this.getUppercased() : Character(tc);
}
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
Character$proto.getWhitespace = function() { return this.value in $WS }
Character$proto.getControl = function() { return this.value<32 || this.value===127 }
Character$proto.getDigit = function() {
var check = this.value & 0xfffffff0;
if (check in $digit) {
return (this.value&0xf) <= 9;
}
if ((check|6) in $digit) {
return (this.value&0xf) >= 6;
}
return this.value>=0x1d7ce && this.value<=0x1d7ff;
}
Character$proto.getIntegerValue = function() { return this.value; }
Character$proto.getInteger = Character$proto.getIntegerValue;
Character$proto.getUppercase = function() {
var str = codepointToString(this.value);
return str.toLowerCase()!==str && !(this.value in $titlecase);
}
Character$proto.getLowercase = function() {
var str = codepointToString(this.value);
return str.toUpperCase()!==str && !(this.value in $titlecase);
}
Character$proto.getTitlecase = function() {return this.value in $titlecase}
Character$proto.getLetter = function() {
//TODO: this captures only letters that have case
var str = codepointToString(this.value);
return str.toUpperCase()!==str || str.toLowerCase()!==str || (this.value in $titlecase);
}
Character$proto.getSuccessor = function() {
var succ = this.value+1;
if ((succ&0xf800) === 0xd800) {return Character(0xe000)}
return Character((succ<=0x10ffff) ? succ:0);
}
Character$proto.getPredecessor = function() {
var succ = this.value-1;
if ((succ&0xf800) === 0xd800) {return Character(0xd7ff)}
return Character((succ>=0) ? succ:0x10ffff);
}
Character$proto.distanceFrom = function(other) {
return this.value - other.value;
}
function StringBuilder() {
var that = new StringBuilder.$$;
that.value = "";
return that;
}
initTypeProto(StringBuilder, 'ceylon.language::StringBuilder', Basic);
var StringBuilder$proto = StringBuilder.$$.prototype;
StringBuilder$proto.getString = function() { return String$(this.value); }
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
this.append(c.getString());
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
StringBuilder$proto.getSize = function() {
return countCodepoints(this.value);
}
StringBuilder$proto.reset = function() {
this.value = "";
return this;
}
StringBuilder$proto.insert = function(pos, content) {
if (pos <= 0) {
this.value = content + this.value;
} else if (pos >= this.getSize()) {
this.value = this.value + content;
} else {
this.value = this.value.slice(0, pos) + content + this.value.slice(pos);
}
return this;
}
StringBuilder$proto.$delete = function(pos, count) {
if (pos < 0) pos=0; else if (pos>this.getSize()) return this;
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
Boolean.prototype.getHash = function() {return this.valueOf()?1:0;}
var trueString = String$("true", 4);
var falseString = String$("false", 5);
Boolean.prototype.getString = function() {return this.valueOf()?trueString:falseString;}
function getTrue() {return true}
function getFalse() {return false}
var $true = true;
var $false = false;
function Finished() {}
initTypeProto(Finished, 'ceylon.language::Finished', Basic);
var $finished = new Finished.$$;
$finished.string = String$("exhausted", 9);
$finished.getString = function() { return this.string; }
function getFinished() { return $finished; }
function Comparison(name) {
var that = new Comparison.$$;
that.name = String$(name);
return that;
}
initTypeProto(Comparison, 'ceylon.language::Comparison', Basic);
var Comparison$proto = Comparison.$$.prototype;
Comparison$proto.getString = function() { return this.name; }
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
return value !== null && value !== undefined && !value.getEmpty();
}
function isOfType(obj, type) {
if (type && type.t) {
if (type.t == 'i' || type.t == 'u') {
return isOfTypes(obj, type);
}
if (obj === null) {
return type.t===Null || type.t===Anything;
}
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
}
return false;
}
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
tn += '<';
for (var i=0; i < obj.$$targs$$.length; i++) {
if (i>0) { tn += ','; }
tn += _typename(obj.$$targs$$[i]);
}
tn += '>';
}
return String$(tn);
}
function identityHash(obj) {
return obj.BasicID;
}
exports.exists=exists;
exports.nonempty=nonempty;
exports.isOfType=isOfType;
exports.className=className;
exports.identityHash=identityHash;
//More functions, related to comprehensions and iterables
function string(/*Iterable<Character>*/chars) {
if (chars === undefined) return String$('',0);
var s = StringBuilder();
var iter = chars.getIterator();
var c; while ((c = iter.next()) !== $finished) {
s.appendCharacter(c);
}
return s.getString();
}
function internalSort(comp, elems, $$$mptypes) {
if (elems===undefined) {return empty;}
var arr = [];
var it = elems.getIterator();
var e;
while ((e=it.next()) !== $finished) {arr.push(e);}
if (arr.length === 0) {return empty;}
arr.sort(function(a, b) {
var cmp = comp(a,b);
return (cmp===larger) ? 1 : ((cmp===smaller) ? -1 : 0);
});
return ArraySequence(arr, $$$mptypes);
}
exports.string=string;
function flatten(tf, $$$mptypes) {
var rf = function() {
var t = empty;
for (var i=0; i < arguments.length; i++) {
var c = arguments[i]===null ? Null :
arguments[i].getT$all ? arguments[i].getT$all() :
Anything;
t = Tuple(arguments[i], t, [c, t.getT$all()[0]]);
}
return tf(t, t.$$targs$$);
}
rf.$$targs$$=$$$mptypes;
return rf;
}
function unflatten(ff, $$$mptypes) {
var ru = function ru() {
var a = [];
for (var i = 0; i < arguments.length; i++) {
a[i] = arguments[i];
}
a[i]=this.$$targs$$;
return ff.apply(this, a);
}
ru.$$targs$$=$$$mptypes;
return ru;
}
//internal
function toTuple(iterable) {
var seq = iterable.getSequence();
return Tuple(seq.getFirst(), seq.getRest().getSequence(),
[seq.$$targs$$[0], seq.$$targs$$[0], {t:Sequential, a:seq.$$targs$$}]);
}
exports.toTuple=toTuple;
function Sequence($$sequence) {
return $$sequence;
}
function $init$Sequence() {
if (Sequence.$$===undefined) {
initTypeProto(Sequence, 'ceylon.language::Sequence', $init$Sequential(),
$init$Container(), $init$Cloneable());
}
return Sequence;
}
$init$Sequence();
var Sequence$proto = Sequence.$$.prototype;
Sequence$proto.getEmpty = function() { return false; }
Sequence$proto.sort = function(comp) {
return internalSort(comp, this, this.$$targs$$);
}
Sequence$proto.collect = function(f, $$$mptypes) {
return this.map(f, $$$mptypes).getSequence();
}
Sequence$proto.getClone = function() { return this; }
function Array$() {
var that = new Array$.$$;
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
return value;
}
initTypeProto(ArraySequence, 'ceylon.language::ArraySequence', Basic, Sequence);
Array$proto.getT$name = function() {
return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$name;
}
Array$proto.getT$all = function() {
return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$all;
}
exports.EmptyArray=EmptyArray;
Array$proto.getSize = function() { return this.length; }
Array$proto.setItem = function(idx,elem) {
if (idx >= 0 && idx < this.length) {
this[idx] = elem;
}
}
Array$proto.get = function(idx) {
var result = this[idx];
return result!==undefined ? result:null;
}
Array$proto.getLastIndex = function() {
return this.length>0 ? (this.length-1) : null;
}
Array$proto.getReversed = function() {
if (this.length === 0) { return this; }
var arr = this.slice(0);
arr.reverse();
return this.$seq ? ArraySequence(arr,this.$$targs$$) : arr.reifyCeylonType(this.$$targs$$);
}
Array$proto.chain = function(other, $$$mptypes) {
if (this.length === 0) { return other; }
return Iterable.$$.prototype.chain.call(this, other, $$$mptypes);
}
Array$proto.getFirst = function() { return this.length>0 ? this[0] : null; }
Array$proto.getLast = function() { return this.length>0 ? this[this.length-1] : null; }
Array$proto.segment = function(from, len) {
if (len <= 0) { return empty; }
var stop = from + len;
var seq = this.slice((from>=0)?from:0, (stop>=0)?stop:0);
return (seq.length > 0) ? ArraySequence(seq,this.$$targs$$) : empty;
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
return to < 0 ? empty : this.span(0, to);
}
Array$proto.spanFrom = function(from) {
return this.span(from, 0x7fffffff);
}
Array$proto.getRest = function() {
return this.length<=1 ? empty : ArraySequence(this.slice(1),this.$$targs$$);
}
Array$proto.items = function(keys) {
if (keys === undefined) return empty;
var seq = [];
for (var i = 0; i < keys.getSize(); i++) {
var key = keys.get(i);
seq.push(this.get(key));
}
return ArraySequence(seq,this.$$targs$$);
}
Array$proto.getKeys = function() { return TypeCategory(this, {t:Integer}); }
Array$proto.contains = function(elem) {
for (var i=0; i<this.length; i++) {
if (elem.equals(this[i])) {
return true;
}
}
return false;
}
Array$proto.getIterator = function() {
var $$$index$$$ = 0;
var $$$arr$$$ = this;
return new ComprehensionIterator(function() {
return ($$$index$$$ === $$$arr$$$.length) ? $finished : $$$arr$$$[$$$index$$$++];
}, this.$$targs$$);
}
exports.ArrayList=ArrayList;
exports.array=function(elems, $$$ptypes) {
var e=[];
if (!(elems === null || elems === undefined)) {
var iter=elems.getIterator();
var item;while((item=iter.next())!==$finished) {
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
initTypeProto(TypeCategory, 'ceylon.language::TypeCategory', Basic, Category);
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
initTypeProto(SequenceBuilder, 'ceylon.language::SequenceBuilder', Basic);
var SequenceBuilder$proto = SequenceBuilder.$$.prototype;
SequenceBuilder$proto.getSequence = function() {
return (this.seq.length > 0) ? ArraySequence(this.seq,this.$$targs$$) : empty;
}
SequenceBuilder$proto.append = function(e) { this.seq.push(e); }
SequenceBuilder$proto.appendAll = function(/*Iterable*/arr) {
if (arr === undefined) return;
var iter = arr.getIterator();
var e; while ((e = iter.next()) !== $finished) {
this.seq.push(e);
}
}
SequenceBuilder$proto.getSize = function() { return this.seq.length; }
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
initTypeProto(languageClass, "ceylon.language::language", Basic);
var lang$proto=languageClass.$$.prototype;
lang$proto.getVersion=function() {
return String$("0.5",3);
}
lang$proto.getMajorVersion=function() { return 0; }
lang$proto.getMinorVersion=function() { return 5; }
lang$proto.getReleaseVersion=function() { return 0; }
lang$proto.getVersionName=function() { return String$("Analytical Engine",11); }
lang$proto.getMajorVersionBinary=function() { return 3; }
lang$proto.getMinorVersionBinary=function() { return 0; }
var languageString = String$("language", 7);
lang$proto.getString = function() {
return languageString;
}
var language$ = languageClass();
function getLanguage() { return language$; }
exports.getLanguage=getLanguage;
function processClass() {
var proc = new processClass.$$;
Basic(proc);
return proc;
}
initTypeProto(processClass, "ceylon.language::process", Basic);
var process$proto = processClass.$$.prototype;
var argv = empty;
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
process$proto.getArguments = function() { return argv; }
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
process$proto.getNewline = function() { return linesep; }
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
process$proto.getMilliseconds = function() {
return Date.now();
}
process$proto.getNanoseconds = function() {
return Date.now()*1000000;
}
if ((typeof process !== "undefined") && (process.exit !== undefined)) {
process$proto.exit = function(code) {
process.exit(code);
}
} else {
process$proto.exit = function() {}
}
var processString = String$("process", 7);
process$proto.getString = function() {
return processString;
}
process$proto.getVm = function() {
if (typeof process !== "undefined" && process.execPath && process.execPath.match(/node(\.exe)?$/)) {
return String$("node.js", 7);
} else if (typeof window === 'object') {
return String$("Browser", 7);
}
return String$("Unknown JavaScript environment", 30);
}
process$proto.getVmVersion = function() {
if (typeof process !== "undefined" && typeof process.version === 'string') {
return String$(process.version);
}
return String$("Unknown");
}
process$proto.getOs = function() {
if (typeof process !== "undefined" && typeof process.platform === 'string') {
return String$(process.platform);
}
return String$("Unknown");
}
process$proto.getOsVersion = function() {
return String$("Unknown");
}
var process$ = processClass();
function getProcess() { return process$; }
exports.getProcess=getProcess;
//Turns out we need to have empty implementations of these annotations
exports.see=function(){};
exports.by=function(){};
exports.tagged=function(){};
exports.Identifiable=Identifiable;
exports.identityHash=$identityHash;
exports.Basic=Basic;
exports.Object=Object$;
exports.Anything=Anything;
exports.Null=Null;
exports.Nothing=Nothing;
exports.Boolean=Boolean$;
exports.Comparison=Comparison;
exports.getNull=getNull;
exports.getTrue=getTrue;
exports.getFalse=getFalse;
exports.Finished=Finished;
exports.getFinished=getFinished;
});
}(typeof define==='function' && define.amd ?
define : function (factory) {
if (typeof exports!=='undefined') {
factory(require, exports, module);
} else {
throw "no module loader";
}
}));
