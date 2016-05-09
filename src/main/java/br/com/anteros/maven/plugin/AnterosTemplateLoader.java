package br.com.anteros.maven.plugin;

import java.net.URL;

import br.com.anteros.core.utils.ResourceUtils;
import freemarker.cache.ClassTemplateLoader;

public class AnterosTemplateLoader extends ClassTemplateLoader {

	private Class<?> cl;

	public AnterosTemplateLoader(Class<?> clazz, String string) {
		super(clazz, string);
		this.cl = clazz;
	}

	@Override
	protected URL getURL(String name) {
		return ResourceUtils.getResource(name, cl);
	}

}
