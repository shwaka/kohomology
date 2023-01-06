import BrowserOnly from "@docusaurus/BrowserOnly"
import TeX from "@matejmazur/react-katex"
import { Button, Container, Divider, FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material"
import { ExhaustivityError } from "@site/src/utils/ExhaustivityError"
import React, { useCallback, useState } from "react"
import "katex/dist/katex.min.css"
import { useDGAEditorDialog } from "../DGAEditorDialog"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { targetNames, TargetName, WorkerOutput, WorkerInfo } from "../worker/workerInterface"
import { ComputeForm } from "./ComputeForm"
import { ShareDGAButton, ShareDGADialog, useShareDGA } from "./ShareDGA"
import { UsageButton, UsageDialog, useUsage } from "./Usage"
import { getCohomologyAsString, TopologicalInvariantAsTex } from "./target"
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

interface CalculatorFormProps {
  printMessages: (result: StyledMessage | StyledMessage[]) => void
  defaultDGAJson: string
}

function CalculatorFormImpl({ printMessages, defaultDGAJson }: CalculatorFormProps): JSX.Element {
  const [dgaInfo, setDgaInfo] = useState<StyledMessage[]>([])
  const [workerInfo, setWorkerInfo] = useState<WorkerInfo>({ status: "idle" })

  const resetWorkerInfo = useCallback(
    (): void => {
      setWorkerInfo({ status: "idle" })
    },
    [setWorkerInfo]
  )

  const onmessage = useCallback(
    (e: MessageEvent<WorkerOutput>): void => {
      const output: WorkerOutput = e.data
      switch (output.command) {
        case "printMessages":
          printMessages(output.messages)
          break
        case "showDgaInfo":
          setDgaInfo(output.messages)
          break
        case "notifyInfo":
          setWorkerInfo(output.info)
          break
      }
    },
    [printMessages, setDgaInfo, setWorkerInfo]
  )
  const { json, setJson, postMessage, restart } = useKohomologyWorker({
    defaultJson: defaultDGAJson,
    onmessage,
    resetWorkerInfo,
  })

  const [targetName, setTargetName] = useState<TargetName>("self")
  const { usageDialogProps, usageButtonProps } = useUsage()
  const { shareDGADialogProps, shareDGAButtonProps } = useShareDGA(json)
  const { TabDialog, tabDialogProps, openDialog } = useDGAEditorDialog(json, setJson)


  // function printError(error: unknown): void {
  //   if (error === null) {
  //     props.printMessages(fromString("error", "This can't happen!"))
  //   } else if (typeof error === "object") {
  //     props.printMessages(fromString("error", error.toString()))
  //   } else {
  //     props.printMessages(fromString("error", "Unknown error!"))
  //   }
  // }

  return (
    <Stack
      direction="column"
      spacing={2}
      divider={<Divider orientation="horizontal"/>}
      sx={{ width: 400, margin: 1 }}
    >
      <StackItem>
        <UsageButton {...usageButtonProps}/>
        <UsageDialog {...usageDialogProps}/>
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
            postMessage(message)
            setWorkerInfo({ status: "computing", progress: null })
          }}
          workerInfo={workerInfo}
        />
      </StackItem>
      <StackItem>
        <Button
          variant="contained" size="small"
          onClick={restart}
        >
          Restart
        </Button>
      </StackItem>
    </Stack>
  )
}

export function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  return (
    <BrowserOnly fallback={<div>Loading...</div>}>
      {() => <CalculatorFormImpl {...props}/>}
    </BrowserOnly>
  )
}
