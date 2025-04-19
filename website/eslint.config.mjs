// @ts-check
import tseslint from "typescript-eslint"
import testingLibrary from "eslint-plugin-testing-library"
import { eslintConfigShwakaReact } from "eslint-config-shwaka"

// const config = {
//   "extends": ["eslint-config-shwaka", "plugin:testing-library/react", "plugin:jest-dom/recommended"],
//   "plugins": ["testing-library"]
// }

const config = tseslint.config(
  ...eslintConfigShwakaReact,
  {
    plugins: {
      "testing-library": testingLibrary,
    },
  },
)
export default config
