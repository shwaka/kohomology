package com.github.shwaka.kohomology.depgraph

import java.io.File

fun writeDepGraph() {
    val viewer = DependencyViewer()
    val rootDir = File("./src/commonMain/")
    for (file in rootDir.walk()) {
        if (file.extension != "kt")
            continue
        viewer.parse(file)
    }
    val prefix = Package.fromString("com.github.shwaka.kohomology")
    val ignore = listOf(
        "com.github.h0tk3y.betterParse.combinators",
        "com.github.h0tk3y.betterParse.grammar",
        "com.github.h0tk3y.betterParse.lexer",
        "com.github.h0tk3y.betterParse.parser",
        "com.ionspin.kotlin.bignum.integer",
        "kotlin",
        "kotlin.math",
        "kotlin.jvm",
        "com.github.shwaka.kococo",
        "com.github.shwaka.parautil",
        "com.github.shwaka.kohomology.exception",
        "com.github.shwaka.kohomology.util",
        "com.github.shwaka.kohomology.util.list",
    ).map { Package.fromString(it) }.toSet()
    val groups = listOf(
        "com.github.shwaka.kohomology.free",
        "com.github.shwaka.kohomology.dg",
        "com.github.shwaka.kohomology.util",
    ).map { Package.fromString(it) }.toSet()
    // println(viewer.getDependency(prefix))
    val uml: String = viewer.toUml(prefix = prefix, ignore = ignore, groups = groups)
    // println(uml)
    val outputFile = File("../website/static/img/uml/depGraph.uml")
    outputFile.writeText(uml)
}
