import { FieldError, MultipleFieldErrors, ValidateResult } from "react-hook-form"

export type MessageWithType = {
  message: ValidateResult // string | string[] | boolean | undefined
  type: string
}

type GetMessagesArgs = {
  fieldErrors: (FieldError | undefined)[]
  showAllErrors: boolean
}

export function getMessages({ fieldErrors, showAllErrors }: GetMessagesArgs): MessageWithType[] {
  if (showAllErrors) {
    return getAllMessages({ fieldErrors })
  } else {
    return getMainMessages({ fieldErrors })
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

function getMainMessages(
  { fieldErrors }: { fieldErrors: (FieldError | undefined)[]}
): MessageWithType[] {
  return fieldErrors.flatMap(getMessageFromFieldError)
}

function getAllMessages(
  { fieldErrors }: { fieldErrors: (FieldError | undefined)[]}
): MessageWithType[] {
  return fieldErrors.flatMap((fieldError) => {
    const types: MultipleFieldErrors | undefined = fieldError?.types
    if (types === undefined) {
      return getMessageFromFieldError(fieldError)
    }
    return Object.entries(types).map(([type, message]) => ({ type, message }))
  })
}
