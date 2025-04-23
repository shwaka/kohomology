// @ts-check

/** @type {import('eslint').Linter.Config} */
const config = {
  "extends": ["eslint-config-shwaka", "plugin:testing-library/react", "plugin:jest-dom/recommended"],
  "plugins": ["testing-library"],
  "rules": {
    "@typescript-eslint/no-floating-promises": "error",
  }
}
module.exports = config
