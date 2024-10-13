pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3'  // Assumes Maven is installed in Jenkins
        PROJECT_DIR = 'user-service' // Path to the user-service project inside the monorepo
        POSTMAN_API_KEY = credentials('postman-api-key') // Use the ID you set in the Jenkins credentials

        SLACK_WEBHOOK_URL = credentials('slack-webhook') // Slack Webhook from Jenkins credentials
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub') // Docker Hub credentials from Jenkins
        // DOCKER_HUB_USERNAME = 'leultewolde' // Your Docker Hub username
        IMAGE_NAME = 'user-service' // Docker image name
        LINODE_IP = credentials('linode-ip')
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
                        bat "docker build -t ${IMAGE_NAME} ." // Build the Docker image based on the JAR from the Maven build
                    }
                }
            }
        }

        stage('Remove Existing Container') {
            steps {
                script {
                    // Stop and remove the container if it exists
                    bat """
                        docker ps -a -q --filter "name=${IMAGE_NAME}" | findstr . && docker stop ${IMAGE_NAME} || exit 0
                        docker ps -a -q --filter "name=${IMAGE_NAME}" | findstr . && docker rm ${IMAGE_NAME} || exit 0
                    """
                }
            }
        }

        stage('Run User Service in Docker with Test Profile') {
            steps {
                script {
                    bat "docker run -d -p 8082:8082 --name ${IMAGE_NAME} ${IMAGE_NAME} --spring.profiles.active=test"
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
                bat "docker stop ${IMAGE_NAME} || exit 0"
                bat "docker rm ${IMAGE_NAME} || exit 0"
            }

            echo 'Build and Postman tests executed successfully.'

            // Docker Hub login and push
            withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                bat """
                    docker login -u %DOCKER_HUB_USERNAME% -p %DOCKER_HUB_PASSWORD%
                    docker tag ${IMAGE_NAME}:latest %DOCKER_HUB_USERNAME%/${IMAGE_NAME}:latest
                    docker push %DOCKER_HUB_USERNAME%/${IMAGE_NAME}:latest
                """
            }

            // Run commands on Linode after the push
            withCredentials([sshUserPrivateKey(credentialsId: 'linode-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                bat """
                    plink -i %SSH_KEY% root@${LINODE_IP} ^
                    "docker compose pull && docker compose up -d"
                """
            }

            // Send success message to Slack
            withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK_URL')]) {
                bat """
                    curl -X POST -H "Content-type: application/json" ^
                    --data "{\\"text\\": \\"✅ *Build SUCCESS*\\n *Job*: ${env.JOB_NAME}\\n *Build Number*: ${env.BUILD_NUMBER}\\n *Status*: SUCCESS\\n *Duration*: ${currentBuild.durationString}\\n *Built By*: ${currentBuild.getBuildCauses()[0].userId ?: 'Automated Trigger'}\\n *Build URL*: [Open Build](${env.BUILD_URL})\\n *Git Branch*: ${env.GIT_BRANCH}\\n *Commit ID*: ${env.GIT_COMMIT}\\"}" ^
                    %SLACK_WEBHOOK_URL%
                """
            }
        }

        failure {
            script {
                bat "docker stop ${IMAGE_NAME} || docker rm ${IMAGE_NAME} || exit 0"
            }

            echo 'Build or tests failed!'

            withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK_URL')]) {
                bat """
                    curl -X POST -H "Content-type: application/json" ^
                    --data "{\\"text\\": \\"❌ *Build FAILED*\\n *Job*: ${env.JOB_NAME}\\n *Build Number*: ${env.BUILD_NUMBER}\\n *Status*: FAILURE\\n *Duration*: ${currentBuild.durationString}\\n *Built By*: ${currentBuild.getBuildCauses()[0].userId ?: 'Automated Trigger'}\\n *Build URL*: [Open Build](${env.BUILD_URL})\\n *Git Branch*: ${env.GIT_BRANCH}\\n *Commit ID*: ${env.GIT_COMMIT}\\"}" ^
                    %SLACK_WEBHOOK_URL%
                """
            }
        }

        always {
            script {
                bat '''
                    docker rmi -f ${IMAGE_NAME} || exit 0
                '''
            }
        }
    }
}