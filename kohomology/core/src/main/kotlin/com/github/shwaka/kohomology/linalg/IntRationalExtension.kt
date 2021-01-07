package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.IntRational

// Cannot use generics since IntRational.times without generics already exists
operator fun IntRational.times(other: NumericalDenseVector<IntRational>): NumericalDenseVector<IntRational> {
    return other * this
}
