import { useLocation } from "@docusaurus/router"
import { useMemo } from "react"
import { sphere } from "./examples"

function useQuery(): URLSearchParams {
  // https://v5.reactrouter.com/web/example/query-parameters
  const { search } = useLocation()
  return useMemo(
    () => new URLSearchParams(search),
    [search]
  )
}

interface CreateURLSearchParamsArgs {
  dgaJson: string
}

export function createURLSearchParams(
  { dgaJson }: CreateURLSearchParamsArgs
): URLSearchParams {
  const urlSearchParams = new URLSearchParams()
  urlSearchParams.append("dgaJson", dgaJson)
  return urlSearchParams
}

export function useDefaultDGAJson(): string {
  const urlSearchParams = useQuery()
  const dgaJson: string | null = urlSearchParams.get("dgaJson")
  return (dgaJson !== null) ? dgaJson : sphere(2)
}
