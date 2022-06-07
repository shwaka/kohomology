import TeX from "@matejmazur/react-katex"
import { Button, Container, Divider, FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material"
import React, { useCallback, useEffect, useRef, useState } from "react"
import "katex/dist/katex.min.css"
import KohomologyWorker from "worker-loader!./kohomology.worker"
import { JsonEditorDialog } from "./JsonEditor"
import Usage from "./_usage.mdx"
import { sphere } from "./examples"
import { StyledMessage, StyledString } from "./styled"
import styles from "./styles.module.scss"
import { targetNames, TargetName, WorkerInput, WorkerOutput } from "./workerInterface"
import { ComputeForm, InputEvent, useComputeForm } from "./ComputeForm"

function styledStringToJSXElement(styledString: StyledString, key: number): JSX.Element {
  const macros = {
    "\\deg": "|#1|",
  }
  switch (styledString.stringType) {
    case "text":
      return <span key={key}>{styledString.content}</span>
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

function targetNameToTex(targetName: TargetName): JSX.Element {
  switch (targetName) {
    case "self":
      return <TeX math="\wedge V"/>
    case "freeLoopSpace":
      return <TeX math="\wedge V \otimes \wedge \overline{V}"/>
    case "cyclic":
      return <TeX math="\wedge u \otimes\wedge V \otimes \wedge \overline{V}"/>
  }
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
  const [json, setJson] = useState(sphere(2))
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

  const [computeFormProps] = useComputeForm({ targetName, worker })

  return (
    <div className={styles.calculatorForm}>
      <Stack
        direction="column"
        spacing={2}
        divider={<Divider orientation="horizontal"/>}
      >
        <details>
          <summary>Usage</summary>
          <div className={styles.usage}>
            <Usage />
          </div>
        </details>
        <StackItem>
          <div>
            {dgaInfo.map((styledMessage, index) => styledMessageToJSXElement(styledMessage, index))}
          </div>
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
          {targetNameToTex(targetName)}
        </StackItem>
        <StackItem>
          <ComputeForm {...computeFormProps}/>
        </StackItem>
      </Stack>
    </div>
  )
}
