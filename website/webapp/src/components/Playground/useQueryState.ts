import { useCallback } from "react"

import { useLocation, useHistory } from "@docusaurus/router"

export function useQueryState(key: string, defaultValue: string): [string, (value: string) => void] {
  const location = useLocation()
  const history = useHistory()

  const searchParams = new URLSearchParams(location.search)
  const currentValue = searchParams.get(key) ?? defaultValue

  const setValue = useCallback(
    (value: string) => {
      const newParams = new URLSearchParams(location.search)

      if (value === defaultValue) {
        newParams.delete(key)
      } else {
        newParams.set(key, value)
      }

      history.replace({
        ...location,
        search: newParams.toString(),
      })
    },
    [history, location, key, defaultValue]
  )

  return [currentValue, setValue]
}
