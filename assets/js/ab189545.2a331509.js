"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[185],{7650:(e,t,n)=>{n.r(t),n.d(t,{default:()=>ge});var a=n(2600),r=n(7294),l=n(1262),o=n(1265),s=n(1927),c=n(3156),i=n(6447),u=n(1519),m=n(3321),d=n(2890),g=n(2699),p=n(9033);function E(){return new Worker(n.p+"assets/js/kohomology.worker.89d14305.worker.js")}var h=n(3811);function f(e){return e.strings.map((e=>e.content)).join("")}const v="messageSuccess__4xu",y="messageError_Skkc";function b(e){let{content:t}=e;const n=t.split("\n");return r.createElement("span",null,n.map(((e,t)=>t<n.length-1?r.createElement(r.Fragment,{key:t},e,r.createElement("br",null)):r.createElement(r.Fragment,{key:t},e))))}function w(e){let{styledString:t}=e;const n={"\\deg":"|#1|"};switch(t.stringType){case"text":return r.createElement(b,{content:t.content});case"math":return r.createElement(h.Z,{math:t.content,settings:{output:"html",macros:n}})}}function Z(e){let t,{styledMessage:n}=e;switch(n.messageType){case"success":t=v;break;case"error":t=y}return r.createElement("div",{className:t,"data-styled-message":f(n)},n.strings.map(((e,t)=>r.createElement(w,{styledString:e,key:t}))))}const C=["self","freeLoopSpace","cyclic","derivation"],k=["basis","dim"];var S=n(5568),x=n(9368),T=n(1703),D=n(44),N=n(4687);function M(e){let{label:t,value:n,setValue:a,width:l,disabled:o}=e;return r.createElement(N.Z,{label:t,value:n,type:"number",onChange:e=>a(parseInt(e.target.value)),sx:{width:l},size:"small",disabled:o})}function V(e){let{label:t,defaultValue:n,width:a,disabled:l}=e;const[o,s]=(0,r.useState)(n);return[o,{label:t,value:o,setValue:s,width:void 0!==a?a:70,disabled:void 0!==l&&l}]}function O(e){let{label:t,value:n,setValue:a,width:l,disabled:o}=e;return r.createElement(N.Z,{label:t,value:n,onChange:e=>a(e.target.value),sx:{width:l},size:"small",disabled:o})}function z(e){switch(e){case"self":return"\\wedge V";case"freeLoopSpace":return"\\wedge V \\otimes \\wedge \\overline{V}";case"cyclic":return"\\wedge u \\otimes\\wedge V \\otimes \\wedge \\overline{V}";case"derivation":return"\\mathrm{Der}(\\wedge V)"}}function I(e){let{targetName:t}=e;return r.createElement(h.Z,{math:z(t),"data-testid":"ComplexAsTex"})}function J(e,t){void 0===t&&(t=void 0);return"H^{"+(void 0!==t?t:"*")+"}("+z(e)+")"}function P(e){let{targetName:t,degree:n}=e;return r.createElement(h.Z,{math:J(t,n)})}function A(e){let{targetName:t,postMessageToWorker:n,visible:a}=e;const[l,o]=V({label:"",defaultValue:0}),[s,c]=V({label:"",defaultValue:20}),[u,E]=(0,r.useState)("basis"),f=(0,r.useCallback)((()=>{n({command:"computeCohomology",targetName:t,minDegree:l,maxDegree:s,showCohomology:u})}),[t,l,s,u,n]);if(!a)return r.createElement(r.Fragment,null);const v=!j(t,"cohomology");return r.createElement("div",{"data-testid":"ComputeCohomologyForm"},r.createElement(i.Z,{spacing:1},r.createElement("span",null,"Compute cohomology ",r.createElement(P,{targetName:t,degree:"n"})," for"),r.createElement(i.Z,{direction:"row",alignItems:"center",justifyContent:"center",spacing:1},r.createElement(M,o),r.createElement(h.Z,{math:"\\leq n \\leq"}),r.createElement(M,c)),r.createElement(d.Z,{row:!0,value:u,onChange:e=>E(e.target.value)},k.map((e=>r.createElement(g.Z,{key:e,value:e,control:r.createElement(p.Z,null),label:e})))),r.createElement(m.Z,{onClick:f,variant:"contained",disabled:v},"Compute"),v&&r.createElement(S.Z,{severity:"info"},"Currently, this type of computation is not supported.")))}function W(e){let{targetName:t,postMessageToWorker:n,visible:a}=e;const l=!j(t,"class"),[o,s]=function(e){let{label:t,defaultValue:n,width:a,disabled:l}=e;const[o,s]=(0,r.useState)(n);return[o,{label:t,value:o,setValue:s,width:void 0!==a?a:70,disabled:void 0!==l&&l}]}({label:"",defaultValue:"x^2",width:200,disabled:l}),[c,u]=(0,r.useState)(!0),d=(0,r.useCallback)((()=>{n({command:"computeCohomologyClass",targetName:t,cocycleString:o,showBasis:c})}),[t,o,c,n]);return a?r.createElement("div",{"data-testid":"ComputeClassForm"},r.createElement(i.Z,{spacing:1},r.createElement("span",null,"Compute cohomology class ",r.createElement(h.Z,{math:"[\\omega] \\in "+J(t)})," for"),r.createElement(i.Z,{direction:"row",alignItems:"center",justifyContent:"center",spacing:1},r.createElement(h.Z,{math:"\\omega ="}),r.createElement(O,s)),r.createElement(g.Z,{control:r.createElement(x.Z,null),label:"Show basis",checked:c,onChange:e=>u(e.target.checked)}),r.createElement(m.Z,{onClick:d,variant:"contained",disabled:l},"Compute"),l&&r.createElement(S.Z,{severity:"info"},"Currently, this type of computation is not supported."))):r.createElement(r.Fragment,null)}function j(e,t){switch(e){case"self":case"freeLoopSpace":case"cyclic":return!0;case"derivation":switch(t){case"cohomology":return!0;case"class":return!1}}}function G(e){let{targetName:t,postMessageToWorker:n}=e;const[a,l]=(0,r.useState)("cohomology");return r.createElement(r.Fragment,null,r.createElement(T.Z,{value:a,onChange:(e,t)=>{l(t)}},r.createElement(D.Z,{value:"cohomology",label:"Cohomology group",sx:{textTransform:"none"}}),r.createElement(D.Z,{value:"class",label:"Cohomology class",sx:{textTransform:"none"}})),r.createElement(A,{targetName:t,postMessageToWorker:n,visible:"cohomology"===a}),r.createElement(W,{targetName:t,postMessageToWorker:n,visible:"class"===a}))}var L=n(8262),F=n(9581),R=n(1425);function B(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return e%2==0?'[\n  ["x", '+e+', "zero"],\n  ["y", '+(2*e-1)+', "x^2"]\n]':'[\n  ["x", '+e+', "zero"]\n]'}function _(e){const[t,n]=(0,r.useState)(e.json);function a(e,t){return r.createElement("input",{type:"button",value:e,onClick:()=>n(t)})}return r.createElement(L.Z,{open:e.isOpen,onClose:e.finish,maxWidth:"sm",fullWidth:!0},r.createElement(F.Z,null,r.createElement(i.Z,{spacing:2},r.createElement(N.Z,{label:"Input your DGA",multiline:!0,value:t,onChange:function(e){n(e.target.value)},inputProps:{"data-testid":"JsonEditorDialog-input-json"}}),r.createElement("div",null,"Examples: ",a("S^2",B(2)),a("CP^3",function(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return'[\n  ["c", 2, "zero"],\n  ["x", '+(2*e+1)+', "c^'+(e+1)+'"]\n]'}(3)),a("7-mfd",'[\n  ["a", 2, "zero"],\n  ["b", 2, "zero"],\n  ["x", 3, "a^2"],\n  ["y", 3, "a*b"],\n  ["z", 3, "b^2"]\n]')))),r.createElement(R.Z,null,r.createElement(m.Z,{onClick:()=>{e.updateDgaWrapper(t),e.finish()}},"Apply"),r.createElement(m.Z,{onClick:()=>{e.finish()}},"Cancel")))}var H=n(4996),q=n(2263),U=n(6893),X=n(6775);function K(e){let{dgaJson:t}=e;const n=new URLSearchParams,a=function(e){try{const t=JSON.parse(e);return JSON.stringify(t,null,void 0)}catch(t){if(t instanceof SyntaxError)return null;throw t}}(t);return null===a?null:(n.append("dgaJson",a),n)}function Q(){const e=function(){const{search:e}=(0,X.TH)();return(0,r.useMemo)((()=>new URLSearchParams(e)),[e])}(),t=B(2);try{const n=e.get("dgaJson");return null!==n?function(e){return"[\n"+JSON.parse(e).map((e=>'  ["'+e[0]+'", '+e[1]+', "'+e[2]+'"]')).join(",\n")+"\n]"}(n):t}catch(n){if(n instanceof SyntaxError)return console.log("[Error] Invalid JSON is given as URL parameter."),console.log(n),t;throw n}}function Y(e){let{setOpen:t}=e;return r.createElement(m.Z,{onClick:()=>t(!0),sx:{textTransform:"none"},variant:"outlined",size:"small"},"Share this DGA")}function $(e){let{text:t}=e;const[n,a]=(0,r.useState)(!1);return r.createElement(U.Z,{title:"Copied",open:n,onClose:()=>a(!1)},r.createElement(m.Z,{onClick:()=>{navigator.clipboard.writeText(t),a(!0)},variant:"contained",size:"small"},"copy"))}function ee(e){let{open:t,setOpen:n,dgaJson:a}=e;const l=K({dgaJson:a}),o=(0,q.Z)().siteConfig.url,s=(0,H.Z)("calculator"),c=null!==l?""+o+s+"?"+l.toString():"Error";return r.createElement(L.Z,{open:t,onClose:()=>n(!1)},r.createElement(F.Z,null,r.createElement(N.Z,{label:"url",value:c,sx:{width:300},size:"small",InputProps:{readOnly:!0},multiline:!0}),r.createElement($,{text:c})),r.createElement(R.Z,null,r.createElement(m.Z,{onClick:()=>n(!1)},"Close")))}var te=n(3117),ne=n(3905);const ae={toc:[]};function re(e){let{components:t,...n}=e;return(0,ne.kt)("wrapper",(0,te.Z)({},ae,n,{components:t,mdxType:"MDXLayout"}),(0,ne.kt)("p",null,'Input a Sullivan algebra from the "Edit DGA" button'),(0,ne.kt)("p",null,"This calculator can compute the cohomology of the Sullivan algebra, its Hochschild homology (cohomology of the free loop space), and its cyclic homology (equivariant cohomology of the free loop space)."),(0,ne.kt)("p",null,'Note that "u" cannot be used as a generator when "cyclic" is selected, since "u" is reserved for the model of the cyclic homology.'))}function le(e){let{setOpen:t}=e;return r.createElement(m.Z,{onClick:()=>t(!0),sx:{textTransform:"none"},variant:"outlined",size:"small"},"Show usage")}function oe(e){let{open:t,setOpen:n}=e;return r.createElement(L.Z,{open:t,onClose:()=>n(!1)},r.createElement(F.Z,null,r.createElement(re,null)),r.createElement(R.Z,null,r.createElement(m.Z,{onClick:()=>n(!1)},"Close")))}function se(e){let{children:t,"data-testid":n}=e;return r.createElement("span",{"data-testid":n},r.createElement(c.Z,{disableGutters:!0,sx:{paddingLeft:1,paddingRight:1}},t))}function ce(e){const t=Q(),[n,a]=(0,r.useState)(t),{usageDialogProps:l,usageButtonProps:o}=function(){const[e,t]=(0,r.useState)(!1);return{usageDialogProps:{open:e,setOpen:t},usageButtonProps:{setOpen:t}}}(),{shareDGADialogProps:s,shareDGAButtonProps:c}=function(e){const[t,n]=(0,r.useState)(!1);return{shareDGADialogProps:{open:t,setOpen:n,dgaJson:e},shareDGAButtonProps:{setOpen:n}}}(n),[h,f]=(0,r.useState)(!1),[v,y]=(0,r.useState)("self"),[b,w]=(0,r.useState)([]),k=(0,r.useRef)(new E).current;k.onmessage=t=>{const n=t.data;switch(n.command){case"printMessages":e.printMessages(n.messages);break;case"showDgaInfo":w(n.messages)}};const S=(0,r.useCallback)((e=>{const t={command:"updateJson",json:e};k.postMessage(t);k.postMessage({command:"dgaInfo"})}),[k]);return(0,r.useEffect)((()=>{S(n)}),[n,S]),r.createElement(i.Z,{direction:"column",spacing:2,divider:r.createElement(u.Z,{orientation:"horizontal"}),sx:{width:400,margin:1}},r.createElement(se,null,r.createElement(le,o),r.createElement(oe,l)),r.createElement(se,{"data-testid":"CalculatorForm-StackItem-DGA"},r.createElement("div",null,b.map(((e,t)=>r.createElement(Z,{styledMessage:e,key:t})))),r.createElement(i.Z,{direction:"row",spacing:2,sx:{marginTop:.5}},r.createElement(m.Z,{variant:"contained",size:"small",onClick:()=>f(!0),sx:{textTransform:"none"}},"Edit DGA"),r.createElement(_,{json:n,updateDgaWrapper:a,finish:()=>f(!1),isOpen:h}),r.createElement(Y,c),r.createElement(ee,s))),r.createElement(se,null,r.createElement(d.Z,{row:!0,value:v,onChange:e=>y(e.target.value)},C.map((e=>r.createElement(g.Z,{key:e,value:e,control:r.createElement(p.Z,null),label:e})))),"Computation target: ",r.createElement(I,{targetName:v})),r.createElement(se,null,r.createElement(G,{targetName:v,postMessageToWorker:e=>k.postMessage(e)})))}re.isMDXComponent=!0;const ie="calculator_V339",ue="calculatorResults_rJuI",me=(0,o.Z)({palette:{primary:{main:"#7e6ca8"}}});function de(){const e={messageType:"success",strings:[{stringType:"text",content:"Computation results will be shown here"}]};const[t,n]=(0,r.useState)([e]),a=(0,r.useRef)(null);function o(e){n(e instanceof Array?t=>t.concat(e):t=>t.concat([e]))}return(0,r.useEffect)((()=>{!function(){const e=a.current;null!==e&&void 0!==e.scrollTo&&setTimeout((()=>{e.scrollTo({top:e.scrollHeight,behavior:"smooth"})}))}()}),[t]),r.createElement(s.Z,{theme:me},r.createElement("div",{className:ie,"data-testid":"Calculator"},r.createElement(l.Z,{fallback:r.createElement("div",null,"Loading...")},(()=>r.createElement(ce,{printMessages:o}))),r.createElement("div",{className:ue,ref:a,"data-testid":"calculator-results"},t.map(((e,t)=>r.createElement(Z,{styledMessage:e,key:t}))))))}function ge(){return r.createElement(a.Z,{title:"Calculator"},r.createElement(de,null))}}}]);