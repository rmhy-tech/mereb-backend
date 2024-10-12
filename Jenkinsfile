pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3'  // Assumes Maven is installed in Jenkins
        PROJECT_DIR = 'user-service' // Path to the user-service project inside the monorepo
        POSTMAN_API_KEY = credentials('postman-api-key') // Use the ID you set in the Jenkins credentials
    }

    tools {
        nodejs "NodeJS_Latest" // Ensure Node.js is configured in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/rmhy-tech/mereb-backend.git',
                    credentialsId: '263fe52f-0aa4-44ad-ae87-9bb00a281eca'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image using the provided Dockerfile
                    bat 'docker build -t user-service .'
                }
            }
        }

        stage('Run User Service in Docker') {
            steps {
                script {
                    // Start the Docker container and run it in detached mode
                    bat 'docker run -d -p 8082:8082 --name user-service user-service'
                }
            }
        }

        stage('Wait for User Service to Start') {
            steps {
                script {
                    retry(5) { // Retry 5 times with a 10-second sleep to wait for the service to start
                        sleep 10
                        bat 'curl http://localhost:8082/api/v2/auth/health'
                    }
                }
            }
        }

        stage('Install Postman CLI') {
            steps {
                bat '''
                    powershell.exe -NoProfile -InputFormat None -ExecutionPolicy AllSigned -Command \
                    "[System.Net.ServicePointManager]::SecurityProtocol = 3072; \
                    iex ((New-Object System.Net.WebClient).DownloadString('https://dl-cli.pstmn.io/install/win64.ps1'))"
                '''
            }
        }

        stage('Postman CLI Login') {
            steps {
                bat 'postman login --with-api-key %POSTMAN_API_KEY%'
            }
        }

        stage('Run Postman Collection') {
            steps {
                bat 'postman collection run "9308902-1c9fad5f-f1b4-4949-a1b3-38b9b50b824a" -e "9308902-8c3e09bd-9de5-4078-9549-12a713ad3489"'
            }
        }
    }

    post {
        success {
            script {
                // Stop and remove the Docker container after tests
                bat 'docker stop user-service'
                bat 'docker rm user-service'
            }

            echo 'Build and Postman tests executed successfully.'
        }

        failure {
            script {
                // Stop the service even if the build fails
                bat 'docker stop user-service || true'
                bat 'docker rm user-service || true'
            }

            echo 'Build or tests failed!'
        }

        always {
            // Optionally clean up the Docker images to save space
            bat 'docker rmi user-service || true'
        }
    }
}
