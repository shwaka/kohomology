import { RowComponentProps } from "@calculator/SortableFields"
import { ArrayPath, DeepRequired, DefaultValues, FieldError, FieldErrorsImpl, FieldValues } from "react-hook-form"
import { z } from "zod"

export interface UseArrayEditorArgs<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> {
  defaultValues: DefaultValues<TFieldValues>
  setValues: (formValues: TFieldValues) => void
  getGlobalErrors: (errors: FieldErrorsImpl<DeepRequired<TFieldValues>>) => (FieldError | undefined)[]
  getNext: (valueArray: TFieldValues[K][number][]) => TFieldValues[K][number]
  schema: z.ZodType<TFieldValues>
  RowComponent: (props: RowComponentProps<TFieldValues, undefined>) => React.JSX.Element
  arrayKey: K
}
