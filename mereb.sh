#!/bin/bash
set -e

VENV_DIR=".venv"

# Check if the virtual environment folder exists
if [ ! -d "$VENV_DIR" ]; then
    echo "Creating virtual environment..."
    python3 -m venv "$VENV_DIR"

    # Activate the virtual environment
    source "$VENV_DIR/bin/activate"

    # Install dependencies from requirements.txt
    if [ -f "requirements.txt" ]; then
        echo "Installing requirements..."
        pip install -r requirements.txt
    else
        echo "No requirements.txt file found."
    fi
else
    # Activate the existing virtual environment
    source "$VENV_DIR/bin/activate"
fi

# Run the Python script with arguments
python "$(dirname "$0")/mereb.py" "$@" || { echo "Python script execution failed."; exit 1; }

# Deactivate the virtual environment
deactivate
