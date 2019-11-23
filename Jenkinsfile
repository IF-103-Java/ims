#!/usr/bin/env groovy

pipeline {
    agent any

    tools {
        jdk "JDK-13.0.1"
    }

    stages {
        stage('Check versions') {
            steps {
                sh 'java -version'
                sh 'javac -version'
                sh 'docker --version'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean install'
            }
        }
    }
}
