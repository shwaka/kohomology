import { Add, Delete, DragHandle } from "@mui/icons-material"
import { Alert, Button, Dialog, DialogActions, DialogContent, IconButton, Stack, TextField, Tooltip } from "@mui/material"
import React, { ReactNode, useCallback, useMemo, useState } from "react"
import { Control, DeepRequired, FieldArrayWithId, FieldErrorsImpl, useFieldArray, UseFieldArrayAppend, UseFieldArrayMove, UseFieldArrayRemove, useForm, UseFormGetValues, UseFormRegister, UseFormTrigger } from "react-hook-form"
import { FormData, RowComponentProps, SortableFields } from "../DGAEditorDialog/SortableFields"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { StringField, useStringField } from "./StringField"

interface UseIdealFormDialogArgs {
  idealJson: string
  setIdealJson: (idealJson: string) => void
}

interface UseIdealFormDialogReturnValue {
  openDialog: () => void
  idealFormDialogProps: IdealFormDialogProps
}

interface Generator {
  text: string
}

interface IdealFormInput {
  generatorArray: Generator[]
}

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as string[]
  return arr.map((text) => ({ text }))
}

function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(({ text }) => text)
  return JSON.stringify(arr)
}

function useIdealFormDialog({
  idealJson,
  setIdealJson,
}: UseIdealFormDialogArgs): UseIdealFormDialogReturnValue {
  const [open, setOpen] = useState(false)
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors } } = useForm<IdealFormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues: {
      generatorArray: jsonToGeneratorArray(idealJson)
    }
  })

  const openDialog = useCallback((): void => {
    const generatorArray = jsonToGeneratorArray(idealJson)
    reset({ generatorArray })
    setOpen(true)
  }, [setOpen, idealJson, reset])

  const closeDialog = useCallback((): void => {
    setOpen(false)
  }, [setOpen])

  const onSubmit = useCallback((): void => {
    handleSubmit(
      ({ generatorArray }) => {
        setIdealJson(generatorArrayToJson(generatorArray))
        closeDialog()
      }
    )()
  }, [closeDialog, setIdealJson, handleSubmit])

  const idealFormDialogProps: IdealFormDialogProps = useMemo(() => ({
    open, onSubmit, closeDialog,
    register, getValues, errors, trigger, control,
  }), [open, onSubmit, closeDialog, register, getValues, errors, trigger, control])

  return {
    openDialog,
    idealFormDialogProps,
  }
}

function IdealFormDialogItem(
  { draggableProps, index, formData: { register, errors, remove, getValues, trigger } }: RowComponentProps<IdealFormInput>
): JSX.Element {
  return (
    <Stack direction="row" spacing={1}>
      <TextField
        label="generator"
        sx={{ width: 200 }} size="small"
        {...register(
          `generatorArray.${index}.text` as const,
          {
            required: "Please enter the generator.",
          }
        )}
      />
      <Tooltip title="Delete this generator">
        <IconButton
          onClick={() => { remove(index); trigger() }}
          size="small"
        >
          <Delete fontSize="small"/>
        </IconButton>
      </Tooltip>
      <IconButton
        {...draggableProps}
        style={{
          cursor: "grab",
          touchAction: "none",
        }}
      >
        <DragHandle/>
      </IconButton>
    </Stack>
  )
}

function SortableFieldsContainer({ children }: { children: ReactNode }): JSX.Element {
  return (
    <Stack spacing={2}>
      {children}
    </Stack>
  )
}

interface IdealFormDialogProps {
  open: boolean
  onSubmit: () => void
  closeDialog: () => void
  register: UseFormRegister<IdealFormInput>
  getValues: UseFormGetValues<IdealFormInput>
  errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>
  trigger: UseFormTrigger<IdealFormInput>
  control: Control<IdealFormInput>
}

function IdealFormDialog({
  open, onSubmit, closeDialog,
  register, getValues, errors, trigger, control,
}: IdealFormDialogProps): JSX.Element {
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
  })
  const formData: FormData<IdealFormInput> = {
    register, remove, errors, getValues, trigger
  }

  return (
    <Dialog
      open={open}
      onClose={closeDialog}
    >
      <DialogContent>
        <SortableFields
          RowComponent={IdealFormDialogItem}
          Container={SortableFieldsContainer}
          {...{ fields, move, formData }}
        />
        <Button
          variant="outlined"
          onClick={() => append({ text: "" })}
          startIcon={<Add/>}
          sx={{ textTransform: "none" }}
        >
          Add a generator
        </Button>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={onSubmit}
          variant="contained"
          sx={{ textTransform: "none" }}
        >
          Apply
        </Button>
      </DialogActions>
    </Dialog>
  )
}

interface IdealConfigProms {
  setIdealJson: (idealJson: string) => void
  idealInfo: StyledMessage
  idealJson: string
}

export function IdealConfig({ setIdealJson, idealInfo, idealJson }: IdealFormProms): JSX.Element {
  const { openDialog, idealFormDialogProps } = useIdealFormDialog({ setIdealJson, idealJson })

  return (
    <div>
      <Alert severity="warning">
        This is an experimental feature
        and may contain some bugs!
      </Alert>

      <ShowStyledMessage
        styledMessage={idealInfo}
      />

      <Button
        onClick={openDialog}
        variant="contained"
        sx={{ textTransform: "none" }}
      >
        Edit ideal
      </Button>

      <IdealFormDialog {...idealFormDialogProps}/>
    </div>
  )
}
