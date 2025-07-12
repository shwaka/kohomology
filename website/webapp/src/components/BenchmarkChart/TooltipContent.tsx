import { ReactElement } from "react"

import { Bench } from "./BenchmarkData"
import { BenchWithCommit } from "./BenchmarkDataHandler"
import { TooltipContentProps } from "./useTooltip"

export function TooltipContent(
  { item: benchWithCommit, renderBox }: TooltipContentProps<BenchWithCommit>
): ReactElement {
  const { commit, bench } = benchWithCommit
  return (
    <div>
      <div>
        <a
          href={commit.url} target="_blank" rel="noreferrer"
          style={{
            color: "inherit",
            textDecoration: "underline",
          }}
        >
          {commit.id}
        </a>
      </div>
      <div>
        <div>
          {commit.message}
        </div>
        <div>
          {`${commit.timestamp} committed by @${commit.committer.username}`}
        </div>
        <div>
          {renderBox()}{getBenchResult(bench)}
        </div>
        <div>
          {getExtra(bench)}
        </div>
      </div>
    </div>
  )
}

function getBenchResult(bench: Bench): string {
  const { range, unit, value } = bench
  let result = value.toString() + " " + unit
  if (typeof range === "string") {
    result += " (" + range + ")"
  } else if (range !== undefined) {
    // See https://github.com/benchmark-action/github-action-benchmark
    throw new Error("range must be a string, but was ${range}")
  }
  return result
}

function getExtra(bench: Bench): string {
  const { extra } = bench
  if (extra === undefined) {
    return ""
  }
  if (typeof extra !== "string") {
    // See https://github.com/benchmark-action/github-action-benchmark
    throw new Error("extra must be a string, but was ${extra}")
  }
  return "\n" + extra
}
