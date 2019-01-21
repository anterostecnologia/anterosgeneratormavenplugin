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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

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
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
public class AnterosMojo extends AbstractMojo implements AnterosGenerationConfig, AnterosGenerationLog {
	public static final String ANTEROS_SECURITY_MODEL_SQL = "br.com.anteros.security.store.sql.domain*";
	public static final String ANTEROS_SECURITY_MODEL_NO_SQL = "br.com.anteros.security.store.mongo.domain*";

	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project}")
	private MavenProject project = null;

	@Parameter(defaultValue = "${project.name}", required = true)
	private String projectDisplayName;

	@Parameter(defaultValue = "${basedir}")
	private String baseDir;

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

	@Parameter(defaultValue = "true")
	private boolean generateExceptionHandler;

	@Parameter(defaultValue = "false", required = true)
	private Boolean generateJavaConfiguration;

	@Parameter(required = true)
	private List<String> packageScanComponentsList = new ArrayList<String>();

	@Parameter(required = true)
	private List<String> packageScanEntitiesList = new ArrayList<String>();

	@Parameter(defaultValue = "")
	private String propertiesFile;

	@Parameter(defaultValue = "false")
	private Boolean generateForAbstractClass;

	@Parameter(defaultValue = "true")
	private Boolean enabled;

	@Parameter(defaultValue = "${project.compileClasspathElements}")
	private List<String> classpathElements;

	@Parameter(defaultValue = "sql")
	private String persistenceDatabase;

	@Parameter(defaultValue = "sql")
	private String securityPersistenceDatabase;

	@Parameter
	private String remoteEndPointCheckToken;

	@Parameter
	private String securedPattern;
	
	@Parameter(defaultValue="v1")
	private String resourceVersion;

	@Parameter
	private String resourceID;

	@Parameter
	private Boolean useAnterosOAuth2Server;
	
	@Parameter(defaultValue = "")
	private String clientID;
	
	@Parameter(defaultValue = "")
	private String clientSecret;

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

	protected List<URL> createClassPath() {
		List<URL> list = new ArrayList<URL>();
		if (classpathElements != null) {
			for (String cpel : classpathElements) {
				try {
					list.add(new File(cpel).toURI().toURL());
				} catch (MalformedURLException mue) {
				}
			}
		}
		return list;
	}

	public String getPackageDirectory() {
		return sourceDestination + File.separatorChar + packageDestination.replace('.', File.separatorChar);
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

	public boolean isGenerateForAbstractClass() {
		return generateForAbstractClass;
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public String getPackageScanEntity() {
		StringBuilder result = new StringBuilder();
		StringBuilder sb = new StringBuilder();

		if (packageScanEntitiesList.size() > 0) {
			boolean boAppendDelimiter = false;
			for (String packageScanEntity : packageScanEntitiesList) {
				if (boAppendDelimiter)
					sb.append(";");
				sb.append(packageScanEntity);
			}
			result.append(sb);

			if (includeSecurity) {
				if (result.toString().length() > 0) {
					result.append(";");
				}
				if ("sql".equals(getSecurityPersistenceDatabase())) {
					result.append(ANTEROS_SECURITY_MODEL_SQL);
				} else {
					result.append(ANTEROS_SECURITY_MODEL_NO_SQL);
				}
			}
		}
		return result.toString();
	}

	public List<String> getPackageScanComponentsList() {
		return packageScanComponentsList;
	}

	public String getSourceDestination() {
		return sourceDestination;
	}

	public boolean isIncludeSecurity() {
		return includeSecurity;
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

	@Override
	public List<URL> getClassPathURLs() {
		return createClassPath();
	}

	@Override
	public boolean isGenerateExceptionHandler() {
		return generateExceptionHandler;
	}

	@Override
	public String getPersistenceDatabase() {
		return persistenceDatabase;
	}

	@Override
	public String getSecurityPersistenceDatabase() {
		return securityPersistenceDatabase;
	}

	@Override
	public String remoteEndPointCheckToken() {
		return remoteEndPointCheckToken;
	}

	@Override
	public String getResourceID() {
		return resourceID;
	}

	@Override
	public String getSecuredPattern() {
		return securedPattern;
	}

	@Override
	public Boolean isUseAnterosOAuth2Server() {
		return useAnterosOAuth2Server;
	}

	@Override
	public String getClientID() {
		return clientID;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	@Override
	public String getResourceVersion() {
		return resourceVersion;
	}

}
