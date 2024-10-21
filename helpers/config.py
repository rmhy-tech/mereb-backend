import argparse
import os

import yaml

from helpers.get_logging import logger

def load_yaml_config(filepath):
    """Load YAML configuration from a file."""
    try:
        with open(filepath, 'r') as stream:
            return yaml.safe_load(stream)
    except FileNotFoundError:
        logger.error(f"Configuration file not found: {filepath}")
        raise  # Re-raise exception to be caught higher up if needed
    except yaml.YAMLError as e:
        logger.error(f"Error parsing YAML file: {filepath} - {e}")
        raise  # Re-raise exception to be caught higher up if needed
    except Exception as e:
        logger.error(f"Unexpected error while loading YAML file: {filepath} - {e}")
        raise  # Re-raise exception to be caught higher up if needed

class ServiceNotFoundError(Exception):
    """Custom exception for missing items."""

    def __init__(self, missing_services):
        self.missing_services = missing_services
        message = f"The following services do not exist: {', '.join(missing_services)}"
        super().__init__(message)


def find_services(services_from_config, services_requested):
    service_names_from_config = [service["name"] for service in services_from_config]

    if not services_requested:
        return list(set(service_names_from_config))
    else:
        missing_services = [service_name for service_name in services_requested if
                            service_name not in service_names_from_config]

        if missing_services:
            raise ServiceNotFoundError(missing_services)
        else:
            return list(set(services_requested))


def find_services_from_yml(services_from_config, services_requested):
    """Return services from config that match the names found in services_requested."""

    services_yml = [service for service in services_from_config if service["name"] in services_requested]

    return services_yml

def get_arguments():
    parser = argparse.ArgumentParser(description="Script to handle mereb services")

    parser.add_argument('--env', type=str, help='Set the environment (e.g. dev, prod, staging)', default='')

    # Add a positional argument for the command (build, test, etc.)
    parser.add_argument('command', type=str, help='Command to execute (e.g. build, test, clean, verify)')
    parser.add_argument('--services', type=str, help='Comma-separated list of services', default='')

    # Parse the command-line arguments
    args = parser.parse_args()
    command = args.command
    services_requested = args.services.split(',') if args.services else []

    if args.env == "dev":
        config_file_name = "services-dev.yml"
    elif args.env == "staging":
        config_file_name = "services-staging.yml"
    elif args.env == "prod":
        config_file_name = "services-prod.yml"
    else:
        config_file_name = "services.yml"

    return config_file_name, command, services_requested

def load_config():
    """Load configuration from YAML file."""
    try:
        config_file_name, command, services_requested = get_arguments()
        config_file = os.getenv('CONFIG_FILE', f'./build-config/{config_file_name}')  # Default to 'services.yml'
        logger.info(f"Using configuration file: {config_file}")

        configs = load_yaml_config(config_file)
        environment = configs.get('environment', 'development')

        services_from_config = configs.get('services', [])
        services = find_services(services_from_config, services_requested)

        postman_key = configs.get('postman-apiKey', '')
        postman_collection = configs.get('postman-collection', '')

        services_yml = find_services_from_yml(services_from_config, services)

        return services, services_yml, command, environment, postman_key, postman_collection
    except FileNotFoundError as e:
        logger.error(f"Configuration file not found or path is incorrect: {e}")
        raise
    except ServiceNotFoundError as e:
        logger.error(f"One or more services not found: {e}")
        raise
    except Exception as e:
        logger.error(f"Error loading configuration: {e}")
        raise

