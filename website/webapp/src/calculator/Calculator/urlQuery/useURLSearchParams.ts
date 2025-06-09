import { useMemo } from "react"

import { useLocation } from "@docusaurus/router"

export function useURLSearchParams(): URLSearchParams {
  // https://v5.reactrouter.com/web/example/query-parameters
  const { search } = useLocation()
  return useMemo(
    () => new URLSearchParams(search),
    [search]
  )
}
