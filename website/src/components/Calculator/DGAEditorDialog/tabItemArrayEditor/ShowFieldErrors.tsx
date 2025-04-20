import { Alert } from "@mui/material"
import { motion, AnimatePresence } from "motion/react"
import React from "react"
import { FieldError, MultipleFieldErrors, ValidateResult } from "react-hook-form"
import { magicMessageToHideError } from "./validation"

interface ShowFieldErrorsProps {
  fieldErrors: FieldError[]
}

export function ShowFieldErrors({ fieldErrors }: ShowFieldErrorsProps): React.JSX.Element {
  return (
    <AnimatePresence mode="sync">
      {getMessages({ fieldErrors }).map(({message, errorType}) => {
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
              sx={{ whiteSpace: "pre-wrap" }}
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

function getMessages({ fieldErrors }: { fieldErrors: FieldError[]}): MessageWithType[] {
  return fieldErrors.flatMap((fieldError) => {
    // Just using fieldError.message may be enough
    const types: MultipleFieldErrors | undefined = fieldError.types
    if (types === undefined) {
      return []
    }
    return Object.entries(types).map(([errorType, message]) => ({ errorType, message }))
  })
}
