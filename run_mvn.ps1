param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$ArgsList
)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$mvnBin = "C:\Users\pedro\.m2\wrapper\dists\apache-maven-3.9.14-bin\1cb7fhup6b5n3bed6kckbrnspv\apache-maven-3.9.14\bin\mvn.cmd"
& $mvnBin @ArgsList

