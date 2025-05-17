import { Editor, EditorDialog, useEditorDialog } from "@calculator/EditorDialog"
import { ShowStyledMessage } from "@calculator/styled/ShowStyledMessage"
import { StyledMessage } from "@calculator/styled/message"
import { Button } from "@mui/material"
import React, { useMemo } from "react"
import { IdealEditor } from "./IdealEditor"
import { useIdealEditor } from "./useIdealEditor"

interface IdealConfigProps {
  setIdealJson: (idealJson: string) => void
  idealInfo: StyledMessage
  idealJson: string
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function IdealConfig({ setIdealJson, idealInfo, idealJson, validateGenerator, validateGeneratorArray }: IdealConfigProps): React.JSX.Element {
  const {
    idealEditorPropsExceptOnSubmit,
    getOnSubmit, beforeOpen, disableSubmit, preventQuit,
  } = useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray })
  const editor: Editor = useMemo(() => ({
    getOnSubmit, preventQuit, disableSubmit, beforeOpen,
    renderContent: (closeDialog) => (
      <IdealEditor
        {...idealEditorPropsExceptOnSubmit}
        onSubmit={getOnSubmit(closeDialog)}
      />
    )
  }), [idealEditorPropsExceptOnSubmit, getOnSubmit, preventQuit, disableSubmit, beforeOpen])
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })

  return (
    <div>
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

      <EditorDialog {...editorDialogProps}/>
    </div>
  )
}
