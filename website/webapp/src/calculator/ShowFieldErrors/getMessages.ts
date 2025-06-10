import { CriteriaMode, FieldError, MultipleFieldErrors } from "react-hook-form"

import { MessageWithType } from "./MessageWithType"

type GetMessagesArgs = {
  fieldErrorArray: (FieldError | undefined)[]
  criteriaMode: CriteriaMode
}

export function getMessages({ fieldErrorArray, criteriaMode }: GetMessagesArgs): MessageWithType[] {
  switch (criteriaMode) {
    case "all":
      return fieldErrorArray.flatMap((fieldError) => {
        const types: MultipleFieldErrors | undefined = fieldError?.types
        if (types === undefined) {
          return getMessageFromFieldError(fieldError)
        }
        return Object.entries(types).map(([type, message]) => ({ type, message }))
      })
    case "firstError":
      return fieldErrorArray.flatMap(getMessageFromFieldError)
  }
}

// returns a list of length 0 or 1
function getMessageFromFieldError(fieldError: FieldError | undefined): MessageWithType[] {
  if (fieldError === undefined) {
    return []
  }
  const message: string | undefined = fieldError?.message
  if (message === undefined) {
    return []
  }
  const type: string = fieldError.type
  return [{ type, message }]
}
