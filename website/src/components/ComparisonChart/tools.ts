export const tools = ["kohomology", "sage"] as const
export type Tool = (typeof tools)[number]

export const targets = ["FreeLoopSpaceOf2Sphere"] as const
export type Target = (typeof targets)[number]
