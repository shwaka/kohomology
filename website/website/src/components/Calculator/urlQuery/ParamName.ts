export const ParamName = {
  dgaJson: "dgaJson",
  dgaDsv: "dgaDsv",
  idealDsv: "idealDsv",
  taretName: "target",
} as const

export type ParamName = (typeof ParamName)[keyof typeof ParamName]
