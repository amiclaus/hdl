import static groovy.io.FileType.FILES

def envar = System.getenv()

def list = [] 
def projName
def projPath
def projJobPath = JOB_NAME

projJobPath = projJobPath.substring(0, projJobPath.lastIndexOf("/"))

def ghdlWorkspace = WORKSPACE + "/ghdl"

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

pipelineJob(projJobPath + "/build-projects"){
    definition {
      parameters {
        stringParam("PATH", PATH)
      }
      triggers {
      	upstream(projJobPath + "/ghdl", 'SUCCESS')
      }
      cps {
        script(buildAllScript)
        sandbox()
      }
    }
  }
  
job(projJobPath + "/ghdl"){
	customWorkspace(ghdlWorkspace)
    scm{
		git('git@gitlab.analog.com:Platformation/ghdl.git', 'master')
	}
	triggers {
      	upstream(JOB_NAME, 'SUCCESS')
     }
}

ghdlWorkspace = ghdlWorkspace + "/bin"

def buildOneProject = '''#!/bin/bash

export DISPLAY_LINK_FOR_EXPORTED_FILES=yes
export PATH=$PATH:$GHDL_WORKSPACE
source use_jenkins.sh  >/dev/null 2>&1
source $(which set_env.sh) "hdl_2018_r1" >> jenkins_build_extra.log 2>&1
source $(which set_project_by_path.sh) >> jenkins_build_extra.log 2>&1
export FLOW_ARGUMENT="simple"

 # MAKE
 if [[ $MMU_PRJ_ALTERA -eq 0 ]]; then
	make NIOS2_MMU=0
 else
	make
 fi

 # BUILD STATUS
  if [ "$?" -eq "0" ]; then
		 source $(which get_tool_name_export_path.sh)
		 get_export_log_issue.sh
		 source $(which file_export_process.sh) network_export
		 build_linux_boot_files.sh network_export
  else
		 source $(which get_tool_name_export_path.sh)
		 get_export_log_issue.sh
  fi
'''

folder(projJobPath + "/projects") {

}

list.each {
	projPath = WORKSPACE + "/projects/" + it
	projPath = projPath.replace(",","/")
	it = projJobPath + "/projects/" + it
	job(it){
	  customWorkspace(projPath)
	  parameters {
		stringParam("PATH", PATH) 
		stringParam("GHDL_WORKSPACE", ghdlWorkspace)
	  }
	  steps {
		shell(buildOneProject)
	  }
	}
}