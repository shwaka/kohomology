type RestrictedTextRange = {
  text: string
  begin: number
  end: number
}
type WholeText = {
  text: string
}
export type TextRange = RestrictedTextRange | WholeText

export function isRestricted(textRange: TextRange): textRange is RestrictedTextRange {
  return "begin" in textRange
}

function getLinesBetween(lines: string[], beginRegExp: RegExp, endRegExp: RegExp): TextRange | null {
  const beginLineNum: number = lines.findIndex(line => line.match(beginRegExp))
  const endLineNum: number = lines.findIndex(line => line.match(endRegExp))
  if (beginLineNum === -1 || endLineNum === -1) {
    return null
  }
  // +1: exclude "// \begin" comment
  const restrictedLines = lines.slice(beginLineNum + 1, endLineNum)
  return {
    text: removeIndent(restrictedLines).join("\n"),
    // +1: 0-based v.s. 1-based
    // +1: exclude "// \begin" comment
    begin: beginLineNum + 2,
    // +1: 0-based v.s. 1-based
    // -1: exluce "// \end" comment
    end: endLineNum,
  }
}

function getIndent(line: string): number {
  if (line === "") {
    return Infinity
  }
  const regexp = new RegExp("^ *")
  const matchObj: RegExpMatchArray | null = line.match(regexp)
  if (matchObj === null) {
    throw new Error("This can't happen")
  }
  return matchObj[0].length
}

function removeIndent(lines: string[]): string[] {
  const indents: number[] = lines.map(line => getIndent(line))
  const indent: number = Math.min(...indents)
  return lines.map(line => line.substring(indent))
}

function createRegExp(beginOrEnd: "begin" | "end", key: string | true): RegExp {
  if (key === true) {
    // "// \begin"
    // "// \end"
    return new RegExp(`// \\\\${beginOrEnd}`)
  } else {
    // "// \begin{key}"
    // "// \end{key}"
    return new RegExp(`// \\\\${beginOrEnd}{${key}}`)
  }
}

export function restrict(text: string, key: string | true | undefined): TextRange | null {
  if (key === undefined) {
    return {
      text: text
    }
  }
  const beginRegExp = createRegExp("begin", key)
  const endRegExp = createRegExp("end", key)
  const lines = text.split("\n")
  return getLinesBetween(lines, beginRegExp, endRegExp)
}
