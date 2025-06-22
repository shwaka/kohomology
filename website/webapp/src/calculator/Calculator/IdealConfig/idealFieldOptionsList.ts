import { FieldOptions } from "@calculator/Editor"

import { IdealFormInput } from "./schema"

export const idealFieldOptionsList: FieldOptions<IdealFormInput>[] = [
  {
    key: "text",
    label: "generator",
    width: 300,
    getRegisterName: (index) => `generatorArray.${index}.text` as const,
    isError: (_errors, _index) => false,
    inputProps: (index) => ({ "data-testid": `IdealEditorItem-input-${index}` }),
  }
]
