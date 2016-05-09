package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${importEntity};
import ${importService};
import br.com.anteros.persistence.session.service.SQLService;
import br.com.anteros.security.spring.AnterosSecurityService;
import br.com.anteros.springWeb.controller.AbstractSQLRestController;

@RestController
@RequestMapping(value = "${requestMapping}")
public class ${controller} extends AbstractSQLRestController<${entityType}, Long> {

	@Autowired
	private ${interfaceService} ${service};

	@Autowired
	protected AnterosSecurityService anterosSecurityService;

	@Override
	public SQLService<${entityType}, Long> getService() {
		return ${service};
	}
}