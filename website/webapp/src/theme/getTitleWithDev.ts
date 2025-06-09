import { isDevelopmentMode } from "@site/src/utils/isDevelopmentMode"

export function getTitleWithDev(title: string): string
export function getTitleWithDev(title: undefined): undefined
export function getTitleWithDev(title: string | undefined): string | undefined
export function getTitleWithDev(title: string | undefined): string | undefined {
  if (title === undefined) {
    return undefined
  } else if (isDevelopmentMode()) {
    return `[dev] ${title}`
  } else {
    return title
  }
}
