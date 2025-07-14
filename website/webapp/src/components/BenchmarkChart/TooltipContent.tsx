import { ReactElement, useMemo } from "react"

import localCommits from "@benchmark/localCommits.json"

import { BenchWithCommit } from "./BenchmarkDataHandler"
import { Bench } from "./benchmarkDataSchema"
import { groupBySeparatorKeys } from "./groupBySeparatorKeys"
import { LocalCommit } from "./localCommitSchema"
import { TooltipContentProps } from "./useTooltip"

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
    <div>
      {localCommitsToShow.map((localCommit) => (
        <div key={localCommit.id}>
          {localCommit.id}
        </div>
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
      <ShowCommits commitHash={commit.id} commitHashArray={commitHashArray} />
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
