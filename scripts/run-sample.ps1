cd $(git rev-parse --show-toplevel) # go to the root of the repository

cd website/sample
Get-ChildItem -Name src/main/kotlin | Foreach-Object {
    ./gradlew.bat run "-DsampleName=$_"
}
