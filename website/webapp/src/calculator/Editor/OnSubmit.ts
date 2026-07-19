import { type BaseSyntheticEvent } from "react"

export type OnSubmit = (e?: BaseSyntheticEvent) => Promise<void>
