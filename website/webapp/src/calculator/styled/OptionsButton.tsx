import MoreHorizIcon from "@mui/icons-material/MoreHoriz"
import { IconButton, Menu, MenuItem } from "@mui/material"
import React, { Fragment, useState } from "react"
import { MessageOptions } from "./options"

interface UseOptionsButtonReturnValue {
  optionsButtonProps: OptionsButtonProps
  open: boolean
}

export function useOptionsButton(containerClass: string, options: MessageOptions): UseOptionsButtonReturnValue {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null)
  const open = Boolean(anchorEl)
  const handleClick = (event: React.MouseEvent<HTMLElement>): void => {
    setAnchorEl(event.currentTarget)
  }
  const handleClose = (): void => {
    setAnchorEl(null)
  }

  const optionsButtonProps: OptionsButtonProps = {
    containerClass, handleClick, handleClose, open, anchorEl, options,
  }
  return { optionsButtonProps, open }
}

interface OptionsButtonProps {
  containerClass: string
  handleClick: (event: React.MouseEvent<HTMLElement>) => void
  handleClose: () => void
  open: boolean
  anchorEl: HTMLElement | null
  options: MessageOptions
}

export function OptionsButton({ containerClass, handleClick, handleClose, open, anchorEl, options }: OptionsButtonProps): React.JSX.Element {
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
        data-testid="OptionsButton"
      >
        <MoreHorizIcon fontSize="small"/>
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
      >
        {options.map((option) => (
          <MenuItemCopyText
            text={option.text}
            label={option.label}
            handleClose={handleClose}
            key={option.label}
          />
        ))}
      </Menu>
    </Fragment>
  )
}

interface MenuItemCopyTextProps {
  text: string | null
  label: string
  handleClose: () => void
}

function MenuItemCopyText({ text, label, handleClose }: MenuItemCopyTextProps): React.JSX.Element {
  const copyText = async (): Promise<void> => {
    if (text !== null) {
      await navigator.clipboard.writeText(text)
      // console.log("Copied:", text)
    }
    handleClose()
  }
  return (
    <MenuItem
      onClick={copyText}
      disabled={text === null}
    >
      {label}
    </MenuItem>
  )
}
