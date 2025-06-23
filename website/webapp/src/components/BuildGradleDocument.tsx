import { ReactElement } from "react"

import CodeBlock from "@theme/CodeBlock"
import TabItem from "@theme/TabItem"
import Tabs from "@theme/Tabs"

export function BuildGradleDocument(): ReactElement {
  return (
    <Tabs>
      <TabItem value="kotlin" label="build.gradle.kts" default>
        <CodeBlock language="kotlin">
          {`// If you are using build.gradle.kts (Kotlin script)
repositories {
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    implementation("com.github.shwaka.kohomology:kohomology:0.13")
}`}
        </CodeBlock>
      </TabItem>
      <TabItem value="groovy" label="build.gradle">
        <CodeBlock language="groovy">
          {`// If you are using buid.gradle (Groovy)
repositories {
    maven {
        url 'https://shwaka.github.io/maven/'
    }
}

dependencies {
    implementation 'com.github.shwaka.kohomology:kohomology:0.13'
}
`}
        </CodeBlock>
      </TabItem>
    </Tabs>
  )
}
