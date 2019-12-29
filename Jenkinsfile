#!/usr/bin/env groovy

pipeline {
    agent any

    tools {
        jdk "JDK-13.0.1"
    }

    environment {
        DOCKERHUB_REPO = "if103java/ims"
        IMAGE_TAG = "$BRANCH_NAME-build-$BUILD_NUMBER".replaceAll("/", "-")
        IMAGE_NAME = "$DOCKERHUB_REPO:$IMAGE_TAG"
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

            steps {
                script {
                    def springBootImage = docker.build(env.IMAGE_NAME)
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        springBootImage.push()
                        if (env.BRANCH_NAME == 'master')
                            springBootImage.push("latest")
                    }
                    sh "docker rmi ${env.IMAGE_NAME}"
                }
            }
        }

        stage('Deploy docker image') {
            when { anyOf { branch 'master'; branch 'dev' } }

            steps {
                timeout(time: 20, unit: 'MINUTES') { input message: 'Approve Deploy?', ok: 'Yes' }
                script {
                    withCredentials([string(credentialsId: 'ims-staging-user', variable: 'SSH_USER'),
                                     string(credentialsId: 'ims-staging-host', variable: 'SSH_HOST')]) {
                        sshagent(credentials: ['ims-staging-ssh-key']) {
                            if (env.BRANCH_NAME == 'master') {
                                updateDockerContainer(
                                    "$SSH_USER",
                                    "$SSH_HOST",
                                    "${env.DOCKERHUB_REPO}:latest",
                                    "ims-spring-boot",
                                    80,
                                    ".ims-env"
                                )
                            } else {
                                updateDockerContainer(
                                    "$SSH_USER",
                                    "$SSH_HOST",
                                    "${env.IMAGE_NAME}",
                                    "ims-spring-boot-dev",
                                    8080,
                                    ".ims-env-dev"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

def updateDockerContainer(String user, String host, String image, String container, Integer port, String evnFile) {
    commands = [
        "docker pull $image",
        "docker stop $container",
        "docker run --env-file $evnFile \
                              --name $container \
                              -p $port:8080 \
                              --restart always \
                              -d $image",
        "docker rm $container",
        "docker image prune -af --filter 'until=12h'"
    ]
    commands.each { command ->
        sh "ssh -o StrictHostKeyChecking=no $user@$host $command"
    }
}
