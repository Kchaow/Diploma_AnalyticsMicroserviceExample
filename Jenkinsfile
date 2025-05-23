pipeline {
	agent any

    environment {
		MAVEN_HOME = '/usr/share/maven'
        M2_REPO = '/var/jenkins_home/.m2/repository'
        GIT_REPO_URL = 'https://github.com/Kchaow/Diploma_AnalyticsMicroserviceExample.git'
        MICROSERVICE_NAME = 'analytics'
        CHANGE_GRAPH_URL = 'http://host.docker.internal:8081/api/v1'
        GIT_BRANCH = 'main'
    }

    stages {
		stage('Checkout Code') {
			steps {
				git branch: "${GIT_BRANCH}", url: "${GIT_REPO_URL}"
            }
        }

        stage('Build with Maven') {
			steps {
				sh '${MAVEN_HOME}/bin/mvn clean package'
            }
        }

        stage('Deploy') {
			steps {
				echo "Проверка целостности связей"
        script {
					def response = httpRequest(
                url: "${CHANGE_GRAPH_URL}/change-graph/process/microservice/${MICROSERVICE_NAME}",
                httpMode: 'GET',
                acceptType: 'APPLICATION_JSON'
            )

            def graphIds = new groovy.json.JsonSlurper().parseText(response.content)
            def graphId = 0
            if (graphIds.isEmpty()) {
						def createGraphResponse = httpRequest(
                    url: "${CHANGE_GRAPH_URL}/change-graph",
                    httpMode: 'POST',
                    contentType: 'APPLICATION_JSON',
                    requestBody: "{\"associatedMicroservices\": [\"${MICROSERVICE_NAME}\"]}"
                )
                graphId = new groovy.json.JsonSlurper().parseText(createGraphResponse.content).id
            } else {
						graphId = graphIds[0]
            }

            sh "${MAVEN_HOME}/bin/mvn letunov:contract-scanner-maven-plugin:1.0-SNAPSHOT:verifyMicroservice -DchangeGraphId=${graphId} -DM2_REPO=${M2_REPO} -DmicroserviceIntegrityServerURL=${CHANGE_GRAPH_URL} -e -\"Dorg.slf4j.simpleLogger.defaultLogLevel\"=DEBUG"

            def attempt = 0
            def maxAttempt = 15
            def graphStatus = "WAIT_FOR_COMMIT"
            def changeGraph = [:]  // Initialize as empty map
            while (!graphStatus.equals("DONE")) {
						def getChangeGraphStatusResponse = httpRequest(
                    url: "${CHANGE_GRAPH_URL}/change-graph/${graphId}",
                    httpMode: 'GET',
                    acceptType: 'APPLICATION_JSON'
                )
                def responseContent = new groovy.json.JsonSlurper().parseText(getChangeGraphStatusResponse.content.toString())
                changeGraph = [
                    status: responseContent.status.toString(),
                    verificationStatus: responseContent.verificationStatus.toString()
                ]
                graphStatus = changeGraph.status
                if (graphStatus.equals("DONE")) {
							break
                }
                attempt++
                if (attempt >= 15) {
							error("Не удалось проверить целостность графа изменений")
                }
                sleep(7)
            }

            switch(changeGraph.verificationStatus) {
					case "ERROR":
                    currentBuild.result = 'FAILURE'
                    error("Граф изменений содержит нарушения целостности")
                    break
                case "WARNING":
                    echo "Граф изменений содержит замечания"
                    break
                case "OK":
                    echo "Граф изменений не имеет проблем"
            }

            sh "${MAVEN_HOME}/bin/mvn letunov:contract-scanner-maven-plugin:1.0-SNAPSHOT:updateMicroserviceGraph -DM2_REPO=${M2_REPO} -DmicroserviceIntegrityServerURL=${CHANGE_GRAPH_URL} -e -\"Dorg.slf4j.simpleLogger.defaultLogLevel\"=DEBUG"
        }
        echo "Выполняется деплой"
    }
}
    }
}