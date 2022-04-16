#! /bin/bash

set -eu

cd $(dirname $0)
scriptsDir=$(pwd)

outdir=img
mkdir -p $outdir

./generate-favicon.kts

cd $outdir

# sudo apt install imagemagick
convert -density 300 favicon.svg -size 300x300 favicon.png
convert -density 300 favicon.svg -define icon:auto-resize favicon.ico

cp * $scriptsDir/../website/static/img/
