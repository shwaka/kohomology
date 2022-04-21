@rem https://stackoverflow.com/questions/18896154/calling-gradle-from-bat-causes-batch-execution-to-stop
@rem Because gradle is a batch file itself, it completes execution and doesn't return control back to your batch file
call gradlew.bat run
pause
