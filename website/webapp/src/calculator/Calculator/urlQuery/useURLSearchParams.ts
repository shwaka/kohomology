import { useLocation } from "@docusaurus/router"
import { useMemo } from "react"

export function useURLSearchParams(): URLSearchParams {
  // https://v5.reactrouter.com/web/example/query-parameters
  const { search } = useLocation()
  return useMemo(
    () => new URLSearchParams(search),
    [search]
  )
}
