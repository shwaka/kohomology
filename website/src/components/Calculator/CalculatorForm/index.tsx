import TeX from "@matejmazur/react-katex"
import { Button, Container, Divider, FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material"
import React, { useCallback, useState } from "react"
import "katex/dist/katex.min.css"
import { useDGAEditorDialog } from "../DGAEditorDialog"
import { sphere } from "../DGAEditorDialog/examples"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { targetNames, TargetName, WorkerOutput, WorkerInfo } from "../worker/workerInterface"
import { ComputeForm } from "./ComputeForm"
import { RestartButton, RestartDialog, useRestart } from "./RestartDialog"
import { ShareDGAButton, ShareDGADialog, useShareDGA } from "./ShareDGADialog"
import { UsageButton, UsageDialog, useUsage } from "./UsageDialog"
import { getCohomologyAsString, TopologicalInvariantAsTex } from "./target"
import { useJsonFromURLQuery } from "./urlQuery"
import { useKohomologyWorker } from "./useKohomologyWorker"

function StackItem({ children, "data-testid": testId }: { children: React.ReactNode, "data-testid"?: string }): JSX.Element {
  return (
    <span data-testid={testId}>
      <Container disableGutters sx={{ paddingLeft: 1, paddingRight: 1 }}>
        {children}
      </Container>
    </span>
  )
}

export function CalculatorForm(): JSX.Element {
  const [dgaInfo, setDgaInfo] = useState<StyledMessage[]>([])
  const [workerInfo, setWorkerInfo] = useState<WorkerInfo>({ status: "idle" })

  const resetWorkerInfo = useCallback(
    (): void => {
      setWorkerInfo({ status: "idle" })
    },
    [setWorkerInfo]
  )

  const onmessage = useCallback(
    (output: WorkerOutput): void => {
      switch (output.command) {
        case "showDgaInfo":
          setDgaInfo(output.messages)
          break
        case "notifyInfo":
          setWorkerInfo(output.info)
          break
      }
    },
    [setDgaInfo, setWorkerInfo]
  )
  const { json, setJson, postMessage, restart } = useKohomologyWorker({
    onmessage,
    resetWorkerInfo,
  })

  const [targetName, setTargetName] = useState<TargetName>("self")
  const { usageDialogProps, usageButtonProps } = useUsage()
  const { restartDialogProps, restartButtonProps } = useRestart(() => {
    restart()
  })
  const { shareDGADialogProps, shareDGAButtonProps } = useShareDGA(json)
  const { TabDialog, tabDialogProps, openDialog } = useDGAEditorDialog(json, setJson)

  return (
    <Stack
      direction="column"
      spacing={2}
      divider={<Divider orientation="horizontal"/>}
      sx={{ width: 400, margin: 1 }}
    >
      <StackItem>
        <Stack
          direction="row"
          spacing={2}
        >
          <UsageButton {...usageButtonProps}/>
          <UsageDialog {...usageDialogProps}/>
          <RestartButton {...restartButtonProps}/>
          <RestartDialog {...restartDialogProps}/>
        </Stack>
      </StackItem>
      <StackItem data-testid="CalculatorForm-StackItem-DGA">
        Input a Sullivan model of a space <TeX math="X"/>:
        <div>
          {dgaInfo.map((styledMessage, index) => (
            <ShowStyledMessage styledMessage={styledMessage} key={index}/>
          ))}
        </div>
        <Stack direction="row" spacing={2} sx={{ marginTop: 0.5 }}>
          <Button
            variant="contained" size="small"
            onClick={openDialog}
            sx={{ textTransform: "none" }}>
            Edit DGA
          </Button>
          <TabDialog {...tabDialogProps}/>
          <ShareDGAButton {...shareDGAButtonProps}/>
          <ShareDGADialog {...shareDGADialogProps}/>
        </Stack>
      </StackItem>
      <StackItem>
        <RadioGroup
          row
          value={targetName}
          onChange={(event) => setTargetName(event.target.value as typeof targetName)}
        >
          {targetNames.map((targetNameForLabel) =>
            <FormControlLabel
              key={targetNameForLabel} value={targetNameForLabel}
              control={<Radio size="small"/>}
              label={<TopologicalInvariantAsTex targetName={targetNameForLabel}/>}/>
          )}
        </RadioGroup>
        <TeX math={`\\cong ${getCohomologyAsString(targetName)}`}/>
      </StackItem>
      <StackItem>
        <ComputeForm
          targetName={targetName}
          postMessageToWorker={(message) => {
            setWorkerInfo({ status: "computing", progress: null })
            postMessage(message)
          }}
          workerInfo={workerInfo}
        />
      </StackItem>
    </Stack>
  )
}
