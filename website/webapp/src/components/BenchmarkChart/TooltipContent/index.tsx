import { ReactElement, useMemo } from "react"

import localCommits from "@benchmark/localCommits.json"
import { TooltipContentProps } from "@components/ChartUtil/useTooltip"
import { Divider, Stack } from "@mui/material"
import { reverse } from "remeda"

import { BenchWithCommit } from "../BenchmarkDataHandler"
import { groupBySeparatorKeys } from "./groupBySeparatorKeys"
import { Bench } from "../schema/benchmarkDataSchema"
import { LocalCommit } from "../schema/localCommitSchema"

function ShowTimestamp({ timestamp }: { timestamp: string }): ReactElement {
  const regex = /^(\d{4}-\d{2}-\d{2})T(\d{2}:\d{2}:\d{2})([+-]\d{2}:\d{2})$/
  const match = timestamp.match(regex)

  if (match) {
    const [, date, time, timezone] = match
    return (
      <span>
        {`${date} ${time} (UTC${timezone})`}
      </span>
    )
  } else {
    return (
      <span>{timestamp}</span>
    )
  }
}

function ShowCommitHeader(
  { url, commitHash, timestamp }: {
    url: string
    commitHash: string
    timestamp: string
  }
): ReactElement {
  return (
    <div>
      <a
        href={url} target="_blank" rel="noreferrer"
        style={{
          color: "inherit",
          textDecoration: "underline",
          fontFamily: "monospace",
          paddingRight: "3px",
        }}
      >
        {commitHash}
      </a>
      <span>
        <ShowTimestamp timestamp={timestamp} />
      </span>
    </div>
  )
}

function ShowMessage({ message }: { message: string }): ReactElement {
  return (
    <div
      style={{
        paddingLeft: "3px",
        lineHeight: 1.1,
        whiteSpace: "pre-wrap",
      }}
    >
      {message}
    </div>
  )
}

function ShowCommit(
  { localCommit }: { localCommit: LocalCommit }
): ReactElement {
  const commitHash = localCommit.id.slice(0, 7)
  return (
    <div>
      <ShowCommitHeader
        url={localCommit.url}
        timestamp={localCommit.timestamp}
        commitHash={commitHash}
      />
      <ShowMessage message={localCommit.message} />
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
