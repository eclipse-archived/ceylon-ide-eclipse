(function(define) {
define(function(require, exports, module) {
//the Ceylon language module
$$metamodel$$={"$mod-name":"ceylon.language","$mod-version":"0.4","ceylon.language":{"Some":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"FixedSized"},{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Bottom"}],"$pk":"ceylon.language","$nm":"ContainerWithFirstElement"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"A fixed-sized, non-empty collection.\""]},"$at":{"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns `false`, since every `Some` contains at\nleast one element.\""],"actual":[]},"$nm":"empty"},"first":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns the first element, which always exists.\""],"actual":[]},"$nm":"first"}},"$nm":"Some"},"Iterator":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Iterable"],"doc":["\"Produces elements of an `Iterable` object. Classes that \nimplement this interface should be immutable.\""],"by":["\"Gavin\""]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The next element, or `exhausted` if\nthere are no more elements to be iterated.\""]},"$nm":"next"}},"$nm":"Iterator"},"Castable":{"$mt":"ifc","$tp":[{"variance":"in","$nm":"Types"}],"$an":{"shared":[],"see":["Integer"],"doc":["\"Abstract supertype for types which can be automatically\nwidened to a different type in numeric operator \nexpressions. The type argument is a union of wider \ntypes to which the subtype can be cast. \n\nFor example, `Integer` satisfies `Castable<Integer|Float>`,\n so `Integer` can be promoted to `Float` in an \n expression like `-1/2.0`.\""],"by":["\"Gavin\""]},"$m":{"castTo":{"$t":{"$nm":"CastValue"},"$mt":"mthd","$tp":[{"satisfies":[{"$nm":"Types"}],"$nm":"CastValue"}],"$an":{"shared":[],"formal":[],"doc":["\"Cast this object to the given type.\""]},"$nm":"castTo"}},"$nm":"Castable"},"Callable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Return"},{"seq":"1","variance":"in","$nm":"CallableArgument"}],"$an":{"shared":[],"doc":["\"A reference to a method or function.\""]},"$nm":"Callable"},"copyArray":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"source"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"target"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Efficiently copy the elements of one array to another \narray.\""]},"$nm":"copyArray"},"Array":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"FixedSized"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Cloneable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Ranged"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"abstract":[],"shared":[],"doc":["\"A fixed-size array of elements. An array may have zero\nsize (an empty array). Arrays are mutable. Any element\nof an array may be set to a new value.\n\nThis class is provided primarily to support interoperation \nwith Java, and for some performance-critical low-level \nprogramming tasks.\""]},"$m":{"setItem":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Replace the existing element at the specified index \nwith the given element. Does nothing if the specified \nindex is negative or larger than the index of the \nlast element in the array.\""]},"$nm":"setItem"}},"$at":{"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Reverse this array, returning a new array.\""],"actual":[]},"$nm":"reversed"}},"$nm":"Array"},"Singleton":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"A sequence with exactly one element.\""]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"a"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `1` if this Singleton's element\nsatisfies the predicate, or `0` otherwise.\""],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"A `Singleton` can be equal to another `List` if \nthat `List` has only one element which is equal to \nthis `Singleton`'s element.\""],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns a `Singleton` if the given starting index \nis  `0` and the given `length` is greater than `0`.\""],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `true` if the specified element is this \n`Singleton`'s element.\""],"actual":[]},"$nm":"contains"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns the contained element, if the specified \nindex is `0`.\""],"actual":[]},"$nm":"item"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"filter":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns a `Singleton` if the given starting index \nis `0`.\""],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns `0`.\""],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns a `Singleton` with the same element.\""],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the element contained in this `Singleton`.\""],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the Singleton itself, since a Singleton\ncannot contain a null.\""],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Return this singleton.\""],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns `Empty`.\""],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the element contained in this `Singleton`.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns `1`.\""],"actual":[]},"$nm":"size"}},"$nm":"Singleton"},"byKey":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Key","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"}],"$an":{"shared":[],"see":["byItem"],"doc":["\"A comparator for `Entry`s which compares their keys \naccording to the given `comparing()` function.\""]},"$nm":"byKey"},"Comparable":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Other"}],"$an":{"shared":[],"doc":["\"The general contract for values whose magnitude can be \ncompared. `Comparable` imposes a total ordering upon\ninstances of any type that satisfies the interface.\nIf a type `T` satisfies `Comparable<T>`, then instances\nof `T` may be compared using the comparison operators\n`<`, `>`, `<=`, >=`, and `<=>`.\n\nThe total order of a type must be consistent with the \ndefinition of equality for the type. That is, there\nare three mutually exclusive possibilities:\n\n- `x<y`,\n- `x>y`, or\n- `x==y`\""],"by":["\"Gavin\""]},"$m":{"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["equals"],"doc":["\"Compares this value with the given value. \nImplementations must respect the constraints that: \n\n- `x==y` if and only if `x<=>y == equal` \n   (consistency with `equals()`), \n- if `x>y` then `y<x` (symmetry), and \n- if `x>y` and `y>z` then `x>z` (transitivity).\""]},"$nm":"compare"}},"$nm":"Comparable","$st":"Other"},"Comparison":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"of":[{"$pk":"ceylon.language","$nm":"larger"},{"$pk":"ceylon.language","$nm":"smaller"},{"$pk":"ceylon.language","$nm":"equal"}],"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Comparable"],"doc":["\"The result of a comparison between two `Comparable` \nobjects.\""],"by":["\"Gavin\""]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"Comparison"},"Empty":{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Bottom"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Bottom"}],"$pk":"ceylon.language","$nm":"None"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$an":{"shared":[],"see":["Sequence"],"doc":["\"A sequence with no elements. The type of the expression\n`{}`.\""]},"$m":{"sort":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns 0 for any given predicate.\""],"actual":[]},"$nm":"count"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns an `Empty` for any given segment.\""],"actual":[]},"$nm":"segment"},"select":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"select"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `false` for any given element.\""],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"withTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"defines"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"item":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns `null` for any given index.\""],"actual":[]},"$nm":"item"},"map":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"withLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"withLeading"},"skipping":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"filter":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"find":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"span":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Returns an `Empty` for any given span.\""],"actual":[]},"$nm":"span"},"collect":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns `null`.\""],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Bottom"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns an iterator that is already exhausted.\""],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns a string description of the empty List: `{}`.\""],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns an `Empty`.\""],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns 0.\""],"actual":[]},"$nm":"size"}},"$nm":"Empty"},"hex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"number"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify a hexadecimal literal.\""]},"$nm":"hex"},"combine":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"},{"$t":"OtherElement","$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"prm","$pt":"f","$nm":"combination"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"otherElements"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"$nm":"Element"},{"$nm":"OtherElement"}],"$an":{"shared":[],"doc":["\"Applies a function to each element of two `Iterable`s\nand returns an `Iterable` with the results.\""],"by":["\"Gavin\"","\"Enrique Zamudio\""]},"$nm":"combine"},"false":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["\"A value representing falsity in Boolean logic.\""],"by":["\"Gavin\""]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"false"},"elements":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"The given elements (usually a comprehension), as an `Iterable`.\""]},"$nm":"elements"},"Finished":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"of":[{"$pk":"ceylon.language","$nm":"exhausted"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Iterator"],"doc":["\"The type of the value that indicates that \nan `Iterator` is exhausted and has no more \nvalues to return.\""]},"$nm":"Finished"},"coalesce":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Return a sequence containing the given values which are\nnot null. If there are no values which are not null,\nreturn an empty sequence.\""]},"$nm":"coalesce"},"Entry":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Key","$hdn":"1","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$hdn":"1","$mt":"prm","$pt":"v","$nm":"item"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["\"A pair containing a key and an associated value\ncalled the item. Used primarily to represent the\nelements of a `Map`.\""],"by":["\"Gavin\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determines if this entry is equal to the given\nentry. Two entries are equal if they have the same\nkey and the same value.\""],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns a description of the entry in the form \n`key->item`.\""],"actual":[]},"$nm":"string"},"item":{"$t":{"$nm":"Item"},"$mt":"attr","$an":{"shared":[],"doc":["\"The value associated with the key.\""]},"$nm":"item"},"key":{"$t":{"$nm":"Key"},"$mt":"attr","$an":{"shared":[],"doc":["\"The key used to access the entry.\""]},"$nm":"key"}},"$nm":"Entry"},"exhausted":{"super":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"obj","$an":{"shared":[],"see":["Iterator"],"doc":["\"A value that indicates that an `Iterator`\nis exhausted and has no more values to \nreturn.\""]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"exhausted"},"Nothing":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Void"},"of":[{"$pk":"ceylon.language","$nm":"null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["null"],"doc":["\"The type of the `null` value. Any union type of form \n`Nothing|T` is considered an optional type, whose values\ninclude `null`. Any type of this form may be written as\n`T?` for convenience.\""],"by":["\"Gavin\""]},"$nm":"Nothing"},"Invertable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Inverse"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["\"Abstraction of types which support a unary additive inversion\noperation. For a numeric type, this should return the \nnegative of the argument value. Note that the type \nparameter of this interface is not restricted to be a \nself type, in order to accommodate the possibility of \ntypes whose additive inverse can only be expressed in terms of \na wider type.\""],"by":["\"Gavin\""]},"$at":{"positiveValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The value itself, expressed as an instance of the\nwider type.\""]},"$nm":"positiveValue"},"negativeValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The additive inverse of the value, which may be expressed\nas an instance of a wider type.\""]},"$nm":"negativeValue"}},"$nm":"Invertable"},"Cloneable":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Clone"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"variance":"out","$nm":"Clone"}],"$an":{"shared":[],"doc":["\"Abstract supertype of objects whose value can be \ncloned.\""]},"$at":{"clone":{"$t":{"$nm":"Clone"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Obtain a clone of this object. For a mutable \nobject, this should return a copy of the object. \nFor an immutable object, it is acceptable to return\nthe object itself.\""]},"$nm":"clone"}},"$nm":"Cloneable","$st":"Clone"},"Sized":{"satisfies":[{"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$an":{"shared":[],"doc":["\"Abstract supertype of `Container`s with a finite number \nof elements, where the number of elements can be \nefficiently determined.\""]},"$at":{"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Determine if the sized container is empty, that is,\nif `sized.size==0`.\""],"actual":[]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The number of elements or entries belonging to the \ncontainer.\""]},"$nm":"size"}},"$nm":"Sized"},"Ordinal":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Character","Integer","Integral","Range"],"doc":["\"Abstraction of ordinal types, that is, types with \nsuccessor and predecessor operations, including\n`Integer` and other `Integral` numeric types.\n`Character` is also considered an ordinal type. \n`Ordinal` types may be used to generate a `Range`.\""],"by":["\"Gavin\""]},"$m":{"distanceFrom":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns the distance between the receiver and\nanother Ordinal of the same type.\""]},"$nm":"distanceFrom"}},"$at":{"predecessor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if this is the minimum value\""],"doc":["\"The predecessor of this value.\""]},"$nm":"predecessor"},"successor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if this is the maximum value\""],"doc":["\"The successor of this value.\""]},"$nm":"successor"}},"$nm":"Ordinal","$st":"Other"},"largest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","smallest","max"],"doc":["\"Given two `Comparable` values, return largest of the\ntwo.\""]},"$nm":"largest"},"ContainerWithFirstElement":{"satisfies":[{"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Nothing"}],"variance":"out","$nm":"Null"}],"$an":{"shared":[],"doc":["\"Abstract supertype of containers which provide an \noperation for accessing the first element, if any. A \ncontainer which may or may not be empty is a\n`ContainerWithFirstElement<Element,Nothing>`. A \ncontainer which is always empty is a \n`ContainerWithFirstElement<Bottom,Nothing>`. A container\nwhich is never empty is a \n`ContainerWithFirstElement<Element,Bottom>`.\""]},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The last element. Should produce `null` if the\ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`.\""]},"$nm":"last"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The first element. Should produce `null` if the \ncontainer is empty, that is, for any instance for\nwhich `empty` evaluates to `true`.\""]},"$nm":"first"}},"$nm":"ContainerWithFirstElement"},"greaterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is greater than its element.\nThis is useful in conjunction with methods that receive\na predicate function.\""]},"$nm":"greaterThan"},"bottom":{"$t":{"$pk":"ceylon.language","$nm":"Bottom"},"$mt":"gttr","$an":{"shared":[],"doc":["\"A value that is assignable to any type, \nbut that results in an exception when \nevaluated. This is most useful for \ngenerating members in an IDE.\""]},"$nm":"bottom"},"Identifiable":{"$mt":"ifc","$an":{"shared":[],"doc":["\"The abstract supertype of all types with a well-defined\nnotion of identity. Values of type `Identifiable` may \nbe compared using the `===` operator to determine if \nthey are references to the same object instance. For\nthe sake of convenience, this interface defines a\ndefault implementation of value equality equivalent\nto identity. Of course, subtypes are encouraged to\nrefine this implementation.\""],"by":["\"Gavin\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Identity equality comparing the identity of the two \nvalues. May be refined by subtypes for which value \nequality is more appropriate. Implementations must\nrespect the constraint that if `x===y` then `x==y` \n(equality is consistent with identity).\""],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["identityHash"],"doc":["\"The system-defined identity hash value of the \ninstance. Subtypes which refine `equals()` must \nalso refine `hash`, according to the general \ncontract defined by `Object`.\""],"actual":[]},"$nm":"hash"}},"$nm":"Identifiable"},"language":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$mt":"obj","$an":{"shared":[],"doc":["\"Contains information about the language\""],"by":["\"The Ceylon Team\""]},"$at":{"majorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The Ceylon language major version.\""]},"$nm":"majorVersion"},"majorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The major version of the code generated for the underlying runtime.\""]},"$nm":"majorVersionBinary"},"minorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The Ceylon language minor version.\""]},"$nm":"minorVersion"},"versionName":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The Ceylon language release name.\""]},"$nm":"versionName"},"releaseVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The Ceylon language release version.\""]},"$nm":"releaseVersion"},"minorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The minor version of the code generated for the underlying runtime.\""]},"$nm":"minorVersionBinary"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The Ceylon language version.\""]},"$nm":"version"}},"$nm":"language"},"bin":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"number"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify a binary literal.\""]},"$nm":"bin"},"array":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Create an array containing the given elements. If no\nelements are provided, create an empty array of the\ngiven element type.\""]},"$nm":"array"},"sort":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable"],"doc":["\"Sort a given elements, returning a new sequence.\""]},"$nm":"sort"},"equalTo":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Returns a partial function that will compare an element\nto any other element and returns true if they're equal.\nThis is useful in conjunction with methods that receive\na predicate function.\""]},"$nm":"equalTo"},"arrayOfSize":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"size"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Create an array of the specified size, populating every \nindex with the given element. If the specified size is\nsmaller than `1`, return an empty array of the given\nelement type.\""]},"$nm":"arrayOfSize"},"Ranged":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Index"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Index"},{"variance":"out","$nm":"Span"}],"$an":{"shared":[],"see":["List","Sequence","String"],"doc":["\"Abstract supertype of ranged objects which map a range\nof `Comparable` keys to ranges of values. The type\nparameter `Span` abstracts the type of the resulting\nrange.\n\nA span may be obtained from an instance of `Ranged`\nusing the span operator:\n\n    print(\"hello world\"[0..5])\n\""]},"$m":{"segment":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Obtain a segment containing the mapped values\nstarting from the given index, with the given \nlength.\""]},"$nm":"segment"},"span":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Index"}]},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Obtain a span containing the mapped values between \nthe two given indices. If the second given index\nis null, the span has no upper bound.\""]},"$nm":"span"}},"$nm":"Ranged"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Produces a sequence of each index to element `Entry` \nfor the given sequence of values.\""]},"$nm":"entries"},"None":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"FixedSized"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Bottom"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"ContainerWithFirstElement"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"A fixed-size collection with no elements.\""]},"$at":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns `emptyIterator`.\""],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns `null`.\""],"actual":[]},"$nm":"last"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns `true`.\""],"actual":[]},"$nm":"empty"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns `null`.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns 0.\""],"actual":[]},"$nm":"size"}},"$nm":"None"},"license":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify the URL of the license of a module or package.\""]},"$nm":"license"},"Object":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Void"},"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["IdentifiableObject","Nothing"],"doc":["\"The abstract supertype of all types representing \ndefinite values. Any two `Object`s may be compared\nfor value equality using the `==` and `!=` operators:\n\n    true==false\n    1==\"hello world\"\n    \"hello\"+ \" \" + \"world\"==\"hello world\"\n    Singleton(\"hello world\")=={ \"hello world\" }\n\nHowever, since `Nothing` is not a subtype of `Object`,\nthe value `null` cannot be compared to any other value\nusing `==`. Thus, value equality is not defined for \noptional types. This neatly voids the problem of \ndeciding the value of the expression `null==null`, \nwhich is simply illegal.\""],"by":["\"Gavin\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determine if two values are equal. Implementations\nshould respect the constraints that:\n\n- if `x===y` then `x==y` (reflexivity), \n- if `x==y` then `y==x` (symmetry), \n- if `x==y` and `y==z` then `x==z` (transitivity).\n\nFurthermore it is recommended that implementations\nensure that if `x==y` then `x` and `y` have the\nsame concrete class.\""]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The hash value of the value, which allows the value\nto be an element of a hash-based set or key of a\nhash-based map. Implementations must respect the\nconstraint that if `x==y` then `x.hash==y.hash`.\""]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"A developer-friendly string representing the \ninstance. Concatenates the name of the concrete \nclass of the instance with the `hash` of the \ninstance. Subclasses are encouraged to refine this \nimplementation to produce a more meaningful \nrepresentation.\""]},"$nm":"string"}},"$nm":"Object"},"null":{"super":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"obj","$an":{"shared":[],"doc":["\"The null value.\""],"by":["\"Gavin\""]},"$nm":"null"},"LazySet":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"An implementation of Set that wraps an `Iterable` of\nelements. All operations on this Set are performed\non the `Iterable`.\""],"by":["\"Enrique Zamudio\""]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"complement"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"exclusiveUnion"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"union"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"LazySet"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazySet"},"min":{"$t":{"comp":"u","$ts":[{"$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"comp":"i","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Null"}],"$pk":"ceylon.language","$nm":"ContainerWithFirstElement"}]},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Nothing"}],"$nm":"Null"}],"$an":{"shared":[],"see":["Comparable","max","smallest"],"doc":["\"Given a nonempty sequence of `Comparable` values, \nreturn the smallest value in the sequence.\""]},"$nm":"min"},"Float":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Exponentiable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Castable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"A 64-bit floating point number. A `Float` is capable of\napproximately representing numeric values between\n2<sup>-1022</sup> and (2-2<sup>-52</sup>)×2<sup>1023</sup>,\nalong with the special values `infinity` and `-infinity`, \nand undefined values (Not a Number). Zero is represented \nby distinct instances `+0`, `-0`, but these instances \nare equal. An undefined value is not equal to any other\nvalue, not even to itself.\""]},"$at":{"strictlyNegative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a negative number,\n`-0`, or `-infinity`. Produces `false` for a\npositive number, `+0`, or undefined.\""]},"$nm":"strictlyNegative"},"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The sign of this value. Produces `1` for a positive \nnumber or `infinity`. Produces `-`` for a negative\nnumber or `-infinity`. Produces `0` for `+0`, `-0`, \nor undefined.\""],"actual":[]},"$nm":"sign"},"infinite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinity","finite"],"doc":["\"Determines whether this value is infinite in magnitude\nProduces `true` for `infinity` and `-infinity`.\nProduces `false` for a finite number, `+0`, `-0`, or\nundefined.\""]},"$nm":"infinite"},"undefined":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Determines whether this value is undefined (that is, Not a Number or NaN).\nThe undefined value has the property that it is not equal (`==`) \nto itself, as a consequence the undefined value cannot sensibly be \nused in most collections.\""]},"$nm":"undefined"},"strictlyPositive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a positive number,\n`+0`, or `infinity`. Produces `false` for a \nnegative number, `-0`, or undefined.\""]},"$nm":"strictlyPositive"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a negative number or\n`-infinity`. Produces `false` for a positive number, \n`+0`, `-0`, or undefined.\""],"actual":[]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determines if this value is a positive number or\n`infinity`. Produces `false` for a negative number, \n`+0`, `-0`, or undefined.\""],"actual":[]},"$nm":"positive"},"finite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"see":["infinite","infinity"],"doc":["\"Determines whether this value is finite. Produces\n`false` for `infinity`, `-infinity`, and undefined.\""]},"$nm":"finite"}},"$nm":"Float"},"deprecated":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"reason"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark types or members which should not be used anymore.\""]},"$nm":"deprecated"},"Collection":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$pk":"ceylon.language","$nm":"Sized"},{"$pk":"ceylon.language","$nm":"Category"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["List","Map","Set"],"doc":["\"Represents an iterable collection of elements of finite \nsize. `Collection` is the abstract supertype of `List`,\n`Map`, and `Set`.\n\nA `Collection` forms a `Category` of its elements.\n\nAll `Collection`s are `Cloneable`. If a collection is\nimmutable, it is acceptable that `clone` produce a\nreference to the collection itself. If a collection is\nmutable, `clone` should produce an immutable collection\ncontaining references to the same elements, with the\nsame structure as the original collection&mdash;that \nis, it should produce an immutable shallow copy of the\ncollection.\""]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return `true` if the given object is an element of\nthis collection. In this default implementation,\nand in most refining implementations, return `false`\notherwise. An acceptable refining implementation\nmay return `true` for objects which are not \nelements of the collection, but this is not \nrecommended. (For example, the `contains()` method \nof `String` returns `true` for any substring of the\nstring.)\""],"actual":[]},"$nm":"contains"}},"$at":{"elementsString":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$nm":"elementsString"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"A string of form `\"{ x, y, z }\"` where `x`, `y`, \nand `z` are the `string` representations of the \nelements of this collection, as produced by the\niterator of the collection, or the string `\"{}\"` \nif this collection is empty. If the collection \niterator produces the value `null`, the string\nrepresentation contains the string `\"null\"`.\""],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Determine if the collection is empty, that is, if \nit has no elements.\""],"actual":[]},"$nm":"empty"}},"$nm":"Collection"},"Range":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"last"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Represents the range of totally ordered, ordinal values \ngenerated by two endpoints of type `Ordinal` and \n`Comparable`. If the first value is smaller than the\nlast value, the range is increasing. If the first value\nis larger than the last value, the range is decreasing.\nIf the two values are equal, the range contains exactly\none element. The range is always nonempty, containing \nat least one value.\n\nA range may be produced using the `..` operator:\n\n    for (i in min..max) { ... }\n    if (char in `A`..`Z`) { ... }\n\""],"by":["\"Gavin\""]},"$m":{"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determines if two ranges are the same by comparing\ntheir endpoints.\""],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"n"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"The element of the range that occurs `n` values after\nthe start of the range. Note that this operation \nis inefficient for large ranges.\""],"actual":[]},"$nm":"item"},"next":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$nm":"next"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determines if the range includes the given object.\""],"actual":[]},"$nm":"contains"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determines if the range includes the given value.\""]},"$nm":"includes"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"span":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The index of the end of the range.\""],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the range itself, since ranges are \nimmutable.\""],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"doc":["\"An iterator for the elements of the range.\""],"actual":[]},"$nm":"iterator"},"distance":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$nm":"distance"},"decreasing":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Determines if the range is decreasing.\""]},"$nm":"decreasing"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["\"The end of the range.\""],"actual":[]},"$nm":"last"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the range itself, since a Range cannot\ncontain nulls.\""],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Reverse this range, returning a new range.\""],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$mt":"gttr","$an":{"shared":[],"doc":["\"The rest of the range, without the start of the\nrange.\""],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["\"The start of the range.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["\"The nonzero number of elements in the range.\""],"actual":[]},"$nm":"size"}},"$nm":"Range"},"Integral":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Integral"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["\"Abstraction of integral numeric types. That is, types \nwith no fractional part, including `Integer`. The \ndivision operation for integral numeric types results \nin a remainder. Therefore, integral numeric types have \nan operation to determine the remainder of any division \noperation.\""],"by":["\"Gavin\""]},"$m":{"remainder":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["divided"],"doc":["\"The remainder, after dividing this number by the \ngiven number.\""]},"$nm":"remainder"}},"$at":{"unit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is one.\""]},"$nm":"unit"},"zero":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is zero.\""]},"$nm":"zero"}},"$nm":"Integral","$st":"Other"},"max":{"$t":{"comp":"u","$ts":[{"$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"comp":"i","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Null"}],"$pk":"ceylon.language","$nm":"ContainerWithFirstElement"}]},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Nothing"}],"$nm":"Null"}],"$an":{"shared":[],"see":["Comparable","min","largest"],"doc":["\"Given a nonempty sequence of `Comparable` values, \nreturn the largest value in the sequence.\""]},"$nm":"max"},"SequenceAppender":{"super":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["\"This class is used for constructing a new nonempty \nsequence by incrementally appending elements to an\nexisting nonempty sequence. The existing sequence is\nnot modified, since `Sequence`s are immutable. This class \nis mutable but threadsafe.\""]},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The resulting nonempty sequence. If no elements \nhave been appended, the original nonempty \nsequence.\""],"actual":[]},"$nm":"sequence"}},"$nm":"SequenceAppender"},"RecursiveInitializationException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["\"Thrown when name could not be initialized due to recursive access during initialization.\""]},"$nm":"RecursiveInitializationException"},"byIncreasing":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Value"}]},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byDecreasing"],"doc":["\"A comparator which orders elements in increasing order \naccording to the `Comparable` returned by the given \n`comparable()` function.\""]},"$nm":"byIncreasing"},"smallest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","largest","min"],"doc":["\"Given two `Comparable` values, return smallest of the\ntwo.\""]},"$nm":"smallest"},"larger":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["\"The value is larger than the given value.\""],"by":["\"Gavin\""]},"$nm":"larger"},"true":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["\"A value representing truth in Boolean logic.\""],"by":["\"Gavin\""]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"true"},"join":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"iterables"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["\"Given a list of iterable objects, return a new sequence \nof all elements of the all given objects. If there are\nno arguments, or if none of the arguments contains any\nelements, return the empty sequence.\""]},"$nm":"join"},"Exponentiable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"},{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$nm":"This"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["\"Abstraction of numeric types that may be raised to a\npower. Note that the type of the exponent may be\ndifferent to the numeric type which can be \nexponentiated.\""]},"$m":{"power":{"$t":{"$nm":"This"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The result of raising this number to the given\npower.\""]},"$nm":"power"}},"$nm":"Exponentiable","$st":"This"},"Character":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["String"],"doc":["\"A 32-bit Unicode character.\""],"by":["\"Gavin\""]},"$at":{"digit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is a numeric digit.\""]},"$nm":"digit"},"uppercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this is an uppercase representation of\nthe character.\""]},"$nm":"uppercase"},"control":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is an ISO control \ncharacter.\""]},"$nm":"control"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The code point of the character.\""]},"$nm":"integer"},"letter":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is a letter.\""]},"$nm":"letter"},"lowercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this is a lowercase representation of\nthe character.\""]},"$nm":"lowercase"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The lowercase representation of this character.\""]},"$nm":"lowercased"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The uppercase representation of this character.\""]},"$nm":"uppercased"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"A string containg just this character.\""],"actual":[]},"$nm":"string"},"whitespace":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this character is a whitespace \ncharacter.\""]},"$nm":"whitespace"},"titlecased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The title case representation of this character.\""]},"$nm":"titlecased"},"titlecase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if this is a title case representation of\nthe character.\""]},"$nm":"titlecase"}},"$nm":"Character"},"forKey":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forItem"],"doc":["\"A function that returns the result of the given `resulting()` function \non the key of a given `Entry`.\""]},"$nm":"forKey"},"process":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$mt":"obj","$an":{"shared":[],"doc":["\"Represents the current process (instance of the virtual\nmachine).\""],"by":["\"Gavin\"","\"Tako\""]},"$m":{"readLine":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Read a line of input text from the standard input \nof the virtual machine process.\""]},"$nm":"readLine"},"writeErrorLine":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Print a line to the standard error of the \nvirtual machine process.\""]},"$nm":"writeErrorLine"},"writeError":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Print a string to the standard error of the \nvirtual machine process.\""]},"$nm":"writeError"},"propertyValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"The value of the given system property of the virtual\nmachine, if any.\""]},"$nm":"propertyValue"},"namedArgumentValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"The value of the first argument of form `-name=value`, \n`--name=value`, or `-name value` specified among the \ncommand line arguments to the virtual machine, if\nany.\""]},"$nm":"namedArgumentValue"},"write":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Print a string to the standard output of the \nvirtual machine process.\""]},"$nm":"write"},"namedArgumentPresent":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Determine if an argument of form `-name` or `--name` \nwas specified among the command line arguments to \nthe virtual machine.\""]},"$nm":"namedArgumentPresent"},"writeLine":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["print"],"doc":["\"Print a line to the standard output of the \nvirtual machine process.\""]},"$nm":"writeLine"},"exit":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"code"}]],"$mt":"mthd","$an":{"shared":[]},"$nm":"exit"}},"$at":{"os":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the name of the operating system this process is running on.\""]},"$nm":"os"},"vmVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the version of the virtual machine this process is running on.\""]},"$nm":"vmVersion"},"vm":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the name of the virtual machine this process is running on.\""]},"$nm":"vm"},"osVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the version of the operating system this process is running on.\""]},"$nm":"osVersion"},"newline":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The line ending character sequence on this platform.\""]},"$nm":"newline"},"arguments":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$mt":"gttr","$an":{"shared":[],"doc":["\"The command line arguments to the virtual machine.\""]},"$nm":"arguments"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"string"},"nanoseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The elapsed time in nanoseconds since an arbitrary\nstarting point.\""]},"$nm":"nanoseconds"},"milliseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The elapsed time in milliseconds since midnight, \n1 January 1970.\""]},"$nm":"milliseconds"}},"$nm":"process"},"forItem":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forKey"],"doc":["\"A function that returns the result of the given `resulting()` function \non the item of a given `Entry`.\""]},"$nm":"forItem"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Create a new string containing the given characters.\""]},"$nm":"string"},"doc":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify API documentation on a type or member.\""]},"$nm":"doc"},"Scalar":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$pk":"ceylon.language","$nm":"Number"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Scalar"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["\"Abstraction of numeric types representing scalar\nvalues, including `Integer` and `Float`.\""],"by":["\"Gavin\""]},"$at":{"magnitude":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The magnitude of this number.\""],"actual":[]},"$nm":"magnitude"},"wholePart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The integral value of the number after truncation \nof the fractional part. For integral numeric types,\nthe integral value of a number is the number \nitself.\""],"actual":[]},"$nm":"wholePart"},"fractionalPart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The fractional part of the number, after truncation \nof the integral part. For integral numeric types,\nthe fractional part is always zero.\""],"actual":[]},"$nm":"fractionalPart"}},"$nm":"Scalar","$st":"Other"},"SequenceBuilder":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"satisfies":[{"$pk":"ceylon.language","$nm":"Sized"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceAppender","join","Singleton"],"doc":["\"Since sequences are immutable, this class is used for\nconstructing a new sequence by incrementally appending \nelements to the empty sequence. This class is mutable\nbut threadsafe.\""]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Append an element to the sequence.\""]},"$nm":"append"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Append multiple elements to the sequence.\""]},"$nm":"appendAll"}},"$at":{"sequence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"The resulting sequence. If no elements have been\nappended, the empty sequence.\""]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Determine if the resulting sequence is empty.\""],"actual":[]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The size of the resulting sequence.\""],"actual":[]},"$nm":"size"}},"$nm":"SequenceBuilder"},"tagged":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to categorize the API by tag.\""]},"$nm":"tagged"},"variable":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark an attribute as variable. \n`variable` attributes must be assigned with `:=` and can be reassigned over time.\""]},"$nm":"variable"},"Correspondence":{"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Map","List","Category"],"doc":["\"Abstract supertype of objects which associate values \nwith keys. `Correspondence` does not satisfy `Category`,\nsince in some cases&mdash;`List`, for example&mdash;it is \nconvenient to consider the subtype a `Category` of its\nvalues, and in other cases&mdash;`Map`, for example&mdash;it \nis convenient to treat the subtype as a `Category` of its\nentries.\n\nThe item corresponding to a given key may be obtained \nfrom a `Correspondence` using the item operator:\n\n    value bg = settings[\"backgroundColor\"] else white;\n\nThe `item()` operation and item operator result in an\noptional type, to reflect the possibility that there is\nno item for the given key.\""],"by":["\"Gavin\""]},"$m":{"definesAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["\"Determines if this `Correspondence` defines a value\nfor any one of the given keys.\""]},"$nm":"definesAny"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["definesAny","definesEvery","keys"],"doc":["\"Determines if there is a value defined for the \ngiven key.\""]},"$nm":"defines"},"items":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["item"],"doc":["\"Returns the items defined for the given keys, in\nthe same order as the corresponding keys.\""]},"$nm":"items"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["items"],"doc":["\"Returns the value defined for the given key, or \n`null` if there is no value defined for the given \nkey.\""]},"$nm":"item"},"definesEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["\"Determines if this `Correspondence` defines a value\nfor every one of the given keys.\""]},"$nm":"definesEvery"}},"$c":{"Items":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"keys"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$m":{"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"item"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"first"}},"$nm":"Items"}},"$at":{"keys":{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["defines"],"doc":["\"The `Category` of all keys for which a value is \ndefined by this `Correspondence`.\""]},"$nm":"keys"}},"$nm":"Correspondence"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"A count of the number of `true` items in the given values.\""]},"$nm":"count"},"byItem":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"see":["byKey"],"doc":["\"A comparator for `Entry`s which compares their items \naccording to the given `comparing()` function.\""]},"$nm":"byItem"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify API authors.\""]},"$nm":"by"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":[],"any":[],"doc":["\"true if every one of the given values is true, otherwise false.\""]},"$nm":"every"},"$pkg-shared":"1","lessThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Returns a partial function that will compare an element\nto any other element and returns true if the compared\nelement is less than its element.\nThis is useful in conjunction with methods that receive\na predicate function.\""]},"$nm":"lessThan"},"identityHash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"see":["identical"],"doc":["\"Return the system-defined identity hash value of the \ngiven value. This hash value is consistent with \nidentity equality.\""]},"$nm":"identityHash"},"optional":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[]},"$nm":"optional"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":[],"doc":["\"true if any of the given values is true, otherwise false\""],"every":[]},"$nm":"any"},"Summable":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Other"}],"$an":{"shared":[],"see":["String","Numeric"],"doc":["\"Abstraction of types which support a binary addition\noperator. For numeric types, this is just familiar \nnumeric addition. For strings, it is string \nconcatenation. In general, the addition operation \nshould be a binary associative operation.\""],"by":["\"Gavin\""]},"$m":{"plus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The result of adding the given value to this value. \nThis operation should never perform any kind of \nmutation upon either the receiving value or the \nargument value.\""]},"$nm":"plus"}},"$nm":"Summable","$st":"Other"},"Set":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"A collection of unique elements.\n\nA `Set` is a `Collection` of its elements.\n\nSets may be the subject of the binary union, \nintersection, exclusive union, and complement operators \n`|`, `&`, `^`, and `~`.\""]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing all the elements in \nthis `Set` that are not contained in the given\n`Set`.\""]},"$nm":"complement"},"subset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Determines if this `Set` is a subset of the given \n`Set`, that is, if the given set contains all of \nthe elements in this set.\""]},"$nm":"subset"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing only the elements \nthat are present in both this `Set` and the given \n`Set`.\""]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing only the elements \ncontained in either this `Set` or the given `Set`, \nbut no element contained in both sets.\""]},"$nm":"exclusiveUnion"},"superset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Determines if this `Set` is a superset of the \nspecified Set, that is, if this `Set` contains all \nof the elements in the specified `Set`.\""]},"$nm":"superset"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Two `Set`s are considered equal if they have the \nsame size and if every element of the first set is\nalso an element of the second set, as determined\nby `contains()`.\""],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["\"Returns a new `Set` containing all the elements of \nthis `Set` and all the elements of the given `Set`.\""]},"$nm":"union"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Set"},"Sequence":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Some"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Empty"],"doc":["\"A nonempty, immutable sequence of values. A sequence of\nvalues may be formed using braces:\n\n    value worlds = { \"hello\", \"world\" };\n    value cubes = { for (n in 0..100) n**3 };\n\nThe union type `Empty|Sequence<Element>`, abbreviated\n`Element[]`, represents a possibly-empty sequence. The\n`if (nonempty ...)` construct may be used to obtain an\ninstance of `Sequence` from a possibly-empty sequence:\n\n    Integer[] nums = ... ;\n    if (nonmpty nums) {\n        Integer first = nums.first;\n        Integer max = max(nums);\n        Sequence<Integer> squares = nums.collect((Integer i) i**2));\n        Sequence<Integer> sorted = nums.sort(byIncreasing((Integer i) i));\n    }\n\nOperations like `first`, `max()`, `collect()`, and \n`sort()`, which polymorphically produce a nonempty\nor non-null output when given a nonempty input are \ncalled _emptiness-preserving_.\""],"by":["\"Gavin\""]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"A nonempty sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\""],"actual":[]},"$nm":"sort"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"doc":["\"A nonempty sequence containing the results of \napplying the given mapping to the elements of this\nsequence.\""],"actual":[]},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"The index of the last element of the sequence.\""],"actual":[]},"$nm":"lastIndex"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"The last element of the sequence, that is, the\nelement with index `sequence.lastIndex`.\""],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"gttr","$an":{"shared":[],"doc":["\"This sequence.\""],"actual":[]},"$nm":"sequence"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Reverse this sequence, returning a new nonempty\nsequence.\""],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The rest of the sequence, without the first \nelement.\""],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The first element of the sequence, that is, the\nelement with index `0`.\""],"actual":[]},"$nm":"first"}},"$nm":"Sequence"},"StringBuilder":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$mt":"cls","$an":{"shared":[],"doc":["\"Since strings are immutable, this class is used for\nconstructing a string by incrementally appending \ncharacters to the empty string. This class is mutable \nbut threadsafe.\""]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Append the characters in the given string.\""]},"$nm":"append"},"appendSpace":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Append a space character.\""]},"$nm":"appendSpace"},"delete":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"count"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Deletes the specified number of characters from the\ncurrent content, starting at the specified position.\nIf the position is beyond the end of the current content,\nnothing is deleted. If the number of characters to delete\nis greater than the available characters from the given\nposition, the content is truncated at the given position.\""]},"$nm":"delete"},"reset":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Remove all content and return to initial state.\""]},"$nm":"reset"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Append the characters in the given strings.\""]},"$nm":"appendAll"},"insert":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Character"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$nm":"content"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Insert a String or Character at the specified position.\nIf the position is beyond the end of the current\nstring, the new content is simply appended to the\ncurrent content. If the position is a negative number,\nthe new content is inserted at index 0.\""]},"$nm":"insert"},"appendNewline":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Append a newline character.\""]},"$nm":"appendNewline"},"appendCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Append the given character.\""]},"$nm":"appendCharacter"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"The resulting string. If no characters have been\nappended, the empty string.\""],"actual":[]},"$nm":"string"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the size of the current content.\""]},"$nm":"size"}},"$nm":"StringBuilder"},"emptyOrSingleton":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Singleton","Empty"],"doc":["\"A `Singleton` if the given element is non-null, otherwise `Empty`.\""]},"$nm":"emptyOrSingleton"},"shared":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a type or member as shared.\nAnnotating them with `shared` makes them visible outside the current unit of code.\""]},"$nm":"shared"},"Binary":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Binary"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["\"Abstraction of numeric types that consist in\na sequence of bits, like `Integer`.\""],"by":["\"Stef\""]},"$m":{"clear":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Returns a new number with the given bit set to 0.\nBits are indexed from right to left.\""]},"$nm":"clear"},"xor":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a logical exclusive OR operation.\""]},"$nm":"xor"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Retrieves a given bit from this bit sequence. Bits are indexed from\nright to left.\""]},"$nm":"get"},"leftLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a left logical shift. Sign is not preserved. Padded with zeros.\""]},"$nm":"leftLogicalShift"},"set":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"bit"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a new number with the given bit set to the given value.\nBits are indexed from right to left.\""]},"$nm":"set"},"or":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a logical inclusive OR operation.\""]},"$nm":"or"},"rightArithmeticShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a right arithmetic shift. Sign is preserved. Padded with zeros.\""]},"$nm":"rightArithmeticShift"},"rightLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a right logical shift. Sign is not preserved. Padded with zeros.\""]},"$nm":"rightLogicalShift"},"flip":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a new number with the given bit flipped to its opposite value.\nBits are indexed from right to left.\""]},"$nm":"flip"},"and":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Performs a logical AND operation.\""]},"$nm":"and"}},"$at":{"not":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The binary complement of this sequence of bits.\""]},"$nm":"not"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The number of bits (0 or 1) that this sequence of bits can hold.\""]},"$nm":"size"}},"$nm":"Binary","$st":"Other"},"Map":{"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Entry","forKey","forItem","byItem","byKey"],"doc":["\"Represents a collection which maps _keys_ to _items_,\nwhere a key can map to at most one item. Each such \nmapping may be represented by an `Entry`.\n\nA `Map` is a `Collection` of its `Entry`s, and a \n`Correspondence` from keys to items.\n\nThe prescence of an entry in a map may be tested\nusing the `in` operator:\n\n    if (\"lang\"->\"en_AU\" in settings) { ... }\n\nThe entries of the map may be iterated using `for`:\n\n    for (key->item in settings) { ... }\n\nThe item for a key may be obtained using the item\noperator:\n\n    String lang = settings[\"lang\"] else \"en_US\";\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Two `Map`s are considered equal iff they have the \nsame _entry sets_. The entry set of a `Map` is the\nset of `Entry`s belonging to the map. Therefore, the\nmaps are equal iff they have same set of `keys`, and \nfor every key in the key set, the maps have equal\nitems.\""],"actual":[]},"$nm":"equals"},"mapItems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"mapping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["\"Returns a `Map` with the same keys as this map. For\nevery key, the item is the result of applying the\ngiven transformation function.\""]},"$nm":"mapItems"}},"$at":{"values":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Collection"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns all the values stored in this `Map`. An \nelement can be stored under more than one key in \nthe map, and so it can be contained more than once \nin the resulting collection.\""]},"$nm":"values"},"inverse":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns a `Map` in which every key is an `Item` in \nthis map, and every value is the set of keys that \nstored the `Item` in this map.\""]},"$nm":"inverse"},"keys":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns the set of keys contained in this `Map`.\""],"actual":[]},"$nm":"keys"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Map"},"LazyMap":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"entries"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["\"A Map implementation that wraps an `Iterable` of\nentries. All operations, such as lookups, size,\netc. are performed on the `Iterable`.\""],"by":["\"Enrique Zamudio\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"item"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"LazyMap"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazyMap"},"Number":{"$mt":"ifc","$an":{"shared":[],"see":["Numeric"],"doc":["\"Abstraction of numbers. Numeric operations are provided\nby the subtype `Numeric`. This type defines operations\nwhich can be expressed without reference to the self\ntype `Other` of `Numeric`.\""],"by":["\"Gavin\""]},"$at":{"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The sign of this number. Returns `1` if the number \nis positive, `-1` if it is negative, or `0` if it \nis zero.\""]},"$nm":"sign"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if the number is too large to be represented \nas an `Integer`\""],"doc":["\"The number, represented as an `Integer`, after \ntruncation of any fractional part.\""]},"$nm":"integer"},"magnitude":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The magnitude of the number.\""]},"$nm":"magnitude"},"wholePart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The integral value of the number after truncation \nof the fractional part.\""]},"$nm":"wholePart"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is negative.\""]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the number is positive.\""]},"$nm":"positive"},"fractionalPart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The fractional part of the number, after truncation \nof the integral part.\""]},"$nm":"fractionalPart"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","\"if the number is too large to be represented \nas a `Float`\""],"doc":["\"The number, represented as a `Float`.\""]},"$nm":"float"}},"$nm":"Number"},"throws":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"when"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark methods as throwing an exception.\""]},"$nm":"throws"},"Numeric":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Invertable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float","Comparable"],"doc":["\"Abstraction of numeric types supporting addition,\nsubtraction, multiplication, and division, including\n`Integer` and `Float`. Additionally, a numeric type \nis expected to define a total order via an \nimplementation of `Comparable`.\""],"by":["\"Gavin\""]},"$m":{"minus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The difference between this number and the given \nnumber.\""]},"$nm":"minus"},"times":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The product of this number and the given number.\""]},"$nm":"times"},"divided":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Integral"],"doc":["\"The quotient obtained by dividing this number by \nthe given number. For integral numeric types, this \noperation results in a remainder.\""]},"$nm":"divided"}},"$nm":"Numeric","$st":"Other"},"Closeable":{"$mt":"ifc","$an":{"shared":[],"doc":["\"Abstract supertype of types which may appear\nas the expression type of a resource expression\nin a `try` statement.\""]},"$m":{"open":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Called before entry to a `try` block.\""]},"$nm":"open"},"close":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Called after completion of a `try` block.\""]},"$nm":"close"}},"$nm":"Closeable"},"IdentifiableObject":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"The default superclass when no superclass is explicitly\nspecified using `extends`. For the sake of convenience, \nthis class inherits a default definition of value\nequality from `Identifiable`.\""],"by":["\"Gavin\""]},"$nm":"IdentifiableObject"},"formal":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a type or member whose implementation must be provided\nby subtypes.\""]},"$nm":"formal"},"byDecreasing":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Value"}]},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byIncreasing"],"doc":["\"A comparator which orders elements in decreasing order \naccording to the `Comparable` returned by the given \n`comparable()` function.\""]},"$nm":"byDecreasing"},"default":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a type or member whose implementation may be overridden\nby subtypes. Non-`default` declarations may not be overridden.\""]},"$nm":"default"},"String":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"FixedSized"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Castable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["string"],"doc":["\"A string of characters. Each character in the string is \na 32-bit Unicode character. The internal UTF-16 \nencoding is hidden from clients.\n\nA string is a `Category` of its `Character`s, and of\nits substrings:\n\n    `w` in greeting \n    \"hello\" in greeting\n\nStrings are summable:\n\n    String greeting = \"hello\" + \" \" + \"world\";\n\nThey are efficiently iterable:\n\n    for (char in \"hello world\") { ... }\n\nThey are `List`s of `Character`s:\n\n    value char = \"hello world\"[5];\n\nThey are ranged:\n\n    String who = \"hello world\"[6...];\n\nNote that since `string[index]` evaluates to the \noptional type `Character?`, it is often more convenient\nto write `string[index..index]`, which evaluates to a\n`String` containing a single character, or to the empty\nstring \"\" if `index` refers to a position outside the\nstring.\n\nThe `string()` function makes it possible to use \ncomprehensions to transform strings:\n\n    string(for (s in \"hello world\") if (s.letter) s.uppercased)\n\nSince a `String` has an underlying UTF-16 encoding, \ncertain operations are expensive, requiring iteration\nof the characters of the string. In particular, `size`\nrequires iteration of the whole string, and `item()`,\n`span()`, and `segment()` require iteration from the \nbeginning of the string to the given index.\""],"by":["\"Gavin\""]},"$m":{"plus":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns the concatenation of this string with the\ngiven string.\""],"actual":[]},"$nm":"plus"},"firstCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The first index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string.\""]},"$nm":"firstCharacterOccurrence"},"startsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if this string starts with the given \nsubstring.\""]},"$nm":"startsWith"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if the given object is a string, and if\nso, if this string has the same length, and the \nsame characters, in the same order, as the given \nstring.\""],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the characters of this string beginning at \nthe given index, returning a string no longer than \nthe given length. If the portion of this string\nstarting at the given index is shorter than \nthe given length, return the portion of this string\nfrom the given index until the end of this string. Otherwise \nreturn a string of the given length. If the start\nindex is larger than the last index of the string,\nreturn the empty string.\""],"actual":[]},"$nm":"segment"},"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Compare this string with the given string \nlexicographically, according to the Unicode values\nof the characters.\""],"actual":[]},"$nm":"compare"},"lastCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The last index at which the given character occurs\nwithin this string, or `null` if the character does\nnot occur in this string.\""]},"$nm":"lastCharacterOccurrence"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"Determines if this string is longer than the given\nlength. This is a more efficient operation than\n`string.size>length`.\""]},"$nm":"longerThan"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if the given object is a `String` and, \nif so, if it occurs as a substring of this string,\nor if the object is a `Character` that occurs in\nthis string. That is to say, a string is considered \na `Category` of its substrings and of its \ncharacters.\""],"actual":[]},"$nm":"contains"},"repeat":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a string formed by repeating this string\nthe given number of times.\""]},"$nm":"repeat"},"join":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Join the given strings, using this string as a \nseparator.\""]},"$nm":"join"},"replace":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"replacement"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns a string formed by replacing every \noccurrence in this string of the given substring\nwith the given replacement string, working from \nthe start of this string to the end.\""]},"$nm":"replace"},"firstOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The first index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string.\""]},"$nm":"firstOccurrence"},"terminal":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the last characters of the string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length.\""]},"$nm":"terminal"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"Determines if this string is shorter than the given\nlength. This is a more efficient operation than\n`string.size>length`.\""]},"$nm":"shorterThan"},"initial":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the first characters of this string, \nreturning a string no longer than the given \nlength. If this string is shorter than the given\nlength, return this string. Otherwise return a\nstring of the given length.\""]},"$nm":"initial"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Character"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns the character at the given index in the \nstring, or `null` if the index is past the end of\nstring. The first character in the string occurs at\nindex zero. The last character in the string occurs\nat index `string.size-1`.\""],"actual":[]},"$nm":"item"},"occurrences":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The character indexes at which the given substring\noccurs within this string. Occurrences do not \noverlap.\""]},"$nm":"occurrences"},"lastOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"The last index at which the given substring occurs\nwithin this string, or `null` if the substring does\nnot occur in this string.\""]},"$nm":"lastOccurrence"},"split":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Callable"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"separator"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"discardSeparators"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"groupSeparators"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Split the string into tokens, using the given\npredicate to determine which characters are \nseparator characters.\""]},"$nm":"split"},"endsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Determines if this string ends with the given \nsubstring.\""]},"$nm":"endsWith"},"span":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Select the characters between the given indexes.\nIf the start index is the same as the end index,\nreturn a string with a single character.\nIf the start index is larger than the end index, \nreturn the characters in the reverse order from\nthe order in which they appear in this string.\nIf both the start index and the end index are \nlarger than the last index in the string, return \nthe empty string. Otherwise, if the last index is \nlarger than the last index in the sequence, return\nall characters from the start index to last \ncharacter of the string.\""],"actual":[]},"$nm":"span"}},"$at":{"normalized":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, after collapsing strings of whitespace \ninto single space characters and discarding whitespace \nfrom the beginning and end of the string.\""]},"$nm":"normalized"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"An iterator for the characters of the string.\""],"actual":[]},"$nm":"iterator"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, with all characters in lowercase.\""]},"$nm":"lowercased"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"hash"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, with all characters in uppercase.\""]},"$nm":"uppercased"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns this string.\""],"actual":[]},"$nm":"coalesced"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["longerThan","shorterThan"],"doc":["\"The length of the string (the number of characters\nit contains). In the case of the empty string, the\nstring has length zero. Note that this operation is\npotentially costly for long strings, since the\nunderlying representation of the characters uses a\nUTF-16 encoding.\""],"actual":[]},"$nm":"size"},"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"doc":["\"The index of the last character in the string, or\n`null` if the string has no characters. Note that \nthis operation is potentially costly for long \nstrings, since the underlying representation of the \ncharacters uses a UTF-16 encoding.\""],"actual":[]},"$nm":"lastIndex"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns the string itself.\""],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"Determines if this string has no characters, that\nis, if it has zero `size`. This is a more efficient \noperation than `string.size==0`.\""],"actual":[]},"$nm":"empty"},"lines":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Split the string into lines of text.\""]},"$nm":"lines"},"trimmed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, after discarding whitespace from the \nbeginning and end of the string.\""]},"$nm":"trimmed"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"This string, with the characters in reverse order.\""],"actual":[]},"$nm":"reversed"},"characters":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The characters in this string.\""]},"$nm":"characters"}},"$nm":"String"},"identical":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$an":{"shared":[],"see":["identityHash"],"doc":["\"Determine if the arguments are identical. Equivalent to\n`x===y`.\""]},"$nm":"identical"},"emptyIterator":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Bottom"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$an":{"doc":["\"An iterator which always returns `exhausted`.\""]},"$m":{"next":{"$t":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$nm":"emptyIterator"},"arrayOfNone":{"$t":{"comp":"i","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"None"}]},"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Create an empty array of the given element type.\""]},"$nm":"arrayOfNone"},"parseFloat":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Float"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"The `Float` value of the given string representation of \na decimal number or `null` if the string does not represent \na decimal number.\n\nThe syntax accepted by this method is the same as the \nsyntax for a `Float` literal in the Ceylon language except \nthat it may optionally begin with a sign character (`+` or \n`-`).\""]},"$nm":"parseFloat"},"Exception":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$ps":[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"String"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"description"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"cause"}],"$mt":"cls","$an":{"shared":[],"doc":["\"The supertype of all exceptions. A subclass represents\na more specific kind of problem, and may define \nadditional attributes which propagate information about\nproblems of that kind.\""],"by":["\"Gavin\"","\"Tom\""]},"$m":{"printStackTrace":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Print the stack trace to the standard error of\nthe virtual machine process.\""]},"$nm":"printStackTrace"}},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["description","cause"],"doc":["\"A message describing the problem. This default \nimplementation returns the description, if any, or \notherwise the message of the cause, if any.\""]},"$nm":"message"},"cause":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"attr","$an":{"shared":[],"doc":["\"The underlying cause of this exception.\""]},"$nm":"cause"},"description":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"attr","$an":{"doc":["\"A description of the problem.\""]},"$nm":"description"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"}},"$nm":"Exception"},"OverflowException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["\"Thrown when a mathematical operation caused a number to overflow from its bounds.\""]},"$nm":"OverflowException"},"Void":{"abstract":"1","of":[{"$pk":"ceylon.language","$nm":"Object"},{"$pk":"ceylon.language","$nm":"Nothing"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"The abstract supertype of all types. A value of type \n`Void` may be a definite value of type `Object`, or it \nmay be the `null` value. A method declared `void` is \nconsidered to have the return type `Void`.\n\nNote that the type `Bottom`, representing the \nintersection of all types, is a subtype of all types.\""],"by":["\"Gavin\""]},"$nm":"Void"},"parseInteger":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"The `Integer` value of the given string representation of an \ninteger, or `null` if the string does not represent an integer \nor if the mathematical integer it represents is too large in magnitude \nto be represented by an `Integer`.\n\nThe syntax accepted by this method is the same as the syntax for an \n`Integer` literal in the Ceylon language except that it may optionally \nbegin with a sign character (`+` or `-`).\""]},"$nm":"parseInteger"},"sum":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["Summable"],"doc":["\"Given a nonempty sequence of `Summable` values, \nreturn the sum of the values.\""]},"$nm":"sum"},"smaller":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["\"The value is smaller than the given value.\""],"by":["\"Gavin\""]},"$nm":"smaller"},"infinity":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"doc":["\"An instance of `Float` representing \npositive infinity ∞.\""]},"$nm":"infinity"},"Iterable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"ContainerWithFirstElement"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Collection"],"doc":["\"Abstract supertype of containers whose elements may be \niterated. An iterable container need not be finite, but\nits elements must at least be countable. There may not\nbe a well-defined iteration order, and so the order of\niterated elements may not be stable.\n\nAn instance of `Iterable` may be iterated using a `for`\nloop:\n\n    for (c in \"hello world\") { ... }\n\n`Iterable` and its subtypes define various operations\nthat return other iterable objects. Such operations \ncome in two flavors:\n\n- _Lazy_ operations return a \"view\" of the receiving\n  iterable object. If the underlying iterable object is\n  mutable, then changes to the underlying object will\n  be reflected in the resulting view. Lazy operations\n  are usually efficient, avoiding memory allocation or\n  iteration of the receiving iterable object.\n  \n- _Eager_ operations return an immutable object. If the\n  receiving iterable object is mutable, changes to this\n  object will not be reflected in the resulting \n  immutable object. Eager operations are often \n  expensive, involving memory allocation and iteration\n  of the receiving iterable object.\n\nLazy operations are preferred, because they can be \nefficiently chained. For example:\n\n    string.filter((Character c) c.letter).map((Character c) c.uppercased)\n\nis much less expensive than:\n\n    string.select((Character c) c.letter).collect((Character c) c.uppercased)\n\nFurthermore, it is always easy to produce a new \nimmutable iterable object given the view produced by a\nlazy operation. For example:\n\n    { string.filter((Character c) c.letter).map((Character c) c.uppercased)... }\n\nLazy operations normally return an instance of \n`Iterable` or `Map`.\n\nHowever, there are certain scenarios where an eager \noperation is more useful, more convenient, or no more \nexpensive than a lazy operation, including:\n\n- sorting operations, which are eager by nature,\n- operations which preserve emptiness/nonemptiness of\n  the receiving iterable object.\n\nEager operations normally return a sequence.\""],"by":["\"Gavin\""]},"$m":{"sort":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Comparison"}]},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["byIncreasing","byDecreasing"],"doc":["\"A sequence containing the elements of this\ncontainer, sorted according to a function \nimposing a partial order upon the elements.\n\nFor convenience, the functions `byIncreasing()` \nand `byDecreasing()` produce a suitable \ncomparison function:\n\n    \"Hello World!\".sort(byIncreasing((Character c) c.lowercased))\n\nThis operation is eager by nature.\""]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return the number of elements in this `Iterable` \nthat satisfy the predicate function.\""]},"$nm":"count"},"select":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["filter"],"doc":["\"A sequence containing the elements of this \ncontainer that satisfy the given predicate. An \neager counterpart to `filter()`.\""]},"$nm":"select"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"throws":["Exception","\"if the given step size is nonpositive, \ni.e. `step<1`\""],"doc":["\"Produce an `Iterable` containing every `step`th \nelement of this iterable object. If the step size \nis `1`, the `Iterable` contains the same elements \nas this iterable object. The step size must be \ngreater than zero. The expression\n\n    (0..10).by(3)\n\nresults in an iterable object with the elements\n`0`, `3`, `6`, and `9` in that order.\""]},"$nm":"by"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["\"The result of applying the accumulating function to \neach element of this container in turn.\""]},"$nm":"fold"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return `true` if all elements satisfy the predicate\nfunction.\""]},"$nm":"every"},"taking":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Produce an `Iterable` containing the first `take`\nelements of this iterable object. If the specified \nnumber of elements is larger than the number of \nelements of this iterable object, the `Iterable` \ncontains the same elements as this iterable object.\""]},"$nm":"taking"},"chain":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["\"The elements of this iterable object, in their\noriginal order, followed by the elements of the \ngiven iterable object also in their original\norder.\""]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Return `true` if at least one element satisfies the\npredicate function.\""]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["collect"],"doc":["\"An `Iterable` containing the results of applying\nthe given mapping to the elements of to this \ncontainer.\""]},"$nm":"map"},"group":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Grouping"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":"Grouping","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"grouping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Grouping"}],"$an":{"shared":[],"default":[],"doc":["\"Creates a Map that contains this `Iterable`'s\nelements, grouped in `Sequence`s under the\nkeys provided by the grouping function.\""]},"$nm":"group"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"The last element which satisfies the given\npredicate, if any, or `null` otherwise.\""]},"$nm":"findLast"},"filter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["select"],"doc":["\"An `Iterable` containing the elements of this \ncontainer that satisfy the given predicate.\""]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"The first element which satisfies the given \npredicate, if any, or `null` otherwise.\""]},"$nm":"find"},"skipping":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Produce an `Iterable` containing the elements of\nthis iterable object, after skipping the first \n`skip` elements. If this iterable object does not \ncontain more elements than the specified number of \nelements, the `Iterable` contains no elements.\""]},"$nm":"skipping"},"collect":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["map"],"doc":["\"A sequence containing the results of applying the\ngiven mapping to the elements of this container. An \neager counterpart to `map()`.\""]},"$nm":"collect"}},"$at":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"An iterator for the elements belonging to this \ncontainer.\""]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"The last element returned by the iterator, if any.\nIterables are potentially infinite, so calling this\nmight never return; also, this implementation will\niterate through all the elements, which might be\nvery time-consuming.\""],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"All entries of form `index->element` where `index` \nis the position at which `element` occurs, for every\nnon-null element of this `Iterable`, ordered by\nincreasing `index`. For a null element at a given\nposition in the original `Iterable`, there is no \nentry with the corresponding index in the resulting \niterable object. The expression \n\n    { \"hello\", null, \"world\" }.indexed\n    \nresults in an iterable object with the entries\n`0->\"hello\"` and `2->\"world\"`.\""]},"$nm":"indexed"},"sequence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"A sequence containing the elements returned by the\niterator.\""]},"$nm":"sequence"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"The non-null elements of this `Iterable`, in their\noriginal order. For null elements of the original \n`Iterable`, there is no entry in the resulting \niterable object.\""]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Determines if the iterable object is empty, that is\nto say, if the iterator returns no elements.\""],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns an iterable object containing all but the \nfirst element of this container.\""]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"The first element returned by the iterator, if any.\nThis should produce the same value as\n`ordered.iterator.head`.\""],"actual":[]},"$nm":"first"}},"$nm":"Iterable"},"LazyList":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"An implementation of List that wraps an `Iterable` of\nelements. All operations on this List are performed\non the Iterable.\""],"by":["\"Enrique Zamudio\""]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"item"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"gttr","$an":{"shared":[],"doc":["\"Returns a `List` with the elements of this\n`List` in reverse order. This operation will\ncreate copy the elements to a new `List`,\nso changes to the original `Iterable` will\nno longer be reflected in the new `List`.\""],"actual":[]},"$nm":"reversed"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"first"}},"$nm":"LazyList"},"className":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"obj"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Return the name of the concrete class of the given \nobject.\""]},"$nm":"className"},"List":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Sequence","Empty","Array"],"doc":["\"Represents a collection in which every element has a \nunique non-negative integer index.\n\nA `List` is a `Collection` of its elements, and a \n`Correspondence` from indices to elements.\n\nDirect access to a list element by index produces a\nvalue of optional type. The following idiom may be\nused instead of upfront bounds-checking, as long as \nthe list element type is a non-`null` type:\n\n    value char = \"hello world\"[index];\n    if (exists char) { /*do something*/ }\n    else { /*out of bounds*/ }\n\nTo iterate the indexes of a `List`, use the following\nidiom:\n\n    for (i->char in \"hello world\".indexed) { ... }\n\n\""]},"$m":{"withTrailing":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["\"Returns a new `List` that contains the specified\nelement appended to the end of this `List`s'\nelements.\""]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Determines if the given index refers to an element\nof this sequence, that is, if\n`index<=sequence.lastIndex`.\""],"actual":[]},"$nm":"defines"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["\"Two `List`s are considered equal iff they have the \nsame `size` and _entry sets_. The entry set of a \nlist `l` is the set of elements of `l.indexed`. \nThis definition is equivalent to the more intuitive \nnotion that two lists are equal iff they have the \nsame `size` and for every index either:\n\n- the lists both have the element `null`, or\n- the lists both have a non-null element, and the\n  two elements are equal.\""],"actual":[]},"$nm":"equals"},"item":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["\"Returns the element of this sequence with the given\nindex, or `null` if the given index is past the end\nof the sequence, that is, if\n`index>sequence.lastIndex`. The first element of\nthe sequence has index `0`.\""],"actual":[]},"$nm":"item"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["\"Returns a new `List` that starts with the specified\nelement, followed by the elements of this `List`.\""]},"$nm":"withLeading"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["\"The index of the last element of the list, or\nnull if the list is empty.\""]},"$nm":"lastIndex"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns the last element of this List, if any.\""],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Reverse this list, returning a new list.\""]},"$nm":"reversed"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["\"Returns the first element of this List, if any.\""],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"see":["lastIndex"],"doc":["\"The number of elements in this sequence, always\n`sequence.lastIndex+1`.\""],"actual":[]},"$nm":"size"}},"$nm":"List"},"Container":{"$mt":"ifc","$an":{"shared":[],"see":["Sized","Category"],"doc":["\"Abstract supertype of objects which may or may not\ncontain one of more other values, called *elements*.\n`Container` does not satisfy `Category`, because it is\nconceptually possible to have a container where the\n`contains()` operation is prohibitively inefficient.\n`Container` does not define a size, since it is possible\nto have a container of infinite or uncomputable size.\""],"by":["\"Gavin\""]},"$at":{"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"Determine if the container is empty, that is, if\nit has no elements.\""]},"$nm":"empty"}},"$nm":"Container"},"abstract":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a class as abstract.\n`abstract` classes may not be directly instantiated.\""]},"$nm":"abstract"},"zip":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"items"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"doc":["\"Given two sequences, form a new sequence consisting of\nall entries where, for any given index in the resulting\nsequence, the key of the entry is the element occurring \nat the same index in the first sequence, and the item \nis the element occurring at the same index in the second \nsequence. The length of the resulting sequence is the \nlength of the shorter of the two given sequences. \n\nThus:\n\n    zip(xs,ys)[i]==xs[i]->ys[i]\n\nfor every `0<=i<min({xs.size,ys.size})`.\""]},"$nm":"zip"},"export":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[]},"$nm":"export"},"equal":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["\"The value is exactly equal to the given value.\""],"by":["\"Gavin\""]},"$nm":"equal"},"Boolean":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"of":[{"$pk":"ceylon.language","$nm":"true"},{"$pk":"ceylon.language","$nm":"false"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"A type capable of representing the values true and\nfalse of Boolean logic.\""],"by":["\"Gavin\""]},"$nm":"Boolean"},"print":{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Void"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":[],"doc":["\"Print a line to the standard output of the virtual \nmachine process, printing the given value's `string`, \nor `«null»` if the value is `null`.\n\nThis method is a shortcut for:\n\n    process.writeLine(line?.string else \"«null»\")\n\nand is intended mainly for debugging purposes.\""],"by":["\"Gavin\""]},"$nm":"print"},"ChainedIterator":{"super":{"$pk":"ceylon.language","$nm":"IdentifiableObject"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"first"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"second"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$tp":[{"$nm":"Element"},{"$nm":"Other"}],"$an":{"shared":[],"doc":["\"An `Iterator` that returns the elements of two\n`Iterable`s, as if they were chained together.\""],"by":["\"Enrique Zamudio\""]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"more":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"more"},"iter":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"iter"}},"$nm":"ChainedIterator"},"arrayOfSome":{"$t":{"comp":"i","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Some"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"Create a nonempty array with the elements of the given\nsequence.\""]},"$nm":"arrayOfSome"},"Category":{"$mt":"ifc","$an":{"shared":[],"see":["Container"],"doc":["\"Abstract supertype of objects that contain other \nvalues, called *elements*, where it is possible to \nefficiently determine if a given value is an element. \n`Category` does not satisfy `Container`, because it is \nconceptually possible to have a `Category` whose \nemptiness cannot be computed.\n\nThe `in` operator may be used to determine if a value\nbelongs to a `Category`:\n\n    if (\"hello\" in \"hello world\") { ... }\n    if (69 in 0..100) { ... }\n    if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n\nOrdinarily, `x==y` implies that `x in cat == y in cat`.\nBut this contract is not required since it is possible\nto form a meaningful `Category` using a different\nequivalence relation. For example, an `IdentitySet` is\na meaningful `Category`.\""],"by":["\"Gavin\""]},"$m":{"containsAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["\"Determines if any of the given values belongs\nto this `Category`\""]},"$nm":"containsAny"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["containsEvery","containsAny"],"doc":["\"Determines if the given value belongs to this\n`Category`, that is, if it is an element of this\n`Category`.\n\nFor most `Category`s, if `x==y`, then \n`category.contains(x)` evaluates to the same\nvalue as `category.contains(y)`. However, it is\npossible to form a `Category` consistent with some \nother equivalence relation, for example `===`. \nTherefore implementations of `contains()` which do \nnot satisfy this relationship are tolerated.\""]},"$nm":"contains"},"containsEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["\"Determines if every one of the given values belongs\nto this `Category`.\""]},"$nm":"containsEvery"}},"$nm":"Category"},"see":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Void"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}]],"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to specify API references to other related API members.\""]},"$nm":"see"},"NegativeNumberException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["\"Thrown when a negative number is not allowed.\""]},"$nm":"NegativeNumberException"},"FixedSized":{"of":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"None"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Some"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["\"Represents a fixed-size collection which may or may not \nbe empty.\""]},"$nm":"FixedSized"},"actual":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"mthd","$an":{"shared":[],"doc":["\"Annotation to mark a type or member as overriding an other type/member.\""]},"$nm":"actual"},"Integer":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Integral"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Binary"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Exponentiable"},{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Integer"},{"$pk":"ceylon.language","$nm":"Float"}]}],"$pk":"ceylon.language","$nm":"Castable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["\"A 64-bit integer (or the closest approximation to a 64-bit integer \nprovided by the underlying platform).\""]},"$at":{"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["\"The UTF-32 character with this UCS code point.\""]},"$nm":"character"}},"$nm":"Integer"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["\"The first of the given elements (usually a comprehension),\nif any.\""]},"$nm":"first"}},"ceylon.language.metamodel":{"sequencedAnnotations":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequence"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"sequencedAnnotations"},"SequencedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequence"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["\"An annotation that may occur multiple times\nat a single program element.\""]},"$nm":"SequencedAnnotation"},"optionalAnnotation":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"optionalAnnotation"},"OptionalAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Nothing"},{"$nm":"Value"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["\"An annotation that may occur at most once\nat a single program element.\""]},"$nm":"OptionalAnnotation"},"Type":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Instance"}],"$an":{"shared":[]},"$nm":"Type"},"Annotation":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["\"An annotation.\""]},"$nm":"Annotation"},"ConstrainedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"variance":"out","$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["\"An annotation. This interface encodes\nconstraints upon the annotation in its\ntype arguments.\""]},"$m":{"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language.metamodel","$nm":"Annotated"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$an":{"shared":[]},"$nm":"occurs"}},"$nm":"ConstrainedAnnotation"},"annotations":{"$t":{"$nm":"Values"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$nm":"Value"},{"$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"annotations"},"Annotated":{"$mt":"ifc","$an":{"shared":[],"doc":["\"A program element that can\nbe annotated.\""]},"$nm":"Annotated"}}};
exports.$$metamodel$$=$$metamodel$$;
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
cons.prototype.getT$name$ = getT$name;
cons.prototype.getT$all$ = getT$all;
}
function initTypeProto(type, typeName) {
initType.apply(this, arguments);
var args = [].slice.call(arguments, 2);
args.unshift(type);
inheritProto.apply(this, args);
}
var initTypeProtoI = initTypeProto;
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
if (cons !== undefined) {
try {
cons.prototype.getT$name$ = getT$name;
cons.prototype.getT$all$ = getT$all;
} catch (exc) {
// browser probably prevented access to the prototype
}
}
}
function initExistingTypeProto(type, cons, typeName) {
var args = [].slice.call(arguments, 0);
args.push(IdentifiableObject);
initExistingType.apply(this, args);
var proto = cons.prototype;
if ((proto !== undefined) && (proto.getHash === undefined)) {
var origToString = proto.toString;
try {
inheritProto(type, IdentifiableObject);
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
var inheritProtoI = inheritProto;
exports.initType=initType;
exports.initTypeProto=initTypeProto;
exports.initTypeProtoI=initTypeProtoI;
exports.initExistingType=initExistingType;
exports.initExistingTypeProto=initExistingTypeProto;
exports.inheritProto=inheritProto;
exports.inheritProtoI=inheritProtoI;
function Void(wat) {
return wat;
}
initType(Void, 'ceylon.language::Void');
function Object$(wat) {
return wat;
}
initTypeProto(Object$, 'ceylon.language::Object', Void);
var Object$proto = Object$.$$.prototype;
Object$proto.getString = function() { return String$(className(this) + "@" + this.getHash()); }
//Object$proto.getString=function() { String$(Object.prototype.toString.apply(this)) };
Object$proto.toString=function() { return this.getString().valueOf(); }
var identifiableObjectID=1;
function $identityHash(x) {
var hash = x.identifiableObjectID;
return (hash !== undefined)
? hash : (x.identifiableObjectID = identifiableObjectID++);
}
function Identifiable(obj) {}
initType(Identifiable, "ceylon.language::Identifiable");
var Identifiable$proto = Identifiable.$$.prototype;
Identifiable$proto.equals = function(that) {
return isOfType(that, 'ceylon.language::Identifiable') && (that===this);
}
Identifiable$proto.getHash = function() { return $identityHash(this); }
function IdentifiableObject(obj) {
return obj;
}
initTypeProto(IdentifiableObject, 'ceylon.language::IdentifiableObject', Object$, Identifiable);
//INTERFACES
function Cloneable(wat) {
return wat;
}
initType(Cloneable, 'ceylon.language::Cloneable');
exports.Cloneable=Cloneable;
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
function Castable(wat) {
return wat;
}
initType(Castable, 'ceylon.language::Castable');
exports.Castable=Castable;
function Closeable(wat) {
return wat;
}
initType(Closeable, 'ceylon.language::Closeable');
exports.Closeable=Closeable;
function Comparable(wat) {
return wat;
}
initType(Comparable, 'ceylon.language::Comparable');
/*var Comparable$proto = Comparable.$$.prototype;
Comparable$proto.largerThan = function(other) {
return Boolean$(this.compare(other)===$larger);
}
Comparable$proto.smallerThan = function(other) {
return Boolean$(this.compare(other)===$smaller);
}
Comparable$proto.asLargeAs = function(other) {
return Boolean$(this.compare(other)!=$smaller);
}
Comparable$proto.asSmallAs = function(other) {
return Boolean$(this.compare(other)!=$larger);
}*/
exports.Comparable=Comparable;
function Container(wat) {
return wat;
}
initType(Container, 'ceylon.language::Container');
exports.Container=Container;
function ContainerWithFirstElement(wat) {
return wat;
}
initType(ContainerWithFirstElement, 'ceylon.language::ContainerWithFirstElement');
exports.ContainerWithFirstElement=ContainerWithFirstElement;
function Correspondence(wat) {
return wat;
}
initType(Correspondence, 'ceylon.language::Correspondence');
var Correspondence$proto=Correspondence.$$.prototype;
Correspondence$proto.defines = function(key) {
return exists(this.item(key));
}
Correspondence$proto.definesEvery = function(keys) {
if (keys === undefined) return true;
for (var i=0; i<keys.length; i++) {
if (!this.defines(keys[i])) {
return false;
}
}
return true;
}
Correspondence$proto.definesAny = function(keys) {
if (keys === undefined) return true;
for (var i=0; i<keys.length; i++) {
if (this.defines(keys[i])) {
return true;
}
}
return false;
}
Correspondence$proto.items = function(keys) {
if (nonempty(keys)) {
var r=[];
for (var i = 0; i < keys.length; i++) {
r.push(this.item(keys[i]));
}
return ArraySequence(r);
}
return $empty;
}
Correspondence$proto.keys = function() {
return TypeCategory(this, 'ceylon.language::Integer');
}
exports.Correspondence=Correspondence;
function Iterator(wat) {
return wat;
}
initType(Iterator, 'ceylon.language::Iterator');
exports.Iterator=Iterator;
function Iterable(wat) {
return wat;
}
initTypeProtoI(Iterable, 'ceylon.language::Iterable', ContainerWithFirstElement);
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
return ArraySequence(a);
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
return ArraySequence(a);
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
IdentifiableObject($cmp$);
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
initTypeProto(skip$iter, 'ceylon.language::SkipIterable', IdentifiableObject, Iterable);
return skip$iter(this,skip);
}
Iterable$proto.taking = function(take) {
if (take <= 0) return $empty;
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
Iterable$proto.group = function(grouping) {
var map = HashMap();
var it = this.getIterator();
var elem;
var newSeq = ArraySequence([]);
while ((elem=it.next()) !== $finished) {
var key = grouping(elem);
var seq = map.put(Entry(key, newSeq), true);
if (seq === null) {
seq = newSeq;
newSeq = ArraySequence([]);
}
seq.push(elem);
}
return map;
}
exports.Iterable=Iterable;
function ChainedIterator(i1, i2) {
var that = new ChainedIterator.$$;
that.i1=i1;
that.i2=i2;
that.more=true;
return that;
}
initTypeProto(ChainedIterator, 'ceylon.language::ChainedIterator', IdentifiableObject, Iterator);
ChainedIterator.$$.prototype.next = function() {
if (this.iter === undefined) {
this.iter = this.i1.getIterator();
}
var e = this.iter.next();
if (e === $finished && this.more) {
this.more=false;
this.iter=this.i2.getIterator();
e=this.iter.next();
}
return e;
}
exports.ChainedIterator=ChainedIterator;
Iterable$proto.chain = function(other) {
return ChainedIterable(this, other);
}
exports.Iterable=Iterable;
function ChainedIterable(first, second, chained) {
if (chained===undefined) {chained = new ChainedIterable.$$;}
IdentifiableObject(chained);
chained.first = first;
chained.second = second;
return chained;
}
initTypeProto(ChainedIterable, "ceylon.language::ChainedIterable",
IdentifiableObject, Iterable);
var ChainedIterable$proto = ChainedIterable.$$.prototype;
ChainedIterable$proto.getIterator = function() {
return ChainedIterator(this.first, this.second);
}
function Sized(wat) {
return wat;
}
initTypeProtoI(Sized, 'ceylon.language::Sized', Container);
Sized.$$.prototype.getEmpty = function() {
return this.getSize() == 0;
}
exports.Sized=Sized;
function Category(wat) {
return wat;
}
initType(Category, 'ceylon.language::Category');
Category.$$.prototype.containsEvery = function(keys) {
if (keys === undefined) return true;
for (var i = 0; i < keys.length; i++) {
if (!this.contains(keys[i])) {
return false;
}
}
return true;
}
Category.$$.prototype.containsAny = function(keys) {
if (keys === undefined) return true;
for (var i = 0; i < keys.length; i++) {
if (this.contains(keys[i])) {
return true;
}
}
return false;
}
exports.Category=Category;
function Collection(wat) {
return wat;
}
initTypeProtoI(Collection, 'ceylon.language::Collection', Iterable, Sized, Category, Cloneable);
var Collection$proto = Collection.$$.prototype;
Collection$proto.contains = function(obj) {
var iter = this.getIterator();
var item;
while ((item = iter.next()) !== $finished) {
if (exists(item) && item.equals(obj)) {
return true;
}
}
return false;
}
Collection$proto.getString = function() {
var s = '{';
var first = true;
var iter = this.getIterator();
var item;
while ((item = iter.next()) !== $finished) {
s += first ? ' ' : ', ';
if (exists(item)) {
s += item.getString();
} else {
s += 'null';
}
first = false;
}
if (!first) {
s += ' ';
}
s += '}';
return String$(s);
}
exports.Collection=Collection;
function FixedSized(wat) {
return wat;
}
initTypeProtoI(FixedSized, 'ceylon.language::FixedSized', Collection);
var FixedSized$proto = FixedSized.$$.prototype;
FixedSized$proto.getFirst = function() {
var e = this.getIterator().next();
return e === $finished ? null : e;
}
exports.FixedSized=FixedSized;
function Some(wat) {
return wat;
}
initTypeProtoI(Some, 'ceylon.language::Some', FixedSized, ContainerWithFirstElement);
var $Some = Some.$$;
$Some.prototype.getFirst = function() {
var e = this.getIterator().next();
if (e === $finished) throw Exception();
return e;
}
$Some.prototype.getEmpty = function() { return false; }
$Some.prototype.getFirst = function() {
var _e = this.getIterator().next();
if (_e === $finished) throw Exception(String$("Some.first should never get Finished!"));
return _e;
}
exports.Some=Some;
function None(wat) {
return wat;
}
initTypeProtoI(None, 'ceylon.language::None', FixedSized, ContainerWithFirstElement);
var None$proto = None.$$.prototype;
None$proto.getFirst = function() { return null; }
None$proto.getIterator = function() { return emptyIterator; }
None$proto.getSize = function() { return 0; }
None$proto.getEmpty = function() { return true; }
None$proto.getFirst = function() { return null; }
exports.None=None;
function Ranged(wat) {
return wat;
}
initType(Ranged, 'ceylon.language::Ranged');
exports.Ranged=Ranged;
function List(wat) {
return wat;
}
initTypeProtoI(List, 'ceylon.language::List', Collection, Correspondence, Ranged, Cloneable);
var List$proto = List.$$.prototype;
List$proto.getSize = function() {
var li = this.getLastIndex();
return li === null ? 0 : li.getSuccessor();
}
List$proto.defines = function(idx) {
var li = this.getLastIndex();
if (li === null) li = -1;
return li.compare(idx) !== smaller;
}
List$proto.getIterator = function() {
return ListIterator(this);
}
List$proto.equals = function(other) {
if (isOfType(other, 'ceylon.language::List') && other.getSize().equals(this.getSize())) {
for (var i = 0; i < this.getSize(); i++) {
var mine = this.item(i);
var theirs = other.item(i);
if (((mine === null) && theirs) || !(mine && mine.equals(theirs))) {
return false;
}
}
return true;
}
return false;
}
List$proto.getHash = function() {
var hc=1;
var iter=this.getIterator();
var e; while ((e = iter.next()) != $finished) {
hc*=31;
if (e !== null) {
hc += e.getHash();
}
}
return hc;
}
List$proto.findLast = function(select) {
var li = this.getLastIndex();
if (li !== null) {
while (li>=0) {
var e = this.item(li);
if (e !== null && select(e)) {
return e;
}
li = li.getPredecessor();
}
}
return null;
}
List$proto.withLeading = function(other) {
var sb = SequenceBuilder();
sb.append(other);
sb.appendAll(this);
return sb.getSequence();
}
List$proto.withTrailing = function(other) {
var sb = SequenceBuilder();
sb.appendAll(this);
sb.append(other);
return sb.getSequence();
}
exports.List=List;
function ListIterator(list) {
var that = new ListIterator.$$;
that.list=list;
that.index=0;
that.lastIndex=list.getLastIndex();
if (that.lastIndex === null) {
that.lastIndex = -1;
} else {
that.lastIndex = that.lastIndex;
}
return that;
}
initTypeProtoI(ListIterator, 'ceylon.language::ListIterator', Iterator);
ListIterator.$$.prototype.next = function() {
if (this.index <= this.lastIndex) {
return this.list.item(this.index++);
}
return $finished;
}
function Empty() {
var that = new Empty.$$;
that.value = [];
return that;
}
initTypeProtoI(Empty, 'ceylon.language::Empty', None, Ranged, Cloneable, List);
var Empty$proto = Empty.$$.prototype;
Empty$proto.getEmpty = function() { return true; }
Empty$proto.defines = function(x) { return false; }
Empty$proto.getKeys = function() { return TypeCategory(this, 'ceylon.language::Integer'); }
Empty$proto.definesEvery = function(x) { return false; }
Empty$proto.definesAny = function(x) { return false; }
Empty$proto.items = function(x) { return this; }
Empty$proto.getSize = function() { return 0; }
Empty$proto.item = function(x) { return null; }
Empty$proto.getFirst = function() { return null; }
Empty$proto.segment = function(a,b) { return this; }
Empty$proto.span = function(a,b) { return this; }
Empty$proto.getIterator = function() { return emptyIterator; }
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
return new ArraySequence([other]);
}
Empty$proto.withTrailing = function(other) {
return new ArraySequence([other]);
}
Empty$proto.chain = function(other) { return other; }
var $empty = Empty();
function EmptyIterator() {
var that = new EmptyIterator.$$;
return that;
}
initTypeProto(EmptyIterator, 'ceylon.language::EmptyIterator', IdentifiableObject, Iterator);
var EmptyIterator$proto = EmptyIterator.$$.prototype;
EmptyIterator$proto.next = function() { return $finished; }
var emptyIterator=EmptyIterator();
exports.empty=$empty;
exports.Empty=Empty;
exports.emptyIterator=emptyIterator;
function Comprehension(makeNextFunc, compr) {
if (compr===undefined) {compr = new Comprehension.$$;}
IdentifiableObject(compr);
compr.makeNextFunc = makeNextFunc;
return compr;
}
initTypeProto(Comprehension, 'ceylon.language::Comprehension', IdentifiableObject, Iterable);
var Comprehension$proto = Comprehension.$$.prototype;
Comprehension$proto.getIterator = function() {
return ComprehensionIterator(this.makeNextFunc());
}
exports.Comprehension=Comprehension;
function ComprehensionIterator(nextFunc, it) {
if (it===undefined) {it = new ComprehensionIterator.$$;}
IdentifiableObject(it);
it.next = nextFunc;
return it;
}
initTypeProto(ComprehensionIterator, 'ceylon.language::ComprehensionIterator',
IdentifiableObject, Iterator);
function ChainedIterator(first, second, chained) {
if (chained===undefined) {chained = new ChainedIterator.$$;}
IdentifiableObject(chained);
chained.it = first.getIterator();
chained.second = second;
return chained;
}
initTypeProto(ChainedIterator, 'ceylon.language::ChainedIterator',
IdentifiableObject, Iterator);
var ChainedIterator$proto = ChainedIterator.$$.prototype;
ChainedIterator$proto.next = function() {
var elem = this.it.next();
if ((elem===$finished) && (this.second!==undefined)) {
this.it = this.second.getIterator();
this.second = undefined;
elem = this.it.next();
}
return elem;
}
exports.ChainedIterator=ChainedIterator;
function LazyList(elems, lst) {
if (lst===undefined) {lst = new LazyList.$$;}
IdentifiableObject(lst);
lst.elems = elems===undefined?$empty:elems;
return lst;
}
initTypeProto(LazyList, 'ceylon.language::LazyList', IdentifiableObject, List);
var LazyList$proto = LazyList.$$.prototype;
LazyList$proto.getIterator = function() { return this.elems.getIterator(); }
LazyList$proto.getClone = function() { return this; }
LazyList$proto.getFirst = function() { return this.elems.getFirst(); }
LazyList$proto.getLast = function() { return this.elems.getLast(); }
LazyList$proto.findLast = function(selecting) {
return this.elems.findLast(selecting);
}
LazyList$proto.getSize = function() {
if (isOfType(this.elems, 'ceylon.language::Sized')) {
return this.elems.getSize();
}
var it = this.elems.getIterator();
var count = 0;
while (it.next() !== $finished) {++count;}
return count;
}
LazyList$proto.getLastIndex = function() {
var size = this.getSize();
return size>0 ? (size-1) : null;
}
LazyList$proto.getEmpty = function() { return this.elems.getEmpty(); }
LazyList$proto.item = function(index) {
var it = this.elems.getIterator();
var elem;
var count = 0;
while ((elem=it.next()) !== $finished) {
if (count++ == index) { return elem; }
}
return null;
}
LazyList$proto.getReversed = function() { return this.elems.getSequence().getReversed(); }
LazyList$proto.equals = function(other) {
if (!isOfType(other, 'ceylon.language::List')) { return false; }
var it1 = this.elems.getIterator();
var it2 = other.getIterator();
var e1;
while ((e1=it1.next()) !== $finished) {
var e2 = it2.next();
var n1 = e1===null;
if ((n1 ^ (e2===null)) || !(n1 || e1.equals(e2))) { return false; }
}
return it2.next()===$finished;
}
LazyList$proto.segment = function(from, length) {
if (length <= 0) { return $empty; }
var seg = (from > 0) ? this.elems.skipping(from) : this.elems;
return LazyList(seg.taking(length));
}
LazyList$proto.span = function(from, to) {
if ((to === undefined) || (to === null)) {
return (from > 0) ? LazyList(this.elems.skipping(from)) : this;
}
if (to < 0) { to = 0; }
if (from < 0) { from = 0; }
if (to < from) {
var seq = (to > 0) ? this.elems.skipping(to) : this.elems;
return seq.taking(from-to+1).getSequence().getReversed();
}
var seq = (from > 0) ? this.elems.skipping(from) : this.elems;
return LazyList(seq.taking(to-from+1));
}
exports.LazyList=LazyList;
function Set(wat) {
return wat;
}
initTypeProtoI(Set, 'ceylon.language::Set', Collection, Cloneable);
var Set$proto = Set.$$.prototype;
Set$proto.superset = function(set) {
var iter = set.getIterator();
var elem; while ((elem = iter.next()) !== $finished) {
if (!this.contains(elem)) {
return false;
}
}
return true;
}
Set$proto.subset = function(set) {
var iter = this.getIterator();
var elem; while ((elem = iter.next()) !== $finished) {
if (!set.contains(elem)) {
return false;
}
}
return true;
}
Set$proto.equals = function(other) {
if (isOfType(other, 'ceylon.language::Set')) {
if (other.getSize().equals(this.getSize())) {
var iter = this.getIterator();
var elem; while ((elem = iter.next()) !== $finished) {
if (!other.contains(elem)) {
return false;
}
}
return true;
}
}
return false;
}
Set$proto.getHash = function() {
var hc = 1;
var iter=this.getIterator();
var elem;while((elem=iter.next())!=$finished) {
hc*=31;
hc+=elem.getHash();
}
return hc;
}
exports.Set=Set;
function Map(wat) {
return wat;
}
initTypeProtoI(Map, 'ceylon.language::Map', Collection, Correspondence, Cloneable);
var Map$proto = Map.$$.prototype;
Map$proto.equals = function(other) {
if (isOfType(other, 'ceylon.language::Map') && other.getSize().equals(this.getSize())) {
var iter = this.getIterator();
var entry; while ((entry = iter.next()) !== $finished) {
var oi = other.item(entry.getKey());
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
function MapValues(map) {
var val = new MapValues.$$;
val.map = map;
return val;
}
initTypeProto(MapValues, 'ceylon.language::MapValues', IdentifiableObject, Collection);
var MapValues$proto = MapValues.$$.prototype;
MapValues$proto.getSize = function() { return this.map.getSize(); }
MapValues$proto.getEmpty = function() { return this.map.getEmpty(); }
MapValues$proto.getClone = function() { return this; }
MapValues$proto.getIterator = function() { return MapValuesIterator(this.map); }
function MapValuesIterator(map) {
var iter = new MapValuesIterator.$$;
iter.it = map.getIterator();
return iter;
}
initTypeProto(MapValuesIterator, 'ceylon.language::MapValuesIterator', IdentifiableObject, Iterator);
MapValuesIterator.$$.prototype.next = function() {
var entry = this.it.next();
return (entry!==$finished) ? entry.getItem() : $finished;
}
Map$proto.getKeys = function() { return KeySet(this); }
function KeySet(map) {
var set = new KeySet.$$;
set.map = map;
return set;
}
initTypeProto(KeySet, 'ceylon.language::KeySet', IdentifiableObject, Set);
var KeySet$proto = KeySet.$$.prototype;
KeySet$proto.getSize = function() { return this.map.getSize(); }
KeySet$proto.getEmpty = function() { return this.map.getEmpty(); }
KeySet$proto.contains = function(elem) { return this.map.defines(elem); }
KeySet$proto.getClone = function() { return this; }
KeySet$proto.getIterator = function() { return KeySetIterator(this.map); }
function KeySetIterator(map) {
var iter = new KeySetIterator.$$;
iter.it = map.getIterator();
return iter;
}
initTypeProto(KeySetIterator, 'ceylon.language::KeySetIterator', IdentifiableObject, Iterator);
KeySetIterator.$$.prototype.next = function() {
var entry = this.it.next();
return (entry!==$finished) ? entry.getKey() : $finished;
}
KeySet$proto.union = function(other) {
var set = hashSetFromMap(this.map);
set.addAll(other);
return set;
}
KeySet$proto.intersection = function(other) {
var set = HashSet();
var it = this.map.getIterator();
var entry;
while ((entry=it.next()) !== $finished) {
var key = entry.getKey();
if (other.contains(key)) { set.add(key); }
}
return set;
}
KeySet$proto.exclusiveUnion = function(other) {
var set = this.complement(other);
var it = other.getIterator();
var elem;
while ((elem=it.next()) !== $finished) {
if (!this.map.defines(elem)) { set.add(elem); }
}
return set;
}
KeySet$proto.complement = function(other) {
var set = HashSet();
var it = this.map.getIterator();
var entry;
while ((entry=it.next()) !== $finished) {
var key = entry.getKey();
if (!other.contains(key)) { set.add(key); }
}
return set;
}
Map$proto.getInverse = function() {
var inv = HashMap();
var it = this.getIterator();
var entry;
var newSet = HashSet();
while ((entry=it.next()) !== $finished) {
var item = entry.getItem();
var set = inv.put(Entry(item, newSet), true);
if (set === null) {
set = newSet;
newSet = HashSet();
}
set.add(entry.getKey());
}
return inv;
}
Map$proto.mapItems = function(mapping) {
function EmptyMap(orig) {
var em = new EmptyMap.$$;
IdentifiableObject(em);
em.orig=orig;
em.clone=function() { return this; }
em.getItem=function() { return null; }
em.getIterator=function() {
function miter(iter) {
var $i = new miter.$$;
$i.iter = iter;
$i.next = function() {
var e = this.iter.next();
return e===$finished ? e : Entry(e.getKey(), mapping(e.getKey(), e.getItem()));
};
return $i;
}
initTypeProto(miter, 'ceylon.language::MappedIterator', IdentifiableObject, Iterator);
return miter(orig.getIterator());
}
em.getSize=function() { return this.orig.getSize(); }
em.getString=function() { return String$('',0); }
return em;
}
initTypeProto(EmptyMap, 'ceylon.language::EmptyMap', IdentifiableObject, Map);
return EmptyMap(this);
}
exports.Map=Map;
function HashMap(entries, map) {
if (map===undefined) { map = new HashMap.$$; }
IdentifiableObject(map);
map.map = {};
map.size = 0;
if (entries !== undefined) { map.putAll(entries); }
return map;
}
initTypeProto(HashMap, 'ceylon.language::HashMap', IdentifiableObject, Map);
function copyHashMap(orig) {
var map = HashMap();
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
HashMap$proto.item = function(key) {
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
if (isOfType(elem, 'ceylon.language::Entry')) {
var item = this.item(elem.getKey());
if (item !== null) { return item.equals(elem.getItem()); }
}
return false;
}
HashMap$proto.defines = function(key) { return this.item(key) !== null; }
function HashSet(elems, set) {
if (set===undefined) { set = new HashSet.$$; }
IdentifiableObject(set);
set.map = HashMap();
if (elems !== undefined) { set.addAll(elems); }
return set;
}
initTypeProto(HashSet, 'ceylon.language::HashSet', IdentifiableObject, Set);
function hashSetFromMap(map) {
var set = new HashSet.$$;
IdentifiableObject(set);
set.map = this;
return set;
}
var HashSet$proto = HashSet.$$.prototype;
HashSet$proto.add = function(elem) { this.map.put(Entry(elem, true)); }
HashSet$proto.addAll = function(elems) {
var it = elems.getIterator();
var elem;
while ((elem=it.next()) !== $finished) { this.map.put(Entry(elem, true)); }
}
HashSet$proto.getSize = function() { return this.map.size; }
HashSet$proto.getEmpty = function() { return this.map.size===0; }
HashSet$proto.getLast = function() {
var entry = this.map.getLast();
return (entry !== null) ? entry.getKey() : null;
}
HashSet$proto.getIterator = function() { return HashSetIterator(this.map); }
HashSet$proto.getClone = function() { return this; }
HashSet$proto.contains = function(elem) { return this.map.item(elem) !== null; }
HashSet$proto.union = function(other) {
var set = hashSetFromMap(copyHashMap(this.map));
set.addAll(other);
return set;
}
HashSet$proto.intersection = function(other) {
var set = HashSet();
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
if (this.map.item(elem) === null) { set.map.put(Entry(elem, true)); }
}
return set;
}
HashSet$proto.complement = function(other) {
var set = HashSet();
var it = this.getIterator();
var elem;
while ((elem=it.next()) !== $finished) {
if (!other.contains(elem)) { set.map.put(Entry(elem, true)); }
}
return set;
}
function HashMapIterator(map) {
var it = new HashMapIterator.$$;
it.map = map;
it.hashs = Object.keys(map);
it.hashIndex = 0;
it.arrIndex = 0;
return it;
}
initTypeProto(HashMapIterator, 'ceylon.language::HashMapIterator', IdentifiableObject, Iterator);
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
function HashSetIterator(map) {
var it = new HashSetIterator.$$;
it.mapIt = map.getIterator();
return it;
}
initTypeProto(HashSetIterator, 'ceylon.language::HashSetIterator', IdentifiableObject, Iterator);
HashSetIterator.$$.prototype.next = function() {
var entry = this.mapIt.next();
return (entry !== $finished) ? entry.getKey() : $finished;
}
function LazyMap(entries, map) {
if (map===undefined) {map = new LazyMap.$$;}
IdentifiableObject(map);
map.entries = entries===undefined?$empty:entries;
return map;
}
initTypeProto(LazyMap, 'ceylon.language::LazyMap', IdentifiableObject, Map);
var LazyMap$proto = LazyMap.$$.prototype;
LazyMap$proto.getEmpty = function() { return this.entries.getEmpty(); }
LazyMap$proto.getSize = function() {
var it = this.entries.getIterator();
var count = 0;
while (it.next() !== $finished) { ++count; }
return count;
}
LazyMap$proto.getClone = function() { return this; }
LazyMap$proto.getIterator = function() { return this.entries.getIterator(); }
LazyMap$proto.item = function(key) {
var it = this.entries.getIterator();
var entry;
while ((entry=it.next()) !== $finished) {
if (entry.getKey().equals(key)) { return entry.getItem(); }
}
return null;
}
LazyMap$proto.equals = function(other) {
var hmap = HashMap(this.entries);
var it = other.getIterator();
var count = 0;
var entry;
while ((entry=it.next()) !== $finished) {
if (!hmap.contains(entry)) { return false; }
++count;
}
return count==hmap.getSize();
}
exports.LazyMap=LazyMap;
function LazySet(elems, set) {
if (set===undefined) {set = new LazySet.$$;}
IdentifiableObject(set);
set.elems = elems===undefined?$empty:elems;
return set;
}
initTypeProto(LazySet, 'ceylon.language::LazySet', IdentifiableObject, Set);
var LazySet$proto = LazySet.$$.prototype;
LazySet$proto.getEmpty = function() { return this.elems.getEmpty(); }
LazySet$proto.getSize = function() { return HashSet(this.elems).getSize(); }
LazySet$proto.getClone = function() { return this; }
LazySet$proto.getIterator = function() { return HashSet(this.elems).getIterator(); }
LazySet$proto.equals = function(other) {
var hset = HashSet(this.elems);
var it = other.getIterator();
var elem;
var count = 0;
while ((elem=it.next()) !== $finished) {
if (!hset.contains(elem)) { return false; }
++count;
}
return count==hset.getSize();
}
LazySet$proto.union = function(other) {
var set = HashSet(this.elems);
set.addAll(other);
return set;
}
LazySet$proto.intersection = function(other) {
var set = HashSet(this.elems);
var result = HashSet();
var it = other.getIterator();
var elem;
while ((elem=it.next()) !== $finished) {
if (set.contains(elem)) { result.add(elem); }
}
return result;
}
LazySet$proto.exclusiveUnion = function(other) {
return other.exclusiveUnion(HashSet(this.elems));
}
LazySet$proto.complement = function(other) {
return other.complement(HashSet(this.elems));
}
exports.LazySet=LazySet;
function Summable(wat) {
return wat;
}
initType(Summable, 'ceylon.language::Summable');
exports.Summable=Summable;
function Number$(wat) {
return wat;
}
initType(Number$, 'ceylon.language::Number');
exports.Number=Number$;
function Invertable(wat) {
return wat;
}
initType(Invertable, 'ceylon.language::Invertable');
exports.Invertable=Invertable;
function Numeric(wat) {
return wat;
}
initTypeProto(Numeric, 'ceylon.language::Numeric', Number$, Comparable, Summable, Invertable);
exports.Numeric=Numeric;
function Ordinal(wat) {
return wat;
}
initType(Ordinal, 'ceylon.language::Ordinal');
exports.Ordinal=Ordinal;
function Integral(wat) {
return wat;
}
initTypeProto(Integral, 'ceylon.language::Integral', Numeric, Ordinal);
exports.Integral=Integral;
function Scalar(scalar) { return scalar; }
initTypeProto(Scalar, 'ceylon.language::Scalar', Numeric, Comparable, Number$);
exports.Scalar=Scalar;
function Exponentiable(exp) { return exp; }
initTypeProto(Exponentiable, 'ceylon.language::Exponentiable', Numeric);
exports.Exponentiable=Exponentiable;
function Exception(description, cause, wat) {
if (wat===undefined) {wat=new Exception.$$;}
wat.description = description;
wat.cause = cause;
return wat;
}
initTypeProto(Exception, 'ceylon.language::Exception', IdentifiableObject);
var Exception$proto = Exception.$$.prototype;
Exception$proto.getCause = function() {return this.cause;}
Exception$proto.getMessage = function() {
return this.description ? this.description
: (this.cause ? this.cause.getMessage() : String$("", 0));
}
Exception$proto.getString = function() {
return String$(className(this) + ' "' + this.getMessage() + '"');
}
//New Binary interface
function Binary(wat) {
return wat;
}
initTypeProtoI(Binary, 'ceylon.language::Binary');
Binary.$$.prototype.clear = function(index) {
return this.set(index, false);
}
exports.Binary=Binary;
function JSNumber(value) { return Number(value); }
initExistingType(JSNumber, Number, 'ceylon.language::JSNumber');
var origNumToString = Number.prototype.toString;
inheritProtoI(JSNumber, Object$, Scalar, Castable, Integral, Exponentiable);
function Integer(value) { return Number(value); }
initTypeProto(Integer, 'ceylon.language::Integer', Object$, Scalar, Castable,
Integral, Exponentiable, Binary);
function Float(value) {
var that = new Number(value);
that.$float = true;
return that;
}
initTypeProto(Float, 'ceylon.language::Float', Object$, Scalar, Castable, Exponentiable);
var JSNum$proto = Number.prototype;
JSNum$proto.getT$all$ = function() {
return (this.$float ? Float : Integer).$$.T$all;
}
JSNum$proto.getT$name$ = function() {
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
initExistingType(String$, String, 'ceylon.language::String', Object$, Comparable,
Ranged, FixedSized, Summable, Castable, Cloneable, List);
var origStrToString = String.prototype.toString;
inheritProtoI(String$, Object$, Comparable, Ranged, FixedSized, Summable, Castable,
Cloneable, List);
function StringOfSome() {}
initType(StringOfSome, "ceylon.language::StringOfSome", String$, Some);
function StringOfNone() {}
initType(StringOfNone, "ceylon.language::StringOfNone", String$, None);
var String$proto = String$.$$.prototype;
String$proto.getT$name$ = function() {
return ((this.length!==0)?StringOfSome:StringOfNone).$$.T$name;
}
String$proto.getT$all$ = function() {
return ((this.length!==0)?StringOfSome:StringOfNone).$$.T$all;
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
var toIndex = (to===null || to===undefined) ? 0x7fffffff : to;
if (from > toIndex) {
return this.segment(toIndex, from-toIndex+1).getReversed();
}
return this.segment(from, toIndex-from+1);
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
return this.length === 0 ? emptyIterator : StringIterator(this);
}
String$proto.item = function(index) {
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
return this.item(0);
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
//we can cheat and add the required behavior to String, avoiding the need to create a Sequence...
//TODO: this probably doesn't work completely because String doesn't satisfy
//      all required interfaces, so "if(is ...)" will be false when it shouldn't.
return this.length>0 ? this:$empty;
}
String$proto.getFirst = function() { return this.getSize()>0?this.item(0):null; }
String$proto.getLast = function() { return this.getSize()>0?this.item(this.getSize().getPredecessor()):null; }
String$proto.getKeys = function() {
//TODO implement!!!
return this.getSize() > 0 ? Range(0, this.getSize().getPredecessor()) : $empty;
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
if (this.length === 0) {return Singleton(this);}
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
if (isOfType(sep, 'ceylon.language::Iterable')) {
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
return ArraySequence(tokens);
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
return ocs.length > 0 ? ocs : $empty;
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
if (take==0) return $empty;
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
initTypeProto(StringIterator, 'ceylon.language::StringIterator', IdentifiableObject, Iterator);
var StringIterator$proto = StringIterator.$$.prototype;
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
initTypeProto(Character, 'ceylon.language::Character', Object$, Comparable);
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
Character$proto.getInteger = function() { return this.value; }
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
initTypeProto(StringBuilder, 'ceylon.language::StringBuilder', IdentifiableObject);
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
Boolean.prototype.getT$name$ = function() {
return (this.valueOf()?trueClass:falseClass).$$.T$name;
}
Boolean.prototype.getT$all$ = function() {
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
initTypeProto(Finished, 'ceylon.language::Finished', IdentifiableObject);
var $finished = new Finished.$$;
$finished.string = String$("exhausted", 9);
$finished.getString = function() { return this.string; }
function getExhausted() { return $finished; }
function Comparison(name) {
var that = new Comparison.$$;
that.name = String$(name);
return that;
}
initTypeProto(Comparison, 'ceylon.language::Comparison', IdentifiableObject);
var Comparison$proto = Comparison.$$.prototype;
Comparison$proto.getString = function() { return this.name; }
function print(line) { process$.writeLine(line===null?"«null»":line.getString()); }
exports.print=print;
var larger = Comparison("larger");
function getLarger() { return larger }
var smaller = Comparison("smaller");
function getSmaller() { return smaller }
var equal = Comparison("equal");
function getEqual() { return equal }
function largest(x, y) { return x.compare(y) === larger ? x : y }
function smallest(x, y) { return x.compare(y) === smaller ? x : y }
exports.getLarger=getLarger;
exports.getSmaller=getSmaller;
exports.getEqual=getEqual;
exports.largest=largest;
exports.smallest=smallest;
//receives ArraySequence, returns element
function min(seq) {
var v = seq.getFirst();
if (v === null) return null;
var iter = seq.getRest().getIterator();
var e; while ((e = iter.next()) !== $finished) {
v = smallest(v, e);
}
return v;
}
//receives ArraySequence, returns element
function max(/*ContainerWithFirstElement*/seq) {
var v = seq.getFirst();
if (v === null) return null;
var iter = seq.getRest().getIterator();
var e; while ((e = iter.next()) !== $finished) {
v = largest(v, e);
}
return v;
}
function sum(seq) {
var v = seq.getFirst();
var iter = seq.getRest().getIterator();
var e; while ((e = iter.next()) !== $finished) {
v = v.plus(e);
}
return v;
}
//receives ArraySequence of ArraySequences, returns flat ArraySequence
function join(seqs) {
if (seqs === undefined) return $empty;
var builder = [];
var it = seqs.getIterator();
var seq;
while ((seq = it.next()) !== $finished) {
var it2 = seq.getIterator();
var elem;
while ((elem = it2.next()) != $finished) {builder.push(elem);}
}
return ArraySequence(builder);
}
//receives ArraySequences, returns ArraySequence
function zip(keys, items) {
var entries = []
var numEntries = Math.min(keys.length, items.length);
for (var i = 0; i < numEntries; i++) {
entries[i] = Entry(keys[i], items[i]);
}
return ArraySequence(entries);
}
//receives and returns ArraySequence
function coalesce(seq) {
if (seq === undefined) {return $empty}
return seq.getCoalesced();
}
//Receives Iterable, returns ArraySequence (with Entries)
function entries(seq) {
if (seq === undefined) return $empty;
return seq.getIndexed();
}
function any(/*Boolean...*/ values) {
if (values === undefined) return false;
var it = values.getIterator();
var v;
while ((v = it.next()) !== $finished) {
if (v) {return true;}
}
return false;
}
function every(/*Boolean...*/ values) {
if (values === undefined) return false;
var it = values.getIterator();
var v;
while ((v = it.next()) !== $finished) {
if (!v) {return false;}
}
return true;
}
function first(/*Element...*/ elements) {
if (elements === undefined) return null;
var e = elements.getIterator().next();
return (e !== $finished) ? e : null;
}
exports.min=min;
exports.max=max;
exports.sum=sum;
exports.join=join;
exports.zip=zip;
exports.coalesce=coalesce;
exports.entries=entries;
exports.any=any;
exports.every=every;
exports.first=first;
//These are operators for handling nulls
function exists(value) {
return value !== null && value !== undefined;
}
function nonempty(value) {
return value !== null && value !== undefined && !value.getEmpty();
}
function isOfType(obj, typeName) {
if (obj === null) {
return typeName==="ceylon.language::Nothing" || typeName==="ceylon.language::Void";
}
return obj.getT$all$ && typeName in obj.getT$all$();
}
function isOfTypes(obj, types) {
if (obj===null) {
return types.l.indexOf('ceylon.language::Nothing')>=0 || types.l.indexOf('ceylon.language::Void')>=0;
}
var unions = false;
var inters = true;
var _ints=false;
var objTypes = obj.getT$all$();
for (var i = 0; i < types.l.length; i++) {
var t = types.l[i];
var partial = false;
if (typeof t === 'string') {
partial = t in objTypes;
} else {
partial = isOfTypes(obj, t);
}
if (types.t==='u') {
unions = partial || unions;
} else {
inters = partial && inters;
_ints=true;
}
}
return _ints ? inters||unions : unions;
}
function className(obj) {
if (obj === null) return String$('ceylon.language::Nothing');
return String$(obj.getT$name$());
}
function identityHash(obj) {
return obj.identifiableObjectID;
}
//This is just so that you can pass a comprehension and return it as iterable
function elements(iter) {
return iter;
}
exports.elements=elements;
exports.exists=exists;
exports.nonempty=nonempty;
exports.isOfType=isOfType;
exports.isOfTypes=isOfTypes;
exports.className=className;
exports.identityHash=identityHash;
//More functions, related to comprehensions and iterables
function byIncreasing/*<Element,Value>*/(/*Callable<Value?,Element>*/comp) {
return function(x, y) {
var a = comp(x);
if (a !== null) {
var b = comp(y);
if (b !== null) {
return a.compare(b);
}
}
return null;
};
}
function byDecreasing/*<Element,Value>*/(/*Callable<Value?,Element>*/comp) {
return function(x, y) {
var a = comp(x);
if (a !== null) {
var b = comp(y);
if (b !== null) {
return b.compare(a);
}
}
return null;
};
}
function count(/*Iterable<Boolean>*/truths) {
if (truths === undefined) return 0;
var c=0;
var iter = truths.getIterator();
var i; while ((i = iter.next()) !== $finished) {
if (i) c++;
}
return c;
}
function string(/*Iterable<Character>*/chars) {
if (chars === undefined) return String$('',0);
var s = StringBuilder();
var iter = chars.getIterator();
var c; while ((c = iter.next()) !== $finished) {
s.appendCharacter(c);
}
return s.getString();
}
function equalTo(v) {
return function(e) {
return v.equals(e);
};
}
function greaterThan(v) {
return function(e) {
return e.compare(v) === larger;
};
}
function lessThan(v) {
return function(e) {
return e.compare(v) === smaller;
};
}
function byKey(/*Callable<Comparison,Key,Key>*/f) {
return function(a,b) {
return f(a.getKey(), b.getKey());
}
}
function byItem(/*Callable<Comparison,Key,Key*/f) {
return function(a,b) {
return f(a.getItem(), b.getItem());
}
}
function emptyOrSingleton(/*Element?*/elem) {
return elem===null ? $empty : Singleton(elem);
}
function forKey(/*Callable<Result,Key>*/f) {
return function(/*Key->Object*/ e) {
return f(e.getKey());
}
}
function forItem(/*Callable<Result,Item>*/f) {
return function(/*Key->Item*/e) {
return f(e.getItem());
}
}
function combine(/*Callable<Result,Element,Other>*/f, /*Iterable<Element>*/i1, /*Iterable<Other>*/i2) {
return Comprehension(function(){
var ei = i1.getIterator();
var oi = i2.getIterator();
return function() {
var ne = ei.next();
var no = oi.next();
if (ne === $finished || no === $finished) {
return $finished;
}
return f(ne, no);
};
});
}
function sort(elems) {
if (elems===undefined) {return $empty;}
var arr = [];
var it = elems.getIterator();
var e;
while ((e=it.next()) !== $finished) {arr.push(e);}
if (arr.length === 0) {return $empty;}
arr.sort(function(a, b) {
var cmp = a.compare(b);
return (cmp===larger) ? 1 : ((cmp===smaller) ? -1 : 0);
});
return ArraySequence(arr);
}
exports.forKey=forKey;
exports.forItem=forItem;
exports.emptyOrSingleton=emptyOrSingleton;
exports.equalTo=equalTo;
exports.greaterThan=greaterThan;
exports.lessThan=lessThan;
exports.byIncreasing=byIncreasing;
exports.byDecreasing=byDecreasing;
exports.count=count;
exports.string=string;
exports.byKey=byKey;
exports.byItem=byItem;
exports.combine=combine;
exports.sort=sort;
function Sequence($$sequence) {
return $$sequence;
}
initTypeProtoI(Sequence, 'ceylon.language::Sequence', Some, Cloneable, Ranged, List);
var Sequence$proto = Sequence.$$.prototype;
Sequence$proto.getLast = function() {
var last = this.item(this.getLastIndex());
if (last === null) throw Exception();
return last;
}
function Array$() {
var that = new Array$.$$;
return that;
}
initExistingType(Array$, Array, 'ceylon.language::Array', Object$, FixedSized,
Cloneable, Ranged, List);
var Array$proto = Array.prototype;
var origArrToString = Array$proto.toString;
inheritProtoI(Array$, Object$, FixedSized, Cloneable, Ranged, List);
Array$proto.toString = origArrToString;
exports.Array=Array$;
function EmptyArray() {
return [];
}
initTypeProto(EmptyArray, 'ceylon.language::EmptyArray', Array$, None);
function ArrayList(items) {
return items;
}
initTypeProto(ArrayList, 'ceylon.language::ArrayList', Array$, List);
function ArraySequence(/* js array */value) {
value.$seq = true;
return value;
}
initTypeProto(ArraySequence, 'ceylon.language::ArraySequence', IdentifiableObject, Sequence);
Array$proto.getT$name$ = function() {
return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$name;
}
Array$proto.getT$all$ = function() {
return (this.$seq ? ArraySequence : (this.length>0?ArrayList:EmptyArray)).$$.T$all;
}
exports.EmptyArray=EmptyArray;
Array$proto.getSize = function() { return this.length; }
Array$proto.setItem = function(idx,elem) {
if (idx >= 0 && idx < this.length) {
this[idx] = elem;
}
}
Array$proto.item = function(idx) {
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
return this.$seq ? ArraySequence(arr) : arr;
}
Array$proto.chain = function(other) {
if (this.length === 0) { return other; }
return Iterable.$$.prototype.chain.call(this, other);
}
Array$proto.getFirst = function() { return this.length>0 ? this[0] : null; }
Array$proto.getLast = function() { return this.length>0 ? this[this.length-1] : null; }
Array$proto.segment = function(from, len) {
if (len <= 0) { return $empty; }
var stop = from + len;
var seq = this.slice((from>=0)?from:0, (stop>=0)?stop:0);
return (seq.length > 0) ? ArraySequence(seq) : $empty;
}
Array$proto.span = function(from, to) {
var toIndex = (to===null || to===undefined) ? 0x7fffffff : to;
if (from > toIndex) {
return this.segment(toIndex, from-toIndex+1).getReversed();
}
return this.segment(from, toIndex-from+1);
}
Array$proto.getRest = function() {
return this.length<=1 ? $empty : ArraySequence(this.slice(1));
}
Array$proto.items = function(keys) {
if (keys === undefined) return $empty;
var seq = [];
for (var i = 0; i < keys.getSize(); i++) {
var key = keys.item(i);
seq.push(this.item(key));
}
return ArraySequence(seq);
}
Array$proto.getKeys = function() { return TypeCategory(this, 'ceylon.language::Integer'); }
Array$proto.contains = function(elem) {
for (var i=0; i<this.length; i++) {
if (elem.equals(this[i])) {
return true;
}
}
return false;
}
exports.ArrayList=ArrayList;
exports.arrayOfNone=function() { return []; }
exports.arrayOfSome=function(/*Sequence*/elems) { //In practice it's an ArraySequence
return elems;
}
exports.array=function(elems) {
if (elems === null || elems === undefined) {
return [];
} else {
var e=[];
var iter=elems.getIterator();
var item;while((item=iter.next())!==$finished) {
e.push(item);
}
return e;
}
}
exports.arrayOfSize=function(size, elem) {
if (size > 0) {
var elems = [];
for (var i = 0; i < size; i++) {
elems.push(elem);
}
return elems;
} else return [];
}
function TypeCategory(seq, type) {
var that = new TypeCategory.$$;
that.type = type;
that.seq = seq;
return that;
}
initTypeProto(TypeCategory, 'ceylon.language::TypeCategory', IdentifiableObject, Category);
var TypeCategory$proto = TypeCategory.$$.prototype;
TypeCategory$proto.contains = function(k) {
return isOfType(k, this.type) && this.seq.defines(k);
}
function SequenceBuilder() {
var that = new SequenceBuilder.$$;
that.seq = [];
return that;
}
initTypeProto(SequenceBuilder, 'ceylon.language::SequenceBuilder', IdentifiableObject, Sized);
var SequenceBuilder$proto = SequenceBuilder.$$.prototype;
SequenceBuilder$proto.getSequence = function() {
return (this.seq.length > 0) ? ArraySequence(this.seq) : $empty;
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
function SequenceAppender(other) {
var that = new SequenceAppender.$$;
that.seq = [];
that.appendAll(other);
return that;
}
initTypeProto(SequenceAppender, 'ceylon.language::SequenceAppender', SequenceBuilder);
function Singleton(elem) {
var that = new Singleton.$$;
that.value = [elem];
that.elem = elem;
return that;
}
initTypeProto(Singleton, 'ceylon.language::Singleton', Object$, Sequence);
var Singleton$proto = Singleton.$$.prototype;
Singleton$proto.getString = function() { return String$("{ " + this.elem.getString() + " }") }
Singleton$proto.item = function(index) {
return index===0 ? this.value[0] : null;
}
Singleton$proto.getSize = function() { return 1; }
Singleton$proto.getLastIndex = function() { return 0; }
Singleton$proto.getFirst = function() { return this.elem; }
Singleton$proto.getLast = function() { return this.elem; }
Singleton$proto.getEmpty = function() { return false; }
Singleton$proto.getRest = function() { return $empty; }
Singleton$proto.defines = function(idx) { return idx.equals(0); }
Singleton$proto.getKeys = function() { return TypeCategory(this, 'ceylon.language::Integer'); }
Singleton$proto.span = function(from, to) {
if ((to === undefined) || (to === null)) {
return (from <= 0) ? this : $empty;
}
return (((from <= 0) && (to >= 0)) || ((from >= 0) && (to <= 0))) ? this : $empty;
}
Singleton$proto.segment = function(idx, len) {
return ((idx <= 0) && ((idx+len) > 0)) ? this : $empty;
}
Singleton$proto.getIterator = function() { return SingletonIterator(this.elem); }
Singleton$proto.getReversed = function() { return this; }
Singleton$proto.equals = function(other) {
if (isOfType(other, 'ceylon.language::List')) {
if (other.getSize() !== 1) {
return false;
}
var o = other.item(0);
return o !== null && o.equals(this.elem);
}
return false;
}
Singleton$proto.$map = function(f) { return ArraySequence([ f(this.elem) ]); }
Singleton$proto.$filter = function(f) {
return f(this.elem) ? this : $empty;
}
Singleton$proto.fold = function(v,f) {
return f(v, this.elem);
}
Singleton$proto.find = function(f) {
return f(this.elem) ? this.elem : null;
}
Singleton$proto.findLast = function(f) {
return f(this.elem) ? this.elem : null;
}
Singleton$proto.any = function(f) {
return f(this.elem);
}
Singleton$proto.$every = function(f) {
return f(this.elem);
}
Singleton$proto.skipping = function(skip) {
return skip==0 ? this : $empty;
}
Singleton$proto.taking = function(take) {
return take>0 ? this : $empty;
}
Singleton$proto.by = function(step) {
return this;
}
Singleton$proto.$sort = function(f) { return this; }
Singleton$proto.count = function(f) {
return f(this.elem) ? 1 : 0;
}
Singleton$proto.contains = function(o) {
return this.elem.equals(o);
}
Singleton$proto.getCoalesced = function() { return this; }
function SingletonIterator(elem) {
var that = new SingletonIterator.$$;
that.elem = elem;
that.done = false;
return that;
}
initTypeProto(SingletonIterator, 'ceylon.language::SingletonIterator', IdentifiableObject, Iterator);
var $SingletonIterator$proto = SingletonIterator.$$.prototype;
$SingletonIterator$proto.next = function() {
if (this.done) {
return $finished;
}
this.done = true;
return this.elem;
}
exports.Sequence=Sequence;
exports.SequenceBuilder=SequenceBuilder;
exports.SequenceAppender=SequenceAppender;
exports.ArraySequence=ArraySequence;
exports.Singleton=Singleton;
// implementation of object "process" in ceylon.language
function languageClass() {
var lang = new languageClass.$$;
IdentifiableObject(lang);
return lang;
}
initTypeProto(languageClass, "ceylon.language::language", IdentifiableObject);
var lang$proto=languageClass.$$.prototype;
lang$proto.getVersion=function() {
return String$("0.4",3);
}
lang$proto.getMajorVersion=function() { return 0; }
lang$proto.getMinorVersion=function() { return 4; }
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
IdentifiableObject(proc);
return proc;
}
initTypeProto(processClass, "ceylon.language::process", IdentifiableObject);
var process$proto = processClass.$$.prototype;
var argv = $empty;
var namedArgs = {};
if ((typeof process !== "undefined") && (process.argv !== undefined)) {
// parse command line arguments
if (process.argv.length > 2) {
var args = process.argv.slice(2);
var argStrings = new Array(args.length);
for (var i in args) { argStrings[i] = String$(args[i]); }
argv = ArraySequence(argStrings);
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
}
}
} else if (typeof window !== "undefined") {
// parse URL parameters
var parts = window.location.search.substr(1).replace('+', ' ').split('&');
if (parts.length > 0) {
var argStrings = new Array(parts.length);
//can't do "for (i in parts)" anymore because of the added stuff to arrays
var i;
for (i=0; i<parts.length; i++) { argStrings[i] = String$(parts[i]); }
argv = ArraySequence(argStrings);
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
if (process && process.execPath && process.execPath.match(/node(\.exe)?$/)) {
return String$("node.js", 7);
} else if (typeof window === 'object') {
return String$("Browser", 7);
}
return String$("Unknown JavaScript environment", 30);
}
process$proto.getVmVersion = function() {
if (process && typeof process.version === 'string') {
return String$(process.version);
}
return String$("Unknown");
}
process$proto.getOs = function() {
if (process && typeof process.platform === 'string') {
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
function Range(first, last) {
var that = new Range.$$;
that.first = first;
that.last = last;
var dist = last.distanceFrom(first);
that.size=(dist>0?dist:-dist)+1;
return that;
}
initTypeProto(Range, 'ceylon.language::Range', Object$, Sequence, Category);
var Range$proto = Range.$$.prototype;
Range$proto.getFirst = function() { return this.first; }
Range$proto.getLast = function() { return this.last; }
Range$proto.getEmpty = function() { return false; }
Range$proto.getDecreasing = function() {
return this.first.compare(this.last) === larger;
}
Range$proto.next = function(x) {
return this.getDecreasing() ? x.getPredecessor() : x.getSuccessor();
}
Range$proto.getSize = function() { return this.size; }
Range$proto.getLastIndex = function() { return this.size-1; }
Range$proto.item = function(index) {
var idx = 0;
var x = this.first;
while (idx < index) {
if (x.equals(this.last)) { return null; }
else {
idx++;
x = this.next(x);
}
}
return x;
}
Range$proto.includes = function(x) {
var compf = x.compare(this.first);
var compl = x.compare(this.last);
return this.getDecreasing() ? ((compf === equal || compf === smaller) && (compl === equal || compl === larger)) : ((compf === equal || compf === larger) && (compl === equal || compl === smaller));
}
Range$proto.contains = function(x) {
if (typeof x.compare==='function' || (x.prototype && typeof x.prototype.compare==='function')) {
return this.includes(x);
}
return false;
}
Range$proto.getRest = function() {
if (this.first.equals(this.last)) return $empty;
var n = this.next(this.first);
return Range(n, this.last);
}
Range$proto.segment = function(from, len) {
//only positive length for now
if (len.compare(0) !== larger) return $empty;
if (!this.defines(from)) return $empty;
var x = this.first;
for (var i=0; i < from; i++) { x = this.next(x); }
var y = x;
for (var i=1; i < len; i++) { y = this.next(y); }
if (!this.includes(y)) { y = this.last; }
return Range(x, y);
}
Range$proto.span = function(from, to) {
var li = this.getLastIndex();
to = (to==null || to==undefined) ? li : to;
if (to<0) {
if (from<0) {
return $empty;
}
to = 0;
}
else if (to > li) {
if (from > li) {
return $empty;
}
to = li;
}
if (from < 0) {
from = 0;
}
else if (from > li) {
from = li;
}
var x = this.first;
for (var i=0; i < from; i++) { x = this.next(x); }
var y = this.first;
for (var i=0; i < to; i++) { y = this.next(y); }
return Range(x, y);
}
Range$proto.definesEvery = function(keys) {
for (var i = 0; i < keys.getSize(); i++) {
if (!this.defines(keys.item(i))) {
return false;
}
}
return true;
}
Range$proto.definesAny = function(keys) {
for (var i = 0; i < keys.getSize(); i++) {
if (this.defines(keys.item(i))) {
return true;
}
}
return false;
}
Range$proto.defines = function(idx) { return idx.compare(this.getSize()) === smaller; }
Range$proto.getString = function() { return String$(this.first.getString() + ".." + this.last.getString()); }
Range$proto.equals = function(other) {
if (!other) { return false; }
return this.first.equals(other.getFirst()) && this.last.equals(other.getLast());
}
Range$proto.getIterator = function() { return RangeIterator(this); }
Range$proto.getReversed = function() { return Range(this.last, this.first); }
Range$proto.skipping = function(skip) {
var x=0;
var e=this.first;
while (x++<skip) {
e=this.next(e);
}
return this.includes(e) ? new Range(e, this.last) : $empty;
}
Range$proto.taking = function(take) {
if (take == 0) {
return $empty;
}
var x=0;
var e=this.first;
while (++x<take) {
e=this.next(e);
}
return this.includes(e) ? new Range(this.first, e) : this;
}
Range$proto.getSequence = function() { return this; }
Range$proto.getCoalesced = function() { return this; }
Range$proto.count = function(f) {
var e = this.getFirst();
var c = 0;
while (this.includes(e)) {
if (f(e)) {
c++;
}
e = this.next(e);
}
return c;
}
function RangeIterator(range) {
var that = new RangeIterator.$$;
that.range = range;
that.current = range.getFirst();
that.next = (range.last>=range.first) ? RangeIterator$forwardNext : RangeIterator$backwardNext;
return that;
}
initTypeProto(RangeIterator, 'ceylon.language::RangeIterator', IdentifiableObject, Iterator);
RangeIterator$forwardNext = function() {
var rval = this.current;
if (rval === $finished) {
return rval;
}
if (rval.compare(this.range.last) === smaller) {
this.current = rval.getSuccessor();
} else {
this.current = $finished;
}
return rval;
}
RangeIterator$backwardNext = function() {
var rval = this.current;
if (rval === $finished) {
return rval;
}
if (rval.compare(this.range.last) === larger) {
this.current = rval.getPredecessor();
} else {
this.current = $finished;
}
return rval;
}
function Entry(key, item) {
var that = new Entry.$$;
Object$(that);
Void(that);
that.key = key;
that.item = item;
return that;
}
initTypeProto(Entry, 'ceylon.language::Entry', Object$);
var Entry$proto = Entry.$$.prototype;
Entry$proto.getString = function() {
return String$(this.key.getString() + "->" + this.item.getString());
}
Entry$proto.getKey = function() { return this.key; }
Entry$proto.getItem = function() { return this.item; }
Entry$proto.equals = function(other) {
return other && isOfType(other, 'ceylon.language::Entry') && this.getKey().equals(other.getKey()) && this.getItem().equals(other.getItem());
}
Entry$proto.getHash = function() { (31 + this.key.getHash()) * 31 + this.item.getHash(); }
function getBottom() {
throw Exception();
}
//Turns out we need to have empty implementations of these annotations
exports.see=function(){};
exports.by=function(){};
exports.tagged=function(){};
exports.Exception=Exception;
exports.Identifiable=Identifiable;
exports.identityHash=$identityHash;
exports.IdentifiableObject=IdentifiableObject;
exports.Object=Object$;
exports.Boolean=Boolean$;
exports.Comparison=Comparison;
exports.getNull=getNull;
exports.getTrue=getTrue;
exports.getFalse=getFalse;
exports.Finished=Finished;
exports.getExhausted=getExhausted;
exports.Range=Range;
exports.Entry=Entry;
exports.getBottom=getBottom;
exports.ContainerWithFirstElement=ContainerWithFirstElement;
});
}(typeof define==='function' && define.amd ?
define : function (factory) {
if (typeof exports!=='undefined') {
factory(require, exports, module);
} else {
throw "no module loader";
}
}));
