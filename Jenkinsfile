pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3'  // Assumes Maven is installed in Jenkins
        PROJECT_DIR = 'user-service' // Path to the user-service project inside the monorepo
        POSTMAN_API_KEY = credentials('postman-api-key') // Use the ID you set in the Jenkins credentials
        SLACK_CHANNEL = '#builds' // Or any other channel you want to use
        SLACK_CREDENTIAL_ID = credentials('slack-token')
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

        stage('Build with Maven') {
            steps {
                dir("${PROJECT_DIR}") {
                    bat "${MAVEN_HOME}/bin/mvn clean package -DskipTests"  // Run the Maven build
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir("${PROJECT_DIR}") { // Navigate to the project directory where the Dockerfile is located
                    script {
                        bat 'docker build -t user-service .' // Build the Docker image based on the JAR from the Maven build
                    }
                }
            }
        }

        stage('Run User Service in Docker with Test Profile') {
            steps {
                script {
                    bat 'docker run -d -p 8082:8082 --name user-service user-service --spring.profiles.active=test'
                }
            }
        }

        stage('Wait for User Service to Start') {
            steps {
                script {
                    retry(5) {
                        sleep 10
                        bat 'curl http://localhost:8082/actuator/health'
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
            slackSend(channel: "${SLACK_CHANNEL}", color: 'good', message: "Build SUCCESS: ${env.JOB_NAME} [${env.BUILD_NUMBER}] (${env.BUILD_URL})")
        }

        failure {
            script {
                // Stop the service even if the build fails
                bat 'docker stop user-service || docker rm user-service || exit 0'
            }

            echo 'Build or tests failed!'
            slackSend(channel: "${SLACK_CHANNEL}", color: 'danger', message: "Build FAILED: ${env.JOB_NAME} [${env.BUILD_NUMBER}] (${env.BUILD_URL})")
        }

        always {
            // Clean up the Docker images to save space
            script {
                bat '''
                    docker rmi -f user-service || exit 0
                '''
            }
        }
    }
}