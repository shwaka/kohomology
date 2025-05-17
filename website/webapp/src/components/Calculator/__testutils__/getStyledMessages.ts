import { screen, within } from "@testing-library/react"

type ContainerType = "results" | "form" // | "all"

function getContainer(containerType: ContainerType): HTMLElement {
  switch (containerType) {
    // case "all":
    //   return document.body
    case "results":
      return screen.getByTestId("calculator-results")
    case "form":
      return screen.getByTestId("CalculatorForm-StackItem-DGA")
  }
}

function getStyledMessagesOfType(containerType: ContainerType): string[] {
  const container = getContainer(containerType)
  const divArray: HTMLElement[] = within(container).queryAllByTestId("show-styled-message")
  return divArray.map((div) => {
    const dataStyledMessage: string | null = div.getAttribute("data-styled-message")
    if (dataStyledMessage === null) {
      return "undefined"
    } else {
      return dataStyledMessage
    }
  })
}

export function getStyledMessages(): { [K in ContainerType]: string[] } {
  return {
    results: getStyledMessagesOfType("results"),
    form: getStyledMessagesOfType("form"),
  }
}
