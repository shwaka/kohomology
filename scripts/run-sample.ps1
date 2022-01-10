cd $(git rev-parse --show-toplevel) # go to the root of the repository

cd sample
Get-ChildItem -Name src/main/kotlin | Foreach-Object {
    gradlew.bat run "-DsampleName=$_"
}
