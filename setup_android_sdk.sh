#!/bin/bash

# Android SDK Setup Script
set -e

ANDROID_HOME="$HOME/Android/Sdk"
CMDLINE_TOOLS_VERSION="11076708"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"

echo "Setting up Android SDK..."

# Create Android SDK directory
mkdir -p "$ANDROID_HOME"
cd "$ANDROID_HOME"

# Download and extract command line tools
echo "Downloading Android SDK Command Line Tools..."
wget -O cmdline-tools.zip "$CMDLINE_TOOLS_URL"

echo "Extracting command line tools..."
unzip -q cmdline-tools.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true
rm -f cmdline-tools.zip

# Set up environment variables
echo "Setting up environment variables..."
if ! grep -q "ANDROID_HOME" ~/.bashrc; then
    echo "" >> ~/.bashrc
    echo "# Android SDK" >> ~/.bashrc
    echo "export ANDROID_HOME=\"$ANDROID_HOME\"" >> ~/.bashrc
    echo "export PATH=\"\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin\"" >> ~/.bashrc
    echo "export PATH=\"\$PATH:\$ANDROID_HOME/platform-tools\"" >> ~/.bashrc
fi

# Source the environment variables for current session
export ANDROID_HOME="$ANDROID_HOME"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin"
export PATH="$PATH:$ANDROID_HOME/platform-tools"

# Accept licenses and install required SDK components
echo "Installing required SDK components..."
yes | sdkmanager --licenses || true
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

echo ""
echo "✅ Android SDK setup complete!"
echo "📁 SDK Location: $ANDROID_HOME"
echo ""
echo "To use the SDK in your current terminal session, run:"
echo "export ANDROID_HOME=\"$ANDROID_HOME\""
echo "export PATH=\"\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools\""
echo ""
echo "Or restart your terminal to load the new environment variables."
