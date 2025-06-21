import { RowComponentProps } from "@calculator/SortableFields"
import { ArrayPath, DeepRequired, DefaultValues, FieldError, FieldErrorsImpl, FieldValues } from "react-hook-form"
import { z } from "zod"

import { ArrayEditorItemProps } from "./ArrayEditorItem"

type UseArrayEditorArgsBase<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> = {
  defaultValues: DefaultValues<TFieldValues>
  setValues: (formValues: TFieldValues) => void
  getGlobalErrors: (errors: FieldErrorsImpl<DeepRequired<TFieldValues>>) => (FieldError | undefined)[]
  getNext: (valueArray: TFieldValues[K][number][]) => TFieldValues[K][number]
  schema: z.ZodType<TFieldValues>
  arrayKey: K
}

export type UseArrayEditorArgs<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> = UseArrayEditorArgsBase<TFieldValues, K> & {
  fieldOptionsList: ArrayEditorItemProps<TFieldValues>["fieldOptionsList"]
  getFieldErrorArray: ArrayEditorItemProps<TFieldValues>["getFieldErrorArray"]
}

export type UseArrayEditorPropsArgs<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> = UseArrayEditorArgsBase<TFieldValues, K> & {
  RowComponent: (props: RowComponentProps<TFieldValues, undefined>) => React.JSX.Element
}
