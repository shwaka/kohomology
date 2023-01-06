export class ExhaustivityError extends Error {
  // https://typescriptbook.jp/reference/statements/never#%E4%BE%8B%E5%A4%96%E3%81%AB%E3%82%88%E3%82%8B%E7%B6%B2%E7%BE%85%E6%80%A7%E3%83%81%E3%82%A7%E3%83%83%E3%82%AF (例外による網羅性チェック)
  constructor(value: never, message = `Unsupported type: ${value}`) {
    super(message)
  }
}
