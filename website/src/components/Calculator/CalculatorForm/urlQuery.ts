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

function compressJson(json: string): string {
  const obj = JSON.parse(json)
  return JSON.stringify(obj, null, undefined)
}

function prettifyJson(json: string): string {
  const obj = JSON.parse(json)
  return JSON.stringify(obj, null, 2)
}

interface CreateURLSearchParamsArgs {
  dgaJson: string
}

export function createURLSearchParams(
  { dgaJson }: CreateURLSearchParamsArgs
): URLSearchParams {
  const urlSearchParams = new URLSearchParams()
  urlSearchParams.append("dgaJson", compressJson(dgaJson))
  return urlSearchParams
}

export function useDefaultDGAJson(): string {
  const urlSearchParams = useQuery()
  const dgaJson: string | null = urlSearchParams.get("dgaJson")
  return (dgaJson !== null) ? prettifyJson(dgaJson) : sphere(2)
}
