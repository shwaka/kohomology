/******/ (() => { // webpackBootstrap
/******/ 	var __webpack_modules__ = ({

/***/ 160:
/***/ (function(module, exports) {

var __WEBPACK_AMD_DEFINE_FACTORY__, __WEBPACK_AMD_DEFINE_ARRAY__, __WEBPACK_AMD_DEFINE_RESULT__;(function(root,factory){if(true)!(__WEBPACK_AMD_DEFINE_ARRAY__ = [exports], __WEBPACK_AMD_DEFINE_FACTORY__ = (factory),
		__WEBPACK_AMD_DEFINE_RESULT__ = (typeof __WEBPACK_AMD_DEFINE_FACTORY__ === 'function' ?
		(__WEBPACK_AMD_DEFINE_FACTORY__.apply(exports, __WEBPACK_AMD_DEFINE_ARRAY__)) : __WEBPACK_AMD_DEFINE_FACTORY__),

/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// This entry need to be wrapped in an IIFE because it need to be in strict mode.
(() => {
"use strict";

// EXTERNAL MODULE: ./kohomology-js/build/js/packages/kohomology-js/kotlin/kohomology-js.js
var kohomology_js = __webpack_require__(160);
;// CONCATENATED MODULE: ./src/components/Calculator/styled/message.ts
const stringTypes=["text","math"];const messageTypes=["success","error"];function formatStyledMessage(styledMessage){return styledMessage.strings.map(styledString=>styledString.content).join("");}function fromString(messageType,str){const styledString={stringType:"text",content:str};return{messageType:messageType,strings:[styledString]};}
;// CONCATENATED MODULE: ./src/components/Calculator/worker/styled.ts
function toStyledString(styledStringKt){const stringType=styledStringKt.stringType;if(!stringTypes.includes(stringType)){throw new Error(`Invalid stringType: ${stringType}`);}return{stringType:stringType,content:styledStringKt.content};}function toStyledMessage(styledMessageKt){const messageType=styledMessageKt.messageType;if(!messageTypes.includes(messageType)){throw new Error(`Invalid messageType: ${messageType}`);}return{messageType:messageType,strings:styledMessageKt.strings.map(toStyledString)};}
;// CONCATENATED MODULE: ./src/components/Calculator/worker/KohomologyMessageHandler.ts
class KohomologyMessageHandler{dgaWrapper=null;constructor(postMessage,log,error){this.postMessage=postMessage;// this.onmessage = this.onmessage.bind(this)
this.log=log??(message=>console.log(message));this.error=error??(message=>console.error(message));}onmessage(input){this.log("Worker start");this.log(input);try{switch(input.command){case"updateJson":this.updateJson(input.json);break;case"computeCohomology":this.computeCohomology(input.targetName,input.minDegree,input.maxDegree,input.showCohomology);break;case"dgaInfo":this.showDgaInfo();break;case"computeCohomologyClass":this.computeCohomologyClass(input.targetName,input.cocycleString,input.showBasis);break;default:throw new ExhaustivityError(input,`Invalid command: ${input}`);}}catch(error){if(error instanceof Error){this.sendMessages(fromString("error",error.message));}this.error(error);}}updateJson(json){try{this.dgaWrapper=new kohomology_js.FreeDGAWrapper(json);}catch(error){this.dgaWrapper=null;throw error;}}sendMessages(messages){if(messages instanceof Array){const output={command:"printMessages",messages:messages};this.postMessage(output);}else{const output={command:"printMessages",messages:[messages]};this.postMessage(output);}}computeCohomology(targetName,minDegree,maxDegree,showCohomology){assertNotNull(this.dgaWrapper);this.sendMessages(toStyledMessage(this.dgaWrapper.computationHeader(targetName,minDegree,maxDegree)));let styledMessages=[];let previousTime=new Date().getTime();// in millisecond
for(let degree=minDegree;degree<=maxDegree;degree++){switch(showCohomology){// Don't send message immediately for performance reason.
// If styledMessages.push(...) is replaced with this.sendMessages(...),
// then the Calculator significantly slows down.
// This is because this.sendMessages(...) causes re-render of the component Calculator.
case"basis":styledMessages.push(toStyledMessage(this.dgaWrapper.computeCohomology(targetName,degree)));break;case"dim":styledMessages.push(toStyledMessage(this.dgaWrapper.computeCohomologyDim(targetName,degree)));break;}const currentTime=new Date().getTime();// in millisecond
if(currentTime-previousTime>500){previousTime=currentTime;this.sendMessages(styledMessages);styledMessages=[];}}this.sendMessages(styledMessages);}computeCohomologyClass(targetName,cocycleString,showBasis){assertNotNull(this.dgaWrapper);this.sendMessages(toStyledMessage(this.dgaWrapper.computeCohomologyClass(targetName,cocycleString,showBasis)));}showDgaInfo(){if(this.dgaWrapper===null){const message="[Error] Your DGA contains errors. Please fix them.";const output={command:"showDgaInfo",messages:[fromString("error",message)]};this.postMessage(output);}else{const output={command:"showDgaInfo",messages:this.dgaWrapper.dgaInfo().map(toStyledMessage)};this.postMessage(output);}}}function assertNotNull(value){if(value===null){throw new Error("The given value is null.");}}class ExhaustivityError extends Error{// https://typescriptbook.jp/reference/statements/never#%E4%BE%8B%E5%A4%96%E3%81%AB%E3%82%88%E3%82%8B%E7%B6%B2%E7%BE%85%E6%80%A7%E3%83%81%E3%82%A7%E3%83%83%E3%82%AF (例外による網羅性チェック)
constructor(value,message=`Unsupported type: ${value}`){super(message);}}
;// CONCATENATED MODULE: ./node_modules/babel-loader/lib/index.js??ruleSet[1].rules[5].use[0]!./src/components/Calculator/worker/kohomology.worker.ts
// eslint-disable-next-line no-restricted-globals
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const ctx=self;const messageHandler=new KohomologyMessageHandler(ctx.postMessage.bind(ctx));onmessage=e=>messageHandler.onmessage(e.data);
})();

/******/ })()
;