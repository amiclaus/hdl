import static groovy.io.FileType.FILES

def envar = System.getenv()

def list = [] 
def projName
def projPath
def projJobPath = JOB_NAME

projJobPath = projJobPath.substring(0, projJobPath.lastIndexOf("/"))

def buildAllScript = "parallel("

new File(WORKSPACE + "/projects").eachFileRecurse(FILES) {
  if(it.name.endsWith('system_project.tcl')) {
        projName = it.path.replace(WORKSPACE + "/projects/","")
   		projName = projName.replace("/system_project.tcl","")
    	projName = projName.replace("/",",")
    	list.add(projName) 
  }
}

list.eachWithIndex { item, index ->
  buildAllScript += "\n b$index: {build job: '"+ projJobPath + "/projects/$item'},"
}

buildAllScript += "\n)\nfailFast: false" 

folder(projJobPath + "/projects") {
	list.each {
  		println it
  		projPath = WORKSPACE + "/projects/" + it
  		projPath = projPath.replace(",","/")
  		it = projJobPath + "/projects/" + it
  		job(it){
          parameters {
            stringParam("PATH", PATH)
      	  }
          steps {
            shell("make -C $projPath")
          }
        }
  	}
}
pipelineJob(projJobPath + "/build-projects"){
    definition {
      parameters {
        stringParam("PATH", PATH)
      }
      triggers {
      	upstream(projJobPath + '/hdl-pipeline', 'SUCCESS')
      }
      cps {
        script(buildAllScript)
        sandbox()
      }
    }
  }