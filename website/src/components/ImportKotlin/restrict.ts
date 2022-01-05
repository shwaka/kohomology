function getLinesBetween(lines: string[], startRegExp: RegExp, endRegExp: RegExp): string[] | null {
  const startLineNum: number = lines.findIndex(line => line.match(startRegExp))
  const endLineNum: number = lines.findIndex(line => line.match(endRegExp))
  if (startLineNum === -1 || endLineNum === -1) {
    return null
  }
  return lines.slice(startLineNum + 1, endLineNum)
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

function createRegExp(startOrEnd: "start" | "end", key: string | true): RegExp {
  if (key === true) {
    return new RegExp(`// ${startOrEnd}`)
  } else {
    return new RegExp(`// ${startOrEnd} +${key}`)
  }
}

export function restrict(text: string, key: string | true | undefined): string | null {
  if (key === undefined) {
    return text
  }
  const startRegExp = createRegExp("start", key)
  const endRegExp = createRegExp("end", key)
  const lines = text.split("\n")
  const restrictedLines: string[] | null = getLinesBetween(lines, startRegExp, endRegExp)
  if (restrictedLines === null) {
    return null
  }
  return removeIndent(restrictedLines).join("\n")
}
