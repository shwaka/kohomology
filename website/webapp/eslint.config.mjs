import { eslintConfigShwakaReact } from "eslint-config-shwaka"
import testingLibrary from "eslint-plugin-testing-library"
import jestDom from "eslint-plugin-jest-dom"
import tseslint from 'typescript-eslint'
import { fixupConfigRules } from "@eslint/compat"
import { FlatCompat } from "@eslint/eslintrc"

const flatCompat = new FlatCompat()

export default tseslint.config(
  ...eslintConfigShwakaReact,
  {
    // "ignores" must be separated from "files"
    ignores: ["**/prismThemes/*.mjs"],
  },
  {
    files: [
      "**/*.js", "**/*.jsx", "**/*.mjs", "**/*.cjs",
      "**/*.ts", "**/*.tsx", "**/*.mts", "**/*.cts",
    ],
  },
  ...fixupConfigRules(
    flatCompat.extends(
      "plugin:testing-library/react",
      "plugin:jest-dom/recommended",
    )
  ),
)
