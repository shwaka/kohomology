(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[316],{6333:function(e,n,t){var o={"./FreeLoopSpace.kt":3632,"./SphereModel.kt":5125};function r(e){var n=a(e);return t(n)}function a(e){if(!t.o(o,e)){var n=new Error("Cannot find module '"+e+"'");throw n.code="MODULE_NOT_FOUND",n}return o[e]}r.keys=function(){return Object.keys(o)},r.resolve=a,e.exports=r,r.id=6333},2829:function(e,n,t){"use strict";t.d(n,{C:function(){return g}});var o=t(102),r=t(254),a=t(7294),i={},l=["href","linkTitle"];function s(e){var n=e.href,t=e.linkTitle,s=(0,o.Z)(e,l);return a.createElement("div",{className:i.MyCodeBlock},null!==n&&a.createElement("a",{href:n,target:"_blank",rel:"noreferrer"},null!==t?t:n),a.createElement(r.Z,s,s.children))}var p="error_c9xe";function c(e){var n=e.map((function(e){return function(e){if(""===e)return 1/0;var n=new RegExp("^ *"),t=e.match(n);if(null===t)throw new Error("This can't happen");return t[0].length}(e)})),t=Math.min.apply(Math,n);return e.map((function(e){return e.substring(t)}))}function u(e,n){return!0===n?new RegExp("// "+e):new RegExp("// "+e+" +"+n)}function m(e,n){if(void 0===n)return{text:e};var t=u("start",n),o=u("end",n);return function(e,n,t){var o=e.findIndex((function(e){return e.match(n)})),r=e.findIndex((function(e){return e.match(t)}));return-1===o||-1===r?null:{text:c(e.slice(o+1,r)).join("\n"),start:o+2,end:r}}(e.split("\n"),t,o)}var h=t(6333);function f(e){return e.startsWith("./")?e:"./"+e}var d=new Map(h.keys().map((function(e){return[f(e),h(e).default]})));function g(e){var n="https://github.com/shwaka/kohomology/blob/main/sample/src/main/kotlin/"+e.path,t=d.get(f(e.path));if(void 0===t)return a.createElement("div",null,"Invalid path: "+e.path);var o=m(t,e.restrict);if(null===o)return a.createElement("div",{className:p},"ERROR: ",a.createElement("code",null,e.restrict)," is not found in ",a.createElement("a",{href:n},n));var r=function(e){return"start"in e}(o)?n+"#L"+o.start+"-L"+o.end:n;return a.createElement("div",null,a.createElement(s,{className:"language-kotlin",href:r,linkTitle:e.path},o.text))}},9149:function(e,n,t){"use strict";t.r(n),t.d(n,{assets:function(){return u},contentTitle:function(){return p},default:function(){return f},frontMatter:function(){return s},metadata:function(){return c},toc:function(){return m}});var o=t(3117),r=t(102),a=(t(7294),t(3905)),i=t(2829),l=["components"],s={title:"Cohomology of free loop space",sidebar_position:4},p=void 0,c={unversionedId:"free-loop-space",id:"free-loop-space",title:"Cohomology of free loop space",description:"Here is an example script computing the cohomology of",source:"@site/docs/free-loop-space.mdx",sourceDirName:".",slug:"/free-loop-space",permalink:"/kohomology/docs/free-loop-space",editUrl:"https://github.com/shwaka/kohomology/edit/main/website/docs/free-loop-space.mdx",tags:[],version:"current",sidebarPosition:4,frontMatter:{title:"Cohomology of free loop space",sidebar_position:4},sidebar:"tutorialSidebar",previous:{title:"Cohomology of Sullivan algebras",permalink:"/kohomology/docs/sullivan-algebra"},next:{title:"Overview of classes",permalink:"/kohomology/docs/overview-of-classes"}},u={},m=[],h={toc:m};function f(e){var n=e.components,t=(0,r.Z)(e,l);return(0,a.kt)("wrapper",(0,o.Z)({},h,t,{components:n,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"Here is an example script computing the cohomology of\nthe free loop space of the even dimensional sphere."),(0,a.kt)("p",null,"By using a Sullivan model ",(0,a.kt)("inlineCode",{parentName:"p"},"sphere")," of the sphere,\nthe model of the free loop space is obtained by ",(0,a.kt)("inlineCode",{parentName:"p"},"FreeLoopSpace(sphere)"),".\nIt is just a special example of ",(0,a.kt)("inlineCode",{parentName:"p"},"FreeDGAlgebra"),",\nwe can apply any computation methods to it. (See the ",(0,a.kt)("a",{parentName:"p",href:"/kohomology/docs/sullivan-algebra"},"previous page")," for details.)"),(0,a.kt)(i.C,{path:"FreeLoopSpace.kt",restrict:!0,mdxType:"ImportKotlin"}))}f.isMDXComponent=!0},3632:function(e,n,t){"use strict";t.r(n),n.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.model.FreeLoopSpace\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational\n\nfun main() {\n    // start\n    // Define the Sullivan model of the 4-sphere.\n    val sphereDim = 4\n    val indeterminateList = listOf(\n        Indeterminate("x", sphereDim),\n        Indeterminate("y", sphereDim * 2 - 1)\n    )\n    val matrixSpace = SparseMatrixSpaceOverBigRational\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->\n        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2\n    }\n\n    // Define the Sullivan model of the free loop space.\n    val freeLoopSpace = FreeLoopSpace(sphere)\n    val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList\n\n    // Assert that d(sy) and -2*x*sx are the same.\n    freeLoopSpace.context.run {\n        println("dsy = ${d(sy)} = ${-2 * x * sx}")\n    }\n\n    // Compute cohomology of the free loop space.\n    for (degree in 0 until 25) {\n        val basis = freeLoopSpace.cohomology[degree].getBasis()\n        println("H^$degree(LS^$sphereDim) = Q$basis")\n    }\n    // end\n}\n'},5125:function(e,n,t){"use strict";t.r(n),n.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational\n\nfun main() {\n    // start def\n    val n = 2\n    // Declare an indeterminate (generator) for the free commutative graded algebra \u039b(x,y)\n    val indeterminateList = listOf(\n        Indeterminate("x", 2 * n),\n        Indeterminate("y", 4 * n - 1)\n    )\n    val matrixSpace = SparseMatrixSpaceOverBigRational\n    // Sullivan algebra can be defined by using the constructor of FreeDGAlgebra.\n    // The last argument is a function\n    // which receives list of generators and returns the list of the values of the differential.\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->\n        // zeroGVector is a special element that represents zero in any degree.\n        val dx = zeroGVector\n        // x.pow(2) represents x^2\n        val dy = x.pow(2)\n        listOf(dx, dy)\n    }\n    // end def\n\n    // start cohomology\n    for (degree in 0 until 10) {\n        val basis = sphere.cohomology[degree].getBasis()\n        println("H^$degree(S^${2 * n}) = Q$basis")\n    }\n    // end cohomology\n\n    // start context\n    val (x, y) = sphere.gAlgebra.generatorList\n\n    // You can\'t write DGA operations here.\n\n    sphere.context.run {\n        // You can write DGA operations in "context.run"\n        println("d(x * y) = ${d(x * y)}")\n        println(d(x).isZero())\n        println(x.cohomologyClass())\n        println(x.pow(2).cohomologyClass())\n    }\n    // end context\n}\n'}}]);