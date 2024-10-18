@echo off
setlocal enabledelayedexpansion

set services=user-service post-service
set versionFile=versions.txt

REM Create the versions file if it doesn't exist
if not exist %versionFile% (
    echo Service Versions > %versionFile%
)

REM Debug: Confirm services and versionFile are set
echo Services: %services%
echo Version file: %versionFile%

REM Function to increment version
:increment_version
set "version=%~1"
echo Incrementing version: %version%

REM Ensure the version string is in a valid format (X.Y.Z)
for /f "tokens=1-3 delims=." %%a in ("%version%") do (
    if "%%a"=="" (
        echo Invalid version format: %version%. Defaulting to 1.0.0
        set "new_version=1.0.0"
    ) else if "%%b"=="" (
        echo Incomplete version, defaulting to minor/patch. Defaulting to 1.0.0
        set "new_version=1.0.0"
    ) else if "%%c"=="" (
        echo Incomplete patch version, defaulting to .0
        set /a patch=0
        set new_version=%%a.%%b.!patch!
    ) else (
        set /a patch=%%c + 1
        set new_version=%%a.%%b.!patch!
    )
)

echo New version: %new_version%
set "%~2=%new_version%"
exit /b

for %%s in (%services%) do (
    echo Building, tagging, and pushing %%s
    cd %%s

    REM Debug: Confirm service directory change
    echo Current directory: %cd%

    REM Build the service, running tests
    echo Running mvn clean package for %%s
    call mvn clean package 
    if errorlevel 1 (
        echo Build failed for %%s, skipping Docker build and push.
        cd ..
        goto :continueLoop
    )
    
    REM Stop and remove the existing container
    echo Stopping and removing Docker container for %%s
    docker stop %%s
    docker rm %%s

    REM Check if the service has a version in the versions file
    set version_found=0
    set version=
    echo Checking for existing version of %%s in %versionFile%
    for /f "tokens=1-2 delims=:" %%a in (%versionFile%) do (
        if "%%a"=="%%s" (
            echo Found version for %%s: %%b
            set "version=%%b"
            set "version_found=1"
        )
    )

    REM If no version found, assign initial version 1.0.0
    if "!version_found!"=="0" (
        echo No version found for %%s, setting initial version to 1.0.0
        set "version=1.0.0"
    ) else (
        echo Current version for %%s: !version!
    )

    REM Increment the version
    call :increment_version !version! new_version

    REM Debug: Show the new version
    echo New version for %%s: !new_version!

    REM Update the version in the versions file
    REM Remove old version
    echo Updating %versionFile% with new version for %%s
    findstr /v "%%s:" %versionFile% > ../temp.txt
    move /y ../temp.txt %versionFile%
    echo %%s:!new_version! >> %versionFile%

    REM Build and tag the Docker image
    echo Building Docker image for %%s: leultewolde/%%s:!new_version!
    docker build -t leultewolde/%%s:!new_version! .

    REM Tag and push the image
    echo Tagging and pushing image for %%s: leultewolde/%%s:!new_version!
    docker tag leultewolde/%%s:!new_version! leultewolde/%%s:latest
    docker push leultewolde/%%s:!new_version!
    docker push leultewolde/%%s:latest

    cd ..
    :continueLoop
)

REM docker-compose down
echo Bringing up Docker services
docker-compose up -d

echo All services have been built, tagged, and pushed to Docker Hub with updated version tracking.
pause
