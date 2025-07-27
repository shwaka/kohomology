import { ReactElement, useMemo } from "react"

import localCommits from "@benchmark/localCommits.json"
import { TooltipContentProps } from "@components/ChartUtil/useTooltip"
import { Divider, Stack } from "@mui/material"
import { reverse } from "remeda"

import { BenchWithCommit } from "../BenchmarkDataHandler"
import { groupBySeparatorKeys } from "./groupBySeparatorKeys"
import { ShowCommit } from "./ShowCommit"
import { Bench } from "../schema/benchmarkDataSchema"
import { LocalCommit } from "../schema/localCommitSchema"

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
    <Stack
      spacing={0.5}
      divider={<Divider orientation="horizontal" flexItem />}
      style={{
        maxHeight: "150px",
        overflowY: "auto",
      }}
    >
      {reverse(localCommitsToShow).map((localCommit) => (
        <ShowCommit key={localCommit.id} localCommit={localCommit} />
      ))}
    </Stack>
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
      <div style={{
        display: "flex",
        alignItems: "center",
        gap: "5px",
      }}
      >
        {renderBox()}
        <span>
          {getBenchResult(bench)}
        </span>
      </div>
      <ShowCommits commitHash={commit.id} commitHashArray={commitHashArray} />
    </div>
  )
}

function getBenchResult(bench: Bench): string {
  const { range, unit, value } = bench
  let result = value.toFixed(3) + " " + unit
  if (typeof range === "string") {
    result += " (" + range + ")"
  } else if (range !== undefined) {
    // See https://github.com/benchmark-action/github-action-benchmark
    throw new Error("range must be a string, but was ${range}")
  }
  return result
}

// function getExtra(bench: Bench): string {
//   const { extra } = bench
//   if (extra === undefined) {
//     return ""
//   }
//   if (typeof extra !== "string") {
//     // See https://github.com/benchmark-action/github-action-benchmark
//     throw new Error("extra must be a string, but was ${extra}")
//   }
//   return "\n" + extra
// }
