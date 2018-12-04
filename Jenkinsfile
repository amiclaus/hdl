def get_path_var() {
    
    /* Create all paths with env vars inherited from the Folder plugin  */
        List XILINX_PATHS = [
                "${env.XILINX_PREFIX_PATH}/Vivado/${XILINX_VERSION}/bin",
                "${env.XILINX_PREFIX_PATH}/Vivado_HLS/${XILINX_VERSION}/bin",
                "${env.XILINX_PREFIX_PATH}/SDK/${XILINX_VERSION}/bin"
            ]
        List QUARTUS_PATHS = [
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/quartus/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/quartus/sopc_builder/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds/sdk2/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds/bin/gnu/H-x86_64-pc-linux-gnu/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/modelsim_ase/linuxaloem",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/mentor/gnu/arm/baremetal/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/preloadergen",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/mkimage",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/mkpimage",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/device_tree",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/diskutils",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/imagecat",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/secureboot",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/gnu/dtc",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/ds-5/sw/gcc/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/ds-5/sw/ARMCompiler5.06u1/bin",
                "${env.QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/ds-5/bin"
            ]
        "${env.PATH}:" + XILINX_PATHS.join(':') + ':' + QUARTUS_PATHS.join(':')
}

pipeline {
    agent any

    environment {
        PATH=get_path_var()
		BUILD_BRANCH = 'jenkins-files'
    }
    
    stages {
        stage('Checkout HDL') {
            steps {
				sh 'printenv'
				withFolderProperties {
					git url: 'https://github.com/analogdevicesinc/hdl.git', branch: BUILD_BRANCH

                }
            }
        }
		stage('Generate Projects') {
            steps {
                 jobDsl targets:'generate_projects.groovy' ,
                 additionalParameters: [PATH: env.PATH, WORKSPACE: env.WORKSPACE, JOB_NAME: env.JOB_NAME]
            }
        }
        stage('Build Library') {
             steps {
			     withFolderProperties {
                     sh "make lib"
			    }
             }
        }
    }
}