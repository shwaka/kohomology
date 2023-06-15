import { useEffect } from "react"
import { URLQueryResult } from "./URLQueryResult"

export function useValueOfURLQueryResult<T>(
  urlQueryResult: URLQueryResult<T>,
  defaultValue: T,
  printError: (message: string) => void,
): T {
  useEffect(() => {
    if (urlQueryResult.type === "error") {
      printError(urlQueryResult.message)
    }
  }, [
    printError,
    // urlQueryResult cannot be directly added here
    // since it is an object and changes on every render.
    urlQueryResult.type,
    // @ts-expect-error since `message` exists only on URLQueryResultParseError
    urlQueryResult.message,
  ])

  if (urlQueryResult.type === "success") {
    return urlQueryResult.value
  } else {
    return defaultValue
  }
}
