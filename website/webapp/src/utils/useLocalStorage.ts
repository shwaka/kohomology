import { useState, useEffect } from "react"

export function useLocalStorage(key: string, initialValue: string): [string, (value: string) => void] {
  const [value, setValue] = useState(() => {
    const storedValue: string | null = localStorage.getItem(key)
    if (storedValue === null) {
      return initialValue
    } else {
      return storedValue
    }
  })

  useEffect(() => {
    localStorage.setItem(key, value)
  }, [key, value])

  return [value, setValue] as const
}
