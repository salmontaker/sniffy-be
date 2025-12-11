pipeline {
    agent any

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    def repoUrl = scm.userRemoteConfigs[0].url
                    env.REPO_NAME = repoUrl.tokenize('/').last().replace('.git', '')
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew build -x test --no-daemon'
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([file(credentialsId: 'sniffy-be', variable: 'ENV_FILE')]) {
                    sh "docker build --label service=${REPO_NAME} -t ${REPO_NAME}:latest ."
                    sh "docker stop ${REPO_NAME} || true"
                    sh "docker rm ${REPO_NAME} || true"
                    sh """
                        docker run \\
                            -d \\
                            --name ${REPO_NAME} \\
                            --network=web \\
                            --restart=always \\
                            --env-file $ENV_FILE \\
                            ${REPO_NAME}:latest
                    """
                }
            }
        }

        stage('Clean Up') {
            steps {
                sh "docker image prune -f --filter 'label=service=${REPO_NAME}' --filter 'dangling=true'"
            }
        }
    }
}