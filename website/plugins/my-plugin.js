const CopyPlugin = require("copy-webpack-plugin");
const path = require('path');

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
            },
            {
              test: /\.worker\.js$/,
              use: {
                loader: "worker-loader",
                options: {
                  inline: "no-fallback"
                }
              }
            },
          ]
        },
        plugins: [
          new CopyPlugin({
            patterns: [
              { from: "../kohomology/build/dokka/html", to: "dokka" },
              { from: "../benchmark-data/dev/bench", to: "benchmark"},
            ]
          })
        ],
        resolve: {
          alias: {
            "@data": path.resolve(context.siteDir, "src/data"),
            "@benchmark": path.resolve(context.siteDir, "../benchmark-data/dev/bench"),
          }
        }
      }
    }
  }
}
