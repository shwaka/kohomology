import { CriteriaMode, FieldError, MultipleFieldErrors, ValidateResult } from "react-hook-form"

import { MessageWithType } from "../ShowErrors"

type GetMessagesArgs = {
  fieldErrorArray: (FieldError | undefined)[]
  criteriaMode: CriteriaMode
}

export function getMessages({ fieldErrorArray, criteriaMode }: GetMessagesArgs): MessageWithType[] {
  switch (criteriaMode) {
    case "all":
      return fieldErrorArray.flatMap(getAllMessagesFromFieldError)
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

function getAllMessagesFromFieldError(fieldError: FieldError | undefined): MessageWithType[] {
  const types: MultipleFieldErrors | undefined = fieldError?.types
  if (types === undefined) {
    // returns a list of length 0 or 1
    return getMessageFromFieldError(fieldError)
  }
  // returns a list of arbitrary length
  return Object.entries(types).map(([type, message]) => ({
    type,
    message: convertValidateResult(message),
  }))
}

function convertValidateResult(message: ValidateResult): string | undefined {
  // type ValidateResult = string | string[] | boolean | undefined
  if ((message === undefined) || (typeof message === "string")) {
    return message
  }
  if (typeof message === "boolean") {
    if (message) {
      return undefined
    } else {
      return "Unknown error"
    }
  }
  // message: string[]
  return message.join("\n")
}
