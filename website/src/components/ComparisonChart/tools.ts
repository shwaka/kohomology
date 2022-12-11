export const tools = ["kohomology", "sage"] as const
export type Tool = (typeof tools)[number]
