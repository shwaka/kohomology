(()=>{"use strict";var e,t,r,o,a,f={},n={};function b(e){var t=n[e];if(void 0!==t)return t.exports;var r=n[e]={exports:{}};return f[e].call(r.exports,r,r.exports,b),r.exports}b.m=f,e=[],b.O=(t,r,o,a)=>{if(!r){var f=1/0;for(d=0;d<e.length;d++){r=e[d][0],o=e[d][1],a=e[d][2];for(var n=!0,i=0;i<r.length;i++)(!1&a||f>=a)&&Object.keys(b.O).every((e=>b.O[e](r[i])))?r.splice(i--,1):(n=!1,a<f&&(f=a));if(n){e.splice(d--,1);var c=o();void 0!==c&&(t=c)}}return t}a=a||0;for(var d=e.length;d>0&&e[d-1][2]>a;d--)e[d]=e[d-1];e[d]=[r,o,a]},b.n=e=>{var t=e&&e.__esModule?()=>e.default:()=>e;return b.d(t,{a:t}),t},r=Object.getPrototypeOf?e=>Object.getPrototypeOf(e):e=>e.__proto__,b.t=function(e,o){if(1&o&&(e=this(e)),8&o)return e;if("object"==typeof e&&e){if(4&o&&e.__esModule)return e;if(16&o&&"function"==typeof e.then)return e}var a=Object.create(null);b.r(a);var f={};t=t||[null,r({}),r([]),r(r)];for(var n=2&o&&e;"object"==typeof n&&!~t.indexOf(n);n=r(n))Object.getOwnPropertyNames(n).forEach((t=>f[t]=()=>e[t]));return f.default=()=>e,b.d(a,f),a},b.d=(e,t)=>{for(var r in t)b.o(t,r)&&!b.o(e,r)&&Object.defineProperty(e,r,{enumerable:!0,get:t[r]})},b.f={},b.e=e=>Promise.all(Object.keys(b.f).reduce(((t,r)=>(b.f[r](e,t),t)),[])),b.u=e=>"assets/js/"+({53:"935f2afb",85:"1f391b9e",185:"ab189545",203:"5fec239d",206:"f8409a7e",237:"1df93b7f",316:"edd7edbb",394:"729f977e",414:"393be207",514:"1be78505",549:"7bb196db",592:"common",648:"e464966a",663:"7fb368e4",695:"cc509668",699:"c179f821",741:"b5e0a78e",795:"86257f88",841:"17444fa8",918:"17896441",960:"424c14fa"}[e]||e)+"."+{53:"4283f588",85:"783d0092",163:"5a250e5c",185:"44e9572b",203:"23edd4aa",206:"b44ac133",237:"018387e9",316:"ed4b4ace",376:"6360a3a7",394:"a0e70e32",414:"dee1e001",514:"d28f6c20",549:"c548ff89",592:"aaf4c509",642:"78f88d24",648:"0d1a4745",663:"a8774204",677:"75ed3864",695:"73ce3f3b",699:"15c690c9",741:"af26a210",795:"be007314",841:"caf38860",857:"14e47435",918:"e13ef290",960:"6c9fbc13",972:"778ffd05"}[e]+".js",b.miniCssF=e=>{},b.g=function(){if("object"==typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"==typeof window)return window}}(),b.o=(e,t)=>Object.prototype.hasOwnProperty.call(e,t),o={},a="website:",b.l=(e,t,r,f)=>{if(o[e])o[e].push(t);else{var n,i;if(void 0!==r)for(var c=document.getElementsByTagName("script"),d=0;d<c.length;d++){var u=c[d];if(u.getAttribute("src")==e||u.getAttribute("data-webpack")==a+r){n=u;break}}n||(i=!0,(n=document.createElement("script")).charset="utf-8",n.timeout=120,b.nc&&n.setAttribute("nonce",b.nc),n.setAttribute("data-webpack",a+r),n.src=e),o[e]=[t];var l=(t,r)=>{n.onerror=n.onload=null,clearTimeout(s);var a=o[e];if(delete o[e],n.parentNode&&n.parentNode.removeChild(n),a&&a.forEach((e=>e(r))),t)return t(r)},s=setTimeout(l.bind(null,void 0,{type:"timeout",target:n}),12e4);n.onerror=l.bind(null,n.onerror),n.onload=l.bind(null,n.onload),i&&document.head.appendChild(n)}},b.r=e=>{"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},b.p="/kohomology/",b.gca=function(e){return e={17896441:"918","935f2afb":"53","1f391b9e":"85",ab189545:"185","5fec239d":"203",f8409a7e:"206","1df93b7f":"237",edd7edbb:"316","729f977e":"394","393be207":"414","1be78505":"514","7bb196db":"549",common:"592",e464966a:"648","7fb368e4":"663",cc509668:"695",c179f821:"699",b5e0a78e:"741","86257f88":"795","17444fa8":"841","424c14fa":"960"}[e]||e,b.p+b.u(e)},(()=>{var e={303:0,532:0};b.f.j=(t,r)=>{var o=b.o(e,t)?e[t]:void 0;if(0!==o)if(o)r.push(o[2]);else if(/^(303|532)$/.test(t))e[t]=0;else{var a=new Promise(((r,a)=>o=e[t]=[r,a]));r.push(o[2]=a);var f=b.p+b.u(t),n=new Error;b.l(f,(r=>{if(b.o(e,t)&&(0!==(o=e[t])&&(e[t]=void 0),o)){var a=r&&("load"===r.type?"missing":r.type),f=r&&r.target&&r.target.src;n.message="Loading chunk "+t+" failed.\n("+a+": "+f+")",n.name="ChunkLoadError",n.type=a,n.request=f,o[1](n)}}),"chunk-"+t,t)}},b.O.j=t=>0===e[t];var t=(t,r)=>{var o,a,f=r[0],n=r[1],i=r[2],c=0;if(f.some((t=>0!==e[t]))){for(o in n)b.o(n,o)&&(b.m[o]=n[o]);if(i)var d=i(b)}for(t&&t(r);c<f.length;c++)a=f[c],b.o(e,a)&&e[a]&&e[a][0](),e[a]=0;return b.O(d)},r=self.webpackChunkwebsite=self.webpackChunkwebsite||[];r.forEach(t.bind(null,0)),r.push=t.bind(null,r.push.bind(r))})()})();