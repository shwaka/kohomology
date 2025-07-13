import { exec } from "child_process"
import fs from "fs"
import path from "path"
import util from "util"

import { LocalCommit, localCommitArraySchema } from "@components/BenchmarkChart/localCommitSchema"
import { z } from "zod/v4"

const execAsync = util.promisify(exec)

const valueSeparator = "---value-sep---"
const commitSeparator = "---commit-sep---"

async function gitLog(): Promise<string> {
  const commitFormat = [
    "%H", // commit hash
    "%cn", // committer name
    "%ce", // committer email
    "%ad", // timestamp (ISO)
    "%B" // commit message
  ].join(valueSeparator)
  const { stdout } = await execAsync(
    `git log --pretty=format:"${commitSeparator}${commitFormat}" --date=iso-strict`
  )
  return stdout
}

function parseLogItem(logItem: string): LocalCommit[] {
  const logItemReplaced = logItem.replace(/(\r?\n)+$/, "")
  if (logItemReplaced === "") {
    return []
  }
  const [commitHash, name, email, timestamp, message] =
    logItemReplaced.split(valueSeparator)
  const url = `https://github.com/shwaka/kohomology/commit/${commitHash}`
  const localCommit: LocalCommit = {
    committer: { email, name },
    id: commitHash,
    message,
    timestamp,
    url,
  }
  return [localCommit]
}

async function getLocalCommitArray(): Promise<LocalCommit[]> {
  const log = await gitLog()
  const localCommitArray = log.split(commitSeparator).flatMap(parseLogItem)
  const parseResult = localCommitArraySchema.safeParse(localCommitArray)
  if (parseResult.success) {
    return parseResult.data
  } else {
    console.error(z.prettifyError(parseResult.error))
    throw new Error("parse failed")
  }
}

// This is extremely slow.
// async function getCommitHashList(): Promise<string[]> {
//   const { stdout } = await execAsync("git log --pretty=format:%H")
//   return stdout.split("\n")
// }
//
// async function getLocalCommit(commitHash: string): Promise<LocalCommit> {
//   const { stdout: committerName } =
//     await execAsync(`git log -1 ${commitHash} --pretty=format:%cn`)
//   const { stdout: committerEmail } =
//     await execAsync(`git log -1 ${commitHash} --pretty=format:%ce`)
//   const { stdout: timestamp } =
//     await execAsync(`git log -1 ${commitHash} --pretty=format:%ad --date=iso-strict`)
//   const { stdout: message } =
//     await execAsync(`git log -1 ${commitHash} --no-decorate --pretty=format:%B`)
//   const url = `https://github.com/shwaka/kohomology/commit/${commitHash}`
//   const result = localCommitSchema.parse({
//     committer: { email: committerEmail, name: committerName },
//     id: commitHash,
//     message,
//     timestamp,
//     url,
//   })
//   return result
// }
//
// async function getLocalCommitArray(): Promise<LocalCommit[]> {
//   // This is extremely slow (100s for 4000 commits)
//   const commitHashList = await getCommitHashList()
//   const localCommitArray = await Promise.all(commitHashList.map(getLocalCommit))
//   return localCommitArray
// }

async function collectGitInfo(): Promise<void> {
  const localCommitArray = await getLocalCommitArray()
  const filepath = path.join(__dirname, "../benchmark-data/localCommits.json")
  fs.writeFileSync(filepath, JSON.stringify(localCommitArray, null, 2))
}

void collectGitInfo()
