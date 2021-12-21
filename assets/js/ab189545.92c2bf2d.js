"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[185],{219:function(e,t,n){n.r(t),n.d(t,{default:function(){return k}});var r=n(4814),a=n(7294),u=n(1262),o=n(3811);function c(){return new Worker(n.p+"assets/js/kohomology.worker.e1b27e79.worker.js")}function s(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return e%2==0?'[\n  ["x", '+e+', "zero"],\n  ["y", '+(2*e-1)+', "x^2"]\n]':'[\n  ["x", '+e+', "zero"]\n]'}var i="messageSuccess_vDVl",l="messageError_4uJX",m="calculator_B-Kn",f="jsonEditor_ZVI7",p="calculatorForm_O8AN",g="computeCohomology_V+jn",v="maxDegree_GgmO",E="calculatorResults_Jd-f";function d(e){var t=(0,a.useState)(e.json),n=t[0],r=t[1];function u(e,t){return a.createElement("input",{type:"button",value:e,onClick:function(){return r(t)}})}return a.createElement("div",{className:f},u("S^2",s(2)),u("CP^3",function(e){if(!Number.isInteger(e))throw new Error("dim must be an integer");if(e<=0)throw new Error("dim must be positive");return'[\n  ["c", 2, "zero"],\n  ["x", '+(2*e+1)+', "c^'+(e+1)+'"]\n]'}(3)),u("7-mfd",'[\n  ["a", 2, "zero"],\n  ["b", 2, "zero"],\n  ["x", 3, "a^2"],\n  ["y", 3, "a*b"],\n  ["z", 3, "b^2"]\n]'),a.createElement("textarea",{value:n,onChange:function(e){r(e.target.value)}}),a.createElement("input",{type:"button",value:"Apply",onClick:function(){e.updateDgaWrapper(n),e.finish()}}),a.createElement("input",{type:"button",value:"Cancel",onClick:function(){e.finish()}}))}var h=["self","freeLoopSpace","cyclic"];function b(e,t){var n;switch(void 0===t&&(t=0),e.messageType){case"success":n=i;break;case"error":n=l}return a.createElement("div",{key:t,className:n},e.strings.map((function(e,t){return function(e,t){switch(e.stringType){case"text":return a.createElement("span",{key:t},e.content);case"math":return a.createElement(o.Z,{key:t,math:e.content,settings:{output:"html",macros:{"\\deg":"|#1|"}}})}}(e,t)})))}function y(e){var t=(0,a.useState)("20"),n=t[0],r=t[1],u=(0,a.useState)("x^2"),o=u[0],i=u[1],l=(0,a.useState)(s(2)),m=l[0],f=l[1],E=(0,a.useState)(!1),y=E[0],w=E[1],k=(0,a.useState)("self"),C=k[0],S=k[1],N=(0,a.useState)([]),x=N[0],D=N[1],_=(0,a.useRef)(new c).current;function j(e){e.preventDefault();var t={command:"computeCohomologyClass",targetName:C,cocycleString:o};_.postMessage(t)}return _.onmessage=function(t){var n=t.data;switch(n.command){case"printMessages":e.printMessages(n.messages);break;case"showDgaInfo":D(n.messages)}},(0,a.useEffect)((function(){!function(e){var t={command:"updateJson",json:e};_.postMessage(t),_.postMessage({command:"dgaInfo"})}(m)}),[m]),a.createElement("div",{className:p},a.createElement("div",null,x.map((function(e,t){return b(e,t)}))),a.createElement("input",{type:"button",value:"Edit DGA",onClick:function(){return w(!0)}}),y&&a.createElement(d,{json:m,updateDgaWrapper:f,finish:function(){return w(!1)}}),a.createElement("div",null,h.map((function(e,t){return a.createElement("label",{key:t},a.createElement("input",{type:"radio",name:"targetName",value:e,checked:e===C,onChange:function(){return S(e)}}),e)}))),a.createElement("form",{className:g,onSubmit:function(e){e.preventDefault();var t={command:"computeCohomology",targetName:C,maxDegree:parseInt(n)};_.postMessage(t)}},a.createElement("input",{type:"submit",value:"Compute cohomology"}),a.createElement("span",null,"up to degree"),a.createElement("input",{type:"number",value:n,onChange:function(e){r(e.target.value)},min:0,className:v})),a.createElement("form",{className:g,onSubmit:j},a.createElement("input",{type:"submit",value:"Compute class"}),a.createElement("span",null,"cocycle:"),a.createElement("input",{type:"text",value:o,onChange:function(e){return i(e.target.value)},onSubmit:j})))}function w(){var e={messageType:"success",strings:[{stringType:"text",content:"Computation results will be shown here"}]},t=(0,a.useState)([e]),n=t[0],r=t[1],o=(0,a.useRef)(null);function c(e){r(e instanceof Array?function(t){return t.concat(e)}:function(t){return t.concat([e])})}return(0,a.useEffect)((function(){var e;null!==(e=o.current)&&setTimeout((function(){e.scrollTo({top:e.scrollHeight,behavior:"smooth"})}))}),[n]),a.createElement("div",{className:m},a.createElement(u.Z,{fallback:a.createElement("div",null,"Loading...")},(function(){return a.createElement(y,{printMessages:c})})),a.createElement("div",{className:E,ref:o},n.map((function(e,t){return b(e,t)}))))}function k(){return a.createElement(r.Z,{title:"Calculator"},a.createElement(w,null))}}}]);