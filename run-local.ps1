param(
    [switch]$SkipDownload
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$mavenVersion = "3.9.9"
$mavenHome = Join-Path $projectRoot ".tools\apache-maven-$mavenVersion"
$mavenZip = Join-Path $projectRoot ".tools\apache-maven-$mavenVersion-bin.zip"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"

function Get-MavenCommand {
    $existing = Get-Command mvn -ErrorAction SilentlyContinue
    if ($existing) {
        return $existing.Source
    }

    if (Test-Path $mavenHome) {
        return (Join-Path $mavenHome "bin\mvn.cmd")
    }

    if ($SkipDownload) {
        throw "Maven was not found and download was skipped."
    }

    New-Item -ItemType Directory -Force (Join-Path $projectRoot ".tools") | Out-Null
    Write-Host "Downloading Maven $mavenVersion ..."
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip
    Write-Host "Extracting Maven ..."
    Expand-Archive -LiteralPath $mavenZip -DestinationPath (Join-Path $projectRoot ".tools") -Force
    return (Join-Path $mavenHome "bin\mvn.cmd")
}

$maven = Get-MavenCommand
Write-Host "Using Maven: $maven"
& $maven spring-boot:run
