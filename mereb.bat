@echo off
set "VENV_DIR=.venv"

REM Check if the virtual environment folder exists
if not exist %VENV_DIR% (
    echo Creating virtual environment...
    python -m venv %VENV_DIR%

    REM Activate the virtual environment
    call %VENV_DIR%\Scripts\activate

    REM Install dependencies
    if exist requirements.txt (
        echo Installing requirements...
        pip install -r requirements.txt
    ) else (
        echo No requirements.txt file found.
    )
) else (
    REM Activate the existing virtual environment
    call %VENV_DIR%\Scripts\activate
)

REM Run the Python script with arguments
python "%~dp0mereb.py" %*

REM Deactivate the virtual environment
deactivate
