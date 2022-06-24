import { Tabs, Tab, Button, Stack } from "@mui/material"
import React, { FormEvent, useCallback, useState } from "react"
import { TargetName, WorkerInput } from "../worker/workerInterface"
import { NumberField, useNumberField } from "./NumberField"
import { StringField, useStringField } from "./StringField"
import styles from "./styles.module.scss"

export type InputEvent = React.ChangeEvent<HTMLInputElement>

export interface ComputeFormProps {
  targetName: TargetName
  postMessageToWorker: (message: WorkerInput) => void
}

function ComputeCohomologyForm({ targetName, postMessageToWorker }: ComputeFormProps): JSX.Element {
  const [maxDegree, maxDegreeFieldProps] = useNumberField({ label: "", defaultValue: 20})
  const handleCohomologyButton = useCallback(
    (): void => {
      const input: WorkerInput = {
        command: "computeCohomology",
        targetName: targetName,
        maxDegree: maxDegree,
      }
      postMessageToWorker(input)
    },
    [targetName, maxDegree, postMessageToWorker]
  )
  return (
    <Stack>
      <Stack direction="row" alignItems="center" spacing={1}>
        <span>Compute cohomology up to degree</span>
        <NumberField {...maxDegreeFieldProps}/>
      </Stack>
      <Button
        onClick={handleCohomologyButton}
        variant="contained"
      >
        Compute
      </Button>
    </Stack>
  )
}

function ComputeClassForm({ targetName, postMessageToWorker }: ComputeFormProps): JSX.Element {
  const [cocycleString, cocycleStringFieldProps] =
    useStringField({ label: "", defaultValue: "x^2", width: 150 })
  const handleComputeCohomologyClassButton = useCallback(
    (): void => {
      const input: WorkerInput = {
        command: "computeCohomologyClass",
        targetName: targetName,
        cocycleString: cocycleString,
      }
      postMessageToWorker(input)
    },
    [targetName, cocycleString, postMessageToWorker]
  )
  return (
    <Stack>
      <Stack direction="row" alignItems="center" spacing={1}>
        <span>Compute cohomology class of the cocycle</span>
        <StringField {...cocycleStringFieldProps}/>
      </Stack>
      <Button
        onClick={handleComputeCohomologyClassButton}
        variant="contained"
      >
        Compute
      </Button>
    </Stack>
  )
}

type ComputationType = "cohomology" | "class"

export function ComputeForm({ targetName, postMessageToWorker }: ComputeFormProps): JSX.Element {
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
      {
        computationType === "cohomology" &&
          <ComputeCohomologyForm targetName={targetName} postMessageToWorker={postMessageToWorker}/>
      }
      {
        computationType === "class" &&
          <ComputeClassForm targetName={targetName} postMessageToWorker={postMessageToWorker}/>
      }
    </React.Fragment>
  )
}
