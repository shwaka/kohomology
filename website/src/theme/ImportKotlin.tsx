// import CodeBlock from '@theme/CodeBlock';
import React from "react"
import { restrict } from "@theme/restrict"
import MyCodeBlock from "./MyCodeBlock"
import styles from "./ImportKotlin.module.css"

const context = require.context("../../../sample/src/main/kotlin", true, /\.kt$/)

function normalizePath(path: string): string {
  if (path.startsWith("./")) {
    return path
  } else {
    return "./" + path
  }
}

const files: Map<string, string> = new Map(
  context.keys().map(path => [ normalizePath(path), context(path).default ])
)

type ImportKotlinProps = {
  path: string;
  restrict?: string | true; // "key" conflicts with react
}

export function ImportKotlin(props: ImportKotlinProps) {
  const href = `https://github.com/shwaka/kohomology/blob/main/sample/src/main/kotlin/${props.path}`
  const code: string | undefined = files.get(normalizePath(props.path))
  if (code === undefined) {
    return <div>{`Invalid path: ${props.path}`}</div>
  }
  const restrictedCode: string | null = restrict(code, props.restrict)
  if (restrictedCode === null) {
    return (
      <div className={styles.error}>
        ERROR: <code>{props.restrict}</code> is not found in <a href={href}>{href}</a>
      </div>
    )
  }
  return (
    <div>
      <MyCodeBlock className="language-kotlin" href={href} linkTitle={props.path}>
        {restrictedCode}
      </MyCodeBlock>
    </div>
  )
}
