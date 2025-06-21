import React from "react"

import { RowComponentProps } from "@calculator/SortableFields"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { ArrayEditorItem, FieldOptions } from "./ArrayEditorItem"
import { GeneratorFormInput } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"

const fieldOptionsList: FieldOptions<GeneratorFormInput>[] = [
  {
    key: "name",
    getLabel: (_values, _index) => "generator",
    width: 90,
    getRegisterName: (index) => `generatorArray.${index}.name` as const,
    isError: (errors, index) => containsError({ errors, index, key: "name" }),
    inputProps: { "data-testid": "ArrayEditor-input-name" },
  },
  {
    key: "degree",
    getLabel: (values, index) => `deg(${values.generatorArray[index].name})`,
    width: 80,
    type: "number", valueAsNumber: true,
    getRegisterName: (index) => `generatorArray.${index}.degree` as const,
    isError: (errors, index) => containsError({ errors, index, key: "degree" }) || containsGlobalDegreeError({ errors }),
    inputProps: { "data-testid": "ArrayEditor-input-degree" },
  },
  {
    key: "differentialValue",
    getLabel: (values, index) => `d(${values.generatorArray[index].name})`,
    width: 200,
    getRegisterName: (index) => `generatorArray.${index}.differentialValue` as const,
    isError: (errors, index) => containsError({ errors, index, key: "differentialValue" }),
    inputProps: { "data-testid": "ArrayEditor-input-differentialValue" },
  },
]

export function GeneratorArrayEditorItem(
  props: RowComponentProps<GeneratorFormInput>
): React.JSX.Element {
  return (
    <ArrayEditorItem
      rowComponentProps={props}
      fieldOptionsList={fieldOptionsList}
      getFieldErrorArray={getFieldErrorArray}
    />
  )
}

function getFieldErrorArray(
  { errors, index }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number}
): (FieldError | undefined)[] {
  const error = errors.generatorArray?.[index]
  if (error === undefined) {
    return []
  }
  return (["name", "degree", "differentialValue"] as const).flatMap((key) => error[key])
}

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number, key: keyof Generator }): boolean {
  const error: FieldError | undefined = errors.generatorArray?.[index]?.[key]
  return error !== undefined
}

function containsGlobalDegreeError({ errors }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>> }): boolean {
  return (errors._global_errors?.generatorDegrees !== undefined)
}
