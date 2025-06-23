import { Fragment, useState, ReactElement, MouseEvent } from "react"

import MoreHorizIcon from "@mui/icons-material/MoreHoriz"
import { IconButton, Menu, MenuItem } from "@mui/material"

import { MessageOptions } from "./options"

interface UseOptionsButtonArgs {
  containerClass: string
  options: MessageOptions
  showAll: () => void
}

interface UseOptionsButtonReturnValue {
  optionsButtonProps: OptionsButtonProps
  open: boolean
}

export function useOptionsButton({
  containerClass, options, showAll,
}: UseOptionsButtonArgs): UseOptionsButtonReturnValue {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null)
  const open = Boolean(anchorEl)
  const handleClick = (event: MouseEvent<HTMLElement>): void => {
    setAnchorEl(event.currentTarget)
  }
  const handleClose = (): void => {
    setAnchorEl(null)
  }

  const optionsButtonProps: OptionsButtonProps = {
    containerClass, handleClick, handleClose, open, anchorEl, options, showAll,
  }
  return { optionsButtonProps, open }
}

interface OptionsButtonProps {
  containerClass: string
  handleClick: (event: MouseEvent<HTMLElement>) => void
  handleClose: () => void
  open: boolean
  anchorEl: HTMLElement | null
  options: MessageOptions
  showAll: () => void
}

export function OptionsButton({ containerClass, handleClick, handleClose, open, anchorEl, options, showAll }: OptionsButtonProps): ReactElement {
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
        <MenuItem onClick={() => { showAll(); handleClose() }}>
          Show all
        </MenuItem>
      </Menu>
    </Fragment>
  )
}

interface MenuItemCopyTextProps {
  text: string | null
  label: string
  handleClose: () => void
}

function MenuItemCopyText({ text, label, handleClose }: MenuItemCopyTextProps): ReactElement {
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
