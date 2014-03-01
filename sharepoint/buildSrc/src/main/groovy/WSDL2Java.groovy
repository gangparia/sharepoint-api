import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.apache.axis2.wsdl.WSDL2Java as CodeGen

class WSDL2Java extends DefaultTask {
	String wsdlfilename
	String databindingName
	String targetSourceFolderLocation
	String packageName

	@TaskAction
	def generateJava() {
		String[] args = 
			[ 
				"-o", targetSourceFolderLocation,
				"-p", packageName,
				"-d", databindingName,				
				"-or",
				"-uri", wsdlfilename
			]
		CodeGen.main(args)
	}
}