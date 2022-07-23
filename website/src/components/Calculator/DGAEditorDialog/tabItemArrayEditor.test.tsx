import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import { renderHook, act, RenderResult } from "@testing-library/react-hooks"
import React from "react"
import { sphere } from "./examples"
import { TabItem } from "./TabDialog"
import { Generator, useTabItemArrayEditor } from "./tabItemArrayEditor"

class ArrayEditorTestUtil {
  result: RenderResult<TabItem<"array">>
  json: string
  rerender: (ui: React.ReactElement) => void
  container: HTMLElement

  constructor() {
    // set up useTabItemArrayEditor
    this.json = sphere(2)
    const updateDgaWrapper = (json: string): void => { this.json = json }
    const { result } = renderHook(() => useTabItemArrayEditor({
      json: this.json, updateDgaWrapper
    }))
    this.result = result
    // render the tab item
    const { rerender, container } = render(this.result.current.render())
    this.rerender = rerender
    this.container = container
  }

  expectInitialState(): void {
    this.expectValue("name", 0, "x")
    this.expectValue("degree", 0, 2)
    this.expectValue("differentialValue", 0, "0")
    this.expectValue("name", 1, "y")
    this.expectValue("degree", 1, 3)
    this.expectValue("differentialValue", 1, "x^2")
  }

  private getInput(key: keyof Generator, index: number): HTMLElement {
    const testId = `ArrayEditor-input-${key}` as const
    const inputs: HTMLElement[] = within(this.container).getAllByTestId(testId)
    if (index >= inputs.length) {
      throw new Error(`index too large: ${index} is given but the length is ${inputs.length}`)
    }
    return inputs[index]
  }

  expectValue(key: keyof Generator, index: number, value: string | number): void {
    const input = this.getInput(key, index)
    expect(input).toHaveValue(value)
  }

  getValue(key: keyof Generator, index: number): string {
    const input = this.getInput(key, index)
    return (input as HTMLInputElement).value
  }

  inputValue(key: keyof Generator, index: number, value: string): void {
    const input = this.getInput(key, index)
    fireEvent.input(input, { target: { value: value } })
  }

  async submit(): Promise<void> {
    const closeDialog = () => {}
    await act(async () => {
      // This async/await is necessary (why?)
      this.result.current.onSubmit(closeDialog)
    })
    this.rerender(this.result.current.render())
  }

  expectSingleError(message: string): void {
    const alert = within(this.container).getByRole("alert")
    expect(alert).toContainHTML(message)
  }
}

function normalize(json: string): string {
  return JSON.stringify(JSON.parse(json))
}

describe("useTabItemArrayEditor", () => {
  test("submit valid value", async () => {
    const testUtil = new ArrayEditorTestUtil()
    testUtil.expectInitialState()
    testUtil.inputValue("differentialValue", 1, "2*x^2")
    await testUtil.submit()
    const json = '[["x", 2, "0"], ["y", 3, "2*x^2"]]'
    expect(normalize(testUtil.json)).toEqual(normalize(json))
  })

  test("empty name", async () => {
    const testUtil = new ArrayEditorTestUtil()
    testUtil.expectInitialState()
    testUtil.inputValue("name", 1, "")
    await testUtil.submit()
    const errorMessage = "Please enter the name."
    testUtil.expectSingleError(errorMessage)
  })

  test("empty degree", async () => {
    const testUtil = new ArrayEditorTestUtil()
    testUtil.expectInitialState()
    testUtil.inputValue("degree", 1, "")
    testUtil.inputValue("differentialValue", 1, "0") // To avoid error on the degree of the differential
    await testUtil.submit()
    const errorMessage = "Please enter the degree."
    testUtil.expectSingleError(errorMessage)
  })

  test("empty differential value", async () => {
    const testUtil = new ArrayEditorTestUtil()
    testUtil.expectInitialState()
    testUtil.inputValue("differentialValue", 1, "")
    await testUtil.submit()
    const errorMessage = "Please enter the value of the differential."
    testUtil.expectSingleError(errorMessage)
  })

  test("differential value of illegal degree", async () => {
    const testUtil = new ArrayEditorTestUtil()
    testUtil.expectInitialState()
    testUtil.inputValue("differentialValue", 1, "x")
    await testUtil.submit()
    const errorMessage = "The degree of d(y) is expected to be deg(y)+1=4, but the given value x has degree 2."
    testUtil.expectSingleError(errorMessage)
  })

  test("duplicated name", async () => {
    const testUtil = new ArrayEditorTestUtil()
    testUtil.expectInitialState()
    testUtil.inputValue("name", 1, "x")
    await testUtil.submit()
    const errorMessage = 'Generator names must be unique. Duplicated names are "x"'
    testUtil.expectSingleError(errorMessage)
  })
})
