# Module kohomology
The module provides tools to compute cohomology of chain complexes, especially Sullivan algebras.

# Package com.github.shwaka.kohomology.dg.degree
Contains definitions of degrees.

Here we consider degrees in a [group](https://en.wikipedia.org/wiki/Group_%28mathematics%29).
The group is represented by the interface [DegreeGroup],
and their elements are represented by the interface [Degree].
For example, [IntDegree] is the usual Z-grading.
We also include definitions of [MultiDegree] and [SuperDegree].
