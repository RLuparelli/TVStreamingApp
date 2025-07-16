#!/bin/bash

# Comprehensive Android SDK WSL Fix
ANDROID_SDK="/mnt/c/Users/lupar/AppData/Local/Android/Sdk"
BUILD_TOOLS_VERSIONS=("35.0.1" "36.0.0")

echo "Creating comprehensive WSL compatibility for Android SDK..."

for version in "${BUILD_TOOLS_VERSIONS[@]}"; do
    BUILD_TOOLS_DIR="$ANDROID_SDK/build-tools/$version"
    
    if [ -d "$BUILD_TOOLS_DIR" ]; then
        echo "Processing build-tools $version..."
        cd "$BUILD_TOOLS_DIR"
        
        # Create symlinks for all .exe files
        for exe_file in *.exe; do
            if [ -f "$exe_file" ]; then
                base_name="${exe_file%.exe}"
                if [ ! -f "$base_name" ]; then
                    ln -sf "$exe_file" "$base_name"
                    echo "  Created symlink: $base_name -> $exe_file"
                fi
            fi
        done
        
        # Create wrapper scripts for .bat files
        for bat_file in *.bat; do
            if [ -f "$bat_file" ]; then
                base_name="${bat_file%.bat}"
                if [ ! -f "$base_name" ]; then
                    cat > "$base_name" << EOF
#!/bin/bash
exec cmd.exe /c "$(realpath "$bat_file")" "\$@"
EOF
                    chmod +x "$base_name"
                    echo "  Created wrapper: $base_name -> $bat_file"
                fi
            fi
        done
    fi
done

echo "✅ Comprehensive WSL compatibility setup complete!"
