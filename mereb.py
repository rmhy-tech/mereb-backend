import os
import sys

from helpers.config import load_config, ServiceNotFoundError
from helpers.docker_compose import run_docker_compose, destroy_docker_compose, generate_docker_compose
from helpers.get_logging import logger
from helpers.processes import package_all_services, clean_all_services, \
    verify_all_services, build_and_deploy_all_services, maven_test_all_services
from helpers.test_services import test_all_services


def main():
    try:
        services, services_yml, command, environment, postman_key, postman_collection = load_config()

        if command == 'mvn-build':
            package_all_services(services, os.getcwd())
        elif command == 'mvn-test':
            maven_test_all_services(services, os.getcwd())
        elif command == 'mvn-clean':
            clean_all_services(services, os.getcwd())
        elif command == 'mvn-verify':
            verify_all_services(services, os.getcwd())
        elif command == 'test':
            test_all_services(services, postman_key, postman_collection)
        elif command == 'build':
            build_and_deploy_all_services(services, environment, os.getcwd())
        elif command == 'deploy':
            build_and_deploy_all_services(services, environment, os.getcwd())
            run_docker_compose(services_yml, environment)
            test_all_services(services, postman_key, postman_collection)
        elif command == 'compose':
            generate_docker_compose(services_yml, environment)
        elif command == 'up':
            run_docker_compose(services_yml, environment)
        elif command == 'down':
            destroy_docker_compose()
    except Exception as e:
        logger.error(f"An error occurred: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
