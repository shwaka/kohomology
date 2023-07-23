import React, { Fragment, useState } from "react"
import { IconButton, Menu, MenuItem } from "@mui/material"
import MoreHorizIcon from "@mui/icons-material/MoreHoriz"

interface UseOptionsButtonReturnValue {
  optionsButtonProps: OptionsButtonProps
  open: boolean
}

export function useOptionsButton(containerClass: string): UseOptionsButtonReturnValue {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null)
  const open = Boolean(anchorEl)
  const handleClick = (event: React.MouseEvent<HTMLElement>): void => {
    setAnchorEl(event.currentTarget)
  }
  const handleClose = (): void => {
    setAnchorEl(null)
  }

  const optionsButtonProps: OptionsButtonProps = {
    containerClass, handleClick, handleClose, open, anchorEl,
  }
  return { optionsButtonProps, open }
}

interface OptionsButtonProps {
  containerClass: string
  handleClick: (event: React.MouseEvent<HTMLElement>) => void
  handleClose: () => void
  open: boolean
  anchorEl: HTMLElement | null
}

export function OptionsButton({ containerClass, handleClick, handleClose, open, anchorEl }: OptionsButtonProps): JSX.Element {
  return (
    <Fragment>
      <IconButton
        size="small"
        onClick={handleClick}
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
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
      >
        <MenuItem onClick={handleClose}>
          {'"copy json" button will be added here'}
        </MenuItem>
      </Menu>
    </Fragment>
  )
}
