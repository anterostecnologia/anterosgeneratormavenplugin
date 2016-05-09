package br.com.anteros.maven.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.metadata.annotation.Entity;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * 
 *
 * 
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class AnterosMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

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

	private Log logger;

	public void execute() throws MojoExecutionException {
		logger = getLog();

		JavaProjectBuilder builder = getBuilder();
		try {
			generate(builder);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void generate(JavaProjectBuilder builder) throws TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, URISyntaxException, TemplateException {
		Configuration configuration = new Configuration();
		configuration.setTemplateLoader(new AnterosTemplateLoader(AnterosMojo.class, "templates"));
		String packageDirectory = sourceDestination + File.separatorChar
				+ packageDestination.replace('.', File.separatorChar);
		FileUtils.forceMkdir(new File(packageDirectory));

		for (JavaSource j : builder.getSources()) {
			List<JavaClass> classes = j.getClasses();
			for (JavaClass jc : classes) {
				if (isGenerateForClass(jc) && isContainsAnnotation(jc, Entity.class)) {
					if (includeSecurity) {
						generateSecurityService(configuration, packageDirectory, jc, packageDestination);
						generateController(configuration, packageDirectory, jc, packageDestination);
						if (generateRepository) {
							generateRepository(configuration, packageDirectory, jc, packageDestination);
						}
					} else {
						generateService(configuration, packageDirectory, jc, packageDestination);
					}
				}
			}
		}

	}

	private void generateService(Configuration configuration, String packageDirectory, JavaClass clazz,
			String packageName) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
			IOException, TemplateException {
		logger.info("Generating class service interface for " + clazz.getName());
		FileUtils.forceMkdir(new File(packageDirectory, "service"));
		FileUtils.forceMkdir(new File(packageDirectory, "service" + File.separatorChar + "impl"));
		Template templateServiceInterface = configuration.getTemplate("serviceInterface.ftl");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		String serviceName = clazz.getName() + "Service";
		String entityType = clazz.getName();
		String fullEntityName = clazz.getCanonicalName();
		Writer out = null;

		Map<String, Object> dataModel = new HashMap<String, Object>();
		File fileService = new File(packageDirectory + "/service/" + clazz.getName() + "Service.java");
		if (!fileService.exists()) {
			out = new FileWriter(fileService);
			dataModel.put("packageName", packageDestination + ".service");
			dataModel.put("serviceName", serviceName);
			dataModel.put("entityType", entityType);
			dataModel.put("importEntity", fullEntityName);
			dataModel.put("time", sdf.format(new Date()));
			templateServiceInterface.process(dataModel, out);
			out.flush();
			out.close();
		}

		Template templateServiceImpl = configuration.getTemplate("serviceImplementation.ftl");
		dataModel = new HashMap<String, Object>();
		File fileServiceImpl = new File(packageDirectory + "/service/impl/" + clazz.getName() + "ServiceImpl.java");
		if (!fileServiceImpl.exists()) {
			out = new FileWriter(fileServiceImpl);
			dataModel.put("packageName", packageDestination + ".service.impl");
			dataModel.put("importEntity", fullEntityName);
			dataModel.put("importService", packageDestination + ".service." + clazz.getName() + "Service");
			dataModel.put("serviceNameImpl", serviceName + "Impl");
			dataModel.put("interfaceService", serviceName);
			dataModel.put("entityType", entityType);
			dataModel.put("time", sdf.format(new Date()));
			templateServiceImpl.process(dataModel, out);
			out.flush();
			out.close();
		}
	}

	private void generateSecurityService(Configuration configuration, String packageDirectory, JavaClass clazz,
			String packageName) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
			IOException, TemplateException {
		logger.info("Generating class security service interface for " + clazz.getName());
		FileUtils.forceMkdir(new File(packageDirectory, "service"));
		FileUtils.forceMkdir(new File(packageDirectory, "service" + File.separatorChar + "impl"));
		Template templateServiceInterface = configuration.getTemplate("securityServiceInterface.ftl");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		String serviceName = clazz.getName() + "Service";
		String entityType = clazz.getName();
		String fullEntityName = clazz.getCanonicalName();
		Writer out = null;

		Map<String, Object> dataModel = new HashMap<String, Object>();
		File fileService = new File(packageDirectory + "/service/" + clazz.getName() + "Service.java");
		if (!fileService.exists()) {
			out = new FileWriter(fileService);
			dataModel.put("packageName", packageDestination + ".service");
			dataModel.put("resourceName", clazz.getName());
			dataModel.put("resourceDescription", clazz.getName());
			dataModel.put("serviceName", serviceName);
			dataModel.put("entityType", entityType);
			dataModel.put("importEntity", fullEntityName);
			dataModel.put("time", sdf.format(new Date()));
			templateServiceInterface.process(dataModel, out);
			out.flush();
			out.close();
		}

		Template templateServiceImpl = configuration.getTemplate("securityServiceImplementation.ftl");
		dataModel = new HashMap<String, Object>();
		File fileServiceImpl = new File(packageDirectory + "/service/impl/" + clazz.getName() + "ServiceImpl.java");
		if (!fileServiceImpl.exists()) {
			out = new FileWriter(fileServiceImpl);
			dataModel.put("packageName", packageDestination + ".service.impl");
			dataModel.put("importEntity", fullEntityName);
			dataModel.put("importService", packageDestination + ".service." + clazz.getName() + "Service");
			dataModel.put("service", StringUtils.uncapitalize(serviceName));
			dataModel.put("serviceNameImpl", serviceName + "Impl");
			dataModel.put("interfaceService", serviceName);
			dataModel.put("entityType", entityType);
			dataModel.put("time", sdf.format(new Date()));
			templateServiceImpl.process(dataModel, out);
			out.flush();
			out.close();
		}
	}

	private void generateController(Configuration configuration, String packageDirectory, JavaClass clazz,
			String packageName) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
			IOException, TemplateException {
		logger.info("Generating class controller for " + clazz.getName());
		FileUtils.forceMkdir(new File(packageDirectory, "controller"));
		Template templateServiceInterface = configuration.getTemplate("restController.ftl");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		String serviceName = clazz.getName() + "Service";
		String entityType = clazz.getName();
		String fullEntityName = clazz.getCanonicalName();
		Writer out = null;

		Map<String, Object> dataModel = new HashMap<String, Object>();
		File fileService = new File(packageDirectory + "/controller/" + clazz.getName() + "Controller.java");
		if (!fileService.exists()) {
			out = new FileWriter(fileService);
			dataModel.put("packageName", packageDestination + ".controller");
			dataModel.put("serviceName", serviceName);
			dataModel.put("entityType", entityType);
			dataModel.put("importEntity", fullEntityName);
			dataModel.put("importService", packageDestination + ".service." + clazz.getName() + "Service");
			dataModel.put("time", sdf.format(new Date()));
			dataModel.put("requestMapping", "/" + StringUtils.uncapitalize(clazz.getName()));
			dataModel.put("controller", clazz.getName() + "Controller");
			dataModel.put("interfaceService", serviceName);
			dataModel.put("service", StringUtils.uncapitalize(serviceName));
			templateServiceInterface.process(dataModel, out);
			out.flush();
			out.close();
		}
	}

	private void generateRepository(Configuration configuration, String packageDirectory, JavaClass clazz,
			String packageName) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
			IOException, TemplateException {
		logger.info("Generating class repository interface for " + clazz.getName());
		FileUtils.forceMkdir(new File(packageDirectory, "repository"));
		FileUtils.forceMkdir(new File(packageDirectory, "repository" + File.separatorChar + "impl"));
		Template templateServiceInterface = configuration.getTemplate("repositoryInterface.ftl");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		String repositoryName = clazz.getName() + "Repository";
		String entityType = clazz.getName();
		String fullEntityName = clazz.getCanonicalName();
		Writer out = null;

		Map<String, Object> dataModel = new HashMap<String, Object>();
		File fileService = new File(packageDirectory + "/repository/" + clazz.getName() + "Repository.java");
		if (!fileService.exists()) {
			out = new FileWriter(fileService);
			dataModel.put("packageName", packageDestination + ".repository");
			dataModel.put("repositoryName", repositoryName);
			dataModel.put("entityType", entityType);
			dataModel.put("importEntity", fullEntityName);
			dataModel.put("time", sdf.format(new Date()));
			templateServiceInterface.process(dataModel, out);
			out.flush();
			out.close();
		}

		Template templateServiceImpl = configuration.getTemplate("repositoryImplementation.ftl");
		dataModel = new HashMap<String, Object>();
		File fileServiceImpl = new File(
				packageDirectory + "/repository/impl/" + clazz.getName() + "RepositoryImpl.java");
		if (!fileServiceImpl.exists()) {
			out = new FileWriter(fileServiceImpl);
			dataModel.put("packageName", packageDestination + ".repository.impl");
			dataModel.put("importEntity", fullEntityName);
			dataModel.put("importRepository", packageDestination + ".repository." + clazz.getName() + "Repository");
			dataModel.put("repository", StringUtils.uncapitalize(repositoryName));
			dataModel.put("repositoryNameImpl", repositoryName + "Impl");
			dataModel.put("repositoryName", repositoryName);
			dataModel.put("entityType", entityType);
			dataModel.put("time", sdf.format(new Date()));
			templateServiceImpl.process(dataModel, out);
			out.flush();
			out.close();
		}
	}

	private boolean isContainsAnnotation(JavaClass jc, Class<?> ac) {
		for (JavaAnnotation ja : jc.getAnnotations()) {
			if (ja.getType().toString().equals(ac.getName())) {
				return true;
			}
		}
		return false;
	}

	private JavaProjectBuilder getBuilder() {
		JavaProjectBuilder docBuilder = new JavaProjectBuilder();
		for (String r : sourcesToScanEntities) {
			docBuilder.addSourceTree(new File(r));
		}

		return docBuilder;
	}

	private boolean isGenerateForClass(JavaClass sourceJavaClass) {
		String classPackage = sourceJavaClass.getPackageName();
		if (packageBaseList != null) {
			for (String source : packageBaseList) {
				if (classPackage.startsWith(source)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

}
