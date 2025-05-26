#!/usr/bin/env groovy

@Library('jenkins-lib@master') _

pipeline {
  agent {
    node {
      label 'kube'
    }
  }
  options {
    ansiColor('xterm')
  }
  environment {
    SERVICE_NAME = "${params.service_name ?: getRepoNameFromGitUrl(env.GIT_URL)}"
    GIT_BRANCH = getBranchNameFromEnv()
  }

  stages {
    stage('Deploy: Setup') {
      steps {
        script {
          intialSetup()
        }
      }
    }
    stage('Deploy: Identify docker namespaces') {
      steps {
        script {
          result = candidature("${SERVICE_NAME}", "${GIT_BRANCH}")
        }
      }
    }
    stage("Deploy: Create an executor docker image") {
      steps {
        script {
          executor = dockerBuild("${SERVICE_NAME}:${env.GIT_COMMIT}-checker", "--target executor .")
        }
      }
    }
    stage('Deploy: Prepare all namespaces') {
      when { expression { result != '' } }
      steps {
        script {
          dockerRunInsideAllNamespaces(executor, {
            sh "SERVICE_NAME=${SERVICE_NAME} bash ./src/main/resources/scripts/prepare.sh"
          })
        }
      }
    }
    stage('Deploy: Reset QAData') {
      when { expression { params.reset_qadata.toString() == 'true' && params.env_name != "" } }
      steps {
        script {
          dockerRunInside(executor, {
            sh "TEAM_SUFFIX=-${params.env_name} VPC_SUFFIX=-stag mvn migrations:reset-qadata -Dapp.environment=docker"
          })
        }
      }
    }
    stage('Deploy: Package & Push to ECR') {
      when { expression { result != '' } }
      steps {
        script {
          dockerBuildAndPush("${SERVICE_NAME}", "--build-arg SERVICE_NAME=${SERVICE_NAME} .")
        }
      }
    }
    stage('Deploy: To docker namespaces') {
      when { expression { result != '' } }
      steps {
        script {
          deploy([:])
        }
      }
    }
    stage('Deploy: Teardown') {
      steps {
        script {
          postStep()
        }
      }
    }
  }
}
