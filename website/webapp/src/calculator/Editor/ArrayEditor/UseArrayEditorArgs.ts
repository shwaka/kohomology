import { ArrayPath, DeepRequired, DefaultValues, FieldError, FieldErrorsImpl, FieldValues } from "react-hook-form"
import { z } from "zod"

import { type ArrayEditorRowComponentData } from "./ArrayEditorItem"

export type ArrayEditorConfig<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> = {
  getGlobalErrors: (errors: FieldErrorsImpl<DeepRequired<TFieldValues>>) => (FieldError | undefined)[]
  getNext: (valueArray: TFieldValues[K][number][]) => TFieldValues[K][number]
  // See https://github.com/react-hook-form/resolvers/issues/782 for typeName in schema
  schema: z.ZodType<TFieldValues> & { _def: { typeName: string } }
  arrayKey: K
  zodResolverMode?: "sync" | "async"
}

export type ArrayEditorValues<TFieldValues extends FieldValues> = {
  defaultValues: DefaultValues<TFieldValues>
  setValues: (formValues: TFieldValues) => void
}

export type UseArrayEditorArgs<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> =
  & ArrayEditorConfig<TFieldValues, K>
  & ArrayEditorValues<TFieldValues>
  & ArrayEditorRowComponentData<TFieldValues>

export type UseArrayEditorPropsArgs<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> =
  ArrayEditorConfig<TFieldValues, K> & ArrayEditorValues<TFieldValues>
