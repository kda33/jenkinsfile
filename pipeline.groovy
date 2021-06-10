stages {
    stage("Name of stage") {
      steps {
        sh '/home/ec2-user/somescript.sh'
        // Sleep step for wait 
        sleep(time: 10, unit: "SECONDS")
      }
    }
    
    stage("Quality gate") {
      steps {
        script {
        def qualitygate = waitForQualityGate()
          if (qualitygate.status != "OK") {
           waitForQualityGate abortPipeline: true
          }
      }
      }
    }
  
  stage('Run tests') {
      steps {
        sh './gradlew --no-daemon clean test'
        sleep(time: 5, unit: "SECONDS")
        // Stash includes 'path to the file' name: 'whatether'
        stash includes: '**', name: 'D'
      }
    }
  
  stage('Unstash') {
      steps {
        // node where will be unstashed files
        node('master') {
          // UnStash somefiles
          unstash 'D'
        }
      }
    }
}
_________________________________

def autoCancelled = false

try {
  stage('checkout') {
    ...
    if (your condition) {
      autoCancelled = true
      error('Aborting the build.')
    }
  }
} catch (e) {
  if (autoCancelled) {
    currentBuild.result = 'SUCCESS'
    // return here instead of throwing error to keep the build "green"
    return
  }
  // normal error handling
  throw e
}
