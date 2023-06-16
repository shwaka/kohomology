import TeX from "@matejmazur/react-katex"
import { Button, Container, Divider, FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material"
import React, { Fragment, useCallback, useState } from "react"
import "katex/dist/katex.min.css"
import { useDGAEditorDialog } from "../DGAEditorDialog"
import { sphere } from "../DGAEditorDialog/examples"
import { IdealConfig } from "../IdealConfig"
import { ShowStyledMessage } from "../styled/components"
import { useJsonFromURLQuery, useTargetNameFromURLQuery, useValueOfURLQueryResult } from "../urlQuery"
import { targetNames, TargetName } from "../worker/workerInterface"
import { ComputeForm } from "./ComputeForm"
import { RestartButton, RestartDialog, useRestart } from "./RestartDialog"
import { ShareDGAButton, ShareDGADialog, useShareDGA } from "./ShareDGADialog"
import { ShowError } from "./ShowError"
import { UsageButton, UsageDialog, useUsage } from "./UsageDialog"
import { getCohomologyAsString, TopologicalInvariantAsTex } from "./target"
import { useKohomologyWorker } from "./useKohomologyWorker"
import { useMutableArray } from "./useMutableArray"
import { useIdealJsonFromURLQuery } from "../urlQuery/useIdealJsonFromURLQuery"

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
  const { array: errorMessages, push: addErrorMessage } = useMutableArray<string>()

  const defaultDGAJson = useValueOfURLQueryResult(
    useJsonFromURLQuery(),
    sphere(2),
    addErrorMessage,
  )

  const defaultIdealJson = useValueOfURLQueryResult(
    useIdealJsonFromURLQuery(),
    "[]",
    addErrorMessage,
  )

  const defaultTargetName: TargetName = useValueOfURLQueryResult(
    useTargetNameFromURLQuery(),
    "self",
    addErrorMessage,
  )

  const { json, setJson, idealJson, setIdealJson, dgaInfo, idealInfo, workerInfo, postMessage, restart, runAsync } =
    useKohomologyWorker({
      defaultJson: defaultDGAJson,
      defaultIdealJson,
      onmessage: (_) => undefined, // previously this was used to pass setState
    })

  const [targetName, setTargetName] = useState<TargetName>(defaultTargetName)
  const { usageDialogProps, usageButtonProps } = useUsage()
  const { restartDialogProps, restartButtonProps } = useRestart(() => {
    restart()
  })
  const { shareDGADialogProps, shareDGAButtonProps } = useShareDGA({ dgaJson: json, idealJson, targetName })
  const { TabDialog, tabDialogProps, openDialog } = useDGAEditorDialog(json, setJson)

  const validateIdealGenerator = useCallback(async (generator: string): Promise<true | string> => {
    return await runAsync("validateIdealGenerator", [generator])
  }, [runAsync])

  const validateIdealGeneratorArray = useCallback(async (generatorArray: string[]): Promise<true | string> => {
    return await runAsync("validateIdealGeneratorArray", [generatorArray])
  }, [runAsync])

  return (
    <Fragment>
      <ShowError messages={errorMessages}/>
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
          {targetName === "idealQuot" && (
            <IdealConfig
              {...{
                setIdealJson, idealInfo, idealJson,
                validateGenerator: validateIdealGenerator,
                validateGeneratorArray: validateIdealGeneratorArray,
              }}
            />
          )}
        </StackItem>
        <StackItem>
          <ComputeForm
            targetName={targetName}
            postMessageToWorker={(message) => {
              // setWorkerInfo({ status: "computing", progress: null })
              postMessage(message)
            }}
            workerInfo={workerInfo}
          />
        </StackItem>
      </Stack>
    </Fragment>
  )
}
