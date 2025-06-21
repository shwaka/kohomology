import { FieldOptions } from "@calculator/ArrayEditor"

import { IdealFormInput } from "./schema"

export const idealFieldOptionsList: FieldOptions<IdealFormInput>[] = [
  {
    key: "text",
    getLabel: (_values, _index) => "generator",
    width: 300,
    getRegisterName: (index) => `generatorArray.${index}.text` as const,
    isError: (_errors, _index) => false,
  }
]
