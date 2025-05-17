// Copied from https://github.com/chartjs/Chart.js/blob/master/src/plugins/plugin.colors.ts

const BORDER_COLORS = [
  "rgb(54, 162, 235)", // blue
  "rgb(255, 99, 132)", // red
  "rgb(255, 159, 64)", // orange
  "rgb(255, 205, 86)", // yellow
  "rgb(75, 192, 192)", // green
  "rgb(153, 102, 255)", // purple
  "rgb(201, 203, 207)" // grey
]

// Border colors with 50% transparency
const BACKGROUND_COLORS = BORDER_COLORS.map(color => color.replace("rgb(", "rgba(").replace(")", ", 0.5)"))

export function getBorderColor(i: number): string {
  return BORDER_COLORS[i % BORDER_COLORS.length]
}

export function getBackgroundColor(i: number): string {
  return BACKGROUND_COLORS[i % BACKGROUND_COLORS.length]
}
