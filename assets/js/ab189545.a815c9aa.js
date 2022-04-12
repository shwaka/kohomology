"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[185],{4743:function(e,t,n){n.r(t),n.d(t,{default:function(){return M}});var a=n(2600),r=n(7294),o=n(1262),c=n(3811);function u(){return new Worker(n.p+"assets/js/kohomology.worker.da22607b.worker.js")}function s(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return e%2==0?'[\n  ["x", '+e+', "zero"],\n  ["y", '+(2*e-1)+', "x^2"]\n]':'[\n  ["x", '+e+', "zero"]\n]'}var l="messageSuccess_O5B5",i="messageError_Fj2V",m="calculator_V339",p="jsonEditor_jGP_",f="calculatorForm_b40K",g="usage_whbz",h="computeCohomology_s7wV",v="maxDegree_b1Ef",E="calculatorResults_rJuI";function d(e){var t=(0,r.useState)(e.json),n=t[0],a=t[1];function o(e,t){return r.createElement("input",{type:"button",value:e,onClick:function(){return a(t)}})}return r.createElement("div",{className:p},o("S^2",s(2)),o("CP^3",function(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return'[\n  ["c", 2, "zero"],\n  ["x", '+(2*e+1)+', "c^'+(e+1)+'"]\n]'}(3)),o("7-mfd",'[\n  ["a", 2, "zero"],\n  ["b", 2, "zero"],\n  ["x", 3, "a^2"],\n  ["y", 3, "a*b"],\n  ["z", 3, "b^2"]\n]'),r.createElement("textarea",{value:n,onChange:function(e){a(e.target.value)}}),r.createElement("input",{type:"button",value:"Apply",onClick:function(){e.updateDgaWrapper(n),e.finish()}}),r.createElement("input",{type:"button",value:"Cancel",onClick:function(){e.finish()}}))}var b=n(3117),y=n(102),k=n(3905),w=["components"],C={toc:[]};function S(e){var t=e.components,n=(0,y.Z)(e,w);return(0,k.kt)("wrapper",(0,b.Z)({},C,n,{components:t,mdxType:"MDXLayout"}),(0,k.kt)("p",null,'Input a Sullivan algebra from the "Edit DGA" button'),(0,k.kt)("p",null,"This calculator can compute the cohomology of the Sullivan algebra, its Hochschild homology (cohomology of the free loop space), and its cyclic homology (equivariant cohomology of the free loop space)."),(0,k.kt)("p",null,'Note that "u" cannot be used as a generator when "cyclic" is selected, since "u" is reserved for the model of the cyclic homology.'))}S.isMDXComponent=!0;var N=["self","freeLoopSpace","cyclic"];function x(e,t){var n;switch(void 0===t&&(t=0),e.messageType){case"success":n=l;break;case"error":n=i}return r.createElement("div",{key:t,className:n},e.strings.map((function(e,t){return function(e,t){switch(e.stringType){case"text":return r.createElement("span",{key:t},e.content);case"math":return r.createElement(c.Z,{key:t,math:e.content,settings:{output:"html",macros:{"\\deg":"|#1|"}}})}}(e,t)})))}function D(e){var t=(0,r.useState)("20"),n=t[0],a=t[1],o=(0,r.useState)("x^2"),c=o[0],l=o[1],i=(0,r.useState)(s(2)),m=i[0],p=i[1],E=(0,r.useState)(!1),b=E[0],y=E[1],k=(0,r.useState)("self"),w=k[0],C=k[1],D=(0,r.useState)([]),_=D[0],M=D[1],j=(0,r.useRef)(new u).current;j.onmessage=function(t){var n=t.data;switch(n.command){case"printMessages":e.printMessages(n.messages);break;case"showDgaInfo":M(n.messages)}};var T=(0,r.useCallback)((function(e){e.preventDefault();var t={command:"computeCohomology",targetName:w,maxDegree:parseInt(n)};j.postMessage(t)}),[w,n,j]),z=(0,r.useCallback)((function(e){e.preventDefault();var t={command:"computeCohomologyClass",targetName:w,cocycleString:c};j.postMessage(t)}),[w,c,j]),I=(0,r.useCallback)((function(e){var t={command:"updateJson",json:e};j.postMessage(t);j.postMessage({command:"dgaInfo"})}),[j]);(0,r.useEffect)((function(){I(m)}),[m,I]);var Z=(0,r.useCallback)((function(e){a(e.target.value)}),[]);return r.createElement("div",{className:f},r.createElement("details",null,r.createElement("summary",null,"Usage"),r.createElement("div",{className:g},r.createElement(S,null))),r.createElement("div",null,_.map((function(e,t){return x(e,t)}))),r.createElement("input",{type:"button",value:"Edit DGA",onClick:function(){return y(!0)}}),b&&r.createElement(d,{json:m,updateDgaWrapper:p,finish:function(){return y(!1)}}),r.createElement("div",null,N.map((function(e,t){return r.createElement("label",{key:t},r.createElement("input",{type:"radio",name:"targetName",value:e,checked:e===w,onChange:function(){return C(e)}}),e)}))),r.createElement("form",{className:h,onSubmit:T},r.createElement("input",{type:"submit",value:"Compute cohomology"}),r.createElement("span",null,"up to degree"),r.createElement("input",{type:"number",value:n,onChange:Z,min:0,className:v})),r.createElement("form",{className:h,onSubmit:z},r.createElement("input",{type:"submit",value:"Compute class"}),r.createElement("span",null,"cocycle:"),r.createElement("input",{type:"text",value:c,onChange:function(e){return l(e.target.value)},onSubmit:z})))}function _(){var e={messageType:"success",strings:[{stringType:"text",content:"Computation results will be shown here"}]},t=(0,r.useState)([e]),n=t[0],a=t[1],c=(0,r.useRef)(null);function u(e){a(e instanceof Array?function(t){return t.concat(e)}:function(t){return t.concat([e])})}return(0,r.useEffect)((function(){var e;null!==(e=c.current)&&setTimeout((function(){e.scrollTo({top:e.scrollHeight,behavior:"smooth"})}))}),[n]),r.createElement("div",{className:m},r.createElement(o.Z,{fallback:r.createElement("div",null,"Loading...")},(function(){return r.createElement(D,{printMessages:u})})),r.createElement("div",{className:E,ref:c},n.map((function(e,t){return x(e,t)}))))}function M(){return r.createElement(a.Z,{title:"Calculator"},r.createElement(_,null))}}}]);