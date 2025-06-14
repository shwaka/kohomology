import { eslintConfigShwakaReact } from "eslint-config-shwaka"
import testingLibrary from "eslint-plugin-testing-library"
import jestDom from "eslint-plugin-jest-dom"
import tseslint from 'typescript-eslint'
import { fixupConfigRules } from "@eslint/compat"
import { FlatCompat } from "@eslint/eslintrc"

const flatCompat = new FlatCompat()

export default tseslint.config(
  { ignores: ["**/prismThemes/*.mjs"] },
  ...eslintConfigShwakaReact,
  ...fixupConfigRules(
    flatCompat.extends(
      "plugin:testing-library/react",
      "plugin:jest-dom/recommended",
    )
  ),
  {
    // plugins: {
    //   "testing-library": testingLibrary, // already defined by flatCompat
    // },
    rules: {
      "@typescript-eslint/no-floating-promises": "error",
      "react/jsx-no-leaked-render": "off", // use @typescript-eslint/strict-boolean-expressions
      "react/jsx-key": "error",
    },
  },
)
