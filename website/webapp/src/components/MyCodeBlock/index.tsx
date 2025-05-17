import CodeBlock from "@theme/CodeBlock"
import type {Props} from "@theme/CodeBlock"
import React from "react"
// ↑ docusaurus-theme-classic/src/theme-classic.d.ts に定義がある
// export interface Props {
//   readonly children: string | ReactElement;
//   readonly className?: string;
//   readonly metastring?: string;
//   readonly title?: string;
//   readonly language?: string;
// }

import styles from "./styles.module.scss"

type MyCodeBlockProps = { href?: string, linkTitle?: string } & Props

export default function MyCodeBlock({
  href, linkTitle, // 追加した
  ...props
}: MyCodeBlockProps): React.JSX.Element {
  // <a> を <CodeBlock> の下に置こうとすると，<CodeBlock> の margin-bottom が邪魔になる
  return (
    <div className={styles.MyCodeBlock}>
      {href !== null && (
        <a href={href} target="_blank" rel="noreferrer">
          {linkTitle !== null ? linkTitle : href}
        </a>
      )}
      <CodeBlock {...props}>
        {props.children}
      </CodeBlock>
    </div>
  )
}
