#!/usr/bin/env groovy

pipeline {
    agent any

    tools {
        jdk "JDK-13.0.1"
    }

    stages {

        stage('Check versions') {
            steps {
                sh '''
                        java -version
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
            when { branch "cd/jenkinsfile" }
            steps {
                script {
                    def springBootImage = docker.build("if103java/ims:$BUILD_NUMBER")
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        springBootImage.push()
                        springBootImage.push("latest")
                    }
                }
                sh "docker rmi if103java/ims:$BUILD_NUMBER"
            }
        }

//        stage('Deploy docker image') {
//            when { branch "cd/jenkinsfile" }
//            steps {
//                sh """docker pull ${springBootImage.imageName()}
//                      docker stop ${springBootImage.imageName()}
//                      docker run --env-file ~/.ims-env \\
//                                 --name ims-spring-boot \\
//                                 -p 80:8080 \\
//                                 -d ${springBootImage.imageName()}"""
//            }
//        }
    }
}
