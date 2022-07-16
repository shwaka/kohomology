import { Button, Container, Divider, FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material"
import React, { useCallback, useEffect, useRef, useState } from "react"
import "katex/dist/katex.min.css"
import KohomologyWorker from "worker-loader!../worker/kohomology.worker"
import { TabDialog, useTabDialog } from "../TabDialog"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { targetNames, TargetName, WorkerInput, WorkerOutput } from "../worker/workerInterface"
import { ComputeForm } from "./ComputeForm"
import { ShareDGAButton, ShareDGADialog, useShareDGA } from "./ShareDGA"
import { UsageButton, UsageDialog, useUsage } from "./Usage"
import { useTabItemExampleSelector } from "./tabItemExampleSelector"
import { useTabItemJsonEditor } from "./tabItemJsonEditor"
import { ComplexAsTex } from "./target"
import { useTabItemArrayEditor } from "./tabItemArrayEditor"

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

export function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const [json, setJson] = useState(props.defaultDGAJson)
  const { usageDialogProps, usageButtonProps } = useUsage()
  const { shareDGADialogProps, shareDGAButtonProps } = useShareDGA(json)
  const [targetName, setTargetName] = useState<TargetName>("self")
  const [dgaInfo, setDgaInfo] = useState<StyledMessage[]>([])
  const tabItems = [
    useTabItemJsonEditor({ json, updateDgaWrapper: setJson }),
    useTabItemArrayEditor({ json, updateDgaWrapper: setJson }),
    useTabItemExampleSelector({ updateDgaWrapper: setJson }),
  ]
  const { tabDialogProps, openDialog } = useTabDialog(tabItems, "json")

  // Worker cannot be accessed during SSR (Server Side Rendering)
  // To avoid SSR, this component should be wrapped in BrowserOnly
  //   (see https://docusaurus.io/docs/docusaurus-core#browseronly)
  const workerRef = useRef(new KohomologyWorker())
  const worker: KohomologyWorker = workerRef.current

  worker.onmessage = (e: MessageEvent<WorkerOutput>) => {
    const output: WorkerOutput = e.data
    switch (output.command) {
      case "printMessages":
        props.printMessages(output.messages)
        break
      case "showDgaInfo":
        setDgaInfo(output.messages)
        break
    }
  }

  // function printError(error: unknown): void {
  //   if (error === null) {
  //     props.printMessages(fromString("error", "This can't happen!"))
  //   } else if (typeof error === "object") {
  //     props.printMessages(fromString("error", error.toString()))
  //   } else {
  //     props.printMessages(fromString("error", "Unknown error!"))
  //   }
  // }

  const applyJson = useCallback(
    (json: string): void => {
      // setJson(json)
      const inputUpdate: WorkerInput = {
        command: "updateJson",
        json: json,
      }
      worker.postMessage(inputUpdate)
      const inputShowInfo: WorkerInput = {
        command: "dgaInfo"
      }
      worker.postMessage(inputShowInfo)
    },
    [worker]
  )

  useEffect(() => {
    applyJson(json)
  }, [json, applyJson])

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
              control={<Radio/>} label={targetNameForLabel}/>
          )}
        </RadioGroup>
        {"Computation target: "}
        <ComplexAsTex targetName={targetName}/>
      </StackItem>
      <StackItem>
        <ComputeForm targetName={targetName} postMessageToWorker={(message) => worker.postMessage(message)}/>
      </StackItem>
    </Stack>
  )
}
