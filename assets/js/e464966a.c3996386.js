(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[648],{6333:function(e,a,n){var t={"./FreeLoopSpace.kt":3632,"./PrintTex.kt":6229,"./SphereModel.kt":5125,"./TopPageExample.kt":7219};function r(e){var a=s(e);return n(a)}function s(e){if(!n.o(t,e)){var a=new Error("Cannot find module '"+e+"'");throw a.code="MODULE_NOT_FOUND",a}return t[e]}r.keys=function(){return Object.keys(t)},r.resolve=s,e.exports=r,r.id=6333},2829:function(e,a,n){"use strict";n.d(a,{C:function(){return k}});var t=n(102),r=n(254),s=n(7294),i={},o=["href","linkTitle"];function m(e){var a=e.href,n=e.linkTitle,m=(0,t.Z)(e,o);return s.createElement("div",{className:i.MyCodeBlock},null!==a&&s.createElement("a",{href:a,target:"_blank",rel:"noreferrer"},null!==n?n:a),s.createElement(r.Z,m,m.children))}var p="error_c9xe";function l(e){var a=e.map((function(e){return function(e){if(""===e)return 1/0;var a=new RegExp("^ *"),n=e.match(a);if(null===n)throw new Error("This can't happen");return n[0].length}(e)})),n=Math.min.apply(Math,a);return e.map((function(e){return e.substring(n)}))}function c(e,a){return!0===a?new RegExp("// "+e):new RegExp("// "+e+" +"+a)}function h(e,a){if(void 0===a)return{text:e};var n=c("start",a),t=c("end",a);return function(e,a,n){var t=e.findIndex((function(e){return e.match(a)})),r=e.findIndex((function(e){return e.match(n)}));return-1===t||-1===r?null:{text:l(e.slice(t+1,r)).join("\n"),start:t+2,end:r}}(e.split("\n"),n,t)}var b=n(6333);function d(e){return e.startsWith("./")?e:"./"+e}var u=new Map(b.keys().map((function(e){return[d(e),b(e).default]})));function k(e){var a="https://github.com/shwaka/kohomology/blob/main/sample/src/main/kotlin/"+e.path,n=u.get(d(e.path));if(void 0===n)return s.createElement("div",null,"Invalid path: "+e.path);var t=h(n,e.restrict);if(null===t)return s.createElement("div",{className:e.className},s.createElement("div",{className:p},"ERROR: ",s.createElement("code",null,e.restrict)," is not found in ",s.createElement("a",{href:a},a)));var r=function(e){return"start"in e}(t)?a+"#L"+t.start+"-L"+t.end:a;return s.createElement("div",{className:e.className},s.createElement(m,{className:"language-kotlin",href:r,linkTitle:e.path},t.text))}},7186:function(e,a,n){"use strict";n.r(a),n.d(a,{assets:function(){return h},contentTitle:function(){return l},default:function(){return u},frontMatter:function(){return p},metadata:function(){return c},toc:function(){return b}});var t=n(3117),r=n(102),s=(n(7294),n(3905)),i=n(254),o=n(2829),m=["components"],p={title:"Print LaTeX code",sidebar_position:4},l=void 0,c={unversionedId:"print-latex",id:"print-latex",title:"Print LaTeX code",description:"Why LaTeX?",source:"@site/docs/print-latex.mdx",sourceDirName:".",slug:"/print-latex",permalink:"/kohomology/docs/print-latex",editUrl:"https://github.com/shwaka/kohomology/edit/main/website/docs/print-latex.mdx",tags:[],version:"current",sidebarPosition:4,frontMatter:{title:"Print LaTeX code",sidebar_position:4},sidebar:"tutorialSidebar",previous:{title:"Cohomology of free loop space",permalink:"/kohomology/docs/free-loop-space"},next:{title:"Overview of classes",permalink:"/kohomology/docs/overview-of-classes"}},h={},b=[{value:"Why LaTeX?",id:"why-latex",level:2},{value:"Print LaTeX code",id:"print-latex-code",level:2},{value:"Print very long line",id:"print-very-long-line",level:2}],d={toc:b};function u(e){var a=e.components,p=(0,r.Z)(e,m);return(0,s.kt)("wrapper",(0,t.Z)({},d,p,{components:a,mdxType:"MDXLayout"}),(0,s.kt)("h2",{id:"why-latex"},"Why LaTeX?"),(0,s.kt)("p",null,"According to ",(0,s.kt)("a",{parentName:"p",href:"./free-loop-space"},"this page"),",\nyou can define the Sullivan model of the free loop space ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow"},"L"),(0,s.kt)("msup",{parentName:"mrow"},(0,s.kt)("mi",{parentName:"msup"},"S"),(0,s.kt)("mn",{parentName:"msup"},"2"))),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"LS^2")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.8141079999999999em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"L"),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.05764em"}},"S"),(0,s.kt)("span",{parentName:"span",className:"msupsub"},(0,s.kt)("span",{parentName:"span",className:"vlist-t"},(0,s.kt)("span",{parentName:"span",className:"vlist-r"},(0,s.kt)("span",{parentName:"span",className:"vlist",style:{height:"0.8141079999999999em"}},(0,s.kt)("span",{parentName:"span",style:{top:"-3.063em",marginRight:"0.05em"}},(0,s.kt)("span",{parentName:"span",className:"pstrut",style:{height:"2.7em"}}),(0,s.kt)("span",{parentName:"span",className:"sizing reset-size6 size3 mtight"},(0,s.kt)("span",{parentName:"span",className:"mord mtight"},"2"))))))))))))," of the 2-sphere ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("msup",{parentName:"mrow"},(0,s.kt)("mi",{parentName:"msup"},"S"),(0,s.kt)("mn",{parentName:"msup"},"2"))),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"S^2")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.8141079999999999em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.05764em"}},"S"),(0,s.kt)("span",{parentName:"span",className:"msupsub"},(0,s.kt)("span",{parentName:"span",className:"vlist-t"},(0,s.kt)("span",{parentName:"span",className:"vlist-r"},(0,s.kt)("span",{parentName:"span",className:"vlist",style:{height:"0.8141079999999999em"}},(0,s.kt)("span",{parentName:"span",style:{top:"-3.063em",marginRight:"0.05em"}},(0,s.kt)("span",{parentName:"span",className:"pstrut",style:{height:"2.7em"}}),(0,s.kt)("span",{parentName:"span",className:"sizing reset-size6 size3 mtight"},(0,s.kt)("span",{parentName:"span",className:"mord mtight"},"2")))))))))))),"\nas follows:"),(0,s.kt)(o.C,{path:"PrintTex.kt",restrict:"def",mdxType:"ImportKotlin"}),(0,s.kt)("p",null,"Now you can compute and print the basis of the cohomology ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("msup",{parentName:"mrow"},(0,s.kt)("mi",{parentName:"msup"},"H"),(0,s.kt)("mo",{parentName:"msup"},"\u2217")),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"("),(0,s.kt)("mi",{parentName:"mrow"},"L"),(0,s.kt)("msup",{parentName:"mrow"},(0,s.kt)("mi",{parentName:"msup"},"S"),(0,s.kt)("mn",{parentName:"msup"},"2")),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},")")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"H^*(LS^2)")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1.064108em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.08125em"}},"H"),(0,s.kt)("span",{parentName:"span",className:"msupsub"},(0,s.kt)("span",{parentName:"span",className:"vlist-t"},(0,s.kt)("span",{parentName:"span",className:"vlist-r"},(0,s.kt)("span",{parentName:"span",className:"vlist",style:{height:"0.688696em"}},(0,s.kt)("span",{parentName:"span",style:{top:"-3.063em",marginRight:"0.05em"}},(0,s.kt)("span",{parentName:"span",className:"pstrut",style:{height:"2.7em"}}),(0,s.kt)("span",{parentName:"span",className:"sizing reset-size6 size3 mtight"},(0,s.kt)("span",{parentName:"span",className:"mbin mtight"},"\u2217")))))))),(0,s.kt)("span",{parentName:"span",className:"mopen"},"("),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"L"),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.05764em"}},"S"),(0,s.kt)("span",{parentName:"span",className:"msupsub"},(0,s.kt)("span",{parentName:"span",className:"vlist-t"},(0,s.kt)("span",{parentName:"span",className:"vlist-r"},(0,s.kt)("span",{parentName:"span",className:"vlist",style:{height:"0.8141079999999999em"}},(0,s.kt)("span",{parentName:"span",style:{top:"-3.063em",marginRight:"0.05em"}},(0,s.kt)("span",{parentName:"span",className:"pstrut",style:{height:"2.7em"}}),(0,s.kt)("span",{parentName:"span",className:"sizing reset-size6 size3 mtight"},(0,s.kt)("span",{parentName:"span",className:"mord mtight"},"2")))))))),(0,s.kt)("span",{parentName:"span",className:"mclose"},")")))))," by"),(0,s.kt)(o.C,{path:"PrintTex.kt",restrict:"plain",mdxType:"ImportKotlin"}),(0,s.kt)("p",null,"and this prints"),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre"},"H^0(LX) = Q[[1]]\nH^1(LX) = Q[[sa], [sb]]\nH^2(LX) = Q[[a], [b], [sasb]]\nH^3(LX) = Q[[asb], [sasx], [2 sasy + sbsx], [1/2 sasz + sbsy], [sbsz]]\nH^4(LX) = Q[[1/2 asx + xsa], [asy - 1/2 bsx + xsb], [1/2 bsx + ysa], [1/2 asz + ysb], [-1/2 asz + bsy + zsa], [1/2 bsz + zsb], [sasbsx], [sasbsy], [sasbsz]]\n")),(0,s.kt)("p",null,"But this output is not very readable since"),(0,s.kt)("ul",null,(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("inlineCode",{parentName:"li"},"sasb")," can be confused with the product of indeterminates ",(0,s.kt)("inlineCode",{parentName:"li"},"s"),", ",(0,s.kt)("inlineCode",{parentName:"li"},"a"),", ",(0,s.kt)("inlineCode",{parentName:"li"},"s")," and ",(0,s.kt)("inlineCode",{parentName:"li"},"b"),"."),(0,s.kt)("li",{parentName:"ul"},"Fractions such as ",(0,s.kt)("inlineCode",{parentName:"li"},"1/2")," are hard to read.")),(0,s.kt)("h2",{id:"print-latex-code"},"Print LaTeX code"),(0,s.kt)("p",null,"By using ",(0,s.kt)("inlineCode",{parentName:"p"},"Printer"),", you can print a LaTeX code:"),(0,s.kt)(o.C,{path:"PrintTex.kt",restrict:"tex",mdxType:"ImportKotlin"}),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-latex"},"H^{0}(LX) &= \\Q[[1]] \\\\\nH^{1}(LX) &= \\Q[[\\bar{a}], [\\bar{b}]] \\\\\nH^{2}(LX) &= \\Q[[{a}], [{b}], [\\bar{a}\\bar{b}]] \\\\\nH^{3}(LX) &= \\Q[[{a}\\bar{b}], [\\bar{a}\\bar{x}], [2 \\bar{a}\\bar{y} + \\bar{b}\\bar{x}], [\\frac{1}{2} \\bar{a}\\bar{z} + \\bar{b}\\bar{y}], [\\bar{b}\\bar{z}]] \\\\\nH^{4}(LX) &= \\Q[[\\frac{1}{2} {a}\\bar{x} + {x}\\bar{a}], [{a}\\bar{y} - \\frac{1}{2} {b}\\bar{x} + {x}\\bar{b}], [\\frac{1}{2} {b}\\bar{x} + {y}\\bar{a}], [\\frac{1}{2} {a}\\bar{z} + {y}\\bar{b}], [-\\frac{1}{2} {a}\\bar{z} + {b}\\bar{y} + {z}\\bar{a}], [\\frac{1}{2} {b}\\bar{z} + {z}\\bar{b}], [\\bar{a}\\bar{b}\\bar{x}], [\\bar{a}\\bar{b}\\bar{y}], [\\bar{a}\\bar{b}\\bar{z}]] \\\\\n")),(0,s.kt)("details",null,(0,s.kt)("summary",null,"Full LaTeX source code"),(0,s.kt)(i.Z,{language:"latex",mdxType:"CodeBlock"},"\\documentclass{jsarticle}\n\\newcommand{\\Q}{\\mathbb Q}\n\\usepackage{amsmath}\n\\usepackage{amssymb}\n\\begin{document}\n\\begin{align*}\n  H^{0}(LX) &= \\Q[[1]] \\\\\n  H^{1}(LX) &= \\Q[[\\bar{a}], [\\bar{b}]] \\\\\n  H^{2}(LX) &= \\Q[[{a}], [{b}], [\\bar{a}\\bar{b}]] \\\\\n  H^{3}(LX) &= \\Q[[{a}\\bar{b}], [\\bar{a}\\bar{x}], [2 \\bar{a}\\bar{y} + \\bar{b}\\bar{x}], [\\frac{1}{2} \\bar{a}\\bar{z} + \\bar{b}\\bar{y}], [\\bar{b}\\bar{z}]] \\\\\n  H^{4}(LX) &= \\Q[[\\frac{1}{2} {a}\\bar{x} + {x}\\bar{a}], [{a}\\bar{y} - \\frac{1}{2} {b}\\bar{x} + {x}\\bar{b}], [\\frac{1}{2} {b}\\bar{x} + {y}\\bar{a}], [\\frac{1}{2} {a}\\bar{z} + {y}\\bar{b}], [-\\frac{1}{2} {a}\\bar{z} + {b}\\bar{y} + {z}\\bar{a}], [\\frac{1}{2} {b}\\bar{z} + {z}\\bar{b}], [\\bar{a}\\bar{b}\\bar{x}], [\\bar{a}\\bar{b}\\bar{y}], [\\bar{a}\\bar{b}\\bar{z}]] \\\\\n\\end{align*}\n\\end{document}")),(0,s.kt)("p",null,(0,s.kt)("img",{alt:"print-latex",src:n(8668).Z,width:"1454",height:"293"})),(0,s.kt)("h2",{id:"print-very-long-line"},"Print very long line"),(0,s.kt)("p",null,"Together with the LaTeX package ",(0,s.kt)("a",{parentName:"p",href:"https://ctan.org/pkg/autobreak?lang=en"},"autobreak"),", line breaks can be added automatically.\nIn the environment ",(0,s.kt)("inlineCode",{parentName:"p"},"autobreak"),",\nline breaks in the LaTeX source code are considered\nas possible candidates for line breaks in the output PDF file.\nSo ",(0,s.kt)("inlineCode",{parentName:"p"},'beforeSign = "\\n"')," and ",(0,s.kt)("inlineCode",{parentName:"p"},'joinToString(",\\n")')," in the following code give such candidates."),(0,s.kt)(o.C,{path:"PrintTex.kt",restrict:"long",mdxType:"ImportKotlin"}),(0,s.kt)("details",null,(0,s.kt)("summary",null,"Full LaTeX source code"),(0,s.kt)(i.Z,{language:"latex",mdxType:"CodeBlock"},"\\documentclass{jsarticle}\n\\newcommand{\\Q}{\\mathbb Q}\n\\usepackage{amsmath}\n\\usepackage{amssymb}\n\\usepackage{autobreak}\n\\begin{document}\n\\begin{align*}\n  \\begin{autobreak}\n    H^{0}(LX) = \\Q[\n    [1]]\n  \\end{autobreak}\\\\\n  \\begin{autobreak}\n    H^{1}(LX) = \\Q[\n    [\\bar{a}],\n    [\\bar{b}]]\n  \\end{autobreak}\\\\\n  \\begin{autobreak}\n    H^{2}(LX) = \\Q[\n    [{a}],\n    [{b}],\n    [\\bar{a}\\bar{b}]]\n  \\end{autobreak}\\\\\n  % ...\n\\end{align*}\n\\end{document}")),(0,s.kt)("p",null,(0,s.kt)("img",{alt:"print-latex",src:n(5453).Z,width:"1331",height:"867"})))}u.isMDXComponent=!0},3632:function(e,a,n){"use strict";n.r(a),a.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.model.FreeLoopSpace\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // start\n    // Define the Sullivan model of the 4-sphere.\n    val sphereDim = 4\n    val indeterminateList = listOf(\n        Indeterminate("x", sphereDim),\n        Indeterminate("y", sphereDim * 2 - 1)\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->\n        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2\n    }\n\n    // Define the Sullivan model of the free loop space.\n    val freeLoopSpace = FreeLoopSpace(sphere)\n    val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList\n\n    // Assert that d(sy) and -2*x*sx are the same.\n    freeLoopSpace.context.run {\n        println("dsy = ${d(sy)} = ${-2 * x * sx}")\n    }\n\n    // Compute cohomology of the free loop space.\n    for (degree in 0 until 25) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        println("H^$degree(LS^$sphereDim) = Q$basis")\n    }\n    // end\n}\n'},6229:function(e,a,n){"use strict";n.r(a),a.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.model.FreeLoopSpace\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\nimport com.github.shwaka.kohomology.util.PrintType\nimport com.github.shwaka.kohomology.util.Printer\nimport com.github.shwaka.kohomology.util.ShowShift\n\nfun main() {\n    // start def\n    val indeterminateList = listOf(\n        Indeterminate("a", 2),\n        Indeterminate("b", 2),\n        Indeterminate("x", 3),\n        Indeterminate("y", 3),\n        Indeterminate("z", 3)\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, x, y, z) ->\n        listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))\n    }\n    val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)\n    // end def\n\n    println("----- plain output -----")\n    // start plain\n    for (degree in 0..4) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        println("H^$degree(LX) = Q$basis")\n    }\n    // end plain\n\n    println("----- tex output -----")\n    // start tex\n    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)\n    for (degree in 0..4) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        println("H^{$degree}(LX) &= \\\\Q${basis.map { v -> p(v) }} \\\\\\\\")\n    }\n    // end tex\n\n    println("----- long tex output -----")\n    // start long\n    val p2 = Printer(printType = PrintType.TEX, beforeSign = "\\n", showShift = ShowShift.BAR)\n    for (degree in 0..6) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        val basisString = basis.joinToString(",\\n") { v -> p2(v) }\n        println("\\\\begin{autobreak}\\nH^{$degree}(LX) = \\\\Q[\\n${basisString}]\\n\\\\end{autobreak}\\\\\\\\")\n    }\n    // end long\n}\n'},5125:function(e,a,n){"use strict";n.r(a),a.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // start def\n    val n = 2\n    // Declare an indeterminate (generator) for the free commutative graded algebra \u039b(x,y)\n    val indeterminateList = listOf(\n        Indeterminate("x", 2 * n),\n        Indeterminate("y", 4 * n - 1),\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    // Sullivan algebra can be defined by using the constructor of FreeDGAlgebra.\n    // The last argument is a function\n    // which receives list of generators and returns the list of the values of the differential.\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->\n        // zeroGVector is a special element that represents zero in any degree.\n        val dx = zeroGVector\n        // x.pow(2) represents x^2\n        val dy = x.pow(2)\n        listOf(dx, dy)\n    }\n    // end def\n\n    // start cohomology\n    for (degree in 0 until 10) {\n        val basis = sphere.cohomology.getBasis(degree)\n        println("H^$degree(S^${2 * n}) = Q$basis")\n    }\n    // end cohomology\n\n    // start context\n    val (x, y) = sphere.gAlgebra.generatorList\n\n    // You can\'t write DGA operations here.\n\n    sphere.context.run {\n        // You can write DGA operations in "context.run"\n        println("d(x * y) = ${d(x * y)}")\n        println(d(x).isZero())\n        println(x.cohomologyClass())\n        println(x.pow(2).cohomologyClass())\n    }\n    // end context\n}\n'},7219:function(e,a,n){"use strict";n.r(a),a.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // start\n    val indeterminateList = listOf(\n        Indeterminate("a", 2),\n        Indeterminate("b", 2),\n        Indeterminate("x", 3),\n        Indeterminate("y", 3),\n        Indeterminate("z", 3)\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, x, y, z) ->\n        val da = zeroGVector // da = 0\n        val db = zeroGVector // db = 0\n        val dx = a.pow(2) // dx = a^2\n        val dy = a * b // dy = ab\n        val dz = b.pow(2) // dz = b^2\n        listOf(da, db, dx, dy, dz)\n    }\n    for (degree in 0 until 10) {\n        val basis = sphere.cohomology.getBasis(degree)\n        println("H^$degree = Q$basis")\n    }\n    // end\n}\n'},5453:function(e,a,n){"use strict";a.Z=n.p+"assets/images/print-latex-long-3d0e53556c06a2480e8b408b7fcf9149.png"},8668:function(e,a,n){"use strict";a.Z=n.p+"assets/images/print-latex-db830218d75bb05deca353970a0419a3.png"}}]);