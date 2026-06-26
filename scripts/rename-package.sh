#!/usr/bin/env bash
set -euo pipefail

usage() {
    echo "Usage: $0 <new-package>"
    echo "Example: $0 com.mycompany"
    exit 1
}

if [ $# -ne 1 ]; then
    usage
fi

NEW_PKG="$1"
OLD_PKG="com.example"
OLD_DIR="com/example"
NEW_DIR="${NEW_PKG//\.//}"

if [ "$NEW_PKG" = "$OLD_PKG" ]; then
    echo "Package is already $OLD_PKG"
    exit 0
fi

echo "Renaming $OLD_PKG -> $NEW_PKG"
echo ""

# Update content in source files
find src -type f \( -name "*.java" -o -name "*.yaml" -o -name "*.yml" -o -name "*.kts" \) \
    -exec sed -i '' "s/$OLD_PKG/$NEW_PKG/g" {} +

# Rename directory tree
find src -type d -path "*/$OLD_DIR" | while read -r dir; do
    parent="$(dirname "$dir")"
    mv "$dir" "$parent/$NEW_DIR"
done

# Update settings.gradle.kts
if [ -f settings.gradle.kts ]; then
    sed -i '' "s/spring-template/${NEW_PKG##*.}-template/" settings.gradle.kts 2>/dev/null || true
fi

echo "Done. Verify with: grep -r '$OLD_PKG' src/"
