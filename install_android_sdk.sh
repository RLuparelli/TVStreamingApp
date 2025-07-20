#!/bin/bash

# Script para instalar Android SDK no WSL
echo "Instalando Android SDK no WSL..."

# Criar diretório para Android SDK
mkdir -p ~/android-sdk

# Baixar command line tools
cd ~/android-sdk
wget https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip
unzip commandlinetools-linux-8512546_latest.zip
rm commandlinetools-linux-8512546_latest.zip

# Configurar variáveis de ambiente
echo 'export ANDROID_HOME=~/android-sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/bin:$ANDROID_HOME/platform-tools' >> ~/.bashrc

# Recarregar bashrc
source ~/.bashrc

# Aceitar licenças e instalar SDK
yes | ~/android-sdk/cmdline-tools/bin/sdkmanager --sdk_root=$ANDROID_HOME "platform-tools" "platforms;android-34" "build-tools;34.0.0"

echo "Android SDK instalado! Reinicie o terminal e execute:"
echo "export ANDROID_HOME=~/android-sdk"
echo "./gradlew assembleDebug"