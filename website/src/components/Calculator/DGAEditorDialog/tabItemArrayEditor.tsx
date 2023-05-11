import { closestCenter, DndContext, DragEndEvent, KeyboardSensor, PointerSensor, SensorDescriptor, SensorOptions, useSensor, useSensors } from "@dnd-kit/core"
import { restrictToParentElement } from "@dnd-kit/modifiers"
import { SortableContext, sortableKeyboardCoordinates, useSortable, verticalListSortingStrategy } from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import { Add, Delete, DragHandle } from "@mui/icons-material"
import { Alert, Button, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import { validateDifferentialValueOfTheLast } from "kohomology-js"
import React from "react"
import { DeepRequired, FieldArrayWithId, FieldError, FieldErrorsImpl, MultipleFieldErrors, useFieldArray, UseFieldArrayAppend, UseFieldArrayRemove, useForm, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { generatorArrayToPrettyJson } from "../jsonUtils"
import { TabItem } from "./TabDialog"

export interface Generator {
  name: string
  degree: number
  differentialValue: string
}

interface GeneratorFormInput {
  dummy: "dummy"
  generatorArray: Generator[]
}

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as [string, number, string][]
  return arr.map(([name, degree, differentialValue]) => ({ name, degree, differentialValue }))
}

function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(
    ({ name, degree, differentialValue }) => {
      return [name, isNaN(degree) ? 1 : degree, differentialValue] as [string, number, string]
    }
  )
  return generatorArrayToPrettyJson(arr)
}

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm<GeneratorFormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues: {
      generatorArray: jsonToGeneratorArray(args.json)
    }
  })
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  )

  function handleDragEnd(event: DragEndEvent): void {
    trigger() // trigger input validation

    const { active, over } = event

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

  function onSubmit(closeDialog: () => void): void {
    handleSubmit(
      ({generatorArray}) => {
        args.updateDgaWrapper(generatorArrayToJson(generatorArray))
        closeDialog()
      }
    )()
  }
  function beforeOpen(): void {
    const generatorArray = jsonToGeneratorArray(args.json)
    reset({ generatorArray })
  }
  function preventQuit(): string | undefined {
    const generatorArray = getValues().generatorArray
    if (generatorArrayToJson(generatorArray) !== args.json) {
      return "Your input is not saved. Are you sure you want to quit?"
    } else {
      return undefined
    }
  }
  function disableSubmit(): boolean {
    return (errors.generatorArray !== undefined) || (errors.dummy !== undefined)
  }
  const arrayEditorProps: Omit<ArrayEditorProps, "submit"> = {
    register, errors, fields, append, remove, getValues, trigger, sensors, handleDragEnd,
  }
  return {
    label: "Array",
    onSubmit,
    beforeOpen,
    preventQuit,
    disableSubmit,
    render: (closeDialog) => (<ArrayEditor submit={() => onSubmit(closeDialog)} {...arrayEditorProps}/>),
  }
}

function validateDifferentialValue(generatorArray: Generator[], index: number, value: string): true | string {
  if (generatorArray[index].differentialValue !== value) {
    throw new Error("generatorArray[index] and value do not match.")
  }
  const generatorsJson: string = generatorArrayToJson(generatorArray.slice(0, index + 1))
  const validationResult = validateDifferentialValueOfTheLast(generatorsJson)
  if (validationResult.type === "success") {
    return true
  } else {
    return validationResult.message
  }
}

function getFieldError({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number}): JSX.Element | undefined {
  const error = errors.generatorArray?.[index]
  if (error === undefined) {
    return undefined
  }
  return (
    <Stack spacing={0.3}>
      {(["name", "degree", "differentialValue"] as const).map((key) => {
        const errorForKey = error[key]
        if (errorForKey === undefined || errorForKey.message === undefined) {
          return undefined
        }
        return (
          <Alert severity="error" key={key} sx={{ whiteSpace: "pre-wrap" }}>
            {errorForKey.message}
          </Alert>
        )
      })}
    </Stack>
  )
}

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number, key: keyof Generator }): boolean {
  const error: FieldError | undefined = errors.generatorArray?.[index]?.[key]
  return error !== undefined
}

function validateGeneratorDegrees(generatorArray: Generator[]): true | string {
  const positiveCount = generatorArray.filter((generator) => generator.degree > 0).length
  const negativeCount = generatorArray.filter((generator) => generator.degree < 0).length
  if (positiveCount > 0 && negativeCount > 0) {
    return "Cannot mix generators of positive and negative degrees."
  }
  return true
}

function validateGeneratorNames(generatorArray: Generator[]): true | string {
  const names = generatorArray.map((generator) => generator.name)
  const duplicatedNames = names.filter((item, index) => names.indexOf(item) !== index)
  if (duplicatedNames.length === 0) {
    return true
  }
  return "Generator names must be unique. Duplicated names are " + duplicatedNames.map((name) => `"${name}"`).join(", ")
}

function getGlobalError(errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>): JSX.Element | undefined {
  const fieldError: FieldError | undefined = errors.dummy
  if (fieldError === undefined) {
    return undefined
  }
  const types: MultipleFieldErrors | undefined = fieldError.types
  if (types === undefined) {
    return undefined
  }
  return (
    <React.Fragment>
      {Object.entries(types).map(([errorType, message]) => (
        <Alert severity="error" key={errorType}>
          {message}
        </Alert>
      ))}
    </React.Fragment>
  )
}

interface ArrayEditorItemProps {
  id: string
  index: number
  register: UseFormRegister<GeneratorFormInput>
  errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>
  remove: UseFieldArrayRemove
  getValues: UseFormGetValues<GeneratorFormInput>
  trigger: UseFormTrigger<GeneratorFormInput>
}

function ArrayEditorItem({ id, index, register, errors, remove, getValues, trigger }: ArrayEditorItemProps): JSX.Element {
  const { attributes, listeners, setNodeRef: setSortableNodeRef, transform, transition } = useSortable({ id })
  const sortableStyle = {
    transform: CSS.Transform.toString(transform),
    transition,
  }
  const generatorName = getValues().generatorArray[index].name
  return (
    <div data-testid="ArrayEditor-row" ref={setSortableNodeRef} style={sortableStyle}>
      <Stack spacing={1}>
        <Stack direction="row" spacing={1}>
          <TextField
            label="generator"
            sx={{ width: 90 }} size="small"
            {...register(
              `generatorArray.${index}.name` as const,
              { required: "Please enter the name."}
            )}
            onBlur={() => trigger()}
            error={containsError({ errors, index, key: "name" })}
            inputProps={{ "data-testid": "ArrayEditor-input-name" }}
          />
          <TextField
            label={`deg(${generatorName})`} type="number"
            sx={{ width: 80}} size="small"
            {...register(
              `generatorArray.${index}.degree` as const,
              {
                valueAsNumber: true,
                required: "Please enter the degree.",
                validate: (value: number) => value === 0 ? "The degree cannot be 0." : true
              }
            )}
            onBlur={() => trigger()}
            error={containsError({ errors, index, key: "degree" })}
            inputProps={{ "data-testid": "ArrayEditor-input-degree" }}
          />
          <TextField
            label={`d(${generatorName})`}
            sx={{ width: 200 }} size="small"
            {...register(
              `generatorArray.${index}.differentialValue` as const,
              {
                validate: (value: string) =>
                  validateDifferentialValue(getValues().generatorArray, index, value),
                required: "Please enter the value of the differential."
              }
            )}
            onBlur={() => trigger()}
            error={containsError({ errors, index, key: "differentialValue" })}
            inputProps={{ "data-testid": "ArrayEditor-input-differentialValue" }}
          />
          <Tooltip title="Delete this generator">
            <IconButton onClick={() => remove(index)} size="small">
              <Delete fontSize="small"/>
            </IconButton>
          </Tooltip>
          <IconButton
            {...attributes} {...listeners}
            style={{
              cursor: "grab",
              touchAction: "none",
            }}
          >
            <DragHandle/>
          </IconButton>
        </Stack>
        {getFieldError({ errors, index })}
      </Stack>
    </div>
  )
}

interface ArrayEditorProps {
  register: UseFormRegister<GeneratorFormInput>
  errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>
  fields: FieldArrayWithId<GeneratorFormInput, "generatorArray", "id">[]
  append: UseFieldArrayAppend<GeneratorFormInput, "generatorArray">
  remove: UseFieldArrayRemove
  getValues: UseFormGetValues<GeneratorFormInput>
  trigger: UseFormTrigger<GeneratorFormInput>
  submit: () => void
  sensors: SensorDescriptor<SensorOptions>[]
  handleDragEnd: (event: DragEndEvent) => void
}

function ArrayEditor({ register, errors, fields, append, remove, getValues, trigger, submit, sensors, handleDragEnd }: ArrayEditorProps): JSX.Element {
  const onSubmit = (event: React.FormEvent<HTMLFormElement>): void => {
    event.preventDefault()
    submit()
  }
  // <button hidden type="submit"/> is necessary for onSubmit in form
  return (
    <form onSubmit={onSubmit}>
      <Stack spacing={2} sx={{ marginTop: 1 }}>
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
              <ArrayEditorItem
                key={field.id} id={field.id}
                {...{index, register, errors, remove, getValues, trigger}}
              />
            ))}
          </SortableContext>
        </DndContext>
        <Button
          variant="outlined"
          onClick={() => append({ name: "", degree: 1, differentialValue: "0" })}
          startIcon={<Add/>}
          sx={{ textTransform: "none" }}
        >
          Add a generator
        </Button>
        <input
          hidden value="dummy"
          {...register("dummy", {
            validate: {
              positiveAndNegativeDegree: (_) => validateGeneratorDegrees(getValues().generatorArray),
              duplicatedNames: (_) => validateGeneratorNames(getValues().generatorArray),
            }
          })}
        />
        {getGlobalError(errors)}
      </Stack>
      <button hidden type="submit"/>
    </form>
  )
}
