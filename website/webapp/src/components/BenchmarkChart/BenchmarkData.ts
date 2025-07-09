import { z } from "zod/v4"

const toolSchema = z.enum(["cargo", "go", "benchmarkjs", "pytest", "googlecpp", "catch2", "julia", "jmh", "benchmarkdotnet", "benchmarkluau", "customBiggerIsBetter", "customSmallerIsBetter"])

export type Tool = z.infer<typeof toolSchema>

const benchSchema = z.strictObject({
  name: z.string(),
  value: z.number(),
  unit: z.string(),
  range: z.unknown().optional(),
  extra: z.unknown().optional(),
})

export type Bench = z.infer<typeof benchSchema>

const userSchema = z.strictObject({
  email: z.string(),
  name: z.string(),
  username: z.string(),
})

// type User = z.infer<typeof userSchema>

const commitSchema = z.strictObject({
  author: userSchema,
  committer: userSchema,
  distinct: z.boolean().optional(),
  id: z.string().regex(/^[0-9a-f]{40}$/i, "Invalid commit hash"),
  message: z.string(),
  timestamp: z.iso.datetime({ offset: true }),
  tree_id: z.string().optional(),
  url: z.string(),
})

export type Commit = z.infer<typeof commitSchema>

const benchmarkSchema = z.strictObject({
  commit: commitSchema,
  date: z.number(),
  tool: toolSchema,
  benches: z.array(benchSchema),
})

export type Benchmark = z.infer<typeof benchmarkSchema>

export const benchmarkDataSchema = z.strictObject({
  lastUpdate: z.number(),
  repoUrl: z.string(),
  entries: z.strictObject({
    Benchmark: z.array(benchmarkSchema),
  }),
})

export type BenchmarkData = z.infer<typeof benchmarkDataSchema>
