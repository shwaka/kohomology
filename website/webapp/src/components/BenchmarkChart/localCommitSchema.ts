import { z } from "zod/v4"

import { commitHashSchema } from "./benchmarkDataSchema"

export const localCommitSchema = z.strictObject({
  commiter: z.string(),
  id: commitHashSchema,
  message: z.string(),
  timestamp: z.iso.datetime({ offset: true }),
  url: z.string(),
})
