import React, { useMemo } from "react"

import { Editor, EditorDialog, useEditorDialog } from "@calculator/EditorDialog"
import { StyledMessage } from "@calculator/styled/message"
import { ShowStyledMessage } from "@calculator/styled/ShowStyledMessage"
import { Button } from "@mui/material"

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
  const editor = useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray })
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
