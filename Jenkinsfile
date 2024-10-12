pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3'  // Assumes Maven is installed in Jenkins
        PROJECT_DIR = 'user-service' // Path to the user-service project inside the monorepo
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/rmhy-tech/mereb-backend.git',
                    credentialsId: '263fe52f-0aa4-44ad-ae87-9bb00a281eca'
            }
        }

        stage('Build') {
            steps {
                // Navigate to the user-service directory and build it
                dir("${PROJECT_DIR}") {
                    bat "${MAVEN_HOME}/bin/mvn clean install"
                }
            }
        }

        stage('Test') {
            steps {
                // Run tests for the user-service
                dir("${PROJECT_DIR}") {
                    bat "${MAVEN_HOME}/bin/mvn test"
                }
            }
        }

        stage('Package') {
            steps {
                // Package the user-service into a JAR
                dir("${PROJECT_DIR}") {
                    bat "${MAVEN_HOME}/bin/mvn package"
                }
            }
        }
    }

    post {
        success {
            // Archive the JAR artifact
            dir("${PROJECT_DIR}") {
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }

            // Publish test results
            dir("${PROJECT_DIR}") {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        failure {
            // Handle build failures
            echo "Build failed!"
        }
    }
}
