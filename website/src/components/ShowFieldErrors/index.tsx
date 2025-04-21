import { Alert } from "@mui/material"
import { motion, AnimatePresence } from "motion/react"
import React from "react"
import { FieldError, MultipleFieldErrors, ValidateResult } from "react-hook-form"

export const magicMessageToHideError = "_HIDE_THIS_ERROR_"

interface ShowFieldErrorsProps {
  fieldErrors: FieldError[]
  showAllErrors?: boolean // If this is true, then all errors from fieldError.types are rendered.
}

// Currently, showAllErrors=false is enough to show the way to fix inputs.
// In some future, showAllErrors=true may be useful to show more information.
export function ShowFieldErrors({ fieldErrors, showAllErrors = false }: ShowFieldErrorsProps): React.JSX.Element {
  const messages = showAllErrors ? getAllMessages({ fieldErrors }) : getMainMessages({ fieldErrors })
  return (
    <AnimatePresence mode="sync">
      {messages.map(({ message, errorType }) => {
        if (message === undefined) {
          return undefined
        }
        if (message === magicMessageToHideError) {
          return undefined
        }
        // motion.div must be placed as a DIRECT child of AnimatePresence
        return (
          <motion.div key={`motion-${errorType}-${message}`} {...motionDivProps}>
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

type MessageWithType = {
  message: ValidateResult // string | string[] | boolean | undefined
  errorType: string
}

function getMainMessages({ fieldErrors }: { fieldErrors: FieldError[]}): MessageWithType[] {
  return fieldErrors.flatMap((fieldError) => {
    const message: string | undefined = fieldError.message
    if (message === undefined) {
      return []
    }
    const errorType = ""
    return [{ errorType, message }]
  })
}

function getAllMessages({ fieldErrors }: { fieldErrors: FieldError[]}): MessageWithType[] {
  return fieldErrors.flatMap((fieldError) => {
    const types: MultipleFieldErrors | undefined = fieldError.types
    if (types === undefined) {
      return []
    }
    return Object.entries(types).map(([errorType, message]) => ({ errorType, message }))
  })
}
