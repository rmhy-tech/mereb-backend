pipeline {
    agent any

    environment {
        PROJECT_DIR = 'user-service'       // Path to the user-service project inside the monorepo
        IMAGE_NAME = 'user-service'        // Docker image name
        DOCKER_HUB_USERNAME = credentials('docker-hub-username') // Docker Hub credentials from Jenkins
        DOCKER_HUB_PASSWORD = credentials('docker-hub-password')
        SLACK_WEBHOOK_URL = credentials('slack-webhook')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/rmhy-tech/mereb-backend.git',
                    credentialsId: '4d8184fd-ce71-4cb5-a166-45bb06ec67cc'
            }
        }

        // Build Docker image if needed or skip if pulling the image from Docker Hub
        stage('Pull Docker Image from Docker Hub') {
            steps {
                script {
                    sh "docker login -u ${DOCKER_HUB_USERNAME} -p ${DOCKER_HUB_PASSWORD}"
                    sh "docker pull ${DOCKER_HUB_USERNAME}/${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Stop and Remove Existing Container') {
            steps {
                script {
                    // Stop and remove the existing container
                    sh """
                        docker ps -a -q --filter "name=user-service" | grep . && docker stop user-service || true
                        docker ps -a -q --filter "name=user-service" | grep . && docker rm user-service || true
                    """
                }
            }
        }

        stage('Deploy User Service with Docker Compose') {
            steps {
                script {
                    // Use docker-compose to deploy the service
                    sh """
                        docker-compose -f ${WORKSPACE}/docker-compose.yml up -d user-service
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    retry(5) {
                        sleep 10
                        // Check if the user-service is running and healthy
                        sh 'curl -f http://localhost:8082/actuator/health || exit 1'
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                // Send a success message to Slack
                sh """
                    curl -X POST -H "Content-type: application/json" \
                    --data "{\\"text\\": \\"✅ *CD Deployment SUCCESS* for user-service\\n *Job*: ${env.JOB_NAME}\\n *Build Number*: ${env.BUILD_NUMBER}\\n *Duration*: ${currentBuild.durationString}\\n *Built By*: ${currentBuild.getBuildCauses()[0].userId ?: 'Automated Trigger'}\\n *Build URL*: ${env.BUILD_URL}\\"}" \
                    ${SLACK_WEBHOOK_URL}
                """
            }
            echo 'Deployment of user-service completed successfully.'
        }
        failure {
            script {
                // Send a failure message to Slack
                sh """
                    curl -X POST -H "Content-type: application/json" \
                    --data "{\\"text\\": \\"❌ *CD Deployment FAILURE* for user-service\\n *Job*: ${env.JOB_NAME}\\n *Build Number*: ${env.BUILD_NUMBER}\\n *Duration*: ${currentBuild.durationString}\\n *Built By*: ${currentBuild.getBuildCauses()[0].userId ?: 'Automated Trigger'}\\n *Build URL*: ${env.BUILD_URL}\\"}" \
                    ${SLACK_WEBHOOK_URL}
                """
            }
            echo 'Deployment of user-service failed.'
        }
        always {
            // Optionally, clean up images that are no longer needed
            sh 'docker image prune -f'
        }
    }
}
