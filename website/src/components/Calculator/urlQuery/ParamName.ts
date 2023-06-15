export const ParamName = {
  dgaJson: "dgaJson",
  dgaDsv: "dgaDsv",
} as const

export type ParamName = (typeof ParamName)[keyof typeof ParamName]
