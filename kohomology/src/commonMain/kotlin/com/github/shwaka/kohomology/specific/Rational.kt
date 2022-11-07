package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.linalg.DecomposedSparseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace

public expect class Rational : Scalar
public expect object RationalField : Field<Rational>

public val DenseNumVectorSpaceOverRational: DenseNumVectorSpace<Rational> =
    DenseNumVectorSpace.from(RationalField)
public val DenseMatrixSpaceOverRational: DenseMatrixSpace<Rational> =
    DenseMatrixSpace.from(DenseNumVectorSpaceOverRational)

public val SparseNumVectorSpaceOverRational: SparseNumVectorSpace<Rational> =
    SparseNumVectorSpace.from(RationalField)
public val SparseMatrixSpaceOverRational: SparseMatrixSpace<Rational> =
    SparseMatrixSpace.from(SparseNumVectorSpaceOverRational)
public val DecomposedSparseMatrixSpaceOverRational: DecomposedSparseMatrixSpace<Rational> =
    DecomposedSparseMatrixSpace.from(SparseNumVectorSpaceOverRational)

// aliases for old names
@Deprecated("Use Rational", ReplaceWith("Rational"))
public typealias BigRational = Rational
@Deprecated("Use RationalField", ReplaceWith("RationalField"))
public typealias BigRationalField = RationalField
@Deprecated("Use DenseNumVectorSpaceOverRational", ReplaceWith("DenseNumVectorSpaceOverRational"))
public val DenseNumVectorSpaceOverBigRational: DenseNumVectorSpace<Rational> by ::DenseNumVectorSpaceOverRational
@Deprecated("Use DenseMatrixSpaceOverRational", ReplaceWith("DenseMatrixSpaceOverRational"))
public val DenseMatrixSpaceOverBigRational: DenseMatrixSpace<Rational> by ::DenseMatrixSpaceOverRational

@Deprecated("Use SparseNumVectorSpaceOverRational", ReplaceWith("SparseNumVectorSpaceOverRational"))
public val SparseNumVectorSpaceOverBigRational: SparseNumVectorSpace<Rational> by ::SparseNumVectorSpaceOverRational
@Deprecated("Use SparseMatrixSpaceOverRational", ReplaceWith("SparseMatrixSpaceOverRational"))
public val SparseMatrixSpaceOverBigRational: SparseMatrixSpace<Rational> by ::SparseMatrixSpaceOverRational
@Deprecated("Use DecomposedSparseMatrixSpaceOverRational", ReplaceWith("DecomposedSparseMatrixSpaceOverRational"))
public val DecomposedSparseMatrixSpaceOverBigRational: DecomposedSparseMatrixSpace<Rational> by ::DecomposedSparseMatrixSpaceOverRational
