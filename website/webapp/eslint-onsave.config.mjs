import { eslintConfigShwakaOnsave } from "eslint-config-shwaka"
import importPlugin from "eslint-plugin-import"
import tseslint from "typescript-eslint"

export default tseslint.config(
  ...eslintConfigShwakaOnsave,
  {
    files: [
      "**/*.js", "**/*.jsx", "**/*.mjs", "**/*.cjs",
      "**/*.ts", "**/*.tsx", "**/*.mts", "**/*.cts",
    ],
  },
  {
    // Due to eslint-disable, definition for react-hooks/exhaustive-deps is required:
    //   /home/shun/Git/kohomology/website/webapp/src/calculator/useScrollToBottom.ts
    //     29:3  error  Definition for rule 'react-hooks/exhaustive-deps' was not found
    // To avoid this error, we define dummy plugins here.
    plugins: {
      "react-hooks": {
        rules: {
          "exhaustive-deps": {},
        },
      },
      "testing-library": {
        rules: {
          "no-node-access": {},
        }
      },
    },
  },
)
