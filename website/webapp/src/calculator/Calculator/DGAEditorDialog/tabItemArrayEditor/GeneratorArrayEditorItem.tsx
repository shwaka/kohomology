import React from "react"

import { RowComponentProps } from "@calculator/SortableFields"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { ArrayEditorItem, FieldOptions } from "./ArrayEditorItem"
import { GeneratorFormInput } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"

export function GeneratorArrayEditorItem(
  props: RowComponentProps<GeneratorFormInput>
): React.JSX.Element {
  const { index, formData: { getValues }} = props
  const generatorName = getValues().generatorArray[index].name

  const fieldOptionsList: FieldOptions<GeneratorFormInput>[] = [
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
      label: `deg(${generatorName})`,
      width: 80,
      type: "number", valueAsNumber: true,
      getRegisterName: (index) => `generatorArray.${index}.degree` as const,
      isError: (errors, index) => containsError({ errors, index, key: "degree" }) || containsGlobalDegreeError({ errors }),
      inputProps: { "data-testid": "ArrayEditor-input-degree" },
    },
    {
      key: "differentialValue",
      label: `d(${generatorName})`,
      width: 200,
      getRegisterName: (index) => `generatorArray.${index}.differentialValue` as const,
      isError: (errors, index) => containsError({ errors, index, key: "differentialValue" }),
      inputProps: { "data-testid": "ArrayEditor-input-differentialValue" },
    },
  ]

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
