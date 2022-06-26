"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[185],{6698:function(e,t,n){n.r(t),n.d(t,{default:function(){return ge}});var r=n(2600),a=n(7294),o=n(1262),l=n(1265),c=n(1927),s=n(3811),u=n(3156),i=n(6447),m=n(1519),f=n(3321),g=n(2890),p=n(2699),d=n(9033);function h(){return new Worker(n.p+"assets/js/kohomology.worker.999c1f83.worker.js")}var E=["self","freeLoopSpace","cyclic","derivation"],v=["basis","dim"],b=n(5568),y=n(9368),w=n(1703),Z=n(44),C=n(4687);function k(e){var t=e.label,n=e.value,r=e.setValue,o=e.width,l=e.disabled;return a.createElement(C.Z,{label:t,value:n,type:"number",onChange:function(e){return r(parseInt(e.target.value))},sx:{width:o},size:"small",disabled:l})}function S(e){var t=e.label,n=e.defaultValue,r=e.width,o=e.disabled,l=(0,a.useState)(n),c=l[0];return[c,{label:t,value:c,setValue:l[1],width:void 0!==r?r:70,disabled:void 0!==o&&o}]}function x(e){var t=e.label,n=e.value,r=e.setValue,o=e.width,l=e.disabled;return a.createElement(C.Z,{label:t,value:n,onChange:function(e){return r(e.target.value)},sx:{width:o},size:"small",disabled:l})}function N(e){switch(e){case"self":return"\\wedge V";case"freeLoopSpace":return"\\wedge V \\otimes \\wedge \\overline{V}";case"cyclic":return"\\wedge u \\otimes\\wedge V \\otimes \\wedge \\overline{V}";case"derivation":return"\\mathrm{Der}(\\wedge V)"}}function T(e){var t=e.targetName;return a.createElement(s.Z,{math:N(t)})}function D(e,t){return void 0===t&&(t=void 0),"H^{"+(void 0!==t?t:"*")+"}("+N(e)+")"}function V(e){var t=e.targetName,n=e.degree;return a.createElement(s.Z,{math:D(t,n)})}function M(e){var t=e.targetName,n=e.postMessageToWorker,r=e.visible,o=S({label:"",defaultValue:0}),l=o[0],c=o[1],u=S({label:"",defaultValue:20}),m=u[0],h=u[1],E=(0,a.useState)("basis"),y=E[0],w=E[1],Z=(0,a.useCallback)((function(){n({command:"computeCohomology",targetName:t,minDegree:l,maxDegree:m,showCohomology:y})}),[t,l,m,y,n]);if(!r)return a.createElement(a.Fragment,null);var C=!z(t,"cohomology");return a.createElement(i.Z,{spacing:1},a.createElement("span",null,"Compute cohomology ",a.createElement(V,{targetName:t,degree:"n"})," for"),a.createElement(i.Z,{direction:"row",alignItems:"center",justifyContent:"center",spacing:1},a.createElement(k,c),a.createElement(s.Z,{math:"\\leq n \\leq"}),a.createElement(k,h)),a.createElement(g.Z,{row:!0,value:y,onChange:function(e){return w(e.target.value)}},v.map((function(e){return a.createElement(p.Z,{key:e,value:e,control:a.createElement(d.Z,null),label:e})}))),a.createElement(f.Z,{onClick:Z,variant:"contained",disabled:C},"Compute"),C&&a.createElement(b.Z,{severity:"info"},"Currently, this type of computation is not supported."))}function O(e){var t=e.targetName,n=e.postMessageToWorker,r=e.visible,o=!z(t,"class"),l=function(e){var t=e.label,n=e.defaultValue,r=e.width,o=e.disabled,l=(0,a.useState)(n),c=l[0];return[c,{label:t,value:c,setValue:l[1],width:void 0!==r?r:70,disabled:void 0!==o&&o}]}({label:"",defaultValue:"x^2",width:200,disabled:o}),c=l[0],u=l[1],m=(0,a.useState)(!0),g=m[0],d=m[1],h=(0,a.useCallback)((function(){n({command:"computeCohomologyClass",targetName:t,cocycleString:c,showBasis:g})}),[t,c,g,n]);return r?a.createElement(i.Z,{spacing:1},a.createElement("span",null,"Compute cohomology class ",a.createElement(s.Z,{math:"[\\omega] \\in "+D(t)})," for"),a.createElement(i.Z,{direction:"row",alignItems:"center",justifyContent:"center",spacing:1},a.createElement(s.Z,{math:"\\omega ="}),a.createElement(x,u)),a.createElement(p.Z,{control:a.createElement(y.Z,null),label:"Show basis",checked:g,onChange:function(e){return d(e.target.checked)}}),a.createElement(f.Z,{onClick:h,variant:"contained",disabled:o},"Compute"),o&&a.createElement(b.Z,{severity:"info"},"Currently, this type of computation is not supported.")):a.createElement(a.Fragment,null)}function z(e,t){switch(e){case"self":case"freeLoopSpace":case"cyclic":return!0;case"derivation":switch(t){case"cohomology":return!0;case"class":return!1}}}function I(e){var t=e.targetName,n=e.postMessageToWorker,r=(0,a.useState)("cohomology"),o=r[0],l=r[1];return a.createElement(a.Fragment,null,a.createElement(w.Z,{value:o,onChange:function(e,t){l(t)}},a.createElement(Z.Z,{value:"cohomology",label:"Cohomology group",sx:{textTransform:"none"}}),a.createElement(Z.Z,{value:"class",label:"Cohomology class",sx:{textTransform:"none"}})),a.createElement(M,{targetName:t,postMessageToWorker:n,visible:"cohomology"===o}),a.createElement(O,{targetName:t,postMessageToWorker:n,visible:"class"===o}))}var J=n(8262),P=n(9581),A=n(1425);function W(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return e%2==0?'[\n  ["x", '+e+', "zero"],\n  ["y", '+(2*e-1)+', "x^2"]\n]':'[\n  ["x", '+e+', "zero"]\n]'}function G(e){var t=(0,a.useState)(e.json),n=t[0],r=t[1];function o(e,t){return a.createElement("input",{type:"button",value:e,onClick:function(){return r(t)}})}return a.createElement(J.Z,{open:e.isOpen,onClose:e.finish,maxWidth:"sm",fullWidth:!0},a.createElement(P.Z,null,a.createElement(i.Z,{spacing:2},a.createElement(C.Z,{label:"Input your DGA",multiline:!0,value:n,onChange:function(e){r(e.target.value)}}),a.createElement("div",null,"Examples: ",o("S^2",W(2)),o("CP^3",function(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return'[\n  ["c", 2, "zero"],\n  ["x", '+(2*e+1)+', "c^'+(e+1)+'"]\n]'}(3)),o("7-mfd",'[\n  ["a", 2, "zero"],\n  ["b", 2, "zero"],\n  ["x", 3, "a^2"],\n  ["y", 3, "a*b"],\n  ["z", 3, "b^2"]\n]')))),a.createElement(A.Z,null,a.createElement(f.Z,{onClick:function(){e.updateDgaWrapper(n),e.finish()}},"Apply"),a.createElement(f.Z,{onClick:function(){e.finish()}},"Cancel")))}var L=n(4996),j=n(2263),R=n(6893),B=n(6775);function F(e){var t=e.dgaJson,n=new URLSearchParams,r=function(e){try{var t=JSON.parse(e);return JSON.stringify(t,null,void 0)}catch(n){if(n instanceof SyntaxError)return null;throw n}}(t);return null===r?null:(n.append("dgaJson",r),n)}function _(){var e,t=(e=(0,B.TH)().search,(0,a.useMemo)((function(){return new URLSearchParams(e)}),[e])),n=W(2);try{var r=t.get("dgaJson");return null!==r?function(e){return"[\n"+JSON.parse(e).map((function(e){return'  ["'+e[0]+'", '+e[1]+', "'+e[2]+'"]'})).join(",\n")+"\n]"}(r):n}catch(o){if(o instanceof SyntaxError)return console.log("[Error] Invalid JSON is given as URL parameter."),console.log(o),n;throw o}}function H(e){var t=e.setOpen;return a.createElement(f.Z,{onClick:function(){return t(!0)},sx:{textTransform:"none"},variant:"outlined",size:"small"},"Share this DGA")}function q(e){var t=e.text,n=(0,a.useState)(!1),r=n[0],o=n[1];return a.createElement(R.Z,{title:"Copied",open:r,onClose:function(){return o(!1)}},a.createElement(f.Z,{onClick:function(){navigator.clipboard.writeText(t),o(!0)},variant:"contained",size:"small"},"copy"))}function U(e){var t=e.open,n=e.setOpen,r=F({dgaJson:e.dgaJson}),o=(0,j.Z)().siteConfig.url,l=(0,L.Z)("calculator"),c=null!==r?""+o+l+"?"+r.toString():"Error";return a.createElement(J.Z,{open:t,onClose:function(){return n(!1)}},a.createElement(P.Z,null,a.createElement(C.Z,{label:"url",value:c,sx:{width:300},size:"small",InputProps:{readOnly:!0},multiline:!0}),a.createElement(q,{text:c})),a.createElement(A.Z,null,a.createElement(f.Z,{onClick:function(){return n(!1)}},"Close")))}var X=n(3117),Y=n(102),K=n(3905),Q=["components"],$={toc:[]};function ee(e){var t=e.components,n=(0,Y.Z)(e,Q);return(0,K.kt)("wrapper",(0,X.Z)({},$,n,{components:t,mdxType:"MDXLayout"}),(0,K.kt)("p",null,'Input a Sullivan algebra from the "Edit DGA" button'),(0,K.kt)("p",null,"This calculator can compute the cohomology of the Sullivan algebra, its Hochschild homology (cohomology of the free loop space), and its cyclic homology (equivariant cohomology of the free loop space)."),(0,K.kt)("p",null,'Note that "u" cannot be used as a generator when "cyclic" is selected, since "u" is reserved for the model of the cyclic homology.'))}function te(e){var t=e.setOpen;return a.createElement(f.Z,{onClick:function(){return t(!0)},sx:{textTransform:"none"},variant:"outlined",size:"small"},"Show usage")}function ne(e){var t=e.open,n=e.setOpen;return a.createElement(J.Z,{open:t,onClose:function(){return n(!1)}},a.createElement(P.Z,null,a.createElement(ee,null)),a.createElement(A.Z,null,a.createElement(f.Z,{onClick:function(){return n(!1)}},"Close")))}ee.isMDXComponent=!0;var re="messageSuccess_wRYA",ae="messageError__Y6B";function oe(e){var t=e.content.split("\n");return a.createElement("span",null,t.map((function(e,n){return n<t.length-1?a.createElement(a.Fragment,{key:n},e,a.createElement("br",null)):a.createElement(a.Fragment,{key:n},e)})))}function le(e,t){var n;switch(void 0===t&&(t=0),e.messageType){case"success":n=re;break;case"error":n=ae}return a.createElement("div",{key:t,className:n},e.strings.map((function(e,t){return function(e,t){switch(e.stringType){case"text":return a.createElement(oe,{key:t,content:e.content});case"math":return a.createElement(s.Z,{key:t,math:e.content,settings:{output:"html",macros:{"\\deg":"|#1|"}}})}}(e,t)})))}function ce(e){var t=e.children;return a.createElement(u.Z,{disableGutters:!0,sx:{paddingLeft:1,paddingRight:1}},t)}function se(e){var t=_(),n=(0,a.useState)(t),r=n[0],o=n[1],l=function(){var e=(0,a.useState)(!1),t=e[0],n=e[1];return{usageDialogProps:{open:t,setOpen:n},usageButtonProps:{setOpen:n}}}(),c=l.usageDialogProps,s=l.usageButtonProps,u=function(e){var t=(0,a.useState)(!1),n=t[0],r=t[1];return{shareDGADialogProps:{open:n,setOpen:r,dgaJson:e},shareDGAButtonProps:{setOpen:r}}}(r),v=u.shareDGADialogProps,b=u.shareDGAButtonProps,y=(0,a.useState)(!1),w=y[0],Z=y[1],C=(0,a.useState)("self"),k=C[0],S=C[1],x=(0,a.useState)([]),N=x[0],D=x[1],V=(0,a.useRef)(new h).current;V.onmessage=function(t){var n=t.data;switch(n.command){case"printMessages":e.printMessages(n.messages);break;case"showDgaInfo":D(n.messages)}};var M=(0,a.useCallback)((function(e){var t={command:"updateJson",json:e};V.postMessage(t);V.postMessage({command:"dgaInfo"})}),[V]);return(0,a.useEffect)((function(){M(r)}),[r,M]),a.createElement(i.Z,{direction:"column",spacing:2,divider:a.createElement(m.Z,{orientation:"horizontal"}),sx:{width:400,margin:1}},a.createElement(ce,null,a.createElement(te,s),a.createElement(ne,c)),a.createElement(ce,null,a.createElement("div",null,N.map((function(e,t){return le(e,t)}))),a.createElement(i.Z,{direction:"row",spacing:2,sx:{marginTop:.5}},a.createElement(f.Z,{variant:"contained",size:"small",onClick:function(){return Z(!0)},sx:{textTransform:"none"}},"Edit DGA"),a.createElement(G,{json:r,updateDgaWrapper:o,finish:function(){return Z(!1)},isOpen:w}),a.createElement(H,b),a.createElement(U,v))),a.createElement(ce,null,a.createElement(g.Z,{row:!0,value:k,onChange:function(e){return S(e.target.value)}},E.map((function(e){return a.createElement(p.Z,{key:e,value:e,control:a.createElement(d.Z,null),label:e})}))),"Computation target: ",a.createElement(T,{targetName:k})),a.createElement(ce,null,a.createElement(I,{targetName:k,postMessageToWorker:function(e){return V.postMessage(e)}})))}var ue="calculator_V339",ie="calculatorResults_rJuI";var me=(0,l.Z)({palette:{primary:{main:"#7e6ca8"}}});function fe(){var e={messageType:"success",strings:[{stringType:"text",content:"Computation results will be shown here"}]},t=(0,a.useState)([e]),n=t[0],r=t[1],l=(0,a.useRef)(null);function s(e){r(e instanceof Array?function(t){return t.concat(e)}:function(t){return t.concat([e])})}return(0,a.useEffect)((function(){var e;null!==(e=l.current)&&setTimeout((function(){e.scrollTo({top:e.scrollHeight,behavior:"smooth"})}))}),[n]),a.createElement(c.Z,{theme:me},a.createElement("div",{className:ue},a.createElement(o.Z,{fallback:a.createElement("div",null,"Loading...")},(function(){return a.createElement(se,{printMessages:s})})),a.createElement("div",{className:ie,ref:l},n.map((function(e,t){return le(e,t)})))))}function ge(){return a.createElement(r.Z,{title:"Calculator"},a.createElement(fe,null))}}}]);