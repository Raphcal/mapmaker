package fr.rca.mapmaker.io.autodeploy;

import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class VSProjectWriter {
	public static void write(Project project, File projectRoot, List<String> contents, OutputStream outputStream) throws XMLStreamException {
		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		final XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream);
		
		writer.writeStartDocument();
		
		writer.writeStartElement("Project");
		writer.writeAttribute("DefaultTargets", "Build");
		writer.writeAttribute("ToolsVersion", "4.0");
		writer.writeDefaultNamespace("http://schemas.microsoft.com/developer/msbuild/2003");
		
		writer.writeStartElement("PropertyGroup");
		writeElement(writer, "Configuration", " '$(Configuration)' == '' ", "Debug");
		writeElement(writer, "Platform", " '$(Platform)' == '' ", "AnyCPU");
		writeElement(writer, "ProductVersion", "10.0.0");
		writeElement(writer, "SchemaVersion", "2.0");
		writeElement(writer, "ProjectGuid", "{753347C6-8677-4F27-8346-D60E5E79A76F}");
		writeElement(writer, "ProjectTypeGuids", "{69878862-DA7D-4DC6-B0A1-50D8FAB4242F};{FAE04EC0-301F-11D3-BF4B-00C04F79EFBC}");
		writeElement(writer, "OutputType", "Exe");
		writeElement(writer, "RootNamespace", "MeltedIce");
		writeElement(writer, "AssemblyName", "MeltedIce");
		writer.writeEndElement();
		
		writeCompileMode(writer, "Debug", true, "full");
		writeCompileMode(writer, "Release", false, "none");
		
		writeItemGroup(writer, "Reference", "System", "System.Xml", "System.Core", "Sce.PlayStation.Core");
		writeItemGroup(writer, "Compile", getSources(projectRoot));
		writeItemGroup(writer, "ShaderProgram", getShaders(projectRoot));
		writeItemGroup(writer, "PsmMetadata", "app.xml");
		
		writer.writeEmptyElement("Import");
		writer.writeAttribute("Project", "$(MSBuildExtensionsPath)\\Sce\\Sce.Psm.CSharp.targets");
		
		writeItemGroup(writer, "Folder", "maps\\", "sprites\\", "audio\\", "scripts\\");
		
		final List<String> allContents = new ArrayList<String>(contents);
		allContents.addAll(Arrays.asList(getFileNames(projectRoot, "audio", ".wav", ".mp3")));
		allContents.addAll(Arrays.asList(getFileNames(projectRoot, "scripts", ".lua")));
		writeItemGroup(writer, "Content", allContents.toArray(new String[allContents.size()]));
		
		writer.writeStartElement("ItemGroup");
		writeProjectReference(writer, "LuaInterface", "..\\..\\LuaInterface\\LuaInterface\\LuaInterface.csproj", "{D29CA5AA-9C3A-47FD-A145-20A13F147E83}");
		writer.writeEndElement();
		
		writer.writeEndElement();
	}
	
	private static void writeCompileMode(XMLStreamWriter writer, String mode, boolean debugSymbols, String debugType) throws XMLStreamException {
		writeStartElement(writer, "PropertyGroup", " '$(Configuration)|$(Platform)' == '" + mode + "|AnyCPU' ");
		
		if(debugSymbols) {
			writeElement(writer, "DebugSymbols", debugSymbols);
		}
		writeElement(writer, "DebugType", debugType);
		writeElement(writer, "Optimize", true);
		writeElement(writer, "OutputPath", "bin\\" + mode);
		
		if(debugSymbols) {
			writeElement(writer, "DefineConstants", "DEBUG;");
		}
		writeElement(writer, "ErrorReport", "prompt");
		writeElement(writer, "WarningLevel", 4);
		writeElement(writer, "ConsolePause", false);
		
		writer.writeEndElement();
	}
	
	private static void writeItemGroup(XMLStreamWriter writer, String category, String... includes) throws XMLStreamException {
		writer.writeStartElement("ItemGroup");
		
		for(final String include : includes) {
			writer.writeEmptyElement(category);
			writer.writeAttribute("Include", include);
		}
		
		writer.writeEndElement();
	}
	
	private static void writeElement(XMLStreamWriter writer, String element, String value) throws XMLStreamException {
		writer.writeStartElement(element);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}
	
	private static void writeElement(XMLStreamWriter writer, String element, int value) throws XMLStreamException {
		writeElement(writer, element, Integer.toString(value));
	}
	
	private static void writeElement(XMLStreamWriter writer, String element, boolean value) throws XMLStreamException {
		writeElement(writer, element, Boolean.toString(value));
	}
	
	private static void writeStartElement(XMLStreamWriter writer, String element, String condition) throws XMLStreamException {
		writer.writeStartElement(element);
		writer.writeAttribute("Condition", condition);
	}
	
	private static void writeElement(XMLStreamWriter writer, String element, String condition, String value) throws XMLStreamException {
		writeStartElement(writer, element, condition);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}
	
	private static void writeProjectReference(XMLStreamWriter writer, String name, String include, String uid) throws XMLStreamException {
		writer.writeStartElement("ProjectReference");
		writer.writeAttribute("Include", include);
		
		writeElement(writer, "Project", uid);
		writeElement(writer, "Name", name);
		
		writer.writeEndElement();
	}
	
	private static String[] getSources(File projectRoot) {
		return getFileNames(projectRoot, null, ".cs");
	}
	
	private static String[] getShaders(File projectRoot) {
		return getFileNames(projectRoot, "shaders", ".fcg", ".vcg");
	}
	
	private static String[] getFileNames(File folder, String subFolder, final String... extensions) {
		final File root = subFolder != null ? new File(folder, subFolder) : folder;
		
		final String[] files = root.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				for(final String extension : extensions) {
					if(name.endsWith(extension)) {
						return true;
					}
				}
				return false;
			}
		});
		
		if(subFolder != null) {
			for(int index = 0; index < files.length; index++) {
				files[index] = subFolder + '\\' + files[index];
			}
		}
		
		return files;
	}
}
