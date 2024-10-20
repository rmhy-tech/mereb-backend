import yaml

from helpers.get_logging import logger
from helpers.processes import run_command

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

        tag = f"{service['version']}-{environment}" if environment != 'production' else service['version']
        if service["version"] == 'latest' and environment != 'production':
            tag = f"{environment}-latest"

        env_file = {'env_file': service.get('env_file', f'.env.{environment}')} if environment == 'production' else {}

        service_definition = {
            'image': f'leultewolde/{service["name"]}:{tag}',
            'container_name': service['name'],
            'ports': service_ports,
            'environment': service_environment,
            'networks': ['mereb_app-network'],
            'healthcheck': service.get('healthcheck')
        }

        service_definition.update(env_file)

        compose['services'][service['name']] = service_definition

    # Write the generated Docker Compose to a file
    compose_file = f'docker-compose.{environment}.yml' if environment != 'production' else 'docker-compose.yml'

    with open(compose_file, 'w') as file:
        yaml.dump(compose, file, default_flow_style=False)

    logger.info(f"📝 Docker Compose file for {environment} environment generated as {compose_file}")
    return compose_file


def run_docker_compose(services, environment):
    # Get the dynamic Docker Compose file
    compose_file = generate_docker_compose(services, environment)

    # Bring up Docker containers
    logger.info("🚢 Bringing up Docker containers", extra={"event": "docker_compose_up"})
    run_command(f"docker-compose -f {compose_file} up -d", event="docker_compose_up")
    logger.info("✅ All services have been built, tagged, pushed, and composed.", extra={"event": "deployment_complete"})
    logger.info(" 🎉 Deployment complete!")


def destroy_docker_compose():
    logger.info("🛠️ Bringing down Docker containers", extra={"event": "docker_compose_down"})
    run_command("docker-compose down", event="docker_compose_down")
