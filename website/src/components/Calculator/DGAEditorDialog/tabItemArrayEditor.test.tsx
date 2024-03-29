import { fireEvent, render, within } from "@testing-library/react"
import { renderHook, act, RenderResult } from "@testing-library/react-hooks"
import React from "react"
import { TabItem } from "./TabDialog"
import { sphere } from "./examples"
import { Generator, useTabItemArrayEditor } from "./tabItemArrayEditor"

class ArrayEditorTestUtil {
  result: RenderResult<TabItem>
  json: string
  rerender: (ui: React.ReactElement) => void
  container: HTMLElement
  closeDialog: (() => void) = (() => undefined)

  constructor() {
    // set up useTabItemArrayEditor
    this.json = sphere(2)
    const updateDgaWrapper = (json: string): void => { this.json = json }
    const { result } = renderHook(() => useTabItemArrayEditor({
      json: this.json, updateDgaWrapper
    }))
    this.result = result
    // render the tab item
    const { rerender, container } = render(this.result.current.render(this.closeDialog))
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

  private getRow(index: number): HTMLElement {
    const rows = within(this.container).getAllByTestId("ArrayEditor-row")
    if (index >= rows.length) {
      throw new Error(`index too large: ${index} is given but the length is ${rows.length}`)
    }
    return rows[index]
  }

  private getInput(key: keyof Generator, index: number): HTMLElement {
    const testId = `ArrayEditor-input-${key}` as const
    const row = this.getRow(index)
    return within(row).getByTestId(testId)
  }

  private getDeleteButton(index: number): HTMLElement {
    const row = this.getRow(index)
    return within(row).getByRole((role, element) =>
      (role === "button") &&
      (element !== null) && (element.innerHTML.includes("Delete"))
    )
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

  deleteRow(index: number): void {
    const button = this.getDeleteButton(index)
    expect(button).toContainHTML("Delete this generator") // as aria-label
    fireEvent.click(button)
  }

  async submit(): Promise<void> {
    const closeDialog = (): void => { return }
    await act(async () => {
      // This async/await is necessary (why?)
      this.result.current.onSubmit(closeDialog)
    })
    this.rerender(this.result.current.render(this.closeDialog))
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
  describe("successful submission", () => {
    test("submit valid value", async () => {
      const testUtil = new ArrayEditorTestUtil()
      testUtil.expectInitialState()
      testUtil.inputValue("differentialValue", 1, "2*x^2")
      await testUtil.submit()
      const json = '[["x", 2, "0"], ["y", 3, "2*x^2"]]'
      expect(normalize(testUtil.json)).toEqual(normalize(json))
    })

    test("delete a generator", async () => {
      const testUtil = new ArrayEditorTestUtil()
      testUtil.expectInitialState()
      testUtil.deleteRow(1)
      await testUtil.submit()
      const json = '[["x", 2, "0"]]'
      expect(normalize(testUtil.json)).toEqual(normalize(json))
    })
  })

  describe("validation errors", () => {
    test("empty name", async () => {
      const testUtil = new ArrayEditorTestUtil()
      testUtil.expectInitialState()
      testUtil.inputValue("name", 1, "")
      await testUtil.submit()
      const errorMessage = "Please enter the name."
      testUtil.expectSingleError(errorMessage)
    })

    test("name starting with number", async () => {
      const testUtil = new ArrayEditorTestUtil()
      testUtil.expectInitialState()
      testUtil.inputValue("name", 1, "1y")
      await testUtil.submit()
      const errorMessage = "must start with alphabets"
      testUtil.expectSingleError(errorMessage)
    })

    test("name containing invalid character", async () => {
      const testUtil = new ArrayEditorTestUtil()
      testUtil.expectInitialState()
      testUtil.inputValue("name", 1, "y-")
      await testUtil.submit()
      const errorMessage = "can only contain alphabets"
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
})
