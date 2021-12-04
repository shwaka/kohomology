function getLinesBetween(lines: string[], startRegExp: RegExp, endRegExp: RegExp): string[] {
  const startLineNum: number = lines.findIndex(line => line.match(startRegExp));
  const endLineNum: number = lines.findIndex(line => line.match(endRegExp));
  return lines.slice(startLineNum + 1, endLineNum);
}

function getIndent(line: string): number {
  if (line === "") {
    return Infinity;
  }
  const regexp = new RegExp("^ *");
  const matchObj: RegExpMatchArray | null = line.match(regexp)
  if (matchObj === null) {
    throw new Error("This can't happen");
  }
  return matchObj[0].length;
}

function removeIndent(lines: string[]): string[] {
  const indents: number[] = lines.map(line => getIndent(line));
  const indent: number = Math.min(...indents);
  return lines.map(line => line.substring(indent));
}

export function restrict(text: string): string {
  const startRegExp = new RegExp("// start");
  const endRegExp = new RegExp("// end");
  const lines = text.split("\n");
  const restrictedLines = getLinesBetween(lines, startRegExp, endRegExp);
  return removeIndent(restrictedLines).join("\n");
}
