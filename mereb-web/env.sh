#!/bin/sh

# Function to escape special characters for sed
escape_sed() {
    echo "$1" | sed -e 's/[\/&]/\\&/g'
}

# Loop through each environment variable that starts with ABLAZE_LANDING
for i in $(env | grep MEREB)
do
    # Extract key and value
    key=$(echo "$i" | cut -d '=' -f 1)
    value=$(echo "$i" | cut -d '=' -f 2-)

    # Escape the key and value for sed
    escaped_key=$(escape_sed "$key")
    escaped_value=$(escape_sed "$value")

    echo "Replacing occurrences of '$escaped_key' with '$escaped_value'"

    # Use find and sed to replace in JS and CSS files only
    find /var/www/html -type f \( -name '*.js' -o -name '*.css' \) -exec sed -i "s|$escaped_key|$escaped_value|g" '{}' +

done

echo "Replacement completed."

