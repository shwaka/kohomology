import { closestCenter, DndContext, DragEndEvent, DraggableAttributes, KeyboardSensor, PointerSensor, useSensor, useSensors } from "@dnd-kit/core"
import { SyntheticListenerMap } from "@dnd-kit/core/dist/hooks/utilities"
import { restrictToParentElement } from "@dnd-kit/modifiers"
import { SortableContext, sortableKeyboardCoordinates, useSortable, verticalListSortingStrategy } from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import React, { ReactNode } from "react"
import { DeepRequired, FieldArrayPath, FieldArrayWithId, FieldErrorsImpl, FieldValues, UseFieldArrayMove, UseFieldArrayRemove, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"

export interface RowComponentProps<
  TFieldValues extends FieldValues = FieldValues,
> {
  draggableProps: DraggableAttributes | (DraggableAttributes & SyntheticListenerMap)
  register: UseFormRegister<TFieldValues>
  index: number
  remove: UseFieldArrayRemove
  errors: FieldErrorsImpl<DeepRequired<TFieldValues>>
  getValues: UseFormGetValues<TFieldValues>
  trigger: UseFormTrigger<TFieldValues>
}

interface SortableRowProps<
  TFieldValues extends FieldValues = FieldValues,
> {
  id: string
  RowComponent: (props: RowComponentProps<TFieldValues>) => JSX.Element
  register: UseFormRegister<TFieldValues>
  index: number
  remove: UseFieldArrayRemove
  errors: FieldErrorsImpl<DeepRequired<TFieldValues>>
  getValues: UseFormGetValues<TFieldValues>
  trigger: UseFormTrigger<TFieldValues>
}

function SortableRow<TFieldValues extends FieldValues = FieldValues>(
  { id, RowComponent, register, index, remove, errors, getValues, trigger }: SortableRowProps<TFieldValues>
): JSX.Element {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id })
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  }
  const rowComponentProps = {
    draggableProps: { ...attributes, ...listeners },
    register,
    index,
    remove,
    errors,
    getValues,
    trigger,
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
> {
  fields: FieldArrayWithId<TFieldValues, TFieldArrayName, "id">[]
  remove: UseFieldArrayRemove
  move: UseFieldArrayMove
  register: UseFormRegister<TFieldValues>
  errors: FieldErrorsImpl<DeepRequired<TFieldValues>>
  getValues: UseFormGetValues<TFieldValues>
  trigger: UseFormTrigger<TFieldValues>
  RowComponent: (props: RowComponentProps<TFieldValues>) => JSX.Element
  Container: (props: { children: ReactNode}) => JSX.Element
}
export function SortableFields<
  TFieldValues extends FieldValues = FieldValues,
  TFieldArrayName extends FieldArrayPath<TFieldValues> = FieldArrayPath<TFieldValues>,
  // TKeyName extends string = "id"
>(
  { fields, remove, move, register, errors, getValues, trigger, RowComponent, Container }: SortableFieldsProps<TFieldValues, TFieldArrayName>
): JSX.Element {
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  )

  function handleDragEnd({ active, over }: DragEndEvent): void {
    trigger() // trigger input validation

    if (over === null) {
      throw new Error("over is null")
    }

    if (active.id !== over.id) {
      console.log(`replace: ${active.id}, ${over.id}`)
      const oldIndex = fields.map((field) => field.id).indexOf(active.id as string)
      const newIndex = fields.map((field) => field.id).indexOf(over.id as string)

      move(oldIndex, newIndex)
    }
  }

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
              {...{ index, register, errors, getValues, trigger, remove }}
              RowComponent={RowComponent}
            />
          ))}
        </SortableContext>
      </DndContext>
    </Container>
  )
}
