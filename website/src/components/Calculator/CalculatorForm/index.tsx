import TeX from "@matejmazur/react-katex"
import { Button, Container, Divider, FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material"
import React, { useCallback, useEffect, useRef, useState } from "react"
import "katex/dist/katex.min.css"
import KohomologyWorker from "worker-loader!../worker/kohomology.worker"
import { StyledMessage, StyledString } from "../worker/styled"
import { targetNames, TargetName, WorkerInput, WorkerOutput } from "../worker/workerInterface"
import { ComputeForm } from "./ComputeForm"
import { JsonEditorDialog } from "./JsonEditor"
import { ShareDGAButton, ShareDGADialog, useShareDGA } from "./ShareDGA"
import { UsageButton, UsageDialog, useUsage } from "./Usage"
import styles from "./styles.module.scss"
import { ComplexAsTex } from "./target"
import { useDefaultDGAJson } from "./urlQuery"

function Text({ content }: { content: string } ): JSX.Element {
  const lines = content.split("\n")
  return (
    <span>
      {lines.map((line, lineNumber) => (
        // If content ends with the newline,
        // the last element of lines is the empty string "".
        // Hence there is no need to write
        //   lineNumber < lines.length - 1 || content.endsWith("\n")
        (lineNumber < lines.length - 1) ? (
          <React.Fragment key={lineNumber}>
            {line}<br/>
          </React.Fragment>
        ): (
          <React.Fragment key={lineNumber}>
            {line}
          </React.Fragment>
        )
      ))}
    </span>
  )
}

function styledStringToJSXElement(styledString: StyledString, key: number): JSX.Element {
  const macros = {
    "\\deg": "|#1|",
  }
  switch (styledString.stringType) {
    case "text":
      return <Text key={key} content={styledString.content}/>
    case "math":
      return <TeX key={key} math={styledString.content} settings={{ output: "html", macros: macros }} />
      // ↑{ output: "html" } is necessary to avoid strange behavior in 'overflow: scroll' (see memo.md for details)
  }
}

export function styledMessageToJSXElement(styledMessage: StyledMessage, key: number = 0): JSX.Element {
  let style: string
  switch (styledMessage.messageType) {
    case "success":
      style = styles.messageSuccess
      break
    case "error":
      style = styles.messageError
      break
  }
  return (
    <div key={key} className={style}>
      {styledMessage.strings.map((styledString, index) => styledStringToJSXElement(styledString, index))}
    </div>
  )
}

function StackItem({ children }: { children: React.ReactNode }): JSX.Element {
  return (
    <Container disableGutters sx={{ paddingLeft: 1, paddingRight: 1 }}>
      {children}
    </Container>
  )
}

interface CalculatorFormProps {
  printMessages: (result: StyledMessage | StyledMessage[]) => void
}

export function CalculatorForm(props: CalculatorFormProps): JSX.Element {
  const defaultDGAJson = useDefaultDGAJson()
  const [json, setJson] = useState(defaultDGAJson)
  const { usageDialogProps, usageButtonProps } = useUsage()
  const { shareDGADialogProps, shareDGAButtonProps } = useShareDGA(json)
  const [editingJson, setEditingJson] = useState(false)
  const [targetName, setTargetName] = useState<TargetName>("self")
  const [dgaInfo, setDgaInfo] = useState<StyledMessage[]>([])

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
      <StackItem>
        <div>
          {dgaInfo.map((styledMessage, index) => styledMessageToJSXElement(styledMessage, index))}
        </div>
        <Stack direction="row" spacing={2} sx={{ marginTop: 0.5 }}>
          <Button
            variant="contained" size="small"
            onClick={() => setEditingJson(true)}
            sx={{ textTransform: "none" }}>
            Edit DGA
          </Button>
          <JsonEditorDialog
            json={json} updateDgaWrapper={setJson}
            finish={() => setEditingJson(false)}
            isOpen={editingJson}
          />
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
