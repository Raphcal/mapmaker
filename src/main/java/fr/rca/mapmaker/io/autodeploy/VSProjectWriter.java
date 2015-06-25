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
		writer.writeCharacters("\n");
		
		writer.writeStartElement("Project");
		writer.writeAttribute("DefaultTargets", "Build");
		writer.writeAttribute("ToolsVersion", "4.0");
		writer.writeDefaultNamespace("http://schemas.microsoft.com/developer/msbuild/2003");
		writer.writeCharacters("\n");
		
		int level = 1;
		
		writeIndent(writer, level++);
		writer.writeStartElement("PropertyGroup");
		writer.writeCharacters("\n");
		writeElement(writer, level, "Configuration", " '$(Configuration)' == '' ", "Debug");
		writeElement(writer, level, "Platform", " '$(Platform)' == '' ", "AnyCPU");
		writeElement(writer, level, "ProductVersion", "10.0.0");
		writeElement(writer, level, "SchemaVersion", "2.0");
		writeElement(writer, level, "ProjectGuid", "{753347C6-8677-4F27-8346-D60E5E79A76F}");
		writeElement(writer, level, "ProjectTypeGuids", "{69878862-DA7D-4DC6-B0A1-50D8FAB4242F};{FAE04EC0-301F-11D3-BF4B-00C04F79EFBC}");
		writeElement(writer, level, "OutputType", "Exe");
		writeElement(writer, level, "RootNamespace", "MeltedIce");
		writeElement(writer, level, "AssemblyName", "MeltedIce");
		writeIndent(writer, --level);
		writer.writeEndElement();
		writer.writeCharacters("\n");
		
		writeCompileMode(writer, level, "Debug", true, "full");
		writeCompileMode(writer, level, "Release", false, "none");
		
		writeItemGroup(writer, level, "Reference", "System", "System.Xml", "System.Core", "Sce.PlayStation.Core");
		writeItemGroup(writer, level, "Compile", getSources(projectRoot));
		writeItemGroup(writer, level, "ShaderProgram", getShaders(projectRoot));
		writeItemGroup(writer, level, "PsmMetadata", "app.xml");
		
		writeIndent(writer, level);
		writer.writeEmptyElement("Import");
		writer.writeAttribute("Project", "$(MSBuildExtensionsPath)\\Sce\\Sce.Psm.CSharp.targets");
		
		writeItemGroup(writer, level, "Folder", "maps\\", "sprites\\", "audio\\", "scripts\\");
		
		final List<String> allContents = new ArrayList<String>(contents);
		allContents.addAll(Arrays.asList(getFileNames(projectRoot, "audio", ".wav", ".mp3")));
		allContents.addAll(Arrays.asList(getFileNames(projectRoot, "scripts", ".lua")));
		writeItemGroup(writer, level, "Content", allContents.toArray(new String[allContents.size()]));
		
		writeIndent(writer, --level);
		writer.writeEndElement();
		writer.writeCharacters("\n");
	}
	
	private static void writeCompileMode(XMLStreamWriter writer, int indent, String mode, boolean debugSymbols, String debugType) throws XMLStreamException {
		writeStartElement(writer, indent++, "PropertyGroup", " '$(Configuration)|$(Platform)' == '" + mode + "|AnyCPU' ");
		writer.writeCharacters("\n");
		
		if(debugSymbols) {
			writeElement(writer, indent, "DebugSymbols", debugSymbols);
		}
		writeElement(writer, indent, "DebugType", debugType);
		writeElement(writer, indent, "Optimize", true);
		writeElement(writer, indent, "OutputPath", "bin\\" + mode);
		
		if(debugSymbols) {
			writeElement(writer, indent, "DefineConstants", "DEBUG;");
		}
		writeElement(writer, indent, "ErrorReport", "prompt");
		writeElement(writer, indent, "WarningLevel", 4);
		writeElement(writer, indent, "ConsolePause", false);
		
		writeIndent(writer, --indent);
		writer.writeEndElement();
		writer.writeCharacters("\n");
	}
	
	private static void writeItemGroup(XMLStreamWriter writer, int indent, String category, String... includes) throws XMLStreamException {
		writeIndent(writer, indent++);
		writer.writeStartElement("ItemGroup");
		writer.writeCharacters("\n");
		
		for(final String include : includes) {
			writeIndent(writer, indent);
			writer.writeEmptyElement(category);
			writer.writeAttribute("Include", include);
			writer.writeCharacters("\n");
		}
		
		writeIndent(writer, --indent);
		writer.writeEndElement();
		writer.writeCharacters("\n");
	}
	
	private static void writeElement(XMLStreamWriter writer, int indent, String element, String value) throws XMLStreamException {
		writeIndent(writer, indent);
		writer.writeStartElement(element);
		writer.writeCharacters(value);
		writer.writeEndElement();
		writer.writeCharacters("\n");
	}
	
	private static void writeElement(XMLStreamWriter writer, int indent, String element, int value) throws XMLStreamException {
		writeElement(writer, indent, element, Integer.toString(value));
	}
	
	private static void writeElement(XMLStreamWriter writer, int indent, String element, boolean value) throws XMLStreamException {
		writeElement(writer, indent, element, Boolean.toString(value));
	}
	
	private static void writeStartElement(XMLStreamWriter writer, int indent, String element, String condition) throws XMLStreamException {
		writeIndent(writer, indent);
		writer.writeStartElement(element);
		writer.writeAttribute("Condition", condition);
	}
	
	private static void writeElement(XMLStreamWriter writer, int indent, String element, String condition, String value) throws XMLStreamException {
		writeStartElement(writer, indent, element, condition);
		writer.writeCharacters(value);
		writer.writeEndElement();
		writer.writeCharacters("\n");
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
	
	private static void writeIndent(XMLStreamWriter writer, int level) throws XMLStreamException {
		for(int i = 0; i < level; i++) {
			writer.writeCharacters("\t");
		}
	}
}
