import { useCallback, useState, ReactElement } from "react"

export interface UseRadioArgs {
  name: string
  candidates: string[]
  defaultValue: string
}

export interface UseRadioReturnValue {
  value: string
  renderRadio: () => ReactElement
}

export function useRadio({
  candidates, defaultValue, name,
}: UseRadioArgs): UseRadioReturnValue {
  const [value, setValue] = useState(defaultValue)
  const renderRadio = useCallback(() => (
    <span>
      {name}:
      {candidates.map((_value) => (
        <label key={_value}>
          <input
            type="radio"
            value={_value}
            checked={_value === value}
            onChange={(e) => setValue(e.target.value)}
          />
          {_value}
        </label>
      ))}
    </span>
  ), [name, candidates, value, setValue])
  return { value, renderRadio }
}
