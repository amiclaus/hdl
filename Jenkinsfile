def get_path_var() {

    def TOOLPATHS = ''
    def XILINX_VERSION = '2018.2'
    def QUARTUS_VERSION = '18.0'
    def BUILD_BRANCH = 'master'
    def XILINX_PREFIX_PATH = '/opt/Xilinx'
    def QUARTUS_PREFIX_PATH = '/opt/altera'
    def NUM_JOBS = '16'
    
    /* Create all paths with env vars inherited from the Folder plugin  */
        List XILINX_PATHS = [
                "${XILINX_PREFIX_PATH}/Vivado/${XILINX_VERSION}/bin",
                "${XILINX_PREFIX_PATH}/Vivado_HLS/${XILINX_VERSION}/bin",
                "${XILINX_PREFIX_PATH}/SDK/${XILINX_VERSION}/bin"
            ]
        List QUARTUS_PATHS = [
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/quartus/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/quartus/sopc_builder/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds/sdk2/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/nios2eds/bin/gnu/H-x86_64-pc-linux-gnu/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/modelsim_ase/linuxaloem",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/mentor/gnu/arm/baremetal/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/preloadergen",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/mkimage",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/mkpimage",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/device_tree",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/diskutils",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/imagecat",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/altera/secureboot",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/host_tools/gnu/dtc",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/ds-5/sw/gcc/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/ds-5/sw/ARMCompiler5.06u1/bin",
                "${QUARTUS_PREFIX_PATH}/${QUARTUS_VERSION}/embedded/ds-5/bin"
            ]
        "${env.PATH}:" + XILINX_PATHS.join(':') + ':' + QUARTUS_PATHS.join(':')
}

pipeline {
    agent any

    environment {
        LM_LICENSE_FILE='2200@10.50.1.20'
        XILINXD_LICENSE_FILE='2100@10.50.1.20:4343@gnat.spd.analog.com'
        BUILD_BRANCH='master'
        PATH=get_path_var()
    }
    
    stages {
        stage('Checkout HDL') {
            steps {
                withFolderProperties {
                    git url: 'https://github.com/amiclaus/hdl', branch: "${env.BUILD_BRANCH}"
                }
            }
        }
        stage('Build Library') {
             steps {
                 withFolderProperties {
                     sh "make lib"
                 }
             }
         }
        stage('Generate Projects') {
            steps {
				println env.WORKSPACE
				jobDsl scriptText: 'job("generate-prj")'
			
				jobDsl targets:'generate_projects.groovy' ,
					   additionalParameters: [PATH: env.PATH, WORKSPACE: env.WORKSPACE]
                //withFolderProperties {
                    //build job: 'generate-projects', parameters: [[$class: 'StringParameterValue', name: 'PATH', value: env.PATH], [$class: 'StringParameterValue', name: 'WORKSPACE', value: env.WORKSPACE]]    
                    //sh "make all -j 4"
                //}
				
            }
        }
    }
}