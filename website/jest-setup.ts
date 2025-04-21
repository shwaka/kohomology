import "@testing-library/jest-dom"
// @ts-expect-error because the following error occurs:
//   error TS2306: File '/path/to/website/node_modules/jest-extended/types/index.d.ts' is not a module.
// Workarounds in https://github.com/jest-community/jest-extended/issues/367 did not work here. (Why?)
// This error is ignored since LSP works well in test files.
import * as matchers from "jest-extended"

expect.extend(matchers)

/* eslint-disable @typescript-eslint/no-empty-function */

// When running tests containing motion/react,
// sometimes we have an error such as "Error: Not implemented: window.scrollTo".
// This is because window.scrollTo is not implemented in jsdom.
Object.defineProperty(window, "scrollTo", {
  writable: true,
  value: () => {},
})
