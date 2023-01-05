import TeX from "@matejmazur/react-katex"
import { Tabs, Tab, Button, Stack, Alert, Checkbox, FormControlLabel, RadioGroup, Radio } from "@mui/material"
import React, { useCallback, useState } from "react"
import { ShowCohomology, showCohomologyCandidates, TargetName, WorkerInput } from "../worker/workerInterface"
import { ButtonWithProgress } from "./ButtonWithProgress"
import { NumberField, useNumberField } from "./NumberField"
import { StringField, useStringField } from "./StringField"
import { CohomologyAsTex, getCohomologyAsString } from "./target"

export type InputEvent = React.ChangeEvent<HTMLInputElement>

// Tab を切り替えたても minDegree, maxDegree, cocycleString の値が保持されるために
// visible を Props に追加して，component そのものは必ず render するようにした．
interface InternalComputeFormProps {
  targetName: TargetName
  postMessageToWorker: (message: WorkerInput) => void
  visible: boolean
  computing: boolean
  workerProgress: number | null
}

function ComputeCohomologyForm({ targetName, postMessageToWorker, visible, computing, workerProgress }: InternalComputeFormProps): JSX.Element {
  const [minDegree, minDegreeFieldProps] = useNumberField({ label: "", defaultValue: 0 })
  const [maxDegree, maxDegreeFieldProps] = useNumberField({ label: "", defaultValue: 20 })
  const [showCohomology, setShowCohomology] = useState<ShowCohomology>("basis")
  const computeCohomology = useCallback(
    (event: React.FormEvent<HTMLFormElement>): void => {
      event.preventDefault()
      const input: WorkerInput = {
        command: "computeCohomology",
        targetName: targetName,
        minDegree: minDegree,
        maxDegree: maxDegree,
        showCohomology: showCohomology,
      }
      postMessageToWorker(input)
    },
    [targetName, minDegree, maxDegree, showCohomology, postMessageToWorker]
  )
  if (!visible) {
    return <React.Fragment></React.Fragment>
  }
  const supported = isSupported(targetName, "cohomology")
  return (
    <form onSubmit={computeCohomology} data-testid="ComputeCohomologyForm">
      <Stack spacing={1}>
        <span>
          {"Compute cohomology "}
          <CohomologyAsTex targetName={targetName} degree="n"/>
          {" for"}
        </span>
        <Stack direction="row" alignItems="center" justifyContent="center" spacing={1}>
          <NumberField {...minDegreeFieldProps}/>
          <TeX math="\leq n \leq"/>
          <NumberField {...maxDegreeFieldProps}/>
        </Stack>
        <RadioGroup
          row
          value={showCohomology}
          onChange={(event) => setShowCohomology(event.target.value as ShowCohomology)}
        >
          {showCohomologyCandidates.map((showCohomologyForLabel) =>
            <FormControlLabel
              key={showCohomologyForLabel} value={showCohomologyForLabel}
              control={<Radio/>} label={showCohomologyForLabel}/>
          )}
        </RadioGroup>
        <ButtonWithProgress
          type="submit" variant="contained" disabled={!supported}
          computing={computing} progress={workerProgress}
        />
        { !supported &&
          <Alert severity="info">
            Currently, this type of computation is not supported.
          </Alert>
        }
      </Stack>
    </form>
  )
}

function ComputeClassForm({ targetName, postMessageToWorker, visible, computing, workerProgress }: InternalComputeFormProps): JSX.Element {
  const supported = isSupported(targetName, "class")
  const [cocycleString, cocycleStringFieldProps] =
    useStringField({ label: "", defaultValue: "x^2", width: 200, disabled: !supported })
  const [showBasis, setShowBasis] = useState(true)
  const computeCohomologyClass = useCallback(
    (event: React.FormEvent<HTMLFormElement>): void => {
      event.preventDefault()
      const input: WorkerInput = {
        command: "computeCohomologyClass",
        targetName: targetName,
        cocycleString: cocycleString,
        showBasis: showBasis,
      }
      postMessageToWorker(input)
    },
    [targetName, cocycleString, showBasis, postMessageToWorker]
  )
  if (!visible) {
    return <React.Fragment></React.Fragment>
  }
  return (
    <form onSubmit={computeCohomologyClass} data-testid="ComputeClassForm">
      <Stack spacing={1}>
        <span>
          {"Compute cohomology class "}
          <TeX math={`[\\omega] \\in ${getCohomologyAsString(targetName)}`}/>
          {" for"}
        </span>
        <Stack direction="row" alignItems="center" justifyContent="center" spacing={1}>
          <TeX math="\omega ="/>
          <StringField {...cocycleStringFieldProps}/>
        </Stack>
        <FormControlLabel
          control={<Checkbox/>} label="Show basis"
          checked={showBasis}
          onChange={(e) => setShowBasis((e as React.ChangeEvent<HTMLInputElement>).target.checked)}
        />
        <ButtonWithProgress
          type="submit" variant="contained" disabled={!supported}
          computing={computing} progress={workerProgress}
        />
        { !supported &&
          <Alert severity="info">
            Currently, this type of computation is not supported.
          </Alert>
        }
      </Stack>
    </form>
  )
}

type ComputationType = "cohomology" | "class"
function isSupported(targetName: TargetName, computationType: ComputationType): boolean {
  switch (targetName) {
    case "self":
    case "freeLoopSpace":
    case "cyclic":
      return true
    case "derivation":
      switch (computationType) {
        case "cohomology":
          return true
        case "class":
          return false
      }
  }
}

export interface ComputeFormProps {
  targetName: TargetName
  postMessageToWorker: (message: WorkerInput) => void
  computing: boolean
  workerProgress: number | null
}

export function ComputeForm({ targetName, postMessageToWorker, computing, workerProgress }: ComputeFormProps): JSX.Element {
  const [computationType, setComputationType] = useState<ComputationType>("cohomology")
  const handleChange = (event: React.SyntheticEvent, newValue: ComputationType): void => {
    setComputationType(newValue)
  }
  return (
    <React.Fragment>
      <Tabs value={computationType} onChange={handleChange}>
        <Tab value="cohomology" label="Cohomology group" sx={{ textTransform: "none" }}/>
        <Tab value="class" label="Cohomology class" sx={{ textTransform: "none" }}/>
      </Tabs>
      <ComputeCohomologyForm
        targetName={targetName}
        postMessageToWorker={postMessageToWorker}
        visible={computationType === "cohomology"}
        computing={computing}
        workerProgress={workerProgress}
      />
      <ComputeClassForm
        targetName={targetName}
        postMessageToWorker={postMessageToWorker}
        visible={computationType === "class"}
        computing={computing}
        workerProgress={workerProgress}
      />
    </React.Fragment>
  )
}
