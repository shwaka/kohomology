import React, { ReactElement } from "react"

import { CriteriaMode, FieldError } from "react-hook-form"

import { getMessages } from "./getMessages"
import { ShowErrors, magicMessageToHideError } from "../ShowErrors"

export { magicMessageToHideError }

interface ShowFieldErrorsProps {
  fieldErrorArray: (FieldError | undefined)[]
  criteriaMode?: CriteriaMode
}

// Currently, showAllErrors=false is enough to show the way to fix inputs.
// In some future, showAllErrors=true may be useful to show more information.
export function ShowFieldErrors({ fieldErrorArray, criteriaMode = "firstError" }: ShowFieldErrorsProps): ReactElement {
  const messages = getMessages({ fieldErrorArray, criteriaMode })
  return (
    <ShowErrors messages={messages}/>
  )
}
