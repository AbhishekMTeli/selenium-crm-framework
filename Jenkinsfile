pipeline {
    agent any

    // ── Parameters exposed in Jenkins UI ─────────────────────────────────────
    parameters {
        choice(name: 'BROWSER',
               choices: ['chrome', 'firefox', 'edge'],
               description: 'Browser to run tests on')
        choice(name: 'ENV',
               choices: ['qa', 'staging'],
               description: 'Target environment')
        choice(name: 'SUITE',
               choices: ['smoke', 'regression', 'parallel'],
               description: 'TestNG suite to execute')
        booleanParam(name: 'HEADLESS',
                     defaultValue: true,
                     description: 'Run browser in headless mode')
    }

    environment {
        JAVA_HOME  = tool 'JDK-11'
        MAVEN_HOME = tool 'Maven-3.9'
        PATH       = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"

        // CRM credentials stored in Jenkins Credentials Store
        CRM_ADMIN_USER = credentials('crm-admin-username')
        CRM_ADMIN_PASS = credentials('crm-admin-password')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo "Branch: ${env.BRANCH_NAME} | Build: ${env.BUILD_NUMBER}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -q'
            }
        }

        stage('Test') {
            steps {
                sh """
                    mvn test \
                        -P${params.SUITE} \
                        -Dbrowser=${params.BROWSER} \
                        -Denv=${params.ENV} \
                        -Dheadless=${params.HEADLESS} \
                        -Dadmin.username=${CRM_ADMIN_USER} \
                        -Dadmin.password=${CRM_ADMIN_PASS} \
                        -Dmaven.test.failure.ignore=true
                """
            }
        }
    }

    post {
        always {
            // Publish TestNG results
            testNG reportFilenamePattern: 'target/surefire-reports/testng-results.xml',
                   showFailedBuilds: true

            // Publish ExtentReports HTML
            publishHTML(target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : 'target/extent-reports',
                reportFiles          : '*.html',
                reportName           : 'CRM Test Report'
            ])

            // Archive screenshots on failure
            archiveArtifacts artifacts: 'target/screenshots/**/*.png',
                             allowEmptyArchive: true

            // Archive logs
            archiveArtifacts artifacts: 'target/logs/*.log',
                             allowEmptyArchive: true
        }

        success {
            echo "Build PASSED - ${params.SUITE} suite on ${params.ENV}"
            // Uncomment to send Slack notification:
            // slackSend channel: '#qa-automation', color: 'good',
            //           message: "CRM Tests PASSED: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }

        failure {
            echo "Build FAILED - Check reports"
            // slackSend channel: '#qa-automation', color: 'danger',
            //           message: "CRM Tests FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }

        cleanup {
            cleanWs()
        }
    }
}
