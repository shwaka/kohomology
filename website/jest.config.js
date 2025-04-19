/** @type {import("ts-jest/dist/types").InitialOptionsTsJest} */
module.exports = {
  preset: "ts-jest",
  testEnvironment: "jsdom",
  transform: {
    "^.+\\.(ts|tsx)$": "ts-jest",
    "^.+\\.(js|jsx)$": "babel-jest",
    ".+\\.(css|styl|less|sass|scss|png|jpg|ttf|woff|woff2)$": "jest-transform-stub",
    "^.+\\.(md|mdx)$": "<rootDir>/markdownTransformer.js",
  },
  setupFilesAfterEnv: ["<rootDir>/jest-setup.ts"],
  // ts-jest[ts-jest-transformer] (WARN) Define `ts-jest` config under `globals` is deprecated. Please do
  // transform: {
  //   <transform_regex>: ['ts-jest', { /* ts-jest config goes here in Jest */ }],
  // },
  // See more at https://kulshekhar.github.io/ts-jest/docs/getting-started/presets#advanced
  // globals: {
  //   "ts-jest": {
  //     // "isolatedModules: true" disables type checking on "npm test".
  //     // This is necessary to use arrays in tests. (Why?)
  //     // "isolatedModules" is deprecated and will be removed in v30.0.0
  //     // isolatedModules: true,
  //   }
  // },
  modulePathIgnorePatterns: [
    "<rootDir>/kohomology-js/build/js/packages/kohomology-js",
    "<rootDir>/kohomology-js/build/tmp",
  ],
  moduleNameMapper: {
    "^.+\\.(css|styl|less|sass|scss|png|jpg|ttf|woff|woff2)$": "jest-transform-stub",
    "^worker-loader!.*/kohomology.worker$": "<rootDir>/src/components/Calculator/worker/__mocks__/kohomology.worker.ts",
    "@site/(.*)$": "<rootDir>/$1",
    "@components/(.*)$": "<rootDir>/src/components/$1",
  },
  testPathIgnorePatterns: ["/node_nodules/", "/__testutils__/"]
}
