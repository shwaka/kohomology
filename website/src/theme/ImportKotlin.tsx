// import CodeBlock from '@theme/CodeBlock';
import React, {useState} from 'react';
import { restrict } from '@theme/restrict';
import MyCodeBlock from './MyCodeBlock';

const context = require.context("../../../sample/src/main/kotlin", true, /\.kt$/);

function normalizePath(path: string): string {
  if (path.startsWith("./")) {
    return path;
  } else {
    return "./" + path;
  }
}

const files: Map<string, string> = new Map(
  context.keys().map(path => [ normalizePath(path), context(path).default ])
);

type ImportKotlinProps = {
  path: string;
}

export function ImportKotlin(props: ImportKotlinProps) {
  const href = `https://github.com/shwaka/kohomology/blob/main/sample/src/main/kotlin/${props.path}`;
  const code: string | undefined = files.get(normalizePath(props.path));
  return (
    <div>
      { code
        ? <MyCodeBlock className="language-kotlin" href={href}>{restrict(code)}</MyCodeBlock>
        : <div>{`Invalid path: ${props.path}`}</div>
      }
      {/*<a href={href} target="_blank">see the original file</a>*/}
    </div>
  );
}
