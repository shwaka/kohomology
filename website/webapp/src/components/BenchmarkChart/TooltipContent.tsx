import { ReactElement, useMemo } from "react"

import localCommits from "@benchmark/localCommits.json"
import { reverse } from "remeda"

import { BenchWithCommit } from "./BenchmarkDataHandler"
import { Bench } from "./benchmarkDataSchema"
import { groupBySeparatorKeys } from "./groupBySeparatorKeys"
import { LocalCommit } from "./localCommitSchema"
import { TooltipContentProps } from "./useTooltip"

function ShowCommit(
  { localCommit }: { localCommit: LocalCommit }
): ReactElement {
  const commitHash = localCommit.id.slice(0, 7)
  return (
    <div>
      <div>
        <a
          href={localCommit.url} target="_blank" rel="noreferrer"
          style={{
            color: "inherit",
            textDecoration: "underline",
            fontFamily: "monospace",
            marginRight: "3px",
          }}
        >
          {commitHash}
        </a>
        <span>
          {localCommit.timestamp}
        </span>
      </div>
      <div>
        {localCommit.message}
      </div>
    </div>
  )
}

function ShowCommits(
  { commitHash, commitHashArray }: {
    commitHash: string
    commitHashArray: string[]
  }
): ReactElement {
  const groups = useMemo(() => (
    groupBySeparatorKeys(
      localCommits as LocalCommit[],
      commitHashArray,
      (localCommit) => localCommit.id,
    )
  ), [commitHashArray])
  const localCommitsToShow: LocalCommit[] | undefined = groups.get(commitHash)
  if (localCommitsToShow === undefined) {
    throw new Error(`commit hash ${commitHash} not found`)
  }
  return (
    <div
      style={{
        maxHeight: "150px",
        overflowY: "auto",
      }}
    >
      {reverse(localCommitsToShow).map((localCommit) => (
        <ShowCommit key={localCommit.id} localCommit={localCommit} />
      ))}
    </div>
  )
}

export function TooltipContent(
  {
    item: benchWithCommit,
    renderBox,
    globalData: commitHashArray,
  }: TooltipContentProps<BenchWithCommit, string[]>
): ReactElement {
  const { commit, bench } = benchWithCommit
  return (
    <div>
      <div>
        {renderBox()}{getBenchResult(bench)}
      </div>
      <ShowCommits commitHash={commit.id} commitHashArray={commitHashArray} />
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
