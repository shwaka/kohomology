import React, { ReactElement } from "react"

export type OnSubmit = (e?: React.BaseSyntheticEvent) => Promise<void>
