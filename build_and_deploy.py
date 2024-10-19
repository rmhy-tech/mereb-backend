import subprocess
import os
import yaml
import logging
import argparse
from concurrent.futures import ThreadPoolExecutor
import time

from datetime import datetime, timezone
from dotenv import load_dotenv
import colorlog

# Load the custom .env file
# load_dotenv(dotenv_path='.env.services')

# Retrieve environment (staging, production, etc.)
# ENVIRONMENT = os.getenv('ENVIRONMENT', 'development')  # Default to 'development' if not set



def load_yaml_config(filepath):
    with open(filepath, 'r') as stream:
        return yaml.safe_load(stream)

# Set up colored logging with subdued colors for less important logs
handler = colorlog.StreamHandler()
handler.setFormatter(colorlog.ColoredFormatter(
    '%(log_color)s%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
    log_colors={
        'DEBUG': 'white',
        'INFO': 'green',
        'WARNING': 'yellow',
        'ERROR': 'red',
        'CRITICAL': 'bold_red',
    },
    secondary_log_colors={
        'message': {
            'INFO': 'cyan',  # Subtle log information, for less prominent logs
            'DEBUG': 'white',
        }
    },
))

logger = logging.getLogger()
logger.setLevel(logging.INFO)
logger.addHandler(handler)


# Configuration for version file
version_file = "versions.txt"

# Function to generate dynamic Docker Compose
def generate_docker_compose(services, environment):
    """Generate a Docker Compose file dynamically for the services based on environment."""
    compose = {
        'services': {},
        'networks': {
            'mereb_app-network': {
                'driver': 'bridge'
            }
        }
    }

    for service in services:
        # Dynamic ports and environment variables
        service_ports = service.get('ports', [])
        service_environment = service.get('environment', [])
        env_file = service.get('env_file', f'.env.{environment}')  # Use environment-specific .env file

        tag = f"{service['version']}-{environment}"

        if service["version"] == 'latest':
            tag = f"{environment}-latest"

        if environment == 'production':
            tag = service['version']  # No environment tag for production

        image = f'leultewolde/{service["name"]}:{tag}'

        # Add service configuration
        compose['services'][service['name']] = {
            'image': image,
            'container_name': service['name'],
            'ports': service_ports,
            'environment': service_environment,
            'env_file': env_file,  # External environment file for each service
            'networks': ['mereb_app-network']
        }

    # Write the generated Docker Compose to a file
    compose_file = f'docker-compose.{environment}.yml'
    if environment == 'production':
            compose_file = 'docker-compose.yml'

    with open(compose_file, 'w') as file:
        yaml.dump(compose, file, default_flow_style=False)

    logger.info(f"📝 Docker Compose file for {environment} environment generated as {compose_file}")
    return compose_file

def initialize_version_file():
    """Create the version file if it doesn't exist."""
    if not os.path.exists(version_file):
        with open(version_file, "w") as f:
            f.write("Service Versions\n")
        logger.info("📁 Version file created", extra={"event": "file_created", "version_file": version_file})

def increment_version(version):
    """Increment the patch version of a semantic version string."""
    parts = version.split(".")
    if len(parts) == 3:
        parts[2] = str(int(parts[2]) + 1)  # Increment the patch version
    else:
        parts = ["1", "0", "0"]  # Default version if not found
    return ".".join(parts)

# Minimal, interactive Maven/Docker logs
def run_command(command, ignore_errors=False, service_name=None, event=None):
    """Run a shell command and optionally ignore errors."""
    logger.info(f"🚀 Running command: {command}", extra={"event": event, "service_name": service_name})
    
    try:
        result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        # Only show a few lines of the output
        if result.stdout:
            logger.info(f"📋 {event} Output: {result.stdout.strip().splitlines()[-10:]}",
                        extra={"event": event, "service_name": service_name})
        if result.returncode == 0:
            logger.info(f"✅ Command succeeded: {command}", extra={"event": event, "service_name": service_name})
        else:
            logger.error(f"❌ Command failed: {command}", extra={"event": event, "service_name": service_name})
            if not ignore_errors:
                exit(1)
    except subprocess.CalledProcessError as e:
        logger.error(f"❌ Error occurred during command execution: {command}", extra={"event": event, "service_name": service_name})


def save_version_to_file(version_data):
    """Save all service versions to the version file."""
    try:
        if not version_data:
            logger.error("Version data is empty, nothing to save", extra={"event": "save_version_failed"})
            return
        
        # Open the file in write mode (overwrite existing file)
        with open(version_file, "w") as f:
            logger.info("Saving versions to file", extra={"event": "save_version", "version_file": version_file})
            for service_name, version in version_data.items():
                f.write(f"{service_name}:{version}\n")
            f.flush()
            os.fsync(f.fileno())  # Force write to disk
        
        logger.info("✅ Version data successfully written to file", extra={"event": "save_version_success"})

    except OSError as e:
        logger.error(f"❌ Error opening/writing to file: {e}", extra={"event": "save_version_failed", "version_file": version_file})

def read_version_data():
    """Read the current versions from the file."""
    version_data = {}
    if os.path.exists(version_file):
        with open(version_file, "r") as f:
            for line in f:
                if ":" in line:
                    service, version = line.strip().split(":")
                    version_data[service] = version
    return version_data

def process_service(service, version_data, environment):
    """Process building, tagging, and pushing Docker images for a service."""
    logger.info(f"🚧 Processing {service}", extra={"event": "process_service", "service_name": service})
    start_time = time.time()
    
    try:
        os.chdir(service)
        
        # Step 1: Build the service using Maven
        logger.info(f"🛠️ Building {service} with Maven", extra={"event": "build_service", "service_name": service})
        run_command("mvn clean package", service_name=service, event="build_service")

        # Step 2: Stop and remove the existing Docker container
        logger.info(f"🗑️ Stopping and removing Docker container for {service}", extra={"event": "docker_cleanup", "service_name": service})
        run_command(f"docker stop {service}", ignore_errors=True, service_name=service, event="stop_container")
        run_command(f"docker rm {service}", ignore_errors=True, service_name=service, event="remove_container")

        # Step 3: Determine the current version and increment it
        version = version_data.get(service, "1.0.0")  # Default to 1.0.0 if not found
        new_version = increment_version(version)

        # Step 4: Build, tag, and push the Docker image with environment-specific tags
        logger.info(f"🚀 Building and pushing Docker image for {service}", extra={"event": "docker_build_push", "service_name": service})
        run_command(f"docker build -t leultewolde/{service}:{new_version}-{environment} .", service_name=service, event="build_docker_image")
        run_command(f"docker tag leultewolde/{service}:{new_version}-{environment} leultewolde/{service}:{environment}-latest", service_name=service, event="tag_docker_image")
        run_command(f"docker push leultewolde/{service}:{new_version}-{environment}", service_name=service, event="push_docker_image")
        run_command(f"docker push leultewolde/{service}:{environment}-latest", service_name=service, event="push_docker_image_latest")

        # Step 5: Update the version in memory
        version_data[service] = new_version

        elapsed_time = time.time() - start_time
        logger.info(f"✅ Service {service} processed successfully in {elapsed_time:.2f} seconds.", extra={"event": "process_service_success", "service_name": service})

    except Exception as e:
        logger.error(f"❌ An error occurred while processing {service}: {e}", extra={"event": "process_service_failed", "service_name": service})
    finally:
        os.chdir("..")

def get_services():
    """Get services from environment variables or command-line arguments."""
    # Step 1: Check if services are passed as command-line arguments
    parser = argparse.ArgumentParser(description="Build and deploy services.")
    parser.add_argument('--services', nargs='+', help="List of services to process")
    args = parser.parse_args()

    # Step 2: If no command-line arguments, check environment variables
    if args.services:
        logger.info(f"🔧 Using services from command-line arguments: {args.services}")
        return args.services
    else:
        services_env = os.getenv('SERVICES')
        if services_env:
            services = [service.strip() for service in services_env.split(",")]
            logger.info(f"🔧 Using services from environment variable: {services}")
            return services
        else:
            logger.error("No services provided. Use --services or set the SERVICES environment variable.", extra={"event": "no_services_provided"})
            exit(1)

def get_docker_compose_file():
    """Get Docker Compose file path from environment variable or use default."""
    compose_file = os.getenv('DOCKER_COMPOSE_FILE', 'docker-compose.yml')  # Default to 'docker-compose.yml'
    logger.info(f"Using Docker Compose file: {compose_file}")
    return compose_file


def main():
    config = load_yaml_config('services.yml')
    environment = config.get('environment', 'development')  # Default to development if not set
    services = config.get('services', [])
    
    initialize_version_file()
    
    # Get services from environment variables or command-line arguments
    # services = get_services()
    
    # Read version data from file
    version_data = read_version_data()

    # Process each service in parallel
    # with ThreadPoolExecutor() as executor:
    #     futures = [executor.submit(process_service, service, version_data) for service in services]
    #     for future in futures:
    #         future.result()  # Wait for all tasks to complete
    for service in services:
        process_service(service["name"], version_data, environment)
        save_version_to_file(version_data)

    # Save version data after all services are processed
    # save_version_to_file(version_data)
    
    # Get the dynamic Docker Compose file
    compose_file = generate_docker_compose(services, environment)

    # Bring up Docker containers
    logger.info("🚢 Bringing up Docker containers", extra={"event": "docker_compose_up"})
    run_command(f"docker-compose -f {compose_file} up -d", event="docker_compose_up")
    logger.info("✅ All services have been built, tagged, and pushed.", extra={"event": "deployment_complete"})

if __name__ == "__main__":
    main()
