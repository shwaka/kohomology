(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[394],{7826:(a,e,t)=>{"use strict";t.d(e,{C:()=>g});var n=t(1287),s=t(6464),m=t(7294);const r={};function p(a){let{href:e,linkTitle:t,...n}=a;return m.createElement("div",{className:r.MyCodeBlock},null!==e&&m.createElement("a",{href:e,target:"_blank",rel:"noreferrer"},null!==t?t:e),m.createElement(s.Z,n,n.children))}const o="error_c9xe";function i(a){const e=a.map((a=>function(a){if(""===a)return 1/0;const e=new RegExp("^ *"),t=a.match(e);if(null===t)throw new Error("This can't happen");return t[0].length}(a))),t=Math.min(...e);return a.map((a=>a.substring(t)))}function l(a,e){return!0===e?new RegExp(`// \\\\${a}`):new RegExp(`// \\\\${a}{${e}}`)}function c(a,e){if(void 0===e)return{text:a};const t=l("begin",e),n=l("end",e);return function(a,e,t){const n=a.findIndex((a=>a.match(e))),s=a.findIndex((a=>a.match(t)));return-1===n||-1===s?null:{text:i(a.slice(n+1,s)).join("\n"),begin:n+2,end:s}}(a.split("\n"),t,n)}const N=t(5284);function h(a){return a.startsWith("./")?a:"./"+a}const k=new Map(N.keys().map((a=>[h(a),N(a).default])));function g(a){const e=`${n.g}/website/sample/src/main/kotlin/${a.path}`,t=k.get(h(a.path));if(void 0===t)return m.createElement("div",null,`Invalid path: ${a.path}`);const s=c(t,a.restrict);if(null===s)return m.createElement("div",{className:a.className},m.createElement("div",{className:o},"ERROR: ",m.createElement("code",null,a.restrict)," is not found in ",m.createElement("a",{href:e},e)));const r=function(a){return"begin"in a}(s)?`${e}#L${s.begin}-L${s.end}`:e;return m.createElement("div",{className:a.className},m.createElement(p,{className:"language-kotlin",href:r,linkTitle:a.path},s.text))}},1287:(a,e,t)=>{"use strict";t.d(e,{g:()=>n});const n="https://github.com/shwaka/kohomology/blob/main"},7806:(a,e,t)=>{"use strict";t.r(e),t.d(e,{assets:()=>i,contentTitle:()=>p,default:()=>N,frontMatter:()=>r,metadata:()=>o,toc:()=>l});var n=t(7462),s=(t(7294),t(3905)),m=t(7826);const r={title:"MultiDegree",sidebar_position:6},p=void 0,o={unversionedId:"multi-degree",id:"multi-degree",title:"MultiDegree",description:"Using MultiDegree, you can efficiently compute cohomology of some Sullivan algebras.",source:"@site/docs/multi-degree.mdx",sourceDirName:".",slug:"/multi-degree",permalink:"/kohomology/docs/multi-degree",draft:!1,editUrl:"https://github.com/shwaka/kohomology/edit/main/website/docs/multi-degree.mdx",tags:[],version:"current",sidebarPosition:6,frontMatter:{title:"MultiDegree",sidebar_position:6},sidebar:"tutorialSidebar",previous:{title:"DGA map",permalink:"/kohomology/docs/dga-map"},next:{title:"Print LaTeX code",permalink:"/kohomology/docs/print-latex"}},i={},l=[{value:"Mathematical explanation",id:"mathematical-explanation",level:2},{value:"Usage",id:"usage",level:2},{value:"Performance",id:"performance",level:2}],c={toc:l};function N(a){let{components:e,...t}=a;return(0,s.kt)("wrapper",(0,n.Z)({},c,t,{components:e,mdxType:"MDXLayout"}),(0,s.kt)("p",null,"Using ",(0,s.kt)("inlineCode",{parentName:"p"},"MultiDegree"),", you can efficiently compute cohomology of some Sullivan algebras."),(0,s.kt)("h2",{id:"mathematical-explanation"},"Mathematical explanation"),(0,s.kt)("p",null,"Usually, Sullivan algebras are ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"\\Z")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.68889em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")))))),"-graded.\nBut, in some case, the grading can be extended to a larger group."),(0,s.kt)("p",null,"For example, the Sullivan model ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"("),(0,s.kt)("mo",{parentName:"mrow"},"\u2227"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"("),(0,s.kt)("mi",{parentName:"mrow"},"x"),(0,s.kt)("mo",{parentName:"mrow",separator:"true"},","),(0,s.kt)("mi",{parentName:"mrow"},"y"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},")"),(0,s.kt)("mo",{parentName:"mrow",separator:"true"},","),(0,s.kt)("mi",{parentName:"mrow"},"d"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},")")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"(\\wedge(x,y), d)")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mopen"},"("),(0,s.kt)("span",{parentName:"span",className:"mord"},"\u2227"),(0,s.kt)("span",{parentName:"span",className:"mopen"},"("),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"x"),(0,s.kt)("span",{parentName:"span",className:"mpunct"},","),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.16666666666666666em"}}),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.03588em"}},"y"),(0,s.kt)("span",{parentName:"span",className:"mclose"},")"),(0,s.kt)("span",{parentName:"span",className:"mpunct"},","),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.16666666666666666em"}}),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"d"),(0,s.kt)("span",{parentName:"span",className:"mclose"},")")))))," of the even dimensional sphere ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("msup",{parentName:"mrow"},(0,s.kt)("mi",{parentName:"msup"},"S"),(0,s.kt)("mrow",{parentName:"msup"},(0,s.kt)("mn",{parentName:"mrow"},"2"),(0,s.kt)("mi",{parentName:"mrow"},"n")))),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"S^{2n}")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.8141079999999999em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.05764em"}},"S"),(0,s.kt)("span",{parentName:"span",className:"msupsub"},(0,s.kt)("span",{parentName:"span",className:"vlist-t"},(0,s.kt)("span",{parentName:"span",className:"vlist-r"},(0,s.kt)("span",{parentName:"span",className:"vlist",style:{height:"0.8141079999999999em"}},(0,s.kt)("span",{parentName:"span",style:{top:"-3.063em",marginRight:"0.05em"}},(0,s.kt)("span",{parentName:"span",className:"pstrut",style:{height:"2.7em"}}),(0,s.kt)("span",{parentName:"span",className:"sizing reset-size6 size3 mtight"},(0,s.kt)("span",{parentName:"span",className:"mord mtight"},(0,s.kt)("span",{parentName:"span",className:"mord mtight"},"2"),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal mtight"},"n"))))))))))))),"\ncan be ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"("),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow"},"\u2295"),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},")")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"(\\Z\\oplus\\Z)")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mopen"},"("),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}}),(0,s.kt)("span",{parentName:"span",className:"mbin"},"\u2295"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mclose"},")"))))),"-graded since it has ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow"},"n")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"n")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.43056em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"n"))))),' as a "parameter".\nMore precisely,\nthe degrees ',(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow",mathvariant:"normal"},"\u2223"),(0,s.kt)("mi",{parentName:"mrow"},"x"),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"normal"},"\u2223"),(0,s.kt)("mo",{parentName:"mrow"},"="),(0,s.kt)("mn",{parentName:"mrow"},"2"),(0,s.kt)("mi",{parentName:"mrow"},"n")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"|x|=2n")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},"\u2223"),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"x"),(0,s.kt)("span",{parentName:"span",className:"mord"},"\u2223"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2777777777777778em"}}),(0,s.kt)("span",{parentName:"span",className:"mrel"},"="),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2777777777777778em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.64444em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},"2"),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"n")))))," and ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow",mathvariant:"normal"},"\u2223"),(0,s.kt)("mi",{parentName:"mrow"},"y"),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"normal"},"\u2223"),(0,s.kt)("mo",{parentName:"mrow"},"="),(0,s.kt)("mn",{parentName:"mrow"},"4"),(0,s.kt)("mi",{parentName:"mrow"},"n"),(0,s.kt)("mo",{parentName:"mrow"},"\u2212"),(0,s.kt)("mn",{parentName:"mrow"},"1")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"|y|=4n-1")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},"\u2223"),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.03588em"}},"y"),(0,s.kt)("span",{parentName:"span",className:"mord"},"\u2223"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2777777777777778em"}}),(0,s.kt)("span",{parentName:"span",className:"mrel"},"="),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2777777777777778em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.72777em",verticalAlign:"-0.08333em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},"4"),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"n"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}}),(0,s.kt)("span",{parentName:"span",className:"mbin"},"\u2212"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.64444em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},"1")))))," can be considered as elements of\n",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow"},"\u2295"),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow"},"="),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"{"),(0,s.kt)("mn",{parentName:"mrow"},"1"),(0,s.kt)("mo",{parentName:"mrow",separator:"true"},","),(0,s.kt)("mi",{parentName:"mrow"},"n"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"}")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"\\Z\\oplus\\Z=\\Z\\{1,n\\}")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.77222em",verticalAlign:"-0.08333em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}}),(0,s.kt)("span",{parentName:"span",className:"mbin"},"\u2295"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.68889em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2777777777777778em"}}),(0,s.kt)("span",{parentName:"span",className:"mrel"},"="),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2777777777777778em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mopen"},"{"),(0,s.kt)("span",{parentName:"span",className:"mord"},"1"),(0,s.kt)("span",{parentName:"span",className:"mpunct"},","),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.16666666666666666em"}}),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"n"),(0,s.kt)("span",{parentName:"span",className:"mclose"},"}")))))," (i.e. ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"{"),(0,s.kt)("mn",{parentName:"mrow"},"1"),(0,s.kt)("mo",{parentName:"mrow",separator:"true"},","),(0,s.kt)("mi",{parentName:"mrow"},"n"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"}")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"\\{1, n\\}")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mopen"},"{"),(0,s.kt)("span",{parentName:"span",className:"mord"},"1"),(0,s.kt)("span",{parentName:"span",className:"mpunct"},","),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.16666666666666666em"}}),(0,s.kt)("span",{parentName:"span",className:"mord mathnormal"},"n"),(0,s.kt)("span",{parentName:"span",className:"mclose"},"}")))))," is the basis of ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow"},"\u2295"),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"\\Z\\oplus\\Z")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.77222em",verticalAlign:"-0.08333em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}}),(0,s.kt)("span",{parentName:"span",className:"mbin"},"\u2295"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.68889em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")))))),")."),(0,s.kt)("p",null,(0,s.kt)("inlineCode",{parentName:"p"},"kohomology")," supports computation of cohomology with\n",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"("),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow"},"\u2295"),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},")")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"(\\Z\\oplus\\Z)")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mopen"},"("),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}}),(0,s.kt)("span",{parentName:"span",className:"mbin"},"\u2295"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mclose"},")"))))),"-grading (or ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},"("),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow"},"\u2295"),(0,s.kt)("mo",{parentName:"mrow"},"\u22ef"),(0,s.kt)("mo",{parentName:"mrow"},"\u2295"),(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z"),(0,s.kt)("mo",{parentName:"mrow",stretchy:"false"},")")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"(\\Z\\oplus\\cdots\\oplus\\Z)")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mopen"},"("),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}}),(0,s.kt)("span",{parentName:"span",className:"mbin"},"\u2295"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.66666em",verticalAlign:"-0.08333em"}}),(0,s.kt)("span",{parentName:"span",className:"minner"},"\u22ef"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}}),(0,s.kt)("span",{parentName:"span",className:"mbin"},"\u2295"),(0,s.kt)("span",{parentName:"span",className:"mspace",style:{marginRight:"0.2222222222222222em"}})),(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"1em",verticalAlign:"-0.25em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")),(0,s.kt)("span",{parentName:"span",className:"mclose"},")"))))),"-grading),\nwhich is faster than the usual one."),(0,s.kt)("h2",{id:"usage"},"Usage"),(0,s.kt)("p",null,"First you need to define ",(0,s.kt)("inlineCode",{parentName:"p"},"degreeGroup"),"."),(0,s.kt)(m.C,{path:"MultiDegree.kt",restrict:"degree",mdxType:"ImportKotlin"}),(0,s.kt)("p",null,"Then you can define a Sullivan algebra using the above ",(0,s.kt)("inlineCode",{parentName:"p"},"degreeGroup"),"."),(0,s.kt)(m.C,{path:"MultiDegree.kt",restrict:"model",mdxType:"ImportKotlin"}),(0,s.kt)("p",null,"Its cohomology can be computed as follows:"),(0,s.kt)(m.C,{path:"MultiDegree.kt",restrict:"cohomology",mdxType:"ImportKotlin"}),(0,s.kt)("h2",{id:"performance"},"Performance"),(0,s.kt)("p",null,"If applicable, computations with ",(0,s.kt)("inlineCode",{parentName:"p"},"MultiDegree")," are much faster than those with ",(0,s.kt)("inlineCode",{parentName:"p"},"IntDegree")," (usual ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("mi",{parentName:"mrow",mathvariant:"double-struck"},"Z")),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"\\Z")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.68889em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathbb"},"Z")))))),"-grading).\n",(0,s.kt)("a",{parentName:"p",href:"/kohomology/docs/free-loop-space"},"This page")," contains benchmark for the case of the free loop space of ",(0,s.kt)("span",{parentName:"p",className:"math math-inline"},(0,s.kt)("span",{parentName:"span",className:"katex"},(0,s.kt)("span",{parentName:"span",className:"katex-mathml"},(0,s.kt)("math",{parentName:"span",xmlns:"http://www.w3.org/1998/Math/MathML"},(0,s.kt)("semantics",{parentName:"math"},(0,s.kt)("mrow",{parentName:"semantics"},(0,s.kt)("msup",{parentName:"mrow"},(0,s.kt)("mi",{parentName:"msup"},"S"),(0,s.kt)("mn",{parentName:"msup"},"2"))),(0,s.kt)("annotation",{parentName:"semantics",encoding:"application/x-tex"},"S^2")))),(0,s.kt)("span",{parentName:"span",className:"katex-html","aria-hidden":"true"},(0,s.kt)("span",{parentName:"span",className:"base"},(0,s.kt)("span",{parentName:"span",className:"strut",style:{height:"0.8141079999999999em",verticalAlign:"0em"}}),(0,s.kt)("span",{parentName:"span",className:"mord"},(0,s.kt)("span",{parentName:"span",className:"mord mathnormal",style:{marginRight:"0.05764em"}},"S"),(0,s.kt)("span",{parentName:"span",className:"msupsub"},(0,s.kt)("span",{parentName:"span",className:"vlist-t"},(0,s.kt)("span",{parentName:"span",className:"vlist-r"},(0,s.kt)("span",{parentName:"span",className:"vlist",style:{height:"0.8141079999999999em"}},(0,s.kt)("span",{parentName:"span",style:{top:"-3.063em",marginRight:"0.05em"}},(0,s.kt)("span",{parentName:"span",className:"pstrut",style:{height:"2.7em"}}),(0,s.kt)("span",{parentName:"span",className:"sizing reset-size6 size3 mtight"},(0,s.kt)("span",{parentName:"span",className:"mord mtight"},"2")))))))))))),"."))}N.isMDXComponent=!0},9589:(a,e,t)=>{"use strict";t.r(e),t.d(e,{default:()=>n});const n='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // \\begin{model}\n    val n = 1\n    val matrixSpace = SparseMatrixSpaceOverRational\n\n    // define a Sullivan model of the 4n-sphere\n    val sphereIndeterminateList = listOf(\n        Indeterminate("x", 4 * n),\n        Indeterminate("y", 8 * n - 1),\n    )\n    val sphere = FreeDGAlgebra(matrixSpace, sphereIndeterminateList) { (x, _) ->\n        listOf(zeroGVector, x.pow(2))\n    }\n\n    // define a Sullivan model of the product of two 2n-spheres\n    val sphereProductIndeterminateList = listOf(\n        Indeterminate("a1", 2 * n),\n        Indeterminate("b1", 4 * n - 1),\n        Indeterminate("a2", 2 * n),\n        Indeterminate("b2", 4 * n - 1),\n    )\n    val sphereProduct = FreeDGAlgebra(matrixSpace, sphereProductIndeterminateList) { (a1, _, a2, _) ->\n        listOf(zeroGVector, a1.pow(2), zeroGVector, a2.pow(2))\n    }\n    // \\end{model}\n\n    // \\begin{dgaMap}\n    val (x, y) = sphere.generatorList\n    val (a1, b1, a2, b2) = sphereProduct.generatorList\n    val valueList = sphereProduct.context.run {\n        listOf(a1 * a2, a1.pow(2) * b2)\n    }\n    val f = sphere.getDGAlgebraMap(sphereProduct, valueList)\n    sphere.context.run {\n        // This \'context\' is necessary for pow(2) and cohomologyClass()\n        println(f(x)) // a1a2\n        println(f(x.pow(2))) // a1^2a2^2\n        println(f.inducedMapOnCohomology(x.cohomologyClass())) // [a1a2]\n        println(f.inducedMapOnCohomology(x.pow(2).cohomologyClass())) // 0\n    }\n    // \\end{dgaMap}\n}\n'},9798:(a,e,t)=>{"use strict";t.r(e),t.d(e,{default:()=>n});const n='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.model.FreeLoopSpace\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // \\begin{sphere}\n    // Define the Sullivan model of the 4-sphere.\n    val sphereDim = 4\n    val indeterminateList = listOf(\n        Indeterminate("x", sphereDim),\n        Indeterminate("y", sphereDim * 2 - 1)\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->\n        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2\n    }\n    // \\end{sphere}\n\n    // \\begin{freeLoopSpace}\n    // Define the Sullivan model of the free loop space.\n    val freeLoopSpace = FreeLoopSpace(sphere)\n    val (x, y, sx, sy) = freeLoopSpace.generatorList\n    // \\end{freeLoopSpace}\n\n    // \\begin{computation}\n    // Assert that d(sy) and -2*x*sx are the same.\n    freeLoopSpace.context.run {\n        println("dsy = ${d(sy)} = ${-2 * x * sx}")\n    }\n\n    // Compute cohomology of the free loop space.\n    for (degree in 0 until 25) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        println("H^$degree(LS^$sphereDim) = Q$basis")\n    }\n    // \\end{computation}\n\n    // \\begin{freeLoopSpaceWithMultiDegree}\n    val freeLoopSpaceWithMultiDegree = FreeLoopSpace.withShiftDegree(sphere)\n    for (degree in 0 until 25) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        println("H^$degree(LS^$sphereDim) = Q$basis")\n    }\n    // \\end{freeLoopSpaceWithMultiDegree}\n}\n'},2303:(a,e,t)=>{"use strict";t.r(e),t.d(e,{default:()=>n});const n='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate\nimport com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // \\begin{degree}\n    val sphereDim = 2\n    val degreeGroup = MultiDegreeGroup(\n        listOf(\n            DegreeIndeterminate("n", sphereDim / 2),\n            DegreeIndeterminate("m", sphereDim / 2),\n        )\n    )\n    val (n, m) = degreeGroup.generatorList\n    // \\end{degree}\n\n    // \\begin{model}\n    val indeterminateList = degreeGroup.context.run {\n        listOf(\n            Indeterminate("x", 2 * n),\n            Indeterminate("y", 4 * n - 1),\n            Indeterminate("a", 2 * m),\n            Indeterminate("b", 4 * m - 1),\n        )\n    }\n    val matrixSpace = SparseMatrixSpaceOverRational\n    val sphere = FreeDGAlgebra(matrixSpace, degreeGroup, indeterminateList) { (x, y, a, b) ->\n        listOf(zeroGVector, x.pow(2), zeroGVector, a.pow(2))\n    }\n    // \\end{model}\n\n    // \\begin{cohomology}\n    degreeGroup.context.run {\n        println(sphere.cohomology.getBasis(0))\n        println(sphere.cohomology.getBasis(2 * n))\n        println(sphere.cohomology.getBasis(2 * m))\n        println(sphere.cohomology.getBasisForAugmentedDegree(sphereDim))\n    }\n    // \\end{cohomology}\n}\n'},7663:(a,e,t)=>{"use strict";t.r(e),t.d(e,{default:()=>n});const n='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.model.FreeLoopSpace\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\nimport com.github.shwaka.kohomology.util.PrintType\nimport com.github.shwaka.kohomology.util.Printer\nimport com.github.shwaka.kohomology.util.ShowShift\n\nfun main() {\n    // \\begin{def}\n    val indeterminateList = listOf(\n        Indeterminate("a", 2),\n        Indeterminate("b", 2),\n        Indeterminate("x", 3),\n        Indeterminate("y", 3),\n        Indeterminate("z", 3)\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, x, y, z) ->\n        listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))\n    }\n    val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)\n    // \\end{def}\n\n    println("----- plain output -----")\n    // \\begin{plain}\n    for (degree in 0..4) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        println("H^$degree(LX) = Q$basis")\n    }\n    // \\end{plain}\n\n    println("----- tex output -----")\n    // \\begin{tex}\n    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)\n    for (degree in 0..4) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        println("H^{$degree}(LX) &= \\\\Q${basis.map { v -> p(v) }} \\\\\\\\")\n    }\n    // \\end{tex}\n\n    println("----- long tex output -----")\n    // \\begin{long}\n    val p2 = Printer(printType = PrintType.TEX, beforeSign = "\\n", showShift = ShowShift.BAR)\n    for (degree in 0..6) {\n        val basis = freeLoopSpace.cohomology.getBasis(degree)\n        val basisString = basis.joinToString(",\\n") { v -> p2(v) }\n        println("\\\\begin{autobreak}\\nH^{$degree}(LX) = \\\\Q[\\n${basisString}]\\n\\\\end{autobreak}\\\\\\\\")\n    }\n    // \\end{long}\n}\n'},4742:(a,e,t)=>{"use strict";t.r(e),t.d(e,{default:()=>n});const n='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // \\begin{def}\n    val n = 2\n    // Declare an indeterminate (generator) for the free commutative graded algebra \u039b(x,y)\n    val indeterminateList = listOf(\n        Indeterminate("x", 2 * n),\n        Indeterminate("y", 4 * n - 1),\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    // Sullivan algebra can be defined by using the constructor of FreeDGAlgebra.\n    // The last argument is a function\n    // which receives list of generators and returns the list of the values of the differential.\n    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->\n        // zeroGVector is a special element that represents zero in any degree.\n        val dx = zeroGVector\n        // x.pow(2) represents x^2\n        val dy = x.pow(2)\n        listOf(dx, dy)\n    }\n    // \\end{def}\n\n    // \\begin{cohomology}\n    for (degree in 0 until 10) {\n        val basis = sphere.cohomology.getBasis(degree)\n        println("H^$degree(S^${2 * n}) = Q$basis")\n    }\n    // \\end{cohomology}\n\n    // \\begin{context}\n    val (x, y) = sphere.generatorList\n\n    // You can\'t write DGA operations here.\n\n    sphere.context.run {\n        // You can write DGA operations in "context.run"\n        println("d(x * y) = ${d(x * y)}")\n        println(d(x).isZero())\n        println(x.cohomologyClass())\n        println(x.pow(2).cohomologyClass())\n    }\n    // \\end{context}\n}\n'},2632:(a,e,t)=>{"use strict";t.r(e),t.d(e,{default:()=>n});const n='package com.github.shwaka.kohomology.sample\n\nimport com.github.shwaka.kohomology.free.FreeDGAlgebra\nimport com.github.shwaka.kohomology.free.monoid.Indeterminate\nimport com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational\n\nfun main() {\n    // \\begin\n    val indeterminateList = listOf(\n        Indeterminate("a", 2),\n        Indeterminate("b", 2),\n        Indeterminate("x", 3),\n        Indeterminate("y", 3),\n        Indeterminate("z", 3)\n    )\n    val matrixSpace = SparseMatrixSpaceOverRational\n    val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, x, y, z) ->\n        val da = zeroGVector // da = 0\n        val db = zeroGVector // db = 0\n        val dx = a.pow(2) // dx = a^2\n        val dy = a * b // dy = ab\n        val dz = b.pow(2) // dz = b^2\n        listOf(da, db, dx, dy, dz)\n    }\n    for (degree in 0 until 10) {\n        val basis = freeDGAlgebra.cohomology.getBasis(degree)\n        println("H^$degree = Q$basis")\n    }\n    // \\end\n}\n'},5284:(a,e,t)=>{var n={"./DGAlgebraMap.kt":9589,"./FreeLoopSpace.kt":9798,"./MultiDegree.kt":2303,"./PrintTex.kt":7663,"./SphereModel.kt":4742,"./TopPageExample.kt":2632};function s(a){var e=m(a);return t(e)}function m(a){if(!t.o(n,a)){var e=new Error("Cannot find module '"+a+"'");throw e.code="MODULE_NOT_FOUND",e}return n[a]}s.keys=function(){return Object.keys(n)},s.resolve=m,a.exports=s,s.id=5284}}]);