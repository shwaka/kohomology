import { Button } from "@mui/material"
import React, { useMemo } from "react"
import { ShowStyledMessage } from "../styled/ShowStyledMessage"
import { StyledMessage } from "../styled/message"
import { Editor, EditorDialog, useEditorDialog } from "@components/TabDialog"
import { useIdealEditor } from "./useIdealEditor"
import { IdealEditor } from "./IdealEditor"

interface IdealConfigProps {
  setIdealJson: (idealJson: string) => void
  idealInfo: StyledMessage
  idealJson: string
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function IdealConfig({ setIdealJson, idealInfo, idealJson, validateGenerator, validateGeneratorArray }: IdealConfigProps): React.JSX.Element {
  const {
    idealEditorProps,
    getOnSubmit, beforeOpen, disableSubmit, preventQuit,
  } = useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray })
  const editor: Editor = useMemo(() => ({
    getOnSubmit, preventQuit, disableSubmit, beforeOpen,
    renderContent: (_closeDialog) => (
      <IdealEditor {...idealEditorProps}/>
    )
  }), [idealEditorProps, getOnSubmit, preventQuit, disableSubmit, beforeOpen])
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
