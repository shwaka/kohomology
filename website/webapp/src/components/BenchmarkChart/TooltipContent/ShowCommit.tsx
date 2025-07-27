import { ReactElement } from "react"

import { Stack } from "@mui/material"

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
  const lines = message.split("\n")
  return (
    <Stack
      style={{
        paddingLeft: "3px",
        paddingRight: "3px",
        lineHeight: 1.1,
      }}
    >
      {lines.map((line, index) => (
        <div
          key={index}
          style={{ minHeight: "0.5em" }}
        >
          {line}
        </div>
      ))}
    </Stack>
  )
}

export function ShowCommit(
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
