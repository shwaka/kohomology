import { z } from "zod/v4"

export const commitHashSchema = z.string().regex(/^[0-9a-f]{40}$/i, "Invalid commit hash")
