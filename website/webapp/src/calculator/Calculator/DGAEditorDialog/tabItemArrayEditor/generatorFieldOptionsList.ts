import { FieldOptions } from "@calculator/Editor"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { GeneratorFormInput } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"

export const generatorFieldOptionsList: FieldOptions<GeneratorFormInput>[] = [
  {
    key: "name",
    label: "generator",
    width: 90,
    getRegisterName: (index) => `generatorArray.${index}.name` as const,
    isError: (errors, index) => containsError({ errors, index, key: "name" }),
    inputProps: { "data-testid": "ArrayEditor-input-name" },
  },
  {
    key: "degree",
    label: (values, index) => `deg(${values.generatorArray[index].name})`,
    width: 80,
    type: "number", valueAsNumber: true,
    getRegisterName: (index) => `generatorArray.${index}.degree` as const,
    isError: (errors, index) => containsError({ errors, index, key: "degree" }) || containsGlobalDegreeError({ errors }),
    inputProps: { "data-testid": "ArrayEditor-input-degree" },
  },
  {
    key: "differentialValue",
    label: (values, index) => `d(${values.generatorArray[index].name})`,
    width: 200,
    getRegisterName: (index) => `generatorArray.${index}.differentialValue` as const,
    isError: (errors, index) => containsError({ errors, index, key: "differentialValue" }),
    inputProps: { "data-testid": "ArrayEditor-input-differentialValue" },
  },
]

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number, key: keyof Generator }): boolean {
  const error: FieldError | undefined = errors.generatorArray?.[index]?.[key]
  return error !== undefined
}

function containsGlobalDegreeError({ errors }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>> }): boolean {
  return (errors._global_errors?.generatorDegrees !== undefined)
}
