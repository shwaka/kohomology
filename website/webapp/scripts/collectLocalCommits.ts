import { exec } from "child_process"
import fs from "fs"
import path from "path"
import util from "util"

import { LocalCommit, localCommitSchema } from "@components/BenchmarkChart/localCommitSchema"

const execAsync = util.promisify(exec)

async function getCommitHashList(): Promise<string[]> {
  const { stdout } = await execAsync("git log --pretty=format:%H")
  return stdout.split("\n")
}

async function getLocalCommit(commitHash: string): Promise<LocalCommit> {
  const { stdout: committerName } =
    await execAsync(`git log -1 ${commitHash} --pretty=format:%cn`)
  const { stdout: committerEmail } =
    await execAsync(`git log -1 ${commitHash} --pretty=format:%ce`)
  const { stdout: timestamp } =
    await execAsync(`git log -1 ${commitHash} --pretty=format:%ad --date=iso-strict`)
  const { stdout: message } =
    await execAsync(`git log -1 ${commitHash} --no-decorate --pretty=format:%B`)
  const url = `https://github.com/shwaka/kohomology/commit/${commitHash}`
  const result = localCommitSchema.parse({
    committer: { email: committerEmail, name: committerName },
    id: commitHash,
    message,
    timestamp,
    url,
  })
  return result
}

async function collectGitInfo(): Promise<void> {
  const commitHashList = await getCommitHashList()
  const localCommitList = await Promise.all(commitHashList.map(getLocalCommit))

  const filepath = path.join(__dirname, "../benchmark-data/localCommits.json")
  fs.writeFileSync(filepath, JSON.stringify(localCommitList, null, 2))
}

collectGitInfo()
