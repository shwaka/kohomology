"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[699],{7575:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>N,contentTitle:()=>f,default:()=>j,frontMatter:()=>y,metadata:()=>w,toc:()=>E});var o=a(7462),n=a(7294),l=a(3905),r=a(6464),i=a(6010);const s="tabItem_Ymn6";function u(e){let{children:t,hidden:a,className:o}=e;return n.createElement("div",{role:"tabpanel",className:(0,i.Z)(s,o),hidden:a},t)}var m=a(2389),p=a(7392),d=a(7094),c=a(2466);const h="tabList__CuJ",k="tabItem_LNqP";function b(e){var t;const{lazy:a,block:l,defaultValue:r,values:s,groupId:u,className:m}=e,b=n.Children.map(e.children,(e=>{if((0,n.isValidElement)(e)&&"value"in e.props)return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)})),v=s??b.map((e=>{let{props:{value:t,label:a,attributes:o}}=e;return{value:t,label:a,attributes:o}})),g=(0,p.l)(v,((e,t)=>e.value===t.value));if(g.length>0)throw new Error(`Docusaurus error: Duplicate values "${g.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`);const y=null===r?r:r??(null==(t=b.find((e=>e.props.default)))?void 0:t.props.value)??b[0].props.value;if(null!==y&&!v.some((e=>e.value===y)))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${y}" but none of its children has the corresponding value. Available values are: ${v.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);const{tabGroupChoices:f,setTabGroupChoices:w}=(0,d.U)(),[N,E]=(0,n.useState)(y),T=[],{blockElementScrollPositionUntilNextRender:j}=(0,c.o5)();if(null!=u){const e=f[u];null!=e&&e!==N&&v.some((t=>t.value===e))&&E(e)}const x=e=>{const t=e.currentTarget,a=T.indexOf(t),o=v[a].value;o!==N&&(j(t),E(o),null!=u&&w(u,String(o)))},I=e=>{var t;let a=null;switch(e.key){case"Enter":x(e);break;case"ArrowRight":{const t=T.indexOf(e.currentTarget)+1;a=T[t]??T[0];break}case"ArrowLeft":{const t=T.indexOf(e.currentTarget)-1;a=T[t]??T[T.length-1];break}}null==(t=a)||t.focus()};return n.createElement("div",{className:(0,i.Z)("tabs-container",h)},n.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,i.Z)("tabs",{"tabs--block":l},m)},v.map((e=>{let{value:t,label:a,attributes:l}=e;return n.createElement("li",(0,o.Z)({role:"tab",tabIndex:N===t?0:-1,"aria-selected":N===t,key:t,ref:e=>T.push(e),onKeyDown:I,onClick:x},l,{className:(0,i.Z)("tabs__item",k,null==l?void 0:l.className,{"tabs__item--active":N===t})}),a??t)}))),a?(0,n.cloneElement)(b.filter((e=>e.props.value===N))[0],{className:"margin-top--md"}):n.createElement("div",{className:"margin-top--md"},b.map(((e,t)=>(0,n.cloneElement)(e,{key:t,hidden:e.props.value!==N})))))}function v(e){const t=(0,m.Z)();return n.createElement(b,(0,o.Z)({key:String(t)},e))}function g(){return n.createElement(v,null,n.createElement(u,{value:"kotlin",label:"build.gradle.kts",default:!0},n.createElement(r.Z,{language:"kotlin"},'// If you are using build.gradle.kts (Kotlin script)\nrepositories {\n    maven(url = "https://shwaka.github.io/maven/")\n}\n\ndependencies {\n    implementation("com.github.shwaka.kohomology:kohomology:0.10")\n}')),n.createElement(u,{value:"groovy",label:"build.gradle"},n.createElement(r.Z,{language:"groovy"},"// If you are using buid.gradle (Groovy)\nrepositories {\n    maven {\n        url 'https://shwaka.github.io/maven/'\n    }\n}\n\ndependencies {\n    implementation 'com.github.shwaka.kohomology:kohomology:0.10'\n}\n")))}const y={title:"Quick start",sidebar_position:2},f=void 0,w={unversionedId:"quick-start",id:"quick-start",title:"Quick start",description:"kohomology is a Kotlin library published at the maven repository shwaka/maven.",source:"@site/docs/quick-start.mdx",sourceDirName:".",slug:"/quick-start",permalink:"/kohomology/docs/quick-start",draft:!1,editUrl:"https://github.com/shwaka/kohomology/edit/main/website/docs/quick-start.mdx",tags:[],version:"current",sidebarPosition:2,frontMatter:{title:"Quick start",sidebar_position:2},sidebar:"tutorialSidebar",previous:{title:"Introduction",permalink:"/kohomology/docs/intro"},next:{title:"Cohomology of Sullivan algebras",permalink:"/kohomology/docs/sullivan-algebra"}},N={},E=[{value:"Requirement",id:"requirement",level:2},{value:"Start from a template project (easiest)",id:"start-from-a-template-project-easiest",level:2},{value:"Start from a new Kotlin project",id:"start-from-a-new-kotlin-project",level:2},{value:"New to Kotlin?",id:"new-to-kotlin",level:2},{value:"New to rational homotopy theory?",id:"new-to-rational-homotopy-theory",level:2}],T={toc:E};function j(e){let{components:t,...a}=e;return(0,l.kt)("wrapper",(0,o.Z)({},T,a,{components:t,mdxType:"MDXLayout"}),(0,l.kt)("p",null,(0,l.kt)("inlineCode",{parentName:"p"},"kohomology")," is a ",(0,l.kt)("a",{parentName:"p",href:"https://kotlinlang.org/"},"Kotlin")," library published at the maven repository ",(0,l.kt)("a",{parentName:"p",href:"https://github.com/shwaka/maven"},"shwaka/maven"),".\nYou can use it in any kotlin project."),(0,l.kt)("h2",{id:"requirement"},"Requirement"),(0,l.kt)("p",null,"You need to install Java Development Kit (JDK).\nHere we give an example of installation, but most JDK distributions and versions should be OK."),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},"[Windows]"," Install from ",(0,l.kt)("a",{parentName:"li",href:"https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html"},"Amazon Corretto"),". Usually you will choose ",(0,l.kt)("a",{parentName:"li",href:"https://corretto.aws/downloads/latest/amazon-corretto-8-x64-windows-jdk.msi"},"amazon-corretto-8-x64-windows-jdk.msi")),(0,l.kt)("li",{parentName:"ul"},"[Mac, Linux]"," First, install ",(0,l.kt)("a",{parentName:"li",href:"https://sdkman.io/"},"sdkman"),". Then run ",(0,l.kt)("inlineCode",{parentName:"li"},"sdk install java 8.292.10.1-amzn"),".")),(0,l.kt)("h2",{id:"start-from-a-template-project-easiest"},"Start from a template project (easiest)"),(0,l.kt)("p",null,"The directory ",(0,l.kt)("a",{parentName:"p",href:"https://github.com/shwaka/kohomology/tree/main/template"},"kohomology/template")," serves as a template project to use kohomology.\nIf you are not familiar with kotlin projects, this is a good starting point.\nSee ",(0,l.kt)("a",{parentName:"p",href:"https://github.com/shwaka/kohomology/blob/main/template/README.md"},"its README.md")," for detailed usage."),(0,l.kt)("h2",{id:"start-from-a-new-kotlin-project"},"Start from a new Kotlin project"),(0,l.kt)("p",null,"Create a gradle project as in ",(0,l.kt)("a",{parentName:"p",href:"https://kotlinlang.org/docs/jvm-get-started.html"},"Get started with Kotlin/JVM | Kotlin"),"\n(or use an existing gradle project).\nThen write the following in your ",(0,l.kt)("inlineCode",{parentName:"p"},"build.gradle.kts")," (or ",(0,l.kt)("inlineCode",{parentName:"p"},"build.gradle"),"):"),(0,l.kt)(g,{mdxType:"BuildGradleDocument"}),(0,l.kt)("h2",{id:"new-to-kotlin"},"New to Kotlin?"),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},"The ",(0,l.kt)("a",{parentName:"li",href:"https://kotlinlang.org/docs/home.html"},"official documentation")," is well-written and is a good starting point."),(0,l.kt)("li",{parentName:"ul"},"I (and the Kotlin official team) strongly recommend to use ",(0,l.kt)("a",{parentName:"li",href:"https://www.jetbrains.com/idea/download/#section=linux"},"IntelliJ IDEA"),"\nto edit Kotlin source codes.")),(0,l.kt)("h2",{id:"new-to-rational-homotopy-theory"},"New to rational homotopy theory?"),(0,l.kt)("ul",null,(0,l.kt)("li",{parentName:"ul"},(0,l.kt)("a",{parentName:"li",href:"https://arxiv.org/abs/math/0604626"},"Rational homotopy theory: a brief introduction"),": a survey due to Hess"),(0,l.kt)("li",{parentName:"ul"},(0,l.kt)("a",{parentName:"li",href:"https://arxiv.org/abs/1708.05245"},"Rational homotopy theory via Sullivan models: a survey"),": a survey due to F\xe9lix and Halperin"),(0,l.kt)("li",{parentName:"ul"},(0,l.kt)("a",{parentName:"li",href:"https://link.springer.com/book/10.1007/978-1-4613-0105-9"},"Rational Homotopy Theory (GTM 205)"),": an introductory book on rational homotopy theory (This book has 500 pages, but this library depends mainly only on Section 12)")))}j.isMDXComponent=!0}}]);