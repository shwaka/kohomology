import React from "react"

import { Alert } from "@mui/material"
import { motion, AnimatePresence } from "motion/react"

import { MessageWithType } from "./MessageWithType"

export const magicMessageToHideError = "_HIDE_THIS_ERROR_"

interface ShowErrorsProps {
  messages: MessageWithType[]
}

export function ShowErrors({ messages }: ShowErrorsProps): React.JSX.Element {
  return (
    <AnimatePresence mode="sync">
      {messages.map(({ message, type }) => {
        if (message === undefined) {
          return undefined
        }
        if (message === magicMessageToHideError) {
          return undefined
        }
        // motion.div must be placed as a DIRECT child of AnimatePresence
        return (
          <motion.div key={`motion-${type}-${message}`} {...motionDivProps}>
            <ShowMessage message={message}/>
          </motion.div>
        )
      })}
    </AnimatePresence>
  )
}

const motionDivProps = {
  initial: { opacity: 0, height: 0 },
  animate: { opacity: 1, height: "auto" },
  exit: { opacity: 0, height: 0 },
  transition: { duration: 0.3 },
  style: { overflow: "hidden", color: "red", marginTop: 4 },
}

interface ShowMessageProps {
  message: string
}

function ShowMessage({ message }: ShowMessageProps): React.JSX.Element {
  return (
    <Alert
      severity="error"
      sx={{
        whiteSpace: "pre-wrap",
        paddingTop: 0,
        paddingBottom: 0,
        paddingLeft: "5px",
        paddingRight: "5px",
      }}
    >
      {message}
    </Alert>
  )
}
