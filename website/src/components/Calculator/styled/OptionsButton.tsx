import React, { Fragment } from "react"
import { IconButton } from "@mui/material"
import MoreHorizIcon from "@mui/icons-material/MoreHoriz"

interface UseOptionsButtonReturnValue {
  optionsButtonProps: OptionsButtonProps
}

export function useOptionsButton(containerClass: string): UseOptionsButtonReturnValue {
  const optionsButtonProps: OptionsButtonProps = {
    containerClass,
  }
  return { optionsButtonProps }
}

interface OptionsButtonProps {
  containerClass: string
}

export function OptionsButton({ containerClass }: OptionsButtonProps): JSX.Element {
  return (
    <Fragment>
      <IconButton
        size="small"
        sx={{
          paddingTop: 0, paddingBottom: 0,
          position: "absolute", bottom: "4px", right: 0,
          visibility: "hidden",
          [`.${containerClass}:hover &`]: {
            visibility: "visible",
          },
        }}
      >
        <MoreHorizIcon fontSize="small"/>
      </IconButton>
    </Fragment>
  )
}
