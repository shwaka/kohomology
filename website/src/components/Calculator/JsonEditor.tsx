import React, { FormEvent, useEffect, useRef, useState } from "react"
import { sphere, complexProjective, sevenManifold } from "./examples"
import styles from "./styles.module.scss"

type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface JsonEditorProps {
  json: string
  updateDgaWrapper: (json: string) => void
  finish: () => void
}

export function JsonEditor(props: JsonEditorProps): JSX.Element {
  const [json, setJson] = useState(props.json)
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input
        type="button" value={valueString}
        onClick={() => setJson(jsonString)} />
    )
  }
  function handleChangeJson(e: TextAreaEvent): void {
    setJson(e.target.value)
  }
  return (
    <div className={styles.jsonEditor}>
      {createButton("S^2", sphere(2))}
      {createButton("CP^3", complexProjective(3))}
      {createButton("7-mfd", sevenManifold())}
      <textarea
        value={json} onChange={handleChangeJson} />
      <input
        type="button" value="Apply"
        onClick={() => { props.updateDgaWrapper(json); props.finish() }} />
      <input
        type="button" value="Cancel"
        onClick={() => { props.finish() }} />
    </div>
  )
}
