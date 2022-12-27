import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import { isDevelopmentMode } from "./isDevelopmentMode"

export function useDomainUrl(): string {
  const context = useDocusaurusContext()
  if (isDevelopmentMode()) {
    // http://localhost:3000
    return window.location.origin
  } else {
    // https://shwaka.github.io
    return context.siteConfig.url
  }
}
