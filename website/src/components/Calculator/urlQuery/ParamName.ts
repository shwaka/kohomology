export const ParamName = {
  dgaJson: "dgaJson",
  dgaDsv: "dgaDsv",
  taretName: "target",
} as const

export type ParamName = (typeof ParamName)[keyof typeof ParamName]
