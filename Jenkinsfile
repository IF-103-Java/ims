#!/usr/bin/env groovy

pipeline {
    agent any

    tools {
        jdk "JDK-13.0.1"
    }

    environment {
        DOCKERHUB_REPO = "if103java/ims"
    }

    stages {
        stage('Show versions') {
            steps {
                sh '''
                        java -version
                        javac -version
                        docker --version
                   '''
            }
        }

        stage('Build application') {
            steps {
                sh './mvnw clean install'
            }
        }

        stage('Build & Push docker image') {
            when { anyOf { branch 'master'; branch 'dev' } }

            environment {
                TAG = "$BRANCH_NAME-build-$BUILD_NUMBER".replaceAll("/", "-")
                IMAGE_NAME = "$DOCKERHUB_REPO:$TAG"
            }

            steps {
                script {
                    def springBootImage = docker.build(env.IMAGE_NAME)
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        springBootImage.push()
                        if (env.BRANCH_NAME == 'master')
                            springBootImage.push("latest")
                    }
                }
                dockerClean()
            }
        }

        stage('Deploy docker image') {
            when { branch "master" }

            steps {
                timeout(time: 20, unit: 'MINUTES') { input message: 'Approve Deploy?', ok: 'Yes' }
                script {
                    withCredentials([string(credentialsId: 'ims-staging-user', variable: 'SSH_USER'),
                                     string(credentialsId: 'ims-staging-host', variable: 'SSH_HOST')]) {
                        sshagent(credentials: ['ims-staging-ssh-key']) {
                            SSH_EXEC = "ssh -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST"
                            updateRemoteDockerContainer(SSH_EXEC)
                            dockerClean(SSH_EXEC)
                        }
                    }
                }
            }
        }
    }
}

void dockerClean(String commandPrefix = "") {
    sh "$commandPrefix docker image prune -af || true"
    sh "$commandPrefix docker container prune -af || true"
}


void updateRemoteDockerContainer(String sshPrefix) {
    sh "$sshPrefix docker pull ${env.DOCKERHUB_REPO}:latest"
    sh "$sshPrefix docker stop ims-spring-boot || true"
    sh "$sshPrefix docker rm ims-spring-boot || true"
    sh "$sshPrefix docker run --env-file .ims-env \
                              --name ims-spring-boot \
                              -p 80:8080 \
                              --restart always \
                              -d ${env.DOCKERHUB_REPO}:latest"
}
