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
            when { anyOf { branch 'cd/jenkinsfile'; branch 'cd/jenkinsfile' } }

            environment {
                TAG = "$BRANCH_NAME-build-$BUILD_NUMBER".replaceAll("/", "-")
                IMAGE_NAME = "$DOCKERHUB_REPO:$TAG"
            }

            steps {
                script {
                    def springBootImage = docker.build(env.IMAGE_NAME)
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        springBootImage.push()
                        if (env.BRANCH_NAME == 'cd/jenkinsfile')
                            springBootImage.push("latest")
                    }
                }
                dockerClean()
            }
        }

        stage('Deploy docker image') {
            when { branch "cd/jenkinsfile" }

            steps {
                timeout(time: 20, unit: 'MINUTES') { input message: 'Approve Deploy?', ok: 'Yes' }
                script {
                    withCredentials([string(credentialsId: 'ims-staging-user', variable: 'SSH_USER'),
                                     string(credentialsId: 'ims-staging-host', variable: 'SSH_HOST')]) {
                        sshagent(credentials: ['ims-staging-ssh-key']) {
                            SSH_EXEC = "ssh -o StrictHostKeyChecking=no $SSH_USER@$SSH_HOST"
                            sh "$SSH_EXEC docker pull ${env.DOCKERHUB_REPO}:latest"
                            sh "$SSH_EXEC docker rm -f ims-spring-boot || true"
                            sh "$SSH_EXEC docker run --env-file .ims-env \
                                                     --name ims-spring-boot \
                                                     -p 80:8080 \
                                                     --restart always \
                                                     -d ${env.DOCKERHUB_REPO}:latest"
                            dockerClean(SSH_EXEC)
                        }
                    }
                }
            }
        }
    }
}

void dockerClean(String commandPrefix="") {
    sh "($commandPrefix docker ps -a --no-trunc | grep 'Exit' | awk '{print \$1}' | xargs docker rm) || true"
    sh "($commandPrefix docker images --no-trunc | grep none | awk '{print \$3}' | xargs docker rmi) || true"
}
