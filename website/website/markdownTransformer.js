// const path = require("path")

module.exports = {
  process(sourceText, sourcePath, options) {
    return {
      code: 'const React = require("react"); module.exports = function () { return React.createElement("div", {}, "Content of a markdown file") }'
      // "Content of a markdown file" の所は本当は sourceText を入れるべき．
      // しかし ${sourceText} ではダメで，エスケープなどの処理をする必要がある．
      // sourcePath を使って code 内で読み込む方が良いかも？
    }
  }
}
