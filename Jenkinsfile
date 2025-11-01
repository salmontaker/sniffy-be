pipeline {
    agent any

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew build -x test --no-daemon'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def repoUrl = scm.userRemoteConfigs[0].url
                    def repoName = repoUrl.tokenize('/').last().replace('.git', '')

                    sh "docker build -t ${repoName}:latest ."
                    sh "docker stop ${repoName} || true"
                    sh "docker rm ${repoName} || true"
                    sh """
                        docker run \\
                            -d \\
                            --name ${repoName} \\
                            --network=web \\
                            --restart=always \\
                            ${repoName}:latest
                    """
                }
            }
        }

        stage('Clean Up') {
            steps {
                sh 'docker image prune -f --filter "dangling=true"'
            }
        }
    }
}