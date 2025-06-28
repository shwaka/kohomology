import { ReactNode, ReactElement } from "react"

import { closestCenter, DndContext, DragEndEvent, DraggableAttributes, KeyboardSensor, PointerSensor, useSensor, useSensors } from "@dnd-kit/core"
import { SyntheticListenerMap } from "@dnd-kit/core/dist/hooks/utilities"
import { restrictToParentElement } from "@dnd-kit/modifiers"
import { SortableContext, sortableKeyboardCoordinates, useSortable, verticalListSortingStrategy } from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import { DeepRequired, FieldArrayPath, FieldArrayWithId, FieldErrorsImpl, FieldValues, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"

export interface FormData<
  TFieldValues extends FieldValues = FieldValues,
> {
  register: UseFormRegister<TFieldValues>
  remove: UseFieldArrayRemove
  errors: FieldErrorsImpl<DeepRequired<TFieldValues>>
  getValues: UseFormGetValues<TFieldValues>
  trigger: UseFormTrigger<TFieldValues>
}

export interface RowComponentProps<
  TFieldValues extends FieldValues,
  TExternalData = undefined,
> {
  draggableProps: DraggableAttributes | (DraggableAttributes & SyntheticListenerMap)
  index: number
  formData: FormData<TFieldValues>
  externalData: TExternalData
}

interface SortableRowProps<
  TFieldValues extends FieldValues,
  TExternalData,
> {
  id: string
  RowComponent: (props: RowComponentProps<TFieldValues, TExternalData>) => ReactElement
  index: number
  formData: FormData<TFieldValues>
  externalData: TExternalData
}

function SortableRow<TFieldValues extends FieldValues, TExternalData>(
  { id, RowComponent, index, formData, externalData }: SortableRowProps<TFieldValues, TExternalData>
): ReactElement {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id })
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  }
  const rowComponentProps: RowComponentProps<TFieldValues, TExternalData> = {
    draggableProps: { ...attributes, ...listeners },
    index,
    formData,
    externalData,
  }

  return (
    <div ref={setNodeRef} style={style}>
      <RowComponent
        {...rowComponentProps}
      />
    </div>
  )
}

export interface SortableFieldsProps<
  TFieldValues extends FieldValues = FieldValues,
  TFieldArrayName extends FieldArrayPath<TFieldValues> = FieldArrayPath<TFieldValues>,
  // TKeyName extends string = "id" // directly specify "id" to avoid error (why error?)
  TExternalData = undefined,
> {
  fields: FieldArrayWithId<TFieldValues, TFieldArrayName, "id">[]
  move: UseFieldArrayMove
  formData: FormData<TFieldValues>
  RowComponent: (props: RowComponentProps<TFieldValues, TExternalData>) => ReactElement
  Container: (props: { children: ReactNode }) => ReactElement
  externalData: TExternalData
}
export function SortableFields<
  TFieldValues extends FieldValues = FieldValues,
  TFieldArrayName extends FieldArrayPath<TFieldValues> = FieldArrayPath<TFieldValues>,
  // TKeyName extends string = "id"
  TExternalData = undefined,
>(
  { fields, move, formData, RowComponent, Container, externalData }: SortableFieldsProps<TFieldValues, TFieldArrayName, TExternalData>
): ReactElement {
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  )

  const { trigger } = formData

  async function handleDragEnd({ active, over }: DragEndEvent): Promise<void> {
    // If trigger() is here, then errors BEFORE move() will be shown.
    // trigger() // trigger input validation

    if (over === null) {
      throw new Error("over is null")
    }

    if (active.id !== over.id) {
      console.log(`replace: ${active.id}, ${over.id}`)
      const oldIndex = fields.map((field) => field.id).indexOf(active.id as string)
      const newIndex = fields.map((field) => field.id).indexOf(over.id as string)

      move(oldIndex, newIndex)
    }
    await trigger() // trigger input validation
  }

  // Container can be
  // - div (or something similar): draggable region is restricted to it
  // - React.Fragment: draggable region is restricted to parent element
  return (
    <Container>
      <DndContext
        sensors={sensors}
        collisionDetection={closestCenter}
        onDragEnd={handleDragEnd}
        modifiers={[restrictToParentElement]}
      >
        <SortableContext
          items={fields}
          strategy={verticalListSortingStrategy}
        >
          {fields.map((field, index) => (
            <SortableRow
              key={field.id}
              id={field.id}
              {...{ index, formData, RowComponent, externalData }}
            />
          ))}
        </SortableContext>
      </DndContext>
    </Container>
  )
}
