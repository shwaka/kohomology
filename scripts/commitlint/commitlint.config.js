// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

/** @type {import('@commitlint/types').UserConfig} */
const config = {
  extends: ["@commitlint/config-conventional"],
  rules: {
    // "scope-empty": [2, "never"], // scope is not required
    "scope-enum": [2, "always", ["core", "website"]],
    "header-max-length": [0],
  },
}

module.exports = config
