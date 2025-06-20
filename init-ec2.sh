#!/bin/bash

# Update package index and upgrade system (optional)
sudo apt update -y
sudo apt upgrade -y

# Install Docker if not already installed
if ! command -v docker &> /dev/null; then
    echo "Docker not found. Installing Docker..."
    sudo apt install -y docker.io

    # Start Docker service and enable it to start on boot
    sudo systemctl start docker
    sudo systemctl enable docker
else
    echo "Docker is already installed."
fi

# Install Docker Compose if not already installed
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose not found. Installing Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
else
    echo "Docker Compose is already installed."
fi

# Add current user to docker group if not already added
if ! groups $USER | grep -q '\bdocker\b'; then
    echo "Adding user to docker group..."
    sudo usermod -aG docker $USER
    echo "Please log out and log back in or restart your terminal to apply group changes."
else
    echo "User is already in docker group."
fi

# Display versions to confirm installation
echo -e "\nCurrent versions:"
docker --version 2>/dev/null || echo "Docker not found in PATH"
docker-compose --version 2>/dev/null || echo "Docker Compose not found in PATH"

echo -e "\nSetup complete."