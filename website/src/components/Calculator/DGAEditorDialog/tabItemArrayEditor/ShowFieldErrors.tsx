import { Alert } from "@mui/material"
import { motion, AnimatePresence } from "motion/react"
import React from "react"
import { FieldError } from "react-hook-form"
import { motionDivProps } from "./motionDivProps"

interface ShowFieldErrorsProps {
  fieldErrors: FieldError[]
}

export function ShowFieldErrors({ fieldErrors }: ShowFieldErrorsProps): React.JSX.Element {
  return (
    <AnimatePresence mode="sync">
      {fieldErrors.map((fieldError) => {
        const message: string | undefined = fieldError.message
        if (message === undefined) {
          return undefined
        }
        // motion.div must be placed as a DIRECT child of AnimatePresence
        return (
          <motion.div key={`motion-${message}`} {...motionDivProps}>
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
