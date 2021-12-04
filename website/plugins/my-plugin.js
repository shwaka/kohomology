const CopyPlugin = require("copy-webpack-plugin");

module.exports = function myPlugin(context, options) {
  return {
    name: "my-plugin",
    configureWebpack(config, isServer, utils) {
      return {
        module: {
          rules: [
            {
              test: /\.kt$/i,
              use: 'raw-loader',
            }
          ]
        },
        plugins: [
          new CopyPlugin({
            patterns: [
              { from: "../kohomology/build/dokka/html", to: "dokka" },
              { from: "../benchmark-data/dev/bench", to: "benchmark"},
            ]
          })
        ]
      }
    }
  }
}
