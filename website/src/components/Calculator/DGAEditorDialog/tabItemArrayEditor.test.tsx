import { fireEvent, render, screen, waitForElementToBeRemoved, within } from "@testing-library/react"
import { renderHook, act, RenderResult } from "@testing-library/react-hooks"
import React from "react"
import { sphere } from "./examples"
import { TabItem } from "./TabDialog"
import { useTabItemArrayEditor } from "./tabItemArrayEditor"

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
    this.expectDifferentialValue(0, "0")
    this.expectDifferentialValue(1, "x^2")
  }

  private getDifferentialValueInput(index: number): HTMLElement {
    const testId = "ArrayEditor-input-differentialValue"
    const inputs: HTMLElement[] = within(this.container).getAllByTestId(testId)
    if (index >= inputs.length) {
      throw new Error(`index too large: ${index} is given but the length is ${inputs.length}`)
    }
    return inputs[index]
  }

  expectDifferentialValue(index: number, value: string): void {
    const input = this.getDifferentialValueInput(index)
    expect(input).toHaveValue(value)
  }

  async inputDifferentialValue(index: number, value: string): Promise<void> {
    const input = this.getDifferentialValueInput(index)
    fireEvent.input(input, { target: { value: value } })
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

test("useTabItemArrayEditor", async () => {
  const testUtil = new ArrayEditorTestUtil()
  testUtil.expectInitialState()
  // input invalid value
  await testUtil.inputDifferentialValue(1, "x")
  const errorMessage = "The degree of d(y) is expected to be deg(y)+1=4, but the given value x has degree 2."
  testUtil.expectSingleError(errorMessage)
})
