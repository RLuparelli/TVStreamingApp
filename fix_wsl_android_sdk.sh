#!/bin/bash

# WSL Android SDK Workaround Script
# This script creates symlinks without .exe extension for WSL compatibility

ANDROID_SDK="/mnt/c/Users/lupar/AppData/Local/Android/Sdk"
BUILD_TOOLS_VERSIONS=("35.0.1" "36.0.0")

echo "Creating WSL-compatible symlinks for Android SDK..."

for version in "${BUILD_TOOLS_VERSIONS[@]}"; do
    BUILD_TOOLS_DIR="$ANDROID_SDK/build-tools/$version"
    
    if [ -d "$BUILD_TOOLS_DIR" ]; then
        echo "Processing build-tools $version..."
        
        cd "$BUILD_TOOLS_DIR"
        
        # Create symlinks for main tools (remove .exe extension)
        for tool in aapt aapt2 aidl zipalign; do
            if [ -f "${tool}.exe" ] && [ ! -f "$tool" ]; then
                ln -sf "${tool}.exe" "$tool"
                echo "  Created symlink: $tool -> ${tool}.exe"
            fi
        done
        
        # Special case for d8 and other tools
        for tool in d8 dexdump; do
            if [ -f "${tool}.bat" ] && [ ! -f "$tool" ]; then
                # Create a wrapper script for .bat files
                cat > "$tool" << EOF
#!/bin/bash
exec cmd.exe /c "$(realpath "${tool}.bat")" "\$@"
EOF
                chmod +x "$tool"
                echo "  Created wrapper: $tool -> ${tool}.bat"
            fi
        done
    fi
done

echo "✅ WSL compatibility setup complete!"
echo ""
echo "Note: This creates symlinks to make Windows Android SDK tools work in WSL."
echo "If you encounter permission issues, try running this script with elevated privileges."
