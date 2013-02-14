(function(define) {
define(function(require, exports, module) {
//the Ceylon language module
var $$metamodel$$={"$mod-name":"ceylon.language","$mod-version":"0.5","ceylon.language":{"Iterator":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Iterable"],"doc":["Produces elements of an `Iterable` object. Classes that \n     implement this interface should be immutable."],"by":["Gavin"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The next element, or `finished` if there are no \n         more elements to be iterated."]},"$nm":"next"}},"$nm":"Iterator"},"Callable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"variance":"in","$nm":"Arguments"}],"$an":{"shared":[],"doc":["A reference to a method or function."]},"$nm":"Callable"},"copyArray":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"source"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"prm","$pt":"v","$nm":"target"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Efficiently copy the elements of one array to another \n     array."]},"$nm":"copyArray"},"Array":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Cloneable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"}],"$pk":"ceylon.language","$nm":"Ranged"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"abstract":[],"shared":[],"doc":["A fixed-size array of elements. An array may have zero\n     size (an empty array). Arrays are mutable. Any element\n     of an array may be set to a new value.\n     \n     This class is provided primarily to support interoperation \n     with Java, and for some performance-critical low-level \n     programming tasks."]},"$m":{"setItem":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Replace the existing element at the specified index \n         with the given element. Does nothing if the specified \n         index is negative or larger than the index of the \n         last element in the array."]},"$nm":"setItem"}},"$at":{"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this array, returning a new array."],"actual":[]},"$nm":"reversed"}},"$nm":"Array"},"Singleton":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A sequence with exactly one element."]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"a"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns the contained element, if the specified \n         index is `0`."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `1` if this `Singleton`'s element\n         satisfies the predicate, or `0` otherwise."],"actual":[]},"$nm":"count"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A `Singleton` can be equal to another `List` if \n         that `List` has only one element which is equal to \n         this `Singleton`'s element."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \n         is `0` and the given `length` is greater than `0`.\n         Otherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `true` if the specified element is this \n         `Singleton`'s element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"spanTo":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"filter":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"span":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns a `Singleton` if the given starting index \n         is `0`. Otherwise, returns an instance of `Empty`."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `0`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"attr","$an":{"shared":[],"doc":["Returns a `Singleton` with the same element."],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"attr","$an":{"shared":[],"doc":["Returns the Singleton itself, since a Singleton\n         cannot contain a null."],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Singleton"},"$mt":"attr","$an":{"shared":[],"doc":["Return this singleton."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `Empty`."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["Returns the element contained in this `Singleton`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `1`."],"actual":[]},"$nm":"size"}},"$nm":"Singleton"},"byKey":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Key","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"}],"$an":{"shared":[],"see":["byItem"],"doc":["A comparator for `Entry`s which compares their keys \n     according to the given `comparing()` function."]},"$nm":"byKey"},"Comparable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Other"}],"$an":{"shared":[],"doc":["The general contract for values whose magnitude can be \n     compared. `Comparable` imposes a total ordering upon\n     instances of any type that satisfies the interface.\n     If a type `T` satisfies `Comparable<T>`, then instances\n     of `T` may be compared using the comparison operators\n     `<`, `>`, `<=`, >=`, and `<=>`.\n     \n     The total order of a type must be consistent with the \n     definition of equality for the type. That is, there\n     are three mutually exclusive possibilities:\n     \n     - `x<y`,\n     - `x>y`, or\n     - `x==y`"],"by":["Gavin"]},"$m":{"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["equals"],"doc":["Compares this value with the given value. \n         Implementations must respect the constraints that: \n         \n         - `x==y` if and only if `x<=>y == equal` \n            (consistency with `equals()`), \n         - if `x>y` then `y<x` (symmetry), and \n         - if `x>y` and `y>z` then `x>z` (transitivity)."]},"$nm":"compare"}},"$nm":"Comparable","$st":"Other"},"Comparison":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"larger"},{"$pk":"ceylon.language","$nm":"smaller"},{"$pk":"ceylon.language","$nm":"equal"}],"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Comparable"],"doc":["The result of a comparison between two `Comparable` \n     objects."],"by":["Gavin"]},"$m":{"largerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"largerThan"},"equal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"equal"},"asSmallAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asSmallAs"},"asLargeAs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"asLargeAs"},"smallerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"smallerThan"},"unequal":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"mthd","$an":{"shared":[],"deprecated":[]},"$nm":"unequal"}},"$nm":"Comparison"},"Empty":{"of":[{"$pk":"ceylon.language","$nm":"empty"}],"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$an":{"shared":[],"see":["Sequence"],"doc":["A sequence with no elements. The type `Empty` may be\n     abbreviated `[]`, and an instance is produced by the \n     expression `[]`. That is, in the following expression,\n     `e` has type `[]` and refers to the value `[]`:\n     \n         [] none = [];\n     \n     (Whether the syntax `[]` refers to the type or the \n     value depends upon how it occurs grammatically.)"]},"$m":{"sort":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"a"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"b"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"sort"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `null` for any given index."],"actual":[]},"$nm":"get"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns 0 for any given predicate."],"actual":[]},"$nm":"count"},"select":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"select"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given segment."],"actual":[]},"$nm":"segment"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns `false` for any given element."],"actual":[]},"$nm":"contains"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"every"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"fold"},"taking":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"withTrailing":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"defines"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanTo"},"chain":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"doc":["Returns `other`."],"actual":[]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"any"},"map":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"map"},"withLeading":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"actual":[]},"$nm":"withLeading"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"spanFrom"},"skipping":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"find":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"find"},"filter":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"filter"},"collect":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":"Result","$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"actual":[]},"$nm":"collect"},"span":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Returns an `Empty` for any given span."],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"doc":["Returns an iterator that is already exhausted."],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"indexed"},"sequence":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["Returns a string description of the empty sequence: \n         `{}`."],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `true`."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"Empty"},"$mt":"attr","$an":{"shared":[],"doc":["Returns an `Empty`."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `null`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["Returns 0."],"actual":[]},"$nm":"size"}},"$nm":"Empty"},"Enumerable":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["Abstraction of ordinal types whose instances can be \n     mapped to the integers or to a range of integers."]},"$at":{"integerValue":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The corresponding integer. The implementation must\n         satisfy these constraints:\n         \n             (x.successor).integerValue = x.integerValue+1\n             (x.predecessor).integerValue = x.integerValue-1\n         \n         for every instance `x` of the enumerable type."]},"$nm":"integerValue"}},"$nm":"Enumerable","$st":"Other"},"combine":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"},{"$t":"OtherElement","$mt":"prm","$pt":"v","$nm":"otherElement"}]],"$mt":"prm","$pt":"f","$nm":"combination"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"OtherElement"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"otherElements"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"},{"$nm":"Element"},{"$nm":"OtherElement"}],"$an":{"shared":[],"doc":["Applies a function to each element of two `Iterable`s\n     and returns an `Iterable` with the results."],"by":["Gavin","Enrique Zamudio","Tako"]},"$nm":"combine"},"empty":{"super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Empty"}],"$mt":"obj","$an":{"shared":[],"doc":["A sequence with no elements, abbreviated `[]`. The \n     unique instance of the type `[]`."]},"$nm":"empty"},"false":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing falsity in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"false"},"compose":{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"X"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Y"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"X"},{"$nm":"Y"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[]},"$nm":"compose"},"Sequential":{"of":[{"$pk":"ceylon.language","$nm":"Empty"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Tuple"],"doc":["A possibly-empty, immutable sequence of values. The \n     type `Sequential<Element>` may be abbreviated \n     `[Element*]` or `Element[]`. \n     \n     `Sequential` has two enumerated subtypes:\n     \n     - `Empty`, abbreviated `[]`, represents an empty \n        sequence, and\n     - `Sequence<Element>`, abbreviated `[Element+]` \n        represents a non-empty sequence, and has the very\n        important subclass `Tuple`."]},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["A string of form `\"[ x, y, z ]\"` where `x`, `y`, \n         and `z` are the `string` representations of the \n         elements of this collection, as produced by the\n         iterator of the collection, or the string `\"{}\"` \n         if this collection is empty. If the collection \n         iterator produces the value `null`, the string\n         representation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \n         element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new sequence."],"actual":[]},"$nm":"reversed"}},"$nm":"Sequential"},"Finished":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"finished"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Iterator"],"doc":["The type of the value that indicates that \n     an `Iterator` is exhausted and has no more \n     values to return."]},"$nm":"Finished"},"coalesce":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Return a sequence containing the given values which are\n     not null. If there are no values which are not null,\n     return an empty sequence."]},"$nm":"coalesce"},"plus":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["times","sum"],"doc":["Add the given `Summable` values."]},"$nm":"plus"},"Entry":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Key","$hdn":"1","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$hdn":"1","$mt":"prm","$pt":"v","$nm":"item"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A pair containing a _key_ and an associated value called \n     the _item_. Used primarily to represent the elements of \n     a `Map`. The type `Entry<Key,Item>` may be abbreviated \n     `Key->Item`. An instance of `Entry` may be constructed \n     using the `->` operator:\n     \n         String->Person entry = person.name->person;\n     "],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if this entry is equal to the given\n         entry. Two entries are equal if they have the same\n         key and the same value."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["Returns a description of the entry in the form \n         `key->item`."],"actual":[]},"$nm":"string"},"item":{"$t":{"$nm":"Item"},"$mt":"attr","$an":{"shared":[],"doc":["The value associated with the key."]},"$nm":"item"},"key":{"$t":{"$nm":"Key"},"$mt":"attr","$an":{"shared":[],"doc":["The key used to access the entry."]},"$nm":"key"}},"$nm":"Entry"},"Invertable":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Inverse"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of types which support a unary additive inversion\n     operation. For a numeric type, this should return the \n     negative of the argument value. Note that the type \n     parameter of this interface is not restricted to be a \n     self type, in order to accommodate the possibility of \n     types whose additive inverse can only be expressed in terms of \n     a wider type."],"by":["Gavin"]},"$at":{"positiveValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The value itself, expressed as an instance of the\n         wider type."]},"$nm":"positiveValue"},"negativeValue":{"$t":{"$nm":"Inverse"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The additive inverse of the value, which may be expressed\n         as an instance of a wider type."]},"$nm":"negativeValue"}},"$nm":"Invertable"},"Cloneable":{"of":[{"$nm":"Clone"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Clone"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"variance":"out","$nm":"Clone"}],"$an":{"shared":[],"doc":["Abstract supertype of objects whose value can be \n     cloned."]},"$at":{"clone":{"$t":{"$nm":"Clone"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Obtain a clone of this object. For a mutable \n         object, this should return a copy of the object. \n         For an immutable object, it is acceptable to return\n         the object itself."]},"$nm":"clone"}},"$nm":"Cloneable","$st":"Clone"},"Ordinal":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Ordinal"}],"variance":"out","$nm":"Other"}],"$an":{"shared":[],"see":["Character","Integer","Integral","Range"],"doc":["Abstraction of ordinal types, that is, types with \n     successor and predecessor operations, including\n     `Integer` and other `Integral` numeric types.\n     `Character` is also considered an ordinal type. \n     `Ordinal` types may be used to generate a `Range`."],"by":["Gavin"]},"$at":{"predecessor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if this is the minimum value"],"doc":["The predecessor of this value."]},"$nm":"predecessor"},"successor":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if this is the maximum value"],"doc":["The successor of this value."]},"$nm":"successor"}},"$nm":"Ordinal","$st":"Other"},"largest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","smallest","max"],"doc":["Given two `Comparable` values, return largest of the\n     two."]},"$nm":"largest"},"unflatten":{"$t":{"$nm":"Return"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"flatFunction"}],[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"args"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"unflatten"},"native":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member whose implementation is \n     be provided by platform-native code."]},"$nm":"native"},"greaterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\n     to any other element and returns true if the compared\n     element is greater than its element.\n     This is useful in conjunction with methods that receive\n     a predicate function."]},"$nm":"greaterThan"},"Identifiable":{"$mt":"ifc","$an":{"shared":[],"doc":["The abstract supertype of all types with a well-defined\n     notion of identity. Values of type `Identifiable` may \n     be compared using the `===` operator to determine if \n     they are references to the same object instance. For\n     the sake of convenience, this interface defines a\n     default implementation of value equality equivalent\n     to identity. Of course, subtypes are encouraged to\n     refine this implementation."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Identity equality comparing the identity of the two \n         values. May be refined by subtypes for which value \n         equality is more appropriate. Implementations must\n         respect the constraint that if `x===y` then `x==y` \n         (equality is consistent with identity)."],"actual":[]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["identityHash"],"doc":["The system-defined identity hash value of the \n         instance. Subtypes which refine `equals()` must \n         also refine `hash`, according to the general \n         contract defined by `Object`."],"actual":[]},"$nm":"hash"}},"$nm":"Identifiable"},"language":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Contains information about the language"],"by":["The Ceylon Team"]},"$at":{"majorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language major version."]},"$nm":"majorVersion"},"majorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The major version of the code generated for the underlying runtime."]},"$nm":"majorVersionBinary"},"minorVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language minor version."]},"$nm":"minorVersion"},"versionName":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release name."]},"$nm":"versionName"},"releaseVersion":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language release version."]},"$nm":"releaseVersion"},"minorVersionBinary":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The minor version of the code generated for the underlying runtime."]},"$nm":"minorVersionBinary"},"version":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The Ceylon language version."]},"$nm":"version"}},"$nm":"language"},"Null":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"of":[{"$pk":"ceylon.language","$nm":"null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["null"],"doc":["The type of the `null` value. Any union type of form \n     `Null|T` is considered an optional type, whose values\n     include `null`. Any type of this form may be written as\n     `T?` for convenience.\n     \n     The `if (exists ... )` construct, or, alternatively,\n     the `assert (exists ...)` construct, may be used to\n     narrow an optional type to a _definite_ type, that is,\n     a subtype of `Object`:\n     \n         String? firstArg = process.arguments.first;\n         if (exists firstArg) {\n             print(\"hello \" + firstArg);\n         }\n     \n     The `else` operator evaluates its second operand if \n     and only if its first operand is `null`:\n     \n         String name = process.arguments.first else \"world\";\n     \n     The `then` operator evaluates its second operand when\n     its first operand evaluates to `true`, and to `null` \n     otherwise:\n     \n         Float? diff = x>=y then x-y;\n     \n     "],"by":["Gavin"]},"$nm":"Null"},"array":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array containing the given elements. If no\n     elements are provided, create an empty array of the\n     given element type."]},"$nm":"array"},"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable"],"doc":["Sort a given elements, returning a new sequence."]},"$nm":"sort"},"equalTo":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\n     to any other element and returns true if they're equal.\n     This is useful in conjunction with methods that receive\n     a predicate function."]},"$nm":"equalTo"},"AssertionException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$ps":[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"message"}],"$mt":"cls","$an":{"shared":[],"doc":["An exception that occurs when an assertion fails, that\n     is, when a condition in an `assert` statement evaluates\n     to false at runtime."]},"$nm":"AssertionException"},"arrayOfSize":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Array"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"size"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"doc":["Create an array of the specified size, populating every \n     index with the given element. If the specified size is\n     smaller than `1`, return an empty array of the given\n     element type."]},"$nm":"arrayOfSize"},"Ranged":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Index"}],"$pk":"ceylon.language","$nm":"Comparable"}],"variance":"in","$nm":"Index"},{"variance":"out","$nm":"Span"}],"$an":{"shared":[],"see":["List","Sequence","String"],"doc":["Abstract supertype of ranged objects which map a range\n     of `Comparable` keys to ranges of values. The type\n     parameter `Span` abstracts the type of the resulting\n     range.\n     \n     A span may be obtained from an instance of `Ranged`\n     using the span operator:\n     \n         print(\"hello world\"[0..5])\n     "]},"$m":{"spanTo":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\n         the start of the receiver and the end index."]},"$nm":"spanTo"},"segment":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a segment containing the mapped values\n         starting from the given index, with the given \n         length."]},"$nm":"segment"},"spanFrom":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between\n         the starting index and the end of the receiver."]},"$nm":"spanFrom"},"span":{"$t":{"$nm":"Span"},"$ps":[[{"$t":"Index","$mt":"prm","$pt":"v","$nm":"from"},{"$t":"Index","$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Obtain a span containing the mapped values between \n         the two given indices."]},"$nm":"span"}},"$nm":"Ranged"},"times":{"$t":{"$nm":"Value"},"$ps":[[{"$t":"Value","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Value","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["plus","product"],"doc":["Multiply the given `Numeric` values."]},"$nm":"times"},"entries":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"doc":["Produces a sequence of each index to element `Entry` \n     for the given sequence of values."]},"$nm":"entries"},"license":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"url"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify the URL of the license of a module \n     or package."]},"$nm":"license"},"null":{"super":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"obj","$an":{"shared":[],"doc":["The null value."],"by":["Gavin"]},"$nm":"null"},"Object":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["Basic","Null"],"doc":["The abstract supertype of all types representing \n     definite values. Any two `Object`s may be compared\n     for value equality using the `==` and `!=` operators:\n     \n         true==false\n         1==\"hello world\"\n         \"hello\"+ \" \" + \"world\"==\"hello world\"\n         Singleton(\"hello world\")=={ \"hello world\" }\n     \n     However, since `Null` is not a subtype of `Object`, the \n     value `null` cannot be compared to any other value\n     using `==`. Thus, value equality is not defined for \n     optional types. This neatly voids the problem of \n     deciding the value of the expression `null==null`, \n     which is simply illegal."],"by":["Gavin"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determine if two values are equal. Implementations\n         should respect the constraints that:\n         \n         - if `x===y` then `x==y` (reflexivity), \n         - if `x==y` then `y==x` (symmetry), \n         - if `x==y` and `y==z` then `x==z` (transitivity).\n         \n         Furthermore it is recommended that implementations\n         ensure that if `x==y` then `x` and `y` have the\n         same concrete class."]},"$nm":"equals"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The hash value of the value, which allows the value\n         to be an element of a hash-based set or key of a\n         hash-based map. Implementations must respect the\n         constraint that if `x==y` then `x.hash==y.hash`."]},"$nm":"hash"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["A developer-friendly string representing the \n         instance. Concatenates the name of the concrete \n         class of the instance with the `hash` of the \n         instance. Subclasses are encouraged to refine this \n         implementation to produce a more meaningful \n         representation."]},"$nm":"string"}},"$nm":"Object"},"LazySet":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of Set that wraps an `Iterable` of\n     elements. All operations on this Set are performed\n     on the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"complement"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"exclusiveUnion"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"actual":[]},"$nm":"union"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"LazySet"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazySet"},"Float":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Float"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A 64-bit floating point number. A `Float` is capable of\n     approximately representing numeric values between\n     2<sup>-1022<\/sup> and \n     (2-2<sup>-52<\/sup>)×2<sup>1023<\/sup>, along with \n     the special values `infinity` and `-infinity`, and \n     undefined values (Not a Number). Zero is represented by \n     distinct instances `+0`, `-0`, but these instances are \n     equal. An undefined value is not equal to any other\n     value, not even to itself."]},"$at":{"strictlyNegative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a negative number, `-0`, \n         or `-infinity`. Produces `false` for a positive \n         number, `+0`, or undefined."]},"$nm":"strictlyNegative"},"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this value. Produces `1` for a positive \n         number or `infinity`. Produces `-1` for a negative\n         number or `-infinity`. Produces `0` for `+0`, `-0`, \n         or undefined."],"actual":[]},"$nm":"sign"},"infinite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"see":["infinity","finite"],"doc":["Determines whether this value is infinite in \n         magnitude. Produces `true` for `infinity` and \n         `-infinity`. Produces `false` for a finite number, \n         `+0`, `-0`, or undefined."]},"$nm":"infinite"},"undefined":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["Determines whether this value is undefined (that is, \n         Not a Number or NaN). The undefined value has the \n         property that it is not equal (`==`) to itself, as \n         a consequence the undefined value cannot sensibly \n         be used in most collections."]},"$nm":"undefined"},"strictlyPositive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a positive number, `+0`, \n         or `infinity`. Produces `false` for a negative \n         number, `-0`, or undefined."]},"$nm":"strictlyPositive"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a negative number or\n         `-infinity`. Produces `false` for a positive number, \n         `+0`, `-0`, or undefined."],"actual":[]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determines if this value is a positive number or\n         `infinity`. Produces `false` for a negative number, \n         `+0`, `-0`, or undefined."],"actual":[]},"$nm":"positive"},"finite":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"see":["infinite","infinity"],"doc":["Determines whether this value is finite. Produces\n         `false` for `infinity`, `-infinity`, and undefined."]},"$nm":"finite"}},"$nm":"Float"},"min":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","max","smallest"],"doc":["Given a nonempty sequence of `Comparable` values, \n     return the smallest value in the sequence."]},"$nm":"min"},"Collection":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$pk":"ceylon.language","$nm":"Category"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["List","Map","Set"],"doc":["Represents an iterable collection of elements of finite \n     size. `Collection` is the abstract supertype of `List`,\n     `Map`, and `Set`.\n     \n     A `Collection` forms a `Category` of its elements.\n     \n     All `Collection`s are `Cloneable`. If a collection is\n     immutable, it is acceptable that `clone` produce a\n     reference to the collection itself. If a collection is\n     mutable, `clone` should produce an immutable collection\n     containing references to the same elements, with the\n     same structure as the original collection&mdash;that \n     is, it should produce an immutable shallow copy of the\n     collection."]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if the given object is an element of\n         this collection. In this default implementation,\n         and in most refining implementations, return `false`\n         otherwise. An acceptable refining implementation\n         may return `true` for objects which are not \n         elements of the collection, but this is not \n         recommended. (For example, the `contains()` method \n         of `String` returns `true` for any substring of the\n         string.)"],"actual":[]},"$nm":"contains"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["A string of form `\"{ x, y, z }\"` where `x`, `y`, \n         and `z` are the `string` representations of the \n         elements of this collection, as produced by the\n         iterator of the collection, or the string `\"{}\"` \n         if this collection is empty. If the collection \n         iterator produces the value `null`, the string\n         representation contains the string `\"null\"`."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["Determine if the collection is empty, that is, if \n         it has no elements."],"actual":[]},"$nm":"empty"}},"$nm":"Collection"},"deprecated":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"reason"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark program elements which should not be \n     used anymore."]},"$nm":"deprecated"},"Range":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Element","$hdn":"1","$mt":"prm","$pt":"v","$nm":"last"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Represents the range of totally ordered, ordinal values \n     generated by two endpoints of type `Ordinal` and \n     `Comparable`. If the first value is smaller than the\n     last value, the range is increasing. If the first value\n     is larger than the last value, the range is decreasing.\n     If the two values are equal, the range contains exactly\n     one element. The range is always nonempty, containing \n     at least one value.\n     \n     A range may be produced using the `..` operator:\n     \n         for (i in min..max) { ... }\n         if (char in `A`..`Z`) { ... }\n     "],"by":["Gavin"]},"$m":{"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"count"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"n"}]],"$mt":"mthd","$an":{"shared":[],"doc":["The element of the range that occurs `n` values after\n         the start of the range. Note that this operation \n         is inefficient for large ranges."],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"next":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$nm":"next"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given object."],"actual":[]},"$nm":"contains"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"by"},"includes":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Determines if the range includes the given value."]},"$nm":"includes"},"spanFrom":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"taking":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"taking"},"skipping":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"skipping"},"span":{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},{"$pk":"ceylon.language","$nm":"Empty"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["The index of the end of the range."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["Returns the range itself, since ranges are \n         immutable."],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"doc":["An iterator for the elements of the range."],"actual":[]},"$nm":"iterator"},"decreasing":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["Determines if the range is decreasing."]},"$nm":"decreasing"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The end of the range."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["Returns this range."],"actual":[]},"$nm":"sequence"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"},"coalesced":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["Returns the range itself, since a Range cannot\n         contain nulls."],"actual":[]},"$nm":"coalesced"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"attr","$an":{"shared":[],"doc":["Reverse this range, returning a new range."],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"gttr","$an":{"shared":[],"doc":["The rest of the range, without the start of the\n         range."],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"doc":["The start of the range."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"doc":["The nonzero number of elements in the range."],"actual":[]},"$nm":"size"}},"$nm":"Range"},"max":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"see":["Comparable","min","largest"],"doc":["Given a nonempty sequence of `Comparable` values, \n     return the largest value in the sequence."]},"$nm":"max"},"Integral":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Integral"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of integral numeric types. That is, types \n     with no fractional part, including `Integer`. The \n     division operation for integral numeric types results \n     in a remainder. Therefore, integral numeric types have \n     an operation to determine the remainder of any division \n     operation."],"by":["Gavin"]},"$m":{"remainder":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["divided"],"doc":["The remainder, after dividing this number by the \n         given number."]},"$nm":"remainder"}},"$at":{"unit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is one."]},"$nm":"unit"},"zero":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is zero."]},"$nm":"zero"}},"$nm":"Integral","$st":"Other"},"SequenceAppender":{"super":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"prm","$pt":"v","$nm":"elements"}],"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceBuilder"],"doc":["This class is used for constructing a new nonempty \n     sequence by incrementally appending elements to an\n     existing nonempty sequence. The existing sequence is\n     not modified, since `Sequence`s are immutable. This \n     class is mutable but threadsafe."]},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting nonempty sequence. If no elements \n         have been appended, the original nonempty \n         sequence."],"actual":[]},"$nm":"sequence"}},"$nm":"SequenceAppender"},"RecursiveInitializationException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when name could not be initialized due to recursive access during initialization."]},"$nm":"RecursiveInitializationException"},"larger":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is larger than the given value."],"by":["Gavin"]},"$nm":"larger"},"smallest":{"$t":{"$nm":"Element"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Comparable","largest","min"],"doc":["Given two `Comparable` values, return smallest of the\n     two."]},"$nm":"smallest"},"byIncreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byDecreasing"],"doc":["A comparator which orders elements in increasing order \n     according to the `Comparable` returned by the given \n     `comparable()` function."]},"$nm":"byIncreasing"},"true":{"super":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"obj","$an":{"shared":[],"doc":["A value representing truth in Boolean logic."],"by":["Gavin"]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"true"},"join":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"iterables"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"see":["SequenceBuilder"],"doc":["Given a list of iterable objects, return a new sequence \n     of all elements of the all given objects. If there are\n     no arguments, or if none of the arguments contains any\n     elements, return the empty sequence."]},"$nm":"join"},"Exponentiable":{"of":[{"$nm":"This"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"This"},{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$nm":"This"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types that may be raised to a\n     power. Note that the type of the exponent may be\n     different to the numeric type which can be \n     exponentiated."]},"$m":{"power":{"$t":{"$nm":"This"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of raising this number to the given\n         power."]},"$nm":"power"}},"$nm":"Exponentiable","$st":"This"},"curry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}],[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"curry"},"forKey":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forItem"],"doc":["A function that returns the result of the given `resulting()` function \n     on the key of a given `Entry`."]},"$nm":"forKey"},"Character":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Enumerable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"see":["String"],"doc":["A 32-bit Unicode character."],"by":["Gavin"]},"$at":{"digit":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a numeric digit."]},"$nm":"digit"},"uppercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is an uppercase representation of\n         the character."]},"$nm":"uppercase"},"control":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is an ISO control \n         character."]},"$nm":"control"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The code point of the character."]},"$nm":"integer"},"letter":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a letter."]},"$nm":"letter"},"lowercase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is a lowercase representation of\n         the character."]},"$nm":"lowercase"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The lowercase representation of this character."]},"$nm":"lowercased"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The uppercase representation of this character."]},"$nm":"uppercased"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["A string containg just this character."],"actual":[]},"$nm":"string"},"whitespace":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this character is a whitespace \n         character."]},"$nm":"whitespace"},"titlecased":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The title case representation of this character."]},"$nm":"titlecased"},"titlecase":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if this is a title case representation of\n         the character."]},"$nm":"titlecase"}},"$nm":"Character"},"Keys":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},"$mt":"prm","$pt":"v","$nm":"correspondence"}],"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"native":[]},"$m":{"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"contains"}},"$nm":"Keys"},"process":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"obj","$an":{"shared":[],"native":[],"doc":["Represents the current process (instance of the virtual\n     machine)."],"by":["Gavin","Tako"]},"$m":{"readLine":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Read a line of input text from the standard input \n         of the virtual machine process."]},"$nm":"readLine"},"writeErrorLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Print a line to the standard error of the \n         virtual machine process."]},"$nm":"writeErrorLine"},"writeError":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard error of the \n         virtual machine process."]},"$nm":"writeError"},"propertyValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the given system property of the virtual\n         machine, if any."]},"$nm":"propertyValue"},"namedArgumentValue":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The value of the first argument of form `-name=value`, \n         `--name=value`, or `-name value` specified among the \n         command line arguments to the virtual machine, if\n         any."]},"$nm":"namedArgumentValue"},"write":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print a string to the standard output of the \n         virtual machine process."]},"$nm":"write"},"namedArgumentPresent":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"name"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Determine if an argument of form `-name` or `--name` \n         was specified among the command line arguments to \n         the virtual machine."]},"$nm":"namedArgumentPresent"},"writeLine":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$def":"1","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["print"],"doc":["Print a line to the standard output of the \n         virtual machine process."]},"$nm":"writeLine"},"exit":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"code"}]],"$mt":"mthd","$an":{"shared":[],"native":[]},"$nm":"exit"}},"$at":{"os":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the operating system this \n         process is running on."]},"$nm":"os"},"vmVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the virtual machine this \n         process is running on."]},"$nm":"vmVersion"},"vm":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the name of the virtual machine this \n         process is running on."]},"$nm":"vm"},"osVersion":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the version of the operating system this \n         process is running on."]},"$nm":"osVersion"},"newline":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The line ending character sequence on this platform."]},"$nm":"newline"},"arguments":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The command line arguments to the virtual machine."]},"$nm":"arguments"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"},"nanoseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in nanoseconds since an arbitrary\n         starting point."]},"$nm":"nanoseconds"},"milliseconds":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The elapsed time in milliseconds since midnight, \n         1 January 1970."]},"$nm":"milliseconds"}},"$nm":"process"},"product":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Value"}],"$an":{"shared":[],"see":["sum"],"doc":["Given a nonempty sequence of `Numeric` values, return \n     the product of the values."]},"$nm":"product"},"forItem":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"resulting"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"entry"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"},{"$nm":"Result"}],"$an":{"shared":[],"see":["forKey"],"doc":["A function that returns the result of the given `resulting()` function \n     on the item of a given `Entry`."]},"$nm":"forItem"},"shuffle":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$nm":"SecondArgs"}],"$pk":"ceylon.language","$nm":"Callable"},{"$mt":"tpm","$nm":"FirstArgs"}],"$pk":"ceylon.language","$nm":"Callable"},"$mt":"prm","$pt":"v","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Result"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"FirstArgs"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"SecondArgs"}],"$an":{"shared":[]},"$nm":"shuffle"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"characters"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Create a new string containing the given characters."]},"$nm":"string"},"nothing":{"$t":{"$pk":"ceylon.language","$nm":"Nothing"},"$mt":"gttr","$an":{"shared":[],"doc":["A value that is assignable to any type, but that \n     results in an exception when evaluated. This is most \n     useful for generating members in an IDE."]},"$nm":"nothing"},"doc":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"description"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify API documentation of a program\n     element."]},"$nm":"doc"},"Scalar":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$pk":"ceylon.language","$nm":"Number"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Scalar"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float"],"doc":["Abstraction of numeric types representing scalar\n     values, including `Integer` and `Float`."],"by":["Gavin"]},"$at":{"magnitude":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of this number."],"actual":[]},"$nm":"magnitude"},"wholePart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \n         of the fractional part. For integral numeric types,\n         the integral value of a number is the number \n         itself."],"actual":[]},"$nm":"wholePart"},"fractionalPart":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \n         of the integral part. For integral numeric types,\n         the fractional part is always zero."],"actual":[]},"$nm":"fractionalPart"}},"$nm":"Scalar","$st":"Other"},"ifExists":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"prm","$pt":"f","$nm":"predicate"}],[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"val"}]],"$mt":"mthd","$nm":"ifExists"},"tagged":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"tags"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to categorize the API by tag."]},"$nm":"tagged"},"SequenceBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$tp":[{"$nm":"Element"}],"$an":{"shared":[],"native":[],"see":["SequenceAppender","join","Singleton"],"doc":["Since sequences are immutable, this class is used for\n     constructing a new sequence by incrementally appending \n     elements to the empty sequence. This class is mutable\n     but threadsafe."]},"$m":{"append":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append an element to the sequence and return this \n         `SequenceBuilder`"]},"$nm":"append"},"appendAll":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"SequenceBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Append multiple elements to the sequence and return \n         this `SequenceBuilder`"]},"$nm":"appendAll"}},"$at":{"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"native":[],"doc":["The resulting sequence. If no elements have been\n         appended, the empty sequence."]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["Determine if the resulting sequence is empty."]},"$nm":"empty"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"doc":["The size of the resulting sequence."]},"$nm":"size"}},"$nm":"SequenceBuilder"},"variable":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark an attribute as variable. A `variable` \n     attribute must be assigned with `=` and may be \n     reassigned over time."]},"$nm":"variable"},"Correspondence":{"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"in","$nm":"Key"},{"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Map","List","Category"],"doc":["Abstract supertype of objects which associate values \n     with keys. `Correspondence` does not satisfy `Category`,\n     since in some cases&mdash;`List`, for example&mdash;it is \n     convenient to consider the subtype a `Category` of its\n     values, and in other cases&mdash;`Map`, for example&mdash;it \n     is convenient to treat the subtype as a `Category` of its\n     entries.\n     \n     The item corresponding to a given key may be obtained \n     from a `Correspondence` using the item operator:\n     \n         value bg = settings[\"backgroundColor\"] else white;\n     \n     The `get()` operation and item operator result in an\n     optional type, to reflect the possibility that there is\n     no item for the given key."],"by":["Gavin"]},"$m":{"definesAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["Determines if this `Correspondence` defines a value\n         for any one of the given keys."]},"$nm":"definesAny"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["definesAny","definesEvery","keys"],"doc":["Determines if there is a value defined for the \n         given key."]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["items"],"doc":["Returns the value defined for the given key, or \n         `null` if there is no value defined for the given \n         key."]},"$nm":"get"},"items":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["get"],"doc":["Returns the items defined for the given keys, in\n         the same order as the corresponding keys."]},"$nm":"items"},"definesEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["defines"],"doc":["Determines if this `Correspondence` defines a value\n         for every one of the given keys."]},"$nm":"definesEvery"}},"$at":{"keys":{"$t":{"$pk":"ceylon.language","$nm":"Category"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["defines"],"doc":["The `Category` of all keys for which a value is \n         defined by this `Correspondence`."]},"$nm":"keys"}},"$nm":"Correspondence"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"doc":["A count of the number of `true` items in the given values."]},"$nm":"count"},"NonemptyContainer":{"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["A nonempty container."]},"$alias":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"NonemptyContainer"},"internalFirst":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"native":[]},"$nm":"internalFirst"},"byItem":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Item","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}],[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"see":["byKey"],"doc":["A comparator for `Entry`s which compares their items \n     according to the given `comparing()` function."]},"$nm":"byItem"},"by":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"authors"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify API authors."]},"$nm":"by"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["any"],"doc":["Determines if every one of the given boolean values \n     (usually a comprehension) is `true`."]},"$nm":"every"},"$pkg-shared":"1","Tuple":{"super":{"$pk":"ceylon.language","$nm":"Object"},"$ps":[{"$t":"First","$hdn":"1","$mt":"prm","$pt":"v","$nm":"first"},{"$t":"Rest","$hdn":"1","$mt":"prm","$pt":"v","$nm":"rest"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$nm":"Element"}],"variance":"out","$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$def":{"$pk":"ceylon.language","$nm":"Empty"},"variance":"out","$nm":"Rest"}],"$an":{"shared":[],"doc":["A _tuple_ is a typed linked list. Each instance of \n     `Tuple` represents the value and type of a single link.\n     The attributes `first` and `rest` allow us to retrieve\n     a value form the list without losing its static type \n     information.\n     \n         value point = Tuple(0.0, Tuple(0.0, Tuple(\"origin\")));\n         Float x = point.first;\n         Float y = point.rest.first;\n         String label = point.rest.rest.first;\n     \n     Usually, we abbreviate code involving tuples.\n     \n         [Float,Float,String] point = [0.0, 0.0, \"origin\"];\n         Float x = point[0];\n         Float y = point[1];\n         String label = point[2];\n     \n     A list of types enclosed in brackets is an abbreviated \n     tuple type. An instance of `Tuple` may be constructed \n     by surrounding a value list in brackets:\n     \n         [String,String] words = [\"hello\", \"world\"];\n     \n     The index operator with a literal integer argument is a \n     shortcut for a chain of evaluations of `rest` and \n     `first`. For example, `point[1]` means `point.rest.first`.\n     \n     A _terminated_ tuple type is a tuple where the type of\n     the last link in the chain is `Empty`. An _unterminated_ \n     tuple type is a tuple where the type of the last link\n     in the chain is `Sequence` or `Sequential`. Thus, a \n     terminated tuple type has a length that is known\n     statically. For an unterminated tuple type only a lower\n     bound on its length is known statically.\n     \n     Here, `point` is an unterminated tuple:\n     \n         String[] labels = ... ;\n         [Float,Float,String*] point = [0.0, 0.0, *labels];\n         Float x = point[0];\n         Float y = point[1];\n         String? firstLabel = point[2];\n         String[] allLabels = point[2...];\n     \n     "],"by":["Gavin"]},"$m":{"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"end"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"$nm":"Element"},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"last"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"reversed"},"rest":{"$t":{"$nm":"Rest"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"rest"},"first":{"$t":{"$nm":"First"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"Tuple"},"lessThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"val"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"shared":[],"doc":["Returns a partial function that will compare an element\n     to any other element and returns true if the compared\n     element is less than its element.\n     This is useful in conjunction with methods that receive\n     a predicate function."]},"$nm":"lessThan"},"identityHash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"see":["identical"],"doc":["Return the system-defined identity hash value of the \n     given value. This hash value is consistent with \n     identity equality."]},"$nm":"identityHash"},"uncurry":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Argument"},{"$mt":"tpm","$nm":"First"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Rest"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"First","$mt":"prm","$pt":"v","$nm":"first"}]],"$mt":"prm","$pt":"f","$nm":"f"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"$nm":"Argument"},{"satisfies":[{"$nm":"Argument"}],"$nm":"First"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Argument"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Rest"}],"$an":{"shared":[]},"$nm":"uncurry"},"optional":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[]},"$nm":"optional"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$an":{"shared":[],"see":["every"],"doc":["Determines if any one of the given boolean values \n     (usually a comprehension) is `true`."]},"$nm":"any"},"Summable":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Other"}],"$an":{"shared":[],"see":["String","Numeric"],"doc":["Abstraction of types which support a binary addition\n     operator. For numeric types, this is just familiar \n     numeric addition. For strings, it is string \n     concatenation. In general, the addition operation \n     should be a binary associative operation."],"by":["Gavin"]},"$m":{"plus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The result of adding the given value to this value. \n         This operation should never perform any kind of \n         mutation upon either the receiving value or the \n         argument value."]},"$nm":"plus"}},"$nm":"Summable","$st":"Other"},"EmptyContainer":{"$mt":"ifc","$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"doc":["An empty container."]},"$alias":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Container"},"$nm":"EmptyContainer"},"Set":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["A collection of unique elements.\n     \n     A `Set` is a `Collection` of its elements.\n     \n     Sets may be the subject of the binary union, \n     intersection, exclusive union, and complement operators \n     `|`, `&`, `^`, and `~`."]},"$m":{"complement":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements in \n         this `Set` that are not contained in the given\n         `Set`."]},"$nm":"complement"},"subset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a subset of the given \n         `Set`, that is, if the given set contains all of \n         the elements in this set."]},"$nm":"subset"},"intersection":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \n         that are present in both this `Set` and the given \n         `Set`."]},"$nm":"intersection"},"exclusiveUnion":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing only the elements \n         contained in either this `Set` or the given `Set`, \n         but no element contained in both sets."]},"$nm":"exclusiveUnion"},"superset":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if this `Set` is a superset of the \n         specified Set, that is, if this `Set` contains all \n         of the elements in the specified `Set`."]},"$nm":"superset"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Set`s are considered equal if they have the \n         same size and if every element of the first set is\n         also an element of the second set, as determined\n         by `contains()`."],"actual":[]},"$nm":"equals"},"union":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Set"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"prm","$pt":"v","$nm":"set"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Other"}],"$an":{"shared":[],"formal":[],"doc":["Returns a new `Set` containing all the elements of \n         this `Set` and all the elements of the given `Set`."]},"$nm":"union"}},"$at":{"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Set"},"Sequence":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Empty"],"doc":["A nonempty, immutable sequence of values. The type \n     `Sequence<Element>`, may be abbreviated `[Element+]`.\n     \n     Given a possibly-empty sequence of type `[Element*], \n     the `if (nonempty ...)` construct, or, alternatively,\n     the `assert (nonempty ...)` construct, may be used to \n     narrow to a nonempty sequence type:\n     \n         [Integer*] nums = ... ;\n         if (nonempty nums) {\n             Integer first = nums.first;\n             Integer max = max(nums);\n             [Integer+] squares = nums.collect((Integer i) i**2));\n             [Integer+] sorted = nums.sort(byIncreasing((Integer i) i));\n         }\n     \n     Operations like `first`, `max()`, `collect()`, and \n     `sort()`, which polymorphically produce a nonempty or \n     non-null output when given a nonempty input are called \n     _emptiness-preserving_."],"by":["Gavin"]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the elements of this\n         container, sorted according to a function \n         imposing a partial order upon the elements."],"actual":[]},"$nm":"sort"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["A nonempty sequence containing the results of \n         applying the given mapping to the elements of this\n         sequence."],"actual":[]},"$nm":"collect"}},"$at":{"lastIndex":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["The index of the last element of the sequence."],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"clone"},"last":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element of the sequence, that is, the\n         element with index `sequence.lastIndex`."],"actual":[]},"$nm":"last"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["This sequence."],"actual":[]},"$nm":"sequence"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"doc":["Returns `false`, since every `Sequence` contains at\n         least one element."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The rest of the sequence, without the first \n         element."],"actual":[]},"$nm":"rest"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequence"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this sequence, returning a new nonempty\n         sequence."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"$nm":"Element"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element of the sequence, that is, the\n         element with index `0`."],"actual":[]},"$nm":"first"}},"$nm":"Sequence"},"StringBuilder":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$mt":"cls","$an":{"shared":[],"native":[],"doc":["Since strings are immutable, this class is used for\n     constructing a string by incrementally appending \n     characters to the empty string. This class is mutable \n     but threadsafe."]},"$m":{"append":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Append the characters in the given string."]},"$nm":"append"},"appendSpace":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a space character."]},"$nm":"appendSpace"},"delete":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"count"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Deletes the specified number of characters from the\n         current content, starting at the specified position.\n         If the position is beyond the end of the current content,\n         nothing is deleted. If the number of characters to delete\n         is greater than the available characters from the given\n         position, the content is truncated at the given position."]},"$nm":"delete"},"reset":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Remove all content and return to initial state."]},"$nm":"reset"},"appendAll":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the characters in the given strings."]},"$nm":"appendAll"},"insert":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"pos"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Character"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$nm":"content"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Insert a String or Character at the specified position.\n         If the position is beyond the end of the current\n         string, the new content is simply appended to the\n         current content. If the position is a negative number,\n         the new content is inserted at index 0."]},"$nm":"insert"},"appendNewline":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$mt":"mthd","$an":{"shared":[],"doc":["Append a newline character."]},"$nm":"appendNewline"},"appendCharacter":{"$t":{"$pk":"ceylon.language","$nm":"StringBuilder"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"character"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Append the given character."]},"$nm":"appendCharacter"}},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["The resulting string. If no characters have been\n         appended, the empty string."],"actual":[]},"$nm":"string"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"native":[],"doc":["Returns the size of the current content."]},"$nm":"size"}},"$nm":"StringBuilder"},"emptyOrSingleton":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Element"}],"$an":{"shared":[],"see":["Singleton","Empty"],"doc":["A `Singleton` if the given element is non-null, otherwise `Empty`."]},"$nm":"emptyOrSingleton"},"shared":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a type or member as shared. A `shared` \n     member is visible outside the block of code in which it\n     is declared."]},"$nm":"shared"},"Binary":{"of":[{"$nm":"Other"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Binary"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer"],"doc":["Abstraction of numeric types that consist in\n     a sequence of bits, like `Integer`."],"by":["Stef"]},"$m":{"clear":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Returns a new number with the given bit set to 0.\n         Bits are indexed from right to left."]},"$nm":"clear"},"xor":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical exclusive OR operation."]},"$nm":"xor"},"get":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Retrieves a given bit from this bit sequence. Bits are indexed from\n         right to left."]},"$nm":"get"},"leftLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a left logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"leftLogicalShift"},"set":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"bit"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit set to the given value.\n         Bits are indexed from right to left."]},"$nm":"set"},"or":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical inclusive OR operation."]},"$nm":"or"},"rightArithmeticShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right arithmetic shift. Sign is preserved. Padded with zeros."]},"$nm":"rightArithmeticShift"},"rightLogicalShift":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"shift"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a right logical shift. Sign is not preserved. Padded with zeros."]},"$nm":"rightLogicalShift"},"flip":{"$t":{"$nm":"Other"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a new number with the given bit flipped to its opposite value.\n         Bits are indexed from right to left."]},"$nm":"flip"},"and":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Performs a logical AND operation."]},"$nm":"and"}},"$at":{"not":{"$t":{"$nm":"Other"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The binary complement of this sequence of bits."]},"$nm":"not"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The number of bits (0 or 1) that this sequence of bits can hold."]},"$nm":"size"}},"$nm":"Binary","$st":"Other"},"commaList":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$nm":"commaList"},"Number":{"$mt":"ifc","$an":{"shared":[],"see":["Numeric"],"doc":["Abstraction of numbers. Numeric operations are provided\n     by the subtype `Numeric`. This type defines operations\n     which can be expressed without reference to the self\n     type `Other` of `Numeric`."],"by":["Gavin"]},"$at":{"sign":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The sign of this number. Returns `1` if the number \n         is positive, `-1` if it is negative, or `0` if it \n         is zero."]},"$nm":"sign"},"integer":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas an `Integer`"],"doc":["The number, represented as an `Integer`, after \n         truncation of any fractional part."]},"$nm":"integer"},"magnitude":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The magnitude of the number."]},"$nm":"magnitude"},"wholePart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The integral value of the number after truncation \n         of the fractional part."]},"$nm":"wholePart"},"negative":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is negative."]},"$nm":"negative"},"positive":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the number is positive."]},"$nm":"positive"},"fractionalPart":{"$t":{"$pk":"ceylon.language","$nm":"Number"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The fractional part of the number, after truncation \n         of the integral part."]},"$nm":"fractionalPart"},"float":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"formal":[],"throws":["OverflowException","if the number is too large to be represented \nas a `Float`"],"doc":["The number, represented as a `Float`."]},"$nm":"float"}},"$nm":"Number"},"LazyMap":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"entries"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$mt":"cls","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"doc":["A Map implementation that wraps an `Iterable` of \n     entries. All operations, such as lookups, size, etc. \n     are performed on the `Iterable`."],"by":["Enrique Zamudio"]},"$m":{"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Item"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"key"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"}},"$at":{"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"LazyMap"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}]},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"size"}},"$nm":"LazyMap"},"Map":{"satisfies":[{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Map"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"variance":"out","$nm":"Item"}],"$an":{"shared":[],"see":["Entry","forKey","forItem","byItem","byKey"],"doc":["Represents a collection which maps _keys_ to _items_,\n     where a key can map to at most one item. Each such \n     mapping may be represented by an `Entry`.\n     \n     A `Map` is a `Collection` of its `Entry`s, and a \n     `Correspondence` from keys to items.\n     \n     The prescence of an entry in a map may be tested\n     using the `in` operator:\n     \n         if (\"lang\"->\"en_AU\" in settings) { ... }\n     \n     The entries of the map may be iterated using `for`:\n     \n         for (key->item in settings) { ... }\n     \n     The item for a key may be obtained using the item\n     operator:\n     \n         String lang = settings[\"lang\"] else \"en_US\";"]},"$m":{"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `Map`s are considered equal iff they have the \n         same _entry sets_. The entry set of a `Map` is the\n         set of `Entry`s belonging to the map. Therefore, the\n         maps are equal iff they have same set of `keys`, and \n         for every key in the key set, the maps have equal\n         items."],"actual":[]},"$nm":"equals"},"mapItems":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Map"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Key","$mt":"prm","$pt":"v","$nm":"key"},{"$t":"Item","$mt":"prm","$pt":"v","$nm":"item"}]],"$mt":"prm","$pt":"f","$nm":"mapping"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["Returns a `Map` with the same keys as this map. For\n         every key, the item is the result of applying the\n         given transformation function."]},"$nm":"mapItems"}},"$at":{"values":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Collection"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["Returns all the values stored in this `Map`. An \n         element can be stored under more than one key in \n         the map, and so it can be contained more than once \n         in the resulting collection."]},"$nm":"values"},"inverse":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"}],"$pk":"ceylon.language","$nm":"Map"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["Returns a `Map` in which every key is an `Item` in \n         this map, and every value is the set of keys that \n         stored the `Item` in this map."]},"$nm":"inverse"},"keys":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"}],"$pk":"ceylon.language","$nm":"Set"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["Returns the set of keys contained in this `Map`."],"actual":[]},"$nm":"keys"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"}},"$nm":"Map"},"throws":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"type"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"when"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a program element that throws an \n     exception."]},"$nm":"throws"},"Numeric":{"of":[{"$nm":"Other"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Invertable"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Other"}],"$pk":"ceylon.language","$nm":"Numeric"}],"$nm":"Other"}],"$an":{"shared":[],"see":["Integer","Float","Comparable"],"doc":["Abstraction of numeric types supporting addition,\n     subtraction, multiplication, and division, including\n     `Integer` and `Float`. Additionally, a numeric type \n     is expected to define a total order via an \n     implementation of `Comparable`."],"by":["Gavin"]},"$m":{"minus":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The difference between this number and the given \n         number."]},"$nm":"minus"},"times":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The product of this number and the given number."]},"$nm":"times"},"divided":{"$t":{"$nm":"Other"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["Integral"],"doc":["The quotient obtained by dividing this number by \n         the given number. For integral numeric types, this \n         operation results in a remainder."]},"$nm":"divided"}},"$nm":"Numeric","$st":"Other"},"Closeable":{"$mt":"ifc","$an":{"shared":[],"doc":["Abstract supertype of types which may appear\n     as the expression type of a resource expression\n     in a `try` statement."]},"$m":{"open":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called before entry to a `try` block."]},"$nm":"open"},"close":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"prm","$pt":"v","$nm":"exception"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Called after completion of a `try` block."]},"$nm":"close"}},"$nm":"Closeable"},"formal":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member whose implementation must \n     be provided by subtypes."]},"$nm":"formal"},"byDecreasing":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Value","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"comparable"}],[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$tp":[{"$nm":"Element"},{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["byIncreasing"],"doc":["A comparator which orders elements in decreasing order \n     according to the `Comparable` returned by the given \n     `comparable()` function."]},"$nm":"byDecreasing"},"default":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member whose implementation may be \n     refined by subtypes. Non-`default` declarations may not \n     be refined."]},"$nm":"default"},"String":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Comparable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Summable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"native":[],"see":["string"],"doc":["A string of characters. Each character in the string is \n     a 32-bit Unicode character. The internal UTF-16 \n     encoding is hidden from clients.\n     \n     A string is a `Category` of its `Character`s, and of\n     its substrings:\n     \n         `w` in greeting \n         \"hello\" in greeting\n     \n     Strings are summable:\n     \n         String greeting = \"hello\" + \" \" + \"world\";\n     \n     They are efficiently iterable:\n     \n         for (char in \"hello world\") { ... }\n     \n     They are `List`s of `Character`s:\n     \n         value char = \"hello world\"[5];\n     \n     They are ranged:\n     \n         String who = \"hello world\"[6...];\n     \n     Note that since `string[index]` evaluates to the \n     optional type `Character?`, it is often more convenient\n     to write `string[index..index]`, which evaluates to a\n     `String` containing a single character, or to the empty\n     string \"\" if `index` refers to a position outside the\n     string.\n     \n     The `string()` function makes it possible to use \n     comprehensions to transform strings:\n     \n         string(for (s in \"hello world\") if (s.letter) s.uppercased)\n     \n     Since a `String` has an underlying UTF-16 encoding, \n     certain operations are expensive, requiring iteration\n     of the characters of the string. In particular, `size`\n     requires iteration of the whole string, and `get()`,\n     `span()`, and `segment()` require iteration from the \n     beginning of the string to the given index."],"by":["Gavin"]},"$m":{"plus":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the concatenation of this string with the\n         given string."],"actual":[]},"$nm":"plus"},"firstCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The first index at which the given character occurs\n         within this string, or `null` if the character does\n         not occur in this string."]},"$nm":"firstCharacterOccurrence"},"startsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if this string starts with the given \n         substring."]},"$nm":"startsWith"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Character"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the character at the given index in the \n         string, or `null` if the index is past the end of\n         string. The first character in the string occurs at\n         index zero. The last character in the string occurs\n         at index `string.size-1`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if the given object is a string, and if\n         so, if this string has the same length, and the \n         same characters, in the same order, as the given \n         string."],"actual":[]},"$nm":"equals"},"segment":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the characters of this string beginning at \n         the given index, returning a string no longer than \n         the given length. If the portion of this string\n         starting at the given index is shorter than \n         the given length, return the portion of this string\n         from the given index until the end of this string. \n         Otherwise return a string of the given length. If \n         the start index is larger than the last index of the \n         string, return the empty string."],"actual":[]},"$nm":"segment"},"compare":{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Compare this string with the given string \n         lexicographically, according to the Unicode values\n         of the characters."],"actual":[]},"$nm":"compare"},"lastCharacterOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The last index at which the given character occurs\n         within this string, or `null` if the character does\n         not occur in this string."]},"$nm":"lastCharacterOccurrence"},"longerThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string is longer than the given\n         length. This is a more efficient operation than\n         `string.size>length`."]},"$nm":"longerThan"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if the given object is a `String` and, \n         if so, if it occurs as a substring of this string,\n         or if the object is a `Character` that occurs in\n         this string. That is to say, a string is considered \n         a `Category` of its substrings and of its \n         characters."],"actual":[]},"$nm":"contains"},"repeat":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"times"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a string formed by repeating this string\n         the given number of times."]},"$nm":"repeat"},"join":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"strings"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Join the given strings, using this string as a \n         separator."]},"$nm":"join"},"replace":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"},{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"replacement"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns a string formed by replacing every \n         occurrence in this string of the given substring\n         with the given replacement string, working from \n         the start of this string to the end."]},"$nm":"replace"},"firstOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The first index at which the given substring occurs\n         within this string, or `null` if the substring does\n         not occur in this string."]},"$nm":"firstOccurrence"},"terminal":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the last characters of the string, \n         returning a string no longer than the given \n         length. If this string is shorter than the given\n         length, return this string. Otherwise return a\n         string of the given length."]},"$nm":"terminal"},"shorterThan":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string is shorter than the given\n         length. This is a more efficient operation than\n         `string.size>length`."]},"$nm":"shorterThan"},"spanTo":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"initial":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the first characters of this string, \n         returning a string no longer than the given \n         length. If this string is shorter than the given\n         length, return this string. Otherwise return a\n         string of the given length."]},"$nm":"initial"},"occurrences":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The character indexes at which the given substring\n         occurs within this string. Occurrences do not \n         overlap."]},"$nm":"occurrences"},"lastOccurrence":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["The last index at which the given substring occurs\n         within this string, or `null` if the substring does\n         not occur in this string."]},"$nm":"lastOccurrence"},"split":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"comp":"u","$ts":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Boolean"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Empty"}],"$pk":"ceylon.language","$nm":"Tuple"}],"$pk":"ceylon.language","$nm":"Callable"}]},"$mt":"prm","$pt":"v","$def":"1","$nm":"separator"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"discardSeparators"},{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"prm","$pt":"v","$def":"1","$nm":"groupSeparators"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Split the string into tokens, using the given\n         predicate to determine which characters are \n         separator characters."]},"$nm":"split"},"endsWith":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"substring"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Determines if this string ends with the given \n         substring."]},"$nm":"endsWith"},"spanFrom":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Select the characters between the given indexes.\n         If the start index is the same as the end index,\n         return a string with a single character.\n         If the start index is larger than the end index, \n         return the characters in the reverse order from\n         the order in which they appear in this string.\n         If both the start index and the end index are \n         larger than the last index in the string, return \n         the empty string. Otherwise, if the last index is \n         larger than the last index in the sequence, return\n         all characters from the start index to last \n         character of the string."],"actual":[]},"$nm":"span"}},"$at":{"normalized":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, after collapsing strings of whitespace \n         into single space characters and discarding whitespace \n         from the beginning and end of the string."]},"$nm":"normalized"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["An iterator for the characters of the string."],"actual":[]},"$nm":"iterator"},"lowercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with all characters in lowercase."]},"$nm":"lowercased"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"actual":[]},"$nm":"hash"},"uppercased":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with all characters in uppercase."]},"$nm":"uppercased"},"coalesced":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["Returns this string."],"actual":[]},"$nm":"coalesced"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["longerThan","shorterThan"],"doc":["The length of the string (the number of characters\n         it contains). In the case of the empty string, the\n         string has length zero. Note that this operation is\n         potentially costly for long strings, since the\n         underlying representation of the characters uses a\n         UTF-16 encoding."],"actual":[]},"$nm":"size"},"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"doc":["The index of the last character in the string, or\n         `null` if the string has no characters. Note that \n         this operation is potentially costly for long \n         strings, since the underlying representation of the \n         characters uses a UTF-16 encoding."],"actual":[]},"$nm":"lastIndex"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"doc":["Returns the string itself."],"actual":[]},"$nm":"string"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["Determines if this string has no characters, that\n         is, if it has zero `size`. This is a more efficient \n         operation than `string.size==0`."],"actual":[]},"$nm":"empty"},"lines":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"String"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Split the string into lines of text."]},"$nm":"lines"},"trimmed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, after discarding whitespace from the \n         beginning and end of the string."]},"$nm":"trimmed"},"reversed":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["This string, with the characters in reverse order."],"actual":[]},"$nm":"reversed"},"characters":{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Character"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The characters in this string."]},"$nm":"characters"}},"$nm":"String"},"identical":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"x"},{"$t":{"$pk":"ceylon.language","$nm":"Identifiable"},"$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"mthd","$an":{"shared":[],"see":["identityHash"],"doc":["Determine if the arguments are identical. Equivalent to\n     `x===y`. Only instances of `Identifiable` have \n     well-defined identity."]},"$nm":"identical"},"late":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to disable definite initialization analysis\n     for a simple attribute."]},"$nm":"late"},"emptyIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"obj","$an":{"shared":[],"doc":["An iterator that returns no elements."]},"$nm":"emptyIterator"},"integerRangeByIterable":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Range"},"$mt":"prm","$pt":"v","$nm":"range"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Ordinal"},{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Comparable"}],"$nm":"Element"}],"$an":{"native":[],"doc":["Provides an optimized implementation of `Range<Integer>.iterator`. \n     This is necessary because we need reified generics in order to write \n     the optimized version in pure Ceylon."]},"$nm":"integerRangeByIterable"},"parseFloat":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Float"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Float` value of the given string representation of \n     a decimal number or `null` if the string does not \n     represent a decimal number.\n     \n     The syntax accepted by this method is the same as the \n     syntax for a `Float` literal in the Ceylon language \n     except that it may optionally begin with a sign \n     character (`+` or `-`)."]},"$nm":"parseFloat"},"Anything":{"abstract":"1","of":[{"$pk":"ceylon.language","$nm":"Object"},{"$pk":"ceylon.language","$nm":"Null"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The abstract supertype of all types. A value of type \n     `Anything` may be a definite value of type `Object`, or \n     it may be the `null` value. A method declared `void` is \n     considered to have the return type `Anything`.\n     \n     Note that the type `Nothing`, representing the \n     intersection of all types, is a subtype of all types."],"by":["Gavin"]},"$nm":"Anything"},"Exception":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"description"},{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$hdn":"1","$mt":"prm","$pt":"v","$def":"1","$nm":"cause"}],"$mt":"cls","$an":{"shared":[],"native":[],"doc":["The supertype of all exceptions. A subclass represents\n     a more specific kind of problem, and may define \n     additional attributes which propagate information about\n     problems of that kind."],"by":["Gavin","Tom"]},"$m":{"printStackTrace":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Print the stack trace to the standard error of\n         the virtual machine process."]},"$nm":"printStackTrace"}},"$at":{"message":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["description","cause"],"doc":["A message describing the problem. This default \n         implementation returns the description, if any, or \n         otherwise the message of the cause, if any."]},"$nm":"message"},"cause":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Exception"}]},"$mt":"attr","$an":{"shared":[],"doc":["The underlying cause of this exception."]},"$nm":"cause"},"description":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"String"}]},"$mt":"attr","$an":{"doc":["A description of the problem."]},"$nm":"description"},"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"string"}},"$nm":"Exception"},"internalSort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$tp":[{"$nm":"Element"}],"$an":{"native":[]},"$nm":"internalSort"},"OverflowException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a mathematical operation caused a number to overflow from its bounds."]},"$nm":"OverflowException"},"parseInteger":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"prm","$pt":"v","$nm":"string"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["The `Integer` value of the given string representation \n     of an integer, or `null` if the string does not represent \n     an integer or if the mathematical integer it represents \n     is too large in magnitude to be represented by an \n     `Integer`.\n     \n     The syntax accepted by this method is the same as the \n     syntax for an `Integer` literal in the Ceylon language \n     except that it may optionally begin with a sign \n     character (`+` or `-`)."]},"$nm":"parseInteger"},"sum":{"$t":{"$nm":"Value"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Nothing"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Summable"}],"$nm":"Value"}],"$an":{"shared":[],"see":["product"],"doc":["Given a nonempty sequence of `Summable` values, return \n     the sum of the values."]},"$nm":"sum"},"infinity":{"$t":{"$pk":"ceylon.language","$nm":"Float"},"$mt":"attr","$an":{"shared":[],"doc":["An instance of `Float` representing positive infinity \n     ∞."]},"$nm":"infinity"},"smaller":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is smaller than the given value."],"by":["Gavin"]},"$nm":"smaller"},"flatten":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Return"},{"$mt":"tpm","$nm":"Args"}],"$pk":"ceylon.language","$nm":"Callable"},"$ps":[[{"$t":"Return","$ps":[[{"$t":"Args","$mt":"prm","$pt":"v","$nm":"tuple"}]],"$mt":"prm","$pt":"f","$nm":"tupleFunction"}]],"$mt":"mthd","$tp":[{"$nm":"Return"},{"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"}],"$nm":"Args"}],"$an":{"shared":[],"native":[]},"$nm":"flatten"},"Iterable":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Container"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"native":[],"see":["Collection"],"doc":["Abstract supertype of containers whose elements may be \n     iterated. An iterable container need not be finite, but\n     its elements must at least be countable. There may not\n     be a well-defined iteration order, and so the order of\n     iterated elements may not be stable.\n     \n     The type `Iterable<Element,Null>`, usually abbreviated\n     `{Element*}` represents a possibly-empty iterable \n     container. The type `Iterable<Element,Nothing>`, \n     usually abbreviated `{Element+}` represents a nonempty \n     iterable container.\n     \n     An instance of `Iterable` may be constructed by \n     surrounding a value list in braces:\n     \n         {String+} words = { \"hello\", \"world\" };\n     \n     An instance of `Iterable` may be iterated using a `for`\n     loop:\n     \n         for (c in \"hello world\") { ... }\n     \n     `Iterable` and its subtypes define various operations\n     that return other iterable objects. Such operations \n     come in two flavors:\n     \n     - _Lazy_ operations return a \"view\" of the receiving\n       iterable object. If the underlying iterable object is\n       mutable, then changes to the underlying object will\n       be reflected in the resulting view. Lazy operations\n       are usually efficient, avoiding memory allocation or\n       iteration of the receiving iterable object.\n       \n     - _Eager_ operations return an immutable object. If the\n       receiving iterable object is mutable, changes to this\n       object will not be reflected in the resulting \n       immutable object. Eager operations are often \n       expensive, involving memory allocation and iteration\n       of the receiving iterable object.\n     \n     Lazy operations are preferred, because they can be \n     efficiently chained. For example:\n     \n         string.filter((Character c) => c.letter)\n               .map((Character c) => c.uppercased)\n     \n     is much less expensive than:\n     \n         string.select((Character c) => c.letter)\n               .collect((Character c) => c.uppercased)\n     \n     Furthermore, it is always easy to produce a new \n     immutable iterable object given the view produced by a\n     lazy operation. For example:\n     \n         [ string.filter((Character c) => c.letter)\n                 .map((Character c) => c.uppercased)... ]\n     \n     Lazy operations normally return an instance of \n     `Iterable` or `Map`.\n     \n     However, there are certain scenarios where an eager \n     operation is more useful, more convenient, or no more \n     expensive than a lazy operation, including:\n     \n     - sorting operations, which are eager by nature,\n     - operations which preserve emptiness\/nonemptiness of\n       the receiving iterable object.\n     \n     Eager operations normally return a sequence."],"by":["Gavin"]},"$m":{"sort":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Comparison"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"x"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"y"}]],"$mt":"prm","$pt":"f","$nm":"comparing"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["byIncreasing","byDecreasing"],"doc":["A sequence containing the elements of this\n         container, sorted according to a function \n         imposing a partial order upon the elements.\n         \n         For convenience, the functions `byIncreasing()` \n         and `byDecreasing()` produce a suitable \n         comparison function:\n         \n             \"Hello World!\".sort(byIncreasing((Character c) => c.lowercased))\n         \n         This operation is eager by nature."]},"$nm":"sort"},"count":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return the number of elements in this `Iterable` \n         that satisfy the predicate function."]},"$nm":"count"},"select":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["filter"],"doc":["A sequence containing the elements of this \n         container that satisfy the given predicate. An \n         eager counterpart to `filter()`."]},"$nm":"select"},"by":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"step"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"throws":["Exception","if the given step size is nonpositive, \ni.e. `step<1`"],"doc":["Produce an `Iterable` containing every `step`th \n         element of this iterable object. If the step size \n         is `1`, the `Iterable` contains the same elements \n         as this iterable object. The step size must be \n         greater than zero. The expression\n         \n             (0..10).by(3)\n         \n         results in an iterable object with the elements\n         `0`, `3`, `6`, and `9` in that order."]},"$nm":"by"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"contains"},"fold":{"$t":{"$nm":"Result"},"$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"initial"},{"$t":"Result","$ps":[[{"$t":"Result","$mt":"prm","$pt":"v","$nm":"partial"},{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"accumulating"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"doc":["The result of applying the accumulating function to \n         each element of this container in turn."]},"$nm":"fold"},"every":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if all elements satisfy the predicate\n         function."]},"$nm":"every"},"defaultNullElements":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"comp":"i","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$nm":"Default"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Default","$mt":"prm","$pt":"v","$nm":"defaultValue"}]],"$mt":"mthd","$tp":[{"$nm":"Default"}],"$an":{"shared":[],"default":[],"doc":["Returns an Iterable that contains this `Iterable`'s elements but that\n         will return `defaultValue` instead of `null` for `null` elements of\n         that `Iterable`.\n         \n         Calling this method on an `Iterable` that cannot have `null` values\n         will not change the `Iterable` behavior. This means that calling this\n         method on an `Iterable` which has been obtained using this method will\n         not change the default value as there is no `null` value anymore."]},"$nm":"defaultNullElements"},"taking":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"take"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the first `take`\n         elements of this iterable object. If the specified \n         number of elements is larger than the number of \n         elements of this iterable object, the `Iterable` \n         contains the same elements as this iterable object."]},"$nm":"taking"},"chain":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"other"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["The elements of this iterable object, in their\n         original order, followed by the elements of the \n         given iterable object also in their original\n         order."]},"$nm":"chain"},"any":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"e"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Return `true` if at least one element satisfies the\n         predicate function."]},"$nm":"any"},"map":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["collect"],"doc":["An `Iterable` containing the results of applying\n         the given mapping to the elements of to this \n         container."]},"$nm":"map"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The last element which satisfies the given\n         predicate, if any, or `null` otherwise."]},"$nm":"findLast"},"filter":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["select"],"doc":["An `Iterable` containing the elements of this \n         container that satisfy the given predicate."]},"$nm":"filter"},"find":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["The first element which satisfies the given \n         predicate, if any, or `null` otherwise."]},"$nm":"find"},"skipping":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"skip"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Produce an `Iterable` containing the elements of\n         this iterable object, after skipping the first \n         `skip` elements. If this iterable object does not \n         contain more elements than the specified number of \n         elements, the `Iterable` contains no elements."]},"$nm":"skipping"},"collect":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Result"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":"Result","$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"prm","$pt":"f","$nm":"collecting"}]],"$mt":"mthd","$tp":[{"$nm":"Result"}],"$an":{"shared":[],"default":[],"see":["map"],"doc":["A sequence containing the results of applying the\n         given mapping to the elements of this container. An \n         eager counterpart to `map()`."]},"$nm":"collect"}},"$at":{"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["An iterator for the elements belonging to this \n         container."]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["The last element returned by the iterator, if any.\n         Iterables are potentially infinite, so calling this\n         might never return; also, this implementation will\n         iterate through all the elements, which might be\n         very time-consuming."],"actual":[]},"$nm":"last"},"indexed":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]}],"$pk":"ceylon.language","$nm":"Entry"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["All entries of form `index->element` where `index` \n         is the position at which `element` occurs, for every\n         non-null element of this `Iterable`, ordered by\n         increasing `index`. For a null element at a given\n         position in the original `Iterable`, there is no \n         entry with the corresponding index in the resulting \n         iterable object. The expression \n         \n             { \"hello\", null, \"world\" }.indexed\n             \n         results in an iterable object with the entries\n         `0->\"hello\"` and `2->\"world\"`."]},"$nm":"indexed"},"sequence":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["A sequence containing the elements returned by the\n         iterator."]},"$nm":"sequence"},"coalesced":{"$t":{"$tp":[{"comp":"i","$mt":"tpm","$ts":[{"$nm":"Element"},{"$pk":"ceylon.language","$nm":"Object"}]},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["The non-null elements of this `Iterable`, in their\n         original order. For null elements of the original \n         `Iterable`, there is no entry in the resulting \n         iterable object."]},"$nm":"coalesced"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["Determines if the iterable object is empty, that is\n         to say, if the iterator returns no elements."],"actual":[]},"$nm":"empty"},"rest":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["Returns an iterable object containing all but the \n         first element of this container."]},"$nm":"rest"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["The first element returned by the iterator, if any.\n         This should always produce the same value as\n         `iterable.iterator.head`."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"default":[]},"$nm":"size"}},"$nm":"Iterable"},"LazyList":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elems"}],"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"doc":["An implementation of List that wraps an `Iterable` of\n     elements. All operations on this List are performed on \n     the Iterable."],"by":["Enrique Zamudio"]},"$m":{"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"equals"},"spanTo":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanTo"},"segment":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"length"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"segment"},"findLast":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":"Element","$mt":"prm","$pt":"v","$nm":"elem"}]],"$mt":"prm","$pt":"f","$nm":"selecting"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"actual":[]},"$nm":"findLast"},"spanFrom":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"spanFrom"},"span":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"from"},{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"to"}]],"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"span"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"gttr","$an":{"shared":[],"actual":[]},"$nm":"lastIndex"},"clone":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"clone"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"doc":["Returns a `List` with the elements of this `List` \n         in reverse order. This operation will create copy \n         the elements to a new `List`, so changes to the \n         original `Iterable` will no longer be reflected in \n         the new `List`."],"actual":[]},"$nm":"reversed"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"first"}},"$nm":"LazyList"},"className":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"obj"}]],"$mt":"mthd","$an":{"shared":[],"native":[],"doc":["Return the name of the concrete class of the given \n     object."]},"$nm":"className"},"List":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Collection"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Correspondence"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Ranged"},{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"}],"$pk":"ceylon.language","$nm":"Cloneable"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"}],"$an":{"shared":[],"see":["Sequence","Empty","Array"],"doc":["Represents a collection in which every element has a \n     unique non-negative integer index.\n     \n     A `List` is a `Collection` of its elements, and a \n     `Correspondence` from indices to elements.\n     \n     Direct access to a list element by index produces a\n     value of optional type. The following idiom may be\n     used instead of upfront bounds-checking, as long as \n     the list element type is a non-`null` type:\n     \n         value char = \"hello world\"[index];\n         if (exists char) { \/*do something*\/ }\n         else { \/*out of bounds*\/ }\n     \n     To iterate the indexes of a `List`, use the following\n     idiom:\n     \n         for (i->char in \"hello world\".indexed) { ... }\n     \n     "]},"$m":{"withTrailing":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["Returns a new `List` that contains the specified\n         element appended to the end of the elements of this \n         `List`."]},"$nm":"withTrailing"},"defines":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Determines if the given index refers to an element\n         of this sequence, that is, if\n         `index<=sequence.lastIndex`."],"actual":[]},"$nm":"defines"},"get":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"prm","$pt":"v","$nm":"index"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"doc":["Returns the element of this sequence with the given\n         index, or `null` if the given index is past the end\n         of the sequence, that is, if\n         `index>sequence.lastIndex`. The first element of\n         the sequence has index `0`."],"actual":[]},"$nm":"get"},"equals":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"that"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"doc":["Two `List`s are considered equal iff they have the \n         same `size` and _entry sets_. The entry set of a \n         list `l` is the set of elements of `l.indexed`. \n         This definition is equivalent to the more intuitive \n         notion that two lists are equal iff they have the \n         same `size` and for every index either:\n         \n         - the lists both have the element `null`, or\n         - the lists both have a non-null element, and the\n           two elements are equal."],"actual":[]},"$nm":"equals"},"withLeading":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Sequence"},"$ps":[[{"$t":"Other","$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$tp":[{"$nm":"Other"}],"$an":{"shared":[],"default":[],"doc":["Returns a new `List` that starts with the specified\n         element, followed by the elements of this `List`."]},"$nm":"withLeading"}},"$at":{"lastIndex":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$pk":"ceylon.language","$nm":"Integer"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"see":["size"],"doc":["The index of the last element of the list, or\n         null if the list is empty."]},"$nm":"lastIndex"},"iterator":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"Iterator"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"iterator"},"last":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"gttr","$an":{"shared":[],"default":[],"doc":["Returns the last element of this `List`, if any."],"actual":[]},"$nm":"last"},"hash":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"gttr","$an":{"shared":[],"default":[],"actual":[]},"$nm":"hash"},"reversed":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"}],"$pk":"ceylon.language","$nm":"List"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Reverse this list, returning a new list."]},"$nm":"reversed"},"first":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"default":[],"doc":["Returns the first element of this `List`, if any."],"actual":[]},"$nm":"first"},"size":{"$t":{"$pk":"ceylon.language","$nm":"Integer"},"$mt":"attr","$an":{"shared":[],"default":[],"see":["lastIndex"],"doc":["The number of elements in this sequence, always\n         `sequence.lastIndex+1`."],"actual":[]},"$nm":"size"}},"$nm":"List"},"Container":{"satisfies":[{"$pk":"ceylon.language","$nm":"Category"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Element"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$def":{"$pk":"ceylon.language","$nm":"Null"},"variance":"out","$nm":"Absent"}],"$an":{"shared":[],"deprecated":["Will be removed in Ceylon 1.0."],"see":["Category"],"doc":["Abstract supertype of objects which may or may not\n     contain one of more other values, called *elements*,\n     and provide an operation for accessing the first \n     element, if any. A container which may or may not be \n     empty is a `Container<Element,Null>`. A container which \n     is always empty is a `Container<Nothing,Null>`. A \n     container which is never empty is a \n     `Container<Element,Nothing>`."],"by":["Gavin"]},"$at":{"last":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The last element. Should produce `null` if the\n         container is empty, that is, for any instance for\n         which `empty` evaluates to `true`."]},"$nm":"last"},"empty":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["Determine if the container is empty, that is, if\n         it has no elements."]},"$nm":"empty"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Element"}]},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The first element. Should produce `null` if the \n         container is empty, that is, for any instance for\n         which `empty` evaluates to `true`."]},"$nm":"first"}},"$nm":"Container"},"abstract":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a class as abstract. An `abstract` \n     class may not be directly instantiated. An `abstract`\n     class may have enumerated cases."]},"$nm":"abstract"},"zip":{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$nm":"Item"}],"$pk":"ceylon.language","$nm":"Entry"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Key"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"keys"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Item"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"items"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Key"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Object"}],"$nm":"Item"}],"$an":{"shared":[],"doc":["Given two sequences, form a new sequence consisting of\n     all entries where, for any given index in the resulting\n     sequence, the key of the entry is the element occurring \n     at the same index in the first sequence, and the item \n     is the element occurring at the same index in the second \n     sequence. The length of the resulting sequence is the \n     length of the shorter of the two given sequences. \n     \n     Thus:\n     \n         zip(xs,ys)[i]==xs[i]->ys[i]\n     \n     for every `0<=i<min({xs.size,ys.size})`."]},"$nm":"zip"},"export":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[]},"$nm":"export"},"equal":{"super":{"$pk":"ceylon.language","$nm":"Comparison"},"$mt":"obj","$an":{"shared":[],"doc":["The value is exactly equal to the given value."],"by":["Gavin"]},"$nm":"equal"},"finished":{"super":{"$pk":"ceylon.language","$nm":"Finished"},"$mt":"obj","$an":{"shared":[],"see":["Iterator"],"doc":["A value that indicates that an `Iterator`\n     is exhausted and has no more values to \n     return."]},"$at":{"string":{"$t":{"$pk":"ceylon.language","$nm":"String"},"$mt":"attr","$an":{"shared":[],"actual":[]},"$nm":"string"}},"$nm":"finished"},"Boolean":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Basic"},"of":[{"$pk":"ceylon.language","$nm":"true"},{"$pk":"ceylon.language","$nm":"false"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A type capable of representing the values true and\n     false of Boolean logic."],"by":["Gavin"]},"$nm":"Boolean"},"print":{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Anything"},"$mt":"prm","$pt":"v","$nm":"line"}]],"$mt":"mthd","$an":{"shared":[],"see":["process.writeLine"],"doc":["Print a line to the standard output of the virtual \n     machine process, printing the given value's `string`, \n     or `«null»` if the value is `null`.\n     \n     This method is a shortcut for:\n     \n         process.writeLine(line?.string else \"«null»\")\n     \n     and is intended mainly for debugging purposes."],"by":["Gavin"]},"$nm":"print"},"ChainedIterator":{"super":{"$pk":"ceylon.language","$nm":"Basic"},"$ps":[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Element"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"first"},{"$t":{"$tp":[{"$mt":"tpm","$nm":"Other"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"second"}],"satisfies":[{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"}],"$mt":"cls","$tp":[{"variance":"out","$nm":"Element"},{"variance":"out","$nm":"Other"}],"$an":{"shared":[],"doc":["An `Iterator` that returns the elements of two\n     `Iterable`s, as if they were chained together."],"by":["Enrique Zamudio"]},"$m":{"next":{"$t":{"comp":"u","$ts":[{"$nm":"Element"},{"$nm":"Other"},{"$pk":"ceylon.language","$nm":"Finished"}]},"$mt":"mthd","$an":{"shared":[],"actual":[]},"$nm":"next"}},"$at":{"more":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"more"},"iter":{"$t":{"$tp":[{"comp":"u","$mt":"tpm","$ts":[{"$nm":"Element"},{"$nm":"Other"}]}],"$pk":"ceylon.language","$nm":"Iterator"},"var":"1","$mt":"attr","$an":{"variable":[]},"$nm":"iter"}},"$nm":"ChainedIterator"},"Basic":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$pk":"ceylon.language","$nm":"Identifiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["The default superclass when no superclass is explicitly\n     specified using `extends`. For the sake of convenience, \n     this class inherits a default definition of value\n     equality from `Identifiable`."],"by":["Gavin"]},"$nm":"Basic"},"Category":{"$mt":"ifc","$an":{"shared":[],"see":["Container"],"doc":["Abstract supertype of objects that contain other \n     values, called *elements*, where it is possible to \n     efficiently determine if a given value is an element. \n     `Category` does not satisfy `Container`, because it is \n     conceptually possible to have a `Category` whose \n     emptiness cannot be computed.\n     \n     The `in` operator may be used to determine if a value\n     belongs to a `Category`:\n     \n         if (\"hello\" in \"hello world\") { ... }\n         if (69 in 0..100) { ... }\n         if (key->value in { for (n in 0..100) n.string->n**2 }) { ... }\n     \n     Ordinarily, `x==y` implies that `x in cat == y in cat`.\n     But this contract is not required since it is possible\n     to form a meaningful `Category` using a different\n     equivalence relation. For example, an `IdentitySet` is\n     a meaningful `Category`."],"by":["Gavin"]},"$m":{"containsAny":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if any one of the given values belongs \n         to this `Category`"]},"$nm":"containsAny"},"contains":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language","$nm":"Object"},"$mt":"prm","$pt":"v","$nm":"element"}]],"$mt":"mthd","$an":{"shared":[],"formal":[],"see":["containsEvery","containsAny"],"doc":["Determines if the given value belongs to this\n         `Category`, that is, if it is an element of this\n         `Category`.\n         \n         For most `Category`s, if `x==y`, then \n         `category.contains(x)` evaluates to the same\n         value as `category.contains(y)`. However, it is\n         possible to form a `Category` consistent with some \n         other equivalence relation, for example `===`. \n         Therefore implementations of `contains()` which do \n         not satisfy this relationship are tolerated."]},"$nm":"contains"},"containsEvery":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Object"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Null"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"elements"}]],"$mt":"mthd","$an":{"shared":[],"default":[],"see":["contains"],"doc":["Determines if every one of the given values belongs\n         to this `Category`."]},"$nm":"containsEvery"}},"$nm":"Category"},"see":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Anything"}],"$pk":"ceylon.language","$nm":"Sequential"},"$mt":"prm","seq":"1","$pt":"v","$nm":"programElements"}]],"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to specify API references to other related \n     program elements."]},"$nm":"see"},"NegativeNumberException":{"super":{"$pk":"ceylon.language","$nm":"Exception"},"$mt":"cls","$an":{"shared":[],"doc":["Thrown when a negative number is not allowed."]},"$nm":"NegativeNumberException"},"actual":{"$t":{"$pk":"ceylon.language","$nm":"Null"},"$mt":"mthd","$an":{"shared":[],"doc":["Annotation to mark a member of a type as refining a \n     member of a supertype."]},"$nm":"actual"},"Integer":{"abstract":"1","super":{"$pk":"ceylon.language","$nm":"Object"},"satisfies":[{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Scalar"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Integral"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Binary"},{"$tp":[{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"},{"$mt":"tpm","$pk":"ceylon.language","$nm":"Integer"}],"$pk":"ceylon.language","$nm":"Exponentiable"}],"$mt":"cls","$an":{"abstract":[],"shared":[],"doc":["A 64-bit integer (or the closest approximation to a \n     64-bit integer provided by the underlying platform)."]},"$at":{"character":{"$t":{"$pk":"ceylon.language","$nm":"Character"},"$mt":"attr","$an":{"shared":[],"formal":[],"doc":["The UTF-32 character with this UCS code point."]},"$nm":"character"}},"$nm":"Integer"},"first":{"$t":{"comp":"u","$ts":[{"$nm":"Absent"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Absent"}],"$pk":"ceylon.language","$nm":"Iterable"},"$mt":"prm","$pt":"v","$nm":"values"}]],"$mt":"mthd","$tp":[{"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language","$nm":"Null"}],"$nm":"Absent"}],"$an":{"shared":[],"doc":["The first of the given elements (usually a comprehension),\n     if any."]},"$nm":"first"}},"ceylon.language.metamodel":{"sequencedAnnotations":{"$t":{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"SequencedAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"sequencedAnnotations"},"optionalAnnotation":{"$t":{"comp":"u","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"OptionalAnnotation"}],"$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"optionalAnnotation"},"SequencedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language","$nm":"Sequential"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur multiple times\n     at a single program element."]},"$nm":"SequencedAnnotation"},"OptionalAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"comp":"u","$mt":"tpm","$ts":[{"$pk":"ceylon.language","$nm":"Null"},{"$nm":"Value"}]},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation that may occur at most once\n     at a single program element."]},"$nm":"OptionalAnnotation"},"Type":{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$mt":"ifc","$tp":[{"variance":"out","$nm":"Instance"}],"$an":{"shared":[]},"$nm":"Type"},"ConstrainedAnnotation":{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"},{"variance":"out","$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"variance":"in","$nm":"ProgramElement"}],"$an":{"shared":[],"doc":["An annotation. This interface encodes\n     constraints upon the annotation in its\n     type arguments."]},"$m":{"occurs":{"$t":{"$pk":"ceylon.language","$nm":"Boolean"},"$ps":[[{"$t":{"$pk":"ceylon.language.metamodel","$nm":"Annotated"},"$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$an":{"shared":[]},"$nm":"occurs"}},"$nm":"ConstrainedAnnotation"},"Annotation":{"$mt":"ifc","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"}],"$pk":"ceylon.language.metamodel","$nm":"Annotation"}],"variance":"out","$nm":"Value"}],"$an":{"shared":[],"doc":["An annotation."]},"$nm":"Annotation"},"annotations":{"$t":{"$nm":"Values"},"$ps":[[{"$t":{"$tp":[{"$mt":"tpm","$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$pk":"ceylon.language.metamodel","$nm":"Type"},"$mt":"prm","$pt":"v","$nm":"annotationType"},{"$t":"ProgramElement","$mt":"prm","$pt":"v","$nm":"programElement"}]],"$mt":"mthd","$tp":[{"satisfies":[{"$tp":[{"$mt":"tpm","$nm":"Value"},{"$mt":"tpm","$nm":"Values"},{"$mt":"tpm","$nm":"ProgramElement"}],"$pk":"ceylon.language.metamodel","$nm":"ConstrainedAnnotation"}],"$nm":"Value"},{"$nm":"Values"},{"satisfies":[{"$pk":"ceylon.language.metamodel","$nm":"Annotated"}],"$nm":"ProgramElement"}],"$an":{"shared":[]},"$nm":"annotations"},"Annotated":{"$mt":"ifc","$an":{"shared":[],"doc":["A program element that can\n     be annotated."]},"$nm":"Annotated"}}};
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
Identifiable$proto.getHash = function() { return $identityHash(this); }
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
Comprehension$proto.getIterator = function() {
return ComprehensionIterator(this.makeNextFunc(), this.$$targs$$);
}
Comprehension$proto.getSequence = function() {
var sb = SequenceBuilder(this.$$targs$$);
sb.appendAll(this);
return sb.getSequence();
}
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
            $$iterable.getEmpty=function getEmpty(){
                var $$iterable=this;
                return isOfType($$iterable.getIterator().next(),{t:Finished});
            };
            $$iterable.getSize=function getSize(){
                var $$iterable=this;
                return $$iterable.count(function (e$1){
                    var $$iterable=this;
                    return true;
                });
            };
            $$iterable.contains=function (element$2){
                var $$iterable=this;
                return $$iterable.any(ifExists((opt$3=element$2,JsCallable(opt$3,opt$3!==null?opt$3.equals:null))));
            };
            $$iterable.getFirst=function getFirst(){
                var $$iterable=this;
                return internalFirst($$iterable,{Value:$$iterable.$$targs$$.Element,Absent:$$iterable.$$targs$$.Absent});
            };
            $$iterable.getLast=function getLast(){
                var $$iterable=this;
                var e$4=$$iterable.getFirst();
                var setE$4=function(e$5){return e$4=e$5;};
                var it$6 = $$iterable.getIterator();
                var x$7;while ((x$7=it$6.next())!==getFinished()){
                    e$4=x$7;
                }
                return e$4;
            };$$iterable.getRest=function getRest(){
                var $$iterable=this;
                return $$iterable.skipping((1));
            };
            $$iterable.getSequence=function getSequence(){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$8=$$iterable.getIterator();
                    var x$9=getFinished();
                    var next$x$9=function(){return x$9=it$8.next();}
                    next$x$9();
                    return function(){
                        if(x$9!==getFinished()){
                            var x$9$10=x$9;
                            function getX$9(){return x$9$10;}
                            var tmpvar$11=getX$9();
                            next$x$9();
                            return tmpvar$11;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$iterable.$$targs$$.Element}).getSequence();
            };
            $$iterable.$map=function (collecting$12,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$13=$$iterable.getIterator();
                    var elem$14=getFinished();
                    var next$elem$14=function(){return elem$14=it$13.next();}
                    next$elem$14();
                    return function(){
                        if(elem$14!==getFinished()){
                            var elem$14$15=elem$14;
                            function getElem$14(){return elem$14$15;}
                            var tmpvar$16=collecting$12(getElem$14());
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
                    var it$18=$$iterable.getIterator();
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
                            function getElem$19(){return elem$19$20;}
                            var tmpvar$21=getElem$19();
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
                var it$26 = $$iterable.getIterator();
                var e$27;while ((e$27=it$26.next())!==getFinished()){
                    r$24=accumulating$23(r$24,e$27);
                }
                return r$24;
            };$$iterable.find=function find(selecting$28){
                var $$iterable=this;
                var it$29 = $$iterable.getIterator();
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
                var it$34 = $$iterable.getIterator();
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
                return $$iterable.$map(collecting$37,{Result:$$$mptypes.Result}).getSequence();
            };
            $$iterable.select=function (selecting$38){
                var $$iterable=this;
                return $$iterable.$filter(selecting$38).getSequence();
            };
            $$iterable.any=function any(selecting$39){
                var $$iterable=this;
                var it$40 = $$iterable.getIterator();
                var e$41;while ((e$41=it$40.next())!==getFinished()){
                    if(selecting$39(e$41)){
                        return true;
                    }
                }
                return false;
            };$$iterable.$every=function $every(selecting$42){
                var $$iterable=this;
                var it$43 = $$iterable.getIterator();
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
                            $$iterable$49.getIterator=function getIterator(){
                                var $$iterable$49=this;
                                var iter$50=$$iterable.getIterator();
                                var i$51=(0);
                                var setI$51=function(i$52){return i$51=i$52;};
                                while(((oldi$53=i$51,i$51=oldi$53.getSuccessor(),oldi$53).compare(skip$45).equals(getSmaller())&&(!isOfType(iter$50.next(),{t:Finished})))){}
                                var oldi$53;
                                return iter$50;
                            };$$iterable$49.getFirst=function getFirst(){
                                var $$iterable$49=this;
                                var first$54;
                                if(!isOfType((first$54=$$iterable$49.getIterator().next()),{t:Finished})){
                                    return first$54;
                                }else {
                                    return null;
                                }
                            };$$iterable$49.getLast=function getLast(){
                                var $$iterable$49=this;
                                return $$iterable.getLast();
                            };
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
                            $$iterable$60.getIterator=function getIterator(){
                                var $$iterable$60=this;
                                var iter$61=$$iterable.getIterator();
                                function iterator$62($$targs$$){
                                    var $$iterator$62=new iterator$62.$$;
                                    $$iterator$62.$$targs$$=$$targs$$;
                                    Iterator($$iterator$62);
                                    $$iterator$62.i$63=(0);
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
                                    $$iterator$62.getI$63=function getI$63(){
                                        return this.i$63;
                                    };
                                    $$iterator$62.setI$63=function setI$63(i$64){
                                        return this.i$63=i$64;
                                    };
                                    $$iterator$62.next=function next(){
                                        var $$iterator$62=this;
                                        return (opt$65=(($$iterator$62.setI$63($$iterator$62.getI$63().getSuccessor())).compare(take$56).equals(getLarger())?getFinished():null),opt$65!==null?opt$65:iter$61.next());
                                        var opt$65;
                                    };
                                })(iterator$62.$$.prototype);
                                var iterator$66=iterator$62({Element:$$iterable.$$targs$$.Element});
                                var getIterator$66=function(){
                                    return iterator$66;
                                }
                                return getIterator$66();
                            };$$iterable$60.getFirst=function getFirst(){
                                var $$iterable$60=this;
                                return $$iterable.getFirst();
                            };
                            $$iterable$60.getLast=function getLast(){
                                var $$iterable$60=this;
                                return $$iterable.getLast();
                            };
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
                //assert at Iterable.ceylon (317:8-318:25)
                if (!(step$68.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Iterable.ceylon (318:15-318:24)'); }
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
                            $$iterable$72.getIterator=function getIterator(){
                                var $$iterable$72=this;
                                var iter$73=$$iterable.getIterator();
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
                                        while(((i$76=i$76.getSuccessor()).compare(step$68).equals(getSmaller())&&(!isOfType(iter$73.next(),{t:Finished})))){}
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
                var it$83 = $$iterable.getIterator();
                var elem$84;while ((elem$84=it$83.next())!==getFinished()){
                    var elem$85;
                    if((elem$85=elem$84)!==null){
                        if(selecting$80(elem$85)){
                            (oldcount$86=count$81,count$81=oldcount$86.getSuccessor(),oldcount$86);
                            var oldcount$86;
                        }
                    }
                }
                return count$81;
            };$$iterable.getCoalesced=function getCoalesced(){
                var $$iterable=this;
                return Comprehension(function(){
                    var e$89;
                    var it$87=$$iterable.getIterator();
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
                            function getE$88(){return e$88$90;}
                            var tmpvar$91=e$89;
                            next$e$88();
                            return tmpvar$91;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
            };
            $$iterable.getIndexed=function getIndexed(){
                var $$iterable=this;
                function indexes$92($$targs$$){
                    var $$indexes$92=new indexes$92.$$;
                    $$indexes$92.$$targs$$=$$targs$$;
                    Iterable($$indexes$92);
                    add_type_arg($$indexes$92,'Absent',{t:Null});
                    add_type_arg($$indexes$92,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                    $$indexes$92.orig$93=$$iterable;
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
                    $$indexes$92.getOrig$93=function getOrig$93(){
                        return this.orig$93;
                    };
                    $$indexes$92.getIterator=function getIterator(){
                        var $$indexes$92=this;
                        function iterator$94($$targs$$){
                            var $$iterator$94=new iterator$94.$$;
                            $$iterator$94.$$targs$$=$$targs$$;
                            Iterator($$iterator$94);
                            add_type_arg($$iterator$94,'Element',{t:Entry,a:{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}}});
                            $$iterator$94.iter$95=$$indexes$92.getOrig$93().getIterator();
                            $$iterator$94.i$96=(0);
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
                            $$iterator$94.getIter$95=function getIter$95(){
                                return this.iter$95;
                            };
                            $$iterator$94.getI$96=function getI$96(){
                                return this.i$96;
                            };
                            $$iterator$94.setI$96=function setI$96(i$97){
                                return this.i$96=i$97;
                            };
                            $$iterator$94.next=function next(){
                                var $$iterator$94=this;
                                var next$98=$$iterator$94.getIter$95().next();
                                var setNext$98=function(next$99){return next$98=next$99;};
                                while((!exists(next$98))){
                                    (oldi$100=$$iterator$94.getI$96(),$$iterator$94.setI$96(oldi$100.getSuccessor()),oldi$100);
                                    var oldi$100;
                                    next$98=$$iterator$94.getIter$95().next();
                                }
                                var n$101;
                                var n$102;
                                if(!isOfType((n$101=next$98),{t:Finished})&&(n$102=n$101)!==null){
                                    return Entry((oldi$103=$$iterator$94.getI$96(),$$iterator$94.setI$96(oldi$103.getSuccessor()),oldi$103),n$102,{Key:{t:Integer},Item:{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}});
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
            };$$iterable.chain=function chain(other$106,$$$mptypes){
                var $$iterable=this;
                function chained$107($$targs$$){
                    var $$chained$107=new chained$107.$$;
                    $$chained$107.$$targs$$=$$targs$$;
                    Iterable($$chained$107);
                    add_type_arg($$chained$107,'Absent',{t:Null});
                    return $$chained$107;
                }
                function $init$chained$107(){
                    if (chained$107.$$===undefined){
                        initTypeProto(chained$107,'ceylon.language::Iterable.chain.chained',Basic,$init$Iterable());
                    }
                    return chained$107;
                }
                $init$chained$107();
                (function($$chained$107){
                    $$chained$107.getIterator=function getIterator(){
                        var $$chained$107=this;
                        return ChainedIterator($$iterable,other$106,{Other:$$$mptypes.Other,Element:$$iterable.$$targs$$.Element});
                    };
                })(chained$107.$$.prototype);
                var chained$108=chained$107({Absent:{t:Null},Element:{ t:'u', l:[$$iterable.$$targs$$.Element,$$$mptypes.Other]}});
                var getChained$108=function(){
                    return chained$108;
                }
                return getChained$108();
            };$$iterable.defaultNullElements=function (defaultValue$109,$$$mptypes){
                var $$iterable=this;
                return Comprehension(function(){
                    var it$110=$$iterable.getIterator();
                    var elem$111=getFinished();
                    var next$elem$111=function(){return elem$111=it$110.next();}
                    next$elem$111();
                    return function(){
                        if(elem$111!==getFinished()){
                            var elem$111$112=elem$111;
                            function getElem$111(){return elem$111$112;}
                            var tmpvar$113=(opt$114=getElem$111(),opt$114!==null?opt$114:defaultValue$109);
                            next$elem$111();
                            return tmpvar$113;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'u', l:[$$$mptypes.Default,{ t:'i', l:[$$iterable.$$targs$$.Element,{t:Object$}]}]}});
            };
        })(Iterable.$$.prototype);
    }
    return Iterable;
}
exports.$init$Iterable=$init$Iterable;
$init$Iterable();
var opt$3,opt$114;
function ifExists(predicate$115){
    return function(val$116){
        var val$117;
        if((val$117=val$116)!==null){
            return predicate$115(val$117);
        }else {
            return false;
        }
    }
};
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
            $$empty.getIterator=function getIterator(){
                var $$empty=this;
                return getEmptyIterator();
            };
            $$empty.get=function (index$118){
                var $$empty=this;
                return null;
            };
            $$empty.segment=function (from$119,length$120){
                var $$empty=this;
                return $$empty;
            };
            $$empty.span=function (from$121,to$122){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanTo=function (to$123){
                var $$empty=this;
                return $$empty;
            };
            $$empty.spanFrom=function (from$124){
                var $$empty=this;
                return $$empty;
            };
            $$empty.getEmpty=function getEmpty(){
                var $$empty=this;
                return true;
            };
            $$empty.getSize=function getSize(){
                var $$empty=this;
                return (0);
            };
            $$empty.getReversed=function getReversed(){
                var $$empty=this;
                return $$empty;
            };
            $$empty.getSequence=function getSequence(){
                var $$empty=this;
                return $$empty;
            };
            $$empty.getString=function getString(){
                var $$empty=this;
                return String$("{}",2);
            };
            $$empty.getLastIndex=function getLastIndex(){
                var $$empty=this;
                return null;
            };
            $$empty.getFirst=function getFirst(){
                var $$empty=this;
                return null;
            };
            $$empty.getLast=function getLast(){
                var $$empty=this;
                return null;
            };
            $$empty.getRest=function getRest(){
                var $$empty=this;
                return $$empty;
            };
            $$empty.getClone=function getClone(){
                var $$empty=this;
                return $$empty;
            };
            $$empty.getCoalesced=function getCoalesced(){
                var $$empty=this;
                return $$empty;
            };
            $$empty.getIndexed=function getIndexed(){
                var $$empty=this;
                return $$empty;
            };
            $$empty.chain=function (other$125,$$$mptypes){
                var $$empty=this;
                return other$125;
            };
            $$empty.contains=function (element$126){
                var $$empty=this;
                return false;
            };
            $$empty.count=function (selecting$127){
                var $$empty=this;
                return (0);
            };
            $$empty.defines=function (index$128){
                var $$empty=this;
                return false;
            };
            $$empty.$map=function (collecting$129,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.$filter=function (selecting$130){
                var $$empty=this;
                return $$empty;
            };
            $$empty.fold=function (initial$131,accumulating$132,$$$mptypes){
                var $$empty=this;
                return initial$131;
            };
            $$empty.find=function (selecting$133){
                var $$empty=this;
                return null;
            };
            $$empty.$sort=function (comparing$134){
                var $$empty=this;
                return $$empty;
            };
            $$empty.collect=function (collecting$135,$$$mptypes){
                var $$empty=this;
                return $$empty;
            };
            $$empty.select=function (selecting$136){
                var $$empty=this;
                return $$empty;
            };
            $$empty.any=function (selecting$137){
                var $$empty=this;
                return false;
            };
            $$empty.$every=function (selecting$138){
                var $$empty=this;
                return false;
            };
            $$empty.skipping=function (skip$139){
                var $$empty=this;
                return $$empty;
            };
            $$empty.taking=function (take$140){
                var $$empty=this;
                return $$empty;
            };
            $$empty.by=function (step$141){
                var $$empty=this;
                return $$empty;
            };
            $$empty.withLeading=function (element$142,$$$mptypes){
                var $$empty=this;
                return Tuple(element$142,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
            $$empty.withTrailing=function (element$143,$$$mptypes){
                var $$empty=this;
                return Tuple(element$143,getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Element,Element:$$$mptypes.Element});
            };
        })(Empty.$$.prototype);
    }
    return Empty;
}
exports.$init$Empty=$init$Empty;
$init$Empty();
function empty(){
    var $$empty=new empty.$$;
    Object$($$empty);
    Empty($$empty);
    return $$empty;
}
function $init$empty(){
    if (empty.$$===undefined){
        initTypeProto(empty,'ceylon.language::empty',Object$,$init$Empty());
    }
    return empty;
}
exports.$init$empty=$init$empty;
$init$empty();
var empty$144=empty();
var getEmpty=function(){
    return empty$144;
}
exports.getEmpty=getEmpty;
function emptyIterator($$targs$$){
    var $$emptyIterator=new emptyIterator.$$;
    $$emptyIterator.$$targs$$=$$targs$$;
    Iterator($$emptyIterator);
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
        return getFinished();
    };
})(emptyIterator.$$.prototype);
var emptyIterator$145=emptyIterator({Element:{t:Nothing}});
var getEmptyIterator=function(){
    return emptyIterator$145;
}
exports.getEmptyIterator=getEmptyIterator;
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
            $$sequential.getSequence=function getSequence(){
                var $$sequential=this;
                return $$sequential;
            };
            $$sequential.getClone=function getClone(){
                var $$sequential=this;
                return $$sequential;
            };
            $$sequential.getString=function getString(){
                var $$sequential=this;
                return (opt$146=($$sequential.getEmpty()?String$("[]",2):null),opt$146!==null?opt$146:StringBuilder().appendAll([String$("[",1),commaList($$sequential).getString(),String$("]",1)]).getString());
            };
        })(Sequential.$$.prototype);
    }
    return Sequential;
}
exports.$init$Sequential=$init$Sequential;
$init$Sequential();
var opt$146;
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
            $$sequence.getEmpty=function getEmpty(){
                var $$sequence=this;
                return false;
            };
            $$sequence.getSequence=function getSequence(){
                var $$sequence=this;
                return $$sequence;
            };
            $$sequence.$sort=function $sort(comparing$147){
                var $$sequence=this;
                var s$148=internalSort(comparing$147,$$sequence,{Element:$$sequence.$$targs$$.Element});
                //assert at Sequence.ceylon (63:8-63:27)
                var s$149;
                if (!(nonempty((s$149=s$148)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (63:15-63:26)'); }
                return s$149;
            };$$sequence.collect=function collect(collecting$150,$$$mptypes){
                var $$sequence=this;
                var s$151=$$sequence.$map(collecting$150,{Result:$$$mptypes.Result}).getSequence();
                //assert at Sequence.ceylon (74:8-74:27)
                var s$152;
                if (!(nonempty((s$152=s$151)))) { throw AssertionException('Assertion failed: \'nonempty s\' at Sequence.ceylon (74:15-74:26)'); }
                return s$152;
            };$$sequence.getClone=function getClone(){
                var $$sequence=this;
                return $$sequence;
            };
        })(Sequence.$$.prototype);
    }
    return Sequence;
}
exports.$init$Sequence=$init$Sequence;
$init$Sequence();
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
function finished(){
    var $$finished=new finished.$$;
    Finished($$finished);
    return $$finished;
}
function $init$finished(){
    if (finished.$$===undefined){
        initTypeProto(finished,'ceylon.language::finished',Finished);
    }
    return finished;
}
exports.$init$finished=$init$finished;
$init$finished();
(function($$finished){
    $$finished.getString=function getString(){
        var $$finished=this;
        return String$("finished",8);
    };
})(finished.$$.prototype);
var finished$153=finished();
var getFinished=function(){
    return finished$153;
}
exports.getFinished=getFinished;
function Keys(correspondence$154, $$targs$$,$$keys){
    $init$Keys();
    if ($$keys===undefined)$$keys=new Keys.$$;
    set_type_args($$keys,$$targs$$);
    $$keys.correspondence$154=correspondence$154;
    Category($$keys);
    return $$keys;
}
function $init$Keys(){
    if (Keys.$$===undefined){
        initTypeProto(Keys,'ceylon.language::Keys',Basic,$init$Category());
        (function($$keys){
            $$keys.contains=function contains(key$155){
                var $$keys=this;
                var key$156;
                if(isOfType((key$156=key$155),$$keys.$$targs$$.Key)){
                    return $$keys.correspondence$154.defines(key$156);
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
function Correspondence($$correspondence){
}
exports.Correspondence=Correspondence;
function $init$Correspondence(){
    if (Correspondence.$$===undefined){
        initTypeProto(Correspondence,'ceylon.language::Correspondence');
        (function($$correspondence){
            $$correspondence.defines=function (key$157){
                var $$correspondence=this;
                return exists($$correspondence.get(key$157));
            };
            $$correspondence.getKeys=function getKeys(){
                var $$correspondence=this;
                return Keys($$correspondence,{Key:$$correspondence.$$targs$$.Key,Item:$$correspondence.$$targs$$.Item});
            };
            $$correspondence.definesEvery=function definesEvery(keys$158){
                var $$correspondence=this;
                var it$159 = keys$158.getIterator();
                var key$160;while ((key$160=it$159.next())!==getFinished()){
                    if((!$$correspondence.defines(key$160))){
                        return false;
                    }
                }
                if (getFinished() === key$160){
                    return true;
                }
            };$$correspondence.definesAny=function definesAny(keys$161){
                var $$correspondence=this;
                var it$162 = keys$161.getIterator();
                var key$163;while ((key$163=it$162.next())!==getFinished()){
                    if($$correspondence.defines(key$163)){
                        return true;
                    }
                }
                if (getFinished() === key$163){
                    return false;
                }
            };$$correspondence.items=function (keys$164){
                var $$correspondence=this;
                return Comprehension(function(){
                    var it$165=keys$164.getIterator();
                    var key$166=getFinished();
                    var next$key$166=function(){return key$166=it$165.next();}
                    next$key$166();
                    return function(){
                        if(key$166!==getFinished()){
                            var key$166$167=key$166;
                            function getKey$166(){return key$166$167;}
                            var tmpvar$168=$$correspondence.get(getKey$166());
                            next$key$166();
                            return tmpvar$168;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'u', l:[{t:Null},$$correspondence.$$targs$$.Item]}}).getSequence();
            };
        })(Correspondence.$$.prototype);
    }
    return Correspondence;
}
exports.$init$Correspondence=$init$Correspondence;
$init$Correspondence();
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
function Binary($$binary){
}
exports.Binary=Binary;
function $init$Binary(){
    if (Binary.$$===undefined){
        initTypeProto(Binary,'ceylon.language::Binary');
        (function($$binary){
            $$binary.clear=function clear(index$169){
                var $$binary=this;
                return $$binary.set(index$169,false);
            };
        })(Binary.$$.prototype);
    }
    return Binary;
}
exports.$init$Binary=$init$Binary;
$init$Binary();
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
            $$collection.getEmpty=function getEmpty(){
                var $$collection=this;
                return $$collection.getSize().equals((0));
            };
            $$collection.contains=function contains(element$170){
                var $$collection=this;
                var it$171 = $$collection.getIterator();
                var elem$172;while ((elem$172=it$171.next())!==getFinished()){
                    var elem$173;
                    if((elem$173=elem$172)!==null&&elem$173.equals(element$170)){
                        return true;
                    }
                }
                if (getFinished() === elem$172){
                    return false;
                }
            };$$collection.getString=function getString(){
                var $$collection=this;
                return (opt$174=($$collection.getEmpty()?String$("{}",2):null),opt$174!==null?opt$174:StringBuilder().appendAll([String$("{ ",2),commaList($$collection).getString(),String$(" }",2)]).getString());
            };
        })(Collection.$$.prototype);
    }
    return Collection;
}
exports.$init$Collection=$init$Collection;
$init$Collection();
var opt$174;
var commaList=function (elements$175){
    return String$(", ",2).join(Comprehension(function(){
        var it$176=elements$175.getIterator();
        var element$177=getFinished();
        var next$element$177=function(){return element$177=it$176.next();}
        next$element$177();
        return function(){
            if(element$177!==getFinished()){
                var element$177$178=element$177;
                function getElement$177(){return element$177$178;}
                var tmpvar$179=(opt$180=(opt$181=getElement$177(),opt$181!==null?opt$181.getString():null),opt$180!==null?opt$180:String$("null",4));
                next$element$177();
                return tmpvar$179;
            }
            return getFinished();
        }
    },{Absent:{t:Anything},Element:{t:String$}}));
};
var opt$180,opt$181;
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
function Category($$category){
}
exports.Category=Category;
function $init$Category(){
    if (Category.$$===undefined){
        initTypeProto(Category,'ceylon.language::Category');
        (function($$category){
            $$category.containsEvery=function containsEvery(elements$182){
                var $$category=this;
                var it$183 = elements$182.getIterator();
                var element$184;while ((element$184=it$183.next())!==getFinished()){
                    if((!$$category.contains(element$184))){
                        return false;
                    }
                }
                if (getFinished() === element$184){
                    return true;
                }
            };$$category.containsAny=function containsAny(elements$185){
                var $$category=this;
                var it$186 = elements$185.getIterator();
                var element$187;while ((element$187=it$186.next())!==getFinished()){
                    if($$category.contains(element$187)){
                        return true;
                    }
                }
                if (getFinished() === element$187){
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
function Tuple(first$188, rest$189, $$targs$$,$$tuple){
    $init$Tuple();
    if ($$tuple===undefined)$$tuple=new Tuple.$$;
    set_type_args($$tuple,$$targs$$);
    Object$($$tuple);
    Sequence($$tuple);
    Cloneable($$tuple);
    add_type_arg($$tuple,'Clone',{t:Tuple,a:{Rest:$$tuple.$$targs$$.Rest,First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.Element}});
    $$tuple.first$190=first$188;
    $$tuple.rest$191=rest$189;
    return $$tuple;
}
exports.Tuple=Tuple;
function $init$Tuple(){
    if (Tuple.$$===undefined){
        initTypeProto(Tuple,'ceylon.language::Tuple',Object$,$init$Sequence(),$init$Cloneable());
        (function($$tuple){
            $$tuple.getFirst=function getFirst(){
                return this.first$190;
            };
            $$tuple.getRest=function getRest(){
                return this.rest$191;
            };
            $$tuple.getSize=function getSize(){
                var $$tuple=this;
                return (1).plus($$tuple.getRest().getSize());
            };
            $$tuple.get=function get(index$192){
                var $$tuple=this;
                
                var case$193=index$192.compare((0));
                if (case$193===getSmaller()) {
                    return null;
                }else if (case$193===getEqual()) {
                    return $$tuple.getFirst();
                }else if (case$193===getLarger()) {
                    return $$tuple.getRest().get(index$192.minus((1)));
                }
            };$$tuple.getLastIndex=function getLastIndex(){
                var $$tuple=this;
                var restLastIndex$194;
                if((restLastIndex$194=$$tuple.getRest().getLastIndex())!==null){
                    return restLastIndex$194.plus((1));
                }else {
                    return (0);
                }
            };$$tuple.getLast=function getLast(){
                var $$tuple=this;
                var rest$195;
                if(nonempty((rest$195=$$tuple.getRest()))){
                    return rest$195.getLast();
                }else {
                    return $$tuple.getFirst();
                }
            };$$tuple.getReversed=function getReversed(){
                var $$tuple=this;
                return $$tuple.getRest().getReversed().withTrailing($$tuple.getFirst(),{Other:$$tuple.$$targs$$.First});
            };
            $$tuple.segment=function segment(from$196,length$197){
                var $$tuple=this;
                if(length$197.compare((0)).equals(getSmaller())){
                    return getEmpty();
                }
                var realFrom$198=(opt$199=(from$196.compare((0)).equals(getSmaller())?(0):null),opt$199!==null?opt$199:from$196);
                var opt$199;
                if(realFrom$198.equals((0))){
                    return (opt$200=(length$197.equals((1))?Tuple($$tuple.getFirst(),getEmpty(),{Rest:{t:Empty},First:$$tuple.$$targs$$.First,Element:$$tuple.$$targs$$.First}):null),opt$200!==null?opt$200:$$tuple.getRest().segment((0),length$197.plus(realFrom$198).minus((1))).withLeading($$tuple.getFirst(),{Other:$$tuple.$$targs$$.First}));
                    var opt$200;
                }
                return $$tuple.getRest().segment(realFrom$198.minus((1)),length$197);
            };$$tuple.span=function span(from$201,end$202){
                var $$tuple=this;
                if((from$201.compare((0)).equals(getSmaller())&&end$202.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var realFrom$203=(opt$204=(from$201.compare((0)).equals(getSmaller())?(0):null),opt$204!==null?opt$204:from$201);
                var opt$204;
                var realEnd$205=(opt$206=(end$202.compare((0)).equals(getSmaller())?(0):null),opt$206!==null?opt$206:end$202);
                var opt$206;
                return (opt$207=((realFrom$203.compare(realEnd$205)!==getLarger())?$$tuple.segment(from$201,realEnd$205.minus(realFrom$203).plus((1))):null),opt$207!==null?opt$207:$$tuple.segment(realEnd$205,realFrom$203.minus(realEnd$205).plus((1))).getReversed().getSequence());
                var opt$207;
            };$$tuple.spanTo=function (to$208){
                var $$tuple=this;
                return (opt$209=(to$208.compare((0)).equals(getSmaller())?getEmpty():null),opt$209!==null?opt$209:$$tuple.span((0),to$208));
            };
            $$tuple.spanFrom=function (from$210){
                var $$tuple=this;
                return $$tuple.span(from$210,$$tuple.getSize());
            };
            $$tuple.getClone=function getClone(){
                var $$tuple=this;
                return $$tuple;
            };
            $$tuple.getIterator=function getIterator(){
                var $$tuple=this;
                function iterator$211($$targs$$){
                    var $$iterator$211=new iterator$211.$$;
                    $$iterator$211.$$targs$$=$$targs$$;
                    Iterator($$iterator$211);
                    $$iterator$211.current$212=$$tuple;
                    return $$iterator$211;
                }
                function $init$iterator$211(){
                    if (iterator$211.$$===undefined){
                        initTypeProto(iterator$211,'ceylon.language::Tuple.iterator.iterator',Basic,$init$Iterator());
                    }
                    return iterator$211;
                }
                $init$iterator$211();
                (function($$iterator$211){
                    $$iterator$211.getCurrent$212=function getCurrent$212(){
                        return this.current$212;
                    };
                    $$iterator$211.setCurrent$212=function setCurrent$212(current$213){
                        return this.current$212=current$213;
                    };
                    $$iterator$211.next=function next(){
                        var $$iterator$211=this;
                        var c$214;
                        if(nonempty((c$214=$$iterator$211.getCurrent$212()))){
                            $$iterator$211.setCurrent$212(c$214.getRest());
                            return c$214.getFirst();
                        }else {
                            return getFinished();
                        }
                    };
                })(iterator$211.$$.prototype);
                var iterator$215=iterator$211({Element:$$tuple.$$targs$$.Element});
                var getIterator$215=function(){
                    return iterator$215;
                }
                return getIterator$215();
            };$$tuple.contains=function contains(element$216){
                var $$tuple=this;
                var first$217;
                if((first$217=$$tuple.getFirst())!==null&&first$217.equals(element$216)){
                    return true;
                }else {
                    return $$tuple.getRest().contains(element$216);
                }
            };
        })(Tuple.$$.prototype);
    }
    return Tuple;
}
exports.$init$Tuple=$init$Tuple;
$init$Tuple();
var opt$209;
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
            $$list.getSize=function getSize(){
                var $$list=this;
                return (opt$218=$$list.getLastIndex(),opt$218!==null?opt$218:(-(1))).plus((1));
            };
            $$list.defines=function (index$219){
                var $$list=this;
                return (index$219.compare((opt$220=$$list.getLastIndex(),opt$220!==null?opt$220:(-(1))))!==getLarger());
            };
            $$list.getIterator=function getIterator(){
                var $$list=this;
                function listIterator$221($$targs$$){
                    var $$listIterator$221=new listIterator$221.$$;
                    $$listIterator$221.$$targs$$=$$targs$$;
                    Iterator($$listIterator$221);
                    $$listIterator$221.index$222=(0);
                    return $$listIterator$221;
                }
                function $init$listIterator$221(){
                    if (listIterator$221.$$===undefined){
                        initTypeProto(listIterator$221,'ceylon.language::List.iterator.listIterator',Basic,$init$Iterator());
                    }
                    return listIterator$221;
                }
                $init$listIterator$221();
                (function($$listIterator$221){
                    $$listIterator$221.getIndex$222=function getIndex$222(){
                        return this.index$222;
                    };
                    $$listIterator$221.setIndex$222=function setIndex$222(index$223){
                        return this.index$222=index$223;
                    };
                    $$listIterator$221.next=function next(){
                        var $$listIterator$221=this;
                        if(($$listIterator$221.getIndex$222().compare((opt$224=$$list.getLastIndex(),opt$224!==null?opt$224:(-(1))))!==getLarger())){
                            //assert at List.ceylon (58:20-58:61)
                            var elem$225;
                            if (!((elem$225=$$list.get((oldindex$226=$$listIterator$221.getIndex$222(),$$listIterator$221.setIndex$222(oldindex$226.getSuccessor()),oldindex$226)))!==null)) { throw AssertionException('Assertion failed: \'exists elem = outer.get(index++)\' at List.ceylon (58:27-58:60)'); }
                            var oldindex$226;
                            return elem$225;
                        }else {
                            return getFinished();
                        }
                        var opt$224;
                    };
                })(listIterator$221.$$.prototype);
                var listIterator$227=listIterator$221({Element:$$list.$$targs$$.Element});
                var getListIterator$227=function(){
                    return listIterator$227;
                }
                return getListIterator$227();
            };$$list.equals=function equals(that$228){
                var $$list=this;
                var that$229;
                if(isOfType((that$229=that$228),{t:List,a:{Element:{t:Anything}}})){
                    if(that$229.getSize().equals($$list.getSize())){
                        var it$230 = Range((0),$$list.getSize().minus((1)),{Element:{t:Integer}}).getIterator();
                        var i$231;while ((i$231=it$230.next())!==getFinished()){
                            var x$232=$$list.get(i$231);
                            var y$233=that$229.get(i$231);
                            var x$234;
                            if((x$234=x$232)!==null){
                                var y$235;
                                if((y$235=y$233)!==null){
                                    if((!x$234.equals(y$235))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$236;
                                if((y$236=y$233)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$231){
                            return true;
                        }
                    }
                }
                return false;
            };$$list.getHash=function getHash(){
                var $$list=this;
                var hash$237=(1);
                var setHash$237=function(hash$238){return hash$237=hash$238;};
                var it$239 = $$list.getIterator();
                var elem$240;while ((elem$240=it$239.next())!==getFinished()){
                    (hash$237=hash$237.times((31)));
                    var elem$241;
                    if((elem$241=elem$240)!==null){
                        (hash$237=hash$237.plus(elem$241.getHash()));
                    }
                }
                return hash$237;
            };$$list.getFirst=function getFirst(){
                var $$list=this;
                return $$list.get((0));
            };
            $$list.getLast=function getLast(){
                var $$list=this;
                var i$242;
                if((i$242=$$list.getLastIndex())!==null){
                    return $$list.get(i$242);
                }
                return null;
            };$$list.withLeading=function withLeading(element$243,$$$mptypes){
                var $$list=this;
                var sb$244=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                sb$244.append(element$243);
                if((!$$list.getEmpty())){
                    sb$244.appendAll($$list.getSequence());
                }
                //assert at List.ceylon (159:8-159:41)
                var seq$245;
                if (!(nonempty((seq$245=sb$244.getSequence())))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (159:15-159:40)'); }
                return seq$245;
            };$$list.withTrailing=function withTrailing(element$246,$$$mptypes){
                var $$list=this;
                var sb$247=SequenceBuilder({Element:{ t:'u', l:[$$list.$$targs$$.Element,$$$mptypes.Other]}});
                if((!$$list.getEmpty())){
                    sb$247.appendAll($$list.getSequence());
                }
                sb$247.append(element$246);
                //assert at List.ceylon (174:8-174:41)
                var seq$248;
                if (!(nonempty((seq$248=sb$247.getSequence())))) { throw AssertionException('Assertion failed: \'nonempty seq=sb.sequence\' at List.ceylon (174:15-174:40)'); }
                return seq$248;
            };
        })(List.$$.prototype);
    }
    return List;
}
exports.$init$List=$init$List;
$init$List();
var opt$218,opt$220;
function ChainedIterator(first$249, second$250, $$targs$$,$$chainedIterator){
    $init$ChainedIterator();
    if ($$chainedIterator===undefined)$$chainedIterator=new ChainedIterator.$$;
    set_type_args($$chainedIterator,$$targs$$);
    $$chainedIterator.second$250=second$250;
    Iterator($$chainedIterator);
    $$chainedIterator.iter$251=first$249.getIterator();
    $$chainedIterator.more$252=true;
    return $$chainedIterator;
}
exports.ChainedIterator=ChainedIterator;
function $init$ChainedIterator(){
    if (ChainedIterator.$$===undefined){
        initTypeProto(ChainedIterator,'ceylon.language::ChainedIterator',Basic,$init$Iterator());
        (function($$chainedIterator){
            $$chainedIterator.getIter$251=function getIter$251(){
                return this.iter$251;
            };
            $$chainedIterator.setIter$251=function setIter$251(iter$253){
                return this.iter$251=iter$253;
            };
            $$chainedIterator.getMore$252=function getMore$252(){
                return this.more$252;
            };
            $$chainedIterator.setMore$252=function setMore$252(more$254){
                return this.more$252=more$254;
            };
            $$chainedIterator.next=function next(){
                var $$chainedIterator=this;
                var e$255=$$chainedIterator.getIter$251().next();
                var setE$255=function(e$256){return e$255=e$256;};
                var f$257;
                if(isOfType((f$257=e$255),{t:Finished})){
                    if($$chainedIterator.getMore$252()){
                        $$chainedIterator.setIter$251($$chainedIterator.second$250.getIterator());
                        $$chainedIterator.setMore$252(false);
                        e$255=$$chainedIterator.getIter$251().next();
                    }
                }
                return e$255;
            };
        })(ChainedIterator.$$.prototype);
    }
    return ChainedIterator;
}
exports.$init$ChainedIterator=$init$ChainedIterator;
$init$ChainedIterator();
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
function Entry(key$258, item$259, $$targs$$,$$entry){
    $init$Entry();
    if ($$entry===undefined)$$entry=new Entry.$$;
    set_type_args($$entry,$$targs$$);
    Object$($$entry);
    $$entry.key$260=key$258;
    $$entry.item$261=item$259;
    return $$entry;
}
exports.Entry=Entry;
function $init$Entry(){
    if (Entry.$$===undefined){
        initTypeProto(Entry,'ceylon.language::Entry',Object$);
        (function($$entry){
            $$entry.getKey=function getKey(){
                return this.key$260;
            };
            $$entry.getItem=function getItem(){
                return this.item$261;
            };
            $$entry.equals=function equals(that$262){
                var $$entry=this;
                var that$263;
                if(isOfType((that$263=that$262),{t:Entry,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    return ($$entry.getKey().equals(that$263.getKey())&&$$entry.getItem().equals(that$263.getItem()));
                }else {
                    return false;
                }
            };$$entry.getHash=function getHash(){
                var $$entry=this;
                return (31).plus($$entry.getKey().getHash()).times((31)).plus($$entry.getItem().getHash());
            };
            $$entry.getString=function getString(){
                var $$entry=this;
                return StringBuilder().appendAll([$$entry.getKey().getString(),String$("->",2),$$entry.getItem().getString()]).getString();
            };
        })(Entry.$$.prototype);
    }
    return Entry;
}
exports.$init$Entry=$init$Entry;
$init$Entry();
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
function Exception(description$264, cause$265, $$exception){
    $init$Exception();
    if ($$exception===undefined)$$exception=new Exception.$$;
    if(description$264===undefined){description$264=null;}
    if(cause$265===undefined){cause$265=null;}
    $$exception.cause$266=cause$265;
    $$exception.description$267=description$264;
    return $$exception;
}
exports.Exception=Exception;
function $init$Exception(){
    if (Exception.$$===undefined){
        initTypeProto(Exception,'ceylon.language::Exception',Basic);
        (function($$exception){
            $$exception.getCause=function getCause(){
                return this.cause$266;
            };
            $$exception.getDescription$267=function getDescription$267(){
                return this.description$267;
            };
            $$exception.getMessage=function getMessage(){
                var $$exception=this;
                return (opt$268=(opt$269=$$exception.getDescription$267(),opt$269!==null?opt$269:(opt$270=$$exception.getCause(),opt$270!==null?opt$270.getMessage():null)),opt$268!==null?opt$268:String$("",0));
            };
            $$exception.getString=function getString(){
                var $$exception=this;
                return className($$exception).plus(String$(" \"\' message \'\"",14));
            };
        })(Exception.$$.prototype);
    }
    return Exception;
}
exports.$init$Exception=$init$Exception;
$init$Exception();
var opt$268,opt$269,opt$270;
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
            $$set.superset=function superset(set$271){
                var $$set=this;
                var it$272 = set$271.getIterator();
                var element$273;while ((element$273=it$272.next())!==getFinished()){
                    if((!$$set.contains(element$273))){
                        return false;
                    }
                }
                if (getFinished() === element$273){
                    return true;
                }
            };$$set.subset=function subset(set$274){
                var $$set=this;
                var it$275 = $$set.getIterator();
                var element$276;while ((element$276=it$275.next())!==getFinished()){
                    if((!set$274.contains(element$276))){
                        return false;
                    }
                }
                if (getFinished() === element$276){
                    return true;
                }
            };$$set.equals=function equals(that$277){
                var $$set=this;
                var that$278;
                if(isOfType((that$278=that$277),{t:Set,a:{Element:{t:Object$}}})&&that$278.getSize().equals($$set.getSize())){
                    var it$279 = $$set.getIterator();
                    var element$280;while ((element$280=it$279.next())!==getFinished()){
                        if((!that$278.contains(element$280))){
                            return false;
                        }
                    }
                    if (getFinished() === element$280){
                        return true;
                    }
                }
                return false;
            };$$set.getHash=function getHash(){
                var $$set=this;
                var hashCode$281=(1);
                var setHashCode$281=function(hashCode$282){return hashCode$281=hashCode$282;};
                var it$283 = $$set.getIterator();
                var elem$284;while ((elem$284=it$283.next())!==getFinished()){
                    (hashCode$281=hashCode$281.times((31)));
                    (hashCode$281=hashCode$281.plus(elem$284.getHash()));
                }
                return hashCode$281;
            };
        })(Set.$$.prototype);
    }
    return Set;
}
exports.$init$Set=$init$Set;
$init$Set();
function AssertionException(message$285, $$assertionException){
    $init$AssertionException();
    if ($$assertionException===undefined)$$assertionException=new AssertionException.$$;
    Exception(message$285,undefined,$$assertionException);
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
function Range(first$286, last$287, $$targs$$,$$range){
    $init$Range();
    if ($$range===undefined)$$range=new Range.$$;
    set_type_args($$range,$$targs$$);
    Object$($$range);
    Sequence($$range);
    Category($$range);
    $$range.first$288=first$286;
    $$range.last$289=last$287;
    return $$range;
}
exports.Range=Range;
function $init$Range(){
    if (Range.$$===undefined){
        initTypeProto(Range,'ceylon.language::Range',Object$,$init$Sequence(),$init$Category());
        (function($$range){
            $$range.getFirst=function getFirst(){
                return this.first$288;
            };
            $$range.getLast=function getLast(){
                return this.last$289;
            };
            $$range.getString=function getString(){
                var $$range=this;
                return $$range.getFirst().getString().plus(String$("..",2)).plus($$range.getLast().getString());
            };
            $$range.getDecreasing=function getDecreasing(){
                var $$range=this;
                return $$range.getLast().compare($$range.getFirst()).equals(getSmaller());
            };
            $$range.next$290=function (x$291){
                var $$range=this;
                return (opt$292=($$range.getDecreasing()?x$291.getPredecessor():null),opt$292!==null?opt$292:x$291.getSuccessor());
            };
            $$range.getSize=function getSize(){
                var $$range=this;
                var last$293;
                var first$294;
                if(isOfType((last$293=$$range.getLast()),{t:Enumerable,a:{Other:{t:Anything}}})&&isOfType((first$294=$$range.getFirst()),{t:Enumerable,a:{Other:{t:Anything}}})){
                    return last$293.getIntegerValue().minus(first$294.getIntegerValue()).getMagnitude().plus((1));
                }else {
                    var size$295=(1);
                    var setSize$295=function(size$296){return size$295=size$296;};
                    var current$297=$$range.getFirst();
                    var setCurrent$297=function(current$298){return current$297=current$298;};
                    while((!current$297.equals($$range.getLast()))){
                        (oldsize$299=size$295,size$295=oldsize$299.getSuccessor(),oldsize$299);
                        var oldsize$299;
                        current$297=$$range.next$290(current$297);
                    }
                    return size$295;
                }
            };$$range.getLastIndex=function getLastIndex(){
                var $$range=this;
                return $$range.getSize().minus((1));
            };
            $$range.getRest=function getRest(){
                var $$range=this;
                if($$range.getSize().equals((1))){
                    return getEmpty();
                }
                var n$300=$$range.next$290($$range.getFirst());
                return (opt$301=(n$300.equals($$range.getLast())?getEmpty():null),opt$301!==null?opt$301:Range(n$300,$$range.getLast(),{Element:$$range.$$targs$$.Element}));
                var opt$301;
            };$$range.get=function get(n$302){
                var $$range=this;
                var index$303=(0);
                var setIndex$303=function(index$304){return index$303=index$304;};
                var x$305=$$range.getFirst();
                var setX$305=function(x$306){return x$305=x$306;};
                while(index$303.compare(n$302).equals(getSmaller())){
                    if(x$305.equals($$range.getLast())){
                        return null;
                    }else {
                        (index$303=index$303.getSuccessor());
                        x$305=$$range.next$290(x$305);
                    }
                }
                return x$305;
            };$$range.getIterator=function getIterator(){
                var $$range=this;
                function RangeIterator$307($$rangeIterator$307){
                    $init$RangeIterator$307();
                    if ($$rangeIterator$307===undefined)$$rangeIterator$307=new RangeIterator$307.$$;
                    $$rangeIterator$307.$$targs$$={Element:$$range.$$targs$$.Element};
                    Iterator($$rangeIterator$307);
                    $$rangeIterator$307.current$308=$$range.getFirst();
                    return $$rangeIterator$307;
                }
                function $init$RangeIterator$307(){
                    if (RangeIterator$307.$$===undefined){
                        initTypeProto(RangeIterator$307,'ceylon.language::Range.iterator.RangeIterator',Basic,$init$Iterator());
                        (function($$rangeIterator$307){
                            $$rangeIterator$307.getCurrent$308=function getCurrent$308(){
                                return this.current$308;
                            };
                            $$rangeIterator$307.setCurrent$308=function setCurrent$308(current$309){
                                return this.current$308=current$309;
                            };
                            $$rangeIterator$307.next=function next(){
                                var $$rangeIterator$307=this;
                                var result$310=$$rangeIterator$307.getCurrent$308();
                                var curr$311;
                                if(!isOfType((curr$311=$$rangeIterator$307.getCurrent$308()),{t:Finished})){
                                    if((opt$312=($$range.getDecreasing()?(curr$311.compare($$range.getLast())!==getLarger()):null),opt$312!==null?opt$312:(curr$311.compare($$range.getLast())!==getSmaller()))){
                                        $$rangeIterator$307.setCurrent$308(getFinished());
                                    }else {
                                        $$rangeIterator$307.setCurrent$308($$range.next$290(curr$311));
                                    }
                                    var opt$312;
                                }
                                return result$310;
                            };$$rangeIterator$307.getString=function getString(){
                                var $$rangeIterator$307=this;
                                return String$("RangeIterator",13);
                            };
                        })(RangeIterator$307.$$.prototype);
                    }
                    return RangeIterator$307;
                }
                $init$RangeIterator$307();
                return RangeIterator$307();
            };$$range.by=function by(step$313){
                var $$range=this;
                //assert at Range.ceylon (112:8-113:25)
                if (!(step$313.compare((0)).equals(getLarger()))) { throw AssertionException('step size must be greater than zero: \'step > 0\' at Range.ceylon (113:15-113:24)'); }
                if(step$313.equals((1))){
                    return $$range;
                }
                var first$314;
                var last$315;
                if(isOfType((first$314=$$range.getFirst()),{t:Integer})&&isOfType((last$315=$$range.getLast()),{t:Integer})){
                    return integerRangeByIterable($$range,step$313,{Element:$$range.$$targs$$.Element});
                }
                return $$range.getT$all()['ceylon.language::Iterable'].$$.prototype.by.call(this,step$313);
            };$$range.contains=function contains(element$316){
                var $$range=this;
                var it$317 = $$range.getIterator();
                var e$318;while ((e$318=it$317.next())!==getFinished()){
                    if(e$318.equals(element$316)){
                        return true;
                    }
                }
                if (getFinished() === e$318){
                    return false;
                }
            };$$range.count=function count(selecting$319){
                var $$range=this;
                var e$320=$$range.getFirst();
                var setE$320=function(e$321){return e$320=e$321;};
                var c$322=(0);
                var setC$322=function(c$323){return c$322=c$323;};
                while($$range.includes(e$320)){
                    if(selecting$319(e$320)){
                        (oldc$324=c$322,c$322=oldc$324.getSuccessor(),oldc$324);
                        var oldc$324;
                    }
                    e$320=$$range.next$290(e$320);
                }
                return c$322;
            };$$range.includes=function (x$325){
                var $$range=this;
                return (opt$326=($$range.getDecreasing()?((x$325.compare($$range.getFirst())!==getLarger())&&(x$325.compare($$range.getLast())!==getSmaller())):null),opt$326!==null?opt$326:((x$325.compare($$range.getFirst())!==getSmaller())&&(x$325.compare($$range.getLast())!==getLarger())));
            };
            $$range.getClone=function getClone(){
                var $$range=this;
                return $$range;
            };
            $$range.segment=function segment(from$327,length$328){
                var $$range=this;
                if(((length$328.compare((0))!==getLarger())||from$327.compare($$range.getLastIndex()).equals(getLarger()))){
                    return getEmpty();
                }
                var x$329=$$range.getFirst();
                var setX$329=function(x$330){return x$329=x$330;};
                var i$331=(0);
                var setI$331=function(i$332){return i$331=i$332;};
                while((oldi$333=i$331,i$331=oldi$333.getSuccessor(),oldi$333).compare(from$327).equals(getSmaller())){
                    x$329=$$range.next$290(x$329);
                }
                var oldi$333;
                var y$334=x$329;
                var setY$334=function(y$335){return y$334=y$335;};
                var j$336=(1);
                var setJ$336=function(j$337){return j$336=j$337;};
                while(((oldj$338=j$336,j$336=oldj$338.getSuccessor(),oldj$338).compare(length$328).equals(getSmaller())&&y$334.compare($$range.getLast()).equals(getSmaller()))){
                    y$334=$$range.next$290(y$334);
                }
                var oldj$338;
                return Range(x$329,y$334,{Element:$$range.$$targs$$.Element});
            };$$range.span=function span(from$339,to$340){
                var $$range=this;
                var toIndex$341=to$340;
                var setToIndex$341=function(toIndex$342){return toIndex$341=toIndex$342;};
                var fromIndex$343=from$339;
                var setFromIndex$343=function(fromIndex$344){return fromIndex$343=fromIndex$344;};
                if(toIndex$341.compare((0)).equals(getSmaller())){
                    if(fromIndex$343.compare((0)).equals(getSmaller())){
                        return getEmpty();
                    }
                    toIndex$341=(0);
                }else {
                    if(toIndex$341.compare($$range.getLastIndex()).equals(getLarger())){
                        if(fromIndex$343.compare($$range.getLastIndex()).equals(getLarger())){
                            return getEmpty();
                        }
                        toIndex$341=$$range.getLastIndex();
                    }
                }
                if(fromIndex$343.compare((0)).equals(getSmaller())){
                    fromIndex$343=(0);
                }else {
                    if(fromIndex$343.compare($$range.getLastIndex()).equals(getLarger())){
                        fromIndex$343=$$range.getLastIndex();
                    }
                }
                var x$345=$$range.getFirst();
                var setX$345=function(x$346){return x$345=x$346;};
                var i$347=(0);
                var setI$347=function(i$348){return i$347=i$348;};
                while((oldi$349=i$347,i$347=oldi$349.getSuccessor(),oldi$349).compare(fromIndex$343).equals(getSmaller())){
                    x$345=$$range.next$290(x$345);
                }
                var oldi$349;
                var y$350=$$range.getFirst();
                var setY$350=function(y$351){return y$350=y$351;};
                var j$352=(0);
                var setJ$352=function(j$353){return j$352=j$353;};
                while((oldj$354=j$352,j$352=oldj$354.getSuccessor(),oldj$354).compare(toIndex$341).equals(getSmaller())){
                    y$350=$$range.next$290(y$350);
                }
                var oldj$354;
                return Range(x$345,y$350,{Element:$$range.$$targs$$.Element});
            };$$range.spanTo=function spanTo(to$355){
                var $$range=this;
                return (opt$356=(to$355.compare((0)).equals(getSmaller())?getEmpty():null),opt$356!==null?opt$356:$$range.span((0),to$355));
                var opt$356;
            };$$range.spanFrom=function spanFrom(from$357){
                var $$range=this;
                return $$range.span(from$357,$$range.getSize());
            };$$range.getReversed=function getReversed(){
                var $$range=this;
                return Range($$range.getLast(),$$range.getFirst(),{Element:$$range.$$targs$$.Element});
            };
            $$range.skipping=function skipping(skip$358){
                var $$range=this;
                var x$359=(0);
                var setX$359=function(x$360){return x$359=x$360;};
                var e$361=$$range.getFirst();
                var setE$361=function(e$362){return e$361=e$362;};
                while((oldx$363=x$359,x$359=oldx$363.getSuccessor(),oldx$363).compare(skip$358).equals(getSmaller())){
                    e$361=$$range.next$290(e$361);
                }
                var oldx$363;
                return (opt$364=($$range.includes(e$361)?Range(e$361,$$range.getLast(),{Element:$$range.$$targs$$.Element}):null),opt$364!==null?opt$364:getEmpty());
                var opt$364;
            };$$range.taking=function taking(take$365){
                var $$range=this;
                if(take$365.equals((0))){
                    return getEmpty();
                }
                var x$366=(0);
                var setX$366=function(x$367){return x$366=x$367;};
                var e$368=$$range.getFirst();
                var setE$368=function(e$369){return e$368=e$369;};
                while((x$366=x$366.getSuccessor()).compare(take$365).equals(getSmaller())){
                    e$368=$$range.next$290(e$368);
                }
                return (opt$370=($$range.includes(e$368)?Range($$range.getFirst(),e$368,{Element:$$range.$$targs$$.Element}):null),opt$370!==null?opt$370:$$range);
                var opt$370;
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
var opt$292,opt$326;
function Singleton(element$371, $$targs$$,$$singleton){
    $init$Singleton();
    if ($$singleton===undefined)$$singleton=new Singleton.$$;
    set_type_args($$singleton,$$targs$$);
    $$singleton.element$371=element$371;
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
                return $$singleton.element$371;
            };
            $$singleton.getLast=function getLast(){
                var $$singleton=this;
                return $$singleton.element$371;
            };
            $$singleton.getRest=function getRest(){
                var $$singleton=this;
                return getEmpty();
            };
            $$singleton.get=function get(index$372){
                var $$singleton=this;
                if(index$372.equals((0))){
                    return $$singleton.element$371;
                }else {
                    return null;
                }
            };$$singleton.getClone=function getClone(){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.getIterator=function getIterator(){
                var $$singleton=this;
                function SingletonIterator$373($$singletonIterator$373){
                    $init$SingletonIterator$373();
                    if ($$singletonIterator$373===undefined)$$singletonIterator$373=new SingletonIterator$373.$$;
                    $$singletonIterator$373.$$targs$$={Element:$$singleton.$$targs$$.Element};
                    Iterator($$singletonIterator$373);
                    $$singletonIterator$373.done$374=false;
                    return $$singletonIterator$373;
                }
                function $init$SingletonIterator$373(){
                    if (SingletonIterator$373.$$===undefined){
                        initTypeProto(SingletonIterator$373,'ceylon.language::Singleton.iterator.SingletonIterator',Basic,$init$Iterator());
                        (function($$singletonIterator$373){
                            $$singletonIterator$373.getDone$374=function getDone$374(){
                                return this.done$374;
                            };
                            $$singletonIterator$373.setDone$374=function setDone$374(done$375){
                                return this.done$374=done$375;
                            };
                            $$singletonIterator$373.next=function next(){
                                var $$singletonIterator$373=this;
                                if($$singletonIterator$373.getDone$374()){
                                    return getFinished();
                                }else {
                                    $$singletonIterator$373.setDone$374(true);
                                    return $$singleton.element$371;
                                }
                            };$$singletonIterator$373.getString=function getString(){
                                var $$singletonIterator$373=this;
                                return String$("SingletonIterator",17);
                            };
                        })(SingletonIterator$373.$$.prototype);
                    }
                    return SingletonIterator$373;
                }
                $init$SingletonIterator$373();
                return SingletonIterator$373();
            };$$singleton.getString=function getString(){
                var $$singleton=this;
                return StringBuilder().appendAll([String$("[",1),$$singleton.element$371.getString().getString(),String$("]",1)]).getString();
            };
            $$singleton.segment=function (from$376,length$377){
                var $$singleton=this;
                return (opt$378=(((from$376.compare((0))!==getLarger())&&from$376.plus(length$377).compare((0)).equals(getLarger()))?$$singleton:null),opt$378!==null?opt$378:getEmpty());
            };
            $$singleton.span=function (from$379,to$380){
                var $$singleton=this;
                return (opt$381=((((from$379.compare((0))!==getLarger())&&(to$380.compare((0))!==getSmaller()))||((from$379.compare((0))!==getSmaller())&&(to$380.compare((0))!==getLarger())))?$$singleton:null),opt$381!==null?opt$381:getEmpty());
            };
            $$singleton.spanTo=function (to$382){
                var $$singleton=this;
                return (opt$383=(to$382.compare((0)).equals(getSmaller())?getEmpty():null),opt$383!==null?opt$383:$$singleton);
            };
            $$singleton.spanFrom=function (from$384){
                var $$singleton=this;
                return (opt$385=(from$384.compare((0)).equals(getLarger())?getEmpty():null),opt$385!==null?opt$385:$$singleton);
            };
            $$singleton.getReversed=function getReversed(){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.equals=function equals(that$386){
                var $$singleton=this;
                var that$387;
                if(isOfType((that$387=that$386),{t:List,a:{Element:{t:Anything}}})){
                    if(that$387.getSize().equals((1))){
                        var elem$388;
                        if((elem$388=that$387.getFirst())!==null){
                            return elem$388.equals($$singleton.element$371);
                        }
                    }
                }
                return false;
            };$$singleton.getHash=function getHash(){
                var $$singleton=this;
                return (31).plus($$singleton.element$371.getHash());
            };
            $$singleton.contains=function (element$389){
                var $$singleton=this;
                return $$singleton.element$371.equals(element$389);
            };
            $$singleton.count=function (selecting$390){
                var $$singleton=this;
                return (opt$391=(selecting$390($$singleton.element$371)?(1):null),opt$391!==null?opt$391:(0));
            };
            $$singleton.$map=function (selecting$392,$$$mptypes){
                var $$singleton=this;
                return Tuple(selecting$392($$singleton.element$371),getEmpty(),{Rest:{t:Empty},First:$$$mptypes.Result,Element:$$$mptypes.Result});
            };
            $$singleton.$filter=function (selecting$393){
                var $$singleton=this;
                return (opt$394=(selecting$393($$singleton.element$371)?$$singleton:null),opt$394!==null?opt$394:getEmpty());
            };
            $$singleton.fold=function (initial$395,accumulating$396,$$$mptypes){
                var $$singleton=this;
                return accumulating$396(initial$395,$$singleton.element$371);
            };
            $$singleton.find=function (selecting$397){
                var $$singleton=this;
                return (selecting$397($$singleton.element$371)?$$singleton.element$371:null);
            };
            $$singleton.findLast=function (selecting$398){
                var $$singleton=this;
                return $$singleton.find(selecting$398);
            };
            $$singleton.$sort=function (comparing$399){
                var $$singleton=this;
                return $$singleton;
            };
            $$singleton.any=function (selecting$400){
                var $$singleton=this;
                return selecting$400($$singleton.element$371);
            };
            $$singleton.$every=function (selecting$401){
                var $$singleton=this;
                return selecting$401($$singleton.element$371);
            };
            $$singleton.skipping=function (skip$402){
                var $$singleton=this;
                return (opt$403=(skip$402.compare((1)).equals(getSmaller())?$$singleton:null),opt$403!==null?opt$403:getEmpty());
            };
            $$singleton.taking=function (take$404){
                var $$singleton=this;
                return (opt$405=(take$404.compare((0)).equals(getLarger())?$$singleton:null),opt$405!==null?opt$405:getEmpty());
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
var opt$378,opt$381,opt$383,opt$385,opt$391,opt$394,opt$403,opt$405;
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
            $$map.equals=function equals(that$406){
                var $$map=this;
                var that$407;
                if(isOfType((that$407=that$406),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})&&that$407.getSize().equals($$map.getSize())){
                    var it$408 = $$map.getIterator();
                    var entry$409;while ((entry$409=it$408.next())!==getFinished()){
                        var item$410;
                        if((item$410=that$407.get(entry$409.getKey()))!==null&&item$410.equals(entry$409.getItem())){
                            continue;
                        }else {
                            return false;
                        }
                    }
                    if (getFinished() === entry$409){
                        return true;
                    }
                }else {
                    return false;
                }
            };$$map.getHash=function getHash(){
                var $$map=this;
                var hashCode$411=(1);
                var setHashCode$411=function(hashCode$412){return hashCode$411=hashCode$412;};
                var it$413 = $$map.getIterator();
                var elem$414;while ((elem$414=it$413.next())!==getFinished()){
                    (hashCode$411=hashCode$411.times((31)));
                    (hashCode$411=hashCode$411.plus(elem$414.getHash()));
                }
                return hashCode$411;
            };$$map.getKeys=function getKeys(){
                var $$map=this;
                return LazySet(Comprehension(function(){
                    var it$415=$$map.getIterator();
                    var k$416,v$417;
                    var next$v$417=function(){
                        var entry$418;
                        if((entry$418=it$415.next())!==getFinished()){
                            k$416=entry$418.getKey();
                            v$417=entry$418.getItem();
                            return entry$418;
                        }
                        v$417=undefined;
                        return getFinished();
                    }
                    next$v$417();
                    return function(){
                        if(v$417!==undefined){
                            var k$416$419=k$416;
                            function getK$416(){return k$416$419;}
                            var v$417$420=v$417;
                            function getV$417(){return v$417$420;}
                            var tmpvar$421=getK$416();
                            next$v$417();
                            return tmpvar$421;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key});
            };
            $$map.getValues=function getValues(){
                var $$map=this;
                return LazyList(Comprehension(function(){
                    var it$422=$$map.getIterator();
                    var k$423,v$424;
                    var next$v$424=function(){
                        var entry$425;
                        if((entry$425=it$422.next())!==getFinished()){
                            k$423=entry$425.getKey();
                            v$424=entry$425.getItem();
                            return entry$425;
                        }
                        v$424=undefined;
                        return getFinished();
                    }
                    next$v$424();
                    return function(){
                        if(v$424!==undefined){
                            var k$423$426=k$423;
                            function getK$423(){return k$423$426;}
                            var v$424$427=v$424;
                            function getV$424(){return v$424$427;}
                            var tmpvar$428=getV$424();
                            next$v$424();
                            return tmpvar$428;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$map.$$targs$$.Item}),{Element:$$map.$$targs$$.Item});
            };
            $$map.getInverse=function getInverse(){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$429=$$map.getIterator();
                    var key$430,item$431;
                    var next$item$431=function(){
                        var entry$432;
                        if((entry$432=it$429.next())!==getFinished()){
                            key$430=entry$432.getKey();
                            item$431=entry$432.getItem();
                            return entry$432;
                        }
                        item$431=undefined;
                        return getFinished();
                    }
                    next$item$431();
                    return function(){
                        if(item$431!==undefined){
                            var key$430$433=key$430;
                            function getKey$430(){return key$430$433;}
                            var item$431$434=item$431;
                            function getItem$431(){return item$431$434;}
                            var tmpvar$435=Entry(getItem$431(),LazySet(Comprehension(function(){
                                var it$436=$$map.getIterator();
                                var k$437,i$438;
                                var next$i$438=function(){
                                    var entry$439;
                                    while((entry$439=it$436.next())!==getFinished()){
                                        k$437=entry$439.getKey();
                                        i$438=entry$439.getItem();
                                        if(i$438.equals(getItem$431())){
                                            return entry$439;
                                        }
                                    }
                                    i$438=undefined;
                                    return getFinished();
                                }
                                next$i$438();
                                return function(){
                                    if(i$438!==undefined){
                                        var k$437$440=k$437;
                                        function getK$437(){return k$437$440;}
                                        var i$438$441=i$438;
                                        function getI$438(){return i$438$441;}
                                        var tmpvar$442=getK$437();
                                        next$i$438();
                                        return tmpvar$442;
                                    }
                                    return getFinished();
                                }
                            },{Absent:{t:Anything},Element:$$map.$$targs$$.Key}),{Element:$$map.$$targs$$.Key}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
                            next$item$431();
                            return tmpvar$435;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{t:Entry,a:{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}}}}),{Key:$$map.$$targs$$.Item,Item:{t:LazySet,a:{Element:$$map.$$targs$$.Key}}});
            };
            $$map.mapItems=function (mapping$443,$$$mptypes){
                var $$map=this;
                return LazyMap(Comprehension(function(){
                    var it$444=$$map.getIterator();
                    var key$445,item$446;
                    var next$item$446=function(){
                        var entry$447;
                        if((entry$447=it$444.next())!==getFinished()){
                            key$445=entry$447.getKey();
                            item$446=entry$447.getItem();
                            return entry$447;
                        }
                        item$446=undefined;
                        return getFinished();
                    }
                    next$item$446();
                    return function(){
                        if(item$446!==undefined){
                            var key$445$448=key$445;
                            function getKey$445(){return key$445$448;}
                            var item$446$449=item$446;
                            function getItem$446(){return item$446$449;}
                            var tmpvar$450=Entry(getKey$445(),mapping$443(getKey$445(),getItem$446()),{Key:$$map.$$targs$$.Key,Item:$$$mptypes.Result});
                            next$item$446();
                            return tmpvar$450;
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
function LazyMap(entries$451, $$targs$$,$$lazyMap){
    $init$LazyMap();
    if ($$lazyMap===undefined)$$lazyMap=new LazyMap.$$;
    set_type_args($$lazyMap,$$targs$$);
    $$lazyMap.entries$451=entries$451;
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
                return $$lazyMap.entries$451.getFirst();
            };
            $$lazyMap.getLast=function getLast(){
                var $$lazyMap=this;
                return $$lazyMap.entries$451.getLast();
            };
            $$lazyMap.getClone=function getClone(){
                var $$lazyMap=this;
                return $$lazyMap;
            };
            $$lazyMap.getSize=function getSize(){
                var $$lazyMap=this;
                return $$lazyMap.entries$451.getSize();
            };
            $$lazyMap.get=function (key$452){
                var $$lazyMap=this;
                return (opt$453=$$lazyMap.entries$451.find(function (e$454){
                    var $$lazyMap=this;
                    return e$454.getKey().equals(key$452);
                }),opt$453!==null?opt$453.getItem():null);
            };
            $$lazyMap.getIterator=function getIterator(){
                var $$lazyMap=this;
                return $$lazyMap.entries$451.getIterator();
            };
            $$lazyMap.equals=function equals(that$455){
                var $$lazyMap=this;
                var that$456;
                if(isOfType((that$456=that$455),{t:Map,a:{Key:{t:Object$},Item:{t:Object$}}})){
                    if(that$456.getSize().equals($$lazyMap.getSize())){
                        var it$457 = $$lazyMap.getIterator();
                        var entry$458;while ((entry$458=it$457.next())!==getFinished()){
                            var item$459;
                            if((item$459=that$456.get(entry$458.getKey()))!==null){
                                if(item$459.equals(entry$458.getItem())){
                                    continue;
                                }
                            }
                            return false;
                        }
                        if (getFinished() === entry$458){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyMap.getHash=function getHash(){
                var $$lazyMap=this;
                var hashCode$460=(1);
                var setHashCode$460=function(hashCode$461){return hashCode$460=hashCode$461;};
                var it$462 = $$lazyMap.entries$451.getIterator();
                var elem$463;while ((elem$463=it$462.next())!==getFinished()){
                    (hashCode$460=hashCode$460.times((31)));
                    (hashCode$460=hashCode$460.plus(elem$463.getHash()));
                }
                return hashCode$460;
            };
        })(LazyMap.$$.prototype);
    }
    return LazyMap;
}
exports.$init$LazyMap=$init$LazyMap;
$init$LazyMap();
var opt$453;
function LazySet(elems$464, $$targs$$,$$lazySet){
    $init$LazySet();
    if ($$lazySet===undefined)$$lazySet=new LazySet.$$;
    set_type_args($$lazySet,$$targs$$);
    $$lazySet.elems$464=elems$464;
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
                var c$465=(0);
                var setC$465=function(c$466){return c$465=c$466;};
                var sorted$467=$$lazySet.elems$464.$sort(byIncreasing(function (e$468){
                    var $$lazySet=this;
                    return e$468.getHash();
                },{Value:{t:Integer},Element:$$lazySet.$$targs$$.Element}));
                var l$469;
                if((l$469=sorted$467.getFirst())!==null){
                    c$465=(1);
                    var last$470=l$469;
                    var setLast$470=function(last$471){return last$470=last$471;};
                    var it$472 = sorted$467.getRest().getIterator();
                    var e$473;while ((e$473=it$472.next())!==getFinished()){
                        if((!e$473.equals(last$470))){
                            (oldc$474=c$465,c$465=oldc$474.getSuccessor(),oldc$474);
                            var oldc$474;
                            last$470=e$473;
                        }
                    }
                }
                return c$465;
            };$$lazySet.getIterator=function getIterator(){
                var $$lazySet=this;
                return $$lazySet.elems$464.getIterator();
            };
            $$lazySet.union=function (set$475,$$$mptypes){
                var $$lazySet=this;
                return LazySet($$lazySet.elems$464.chain(set$475,{Other:$$$mptypes.Other}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };
            $$lazySet.intersection=function (set$476,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var e$479;
                    var it$477=set$476.getIterator();
                    var e$478=getFinished();
                    var e$479;
                    var next$e$478=function(){
                        while((e$478=it$477.next())!==getFinished()){
                            if(isOfType((e$479=e$478),$$lazySet.$$targs$$.Element)&&$$lazySet.contains(e$479)){
                                return e$478;
                            }
                        }
                        return getFinished();
                    }
                    next$e$478();
                    return function(){
                        if(e$478!==getFinished()){
                            var e$478$480=e$478;
                            function getE$478(){return e$478$480;}
                            var tmpvar$481=e$479;
                            next$e$478();
                            return tmpvar$481;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}}),{Element:{ t:'i', l:[$$$mptypes.Other,$$lazySet.$$targs$$.Element]}});
            };
            $$lazySet.exclusiveUnion=function exclusiveUnion(other$482,$$$mptypes){
                var $$lazySet=this;
                var hereNotThere$483=Comprehension(function(){
                    var it$484=$$lazySet.elems$464.getIterator();
                    var e$485=getFinished();
                    var next$e$485=function(){
                        while((e$485=it$484.next())!==getFinished()){
                            if((!other$482.contains(e$485))){
                                return e$485;
                            }
                        }
                        return getFinished();
                    }
                    next$e$485();
                    return function(){
                        if(e$485!==getFinished()){
                            var e$485$486=e$485;
                            function getE$485(){return e$485$486;}
                            var tmpvar$487=getE$485();
                            next$e$485();
                            return tmpvar$487;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$lazySet.$$targs$$.Element});
                var thereNotHere$488=Comprehension(function(){
                    var it$489=other$482.getIterator();
                    var e$490=getFinished();
                    var next$e$490=function(){
                        while((e$490=it$489.next())!==getFinished()){
                            if((!$$lazySet.contains(e$490))){
                                return e$490;
                            }
                        }
                        return getFinished();
                    }
                    next$e$490();
                    return function(){
                        if(e$490!==getFinished()){
                            var e$490$491=e$490;
                            function getE$490(){return e$490$491;}
                            var tmpvar$492=getE$490();
                            next$e$490();
                            return tmpvar$492;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$$mptypes.Other});
                return LazySet(hereNotThere$483.chain(thereNotHere$488,{Other:$$$mptypes.Other}),{Element:{ t:'u', l:[$$lazySet.$$targs$$.Element,$$$mptypes.Other]}});
            };$$lazySet.complement=function (set$493,$$$mptypes){
                var $$lazySet=this;
                return LazySet(Comprehension(function(){
                    var it$494=$$lazySet.getIterator();
                    var e$495=getFinished();
                    var next$e$495=function(){
                        while((e$495=it$494.next())!==getFinished()){
                            if((!set$493.contains(e$495))){
                                return e$495;
                            }
                        }
                        return getFinished();
                    }
                    next$e$495();
                    return function(){
                        if(e$495!==getFinished()){
                            var e$495$496=e$495;
                            function getE$495(){return e$495$496;}
                            var tmpvar$497=getE$495();
                            next$e$495();
                            return tmpvar$497;
                        }
                        return getFinished();
                    }
                },{Absent:{t:Anything},Element:$$lazySet.$$targs$$.Element}),{Element:$$lazySet.$$targs$$.Element});
            };
            $$lazySet.equals=function equals(that$498){
                var $$lazySet=this;
                var that$499;
                if(isOfType((that$499=that$498),{t:Set,a:{Element:{t:Object$}}})){
                    if(that$499.getSize().equals($$lazySet.getSize())){
                        var it$500 = $$lazySet.elems$464.getIterator();
                        var element$501;while ((element$501=it$500.next())!==getFinished()){
                            if((!that$499.contains(element$501))){
                                return false;
                            }
                        }
                        if (getFinished() === element$501){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazySet.getHash=function getHash(){
                var $$lazySet=this;
                var hashCode$502=(1);
                var setHashCode$502=function(hashCode$503){return hashCode$502=hashCode$503;};
                var it$504 = $$lazySet.elems$464.getIterator();
                var elem$505;while ((elem$505=it$504.next())!==getFinished()){
                    (hashCode$502=hashCode$502.times((31)));
                    (hashCode$502=hashCode$502.plus(elem$505.getHash()));
                }
                return hashCode$502;
            };
        })(LazySet.$$.prototype);
    }
    return LazySet;
}
exports.$init$LazySet=$init$LazySet;
$init$LazySet();
function LazyList(elems$506, $$targs$$,$$lazyList){
    $init$LazyList();
    if ($$lazyList===undefined)$$lazyList=new LazyList.$$;
    set_type_args($$lazyList,$$targs$$);
    $$lazyList.elems$506=elems$506;
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
                var size$507=$$lazyList.elems$506.getSize();
                return (opt$508=(size$507.compare((0)).equals(getLarger())?size$507.minus((1)):null),opt$508!==null?opt$508:null);
                var opt$508;
            };$$lazyList.get=function get(index$509){
                var $$lazyList=this;
                if(index$509.equals((0))){
                    return $$lazyList.elems$506.getFirst();
                }else {
                    return $$lazyList.elems$506.skipping(index$509).getFirst();
                }
            };$$lazyList.getIterator=function getIterator(){
                var $$lazyList=this;
                return $$lazyList.elems$506.getIterator();
            };
            $$lazyList.getReversed=function getReversed(){
                var $$lazyList=this;
                return $$lazyList.elems$506.getSequence().getReversed();
            };
            $$lazyList.getClone=function getClone(){
                var $$lazyList=this;
                return $$lazyList;
            };
            $$lazyList.span=function span(from$510,to$511){
                var $$lazyList=this;
                if((to$511.compare((0)).equals(getSmaller())&&from$510.compare((0)).equals(getSmaller()))){
                    return getEmpty();
                }
                var toIndex$512=largest(to$511,(0),{Element:{t:Integer}});
                var fromIndex$513=largest(from$510,(0),{Element:{t:Integer}});
                if((toIndex$512.compare(fromIndex$513)!==getSmaller())){
                    var els$514=(opt$515=(fromIndex$513.compare((0)).equals(getLarger())?$$lazyList.elems$506.skipping(fromIndex$513):null),opt$515!==null?opt$515:$$lazyList.elems$506);
                    var opt$515;
                    return LazyList(els$514.taking(toIndex$512.minus(fromIndex$513).plus((1))),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    var seq$516=(opt$517=(toIndex$512.compare((0)).equals(getLarger())?$$lazyList.elems$506.skipping(toIndex$512):null),opt$517!==null?opt$517:$$lazyList.elems$506);
                    var opt$517;
                    return seq$516.taking(fromIndex$513.minus(toIndex$512).plus((1))).getSequence().getReversed();
                }
            };$$lazyList.spanTo=function (to$518){
                var $$lazyList=this;
                return (opt$519=(to$518.compare((0)).equals(getSmaller())?getEmpty():null),opt$519!==null?opt$519:LazyList($$lazyList.elems$506.taking(to$518.plus((1))),{Element:$$lazyList.$$targs$$.Element}));
            };
            $$lazyList.spanFrom=function (from$520){
                var $$lazyList=this;
                return (opt$521=(from$520.compare((0)).equals(getLarger())?LazyList($$lazyList.elems$506.skipping(from$520),{Element:$$lazyList.$$targs$$.Element}):null),opt$521!==null?opt$521:$$lazyList);
            };
            $$lazyList.segment=function segment(from$522,length$523){
                var $$lazyList=this;
                if(length$523.compare((0)).equals(getLarger())){
                    var els$524=(opt$525=(from$522.compare((0)).equals(getLarger())?$$lazyList.elems$506.skipping(from$522):null),opt$525!==null?opt$525:$$lazyList.elems$506);
                    var opt$525;
                    return LazyList(els$524.taking(length$523),{Element:$$lazyList.$$targs$$.Element});
                }else {
                    return getEmpty();
                }
            };$$lazyList.equals=function equals(that$526){
                var $$lazyList=this;
                var that$527;
                if(isOfType((that$527=that$526),{t:List,a:{Element:{t:Anything}}})){
                    var size$528=$$lazyList.elems$506.getSize();
                    if(that$527.getSize().equals(size$528)){
                        var it$529 = Range((0),size$528.minus((1)),{Element:{t:Integer}}).getIterator();
                        var i$530;while ((i$530=it$529.next())!==getFinished()){
                            var x$531=$$lazyList.get(i$530);
                            var y$532=that$527.get(i$530);
                            var x$533;
                            if((x$533=x$531)!==null){
                                var y$534;
                                if((y$534=y$532)!==null){
                                    if((!x$533.equals(y$534))){
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                var y$535;
                                if((y$535=y$532)!==null){
                                    return false;
                                }
                            }
                        }
                        if (getFinished() === i$530){
                            return true;
                        }
                    }
                }
                return false;
            };$$lazyList.getHash=function getHash(){
                var $$lazyList=this;
                var hash$536=(1);
                var setHash$536=function(hash$537){return hash$536=hash$537;};
                var it$538 = $$lazyList.elems$506.getIterator();
                var elem$539;while ((elem$539=it$538.next())!==getFinished()){
                    (hash$536=hash$536.times((31)));
                    var elem$540;
                    if((elem$540=elem$539)!==null){
                        (hash$536=hash$536.plus(elem$540.getHash()));
                    }
                }
                return hash$536;
            };$$lazyList.findLast=function (selecting$541){
                var $$lazyList=this;
                return $$lazyList.elems$506.findLast(selecting$541);
            };
            $$lazyList.getFirst=function getFirst(){
                var $$lazyList=this;
                return $$lazyList.elems$506.getFirst();
            };
            $$lazyList.getLast=function getLast(){
                var $$lazyList=this;
                return $$lazyList.elems$506.getLast();
            };
        })(LazyList.$$.prototype);
    }
    return LazyList;
}
exports.$init$LazyList=$init$LazyList;
$init$LazyList();
var opt$519,opt$521;
var byIncreasing=function (comparable$542,$$$mptypes){
    return function(x$543,y$544){{
        return comparable$542(x$543).compare(comparable$542(y$544));
    }
}
}
;
exports.byIncreasing=byIncreasing;
var equalTo=function (val$545,$$$mptypes){
    return function(element$546){{
        return element$546.equals(val$545);
    }
}
}
;
exports.equalTo=equalTo;
var curry=function (f$547,$$$mptypes){
    return function(first$548){{
        return flatten(function (args$549){
            return unflatten(f$547,{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return})(Tuple(first$548,args$549,{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}));
        },{Args:$$$mptypes.Rest,Return:$$$mptypes.Return});
    }
}
}
;
exports.curry=curry;
var uncurry=function (f$550,$$$mptypes){
return flatten(function (args$551){
    return unflatten(f$550(args$551.getFirst()),{Args:$$$mptypes.Rest,Return:$$$mptypes.Return})(args$551.getRest());
},{Args:{t:Tuple,a:{Rest:$$$mptypes.Rest,First:$$$mptypes.First,Element:$$$mptypes.Argument}},Return:$$$mptypes.Return});
};
exports.uncurry=uncurry;
var coalesce=function (values$552,$$$mptypes){
    return values$552.getCoalesced();
};
exports.coalesce=coalesce;
var byKey=function (comparing$553,$$$mptypes){
    return function(x$554,y$555){{
        return comparing$553(x$554.getKey(),y$555.getKey());
    }
}
}
;
exports.byKey=byKey;
var byItem=function (comparing$556,$$$mptypes){
    return function(x$557,y$558){{
        return comparing$556(x$557.getItem(),y$558.getItem());
    }
}
}
;
exports.byItem=byItem;
var byDecreasing=function (comparable$559,$$$mptypes){
    return function(x$560,y$561){{
        return comparable$559(y$561).compare(comparable$559(x$560));
    }
}
}
;
exports.byDecreasing=byDecreasing;
function emptyOrSingleton(element$562,$$$mptypes){
    var element$563;
    if((element$563=element$562)!==null){
        return Singleton(element$563,{Element:$$$mptypes.Element});
    }else {
        return getEmpty();
    }
}
exports.emptyOrSingleton=emptyOrSingleton;
var entries=function (elements$564,$$$mptypes){
    return elements$564.getIndexed();
};
exports.entries=entries;
function any(values$565){
    var it$566 = values$565.getIterator();
    var val$567;while ((val$567=it$566.next())!==getFinished()){
        if(val$567){
            return true;
        }
    }
    return false;
}
exports.any=any;
function count(values$568){
    var count$569=(0);
    var setCount$569=function(count$570){return count$569=count$570;};
    var it$571 = values$568.getIterator();
    var val$572;while ((val$572=it$571.next())!==getFinished()){
        if(val$572){
            (oldcount$573=count$569,count$569=oldcount$573.getSuccessor(),oldcount$573);
            var oldcount$573;
        }
    }
    return count$569;
}
exports.count=count;
function every(values$574){
    var it$575 = values$574.getIterator();
    var val$576;while ((val$576=it$575.next())!==getFinished()){
        if((!val$576)){
            return false;
        }
    }
    return true;
}
exports.every=every;
var lessThan=function (val$577,$$$mptypes){
    return function(element$578){{
        return element$578.compare(val$577).equals(getSmaller());
    }
}
}
;
exports.lessThan=lessThan;
var first=function (values$579,$$$mptypes){
    return internalFirst(values$579,{Value:$$$mptypes.Value,Absent:$$$mptypes.Absent});
};
exports.first=first;
var forKey=function (resulting$580,$$$mptypes){
    return function(entry$581){{
        return resulting$580(entry$581.getKey());
    }
}
}
;
exports.forKey=forKey;
var forItem=function (resulting$582,$$$mptypes){
    return function(entry$583){{
        return resulting$582(entry$583.getItem());
    }
}
}
;
exports.forItem=forItem;
var smallest=function (x$584,y$585,$$$mptypes){
    return (opt$586=(x$584.compare(y$585).equals(getSmaller())?x$584:null),opt$586!==null?opt$586:y$585);
};
exports.smallest=smallest;
var opt$586;
function print(line$587){
    getProcess().writeLine((opt$588=(opt$589=line$587,opt$589!==null?opt$589.getString():null),opt$588!==null?opt$588:String$("«null»",6)));
    var opt$588,opt$589;
}
exports.print=print;
var getNothing=function(){
    throw Exception();
}
exports.getNothing=getNothing;
function sum(values$590,$$$mptypes){
    var sum$591=values$590.getFirst();
    var setSum$591=function(sum$592){return sum$591=sum$592;};
    var it$593 = values$590.getRest().getIterator();
    var val$594;while ((val$594=it$593.next())!==getFinished()){
        (sum$591=sum$591.plus(val$594));
    }
    return sum$591;
}
exports.sum=sum;
function max(values$595,$$$mptypes){
    var first$596=values$595.getFirst();
    var first$597;
    if((first$597=first$596)!==null){
        var max$598=first$597;
        var setMax$598=function(max$599){return max$598=max$599;};
        var it$600 = values$595.getRest().getIterator();
        var val$601;while ((val$601=it$600.next())!==getFinished()){
            if(val$601.compare(max$598).equals(getLarger())){
                max$598=val$601;
            }
        }
        return max$598;
    }else {
        return first$596;
    }
}
exports.max=max;
function min(values$602,$$$mptypes){
    var first$603=values$602.getFirst();
    var first$604;
    if((first$604=first$603)!==null){
        var min$605=first$604;
        var setMin$605=function(min$606){return min$605=min$606;};
        var it$607 = values$602.getRest().getIterator();
        var val$608;while ((val$608=it$607.next())!==getFinished()){
            if(val$608.compare(min$605).equals(getSmaller())){
                min$605=val$608;
            }
        }
        return min$605;
    }else {
        return first$603;
    }
}
exports.min=min;
function zip(keys$609,items$610,$$$mptypes){
    var iter$611=items$610.getIterator();
    return Comprehension(function(){
        var item$614;
        var it$612=keys$609.getIterator();
        var key$613=getFinished();
        var item$614;
        var next$key$613=function(){
            while((key$613=it$612.next())!==getFinished()){
                if(!isOfType((item$614=iter$611.next()),{t:Finished})){
                    return key$613;
                }
            }
            return getFinished();
        }
        next$key$613();
        return function(){
            if(key$613!==getFinished()){
                var key$613$615=key$613;
                function getKey$613(){return key$613$615;}
                var tmpvar$616=Entry(getKey$613(),item$614,{Key:$$$mptypes.Key,Item:$$$mptypes.Item});
                next$key$613();
                return tmpvar$616;
            }
            return getFinished();
        }
    },{Absent:{t:Anything},Element:{t:Entry,a:{Key:$$$mptypes.Key,Item:$$$mptypes.Item}}}).getSequence();
}
exports.zip=zip;
function product(values$617,$$$mptypes){
    var product$618=values$617.getFirst();
    var setProduct$618=function(product$619){return product$618=product$619;};
    var it$620 = values$617.getRest().getIterator();
    var val$621;while ((val$621=it$620.next())!==getFinished()){
        (product$618=product$618.times(val$621));
    }
    return product$618;
}
exports.product=product;
var greaterThan=function (val$622,$$$mptypes){
    return function(element$623){{
        return element$623.compare(val$622).equals(getLarger());
    }
}
}
;
exports.greaterThan=greaterThan;
var largest=function (x$624,y$625,$$$mptypes){
    return (opt$626=(x$624.compare(y$625).equals(getLarger())?x$624:null),opt$626!==null?opt$626:y$625);
};
exports.largest=largest;
var opt$626;
var join=function (iterables$627,$$$mptypes){
    if(iterables$627===undefined){iterables$627=getEmpty();}
    return Comprehension(function(){
        var it$628=iterables$627.getIterator();
        var it$629=getFinished();
        var next$it$629=function(){
            if((it$629=it$628.next())!==getFinished()){
                it$630=it$629.getIterator();
                next$val$631();
                return it$629;
            }
            return getFinished();
        }
        var it$630;
        var val$631=getFinished();
        var next$val$631=function(){return val$631=it$630.next();}
        next$it$629();
        return function(){
            do{
                if(val$631!==getFinished()){
                    var val$631$632=val$631;
                    function getVal$631(){return val$631$632;}
                    var tmpvar$633=getVal$631();
                    next$val$631();
                    return tmpvar$633;
                }
            }while(next$it$629()!==getFinished());
            return getFinished();
        }
    },{Absent:{t:Anything},Element:$$$mptypes.Element}).getSequence();
};
exports.join=join;
function combine(combination$634,elements$635,otherElements$636,$$$mptypes){
    function CombineIterable$637($$combineIterable$637){
        $init$CombineIterable$637();
        if ($$combineIterable$637===undefined)$$combineIterable$637=new CombineIterable$637.$$;
        $$combineIterable$637.$$targs$$={Absent:$$$mptypes.Absent,Element:$$$mptypes.Result};
        Iterable($$combineIterable$637);
        return $$combineIterable$637;
    }
    function $init$CombineIterable$637(){
        if (CombineIterable$637.$$===undefined){
            initTypeProto(CombineIterable$637,'ceylon.language::combine.CombineIterable',Basic,$init$Iterable());
            (function($$combineIterable$637){
                $$combineIterable$637.getIterator=function getIterator(){
                    var $$combineIterable$637=this;
                    function CombineIterator$638($$combineIterator$638){
                        $init$CombineIterator$638();
                        if ($$combineIterator$638===undefined)$$combineIterator$638=new CombineIterator$638.$$;
                        $$combineIterator$638.$$targs$$={Element:$$$mptypes.Result};
                        Iterator($$combineIterator$638);
                        $$combineIterator$638.iter$639=elements$635.getIterator();
                        $$combineIterator$638.otherIter$640=otherElements$636.getIterator();
                        return $$combineIterator$638;
                    }
                    function $init$CombineIterator$638(){
                        if (CombineIterator$638.$$===undefined){
                            initTypeProto(CombineIterator$638,'ceylon.language::combine.CombineIterable.iterator.CombineIterator',Basic,$init$Iterator());
                            (function($$combineIterator$638){
                                $$combineIterator$638.getIter$639=function getIter$639(){
                                    return this.iter$639;
                                };
                                $$combineIterator$638.getOtherIter$640=function getOtherIter$640(){
                                    return this.otherIter$640;
                                };
                                $$combineIterator$638.next=function next(){
                                    var $$combineIterator$638=this;
                                    var elem$641=$$combineIterator$638.getIter$639().next();
                                    var otherElem$642=$$combineIterator$638.getOtherIter$640().next();
                                    var elem$643;
                                    var otherElem$644;
                                    if(!isOfType((elem$643=elem$641),{t:Finished})&&!isOfType((otherElem$644=otherElem$642),{t:Finished})){
                                        return combination$634(elem$643,otherElem$644);
                                    }else {
                                        return getFinished();
                                    }
                                };
                            })(CombineIterator$638.$$.prototype);
                        }
                        return CombineIterator$638;
                    }
                    $init$CombineIterator$638();
                    return CombineIterator$638();
                };
            })(CombineIterable$637.$$.prototype);
        }
        return CombineIterable$637;
    }
    $init$CombineIterable$637();
    return CombineIterable$637();
}
exports.combine=combine;
var sort=function (elements$645,$$$mptypes){
    return internalSort(byIncreasing(function (e$646){
        return e$646;
    },{Value:$$$mptypes.Element,Element:$$$mptypes.Element}),elements$645,{Element:$$$mptypes.Element});
};
exports.sort=sort;
var shuffle=function (f$647,$$$mptypes){
    return flatten(function (secondArgs$648){
        return flatten(function (firstArgs$649){
            return unflatten(unflatten(f$647,{Args:$$$mptypes.FirstArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.SecondArgs,Return:$$$mptypes.Result}}})(firstArgs$649),{Args:$$$mptypes.SecondArgs,Return:$$$mptypes.Result})(secondArgs$648);
        },{Args:$$$mptypes.FirstArgs,Return:$$$mptypes.Result});
    },{Args:$$$mptypes.SecondArgs,Return:{t:Callable,a:{Arguments:$$$mptypes.FirstArgs,Return:$$$mptypes.Result}}});
};
exports.shuffle=shuffle;
function internalFirst(values$650,$$$mptypes){
    var first$651;
    var next$652;
    if(!isOfType((next$652=values$650.getIterator().next()),{t:Finished})){
        first$651=next$652;
    }else {
        first$651=null;
    }
    //assert at internalFirst.ceylon (10:4-10:34)
    var first$653;
    if (!(isOfType((first$653=first$651),{ t:'u', l:[$$$mptypes.Absent,$$$mptypes.Value]}))) { throw AssertionException('Assertion failed: \'is Absent|Value first\' at internalFirst.ceylon (10:11-10:33)'); }
    return first$653;
}
exports.internalFirst=internalFirst;
var plus=function (x$654,y$655,$$$mptypes){
    return x$654.plus(y$655);
};
exports.plus=plus;
var compose=function (x$656,y$657,$$$mptypes){
    return flatten(function (args$658){
        return x$656(unflatten(y$657,{Args:$$$mptypes.Args,Return:$$$mptypes.Y})(args$658));
    },{Args:$$$mptypes.Args,Return:$$$mptypes.X});
};
exports.compose=compose;
var identical=function (x$659,y$660){
    return (x$659===y$660);
};
exports.identical=identical;
var times=function (x$661,y$662,$$$mptypes){
    return x$661.times(y$662);
};
exports.times=times;
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
that.$float = true;
return that;
}
initTypeProto(Float, 'ceylon.language::Float', Object$, Scalar, Exponentiable);
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
Ranged, Summable, Cloneable);
var origStrToString = String.prototype.toString;
inheritProto(String$, Object$, Sequential, Comparable, Ranged, Summable,
Cloneable);
function SequenceString() {}
initType(SequenceString, "ceylon.language::SequenceString", String$, Sequence);
function EmptyString() {}
initType(EmptyString, "ceylon.language::EmptyString", String$, Empty);
var String$proto = String$.$$.prototype;
String$proto.$$targs$$={Element:{t:Character}, Absent:{t:Null}};
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
String$proto.equals = function(other) {
if (other.constructor===String) {
return other.valueOf()===this.valueOf();
} else if (isOfType(other, {t:Iterable, a:{Element:{t:Character}}})) {
if (other.getSize()===this.getSize()) {
for (var i=0;i<this.getSize();i++) {
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
return this.length>0 ? this:getEmpty();
}
String$proto.getFirst = function() { return this.getSize()>0?this.get(0):null; }
String$proto.getLast = function() { return this.getSize()>0?this.get(this.getSize().getPredecessor()):null; }
String$proto.getKeys = function() {
//TODO implement!!!
return this.getSize() > 0 ? Range(0, this.getSize().getPredecessor(), [{t:Integer}]) : getEmpty();
}
String$proto.join = function(strings) {
if (strings === undefined) {return String$("", 0)}
var it = strings.getIterator();
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
var it = sep.getIterator();
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
return ocs.length > 0 ? ocs : getEmpty();
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
String$proto.getCoalesced = function() { return this; }
function StringIterator(string) {
var that = new StringIterator.$$;
that.string = string;
that.index = 0;
return that;
}
initTypeProto(StringIterator, 'ceylon.language::StringIterator', $init$Basic(), Iterator);
var StringIterator$proto = StringIterator.$$.prototype;
StringIterator$proto.$$targs$$={Element:{t:Character}, Absent:{t:Null}};
StringIterator$proto.next = function() {
if (this.index >= this.string.length) { return getFinished(); }
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
initTypeProto(StringBuilder, 'ceylon.language::StringBuilder', $init$Basic());
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
function Comparison(name) {
var that = new Comparison.$$;
that.name = String$(name);
return that;
}
initTypeProto(Comparison, 'ceylon.language::Comparison', $init$Basic());
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
else if (types.l[i].t==='u') {
if (isOfTypes(null, types.l[i])) return true;
}
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
exports.set_type_args=set_type_args;
exports.add_type_arg=add_type_arg;
exports.exists=exists;
exports.nonempty=nonempty;
exports.isOfType=isOfType;
exports.className=className;
exports.identityHash=identityHash;
function string(/*Iterable<Character>*/chars) {
if (chars === undefined) return String$('',0);
var s = StringBuilder();
var iter = chars.getIterator();
var c; while ((c = iter.next()) !== getFinished()) {
s.appendCharacter(c);
}
return s.getString();
}
function internalSort(comp, elems, $$$mptypes) {
if (elems===undefined) {return getEmpty();}
var arr = [];
var it = elems.getIterator();
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
if (seq===undefined || seq.getSize() === 0) { return ff(); }
var a = [];
for (var i = 0; i < seq.getSize(); i++) {
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
var seq = iterable.getSequence();
return Tuple(seq.getFirst(), seq.getRest().getSequence(),
{First:seq.$$targs$$.Element, Element:seq.$$targs$$.Element, Rest:{t:Sequential, a:seq.$$targs$$}});
}
exports.toTuple=toTuple;
function integerRangeByIterable(range, step, $$$mptypes) {
return Comprehension(function(){
var a = range.getFirst();
var b = range.getLast();
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
Array$proto.getRest = function() {
return this.length<=1 ? getEmpty() : ArraySequence(this.slice(1),this.$$targs$$);
}
Array$proto.items = function(keys) {
if (keys === undefined) return getEmpty();
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
return ($$$index$$$ === $$$arr$$$.length) ? getFinished() : $$$arr$$$[$$$index$$$++];
}, this.$$targs$$);
}
exports.ArrayList=ArrayList;
exports.array=function(elems, $$$ptypes) {
var e=[];
if (!(elems === null || elems === undefined)) {
var iter=elems.getIterator();
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
SequenceBuilder$proto.getSequence = function() {
return (this.seq.length > 0) ? ArraySequence(this.seq,this.$$targs$$) : getEmpty();
}
SequenceBuilder$proto.append = function(e) { this.seq.push(e); }
SequenceBuilder$proto.appendAll = function(/*Iterable*/arr) {
if (arr === undefined) return;
var iter = arr.getIterator();
var e; while ((e = iter.next()) !== getFinished()) {
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
initTypeProto(languageClass, "ceylon.language::language", $init$Basic());
var lang$proto=languageClass.$$.prototype;
lang$proto.getVersion=function() {
return String$("0.5",3);
}
lang$proto.getMajorVersion=function() { return 0; }
lang$proto.getMinorVersion=function() { return 5; }
lang$proto.getReleaseVersion=function() { return 0; }
lang$proto.getVersionName=function() { return String$("Analytical Engine",11); }
lang$proto.getMajorVersionBinary=function() { return 4; }
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
exports.Object=Object$;
exports.Anything=Anything;
exports.Null=Null;
exports.Nothing=Nothing;
exports.Boolean=Boolean$;
exports.Comparison=Comparison;
exports.getNull=getNull;
exports.getTrue=getTrue;
exports.getFalse=getFalse;
});
}(typeof define==='function' && define.amd ?
define : function (factory) {
if (typeof exports!=='undefined') {
factory(require, exports, module);
} else {
throw "no module loader";
}
}));
