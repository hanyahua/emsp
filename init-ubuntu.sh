#!/bin/bash

# Update package index and upgrade system (optional)
sudo apt update -y
sudo apt upgrade -y

# Install Docker
sudo apt install -y docker.io

# Start Docker service and enable it to start on boot
sudo systemctl start docker
sudo systemctl enable docker

# Install standalone Docker Compose binary
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Make Docker Compose executable
sudo chmod +x /usr/local/bin/docker-compose

# Add current user to the docker group to avoid using sudo with docker commands
sudo usermod -aG docker $USER

# Apply the new group membership immediately
newgrp docker

# Display versions to confirm installation
echo "Docker version:"
docker --version

echo "Docker Compose version:"
docker-compose --version

echo "Setup complete. Please log out and log back in or restart your terminal to apply group changes."

