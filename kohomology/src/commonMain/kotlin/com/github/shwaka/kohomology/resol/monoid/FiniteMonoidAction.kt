package com.github.shwaka.kohomology.resol.monoid

public class FiniteMonoidAction<EA : FiniteMonoidElement, E : FiniteMonoidElement>(
    public val source: FiniteMonoid<EA>,
    public val target: FiniteMonoid<E>,
    public val targetEnd: FiniteMonoidEnd<E>,
    public val actionMap: FiniteMonoidMap<EA, EndElement<E>>,
) {
    init {
        require(actionMap.target == targetEnd) {
            "actionMap.target and targetEnd are different. " +
                "They are ${actionMap.target} and $targetEnd"
        }
    }
}
