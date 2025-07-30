#!/bin/bash
# Versi 4: Output dalam format CSV, sempurna untuk Excel.

if [ -z "$1" ]; then
    echo "Error: Mohon sediakan path ke file messages.properties."
    exit 1
fi

PROPERTIES_FILE="$1"
SEARCH_DIR="./grails-app/"

# Header untuk file CSV
echo '"Key","File_Path"'

while IFS= read -r line || [[ -n "$line" ]]; do
    if [[ -z "$line" ]] || [[ "$line" == \#* ]] || [[ "$line" == \!* ]]; then
        continue
    fi
    key=$(echo "$line" | cut -d'=' -f1 | xargs)
    files_found=$(grep -rl "$key" "$SEARCH_DIR" | grep -v "/i18n/" | sort -u)
    if [ -n "$files_found" ]; then
        echo "$files_found" | while IFS= read -r file_path; do
            clean_path=$(echo "$file_path" | sed 's|^\./||')
            # Cetak setiap file yang ditemukan sebagai baris baru di CSV
            echo "\"$key\",\"$clean_path\""
        done
    else
        # Jika tidak ditemukan, catat sebagai "not usage"
        echo "\"$key\",\"not usage\""
    fi
done < "$PROPERTIES_FILE"