import { Button } from "@mui/material"
import React from "react"
import { ShowStyledMessage } from "../styled/ShowStyledMessage"
import { StyledMessage } from "../styled/message"
import { IdealEditorDialog } from "./IdealEditorDialog"
import { useIdealEditorDialog } from "./useIdealEditorDialog"

interface IdealConfigProps {
  setIdealJson: (idealJson: string) => void
  idealInfo: StyledMessage
  idealJson: string
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function IdealConfig({ setIdealJson, idealInfo, idealJson, validateGenerator, validateGeneratorArray }: IdealConfigProps): React.JSX.Element {
  const { openDialog, idealEditorDialogProps } = useIdealEditorDialog({ setIdealJson, idealJson, validateGenerator, validateGeneratorArray })

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

      <IdealEditorDialog {...idealEditorDialogProps}/>
    </div>
  )
}
