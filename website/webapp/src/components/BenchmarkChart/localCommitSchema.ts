import { z } from "zod/v4"

import { commitHashSchema } from "./benchmarkDataSchema"

const localUserSchema = z.strictObject({
  email: z.email(),
  name: z.string(),
  // username: z.string(), // username in userSchema is the username in GitHub
})

export const localCommitSchema = z.strictObject({
  committer: localUserSchema,
  id: commitHashSchema,
  message: z.string(),
  timestamp: z.iso.datetime({ offset: true }),
  url: z.string(),
})

export type LocalCommit = z.infer<typeof localCommitSchema>

export const localCommitArraySchema = z.array(localCommitSchema)
