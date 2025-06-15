import React, { useCallback, useState } from "react"

import TeX from "@matejmazur/react-katex"
import { Tabs, Tab, Stack, Alert, Checkbox, FormControlLabel, RadioGroup, Radio } from "@mui/material"

import { ButtonWithProgress } from "./ButtonWithProgress"
import { NumberField, useNumberField } from "./NumberField"
import { StringField, useStringField } from "./StringField"
import { CohomologyAsTex, getCohomologyAsString } from "./target"
import { ShowCohomology, showCohomologyCandidates, TargetName, WorkerInfo, KohomologyWorkerInput } from "../kohomologyWorker/workerInterface"

export type InputEvent = React.ChangeEvent<HTMLInputElement>

function destructureWorkerInfo(
  workerInfo: WorkerInfo
): { computing: boolean, workerProgress: number | null, message: string | undefined } {
  const computing = (workerInfo.status === "computing")
  const workerProgress: number | null = (workerInfo.status === "computing") ? workerInfo.progress : null
  const message: string | undefined = (workerInfo.status === "computing") ? workerInfo.message : undefined
  return { computing, workerProgress, message }
}

// Tab を切り替えたても minDegree, maxDegree, cocycleString の値が保持されるために
// visible を Props に追加して，component そのものは必ず render するようにした．
interface InternalComputeFormProps {
  targetName: TargetName
  postMessageToWorker: (message: KohomologyWorkerInput) => void
  visible: boolean
  workerInfo: WorkerInfo
}

function ComputeCohomologyForm({ targetName, postMessageToWorker, visible, workerInfo }: InternalComputeFormProps): React.JSX.Element {
  const [minDegree, minDegreeFieldProps] = useNumberField({ label: "", defaultValue: 0 })
  const [maxDegree, maxDegreeFieldProps] = useNumberField({ label: "", defaultValue: 20 })
  const [showCohomology, setShowCohomology] = useState<ShowCohomology>("basis")
  const computeCohomology = useCallback(
    (event: React.FormEvent<HTMLFormElement>): void => {
      event.preventDefault()
      const input: KohomologyWorkerInput = {
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
  const { computing, workerProgress } = destructureWorkerInfo(workerInfo)

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

function ComputeClassForm({ targetName, postMessageToWorker, visible, workerInfo }: InternalComputeFormProps): React.JSX.Element {
  const supported = isSupported(targetName, "class")
  const [cocycleString, cocycleStringFieldProps] =
    useStringField({ label: "", defaultValue: "x^2", width: 200, disabled: !supported })
  const [showBasis, setShowBasis] = useState(true)
  const computeCohomologyClass = useCallback(
    (event: React.FormEvent<HTMLFormElement>): void => {
      event.preventDefault()
      const input: KohomologyWorkerInput = {
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

  const { computing, workerProgress } = destructureWorkerInfo(workerInfo)

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

function ComputeMinimalModelForm({ targetName, postMessageToWorker, visible, workerInfo }: InternalComputeFormProps): React.JSX.Element {
  const [isomorphismUpTo, isomorphismUpToFieldProps] = useNumberField({ label: "", defaultValue: 10 })
  const computeMinimalModel = useCallback(
    (event: React.FormEvent<HTMLFormElement>): void => {
      event.preventDefault()
      const input: KohomologyWorkerInput = {
        command: "computeMinimalModel",
        targetName: targetName,
        isomorphismUpTo,
      }
      postMessageToWorker(input)
    },
    [targetName, isomorphismUpTo, postMessageToWorker]
  )

  if (!visible) {
    return <React.Fragment></React.Fragment>
  }

  const supported = isSupported(targetName, "minimal")
  const { computing, workerProgress, message } = destructureWorkerInfo(workerInfo)

  return (
    <form onSubmit={computeMinimalModel} data-testid="ComputeMinimalModel">
      <Stack spacing={1}>
        <Stack direction="row" alignItems="center" justifyContent="center" spacing={1}>
          {"Compute minimal model until degree"}
          <NumberField {...isomorphismUpToFieldProps}/>
        </Stack>
        <ButtonWithProgress
          type="submit" variant="contained" disabled={!supported}
          computing={computing} progress={workerProgress} message={message}
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

type ComputationType = "cohomology" | "class" | "minimal"
function isSupported(targetName: TargetName, computationType: ComputationType): boolean {
  switch (targetName) {
    case "self":
    case "cyclic":
    case "idealQuot":
      return true
    case "freeLoopSpace":
      switch (computationType) {
        case "cohomology":
        case "class":
          return true
        case "minimal":
          return false
      }
    // Disable no-fallthrough since it is false-positive
    // eslint-disable-next-line no-fallthrough
    case "derivation":
      switch (computationType) {
        case "cohomology":
          return true
        case "class":
        case "minimal":
          return false
      }
  }
}

export interface ComputeFormProps {
  targetName: TargetName
  postMessageToWorker: (message: KohomologyWorkerInput) => void
  workerInfo: WorkerInfo
}

export function ComputeForm({ targetName, postMessageToWorker, workerInfo }: ComputeFormProps): React.JSX.Element {
  const [computationType, setComputationType] = useState<ComputationType>("cohomology")
  const handleChange = (_event: React.SyntheticEvent, newValue: ComputationType): void => {
    setComputationType(newValue)
  }
  return (
    <React.Fragment>
      <Tabs value={computationType} onChange={handleChange}>
        <Tab value="cohomology" label="Cohomology group" sx={{ textTransform: "none" }}/>
        <Tab value="class" label="Cohomology class" sx={{ textTransform: "none" }}/>
        <Tab value="minimal" label="Minimal model" sx={{ textTransform: "none" }}/>
      </Tabs>
      <ComputeCohomologyForm
        targetName={targetName}
        postMessageToWorker={postMessageToWorker}
        visible={computationType === "cohomology"}
        workerInfo={workerInfo}
      />
      <ComputeClassForm
        targetName={targetName}
        postMessageToWorker={postMessageToWorker}
        visible={computationType === "class"}
        workerInfo={workerInfo}
      />
      <ComputeMinimalModelForm
        targetName={targetName}
        postMessageToWorker={postMessageToWorker}
        visible={computationType === "minimal"}
        workerInfo={workerInfo}
      />
    </React.Fragment>
  )
}
