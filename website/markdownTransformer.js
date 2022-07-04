// const path = require("path")

module.exports = {
  process(sourceText, sourcePath, options) {
    return {
      code: `import React from "react"; module.exports = function () { return React.createElement("div", {}, ${sourceText}) }`
    }
  }
}
