package br.com.anteros.maven.plugin;
/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.thoughtworks.qdox.model.JavaClass;

import br.com.anteros.generator.AnterosGenerationLog;
import br.com.anteros.generator.AnterosGeneratorManager;
import br.com.anteros.generator.config.AnterosGenerationConfig;
import freemarker.template.Configuration;

/**
 * 
 * @author Edson Martins
 *
 */
@Mojo(name = "generate", defaultPhase=LifecyclePhase.GENERATE_SOURCES)
public class AnterosMojo extends AbstractMojo implements AnterosGenerationConfig, AnterosGenerationLog {
	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project.name}", required = true)
	private String projectDisplayName;

	@Parameter(defaultValue = "${basedir}")
	private String baseDir;

	@Parameter(required = true)
	private List<String> sourcesToScanEntities;

	@Parameter(required = true)
	private List<String> packageBaseList;

	@Parameter(defaultValue = "${basedir}/src/main/java", required = true)
	private String sourceDestination;

	@Parameter(required = true)
	private String packageDestination;

	@Parameter(defaultValue = "true")
	private Boolean includeSecurity;

	@Parameter(defaultValue = "false")
	private Boolean generateRepository;
	
	@Parameter(defaultValue = "false")
	private Boolean generateService;
	
	@Parameter(defaultValue = "false")
	private Boolean generateController;

	@Parameter(defaultValue = "false", required = true)
	private Boolean generateJavaConfiguration;

	@Parameter(required = true)
	private List<String> packageScanComponentsList = new ArrayList<String>();

	@Parameter(required = true)
	private String packageScanEntity;

	@Parameter(defaultValue = "")
	private String propertiesFile;

	@Parameter(defaultValue = "false")
	private Boolean generateSwaggerConfiguration;

	@Parameter(defaultValue = "false")
	private Boolean generateJSONDocConfiguration;

	@Parameter(defaultValue = "** INSIRA AQUI O TÍTULO DA SUA API **")
	private String titleAPI;

	@Parameter(defaultValue = "** INSIRA AQUI A DESCRIÇÃO DA SUA API **")
	private String descriptionAPI;

	@Parameter(defaultValue = "Versão API 1.0")
	private String versionAPI;

	@Parameter(defaultValue = "** INSIRA AQUI O TERMO DA LICENÇA **")
	private String termsOfServiceUrl;

	@Parameter(defaultValue = "email@email.com")
	private String contactName;

	@Parameter(defaultValue = "** INSIRA AQUI A LICENÇA DA SUA API **")
	private String licenseAPI;

	@Parameter(defaultValue = "** INSIRA AQUI A URL CONTENDO A LICENÇA DA SUA API **")
	private String licenseUrl;

	@Parameter(defaultValue = "http://localhost/api")
	private String basePathJSONDoc;

	@Parameter
	private List<String> packageScanJSONDocList = new ArrayList<String>();

	@Parameter(defaultValue = "true")
	private Boolean enabled;

	private Log logger;

	private JavaClass clazz;

	private Configuration configuration;

	public void execute() throws MojoExecutionException {
		if (enabled) {
			logger = getLog();
			try {
				AnterosGeneratorManager.getInstance().generate(this, AnterosMojo.class);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public String getPackageDirectory() {
		return sourceDestination + File.separatorChar
				+ packageDestination.replace('.', File.separatorChar);
	}

	public JavaClass getClazz() {
		return clazz;
	}
	
	public void setClazz(JavaClass clazz) {
		this.clazz = clazz;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getPackageDestination() {
		return packageDestination;
	}

	public String getProjectDisplayName() {
		return projectDisplayName;
	}

	public boolean isGenerateSwaggerConfiguration() {
		return generateSwaggerConfiguration;
	}

	public boolean isGenerateJSONDocConfiguration() {
		return generateJSONDocConfiguration;
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public String getPackageScanEntity() {
		return packageScanEntity;
	}

	public String getTitleAPI() {
		return titleAPI;
	}

	public String getDescriptionAPI() {
		return descriptionAPI;
	}

	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	public String getContactName() {
		return contactName;
	}

	public String getLicenseAPI() {
		return licenseAPI;
	}

	public String getVersionAPI() {
		return versionAPI;
	}

	public String getLicenseUrl() {
		return licenseUrl;
	}

	public List<String> getPackageScanJSONDocList() {
		return packageScanJSONDocList;
	}

	public String getBasePathJSONDoc() {
		return basePathJSONDoc;
	}

	public List<String> getPackageScanComponentsList() {
		return packageScanComponentsList;
	}

	public String getSourceDestination() {
		return sourceDestination;
	}

	public List<String> getSourcesToScanEntities() {
		return sourcesToScanEntities;
	}

	public boolean isIncludeSecurity() {
		return includeSecurity;
	}

	public List<String> getPackageBaseList() {
		return packageBaseList;
	}

	public boolean isGenerateRepository() {
		return generateRepository;
	}

	public boolean isGenerateJavaConfiguration() {
		return generateJavaConfiguration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void log(String msg) {
		logger.info(msg);		
	}

	public AnterosGenerationLog getGenerationLog() {
		return this;
	}

	@Override
	public boolean isGenerateService() {
		return generateService;
	}

	@Override
	public boolean isGenerateController() {
		return generateController;
	}

}
