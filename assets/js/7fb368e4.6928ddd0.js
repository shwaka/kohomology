(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[663],{6333:function(e,t,n){var o={"./FreeLoopSpace.kt":3632,"./SphereModel.kt":5125};function r(e){var t=a(e);return n(t)}function a(e){if(!n.o(o,e)){var t=new Error("Cannot find module '"+e+"'");throw t.code="MODULE_NOT_FOUND",t}return o[e]}r.keys=function(){return Object.keys(o)},r.resolve=a,e.exports=r,r.id=6333},3229:function(e,t,n){"use strict";n.d(t,{Z:function(){return i}});var o={plain:{color:"#bfc7d5",backgroundColor:"#292d3e"},styles:[{types:["comment"],style:{color:"rgb(105, 112, 152)",fontStyle:"italic"}},{types:["string","inserted"],style:{color:"rgb(195, 232, 141)"}},{types:["number"],style:{color:"rgb(247, 140, 108)"}},{types:["builtin","char","constant","function"],style:{color:"rgb(130, 170, 255)"}},{types:["punctuation","selector"],style:{color:"rgb(199, 146, 234)"}},{types:["variable"],style:{color:"rgb(191, 199, 213)"}},{types:["class-name","attr-name"],style:{color:"rgb(255, 203, 107)"}},{types:["tag","deleted"],style:{color:"rgb(255, 85, 114)"}},{types:["operator"],style:{color:"rgb(137, 221, 255)"}},{types:["boolean"],style:{color:"rgb(255, 88, 116)"}},{types:["keyword"],style:{fontStyle:"italic"}},{types:["doctype"],style:{color:"rgb(199, 146, 234)",fontStyle:"italic"}},{types:["namespace"],style:{color:"rgb(178, 204, 214)"}},{types:["url"],style:{color:"rgb(221, 221, 221)"}}]},r=n(5350),a=n(2822),i=function(){var e=(0,a.LU)().prism,t=(0,r.Z)().isDarkTheme,n=e.theme||o,i=e.darkTheme||n;return t?i:n}},1705:function(e,t,n){"use strict";n.d(t,{C:function(){return O}});var o=n(7294);function r(e){var t=e.map((function(e){return function(e){if(""===e)return 1/0;var t=new RegExp("^ *"),n=e.match(t);if(null===n)throw new Error("This can't happen");return n[0].length}(e)})),n=Math.min.apply(Math,t);return e.map((function(e){return e.substring(n)}))}function a(e,t){return!0===t?new RegExp("// "+e):new RegExp("// "+e+" +"+t)}var i=n(7462),l=n(6010),s=n(3746),c=n(195),u=n(7594),p=n.n(u),m=n(3229),d=n(5999),h="codeBlockContainer_k6sy",g="codeBlockContent_zmsB",y="codeBlockTitle_lyY6",f="codeBlock_ohZ0",v="copyButton_KdEu",b="codeBlockLines_xAn1",k=n(2822),x=/{([\d,-]+)}/,w=["js","jsBlock","jsx","python","html"],S={js:{start:"\\/\\/",end:""},jsBlock:{start:"\\/\\*",end:"\\*\\/"},jsx:{start:"\\{\\s*\\/\\*",end:"\\*\\/\\s*\\}"},python:{start:"#",end:""},html:{start:"\x3c!--",end:"--\x3e"}},E=["highlight-next-line","highlight-start","highlight-end"],j=function(e){void 0===e&&(e=w);var t=e.map((function(e){var t=S[e],n=t.start,o=t.end;return"(?:"+n+"\\s*("+E.join("|")+")\\s*"+o+")"})).join("|");return new RegExp("^\\s*(?:"+t+")\\s*$")};function C(e){var t=e.children,n=e.className,r=e.metastring,a=e.title,u=e.href,w=e.linkTitle,S=(0,k.LU)().prism,E=(0,o.useState)(!1),C=E[0],D=E[1],L=(0,o.useState)(!1),T=L[0],O=L[1];(0,o.useEffect)((function(){O(!0)}),[]);var N=(0,k.bc)(r)||a,B=(0,o.useRef)(null),Z=[],I=(0,m.Z)(),A=Array.isArray(t)?t.join(""):t;if(r&&x.test(r)){var P=r.match(x)[1];Z=p()(P).filter((function(e){return e>0}))}var _=null==n?void 0:n.split(" ").find((function(e){return e.startsWith("language-")})),R=null==_?void 0:_.replace(/language-/,"");!R&&S.defaultLanguage&&(R=S.defaultLanguage);var M=A.replace(/\n$/,"");if(0===Z.length&&void 0!==R){for(var $,F="",G=function(e){switch(e){case"js":case"javascript":case"ts":case"typescript":return j(["js","jsBlock"]);case"jsx":case"tsx":return j(["js","jsBlock","jsx"]);case"html":return j(["js","jsBlock","html"]);case"python":case"py":return j(["python"]);default:return j()}}(R),z=A.replace(/\n$/,"").split("\n"),U=0;U<z.length;){var H=U+1,K=z[U].match(G);if(null!==K){switch(K.slice(1).reduce((function(e,t){return e||t}),void 0)){case"highlight-next-line":F+=H+",";break;case"highlight-start":$=H;break;case"highlight-end":F+=$+"-"+(H-1)+","}z.splice(U,1)}else U+=1}Z=p()(F),M=z.join("\n")}var Q=function(){(0,c.Z)(M),D(!0),setTimeout((function(){return D(!1)}),2e3)};return o.createElement(s.ZP,(0,i.Z)({},s.lG,{key:String(T),theme:I,code:M,language:R}),(function(e){var t=e.className,r=e.style,a=e.tokens,s=e.getLineProps,c=e.getTokenProps;return o.createElement("div",{className:(0,l.Z)(h,null==n?void 0:n.replace(/language-[^ ]+/,""))},(N||u)&&o.createElement("div",{style:r,className:y},N,u&&o.createElement("a",{href:u,target:"_blank"},w||u)),o.createElement("div",{className:(0,l.Z)(g,R)},o.createElement("pre",{tabIndex:0,className:(0,l.Z)(t,f,"thin-scrollbar"),style:r},o.createElement("code",{className:b},a.map((function(e,t){1===e.length&&"\n"===e[0].content&&(e[0].content="");var n=s({line:e,key:t});return Z.includes(t+1)&&(n.className+=" docusaurus-highlight-code-line"),o.createElement("span",(0,i.Z)({key:t},n),e.map((function(e,t){return o.createElement("span",(0,i.Z)({key:t},c({token:e,key:t})))})),o.createElement("br",null))})))),o.createElement("button",{ref:B,type:"button","aria-label":(0,d.I)({id:"theme.CodeBlock.copyButtonAriaLabel",message:"Copy code to clipboard",description:"The ARIA label for copy code blocks button"}),className:(0,l.Z)(v,"clean-btn"),onClick:Q},C?o.createElement(d.Z,{id:"theme.CodeBlock.copied",description:"The copied button label on code blocks"},"Copied"):o.createElement(d.Z,{id:"theme.CodeBlock.copy",description:"The copy button label on code blocks"},"Copy"))))}))}var D=n(6333);function L(e){return e.startsWith("./")?e:"./"+e}var T=new Map(D.keys().map((function(e){return[L(e),D(e).default]})));function O(e){var t="https://github.com/shwaka/kohomology/blob/main/sample/src/main/kotlin/"+e.path,n=T.get(L(e.path));if(void 0===n)return o.createElement("div",null,"Invalid path: "+e.path);var i=function(e,t){if(void 0===t)return e;var n=a("start",t),o=a("end",t),i=function(e,t,n){var o=e.findIndex((function(e){return e.match(t)})),r=e.findIndex((function(e){return e.match(n)}));return-1===o||-1===r?null:e.slice(o+1,r)}(e.split("\n"),n,o);return null===i?null:r(i).join("\n")}(n,e.restrict);return null===i?o.createElement("div",null,"ERROR: ",o.createElement("code",null,e.restrict)," is not found in ",o.createElement("a",{href:t},t)):o.createElement("div",null,o.createElement(C,{className:"language-kotlin",href:t,linkTitle:e.path},i))}},5740:function(e,t,n){"use strict";n.r(t),n.d(t,{frontMatter:function(){return s},contentTitle:function(){return c},metadata:function(){return u},toc:function(){return p},default:function(){return d}});var o=n(7462),r=n(3366),a=(n(7294),n(3905)),i=n(1705),l=["components"],s={title:"Cohomology of Sullivan algebras",sidebar_position:3},c=void 0,u={unversionedId:"sullivan-algebra",id:"sullivan-algebra",isDocsHomePage:!1,title:"Cohomology of Sullivan algebras",description:"Cohomology of a Sullivan algebra can be computed in the following way.",source:"@site/docs/sullivan-algebra.mdx",sourceDirName:".",slug:"/sullivan-algebra",permalink:"/kohomology/docs/sullivan-algebra",editUrl:"https://github.com/shwaka/kohomology/edit/main/website/docs/sullivan-algebra.mdx",tags:[],version:"current",sidebarPosition:3,frontMatter:{title:"Cohomology of Sullivan algebras",sidebar_position:3},sidebar:"tutorialSidebar",previous:{title:"Quick start",permalink:"/kohomology/docs/quick-start"},next:{title:"Cohomology of free loop space",permalink:"/kohomology/docs/free-loop-space"}},p=[],m={toc:p};function d(e){var t=e.components,n=(0,r.Z)(e,l);return(0,a.kt)("wrapper",(0,o.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"Cohomology of a Sullivan algebra can be computed in the following way."),(0,a.kt)(i.C,{path:"SphereModel.kt",restrict:"basic",mdxType:"ImportKotlin"}),(0,a.kt)("p",null,"DGA operations (e.g. sum, multiplication, differential) can be applied within ",(0,a.kt)("inlineCode",{parentName:"p"},"context.run"),"."),(0,a.kt)(i.C,{path:"SphereModel.kt",restrict:"context",mdxType:"ImportKotlin"}))}d.isMDXComponent=!0},7594:function(e,t){function n(e){let t,n=[];for(let o of e.split(",").map((e=>e.trim())))if(/^-?\d+$/.test(o))n.push(parseInt(o,10));else if(t=o.match(/^(-?\d+)(-|\.\.\.?|\u2025|\u2026|\u22EF)(-?\d+)$/)){let[e,o,r,a]=t;if(o&&a){o=parseInt(o),a=parseInt(a);const e=o<a?1:-1;"-"!==r&&".."!==r&&"\u2025"!==r||(a+=e);for(let t=o;t!==a;t+=e)n.push(t)}}return n}t.default=n,e.exports=n},3746:function(e,t,n){"use strict";n.d(t,{ZP:function(){return g},lG:function(){return i}});var o=n(7410),r={plain:{backgroundColor:"#2a2734",color:"#9a86fd"},styles:[{types:["comment","prolog","doctype","cdata","punctuation"],style:{color:"#6c6783"}},{types:["namespace"],style:{opacity:.7}},{types:["tag","operator","number"],style:{color:"#e09142"}},{types:["property","function"],style:{color:"#9a86fd"}},{types:["tag-id","selector","atrule-id"],style:{color:"#eeebff"}},{types:["attr-name"],style:{color:"#c4b9fe"}},{types:["boolean","string","entity","url","attr-value","keyword","control","directive","unit","statement","regex","at-rule","placeholder","variable"],style:{color:"#ffcc99"}},{types:["deleted"],style:{textDecorationLine:"line-through"}},{types:["inserted"],style:{textDecorationLine:"underline"}},{types:["italic"],style:{fontStyle:"italic"}},{types:["important","bold"],style:{fontWeight:"bold"}},{types:["important"],style:{color:"#c4b9fe"}}]},a=n(7294),i={Prism:o.default,theme:r};function l(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(){return s=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var o in n)Object.prototype.hasOwnProperty.call(n,o)&&(e[o]=n[o])}return e},s.apply(this,arguments)}var c=/\r\n|\r|\n/,u=function(e){0===e.length?e.push({types:["plain"],content:"\n",empty:!0}):1===e.length&&""===e[0].content&&(e[0].content="\n",e[0].empty=!0)},p=function(e,t){var n=e.length;return n>0&&e[n-1]===t?e:e.concat(t)},m=function(e,t){var n=e.plain,o=Object.create(null),r=e.styles.reduce((function(e,n){var o=n.languages,r=n.style;return o&&!o.includes(t)||n.types.forEach((function(t){var n=s({},e[t],r);e[t]=n})),e}),o);return r.root=n,r.plain=s({},n,{backgroundColor:null}),r};function d(e,t){var n={};for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&-1===t.indexOf(o)&&(n[o]=e[o]);return n}var h=function(e){function t(){for(var t=this,n=[],o=arguments.length;o--;)n[o]=arguments[o];e.apply(this,n),l(this,"getThemeDict",(function(e){if(void 0!==t.themeDict&&e.theme===t.prevTheme&&e.language===t.prevLanguage)return t.themeDict;t.prevTheme=e.theme,t.prevLanguage=e.language;var n=e.theme?m(e.theme,e.language):void 0;return t.themeDict=n})),l(this,"getLineProps",(function(e){var n=e.key,o=e.className,r=e.style,a=s({},d(e,["key","className","style","line"]),{className:"token-line",style:void 0,key:void 0}),i=t.getThemeDict(t.props);return void 0!==i&&(a.style=i.plain),void 0!==r&&(a.style=void 0!==a.style?s({},a.style,r):r),void 0!==n&&(a.key=n),o&&(a.className+=" "+o),a})),l(this,"getStyleForToken",(function(e){var n=e.types,o=e.empty,r=n.length,a=t.getThemeDict(t.props);if(void 0!==a){if(1===r&&"plain"===n[0])return o?{display:"inline-block"}:void 0;if(1===r&&!o)return a[n[0]];var i=o?{display:"inline-block"}:{},l=n.map((function(e){return a[e]}));return Object.assign.apply(Object,[i].concat(l))}})),l(this,"getTokenProps",(function(e){var n=e.key,o=e.className,r=e.style,a=e.token,i=s({},d(e,["key","className","style","token"]),{className:"token "+a.types.join(" "),children:a.content,style:t.getStyleForToken(a),key:void 0});return void 0!==r&&(i.style=void 0!==i.style?s({},i.style,r):r),void 0!==n&&(i.key=n),o&&(i.className+=" "+o),i})),l(this,"tokenize",(function(e,t,n,o){var r={code:t,grammar:n,language:o,tokens:[]};e.hooks.run("before-tokenize",r);var a=r.tokens=e.tokenize(r.code,r.grammar,r.language);return e.hooks.run("after-tokenize",r),a}))}return e&&(t.__proto__=e),t.prototype=Object.create(e&&e.prototype),t.prototype.constructor=t,t.prototype.render=function(){var e=this.props,t=e.Prism,n=e.language,o=e.code,r=e.children,a=this.getThemeDict(this.props),i=t.languages[n];return r({tokens:function(e){for(var t=[[]],n=[e],o=[0],r=[e.length],a=0,i=0,l=[],s=[l];i>-1;){for(;(a=o[i]++)<r[i];){var m=void 0,d=t[i],h=n[i][a];if("string"==typeof h?(d=i>0?d:["plain"],m=h):(d=p(d,h.type),h.alias&&(d=p(d,h.alias)),m=h.content),"string"==typeof m){var g=m.split(c),y=g.length;l.push({types:d,content:g[0]});for(var f=1;f<y;f++)u(l),s.push(l=[]),l.push({types:d,content:g[f]})}else i++,t.push(d),n.push(m),o.push(0),r.push(m.length)}i--,t.pop(),n.pop(),o.pop(),r.pop()}return u(l),s}(void 0!==i?this.tokenize(t,o,i,n):[o]),className:"prism-code language-"+n,style:void 0!==a?a.root:{},getLineProps:this.getLineProps,getTokenProps:this.getTokenProps})},t}(a.Component),g=h},3632:function(e,t,n){"use strict";n.r(t),t.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.model.FreeLoopSpace\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational\n\nfun main() {\n    // start\n    val sphereDim = 4\n    val indeterminateList = listOf(\n        Indeterminate("x", sphereDim),\n        Indeterminate("y", sphereDim * 2 - 1)\n    )\n    val matrixSpace = SparseMatrixSpaceOverBigRational\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->\n        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2\n    }\n\n    val freeLoopSpace = FreeLoopSpace(sphere)\n    val (x, _, sx, sy) = freeLoopSpace.gAlgebra.generatorList\n\n    freeLoopSpace.context.run {\n        println("dsy = ${d(sy)} = ${-2 * x * sx}")\n    }\n\n    for (degree in 0 until 25) {\n        val basis = freeLoopSpace.cohomology[degree].getBasis()\n        println("H^$degree(LS^$sphereDim) = Q$basis")\n    }\n    // end\n}\n'},5125:function(e,t,n){"use strict";n.r(t),t.default='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational\n\nfun main() {\n    // start basic\n    val sphereDim = 4\n    val indeterminateList = listOf(\n        Indeterminate("x", sphereDim),\n        Indeterminate("y", sphereDim * 2 - 1)\n    )\n    val matrixSpace = SparseMatrixSpaceOverBigRational\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->\n        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2\n    }\n\n    for (degree in 0 until 10) {\n        val basis = sphere.cohomology[degree].getBasis()\n        println("H^$degree(S^$sphereDim) = Q$basis")\n    }\n    // end basic\n\n    // start context\n    val (x, y) = sphere.gAlgebra.generatorList\n\n    sphere.context.run {\n        // Operations in a DGA can be applied within \'context.run\'\n        println("d(x * y) = ${d(x * y)}")\n        println(d(x).isZero())\n        println(x.cohomologyClass())\n        println(x.pow(2).cohomologyClass())\n    }\n    // end context\n}\n'},195:function(e,t,n){"use strict";function o(e,t){var n=(void 0===t?{}:t).target,o=void 0===n?document.body:n,r=document.createElement("textarea"),a=document.activeElement;r.value=e,r.setAttribute("readonly",""),r.style.contain="strict",r.style.position="absolute",r.style.left="-9999px",r.style.fontSize="12pt";var i=document.getSelection(),l=!1;i.rangeCount>0&&(l=i.getRangeAt(0)),o.append(r),r.select(),r.selectionStart=0,r.selectionEnd=e.length;var s=!1;try{s=document.execCommand("copy")}catch(c){}return r.remove(),l&&(i.removeAllRanges(),i.addRange(l)),a&&a.focus(),s}n.d(t,{Z:function(){return o}})}}]);